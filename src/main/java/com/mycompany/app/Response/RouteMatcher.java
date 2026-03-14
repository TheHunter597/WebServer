package com.mycompany.app.Response;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteMatcher {

    private static final Logger logger = LoggerFactory.getLogger(RouteMatcher.class);

    // this matches makes sure that the route pattern matches the actual request path,
    // including path parameters and query parameters. It also checks that the HTTP method matches.
    public static boolean matches(String pattern, String actual, String patternMethod, String actualMethod) {
        if (!patternMethod.equals(actualMethod)) {
            return false;
        }

        String patternPath = pathOnly(pattern);
        String actualPath = pathOnly(actual);
        String[] patternParts = patternPath.split("/");
        String[] actualParts = actualPath.split("/");

        // Check path lengths match
        if (patternParts.length != actualParts.length) {
            return false;
        }

        // Check path parts match
        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            String actualPart = actualParts[i];

            if (!patternPart.startsWith(":") && !patternPart.equals(actualPart)) {
                return false;
            }
        }

        // Parse query parameters
        Map<String, String> patternParams = parseQuery(queryOnly(pattern));
        Map<String, String> actualParams = parseQuery(queryOnly(actual));

        // If the number of query params differs, routes don't match
        if (patternParams.size() != actualParams.size()) {
            logger.debug("Query param count mismatch: {} vs {}", patternParams.size(), actualParams.size());
            return false;
        }

        // Check that ALL pattern parameters exist in the request and match types
        for (var entry : patternParams.entrySet()) {
            String key = entry.getKey();
            String expectedType = entry.getValue();

            if (!actualParams.containsKey(key)) {
                logger.debug("Missing required query parameter: {}", key);
                return false;
            }

            String actualValue = actualParams.get(key);
            if (!matchesType(expectedType, actualValue)) {
                logger.debug("Type mismatch for parameter {}: expected {}, got {}", key, expectedType, actualValue);
                return false;
            }
        }

        return true;
    }

    public static String pathOnly(String url) {
        int q = url.indexOf('?');
        return q == -1 ? url : url.substring(0, q);
    }

    public static String queryOnly(String url) {
        int q = url.indexOf('?');
        return q == -1 ? "" : url.substring(q + 1);
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query.isEmpty()) {
            return map;
        }

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        logger.debug("Parsed query parameters: {} from query string: {}", map, query);
        return map;
    }

    public static boolean matchesType(String pattern, String value) {
        return switch (pattern) {
            case "int" ->
                value.matches("-?\\d+");
            case "str" ->
                value.matches("[a-zA-Z_]+");
            case "*" ->
                true;
            default ->
                pattern.equals(value);
        };
    }
}
