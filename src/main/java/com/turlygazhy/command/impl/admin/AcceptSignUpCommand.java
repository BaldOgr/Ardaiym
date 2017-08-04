package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AcceptSignUpCommand extends Command {
    private User user;
    private List<User> users;
    private List<User> distributeFor;
    
    private int page = 0;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            String data = update.getCallbackQuery().getData();
            int userId = Integer.parseInt(data.substring(3, data.indexOf(" ")));
            user = userDao.getUserById(userId);
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(user.getName() + "\n" + user.getCity() + "\n" + user.getPhoneNumber() + "\n" + messageDao.getMessageText(138))
                    .setReplyMarkup(keyboardMarkUpDao.select(55)));
            waitingType = WaitingType.CHOOSE_ANSWER;
            return false;
        }

        switch (waitingType) {
            case CHOOSE_ANSWER:
                if (updateMessageText.equals(buttonDao.getButtonText(139))) {   // Подтверждаю
                    friendsDao.insert(user.getChatId(), chatId);
                    user.setRules(1);
                    userDao.updateUser(user);
                    distributeFor = new ArrayList<>();
                    sendMessage("Done!");
                    sendMessage(15, user.getChatId(), bot);
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(140))) {   // Спросить у волонтеров
                    distributeFor = new ArrayList<>();
                    users = userDao.getUsers();
                    for (User user1 : users) {
                        if (user1.getId() == user.getId()) {
                            users.remove(user1);
                            break;
                        }
                    }
                    bot.sendMessage(new SendMessage()
                            .setText(messageDao.getMessageText(142))
                            .setChatId(chatId)
                            .setReplyMarkup(getUsersKeyboard()));
                    waitingType = WaitingType.CHOOSE_USERS;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(141))) {   // Отказать
                    sendMessage(139, chatId, bot);  // Вы уверены?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                return false;

            case CHOOSE_USERS:
                if (updateMessageText.equals("prev")) {
                    page--;
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setReplyMarkup(getUsersKeyboard())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(142)));
                    return false;
                }
                if (updateMessageText.equals("next")) {
                    page++;
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setReplyMarkup(getUsersKeyboard())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(142)));
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(47))) {    // Для всех волонтеров
                    distributeFor = userDao.getUsers();
                    distributeRequest();
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(42))) {    // Готово
                    if (distributeFor.size() == 0) {
                        sendMessage(86, chatId, bot);
                        return false;
                    }
                    distributeRequest();
                    return true;
                }
                int userId = Integer.parseInt(updateMessageText);
                for (User user : users) {
                    if (user.getId() == userId) {
                        users.remove(user);
                        distributeFor.add(user);
                        break;
                    }
                }
                bot.editMessageText(new EditMessageText()
                        .setMessageId(updateMessage.getMessageId())
                        .setReplyMarkup(getUsersKeyboard())
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(142)));
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(56))) {    // Yes
                    sendMessage(140, chatId, bot);  // Вы точно уверены?
                    waitingType = WaitingType.SECOND_CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(57))) {    // No
                    sendMessage(55, chatId, bot);
                    waitingType = WaitingType.CHOOSE_ANSWER;
                    return false;
                }
                return false;

            case SECOND_CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(56))) {    // Yes
                    sendMessage(141, chatId, bot);  // Предложение отклонено
                    sendMessage(147, user.getChatId(), bot);    // Ваша кандидатура была отклонена
                    userDao.delete(user);
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(57))) {    // No
                    sendMessage(55, chatId, bot);
                    waitingType = WaitingType.CHOOSE_ANSWER;
                    return false;
                }
                return false;

        }
        return false;
    }

    private void distributeRequest() throws SQLException, TelegramApiException {
        bot.editMessageText(new EditMessageText()
                .setChatId(chatId)
                .setMessageId(updateMessage.getMessageId())
                .setText("Doing..."));
        for (User user : distributeFor) {
            try {
                bot.sendMessage(new SendMessage()
                        .setChatId(user.getChatId())
                        .setText(user.getName() + "\n" + user.getCity() + "\n" + user.getPhoneNumber() + "\n" + messageDao.getMessageText(138))
                        .setReplyMarkup(getAcceptKeyboard()));
            } catch (TelegramApiException ex) {
                sendMessage("BAN FROM: " + user.getName());
            }
        }
        sendMessage("Done");
    }

    private ReplyKeyboard getAcceptKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(142));
        button.setCallbackData("id=" + user.getId() + " cmd=" + buttonDao.getButtonText(142));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getUsersKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = page * 10; i < page * 10 + 10 && i < users.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(users.get(i).getName());
            button.setCallbackData(String.valueOf(users.get(i).getId()));
            row.add(button);
            rows.add(row);
        }
        if (page != 0) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("prev");
            button.setCallbackData("prev");
            row.add(button);
            rows.add(row);
        }
        if (page * 10 + 9 < users.size()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("next");
            button.setCallbackData("next");
            row.add(button);
            rows.add(row);
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(47));
        button.setCallbackData(buttonDao.getButtonText(47));
        row.add(button);
        rows.add(row);
        button = new InlineKeyboardButton();
        row = new ArrayList<>();
        button.setText(buttonDao.getButtonText(42));
        button.setCallbackData(buttonDao.getButtonText(42));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
