package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class StockDao extends AbstractDao {
    private Connection connection;

    public StockDao(Connection connection) {
        this.connection = connection;
    }

    public Stock getStock(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseStock(rs);
        }
        return null;
    }

    private Stock parseStock(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setId(rs.getInt("ID"));
        stock.setFinished(rs.getBoolean("FINISHED"));
        stock.setDescription(rs.getString("DESCRIPTION"));
        stock.setTitle(rs.getString("TITLE"));
        stock.setTitleForAdmin(rs.getString("TITLE_FOR_ADMIN"));
        stock.setReport(rs.getString("REPORT"));
        stock.setTaskList(DaoFactory.getFactory().getTypeOfWorkDao().getTypeOfWorkList(stock.getId()));
        return stock;
    }

    public void insertStock(Stock stock) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO STOCK (TITLE, TITLE_FOR_ADMIN, DESCRIPTION, REPORT) VALUES (?, ?, ?, ?)");
        ps.setString(1, stock.getTitle());
        ps.setString(2, stock.getTitleForAdmin());
        ps.setString(3, stock.getDescription());
        ps.setString(4, stock.getReport());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            stock.setId(rs.getInt(1));
        }
        for (Task task : stock.getTaskList()){
            task.setStockId(stock.getId());
        }
        DaoFactory.getFactory().getTypeOfWorkDao().insertTypeOfWorkList(stock.getTaskList());
    }

    public List<Stock> getUndoneStockList() throws SQLException {
        List<Stock> stockList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK WHERE FINISHED = FALSE");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            stockList.add(parseStock(rs));
        }
        return stockList;
    }

    public List<Stock> getDoneStockList() throws SQLException {
        List<Stock> stockList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK WHERE FINISHED = TRUE");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            stockList.add(parseStock(rs));
        }
        return stockList;
    }
}
