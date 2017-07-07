package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 03.07.17.
 */
public class ChooseCarCommand extends Command {

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            bot.sendMessage(new SendMessage()
                    .setText("Choose car")
                    .setChatId(chatId)
                    .setReplyMarkup(getCarKeyboard()));
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                int carId = Integer.parseInt(updateMessageText);
                User user = userDao.getUserByChatId(chatId);
                volunteersGroupDao.insertVolunteer(user, carId);
                VolunteersGroup group = volunteersGroupDao.getVolunteersGroup(carId);
                StringBuilder sb = new StringBuilder();
                for (User user1 : group.getUsers()){
                    sb.append(user1.getName()).append("\n");
                    sendMessage(user.getName(), user1.getChatId(), bot);
                }
                sendMessage(sb.toString());
                return true;
        }

        return false;
    }

    private ReplyKeyboard getCarKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Car car : carDao.getCars()) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(car.getName());
            button.setCallbackData(String.valueOf(car.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        keyboard.setKeyboard(row);
        return keyboard;
    }
}
