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
        server.use("/", (req, res) -> {
            System.err.println("I ran");
            res.setStatusCode(300);
        });

        server.post("/file", (req, res) -> {
            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Content-Type");
            try {
                FileWriter writer = new FileWriter(req.getFile().get("name"), true);
                writer.write(req.getFile().get("chunk"));
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            res.setStatusCode(200);
            return res;
        });

        server.get("/users", (req, res, db) -> {
            @Setter
            @Getter
            class User {
                private int id;
                private String username;
                private String password;

            }
            var requestData = req.getBodyAsJson(RequestUser.class);
            if (requestData == null) {
                res.setStatusCode(400);
                res.json("Invalid JSON body");
                return res;
            }

            var result = db.updateOne("""
                        UPDATE CUSTOM_USERS
                        SET USERNAME = ?
                        WHERE ID = ?
                    """, requestData.username, requestData.id);
            res.setStatusCode(200);
            Cookie cookie = new Cookie("session_id", "abc123xyz");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            res.addCookie(cookie);
            res.json(result);
            return res;
        });

        server.start();
    }
}
