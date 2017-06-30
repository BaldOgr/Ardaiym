package com.turlygazhy.entity;

import org.telegram.telegrambots.api.objects.Location;

/**
 * Created by daniyar on 28.06.17.
 */
public class Report {
    int id;
    int participantId;
    Location location;
    String text;
    String photo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParticipant() {
        return participantId;
    }

    public void setParticipant(int participant) {
        this.participantId = participant;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
