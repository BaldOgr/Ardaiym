package com.turlygazhy.tool.GoogleSheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vanderkast on 11.07.2017.
 */
public class GoogleSheetsReading {
    private Sheets sheets = null;
    private String spreadsheetId = null;

    public GoogleSheetsReading(Sheets sheets, String spreadsheetId) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
    }

    /**
     *  Simple reading without formatting into String
     * @param range
     * @return
     * @throws IOException
     */
    public List<List<Object>> readTable(String range) throws IOException{
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }

    /**
     * @param range standart simple range
     * @return List (rows) of list (columns) with String (cells)
     * @throws IOException
     */
    public List<List<String>> readTableAsStringList(String range) throws IOException{
        List<List<Object>> rows = readTable(range);
        List<List<String>> response = new ArrayList<List<String>>();
        for(List<Object> row : rows){
            List<String> responseRow = new ArrayList<String>();
            for(Object obj : row){
                responseRow.add(obj.toString());
            }
            response.add(responseRow);
        }
        return response;
    }

    /**
     *
     * @param ranges ArrayList of ranges, if you need to custom range
     * @return List (rows) of Value range
     * @throws IOException
     */
    public List<ValueRange> readSelectedRange(List<String> ranges) throws IOException{
        BatchGetValuesResponse response = sheets.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();
        return response.getValueRanges();
    }

    /**
     * " " - after value;
     * "|\n" - after row;
     * @param range (table_name ! from_column_name_(Y_Pos) : to_column_name) example: "table!A1:C"
     * @return values in table as String
     * @throws IOException
     */
    public String readTableAsString(String range) throws IOException{
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> rows = response.getValues();
        String read = null;
        for(List<Object> row : rows){
            for(Object object : row){
                read = read + object.toString() + " ";
            }
            read = read + "|\n";
        }
        return read;
    }

    public Sheets getSheets() {
        return sheets;
    }

    public void setSheets(Sheets sheets) {
        this.sheets = sheets;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }
}
