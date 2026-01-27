package com.mycompany.app.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.app.Errors.HttpServerError;
import com.mycompany.app.Request.Request;
import com.mycompany.app.Request.RequestHandler;
import com.mycompany.app.Request.RequestHandlerDB;
import com.mycompany.app.sockets.Middleware;
import com.mycompany.app.sockets.Server;

import lombok.Getter;

@Getter
public class Route {
    private String route;
    private String method;
    private RequestHandler handler;
    private RequestHandlerDB dbHandler;
    private ArrayList<String> allowedMethods;

    public Route() {
        this.allowedMethods = new ArrayList<>();
        allowedMethods.add("GET");
        allowedMethods.add("POST");
        allowedMethods.add("OPTIONS");
    }

    public Route(String method, String route, RequestHandlerDB handler) {
        this();
        this.method = method;
        this.route = route;
        this.dbHandler = handler;
    }

    public Route(String method, String route, RequestHandler handler) {
        this();
        if (!allowedMethods.contains(method)) {
            throw new HttpServerError(
                    String.format("Allowed methods are %s you, your provided method %s is not supported",
                            String.join(",", allowedMethods), method));
        }
        this.method = method;
        this.route = route;
        this.handler = handler;

    }

    public void executeRoute(OutputStream out, Request request) throws IOException {
        Response response = new Response();
        if (route.contains(".css") || route.contains(".js")) {
            response.httpFileResponse(route);
        }

        for (Map.Entry<String, ArrayList<Middleware>> element : Server.middlewares.entrySet()) {
            if (route.contains(element.getKey())) {
                for (Middleware middlewareFunction : element.getValue()) {
                    middlewareFunction.apply(request, response);
                }
            }
        }
        Response result;
        if (this.dbHandler != null) {
            result = this.dbHandler.apply(request, response, Server.jdbcTemplate);
        } else {
            result = this.handler.apply(request, response);
        }
        String responseHeaders = result.formulateResponseHeaders();
        byte[] body = result.getBody().getBytes(StandardCharsets.UTF_8);
        out.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    public Route(String method, String route) {
        this.allowedMethods = new ArrayList<>();
        allowedMethods.add("GET");
        allowedMethods.add("POST");
        allowedMethods.add("OPTIONS");
        if (!allowedMethods.contains(method)) {
            throw new HttpServerError(
                    String.format("Allowed methods are %s you, your provided method %s is not supported",
                            String.join(",", allowedMethods), method));
        }
        this.method = method;
        this.route = route;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((route == null) ? 0 : route.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((allowedMethods == null) ? 0 : allowedMethods.hashCode());
        return result;
    }

    private static String pathOnly(String url) {
        int q = url.indexOf('?');
        return q == -1 ? url : url.substring(0, q);
    }

    private static String queryOnly(String url) {
        int q = url.indexOf('?');
        return q == -1 ? "" : url.substring(q + 1);
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query.isEmpty())
            return map;

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    private static boolean matchesType(String pattern, String value) {
        return switch (pattern) {
            case "int" -> value.matches("-?\\d+");
            case "str" -> value.matches("[a-zA-Z_]+");
            case "*" -> true;
            default -> pattern.equals(value);
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Route other))
            return false;
        if (!this.method.equals(other.method))
            return false;

        String thisPath = pathOnly(this.route);
        String otherPath = pathOnly(other.route);

        String[] thisParts = thisPath.split("/");
        String[] otherParts = otherPath.split("/");

        if (thisParts.length != otherParts.length)
            return false;

        for (int i = 0; i < thisParts.length; i++) {
            String patternPart = thisParts[i];
            String actualPart = otherParts[i];

            if (patternPart.startsWith(":")) {
                continue;
            } else if (!patternPart.equals(actualPart)) {
                return false;
            }
        }

        Map<String, String> thisParams = parseQuery(queryOnly(this.route));
        Map<String, String> otherParams = parseQuery(queryOnly(other.route));

        for (var entry : thisParams.entrySet()) {
            String key = entry.getKey();
            String expectedType = entry.getValue();

            if (!otherParams.containsKey(key))
                return false;

            String actualValue = otherParams.get(key);
            if (!matchesType(expectedType, actualValue))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Route [route=" + route + ", method=" + method + ", allowedMethods=" + allowedMethods + "]";
    }
}
