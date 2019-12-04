package com.example.allakumarreddy.moneybook.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentService;
import com.example.allakumarreddy.moneybook.fragments.DashboardFilterFragment;
import com.example.allakumarreddy.moneybook.fragments.DashboardFragment;
import com.example.allakumarreddy.moneybook.fragments.HomeFragment;
import com.example.allakumarreddy.moneybook.fragments.PaymentMethodFragment;
import com.example.allakumarreddy.moneybook.handler.MoneyBookIntentServiceHandler;
import com.example.allakumarreddy.moneybook.interfaces.DashUIUpdateInterface;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.AnimationUtils;
import com.example.allakumarreddy.moneybook.utils.Backup;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_BACKUP_MAIN_ACTIVITY_OPEN;
import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_IMPORT;
import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DashUIUpdateInterface {

    final static String TAG = "MainActivity";
    private static final int REQUESTCODE_PICK_JSON = 504;
    private static final int REQUEST_CODE_SIGN_IN = 200;
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1002;
    public static final int ADD_ACTIVITY = 1001;
    private static final String LOCAL_BACKUP = "LOCAL_BACKUP";
    private int currentScreen = 0;
    private View mProgressBAr;
    private String bfrPermissionAction;
    private int mainLayout;
    private boolean closeActivity = false;
    private View main;

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
            case R.id.action_backup:
                //backup(true);
                backupToGoogleDrive();
                return true;
            case R.id.action_import:
                importAction();
                return true;
            case R.id.action_analytics:
                goToAnalytics("0");
                break;
            case R.id.action_msgParser:
                startActivity(new Intent(this, MessagesActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_auto_add:
                startActivity(new Intent(this, AutoAddActivity.class));
                break;
            /*case R.id.action_test:
                //db.exec();
                //startActivity(new Intent(this, DataBaseActivity.class));
                //signIn();
                //test();
                AutoAddManager am = new AutoAddManager(this);
                am.process();
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

        //showProgressBar();
        if (id == R.id.dash) {
            showCategory();
        } else if (id == R.id.home) {
            showHome();
        } else if (id == R.id.payment_method) {
            showPaymentMethod();
        } else if (id == R.id.graphs) {
            showGraphs();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showGraphs() {
        showGraphsActionBar();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainLayout, new DashboardFilterFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    private void showGraphsActionBar() {
        getSupportActionBar().setTitle("Graphs");
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

    private void showProgressBar() {
        mProgressBAr.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        AnimationUtils.revealAnimation(mProgressBAr, main, main);
    }

    public void goToAnalytics(String name) {
        startActivity(new Intent(MainActivity.this, AnalyticsActivity.class).putExtra("name", name));
    }

    private void showHomeActionBar() {
        getSupportActionBar().setTitle("Home");
    }

    private void performPendingTasks() {

        boolean isShown = false;
        boolean isSmartRemainderNoti = getIntent().getBooleanExtra(GlobalConstants.SMART_REMAINDER_NOTI, false);
        if (isSmartRemainderNoti) {
            isShown = true;
            LoggerCus.d(TAG, "" + isSmartRemainderNoti);
            showHome();
        }
        int type = -1;
        for (int i = 0; i < GlobalConstants.REPORTS_NOTI.length; i++) {
            if (getIntent().getBooleanExtra(GlobalConstants.REPORTS_NOTI[i], false)) {
                type = i;
                break;
            }
        }
        if (type != -1) {
            isShown = true;
            LoggerCus.d(TAG, "" + type);
            //mHomeViewPager.setCurrentItem(type, false);
            showHome();
        }
        if (!isShown)
            showHome();
        makeLocalBackup();
        hideProgressBar();
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
        mainLayout = R.id.main;
        main = findViewById(mainLayout);
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
}