package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.Dates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 28.06.17.
 */
public class DatesTemplateDao {
    private Connection connection;

    public DatesTemplateDao(Connection connection) {
        this.connection = connection;
    }

    public Dates getDateById(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM DATES_TEMPLATE WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseDates(rs);
        }
        return null;
    }

    public List<Dates> getDatesbyTaskId(int id) throws SQLException {
        List<Dates> dates = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM DATES_TEMPLATE WHERE TYPE_OF_WORK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            dates.add(parseDates(rs));
        }
        return dates;
    }

    public void insertDatesList(List<Dates> dates) throws SQLException {
        for (Dates dates1 : dates){
            insertDates(dates1);
        }
    }

    public void insertDates(Dates dates) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO DATES_TEMPLATE (TYPE_OF_WORK_ID, DATE) VALUES (?, ?)");
        ps.setInt(1, dates.getTypeOfWorkId());
        ps.setString(2, dates.getDate());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            dates.setId(rs.getInt(1));
        }
    }

    private Dates parseDates(ResultSet rs) throws SQLException {
        Dates date = new Dates();
        date.setId(rs.getInt("ID"));
        date.setDate(rs.getString("DATE"));
        date.setTypeOfWorkId(rs.getInt("TYPE_OF_WORK_ID"));
        return date;
    }

    public void remove(Dates dates) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM DATES_TEMPLATE WHERE ID = ?");
        ps.setInt(1, dates.getId());
        ps.execute();
    }

    public void update(Dates dates) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update dates_template set date = ? WHERE ID = ?");
        ps.setString(1, dates.getDate());
        ps.setInt(2, dates.getId());
        ps.execute();
    }
}
