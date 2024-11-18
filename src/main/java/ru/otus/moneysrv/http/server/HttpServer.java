package ru.otus.moneysrv.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.moneysrv.http.server.processors.RequestProcessor;
//import ru.otus.atm.http.server.processors.RequestProcessorjdbc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private RequestProcessor requestProcessor;
    private static final Logger LOGGER = LogManager.getLogger(HttpRequest.class);
    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher(this);
       // requestProcessor = new RequestProcessorjdbc(this);
    }

    public void start()  {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Сервер запущен на порту: " + port);
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> {
                    try {
                        new ClientHandler(this, socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
