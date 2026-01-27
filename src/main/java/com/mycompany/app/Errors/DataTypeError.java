package com.mycompany.app.Errors;

public class DataTypeError extends RuntimeException {
    public DataTypeError(String dataType) {
        super("Data provided is of wrong type expected " + dataType);
        this.printStackTrace();

    }
}
