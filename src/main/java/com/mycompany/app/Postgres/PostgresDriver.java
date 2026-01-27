package com.mycompany.app.Postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.DB.PostgresDatabaseConnectionError;

abstract public class PostgresDriver {
    ConfigurationManager manager;

    public PostgresDriver(ConfigurationManager manager) {
        this.manager = manager;
    }

    public Connection createConnection(Properties customConfigs) {
        Properties props = new Properties();
        props.setProperty("user", manager.getConfig().getJdbcPostgresUser());
        props.setProperty("password", manager.getConfig().getJdbcPostgresPassword());
        Connection conn;

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", props);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
        return conn;
    }

    public Connection createConnection() {
        return this.createConnection(null);
    }

    public Statement createStatement() {
        try {
            Connection conn = this.createConnection();
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public Statement createStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public PreparedStatement getPreparedStatement(String sqlQuery, Connection conn) {
        try {
            return conn.prepareStatement(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public PreparedStatement getPreparedStatement(String sqlQuery) {
        Connection conn = this.createConnection();
        return this.getPreparedStatement(sqlQuery, conn);
    }

}
