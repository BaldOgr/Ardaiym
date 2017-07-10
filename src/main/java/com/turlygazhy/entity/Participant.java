package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.ButtonDao;
import com.turlygazhy.dao.impl.MessageDao;

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

    public void addReport(Report report) {
        if (this.reports == null) {
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
        MessageDao messageDao = factory.getMessageDao();
        ButtonDao buttonDao = factory.getButtonDao();
        try {
            Task task = factory.getTypeOfWorkDao().getTypeOfWork(typeOfWorkId);
            Stock stock = factory.getStockDao().getStock(task.getStockId());
            Dates dates = factory.getDatesDao().getDatesById(dateId);
            StringBuilder sb = new StringBuilder();
            sb.append("<b>").append(stock.getTitle()).append("</b>\n\n")
                    .append("<b>").append(messageDao.getMessageText(79)).append(": </b>\n")
                    .append("\t").append(stock.getDescription()).append("\n")
                    .append("<b>").append(messageDao.getMessageText(89)).append(": </b>\n")
                    .append("\t").append(task.getName()).append("\n")
                    .append(dates.getDate()).append("\n");
            if (finished) {
                sb.append(buttonDao.getButtonText(25)).append("\n<b>");    // Выполнено
                sb.append(messageDao.getMessageText(88)).append(": </b>\n");    // Отчеты
                for (int i = 0; i < reports.size(); i++) {
                    sb.append(i + 1).append(") ").append(reports.get(i).toString()).append("\n");
                }
            } else {
                sb.append(buttonDao.getButtonText(86));    // Невыполнено
            }

            return sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
