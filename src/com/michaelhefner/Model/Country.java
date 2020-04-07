package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Country extends SQLEntry {
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Country(int id, LocalDate createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy, String country) {
        super(id, createDate, createdBy, lastUpdate, lastUpdateBy);
        setCountry(country);
    }
}
