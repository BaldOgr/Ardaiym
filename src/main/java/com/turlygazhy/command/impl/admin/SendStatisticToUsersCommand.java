package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Dates;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.Task;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

public class SendStatisticToUsersCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!userDao.isAdmin(chatId)){
            return true;
        }
        String data = update.getCallbackQuery().getData();
        int stockId = Integer.parseInt(data.substring(3, data.indexOf(" ")));
        Stock stock = stockDao.getStock(stockId);

        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(messageDao.getMessageText(47)).append(": </b>").append(stock.getTitle()).append("\n");
        for (Task task : stock.getTaskList()) {
            sb.append("\t<b>").append(task.getName()).append(":</b>\n");
            for (Dates dates : task.getDates()) {
                sb.append("\t\t<b>").append(dates.getDate()).append(":</b>\n");
                task.getParticipants().forEach(obj -> {
                    if (dates.getDate().equals(obj.getDate().getDate()))
                        sb.append("\t\t\t").append(obj.getUser().getName()).append("\n");
                });
            }
        }
        List<User> users = userDao.getUsers();
        users.forEach(user -> {
            try {
                sendMessage(sb.toString(), user.getChatId(), bot);
            } catch (SQLException | TelegramApiException e) {
                e.printStackTrace();
            }
        });

        return false;
    }
}
