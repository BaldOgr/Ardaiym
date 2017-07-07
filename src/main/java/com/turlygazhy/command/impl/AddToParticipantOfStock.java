package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class AddToParticipantOfStock extends Command {
    Stock stock;
    Task task = null;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            if (update.getCallbackQuery() == null) {
                bot.sendMessage(new SendMessage()
                        .setText(messageDao.getMessageText(37))
                        .setChatId(chatId)
                        .setReplyMarkup(getStocksKeyboard()));
                waitingType = WaitingType.CHOOSE_STOCK;
                return false;
            } else {
                String data = update.getCallbackQuery().getData();
                String stockIdString = data.substring(data.indexOf("id=") + 3, data.indexOf(" "));
                int stockId = Integer.valueOf(stockIdString);
                sendTypeOfWorkList(bot, stockId);
                return false;
            }
        }
        switch (waitingType) {
            case CHOOSE_STOCK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                int stockId = Integer.parseInt(updateMessageText);
                sendTypeOfWorkList(bot, stockId);
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
                    addParticipant(task.getDates().get(0).getId());
                    sendMessage(40, chatId, bot);   // Готово
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }

            case CHOOSE_DATE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                int dateId = Integer.parseInt(update.getCallbackQuery().getData());
                if (participantOfStockDao.hasParticipant(chatId, task.getId(), dateId)) {
                    sendMessage(57, chatId, bot);   // Вы уже участвуете в данной работе
                    return false;
                }
                addParticipant(dateId);
                sendMessage(40, chatId, bot);   // Готово
                sendMessage(2, chatId, bot);    // Главное меню
                return true;

        }
        return false;
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

    private void sendTypeOfWorkList(Bot bot, int stockId) throws SQLException, TelegramApiException {
        stock = stockDao.getStock(stockId);
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
