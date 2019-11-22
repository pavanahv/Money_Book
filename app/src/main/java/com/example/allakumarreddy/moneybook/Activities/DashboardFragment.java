package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Adapter.DashBoardAdapter;
import com.example.allakumarreddy.moneybook.Adapter.DashBoardAdapterInterface;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DashboardFragment extends Fragment implements DashBoardAdapterInterface {


    private static final int ADD_ACTIVITY = 1001;
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private ListView dashBoardList;
    private TextView totalD;
    private TextView totalM;
    private TextView totalY;
    private ArrayList<DashBoardRecord> dbr;
    private DashBoardAdapter dashBoardAdapter;
    private TextView dataD;
    private TextView dateM;
    private TextView dateY;
    private DbHandler db;
    private DashUIUpdateInterface mDashUIUpdateInterface;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        dashBoardList = (ListView) view.findViewById(R.id.dashboardlist);

        totalD = (TextView) view.findViewById(R.id.dashboardtotald);
        totalM = (TextView) view.findViewById(R.id.dashboardtotalm);
        totalY = (TextView) view.findViewById(R.id.dashboardtotaly);

        dataD = ((TextView) view.findViewById(R.id.dashday));
        dateM = ((TextView) view.findViewById(R.id.dashmonth));
        dateY = ((TextView) view.findViewById(R.id.dashyear));

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddActivity();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewData();
    }

    private void initViewData() {
        dataD.setText(new SimpleDateFormat("dd").format(new Date()));
        dateM.setText(new SimpleDateFormat("MMM").format(new Date()));
        dateY.setText(new SimpleDateFormat("yyyy").format(new Date()));

        dbr = new ArrayList<>();
        dashBoardAdapter = new DashBoardAdapter(dbr, getContext(), this, 1);
        dashBoardList.setAdapter(dashBoardAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        dashBoardUIData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDashUIUpdateInterface = (DashUIUpdateInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DashUIUpdateInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void addCategory(String name, int type) {
        db.addCategory(name, type);
        DashBoardRecord temp = db.getDashBoardRecord(name);
        dbr.add(temp);
        dashBoardAdapter.add(temp);
        dashBoardAdapter.notifyDataSetChanged();
    }

    public void dashBoardUIData() {
        new Thread(() -> {
            String dayCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneySpentInCurrentDay());
            String monthCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneySpentInCurrentMonth());
            String yearCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneySpentInCurrentYear());
            dbr = db.getDashBoardRecords();
            getActivity().runOnUiThread(() -> {
                totalD.setText(dayCountTotalHeadText);
                totalM.setText(monthCountTotalHeadText);
                totalY.setText(yearCountTotalHeadText);
                dashBoardAdapter.clear();
                dashBoardAdapter.addAll(dbr);
                dashBoardAdapter.notifyDataSetChanged();
                mDashUIUpdateInterface.switchScreen();
            });
        }).start();
    }

    @Override
    public void viewOnClick(String dataText) {
        startActivity(new Intent(getContext(), AnalyticsActivity.class).putExtra("name", dataText));
    }

    @Override
    public void deleteCategoryPopMenu(String categoryName) {
        db.deleteCategory(categoryName.toLowerCase());
        dashBoardUIData();
    }

    @Override
    public void moneyTrasferPopMenu(String categoryName) {
        // no need to implement
    }

    private void startAddActivity() {
        Intent intent = new Intent(getContext(), AddActivity.class);
        intent.putExtra("type", GlobalConstants.CATERGORY_SCREEN);
        startActivityForResult(intent, ADD_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ADD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    String s[] = new String[]{data.getStringExtra("fdes"),
                            data.getStringExtra("famount"),
                            data.getStringExtra("fcategory"),
                            data.getStringExtra("tcategory"),
                            data.getStringExtra("payment_method")};
                    addCategory(s[0], 0);
                }
                break;
            }
        }
    }
}
