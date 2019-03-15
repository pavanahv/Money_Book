package com.example.allakumarreddy.moneybook.test;

import java.util.ArrayList;

public class Test {

    public static void main(String args[]) {

        /*String s1 = "INR9,500.00 debited on Credit Card XX1008 on 11-Mar-19.Info:PAYTM.Avbl Lmt:INR68,910.00.Call 18601207777 for dispute or SMS BLOCK 1008 to 9215676766";
        String s2 = "INR1,500.00 debited on Credit Card XX1008 on 11-Mar-19.Info:PAYTM.Avbl Lmt:INR87,910.00.Call 18601207777 for dispute or SMS BLOCK 1008 to 9215676766";

        for (String s : s2.split(" ")) {
            if (s1.indexOf(s) != -1) {
                System.out.println("S -> " + s);
            } else
                System.out.println("N -> " + s);
        }*/

        trail1();
    }

    public static void trail1() {
        //String orgStr = "You have made a purchase for an amount of Rs. 214.00  on your Sodexo Card 637513-xxxxxx-6056 on 20:43,30 Nov at FRESH ZONE. The available balance on your Sodexo Card is INR 10.60.Download the Zeta App to track your spends http://bit.ly/2RySHSA.";
        //String savStr = "You have made a purchase for an amount of Rs. {{24.00}}  on your Sodexo Card 637513-xxxxxx-6056 on {{10:33,11 Mar}} at {{TULASI JUICE JUNCTION}}. The available balance on your Sodexo Card is INR {{1000.10}}.Download the Zeta App to track your spends http://bit.ly/2RySHSA.";
        //String orgStr = "INR9,500.00 debited on Credit Card XX1008 on 11-Mar-19.Info:PAYTM.Avbl Lmt:INR68,910.00.Call 18601207777 for dispute or SMS BLOCK 1008 to 9215676766";
        //String savStr = "INR{{9,500.00}} debited on Credit Card XX1008 on {{11-Mar-19}}.Info:{{PAYTM}}.Avbl Lmt:INR{{68,910.00}}.Call 18601207777 for dispute or SMS BLOCK 1008 to 9215676766";
        //String orgStr = "INR 134.69 has been spent on your YES BANK Credit Card ending with 8175 at ZAAK EPAYME on 09/03/2019 at 13:28:57. Avl Bal INR 15,141.31. In case of suspicious transaction, to block your card, SMS BLKCC <Space><Last 4 digits of card number> to 9840909000 from your registered mobile number";
        //String savStr = "INR {{134.69}} has been spent on your YES BANK Credit Card ending with 8175 at {{ZAAK EPAYME}} on {{09/03/2019}} at {{13:28:57}}. Avl Bal INR {{15,141.31}}. In case of suspicious transaction, to block your card, SMS BLKCC <Space><Last 4 digits of card number> to 9840909000 from your registered mobile number";
        String orgStr = "ALERT: You've spent Rs.128.01  on CREDIT Card xx6144 at Uber India Systems on 2019-02-23:13:11:43.Avl bal - Rs.98336.99, curr o/s - Rs.34663.01.Not you? Call 18002586161.";
        String savStr = "ALERT: You've spent Rs.{{128.01}}  on CREDIT Card xx6144 at {{Uber India Systems}} on {{2019-02-23:13:11:43}}.Avl bal - Rs.{{98336.99}}, curr o/s - Rs.{{34663.01}}.Not you? Call 18002586161.";
        ArrayList<String> constList = new ArrayList<>();
        int i;
        for (i = 0; i < savStr.length(); ) {
            int sind = savStr.indexOf("{{", i);
            int eind = -1;
            if (sind != -1) {
                eind = savStr.indexOf("}}", sind);
                if (eind != -1) {
                    //System.out.println(savStr.substring(i,sind));
                    //sind + 2, eind
                    constList.add(savStr.substring(i, sind));
                }
            } else
                break;
            i = eind + 2;
        }
        if (i != savStr.length())
            constList.add(savStr.substring(i));
        System.out.println(constList);

        int count = 0;
        for (String s : constList) {
            if (orgStr.indexOf(s) == -1)
                break;
            count++;
        }
        ArrayList<String> flist=new ArrayList<>();
        if (count == constList.size()) {
            System.out.println("valid");
            final int len = constList.size();
            int sind = orgStr.indexOf(constList.get(0));
            if ( sind != 0){
                flist.add(orgStr.substring(0,sind));
            }
            sind+=constList.get(0).length();
            for (int j=1;j<len;j++) {
                String s=constList.get(j);
                int ind = orgStr.indexOf(s,sind);
                flist.add(orgStr.substring(sind,ind));
                sind = ind+s.length();
            }
            System.out.println(flist);
        }
    }
}
