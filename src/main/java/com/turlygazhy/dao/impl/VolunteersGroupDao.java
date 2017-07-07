package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.VolunteersGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by daniyar on 03.07.17.
 */
public class VolunteersGroupDao extends AbstractDao {
    Connection connection;

    public VolunteersGroupDao(Connection connection) {
        this.connection = connection;
    }

    public void insertVolunteer(User user, int carId) throws SQLException {
        PreparedStatement ps =connection.prepareStatement("INSERT INTO VOLUNTEERS_GROUP (USER_ID, CARS_ID) VALUES(?, ?)");
        ps.setLong(1, user.getChatId());
        ps.setInt(2, carId);
        ps.execute();
    }

    public VolunteersGroup getVolunteersGroup(int carId) throws SQLException {
        VolunteersGroup volunteersGroup = new VolunteersGroup();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM VOLUNTEERS_GROUP WHERE CARS_ID = ?");
        ps.setInt(1, carId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        volunteersGroup.setCar(factory.getCarDao().getCar(carId));
        while (rs.next()) {
            volunteersGroup.setId(rs.getInt("ID"));
            volunteersGroup.addUser(factory.getUserDao().getUserByChatId(rs.getLong("USER_ID")));
        }
        return volunteersGroup;
    }

    public VolunteersGroup getVolunteersGroup(Long chatId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM VOLUNTEERS_GROUP WHERE USER_ID = ?");
        ps.setLong(1, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return getVolunteersGroup(rs.getInt("CARS_ID"));
        }
        return null;
    }
}
