package ru.otus.october.http.server;

import ru.otus.october.http.server.app.ItemsRepository;
import ru.otus.october.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Dispatcher {
    private final HashSet<String> allowedUris;
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor defaultMethodNotAllowedProcessor;
    private ItemsRepository itemsRepository;

    public Dispatcher() {
        this.itemsRepository = new ItemsRepository();
        this.processors = new HashMap<>();
       
        this.allowedUris = new HashSet<>();
//        this.allowedUris.add("/calculator");
//        this.allowedUris.add("/items");
//        this.allowedUris.add("/");
        for (String key : processors.keySet()) {
            String uri = key.split(" ")[1];
            if (!allowedUris.contains(uri)) {
                allowedUris.add(uri);
            }
        }
        this.processors.put(HttpMethod.GET + " /", new HelloWorldProcessor());
        this.processors.put(HttpMethod.GET + " /calculator", new CalculatorProcessor());
        this.processors.put(HttpMethod.GET + " /items", new GetAllItemsProcessor(itemsRepository));
        this.processors.put(HttpMethod.POST + " /items", new CreateNewItemsProcessor(itemsRepository));
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.defaultMethodNotAllowedProcessor = new DefaultMethodNotAllowedProcessor();
    }
    public void execute(HttpRequest request, OutputStream out) throws IOException {
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
        }
    }
}
