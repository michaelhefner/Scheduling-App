package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class City extends SQLEntry {
    private String city;
    private int countryId;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public City(int id, LocalDate createDate, String createdBy,
                LocalDateTime lastUpdate, String lastUpdateBy, String city, int countryId) {
        super(id, createDate, createdBy, lastUpdate, lastUpdateBy);
        setCity(city);
        setCountryId(countryId);
    }
}
