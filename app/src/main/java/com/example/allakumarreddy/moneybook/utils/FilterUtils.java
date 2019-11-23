package com.example.allakumarreddy.moneybook.utils;

import com.example.allakumarreddy.moneybook.db.DbHandler;

import java.util.Arrays;

public class FilterUtils {

    public static void initCategories(String tempCat, DbHandler db, AnalyticsFilterData analyticsFilterData) {
        // categories initialization
        String[] cat = db.getCategeories();
        analyticsFilterData.subMenuCatogeoryData = new String[cat.length + 1];
        analyticsFilterData.subMenuCatogeoryData[0] = "All";
        for (int i = 1; i < analyticsFilterData.subMenuCatogeoryData.length; i++)
            analyticsFilterData.subMenuCatogeoryData[i] = cat[i - 1];

        analyticsFilterData.subMenuCatogeoryDataBool = new boolean[analyticsFilterData.subMenuCatogeoryData.length];

        if (tempCat != null && (!(tempCat.compareToIgnoreCase("0") == 0))) {
            Arrays.fill(analyticsFilterData.subMenuCatogeoryDataBool, false);
            for (int i = 1; i < analyticsFilterData.subMenuCatogeoryData.length; i++) {
                if (analyticsFilterData.subMenuCatogeoryData[i].compareToIgnoreCase(tempCat) == 0) {
                    analyticsFilterData.subMenuCatogeoryDataBool[i] = true;
                    break;
                }
            }
        } else {
            Arrays.fill(analyticsFilterData.subMenuCatogeoryDataBool, true);
        }
    }

    public static void initPaymentMethods(String tempCat, DbHandler db, AnalyticsFilterData analyticsFilterData) {
        // payment methods initialization
        String[] pay = db.getPaymentMethods();
        analyticsFilterData.subMenuPaymentMethodData = new String[pay.length + 1];
        analyticsFilterData.subMenuPaymentMethodData[0] = "All";
        for (int i = 1; i < analyticsFilterData.subMenuPaymentMethodData.length; i++)
            analyticsFilterData.subMenuPaymentMethodData[i] = pay[i - 1];

        analyticsFilterData.subMenuPaymentMethodDataBool = new boolean[analyticsFilterData.subMenuPaymentMethodData.length];

        if (tempCat != null && (!(tempCat.compareToIgnoreCase("0") == 0))) {
            Arrays.fill(analyticsFilterData.subMenuPaymentMethodDataBool, false);
            for (int i = 1; i < analyticsFilterData.subMenuPaymentMethodData.length; i++) {
                if (analyticsFilterData.subMenuPaymentMethodData[i].compareToIgnoreCase(tempCat) == 0) {
                    analyticsFilterData.subMenuPaymentMethodDataBool[i] = true;
                    break;
                }
            }
        } else {
            Arrays.fill(analyticsFilterData.subMenuPaymentMethodDataBool, true);
        }
    }

    public static void initFilters(DbHandler db, AnalyticsFilterData analyticsFilterData) {
        analyticsFilterData.filters = db.getFilterRecords();
        analyticsFilterData.subMenuFilterData = new String[analyticsFilterData.filters.length + 1];
        analyticsFilterData.subMenuFilterData[0] = "None";
        for (int i = 0; i < analyticsFilterData.subMenuFilterData.length - 1; i++) {
            analyticsFilterData.subMenuFilterData[i + 1] = analyticsFilterData.filters[i][0];
        }
        analyticsFilterData.subMenuFilterDataBool = new boolean[analyticsFilterData.subMenuFilterData.length];
        Arrays.fill(analyticsFilterData.subMenuFilterDataBool, false);
        analyticsFilterData.subMenuFilterDataBool[0] = true;
    }
}
