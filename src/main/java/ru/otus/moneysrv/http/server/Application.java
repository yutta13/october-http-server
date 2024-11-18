package ru.otus.moneysrv.http.server;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        new HttpServer(8189).start();
    }
}
