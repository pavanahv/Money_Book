package com.example.allakumarreddy.moneybook.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.allakumarreddy.moneybook.interfaces.ChunksFragmentInteractionListener;
import com.example.allakumarreddy.moneybook.R;

import java.util.ArrayList;


public class MessageListFragment extends Fragment {

    private static final String TAG = MessageListFragment.class.getSimpleName();
    private ChunksFragmentInteractionListener mListener;
    private ListView lv;
    private View progressBar;
    private ArrayList<String> msgList;
    private ArrayAdapter<String> arrayAdapter;
    private SearchView sv;

    public MessageListFragment() {
        // Required empty public constructor
    }

    public static MessageListFragment newInstance(String param1, String param2) {
        MessageListFragment fragment = new MessageListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        lv = (ListView) view.findViewById(R.id.list_view);
        progressBar = view.findViewById(R.id.progressBar);
        sv = view.findViewById(R.id.sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                readMsgs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                readMsgs(newText);
                return false;
            }
        });
        msgList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1);
        lv.setAdapter(arrayAdapter);

        lv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        readMsgs("");
        lv.setOnItemClickListener((parent, view1, position, id) -> {
            mListener.seelctedMsg(msgList.get(position));
        });

        return view;
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
    public void onResume() {
        super.onResume();
    }

    private void readMsgs(String s) {

        lv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            msgList.clear();
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = getActivity().getContentResolver();
            Cursor c = cr.query(message, null, "body LIKE \'%" + s + "%\'",
                    null, null);
            int totalSMS = c.getCount();
            if (totalSMS >= 1000)
                totalSMS = 1000;
            if (c.moveToFirst()) {
                int ind = c.getColumnIndex("body");
                for (int i = 0; i < totalSMS; i++) {
                    msgList.add(c.getString(ind));
                    c.moveToNext();
                }
            }
            c.close();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arrayAdapter.clear();
                    arrayAdapter.addAll(msgList);
                    arrayAdapter.notifyDataSetChanged();
                    lv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
