package com.turlygazhy.dao;

import com.turlygazhy.connection_pool.ConnectionPool;
import com.turlygazhy.dao.impl.*;
import com.turlygazhy.entity.FamilyRate;
import com.turlygazhy.entity.VolunteersGroup;

import java.sql.Connection;

/**
 * Created by user on 12/11/16.
 */
public class DaoFactory {

    // Takes connection from ConnectionPool
    private static Connection connection = ConnectionPool.getConnection();
    private static DaoFactory daoFactory = new DaoFactory();

    public DaoFactory() {
    }

    //initialization of connection
    public DaoFactory(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return connection
     */
    public static DaoFactory getFactory() {
        return daoFactory;
    }

    public void close() {
        ConnectionPool.releaseConnection(connection);
    }

    public MessageDao getMessageDao() {
        return new MessageDao(connection);
    }

    public CommandDao getCommandDao() {
        return new CommandDao(connection);
    }

    public UserDao getUserDao() {
        return new UserDao(connection);
    }

    public KeyboardMarkUpDao getKeyboardMarkUpDao() {
        return new KeyboardMarkUpDao(connection);
    }

    public ButtonDao getButtonDao() {
        return new ButtonDao(connection);
    }

    public ListDao getListDao(String listName) {
        return new ListDao(connection, listName);
    }

    public ConstDao getConstDao() {
        return new ConstDao(connection);
    }

    public ListKeyboardDao getListKeyboardDao() {
        return new ListKeyboardDao(connection);
    }

    public MemberDao getMemberDao() {
        return new MemberDao(connection);
    }

    public KeyWordDao getKeyWordDao() {
        return new KeyWordDao(connection);
    }

    public ReservationDao getReservationDao() {
        return new ReservationDao(connection);
    }

    public GroupDao getGroupDao() {
        return new GroupDao(connection);
    }

    public GoalDao getGoalDao() {
        return new GoalDao(connection);
    }

    public ThesisDao getThesisDao() {
        return new ThesisDao(connection);
    }

    public SavedResultsDao getSavedResultsDao() {
        return new SavedResultsDao(connection);
    }

    public TaskDao getTypeOfWorkDao() {
        return new TaskDao(connection);
    }

    public ReportDao getReportDao() {
        return new ReportDao(connection);
    }

    public DatesDao getDatesDao() {
        return new DatesDao(connection);
    }

    public StockDao getStockDao(){
        return new StockDao(connection);
    }

    public ParticipantOfStockDao getParticipantOfStackDao(){
        return new ParticipantOfStockDao(connection);
    }

    public CarDao getCarDao() {
        return new CarDao(connection);
    }

    public VolunteersGroupDao getVolunteersGroupDao() {
        return new VolunteersGroupDao(connection);
    }

    public FamiliesDao getFamiliesDao() {
        return new FamiliesDao(connection);
    }

    public SurveyDao getSurveyDao() {
        return new SurveyDao(connection);
    }

    public FamilyRateDao getFamilyRateDao() {
        return new FamilyRateDao(connection);
    }

    public StockTemplateDao getStockTemplateDao(){
        return new StockTemplateDao(connection);
    }

    public TaskTemplateDao getTaskTemplateDao(){
        return new TaskTemplateDao(connection);
    }

    public DatesTemplateDao getDatesTemplateDao() {
        return new DatesTemplateDao(connection);
    }

    public UserOfListDao getUserOfListDao() {
        return new UserOfListDao(connection);
    }

    public FriendsDao getFriendsDao() {
        return new FriendsDao(connection);
    }
}
