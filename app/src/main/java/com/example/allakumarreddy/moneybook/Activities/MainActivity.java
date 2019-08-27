package com.example.allakumarreddy.moneybook.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.Adapter.DashBoardAdapter;
import com.example.allakumarreddy.moneybook.Adapter.DashboardViewPagerAdapter;
import com.example.allakumarreddy.moneybook.Adapter.MyAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentService;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentServiceHandler;
import com.example.allakumarreddy.moneybook.backup.GoogleDriveBackup;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.home.HomeDueFragment;
import com.example.allakumarreddy.moneybook.home.HomeEarnFragment;
import com.example.allakumarreddy.moneybook.home.HomeLoanFragment;
import com.example.allakumarreddy.moneybook.home.HomeSpentFragment;
import com.example.allakumarreddy.moneybook.home.HomeViewPagerAdapter;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_BACKUP_MAIN_ACTIVITY_OPEN;
import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_IMPORT;
import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DashUIUpdateInterface, HomeAdapterInterface {

    final static String TAG = "MainActivity";
    private static final int REQUESTCODE_PICK_JSON = 504;
    private static final int REQUEST_CODE_SIGN_IN = 200;
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1002;
    public static final int ADD_ACTIVITY = 1001;
    private TabHost host;
    private ListView[] mRecyclerView;
    private List<String>[] des, amount, date;
    private MyAdapter[] mAdapter;
    private int currentScreen = 0;
    private SimpleDateFormat format;
    public DbHandler db;
    private LinearLayout mDashBoard;
    private ListView dashBoardList;
    private DashBoardAdapter dashBoardAdapter;
    private ArrayList<DashBoardRecord> dbr;
    private TextView totalD;
    private TextView[] mTextViewTotal;
    private FrameLayout mainLayout;
    private String tablesDate;
    private GoogleSignInClient mGoogleSignInClient;
    private PreferencesCus sp;
    public File mBackupFile;
    private GoogleDriveBackup gdb;
    private TextView totalM;
    private TextView totalY;
    private View mProgressBAr;
    private ArrayList<MBRecord>[] mbr;
    private String bfrPermissionAction;
    private RecyclerView dashBoardFilterList;
    private ViewPager mViewPager;
    private DashboardViewPagerAdapter mDashBoardViewPagerAdapter;
    private View mHome;
    private ViewPager mHomeViewPager;
    private HomeViewPagerAdapter mHomeViewPagerAdapter;
    private TabLayout tabLayout;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialog();
                //new AddDialog(MainActivity.this, currentScreen).show();
                startAddActivity();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sp = new PreferencesCus(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ((TextView) headerView.findViewById(R.id.mail)).setText(sp.getData(Utils.getEmail()));
        init();
    }

    private void startAddActivity() {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        intent.putExtra("type", currentScreen);
        startActivityForResult(intent, ADD_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
            case R.id.action_test:
                //db.exec();
                //startActivity(new Intent(this, DataBaseActivity.class));
                //signIn();
                test();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void test() {
        Intent intent = new Intent(this, FingerPrintLoginActivity.class);
        intent.putExtra(GlobalConstants.SMART_REMAINDER_NOTI, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        String spentSpan = "Spent : " + 1000 + "\n";
        String earnSpan = "Earn : " + 0 + "\n";
        String dueSpan = "Due : " + 0 + "\n";
        String loanSpan = "Loan : " + 200 + "\n";
        Spannable wordtoSpan = new SpannableString(spentSpan + earnSpan + dueSpan + loanSpan);
        //red #d9534f
        //earn #5bc0de
        //due #f0ad4e
        //loan #FFFF00
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("")), 0, spentSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spentSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), spentSpan.length(), spentSpan.length() + earnSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), spentSpan.length() + earnSpan.length(), spentSpan.length() + earnSpan.length() + dueSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.YELLOW), spentSpan.length() + earnSpan.length() + dueSpan.length(), spentSpan.length() + earnSpan.length() + dueSpan.length() + loanSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        bigText.bigText(wordtoSpan);
        bigText.setSummaryText("Reports By MoneyBook");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelid")
                .setSmallIcon(R.drawable.ic_report)
                .setContentTitle("Daily Report")
                .setContentText("content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.earn))
                .setStyle(bigText)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MoneyBook Channel Name";
            String description = "MoneyBook Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channelid", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1001, builder.build());
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
                            updateUI();
                            break;
                    }
                }
                return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // TODO Auto-generated method stub
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
                            db.addRecords(s.toString());
                            MainActivity.this.runOnUiThread(() -> {
                                updateUI();
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
                    //mDriveClientbackupToDrive();
                } else {
                    LoggerCus.d(TAG, "error in sign in");
                }
                break;

            case (ADD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {

                    afterCallingAddDialog(new String[]{data.getStringExtra("fdes"),
                                    data.getStringExtra("famount"),
                                    data.getStringExtra("fcategory"),
                                    data.getStringExtra("tcategory")},
                            data.getIntExtra("type", -1));
                }
                break;
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        showProgressBar();
        if (id == R.id.dash) {
            showDashBoard();
        } else if (id == R.id.home) {
            showHome();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDashBoard() {
        currentScreen = 1;
        updateUI();
    }

    private void showHome() {
        currentScreen = 0;
        homeUIUpdate();
    }

    private void showProgressBar() {
        mProgressBAr.setVisibility(View.VISIBLE);
    }

    public void switchScreen() {
        closeOtherScreens();
        if (currentScreen == 1) {
            mHome.setVisibility(View.INVISIBLE);
            mDashBoard.setVisibility(View.VISIBLE);
        } else {
            mHome.setVisibility(View.VISIBLE);
            mDashBoard.setVisibility(View.INVISIBLE);
        }
    }

    public void goToAnalytics(String name) {
        startActivity(new Intent(MainActivity.this, AnalyticsActivity.class).putExtra("name", name));
    }

    private void closeOtherScreens() {
        if (mProgressBAr.getVisibility() == View.VISIBLE)
            mProgressBAr.setVisibility(View.GONE);
    }

    private void home() {
        mHome = findViewById(R.id.home);
        mHomeViewPager = (ViewPager) findViewById(R.id.home_pager);
        mHomeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        mHomeViewPager.setAdapter(mHomeViewPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.home_tab_layout);
        tabLayout.setupWithViewPager(mHomeViewPager);
    }

    private void homeUIUpdate() {
        switchScreen();
    }

    private int getTotalAmount(int i) {
        int total = 0;
        for (MBRecord n : mbr[i])
            total += n.getAmount();
        return total;
    }

    private void dashBoard() {
        mDashBoard = (LinearLayout) findViewById(R.id.dashboard);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mDashBoardViewPagerAdapter = new DashboardViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mDashBoardViewPagerAdapter);
    }

    private void performPendingTasks() {
        home();
        dashBoard();

        boolean isSmartRemainderNoti = getIntent().getBooleanExtra(GlobalConstants.SMART_REMAINDER_NOTI, false);
        if (isSmartRemainderNoti) {
            LoggerCus.d(TAG,""+isSmartRemainderNoti);
            showHome();
            startAddActivity();
        }
        int type = -1;
        for (int i = 0; i < GlobalConstants.REPORTS_NOTI.length; i++) {
            if (getIntent().getBooleanExtra(GlobalConstants.REPORTS_NOTI[i], false)) {
                type = i;
                break;
            }
        }
        if (type != -1) {
            LoggerCus.d(TAG,""+type);
            mHomeViewPager.setCurrentItem(type,true);
            showHome();
        }
    }

    private void init() {
        db = new DbHandler(this);
        mainLayout = (FrameLayout) findViewById(R.id.main);
        currentScreen = 1;
        Date curDate = new Date();
        format = new SimpleDateFormat("yyyy/MM/dd");
        String curDateStr = format.format(curDate);
        tablesDate = curDateStr;
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

    private void updateUI() {
        DashboardFragment dashboardFragment = (DashboardFragment) mDashBoardViewPagerAdapter.getRegisteredFragment(0);
        dashboardFragment.dashBoardUIData();

        DashboardFilterFragment dashboardFilterFragment = (DashboardFilterFragment) mDashBoardViewPagerAdapter.getRegisteredFragment(1);
        dashboardFilterFragment.dashBoardUIData();

        switchScreen();
    }

    private void createTabs(final int hostId, final int t1, final int t2, final int t3, final int t4, final int position) {
        host = (TabHost) findViewById(hostId);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Spent");
        spec.setContent(t1);
        spec.setIndicator("Spent");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Earn");
        spec.setContent(t2);
        spec.setIndicator("Earn");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Due");
        spec.setContent(t3);
        spec.setIndicator("Due");
        host.addTab(spec);

        //Tab 4
        spec = host.newTabSpec("Loan");
        spec.setContent(t4);
        spec.setIndicator("Loan");
        host.addTab(spec);
        host.setVisibility(View.INVISIBLE);
    }

    public void afterCallingAddDialog(String[] s, int type) {
        if (this.currentScreen == 0) {
            if ((s[0] != "") && (s[1] != "") && (s[2] != "")) {
                final int pos = mHomeViewPager.getCurrentItem();
                MBRecord mbr = new MBRecord(s[0], Integer.parseInt(s[1]), new Date(), s[2]);
                db.addRecord(mbr, pos);
            }
        } else {
            if (type == 1) {
                DashboardFragment dashboardFragment = (DashboardFragment) mDashBoardViewPagerAdapter.getRegisteredFragment(0);
                dashboardFragment.addCategory(s[0]);
            }
        }
    }

    private void addItem(final int position) {
        mAdapter[position].clear();
        mbr[position] = db.getRecords(tablesDate, position);
        mAdapter[position].addAll(mbr[position]);
        mAdapter[position].notifyDataSetChanged();
        mTextViewTotal[position].setText(Utils.getFormattedNumber(getTotalAmount(position)));
    }

    @Override
    public void onClickItem(MBRecord mbr) {
        startDetailActivity(mbr);
    }

    public void startDetailActivity(MBRecord mbr) {
        Intent intent = new Intent(MainActivity.this, AnalyticsItemDetail.class);
        intent.putExtra("MBRecord", mbr);
        startActivity(intent);
    }
}