package com.mycompany.app.Postgres;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Errors.DB.PostgresDatabaseConnectionError;

public class JdbcTemplate extends PostgresDriver {

    public JdbcTemplate(ConfigurationManager manager) {
        super(manager);
    }

    public ResultSet query(String sqlQuery) {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = this.createConnection();
            stmt = this.createStatement(conn);
            return stmt.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public ResultSet query(String sqlQuery, Connection conn) {
        java.sql.Statement stmt = null;
        try {
            stmt = this.createStatement(conn);
            return stmt.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    public <T> T query(String sqlQuery, ResultSetExtractor<T> extractor) {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = this.createConnection();
            stmt = this.createStatement(conn);
            T result = extractor.extractData(stmt.executeQuery(sqlQuery));
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T query(PreparedStatement statement, ResultSetExtractor<T> extractor) {
        try {
            return extractor.extractData(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T queryForSingleObject(String sqlQuery, ResultSetExtractor<T> extractor) {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = this.createConnection();
            stmt = this.createStatement(conn);
            ResultSet rs = stmt.executeQuery(sqlQuery);
            if (rs.next()) {
                return extractor.extractData(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T queryForSingleObject(String sqlQuery, ResultSetExtractor<T> extractor, Object... params) {
        Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        try {
            conn = this.createConnection();
            stmt = this.getPreparedStatement(sqlQuery, conn);

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i], stmt.getParameterMetaData().getParameterType(i + 1));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractor.extractData(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PostgresDatabaseConnectionError(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Object convert(Object value, Class<?> targetType) {

        if (value == null)
            return null;

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (targetType == Integer.class || targetType == int.class) {
            return ((Number) value).intValue();
        }

        if (targetType == Long.class || targetType == long.class) {
            return ((Number) value).longValue();
        }

        if (targetType == Double.class || targetType == double.class) {
            return ((Number) value).doubleValue();
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            return value instanceof Boolean
                    ? value
                    : Boolean.parseBoolean(value.toString());
        }

        if (targetType == String.class) {
            return value.toString();
        }

        throw new IllegalArgumentException(
                "Unsupported conversion from " +
                        value.getClass().getName() +
                        " to " + targetType.getName());
    }

    private Map<String, Method> findSetters(Class<?> clazz) {
        Map<String, Method> setters = new HashMap<>();

        for (Method method : clazz.getMethods()) {
            if (!method.getName().startsWith("set"))
                continue;
            if (method.getParameterCount() != 1)
                continue;

            String property = Character.toLowerCase(method.getName().charAt(3)) +
                    method.getName().substring(4);

            setters.put(property.toLowerCase(), method);
        }

        return setters;
    }

    private <T> T mapRow(ResultSet rs, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            Map<String, Method> setters = findSetters(clazz);

            for (int i = 1; i <= columnCount; i++) {
                String column = meta.getColumnLabel(i);
                Method setter = setters.get(column.toLowerCase());

                if (setter == null) {
                    continue;
                }

                Object value = rs.getObject(i);
                Object converted = convert(value, setter.getParameterTypes()[0]);

                setter.invoke(instance, converted);
            }

            return instance;

        } catch (ReflectiveOperationException | SQLException e) {
            throw new RuntimeException(
                    "Failed to map result set to " + clazz.getName(), e);
        }
    }

    public <T> T queryForSingleObject(
            String sql,
            Class<T> clazz,
            Object... params) {

        try (
                Connection conn = createConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                T result = mapRow(rs, clazz);

                if (rs.next()) {
                    throw new IllegalStateException(
                            "Expected one row but query returned more than one");
                }

                return result;
            }

        } catch (SQLException e) {
            throw new PostgresDatabaseConnectionError(e.getMessage());
        }
    }

    // public <T> T queryForSingleObject(String sqlQuery, Class<T> clazz, Object...
    // params) throws Exception {
    // Connection conn = null;
    // java.sql.PreparedStatement stmt = null;
    // try {
    // conn = this.createConnection();
    // stmt = this.getPreparedStatement(sqlQuery, conn);
    // for (int i = 0; i < params.length; i++) {
    // stmt.setObject(i + 1, params[i]);
    // }
    // ResultSet rs = stmt.executeQuery();
    // ResultSetMetaData rsmd = rs.getMetaData();

    // if (rsmd.getColumnCount() != clazz.getDeclaredFields().length) {
    // throw new IllegalArgumentException(
    // "The number of columns in the result set does not match the number of fields
    // in the class "
    // + clazz.getName());
    // }
    // rs.next();

    // try {
    // Field[] fields = clazz.getDeclaredFields();
    // Method[] methods = clazz.getDeclaredMethods();
    // T classInstance = clazz.getConstructor().newInstance();

    // for (Field field : fields) {
    // String fieldName = field.getName();
    // String setterName = "set" + Character.toUpperCase(fieldName.charAt(0))
    // + fieldName.substring(1);
    // for (Method method : methods) {
    // if (method.getName().equals(setterName)) {
    // Object value = rs.getObject(fieldName, field.getType());
    // method.invoke(classInstance, value);
    // break;
    // }
    // }
    // }
    // return classInstance;
    // } catch (InstantiationException | IllegalAccessException |
    // IllegalArgumentException
    // | InvocationTargetException | NoSuchMethodException | SecurityException e) {
    // e.printStackTrace();
    // throw new Exception(
    // "Please make sure that the class has a default constructor and is accessible,
    // like it should be public class, it also should have setters for all of its
    // fields.");
    // }

    // } catch (SQLException e) {
    // e.printStackTrace();
    // throw new PostgresDatabaseConnectionError(e.getMessage());
    // } finally {
    // try {
    // if (stmt != null)
    // stmt.close();
    // if (conn != null)
    // conn.close();
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }
    // }

    public <T> List<T> queryForSingleColumnList(String sqlQuery, Class<T> elementType) {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = this.createConnection();
            stmt = this.createStatement(conn);
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
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer updateOne(String sqlUpdate) throws PostgresDatabaseConnectionError {
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = this.createConnection();
            conn.setAutoCommit(false);
            stmt = this.createStatement(conn);
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
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PostgresDatabaseConnectionError(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer updateOne(String sqlUpdate, Object... params) throws PostgresDatabaseConnectionError {
        Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        try {
            conn = this.createConnection();
            conn.setAutoCommit(false);
            stmt = this.getPreparedStatement(sqlUpdate, conn);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i], stmt.getParameterMetaData().getParameterType(i + 1));
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
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PostgresDatabaseConnectionError(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
