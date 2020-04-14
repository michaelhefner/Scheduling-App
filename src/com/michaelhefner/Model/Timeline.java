package com.michaelhefner.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Timeline {
    private static ObservableList<TimeSlot> dateTimeObservableList = FXCollections.observableArrayList();

    public static ObservableList<TimeSlot> getDateTimeObservableList() {
        return dateTimeObservableList;
    }

    public static void removeTimeSlot(TimeSlot timeSlot) {
        dateTimeObservableList.remove(timeSlot);
    }

    public static boolean addTimeSlot(TimeSlot timeSlotToAdd) {
        boolean isOk = true;

        for (TimeSlot timeSlot : dateTimeObservableList) {
            if (!((timeSlotToAdd.getStart().isBefore(timeSlot.getStart())
                    && timeSlotToAdd.getEnd().isBefore(timeSlot.getStart()))
                    || (timeSlotToAdd.getStart().isAfter(timeSlot.getEnd())))) {
                isOk = false;
            }
        }

        if (timeSlotToAdd.getStart().isAfter(timeSlotToAdd.getEnd()))
            isOk = false;
        if (dateTimeObservableList.isEmpty() || isOk)
            dateTimeObservableList.add(timeSlotToAdd);
        return isOk;
    }
}