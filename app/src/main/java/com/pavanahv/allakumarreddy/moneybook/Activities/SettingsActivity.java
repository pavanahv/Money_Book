package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.utils.Backup;
import com.pavanahv.allakumarreddy.moneybook.utils.FingerPrintManager;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.TimePreference;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "SettingsAcitivy";
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                if (prefInit) {
                    switch (preference.getKey()) {
                        case GlobalConstants.PREF_BACKUP_FREQUENCY:
                            Utils.cancelAlarmForGoogleDriveBackup(context);
                            Utils.setAlarmForGoogleDriveBackup(context);
                            break;
                        case GlobalConstants.PREF_LOCK_TYPE:
                            String lockType = (String) value;
                            if (lockType != null) {
                                switch (Integer.parseInt(lockType)) {
                                    case 2:
                                        // no lock
                                        Toast.makeText(context, "No Lock Selected!\n For your Privacy We Recommend You To Set Lock", Toast.LENGTH_LONG).show();
                                        break;
                                    case 0:
                                        // pin lock
                                        context.startActivity(new Intent(context, CreatePinActivity.class));
                                        break;
                                    case 1:
                                        // smart lock
                                        context.startActivity(new Intent(context, CreateSmartPinActivity.class));
                                        break;
                                }
                            }
                            break;
                    }
                }
            } else if (preference instanceof TimePreference) {
                if (prefInit) {
                    switch (preference.getKey()) {
                        case GlobalConstants.PREF_REPORTS_REMAINDER_TIME:
                            Utils.cancelAlarmForReportsRemainder(context);
                            Utils.setAlarmForReportsRemainder(context);
                            break;
                        case GlobalConstants.PREF_REPORTS_TIME:
                            Utils.cancelAlarmForReportsNotification(context);
                            Utils.setAlarmForReportsNotification(context);
                            break;
                    }
                }
            } else if (preference instanceof SwitchPreference) {
                if (prefInit) {
                    switch (preference.getKey()) {
                        case GlobalConstants.PREF_REPORTS_REMAINDER_SWITCH:
                            if ((boolean) value) {
                                Utils.setAlarmForReportsRemainder(context);
                            } else {
                                Utils.cancelAlarmForReportsRemainder(context);
                            }
                            break;

                        case GlobalConstants.PREF_REPORTS_SWITCH:
                            if ((boolean) value) {
                                Utils.setAlarmForReportsNotification(context);
                            } else {
                                Utils.cancelAlarmForReportsNotification(context);
                            }
                            break;

                        case GlobalConstants.PREF_LOCK_FINGERPRINT:
                            if ((boolean) value) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    FingerPrintManager fm = new FingerPrintManager(context, (isSuccess, message) -> {

                                    });
                                    boolean res = fm.main();
                                    if (!res) {
                                        //PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(GlobalConstants.PREF_LOCK_FINGERPRINT, false).apply();
                                        Toast.makeText(context, fm.getErrorText(), Toast.LENGTH_LONG).show();
                                        return false;
                                    } else {
                                        Toast.makeText(context, "Fingerprint Successfully Enabled !", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Unsupported OS for FingerPrint Login. Minimum OS should be \"M\"", Toast.LENGTH_LONG).show();
                                }

                            }
                            break;
                    }
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private static boolean prefInit = false;
    private static Context context;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        prefInit = false;
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
        prefInit = true;
    }

    private static void bindPreferenceSummaryToLongValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        prefInit = false;
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getLong(preference.getKey(), -1));
        prefInit = true;
    }

    private static void bindPreferenceSummaryToValueBool(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        prefInit = false;
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
        prefInit = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValueBool(findPreference(GlobalConstants.PREF_BACKUP_TO_GOOGLE_DRIVE));
            bindPreferenceSummaryToValue(findPreference(GlobalConstants.PREF_EMAIL));
            bindPreferenceSummaryToValue(findPreference(GlobalConstants.PREF_BACKUP_FREQUENCY));
            bindPreferenceSummaryToValue(findPreference(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_SIZE));
            bindPreferenceSummaryToValue(findPreference(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_DATE));
            Preference localBackupPreference = findPreference(GlobalConstants.PREF_LOCAL_BACKUP_DATA);
            bindPreferenceSummaryToValue(localBackupPreference);
            localBackupPreference.setOnPreferenceClickListener(preference -> {
                new Thread(() -> {
                    Backup backup = new Backup(context);
                    if (backup.send()) {
                        getActivity().runOnUiThread(() -> {
                            final String summary = "Tap To Make Local Backup\nLast Local Backup Date : "
                                    + new SimpleDateFormat("yyyy - MM - dd  H : m : s : S").format(new Date())
                                    + "\nLast Local Backup File Size : "
                                    + backup.getBackupFile().length() + " Bytes";
                            PreferenceManager.getDefaultSharedPreferences(context).edit()
                                    .putString(GlobalConstants.PREF_LOCAL_BACKUP_DATA, summary)
                                    .apply();
                            localBackupPreference.setSummary(summary);
                            Toast.makeText(getActivity(), "Local Backup Successfull!", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Error: Something went wrong while making Local Backup", Toast.LENGTH_LONG).show();
                        });
                    }
                }).start();
                return false;
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValueBool(findPreference(GlobalConstants.PREF_REPORTS_SWITCH));
            bindPreferenceSummaryToLongValue(findPreference(GlobalConstants.PREF_REPORTS_TIME));
            bindPreferenceSummaryToValueBool(findPreference(GlobalConstants.PREF_REPORTS_REMAINDER_SWITCH));
            bindPreferenceSummaryToLongValue(findPreference(GlobalConstants.PREF_REPORTS_REMAINDER_TIME));
            bindPreferenceSummaryToValueBool(findPreference(GlobalConstants.PREF_MSG_PARSER_SWITCH));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValueBool(findPreference(GlobalConstants.PREF_LOCK_FINGERPRINT));
            bindPreferenceSummaryToValue(findPreference(GlobalConstants.PREF_LOCK_TYPE));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
