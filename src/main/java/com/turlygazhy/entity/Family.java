package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.MessageDao;

import java.sql.SQLException;

/**
 * Created by daniyar on 03.07.17.
 */
public class Family {
    int id;
    String name;
    String address;
    Double longitude;
    Double latitude;
    int status;
    int carId;
    private String phoneNumber;
    private int group;
    private int stockId;
    private String report;
    private int volunteersGroupId;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusString() throws SQLException {

        MessageDao messageDao = DaoFactory.getFactory().getMessageDao();
        switch (status){
            case 0:
                return messageDao.getMessageText(125);
            case 1:
                return messageDao.getMessageText(126);
            case 2:
                return messageDao.getMessageText(127);
            case 3:
                return messageDao.getMessageText(128);
            case 4:
                return messageDao.getMessageText(129);
        }
        return null;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getStockId() {
        return stockId;
    }

    @Override
    public String toString() {
        try {
            MessageDao messageDao = DaoFactory.getFactory().getMessageDao();

            return "/id" + id + " - " + name + "\n" +
                    messageDao.getMessageText(96) + address + "\n" +
                    messageDao.getMessageText(97) + phoneNumber;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getReport() {
        return report;
    }

    public void setVolunteersGroupId(int volunteersGroupId) {
        this.volunteersGroupId = volunteersGroupId;
    }

    public int getVolunteersGroupId() {
        return volunteersGroupId;
    }
}
