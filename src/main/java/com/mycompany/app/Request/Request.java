package com.mycompany.app.Request;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.mycompany.app.Handlers.FileParser;

import lombok.Getter;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Getter
public class Request {
    String request;
    String requestBody;

    HashMap<String, String> contentType;
    HashMap<String, String> coreData;
    HashMap<String, String> params;
    HashMap<String, String> headers;
    HashMap<String, String> cookies;
    HashMap<String, String> routeParameters;

    HashMap<String, String> File;

    public String getMethod() {
        return coreData.get("method");
    }

    public String getPath() {
        return coreData.get("path");
    }

    public String httpType() {
        return coreData.get("http");
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public Request(InputStream stream) throws IOException {
        request = processBody(stream);
        Request.requestPropertiesAssigner(request, this);
        if (contentType != null && contentType.get("Content-Type").equals("multipart/form-data")) {
            File = FileParser.parseMultipartFormData(requestBody, contentType.get("boundary"));
        }
    }

    public String requestBodyExtractor(String request) {
        var bodyIndex = request.indexOf("\r\n\r\n");
        String body = null;
        if (bodyIndex != -1) {
            body = request.substring(bodyIndex + 4, request.length());
        } else {
            body = "";
        }
        return body;
    }

    public JsonNode getBodyAsJson() {
        ObjectMapper om = new ObjectMapper();
        return om.readTree(requestBody);
    }

    public <T> T getBodyAsJson(Class<T> clazz) {

        Field[] fields = clazz.getFields();
        // This checks for the RequestParameterRequired annotation
        // should have just used jackson annotations but too late now
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestParmaterRequired) {
                    ObjectMapper om = new ObjectMapper();
                    try {
                        JsonNode jsonNode = om.readTree(requestBody);
                        if (!jsonNode.has(field.getName())) {
                            return null;
                        }
                    } catch (DatabindException e) {
                        return null;
                    }
                }
            }
        }
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(requestBody, clazz);
        } catch (DatabindException e) {
            return null;
        }
    }

    public String processBody(InputStream stream) throws IOException {

        // I hate this method

        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[4096];

        try {
            int read;
            while ((read = stream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
        }

        return sb.toString();
    }

    public static void requestPropertiesAssigner(String request, Request requestObject) {
        var funcs = Request.requestModifyingFunctions();

        var requestAndRequestObject = new HashMap<String, Object>();
        requestAndRequestObject.put("request", request);
        requestAndRequestObject.put("requestObject", requestObject);

        for (BiFunction<String, Request, HashMap<String, Object>> func : funcs) {
            requestAndRequestObject = func.apply((String) requestAndRequestObject.get("request"),
                    (Request) requestAndRequestObject.get("requestObject"));
        }
        request = requestAndRequestObject.get("request").toString();
        requestObject = (Request) requestAndRequestObject.get("requestObject");
    }

    public static List<BiFunction<String, Request, HashMap<String, Object>>> requestModifyingFunctions() {

        List<BiFunction<String, Request, HashMap<String, Object>>> funcs = new ArrayList<>(Arrays.asList(
                RequestParser::requestBodyExtractor,
                RequestParser::coreDataExtractor,
                RequestParser::requestParametersExtractor,
                RequestParser::requestHeadersExtractor,
                RequestParser::requestCookiesExtractor,
                RequestParser::requestRouteParametersExtractor,
                RequestParser::parseContentTypeHeader));

        return funcs;
    }

}
