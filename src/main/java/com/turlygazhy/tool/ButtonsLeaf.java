package com.turlygazhy.tool;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class ButtonsLeaf {
    private List<String> allNamesButtonList;
    private int countButtons = 5;
    private int indexCurrentButton;
    private String left = "<<";
    private String right = ">>";
    private int page = 1;
    /**
     * Указываем список с именами, кол-во кнопок, названия кнопок листалок
     * */
    public ButtonsLeaf(List<String> allNamesButtonList, int countButtons, String left, String right) {
        this.allNamesButtonList = allNamesButtonList;
        this.countButtons = countButtons;
        this.left = left;
        this.right = right;
    }
    /**
     * проверяем запрашивали ли следующие страницы.
     * если да снова берем клавиатуру. Она уже будет обновлена.
     *
     * пример:
     * case CHOOSE_EMPLOYEE:
     *
     *      //отправка в первый раз
     *      sendMessageWithKeyboard(15, butLeaf.getListButton)
     *      waitType = WaitType.EDIT_EMPLOYEE
     *      return COMEBACK;
     *
     * case EDIT_EMPLOYEE:
     *      //проверка на использование листалок
     *      if(butLeaf.isNext(updateMessageText)){
     *
     *          //отправляем клавиаутру снова - она уже обновлена в методе isNext.
     *          sendMessageWithKeyboard(15, butLeaf.getListButton);
     *
     *          //возврат без сметы вайт тайп
     *          return COMEBACK;
     *      }
     *      //получение выбранной кнопки
     *      listEmployees.get(Integer.parseInt(updateMessageText));
     * */
    public boolean isNext(String updateMessageText){
        if (updateMessageText.equals(left)) {
            page--;
            if (page < 1) page = countPage();
            return true;
        } else if (updateMessageText.equals(right)) {
            page++;
            if (page > countPage()) page = 1;
            return true;
        }
        return false;
    }
    /**
     * для отображения кол-ва страниц
     * */
    public int countPage() {
        int result = allNamesButtonList.size() / countButtons;
        if (allNamesButtonList.size() % countButtons != 0) result++;
        return result;
    }
    /**
     *  выдает клавиатуру с текущими кнопками из списка переданного в конструктор.
     * */
    public InlineKeyboardMarkup getListButton() throws TelegramApiException {
        indexCurrentButton = (page - 1) * countButtons;
        List<String> currentButtonNames = new ArrayList<>();
        List<String> callbackDataButtons = new ArrayList<>();
        try {
            for (int i = 0; i < countButtons; i++) {
                currentButtonNames.add(allNamesButtonList.get(indexCurrentButton));
                callbackDataButtons.add(String.valueOf(indexCurrentButton));
                indexCurrentButton++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (countButtons >= allNamesButtonList.size())
            return getInlineKeyboard(currentButtonNames, callbackDataButtons);
        return addButton(getInlineKeyboard(currentButtonNames, callbackDataButtons));
    }

    public int getCurrentPage(){
        return page;
    }

    private InlineKeyboardMarkup addButton(InlineKeyboardMarkup inlineKeyboardMarkup) {
        List<InlineKeyboardButton> rowButton = new ArrayList<>();
        InlineKeyboardButton leftBtn = new InlineKeyboardButton();
        leftBtn.setText(left);
        leftBtn.setCallbackData(left);
        InlineKeyboardButton rightBtn = new InlineKeyboardButton();
        rightBtn.setText(right);
        rightBtn.setCallbackData(right);
        rowButton.add(leftBtn);
        rowButton.add(rightBtn);
        inlineKeyboardMarkup.getKeyboard().add(rowButton);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboard(List<String> namesButton, List<String> callbackMessage) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsKeyboard = new ArrayList<>();
        String buttonIdsString;
        for (int i = 0; i < namesButton.size(); i++) {
            buttonIdsString = namesButton.get(i);
            List<InlineKeyboardButton> rowButton = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonIdsString);
            button.setCallbackData(callbackMessage.get(i));
            rowButton.add(button);
            rowsKeyboard.add(rowButton);
        }
        keyboard.setKeyboard(rowsKeyboard);
        return keyboard;
    }
}