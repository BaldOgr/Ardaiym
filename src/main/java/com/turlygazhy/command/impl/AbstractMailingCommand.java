package com.turlygazhy.command.impl;

import Constructors.InlineKeyboardConstructor;
import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Eshu on 13.07.2017.
 */
public abstract class AbstractMailingCommand extends Command {
    private int     step = 0;
    private String  text;
    private String  filterCriteria;
    private boolean expectEnter;
    private boolean mailingToAll;
    private long    chatId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectEnter){
            switch (step){
                case 0:
                    try {
                        text = update.getMessage().getText();
                        step = 1;
                    } catch (Exception e){
                        bot.sendMessage(new SendMessage().setChatId(chatId)
                                .setText("Ошибка, вы отправили не текст\n попробуйте снова"));
                        return false;
                    }
                    break;
                case 1:
                    try {
                        switch (update.getCallbackQuery().getData()){
                            case "mailingToAll":
                                mailingToAll = true;
                                step = 3;
                                break;
                            case "mailingToFilter":
                                mailingToAll = false;
                                step = 2;
                        }
                    } catch (Exception e) {
                        bot.sendMessage(new SendMessage().setChatId(chatId).setText("Вам нужно нажать на кнопку, а не писать"));
                        return false;
                    }
                    break;
                case 2:
                    try{
                        filterCriteria = update.getMessage().getText();
                        step = 3;
                    } catch (Exception e){
                        bot.sendMessage(new SendMessage().setChatId(chatId)
                                .setText("\"Ошибка, вы отправили не текст\\n попробуйте снова"));
                        return false;
                    }
            }
        }
        if(step == 0){
            org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
            if (updateMessage == null) {
                updateMessage = update.getCallbackQuery().getMessage();
            }
            chatId = updateMessage.getChatId();
            bot.sendMessage(new SendMessage().setChatId(chatId).setText(getFirstQuestionText()));
            expectEnter = true;
            return false;
        }
        if(step == 1){
            bot.sendMessage(new SendMessage().setChatId(chatId).setText(getTextAboutSendToAllOrFilter())
                    .setReplyMarkup(getKeysForTargetMailingChose()));
            return false;
        }
        if(step == 2){
            bot.sendMessage(new SendMessage().setChatId(chatId).setText(getTextForFilterEnter()));
            return false;
        }
        if(step == 3){
            String mailingText = mailingTextBuilder(text);
            if(mailingToAll){
                for (String chatId : getAllChatIds()){
                    bot.sendMessage(new SendMessage().setChatId(chatId)
                            .setText(mailingText));
                }
            } else {
                for (String chatId : getFilteredChatIds(filterCriteria)){
                    bot.sendMessage(new SendMessage().setChatId(chatId)
                            .setText(mailingText));
                }
            }
            bot.sendMessage(new SendMessage().setChatId(chatId)
                    .setText(getTextAboutSuccess()));
        }
        step           = 0;
        text           = null;
        filterCriteria = null;
        expectEnter    = false;
        mailingToAll   = false;
        chatId         = 0;
        return true;
    }

    /**
     * Place text like - do you want send that mailing to all or to chosen members?
     */
    abstract String getTextAboutSendToAllOrFilter();

    /**
     * Place text like - enter your text for mailing
     */
    abstract String getFirstQuestionText();

    /**
     * Place text like - no, send that mailing to all members
     */
    abstract String getTextForButtonAll();

    /**
     * Place text like - yes, send mailing to chosen members
     */
    abstract String getTextForButtonFilter();

    /**
     * Place text like - enter your criteria
     */
    abstract String getTextForFilterEnter();

    /**
     * Place text like - Success!Mailing is done!
     */
    abstract String getTextAboutSuccess();

    abstract String mailingTextBuilder(String text);

    abstract String[] getAllChatIds();

    abstract String[] getFilteredChatIds(String filterCriteria);

    private InlineKeyboardMarkup getKeysForTargetMailingChose(){
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        buttonText.add(getTextForButtonAll());
        buttonData.add("mailingToAll");
        buttonText.add(getTextForButtonFilter());
        buttonData.add("mailingToFilter");
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData);
    }

}