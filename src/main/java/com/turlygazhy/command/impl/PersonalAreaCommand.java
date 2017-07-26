package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Answer;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 29.06.17.
 */
public class PersonalAreaCommand extends Command {
    User user;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage(20, chatId, bot);   // Личный кабинет
            waitingType = WaitingType.COMMAND;

            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(2, chatId, bot);
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(22))) {    // Редактировать профиль
                    user = userDao.getUserByChatId(chatId);
                    sendMessage(21, chatId, bot);   // Что будем менять?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(20, chatId, bot);   // Личный кабинет
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(23))) {    // Фио
                    sendMessage(22, chatId, bot);   // Введите новое имя
                    waitingType = WaitingType.NAME;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(24))) {    // Номер телефона
                    sendMessage(23, chatId, bot);   // Введите новый номер телефона
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(25))) {    // Город
                    sendMessage(24, chatId, bot);   // Введите новый город
                    waitingType = WaitingType.CITY;
                    return false;
                }
                return false;

            case NAME:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(21, chatId, bot);   // Что будем менять?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                user.setName(updateMessageText);
                userDao.updateUser(user);
                sendMessage(25, chatId, bot);   // Данные успешно обновлены
                sendMessage(20, chatId, bot);   // Личный кабинет
                waitingType = WaitingType.COMMAND;
                return false;

            case PHONE_NUMBER:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(21, chatId, bot);   // Что будем менять?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessage.getContact() != null) {
                    user.setPhoneNumber(updateMessage.getContact().getPhoneNumber());
                } else {
                    user.setPhoneNumber(updateMessageText);
                }
                userDao.updateUser(user);
                sendMessage(25, chatId, bot);   // Данные успешно обновлены
                sendMessage(20, chatId, bot);   // Личный кабинет
                waitingType = WaitingType.COMMAND;
                return false;

            case CITY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(21, chatId, bot);   // Что будем менять?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                user.setCity(updateMessageText);
                userDao.updateUser(user);
                sendMessage(25, chatId, bot);   // Данные успешно обновлены
                sendMessage(20, chatId, bot);   // Личный кабинет
                waitingType = WaitingType.COMMAND;
                return false;

        }
        return false;
    }
}
