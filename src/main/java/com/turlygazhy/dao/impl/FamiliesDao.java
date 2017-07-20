package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Car;
import com.turlygazhy.entity.Family;
import com.turlygazhy.entity.User;
import com.turlygazhy.tool.SheetsAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 04.07.17.
 */
public class FamiliesDao extends AbstractDao {
    Connection connection;

    public FamiliesDao(Connection connection) {
        this.connection = connection;
    }

    private Family parseFamily(ResultSet rs) throws SQLException {
        Family family = new Family();
        family.setId(rs.getInt("ID"));
        family.setName(rs.getString("NAME"));
        family.setAddress(rs.getString("ADDRESS"));
        family.setLatitude(rs.getDouble("LATITUDE"));
        family.setLongitude(rs.getDouble("LONGITUDE"));
        family.setPhoneNumber(rs.getString("PHONE_NUMBER"));
        family.setGroup(rs.getInt("GROUP_ID"));
        family.setStockId(rs.getInt("STOCK_ID"));
        return family;
    }

    public void loadFamiliesFromGoogleSheets(int id) throws SQLException {
        List<Family> families = null;
        try {
            families = SheetsAdapter.getFamiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Family family : families) {
            family.setStockId(id);
            insertFamily(family);
        }
    }

    public List<Family> getFamilyList(int count) throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from families where car_id = 0 limit ?");
        ps.setInt(1, count);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            families.add(parseFamily(rs));
        }
        return families;
    }

    public List<Family> getFamilyListByCar(Car car) throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from families where car_id = ?");
        ps.setInt(1, car.getId());
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            families.add(parseFamily(rs));
        }
        return families;
    }

    public void updateFamily(Family family) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update families set finished = ?, car_id = ?, name = ?, phone_number = ?, address = ?, longitude = ?, latitude = ? where id = ?");
        ps.setBoolean(1, family.isFinished());
        ps.setInt(2, family.getCarId());
        ps.setString(3, family.getName());
        ps.setString(4, family.getPhoneNumber());
        ps.setString(5, family.getAddress());
        ps.setDouble(6, family.getLongitude());
        ps.setDouble(7, family.getLatitude());
        ps.setInt(8, family.getId());
        ps.execute();
    }

    public void insertFamily(Family family) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO FAMILIES (NAME, PHONE_NUMBER, ADDRESS, LONGITUDE, LATITUDE, GROUP_ID, STOCK_ID) VALUES(?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, family.getName());
        ps.setString(2, family.getPhoneNumber());
        ps.setString(3, family.getAddress());
        ps.setDouble(4, family.getLongitude());
        ps.setDouble(5, family.getLatitude());
        ps.setInt(6, family.getGroup());
        ps.setInt(7, family.getStockId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        family.setId(rs.getInt(1));
    }

    public List<Family> getFamilyList() throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from families");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            families.add(parseFamily(rs));
        }
        return families;
    }

    public List<Family> getFamilyListByGroupId(int familyGroupId, int stockId) throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from FAMILY_GROUPS where group_id = ? and STOCK_ID = ?");
        ps.setInt(1, familyGroupId);
        ps.setInt(2, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            ps = connection.prepareStatement("SELECT * FROM FAMILIES WHERE GROUP_ID = ? and STOCK_ID = ?");
            ps.setInt(1, rs.getInt("FAMILY_GROUP_ID"));
            ps.setInt(2, stockId);
            ps.execute();
            ResultSet resultSet = ps.getResultSet();
            while (resultSet.next()) {
                families.add(parseFamily(resultSet));
            }
        }
        return families;
    }

    public void insertVolunteerGroups(List<User> userVolunteers, int groupId) throws SQLException {
        for (User user : userVolunteers) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO GROUPS_OF_VOLUNTEERS (USER_ID, GROUP_ID) values(?, ?)");
            ps.setInt(1, user.getId());
            ps.setInt(2, groupId);
            ps.execute();
        }
    }

    public void insertFamilyGroups(List<Integer> familiesForVolunteers, int id, int stock_id) throws SQLException {
        for (Integer familyGroupId : familiesForVolunteers) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO FAMILY_GROUPS (GROUP_ID, FAMILY_GROUP_ID, STOCK_ID) values(?, ?, ?)");
            ps.setInt(1, id);
            ps.setInt(2, familyGroupId);
            ps.setInt(3, stock_id);
            ps.execute();
        }
    }

    public List<User> getUsersByGroupId(int group_id) throws SQLException {
        List<User> users = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM GROUPS_OF_VOLUNTEERS WHERE GROUP_ID = ?");
        ps.setInt(1, group_id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            users.add(factory.getUserDao().getUserById(rs.getInt("USER_ID")));
        }
        return users;
    }

    public int getGroupId(int userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM GROUPS_OF_VOLUNTEERS WHERE USER_ID = ?");
        ps.setInt(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return rs.getInt("GROUP_ID");
        }
        return 0;
    }

    public void downloadFamiliesToGoogle(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FAMILIES WHERE STOCK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        List<List<Object>> writeData = new ArrayList<>();
        while (rs.next()){
            List<Object> dataRow = new ArrayList<>();
            for (int i = 2; i < 7; i++) {
                dataRow.add(rs.getString(i));
            }
            dataRow.add(rs.getString(9));
            writeData.add(dataRow);
        }
        try {
            SheetsAdapter.writeData(writeData);
            ps = connection.prepareStatement("DELETE FROM FAMILIES WHERE STOCK_ID = ?; DELETE FROM FAMILY_GROUPS; DELETE FROM GROUPS_OF_VOLUNTEERS;");
            ps.setInt(1, id);
            ps.execute();
            ps = connection.prepareStatement("DELETE FROM VOLUNTEERS_GROUP WHERE STOCK_ID = ?; ");
            ps.setInt(1, id);
            ps.execute();
            ps = connection.prepareStatement("DELETE FROM CARS WHERE STOCK_ID = ?;");
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List getUnchoosedFamilies() throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from families where car_id = 0");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            families.add(parseFamily(rs));
        }
        return families;
    }
}
