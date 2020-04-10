package com.michaelhefner.Model;

public class City {
    private int id;
    private String city;
    private int countryId;

    public City(String city, int countryId) {
        setId(id);
        setCity(city);
        setCountryId(countryId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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


    @Override
    public String toString(){
        return city;
    }

}
