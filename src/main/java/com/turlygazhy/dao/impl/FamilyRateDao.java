package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.FamilyRate;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FamilyRateDao extends AbstractDao {
    Connection connection;

    public FamilyRateDao(Connection connection) {
        this.connection = connection;
    }

    public void insertRate(FamilyRate rate) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into FAMILY_RATE (FAMILY_ID, USER_ID, TYPE, COMMENT) VALUES(?, ?, ?, ?)");
        ps.setInt(1, rate.getFamily().getId());
        ps.setInt(2, rate.getRatedUser().getId());
        ps.setInt(3, rate.getType());
        ps.setString(4, rate.getComment());
        ps.execute();
    }

    public List<FamilyRate> getDamilyRate(int familyId) throws SQLException {
        List<FamilyRate> familyRates = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FAMILY_RATE WHERE FAMILY_ID = ?");
        ps.setInt(1, familyId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            familyRates.add(parseFamily(rs));
        }
        return familyRates;
    }

    private FamilyRate parseFamily(ResultSet rs) throws SQLException {
        FamilyRate familyRate = new FamilyRate();
        familyRate.setFamily(factory.getFamiliesDao().getFamily(rs.getInt("FAMILY_ID")));
        familyRate.setRatedUser(factory.getUserDao().getUserById(rs.getInt("USER_ID")));
        familyRate.setType(rs.getInt("TYPE"));
        familyRate.setComment(rs.getString("COMMENT"));
        return familyRate;
    }
}
