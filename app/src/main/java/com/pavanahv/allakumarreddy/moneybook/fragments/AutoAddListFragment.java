package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pavanahv.allakumarreddy.moneybook.Adapter.AutoAddAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.AutoAddAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AutoAddRecord;

import java.util.ArrayList;

public class AutoAddListFragment extends Fragment {

    private OnAutoAddListFragmentInteractionListener mListener;
    private ListView mListView;
    private DbHandler db;
    private ArrayList<AutoAddRecord> list;
    private AutoAddAdapter mAutoAddAdpter;
    private View noData;

    public AutoAddListFragment() {
        // Required empty public constructor
    }

    public static AutoAddListFragment newInstance() {
        AutoAddListFragment fragment = new AutoAddListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_add_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        view.findViewById(R.id.add_button).setOnClickListener(v -> {
            if (mListener != null) {
                mListener.addButtonClicked();
            }
        });

        noData = view.findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);
        mListView = view.findViewById(R.id.lv);
        list = new ArrayList<>();
        mAutoAddAdpter = new AutoAddAdapter(list, getContext(), new AutoAddAdapterInterface() {
            @Override
            public void onClickItem(AutoAddRecord autoAddRecord) {
                mListener.itemClicked(autoAddRecord);
            }
        });
        mListView.setAdapter(mAutoAddAdpter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        new Thread(() -> {
            list.clear();
            list.addAll(db.getAutoAddRecords());
            getActivity().runOnUiThread(() -> {
                mAutoAddAdpter.notifyDataSetChanged();
                if (mAutoAddAdpter.getCount() <= 0) {
                    mListView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAutoAddListFragmentInteractionListener) {
            mListener = (OnAutoAddListFragmentInteractionListener) context;
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

    public interface OnAutoAddListFragmentInteractionListener {
        void addButtonClicked();

        void itemClicked(AutoAddRecord record);
    }

}
