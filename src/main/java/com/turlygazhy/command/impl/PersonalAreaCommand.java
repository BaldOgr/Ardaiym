package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Participant;
import com.turlygazhy.entity.Report;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 29.06.17.
 */
public class PersonalAreaCommand extends Command {
    private List<Participant> participants;
    private Participant participant;
    private Report report;
    private User user;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage(20, chatId, bot);   // Личный кабинет
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(2, chatId, bot);
                    return true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(21))) {    // Мои задания
                    participants = participantOfStockDao.getParticipantOfStock(chatId);
                    if (participants.size() == 0) {
                        sendMessage(21, chatId, bot);   // Задании нет
                        return false;
                    }
                    sendMessage(22, chatId, bot);   // Выберите тип задания
                    waitingType = WaitingType.CHOOSE_TASK_TYPE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(22))) {    // Редактировать профиль
                    sendMessage(159, chatId, bot);  // Что будем редактировать?
                    user = userDao.getUserByChatId(chatId);
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(20, chatId, bot);   // Личный кабинет
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(76))) {    // Фио
                    sendMessage(11, chatId, bot);   // Введите Вашу фамилию на кирилице
                    waitingType = WaitingType.SECOND_NAME;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(77))) {    // Номер телефона
                    sendMessage(14, chatId, bot);   // Введите новый номер телефона
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(78))) {    // Город
                    sendMessage(13, chatId, bot);   // Введите новый город
                    waitingType = WaitingType.CITY;
                    return false;
                }
                return false;

            case SECOND_NAME:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(159, chatId, bot);  // Что будем редактировать?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                user.setName(updateMessageText);
                sendMessage(16, chatId, bot);   // Введите имя на кириллице
                waitingType = WaitingType.NAME;
                return false;

            case NAME:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(159, chatId, bot);  // Что будем редактировать?
                    waitingType = WaitingType.CHOOSE;
                    user = userDao.getUserByChatId(chatId);
                    return false;
                }
                user.setName(user.getName() + " " + updateMessageText);
                userDao.updateUser(user);
                sendMessage(25, chatId, bot);   // Данные успешно обновлены
                sendMessage(20, chatId, bot);   // Личный кабинет
                waitingType = WaitingType.COMMAND;
                return false;


            case PHONE_NUMBER:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(159, chatId, bot);  // Что будем редактировать?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessage.getContact() != null) {
                    user.setPhoneNumber(updateMessage.getContact().getPhoneNumber());
                } else {
                    user.setPhoneNumber(updateMessageText);
                }
                userDao.updateUser(user);
                sendMessage(25, chatId, bot);   // Данные успешно обновлены
                sendMessage(20, chatId, bot);   // Личный кабинет
                waitingType = WaitingType.COMMAND;
                return false;

            case CITY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(159, chatId, bot);  // Что будем редактировать?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(96))) {
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(13)) // Где вы проживаете?
                            .setReplyMarkup((InlineKeyboardMarkup) keyboardMarkUpDao.select(38)));
                    return false;
                }
                user.setCity(updateMessageText);
                userDao.updateUser(user);
                sendMessage(25, chatId, bot);   // Данные успешно обновлены
                sendMessage(20, chatId, bot);   // Личный кабинет
                waitingType = WaitingType.COMMAND;
                return false;

            case CHOOSE_TASK_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(20, chatId, bot);
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(23))) {    // Невыполненные задания
                    sendTasks(false);
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(24))) {    // Выполненные задания
                    sendTasks(true);
                    return false;
                }

            case CHOOSE_TASK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(22, chatId, bot);   // Выберите тип задания
                    waitingType = WaitingType.CHOOSE_TASK_TYPE;
                    return false;
                }
                int participantId = Integer.parseInt(updateMessageText.substring(3));
                participant = participantOfStockDao.getParticipantById(participantId);
                SendMessage message = new SendMessage()
                        .setText(participant.toString())
                        .setChatId(chatId)
                        .setParseMode(ParseMode.HTML);
                if (participant.isFinished()) {
                    message = message.setReplyMarkup(keyboardMarkUpDao.select(11));
                } else {
                    message = message.setReplyMarkup(keyboardMarkUpDao.select(6));
                }
                bot.sendMessage(message);
                waitingType = WaitingType.TASK_COMMAND;
                return false;

            case TASK_COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    StringBuilder sb = new StringBuilder();
                    for (Participant pr : participants) {
                        sb.append("/id").append(pr.getId()).append(" - ").append(taskDao.getTypeOfWork(pr.getTypeOfWorkId()).getName()).append("\n");
                    }
                    bot.sendMessage(new SendMessage()
                            .setText(sb.toString())
                            .setChatId(chatId)
                            .setReplyMarkup(keyboardMarkUpDao.select(10)));
                    waitingType = WaitingType.CHOOSE_TASK;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(25))) {    // Выполнено
                    participant.setFinished(true);
                    participantOfStockDao.update(participant);
                    sendMessage(40, chatId, bot);   // Готово
                }
                if (updateMessageText.equals(buttonDao.getButtonText(26))) {    // Добавить отчет
                    sendMessage(23, chatId, bot);
                    waitingType = WaitingType.TEXT;
                }
                return false;

            case TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {   // Назад
                    message = new SendMessage()
                            .setText(participant.toString())
                            .setChatId(chatId);
                    if (participant.isFinished()) {
                        message = message.setReplyMarkup(keyboardMarkUpDao.select(10));
                    } else {
                        message = message.setReplyMarkup(keyboardMarkUpDao.select(6));
                    }
                    bot.sendMessage(message);
                    waitingType = WaitingType.TASK_COMMAND;
                }
                report = new Report();
                report.setParticipantId(participant.getId());
                report.setText(updateMessageText);
                reportDao.insertReport(report);
                sendMessage(40, chatId, bot);   // Готово
                return false;
        }

        return false;
    }

    private void sendTasks(boolean finished) throws SQLException, TelegramApiException {
        participants = participantOfStockDao.getParticipantOfStock(chatId, finished);
        if (participants.size() == 0) {
            sendMessage(21, chatId, bot);   // Задании нет
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Participant pr : participants) {
            sb.append("/id").append(pr.getId()).append(" - ").append(taskDao.getTypeOfWork(pr.getTypeOfWorkId()).getName()).append("\n");
        }
        bot.sendMessage(new SendMessage()
                .setText(sb.toString())
                .setChatId(chatId)
                .setReplyMarkup(keyboardMarkUpDao.select(10)));
        waitingType = WaitingType.CHOOSE_TASK;
    }
}
