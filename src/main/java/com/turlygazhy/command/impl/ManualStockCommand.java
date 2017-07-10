package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Family;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by daniyar on 07.07.17.
 */
public class ManualStockCommand extends Command {
    List<Family> families;
    List<User> users;
    int groupId;
    Family family;
    enum Change{
        NAME,
        PHONE_NUMBER,
        ADDRESS,
        LOCATION
    }
    Change change;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            groupId = familiesDao.getGroupId(userDao.getUserByChatId(chatId).getId());
            users = familiesDao.getUsersByGroupId(groupId);
            families = familiesDao.getFamilyListByGroupId(groupId);
            sendFamiliesList();
            return false;
        }

        switch (waitingType) {
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
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(10))){
                    sendFamilyInfo();
                    return false;
                }
                switch (change){
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
        int count = families.size();
        Iterator iterator = families.listIterator();
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            if (!iterator.hasNext()){
                break;
            }
            for (int j = 0; j < 50; j++) {
                if (iterator.hasNext()){
                    sb.append(iterator.next().toString()).append("\n");
                } else {
                    break;
                }
            }
            sendMessage(sb.toString(), chatId, bot);
        }
        waitingType = WaitingType.CHOOSE_FAMILY;
    }
}
