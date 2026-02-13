package com.mycompany.app.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.app.Handlers.HttpDriver;
import com.mycompany.app.Response.Route;

public class RequestParser {
    public static HashMap<String, Object> requestCookiesExtractor(String httpRequest, Request requestObject) {

        HashMap<String, String> cookies = new HashMap<>();
        String cookieHeader = requestObject.headers.get("Cookie");

        HashMap<String, Object> requestAndRequestObject = new HashMap<>();
        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);

        if (cookieHeader == null) {
            return requestAndRequestObject;
        }

        ArrayList<String> splitCookies = new ArrayList<>(List.of(cookieHeader.split(";")));
        for (int index = 0; index < splitCookies.size(); index++) {
            var result = splitCookies.get(index).split("=");
            cookies.put(result[0], result[1]);
        }
        requestObject.cookies = cookies;

        return requestAndRequestObject;
    }

    public static HashMap<String, Object> requestRouteParametersExtractor(
            String httpRequest, Request requestObject) {

        HashMap<String, String> params = new HashMap<>();

        ArrayList<Route> currentThreadRoutes = HttpDriver.threadRoutes;
        ArrayList<Route> paramitraizedRoutes = new ArrayList<>();

        HashMap<String, Object> requestAndRequestObject = new HashMap<>();
        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);

        for (Route route : currentThreadRoutes) {
            if (!route.getRoute().contains(":")) {
                continue;
            }
            paramitraizedRoutes.add(route);
        }
        String sentRoute = requestObject.coreData.get("path");
        if (sentRoute == null) {
            return requestAndRequestObject;
        }
        for (Route route : paramitraizedRoutes) {
            String[] routeBaseUrl = route.getRoute().split("/");
            String[] sendRequestBaseUrl = sentRoute.split("/");

            try {
                for (int index = 0; index < routeBaseUrl.length; index++) {

                    if (routeBaseUrl[index].contains(":")) {
                        params.put(routeBaseUrl[index], sendRequestBaseUrl[index]);
                        if (sendRequestBaseUrl[index] == null) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                break;
            }
        }

        HashMap<String, String> sanitizedParams = new HashMap<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sanitizedParams.put(entry.getKey().substring(1, entry.getKey().length()), entry.getValue());
        }
        requestObject.routeParameters = sanitizedParams;

        return requestAndRequestObject;
    }

    public static HashMap<String, Object> requestHeadersExtractor(String httpRequest, Request requestObject) {
        HashMap<String, String> headers = new HashMap<>();

        String[] lines = httpRequest.split("\r\n");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.isEmpty()) {
                break;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex == -1) {
                continue;
            }

            String key = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();

            headers.put(key, value);
        }
        requestObject.headers = headers;
        HashMap<String, Object> requestAndRequestObject = new HashMap<>();
        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);
        return requestAndRequestObject;
    }

    public static HashMap<String, Object> requestParametersExtractor(String httpRequest, Request requestObject) {
        HashMap<String, String> parametersMap = new HashMap<>();

        HashMap<String, Object> requestAndRequestObject = new HashMap<>();
        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);

        if (httpRequest.length() == 0) {
            return requestAndRequestObject;
        }

        String path = requestObject.coreData.get("path");
        if (!path.contains("?")) {
            return requestAndRequestObject;
        }

        ArrayList<String> params = new ArrayList<String>(List.of(path.split("\\?")));

        params.remove(0);
        try {
            var result = (new ArrayList<String>(List.of(
                    String.join("", params).split("&")))).stream().reduce(new ArrayList<String>(), (acc, param) -> {
                        ArrayList<String> data = null;
                        if (param.contains("/")) {
                            param = param.split("/")[0];
                        }
                        data = new ArrayList<String>(List.of(param.split("=")));

                        ArrayList<String> newArrayList = new ArrayList<String>();

                        newArrayList.addAll(data);
                        newArrayList.addAll(acc);

                        return newArrayList;
                    }, (acc, otherAcc) -> {
                        acc.addAll(otherAcc);
                        return acc;
                    });

            var resultIterator = result.iterator();

            while (resultIterator.hasNext()) {
                String key = resultIterator.next();
                String value = resultIterator.next();

                parametersMap.put(key, value);
            }
            requestObject.params = parametersMap;
            requestAndRequestObject.put("requestObject", requestObject);
            return requestAndRequestObject;
        } catch (Exception e) {
            System.err.println(e);
            return requestAndRequestObject;
        }

    }

    public static HashMap<String, Object> requestBodyExtractor(String httpRequest, Request requestObject) {
        var bodyIndex = httpRequest.indexOf("\r\n\r\n");
        String body = null;
        if (bodyIndex != -1) {
            body = httpRequest.substring(bodyIndex + 4, httpRequest.length());
        } else {
            body = "";
        }
        requestObject.requestBody = body;

        HashMap<String, Object> requestAndRequestObject = new HashMap<>();
        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);

        return requestAndRequestObject;
    }

    public static String requestBodyExtractor(String request) {
        var bodyIndex = request.indexOf("\r\n\r\n");
        String body = null;
        if (bodyIndex != -1) {
            body = request.substring(bodyIndex + 4, request.length());
        } else {
            body = "";
        }
        return body;
    }

    public static HashMap<String, Object> coreDataExtractor(String httpRequest, Request requestObject) {
        HashMap<String, String> headersMap = new HashMap<>();
        HashMap<String, Object> requestAndRequestObject = new HashMap<>();

        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);
        requestObject.coreData = headersMap;

        if (httpRequest.length() == 0) {
            return requestAndRequestObject;
        }

        var headersEnd = httpRequest.indexOf("\r\n\r\n");
        var headers = httpRequest.substring(0, headersEnd);

        var headersLines = headers.split("\r\n");

        var httpDataLines = headersLines[0].split(" ");

        headersMap.put("method", httpDataLines[0]);
        headersMap.put("path", httpDataLines[1]);
        headersMap.put("http", httpDataLines[2]);

        return requestAndRequestObject;
    }

    public static HashMap<String, Object> parseContentTypeHeader(String httpRequest, Request requestObject) {
        String contentTypeHeader = requestObject.headers.get("Content-Type");
        if (contentTypeHeader == null || contentTypeHeader.isBlank()) {
            return null;
        }

        HashMap<String, String> result = new HashMap<>();

        String[] parts = contentTypeHeader.split(";");

        String mediaType = parts[0].trim();

        result.put("Content-Type", mediaType);

        HashMap<String, Object> requestAndRequestObject = new HashMap<>();
        requestAndRequestObject.put("request", httpRequest);
        requestAndRequestObject.put("requestObject", requestObject);

        if (parts.length == 1) {
            requestObject.contentType = result;

            return requestAndRequestObject;
        }
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();

            if (part.startsWith("boundary=")) {
                String boundary = part.substring("boundary=".length());
                boundary = boundary.replace("\"", "");
                result.put("boundary", boundary);
            }
        }

        if (!result.containsKey("boundary")) {
            throw new IllegalArgumentException("Boundary not found in Content-Type header");
        }
        requestObject.contentType = result;

        return requestAndRequestObject;
    }

}
