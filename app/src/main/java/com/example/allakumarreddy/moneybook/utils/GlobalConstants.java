package com.example.allakumarreddy.moneybook.utils;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class GlobalConstants {
    public static final int MSG_PARSING_COMPLETED = 10000;
    public static final String HANDLER_NAME = "handler";
    public static final String ACTION_BACKUP = "backup";
    public static final String ACTION_BACKUP_MAIN_ACTIVITY_OPEN = "backup_when_main_activity_open";
    public static final String ACTION_IMPORT = "import";
    public static final int BACKUP_COMPLETED = 10001;
    public static final String PREF_BACKUP_TO_GOOGLE_DRIVE = "backup_to_google_drive";
    public static final String PREF_EMAIL = "EMAIL";
    public static final String PREF_BACKUP_FREQUENCY = "backup_frequency";
    public static final String PREF_GOOGLE_DRIVE_BACKUP_FILE_NAME = "PREF_GOOGLE_DRIVE_BACKUP_FILE_NAME";
    public static final String GOOGLE_DRIVE_BACKUP_FILE_NAME = "MoneyBook.json";
    public static final String PREF_GOOGLE_DRIVE_BACKUP_FILE_SIZE = "PREF_GOOGLE_DRIVE_BACKUP_FILE_SIZE";
    public static final String PREF_GOOGLE_DRIVE_BACKUP_FILE_DATE = "PREF_GOOGLE_DRIVE_BACKUP_FILE_DATE";
    public static final String ACTION_INTERNET_CONNECTED = "ACTION_INTERNET_CONNECTED";
    public static final int ACTIVATE_GRAPH_ACTIVITY_WITHOUT_ADD_TO_SCREEN_MENU = 1;
    public static final int ACTIVATE_GRAPH_ACTIVITY_WITH_ADD_TO_SCREEN_MENU = 0;
    public static String type[] = {"SPENT", "EARN", "DUE", "LOAN"};
    public static String fields[] = {"DESCRIPTION", "AMOUNT", "DATE"};
    public static final String ACTION_MSG_PARSE_BY_DATE = "com.example.allakumarreddy.moneybook.MessageParser.action.ParseMessagesFromContentProviderByTime";
    public static final String BACKUP_TO_GOOGLE_DRIVE = "BACKUP_TO_GOOGLE_DRIVE";
    public static final String RESTORE_FROM_GOOGLE_DRIVE = "RESOTRE_FROM_GOOGLE_DRIVE";
}
