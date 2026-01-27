package com.mycompany.app.Errors;

public class FileNotFound extends RuntimeException {
    public FileNotFound(String route) {
        super("File not found at " + route);
        this.printStackTrace();

    }

}
