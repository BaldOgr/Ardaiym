package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.MessageDao;

import java.sql.SQLException;

/**
 * Created by daniyar on 13.07.17.
 */
public class RequestCall {
    int id;
    String text;
    String name;
    String phoneNumber;
    boolean called;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    @Override
    public String toString() {
        try {
            MessageDao messageDao = DaoFactory.getFactory().getMessageDao();
            return messageDao.getMessageText(45) + name + "\n" +
                    messageDao.getMessageText(46) + text + "\n" +
                    messageDao.getMessageText(47) + phoneNumber;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return name + "\n" + text + "\n" + phoneNumber;
    }
}
