package com.turlygazhy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 28.06.17.
 */
public class Stock {
    int id;
    String title;
    String titleForAdmin;
    String description;
    boolean finished;
    String report;
    List<Task> taskList;

    public Stock() {
        taskList = new ArrayList<>();
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void addTypeOfWork(Task task){
        if (this.taskList == null){
            this.taskList = new ArrayList<>();
        }
        taskList.add(task);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleForAdmin() {
        return titleForAdmin;
    }

    public void setTitleForAdmin(String titleForAdmin) {
        this.titleForAdmin = titleForAdmin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
