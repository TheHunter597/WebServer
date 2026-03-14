package com.mycompany.app.Request;

import com.mycompany.app.Postgres.JdbcTemplate;
import com.mycompany.app.Response.Response;

@FunctionalInterface
public interface RequestHandlerDB {
    Response apply(Request req, Response res, JdbcTemplate jdbcTemplate);

}
