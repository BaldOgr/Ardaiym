package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
            User user = userDao.getUserByChatId(chatId);
            if (user == null) {
                sendMessage(1, chatId, bot);    // Чтобы продолжить нужно зарегистрироваться
            } else if (user.getRules() != 0) {
                sendMessage(2, chatId, bot);    // Главное меню
            } else {
//                sendMessage(144, chatId, bot);  // Ваша кандидатура на рассмотрении
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(144))
                        .setReplyMarkup(new ReplyKeyboardMarkup()));
            }
            return true;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
