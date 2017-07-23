package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.UserOfList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserOfListDao extends AbstractDao {
    Connection connection;

    public UserOfListDao(Connection connection) {
        this.connection = connection;
    }

    public List<UserOfList> getUserOfList() throws SQLException {
        List<UserOfList> userOfLists = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER_OF_LIST");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            userOfLists.add(parseUserOfList(rs));
        }
        return userOfLists;
    }

    public UserOfList getUserOfList(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER_OF_LIST WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseUserOfList(rs);
        }
        return null;
    }

    public void insert(UserOfList list) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO USER_OF_LIST (NAME) values (?)");
        ps.setString(1, list.getName());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (!rs.next()) {
            return;
        }
        list.setId(rs.getInt(1));
        insertUsers(list);
    }

    private void insertUsers(UserOfList list) throws SQLException {
        for (User user : list.getUsers()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO PARTICIPANT_USER_OF_LIST (USER_OF_LIST_ID, USER_ID) values (?, ?)");
            ps.setInt(1, list.getId());
            ps.setInt(2, user.getId());
            ps.execute();
        }
    }

    private UserOfList parseUserOfList(ResultSet rs) throws SQLException {
        UserOfList userOfList = new UserOfList();
        userOfList.setId(rs.getInt("ID"));
        userOfList.setName(rs.getString("NAME"));

        PreparedStatement ps = connection.prepareStatement("SELECT USER_ID FROM PARTICIPANT_USER_OF_LIST WHERE USER_OF_LIST_ID = ?");
        ps.setInt(1, userOfList.getId());
        ps.execute();
        ResultSet resultSets = ps.getResultSet();
        while (resultSets.next()) {
            userOfList.addUser(factory.getUserDao().getUserById(resultSets.getInt(1)));
        }
        return userOfList;
    }
}
