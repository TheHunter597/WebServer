package com.mycompany.app.Postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.DB.PostgresDatabaseConnectionError;

public class JdbcTemplate extends PostgresDriver {

    public JdbcTemplate(ConfigurationManager manager) {
        super(manager);
    }

    public ResultSet query(String sqlQuery) {
        try {
            java.sql.Statement stmt = this.createStatement();
            return stmt.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public ResultSet query(String sqlQuery, Connection conn) {
        try {
            java.sql.Statement stmt = this.createStatement(conn);
            return stmt.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public <T> T query(String sqlQuery, ResultSetExtractor<T> extractor) {
        try {
            java.sql.Statement stmt = this.createStatement();
            return extractor.extractData(stmt.executeQuery(sqlQuery));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public <T> T query(PreparedStatement statement, ResultSetExtractor<T> extractor) {
        try {
            return extractor.extractData(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public <T> T query(PreparedStatement statement, ResultSetExtractor<T> extractor, Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return extractor.extractData(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public <T> T queryForSingleObject(String sqlQuery, ResultSetExtractor<T> extractor) {
        try {
            java.sql.Statement stmt = this.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            if (rs.next()) {
                return extractor.extractData(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public <T> List<T> queryForSingleColumnList(String sqlQuery, Class<T> elementType) {
        try {
            java.sql.Statement stmt = this.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            ResultSetMetaData rsmd = rs.getMetaData();

            if (rsmd.getColumnCount() != 1) {
                throw new IllegalArgumentException("The query must return exactly one column");
            }
            java.util.ArrayList<T> list = new java.util.ArrayList<>();
            while (rs.next()) {
                list.add(rs.getObject(1, elementType));

            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public Integer updateOne(String sqlUpdate) throws PostgresDatabaseConnectionError {
        try {
            Connection conn = this.createConnection();
            conn.setAutoCommit(false);
            java.sql.Statement stmt = this.createStatement(conn);
            var result = stmt.executeUpdate(sqlUpdate);
            if (result != 1) {
                conn.rollback();
                throw new PostgresDatabaseConnectionError(
                        "Expected to update one row, but updated " + result + " rows.");
            }
            conn.commit();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public Integer updateOne(String sqlUpdate, Object... params) throws PostgresDatabaseConnectionError {
        try {
            Connection conn = this.createConnection();
            conn.setAutoCommit(false);
            java.sql.PreparedStatement stmt = this.getPreparedStatement(sqlUpdate, conn);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            var result = stmt.executeUpdate();
            if (result != 1) {
                conn.rollback();
                throw new PostgresDatabaseConnectionError(
                        "Expected to update one row, but updated " + result + " rows.");
            }
            conn.commit();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

}
