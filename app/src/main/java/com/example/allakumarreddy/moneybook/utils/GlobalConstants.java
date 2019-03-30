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
    public static String type[]={"SPENT","EARN","DUE","LOAN"};
    public static String fields[]={"DESCRIPTION","AMOUNT","DATE"};
    public static final String ACTION_MSG_PARSE_BY_DATE = "com.example.allakumarreddy.moneybook.MessageParser.action.ParseMessagesFromContentProviderByTime";
}
