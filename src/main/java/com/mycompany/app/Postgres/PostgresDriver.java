package com.mycompany.app.Postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.DB.PostgresDatabaseConnectionError;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

abstract public class PostgresDriver {

    private static final Logger logger = LoggerFactory.getLogger(PostgresDriver.class);
    ConfigurationManager manager;
    HikariConfig hikariConfig;
    HikariDataSource dataSource;

    public PostgresDriver(ConfigurationManager manager) {
        this.manager = manager;
        this.hikariConfig = new HikariConfig();
        this.hikariConfig.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s",
                manager.getConfig().getJdbcPostgresHost(),
                manager.getConfig().getJdbcPostgresPort(),
                manager.getConfig().getJdbcPostgresDatabase()));
        this.hikariConfig.setUsername(manager.getConfig().getJdbcPostgresUser());
        this.hikariConfig.setPassword(manager.getConfig().getJdbcPostgresPassword());
        this.dataSource = new HikariDataSource(this.hikariConfig);
        logger.info("PostgresDriver initialized successfully with HikariCP");
    }

    public Connection createConnection(Properties customConfigs) {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to create database connection", e);
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public Connection createConnection() {
        return this.createConnection(null);
    }

    public Statement createStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            logger.error("Failed to create statement", e);
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public PreparedStatement getPreparedStatement(String sqlQuery, Connection conn) {
        try {
            return conn.prepareStatement(sqlQuery);
        } catch (SQLException e) {
            logger.error("Failed to create prepared statement", e);
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

}
