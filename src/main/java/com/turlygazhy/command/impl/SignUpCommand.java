package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 29.06.17.
 */
public class SignUpCommand extends Command {
    User user;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null){
            sendMessage(11, chatId, bot);   // Введите ваше ФИО
            user = new User();
            user.setChatId(chatId);
            waitingType = WaitingType.NAME;
            return false;
        }

        switch (waitingType){
            case NAME:
                user.setName(updateMessageText);
                sendMessage(12, chatId, bot);   // Введите вашу дату рождения
                waitingType = WaitingType.BIRTHDAY;
                return false;

            case BIRTHDAY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(11, chatId, bot);   // Введите ваше ФИО
                    waitingType = WaitingType.NAME;
                    return false;
                }
                user.setBirthday(updateMessageText);
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
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(10, chatId, bot);   // Ваш пол
                    waitingType = WaitingType.SEX;
                    return false;
                }
                user.setPhoneNumber(updateMessageText);
                sendMessage(13, chatId, bot);
                waitingType = WaitingType.CITY;
                return false;

            case CITY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(14, chatId, bot);   // Введите свой номер телефона
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                }
                user.setCity(updateMessageText);
                userDao.insertUser(user);
                sendMessage(15, chatId, bot);       // Готово! Чтобы войти в главное меню, напишите /start
                return true;
        }

        return false;
    }
}
