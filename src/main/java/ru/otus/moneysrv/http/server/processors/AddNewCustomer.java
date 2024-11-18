package ru.otus.moneysrv.http.server.processors;

import ru.otus.moneysrv.http.server.HttpRequest;
import ru.otus.moneysrv.http.server.ParametersFormatCheck;
import ru.otus.moneysrv.http.server.db.Db_queries;
import ru.otus.moneysrv.http.server.ParametersExistsCheck;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AddNewCustomer implements RequestProcessor {
    Connection connection;
    public AddNewCustomer(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException, DefaultInternalServerErrorProcessor {
        final String name;
        final String surname;
        final String card_no;
        final int exp_date;
        final int p_offset;
        final String acct_num;

        ParametersExistsCheck.checkParameterExists(request, "name");
        ParametersExistsCheck.checkParameterExists(request, "surname");
        ParametersExistsCheck.checkParameterExists(request, "card_no");
        ParametersExistsCheck.checkParameterExists(request, "date");
        ParametersExistsCheck.checkParameterExists(request, "pin");

        name =  ParametersFormatCheck.checkParameterFormat(request,"name" );
        surname =  ParametersFormatCheck.checkParameterFormat(request,"surname" );
        card_no =  ParametersFormatCheck.checkParameterFormat(request,"card_no" );
        exp_date = ParametersFormatCheck.checkParameterFormatInt(request,"date");
        p_offset = ParametersFormatCheck.checkParameterFormatInt(request,"pin" );


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getCardPresentQuery(card_no));
        } catch (SQLException e) {
            throw new DefaultInternalServerErrorProcessor();
        }

        try {
            PreparedStatement preparedStatement = connection.
                    prepareStatement(Db_queries.getAddCustomerQuery(name, surname));
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String cardholder_id = resultSet.getString("id");
            int id = Integer.parseInt(cardholder_id);
            preparedStatement = connection.
                    prepareStatement(Db_queries.getAddcrefQuery(card_no, exp_date, id,  p_offset));
            preparedStatement.setString(1, card_no);
            preparedStatement.setInt(2, exp_date);
            preparedStatement.setInt(3, id);
            preparedStatement.setInt(4, p_offset);
            preparedStatement.executeUpdate();
            acct_num = generateAcctRub(card_no);
            preparedStatement = connection.
                    prepareStatement(Db_queries.getAddacctrubQuery( card_no,acct_num));
            preparedStatement.setString(1, acct_num);
            preparedStatement.setString(2, card_no);
            preparedStatement.setString(3, acct_num);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String response = "" +
                "HTTP/1.1 201 Created\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAcctRub(String card_no) {
        int curr=810;
        Set<Integer> generatedNumbers = new HashSet<>();
        Random random = new Random();
        String newAcctNum = card_no.substring(0, 6) + curr;

        for (int i = 1; i <= 100000; i++) {
            int tail = random.nextInt(1000000) + i;
            if (!generatedNumbers.add(tail)) {
                continue;
            }
            int bankID = random.nextInt(100) + i;
            if (!generatedNumbers.add(bankID)) {
                continue;
            }
            newAcctNum = newAcctNum.concat(String.valueOf(bankID)).concat(String.valueOf(tail));
            break;
        }
        System.out.println(newAcctNum);
        return newAcctNum;
    }

    @Override
    public void close() throws Exception {
    }
}
