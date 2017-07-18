package com.turlygazhy.command.impl.admin_commands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Question;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 12.07.17.
 */
public class EditQuestionsCommand extends Command {
    Question question;
    private List<Question> questions;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null){
            if (!userDao.isAdmin(chatId)){
                sendMessage("Nothing to show");
                return true;
            }
            sendMessage(26, chatId, bot);   // Меню вопросов
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType){
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(27))) {    // Редактировать вопросы
                    questions = questionDao.getQuestions();
                    if (questions.size() == 0){
                        sendMessage("Nothing to show. Add questions");
                        return false;
                    }
                    sendQuestionList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(28))) {    // Добавить новый вопрос
                    sendMessage(29, chatId, bot);   // Введите текст вопроса
                    waitingType = WaitingType.NEW_QUESTION_TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(3, chatId, bot);    // Меню админа
                    return true;
                }
                return false;

///////////////////////////  Создаем новый вопрос  /////////////////////////////////////////

            case NEW_QUESTION_TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(26, chatId, bot);   // Меню вопросов
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                question = new Question();
                question.setText(updateMessageText);
                sendMessage(30, chatId, bot);   // Выберите тип ответа
                waitingType = WaitingType.NEW_QUESTION_TYPE;
                return false;

            case NEW_QUESTION_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(29, chatId, bot);   // Введите текст вопроса
                    waitingType = WaitingType.NEW_QUESTION_TEXT;
                    return false;
                }
                int type = 0;
                if (updateMessageText.equals(buttonDao.getButtonText(32))) {    // Текст
                    type = 0;   // Тип ответа - текст
                } else if (updateMessageText.equals(buttonDao.getButtonText(33))) { // Фото
                    type = 1;
                } else if (updateMessageText.equals(buttonDao.getButtonText(33))) { // Аудио
                    type = 2;
                } else if (updateMessageText.equals(buttonDao.getButtonText(33))) { // Контакт
                    type = 3;
                }
                question.setType(type);
                questionDao.insertQuestion(question);
                sendMessage(34, chatId, bot);   // Вопрос успешно создан!
                waitingType = WaitingType.COMMAND;
                return false;

            case CHOOSE_QUESTION:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(26, chatId, bot);   // Меню вопросов
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                int questionId = Integer.parseInt(updateMessageText.substring(3));
                question = questionDao.getQuestion(questionId);
                sendQuestion();
                return false;

///////////////////////////  Редактируем вопрос  /////////////////////////////////////////

            case CHOOSE_PARAMETR:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendQuestionList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(29))) {    // Текст вопроса
                    sendMessage(32, chatId, bot);   // Введите текст вопроса
                    waitingType = WaitingType.QUESTION_TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(30))) {    // Тип ответа
                    sendMessage(30, chatId, bot);
                    waitingType = WaitingType.QUESTION_TYPE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(31))) {    // Показывать вопрос?
                    question.setShow(!question.isShow());
                    questionDao.updateQuestion(question);
                    if (question.isShow()){
                        sendMessage(31, chatId, bot);   // Вопрос теперь показывается!
                    } else {
                        sendMessage(32, chatId, bot);   // Вопрос больше не показывается
                    }
                }
                return false;

            case QUESTION_TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendQuestion();
                    return false;
                }
                question.setText(updateMessageText);
                questionDao.updateQuestion(question);
                sendMessage(33, chatId, bot);   // Вопрос успешно обновлен!
                sendQuestion();
                return false;

            case QUESTION_TYPE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendQuestion();
                    return false;
                }
                type = question.getType();
                if (updateMessageText.equals(buttonDao.getButtonText(32))) {    // Текст
                    type = 0;   // Тип ответа - текст
                } else if (updateMessageText.equals(buttonDao.getButtonText(33))) { // Фото
                    type = 1;
                } else if (updateMessageText.equals(buttonDao.getButtonText(34))) { // Аудио
                    type = 2;
                } else if (updateMessageText.equals(buttonDao.getButtonText(35))) { // Контакт
                    type = 3;
                }
                question.setType(type);
                questionDao.updateQuestion(question);
                sendMessage(33, chatId, bot);   // Вопрос успешно обновлен!
                sendQuestion();
                return false;

        }
        return false;
    }

    private void sendQuestion() throws SQLException, TelegramApiException {
        sendMessage(question.toString());
        sendMessage(28, chatId, bot);    // Что будем менять?
        waitingType = WaitingType.CHOOSE_PARAMETR;
    }

    private void sendQuestionList() throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        for (Question question : questions){
            sb.append("/id").append(question.getId()).append(" - ").append(question.getText()).append("\n");
        }
        sendMessage(27, chatId, bot);   //Выберите вопрос
        sendMessage(sb.toString());
        waitingType = WaitingType.CHOOSE_QUESTION;
    }
}
