package ru.otus.moneysrv.http.server;

public class ParametersFormatCheck {
    public static String checkParameterFormat(HttpRequest request, String parameterName) throws BadRequestException {
        try {
            parameterName = request.getParameter(parameterName);
            return parameterName;
        } catch (NumberFormatException e) {
            throw new BadRequestException("Parameter " + parameterName + " has incorrect type");
        }
    }

    public static int checkParameterFormatInt(HttpRequest request, String parameterName) throws BadRequestException {
        try {
            int paramvalue = Integer.parseInt(request.getParameter(parameterName));
            return paramvalue;
        } catch (NumberFormatException e) {
            throw new BadRequestException("Parameter " + parameterName + " has incorrect type");
        }
    }

}
