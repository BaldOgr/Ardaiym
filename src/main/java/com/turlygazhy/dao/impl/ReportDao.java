package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 28.06.17.
 */
public class ReportDao {
    Connection connection;

    public ReportDao(Connection connection) {
    this.connection = connection;
    }


    public List<Report> getReports(int id) throws SQLException {
        List<Report> reports = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM REPORT WHERE PARTICIPANT_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            reports.add(parseReport(rs));
        }
        return reports;
    }

    private Report parseReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("ID"));
        report.setText(rs.getString("TEXT"));
        report.setPhoto(rs.getString("PHOTO"));
        return report;
    }

    public void insertReportList(List<Report> reports) throws SQLException {
        for (Report report : reports){
            insertReport(report);
        }
    }

    public void insertReport(Report report) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO REPORT (PARTICIPANT_ID, TEXT, PHOTO) VALUES (?, ?, ?)");
        ps.setInt(1, report.getParticipant());
        ps.setString(2, report.getText());
        ps.setString(3, report.getPhoto());
        ps.execute();
    }
}
