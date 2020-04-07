package com.michaelhefner.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JDBCEntries {
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    public static void setAllAppointments(ObservableList<Appointment> allAppointments) {
        JDBCEntries.allAppointments = allAppointments;
    }
}
