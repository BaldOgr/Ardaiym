package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by daniyar on 07.07.17.
 */
public class AdminControlCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!userDao.isSuperAdmin(chatId)){
            sendMessage(6, chatId, bot);
            return true;
        }

        if (waitingType == null) {
            sendMessage(73, chatId, bot);   // Меню управления админами
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(7, chatId, bot);
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(69))) {    // Добавить админа
                    sendMessage(74, chatId, bot);   // Выберите пользователя
                    StringBuilder sb = new StringBuilder();
                    for (User user : userDao.getUsers()) {
                        sb.append(user);
                    }
                    sendMessage(sb.toString());
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(70))) {    // Удалить админа
                    sendMessage(74, chatId, bot);   // Выберите пользователя
                    StringBuilder sb = new StringBuilder();
                    for (User user : userDao.getUsers()) {
                        sb.append(user);
                    }
                    sendMessage(sb.toString());
                    waitingType = WaitingType.CHOOSE_ADMIN;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(71))) {    // Отдать права суперадмина
                    sendMessage(74, chatId, bot);   // Выберите пользователя
                    StringBuilder sb = new StringBuilder();
                    for (User user : userDao.getUsers()) {
                        sb.append(user);
                    }
                    sendMessage(sb.toString());
                    waitingType = WaitingType.CHOOSE_SUPER_ADMIN;
                    return false;
                }

                return false;
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(73, chatId, bot);   // Меню управления админами
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                int userId = Integer.parseInt(updateMessageText.substring(3));
                User user = userDao.getUserById(userId);
                user.setRules(2);
                userDao.updateUser(user);
                sendMessage("Done");
                return false;

            case CHOOSE_ADMIN:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(73, chatId, bot);   // Меню управления админами
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                userId = Integer.parseInt(updateMessageText.substring(3));
                user = userDao.getUserById(userId);
                user.setRules(1);
                userDao.updateUser(user);
                sendMessage("Done");
                return false;

            case CHOOSE_SUPER_ADMIN:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(73, chatId, bot);   // Меню управления админами
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                userId = Integer.parseInt(updateMessageText.substring(3));
                user = userDao.getUserById(userId);
                user.setRules(3);
                userDao.updateUser(user);
                user = userDao.getUserByChatId(chatId);
                user.setRules(1);
                userDao.updateUser(user);
                sendMessage("Done");
                return false;


        }
        return false;
    }
}
