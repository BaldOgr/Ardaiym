package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Family;
import com.turlygazhy.entity.User;
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

public class SendRejectedFamiliesCommand extends Command {
    private List<Family> families;
    private List<Family> tempFamilies = new ArrayList<>();
    //    private List<User> users;
    private List<User> tempUsers = new ArrayList<>();
    private List<Integer> groups;
    private int groupId;
    private int stockId;
    private int familiesCount;
//    private int usersCount;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            stockId = Integer.parseInt(updateMessageText.substring(3, updateMessageText.indexOf(" ")));
            groups = familiesDao.getGroupsByStockId(stockId);
            families = familiesDao.getFamilyListByStatus(4);
            familiesCount = families.size();
            sendGroups();
            return false;
        }

        switch (waitingType) {
            case CHOOSE_GROUP:
                groupId = Integer.parseInt(updateMessageText);
                tempUsers = familiesDao.getUsersByGroupId(groupId, stockId);
                familiesCount = families.size();
                sendFamilies();
                return false;

            case CHOOSE_FAMILY:
                if (updateMessageText.equals(buttonDao.getButtonText(42))) {    // Готово
                    sendMessage("Sending...");
                    for (Family family : tempFamilies) {
                        family.setVolunteersGroupId(groupId);
                        familiesDao.updateFamily(family);
                    }
                    sendMessage("Done!");
                    return true;
                }
                int familyId = Integer.parseInt(updateMessageText);
                for (Family family : families) {
                    if (familyId == family.getId()) {
                        tempFamilies.add(family);
                        families.remove(family);
                        break;
                    }
                }
                sendFamilies();
                return false;

        }

        return false;
    }

    private void sendGroups() throws TelegramApiException, SQLException {
        bot.sendMessage(new SendMessage()
                .setText("Text")
                .setChatId(chatId)
                .setReplyMarkup(getChooseGroupKeyboard()));
        waitingType = WaitingType.CHOOSE_GROUP;

    }

    private InlineKeyboardMarkup getChooseGroupKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Integer groupId : groups) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(groupId));
            button.setCallbackData(String.valueOf(groupId));
            buttons.add(button);
            row.add(buttons);
        }
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private void sendFamilies() throws TelegramApiException, SQLException {
        if (families.size() == familiesCount) {
            bot.sendMessage(new SendMessage()
                    .setText("Text")
                    .setChatId(chatId)
                    .setReplyMarkup(getChooseFamiliesKeyboard()));
        } else {
            bot.editMessageText(new EditMessageText()
                    .setMessageId(updateMessage.getMessageId())
                    .setText("Text")
                    .setChatId(chatId)
                    .setReplyMarkup(getChooseFamiliesKeyboard()));

        }
        waitingType = WaitingType.CHOOSE_FAMILY;
    }

    private InlineKeyboardMarkup getChooseFamiliesKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Family family : families) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(family.getName());
            button.setCallbackData(String.valueOf(family.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(42));
        button.setCallbackData(buttonDao.getButtonText(42));
        buttons.add(button);
        row.add(buttons);
        keyboard.setKeyboard(row);
        return keyboard;
    }

}
