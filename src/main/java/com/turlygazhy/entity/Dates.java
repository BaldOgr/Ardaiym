package com.turlygazhy.entity;

/**
 * Created by daniyar on 28.06.17.
 */
public class Dates {
    int id;
    int typeOfWorkId;
    String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeOfWorkId() {
        return typeOfWorkId;
    }

    public void setTypeOfWorkId(int typeOfWorkId) {
        this.typeOfWorkId = typeOfWorkId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return date;
    }
}
