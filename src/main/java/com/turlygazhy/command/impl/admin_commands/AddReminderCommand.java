package com.turlygazhy.command.impl.admin_commands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.tool.DateUtil;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by daniyar on 14.07.17.
 */
public class AddReminderCommand extends Command {
    Date date;
    User user;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage("Send date in format HH:MM DD.MM.YYYY");
            waitingType = WaitingType.DATE;
            return false;
        }

        switch (waitingType) {
            case DATE:
                date = DateUtil.parseDate(updateMessageText);
                sendMessage("Choose User");
                StringBuilder sb = new StringBuilder();
                for (User user : userDao.getUsers()) {
                    sb.append(user.toString()).append("\n");
                }
                sendMessage(sb.toString());
                waitingType = WaitingType.USER;
                return false;

            case USER:
                user = userDao.getUserById(Integer.parseInt(updateMessageText.substring(3)));
                sendMessage("Send text");
                waitingType = WaitingType.TEXT;
                return false;

            case TEXT:
                sb = new StringBuilder();
                sb.append("Reminder from ").append(userDao.getUserByChatId(chatId).getName()).append("\n")
                        .append(updateMessageText);
                Reminder reminder = new Reminder(bot);
                reminder.addMorningReminder(new SendMessage().setText(sb.toString()).setChatId(chatId));
                reminder.addLunchReminder(new SendMessage().setText(sb.toString()).setChatId(chatId));
                reminder.addEndDayReminder(new SendMessage().setText(sb.toString()).setChatId(chatId));
                sendMessage("Done!");

                return true;
        }
        return false;
    }
}
