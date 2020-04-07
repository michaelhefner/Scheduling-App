package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User extends SQLEntry {
    private String name;
    private String password;
    private int active;

    public User(int id, LocalDate createDate, String createdBy, LocalDateTime lastUpdate,
                String lastUpdateBy, String name, String password, int active) {
        super(id, createDate, createdBy, lastUpdate, lastUpdateBy);
        setName(name);
        setActive(active);
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
