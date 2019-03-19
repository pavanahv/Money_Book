package com.example.allakumarreddy.moneybook.MessageParser;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.allakumarreddy.moneybook.R;

import java.util.ArrayList;

public class MessageListFragment extends Fragment {

    private ChunksFragmentInteractionListener mListener;
    private ListView lv;
    private View progressBar;
    private View content;
    private ArrayList<String> msgList;
    private ArrayAdapter<String> arrayAdapter;

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
        lv=(ListView)view.findViewById(R.id.list_view);
        progressBar=view.findViewById(R.id.progressBar);
        content=view.findViewById(R.id.content);

        msgList=new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1);
        lv.setAdapter(arrayAdapter);

        content.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        readMsgs();
        lv.setOnItemClickListener((parent, view1, position, id) -> {
            //Toast.makeText(getContext(),msgList.get(position),Toast.LENGTH_LONG).show();
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

    private void readMsgs() {
        new Thread(() -> {
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = getActivity().getContentResolver();
            Cursor c = cr.query(message, null, null, null, null);
            int totalSMS = c.getCount();
            if(totalSMS>=1000)
                totalSMS=1000;
            if (c.moveToFirst()) {
                int ind = c.getColumnIndex("body");
                for (int i = 0; i < totalSMS; i++) {
                    msgList.add(c.getString(ind));
                    c.moveToNext();
                }
                getActivity().runOnUiThread(() -> {
                    arrayAdapter.clear();
                    arrayAdapter.addAll(msgList);
                    arrayAdapter.notifyDataSetChanged();
                    content.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });
            }
            c.close();
        }).start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
