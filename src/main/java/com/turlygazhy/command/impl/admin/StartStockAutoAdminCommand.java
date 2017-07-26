package com.turlygazhy.command.impl.admin;

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
import java.util.Random;

/**
 * Created by daniyar on 30.06.17.
 */
public class StartStockAutoAdminCommand extends Command {
    private Stock stock;
    List<Long> users;
    Task task;

    public StartStockAutoAdminCommand(Stock stock) {
        this.stock = stock;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);
        if (!userDao.isAdmin(chatId)) {
            sendMessage(6, chatId, bot);
            return true;
        }

        if (users == null) {
            addAllUsersInStock();
        }


        if (waitingType == null) {
            if (stock.getStatus() == 0) {
                familiesDao.loadFamiliesFromGoogleSheets(stock.getId());
                String messageText = messageDao.getMessageText(58) + "\n"
                        + stock.getTitle() + "\n";
                for (Long userChatId : users) {
                    bot.sendMessage(new SendMessage()
                            .setChatId(userChatId)
                            .setText(messageText)
                            .setReplyMarkup(getKeyboard()));
                }
                stock.setStatus(1);
                stock.setAddedBy(userDao.getUserByChatId(chatId));
                stockDao.updateStock(stock);
                sendMessage(62, chatId, bot);   // Раздать список машин?
                waitingType = WaitingType.CAR_LIST;
                return false;
            } else {
                switch (stock.getStatus()) {
                    case 1:
                        sendMessage(62, chatId, bot);   // Раздать список машин?
                        waitingType = WaitingType.CAR_LIST;
                        break;
                    case 2:
                        sendMessage(66, chatId, bot);   // Раздать список семей?
                        waitingType = WaitingType.FAMILY_LIST;
                        break;
                    case 3:
                        sendMessage(67, chatId, bot);   // Завершить акцию?
                        waitingType = WaitingType.COMMAND;
                        break;
                }
            }
        }

        switch (waitingType) {
            case CAR_LIST:
                if (updateMessageText.equals(buttonDao.getButtonText(58))) {    // Раздать список машин
                    sendMessage("Working...");
                    List<Car> cars = carDao.getCars();
                    for (Car car : cars) {
                        users.remove(car.getUserId());
                    }
                    for (Long userChatId : users) {
                        bot.sendMessage(new SendMessage()
                                .setChatId(userChatId)
                                .setText(messageDao.getMessageText(63))   // Выберите машину
                                .setReplyMarkup(getChooseCarKeyboard()));
                    }
                    stock.setStatus(2);
                    stockDao.updateStock(stock);
                    sendMessage("Done!");
                    sendMessage(66, chatId, bot);   // Раздать список семей?
                    waitingType = WaitingType.FAMILY_LIST;
                }
                return false;
            case FAMILY_LIST:
                if (updateMessageText.equals(buttonDao.getButtonText(59))) {    // Раздать список семей
                    sendMessage("Working...");
                    addAllUsersInStock();
                    for (Long userChatId : users) {
                        bot.sendMessage(new SendMessage()
                                .setChatId(userChatId)
                                .setText(messageDao.getMessageText(64))   // Выберите семьи
                                .setReplyMarkup(getChooseFamiliesKeyboard()));
                    }
                    sendMessage("Done!");
                    stock.setStatus(3);
                    stockDao.updateStock(stock);
                    sendMessage(67, chatId, bot);   // Завершить акцию?
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                return false;
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(42))) {    // Готово
                    sendMessage(messageDao.getMessageText(23), chatId, bot);    // Напишите отчет
                    waitingType = WaitingType.TEXT;
                }
                return false;

            case TEXT:
                stock.setReport(updateMessageText);
                stock.setStatus(4);
                stockDao.updateStock(stock);
                familiesDao.downloadFamiliesToGoogle(stock.getId());
                sendMessage(91, chatId, bot);   // Рассылавем опрос
                sendSurvey();
                sendMessage(40, chatId, bot);   // Готово
                return true;
        }


        return false;
    }

    private ReplyKeyboard getChooseFamiliesKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(61));
        button.setCallbackData("id=" + stock.getId() + " cmd=" + buttonDao.getButtonText(61));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private ReplyKeyboard getChooseCarKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(60));
        button.setCallbackData("id=" + stock.getId() + " cmd=" + buttonDao.getButtonText(60));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private void addAllUsersInStock() {
        users = new ArrayList<>();
        for (Task task : stock.getTaskList()) {
            for (Participant participant : task.getParticipants()) {
                if (!hasUser(participant.getUser().getChatId(), users)) {
                    users.add(participant.getUser().getChatId());           // Добавляем пользователей только 1 раз, чтобы не отправлять одно сообщение 2 раза
                }
            }
        }
    }

    private void sendSurvey() throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        sb.append(stock.getTitle()).append("\n").append(messageDao.getMessageText(80));
        for (User user : userDao.getUsers()) {
            bot.sendMessage(new SendMessage()
                    .setChatId(user.getChatId())
                    .setText(sb.toString())
                    .setReplyMarkup(getSurveyKeyboard()));
        }
    }

    private ReplyKeyboard getSurveyKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(80));
        button.setCallbackData("id=" + stock.getId() + " cmd=" + buttonDao.getButtonText(80));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }


    private ReplyKeyboard getKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(55));
        button.setCallbackData("id=" + stock.getId() + " cmd=" + buttonDao.getButtonText(55));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private boolean hasUser(Long chatId, List<Long> users) {
        for (Long user : users) {
            if (user.equals(chatId)) {
                return true;
            }
        }
        return false;
    }
}
