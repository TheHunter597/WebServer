package com.mycompany.app.Handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
            System.err.println("Route not found: " + route.getMethod() + " " + route.getRoute());
            foundRoute = new Route(route.getMethod(), route.getRoute(), (req, res) -> {
                if (route.getRoute().split("\\.").length == 1) {
                    // if route is not found we are going to return the notfound file
                    // this would be set to the response getting back to the browser

                    // if the browswer is asking for lets say .css or .js file
                    // first the response would have the notfound.html as the returned type
                    // then if you go to executeRoute method in the route you would find that if
                    // a file is returned the return content would be overwritten with the required
                    // file, not the notfound.html file

                    System.err.println("Inside not found route handler");
                    res.setStatusCode(404);
                    try {
                        res.httpFileResponse("/notfound.html");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (route.getRoute().contains(".css") || route.getRoute().contains(".js")) {
                    System.err.println("Inside static file route handler");
                    try {
                        res.httpFileResponse(route.getRoute());
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
