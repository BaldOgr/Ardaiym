package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.Dates;
import com.turlygazhy.entity.Participant;
import com.turlygazhy.entity.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 28.06.17.
 */
public class TaskTemplateDao extends AbstractDao {
    Connection connection;

    public TaskTemplateDao(Connection connection) {
        this.connection = connection;
    }

    public Task getTypeOfWork(int typeOfWorkId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TYPE_OF_WORK_TEMPLATE WHERE ID = ?");
        ps.setInt(1, typeOfWorkId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseTypeOfWork(rs);
        }
        return null;
    }

    public List<Task> getTypeOfWorkList(int id) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TYPE_OF_WORK_TEMPLATE WHERE STOCK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            tasks.add(parseTypeOfWork(rs));
        }
        return tasks;
    }

    public void insertTypeOfWorkList(List<Task> taskList) throws SQLException {
        for (Task task : taskList){
            insertTypeOfWork(task);
        }
    }

    public void insertTypeOfWork(Task task) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO TYPE_OF_WORK_TEMPLATE (STOCK_ID, NAME) VALUES (?, ?)");
        ps.setInt(1, task.getStockId());
        ps.setString(2, task.getName());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            task.setId(rs.getInt(1));
        }
        for (Dates dates : task.getDates()){
            dates.setTypeOfWorkId(task.getId());
        }
        factory.getDatesTemplateDao().insertDatesList(task.getDates());
        for (Participant participant : task.getParticipants()){
            participant.setTypeOfWorkId(task.getId());
        }
    }

    public void update(Task task) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE TYPE_OF_WORK_TEMPLATE SET NAME = ? WHERE ID = ?");
        ps.setString(1, task.getName());
        ps.setInt(2, task.getId());
        ps.execute();
    }

    private Task parseTypeOfWork(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("ID"));
        task.setName(rs.getString("NAME"));
        task.setStockId(rs.getInt("STOCK_ID"));
        task.setDates(DaoFactory.getFactory().getDatesTemplateDao().getDatesbyTaskId(task.getId()));
        task.setFinished(rs.getBoolean("FINISHED"));
        return task;
    }

    public void remove(Task task) throws SQLException {
        PreparedStatement ps =connection.prepareStatement("DELETE FROM TYPE_OF_WORK_TEMPLATE WHERE ID = ?");
        ps.setInt(1, task.getId());
    }
}
