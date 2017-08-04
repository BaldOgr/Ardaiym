package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
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

/**
 * Created by daniyar on 30.06.17.
 */
public class ShowStockCommand extends Command {
    private List<Stock> stocks;
    private Stock stock;
    private Command command;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!userDao.isAdmin(chatId)) {
            sendMessage(6, chatId, bot);
            return true;
        }

        if (command != null) {
            if (command.execute(update, bot)) {
                command = null;
                sendStockInfo();
            }
            return false;
        }
        if (waitingType == null) {
            sendMessage(53, chatId, bot);   // Меню акции
            waitingType = WaitingType.CHOOSE_STOCK_TYPE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE_STOCK_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(7, chatId, bot);    // Меню админа
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(50))) {    // Законченные акции
                    stocks = stockDao.getDoneStockList();
                }
                if (updateMessageText.equals(buttonDao.getButtonText(51))) {    // Незаконченные акции
                    stocks = stockDao.getUndoneStockList();
                }
                if (stocks.size() == 0) {
                    sendMessage(69, chatId, bot);
                    return false;
                }
                sendStockList();
                return false;

            case CHOOSE_STOCK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(53, chatId, bot);
                    waitingType = WaitingType.CHOOSE_STOCK_TYPE;
                    return false;
                }
                int stockId = Integer.parseInt(updateMessageText.substring(3));
                stock = stockDao.getStock(stockId);
                sendStockInfo();
                return false;

            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(53))) {    // Начать акцию
                    sendMessage(72, chatId, bot);   // Автоматический или ручной режим?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(54))) {    // Закончить акцию
                    endStock();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(150))) {   // Список отказников
                    command = new SendRejectedFamiliesCommand(stock.getId());
                    if (command.execute(update, bot)) {
                        command = null;
                        sendStockInfo();
                    }
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(84))) {
                    List<List> objects = surveyDao.getSurveis(stock.getId());
                    List<String> reports = objects.get(0);
                    List<Integer> rating = objects.get(1);
                    int stockRating = 0;
                    StringBuilder sb = new StringBuilder();

                    if (rating.size() != 0) {
                        for (Integer rate : rating) {
                            if (rate == 1) {
                                stockRating++;
                            }
                        }
                        sb.append(messageDao.getMessageText(85)).append(stockRating).append(":").append(rating.size() - stockRating).append("\n");
                    }
                    if (reports.size() != 0) {
                        for (String str : reports) {
                            if (str != null) {
                                sb.append(str).append("\n");
                            }
                        }
                    }
                    if (sb.toString().length() == 0) {
                        sendMessage(84, chatId, bot);   // Нет результатов
                        return false;
                    }
                    sendMessage(sb.toString());
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(52))) {    // Редактировать отчет
                    sendMessage(75, chatId, bot);   // Напишите отчет
                    waitingType = WaitingType.REPORT;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendStockList();
                }
                return false;


            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(65))) {
                    command = new StartStockAutoAdminCommand(stock);
                } else {
                    command = new StartStockManualAdminCommand(stock);
                }
                if (command.execute(update, bot)) {
                    command = null;
                    sendStockInfo();
                }
                return false;

            case TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendStockList();
                    return false;
                }
                stock.setReport(updateMessageText);
                stock.setAddedBy(userDao.getUserByChatId(chatId));
                stockDao.updateStock(stock);
                familiesDao.downloadFamiliesToGoogle(stock.getId());
                sendMessage(40, chatId, bot);   // Готово
//                sendSurvey();   // Опрос для пользователей
                sendMessage(53, chatId, bot);   // Меню акции
                waitingType = WaitingType.CHOOSE_STOCK_TYPE;
                return false;

            case REPORT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendStockList();
                }
                stock.setReport(updateMessageText);
                stockDao.updateStock(stock);
                sendMessage(stock.parseStockForMessage(), chatId, bot);
                return false;
        }
        return false;
    }

    private void sendStockInfo() throws TelegramApiException, SQLException {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(stock.parseStockForMessage())
                .setParseMode(ParseMode.HTML);
        if (stock.getStatus() == 4) {
            message = message.setReplyMarkup(keyboardMarkUpDao.select(15));
        } else {
            message = message.setReplyMarkup(keyboardMarkUpDao.select(16));
        }
        bot.sendMessage(message);
        waitingType = WaitingType.COMMAND;
    }

    private void sendStockList() throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        for (Stock stock : stocks) {
            sb.append("/id").append(stock.getId()).append(" - ").append(stock.getTitleForAdmin()).append("\n");
        }
        bot.sendMessage(new SendMessage()
                .setText(sb.toString())
                .setChatId(chatId)
                .setReplyMarkup(keyboardMarkUpDao.select(10)));
        waitingType = WaitingType.CHOOSE_STOCK;
    }

    private void sendSurvey() throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        sb.append(stock.getTitle()).append("\n").append(messageDao.getMessageText(80));
        for (User user : userDao.getUsers()) {
            try {
                bot.sendMessage(new SendMessage()
                        .setChatId(user.getChatId())
                        .setText(sb.toString())
                        .setReplyMarkup(getKeyboard()));
            }catch (TelegramApiException ex){
                ex.printStackTrace();
            }
        }
    }

    private ReplyKeyboard getKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(80));    // Пройти опрос
        button.setCallbackData("id=" + stock.getId() + " cmd=" + buttonDao.getButtonText(80));  // Пройти опрос
        row.add(button);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private void endStock() throws SQLException, TelegramApiException {
        stock.setStatus(4);
        sendMessage(75, chatId, bot);   // Напишите отчет
        waitingType = WaitingType.TEXT;
    }
}
