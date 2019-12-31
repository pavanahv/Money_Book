package com.pavanahv.allakumarreddy.moneybook.utils;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;

import java.util.ArrayList;

import static com.pavanahv.allakumarreddy.moneybook.utils.Utils.getColors;

public class GraphUtils {

    public static PieChart drawPieGraph(int len, String[] label, String[] data, Context context, String title) {
        PieChart chart = new PieChart(context);
        chart.getLegend().setEnabled(false);
        ArrayList<PieEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            PieEntry pentry = new PieEntry(Float.parseFloat(data[i]),
                    Utils.getFormattedNumber(Integer.parseInt(data[i])),
                    new String[]{label[i], "" + i});
            pentry.setX(Float.parseFloat("" + i));
            entry.add(pentry);
        }
        PieDataSet dataSet = new PieDataSet(entry, "Amount");
        dataSet.setHighlightEnabled(true);
        PieData barData = new PieData(dataSet);
        barData.setHighlightEnabled(true);
        chart.setData(barData);
        chart.animateXY(2000, 2000);
        dataSet.setColors(getColors());
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                String s = ((String[]) pieEntry.getData())[0];
                int ind = s.indexOf("(");
                if (ind != -1)
                    return s.substring(0, ind);
                else
                    return s;
            }
        });
        chart.setUsePercentValues(true);
        chart.setCenterText(title);
        chart.getDescription().setEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        return chart;
    }

    public static LineChart drawLineGraph(ArrayList<String[]> labels,
                                          ArrayList<String[]> datas, Context context, String title) {

        PreferencesCus pref = new PreferencesCus(context);
        LineChart chart = new LineChart(context);

        chart.getXAxis().setDrawLabels(true);
        /*chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = (int) (value * 100);
                val = val % 100;
                if (val == 0)
                    return label[(int) value];
                else
                    return "";
            }
        });*/

        ArrayList<ILineDataSet> list = new ArrayList<>();

        String[] typeStr = new String[]{"Expenses", "Income", "Dues", "Loans"};
        final int llen = labels.size();
        for (int i = 0; i < llen; i++) {
            ArrayList<Entry> entry = new ArrayList<>();
            String[] label = labels.get(i);
            String[] data = datas.get(i);
            final int len = label.length;
            for (int j = 0; j < len; j++) {
                entry.add(new Entry(Float.parseFloat(j + ""),
                        Float.parseFloat(data[j]),
                        new String[]{label[j], "" + j}));
            }
            LineDataSet dataSet = new LineDataSet(entry, typeStr[i]);
            int tempColor = Utils.getColorPref(i, pref);
            dataSet.setColor(tempColor);
            dataSet.setDrawValues(false);
            dataSet.setCircleColor(tempColor);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(tempColor);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            list.add(dataSet);
        }

        LineData lineData = new LineData(list);
        chart.setData(lineData);
        chart.getXAxis().setEnabled(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        CusMarkerView mv = new CusMarkerView(context, R.layout.marker_layout);
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setText(title);
        chart.setDrawGridBackground(false);
        return chart;
    }

    public static LineChart drawLineGraph(String[] label,
                                          String[] data, Context context, String title,
                                          int type) {

        PreferencesCus pref = new PreferencesCus(context);
        LineChart chart = new LineChart(context);
        chart.getXAxis().setDrawLabels(true);
        ArrayList<ILineDataSet> list = new ArrayList<>();
        String[] typeStr = new String[]{"Expenses", "Income", "Dues", "Loans"};
        ArrayList<Entry> entry = new ArrayList<>();
        final int len = label.length;
        for (int j = 0; j < len; j++) {
            entry.add(new Entry(Float.parseFloat(j + ""),
                    Float.parseFloat(data[j]),
                    new String[]{label[j], "" + j}));
        }

        LineDataSet dataSet = new LineDataSet(entry, typeStr[type]);
        int tempColor = Utils.getColorPref(type, pref);
        dataSet.setColor(tempColor);
        dataSet.setDrawValues(false);
        dataSet.setCircleColor(tempColor);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(tempColor);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        list.add(dataSet);

        LineData lineData = new LineData(list);
        chart.setData(lineData);
        chart.getXAxis().setEnabled(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        CusMarkerView mv = new CusMarkerView(context, R.layout.marker_layout);
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setText(title);
        chart.setDrawGridBackground(false);
        return chart;
    }

    public static BarChart drawStackedBarGraph(ArrayList<String> labelList,
                                               ArrayList<Integer>[] catMaps,
                                               ArrayList<String> data,
                                               Context context, String title,
                                               float limit, String budgetString) {

        BarChart chart = new BarChart(context);
        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = (int) (value * 100);
                val = val % 100;
                if (val == 0)
                    return data.get((int) value);
                else
                    return "";
            }
        });
        ArrayList<IBarDataSet> list = new ArrayList<>();
        ArrayList<BarEntry> entry = new ArrayList<>();
        final int catLen = labelList.size();
        final int len = catMaps[0].size();
        for (int j = 0; j < len; j++) {
            float yVals[] = new float[catLen];
            for (int i = 0; i < catLen; i++) {
                yVals[i] = (float) catMaps[i].get(j);
            }
            entry.add(new BarEntry(Float.parseFloat(j + ""),
                    yVals,
                    new String[]{data.get(j), "" + j}));
        }

        String labels[] = new String[labelList.size()];
        for (int i = 0; i < labels.length; i++)
            labels[i] = labelList.get(i);

        BarDataSet dataSet = new BarDataSet(entry, "Categories");
        dataSet.setDrawValues(false);
        dataSet.setColors(getColors(labels.length));
        dataSet.setStackLabels(labels);
        list.add(dataSet);

        LimitLine ll1 = new LimitLine(limit, "Budget Limit " + budgetString);
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        chart.getAxisLeft().addLimitLine(ll1);

        BarData lineData = new BarData(list);
        lineData.setValueTextColor(Color.WHITE);
        chart.setData(lineData);
        XAxis xLabels = chart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        CusMarkerView mv = new CusMarkerView(context, R.layout.marker_layout);
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setText(title);
        chart.setDrawGridBackground(false);
        return chart;
    }
}
