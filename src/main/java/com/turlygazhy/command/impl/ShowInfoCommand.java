package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.reminder.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 * how to use: https://www.youtube.com/watch?v=5sBI1kLu7Ks
 */
public class ShowInfoCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(ShowInfoCommand.class);

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        logger.debug("show info.");
        try {
            if (userDao.getUserByChatId(chatId) == null){
                sendMessage(1, chatId, bot);    // Чтобы продолжить нужно зарегистрироваться
            } else {
                sendMessage(2, chatId, bot);    // Главное меню
            }
            return true;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
