package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 29.06.17.
 */
public class AdminMenuCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (userDao.isAdmin(chatId)){
            sendMessage(7, chatId, bot);
            return true;
        }

        sendMessage(6, chatId, bot);
        return true;
    }
}
