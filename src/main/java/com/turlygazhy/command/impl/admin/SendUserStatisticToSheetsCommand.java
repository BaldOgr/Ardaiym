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
        for (User user : users){
            List<Object> userData = new ArrayList<>();
            userData.add(user.getId());
            userData.add(user.getName());
            userData.add(user.getPhoneNumber());
            userData.add(user.getCity());
            if (user.isSex()){
                userData.add(buttonDao.getButtonText(11));  // Мужчина
            } else {
                userData.add(buttonDao.getButtonText(12));  // Женщина
            }
            userData.add(user.getBirthday());
            StringBuilder regInStock = new StringBuilder();
            StringBuilder participated = new StringBuilder();
            for (Stock stock : stocks){                                 /////
                for (Task task: stock.getTaskList()){                   //Добавляем акции,
                    if (addParticipant(task, user)) {                   //в которых участвовал
                        regInStock.append(stock.getTitle()).append("\n");       //
                        break;                                          //волонтер
                    }                                                   //////
                }
                List<Integer> groups = familiesDao.getGroupsByStockId(stock.getId());
                for (Integer groupId : groups){
                    List<User> groupUsers = familiesDao.getUsersByGroupId(groupId, stock.getId());
                    if (participated(groupUsers, user)){
                        participated.append(stock.getTitle()).append("\n");
                        break;
                    }
                }
            }
            userData.add(regInStock.toString());
            userData.add(participated.toString());
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

    private boolean participated(List<User> groupUsers, User user) {
        for (User user1 : groupUsers){
            if (user1.getChatId().equals(user.getChatId())){
                return true;
            }
        }
        return false;
    }

    private boolean addParticipant(Task task, User user) {
        for (Participant participant : task.getParticipants()){
            if (participant.getUser().getChatId().equals(user.getChatId())){
                return true;
            }
        }
        return false;
    }
}
