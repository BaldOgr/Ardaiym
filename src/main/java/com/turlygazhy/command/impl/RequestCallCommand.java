package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.RequestCall;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 13.07.17.
 * Для корректной работы необходимо занести в базу данных следующее:
 * В таблицу MESSAGE:
 * - Введите ваше имя
 * - Введите вопрос
 * - Введите номер телефона
 * - Ваша заявка принята
 */
public class RequestCallCommand extends Command {
    RequestCall requestCall = new RequestCall();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null){
            sendMessage(11, chatId, bot);   // Введите ваше имя
            waitingType = WaitingType.NAME;
            return false;
        }

        switch (waitingType){
            case NAME:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) { // Назад
                    // Переход в главное меню
                    return false;
                }
                requestCall.setName(updateMessageText);
                sendMessage(40, chatId, bot);   // Введите вопрос
                waitingType = WaitingType.TEXT;
                return false;

            case TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) { // Назад
                    sendMessage(11, chatId, bot);
                    waitingType = WaitingType.NAME;
                    return false;
                }
                requestCall.setText(updateMessageText);
                sendMessage(41, chatId, bot);   // Введите номер телефона
                waitingType = WaitingType.PHONE_NUMBER;
                return false;


            case PHONE_NUMBER:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) { // Назад
                    sendMessage(40,chatId, bot);    // Введите вопрос
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessage.getContact() != null){
                    requestCall.setPhoneNumber(updateMessage.getContact().getPhoneNumber());
                } else {
                    requestCall.setPhoneNumber(updateMessageText);
                }
                requestCallDao.insert(requestCall);
                sendMessage(42, chatId, bot);   // Ваша заявка принята!
                try {
                    sendMessageToAdmin(requestCall.toString(), bot);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
        }

        return false;
    }
}
