package com.mycompany.app.sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ExecutorService executor;
    private ConfigurationManager manager;
    private HttpDriver driver;
    public static JdbcTemplate jdbcTemplate;
    public static HashMap<String, ArrayList<Middleware>> middlewares = new HashMap<>();

    private Server(Builder builder) {
        this.executor = builder.executor;
        this.manager = builder.manager;
        this.driver = builder.driver != null ? builder.driver : new HttpDriver();
    }

    public static class Builder {
        private ExecutorService executor = Executors.newCachedThreadPool();
        private ConfigurationManager manager = ConfigurationManager.getInstance();
        private HttpDriver driver;
        private boolean enableDatabase = false;

        public Builder() {
        }

        public Builder withThreads(int threadCount) {
            this.executor = Executors.newFixedThreadPool(threadCount);
            return this;
        }

        public Builder withCachedThreadPool() {
            this.executor = Executors.newCachedThreadPool();
            return this;
        }

        public Builder withExecutor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder withConfiguration(ConfigurationManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder withDriver(HttpDriver driver) {
            this.driver = driver;
            return this;
        }

        public Builder enableDatabase() {
            this.enableDatabase = true;
            return this;
        }

        public Server build() {
            Server server = new Server(this);

            if (enableDatabase) {
                server.enableDatabaseConnection();
            }

            return server;
        }
    }

    private void enableDatabaseConnection() {
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

    public void put(String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route("PUT", route, handler));
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