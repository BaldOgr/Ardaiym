package com.turlygazhy;

import com.turlygazhy.command.Command;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.exception.CannotHandleUpdateException;
import com.turlygazhy.exception.CommandNotFoundException;
import com.turlygazhy.service.CommandService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public class Conversation {
    private CommandService commandService = new CommandService();
    private Command command;
    private DaoFactory factory = DaoFactory.getFactory();
    private MessageDao messageDao = factory.getMessageDao();
    private KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();

    public void handleUpdate(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String inputtedText;
        if (updateMessage == null) {
            String data = update.getCallbackQuery().getData();
            if (data.contains("cmd=")){
                inputtedText = data.substring(data.indexOf("cmd=")+4);
            } else {
                inputtedText = update.getCallbackQuery().getData();
            }
            updateMessage = update.getCallbackQuery().getMessage();
        } else {
            inputtedText = updateMessage.getText();
        }

        try {
            command = commandService.getCommand(inputtedText);
        } catch (CommandNotFoundException e) {
            if (updateMessage.isGroupMessage()) {
                return;
            }
            if (command == null) {
                showMain(update, bot);
                return;
            }
        }
        command.initMessage(update, bot);
        boolean commandFinished = true;
        try {
            commandFinished = command.execute(update, bot);
        } catch (CannotHandleUpdateException e) {
            showMain(update, bot);
        }
        if (commandFinished) {
            command = null;
        }
    }

    private void showMain(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(2);
        SendMessage sendMessage = message.getSendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
    }
}
