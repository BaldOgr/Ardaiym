package com.turlygazhy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 28.06.17.
 */
public class Task {
    int id;
    int stockId;
    String name;
    List<Dates> dates;
    List<Participant> participants;
    boolean finished;

    public Task() {
        dates = new ArrayList<>();
        participants = new ArrayList<>();
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipantOfStocks(Participant participant){
        if (this.participants == null){
            this.participants = new ArrayList<>();
        }
        this.participants.add(participant);
    }

    public List<Dates> getDates() {
        return dates;
    }

    public void setDates(List<Dates> dates) {
        this.dates = dates;
    }

    public void addDates(Dates dates){
        if (this.dates == null){
            this.dates = new ArrayList<>();
        }
        this.dates.add(dates);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
