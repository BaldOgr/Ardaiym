package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class ParticipantOfStockDao extends AbstractDao {
    Connection connection;

    public ParticipantOfStockDao(Connection connection) {
        this.connection = connection;
    }


    public List<Participant> getParticipantListByTypeOfWorkID(int id) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK WHERE TYPE_OF_WORK_ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            participants.add(parseParticipant(rs));
        }
        return participants;
    }

    public Participant getParticipantById(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK  WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseParticipant(rs);
        }
        return null;
    }

    private Participant parseParticipant(ResultSet rs) throws SQLException {
        Participant participant = new Participant();
        participant.setId(rs.getInt("ID"));
        participant.setDateId(rs.getInt("DATES_ID"));
        participant.setTypeOfWorkId(rs.getInt("TYPE_OF_WORK_ID"));
        participant.setUser(DaoFactory.getFactory().getUserDao().getUserByChatId(rs.getLong("USER_ID")));
        participant.setReports(DaoFactory.getFactory().getReportDao().getReports(participant.getId()));
        participant.setFinished(rs.getBoolean("FINISHED"));
        return participant;
    }

    public void insertParticipant(Participant participant) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PARTICIPANTS_OF_STOCK (TYPE_OF_WORK_ID, USER_ID, DATES_ID) VALUES (?, ?, ?)");
        ps.setInt(1, participant.getTypeOfWorkId());
        ps.setLong(2, participant.getUser().getChatId());
        ps.setInt(3, participant.getDateId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            participant.setId(rs.getInt(1));
        }
    }

    public void insertParticipantList(List<Participant> participants) throws SQLException {
        for (Participant participant : participants) {
            insertParticipant(participant);
        }
    }

    public void update(Participant participant) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update PARTICIPANTS_OF_STOCK SET FINISHED = ? WHERE ID = ?");
        ps.setBoolean(1, participant.isFinished());
        ps.setInt(2, participant.getId());
        ps.execute();
        factory.getReportDao().insertReportList(participant.getReports());
    }

    public void update(List<Participant> participants) throws SQLException {
        for (Participant participant : participants){
            update(participant);
        }
    }

    public List<Participant> getParticipantOfStock(Long chatId, boolean b) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK WHERE USER_ID = ? AND FINISHED = ?");
        ps.setLong(1, chatId);
        ps.setBoolean(2, b);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            participants.add(parseParticipant(rs));
        }
        return participants;
    }

    public List<Participant> getParticipantByChatId(Long chatId) {

        return null;
    }

    public List<Participant> getParticipantOfStock(Long chatId) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK WHERE USER_ID = ?");
        ps.setLong(1, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            participants.add(parseParticipant(rs));
        }
        return participants;
    }
}
