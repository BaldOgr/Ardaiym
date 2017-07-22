package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.FamilyRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
