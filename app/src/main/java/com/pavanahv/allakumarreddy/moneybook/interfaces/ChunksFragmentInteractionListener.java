package com.pavanahv.allakumarreddy.moneybook.interfaces;

public interface ChunksFragmentInteractionListener {
    void onParse(String str, String msgStr, String nameStr);

    void saveFields(String desStr, String amountStr, int typeInt, String cateStr, String balLeftStr, String paymStr);

    void seelctedMsg(String s);

    void saveEverything();

}
