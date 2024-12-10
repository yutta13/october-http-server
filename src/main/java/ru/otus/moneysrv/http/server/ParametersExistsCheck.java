package ru.otus.moneysrv.http.server;

public class ParametersExistsCheck {
    public static void checkParameterExists(HttpRequest request, String parameterName) throws BadRequestException {
        if (!request.containsParameter(parameterName)) {
            throw new BadRequestException("Parameter '" + parameterName + "' is missing");
        }
    }
}
