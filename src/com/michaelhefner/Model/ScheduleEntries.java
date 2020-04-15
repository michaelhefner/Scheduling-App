package com.michaelhefner.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ScheduleEntries {
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    public static void addAppointment(Appointment appointment) {
        allAppointments.add(appointment);
    }

    public static void addCustomer(Customer customer) {
        allCustomers.add(customer);
    }

    public static void deleteCustomer(Customer customer) {
        allCustomers.remove(customer);
    }

    public static void deleteAppointment(Appointment appointment) {
        allAppointments.remove(appointment);
    }

    public static void updateCustomer(int index, Customer customer) {
        allCustomers.set(index, customer);
    }

    public static void updateAppointment(int indexOfAppointment, Appointment appointmentToModify) {
        allAppointments.set(indexOfAppointment, appointmentToModify);
    }
}
