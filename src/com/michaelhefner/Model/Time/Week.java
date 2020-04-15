package com.michaelhefner.Model.Time;

public class Week {
    private Day Sunday;
    private Day Monday;
    private Day Tuesday;
    private Day Wednesday;
    private Day Thursday;
    private Day Friday;
    private Day Saturday;

    public Week(Day sunday, Day monday, Day tuesday, Day wednesday, Day thursday, Day friday, Day saturday) {
        setSunday(sunday);
        setMonday(monday);
        setTuesday(tuesday);
        setWednesday(wednesday);
        setThursday(thursday);
        setFriday(friday);
        setSaturday(saturday);
    }

    public Day getSunday() {
        return Sunday;
    }

    public void setSunday(Day sunday) {
        Sunday = sunday;
    }

    public Day getMonday() {
        return Monday;
    }

    public void setMonday(Day monday) {
        Monday = monday;
    }

    public Day getTuesday() {
        return Tuesday;
    }

    public void setTuesday(Day tuesday) {
        Tuesday = tuesday;
    }

    public Day getWednesday() {
        return Wednesday;
    }

    public void setWednesday(Day wednesday) {
        Wednesday = wednesday;
    }

    public Day getThursday() {
        return Thursday;
    }

    public void setThursday(Day thursday) {
        Thursday = thursday;
    }

    public Day getFriday() {
        return Friday;
    }

    public void setFriday(Day friday) {
        Friday = friday;
    }

    public Day getSaturday() {
        return Saturday;
    }

    public void setSaturday(Day saturday) {
        Saturday = saturday;
    }
}
