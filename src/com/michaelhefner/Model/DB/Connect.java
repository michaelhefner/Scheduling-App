package com.michaelhefner.Model.DB;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//3.227.166.251/U05yp2";
    private static final String jdbcUrl = protocol + vendorName + ipAddress;
    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";
    private static final String username = "U05yp2";
    private static final String password = "53688646210";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connection to MySQL was successful.");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection to MySQL was closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}