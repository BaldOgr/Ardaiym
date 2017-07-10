package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Car;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.VolunteersGroup;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 03.07.17.
 */
public class RegistrationInStockCommand extends Command {
    int stockId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            stockId = Integer.parseInt(updateMessageText.substring(3, updateMessageText.indexOf(" ")));
            sendMessage(59, chatId, bot);   // Вы на машине?
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(56))) { // Да
                    sendMessage(60, chatId, bot);   // Введите номер и марку машины
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                sendMessage(61, chatId, bot);   // Ожидайте...
                return true;
            case TEXT:
                Car car = new Car();
                car.setName(updateMessageText);
                car.setUserId(chatId);
                car.setStockId(stockId);
                carDao.insertCar(car);
                volunteersGroupDao.insertVolunteer(userDao.getUserByChatId(chatId), car.getId(), stockId);
                sendMessage(61, chatId, bot);
                return true;
        }
        return false;
    }
}
