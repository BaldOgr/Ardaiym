package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by daniyar on 20.07.17.
 */
public class NewTextCommand extends Command {
    Stock stock = new Stock();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null){
            sendMessage(102, chatId, bot);  // Выберите тип
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(36))){ // NONE
                    sendMessage(103, chatId, bot);  // Введите название
                    waitingType = WaitingType.NAME;
                    return false;
                }
                return false;

            case NAME:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(102, chatId, bot); // Выберите тип
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                Date date = new Date();
                StringBuilder sb = new StringBuilder();
                sb.append(updateMessageText).append(" ");
                if (date.getDate() < 10){
                    sb.append("0");
                }
                        sb.append(date.getDate()).append(".");
                if (date.getMonth() < 11){
                    sb.append("0");
                }
                sb.append(date.getMonth() + 1).append(".")
                        .append(date.getYear() + 1900);
                stock.setTitle(sb.toString());
                stock.setCTA(false);
                sendMessage(32, chatId, bot);  // Введите описание
                waitingType = WaitingType.DESCRIPTION;
                return false;

            case DESCRIPTION:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(103, chatId, bot); // Выберите тип
                    waitingType = WaitingType.NAME;
                    return false;
                }
                stock.setDescription(updateMessageText);
                stockDao.insertStock(stock);
                sendMessage(39, chatId, bot);   // Готово
                sendMessage(stock.toString());
                return true;
        }

        return false;
    }
}
