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
public class DatesDao {
    private Connection connection;

    public DatesDao(Connection connection) {
        this.connection = connection;
    }

    public Dates getDateById(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM DATES WHERE ID = ?");
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
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM DATES WHERE TYPE_OF_WORK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            dates.add(parseDates(rs));
        }
        return dates;
    }

    private Dates parseDates(ResultSet rs) throws SQLException {
        Dates date = new Dates();
        date.setId(rs.getInt("ID"));
        date.setDate(rs.getString("DATE"));
        date.setTypeOfWorkId(rs.getInt("TYPE_OF_WORK_ID"));
        return date;
    }

    public void insertDates(Dates dates) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO DATES (TYPE_OF_WORK_ID, DATE) VALUES (?, ?)");
        ps.setInt(1, dates.getTypeOfWorkId());
        ps.setString(2, dates.getDate());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            dates.setId(rs.getInt(1));
        }
    }

    public void insertDatesList(List<Dates> dates) throws SQLException {
        for (Dates dates1 : dates){
            insertDates(dates1);
        }
    }

    public Dates getDatesById(int dateId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM DATES WHERE ID = ?");
        ps.setInt(1, dateId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseDates(rs);
        }
        return null;
    }
}
