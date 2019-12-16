package com.pavanahv.allakumarreddy.moneybook.test;

import java.util.Calendar;

public class Test {

    public static void main(String args[]) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR,1);
        // Add day to week
//        cal.add(Calendar.WEEK_OF_MONTH, 1);
//        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);

        // add day to month
//        cal.add(Calendar.MONTH,1);
//        cal.set(Calendar.DAY_OF_MONTH,23);

        // select a particular date in year
//        cal.add(Calendar.YEAR, 1);
//        cal.set(Calendar.MONTH, 2);
//        cal.set(Calendar.DAY_OF_MONTH, 24);

        long l = cal.getTimeInMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(l);
        System.out.println(cal1.getTime());
        System.out.println(Calendar.getInstance().getTime());
        System.out.println(cal1.getTimeInMillis());
        System.out.println(Calendar.getInstance().getTimeInMillis());
    }
}
