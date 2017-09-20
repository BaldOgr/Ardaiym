package com.turlygazhy.command.impl.admin;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import com.turlygazhy.tool.SheetsAdapter;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SendUserStatisticToSheetsCommand extends Command {
    private List<User> users;
    private int userListPage = 0;
    private boolean newMessage = true;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingType == null) {
            bot.sendMessage(new SendMessage()
                    .setText(getStatistic())
                    .setChatId(chatId)
                    .setParseMode(ParseMode.HTML)
                    .setReplyMarkup(keyboardMarkUpDao.select(57)));
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                userListPage = 0;
                newMessage = true;
                if (updateMessageText.equals(buttonDao.getButtonText(143))) {   // Волонтеры
                    users = userDao.getUsers();
                    sendUserList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(144))) {   // Мужчины
                    users = userDao.getUsersBySex(true);
                    sendUserList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(145))) {   // Женщины
                    users = userDao.getUsersBySex(false);
                    sendUserList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(146))) {   // Из Караганды
                    users = new ArrayList<>();
                    for (int i = 103; i < 111; i++) {
                        users.addAll(userDao.getUsersByCity(buttonDao.getButtonText(i)));
                    }
                    sendUserList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(147))) {   // Не из Караганды
                    users = new ArrayList<>();
                    for (int i = 87; i < 103; i++) {
                        users.addAll(userDao.getUsersByCity(buttonDao.getButtonText(i)));
                    }
                    sendUserList();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(148))) {   // Акции
                    List<Stock> stocks = stockDao.getDoneStockList();
                    sendStockList(stocks);
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(149))) {   // Загрузить статистику в облако
                    sendStatisticToGoogleSheets();
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(7, chatId, bot);
                    return false;
                }
                return false;

            case CHOOSE_STOCK:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    bot.sendMessage(new SendMessage()
                            .setText(getStatistic())
                            .setChatId(chatId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkUpDao.select(57)));
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                int stockId = Integer.parseInt(updateMessageText.substring(3));
                sendStatisticByStock(stockDao.getStock(stockId));
                return false;

            case CHOOSE_USERS:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    bot.sendMessage(new SendMessage()
                            .setText(getStatistic())
                            .setChatId(chatId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkUpDao.select(57)));
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                if (updateMessageText.equals("prev")) {
                    userListPage--;
                    sendUserList();
                    return false;
                }
                if (updateMessageText.equals("next")) {
                    userListPage++;
                    sendUserList();
                    return false;
                }
                int userId = Integer.parseInt(updateMessageText);
                User user = userDao.getUserById(userId);
                bot.sendContact(new SendContact()
                        .setChatId(chatId)
                        .setPhoneNumber(user.getPhoneNumber())
                        .setFirstName(user.getName()));
                return false;
        }
        return false;
    }

    private void sendStatisticByStock(Stock stock) throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();
        List<Integer> groupsId = familiesDao.getGroupsByStockId(stock.getId());
        List<List<User>> groups = new ArrayList<>();
        for (Integer groupId : groupsId) {
            List<User> groupUsers = familiesDao.getUsersByGroupId(groupId, stock.getId());
            groups.add(groupUsers);
        }
        sb.append("<b>").append(messageDao.getMessageText(47)).append(": </b>").append(stock.getTitle()).append("\n");

        for (Task task : stock.getTaskList()) {
            sb.append("\t<b>").append(task.getName()).append(":</b>\n");

            for (Dates dates : task.getDates()) {
                sb.append("\t\t<b>").append(dates.getDate()).append(":</b>\n");

                for (Participant user : task.getParticipants()) {
                    if (dates.getDate().equals(user.getDate().getDate())) {
                        sb.append("\t\t\t").append(user.getUser().getName()).append(" - <b>");
                        for (List<User> users : groups) {
                            if (participated(users, user.getUser())) {
                                sb.append(messageDao.getMessageText(155));
                            } else {
                                sb.append(messageDao.getMessageText(156));
                            }
                        }
                        sb.append("</b>\n");
                    }
                }
            }
        }


        bot.sendMessage(new SendMessage()
                .setChatId(stock.getAddedBy().getChatId())
                .setText(sb.toString())
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboardMarkUpDao.select(10)));
    }

    private void sendStockList(List<Stock> stocks) throws SQLException, TelegramApiException {
        if (stocks.size() == 0) {
            sendMessage("No have done stocks");
        }
        StringBuilder sb = new StringBuilder();
        for (Stock stock : stocks) {
            sb.append("/id").append(stock.getId()).append(" - ").append(stock.getTitleForAdmin()).append("\n");
        }
        sendMessage(sb.toString());
        waitingType = WaitingType.CHOOSE_STOCK;
    }

    private void sendUserList() throws SQLException, TelegramApiException {
        if (newMessage) {
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText("Choose Volunteer")
                    .setReplyMarkup(getUserList(users)));
            newMessage = false;
        } else {
            bot.editMessageText(new EditMessageText()
                    .setMessageId(updateMessage.getMessageId())
                    .setChatId(chatId)
                    .setText("Choose Volunteer")
                    .setReplyMarkup(getUserList(users)));
        }
        waitingType = WaitingType.CHOOSE_USERS;
    }

    private InlineKeyboardMarkup getUserList(List<User> users) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (int i = userListPage * 5; i < (userListPage + 1) * 5 && users.size() > i; i++) {
            User user = users.get(i);
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(user.getName());
            button.setCallbackData(String.valueOf(user.getId()));
            buttons.add(button);
            row.add(buttons);
        }

        if (userListPage != 0) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("prev");
            button.setCallbackData("prev");
            buttons.add(button);
            row.add(buttons);
        }
        if (userListPage * 5 + 5 < users.size()) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("next");
            button.setCallbackData("next");
            buttons.add(button);
            row.add(buttons);
        }
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(10));
        button.setCallbackData(buttonDao.getButtonText(10));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(button);
        row.add(buttons);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private String getStatistic() throws SQLException {
        List<User> users = userDao.getUsers();
        StringBuilder sb = new StringBuilder();
        int manCount = 0;
        int womanCount = 0;
        int[] oblCount = new int[24];
        for (User user : users) {
            if (user.isSex()) {
                manCount++;
            } else {
                womanCount++;
            }
            for (int i = 87; i < 111; i++) {
                if (user.getCity().equals(buttonDao.getButtonText(i))) {
                    oblCount[i - 87]++;
                    break;
                }
            }
        }
        sb.append("<b>").append(messageDao.getMessageText(152)).append("</b>").append(users.size()).append("\n");
        sb.append("<b>").append(messageDao.getMessageText(153)).append("</b>").append(manCount).append("\n");
        sb.append("<b>").append(messageDao.getMessageText(154)).append("</b>").append(womanCount).append("\n");
        for (int i = 87; i < 111; i++) {
            if (oblCount[i - 87] != 0) {
                sb.append("<b>").append(buttonDao.getButtonText(i)).append(": </b>").append(oblCount[i - 87]).append("\n");
            }
        }
        return sb.toString();
    }

    private void sendStatisticToGoogleSheets() throws SQLException, TelegramApiException {
        sendMessage("Doing..");
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            document = implementation.createDocument(null, null, null);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        Element root = document.createElement("users");
        document.appendChild(root);
        List<User> userList = userDao.getUsers();
        List<Stock> stocks = stockDao.getStocks();
        List<List<Object>> writeData = new ArrayList<>();
        for (User user : userList) {
            if (user == null) {
                continue;
            }
            int registeredInStockCount = 0;
            int participatedCount = 0;
            List<Object> userData = new ArrayList<>();
            List<String> registered = new ArrayList<>();
            List<String> participated = new ArrayList<>();

            Element userElement = document.createElement("user");
            root.appendChild(userElement);

            addInfoToXml(userElement, document, user);

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
            Element friends = document.createElement("friends");
            userElement.appendChild(friends);
            for (User user1 : userFriends) {
                if (user1 == null) {
                    continue;
                }
                userData.add(user1.getName());
                Element friend = document.createElement("user");
                friends.appendChild(friend);
                friend.setAttribute("id", String.valueOf(user1.getId()));

            }

            Element stocksXml = document.createElement("stocks");
            userElement.appendChild(stocksXml);
            Element registeredXml = document.createElement("registered");
            stocksXml.appendChild(registeredXml);

            Element participatedXml = document.createElement("participated");
            stocksXml.appendChild(participatedXml);

            for (Stock stock : stocks) {

                for (Task task : stock.getTaskList()) {                     //Добавляем акции, в которых участвовал волонтер
                    if (addParticipant(task, user)) {
                        registered.add(stock.getTitleForAdmin());
                        registeredInStockCount++;

                        Element stockXml = document.createElement("stock");
                        registeredXml.appendChild(stockXml);
                        stockXml.setTextContent(stock.getTitleForAdmin());
                        break;
                    }
                }
                List<Integer> groups = familiesDao.getGroupsByStockId(stock.getId());
                for (Integer groupId : groups) {
                    List<User> groupUsers = familiesDao.getUsersByGroupId(groupId, stock.getId());
                    if (participated(groupUsers, user)) {
                        participated.add(stock.getTitleForAdmin());
                        participatedCount++;

                        Element stockXml = document.createElement("stock");
                        participatedXml.appendChild(stockXml);
                        stockXml.setTextContent(stock.getTitleForAdmin());
                        break;
                    }
                }
            }

            userData.add(registeredInStockCount + "/" + participatedCount);

            stocksXml.setAttribute("registered", String.valueOf(registeredInStockCount));
            stocksXml.setAttribute("participated", String.valueOf(participatedCount));

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
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            File file = new File("users.xml");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            bot.sendDocument(new SendDocument()
                    .setChatId(chatId)
                    .setNewDocument(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMessage(40, chatId, bot);   // Готово
    }

    private void addInfoToXml(Element userElement, Document document, User user) throws SQLException {
        Element idXml = document.createElement("id");
        userElement.appendChild(idXml);
        idXml.setTextContent(String.valueOf(user.getId()));

        Element nameXml = document.createElement("name");
        userElement.appendChild(nameXml);
        nameXml.setTextContent(user.getName());

        Element sexXml = document.createElement("sex");
        userElement.appendChild(sexXml);

        if (user.isSex()) {
            sexXml.setTextContent(buttonDao.getButtonText(11));
        } else {
            sexXml.setTextContent(buttonDao.getButtonText(12));
        }
        Element phoneXml = document.createElement("phone_number");
        userElement.appendChild(phoneXml);
        phoneXml.setTextContent(user.getPhoneNumber());

        Element cityXml = document.createElement("city");
        userElement.appendChild(cityXml);
        cityXml.setTextContent(user.getCity());

        Element birthdayXml = document.createElement("birthday");
        userElement.appendChild(birthdayXml);
        birthdayXml.setTextContent(user.getBirthday());


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
