package com.turlygazhy.tool.GoogleSheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vanderkast on 11.07.2017.
 *
 * This class uses to write data into google sheets
 */
public class GoogleSheetsWriting {
    private Sheets sheets = null;
    private String spreadsheetId = null;

    public GoogleSheetsWriting(Sheets sheets, String spreadsheetId) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
    }

    /**
     * Sets valueRange into range /converted by valueInputOption ("RAW", "USER_ENTERED")/
     * @param range
     * @param valueRange
     * @param valueInputOption
     * @return rows, which were updated
     * @throws IOException
     */
    public Integer updateValueResponse(String range, ValueRange valueRange, String valueInputOption) throws IOException {
        UpdateValuesResponse response = sheets.spreadsheets().values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption(valueInputOption)
                .execute();
        return response.getUpdatedCells();
    }

    /**
     * Append valueRange into range  /converted by valueInputOption ("RAW", "USER_ENTERED")/
     * @param range
     * @param valueRange
     * @param valueInputOption
     * @return
     * @throws IOException
     */
    public Integer appendValueResponse(String range, ValueRange valueRange, String valueInputOption) throws IOException {
        AppendValuesResponse response = sheets.spreadsheets().values().append(spreadsheetId, range, valueRange)
                .setValueInputOption(valueInputOption)
                .execute();
        return response.getUpdates().getUpdatedCells();
    }

    /**
     * Converts List of String into List of Object
     * @param rowData
     * @return
     */
    public List<Object> convertRow(List<String> rowData) {
        List<Object> row = new ArrayList<Object>();
        row.addAll(rowData);
        return row;
    }

    /**
     * converts List of List of Object into ValueRange
     * /converted by User_entered/
     * @param rowsData
     * @return
     */
    public ValueRange convertValueRange(List<List<Object>> rowsData) {
        ValueRange values = new ValueRange();
        values.setValues(rowsData);
        values.setMajorDimension("ROWS");
        return new ValueRange().setValues(rowsData);
    }

    /**
     * converts List of List of Object into ValueRange
     * /converted by USER_ENTERED/
     * @param rowsData
     * @return
     */
    public ValueRange convertUserEnteredValueRange(List<List<Object>> rowsData){
        ValueRange values = new ValueRange();
        values.setValues(rowsData);
        values.setMajorDimension("USER_ENTERED");
        return new ValueRange().setValues(rowsData);
    }
}
