package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 07.07.17.
 */
public class SurveyDao extends AbstractDao {
    Connection connection;

    public SurveyDao(Connection connection) {
        this.connection = connection;
    }

    public void insertSurvey(int stockId, String report, int rating) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO SURVEY (stock_id, report, rating) VALUES (?, ?, ?)");
        ps.setInt(1, stockId);
        ps.setString(2, report);
        ps.setInt(3, rating);
        ps.execute();
    }

    public List getSurveis(int id) throws SQLException {
        List<String> strings = new ArrayList<>();
        List<Integer> rating = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SURVEY WHERE STOCK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            strings.add(rs.getString("REPORT"));
            rating.add(rs.getInt("RATING"));
        }
        List<List> objects = new ArrayList<>();
        objects.add(strings);
        objects.add(rating);
        return objects;
    }
}
