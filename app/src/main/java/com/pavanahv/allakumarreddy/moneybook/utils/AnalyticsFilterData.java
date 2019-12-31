package com.pavanahv.allakumarreddy.moneybook.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AnalyticsFilterData implements Serializable {

    private static final String TAG = AnalyticsFilterData.class.getSimpleName();
    public String[] mainMenuData = new String[]{"Date", "Date Interval", "Money Type", "Group By", "Sort By", "Sorting Order", "Category", "Payment Method", "View By", "Graph Type", "Saved Filters"};
    public String[] subMenuDateData = new String[]{"Current Day", "Current Month", "Current Year", "Custom", "All"};
    public boolean[] subMenuDateDataBool = new boolean[]{false, false, true, false, false};
    public String[] subMenuMoneyTypeData = new String[]{"Spent", "Earn", "Due", "Loan", "Money Transfer"};
    public boolean[] subMenuMoneyTypeDataBool = new boolean[]{true, false, false, false, false};
    public String[] subMenuDateIntervalData = new String[]{"Day", "Month", "Year"};
    public boolean[] subMenuDateIntervalDataBool = new boolean[]{true, false, false};
    public String[] subMenuViewByData = new String[]{"Graph", "List"};
    public boolean[] subMenuViewByDataBool = new boolean[]{false, true};
    public String[] subMenuGraphTypeData = new String[]{"Line", "Bar", "Pie", "Radar", "Scatter"};
    public boolean[] subMenuGraphTypeDataBool = new boolean[]{true, false, false, false, false};
    public String[] subMenuGroupByData = new String[]{"Item", "Date", "Categories", "Payment Method", "None"};
    public boolean[] subMenuGroupByDataBool = new boolean[]{false, false, false, false, true};
    public String[] subMenuSortByData = new String[]{"Date", "Price", "Item"};
    public boolean[] subMenuSortByDataBool = new boolean[]{true, false, false};
    public String[] subMenuSortingOrderData = new String[]{"Increasing", "Decreasing"};
    public boolean[] subMenuSortingOrderDataBool = new boolean[]{false, true};
    public String[] subMenuCatogeoryData = new String[]{"All"};
    public boolean[] subMenuCatogeoryDataBool = new boolean[]{true};
    public String[] subMenuPaymentMethodData = new String[]{"All"};
    public boolean[] subMenuPaymentMethodDataBool = new boolean[]{true};
    public String[] subMenuFilterData = new String[]{"No Filters"};
    public boolean[] subMenuFilterDataBool = new boolean[]{false};
    public String queryText = "";
    public Date sDate = new Date();
    public Date eDate = new Date();
    public String[][] filters;

    public AnalyticsFilterData clone() {
        AnalyticsFilterData analyticsFilterData = new AnalyticsFilterData();
        analyticsFilterData.mainMenuData = Arrays.copyOf(mainMenuData, mainMenuData.length);

        analyticsFilterData.subMenuDateData = Arrays.copyOf(subMenuDateData, subMenuDateData.length);
        analyticsFilterData.subMenuDateDataBool = Arrays.copyOf(subMenuDateDataBool, subMenuDateDataBool.length);

        analyticsFilterData.subMenuMoneyTypeData = Arrays.copyOf(subMenuMoneyTypeData, subMenuMoneyTypeData.length);
        analyticsFilterData.subMenuMoneyTypeDataBool = Arrays.copyOf(subMenuMoneyTypeDataBool, subMenuMoneyTypeDataBool.length);

        analyticsFilterData.subMenuDateIntervalData = Arrays.copyOf(subMenuDateIntervalData, subMenuDateIntervalData.length);
        analyticsFilterData.subMenuDateIntervalDataBool = Arrays.copyOf(subMenuDateIntervalDataBool, subMenuDateIntervalDataBool.length);

        analyticsFilterData.subMenuViewByData = Arrays.copyOf(subMenuViewByData, subMenuViewByData.length);
        analyticsFilterData.subMenuViewByDataBool = Arrays.copyOf(subMenuViewByDataBool, subMenuViewByDataBool.length);

        analyticsFilterData.subMenuGraphTypeData = Arrays.copyOf(subMenuGraphTypeData, subMenuGraphTypeData.length);
        analyticsFilterData.subMenuGraphTypeDataBool = Arrays.copyOf(subMenuGraphTypeDataBool, mainMenuData.length);

        analyticsFilterData.subMenuGroupByData = Arrays.copyOf(subMenuGroupByData, subMenuGroupByData.length);
        analyticsFilterData.subMenuGroupByDataBool = Arrays.copyOf(subMenuGroupByDataBool, subMenuGroupByDataBool.length);

        analyticsFilterData.subMenuSortByData = Arrays.copyOf(subMenuSortByData, subMenuSortByData.length);
        analyticsFilterData.subMenuSortByDataBool = Arrays.copyOf(subMenuSortByDataBool, subMenuSortByDataBool.length);

        analyticsFilterData.subMenuSortingOrderData = Arrays.copyOf(subMenuSortingOrderData, subMenuSortingOrderData.length);
        analyticsFilterData.subMenuSortingOrderDataBool = Arrays.copyOf(subMenuSortingOrderDataBool, subMenuSortingOrderDataBool.length);

        analyticsFilterData.subMenuCatogeoryData = Arrays.copyOf(subMenuCatogeoryData, subMenuCatogeoryData.length);
        analyticsFilterData.subMenuCatogeoryDataBool = Arrays.copyOf(subMenuCatogeoryDataBool, subMenuCatogeoryDataBool.length);

        analyticsFilterData.subMenuPaymentMethodData = Arrays.copyOf(subMenuPaymentMethodData, subMenuPaymentMethodData.length);
        analyticsFilterData.subMenuPaymentMethodDataBool = Arrays.copyOf(subMenuPaymentMethodDataBool, subMenuPaymentMethodDataBool.length);

        analyticsFilterData.subMenuFilterData = Arrays.copyOf(subMenuFilterData, subMenuFilterData.length);
        analyticsFilterData.subMenuFilterDataBool = Arrays.copyOf(subMenuFilterDataBool, subMenuFilterDataBool.length);

        analyticsFilterData.queryText = queryText + "";

        analyticsFilterData.sDate = new Date(sDate.getTime());
        analyticsFilterData.eDate = new Date(eDate.getTime());

        if (filters.length > 0) {
            analyticsFilterData.filters = new String[filters.length][filters[0].length];
            for (int i = 0; i < filters.length; i++) {
                analyticsFilterData.filters[i] = Arrays.copyOf(filters[i], filters[i].length);
            }
        }
        return analyticsFilterData;
    }

    public void initDataAgainAfterClear() {
        subMenuDateDataBool = new boolean[]{false, false, true, false, false};
        subMenuMoneyTypeDataBool = new boolean[]{true, false, false, false, false};
        subMenuDateIntervalDataBool = new boolean[]{true, false, false};
        subMenuViewByDataBool = new boolean[]{false, true};
        subMenuGraphTypeDataBool = new boolean[]{true, false, false, false, false};
        subMenuGroupByDataBool = new boolean[]{false, false, false, false, true};
        subMenuSortByDataBool = new boolean[]{true, false, false};
        subMenuSortingOrderDataBool = new boolean[]{false, true};
        subMenuCatogeoryDataBool = new boolean[]{true};
        subMenuPaymentMethodDataBool = new boolean[]{true};
        subMenuFilterDataBool = new boolean[]{false};
        queryText = "";
    }

    @Override
    public String toString() {
        return "AnalyticsFilterData{" +
                "mainMenuData=" + Arrays.toString(mainMenuData) +
                ",\n subMenuDateData=" + Arrays.toString(subMenuDateData) +
                ",\n subMenuDateDataBool=" + Arrays.toString(subMenuDateDataBool) +
                ",\n subMenuMoneyTypeData=" + Arrays.toString(subMenuMoneyTypeData) +
                ",\n subMenuMoneyTypeDataBool=" + Arrays.toString(subMenuMoneyTypeDataBool) +
                ",\n subMenuDateIntervalData=" + Arrays.toString(subMenuDateIntervalData) +
                ",\n subMenuDateIntervalDataBool=" + Arrays.toString(subMenuDateIntervalDataBool) +
                ",\n subMenuViewByData=" + Arrays.toString(subMenuViewByData) +
                ",\n subMenuViewByDataBool=" + Arrays.toString(subMenuViewByDataBool) +
                ",\n subMenuGraphTypeData=" + Arrays.toString(subMenuGraphTypeData) +
                ",\n subMenuGraphTypeDataBool=" + Arrays.toString(subMenuGraphTypeDataBool) +
                ",\n subMenuGroupByData=" + Arrays.toString(subMenuGroupByData) +
                ",\n subMenuGroupByDataBool=" + Arrays.toString(subMenuGroupByDataBool) +
                ",\n subMenuSortByData=" + Arrays.toString(subMenuSortByData) +
                ",\n subMenuSortByDataBool=" + Arrays.toString(subMenuSortByDataBool) +
                ",\n subMenuSortingOrderData=" + Arrays.toString(subMenuSortingOrderData) +
                ",\n subMenuSortingOrderDataBool=" + Arrays.toString(subMenuSortingOrderDataBool) +
                ",\n subMenuCatogeoryData=" + Arrays.toString(subMenuCatogeoryData) +
                ",\n subMenuCatogeoryDataBool=" + Arrays.toString(subMenuCatogeoryDataBool) +
                ",\n subMenuPaymentMethodData=" + Arrays.toString(subMenuPaymentMethodData) +
                ",\n subMenuPaymentMethodDataBool=" + Arrays.toString(subMenuPaymentMethodDataBool) +
                ",\n subMenuFilterData=" + Arrays.toString(subMenuFilterData) +
                ",\n subMenuFilterDataBool=" + Arrays.toString(subMenuFilterDataBool) +
                ",\n queryText='" + queryText + '\'' +
                ",\n sDate=" + sDate +
                ",\n eDate=" + eDate +
                ",\n filters=" + Arrays.toString(filters) +
                '}';
    }

    public String getParcelableJSONStringForFilter() {

        // creating dateinterval var
        int dateInterval = -1;
        for (int i = 0; i < subMenuDateIntervalDataBool.length; i++) {
            if (subMenuDateIntervalDataBool[i]) {
                dateInterval = i;
                break;
            }
        }

        // initializing group by var
        int groupBy = -1;
        if (!subMenuGroupByDataBool[4]) {
            for (int i = 0; i < subMenuGroupByDataBool.length - 1; i++) {
                if (subMenuGroupByDataBool[i]) {
                    groupBy = i;
                    break;
                }
            }
        }

        int sortBy = -1;
        for (int i = 0; i < subMenuSortByDataBool.length; i++) {
            if (subMenuSortByDataBool[i]) {
                sortBy = i;
                break;
            }
        }

        // intializing sorting order var
        int sortingOrder = -1;
        for (int i = 0; i < subMenuSortingOrderDataBool.length; i++) {
            if (subMenuSortingOrderDataBool[i]) {
                sortingOrder = i;
                break;
            }
        }

        // intializing money type var
        int moneyType = -1;
        for (int i = 0; i < subMenuMoneyTypeDataBool.length; i++) {
            if (subMenuMoneyTypeDataBool[i]) {
                moneyType = i;
                break;
            }
        }


        int graphType = -1;
        for (int i = 0; i < subMenuGraphTypeDataBool.length; i++) {
            if (subMenuGraphTypeDataBool[i]) {
                graphType = i;
                break;
            }
        }

        JSONObject jobj = new JSONObject();
        try {
            jobj.put("queryText", queryText);
            jobj.put("dateAll", subMenuDateDataBool[4]);
            SimpleDateFormat jformat = new SimpleDateFormat("yyyy/MM/dd");
            jobj.put("sDate", jformat.format(sDate));
            jobj.put("eDate", jformat.format(eDate));

            JSONArray jarrDateBool = new JSONArray();
            for (int i = 0; i < subMenuDateDataBool.length; i++) {
                JSONObject jobjSub = new JSONObject();
                jobjSub.put("element" + i, subMenuDateDataBool[i]);
                jarrDateBool.put(jobjSub);
            }
            jobj.put("dateDataBool", jarrDateBool);

            jobj.put("moneyType", moneyType);
            jobj.put("dateInterval", dateInterval);
            jobj.put("groupByNone", subMenuGroupByDataBool[4]);
            jobj.put("groupBy", groupBy);
            jobj.put("sortBy", sortBy);
            jobj.put("sortingOrder", sortingOrder);

            JSONArray jarrCatTypeBool = new JSONArray();
            for (int i = 0; i < subMenuCatogeoryDataBool.length; i++) {
                JSONObject jobjSub = new JSONObject();
                jobjSub.put("element" + i, subMenuCatogeoryDataBool[i]);
                jarrCatTypeBool.put(jobjSub);
            }
            jobj.put("CatTypeBool", jarrCatTypeBool);

            JSONArray jarrPayMethBool = new JSONArray();
            for (int i = 0; i < subMenuPaymentMethodDataBool.length; i++) {
                JSONObject jobjSub = new JSONObject();
                jobjSub.put("element" + i, subMenuPaymentMethodDataBool[i]);
                jarrPayMethBool.put(jobjSub);
            }
            jobj.put("PayMethBool", jarrPayMethBool);

            JSONArray jarrcols = new JSONArray();
            for (int i = 0; i < subMenuCatogeoryData.length; i++) {
                JSONObject jobjSub = new JSONObject();
                jobjSub.put("element" + i, subMenuCatogeoryData[i]);
                jarrcols.put(jobjSub);
            }
            jobj.put("cols", jarrcols);

            JSONArray jarrpays = new JSONArray();
            for (int i = 0; i < subMenuPaymentMethodData.length; i++) {
                JSONObject jobjSub = new JSONObject();
                jobjSub.put("element" + i, subMenuPaymentMethodData[i]);
                jarrpays.put(jobjSub);
            }
            jobj.put("pays", jarrpays);

            jobj.put("graphType", graphType);
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return jobj.toString();
    }
}
