package com.mycompany.app.Request;

import com.mycompany.app.Response.Response;

@FunctionalInterface
public interface RequestHandler {
    Response apply(Request req, Response res);

}
