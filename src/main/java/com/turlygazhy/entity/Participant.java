package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 28.06.17.
 */
public class Participant {
    int id;
    int typeOfWorkId;
    int dateId;
    User user;
    List<Report> reports;
    boolean finished;

    public Participant() {
        reports = new ArrayList<>();
    }

    public int getTypeOfWorkId() {
        return typeOfWorkId;
    }

    public void setTypeOfWorkId(int typeOfWorkId) {
        this.typeOfWorkId = typeOfWorkId;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report){
        if (this.reports == null){
            this.reports = new ArrayList<>();
        }
        reports.add(report);
    }

    public int getDateId() {
        return dateId;
    }

    public void setDateId(int dateId) {
        this.dateId = dateId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        DaoFactory factory = DaoFactory.getFactory();
        try {
            Task task = factory.getTypeOfWorkDao().getTypeOfWork(typeOfWorkId);
            Stock stock = factory.getStockDao().getStock(task.getStockId());
            Dates dates = factory.getDatesDao().getDatesById(dateId);
            return "<b>" + stock.getTitle() +"</b>\n"
                    + stock.getDescription() + "\n"
                    + task.getName() + "\n"
                    + dates.getDate() + "\n"
                    + finished + "\n"
                    + reports.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
