package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.ChunksFragmentInteractionListener;

import java.util.ArrayList;


public class ChunksFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    private String mParam1;
    private String mParam2;

    private ChunksFragmentInteractionListener mListener;
    private TextView msg;
    private TextView chunks;
    private EditText name;
    private String msgText;

    public ChunksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChunksFragment.
     */
    public static ChunksFragment newInstance(String param1, String param2) {
        ChunksFragment fragment = new ChunksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chunks, container, false);

        msg = (TextView) view.findViewById(R.id.msg);
        name = (EditText) view.findViewById(R.id.name);
        //chunks = (TextView) view.findViewById(R.id.parsed_chunks);
        ((Button) view.findViewById(R.id.parse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgStr = msg.getText().toString();
                String nameStr = name.getText().toString();
                if (nameStr != null && nameStr.trim().length() <= 0) {
                    error("Name should not be empty");
                    return;
                }
                if (msgStr != null && msgStr.trim().length() <= 0) {
                    error("Message should not be empty");
                    return;
                }
                parse(msgStr, nameStr);
            }
        });
        msg.setText(msgText);
        ((Button) view.findViewById(R.id.select)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectText();
            }
        });
        return view;
    }

    private void error(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    private void selectText() {
        int start = msg.getSelectionStart();
        int end = msg.getSelectionEnd();
        String text = msg.getText().toString();
        String selectedText = text.substring(start, end);
        String fstr = text.substring(0, start) + "{{" + selectedText + "}}" + text.substring(end);
        Spannable span = new SpannableString(fstr);
        int lastInd = 0;
        while (true) {
            int indStart = fstr.indexOf("{{", lastInd);
            if (indStart == -1)
                break;
            int indEnd = fstr.indexOf("}}", indStart);
            indEnd += 2;
            span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), indStart, indEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            lastInd = indEnd;
        }
        span.setSpan(new ForegroundColorSpan(Color.RED), start, end + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg.setText(span);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
    }

    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
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

    private void parse(String msgStr, String nameStr) {
        ArrayList<String> constList = new ArrayList<>();
        ArrayList<String> varList = new ArrayList<>();
        int i;
        for (i = 0; i < msgStr.length(); ) {
            int sind = msgStr.indexOf("{{", i);
            int eind = -1;
            if (sind != -1) {
                eind = msgStr.indexOf("}}", sind);
                if (eind != -1) {
                    //System.out.println(savStr.substring(i,sind));
                    //sind + 2, eind
                    varList.add(msgStr.substring(sind + 2, eind));
                    constList.add(msgStr.substring(i, sind));
                }
            } else
                break;
            i = eind + 2;
        }
        if (i != msgStr.length())
            constList.add(msgStr.substring(i));
        Log.d("chunksfragment", constList + "");
        Log.d("chunksfragment", varList + "");

        StringBuilder sb = new StringBuilder();
        final int len = varList.size();
        for (int j = 0; j < len; j++) {
            sb.append("" + (j + 1) + " -> " + varList.get(j) + "\n");
        }

        if (varList.size() <= 0) {
            error("Message should contain all the changes from message to message of same format. Please refer help");
            return;
        }
        mListener.onParse(sb.toString(), msgStr, nameStr);
        //chunks.setText(sb.toString());
    }

    public void setMessage(String s) {
        this.msgText = s;
    }
}
