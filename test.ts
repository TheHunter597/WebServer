
        server.get("/users", (req, res, db) -> {
            var result = db.query("SELECT * FROM CUSTOM_USERS", (rs) -> {
                var users = new java.util.ArrayList<CustomUsers>();
                while (rs.next()) {
                    CustomUsers user = new CustomUsers();
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    users.add(user);
                }
                return users;
            });
            res.json(result);
            return res;
        });

        server.post("/users/create-user", (req, res, db) -> {
            var requestData = req.getBodyAsJson(RequestUser.class);
            System.err.println("Request data: " + requestData);
            db.updateOne("""
                    INSERT INTO CUSTOM_USERS(USERNAME,PASSWORD)
                    VALUES (?, ?)
                    """, requestData.username, requestData.password);
            res.setStatusCode(201);
            res.json("User created successfully");
            return res;
        });

        server.get("/", (req, res) -> {
            res.setStatusCode(200);
            try {
                res.httpFileResponse("/index.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        });
        server.start();
