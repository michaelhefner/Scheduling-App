package com.michaelhefner.Model.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Query {
    private static PreparedStatement preparedStatement;

    public static void setPreparedStatement(Connection connection, String statementString) throws SQLException {
        preparedStatement = connection.prepareStatement(statementString);
    }

    public static ResultSet executeQuery(String queryStatement) throws SQLException {
        setPreparedStatement(Connect.getConnection(), queryStatement);
        preparedStatement.executeQuery();
        return  preparedStatement.getResultSet();
    }

    public static ResultSet executeQuery(String queryStatement, Map<Integer, Object> queryParams) throws SQLException {
        setPreparedStatement(Connect.getConnection(), queryStatement);
        loopThroughMap(queryParams);
        preparedStatement.executeQuery();
        return  preparedStatement.getResultSet();
    }
    public static int executeUpdate(String queryStatement, Map<Integer, Object> queryParams) throws SQLException {
        setPreparedStatement(Connect.getConnection(), queryStatement);
        loopThroughMap(queryParams);
        preparedStatement.executeUpdate();
        return preparedStatement.getUpdateCount();
    }

    private static void loopThroughMap(Map<Integer, Object> queryParams) throws SQLException {
        if (queryParams != null) {
            for (Map.Entry<Integer, Object> entry : queryParams.entrySet()){
                if (entry.getValue() instanceof String) {
                    preparedStatement.setString(entry.getKey(), entry.getValue().toString());
                }else if (entry.getValue() instanceof Integer) {
                    preparedStatement.setInt(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
                }
            }
        }
    }
}
