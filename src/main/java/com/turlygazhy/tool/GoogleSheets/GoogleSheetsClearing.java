package com.turlygazhy.tool.GoogleSheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchClearValuesRequest;
import com.google.api.services.sheets.v4.model.BatchClearValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by Vanderkast on 12.07.2017.
 */
public class GoogleSheetsClearing {
    private Sheets sheets = null;
    private String spreadsheetId = null;

    public GoogleSheetsClearing(Sheets sheets, String spreadsheetId) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
    }

    /**
     * Clears sheet by range
     * @param range
     * @return
     * @throws IOException
     */
    public ClearValuesResponse clearSheetByRange(String range) throws IOException {
        ClearValuesRequest requestBody = new ClearValuesRequest();

        Sheets.Spreadsheets.Values.Clear request = sheets.spreadsheets().values().clear(spreadsheetId, range, requestBody);
        return request.execute();
    }

    /**
     * Clears sheet by custom range
     * @param ranges
     * @return
     * @throws IOException
     */
    public BatchClearValuesResponse clearSheetByRanges(List<String> ranges) throws IOException {
        BatchClearValuesRequest requestBody = new BatchClearValuesRequest();
        requestBody.setRanges(ranges);

        Sheets.Spreadsheets.Values.BatchClear request = sheets.spreadsheets().values().batchClear(spreadsheetId, requestBody);

        return request.execute();
    }
}
