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

public class AddFundsProcessor implements RequestProcessor {
    Connection connection;
    private RequestProcessor defaultUnauthorizedProcessor;

    public AddFundsProcessor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException, DefaultInternalServerErrorProcessor {
        final String card_no;
        final int transAmount;
        final int p_offset;

        ParametersExistsCheck.checkParameterExists(request, "card_no");
        ParametersExistsCheck.checkParameterExists(request, "amount");
        ParametersExistsCheck.checkParameterExists(request, "pin");

        card_no = ParametersFormatCheck.checkParameterFormat(request, "card_no");
        transAmount = ParametersFormatCheck.checkParameterFormatInt(request, "amount");
        p_offset = ParametersFormatCheck.checkParameterFormatInt(request, "pin");

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getCardPresentQuery(card_no));
        } catch (SQLException e) {
            throw new DefaultInternalServerErrorProcessor();
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getCheckpinQuery(card_no));
            preparedStatement.setString(1, card_no);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int in_p_offset = Integer.parseInt(resultSet.getString("p_offset"));
            if (in_p_offset != p_offset) {
            this.defaultUnauthorizedProcessor = new DefaultUnauthorizedProcessor();
            defaultUnauthorizedProcessor.execute(request, output);
            return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getAddfundsQuery(transAmount, card_no));
            preparedStatement.setInt(1, transAmount);
            preparedStatement.setString(2, card_no);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>" + "Успешное пополнение " + "</h1></body></html>";

        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws Exception {

    }
}
