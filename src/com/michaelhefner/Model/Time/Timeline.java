package com.michaelhefner.Model.Time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Timeline {
    private static final ObservableList<TimeSlot> dateTimeObservableList = FXCollections.observableArrayList();

    public static ObservableList<TimeSlot> getDateTimeObservableList() {
        return dateTimeObservableList;
    }

    public static void removeTimeSlot(TimeSlot timeSlot) {
        dateTimeObservableList.remove(timeSlot);
    }

    public static boolean lookupTimeSlot(TimeSlot timeSlotToLookup) {
        for (TimeSlot timeSlot : dateTimeObservableList) {
            if (!((timeSlotToLookup.getStart().isBefore(timeSlot.getStart())
                    && timeSlotToLookup.getEnd().isBefore(timeSlot.getStart()))
                    || (timeSlotToLookup.getStart().isAfter(timeSlot.getEnd())))) {
                return false;
            }
        }
        return true;
    }

    public static boolean addTimeSlot(TimeSlot timeSlotToAdd) {
        boolean isOk;

        isOk = lookupTimeSlot(timeSlotToAdd);

        if (timeSlotToAdd.getStart().isAfter(timeSlotToAdd.getEnd()))
            isOk = false;
        if (dateTimeObservableList.isEmpty() || isOk)
            dateTimeObservableList.add(timeSlotToAdd);
        return isOk;
    }

}