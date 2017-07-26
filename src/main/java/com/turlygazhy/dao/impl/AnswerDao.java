package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 12.07.17.
 */
public class AnswerDao extends AbstractDao{
    Connection connection;

    public AnswerDao(Connection connection) {
        this.connection = connection;
    }

    public List<Answer> getAnswersByChatId(Long chatId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ANSWER WHERE USER_ID = ?");
        ps.setLong(1, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            answers.add(parseAnswer(rs));
        }
        return answers;
    }

    public Answer getAnswer(int answerId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ANSWER WHERE ID = ?");
        ps.setInt(1, answerId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseAnswer(rs);
        }
        return null;
    }

    public void insertAnswer(Answer answer) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO ANSWER " +
                "(QUESTION_ID, USER_ID, TEXT," +
                " AUDIO, PHOTO, CONTACT_USER_ID, " +
                "CONTACT_FIRST_NAME, CONTACT_SECOND_NAME, CONTACT_PHONE_NUMBER) " +
                "VALUES(?, ?, ?," +
                " ?, ?, ?, " +
                "?, ?, ?)");
        ps.setInt(1, answer.getQuestion().getId());
        ps.setLong(2, answer.getUserId());
        ps.setString(3, answer.getText());
        ps.setString(4, answer.getAudio());
        ps.setString(5, answer.getPhoto());
        ps.setLong(6, answer.getContactUserId());
        ps.setString(7, answer.getContactFirstName());
        ps.setString(8, answer.getContactSecondName());
        ps.setString(9, answer.getContactPhoneNumber());
        ps.execute();
    }

    public void updateAnswer(Answer answer) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE ANSWER SET USER_ID = ?, TEXT = ?, AUDIO = ?, " +
                "PHOTO = ?, CONTACT_USER_ID = ?, CONTACT_FIRST_NAME = ?, " +
                "CONTACT_SECOND_NAME = ?, CONTACT_PHONE_NUMBER = ? " +
                "WHERE QUESTION_ID = ?");
        ps.setLong(1, answer.getUserId());
        ps.setString(2, answer.getText());
        ps.setString(3, answer.getAudio());
        ps.setString(4, answer.getPhoto());
        ps.setLong(5, answer.getContactUserId());
        ps.setString(6, answer.getContactFirstName());
        ps.setString(7, answer.getContactSecondName());
        ps.setString(8, answer.getContactPhoneNumber());
        ps.setInt(9, answer.getQuestion().getId());
        ps.execute();
    }

    private Answer parseAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("ID"));
        answer.setUserId(rs.getLong("USER_ID"));
        answer.setText(rs.getString("TEXT"));
        answer.setPhoto(rs.getString("PHOTO"));
        answer.setAudio(rs.getString("AUDIO"));
        answer.setContactUserId(rs.getLong("CONTACT_USER_ID"));
        answer.setContactFirstName(rs.getString("CONTACT_FIRST_NAME"));
        answer.setContactSecondName(rs.getString("CONTACT_SECOND_NAME"));
        answer.setContactPhoneNumber(rs.getString("CONTACT_PHONE_NUMBER"));
        answer.setQuestion(factory.getQuestionDao().getQuestion(rs.getInt("QUESTION_ID")));
        return answer;
    }
}
