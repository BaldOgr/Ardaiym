package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;

import java.sql.SQLException;

/**
 * Created by daniyar on 12.07.17.
 * Тип 0 - текст
 *     1 - фото
 *     2 - аудио
 *     3 - контакт
 */
public class Question {
    int id;
    String text;
    int type;
    boolean show;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        try {
            return "<b>" + DaoFactory.getFactory().getMessageDao().getMessageText(38) + "</b>" + text + "\n";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return text + "\n";
    }
}
