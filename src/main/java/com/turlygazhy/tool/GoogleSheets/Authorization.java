package com.turlygazhy.tool.GoogleSheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vanderkast on 10.07.2017.
 */
public class Authorization {
    /**
     * Path to file of secrets for google api
     */
    private String CLIENT_SECRETS_PATH = "/client_secrets.json";

    /**
     * Global instance of the JSON factory.
     */
    private final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Directory to store user credentials for this application.
     * Change it, if you need another permissions for GoogleSheets
     */
    private File DATA_STORE_DIR = new File(
            System.getProperty("user.home"), ".credentials/sheets.adapter");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the HTTP transport.
     */
    private HttpTransport HTTP_TRANSPORT;

    /**
     * Param, witch set up settings to use sheet (can be switched)
     */
    private List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    /**
     * @param CLIENT_SECRETS_PATH defaults: "/client_secrets.json"
     * @param sheetsScopes defaults: SPREADSHEETS_READONLY
     */
    public Authorization(String CLIENT_SECRETS_PATH, String sheetsScopes) {
        this.CLIENT_SECRETS_PATH = CLIENT_SECRETS_PATH;
        this.SCOPES = Arrays.asList(sheetsScopes);

    }

    /**
     * @param CLIENT_SECRETS_PATH defaults: "/client_secrets.json"
     * @param DATA_STORE_DIR defaults: java.io.File(
     *                          System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");
     * @param sheetsScopes defaults: SPREADSHEETS_READONLY
     */
    public Authorization(String CLIENT_SECRETS_PATH, File DATA_STORE_DIR, String sheetsScopes) {
        this.CLIENT_SECRETS_PATH = CLIENT_SECRETS_PATH;
        this.DATA_STORE_DIR = DATA_STORE_DIR;
        this.SCOPES = Arrays.asList(sheetsScopes);
    }

    private void connect() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @return Credential (connection application with GoogleSheetsAPI)
     * @throws IOException
     */
    public Credential authorise() throws IOException {
        connect();

        InputStream in = GoogleSheet.class.getResourceAsStream(CLIENT_SECRETS_PATH);

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    public String getCLIENT_SECRETS_PATH() {
        return CLIENT_SECRETS_PATH;
    }

    public void setCLIENT_SECRETS_PATH(String CLIENT_SECRETS_PATH) {
        this.CLIENT_SECRETS_PATH = CLIENT_SECRETS_PATH;
    }

    public JsonFactory getJSON_FACTORY() {
        return JSON_FACTORY;
    }

    public File getDATA_STORE_DIR() {
        return DATA_STORE_DIR;
    }

    public void setDATA_STORE_DIR(File DATA_STORE_DIR) {
        this.DATA_STORE_DIR = DATA_STORE_DIR;
    }

    public FileDataStoreFactory getDATA_STORE_FACTORY() {
        return DATA_STORE_FACTORY;
    }

    public void setDATA_STORE_FACTORY(FileDataStoreFactory DATA_STORE_FACTORY) {
        this.DATA_STORE_FACTORY = DATA_STORE_FACTORY;
    }

    public List<String> getSCOPES() {
        return SCOPES;
    }

    public void setSCOPES(List<String> SCOPES) {
        this.SCOPES = SCOPES;
    }

    public HttpTransport getHTTP_TRANSPORT() {
        return HTTP_TRANSPORT;
    }

    public void setHTTP_TRANSPORT(HttpTransport HTTP_TRANSPORT) {
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
    }
}
