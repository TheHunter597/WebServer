package com.mycompany.app.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.DataTypeError;
import com.mycompany.app.Errors.FileNotFound;
import com.mycompany.app.Handlers.Cookie;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Getter
@Setter
@ToString
public class Response {
    private String method;
    private ArrayList<Cookie> cookies;
    private Integer statusCode;
    private String contentType;

    private String body = "";

    private HashMap<String, String> responseHeaders = new HashMap<String, String>();

    public static final Map<String, String> MIME_TYPES;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("html", "text/html");
        map.put("css", "text/css");
        map.put("js", "application/javascript");
        map.put("png", "image/png");
        map.put("jpg", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("gif", "image/gif");

        MIME_TYPES = Collections.unmodifiableMap(map);
    }

    public Response() {
        this.method = "POST";
        this.statusCode = 200;
        this.cookies = new ArrayList<Cookie>();
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public Cookie getCookie(String key) {
        try {
            return cookies.stream().filter((curr) -> curr.getName().equals(key)).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public void json(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value = mapper.writeValueAsString(data);
            this.contentType = "application/json";
            this.body = value;
        } catch (JacksonException e) {
            throw new DataTypeError("json");
        }
    }

    public void httpFileResponse(String route) throws IOException {

        int dot = route.lastIndexOf('.');

        this.setContentType(MIME_TYPES.getOrDefault(route.substring(dot + 1), "application/octet-stream"));

        String baseDir = ConfigurationManager.getInstance().getConfig().getBaseDir();
        File file = new File(baseDir + route).getCanonicalFile();

        if (!file.exists() || !file.getPath()
                .startsWith(new File(baseDir).getCanonicalPath())) {
            throw new FileNotFound(route);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
        byte[] current = new byte[2048];
        var read = 0;
        StringBuilder builder = new StringBuilder();
        try {
            while ((read = stream.read(current)) != -1) {
                String stringToAdd = new String(current, 0, read, java.nio.charset.StandardCharsets.UTF_8);
                builder.append(stringToAdd);
                current = new byte[2048];
            }
            this.body = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFound(route);
        }

    }

    public void setHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    public String createResponseSetCookieHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Cookie cookie : cookies) {
            stringBuilder.append(Cookie.createCookieHeader(cookie)).append("\r\n");
        }
        return stringBuilder.toString();
    }

    public String formulateResponseHeaders() {
        StringBuilder builder = new StringBuilder();
        this.setHeader("X-Powered-By", "TheHunterJavaServer/1.0");
        builder.append(String.format("HTTP/1.1 %s %s\r\n" +
                "Content-Type: %s; charset=UTF-8\r\n" +
                "Content-Length: " + this.getBody().length() + "\r\n" +
                "Connection: close\r\n" + "%s", this.getStatusCode(), HttpResponseText.loadText(statusCode),
                this.getContentType(), this.createResponseSetCookieHeaders()));
        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            builder.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        }
        builder.append("\r\n");
        return builder.toString();
    }
}
