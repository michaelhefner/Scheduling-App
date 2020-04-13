package com.michaelhefner.Model;

public class User {
    private static String name;

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        User.id = id;
    }

    private static String id;
    private static String password;
    private int active;
    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }
    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

}
