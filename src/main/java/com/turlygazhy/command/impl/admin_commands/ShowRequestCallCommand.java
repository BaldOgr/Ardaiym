package com.turlygazhy.command.impl.admin_commands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.RequestCall;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 13.07.17.
 * Для корректной работы нужно занести в базу данных следующее:
 * В таблицу MESSAGE:
 * - Меню админа
 * - Звонок совершен
 * В таблицу BUTTON:
 * - Назад
 * - Звонок совершен
 */
public class ShowRequestCallCommand extends Command {
    RequestCall requestCall;
    List<RequestCall> calls;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendCalls();
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(3, chatId, bot);    // Админ меню
                    return true;
                }
                int callId = Integer.parseInt(updateMessageText.substring(3));
                requestCall = requestCallDao.getRequestCall(callId);
                bot.sendMessage(new SendMessage()
                        .setText(requestCall.toString())
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(12))); // Назад, Звонок завершен
                waitingType = WaitingType.COMMAND;
                return false;

            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendCalls();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(12))) {    // Звонок завершен
                    requestCall.setCalled(true);
                    requestCallDao.update(requestCall);
                    sendMessage(44, chatId, bot);   // Звонок совершен
                    sendCalls();
                    return false;
                }
//                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
//
//                }
                return false;
        }
        return false;
    }

    private void sendCalls() throws SQLException, TelegramApiException {
        if (calls == null) {
            calls = requestCallDao.getRequestCallList(false);
        }
        if (calls.size() == 0) {
            sendMessage("Nothing to show");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (RequestCall call : calls) {
            sb.append("/id").append(call.getId()).append(" - ").append(call.getName()).append("\n")
                    .append(call.getText()).append("\n")
                    .append(call.getPhoneNumber()).append("\n");
        }
        sendMessage(sb.toString());
        waitingType = WaitingType.CHOOSE;
    }
}
