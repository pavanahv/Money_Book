package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Adapter.DashBoardAdapter;
import com.example.allakumarreddy.moneybook.Adapter.MyAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentService;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentServiceHandler;
import com.example.allakumarreddy.moneybook.backup.Backup;
import com.example.allakumarreddy.moneybook.backup.GoogleDriveBackup;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.dialog.AddDialog;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String TAG = "MainActivity";
    private static final int REQUESTCODE_PICK_JSON = 504;
    private static final int REQUEST_CODE_SIGN_IN = 200;
    private TabHost host;
    private String[] addDialogDetails = {"", "", ""};
    private ListView[] mRecyclerView;
    private List<String>[] des, amount, date;
    private MyAdapter[] mAdapter;
    private int currentScreen;
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
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private PreferencesCus sp;
    public File mBackupFile;
    private GoogleDriveBackup gdb;
    private TextView totalM;
    private TextView totalY;
    private View mProgressBAr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To check whether it is launched from login activity or not. if not just finish this.
        boolean loginCrt = getIntent().getBooleanExtra("login", false);
        if (!loginCrt)
            finish();

        mProgressBAr = findViewById(R.id.mainlayoutProgressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddDialog(MainActivity.this, currentScreen).show();
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
        init();
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
                new Backup(db, this).send();
                return true;
            case R.id.action_import:
                Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mediaIntent.setType("*/*"); // Set MIME type as per requirement
                startActivityForResult(mediaIntent, REQUESTCODE_PICK_JSON);
                return true;
            case R.id.action_analytics:
                goToAnalytics("0");
                break;
            case R.id.action_msgParser:
                startActivity(new Intent(this, MessagesActivity.class));
                break;
            case R.id.action_test:
                //startActivity(new Intent(this, DataBaseActivity.class));
                db.exec();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
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
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    sp.setData(Utils.getEmail(), GoogleSignIn.getLastSignedInAccount(this).getEmail());
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    gdb = new GoogleDriveBackup(mBackupFile, this, mDriveResourceClient, db.getRecords());
                    gdb.backup();
                } else {
                    LoggerCus.d(TAG, "error in sign in");
                }
                break;
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dash) {
            currentScreen = 1;
            dashBoard();
            switchScreen();
        } else if (id == R.id.home) {
            currentScreen = 0;
            switchScreen();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchScreen() {
        closeOtherScreens();
        if (currentScreen == 1) {
            host.setVisibility(View.INVISIBLE);
            mDashBoard.setVisibility(View.VISIBLE);
        } else {
            host.setVisibility(View.VISIBLE);
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
        Date curDate = new Date();
        format = new SimpleDateFormat("yyyy/MM/dd");
        String curDateStr = format.format(curDate);
        tablesDate = curDateStr;

        mDashBoard = (LinearLayout) findViewById(R.id.dashboard);
        int[] o = {R.id.my_recycler_view1, R.id.my_recycler_view2, R.id.my_recycler_view3, R.id.my_recycler_view4};
        int[] t = {R.id.homespenttotal, R.id.homeearntotal, R.id.homeduetotal, R.id.homeloantotal};
        mRecyclerView = new ListView[4];
        mTextViewTotal = new TextView[4];

        des = new ArrayList[4];
        date = new ArrayList[4];
        amount = new ArrayList[4];
        mAdapter = new MyAdapter[4];
        for (int i = 0; i < 4; i++) {
            mRecyclerView[i] = (ListView) findViewById(o[i]);
            mTextViewTotal[i] = (TextView) findViewById(t[i]);

            ArrayList<MBRecord> mbr = db.getRecords(curDateStr, i);
            final int len = mbr.size();
            LoggerCus.d(TAG, "length : " + len);
            des[i] = new ArrayList();
            date[i] = new ArrayList();
            amount[i] = new ArrayList();
            for (int j = 0; j < len; j++) {
                des[i].add(mbr.get(j).getDescription());
                amount[i].add(mbr.get(j).getAmount() + "");
                date[i].add(mbr.get(j).getDate().toString());
            }
            // specify an adapter
            mAdapter[i] = new MyAdapter(mbr, this, i);
            mRecyclerView[i].setAdapter(mAdapter[i]);
            mTextViewTotal[i].setText(Utils.getFormattedNumber(getTotalAmount(i)));
        }
    }

    private int getTotalAmount(int i) {
        int total = 0;
        for (String n : amount[i])
            total += Integer.parseInt(n);
        return total;
    }

    private void dashBoard() {
        dashBoardList = (ListView) findViewById(R.id.dashboardlist);

        totalD = (TextView) findViewById(R.id.dashboardtotald);
        totalM = (TextView) findViewById(R.id.dashboardtotalm);
        totalY = (TextView) findViewById(R.id.dashboardtotaly);

        ((TextView) findViewById(R.id.dashday)).setText(new SimpleDateFormat("dd").format(new Date()));
        ((TextView) findViewById(R.id.dashmonth)).setText(new SimpleDateFormat("MMM").format(new Date()));
        ((TextView) findViewById(R.id.dashyear)).setText(new SimpleDateFormat("yyyy").format(new Date()));
    }

    private void dashBoardUIData() {
        new Thread(() -> {
            String dayCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneySpentInCurrentDay());
            String monthCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneySpentInCurrentMonth());
            String yearCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneySpentInCurrentYear());
            dbr = db.getDashBoardRecords();
            runOnUiThread(() -> {
                totalD.setText(dayCountTotalHeadText);
                totalM.setText(monthCountTotalHeadText);
                totalY.setText(yearCountTotalHeadText);
                dashBoardAdapter = new DashBoardAdapter(dbr, MainActivity.this);
                dashBoardList.setAdapter(dashBoardAdapter);
                switchScreen();
            });
        }).start();
    }

    private void init() {
        startMsgParserService();
        db = new DbHandler(this);
        mainLayout = (FrameLayout) findViewById(R.id.main);
        createTabs(R.id.tabHost, R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, 0);
        home();
        currentScreen = 1;
        dashBoard();
    }

    private void startMsgParserService() {
        Intent intent = new Intent(MainActivity.this, MoneyBookIntentService.class);
        intent.setAction(ACTION_MSG_PARSE_BY_DATE);
        intent.putExtra(GlobalConstants.HANDLER_NAME, new Messenger(new MoneyBookIntentServiceHandler(msg -> {
            if (msg.what == GlobalConstants.MSG_PARSING_COMPLETED) {
                updateUI();
            }
        })));
        startService(intent);
    }

    private void updateUI() {
        dashBoardUIData();
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

    public void afterCallingAddDialog() {
        String[] s = this.getAddDialogDetails();
        if (this.currentScreen == 0) {
            if ((s[0] != "") && (s[1] != "") && (s[2] != "")) {
                final int pos = host.getCurrentTab();
                MBRecord mbr = new MBRecord(s[0], Integer.parseInt(s[1]), new Date(), s[2]);
                db.addRecord(mbr, pos);
                addItem(mbr, pos);
            }
        } else {
            db.addCategory(s[0]);
            DashBoardRecord temp = db.getDashBoardRecord(s[0]);
            dbr.add(temp);
            dashBoardAdapter.notifyDataSetChanged();
        }
    }

    private void addItem(MBRecord mbr, final int position) {
        des[position].add(mbr.getDescription());
        amount[position].add(mbr.getAmount() + "");
        date[position].add(format.format(new Date()));
        mAdapter[position].add(mbr);
        mAdapter[position].notifyDataSetChanged();
        mTextViewTotal[position].setText(Utils.getFormattedNumber(getTotalAmount(position)));
    }

    public String[] getAddDialogDetails() {
        return this.addDialogDetails;
    }

    public void setAddDialogDetails(String[] s) {
        this.addDialogDetails = s.clone();
    }

    public void signIn() {
        LoggerCus.d(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Build a Google SignIn client.
     */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

}