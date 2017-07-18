package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 12.07.17.
 */
public class QuestionDao extends AbstractDao {
    Connection connection;

    public QuestionDao(Connection connection) {
        this.connection = connection;
    }

    public List<Question> getQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUESTION");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            questions.add(parseQuestion(rs));
        }
        return questions;
    }

    public List<Question> getQuestions(boolean show) throws SQLException {
        List<Question> questions = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUESTION WHERE SHOW = ?");
        ps.setBoolean(1, show);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            questions.add(parseQuestion(rs));
        }
        return questions;
    }

    public Question getQuestion(int questionId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUESTION WHERE ID = ?");
        ps.setInt(1, questionId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseQuestion(rs);
        }
        return null;
    }

    public void insertQuestion(Question question) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO QUESTION (TEXT, TYPE) VALUES(?, ?)");
        ps.setString(1, question.getText());
        ps.setInt(2, question.getType());
        ps.execute();
    }

    public void updateQuestion(Question question) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE QUESTION SET TEXT = ?, TYPE = ?, SHOW = ? WHERE ID = ?");
        ps.setString(1, question.getText());
        ps.setInt(2, question.getType());
        ps.setBoolean(3, question.isShow());
        ps.setInt(4, question.getId());
        ps.execute();
    }

    private Question parseQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("ID"));
        question.setText(rs.getString("TEXT"));
        question.setType(rs.getInt("TYPE"));
        question.setShow(rs.getBoolean("SHOW"));
        return question;
    }
}
