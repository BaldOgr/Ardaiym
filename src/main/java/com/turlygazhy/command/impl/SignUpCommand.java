package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Answer;
import com.turlygazhy.entity.Question;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by daniyar on 29.06.17.
 */
public class SignUpCommand extends Command {
    User user;
    List<Question> questions;
    Question question;
    Answer answer;
    Iterator iterator;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            sendMessage(11, chatId, bot);   // Введите ваше ФИО
            user = new User();
            user.setChatId(chatId);
            waitingType = WaitingType.NAME;
            return false;
        }

        switch (waitingType) {
            case NAME:
                user.setName(updateMessageText);
                sendMessage(12, chatId, bot);   // Введите вашу дату рождения
                waitingType = WaitingType.BIRTHDAY;
                return false;

            case BIRTHDAY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(11, chatId, bot);   // Введите ваше ФИО
                    waitingType = WaitingType.NAME;
                    return false;
                }
                user.setBirthday(updateMessageText);
                sendMessage(10, chatId, bot);   // Ваш пол
                waitingType = WaitingType.SEX;
                return false;

            case SEX:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(12, chatId, bot);   // Введите вашу дату рождения
                    waitingType = WaitingType.BIRTHDAY;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(11))) {    // Мужчина
                    user.setSex(true);
                } else {
                    user.setSex(false);
                }
                sendMessage(14, chatId, bot);   // Введите свой номер телефона
                waitingType = WaitingType.PHONE_NUMBER;
                return false;

            case PHONE_NUMBER:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(10, chatId, bot);   // Ваш пол
                    waitingType = WaitingType.SEX;
                    return false;
                }
                if (updateMessage.getContact() != null) {
                    user.setPhoneNumber(updateMessage.getContact().getPhoneNumber());
                } else {
                    user.setPhoneNumber(updateMessageText);
                }
                sendMessage(13, chatId, bot);
                waitingType = WaitingType.CITY;
                return false;

            case CITY:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(14, chatId, bot);   // Введите свой номер телефона
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                }
                user.setCity(updateMessageText);
                userDao.insertUser(user);
                questions = questionDao.getQuestions(false);
                if (questions.size() != 0){
                    iterator = questions.iterator();
                    question = (Question) iterator.next();
                    sendMessage(question.getText());
                    waitingType = WaitingType.ANSWER;
                    return false;
                }
                sendMessage(15, chatId, bot);       // Готово! Чтобы войти в главное меню, напишите /start
                return true;

            case ANSWER:
                answer = new Answer();
                answer.setQuestion(question);
                answer.setUserId(chatId);
                switch (question.getType()){
                    case 0: // Ответ - текст
                        answer.setText(updateMessageText);
                        break;
                    case 1: // Ответ - фото
                        if (updateMessage.getPhoto() != null) {
                            answer.setPhoto(updateMessage.getPhoto().get(0).getFileId());
                        } else {
                            sendMessage(36, chatId, bot );  // Отправьте фото
                            return false;
                        }
                        break;
                    case 2: // Ответ - аудио
                        if (updateMessage.getAudio() != null) {
                            answer.setAudio(updateMessage.getAudio().getFileId());
                        } else {
                            sendMessage(37, chatId, bot);   // Запишите аудио
                            return false;
                        }
                        break;
                    case 3: // Ответ - контакт
                        Contact contact = updateMessage.getContact();
                        if (contact != null){
                            answer.setContactUserId(Long.valueOf(contact.getUserID()));
                            answer.setContactPhoneNumber(contact.getPhoneNumber());
                            answer.setContactFirstName(contact.getFirstName());
                            answer.setContactSecondName(contact.getLastName());
                        } else {
                            answer.setContactPhoneNumber(updateMessageText);
                        }
                        break;
                }
                answerDao.insertAnswer(answer);
                if (iterator.hasNext()){
                    question = (Question) iterator.next();
                    sendMessage(question.getText());
                    return false;
                } else {
                    user = new User();
                    user.setChatId(chatId);
                    userDao.insertUser(user);
                    sendMessage(15, chatId, bot);   // Готово! Вопросов больше нет
                    return true;
                }

        }

        return false;
    }
}
