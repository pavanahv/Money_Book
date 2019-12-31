package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.Activities.AddActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.AnalyticsActivity;
import com.pavanahv.allakumarreddy.moneybook.Adapter.DashBoardAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.DashBoardAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.interfaces.DashUIUpdateInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pavanahv.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;


public class PaymentMethodFragment extends Fragment implements DashBoardAdapterInterface {


    private static final int ADD_ACTIVITY = 1001;
    private static final int ADD_ACTIVITY_MT = 1002;
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
    private View mainView;
    private View progress;
    private PreferencesCus mPref;

    public PaymentMethodFragment() {
        // Required empty public constructor
    }

    public static PaymentMethodFragment newInstance(String param1, String param2) {
        PaymentMethodFragment fragment = new PaymentMethodFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getActivity());
        mPref = new PreferencesCus(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_calender).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mainView = view.findViewById(R.id.mainView);
        mainView.setVisibility(View.GONE);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        dashBoardList = (ListView) view.findViewById(R.id.dashboardlist);

        totalD = (TextView) view.findViewById(R.id.dashboardtotald);
        totalM = (TextView) view.findViewById(R.id.dashboardtotalm);
        totalY = (TextView) view.findViewById(R.id.dashboardtotaly);

        dataD = ((TextView) view.findViewById(R.id.dashday));
        dateM = ((TextView) view.findViewById(R.id.dashmonth));
        dateY = ((TextView) view.findViewById(R.id.dashyear));

        initColors();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddActivity();
            }
        });
    }

    private void initColors() {
        int tempColor = Utils.getDayWiseColor(mPref);
        dataD.setTextColor(tempColor);
        totalD.setTextColor(tempColor);
        tempColor = Utils.getMonthWiseColor(mPref);
        dateM.setTextColor(tempColor);
        totalM.setTextColor(tempColor);
        tempColor = Utils.getYearWiseColor(mPref);
        dateY.setTextColor(tempColor);
        totalY.setTextColor(tempColor);
    }

    private void startAddActivity() {
        Intent intent = new Intent(getContext(), AddActivity.class);
        intent.putExtra("type", GlobalConstants.PAYMENT_METHOD_SCREEN);
        startActivityForResult(intent, ADD_ACTIVITY);
        getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
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
        dashBoardAdapter = new DashBoardAdapter(dbr, getActivity(), this, 2);
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

    public void dashBoardUIData() {
        new Thread(() -> {
            String dayCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneyInCurrentDay(GlobalConstants.TYPE_SPENT));
            String monthCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneyInCurrentMonth(GlobalConstants.TYPE_SPENT));
            String yearCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneyInCurrentYear(GlobalConstants.TYPE_SPENT));
            dbr = db.getPaymentMethodRecords();
            getActivity().runOnUiThread(() -> {
                totalD.setText(dayCountTotalHeadText);
                totalM.setText(monthCountTotalHeadText);
                totalY.setText(yearCountTotalHeadText);
                dashBoardAdapter.clear();
                dashBoardAdapter.addAll(dbr);
                dashBoardAdapter.notifyDataSetChanged();
                mDashUIUpdateInterface.switchScreen();
                initColors();
                showMainView();
            });
        }).start();
    }

    private void showMainView() {
        revealAnimation(progress, mainView, getView());
    }

    @Override
    public void viewOnClick(String dataText, View finalConvertView) {
        startActivity(new Intent(getActivity(), AnalyticsActivity.class).putExtra("paymentMethod", dataText),
                ActivityOptions.makeSceneTransitionAnimation(getActivity(), finalConvertView,
                        getResources().getString(R.string.shared_anim_cat_item)).toBundle());
    }

    @Override
    public void deleteCategoryPopMenu(String categoryName) {
        db.deletePaymentMethod(categoryName.toLowerCase());
        dashBoardUIData();
    }

    @Override
    public void moneyTrasferPopMenu(String categoryName) {
        Intent intent = new Intent(getActivity(), AddActivity.class);
        intent.putExtra("type", GlobalConstants.PAYMENT_METHOD_MONEY_TRANSFER_SCREEN);
        intent.putExtra("tcategory", categoryName.toLowerCase());
        startActivityForResult(intent, ADD_ACTIVITY_MT);
        getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_ACTIVITY_MT:
                if (resultCode == Activity.RESULT_OK) {

                    String s[] = new String[]{data.getStringExtra("fdes"),
                            data.getStringExtra("famount"),
                            data.getStringExtra("fcategory"),
                            data.getStringExtra("tcategory"),
                            data.getStringExtra("payment_method")};

                    if ((s[1].compareToIgnoreCase("") != 0) && (s[2].compareToIgnoreCase("") != 0)) {
                        MBRecord mbr = new MBRecord(s[0], Integer.parseInt(s[1]), new Date(), s[2], s[4]);
                        mbr.setToCategory(s[3]);
                        boolean res = db.addMTRecord(mbr);
                        if (res) {
                            Toast.makeText(getActivity(), "Succrssfully Trasfered !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Something Went Wrong\nPlease Try Again!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Amount", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case (ADD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    String s[] = new String[]{data.getStringExtra("fdes"),
                            data.getStringExtra("famount"),
                            data.getStringExtra("fcategory"),
                            data.getStringExtra("tcategory"),
                            data.getStringExtra("payment_method")};
                    addPaymentMethod(s[0]);
                }
                break;
            }
        }
    }

    public void addPaymentMethod(String s) {
        db.addPaymentMethod(s);
    }
}
