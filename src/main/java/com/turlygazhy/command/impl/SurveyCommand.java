package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 07.07.17.
 */
public class SurveyCommand extends Command {
    int stockId;
    int rate;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            stockId = Integer.parseInt(updateMessageText.substring(3, updateMessageText.indexOf(" ")));
            sendMessage(81, chatId, bot);   // Оцените акцию
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(81))) {    // Да
                    rate = 1;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(82))) {    // Нет
                    rate = -1;
                }
                sendMessage(82, chatId, bot);
                waitingType = WaitingType.TEXT;
                return false;

            case TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(83))) {    // Нет пожеланий
                    surveyDao.insertSurvey(stockId, null, rate);
                } else {
                    surveyDao.insertSurvey(stockId, updateMessageText, rate);
                }
                sendMessage(83, chatId, bot);   // Спасибо за ваш отзыв
                return true;
        }

        return false;
    }
}
