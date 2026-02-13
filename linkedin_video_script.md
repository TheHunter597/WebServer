# LinkedIn Video Script: "I Built an HTTP Server from Scratch in Java"

## 🎯 Video Concept

**Title**: "I Built an HTTP Server from Scratch (No Frameworks!)"  
**Duration**: 2:45 minutes  
**Target**: Backend engineers, recruiters, hiring managers  
**Hook**: Most developers use Express or Spring Boot—but what if you built the server itself?

---

## 🎬 Video Structure

### SEGMENT 1: The Hook (0:00 - 0:20)

**[Screen: Terminal or IDE visible]**

**YOU SAY:**  
"Most developers use Express.js or Spring Boot to build servers... but I wanted to understand what happens UNDER the hood. So I built an HTTP server from scratch using just Java sockets and raw TCP connections. No frameworks. Let me show you how it works."

**[Quick cut to server running in terminal]**

---

### SEGMENT 2: The Demo (0:20 - 1:00)

**[Screen: Split screen - browser on left, code on right]**

**YOU SAY:**  
"Here's my server running on port 8001. Watch what happens when I make a request..."

**[Navigate to localhost:8001 in browser, show your index.html loading]**

"The server parses the raw HTTP request, routes it, and serves static files. But here's where it gets interesting..."

**[Make a POST request using Postman or curl visible on screen]**

```bash
curl -X POST http://localhost:8001/users/create-user \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "secret123"}'
```

"It handles dynamic routes, JSON parsing, and database operations—all built from the ground up."

---

### SEGMENT 3: Code Deep Dive (1:00 - 2:00)

**[Screen: Your IDE with code visible]**

**YOU SAY:**  
"Let me show you the three core components that make this work..."

#### Part A: Socket Server (15 seconds)

**[Show Server.java or MainServerThread.java]**

```java
ServerSocket serverSocket = new ServerSocket(port);
while (true) {
    Socket clientSocket = serverSocket.accept();
    // Spawn thread to handle request
    executor.execute(new HandlerThread(clientSocket));
}
```

**YOU SAY:**  
"First, a ServerSocket listens on port 8001. When a request comes in, I spawn a new thread to handle it concurrently."

#### Part B: Request Parser (20 seconds)

**[Show RequestParser.java highlights]**

**YOU SAY:**  
"Next, the raw HTTP request gets parsed—I manually extract the method, path, headers, and body from the TCP stream."

**[Show code snippet]**

```java
// Parsing raw HTTP: "GET /users/123 HTTP/1.1"
String[] requestLine = firstLine.split(" ");
String method = requestLine[0];
String path = requestLine[1];
```

#### Part C: Routing & Database (25 seconds)

**[Show App.java with route definition]**

**YOU SAY:**  
"Then I built a routing system with lambda handlers, just like Express.js:"

```java
server.post("/users/create-user", (req, res, db) -> {
    var user = req.getBodyAsJson(RequestUser.class);
    db.updateOne(
        "INSERT INTO CUSTOM_USERS(USERNAME, PASSWORD) VALUES (?, ?)",
        user.username, user.password
    );
    res.setStatusCode(201);
    res.json("User created successfully");
    return res;
});
```

**YOU SAY:**  
"Notice the 'db' parameter? I built a custom JDBC template that provides database access to every route handler."

---

### SEGMENT 4: Key Technical Wins (2:00 - 2:30)

**[Screen: Quick bullet points with B-roll of code]**

**YOU SAY:**  
"Here's what I learned building this:

1. **Concurrency**: Managing thread pools to handle thousands of concurrent requests
2. **Protocol Deep Dive**: Understanding HTTP/1.1 specification—headers, status codes, chunked encoding
3. **Database Pooling**: Implementing connection management without an ORM
4. **Error Handling**: Custom exception hierarchy for different failure scenarios"

**[Show your custom exceptions folder structure quickly]**

---

### SEGMENT 5: The Closer (2:30 - 2:45)

**[Screen: Back to you, maybe with GitHub repo visible]**

**YOU SAY:**  
"This project taught me more about backend systems than any tutorial ever could. I went from 'Spring Boot magic' to understanding TCP sockets, thread safety, and protocol design.

If you're a recruiter or engineering leader looking for someone who doesn't just use frameworks but understands what's underneath them—let's talk. Link to the full code is in the comments."

**[Smile and end]**

---

## 🎨 Visual Tips for Recording

### Screen Setup

- **Split screen**: 60% IDE/Terminal, 40% Browser/Postman
- **Use a code theme** with good contrast (Monokai, Dracula, or Nord)
- **Zoom in**: Code should be readable on mobile (minimum 16pt font)

### Recording Flow

1. **Record in 1080p minimum** (1920x1080)
2. **Use screen recording** software like OBS Studio (free) or Loom
3. **Add yourself in corner** (picture-in-picture) for segments 1, 4, and 5
4. **Cut quickly** between segments—don't linger
5. **Add subtle background music** (low volume, non-distracting)

### Code Snippets to Show

Prepare these code blocks in advance (clean, well-commented):

1. **Server initialization** (Server.java)
2. **Route handler example** (App.java - uncomment one clean route)
3. **Request parsing** (RequestParser.java - the HTTP parsing logic)
4. **JDBC template** (JdbcTemplate.java - show the query method)

---

## 📝 LinkedIn Post Caption (to accompany video)

```
I built an HTTP server from scratch in Java—no Spring Boot, no frameworks. Just raw sockets. 🔧

Most developers (myself included) start with frameworks like Express or Spring Boot. But I wanted to understand what happens when you type "localhost:8001" in your browser.

This 3-week project taught me:
✅ TCP/IP and socket programming
✅ HTTP protocol internals (parsing, routing, headers)
✅ Concurrency patterns (thread pools, connection handling)
✅ Database connection pooling without an ORM
✅ Building APIs from first principles

The server handles:
- Static file serving with MIME type detection
- Dynamic routing with path parameters (/users/:id)
- JSON request/response parsing
- PostgreSQL integration with custom JDBC templates
- Cookie management and multipart form data

600+ lines of Java that gave me a deeper understanding of backend systems than any course could.

Open to backend engineering roles | GitHub in comments 👇

#Java #BackendEngineering #WebDevelopment #SoftwareEngineering #Coding
```

---

## 🎯 Why This Video Will Work

### For Recruiters

- **Shows depth**: You don't just use tools—you understand them
- **Problem-solving**: You tackle complex technical challenges
- **Communication**: You can explain technical concepts clearly
- **Initiative**: Self-driven project shows passion

### For Engineers

- **Educational**: They'll learn something watching
- **Impressive**: Not many people build HTTP servers from scratch
- **Specific**: Concrete technical details, not buzzwords
- **Relatable**: Everyone uses servers, few understand them

---

## ⚡ Pro Tips

### Before Recording

1. **Clean up App.java**: Uncomment 2-3 clean route examples (not all)
2. **Prepare database**: Have sample data in PostgreSQL to demo queries
3. **Test everything**: Make sure server starts and routes work
4. **Practice**: Run through the script 2-3 times

### During Recording

1. **Speak with energy**: You're excited about this project—show it!
2. **Slow down**: Technical content needs clear pacing
3. **Show, don't just tell**: Run actual requests, show real results
4. **Keep it tight**: If a segment runs long, cut it down in editing

### After Recording

1. **Add captions**: 85% of LinkedIn videos are watched without sound
2. **Post at peak times**: Tuesday-Thursday, 9-11 AM or 12-2 PM
3. **Engage in comments**: Respond to questions quickly
4. **Cross-post**: Share on Twitter, dev.to, Reddit (r/java, r/programming)

---

## 🔥 Alternative Hooks (Pick One)

If you want to experiment with different openings:

1. **The Challenge Hook**: "I gave myself a challenge: build a web server without using any frameworks. Here's what happened..."

2. **The Question Hook**: "What actually happens when you visit a website? I built a server from scratch to find out..."

3. **The Comparison Hook**: "Express.js: 20 lines. Spring Boot: 50 lines. My server from scratch: 600 lines. But here's what I learned..."

4. **The Mistake Hook**: "I thought frameworks were magic. Then I built an HTTP server from scratch and realized..."

---

Good luck with your video! Remember: authenticity beats perfection. Recruiters want to see your thought process and passion more than Hollywood-level production. 🚀
