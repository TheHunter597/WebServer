package com.mycompany.app.Handlers;

import java.util.HashMap;

public class FileParser {
    public static HashMap<String, String> parseMultipartFormData(String body, String boundary) {
        HashMap<String, String> result = new HashMap<>();

        String delimiter = "--" + boundary;
        String[] parts = body.split(delimiter);

        for (String part : parts) {
            part = part.trim();

            if (part.isEmpty() || part.equals("--")) {
                continue;
            }

            String[] sections = part.split("\r\n\r\n", 2);
            if (sections.length != 2) {
                continue;
            }

            String headers = sections[0];
            String content = sections[1];

            content = content.replaceAll("\r\n$", "");

            String name = null;

            for (String headerLine : headers.split("\r\n")) {
                headerLine = headerLine.trim();

                if (headerLine.startsWith("Content-Disposition")) {
                    String[] tokens = headerLine.split(";");
                    for (String token : tokens) {
                        token = token.trim();
                        if (token.startsWith("name=")) {
                            name = token.substring(5).replace("\"", "");
                        }
                    }
                }
            }

            if (name != null) {
                result.put(name, content);
            }
        }

        return result;
    }

}
