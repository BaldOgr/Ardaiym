package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class AcceptSignUpCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String data = update.getCallbackQuery().getData();
        int userId = Integer.parseInt(data.substring(3, data.indexOf(" ")));
        User user = userDao.getUserById(userId);
        friendsDao.insert(user.getChatId(), chatId);
        user.setRules(1);
        userDao.updateUser(user);
        bot.editMessageText(new EditMessageText()
                .setText("Done!")
                .setChatId(chatId)
                .setMessageId(updateMessage.getMessageId()));
        sendMessage(15, user.getChatId(), bot);
        return true;
    }
}
