package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Voting;
import patterns.KeyboardPattern;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Eshu on 07.07.2017.
 * example of vote dao for voting command
 */
public class VoteDao extends AbstractDao {
    private Connection connection;

    public VoteDao(Connection connection) {
        this.connection = connection;}



    public void makeVote(long voteId, int section, String chatIdOrMemberId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC.VOTINGS SET VOTES_IN_SELECTIONS[?] = concat("
                + " VOTES_IN_SELECTIONS[?], ?, '/') where id =?");
        //also new crutch because postgresql counts from one
        ps.setInt(   1,section+1);
        ps.setInt(   2,section+1);
        ps.setString(3,chatIdOrMemberId);
        ps.setLong(  4,voteId);
        ps.execute();
    }


    public String[] getVotesSelectionNames(long votingId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT VOTE_SELECTIONS FROM PUBLIC.VOTINGS WHERE ID=?");
        ps.setLong(1, votingId);
        ps.execute();
        ResultSet rs         = ps.getResultSet();
        rs.next();
        return  (String[]) rs.getArray(1).getArray();
    }


    public String[] getAllVotesMembers(long votingId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT VOTES_IN_SELECTIONS FROM PUBLIC.VOTINGS WHERE id=?");
        ps.setLong(1, votingId);
        ps.execute();
        ResultSet rs         = ps.getResultSet();
        rs.next();
        try {
            return (String[]) rs.getArray(1).getArray();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Voting getVoting(long votingId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC.VOTINGS WHERE ID=?");
        ps.setLong(1,votingId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if(rs.next()){
            return new Voting(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    (String[]) rs.getArray(4).getArray(),
                    (String[]) rs.getArray(5).getArray(),
                    getPatternFromInt(rs.getInt(6)),
                    rs.getLong(7));}
        else return null;
    }

    public long createVote(String text, String photo, String[] voteSelections, long voteCreatorId, KeyboardPattern keyboardPattern) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO public.votings ( text, photo, VOTE_SELECTIONS,votes_in_selections, KEYBOARD_PATTERN, CREATOR_ID)" +
                " values (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        int keyboardPatternInt = 0;
        switch ( keyboardPattern){
            case ONE_BUTTON_AT_ROW:
                keyboardPatternInt = 1;
                break;
            case TWO_BUTTON_AT_ROW:
                keyboardPatternInt = 2;
                break;
            case THREE_BUTTON_AT_ROW:
                keyboardPatternInt = 3;
        }
        String[] votes_in_selections = new String[voteSelections.length];
        Array arrayVotes      = connection.createArrayOf("text", votes_in_selections);
        Array arraySelections = connection.createArrayOf("text", voteSelections);
        ps.setString(1,text);
        ps.setString(2,photo);
        ps.setArray( 3,arraySelections);
        ps.setArray( 4,arrayVotes);
        ps.setInt(   5,keyboardPatternInt);
        ps.setLong(  6,voteCreatorId);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        return rs.getLong(1);
    }

    public ArrayList<Voting> getAllVotingsByMemberId(long memberId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC.VOTINGS WHERE CREATOR_ID=?");
        ps.setLong(1, memberId);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        ArrayList<Voting> votingArrayList = new ArrayList<>();
        while (resultSet.next()){

            votingArrayList.add(new Voting(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    (String[]) resultSet.getArray(4).getArray(),
                    (String[]) resultSet.getArray(5).getArray(),
                    getPatternFromInt(resultSet.getInt(6)),
                    resultSet.getLong(7)));

        }
        return votingArrayList;
    }

    public void deleteVoting(long votingId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from public.votings where id=?");
        ps.setLong(1,votingId);
        ps.execute();
    }

    private KeyboardPattern getPatternFromInt(int i){
        switch (i){
            case 1:
                return KeyboardPattern.ONE_BUTTON_AT_ROW;
            case 2:
                return KeyboardPattern.TWO_BUTTON_AT_ROW;
            default:
                return KeyboardPattern.THREE_BUTTON_AT_ROW;
        }
    }

}