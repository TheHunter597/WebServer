package com.mycompany.app.Postgres;

@FunctionalInterface
public interface ResultSetExtractor<T> {
    public T extractData(java.sql.ResultSet rs) throws java.sql.SQLException;
}
