package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 04.07.17.
 */
public class ContactsCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        sendMessage(MessageDao.CONTACTS, chatId, bot);
        User user = userDao.getUserByChatId(userDao.getAdminChatId());
        bot.sendContact(new SendContact()
                .setChatId(chatId)
                .setFirstName(user.getName())
                .setPhoneNumber(user.getPhoneNumber()));
        return true;
    }
}
