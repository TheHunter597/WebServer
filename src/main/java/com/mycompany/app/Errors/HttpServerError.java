package com.mycompany.app.Errors;

public class HttpServerError extends RuntimeException {
    public HttpServerError(String message) {
        super(message);
        this.printStackTrace();
    }
}
