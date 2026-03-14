package com.mycompany.app.sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.HttpServerError;
import com.mycompany.app.Handlers.HttpDriver;
import com.mycompany.app.Postgres.JdbcTemplate;
import com.mycompany.app.Request.RequestHandler;
import com.mycompany.app.Request.RequestHandlerDB;
import com.mycompany.app.Response.HttpResponseText;
import com.mycompany.app.Response.Route;

public class Server {

    private final ExecutorService executor;
    private final ConfigurationManager manager;
    private final HttpDriver driver;
    private JdbcTemplate jdbcTemplate;
    private final HashMap<String, ArrayList<Middleware>> middlewares = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public HashMap<String, ArrayList<Middleware>> getMiddlewares() {
        return middlewares;
    }

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
            this.jdbcTemplate = new JdbcTemplate(this.manager);
            logger.info("Database connection enabled successfully");
        } catch (Exception e) {
            logger.error("Failed to enable database connection", e);
            throw new HttpServerError("Failed to enable database connection: " + e.getMessage());
        }
    }

    public void start() throws IOException {
        if (HttpDriver.threadRoutes.isEmpty()) {
            throw new HttpServerError("Please add routes to handle the requests");
        }
        MainServerThread mainThread = new MainServerThread(manager, this.executor, this.driver, this);
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

    public void put(String route, RequestHandler handler) {
        this.driver.addNewRoute(new Route("PUT", route, handler));
    }

    public void delete(String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route("DELETE", route, handler));
    }

    public void delete(String route, RequestHandler handler) {
        this.driver.addNewRoute(new Route("DELETE", route, handler));
    }

    public void patch(String route, RequestHandler handler) {
        this.driver.addNewRoute(new Route("PATCH", route, handler));
    }

    public void patch(String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route("PATCH", route, handler));
    }

    public void options(String route, RequestHandlerDB handler) {
        this.driver.addNewRoute(new Route("OPTIONS", route, handler));
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
