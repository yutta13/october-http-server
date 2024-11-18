package ru.otus.moneysrv.http.server;

import ru.otus.moneysrv.http.server.processors.DefaultInternalServerErrorProcessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private HttpServer httpServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Dispatcher dispatcher;


    public ClientHandler(HttpServer httpServer, Socket socket) throws IOException {
        this.httpServer = httpServer;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.dispatcher = new Dispatcher(httpServer);
        try {
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            if (n < 1) {
            } else {
                String rawRequest = new String(buffer, 0, n);
                HttpRequest request = new HttpRequest(rawRequest);
                request.info();
                dispatcher.execute(request, socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DefaultInternalServerErrorProcessor e) {
            throw new RuntimeException(e);
        } finally {
            disconnect();
        }
    }


    public void disconnect() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


