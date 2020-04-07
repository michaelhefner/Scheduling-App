package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Customer extends SQLEntry {
    private String name;
    private int addressId;
    private int active;



    public Customer(int id, LocalDate createDate, String createdBy, LocalDateTime lastUpdate,
                    String lastUpdateBy, String name, int addressId, int active) {
        super(id, createDate, createdBy, lastUpdate, lastUpdateBy);
        setActive(active);
        setAddressId(addressId);
        setName(name);
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}
