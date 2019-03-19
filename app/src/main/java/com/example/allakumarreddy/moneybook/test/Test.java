package com.example.allakumarreddy.moneybook.test;

public class Test {

    public static void main(String args[]) {
        float f=Float.parseFloat("2,21,1.01".replaceAll(",", ""));
        System.out.println(f);
        int n=(int)f;
        System.out.println(n);
    }
}
