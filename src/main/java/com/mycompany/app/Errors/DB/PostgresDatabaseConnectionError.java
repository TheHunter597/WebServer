package com.mycompany.app.Errors.DB;

public class PostgresDatabaseConnectionError extends RuntimeException {
    public PostgresDatabaseConnectionError(String message) {
        super(message);
    }

}
