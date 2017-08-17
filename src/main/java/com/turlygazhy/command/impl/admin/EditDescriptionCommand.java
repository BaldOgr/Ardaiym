package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Button;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CommandNotFoundException;
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
 * Created by daniyar on 30.06.17.
 */
public class EditDescriptionCommand extends Command {
    Button button;
    long messageId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!userDao.isAdmin(chatId)) {
            sendMessage(6, chatId, bot);
            return true;
        }
        if (waitingType == null) {
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(messageDao.getMessageText(54))
                    .setReplyMarkup(getChooseKeyboard()));
            waitingType = WaitingType.CHOOSE;
            return false;
        }
        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals("15")) {
                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(54)) // Что будем редактировать?
                            .setReplyMarkup(getChooseNewsKeyboard()));
                    waitingType = WaitingType.CHOOSE_NEWS;
                    return false;
                }
                if (updateMessageText.equals("18")) {    // Контакты
                    messageId = MessageDao.CONTACTS;
                }
                if (updateMessageText.equals("19")) {    // О фонде
                    messageId = MessageDao.ABOUT;
                }
                if (updateMessageText.equals("165")) {   // Приветствие
                    messageId = MessageDao.WELCOME;
                }
                sendMessage(55, chatId, bot);   // Введите текст
                waitingType = WaitingType.TEXT;
                return false;

            case TEXT:
                messageDao.updateText(updateMessageText, messageId);
                sendMessage(39, chatId, bot);   // Готово!
                return true;

            case CHOOSE_NEWS:
                button = buttonDao.getButton(Integer.parseInt(updateMessageText));
                sendMessage(56, chatId, bot);   // Введите ссылку
                waitingType = WaitingType.URL;
                return false;

            case URL:
                button.setUrl(updateMessageText);
                buttonDao.updateButtonText(button.getId(), button.getText(), button.getUrl());
                sendMessage(39, chatId, bot);   // Готово!
                return true;
        }
        return false;
    }

    private ReplyKeyboard getChooseKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(15));
        button.setCallbackData(String.valueOf(15));
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(18));
        button.setCallbackData(String.valueOf(18));
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(19));
        button.setCallbackData(String.valueOf(19));
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(151));
        button.setCallbackData(String.valueOf(165));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private ReplyKeyboard getChooseNewsKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(44));
        button.setCallbackData(String.valueOf(44));
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(45));
        button.setCallbackData(String.valueOf(45));
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(46));
        button.setCallbackData(String.valueOf(46));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
