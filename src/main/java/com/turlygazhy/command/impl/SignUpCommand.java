package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.tool.ButtonsLeaf;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by daniyar on 29.06.17.
 */
public class SignUpCommand extends Command {
    private User user;
    private String year;
    private String month;
    private String date;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage(11, chatId, bot);   // Введите вашу Фамилию
            user = new User();
            user.setChatId(chatId);
            waitingType = WaitingType.NAME;
            return false;
        }

        switch (waitingType) {
            case NAME:
                user.setName(updateMessageText);
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(16)));   // Введите ваше Имя
                waitingType = WaitingType.SECOND_NAME;
                return false;

            case SECOND_NAME:
                user.setName(user.getName() + " " + updateMessageText);
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(12))
                        .setReplyMarkup(getDecades()));   // Введите вашу дату рождения
                waitingType = WaitingType.BIRTHDAY;
                return false;

            case BIRTHDAY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(11, chatId, bot);   // Введите ваше ФИО
                    waitingType = WaitingType.NAME;
                    return false;
                }
                bot.editMessageText(new EditMessageText()
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(99))
                        .setReplyMarkup(getYears(Integer.parseInt(updateMessageText))));
                waitingType = WaitingType.YEAR;
                return false;

            case YEAR:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(11, chatId, bot);   // Введите ваше ФИО
                    waitingType = WaitingType.NAME;
                    return false;
                }
                year = updateMessageText;
                bot.editMessageText(new EditMessageText()
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(100))
                        .setReplyMarkup(getMonth()));
                waitingType = WaitingType.MONTH;
                return false;

            case MONTH:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(11, chatId, bot);   // Введите ваше ФИО
                    waitingType = WaitingType.NAME;
                    return false;
                }
                month = updateMessageText;
                bot.editMessageText(new EditMessageText()
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(101))
                        .setReplyMarkup(getDays(Integer.parseInt(updateMessageText))));
                waitingType = WaitingType.DAY;
                return false;

            case DAY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(11, chatId, bot);   // Введите ваше ФИО
                    waitingType = WaitingType.NAME;
                    return false;
                }
                bot.editMessageText(new EditMessageText()
                        .setChatId(chatId)
                        .setText("Ok")
                        .setMessageId(updateMessage.getMessageId()));
                date = updateMessageText;
                user.setBirthday(date + "." + month + "." + year);
                sendMessage(10, chatId, bot);   // Ваш пол
                waitingType = WaitingType.SEX;
                return false;

            case SEX:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(12, chatId, bot);   // Введите вашу дату рождения
                    waitingType = WaitingType.BIRTHDAY;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(11))) {    // Мужчина
                    user.setSex(true);
                } else {
                    user.setSex(false);
                }
                sendMessage(14, chatId, bot);   // Введите свой номер телефона
                waitingType = WaitingType.PHONE_NUMBER;
                return false;

            case PHONE_NUMBER:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(10, chatId, bot);   // Ваш пол
                    waitingType = WaitingType.SEX;
                    return false;
                }
                if (updateMessage.getContact() != null) {
                    user.setPhoneNumber(updateMessage.getContact().getPhoneNumber());
                } else {
                    user.setPhoneNumber(updateMessageText);
                }
                sendMessage(13, chatId, bot);   // Выберите область
                waitingType = WaitingType.CITY;
                return false;

            case CITY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(14, chatId, bot);   // Введите свой номер телефона
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(96))) { // Карагандинская обл.
                    bot.editMessageText(new EditMessageText()
                            .setChatId(chatId)
                            .setMessageId(updateMessage.getMessageId())
                            .setText(messageDao.getMessageText(13)) // Выберите городо
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(38)));
                    return false;
                }
                user.setCity(updateMessageText);
                userDao.insertUser(user);
                for (User user : userDao.getAdmins()) {
                    bot.sendMessage(new SendMessage()
                            .setChatId(user.getChatId())
                            .setText(messageDao.getMessageText(137) + "\n" + user.getName())   // Подтвердите регистрацию)
                            .setReplyMarkup(getAcceptSignUpKeyboard()));
                }
                sendMessage(143, chatId, bot);  // Ваша кандидатура подана на рассмотрение
                return true;
        }

        return false;
    }

    private InlineKeyboardMarkup getDecades() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 1960; i <= 2010; i += 10) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(i + "");
            button.setCallbackData(i + "");
            row.add(button);
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getYears(int decade) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = decade; i < decade + 10; ) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (i >= decade + 10) {
                    break;
                }
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(i + "");
                button.setCallbackData(i + "");
                row.add(button);
                i++;
            }
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getMonth() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 1; i < 13; ) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (i >= 13) {
                    break;
                }
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(i < 10 ? "0" + i : i + "");
                button.setCallbackData(i < 10 ? "0" + i : i + "");
                row.add(button);
                i++;
            }
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getDays(int month) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int days;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            days = 30;
        } else if (month == 2) {
            days = 28;
        } else {
            days = 31;
        }
        for (int i = 1; i <= days; ) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (i > days) {
                    break;
                }
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(i < 10 ? "0" + i : i + "");
                button.setCallbackData(i < 10 ? "0" + i : i + "");
                row.add(button);
                i++;
            }
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getAcceptSignUpKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(138));
        button.setCallbackData("id=" + user.getId() + " cmd=" + buttonDao.getButtonText(138));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
