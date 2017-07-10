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

/**
 * Created by daniyar on 06.07.17.
 */
public class StartStockManualAdminCommand extends Command {
    private Stock stock;
    private List<User> users;
    private List<User> userVolunteers;
    private List<List<User>> usersOnStock = new ArrayList<>();
    private List<Integer> familyGroups = new ArrayList<>();
    private List<Integer> familiesForVolunteers;

    public StartStockManualAdminCommand(Stock stock) {
        this.stock = stock;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);
        if (!userDao.isAdmin(chatId)) {
            sendMessage(6, chatId, bot);    // Главное меню
            return true;
        }

        if (waitingType == null) {
            if (stock.getStatus() == 3){
                sendMessage(98, chatId, bot);   //Акция уже началась, чтобы закончить, нажмите кнопку "Закончить акцию"
                return true;
            }
            familiesDao.loadFamiliesFromGoogleSheets(stock.getId());
            List<Family> families = familiesDao.getFamilyList();
            for (Family family : families) {
                if (!hasFamily(family, familyGroups)) {
                    familyGroups.add(family.getGroup());
                }
            }
            if (users == null) {
                users = new ArrayList<>();
                for (Task task : stock.getTaskList()) {
                    for (Participant participant : task.getParticipants()) {
                        if (!hasUser(participant.getUser().getChatId(), users)) {
                            users.add(participant.getUser());           // Добавляем пользователей только 1 раз, чтобы не отправлять одно сообщение 2 раза
                        }
                    }
                }
            }
            sendMessage(70, chatId, bot);   // Выберите волонтеров
            sendUserList();
            userVolunteers = new ArrayList<>();
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(64))) {    // Выбрать семьи
                    if (userVolunteers.size() == 0){
                        sendMessage(86, chatId, bot);   // Выберите минимум 1 волонтера
                        sendUserList();
                        return false;
                    }
                    sendMessage(71, chatId, bot);   // Выберите семьи
                    sendFamiliesList();
                    familiesForVolunteers = new ArrayList<>();
                    waitingType = WaitingType.CHOOSE_FAMILY;
                    return false;
                }
                int userId = Integer.parseInt(updateMessageText.substring(3));
                for (User user : users) {
                    if (user.getId() == userId) {
                        userVolunteers.add(user);
                        users.remove(user);
                        break;
                    }
                }
                sendUserList();
                return false;
            case CHOOSE_FAMILY:
                if (updateMessageText.equals(buttonDao.getButtonText(67))) {    // Добавить новую группу
                    if (familiesForVolunteers.size() == 0){
                        sendMessage(87, chatId, bot);   // Выберите минимум 1 семью
                        sendFamiliesList();
                        return false;
                    }
                    familiesDao.insertVolunteerGroups(userVolunteers, userVolunteers.get(0).getId());
                    familiesDao.insertFamilyGroups(familiesForVolunteers, userVolunteers.get(0).getId());
                    usersOnStock.add(userVolunteers);
                    userVolunteers = new ArrayList<>();
                    familiesForVolunteers = new ArrayList<>();
                    sendMessage(70, chatId, bot);   // Выберите волонтеров
                    sendUserList();
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(68))) {    // Закончить и начать акцию
                    familiesDao.insertVolunteerGroups(userVolunteers, userVolunteers.get(0).getId());
                    familiesDao.insertFamilyGroups(familiesForVolunteers, userVolunteers.get(0).getId());
                    usersOnStock.add(userVolunteers);
                    distributeAll();
                    stock.setStatus(3);
                    stock.setAddedBy(userDao.getUserByChatId(chatId));
                    stockDao.updateStock(stock);
                    return false;
                }
                int familyGroupId = Integer.parseInt(updateMessageText.substring(3));
                for (Integer familyGroup : familyGroups){
                    if (familyGroup == familyGroupId){
                        familyGroups.remove(familyGroup);
                        break;
                    }
                }
                familiesForVolunteers.add(familyGroupId);
                sendFamiliesList();
                return false;
        }
        return false;
    }

    private boolean hasFamily(Family family, List<Integer> familyGroups) {
        for (Integer groupId : familyGroups) {
            if (family.getGroup() == groupId) {
                return true;
            }
        }
        return false;
    }

    private void distributeAll() throws SQLException, TelegramApiException {
        sendMessage(50, chatId, bot);   // Начинаю рассылку
        for (List<User> users : usersOnStock) {
            for (User user : users) {
                try {
                    bot.sendMessage(new SendMessage()
                            .setText(messageDao.getMessageText(76))     // Начинаем акцию
                            .setChatId(user.getChatId())
                            .setReplyMarkup(getKeyboard()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                }
            }
        }
        sendMessage(40, chatId, bot);   // Готово
        sendMessage(7,chatId, bot);     // Меню админов
    }

    private ReplyKeyboard getKeyboard() throws SQLException {

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(72));
        button.setCallbackData("cmd=" + buttonDao.getButtonText(72));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private void sendFamiliesList() throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        for (Integer groupId : familyGroups) {
            sb.append("/id").append(groupId).append("\n");
        }
        sendMessage(sb.toString(), chatId, bot);
    }

    private void sendUserList() throws TelegramApiException {
        if (users.size() == 0) {
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText("No user"));
        } else {
            StringBuilder sb = new StringBuilder();
            for (User user : users) {
                sb.append("/id").append(user.getId()).append(" - ").append(user.getName()).append("\n");
            }
            bot.sendMessage(new SendMessage()
                    .setText(sb.toString())
                    .setChatId(chatId));
        }
    }

    private boolean hasUser(Long chatId, List<User> users) {
        for (User user : users) {
            if (user.getChatId().equals(chatId)) {
                return true;
            }
        }
        return false;
    }

}
