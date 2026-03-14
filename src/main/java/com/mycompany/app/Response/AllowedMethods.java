package com.mycompany.app.Response;

import java.util.ArrayList;

public class AllowedMethods {

    public static final ArrayList<String> allowedMethods;

    static {
        allowedMethods = new ArrayList<>();
        allowedMethods.add("GET");
        allowedMethods.add("POST");
        allowedMethods.add("PUT");
        allowedMethods.add("OPTIONS");
        allowedMethods.add("DELETE");
        allowedMethods.add("HEAD");
        allowedMethods.add("PATCH");
    }
}
