package com.pavanahv.allakumarreddy.moneybook.utils;

import android.content.Context;

import com.pavanahv.allakumarreddy.moneybook.Activities.AddActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.AnalyticsActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.AnalyticsItemDetail;
import com.pavanahv.allakumarreddy.moneybook.Activities.AppCompatPreferenceActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.AutoAddActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.CreatePinActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.CreateSmartPinActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.ExportActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.FiltersAnalyticsActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.GoogleDriveActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.GraphActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.LoginActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.MainActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.MessageDetailActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.MessageParseActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.MessagesActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.RePaymentActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.ReportGraphDetailActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.RestoreActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.SettingsActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.WelcomeActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.WelcomeIconViewActivity;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.test.DataBaseActivity;
import com.pavanahv.allakumarreddy.moneybook.test.TestActivity;

import java.util.HashMap;

public class ThemeUtils {

    private final static int ACTION_BAR = 0;
    private final static int NO_ACTION_BAR = 1;
    private final static int FULL_SCREEN = 2;

    // themes array with two dimension. 1->dimension is according to settings display themes
    // And 2-> dimension will have three values in side -> theme, them_noActionBar, FullScreenTheme
    private static int themeArr[][] = new int[][]{
            {R.style.AppTheme, R.style.AppTheme_NoActionBar, R.style.FullscreenTheme},
            {R.style.AppThemeDark, R.style.AppThemeDark_NoActionBar, R.style.FullscreenThemeDark},
            {R.style.AppThemeRed, R.style.AppThemeRed_NoActionBar, R.style.FullscreenThemeRed},
            {R.style.AppThemeBlue, R.style.AppThemeBlue_NoActionBar, R.style.FullscreenThemeBlue},
            {R.style.AppThemeLightGreen, R.style.AppThemeLightGreen_NoActionBar, R.style.FullscreenThemeLightGreen},
            {R.style.AppThemeLightBlue, R.style.AppThemeLightBlue_NoActionBar, R.style.FullscreenThemeLightBlue},
            {R.style.AppThemeYellow, R.style.AppThemeYellow_NoActionBar, R.style.FullscreenThemeYellow},
            {R.style.AppThemeBlack, R.style.AppThemeBlack_NoActionBar, R.style.FullscreenThemeBlack},
    };

    private static HashMap<String, Integer> activityListMap = new HashMap<>();

    static {
        activityListMap.put(ExportActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(ReportGraphDetailActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(WelcomeIconViewActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(AutoAddActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(RePaymentActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(CreateSmartPinActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(CreatePinActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(FiltersAnalyticsActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(TestActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(SettingsActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(AddActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(MessageDetailActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(MessagesActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(DataBaseActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(MessageParseActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(LoginActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(MainActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(AnalyticsActivity.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(GraphActivity.class.getSimpleName(), FULL_SCREEN);
        activityListMap.put(AnalyticsItemDetail.class.getSimpleName(), NO_ACTION_BAR);
        activityListMap.put(GoogleDriveActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(RestoreActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(WelcomeActivity.class.getSimpleName(), ACTION_BAR);
        activityListMap.put(AppCompatPreferenceActivity.class.getSimpleName(), ACTION_BAR);
    }

    public static int getTheme(String activityName, Context context) {
        int type = getTypeTheme(activityName);
        int prefTheme = new PreferencesCus(context).getTheme();
        return themeArr[prefTheme][type];
    }

    private static int getTypeTheme(String activityName) {
        if (activityListMap.containsKey(activityName)) {
            return activityListMap.get(activityName);
        }
        return ACTION_BAR;
    }
}
