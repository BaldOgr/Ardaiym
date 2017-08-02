package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.MessageDao;

import java.sql.SQLException;

public class FamilyRate {
    int id;
    Family family;
    User ratedUser;
    int type;
    String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public User getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(User ratedUser) {
        this.ratedUser = ratedUser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ratedUser.getName()).append(" - ");
        MessageDao messageDao = DaoFactory.getFactory().getMessageDao();
        try {
            switch (type) {
                case 0:
                    sb.append(messageDao.getMessageText(148));
                    break;
                case 1:
                    sb.append(messageDao.getMessageText(149));
                    break;
                case 2:
                    sb.append(messageDao.getMessageText(150));
                    break;
                case 3:
                    sb.append(messageDao.getMessageText(151)).append(comment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }
}
