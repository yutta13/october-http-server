package ru.otus.moneysrv.http.server.processors;

import ru.otus.moneysrv.http.server.HttpRequest;
import ru.otus.moneysrv.http.server.ParametersFormatCheck;
import ru.otus.moneysrv.http.server.db.Db_queries;
import ru.otus.moneysrv.http.server.ParametersExistsCheck;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class BalInqProcessor implements RequestProcessor {
    //http://localhost:8189/balinq?card_no=4000010000000001
    Connection connection;
    String balance;


    public BalInqProcessor(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        final String card_no;

        ParametersExistsCheck.checkParameterExists(request, "card_no");
        card_no =  ParametersFormatCheck.checkParameterFormat(request,"card_no" );

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getBalanceforcardQuery(card_no));
            preparedStatement.setString(1, card_no);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            balance = resultSet.getString("balance");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>" + balance + " рублей на карте " + card_no + "</h1></body></html>";

        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
    @Override
    public void close() throws Exception {

    }
}