package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 30.06.17.
 */
public class ShowStockCommand extends Command {
    List<Stock> stocks;
    Stock stock;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage(53, chatId, bot);   // Меню акции
            waitingType = WaitingType.CHOOSE_STOCK_TYPE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE_STOCK_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(50))) {    // Законченные акции
                    stocks = stockDao.getDoneStockList();
                }
                if (updateMessageText.equals(buttonDao.getButtonText(51))) {    // Незаконченные акции
                    stocks = stockDao.getUndoneStockList();
                }
                StringBuilder sb = new StringBuilder();
                for (Stock stock : stocks) {
                    sb.append("/id").append(stock.getId()).append(" - ").append(stock.getTitleForAdmin()).append("\n");
                }
                sendMessage(sb.toString(), chatId, bot);
                waitingType = WaitingType.CHOOSE_STOCK;
                return false;

            case CHOOSE_STOCK:
                int stockId = Integer.parseInt(updateMessageText.substring(3));
                stock = stockDao.getStock(stockId);
                sb = new StringBuilder();
                sb.append("<b>Stock: </b>").append(stock.getTitle()).append("\n")
                        .append("<b>Description: </b>").append(stock.getDescription());
                SendMessage message = new SendMessage().setChatId(chatId).setText(sb.toString()).setParseMode(ParseMode.HTML);
                if (stock.isFinished()) {
                    message = message.setReplyMarkup(keyboardMarkUpDao.select(15));
                } else {
                    message = message.setReplyMarkup(keyboardMarkUpDao.select(16));
                }
                bot.sendMessage(message);
                waitingType = WaitingType.COMMAND;
                return false;

            case COMMAND:

                return false;
        }
        return false;
    }
}
