package ru.otus.october.http.server.processors;

import ru.otus.october.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloWorldProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Hello World!!!</h1><table><tr><td>1</td><td>2</td></tr></table></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
