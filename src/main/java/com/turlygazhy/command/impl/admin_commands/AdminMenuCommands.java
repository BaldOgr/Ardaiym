package com.turlygazhy.command.impl.admin_commands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 12.07.17.
 */
public class AdminMenuCommands extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (userDao.isAdmin(chatId)){
            sendMessage(3, chatId, bot);    // Админ меню
        } else {
            sendMessage("Nothing to show");
        }
        return true;
    }
}
