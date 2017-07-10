package com.turlygazhy.tool;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.turlygazhy.entity.Family;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsAdapter {
    private static final String APPLICATION_NAME = "Google spreadsheet";
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static HttpTransport httpTransport;
    private static final List<String> SPREADSHEET_SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    private static final String SPREAD_SHEET_ID = "14s83d9z4xwEmyOWwWSWPrA3QvDVE0NKl0JMGmf8dXjU";
    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy hh:mm");


    private static Sheets service;

    private static void authorize(String securityFileName) throws Exception {
        try (InputStream stream = new FileInputStream(securityFileName)) {
            authorize(stream);
        }
    }

    private static void authorize(InputStream stream) throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential.fromStream(stream)
                .createScoped(SPREADSHEET_SCOPES);

        service = new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void writeData(String spreadsheetId,
                                 String sheetName,
                                 char colStart, int rowId, String data)
            throws Exception {
        authorize("/home/daniyar/IdeaProjects/templates/members-36a5849089da.json");

        String writeRange = sheetName + "!" + colStart + rowId + ":" + (char) (colStart + 7);

        List<List<Object>> writeData = new ArrayList<>();

        List<Object> dataRow = new ArrayList<>();

        dataRow.add(data);

        writeData.add(dataRow);

        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
        service.spreadsheets().values()
                .append(spreadsheetId, writeRange, vr)
                .setValueInputOption("RAW")
                .execute();
        System.out.println("Added!");

//        service.spreadsheets().values()
//                .update(spreadsheetId, writeRange, vr)
//                .setValueInputOption("RAW")
//                .execute();
    }

    public static void writeData(List<List<Object>> writeData) throws Exception {
        String writeRange = "Лист1!B2:G";
        authorize("/home/daniyar/IdeaProjects/templates/members-36a5849089da.json");
        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
        service.spreadsheets().values().update(SPREAD_SHEET_ID, writeRange, vr).setValueInputOption("RAW").execute();
    }

    public static List<Family> getFamiles() throws Exception {
        authorize("/home/daniyar/IdeaProjects/templates/members-36a5849089da.json");
        String range = "Лист1!B2:G";
        ValueRange response = service.spreadsheets().values().get(SPREAD_SHEET_ID, range).execute();

        List<List<Object>> values = response.getValues();
        List<Family> families = new ArrayList<>();

        for (List row : values) {
            Family family = new Family();
            try {
                family.setName((String) row.get(0));
                family.setPhoneNumber((String) row.get(1));
                family.setAddress((String) row.get(2));
                family.setLongitude(Double.parseDouble((String) row.get(3)));
                family.setLatitude(Double.parseDouble((String) row.get(4)));
                family.setGroup(Integer.parseInt((String) row.get(5)));
                families.add(family);
            } catch (IndexOutOfBoundsException ex){
                System.out.println("Nothing on this");
            }
        }
        return families;
    }


/*
Этот метод умеет добавлять данные в хвост, но с ним еще нужно разбираться
    private void  AddData() throws Exception {
        service = getSheetsService();
        String spreadSheetID = "1ZAFFrDgmkCcCVrw_zMFvOnogy0bQ258CxROT11R7LD0";
        //Integer sheetID = 123;
        String DateValue = "2015-07-13";
        List<RowData> rowData = new ArrayList<RowData>();
        List<CellData> cellData = new ArrayList<CellData>();
        CellData cell = new CellData();
        cell.setUserEnteredValue(new ExtendedValue().setStringValue(DateValue));
        cell.setUserEnteredFormat(new CellFormat().setNumberFormat(new NumberFormat().setType("DATE")));
        cellData.add(cell);
        rowData.add(new RowData().setValues(cellData));
        //Sheets.Spreadsheets
        BatchUpdateSpreadsheetRequest batchRequests = new BatchUpdateSpreadsheetRequest();
        BatchUpdateSpreadsheetResponse response;
        List<Request> requests = new ArrayList<Request>();
        AppendCellsRequest appendCellReq = new AppendCellsRequest();
        //appendCellReq.setSheetId( sheetID);
        appendCellReq.setRows(rowData);
        appendCellReq.setFields("userEnteredValue,userEnteredFormat.numberFormat");
        requests = new ArrayList<Request>();
        requests.add(new Request().setAppendCells(appendCellReq));
        batchRequests = new BatchUpdateSpreadsheetRequest();
        batchRequests.setRequests(requests);
        response = service.spreadsheets().batchUpdate(spreadSheetID, batchRequests).execute();
        System.out.println("Request \n\n");
        System.out.println(batchRequests.toPrettyString());
        System.out.println("\n\nResponse \n\n");
        System.out.println(response.toPrettyString());
    }
    */
    /*private static final int LAST_ROW_DATA_ID = 3;
    private String userId;
    private static final String KEY = "src/main/resources/members-36a5849089da.json";
//    private static final String KEY = "C:\\bots-data\\members-36a5849089da.json";
    private static final String SPREAD_SHEET_ID = "1HyLocKj3xc-auD2zCbk5zpXNioHveMJEYYvpHHVvCEM";
    public AddToGoogleSheetsCommand(String userId) {
        super();
        this.userId = userId;
    }
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Integer userId = Integer.valueOf(this.userId);
        Member member = memberDao.selectByUserId(userId);
        SheetsAdapter sheets = new SheetsAdapter();
        ArrayList<Member> list = new ArrayList<>();
        list.add(member);
        int lastRow = Integer.parseInt(constDao.select(LAST_ROW_DATA_ID));
        int puttedRow = lastRow + 1;
        try {
            sheets.authorize(KEY);
            sheets.writeData(SPREAD_SHEET_ID, "list", 'A', puttedRow, list);
            constDao.update(LAST_ROW_DATA_ID, String.valueOf(puttedRow));
            memberDao.setAddedToGroup(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendMessage(60, update.getCallbackQuery().getMessage().getChatId(), bot);
        sendMessage(72, member.getChatId(), bot);
        sendMessage(70, member.getChatId(), bot);
        return true;
    }*/
}