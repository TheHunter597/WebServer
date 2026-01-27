package com.mycompany.app.Handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.mycompany.app.Response.Route;

import lombok.Getter;

@Getter
public class HttpDriver {

    public static final ArrayList<Route> threadRoutes = new ArrayList<>();

    public HttpDriver() {
    }

    public void addNewRoute(Route route) {
        HttpDriver.threadRoutes.add(route);
    }

    public void addNewRoute(String method, String route) {
        HttpDriver.threadRoutes.add(new Route(method, route));
    }

    public Route findRoute(Route route) {
        ArrayList<Route> routes = HttpDriver.threadRoutes;
        Route foundRoute = null;
        try {
            foundRoute = routes.stream()
                    .filter(curr -> curr.equals(route))
                    .findFirst()
                    .get();
        } catch (NoSuchElementException e) {
            foundRoute = new Route(route.getMethod(), route.getRoute(), (req, res) -> {
                if (route.getRoute().split(".").length > 1) {
                    // if we are returning a file dont return the notfound.html
                    // not like I should be returning the notfound file here anyway
                    res.setStatusCode(404);
                    try {
                        res.httpFileResponse("/notfound.html");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                return res;
            });
        }
        return foundRoute;
    }

}
