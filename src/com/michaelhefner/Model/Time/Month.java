package com.michaelhefner.Model.Time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Month {
    private static ObservableList<Week> weeks = FXCollections.observableArrayList();

    public static ObservableList<Week> getAllWeeks() {
        return weeks;
    }

    public static void addWeek(Week week) {
        weeks.add(week);
    }
}
