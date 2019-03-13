package com.example.allakumarreddy.moneybook.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.allakumarreddy.moneybook.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GraphActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private FrameLayout mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    private int type = 0;
    private String[] label;
    private String[] data;
    private int len;
    private FrameLayout.LayoutParams glp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (FrameLayout) findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        Intent intent = getIntent();
        label = intent.getExtras().getStringArray("label");
        data = intent.getExtras().getStringArray("data");
        type = intent.getExtras().getInt("type");
        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void init() {
        this.len = data.length;
        if (len > 0) {
            switch (this.type) {
                case 0:
                    drawLineGraph();
                    break;

                case 1:
                    drawBarGraph();
                    break;

                case 2:
                    drawPieGraph();
                    break;

                case 3:
                    drawRadarGraph();
                    break;

                case 4:
                    drawScatterGraph();
                    break;

                default:
                    break;
            }
        }
    }

    private void setLayout(Object o) {
        ((View) o).setLayoutParams(glp);
        if (mContentView.getChildCount() > 0)
            mContentView.removeAllViews();
        mContentView.addView((View) o);
    }

    private void drawLineGraph() {
        LineChart chart = new LineChart(this);
        setLayout(chart);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        LineDataSet dataSet = new LineDataSet(entry, "Amount");
        LineData lineData = new LineData(labels, dataSet);
        chart.setData(lineData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void drawBarGraph() {
        BarChart chart = new BarChart(this);
        setLayout(chart);

        ArrayList<BarEntry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            entry.add(new BarEntry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        BarDataSet dataSet = new BarDataSet(entry, "Amount");
        BarData barData = new BarData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void drawPieGraph() {
        PieChart chart = new PieChart(this);
        setLayout(chart);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        PieDataSet dataSet = new PieDataSet(entry, "Amount");
        PieData barData = new PieData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void drawRadarGraph() {
        RadarChart chart = new RadarChart(this);
        setLayout(chart);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        RadarDataSet dataSet = new RadarDataSet(entry, "Amount");
        RadarData barData = new RadarData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void drawScatterGraph() {
        ScatterChart chart = new ScatterChart(this);
        setLayout(chart);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        ScatterDataSet dataSet = new ScatterDataSet(entry, "Amount");
        ScatterData barData = new ScatterData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }
}
