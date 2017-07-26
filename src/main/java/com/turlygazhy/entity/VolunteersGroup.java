package com.turlygazhy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 03.07.17.
 */
public class VolunteersGroup {
    int id;
    Car car;
    List<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user){
        if (this.users == null){
            users = new ArrayList<>();
        }
        users.add(user);
    }
}
