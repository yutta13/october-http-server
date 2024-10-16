package ru.otus.october.http.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        this.dispatcher = new Dispatcher();

        try {
            System.out.println("Клиент подключился ");
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.info(true);
            dispatcher.execute(request, socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
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


