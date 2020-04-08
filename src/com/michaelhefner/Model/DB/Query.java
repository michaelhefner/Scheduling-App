package com.michaelhefner.Model.DB;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class Query {
    private static Statement statement;

    public static void setStatement(Connection connection) throws SQLException {
        statement = connection.createStatement();
    }

    public static Statement getStatement(){
        return statement;
    }

}
