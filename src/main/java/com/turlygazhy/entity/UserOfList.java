package com.turlygazhy.entity;

import java.util.ArrayList;
import java.util.List;

public class UserOfList {
    int id;
    String name;
    List<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        if (users == null){
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        if (users == null){
            users = new ArrayList<>();
        }
        users.add(user);
    }
}
