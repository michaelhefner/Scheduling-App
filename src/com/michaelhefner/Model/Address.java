package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Address extends SQLEntry {
    private String address;
    private String address2;
    private int cityId;
    private String postalCode;
    private String phone;


    public Address(int id, LocalDate createDate, String createdBy, LocalDateTime lastUpdate,
                   String lastUpdateBy, String address, String address2, int cityId, String postalCode, String phone) {
        super(id, createDate, createdBy, lastUpdate, lastUpdateBy);
        setAddress(address);
        setAddress2(address2);
        setCityId(cityId);
        setPhone(phone);
        setPostalCode(postalCode);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
