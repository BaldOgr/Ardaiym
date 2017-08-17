package com.turlygazhy.reminder;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.timer_task.*;
import com.turlygazhy.tool.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * Created by Yerassyl_Turlygazhy on 02-Mar-17.
 */
public class Reminder {
    private static final Logger logger = LoggerFactory.getLogger(Reminder.class);

    private Bot bot;
    private Timer timer = new Timer(true);

    public Reminder(Bot bot) {
        this.bot = bot;
    }

    public void addMorningReminder(SendMessage message) {
        List<SendMessage> messages = new ArrayList<>();
        messages.add(message);
        addMorningReminder(messages);
    }

    public void addMorningReminder(List<SendMessage> messages) {
        Date date = new Date();
        if (date.getHours() >= 10) {
            date.setDate(date.getDate() + 1);

        }
        date.setHours(10);
        date.setMinutes(0);
        addReminder(date, messages);
    }

    public void addLunchReminder(SendMessage message) {
        List<SendMessage> messages = new ArrayList<>();
        messages.add(message);
        addLunchReminder(messages);
    }

    public void addLunchReminder(List<SendMessage> messages) {
        Date date = new Date();
        if (date.getHours() >= 15) {
            date.setDate(date.getDate() + 1);
        }
        date.setHours(15);
        date.setMinutes(0);
        addReminder(date, messages);
    }

    public void addEndDayReminder(SendMessage message) {
        List<SendMessage> messages = new ArrayList<>();
        messages.add(message);
        addEndDayReminder(messages);
    }

    public void addEndDayReminder(List<SendMessage> messages) {
        Date date = new Date();
        if (date.getHours() >= 21) {
            date.setDate(date.getDate() + 1);
        }
        date.setHours(21);
        date.setMinutes(0);
        addReminder(date, messages);
    }

    public void addReminder(Date date, SendMessage message) {
        List<SendMessage> messages = new ArrayList<>();
        messages.add(message);
        addReminder(date, messages);
    }

    public void addReminder(Date date, List<SendMessage> messages) {
        logger.info("New reminder at: " + date);

        System.out.println(date);
        UserTask userTask = new UserTask(bot, this, messages);
        timer.schedule(userTask, date);
    }


}
