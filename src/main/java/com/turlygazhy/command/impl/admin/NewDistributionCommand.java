package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by lol on 05.06.2017.
 */
public class NewDistributionCommand extends Command {
    private List<User> users;
    private Stock stock;
    private StringBuilder sb = new StringBuilder();

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!userDao.isAdmin(chatId)){
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
                    sendMessage(44, chatId, bot);
                    users = userDao.getUsers();
                    waitingType = WaitingType.MESSAGE;
                    sb.append(messageDao.getMessageText(43)).append("\n");
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(48))) {    // Для волонтеров акции
                    if (stockDao.getUndoneStockList().size() == 0) {
                        sendMessage(45, chatId, bot);          // Нет действующих акции
                        return false;
                    }
                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(46))
                            .setReplyMarkup(getChooseStockKeyboard(stockDao.getUndoneStockList())));
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                return false;
            case CHOOSE:
                int stockId = Integer.parseInt(updateMessageText);
                stock = stockDao.getStock(stockId);
                sb.append(messageDao.getMessageText(47)).append("\n<b>").append(stock.getTitle()).append("</b>\n");
                bot.sendMessage(new SendMessage()
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
                    if (users.size() == 0){
                        sendMessage(52, chatId, bot);   // Нет волонтеров, участвующих в акции
                        bot.sendMessage(new SendMessage()
                                .setChatId(chatId)
                                .setText(messageDao.getMessageText(46))
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
                sendMessage(44, chatId, bot);   //  Введите сообщение
                waitingType = WaitingType.MESSAGE;
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

    private boolean userContains(User user) {
        for (User user1 : users) {
            if (Objects.equals(user.getChatId(), user1.getChatId())) {
                return true;
            }
        }
        return false;
    }

    private ReplyKeyboard getChooseWorkKeyboard(List<Task> typesOfWork) throws SQLException {
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

    private ReplyKeyboard getChooseStockKeyboard(List<Stock> undoneStocks) {
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
}
