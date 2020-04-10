package com.michaelhefner.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JDBCEntries {
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public static void setAllCustomers(ObservableList<Customer> allCustomers) {
        JDBCEntries.allCustomers = allCustomers;
    }

    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    public static void setAllAppointments(ObservableList<Appointment> allAppointments) {
        JDBCEntries.allAppointments = allAppointments;
    }

    public static void addAppointment(Appointment appointment){
        allAppointments.add(appointment);
    }
    public static void addCustomer(Customer customer){
        allCustomers.add(customer);
    }

    public static void updateCustomer(int index, Customer customer){
        allCustomers.set(index, customer);
    }

    public static Customer lookupCustomer(Customer customer){
        return allCustomers.get(allCustomers.indexOf(customer));
    }
    public static Customer lookupCustomer(int id){
        return allCustomers.get(id);
    }
}
