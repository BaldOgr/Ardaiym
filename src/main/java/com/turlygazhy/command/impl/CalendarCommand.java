package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 04.07.17.
 */
public class CalendarCommand extends Command {
    List<Stock> stocks;
    Stock stock;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage(68, chatId, bot);   // Каледарь событий
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(2, chatId, bot);    // Главное меню
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(62))) {
                    stocks = stockDao.getDoneStockList();
                }
                if (updateMessageText.equals(buttonDao.getButtonText(63))) {
                    stocks = stockDao.getUndoneStockList();
                }
                if (stocks.size() == 0) {
                    sendMessage(69, chatId, bot);  // Нечего показывать
                    return false;
                }
                StringBuilder sb = new StringBuilder();
                for (Stock stock : stocks) {
                    sb.append(stock.toString()).append("\n");
                }
                sendMessage(sb.toString());
                waitingType = WaitingType.CHOOSE_STOCK;
                return false;

            case CHOOSE_STOCK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(68, chatId, bot);   // Каледарь событий
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(62))) {
                    stocks = stockDao.getDoneStockList();
                    showStockList();
                }
                if (updateMessageText.equals(buttonDao.getButtonText(63))) {
                    stocks = stockDao.getUndoneStockList();
                    showStockList();
                }
                int stockId = Integer.parseInt(updateMessageText.substring(3));
                stock = stockDao.getStock(stockId);
                String message = "<b>" + stock.getTitle() + "</b>" + "\n\n"
                        + stock.getDescription() + "\n\n";
                if (stock.getStatus() == 4){
                    message = message.concat(stock.getReport());
                }
                bot.sendMessage(new SendMessage()
                        .setText(message)
                        .setChatId(chatId)
                        .setParseMode(ParseMode.HTML));
                return false;
        }
        return false;
    }

    private void showStockList() throws SQLException, TelegramApiException {
        if (stocks.size() == 0) {
            sendMessage(69, chatId, bot);  // Нечего показывать
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Stock stock : stocks) {
            sb.append(stock.toString()).append("\n");
        }
        sendMessage(sb.toString());
        waitingType = WaitingType.CHOOSE_STOCK;
    }
}
