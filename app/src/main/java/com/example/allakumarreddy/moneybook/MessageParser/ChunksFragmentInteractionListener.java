package com.example.allakumarreddy.moneybook.MessageParser;

public interface ChunksFragmentInteractionListener {
    void onParse(String str);

    void saveFields(String desStr, String amountStr, int typeInt, String cateStr, String balLeftStr);

    void seelctedMsg(String s);
}
