package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by daniyar on 03.07.17.
 */
public class StatusDao extends AbstractDao {
    Connection connection;

    public StatusDao(Connection connection) {
        this.connection = connection;
    }

    public boolean getStatus(int stockId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STATUS WHERE STOCK_ID = ?");
        ps.setInt(1, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            switch (rs.getInt("STATUS")) {
                case 0:
                    return false;
                case 1:
                    return true;
            }
        }
        return false;
    }
}
