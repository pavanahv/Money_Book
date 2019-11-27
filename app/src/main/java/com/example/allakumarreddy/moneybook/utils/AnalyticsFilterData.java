package com.example.allakumarreddy.moneybook.utils;

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
    public boolean[] subMenuDateDataBool = new boolean[]{false, false, false, false, true};
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

    public void initDataAgainAfterClear() {
        subMenuDateDataBool = new boolean[]{false, false, false, false, true};
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

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(queryText);
        sb.append("\n");
        sb.append(sDate);
        sb.append("\n");
        sb.append(eDate);
        sb.append("\n");
        sb.append(Arrays.toString(subMenuDateData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuDateDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuMoneyTypeData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuMoneyTypeDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuDateIntervalData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuDateIntervalDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuGraphTypeData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuGraphTypeDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuGroupByData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuGroupByDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuSortByData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuSortByDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuSortingOrderData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuSortingOrderDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuCatogeoryData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuCatogeoryDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuPaymentMethodDataBool));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuFilterData));
        sb.append("\n");
        sb.append(Arrays.toString(subMenuFilterDataBool));

        //LoggerCus.d(TAG, sb.toString());
        return sb.toString();
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
