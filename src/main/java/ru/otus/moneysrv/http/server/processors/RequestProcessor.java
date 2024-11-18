package ru.otus.moneysrv.http.server.processors;

import ru.otus.moneysrv.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public interface RequestProcessor extends AutoCloseable{
    void execute(HttpRequest request, OutputStream output) throws SQLException, IOException, DefaultInternalServerErrorProcessor;
}
