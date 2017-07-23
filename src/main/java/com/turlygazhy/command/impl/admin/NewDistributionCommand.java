package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by lol on 05.06.2017.
 */
public class NewDistributionCommand extends Command {
    private List<User> users;
    private List<User> usersForList;
    private int count;
    private Stock stock;
    private StringBuilder sb = new StringBuilder();
    private UserOfList userOfList;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!userDao.isAdmin(chatId)) {
            sendMessage(6, chatId, bot);
            return true;
        }
        if (waitingType == null) {
            sendMessage(42, chatId, bot);   // Для кого рассылка
            waitingType = WaitingType.FOR_WHOM;
            return false;
        }

        switch (waitingType) {

            ///////// Выбираем каких волонтерам пересылать сообщения ////////////

            case FOR_WHOM:
                if (updateMessageText.equals(buttonDao.getButtonText(47))) {    // Для всех
                    users = userDao.getUsers();
                    bot.editMessageText(new EditMessageText()
                            .setText(messageDao.getMessageText(117)) // Выберие действие
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(42)));
                    waitingType = WaitingType.CHOOSE_TYPE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(48))) {    // Для волонтеров акции
                    if (stockDao.getUndoneStockList().size() == 0) {
                        bot.editMessageText(new EditMessageText()
                                .setText(messageDao.getMessageText(45)) // Нет действующих акции
                                .setMessageId(updateMessage.getMessageId())
                                .setChatId(chatId)
                                .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(13)));
                        return false;
                    }
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(46))
                            .setReplyMarkup((InlineKeyboardMarkup) getChooseStockKeyboard(stockDao.getUndoneStockList())));
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(113))) {   // Создать список
                    sendMessage(111, chatId, bot);  // Введите название
                    waitingType = WaitingType.NAME;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(114))) {   // Списки
                    sendListKeyboard();
                    waitingType = WaitingType.CHOOSE_LIST;
                    return false;
                }
                return false;
            case CHOOSE:
                int stockId = Integer.parseInt(updateMessageText);
                stock = stockDao.getStock(stockId);
                sb.append(messageDao.getMessageText(47)).append("\n<b>").append(stock.getTitle()).append("</b>\n");
                bot.editMessageText(new EditMessageText()
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(48))
                        .setReplyMarkup(getChooseWorkKeyboard(stock.getTaskList())));
                waitingType = WaitingType.CHOOSE_TYPE_OF_WORK;
                return false;

            case CHOOSE_TYPE_OF_WORK:
                if (updateMessageText.equals(buttonDao.getButtonText(47))) {    // Для всех волонтеров
                    users = new ArrayList<>();
                    for (Task task : stock.getTaskList()) {
                        for (Participant participant : task.getParticipants()) {
                            if (!userContains(participant.getUser())) {
                                users.add(participant.getUser());
                            }
                        }
                    }
                    if (users.size() == 0) {
                        bot.editMessageText(new EditMessageText()
                                .setMessageId(updateMessage.getMessageId())
                                .setChatId(chatId)
                                .setText(messageDao.getMessageText(52))
                                .setReplyMarkup(getChooseStockKeyboard(stockDao.getUndoneStockList())));
                        waitingType = WaitingType.CHOOSE;
                        return false;
                    }
                } else {
                    List<Participant> participants = taskDao.getTypeOfWork(Integer.parseInt(updateMessageText)).getParticipants();
                    if (participants.size() == 0) {
                        sendMessage(49, chatId, bot);   // Нет волонтеров, участвующих в данном задании
                        return false;
                    }
                    users = new ArrayList<>();
                    for (Participant participant : participants) {
                        users.add(participant.getUser());
                    }
                }
                bot.editMessageText(new EditMessageText()
                        .setText(messageDao.getMessageText(117)) // Выберие действие
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(42)));
                waitingType = WaitingType.CHOOSE_TYPE;
                return false;

            case NAME:
                userOfList = new UserOfList();
                userOfList.setName(updateMessageText);
                usersForList = userDao.getUsers();
                count = usersForList.size();
                waitingType = WaitingType.CHOOSE_USERS;
                sendUserListKeyboard();
                return false;


            case CHOOSE_USERS:
                if (updateMessageText.equals(buttonDao.getButtonText(42))) {    // Готово
                    userOfListDao.insert(userOfList);
                    sendMessage(40, chatId, bot);   // Готово
                    sendMessage(42, chatId, bot);   // Для кого рассылка
                    waitingType = WaitingType.FOR_WHOM;
                    return false;
                }
                int userId = Integer.parseInt(updateMessageText);
                userOfList.getUsers().add(userDao.getUserById(userId));
                Iterator iterator = usersForList.iterator();
                while (iterator.hasNext()) {
                    User user = (User) iterator.next();
                    if (user.getId() == userId) {
                        iterator.remove();
                        break;
                    }
                }
                sendUserListKeyboard();
                return false;

            ////////// Выбор списка //////////////////

            case CHOOSE_LIST:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    bot.editMessageText(new EditMessageText()
                            .setText(messageDao.getMessageText(45)) // Нет действующих акции
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(13)));

                    waitingType = WaitingType.FOR_WHOM;
                    return false;
                }
                int userOfListId = Integer.parseInt(updateMessageText);
                users = userOfListDao.getUserOfList(userOfListId).getUsers();
                bot.editMessageText(new EditMessageText()
                        .setText(messageDao.getMessageText(117)) // Выберие действие
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(42)));
                waitingType = WaitingType.CHOOSE_TYPE;
                return false;


            case CHOOSE_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendListKeyboard();
                    waitingType = WaitingType.CHOOSE_LIST;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(116))) {   // Введите текст
                    sendMessage(44, chatId, bot);   //  Введите сообщение
                    waitingType = WaitingType.MESSAGE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(117))) {   // Скрипты
                    bot.editMessageText(new EditMessageText()
                            .setText(messageDao.getMessageText(118)) // Выберите тип скрипта
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(43)));
                    waitingType = WaitingType.CHOOSE_STOCK_TEMPLATE_TYPE;
                    return false;
                }
                return false;

            case CHOOSE_STOCK_TEMPLATE_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setText(messageDao.getMessageText(117)) // Выберие действие
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(42)));
                    waitingType = WaitingType.CHOOSE_TYPE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(118))) {    // CTA script
                    sendTemplateStocks(true);
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(119))) {    // NONE script
                    sendTemplateStocks(false);
                    return false;
                }
                return false;

            case CHOOSE_STOCK_TEMPLATE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(118))
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(43)));
                    waitingType = WaitingType.CHOOSE_STOCK_TEMPLATE_TYPE;
                    return false;
                }
                int templateStockId = Integer.parseInt(updateMessageText);
                stock = stockTemplateDao.getStock(templateStockId);
                bot.editMessageText(new EditMessageText()
                        .setMessageId(updateMessage.getMessageId())
                        .setChatId(chatId)
                        .setParseMode(ParseMode.HTML)
                        .setText(stock.parseStockForMessage())
                        .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(44)));
                waitingType = WaitingType.COMMAND;
                return false;

            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setText(messageDao.getMessageText(117)) // Выберие действие
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(42)));
                    waitingType = WaitingType.CHOOSE_TYPE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(120))) {   // Отправить
                    sendMessage(50, chatId, bot);   // Начинаю рассылку...
                    if (stock.isCTA()){
                        stockDao.insertStock(stock);
                    }
                    SendMessage sendMessage = new SendMessage()
                            .setText(stock.parseStockForMessage())
                            .setParseMode(ParseMode.HTML);
                    if (stock.isCTA()) {
                        sendMessage = sendMessage.setReplyMarkup(getKeyboard());
                    }
                    for (User user : users) {
                        try {
                            bot.sendMessage(sendMessage.setChatId(user.getChatId()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                        }
                    }
                    sendMessage(51, chatId, bot);   // Рассылка закончена!
                }
                if (updateMessageText.equals(buttonDao.getButtonText(121))) {   // Редактировать
                    return false;
                }
                return false;

            ///////// Сама рассылка сообщений /////////

            case MESSAGE:
                sendMessage(50, chatId, bot);   // Начинаю рассылку...
                sb.append(updateMessageText);
                for (User user : users) {
                    try {
                        bot.sendMessage(new SendMessage()
                                .setText(sb.toString())
                                .setChatId(user.getChatId())
                                .setParseMode(ParseMode.HTML));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                    }
                }
                sendMessage(51, chatId, bot);   // Рассылка закончена!
                return true;
        }

        return false;
    }

    private void sendTemplateStocks(boolean cta) throws SQLException, TelegramApiException {
        List<Stock> stocks = stockTemplateDao.getStocks(cta);
        bot.editMessageText(new EditMessageText()
                .setMessageId(updateMessage.getMessageId())
                .setText(messageDao.getMessageText(119))
                .setChatId(chatId)
                .setReplyMarkup(getChooseTemplateStock(stocks)));
        waitingType = WaitingType.CHOOSE_STOCK_TEMPLATE;
    }

    private void sendListKeyboard() throws SQLException, TelegramApiException {
        bot.editMessageText(new EditMessageText()
                .setMessageId(updateMessage.getMessageId())
                .setChatId(chatId)
                .setText(messageDao.getMessageText(116))    // Выберите список
                .setReplyMarkup(getChooseListKeyboard()));
    }

    private void sendUserListKeyboard() throws TelegramApiException, SQLException {
        if (count == usersForList.size()) {
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(messageDao.getMessageText(70)) // Выберите волонтеров
                    .setReplyMarkup(getUserListKeyboard()));
        } else {
            bot.editMessageText(new EditMessageText()
                    .setMessageId(updateMessage.getMessageId())
                    .setChatId(chatId)
                    .setText(messageDao.getMessageText(70)) // Выберите волонтеров
                    .setReplyMarkup(getUserListKeyboard()));
        }
    }

    private InlineKeyboardMarkup getChooseTemplateStock(List<Stock> stocks) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Stock stock : stocks) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(stock.getTitleForAdmin());
            button.setCallbackData(String.valueOf(stock.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        InlineKeyboardButton forAll = new InlineKeyboardButton();
        forAll.setText(buttonDao.getButtonText(10));
        forAll.setCallbackData(buttonDao.getButtonText(10));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(forAll);
        row.add(buttons);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private InlineKeyboardMarkup getUserListKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (User user : usersForList) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(user.getName());
            button.setCallbackData(String.valueOf(user.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        InlineKeyboardButton forAll = new InlineKeyboardButton();
        forAll.setText(buttonDao.getButtonText(42));
        forAll.setCallbackData(buttonDao.getButtonText(42));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(forAll);
        row.add(buttons);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private InlineKeyboardMarkup getChooseListKeyboard() throws SQLException {
        List<UserOfList> userOfLists = userOfListDao.getUserOfList();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (UserOfList userOfList : userOfLists) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(userOfList.getName());
            button.setCallbackData(String.valueOf(userOfList.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(10));
        button.setCallbackData(buttonDao.getButtonText(10));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(button);
        row.add(buttons);
        keyboard.setKeyboard(row);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private InlineKeyboardMarkup getChooseWorkKeyboard(List<Task> typesOfWork) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Task typeOfWork : typesOfWork) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(typeOfWork.getName());
            button.setCallbackData(String.valueOf(typeOfWork.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        InlineKeyboardButton forAll = new InlineKeyboardButton();
        forAll.setText(buttonDao.getButtonText(47));
        forAll.setCallbackData(buttonDao.getButtonText(47));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(forAll);
        row.add(buttons);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private InlineKeyboardMarkup getChooseStockKeyboard(List<Stock> undoneStocks) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Stock stock : undoneStocks) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(stock.getTitle());
            button.setCallbackData(String.valueOf(stock.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private InlineKeyboardMarkup getKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(43));
        button.setCallbackData("id=" + stock.getId() + " cmd=" + buttonDao.getButtonText(43));
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private boolean userContains(User user) {
        for (User user1 : users) {
            if (Objects.equals(user.getChatId(), user1.getChatId())) {
                return true;
            }
        }
        return false;
    }
}
