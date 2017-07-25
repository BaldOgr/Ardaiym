package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by daniyar on 07.07.17.
 */
public class ManualStockCommand extends Command {
    private List<Family> families;
    private List<Family> tempFamilies;
    private List<User> users;
    private Family family;
    private int stockId;
    private int groupId;
    private Long adminChatId;

    private Change change;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            if (updateMessageText.startsWith("stockId=")) {
                stockId = Integer.parseInt(updateMessageText.substring(8, updateMessageText.indexOf(" ")));
                sendMessage(120, chatId, bot);  // Хотите принять участие в акции?
                waitingType = WaitingType.CHOOSE;
            } else {
                sendMessage(131, chatId, bot);
                List<Stock> stocks = stockDao.getStocks(3);
                StringBuilder sb = new StringBuilder();
                stocks.forEach(stock -> {
                    sb.append(stock.toString()).append("\n");
                });
                sendMessage(sb.toString());
                waitingType = WaitingType.CHOOSE_STOCK;
            }
            return false;
        }

        switch (waitingType) {
            case CHOOSE_STOCK:
                stockId = Integer.parseInt(updateMessageText.substring(3));
                sendMessage(120, chatId, bot);
                waitingType = WaitingType.CHOOSE;
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(122))) {   // Принять
                    groupId = familiesDao.getGroupId(userDao.getUserByChatId(chatId).getId(), stockId);
                    users = familiesDao.getUsersByGroupId(groupId, stockId);
                    adminChatId = stockDao.getStock(stockId).getAddedBy().getChatId();
                    families = familiesDao.getFamilyListByGroupId(groupId, stockId);
                    return sendFamiliesList();
                }
                if (updateMessageText.equals(buttonDao.getButtonText(123))) {   // Отказаться
                    sendMessage(121, chatId, bot);  // Ждем Вас в следующих акциях
                    return true;
                }
                return false;

            case CHOOSE_FAMILY:
                tempFamilies = familiesDao.getRejectedFamiliesByGroup(groupId, stockId);
                if (tempFamilies.size() != 0) {
                    families.addAll(tempFamilies);
                    sendMessage(130, chatId, bot);
                    tempFamilies.forEach(obj -> {
                        obj.setVolunteersGroupId(0);
                        try {
                            familiesDao.updateFamily(family);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    tempFamilies = null;
                }
                int familyId = Integer.parseInt(updateMessageText.substring(3));
                for (Family family1 : families) {
                    if (family1.getId() == familyId) {
                        family = family1;
                        break;
                    }
                }
                sendFamilyInfo();
                return false;

            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(73))) {    // Изменить информацию
                    sendMessage(77, chatId, bot);   // Что изменяем?
                    waitingType = WaitingType.CHOOSE_PARAMETR;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(75))) {    // Готово
//                    family.setFinished(true);
//                    familiesDao.updateFamily(family);
//                    families.remove(family);
//                    return sendFamiliesList();
                    sendMessage(122, chatId, bot);  // Вы подтверждаете, что семье была оказана помощь?
                    waitingType = WaitingType.CHOOSE_ANSWER;
                    return false;
                }
                // Выбрать семью
                return updateMessageText.equals(buttonDao.getButtonText(74)) && sendFamiliesList();

            case CHOOSE_ANSWER:
                if (updateMessageText.equals(buttonDao.getButtonText(56))) {    // Да
                    sendMessage(123, chatId, bot);  // Хотите отправить отчет?
                    waitingType = WaitingType.CHOOSE_REPORT_ANSWER;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(57))) {    // Нет
                    sendMessage(124, chatId, bot);  // Причина отказа
                    waitingType = WaitingType.CHOOSE_DECILINE_ANSWER;
                    return false;
                }
                return false;

            case CHOOSE_REPORT_ANSWER:
                if (updateMessageText.equals(buttonDao.getButtonText(56))) {    // Да
                    sendMessage(75, chatId, bot);  // Напишите отчет
                    waitingType = WaitingType.REPORT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(57))) {    // Нет
                    family.setStatus(1);    // Завершен
                    familiesDao.updateFamily(family);
                    families.remove(family);
                    return sendFamiliesList();
                }
                return false;

            case REPORT:
                family.setStatus(1);    // Завершен
                familiesDao.updateFamily(family);
                families.remove(family);
                return sendFamiliesList();

            case CHOOSE_DECILINE_ANSWER:
                if (updateMessageText.equals(buttonDao.getButtonText(124))) {   // Их нет дома
                    family.setStatus(2);
                }
                if (updateMessageText.equals(buttonDao.getButtonText(125))) {   // Неверный адрес
                    family.setStatus(3);
                }
                if (updateMessageText.equals(buttonDao.getButtonText(126))) {   // Нет времени
                    family.setStatus(4);
                }
                String msg = family.getStatusString() + "\n" + family;
                bot.sendMessage(new SendMessage()
                        .setChatId(adminChatId)
                        .setText(msg)
                        .setReplyMarkup(getKeyboard()));   // Отправляем отказников админу
                familiesDao.updateFamily(family);
                families.remove(family);
                return sendFamiliesList();

            case CHOOSE_PARAMETR:
                if (updateMessageText.equals(buttonDao.getButtonText(76))) {   // ФИО
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = Change.NAME;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(77))) {   // Номер телефона
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = Change.PHONE_NUMBER;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(78))) {   // Адрес
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = Change.ADDRESS;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(79))) {   // Положение на карте
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = Change.LOCATION;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {   // Назад
                    sendFamilyInfo();
                    return false;
                }
                return false;

            case TEXT:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendFamilyInfo();
                    return false;
                }
                switch (change) {
                    case NAME:
                        family.setName(updateMessageText);
                        break;
                    case PHONE_NUMBER:
                        family.setPhoneNumber(updateMessageText);
                        break;
                    case ADDRESS:
                        family.setAddress(updateMessageText);
                        break;
                    case LOCATION:
                        Location location = updateMessage.getLocation();
                        family.setLatitude(location.getLatitude());
                        family.setLongitude(location.getLongitude());
                        break;
                }
                familiesDao.updateFamily(family);
                sendMessage("Done");
                sendFamilyInfo();
                return false;
        }
        return false;
    }

    private ReplyKeyboard getKeyboard() throws SQLException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData("id=" + stockId + " cmd=" + buttonDao.getButtonText(127));
        button.setText(buttonDao.getButtonText(127));
        row.add(button);
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private void sendFamilyInfo() throws SQLException, TelegramApiException {
        sendMessage(family.toString());
        bot.sendLocation(new SendLocation()
                .setLongitude(family.getLongitude().floatValue())
                .setLatitude(family.getLatitude().floatValue())
                .setChatId(chatId)
                .setReplyMarkup(keyboardMarkUpDao.select(33)));
        waitingType = WaitingType.COMMAND;
    }

    private boolean sendFamiliesList() throws SQLException, TelegramApiException {
        int count = families.size();
        if (count == 0) {
            sendMessage(113, chatId, bot);  // Семей больше нет
            sendInfoForAdmin();
            return true;
        }
        Iterator iterator = families.listIterator();
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            if (!iterator.hasNext()) {
                break;
            }
            for (int j = 0; j < 50; j++) {
                if (iterator.hasNext()) {
                    sb.append(iterator.next().toString()).append("\n");
                } else {
                    break;
                }
            }
            sendMessage(sb.toString(), chatId, bot);
        }
        waitingType = WaitingType.CHOOSE_FAMILY;
        return false;
    }

    private void sendInfoForAdmin() throws SQLException, TelegramApiException {
        Stock stock = stockDao.getStock(stockId);
        families = familiesDao.getFamilyListByGroupId(groupId, stockId);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(messageDao.getMessageText(114)).append("</b>\n"); // Волонтеры
        for (int i = 0; i < users.size(); i++) {
            sb.append(i + 1).append(") ").append(users.get(i).getName()).append("\n")
                    .append("\t").append(users.get(i).getPhoneNumber()).append("\n");
        }
        sb.append("<b>").append(messageDao.getMessageText(115)).append("</b>\n"); // Семьи
        for (int i = 0; i < families.size(); i++) {
            Family family = families.get(i);
            sb.append(i + 1).append(") ").append(family.getName()).append("\n")
                    .append("\t").append(family.getAddress()).append("\n")
                    .append("\t").append(family.getPhoneNumber()).append("\n");
        }

        sendMessage(sb.toString(), stock.getAddedBy().getChatId(), bot);
    }
}