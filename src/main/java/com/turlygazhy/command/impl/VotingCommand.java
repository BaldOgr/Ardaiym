package com.turlygazhy.command.impl;

import Constructors.InlineKeyboardConstructor;
import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.VoteDao;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Voting;
import com.turlygazhy.tool.Charts.PieChartConstructor;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import patterns.KeyboardPattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static patterns.KeyboardPattern.ONE_BUTTON_AT_ROW;
import static patterns.KeyboardPattern.THREE_BUTTON_AT_ROW;
import static patterns.KeyboardPattern.TWO_BUTTON_AT_ROW;



/**For all thats stuff you need create in db buttons with command id to this command
 * buttons called - voteSelection, getVoteResults, getVoteGraphs
 * makeResMailing, getMyVotings, deleteVote, getVoteRsFCre
 * and one button with any name you want to create new voting
 * ALSO create filter for inputted text in conversation call like this bot, because
 * he used callback data with : char and voting id int
 * ALSO you need table named "votings" with columns -
 * ID big int primary key not null auto increment
 * text just text
 * photo text too but you can use shorter things
 * vote_selections text[] for save your vote selections
 * votes_in_selections text[] for save your votes in selections
 * keyboard_pattern i'm really don't know why i have created this, but in future
 * i think it's can be useful
 * creator_id bigint for save creator id
 * that's all
 *
 * Created by Eshu on 07.07.2017.
 */
public class VotingCommand extends Command {
    private boolean           expectedEnter;
    private ArrayList<String> voteSelections = new ArrayList<>();
    private String            image;
    private String            text;
    private int               step = 0;
    private long              chatId;
    private String            chose;
    private long              votingId;
    private boolean           voteInGroup;
    private KeyboardPattern   keyboardPattern;
    // todo Put here your group chat id for voting;
    private long              GROUP_FOR_VOTE = 0;

    private VoteDao voteDao  = factory.getVoteDao();

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        if(expectedEnter){
            switch (step){
                case 0:
                    text = updateMessage.getText();
                    step = 1;
                    break;
                case 1:
                    try {
                        image = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                    } catch (Exception e) {
                        // here is chose to make vote without photo
                        try{
                            if (update.getCallbackQuery().getData().equals("Без фото")) {
                                image = null;
                            }
                        } catch (NullPointerException e1){
                            bot.sendMessage(new SendMessage().setChatId(chatId)
                                    .setText("Если вы не хотите загружать фото то нажимте на кнопку ''Без фото''"));
                            return false;
                        }
                    }
                    step = 2;
                    break;
                case 2:
                    try {
                        voteSelections.add(update.getMessage().getText());
                        bot.sendMessage(new SendMessage().setChatId(chatId).setText("Добавьте вариант ответа(Ответы должны быть уникальными)")
                                .setReplyMarkup(getkeyboardForVoteSelectionCreation()));
                        return false;
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals("finishVoteSelectionsCreation")){
                            step = 3;
                        }
                    }
                    break;
                case 3:
                    try {
                        switch (update.getCallbackQuery().getData()){
                            case "voteOneButtonPerRow":
                                keyboardPattern = ONE_BUTTON_AT_ROW;
                                break;
                            case "voteTwoButtonsPerRow":
                                keyboardPattern = TWO_BUTTON_AT_ROW;
                                break;
                            case "voteThreeButtonsPerRow":
                                keyboardPattern = THREE_BUTTON_AT_ROW;
                        }
                    } catch (Exception e) {
                        bot.sendMessage(new SendMessage().setChatId(chatId).setText("Вам нужно нажать на кнопку, а не писать"));
                        return false;
                    }
                    step = 4;
                    break;
                case 4:
                    try {
                        switch (update.getCallbackQuery().getData()){
                            case "voteToGroup":
                                voteInGroup = true;
                                break;
                            case "voteToMembers":
                                voteInGroup = false;
                        }
                    } catch (NullPointerException e) {
                        bot.sendMessage(new SendMessage().setChatId(chatId).setText("Вам нужно выбрать кнопку"));
                        return false;
                    }
                    step = 5;
            }
        }
        if(chose == null) {
            chatId = updateMessage.getChatId();
            if (update.getCallbackQuery() != null) {
                if (update.getCallbackQuery().getData().contains(":")) {
                    if(update.getCallbackQuery().getData().contains("/")){
                        chose    = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery().getData().indexOf("/"));
                        votingId = Long.parseLong(update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/") + 1));
                        int voteSelectionCount = voteDao.getVotesSelectionNames(votingId).length;
                        for (int i = 0; i < voteSelectionCount;) {
                            if (chose.equals("voteSelection:" + i)) {
                                addVote(bot,update, chatId, votingId, i);
                                return true;
                            }
                            i++;
                        }
                    }
                    else {
                        chose    = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery().getData().indexOf(":"));
                        votingId = Long.parseLong(update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1));
                        try {
                            operationsWithVoteResult(bot,update, chatId, votingId, chose);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                if(update.getCallbackQuery().getData().equals("getMyVotings")){
                    getMyVotings(bot, chatId);
                    return true;
                }
            }
        }
        //Section for create a new vote
        if(step == 0 & text == null){
            //Asking text
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("Введите текст для голосования"));
            expectedEnter = true;
            return false;
        }
        if(step == 1 & image == null){
            //Ask image and create button for choose version without photo
            //with Vanderkast keyboard constructor
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add("Без фото");
            bot.sendMessage(new SendMessage().setReplyMarkup(InlineKeyboardConstructor.getKeyboard(stringArrayList))
                    .setText("Добавить фото?").setChatId(chatId));
            return false;
        }
        if(step == 2 & voteSelections.isEmpty()){
            //Ask vote selections
            bot.sendMessage(new SendMessage().setChatId(chatId)
                    .setText("Добавьте вариант ответа"));
            return false;
        }
        if(step == 3){
            //Asking about how much set buttons per row
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("Выберите количество кнопок в ряду")
                    .setReplyMarkup(getKeyboardForRowSelect()));
            return false;
        }
        if(step == 4){
            //Asking about vote for group or members
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("В группу или всем подписчикам?")
                    .setReplyMarkup(getKeyboardForSelectVotePlace()));
            return false;
        }
        if(step == 5){
            //Sending and saving vote
            long votingId = voteDao.createVote(text, image,
                    voteSelections.toArray(new String[voteSelections.size()]),chatId,keyboardPattern);

            SendMessage sendVote = new SendMessage().setText(text).setReplyMarkup(
                    getKeyboardForVote(voteSelections,keyboardPattern, votingId));
            //Saving vote to db
            if(voteInGroup){
                if(image!=null) {
                    bot.sendPhoto(new SendPhoto().setChatId(GROUP_FOR_VOTE).setPhoto(image));
                }
                bot.sendMessage(sendVote.setChatId(GROUP_FOR_VOTE));
            }else {
                sendVoteToMembers(text, image, bot, voteSelections, keyboardPattern, votingId);
            }
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("Опрос успешно создан"));
        }



        expectedEnter   = false;
        voteSelections  = null;
        image           = null;
        text            = null;
        step            = 0;
        chatId          = 0;
        chose           = null;
        votingId        = 0;
        voteInGroup     = false;
        keyboardPattern = null;
        return true;
    }



    //Custom keyboards
    private InlineKeyboardMarkup getkeyboardForVoteSelectionCreation(){
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        buttonText.add("Закончить добавление вариантов ответа");
        buttonData.add("finishVoteSelectionsCreation");
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData);
    }

    private InlineKeyboardMarkup getKeyboardForRowSelect(){
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        buttonText.add("Одна кнопка в ряду");
        buttonData.add("voteOneButtonPerRow");
        buttonText.add("Две кнопки в ряду");
        buttonData.add("voteTwoButtonsPerRow");
        buttonText.add("Три кнопки в ряду");
        buttonData.add("voteThreeButtonsPerRow");
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData);
    }

    private InlineKeyboardMarkup getKeyboardForSelectVotePlace(){
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        buttonText.add("В группу");
        buttonData.add("voteToGroup");
        buttonText.add("Подписчикам");
        buttonData.add("voteToMembers");
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData);
    }

    private InlineKeyboardMarkup getKeyboardForVote(ArrayList<String> voteSelections, KeyboardPattern keyboardPattern, long votingId) throws SQLException {
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        buttonText.addAll(voteSelections);
        String[] votes = voteDao.getAllVotesMembers(votingId);
        for (int i = 0; i < buttonText.size(); i++) {
            String votesToButton;
            votesToButton = votes[i];
            buttonText.set(i, buttonText.get(i) + " " + getVoteCount(votesToButton));
        }
        int i = 0;
        for (String ignored : voteSelections) {
            buttonData.add("voteSelection:" + i + "/" + votingId);
            i++;
        }
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData, keyboardPattern);
    }


    private InlineKeyboardMarkup getKeyForGraph(long votingId){
        ArrayList<String> stringButtonText = new ArrayList<>();
        stringButtonText.add("Показать график");
        ArrayList<String> stringArrayCallBack = new ArrayList<>();
        stringArrayCallBack.add("getVoteGraphs" + ":" + votingId);
        return InlineKeyboardConstructor.getKeyboard(stringButtonText, stringArrayCallBack);
    }

    private InlineKeyboardMarkup getVotingsViaButtonsForCreator(ArrayList<Voting> votings){
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        for(Voting voting : votings){
            buttonText.add(voting.getVoteText());
            buttonData.add("getVoteRsFCre:" + voting.getId());
            buttonText.add("Удалить голосование ⬆️");
            buttonData.add("deleteVote:" + voting.getId());
        }
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData);
    }

    private InlineKeyboardMarkup getKeyboardForCreator(long votingId){
        ArrayList<String> buttonText = new ArrayList<>();
        ArrayList<String> buttonData = new ArrayList<>();
        buttonText.add("Сделать рассылку всем проголосовавшим");
        buttonData.add("makeResMailing:"+ votingId);
        buttonText.add("Показать график");
        buttonData.add("getVoteGraphs" + ":" + votingId);
        buttonText.add("Удалить результаты голосования");
        buttonData.add("deleteVote:" + votingId);
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonData);
    }


    //Send messages about vote and other stuff
    private void sendVoteToMembers(String text, String image, Bot bot,
                                   ArrayList<String> voteSelections, KeyboardPattern keyboardPattern,long votingId) throws SQLException {
        //todo use your own dao and other stuff for select your bot followers
        List<Member> memberArrayList = memberDao.selectAll();
        if(memberArrayList!=null) {
            SendMessage senVote = new SendMessage().setText(text).setReplyMarkup(getKeyboardForVote(voteSelections, keyboardPattern, votingId));
            for (Member member : memberArrayList){
                try {
                    if(image!=null){
                        bot.sendPhoto(new SendPhoto().setChatId(member.getChatId()).setPhoto(image));
                    }
                    bot.sendMessage(senVote.setChatId(member.getChatId()).setParseMode(ParseMode.HTML));

                } catch (TelegramApiException ignored){

                }
            }
        }
    }


    //Add new vote to Voting
    private void addVote(Bot bot, Update update, long chatId, long votingId, int voteSelection) throws SQLException, TelegramApiException {
        long memberChatId = chatId;
        if(chatId<0){
            memberChatId = update.getCallbackQuery().getFrom().getId();
        }
        if(isVoted(chatId)){
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("Вы уже проголосовали"));
        }else {
            //todo add your function memberdao or something for future check - that user is voted
            voteDao.makeVote(votingId, voteSelection, String.valueOf(memberChatId));
            Voting voting = voteDao.getVoting(votingId);
            bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(update
                    .getCallbackQuery().getMessage().getMessageId()).setReplyMarkup(getKeyboardForVote(
                    new ArrayList<>(Arrays.asList(voting.getVoteSelections())), voting.getKeyboardPattern(), votingId)));

        }
    }
    //Regular result with default result keyboard
    private void getRegularResult(Bot bot, long chatId, long votingId) throws SQLException {
        Voting voting = voteDao.getVoting(votingId);
        if(voting==null){
            try {
                bot.sendMessage(new SendMessage().setChatId(chatId).setText("Голосование не найдено"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        try{
            if(voting.getPhoto()!=null){
                bot.sendPhoto(new SendPhoto().setChatId(chatId).setPhoto(voting.getPhoto()));
            }
            bot.sendMessage(new SendMessage().setChatId(chatId).setText(getVoteResultsText(voting))
                    .setParseMode(ParseMode.HTML)
                    .setReplyMarkup(getKeyForGraph(votingId)));
        } catch (TelegramApiException ignored){
        }
    }
    //Result for creator with additional keyboard
    private void getResultToCreator(Bot bot, long chatId, long votingId) throws SQLException {
        Voting voting = voteDao.getVoting(votingId);
        if(voting==null){
            try {
                bot.sendMessage(new SendMessage().setChatId(chatId).setText("Голосование не найдено"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        try{
            if(voting.getPhoto()!=null){
                bot.sendPhoto(new SendPhoto().setChatId(chatId).setPhoto(voting.getPhoto()));
            }
            bot.sendMessage(new SendMessage().setChatId(chatId).setText(getVoteResultsText(voting))
                    .setReplyMarkup(getKeyboardForCreator(votingId))
                    .setParseMode(ParseMode.HTML));
        } catch (TelegramApiException ignored){
        }
    }


    //Operations with result like get regular result ot delete vote
    private void operationsWithVoteResult(Bot bot,Update update, long chatId, long votingId, String chose) throws TelegramApiException, SQLException, IOException {
        switch (chose){
            case "getVoteResults":
                getRegularResult(bot, chatId, votingId);
                return;
            case "getVoteRsFCre":
                getResultToCreator(bot, chatId, votingId);
                return;
            case "getVoteGraphs":
                getPie(bot, chatId, votingId);
                return;
            case "makeResMailing":
                makeResultMailing(bot, votingId, chatId);
                return;
            case "deleteVote":
                deleteVoting(bot,update, chatId, votingId);
        }
    }

    //Get pie command, that's all
    private void getPie(Bot bot, long chatId, long votingId) throws SQLException, IOException, TelegramApiException {
        File file;
        FileInputStream fileInputStream;
        Voting voting = voteDao.getVoting(votingId);
        ArrayList<Integer> votesInSections = new ArrayList<>();

        //Check for votes to pieChart
        if(Arrays.toString(voting.getVotesInSelections()).length() <= 6 * voting.getVotesInSelections().length) {
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("Еще никто не проголосовал"));
            return;
        }
        for (String string : voting.getVotesInSelections()){
            votesInSections.add(getVoteCount(string));
        }

        String currentDir = System.getProperty("user.dir") +"/src/main/resources/graphs";
        PieChartConstructor pieChartConstructor = new PieChartConstructor(currentDir,voting.getVoteText(),
                new ArrayList<>(Arrays.asList(voting.getVoteSelections())),votesInSections,800,600);
        new File(pieChartConstructor.getPieChartConstructed());
        String filePath = pieChartConstructor.getPath()+".jpg";
        file = new File(filePath);
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            bot.sendPhoto(new SendPhoto()
                    .setChatId(chatId)
                    .setNewPhoto("photo", fileInputStream)
            );
        } catch (TelegramApiException ignored) {
        }
        fileInputStream.close();
        Path path = Paths.get(filePath);
        Files.delete(path);
    }



    //Command for result mailing, kind of crutch
    private void makeResultMailing(Bot bot, long votingId, long chatId) throws TelegramApiException, SQLException {
        try {
            String[] membersIds = voteDao.getAllVotesMembers(votingId);
            StringBuilder sb = new StringBuilder();
            for(String s : membersIds) {
                if(s!=null){
                    sb.append(s);}
            }
            String membersIdsInOneString = sb.toString();
            membersIds = membersIdsInOneString.split("/");
            Voting voting = voteDao.getVoting(votingId);
            String text = getVoteResultsText(voting);
            for (String s : membersIds) {
                //Postgresql always creates new element of array with null entity, i don't know why, if you know - pm me pls.
                //Until i will have enlightenment this crutch will be here
//                if(!s.equals("null")) {
                try {
                    if(voting.getPhoto()!=null){
                        bot.sendPhoto(new SendPhoto().setPhoto(voting.getPhoto()).setChatId(s));
                    }
                    bot.sendMessage(new SendMessage().setText(text).setChatId(s)
                            .setReplyMarkup(getKeyForGraph(votingId))
                            .setParseMode(ParseMode.HTML));
                }catch
                    //For users who blocked bot
                        (TelegramApiException ignored){

                }
//                }
            }
        } catch (NullPointerException e){
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("Голосование не найдено"));
        }
    }

    //Thing for result text create
    private String getVoteResultsText(Voting voting) throws SQLException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<b>Текст опроса</b>\n\n").append(voting.getVoteText()).append("\n\n<b>Результаты</b>\n\n");
            for (int i = 0; i < voting.getVoteSelections().length; i++){
                sb.append(voting.getVoteSelections()[i]).append(": ").append(getVoteCount(voting.getVotesInSelections()[i])).append("\n");
            }
            return sb.toString();} catch (NullPointerException e){
            return "Ошибка";
        }
    }

    //Just count for buttons etc
    private int getVoteCount(String votes) {
        int count = 0;
        if (votes == null) {
            return count;
        } else {
            for (char element : votes.toCharArray()) {
                if (element == '/') count++;
            }
        }
        return count;
    }

    //Thats thing show votings where you are creator
    private void getMyVotings(Bot bot, long chatId) throws SQLException, TelegramApiException {
        ArrayList<Voting> votingArrayList = voteDao.getAllVotingsByMemberId(chatId);
        if(votingArrayList.size()!=0){
            bot.sendMessage(new SendMessage().setChatId(chatId).setReplyMarkup(getVotingsViaButtonsForCreator(votingArrayList))
                    .setText("Ваши голосования"));
        } else {
            bot.sendMessage(new SendMessage().setChatId(chatId).setText("У вас нет голосований"));
        }
    }

    //Thats thing delete your voting
    private void deleteVoting(Bot bot,Update update, long chatId, long votingId) throws SQLException, TelegramApiException {
        voteDao.deleteVoting(votingId);
        bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(update
                .getCallbackQuery().getMessage().getMessageId()).setReplyMarkup(getVotingsViaButtonsForCreator(
                voteDao.getAllVotingsByMemberId(chatId))));
        bot.sendMessage(new SendMessage().setChatId(chatId).setText("Голосование удалено"));
    }

    //todo Complete this function for you needs
    private boolean isVoted(long chatId){
        //Here you need place your code for check voted user or not, like ask member db or something else
        return false;
    }




}