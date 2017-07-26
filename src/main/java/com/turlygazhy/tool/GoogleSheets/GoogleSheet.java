package com.turlygazhy.tool.GoogleSheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;

/**
 * Created by Vanderkast on 11.07.2017.
 * <p>
 * This class uses to connect application with GoogleSheetsApi and build Sheets
 */
public class GoogleSheet {
    /**
     * Application name
     */
    private static String APPLICATION_NAME = "SIMPLE_GOOGLE_SHEETS_APP";

    /**
     * Builds Sheets by set params
     *
     * @return Sheets
     * @throws IOException
     */
    public static Sheets getBuilt(Authorization authorization) throws IOException {
        Credential credential = authorization.authorise();
        return new Sheets.Builder(authorization.getHTTP_TRANSPORT(), authorization.getJSON_FACTORY(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * @param authorization (use created Authorization object)
     * @param APPLICATION_NAME default: "SIMPLE_GOOGLE_SHEETS_APP"
     * @return Built sheet
     * @throws IOException
     */
    public static Sheets getBuilt(Authorization authorization, String APPLICATION_NAME) throws IOException {
        Credential credential = authorization.authorise();
        return new Sheets.Builder(authorization.getHTTP_TRANSPORT(), authorization.getJSON_FACTORY(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static String getAPPLICATION_NAME() {
        return APPLICATION_NAME;
    }

    public static void setAPPLICATION_NAME(String application_name) {
        APPLICATION_NAME = application_name;
    }
}

