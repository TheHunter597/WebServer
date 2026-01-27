# Test Web Server

A lightweight Java HTTP server with PostgreSQL integration, built from scratch using sockets and JDBC. Serves static files, handles dynamic routes, and provides database connectivity for building full-stack applications.

Note: I Used AI agent to create this README.md file, the server is written by me but this file is written by AI cause I think it dose way better job in documenting than I would every do.

## Features

- **Socket-based HTTP Server**: Handles concurrent requests with thread-per-connection model
- **Static File Serving**: Automatically serves files from the `www` directory with correct MIME types
- **Dynamic Routing**: Support for GET, POST, and middleware routes with dynamic parameters
- **PostgreSQL Integration**: Built-in JDBC connectivity via `JdbcTemplate`
- **Request/Response API**: Clean abstraction for handling HTTP interactions
- **Cookie Support**: Parse incoming cookies and set response cookies
- **Multipart Form Data**: Handle file uploads via multipart form data
- **Error Handling**: Custom exceptions for database, file, and HTTP errors

## Quick Start

### Prerequisites

- Java 21+
- PostgreSQL 12+
- Maven 3.6+

### Configuration

Edit `src/main/resources/default-config.json`:

```json
{
  "port": "8001",
  "ApplicationName": "Test",
  "baseDir": "www",
  "jdbcPostgresPort": "5432",
  "jdbcPostgresHost": "localhost",
  "jdbcPostgresDatabase": "mydb",
  "jdbcPostgresUser": "admin",
  "jdbcPostgresPassword": "secret"
}
```

### Build & Run

```bash
cd my-app
mvn clean install
mvn exec:java -Dexec.mainClass="com.mycompany.app.App"
```

The server starts on `http://localhost:8001` by default.

## Documentation

Visit `/index.html` for full API documentation including:

- **Request** API: Access method, path, headers, parameters, body, JSON parsing
- **Response** API: Set status, headers, body, JSON serialization, file serving
- **JdbcTemplate** API: Query, update, single object extraction, and list results
- **Handlers**: `RequestHandler` for static routes, `RequestHandlerDB` for database routes
- **Examples**: Code samples for common patterns

## Server Use Cases

`App.java` contains example routes demonstrating common patterns:

- **Static files**: Serve HTML, CSS, JS from `www` directory
- **JSON endpoints**: Return JSON from in-memory data or database
- **Parameterized routes**: Dynamic segments like `/upload/:data/:mango`
- **File uploads**: Handle multipart form-data with chunked uploads
- **Database updates**: Execute SQL with parameter binding and transaction control
- **Cookie handling**: Set secure, HTTP-only cookies with custom paths
- **Request validation**: Deserialize JSON with required field checking

**Note**: These use cases are examples and can be removed if not needed. Just delete the route definitions in `App.java` and keep only your custom routes.

## Project Structure

```
my-app/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/app/
│   │   │   ├── App.java                    (Main entry point with route examples)
│   │   │   ├── Config/                     (Configuration management)
│   │   │   ├── Errors/                     (Custom exception types)
│   │   │   ├── Handlers/                   (HTTP and file handling)
│   │   │   ├── Postgres/                   (JDBC and JdbcTemplate)
│   │   │   ├── Request/                    (Request parsing)
│   │   │   ├── Response/                   (Response building)
│   │   │   └── sockets/                    (Server socket management)
│   │   └── resources/default-config.json   (Server configuration)
│   └── test/
└── www/                                    (Static file root)
    ├── index.html                          (API documentation)
    └── styles.css                          (Documentation styles)
```

## Key Classes

| Class              | Purpose                                                  |
| ------------------ | -------------------------------------------------------- |
| `Server`           | Main socket server, route registration, request dispatch |
| `Request`          | Parsed HTTP request with headers, params, body           |
| `Response`         | HTTP response building with status, headers, body        |
| `RequestHandler`   | Functional interface for static routes                   |
| `RequestHandlerDB` | Extends RequestHandler with JdbcTemplate parameter       |
| `JdbcTemplate`     | JDBC wrapper for queries, updates, single objects        |
| `Cookie`           | HTTP cookie with secure, httpOnly, path, domain settings |

## Basic Example

```java
Server server = new Server(3);

server.get("/api/users", (req, res, db) -> {
  String sql = "SELECT id, username FROM users LIMIT 10";
  var users = db.query(sql, rs -> {
    var list = new java.util.ArrayList<Map<String, Object>>();
    while (rs.next()) {
      var map = new HashMap<String, Object>();
      map.put("id", rs.getInt("id"));
      map.put("username", rs.getString("username"));
      list.add(map);
    }
    return list;
  });
  res.json(users);
  return res;
});

server.start();
```

## Response Codes

| Route    | Method | Purpose                   |
| -------- | ------ | ------------------------- |
| `/`      | GET    | Serves `index.html`       |
| `/json`  | GET    | Returns JSON response     |
| `/data`  | POST   | Accepts JSON body         |
| `/file`  | POST   | Handles multipart uploads |
| `/users` | GET    | Database query example    |

## Notes

- The server is **not production-ready**; use for learning and local development only
- Passwords should not be stored in plain text JSON; this is a limitation for demo purposes
- Each request runs on its own thread; connection pooling is not implemented
- Error handling via custom exception types; catch `PostgresDatabaseConnectionError` for DB issues
- File uploads are unbuffered; large files may cause memory issues

## License

Educational use only.
