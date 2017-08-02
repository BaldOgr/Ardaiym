package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsDao extends AbstractDao {
    Connection connection;

    public FriendsDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(long userId, long friendUserId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO FRIENDS (USER_ID, FRIEND_USER_ID) VALUES (?, ?)");
        ps.setLong(1, userId);
        ps.setLong(2, friendUserId);
        ps.execute();
    }

    public List<User> getFriends(Long chatId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRIENDS WHERE USER_ID = ?");
        ps.setLong(1, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        List<User> users = new ArrayList<>();
        while (rs.next()){
            users.add(factory.getUserDao().getUserByChatId(rs.getLong("FRIEND_USER_ID")));
        }
        return users;
    }
}
