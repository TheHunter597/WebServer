package com.mycompany.app;

import java.io.IOException;
import java.util.HashMap;

import com.mycompany.app.Request.RequestParmaterRequired;
import com.mycompany.app.sockets.Server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
class CustomUsers {
    private String username;
    private String password;
}

class RequestUser {
    public String username;
    public String password;

    @Override
    public String toString() {
        return "RequestUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

public class App {
    public static void main(String[] args) throws IOException {

        Server server = new Server.Builder().enableDatabase().withCachedThreadPool().build();

        server.get("/hello", (req, res) -> {
            res.setHeader("Content-Type", "application/text");
            res.setBody("Hello there");
            return res;
        });

        server.get("/", (req, res) ->

        {
            res.setStatusCode(200);
            try {
                res.httpFileResponse("/index.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;

        });

        server.get("/json", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "Mohamed");
            res.json(map);
            return res;
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
        server.post("/data", (req, res) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "Mohamed");
            res.json(map);
            return res;
        });
        server.post("/upload", (req, res) -> {
            res.setHeader("Authorization", "test");
            return res;
        });
        server.post("/upload/:data", (req, res) -> {
            res.setHeader("Authorization", "test");
            System.err.println(req.getRouteParameters());
            return res;
        });
        server.post("/upload/:data/:mango", (req, res) -> {
            res.setHeader("Authorization", "test");
            return res;
        });

        server.get("/users", (req, res, db) -> {
            try {
                var users = db.query(
                        "SELECT id, username, password FROM CUSTOM_USERS",
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
                e.printStackTrace();
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
