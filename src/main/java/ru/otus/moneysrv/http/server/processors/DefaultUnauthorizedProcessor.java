package ru.otus.moneysrv.http.server.processors;

import ru.otus.moneysrv.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DefaultUnauthorizedProcessor extends Throwable implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 401 Unauthorized\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Bad Pin</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws Exception {

    }
}
