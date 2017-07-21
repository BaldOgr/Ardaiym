package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class AddToParticipantOfStock extends Command {
    Stock stock;
    Task task = null;
    Dates dates;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendFirstMessage(update);
        }
        switch (waitingType) {
            case CHOOSE_STOCK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                int stockId = Integer.parseInt(updateMessageText);
                sendTypeOfWorkList(stockId);
                return false;

            case CHOOSE_TYPE_OF_WORK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                int typeOfWorkId = Integer.parseInt(update.getCallbackQuery().getData());
                for (Task task : stock.getTaskList()) {
                    if (task.getId() == typeOfWorkId) {
                        this.task = task;
                        break;
                    }
                }
                if (task.getDates().size() > 1) {
                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(38))     // Выберите дату
                            .setReplyMarkup(getDatesKeyboard()));
                    waitingType = WaitingType.CHOOSE_DATE;
                    return false;
                } else {
                    if (participantOfStockDao.hasParticipant(chatId, task.getId(), task.getDates().get(0).getId())) {
                        sendMessage(57, chatId, bot);   // Вы уже участвуете в данной работе
                        return false;
                    }
                    dates = task.getDates().get(0);
                    sendTaskInfo();
                    return false;
                }

            case CHOOSE_DATE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                int dateId = Integer.parseInt(update.getCallbackQuery().getData());
                for (Dates dates1 : task.getDates()) {
                    if (dates1.getId() == dateId) {
                        dates = dates1;
                        break;
                    }
                }
                if (participantOfStockDao.hasParticipant(chatId, task.getId(), dateId)) {
                    sendMessage(57, chatId, bot);   // Вы уже участвуете в данной работе
                    return false;
                }
                sendMessage(40, chatId, bot);   // Готово
                sendTaskInfo();
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(111))) {   // Хочу еще одно задание!
                    stock.getTaskList().remove(task);
                    task = null;
                    sendFirstMessage(update);
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(112))) {   // Мне достаточно
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                return false;

        }
        return false;
    }

    private void sendFirstMessage(Update update) throws SQLException, TelegramApiException {
        if (update.getCallbackQuery() == null || stock != null) {
            bot.sendMessage(new SendMessage()
                    .setText(messageDao.getMessageText(37))
                    .setChatId(chatId)
                    .setReplyMarkup(getStocksKeyboard()));
            waitingType = WaitingType.CHOOSE_STOCK;
        } else {
            String data = update.getCallbackQuery().getData();
            String stockIdString = data.substring(data.indexOf("id=") + 3, data.indexOf(" "));
            int stockId = Integer.valueOf(stockIdString);
            sendTypeOfWorkList(stockId);
        }
    }

    private void sendTaskInfo() throws SQLException, TelegramApiException {
        addParticipant(dates.getId());
        sendMessage(40, chatId, bot);   // Готово
        String message = "<b>" + messageDao.getMessageText(104) + "</b>" + stock.getTitle() + "\n"
                + "<b>" + messageDao.getMessageText(89) + ": " + "</b>" + task.getName() + "\n"
                + "<b>" + messageDao.getMessageText(105) + "</b>" + dates.getDate();
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(message)
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboardMarkUpDao.select(40)));
        waitingType = WaitingType.CHOOSE;
    }

    private void addParticipant(int dateId) throws SQLException {
        Participant participant = new Participant();
        participant.setTypeOfWorkId(task.getId());
        participant.setUser(userDao.getUserByChatId(chatId));
        participant.setDateId(dateId);
        task.addParticipantOfStocks(participant);
        participantOfStockDao.insertParticipant(participant);
    }

    private ReplyKeyboard getDatesKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Dates dates : task.getDates()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(dates.getDate());
            button.setCallbackData(String.valueOf(dates.getId()));
            row.add(button);
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private void sendTypeOfWorkList(int stockId) throws SQLException, TelegramApiException {
        stock = stockDao.getStock(stockId);
        Iterator iterator = stock.getTaskList().iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            for (Participant participant : task.getParticipants()) {
                if (participant.getUser().getChatId().equals(chatId)) {
                    iterator.remove();
                    break;
                }
            }
        }
        bot.sendMessage(new SendMessage()
                .setText(messageDao.getMessageText(41))
                .setChatId(chatId)
                .setReplyMarkup(getTypeOfWorkKeyboard()));
        waitingType = WaitingType.CHOOSE_TYPE_OF_WORK;
    }

    private InlineKeyboardMarkup getStocksKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Stock stock : stockDao.getUndoneStockList()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(stock.getTitle());
            button.setCallbackData(String.valueOf(stock.getId()));
            row.add(button);
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getTypeOfWorkKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Task typeOfWork : stock.getTaskList()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(typeOfWork.getName());
            button.setCallbackData(String.valueOf(typeOfWork.getId()));
            row.add(button);
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
