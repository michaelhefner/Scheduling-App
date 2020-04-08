package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    private static String name;
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
