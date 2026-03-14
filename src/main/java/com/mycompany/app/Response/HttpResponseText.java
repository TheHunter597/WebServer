package com.mycompany.app.Response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseText {

    // Static map holding all status code -> reason phrases
    private static final Map<Integer, String> STATUS_TEXT = new HashMap<>();

    // Load the map (call this once at startup)
    public static void loadHttpResponseText() {
        // 1xx Informational
        STATUS_TEXT.put(100, "Continue");
        STATUS_TEXT.put(101, "Switching Protocols");
        STATUS_TEXT.put(102, "Processing");
        STATUS_TEXT.put(103, "Early Hints");

        // 2xx Success
        STATUS_TEXT.put(200, "OK");
        STATUS_TEXT.put(201, "Created");
        STATUS_TEXT.put(202, "Accepted");
        STATUS_TEXT.put(203, "Non-Authoritative Information");
        STATUS_TEXT.put(204, "No Content");
        STATUS_TEXT.put(205, "Reset Content");
        STATUS_TEXT.put(206, "Partial Content");
        STATUS_TEXT.put(207, "Multi-Status");
        STATUS_TEXT.put(208, "Already Reported");
        STATUS_TEXT.put(226, "IM Used");

        // 3xx Redirection
        STATUS_TEXT.put(300, "Multiple Choices");
        STATUS_TEXT.put(301, "Moved Permanently");
        STATUS_TEXT.put(302, "Found");
        STATUS_TEXT.put(303, "See Other");
        STATUS_TEXT.put(304, "Not Modified");
        STATUS_TEXT.put(305, "Use Proxy");
        STATUS_TEXT.put(307, "Temporary Redirect");
        STATUS_TEXT.put(308, "Permanent Redirect");

        // 4xx Client Errors
        STATUS_TEXT.put(400, "Bad Request");
        STATUS_TEXT.put(401, "Unauthorized");
        STATUS_TEXT.put(402, "Payment Required");
        STATUS_TEXT.put(403, "Forbidden");
        STATUS_TEXT.put(404, "Not Found");
        STATUS_TEXT.put(405, "Method Not Allowed");
        STATUS_TEXT.put(406, "Not Acceptable");
        STATUS_TEXT.put(407, "Proxy Authentication Required");
        STATUS_TEXT.put(408, "Request Timeout");
        STATUS_TEXT.put(409, "Conflict");
        STATUS_TEXT.put(410, "Gone");
        STATUS_TEXT.put(411, "Length Required");
        STATUS_TEXT.put(412, "Precondition Failed");
        STATUS_TEXT.put(413, "Payload Too Large");
        STATUS_TEXT.put(414, "URI Too Long");
        STATUS_TEXT.put(415, "Unsupported Media Type");
        STATUS_TEXT.put(416, "Range Not Satisfiable");
        STATUS_TEXT.put(417, "Expectation Failed");
        STATUS_TEXT.put(418, "I'm a teapot");
        STATUS_TEXT.put(421, "Misdirected Request");
        STATUS_TEXT.put(422, "Unprocessable Entity");
        STATUS_TEXT.put(423, "Locked");
        STATUS_TEXT.put(424, "Failed Dependency");
        STATUS_TEXT.put(425, "Too Early");
        STATUS_TEXT.put(426, "Upgrade Required");
        STATUS_TEXT.put(428, "Precondition Required");
        STATUS_TEXT.put(429, "Too Many Requests");
        STATUS_TEXT.put(431, "Request Header Fields Too Large");
        STATUS_TEXT.put(451, "Unavailable For Legal Reasons");

        // 5xx Server Errors
        STATUS_TEXT.put(500, "Internal Server Error");
        STATUS_TEXT.put(501, "Not Implemented");
        STATUS_TEXT.put(502, "Bad Gateway");
        STATUS_TEXT.put(503, "Service Unavailable");
        STATUS_TEXT.put(504, "Gateway Timeout");
        STATUS_TEXT.put(505, "HTTP Version Not Supported");
        STATUS_TEXT.put(506, "Variant Also Negotiates");
        STATUS_TEXT.put(507, "Insufficient Storage");
        STATUS_TEXT.put(508, "Loop Detected");
        STATUS_TEXT.put(510, "Not Extended");
        STATUS_TEXT.put(511, "Network Authentication Required");
    }

    // Get the reason phrase for a given status code
    public static String loadText(Integer statusCode) {
        return STATUS_TEXT.getOrDefault(statusCode, "Unknown Status");
    }

    // Optional: convenience method to get full status line
    public static String getStatusLine(int statusCode) {
        return statusCode + " " + loadText(statusCode);
    }
}
