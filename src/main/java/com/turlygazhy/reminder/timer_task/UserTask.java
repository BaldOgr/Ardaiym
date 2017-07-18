package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.entity.User;
import com.turlygazhy.reminder.Reminder;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

/**
 * Created by daniyar on 14.07.17.
 */
public class UserTask extends AbstractTask {
    private List<SendMessage> messages;

    public UserTask(Bot bot, Reminder reminder, List<SendMessage> messages) {
        super(bot, reminder);
        this.messages = messages;
    }

    @Override
    public void run() {
        try {
            for (SendMessage sendMessage : messages) {
                bot.sendMessage(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
