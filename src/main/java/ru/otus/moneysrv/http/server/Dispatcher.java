package ru.otus.moneysrv.http.server;

import ru.otus.moneysrv.http.server.db.PathConf;
import ru.otus.moneysrv.http.server.processors.*;


import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Dispatcher implements RequestProcessor {
    public HttpServer httpServer;
    private Connection connection;
    private HashSet<String> allowedUris;
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor defaultMethodNotAllowedProcessor;


    public Dispatcher(HttpServer httpServer) {
        this.httpServer = httpServer;
        try {
            connection = DriverManager.getConnection(PathConf.getDATABASE_URL(), PathConf.getDbLogin(), PathConf.getDbPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.processors = new HashMap<>();
        this.allowedUris = new HashSet<>();
        this.processors.put(HttpMethod.GET + " /", new MoneyServiceStarted());
        this.processors.put(HttpMethod.GET + " /balinq", new BalInqProcessor(connection));
        this.processors.put(HttpMethod.PUT + " /cashwithdrawal", new CashWithdrawalProcessor(connection));
        this.processors.put(HttpMethod.PUT + " /addfunds", new AddFundsProcessor(connection));
        this.processors.put(HttpMethod.POST + " /addcustomer", new AddNewCustomer(connection));
        for (String key : processors.keySet()) {
            String uri = key.split(" ")[1];
            if (!allowedUris.contains(uri)) {
                allowedUris.add(uri);
            }
        }
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.defaultMethodNotAllowedProcessor = new DefaultMethodNotAllowedProcessor();
    }

    public Dispatcher(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void execute(HttpRequest request, OutputStream out) throws IOException, SQLException, DefaultInternalServerErrorProcessor {
        try {
            if (!processors.containsKey(request.getRoutingKey())) {
                if (allowedUris.contains(request.getUri())) {
                    defaultMethodNotAllowedProcessor.execute(request, out);
                    return;
                }
                defaultNotFoundProcessor.execute(request, out);
                return;
            }

            processors.get(request.getRoutingKey()).execute(request, out);
        } catch (BadRequestException e) {
            request.setException(e);
            defaultBadRequestProcessor.execute(request, out);
        } catch (Exception e) {
            e.printStackTrace();
            defaultInternalServerErrorProcessor.execute(request, out);
        } catch (DefaultInternalServerErrorProcessor e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void close() throws Exception {

    }
}
