package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.RequestCall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 13.07.17.
 */
public class RequestCallDao extends AbstractDao {
    Connection connection;

    public RequestCallDao(Connection connection) {
        this.connection = connection;
    }

    public RequestCall getRequestCall(int callId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM REQUEST_CALL WHERE ID = ?");
        ps.setInt(1, callId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parse(rs);
        }
        return null;
    }

    public List<RequestCall> getRequestCallList() throws SQLException {
        List<RequestCall> list = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM REQUEST_CALL");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            list.add(parse(rs));
        }
        return list;
    }

    public List<RequestCall> getRequestCallList(boolean called) throws SQLException {
        List<RequestCall> list = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM REQUEST_CALL WHERE CALLED = ?");
        ps.setBoolean(1, called);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            list.add(parse(rs));
        }
        return list;
    }

    public void insert(RequestCall requestCall) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO REQUEST_CALL (NAME, TEXT, PHONE_NUMBER) VALUES(?, ?, ?)");
        ps.setString(1, requestCall.getName());
        ps.setString(2, requestCall.getText());
        ps.setString(3, requestCall.getPhoneNumber());
        ps.execute();
    }

    public void update(RequestCall requestCall) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE REQUEST_CALL SET CALLED = ? WHERE ID = ?");
        ps.setBoolean(1, requestCall.isCalled());
        ps.setInt(2, requestCall.getId());
        ps.execute();
    }

    private RequestCall parse(ResultSet rs) throws SQLException {
        RequestCall call = new RequestCall();
        call.setId(rs.getInt("ID"));
        call.setText(rs.getString("TEXT"));
        call.setName(rs.getString("NAME"));
        call.setPhoneNumber(rs.getString("PHONE_NUMBER"));
        call.setCalled(rs.getBoolean("CALLED"));
        return call;
    }
}
