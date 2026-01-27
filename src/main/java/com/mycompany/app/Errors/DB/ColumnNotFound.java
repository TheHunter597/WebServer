package com.mycompany.app.Errors.DB;

public class ColumnNotFound extends RuntimeException {
    public ColumnNotFound(String message) {
        super(message);
    }

}
