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
    int status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "/id" + id + " - " + title;
    }
}
