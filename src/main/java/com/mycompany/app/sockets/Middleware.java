package com.mycompany.app.sockets;

import com.mycompany.app.Request.Request;
import com.mycompany.app.Response.Response;

@FunctionalInterface
public interface Middleware {
    void apply(Request req, Response res);

}
