package com.michaelhefner.Model;

public class Country {
    private int id;
    private String country;

    public Country(String country) {
        setCountry(country);
    }

    public String getCountry() {
        return country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    @Override
    public String toString(){
        return country;
    }
}
