package com.michaelhefner.Model.Time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Day {
    private ObservableList<TimeSlot> timeSlots = FXCollections.observableArrayList();
    private String name;
    private int dayOfMonth;

    public Day(String name, int dayOfMonth) {
        setName(name);
        setDayOfMonth(dayOfMonth);
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTimeSlotToDay(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
    }

    public ObservableList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public String toString() {
        return name;
    }
}
