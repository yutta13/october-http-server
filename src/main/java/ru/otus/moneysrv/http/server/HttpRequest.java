package ru.otus.moneysrv.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private String rawRequest;

    public HttpMethod getMethod() {
        return method;
    }

    private HttpMethod method;
    private String uri;
    private String body;
    private Map<String, String> parameters;
    private Exception exception;
    private Map<String, String> headersMap;
    private static final Logger LOGGER = LogManager.getLogger(HttpRequest.class);

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getUri() {
        return uri;
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.parse();
        this.headersParsing();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    private void parse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        uri = rawRequest.substring(startIndex + 1, endIndex);
        method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));
        parameters = new HashMap<>();
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            uri = elements[0];
            String[] keysValues = elements[1].split("[&]");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        if (method == HttpMethod.POST) {
            this.body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        }
    }

    private void headersParsing() {
        String headers = rawRequest.substring(rawRequest.indexOf("\r\n") + 2);
        String[] lines = headers.split("\r\n");
        headersMap = new HashMap<>();
        for (String line : lines) {
            int index = line.indexOf(": ");
            if (index != -1) {
                String headerKey = line.substring(0, index).trim();
                String headerValue = line.substring(index + 2).trim();
                headersMap.put(headerKey, headerValue);
            }
        }

    }

    public void info() {
        LOGGER.debug(rawRequest);
        LOGGER.info("Method: " + method);
        LOGGER.info("URI: " + uri);
        LOGGER.info("Parameters: " + parameters);
        LOGGER.info("Body: " + body);
    }
}
