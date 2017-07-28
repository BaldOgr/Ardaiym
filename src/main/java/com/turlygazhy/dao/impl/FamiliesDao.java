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

    public Family getFamily(int familyId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from families where id = ?");
        ps.setInt(1, familyId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseFamily(rs);
        }
        return null;
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

    public List<Family> getFamilyListByGroupId(int familyGroupId, int stockId) throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from FAMILY_GROUPS where group_id = ? and STOCK_ID = ?");
        ps.setInt(1, familyGroupId);
        ps.setInt(2, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            ps = connection.prepareStatement("SELECT * FROM FAMILIES WHERE GROUP_ID = ? and STOCK_ID = ? and status = 0");
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

    public List<Family> getFamilyListByStatus(int status) throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from families where status = ?");
        ps.setInt(1, status);
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

    public void insertVolunteerGroups(List<User> userVolunteers, int groupId, int stockId) throws SQLException {
        for (User user : userVolunteers) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO GROUPS_OF_VOLUNTEERS (USER_ID, GROUP_ID, STOCK_ID) values(?, ?, ?)");
            ps.setInt(1, user.getId());
            ps.setInt(2, groupId);
            ps.setInt(3, stockId);
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

    public void updateFamily(Family family) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update families set status = ?, car_id = ?, name = ?, phone_number = ?, address = ?, longitude = ?, latitude = ?, report = ?, volunteers_group_id = ? where id = ?");
        ps.setInt(1, family.getStatus());
        ps.setInt(2, family.getCarId());
        ps.setString(3, family.getName());
        ps.setString(4, family.getPhoneNumber());
        ps.setString(5, family.getAddress());
        ps.setDouble(6, family.getLongitude());
        ps.setDouble(7, family.getLatitude());
        ps.setString(8, family.getReport());
        ps.setInt(9, family.getVolunteersGroupId());
        ps.setInt(10, family.getId());
        ps.execute();
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

    public int getGroupId(int userId, int stockId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM GROUPS_OF_VOLUNTEERS WHERE USER_ID = ? and STOCK_ID = ?");
        ps.setInt(1, userId);
        ps.setInt(2, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return rs.getInt("GROUP_ID");
        }
        return 0;
    }

    public List<User> getUsersByGroupId(int group_id, int stockId) throws SQLException {
        List<User> users = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM GROUPS_OF_VOLUNTEERS WHERE GROUP_ID = ? and STOCK_ID = ?");
        ps.setInt(1, group_id);
        ps.setInt(2, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            users.add(factory.getUserDao().getUserById(rs.getInt("USER_ID")));
        }
        return users;
    }

    public List<Integer> getGroupsByStockId(int stockId) throws SQLException {
        List<Integer> groups = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM GROUPS_OF_VOLUNTEERS WHERE STOCK_ID = ?");
        ps.setInt(1, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            int groupId = rs.getInt("GROUP_ID");
            if (!hasGroup(groups, groupId)){
                groups.add(groupId);
            }
        }
        return groups;
    }

    private boolean hasGroup(List<Integer> groups, int groupId) {
        for (Integer group : groups){
            if (group.equals(groupId)){
                return true;
            }
        }
        return false;
    }

    public void downloadFamiliesToGoogle(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FAMILIES WHERE STOCK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        List<List<Object>> writeData = new ArrayList<>();
        while (rs.next()) {
            List<Object> dataRow = new ArrayList<>();
            for (int i = 2; i < 7; i++) {
                dataRow.add(rs.getString(i));
            }
            dataRow.add(rs.getString(9));
            writeData.add(dataRow);
        }
        try {
            SheetsAdapter.writeDataFromFamilySheet(writeData);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        family.setStatus(rs.getInt("STATUS"));
        family.setReport(rs.getString("REPORT"));
        return family;
    }

    public List<Family> getRejectedFamiliesByGroup(int groupId, int stockId) throws SQLException {
        List<Family> families = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from families where volunteers_group_id = ? and stock_id = ?");
        ps.setInt(1, groupId);
        ps.setInt(2, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            families.add(parseFamily(rs));
        }
        return families;
    }
}
