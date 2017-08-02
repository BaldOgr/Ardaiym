package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Participant;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.Task;
import com.turlygazhy.entity.User;
import com.turlygazhy.tool.SheetsAdapter;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SendUserStatisticToSheetsCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        sendMessage("Doing...");
        List<User> users = userDao.getUsers();
        List<Stock> stocks = stockDao.getStocks();
        List<List<Object>> writeData = new ArrayList<>();
        for (User user : users) {
            if (user == null) {
                continue;
            }
            int registeredInStockCount = 0;
            int participatedCount = 0;
            List<Object> userData = new ArrayList<>();
            List<String> registered = new ArrayList<>();
            List<String> participated = new ArrayList<>();

            userData.add(user.getId());
            userData.add(user.getName());
            userData.add(user.getPhoneNumber());
            userData.add(user.getCity());
            if (user.isSex()) {
                userData.add(buttonDao.getButtonText(11));  // Мужчина
            } else {
                userData.add(buttonDao.getButtonText(12));  // Женщина
            }
            userData.add(user.getBirthday());
            List<User> userFriends = friendsDao.getFriends(user.getChatId());
            userData.add(messageDao.getMessageText(145));   // Одобрили
            for (User user1 : userFriends) {
                userData.add(user1.getName());
            }
            for (Stock stock : stocks) {
                for (Task task : stock.getTaskList()) {                     //Добавляем акции, в которых участвовал волонтер
                    if (addParticipant(task, user)) {
                        registered.add(stock.getTitleForAdmin());
                        registeredInStockCount++;
                        break;
                    }
                }
                List<Integer> groups = familiesDao.getGroupsByStockId(stock.getId());
                for (Integer groupId : groups) {
                    List<User> groupUsers = familiesDao.getUsersByGroupId(groupId, stock.getId());
                    if (participated(groupUsers, user)) {
                        participated.add(stock.getTitleForAdmin());
                        participatedCount++;
                        break;
                    }
                }
            }

            userData.add(registeredInStockCount + "/" + participatedCount);
            for (String str : registered) {
                userData.add(str);
                userData.add(messageDao.getMessageText(134));
                userData.add(str);
                if (wasParticipated(str, participated)) {
                    userData.add(messageDao.getMessageText(135));
                } else {
                    userData.add(messageDao.getMessageText(136));
                }
            }
            writeData.add(userData);
        }
        try {
            SheetsAdapter.writeDataToUsersSheet(writeData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMessage(40, chatId, bot);   // Готово
        return true;
    }

    private boolean wasParticipated(String str, List<String> participated) {
        for (String part : participated) {
            if (part.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean participated(List<User> groupUsers, User user) {
        for (User user1 : groupUsers) {
            if (user1 == null) {
                continue;
            }
            if (user1.getChatId().equals(user.getChatId())) {
                return true;
            }
        }
        return false;
    }

    private boolean addParticipant(Task task, User user) {
        for (Participant participant : task.getParticipants()) {
            if (participant.getUser().getChatId().equals(user.getChatId())) {
                return true;
            }
        }
        return false;
    }
}
