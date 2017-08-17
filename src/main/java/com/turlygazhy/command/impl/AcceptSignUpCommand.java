package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AcceptSignUpCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String data = update.getCallbackQuery().getData();
        int userId = Integer.parseInt(data.substring(3, data.indexOf(" ")));
        User user = userDao.getUserById(userId);
        friendsDao.insert(user.getChatId(), chatId);
        bot.editMessageText(new EditMessageText()
                .setText("Thanks!")
                .setChatId(chatId)
                .setMessageId(updateMessage.getMessageId()));
        StringBuilder sb = new StringBuilder();
        sb.append(user.getName()).append(messageDao.getMessageText(163)).append("\n");
        for (User userFriend : friendsDao.getFriends(user.getChatId())) {
            sb.append(userFriend.getName()).append("\n");
        }
        bot.sendMessage(new SendMessage()
                .setChatId(userDao.getAdminChatId())
                .setText(sb.toString())
                .setReplyMarkup(getAcceptSignUpKeyboard(user)));
        return true;
    }

    private InlineKeyboardMarkup getAcceptSignUpKeyboard(User user) throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(138));
        button.setCallbackData("id=" + user.getId() + " cmd=" + buttonDao.getButtonText(138));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
