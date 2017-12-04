package com.example.allakumarreddy.moneybook;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.allakumarreddy.moneybook.R.menu.main;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String TAG = "MainActivity";
    private TabHost[] host = new TabHost[4];
    private String[] addDialogDetails = {"", ""};
    private RecyclerView[] mRecyclerView;
    private List<String>[] des, amount, date;
    private MyAdapter[] mAdapter;
    private FrameLayout mainLayout;
    private int currentScreen;
    private int prevScreen = 0;
    private SimpleDateFormat format;
    private DbHandler db;
    private LinearLayout tables;
    private int spinnerSelectedItem = 0;
    private String tablesDate;
    private int spinner2SelectedItem = 0;
    private int spinner3SelectedItem = 0;
    private ScrollView sv;
    private LinearLayout gl;
    private View prevView = null;
    private int spinner4SelectedItem = 0;
    private int spinner5SelectedItem;
    private boolean saveBook=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddDialog(MainActivity.this).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            currentScreen = 0;
            switchScreen();
        } else if (id == R.id.nav_gallery) {
            currentScreen = 1;
            graphs();
            switchScreen();
        } else if (id == R.id.nav_manage) {
            currentScreen = 2;
            showTables();
        }  else if (id == R.id.nav_send) {
            saveBook=true;
            selectTorG();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTables() {
        host[prevScreen].setVisibility(View.INVISIBLE);
        tables.setVisibility(View.VISIBLE);
    }

    private void switchScreen() {
        closeOtherScreens();
        host[prevScreen].setVisibility(View.INVISIBLE);
        host[currentScreen].setVisibility(View.VISIBLE);
        prevScreen = currentScreen;
    }

    private void closeOtherScreens() {
        tables.setVisibility(View.INVISIBLE);
    }

    TabHost.OnTabChangeListener tabChangeListenerGraph = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            LoggerCus.d(TAG, "current tabid is : " + tabId);
            graphs();
        }
    };

    private void home() {
        Date curDate = new Date();
        format = new SimpleDateFormat("yyyy/MM/dd");
        String curDateStr = format.format(curDate);
        tablesDate = curDateStr;

        int[] o = {R.id.my_recycler_view1, R.id.my_recycler_view2, R.id.my_recycler_view3, R.id.my_recycler_view4};
        mRecyclerView = new RecyclerView[4];
        des = new ArrayList[4];
        date = new ArrayList[4];
        amount = new ArrayList[4];
        mAdapter = new MyAdapter[4];
        for (int i = 0; i < 4; i++) {
            mRecyclerView[i] = (RecyclerView) findViewById(o[i]);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView[i].setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView[i].setLayoutManager(mLayoutManager);

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
            mAdapter[i] = new MyAdapter(des[i], amount[i]);
            mRecyclerView[i].setAdapter(mAdapter[i]);
        }
    }

    private void graphs() {
        final int pos = host[currentScreen].getCurrentTab();
        String[] s = new String[des[pos].size()], s1 = new String[des[pos].size()];
        int co = 0;
        if (des[pos].size() > 0) {
            for (String i : des[pos]) {
                s[co] = i;
                co++;
            }
            co = 0;
            for (String i : amount[pos]) {
                s1[co] = i;
                co++;
            }
        }
        int layout = 0;
        switch (pos) {
            case 0:
                layout = R.id.tabg1;
                break;

            case 1:
                layout = R.id.tabg2;
                break;

            case 2:
                layout = R.id.tabg3;
                break;

            case 3:
                layout = R.id.tabg4;
                break;
        }
        new GraphCus(this, 0, s, s1, layout);
    }

    private void init() {
        db = new DbHandler(this);
        mainLayout = (FrameLayout) findViewById(R.id.main);
        createTabs(R.id.tabHost, R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, 0);
        createTabs(R.id.tabHostg, R.id.tabg1, R.id.tabg2, R.id.tabg3, R.id.tabg4, 1);
        host[1].setOnTabChangedListener(tabChangeListenerGraph);
        tables = (LinearLayout) findViewById(R.id.tables);
        tables.setVisibility(View.INVISIBLE);
        home();
        createTables();
        ((Spinner) findViewById(R.id.spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedItem = position;
                LoggerCus.d(TAG, "spinner value : " + position + "");
                selectTorG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Spinner) findViewById(R.id.spinner2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner2SelectedItem = position;
                LoggerCus.d(TAG, "spinner2 value : " + position + "");
                selectTorG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Spinner) findViewById(R.id.spinner3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner3SelectedItem = position;
                LoggerCus.d(TAG, "spinner3 value : " + position + "");
                selectTorG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Spinner) findViewById(R.id.spinner4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner4SelectedItem = position;
                LoggerCus.d(TAG, "spinner4 value : " + position + "");
                selectTorG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Spinner) findViewById(R.id.spinner5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner5SelectedItem = position;
                LoggerCus.d(TAG, "spinner5 value : " + position + "");
                selectTorG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switchScreen();
    }

    protected void showDatePicker(View v) {
        new DatePickerCus(this).show();
    }

    public void afterDate(String s) {
        tablesDate = s;
        selectTorG();
    }

    private void selectTorG() {
        String s = "";
        switch (spinner2SelectedItem) {
            case 0:
                try {
                    s = format.format(format.parse(tablesDate));
                } catch (ParseException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
                break;

            case 1:
                SimpleDateFormat smp = new SimpleDateFormat("yyyy/MM");
                try {
                    s = smp.format(format.parse(tablesDate));
                } catch (ParseException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
                break;

            case 2:
                smp = new SimpleDateFormat("yyyy");
                try {
                    s = smp.format(format.parse(tablesDate));
                } catch (ParseException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
                break;

            case 3:
                s = "";
                break;

        }
        ((EditText) findViewById(R.id.datePicker1)).setText(s);
        if (spinner3SelectedItem == 0)
            processTabs(s);
        else
            processGraphs(s);
    }

    private void processGraphs(String s) {
        tables.removeView(prevView);
        gl = new LinearLayout(this);
        prevView = gl;
        gl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        tables.addView(gl);

        ArrayList<MBRecord> mbr = db.getRecords(s, spinnerSelectedItem);
        if(saveBook)
        {
            saveBook=false;
            new XLStore("MoneyBook",mbr);
        }
        final int len = mbr.size();
        LoggerCus.d(TAG, "length : " + len);
        String[] ds = new String[len], s1 = new String[len];
        for (int j = 0; j < len; j++) {
            if (spinner5SelectedItem == 0)
                ds[j] = mbr.get(j).getDescription();
            else
                ds[j] = format.format(mbr.get(j).getDate());
            s1[j] = mbr.get(j).getAmount() + "";
        }
        new GraphCus(this, spinner4SelectedItem, ds, s1, gl);
    }

    private void processTabs(String s) {
        tables.removeView(prevView);
        sv = new ScrollView(this);
        prevView = sv;
        sv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        TableLayout tableView;
        tableView = new TableLayout(this);
        tableView.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableView.removeAllViews();
        sv.addView(tableView);
        tables.addView(sv);

        TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(tlp);
        tr.setOrientation(LinearLayout.HORIZONTAL);

        TableRow.LayoutParams trp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        TextView des = new TextView(this);
        des.setLayoutParams(trp);
        des.setText("Description");
        des.setTextSize(25f);
        des.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        des.setTextColor(Color.WHITE);

        TextView am = new TextView(this);
        am.setLayoutParams(trp);
        am.setTextSize(25f);
        am.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        am.setTextColor(Color.WHITE);
        am.setText("Amount");

        TextView dt = new TextView(this);
        dt.setLayoutParams(trp);
        dt.setText("Date");
        dt.setTextSize(25f);
        dt.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        dt.setTextColor(Color.WHITE);

        tr.addView(des);
        tr.addView(am);
        tr.addView(dt);

        tableView.addView(tr);

        ArrayList<MBRecord> mbr = db.getRecords(s, spinnerSelectedItem);
        if(saveBook)
        {
            saveBook=false;
            new XLStore("MoneyBook",mbr);
        }
        final int len = mbr.size();
        LoggerCus.d(TAG, "length : " + len);
        for (int j = 0; j < len; j++) {
            tr = new TableRow(this);
            tr.setLayoutParams(tlp);
            tr.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(this);
            tv.setLayoutParams(trp);
            tv.setText(mbr.get(j).getDescription());
            tv.setTextSize(20f);
            tv.setBackground(getDrawable(R.drawable.table_border));
            tr.addView(tv);

            tv = new TextView(this);
            tv.setLayoutParams(trp);
            tv.setText(mbr.get(j).getAmount() + "");
            tv.setTextSize(20f);
            tv.setBackground(getDrawable(R.drawable.table_border));
            tr.addView(tv);

            tv = new TextView(this);
            tv.setLayoutParams(trp);
            tv.setText(format.format(mbr.get(j).getDate()));
            tv.setTextSize(20f);
            tv.setBackground(getDrawable(R.drawable.table_border));
            tr.addView(tv);

            tableView.addView(tr);
        }
    }

    private void createTables() {
        Spinner dropdown = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"Spent", "Earn", "Due", "Loan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown = (Spinner) findViewById(R.id.spinner2);
        items = new String[]{"Day", "Month", "Year", "All"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown = (Spinner) findViewById(R.id.spinner3);
        items = new String[]{"Table", "Graph"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown = (Spinner) findViewById(R.id.spinner4);
        items = new String[]{"Line", "Bar", "Pie", "Radar", "Scatter", "None"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown = (Spinner) findViewById(R.id.spinner5);
        items = new String[]{"Item", "Date"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    private void createTabs(final int hostId, final int t1, final int t2, final int t3, final int t4, final int position) {
        host[position] = (TabHost) findViewById(hostId);
        host[position].setup();

        //Tab 1
        TabHost.TabSpec spec = host[position].newTabSpec("Spent");
        spec.setContent(t1);
        spec.setIndicator("Spent");
        host[position].addTab(spec);

        //Tab 2
        spec = host[position].newTabSpec("Earn");
        spec.setContent(t2);
        spec.setIndicator("Earn");
        host[position].addTab(spec);

        //Tab 3
        spec = host[position].newTabSpec("Due");
        spec.setContent(t3);
        spec.setIndicator("Due");
        host[position].addTab(spec);

        //Tab 4
        spec = host[position].newTabSpec("Loan");
        spec.setContent(t4);
        spec.setIndicator("Loan");
        host[position].addTab(spec);
        host[position].setVisibility(View.INVISIBLE);
    }

    public void afterCallingAddDialog() {
        String[] s = this.getAddDialogDetails();
        if ((s[0] != "") && (s[1] != "")) {
            final int pos = host[currentScreen].getCurrentTab();
            MBRecord mbr = new MBRecord(s[0], Integer.parseInt(s[1]), new Date());
            db.addRecord(mbr, pos);
            addItem(mbr, pos);
        }
    }

    private void addItem(MBRecord mbr, final int position) {
        des[position].add(mbr.getDescription());
        amount[position].add(mbr.getAmount() + "");
        date[position].add(format.format(new Date()));
        mAdapter[position].notifyItemInserted(des[position].size() - 1);
    }

    public String[] getAddDialogDetails() {
        return this.addDialogDetails;
    }

    public void setAddDialogDetails(String[] s) {
        this.addDialogDetails = s.clone();
    }
}