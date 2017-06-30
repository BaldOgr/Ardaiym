package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */

public class NewStockCommand extends Command {
    private Stock stock;
    Task typeOfWork;
    private int shownDates = 0;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(30, chatId, bot);           // Введите название акции
            waitingType = WaitingType.NAME;
            stock = new Stock();
            return false;
        }

        switch (waitingType) {
            case NAME:
                stock.setTitle(updateMessageText);
                sendMessage(31, chatId, bot);
                waitingType = WaitingType.NAME_FOR_ADMIN;
                return false;

            case NAME_FOR_ADMIN:
                stock.setTitleForAdmin(updateMessageText);
                sendMessage(32, chatId, bot);
                waitingType = WaitingType.DESCRIPTION;
                return false;

            case DESCRIPTION:
                stock.setDescription(updateMessageText);
                sendMessage(33, chatId, bot);
                waitingType = WaitingType.TYPE_OF_WORK;
                return false;

            case TYPE_OF_WORK:
                typeOfWork = new Task();
                typeOfWork.setName(updateMessageText);
                bot.sendMessage(new SendMessage()
                        .setText(messageDao.getMessageText(34))
                        .setChatId(chatId)
                        .setReplyMarkup(getDeadlineKeyboard(shownDates)));
                stock.getTaskList().add(typeOfWork);
                waitingType = WaitingType.DATE;
                return false;

            case DATE:
                Dates date = new Dates();
                date.setDate(updateMessageText);
                typeOfWork.addDates(date);
                sendMessage(35, chatId, bot);
                waitingType = WaitingType.COMMAND;
                return false;

            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(40))) {
                    sendMessage(33, chatId, bot);
                    waitingType = WaitingType.TYPE_OF_WORK;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(41))) {
                    bot.sendMessage(new SendMessage()
                            .setText(messageDao.getMessageText(34))
                            .setChatId(chatId)
                            .setReplyMarkup(getDeadlineKeyboard(shownDates)));
                    waitingType = WaitingType.DATE;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(42))) {
                    sendMessage(36, chatId, bot);
                    stockDao.insertStock(stock);
                    StringBuilder sb = new StringBuilder();
                    sb.append(messageDao.getMessageText(24)).append(stock.getTitle());
                    for (User user : userDao.getUsers()) {
                        bot.sendMessage(new SendMessage()
                                .setChatId(user.getChatId())
                                .setText(sb.toString())
                                .setReplyMarkup(getKeyboard()));
                    }
                    sendMessage(39, chatId, bot);
                    return true;
                }
                return false;
        }

        return false;
    }

    private InlineKeyboardMarkup getKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(43));
        button.setCallbackData("id="+ stock.getId()+" cmd="+buttonDao.getButtonText(43));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getDeadlineKeyboard(int shownDates) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        Date date = new Date();
        date.setDate(date.getDate() + (shownDates * 9));
        List<InlineKeyboardButton> row = null;
        for (int i = 1; i < 10; i++) {
            if (row == null) {
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            int dateToString = date.getDate();
            String stringDate;
            if (dateToString > 9) {
                stringDate = String.valueOf(dateToString);
            } else {
                stringDate = "0" + dateToString;
            }
            int monthToString = date.getMonth() + 1;
            String stringMonth;
            if (monthToString > 9) {
                stringMonth = String.valueOf(monthToString);
            } else {
                stringMonth = "0" + monthToString;
            }
            String dateText = stringDate + "." + stringMonth;
            button.setText(dateText);
            button.setCallbackData(dateText);
            row.add(button);
            if (i % 3 == 0) {
                rows.add(row);
                row = null;
            }
            date.setDate(date.getDate() + 1);
        }

        if (shownDates > 0) {
            rows.add(getNextPrevRows(true, true));
        } else {
            rows.add(getNextPrevRows(false, true));
        }


        keyboard.setKeyboard(rows);
        return keyboard;
    }

//    private InlineKeyboardMarkup getInlineKeyboard() throws SQLException {
//        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//        for (String typeOfWork : stock.getTypeOfWork()) {
//            List<InlineKeyboardButton> row = new ArrayList<>();
//            InlineKeyboardButton button = new InlineKeyboardButton();
//            button.setText(typeOfWork);
//            button.setCallbackData(typeOfWork + "id" + stock.getId() + " cmd=" + buttonDao.getButtonText(40));
//            row.add(button);
//            buttons.add(row);
//        }
//        keyboardMarkup.setKeyboard(buttons);
//        return keyboardMarkup;
//    }
}
