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
    public static final String ANALYTICS_FILTER_ACTIVITY = "ANALYTICS_FILTER_ACTIVITY";
    public static final String PREF_REPORTS_SWITCH = "PREF_REPORTS_SWITCH";
    public static final String PREF_REPORTS_TIME = "PREF_REPORTS_TIME";
    public static final String PREF_REPORTS_REMAINDER_SWITCH = "PREF_REPORTS_REMAINDER_SWITCH";
    public static final String PREF_REPORTS_REMAINDER_TIME = "PREF_REPORTS_REMAINDER_TIME";
    public static final String SMART_REMAINDER_NOTI = "SMART_REMAINDER_NOTI";
    public static final String LOGIN_CHECK = "LOGIN_CHECK";
    public static final String REPORTS_NOTI[] = new String[]{"REPORTS_NOTI_SPENT","REPORTS_NOTI_EARN","REPORTS_NOTI_DUE","REPORTS_NOTI_LOAN"};
    public static final String NOTIFICATION_CHANNLE_ID = "NOTIFICATION_CHANNLE_ID";
    public static final String  NOTIFICATION_CHANNLE_NAME = "NOTIFICATION_CHANNLE_NAME";
    public static final String NOTIFICATION_CHANNLE_DESCRIPTION = "NOTIFICATION_CHANNLE_DESCRIPTION";
    public static final int NOTIFICATION_ID = 2019;
    public static final int TYPE_SPENT = 0;
    public static final int TYPE_EARN = 1;
    public static final int TYPE_DUE = 2;
    public static final int TYPE_LOAN = 3;
    public static final int[] TYPE_NOTIFICATION_ID = new int[]{20191,20192,20193,20194};
    public static final int SMART_REMAINDER_NOTIFICATION_ID = 20195;
    public static final String PREF_LOCK_FINGERPRINT = "PREF_LOCK_FINGERPRINT";
    public static final String PREF_LOCK_TYPE = "PREF_LOCK_TYPE";
    public static final String PREF_LOCK_PIN = "PREF_LOCK_PIN";
    public static final String ACTION_SMART_REMAINDER_NOTIFICATION = "ACTION_SMART_REMAINDER_NOTIFICATION";
    public static final String ACTION_REPORT_NOTIFICATION = "ACTION_REPORT_NOTIFICATION";
    public static final int REQ_CODE_PENDING_INTENT_SMART_REMAINDER = 201991;
    public static final int REQ_CODE_PENDING_INTENT_REPORTS = 201992;
    public static final int HOME_SCREEN = 0;
    public static final int PAYMENT_METHOD_MONEY_TRANSFER_SCREEN = 10;
    public static final int SAVE_FILTER_SCREEN = 11;
    public static final int CATERGORY_SCREEN = 1;
    public static final int PAYMENT_METHOD_SCREEN = 2;
    public static final String PREF_LOCAL_BACKUP_DATA = "PREF_LOCAL_BACKUP_DATA";
    public static final String OTHERS_CAT = "others";
    public static final String ANALYTICS_FILTER_ACTIVITY_CATEGORY = "category";
    public static final String ANALYTICS_FILTER_ACTIVITY_PAYMENT_METHOD = "payment_method";
    public static final String ANALYTICS_FILTER_ACTIVITY_FILTER = "filter";
    public static final String CATEGORY_TYPE = "ADD_ACTIVITY_CATERGORY_TYPE";
    public static final String FRAGMENT_ACTIVATE_TYPE = "FRAGMENT_ACTIVATE_TYPE";
    public static String type[] = {"SPENT", "EARN", "DUE", "LOAN"};
    public static String fields[] = {"DESCRIPTION", "AMOUNT", "DATE"};
    public static final String ACTION_MSG_PARSE_BY_DATE = "com.example.allakumarreddy.moneybook.MessageParser.action.ParseMessagesFromContentProviderByTime";
    public static final String BACKUP_TO_GOOGLE_DRIVE = "BACKUP_TO_GOOGLE_DRIVE";
    public static final String RESTORE_FROM_GOOGLE_DRIVE = "RESOTRE_FROM_GOOGLE_DRIVE";
}
