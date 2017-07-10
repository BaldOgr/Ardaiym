package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 03.07.17.
 */
public class ChooseFamiliesCommand extends Command {
    int stockId;
    VolunteersGroup group;
    List<Family> families;
    Family family;
    private ManualStockCommand.Change change;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            //////      Проверяем, выбирала ли группа семьи ////////
            stockId = Integer.parseInt(updateMessageText.substring(3, updateMessageText.indexOf(" ")));

            group = volunteersGroupDao.getVolunteersGroup(chatId, stockId);
            if (group != null) {
                families = familiesDao.getFamilyListByCar(group.getCar());
                if (families.size() != 0) {             // Если да, то выводим семьи
                    sendFamiliesList();
                    waitingType = WaitingType.CHOOSE_FAMILY;
                } else {                            // Если нет, то даем им выбор
                    sendUnchoosedFamiliesCount();
                    sendMessage(65, chatId, bot);   // Сколько семей вы хотите?
                    waitingType = WaitingType.COUNT;
                }
            }
            return false;
        }

        switch (waitingType) {
            case COUNT:
                families = familiesDao.getFamilyListByCar(group.getCar());
                if (families.size() != 0){
                    sendFamiliesList();
                    sendMessage(messageDao.getMessageText(94) + families.size(), chatId, bot);   // Один из участников группы уже выбрал количество семей -
                    return false;
                }
                int count = Integer.parseInt(updateMessageText);
                group = volunteersGroupDao.getVolunteersGroup(chatId, stockId);
                families = familiesDao.getFamilyList(count);
                sendFamiliesList();
                for (Family family : families) {
                    family.setCarId(group.getCar().getId());
                    familiesDao.updateFamily(family);
                }
                sendUnchoosedFamiliesCountToAdmin();
                waitingType = WaitingType.CHOOSE_FAMILY;
                return false;

            case CHOOSE_FAMILY:
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
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(75))) {    // Готово
                    family.setFinished(true);
                    familiesDao.updateFamily(family);
                    families.remove(family);
                    if (families.size() == 0){
                        sendMessage(92, chatId, bot);   // Вы выполнили задание!
                        return true;
                    }
                    sendFamiliesList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(74))) {    // Выбрать семью
                    sendFamiliesList();
                    return false;
                }
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(76))) {   // ФИО
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = ManualStockCommand.Change.NAME;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(77))) {   // Номер телефона
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = ManualStockCommand.Change.PHONE_NUMBER;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(78))) {   // Адрес
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = ManualStockCommand.Change.ADDRESS;
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(79))) {   // Положение на карте
                    sendMessage(78, chatId, bot);   //  Введите новую информацию
                    change = ManualStockCommand.Change.LOCATION;
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
                sendMessage(40, chatId, bot);   // Готово
                sendFamilyInfo();
                return false;
        }
        return false;
    }

    private void sendUnchoosedFamiliesCount() throws SQLException, TelegramApiException {
        sendMessage(String.valueOf(familiesDao.getUnchoosedFamilies().size()) + " " + messageDao.getMessageText(95), chatId, bot);
    }

    private void sendUnchoosedFamiliesCountToAdmin() throws SQLException, TelegramApiException {
        Long adminChatId = stockDao.getStock(stockId).getAddedBy().getChatId();
        sendMessage(String.valueOf(familiesDao.getUnchoosedFamilies().size()) + " " + messageDao.getMessageText(95), adminChatId, bot);
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

    private void sendFamiliesList() throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        for (Family family : families) {
            sb.append(family).append("\n");
        }
        sendMessage(sb.toString());
        waitingType = WaitingType.CHOOSE_FAMILY;
    }

}
