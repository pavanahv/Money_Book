package com.example.allakumarreddy.moneybook.MessageParser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

import java.util.ArrayList;

public class ItemDetailFragment extends Fragment {

    private static final String TAG = "ItemDetailFragment";
    private ChunksFragmentInteractionListener mListener;
    private TextView chunks;
    private EditText des;
    private EditText amount;
    private Spinner type;
    private Spinner cate;
    private String[] catArr;
    private DbHandler db;
    private EditText balLeft;

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    private String chunkText;

    public int getTypeActivate() {
        return typeActivate;
    }

    public void setTypeActivate(int typeActivate) {
        this.typeActivate = typeActivate;
    }

    private int typeActivate;

    public String getMsgStr() {
        return msgStr;
    }

    public void setMsgStr(String msgStr) {
        this.msgStr = msgStr;
    }

    private String msgStr;
    private String desStr;
    private String amountStr;
    private int typeStr;
    private String cateStr;
    private String balLeftStr;

    public String getDesStr() {
        return desStr;
    }

    public void setDesStr(String desStr) {
        this.desStr = desStr;
    }

    public String getAmountStr() {
        return amountStr;
    }

    public void setAmountStr(String amountStr) {
        this.amountStr = amountStr;
    }

    public int getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(int typeStr) {
        this.typeStr = typeStr;
    }

    public String getCateStr() {
        return cateStr;
    }

    public void setCateStr(String cateStr) {
        this.cateStr = cateStr;
    }

    public String getBalLeftStr() {
        return balLeftStr;
    }

    public void setBalLeftStr(String balLeftStr) {
        this.balLeftStr = balLeftStr;
    }

    public String getSelectedMsgStr() {
        return selectedMsgStr;
    }

    public void setSelectedMsgStr(String selectedMsgStr) {
        this.selectedMsgStr = selectedMsgStr;
    }

    private String selectedMsgStr;

    public ItemDetailFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemDetailFragment newInstance() {
        ItemDetailFragment fragment = new ItemDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        chunks = (TextView) view.findViewById(R.id.chunks);

        des = (EditText) view.findViewById(R.id.descitem);
        amount = (EditText) view.findViewById(R.id.amountitem);
        type = (Spinner) view.findViewById(R.id.typeitem);
        balLeft = (EditText) view.findViewById(R.id.leftOutBalance);
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Spent", "Earn", "Due", "Loan"});
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);

        cate = (Spinner) view.findViewById(R.id.catitem);
        catArr = db.getCategeories();
        ArrayAdapter caa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, catArr);
        caa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate.setAdapter(caa);

        if (typeActivate == 1) {
            chunks.setText("Please observe the fields captured below are same as what you have defined in previous steps. If they are correct please save else go back and perform steps correctly.");

            // dont allow user to edit data. since we are just showing them how it parsed
            des.setEnabled(false);
            des.setHint("");
            amount.setEnabled(false);
            amount.setHint("");
            balLeft.setEnabled(false);
            balLeft.setHint("");
            type.setEnabled(false);
            cate.setEnabled(false);

            // fill the data
            compareParseData();
        } else if (typeActivate == 0) {
            chunks.append("\n" + chunkText);
        }
        ((ImageButton) view.findViewById(R.id.savbtn)).setOnClickListener(v -> save());

        return view;
    }

    private void compareParseData() {
        ArrayList<String> constList = getChunksList();

        // check its a valid message or not i.e to compare both messages
        String orgStr = this.selectedMsgStr;
        int count = 0;
        for (String s : constList) {
            if (orgStr.indexOf(s) == -1)
                break;
            count++;
        }
        if (count == constList.size()) {
            // yes selected message is valid

            // extract required details into list
            ArrayList<String> flist = new ArrayList<>();
            final int len = constList.size();
            int sind = orgStr.indexOf(constList.get(0));
            LoggerCus.d(TAG, constList.get(0));
            LoggerCus.d(TAG, "sind " + sind);
            if (sind != 0) {
                flist.add(orgStr.substring(0, sind));
            }
            sind += constList.get(0).length();
            LoggerCus.d(TAG, "sind " + sind);
            for (int j = 1; j < len; j++) {
                String s = constList.get(j);
                int ind = orgStr.indexOf(s, sind);
                flist.add(orgStr.substring(sind, ind));
                sind = ind + s.length();
            }
            if ((len == 1) && (flist.size() == 0))
                flist.add(orgStr.substring(sind));
            LoggerCus.d(TAG, "sind " + sind);
            LoggerCus.d(TAG, "len " + len);
            LoggerCus.d(TAG, flist.toString());
            parseUpdateAllFields(flist);
        } else {
            // selected message is not valid
            Toast.makeText(getContext(), "Not Valid Message Seleted", Toast.LENGTH_LONG).show();
            getFragmentManager().popBackStack();
        }
    }

    private void parseUpdateAllFields(ArrayList<String> flist) {
        des.setText(parseUpdateField(flist, desStr));
        amount.setText(parseUpdateField(flist, amountStr));
        type.setSelection(typeStr);

        // for selecting correct item in category array
        for (int i = 0; i < catArr.length; i++) {
            if (catArr[i].compareToIgnoreCase(cateStr) == 0) {
                cate.setSelection(i);
                break;
            }
        }
        balLeft.setText(parseUpdateField(flist, balLeftStr));
    }

    private String parseUpdateField(ArrayList<String> flist, String str) {
        StringBuilder strBuilder = new StringBuilder();
        final int len = str.length();
        int pind = 0;
        for (int i = 0; i < len; ) {
            int sind = str.indexOf("{{", i);
            int eind = -1;
            if (sind != -1) {
                eind = str.indexOf("}}", sind);
                if (eind != -1) {
                    int arg = Integer.parseInt(str.substring(sind + 2, eind));
                    String prefixStr = str.substring(pind, sind);
                    strBuilder.append(prefixStr + flist.get(arg - 1));
                }
            } else
                break;
            i = eind + 2;
            pind = i;
        }
        if (pind < len)
            strBuilder.append(str.substring(pind, len));
        return strBuilder.toString();
    }

    private ArrayList<String> getChunksList() {
        String savStr = this.msgStr;
        ArrayList<String> constList = new ArrayList<>();
        int i;
        for (i = 0; i < savStr.length(); ) {
            int sind = savStr.indexOf("{{", i);
            int eind = -1;
            if (sind != -1) {
                eind = savStr.indexOf("}}", sind);
                if (eind != -1) {
                    constList.add(savStr.substring(i, sind));
                }
            } else
                break;
            i = eind + 2;
        }
        if (i != savStr.length())
            constList.add(savStr.substring(i));
        return constList;
    }

    private void save() {
        if (typeActivate == 0) {
            String desStr = des.getText().toString();
            String amountStr = amount.getText().toString();
            int typeInt = type.getSelectedItemPosition();
            String cateStr = catArr[cate.getSelectedItemPosition()];
            String balLeftStr = balLeft.getText().toString();
            mListener.saveFields(desStr, amountStr, typeInt, cateStr, balLeftStr);
        } else {
            mListener.saveEverything();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChunksFragmentInteractionListener) {
            mListener = (ChunksFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
