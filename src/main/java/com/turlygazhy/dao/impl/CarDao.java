package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Car;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 03.07.17.
 */
public class CarDao extends AbstractDao {
    Connection connection;

    public CarDao(Connection connection) {
        this.connection = connection;
    }

    public void insertCar(Car car) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO CARS (NAME, USER_ID, STOCK_ID) VALUES(?, ?, ?)");
        ps.setString(1, car.getName());
        ps.setLong(2, car.getUserId());
        ps.setInt(3, car.getStockId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            car.setId(rs.getInt(1));
        }
    }


    public Car getCar(int cars_id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CARS WHERE ID = ?");
        ps.setInt(1, cars_id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseCar(rs);
        }
        return null;
    }

    private Car parseCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("ID"));
        car.setName(rs.getString("NAME"));
        car.setUserId(rs.getLong("USER_ID"));
        return car;
    }

    public List<Car> getCars() throws SQLException {
        List<Car> cars = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CARS");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            cars.add(parseCar(rs));
        }
        return cars;
    }
}
