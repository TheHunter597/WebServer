package com.mycompany.app.sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.HttpServerError;
import com.mycompany.app.Handlers.HttpDriver;
import com.mycompany.app.Postgres.JdbcTemplate;
import com.mycompany.app.Request.RequestHandler;
import com.mycompany.app.Request.RequestHandlerDB;
import com.mycompany.app.Response.HttpResponseText;
import com.mycompany.app.Response.Route;

public class Server {
    ExecutorService executor;
    ConfigurationManager manager;
    HttpDriver driver;
    public static JdbcTemplate jdbcTemplate;

    public static HashMap<String, ArrayList<Middleware>> middlewares = new HashMap<>();

    public Server() {
        this.executor = this.executor != null ? this.executor : Executors.newCachedThreadPool();
        this.manager = this.manager != null ? this.manager : ConfigurationManager.getInstance();
        this.driver = new HttpDriver();
    }

    public Server(Integer ThreadsNumber) {
        this();
        this.executor = Executors.newFixedThreadPool(ThreadsNumber);
    }

    public Server(Integer ThreadsNumber, ConfigurationManager manager) {
        this();
        this.manager = manager;
        this.executor = Executors.newFixedThreadPool(ThreadsNumber);
    }

    public void enableDatabaseConnection() {
        try {
            Server.jdbcTemplate = new JdbcTemplate(this.manager);
            System.err.println("Database connection enabled successfully");

        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpServerError("Failed to enable database connection: " + e.getMessage());
        }
    }

    public void start() throws IOException {
        if (HttpDriver.threadRoutes.size() == 0) {
            throw new HttpServerError("Please add routes to handle the requests");
        }
        MainServerThread mainThread = new MainServerThread(manager, this.executor, this.driver);
        HttpResponseText.loadHttpResponseText();
        mainThread.start();

    }

    public void addRoute(String method, String route, RequestHandler handler) {
        this.driver.addNewRoute(new Route(method, route, handler));
    }

    public void get(String route, RequestHandler handler) {
        this.driver.addNewRoute(new Route("GET", route, handler));
    }

    public void post(String route, RequestHandler handler) {
        this.driver.addNewRoute(new Route("POST", route, handler));
    }

    public void addRoute(String method, String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route(method, route, handler));
    }

    public void get(String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route("GET", route, handler));
    }

    public void post(String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route("POST", route, handler));
    }

    public void use(String path, Middleware middleware) {
        middlewares.compute(path, (key, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(middleware);
            return value;
        });

    }
}
