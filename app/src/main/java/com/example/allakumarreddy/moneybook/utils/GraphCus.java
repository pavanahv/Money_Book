package com.example.allakumarreddy.moneybook.utils;

import android.view.View;
import android.widget.LinearLayout;

import com.example.allakumarreddy.moneybook.Activities.MainActivity;
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
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/24/2017.
 */

public class GraphCus {
    private LinearLayout layout;
    private MainActivity mainActivity;
    private int type;
    private String[] label;
    private String[] data;
    private LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    private LinearLayout graphLayout;
    private int len;
    private boolean layoutType = false;

    public GraphCus(MainActivity mainActivity, int type, String[] labels, String[] data, LinearLayout layout) {
        this.mainActivity = mainActivity;
        this.type = type;
        this.label = labels;
        this.data = data;
        this.layoutType = true;
        this.layout = layout;
        init();
    }

    public GraphCus(MainActivity mainActivity, int type, String[] labels, String[] data, int layout) {
        this.mainActivity = mainActivity;
        this.type = type;
        this.label = labels;
        this.data = data;
        graphLayout = (LinearLayout) this.mainActivity.findViewById(layout);
        if (graphLayout.getChildCount() > 0)
            graphLayout.removeAllViews();
        init();
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

    private void setLayout(View o) {
        o.setLayoutParams(glp);
        if (layoutType) {
            layout.addView(o);
        } else {
            graphLayout.addView(o);
        }
    }

    private void setLayout(BarChart o) {
        o.setLayoutParams(glp);
        if (layoutType) {
            layout.addView(o);
        } else {
            graphLayout.addView(o);
        }
    }

    private void drawLineGraph() {
        LineChart chart = new LineChart(mainActivity);
        setLayout(chart);

        ArrayList<Entry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(i + ""),
                    Float.parseFloat(data[i]),
                    label[i]));
        }
        LineDataSet dataSet = new LineDataSet(entry, "Amount");
        ArrayList<ILineDataSet> list = new ArrayList<>();
        list.add(dataSet);
        LineData lineData = new LineData(list);
        chart.setData(lineData);

        chart.animateXY(2000, 2000);
        chart.getDescription().setEnabled(false);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

    }

    private void drawBarGraph() {
        BarChart chart = new BarChart(mainActivity);
        setLayout(chart);

        chart.animateXY(2000, 2000);
        chart.getDescription().setEnabled(false);

        ArrayList<BarEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new BarEntry(Float.parseFloat(data[i])
                    , Float.parseFloat("" + i)));
        }
        BarDataSet dataSet = new BarDataSet(entry, "Amount");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        ArrayList<IBarDataSet> list = new ArrayList<>();
        list.add(dataSet);
        BarData barData = new BarData(list);
        //chart.setData(barData);
    }

    private void drawPieGraph() {
        PieChart chart = new PieChart(mainActivity);
        setLayout(chart);

        ArrayList<PieEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new PieEntry(Float.parseFloat(data[i]), label[i]));
        }
        PieDataSet dataSet = new PieDataSet(entry, "Amount");
        PieData barData = new PieData(dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void drawRadarGraph() {
        RadarChart chart = new RadarChart(mainActivity);
        setLayout(chart);

        ArrayList<RadarEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new RadarEntry(Float.parseFloat(data[i]), label[i]));
        }
        RadarDataSet dataSet = new RadarDataSet(entry, "Amount");
        ArrayList<IRadarDataSet> list = new ArrayList<>();
        list.add(dataSet);
        RadarData barData = new RadarData(list);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.getDescription().setEnabled(false);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void drawScatterGraph() {
        ScatterChart chart = new ScatterChart(mainActivity);
        setLayout(chart);

        ArrayList<Entry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), Float.parseFloat("" + i), label[i]));
        }
        ScatterDataSet dataSet = new ScatterDataSet(entry, "Amount");
        ArrayList<IScatterDataSet> list = new ArrayList<>();
        list.add(dataSet);
        ScatterData barData = new ScatterData(list);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.getDescription().setEnabled(false);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }
}
