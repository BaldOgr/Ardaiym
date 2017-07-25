package com.turlygazhy.command;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.GoalDao;
import com.turlygazhy.dao.impl.*;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.VolunteersGroup;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.tool.SheetsAdapter;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public abstract class Command {
    protected long id;
    protected long messageId;

    protected DaoFactory factory = DaoFactory.getFactory();
    protected UserDao userDao = factory.getUserDao();
    protected MessageDao messageDao = factory.getMessageDao();
    protected KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    protected ButtonDao buttonDao = factory.getButtonDao();
    protected CommandDao commandDao = factory.getCommandDao();
    protected ConstDao constDao = factory.getConstDao();
    protected MemberDao memberDao = factory.getMemberDao();
    protected KeyWordDao keyWordDao = factory.getKeyWordDao();
    protected ReservationDao reservationDao = factory.getReservationDao();
    protected GroupDao groupDao = factory.getGroupDao();
    protected GoalDao goalDao = factory.getGoalDao();
    protected ThesisDao thesisDao = factory.getThesisDao();
    protected SavedResultsDao savedResultsDao = factory.getSavedResultsDao();
    protected ParticipantOfStockDao participantOfStockDao = factory.getParticipantOfStackDao();
    protected TaskDao taskDao = factory.getTypeOfWorkDao();
    protected ReportDao reportDao = factory.getReportDao();
    protected StockDao stockDao = factory.getStockDao();
    protected CarDao carDao = factory.getCarDao();
    protected VolunteersGroupDao volunteersGroupDao = factory.getVolunteersGroupDao();
    protected FamiliesDao familiesDao = factory.getFamiliesDao();
    protected SurveyDao surveyDao = factory.getSurveyDao();
    protected FamilyRateDao familyRateDao = factory.getFamilyRateDao();
    protected StockTemplateDao stockTemplateDao = factory.getStockTemplateDao();
    protected TaskTemplateDao taskTemplateDao = factory.getTaskTemplateDao();
    protected DatesTemplateDao datesTemplateDao = factory.getDatesTemplateDao();
    protected UserOfListDao userOfListDao = factory.getUserOfListDao();

    protected WaitingType waitingType;
    protected org.telegram.telegrambots.api.objects.Message updateMessage;
    protected String updateMessageText;
    protected Long chatId;
    protected Bot bot;

    public void initMessage(Update update, Bot bot) throws TelegramApiException, SQLException {
        this.bot = bot;
//        try {
//            SheetsAdapter.writeDataFromFamilySheet("14s83d9z4xwEmyOWwWSWPrA3QvDVE0NKl0JMGmf8dXjU", "Лист1",
//                    'A', 2, updateMessageText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        updateMessage = update.getMessage();
        if (updateMessage == null) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
//            String waitText = messageDao.getMessageText(88);
            if (chatId == null) {
                chatId = updateMessage.getChatId();
            }
//            try {
//                bot.editMessageText(new EditMessageText()
//                        .setText(waitText)
//                        .setChatId(chatId)
//                        .setMessageId(updateMessage.getMessageId())
//                );
//            } catch (Exception ignored) {
//            }
        } else {
            updateMessageText = updateMessage.getText();
            if (chatId == null) {
                chatId = updateMessage.getChatId();
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return is command finished
     */
    public abstract boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException;

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(messageId, chatId, bot, null);
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(text, chatId, bot, null);
    }

    protected void sendMessage(String text) throws SQLException, TelegramApiException {
        sendMessage(text, chatId, bot);
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.HTML);
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessageToAdmin(long messageId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot);
    }

    public long getAdminChatId() {
        return userDao.getAdminChatId();
    }

    public void sendMessageToAdmin(long messageId, Bot bot, Contact contact) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot, contact);
    }

    public void sendMessageToAdmin(String text, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(text, adminChatId, bot);
    }

    public void sendPhotoToAdmin(String photo, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendPhoto(new SendPhoto()
                .setChatId(adminChatId)
                .setPhoto(photo)
        );
    }

    public boolean validateTime(String theTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); //HH = 24h format
        dateFormat.setLenient(false); //this will not enable 25:67 for example
        try {
            dateFormat.parse(theTime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    protected List<InlineKeyboardButton> getNextPrevRows(boolean prev, boolean next) throws SQLException {
        List<InlineKeyboardButton> row = new ArrayList<>();
        String prevText = "prev";
        String nextText = "next";

        if (prev) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            prevButton.setText(prevText);
            prevButton.setCallbackData(prevText);
            row.add(prevButton);
        }
        if (next) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton();
            nextButton.setText(nextText);
            nextButton.setCallbackData(nextText);
            row.add(nextButton);
        }

        return row;
    }

}
