package com.mycompany.app;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.mycompany.app.Handlers.Cookie;
import com.mycompany.app.Request.RequestParmaterRequired;
import com.mycompany.app.sockets.Server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
class RequestUser {
    @RequestParmaterRequired
    public String username;
    @RequestParmaterRequired
    public Integer id;

    public RequestUser() {
    }
}

public class App {
    public static void main(String[] args) throws IOException {
        Server server = new Server(3);
        server.enableDatabaseConnection();
        server.addRoute("GET", "/", (req, res) -> {
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
        server.get("/json/?id=str/?age=int", (req, res) -> {
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

        server.post("/users/update", (req, res, db) -> {
            var requestData = req.getBodyAsJson(RequestUser.class);
            if (requestData == null) {
                res.setStatusCode(400);
                res.json("Invalid JSON body");
                return res;
            }

            try {
                db.updateOne("""
                        UPDATE CUSTOM_USERS
                        SET USERNAME = ?
                        WHERE ID = ?
                        """, requestData.username, requestData.id);
                var updatedUser = db.queryForSingleObject(
                        "SELECT id, username, password FROM CUSTOM_USERS WHERE id = ?",
                        User.class, requestData.id);
                System.err.println("Updated user: " + updatedUser);
                if (updatedUser == null) {
                    res.setStatusCode(404);
                    res.json("User not found after update");
                    return res;
                }

                res.setStatusCode(200);
                res.json(updatedUser);
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatusCode(500);
                HashMap<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Error retrieving updated user: " + e.getMessage());
                res.json(errorResponse);
                return res;
            }

        });
        server.start();
    }
}
