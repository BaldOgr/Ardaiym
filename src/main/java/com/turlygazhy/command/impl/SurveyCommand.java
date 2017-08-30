package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Family;
import com.turlygazhy.entity.FamilyRate;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 07.07.17.
 */
public class SurveyCommand extends Command {
    enum Type {
        GOOD_FAMILIES,
        LIKE,
        DID_NOT_LIKE,
        COMMENT,
        DONE
    }

    private List<Family> families;
    private List<Family> tempFamilies;
    private Type type;
    private int stockId;
    FamilyRate rate;
    private int groupId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            stockId = Integer.parseInt(updateMessageText.substring(3, updateMessageText.indexOf(" ")));
            updateMessageText = updateMessageText.substring(updateMessageText.indexOf(" ")+1);
            groupId = Integer.parseInt(updateMessageText.substring(updateMessageText.indexOf("gr=")+3, updateMessageText.indexOf(" ")));
            sendMessage(106, chatId, bot);  // Как вы думаете, есть ли среди тех...
            type = Type.GOOD_FAMILIES;
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(56))) {    // Да
                    if (tempFamilies != null) {
                        families = new ArrayList<>(tempFamilies);
                    }
                    sendFamilyList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(57))) {    // Нет
                    return changeType();
                }
                return false;


            case CHOOSE_FAMILY:
                if (updateMessageText.equals(buttonDao.getButtonText(42))) {    // Готово
                    return changeType();
                }
                int familyId = Integer.parseInt(updateMessageText);
                for (Family family : families) {
                    if (family.getId() == familyId) {
                        families.remove(family);
                        break;
                    }
                }
                rate = new FamilyRate();
                rate.setRatedUser(userDao.getUserByChatId(chatId));
                rate.setFamily(familiesDao.getFamily(familyId));
                rate.setType(type.ordinal());
                switch (type) {
                    case COMMENT:
                        sendMessage(112, chatId, bot);  // Напишите коментарий
                        waitingType = WaitingType.TEXT;
                        return false;

                    default:
                        familyRateDao.insertRate(rate);
                        sendFamilyList();
                        return false;
                }

            case TEXT:
                rate.setComment(updateMessageText);
                familyRateDao.insertRate(rate);
                sendFamilyList();
                return false;

        }

        return false;
    }

    private boolean changeType() throws SQLException, TelegramApiException {
        switch (type) {
            case GOOD_FAMILIES:
                sendMessage(107, chatId, bot);  // Есть ли семьи, которые Вам сильно понравились?
                waitingType = WaitingType.CHOOSE;
                type = Type.LIKE;
                return false;

            case LIKE:
                sendMessage(108, chatId, bot);  // Есть ли семьи, которые Вам НЕ понравились,
                waitingType = WaitingType.CHOOSE;
                type = Type.DID_NOT_LIKE;
                return false;

            case DID_NOT_LIKE:
                sendMessage(109, chatId, bot);  // Хотели бы отправить коментарий к определенной семье?
                waitingType = WaitingType.CHOOSE;
                type = Type.COMMENT;
                return false;

            case COMMENT:
                sendMessage(110, chatId, bot);  // Спасибо! Нам важно Ваше мнение!
                return true;
        }
        return false;
    }

    private void sendFamilyList() throws SQLException, TelegramApiException {
        if (families == null) {
            tempFamilies = familiesDao.getFamilyListByGroupId(groupId, stockId);
            families = new ArrayList<>(tempFamilies);
        }
        if (families.size() == tempFamilies.size() || type == Type.COMMENT) {
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(messageDao.getMessageText(71))
                    .setReplyMarkup(getKeyboard()));
        } else {
            bot.editMessageText(new EditMessageText()
                    .setMessageId(updateMessage.getMessageId())
                    .setChatId(chatId)
                    .setText(messageDao.getMessageText(71))
                    .setReplyMarkup(getKeyboard()));
        }
        waitingType = WaitingType.CHOOSE_FAMILY;
    }

    private InlineKeyboardMarkup getKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Family family : families) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData(String.valueOf(family.getId()));
            button.setText(family.getName());
            row.add(button);
            rows.add(row);
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(buttonDao.getButtonText(42));
        button.setText(buttonDao.getButtonText(42));
        row.add(button);
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
