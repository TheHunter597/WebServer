package com.mycompany.app;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.app.Handlers.Cookie;
import com.mycompany.app.sockets.Server;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        // Use this for not database connection server
        Server server = new Server.Builder().withThreads(3).build();

        // use this for database connection server,
        // make sure to set the environment variables for the database connection in the config file
        // Server server = new Server.Builder().enableDatabase().withThreads(3).build();
        server.get("/hello", (req, res) -> {
            res.setBody("hello there");
            var cookie = new Cookie("random", "randomvalue");
            res.addCookie(cookie);
            return res;
        });

        server.get("/json", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "sdf");
            res.json(map);
            return res;
        });
        server.use("/", (req, res) -> {
            logger.debug("Middleware for all routes");
        });
        server.get("/", (req, res) -> {
            try {
                res.httpFileResponse("/index.html");
            } catch (IOException e) {
                logger.error("Failed to serve index.html", e);
            }
            return res;
        });

        server.get("/users/:id", (req, res, db) -> {
            var idFromRequestParameters = req.getRouteParameters().get("id");
            User user = db.queryForSingleObject("select * from test_users where id = ?", User.class, idFromRequestParameters);
            res.json(user);
            return res;
        });

        server.get("/json/?id=str&age=int", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            logger.debug("Query parameters: {}", req.getParams());
            map.put("name", "sdf");
            res.json(map);
            return res;
        });

        server.post("/users/create-user", (req, res, db) -> {
            User body = req.getBodyAsJson(User.class);
            try {
                db.updateOne("INSERT INTO test_users (username,password) VALUES (?,?)", body.getUsername(), body.getPassword());
                User newUser = db.queryForSingleObject("SELECT * FROM TEST_USERS WHERE USERNAME = ?", User.class, body.getUsername());
                HashMap<String, Object> responseObject = new HashMap<>();
                responseObject.put("message", "User created sucessfully");
                responseObject.put("new-user", newUser);
                res.json(responseObject);
                return res;
            } catch (Exception e) {
                HashMap<String, Object> errorData = new HashMap<>();
                errorData.put("message", "Error happened while adding new user");
                errorData.put("error", e);
                res.json(errorData);
                return res;
            }

        });

        server.get("/json/?id=str", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "Mohamed");
            res.json(map);
            return res;
        });
        server.get("/json/?id=str&age=int", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "Mohamed");
            res.json(map);
            return res;
        });

        server.get("/users", (req, res, db) -> {
            try {
                var users = db.query(
                        "SELECT id, username, password FROM test_users",
                        rs -> {
                            var usersList = new java.util.ArrayList<User>();
                            while (rs.next()) {
                                User user = new User();
                                user.setId(rs.getLong("id"));
                                user.setUsername(rs.getString("username"));
                                user.setPassword(rs.getString("password"));
                                usersList.add(user);
                            }
                            return usersList;
                        });
                res.setStatusCode(200);
                res.json(users);
                return res;
            } catch (Exception e) {
                logger.error("Error retrieving users", e);
                res.setStatusCode(500);
                HashMap<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Error retrieving users: " + e.getMessage());
                res.json(errorResponse);
                return res;
            }
        });
        server.start();

    }

}
