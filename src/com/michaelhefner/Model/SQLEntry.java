package com.michaelhefner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class SQLEntry {
    private int id;
    private LocalDate createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public SQLEntry(int id, LocalDate createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy){
        setId(id);
        setCreateDate(createDate);
        setCreatedBy(createdBy);
        setLastUpdate(lastUpdate);
        setLastUpdateBy(lastUpdateBy);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
}
