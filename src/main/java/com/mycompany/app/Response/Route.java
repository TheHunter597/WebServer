package com.mycompany.app.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private final ArrayList<String> allowedMethods = AllowedMethods.allowedMethods;

    public Route(String method, String route) {
        if (!allowedMethods.contains(method)) {
            throw new HttpServerError(
                    String.format("Allowed methods are %s you, your provided method %s is not supported",
                            String.join(",", allowedMethods), method));
        }
        this.method = method;
        this.route = route;
    }

    public Route(String method, String route, RequestHandler handler) {
        this(method, route);
        this.handler = handler;
    }

    public Route(String method, String route, RequestHandlerDB handler) {
        this(method, route);
        this.dbHandler = handler;
    }

    public void executeRoute(OutputStream out, Request request, Server server) throws IOException {
        Response response = new Response();

        for (Map.Entry<String, ArrayList<Middleware>> element : server.getMiddlewares().entrySet()) {
            if (route.contains(element.getKey())) {
                for (Middleware middlewareFunction : element.getValue()) {
                    middlewareFunction.apply(request, response);
                }
            }
        }
        Response result;
        if (this.dbHandler != null) {
            if (server.getJdbcTemplate() == null) {
                throw new HttpServerError(
                        "Database connection is not enabled. Please enable it before using DB routes. use server.enableDatabaseConnection()");
            }
            result = this.dbHandler.apply(request, response, server.getJdbcTemplate());
        } else {
            result = this.handler.apply(request, response);
        }
        String responseHeaders = result.formulateResponseHeaders();
        byte[] body = result.getBody().getBytes(StandardCharsets.UTF_8);
        out.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Route other)) {
            return false;
        }
        return RouteMatcher.matches(this.route, other.route, this.method, other.method);
    }

    @Override
    public String toString() {
        return "Route [route=" + route + ", method=" + method + ", allowedMethods=" + allowedMethods + "]";
    }
}
