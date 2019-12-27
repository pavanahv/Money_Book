package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.pavanahv.allakumarreddy.moneybook.Services.MoneyBookIntentService;
import com.pavanahv.allakumarreddy.moneybook.fragments.DashboardFilterFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.DashboardFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.HomeFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.PaymentMethodFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.ReportsFragment;
import com.pavanahv.allakumarreddy.moneybook.handler.MoneyBookIntentServiceHandler;
import com.pavanahv.allakumarreddy.moneybook.interfaces.DashUIUpdateInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AnimationUtils;
import com.pavanahv.allakumarreddy.moneybook.utils.Backup;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_BACKUP_MAIN_ACTIVITY_OPEN;
import static com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_IMPORT;
import static com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, DashUIUpdateInterface {

    final static String TAG = "MainActivity";
    private static final int REQUESTCODE_PICK_JSON = 504;
    private static final int REQUEST_CODE_SIGN_IN = 200;
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1002;
    public static final int ADD_ACTIVITY = 1001;
    private static final String LOCAL_BACKUP = "LOCAL_BACKUP";
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_CATEGORY = 1;
    private static final int FRAGMENT_PAYMENT_METHOD = 2;
    private static final int FRAGMENT_GRAPHS = 3;
    private static final int FRAGMENT_REPORTS = 4;
    private int currentScreen = 0;
    private View mProgressBAr;
    private String bfrPermissionAction;
    private int mainLayout;
    private boolean closeActivity = false;
    private View mainView;
    private View main;
    private View calenderLayout;
    private CalendarView calender;
    private int currentFragmentIndex = -1;
    private PreferencesCus preferencesCus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To check whether it is launched from login activity or not. if not just finish this.
        boolean loginCrt = getIntent().getBooleanExtra(GlobalConstants.LOGIN_CHECK, false);
        if (!loginCrt)
            finish();

        mProgressBAr = findViewById(R.id.mainlayoutProgressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PreferencesCus sp = new PreferencesCus(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ((TextView) headerView.findViewById(R.id.mail)).setText(sp.getData(Utils.getEmail()));
        init();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!closeActivity) {
                closeActivity = true;
                Toast.makeText(this, "Press Again To Exit", Toast.LENGTH_LONG).show();
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        closeActivity = false;
                    } catch (InterruptedException e) {
                        LoggerCus.d(TAG, "Error while making back press thread to sleep -> " + e.getMessage());
                    }
                }).start();
                return;
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_calender:
                showCalenderView();
                break;
            case R.id.action_backup:
                //backup(true);
                backupToGoogleDrive();
                return true;
            /*case R.id.action_import:
                importAction();
                return true;*/
            case R.id.action_analytics:
                goToAnalytics("0");
                break;
            case R.id.action_msgParser:
                startActivity(new Intent(this, MessagesActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;

            case R.id.action_auto_add:
                startActivity(new Intent(this, AutoAddActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            /*case R.id.action_test:
                //db.exec();
                //startActivity(new Intent(this, DataBaseActivity.class));
                //signIn();
                test();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void test() {
        startActivity(new Intent(this, CreateSmartPinActivity.class));
    }

    private void backupToGoogleDrive() {
        Intent intent = new Intent(MainActivity.this, BackupToGoogleDriveService.class);
        intent.setAction(GlobalConstants.BACKUP_TO_GOOGLE_DRIVE);
        startService(intent);
    }

    private void importAction() {
        boolean perm = Utils.checkReadWriteStoragePermissions(this);
        if (perm) {
            Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
            mediaIntent.setType("*/*"); // Set MIME type as per requirement
            startActivityForResult(mediaIntent, REQUESTCODE_PICK_JSON);
        } else {
            bfrPermissionAction = ACTION_IMPORT;
            requestPermissionForReadWriteStorage();
        }
    }

    private void backup(boolean typeActivate) {
        boolean perm = Utils.checkReadWriteStoragePermissions(this);
        Intent intent = new Intent(MainActivity.this, MoneyBookIntentService.class);
        if (typeActivate) {
            if (perm) {
                intent.setAction(GlobalConstants.ACTION_BACKUP_MAIN_ACTIVITY_OPEN);
                intent.putExtra(GlobalConstants.HANDLER_NAME, new Messenger(new MoneyBookIntentServiceHandler(msg -> {
                    if (msg.what == GlobalConstants.BACKUP_COMPLETED) {
                        if (msg.arg1 == 0) {
                            Toast.makeText(MainActivity.this, "Failed To Backup !\nSomething Went Wrong", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Backup Successful !", Toast.LENGTH_LONG).show();
                        }
                    }
                })));
            } else {
                bfrPermissionAction = ACTION_BACKUP_MAIN_ACTIVITY_OPEN;
                requestPermissionForReadWriteStorage();
            }
        } else {
            if (perm)
                intent.setAction(GlobalConstants.ACTION_BACKUP);
            else
                return;
        }
        startService(intent);
    }

    private void requestPermissionForReadWriteStorage() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (bfrPermissionAction) {
                        case ACTION_BACKUP_MAIN_ACTIVITY_OPEN:
                            //backup(true);
                            backupToGoogleDrive();
                            break;
                        case ACTION_IMPORT:
                            importAction();
                            break;
                        case LOCAL_BACKUP:
                            makeLocalBackup();
                            break;
                    }
                } else {
                    switch (bfrPermissionAction) {
                        case ACTION_BACKUP_MAIN_ACTIVITY_OPEN:
                            Toast.makeText(this, "Not given permission to backup. Try again", Toast.LENGTH_LONG).show();
                            break;
                        case ACTION_IMPORT:
                            Toast.makeText(this, "Not given permission to import. Try again", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_READ_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (bfrPermissionAction) {
                        case ACTION_MSG_PARSE_BY_DATE:
                            startMsgParserService();
                            break;
                    }
                } else {
                    switch (bfrPermissionAction) {
                        case ACTION_MSG_PARSE_BY_DATE:
                            Toast.makeText(this, "Message parser will not work. Try again", Toast.LENGTH_LONG).show();
                            showCurrentFragment();
                            break;
                    }
                }
                return;
        }
    }

    private void showCurrentFragment() {
        showFragment(currentFragmentIndex, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_PICK_JSON:
                if (resultCode == Activity.RESULT_OK) {
                    new Thread(() -> {
                        Uri uri = data.getData();
                        StringBuilder s = new StringBuilder();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            if (inputStream.available() != 0) {
                                for (int i; (i = inputStream.read()) != -1; ) {
                                    s.append((char) i);
                                }
                            }
                            DbHandler db = new DbHandler(this);
                            db.addRecords(s.toString());
                            MainActivity.this.runOnUiThread(() -> {
                                showCurrentFragment();
                            });
                        } catch (FileNotFoundException e) {
                            LoggerCus.d(TAG, e.getMessage());
                        } catch (IOException e) {
                            LoggerCus.d(TAG, e.getMessage());
                        }
                    }).start();
                }
                break;

            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    LoggerCus.d(TAG, "Signed in successfully.");
                } else {
                    LoggerCus.d(TAG, "error in sign in");
                }
                break;

        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            default:
            case R.id.home:
                showFragment(FRAGMENT_HOME, true);
                break;

            case R.id.dash:
                showFragment(FRAGMENT_CATEGORY, true);
                break;

            case R.id.payment_method:
                showFragment(FRAGMENT_PAYMENT_METHOD, true);
                break;

            case R.id.graphs:
                showFragment(FRAGMENT_GRAPHS, true);
                break;

            case R.id.reports:
                showFragment(FRAGMENT_REPORTS, true);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showReports() {
        showReportsActionBar();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainLayout, new ReportsFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    private void showGraphs() {
        showGraphsActionBar();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainLayout, new DashboardFilterFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        showCurrentFragment();
        super.onConfigurationChanged(newConfig);
    }

    private void showGraphsActionBar() {
        getSupportActionBar().setTitle("Graphs");
    }

    private void showReportsActionBar() {
        getSupportActionBar().setTitle("Reports");
    }

    private void showPaymentMethod() {
        showPaymentMethodActionBar();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainLayout, new PaymentMethodFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void showPaymentMethodActionBar() {
        getSupportActionBar().setTitle("Payment Method");
    }

    private void showCategory() {
        showCategoryActionBar();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainLayout, new DashboardFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void showCategoryActionBar() {
        getSupportActionBar().setTitle("Category");
    }

    private void showHome() {
        showHomeActionBar();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainLayout, new HomeFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void showFragment(int index, boolean isDeleteCurrDate) {
        if (isDeleteCurrDate) {
            if (currentFragmentIndex == FRAGMENT_HOME)
                preferencesCus.setCurrentDate(new Date().getTime());
        }
        currentFragmentIndex = index;
        switch (index) {
            case FRAGMENT_HOME:
                showHome();
                break;

            case FRAGMENT_CATEGORY:
                showCategory();
                break;

            case FRAGMENT_PAYMENT_METHOD:
                showPaymentMethod();
                break;

            case FRAGMENT_GRAPHS:
                showGraphs();
                break;

            case FRAGMENT_REPORTS:
                showReports();
                break;
        }
    }

    private void showProgressBar() {
        mProgressBAr.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        AnimationUtils.revealAnimation(mProgressBAr, mainView, mainView);
    }

    public void goToAnalytics(String name) {
        startActivity(new Intent(MainActivity.this, AnalyticsActivity.class).putExtra("name", name));
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    private void showHomeActionBar() {
        getSupportActionBar().setTitle("Home");
    }

    private void performPendingTasks() {

        Intent fwdIntent = getIntent();
        boolean isShown = false;
        boolean isSmartRemainderNoti = fwdIntent.getBooleanExtra(GlobalConstants.SMART_REMAINDER_NOTI, false);
        if (isSmartRemainderNoti) {
            isShown = true;
            LoggerCus.d(TAG, "" + isSmartRemainderNoti);
            showFragment(FRAGMENT_HOME, true);
        }

        if (fwdIntent.getBooleanExtra(GlobalConstants.REPORTS_NOTI, false)) {
            isShown = true;
            showFragment(FRAGMENT_REPORTS, true);
        }
        if (!isShown)
            showFragment(FRAGMENT_HOME, true);
        makeLocalBackup();
        hideProgressBar();

        if (fwdIntent.getBooleanExtra(GlobalConstants.MSG_PARSER_NOTI, false)) {
            MBRecord mbr = ((MBRecord) fwdIntent.getSerializableExtra(GlobalConstants.MBRECORD));
            LoggerCus.d(TAG, mbr.toString());
            startDetailActivity(mbr);
        }
    }

    private void makeLocalBackup() {
        if (!Utils.checkReadWriteStoragePermissions(this)) {
            bfrPermissionAction = LOCAL_BACKUP;
            requestPermissionForReadWriteStorage();
            return;
        }
        new Thread(() -> {
            Backup backup = new Backup(MainActivity.this);
            if (backup.send()) {
                runOnUiThread(() -> {
                    final String summary = "Tap To Make Local Backup\nLast Local Backup Date : "
                            + new SimpleDateFormat("yyyy - MM - dd  H : m : s : S").format(new Date())
                            + "\nLast Local Backup File Size : "
                            + backup.getBackupFile().length() + " Bytes";
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                            .putString(GlobalConstants.PREF_LOCAL_BACKUP_DATA, summary)
                            .apply();
                });
            }
        }).start();
    }

    private void init() {
        preferencesCus = new PreferencesCus(this);
        preferencesCus.setCurrentDate(new Date().getTime());
        mainLayout = R.id.main;
        mainView = findViewById(R.id.main_view);
        main = findViewById(R.id.main);
        calenderLayout = findViewById(R.id.calender_layout);
        calender = (CalendarView) findViewById(R.id.calendarView);
        showProgressBar();
        startMsgParserService();
    }

    private void startMsgParserService() {
        boolean perm = Utils.checkReadSMSPermissions(this);
        if (perm) {
            Intent intent = new Intent(MainActivity.this, MoneyBookIntentService.class);
            intent.setAction(ACTION_MSG_PARSE_BY_DATE);
            intent.putExtra(GlobalConstants.HANDLER_NAME, new Messenger(new MoneyBookIntentServiceHandler(msg -> {
                if (msg.what == GlobalConstants.MSG_PARSING_COMPLETED) {
                    performPendingTasks();
                }
            })));
            startService(intent);
        } else {
            bfrPermissionAction = ACTION_MSG_PARSE_BY_DATE;
            requestPermissionForReadSMS();
        }
    }

    private void requestPermissionForReadSMS() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_READ_SMS);
    }

    @Override
    public void switchScreen() {

    }

    private void showCalenderView() {
        Date tempDate = preferencesCus.getCurrentDate();
        if (tempDate != null)
            calender.setDate(tempDate.getTime());
        main.setVisibility(View.GONE);
        calenderLayout.setVisibility(View.VISIBLE);
        calender.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            hideCalenderView(cal);
        });
    }

    private void hideCalenderView(Calendar cal) {
        if (cal != null)
            preferencesCus.setCurrentDate(cal.getTimeInMillis());
        calenderLayout.setVisibility(View.GONE);
        main.setVisibility(View.VISIBLE);
        showCurrentFragment();
    }

    public void calenderLayoutClick(View view) {
        hideCalenderView(null);
    }

    public void startDetailActivity(MBRecord mbr) {
        int mType = mbr.getType();
        if (mType == GlobalConstants.TYPE_DUE || mType == GlobalConstants.TYPE_LOAN) {
            Intent intent = new Intent(MainActivity.this, RePaymentActivity.class);
            intent.putExtra("MBRecord", mbr);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, AnalyticsItemDetail.class);
            mbr.setType(mType);
            intent.putExtra("MBRecord", mbr);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

}