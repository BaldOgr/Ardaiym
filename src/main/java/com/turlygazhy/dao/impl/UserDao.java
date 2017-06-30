package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by user on 12/18/16.
 */
public class UserDao {
    private static final String SELECT_ADMIN_CHAT_ID = "SELECT * FROM PUBLIC.USER WHERE ID=?";
    private static final int PARAMETER_USER_ID = 1;
    private static final int CHAT_ID_COLUMN_INDEX = 2;
    public static final int ADMIN_ID = 1;
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public User getUserByChatId(Long chatId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE CHAT_ID = ?");
        ps.setLong(1, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseUser(rs);
        }
        return null;
    }

    public Long getAdminChatId() {
        try {
            PreparedStatement ps = connection.prepareStatement(SELECT_ADMIN_CHAT_ID);
            ps.setLong(PARAMETER_USER_ID, ADMIN_ID);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getLong(CHAT_ID_COLUMN_INDEX);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    User parseUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("ID"));
        user.setChatId(rs.getLong("CHAT_ID"));
        user.setName(rs.getString("NAME"));
        user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
        user.setCity(rs.getString("CITY"));
        user.setSex(rs.getBoolean("SEX"));
        user.setBirthday(rs.getString("BIRTHDAY"));
        user.setRules(rs.getInt("RULES"));
        return user;
    }

    public boolean isAdmin(Long chatId) throws SQLException {
        return Objects.equals(2, getUserByChatId(chatId).getRules());
    }
    public boolean isSuperAdmin(Long chatId) throws SQLException {
        return Objects.equals(3, getUserByChatId(chatId).getRules());
    }

    public void insertUser(User user) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO USER (ID, CHAT_ID, NAME, PHONE_NUMBER, CITY, SEX, BIRTHDAY) VALUES (default, ?, ?, ?, ?, ?, ?)");
        ps.setLong(1, user.getChatId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPhoneNumber());
        ps.setString(4, user.getCity());
        ps.setBoolean(5, user.isSex());
        ps.setString(6, user.getBirthday());
        ps.execute();
    }

    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            users.add(parseUser(rs));
        }
        return users;
    }
}
