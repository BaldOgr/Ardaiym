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

public class StockTemplateDao extends AbstractDao {
    private Connection connection;

    public StockTemplateDao(Connection connection) {
        this.connection = connection;
    }

    public Stock getStock(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK_TEMPLATE WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseStock(rs);
        }
        return null;
    }

    public void insertStock(Stock stock) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO STOCK_TEMPLATE (TITLE, TITLE_FOR_ADMIN, DESCRIPTION, REPORT, CTA, ADDED_BY) VALUES (?, ?, ?, ?, ?, ?)");
        ps.setString(1, stock.getTitle());
        ps.setString(2, stock.getTitleForAdmin());
        ps.setString(3, stock.getDescription());
        ps.setString(4, stock.getReport());
        ps.setBoolean(5, stock.isCTA());
        ps.setLong(6, stock.getAddedBy().getChatId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            stock.setId(rs.getInt(1));
        }
        for (Task task : stock.getTaskList()) {
            task.setStockId(stock.getId());
        }
        DaoFactory.getFactory().getTaskTemplateDao().insertTypeOfWorkList(stock.getTaskList());
    }

    public void updateStock(Stock stock) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE STOCK_TEMPLATE SET STATUS = ?, REPORT = ?, ADDED_BY = ?, TITLE = ?, DESCRIPTION = ?, TITLE_FOR_ADMIN = ? WHERE ID = ?");
        ps.setInt(1, stock.getStatus());
        ps.setString(2, stock.getReport());
        if (stock.getAddedBy() != null) {
            ps.setLong(3, stock.getAddedBy().getChatId());
        } else {
            ps.setLong(3, 0);
        }
        ps.setString(4, stock.getTitle());
        ps.setString(5, stock.getDescription());
        ps.setString(6, stock.getTitleForAdmin());
        ps.setInt(7, stock.getId());
        ps.execute();
    }

    private Stock parseStock(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setId(rs.getInt("ID"));
        stock.setStatus(rs.getInt("STATUS"));
        stock.setDescription(rs.getString("DESCRIPTION"));
        stock.setTitle(rs.getString("TITLE"));
        stock.setTitleForAdmin(rs.getString("TITLE_FOR_ADMIN"));
        stock.setReport(rs.getString("REPORT"));
        stock.setAddedBy(factory.getUserDao().getUserByChatId(rs.getLong("ADDED_BY")));
        stock.setCTA(rs.getBoolean("CTA"));
        if (stock.isCTA()) {
            stock.setTaskList(DaoFactory.getFactory().getTaskTemplateDao().getTypeOfWorkList(stock.getId()));
        }
        return stock;
    }

    public List<Stock> getStocks(boolean cta) throws SQLException {
        List<Stock> stocks = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK_TEMPLATE WHERE CTA = ?");
        ps.setBoolean(1, cta);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            stocks.add(parseStock(rs));
        }
        return stocks;
    }
}
