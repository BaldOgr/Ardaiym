package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;

import java.sql.SQLException;

/**
 * Created by daniyar on 12.07.17.
 */
public class Answer {
    int id;
    private Question question;
    private Long userId;
    private String text;
    private String audio;
    private String photo;
    private Long contactUserId = 0L;
    private String contactFirstName;
    private String contactSecondName;
    private String contactPhoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactSecondName() {
        return contactSecondName;
    }

    public void setContactSecondName(String contactSecondName) {
        this.contactSecondName = contactSecondName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(question.toString()).append("\n")
                    .append("<b>").append(DaoFactory.getFactory().getMessageDao().getMessageText(39)).append("</b>").append("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        switch (question.getType()){
            case 0: sb.append(text);
            break;
            case 3:
                if (contactUserId != 0L){
                    sb.append(contactFirstName).append("\n")
                            .append(contactSecondName).append("\n");
                }
                sb.append(contactPhoneNumber);
        }
        return sb.toString();
    }
}
