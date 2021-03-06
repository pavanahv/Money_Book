package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pavanahv.allakumarreddy.moneybook.Activities.AnalyticsItemDetail;
import com.pavanahv.allakumarreddy.moneybook.Activities.RePaymentActivity;
import com.pavanahv.allakumarreddy.moneybook.Adapter.MyAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.HomeAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.pavanahv.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;
import static com.pavanahv.allakumarreddy.moneybook.utils.Utils.addItem;


public class HomeInnerFragment extends Fragment {

    private static final String TAG = HomeInnerFragment.class.getSimpleName();
    private SimpleDateFormat format;
    private String curDateStr;
    private DbHandler mDbHandler;
    private ListView mListView;
    private TextView mTotalTextView;
    private ArrayList<MBRecord> mbr;
    private HomeAdapterInterface mHomeAdapterInterface = new HomeAdapterInterface() {
        @Override
        public void onClickItem(MBRecord mbr, View view) {
            startDetailActivity(mbr, view);
        }
    };
    private MyAdapter mAdapter;
    int mType = -1;
    private View mainView;
    private View progress;
    private View noData;
    private Date curDate;
    private PreferencesCus pref;
    private TextView dateTv;
    private boolean isResumed = false;
    private ImageButton prev;
    private ImageButton next;
    private boolean userVisible = false;
    private boolean isVisit;

    public HomeInnerFragment() {
        // Required empty public constructor
    }

    public static HomeInnerFragment newInstance(int type) {
        HomeInnerFragment fragment = new HomeInnerFragment();
        Bundle args = new Bundle();
        args.putInt(GlobalConstants.FRAGMENT_ACTIVATE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readBundle(getArguments());
        curDate = new Date();
        pref = new PreferencesCus(getContext());
        format = new SimpleDateFormat("yyyy/MM/dd");
        initDate();
        mDbHandler = new DbHandler(getContext());
        mbr = new ArrayList<>();
        mAdapter = new MyAdapter(mbr, getContext(), mType, mHomeAdapterInterface);

        isVisit = pref.isVisit(TAG);
    }

    private void initDate() {
        Date tempDate = pref.getCurrentDate();
        if (tempDate != null)
            curDate = tempDate;
        curDateStr = format.format(curDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home_inner, container, false);
        mainView = layout.findViewById(R.id.mainView);
        mainView.setVisibility(View.GONE);
        progress = layout.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        noData = layout.findViewById(R.id.no_data);
        mListView = layout.findViewById(R.id.lv);
        mListView.setAdapter(mAdapter);
        mTotalTextView = layout.findViewById(R.id.total_tv);
        dateTv = (TextView) layout.findViewById(R.id.date_text);
        prev = ((ImageButton) layout.findViewById(R.id.prev));
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(false);
            }
        });
        next = ((ImageButton) layout.findViewById(R.id.next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(true);
            }
        });
        initDateView();
        return layout;
    }

    private void setDate(boolean isNext) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        if (isNext)
            cal.add(Calendar.DAY_OF_MONTH, 1);
        else
            cal.add(Calendar.DAY_OF_MONTH, -1);
        pref.setCurrentDate(cal.getTimeInMillis());
        initDate();
        updataUI();
    }

    private void initDateView() {
        dateTv.setText(new SimpleDateFormat("dd MMM yyyy").format(curDate));
        dateTv.setTextColor(Utils.getColorPref(mType, pref));
        LoggerCus.d(TAG, new SimpleDateFormat("dd MMM yyyy").format(curDate));
    }

    private void readBundle(Bundle args) {
        if (args != null) {
            mType = args.getInt(GlobalConstants.FRAGMENT_ACTIVATE_TYPE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            userVisible = true;
            if (isResumed) {
                updataUI();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        updataUI();
    }

    public void updataUI() {
        initDate();
        initDateView();

        new Thread(() -> {
            mbr = mDbHandler.getRecords(curDateStr, mType);
            getActivity().runOnUiThread(() -> {
                mAdapter.clear();
                mAdapter.addAll(mbr);
                mAdapter.notifyDataSetChanged();
                mTotalTextView.setText(Utils.getFormattedNumber(getTotalAmount()));
                if (mAdapter.getCount() <= 0) {
                    noData.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    noData.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                showMainView();
                presentShowcaseSequence();
            });
        }).start();
    }

    private void showMainView() {
        revealAnimation(progress, mainView, getView());
    }

    private void presentShowcaseSequence() {

        if (!userVisible)
            return;

        if (!isVisit) {
            isVisit = true;
            pref.setVisit(TAG, true);
            ShowcaseConfig config = new ShowcaseConfig();
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), TAG);
            sequence.setConfig(config);
            FragmentActivity activity = getActivity();
            addItem(sequence, dateTv, "All the expenses shown below belongs to this date", activity);
            addItem(sequence, mTotalTextView, "Total amount of all the records mentioned below", activity);
            addItem(sequence, prev, "Used to go to previous date", activity);
            addItem(sequence, next, "Used to go to next date", activity);
            View calenderMenu = getActivity().findViewById(R.id.action_calender);
            View newMenu = getActivity().findViewById(R.id.action_new);
            View anallyticsMenu = getActivity().findViewById(R.id.action_analytics);
            View backupMenu = getActivity().findViewById(R.id.action_backup);
            View fab = getActivity().findViewById(R.id.fab);
            addItem(sequence, calenderMenu, "Navigate to particular date using calender", activity);
            addItem(sequence, newMenu, "Select to Add new Item, Category, Payment method", activity);
            addItem(sequence, anallyticsMenu, "Click to go to analytics", activity);
            addItem(sequence, backupMenu, "Click to backup to google drive", activity);
            addItem(sequence, fab, "Click to add new item", activity);
            sequence.start();
        }
    }

    private int getTotalAmount() {
        int total = 0;
        for (MBRecord n : mbr)
            total += n.getAmount();
        return total;
    }

    public void startDetailActivity(MBRecord mbr, View view) {
        Intent intent = null;
        if (mType == GlobalConstants.TYPE_DUE || mType == GlobalConstants.TYPE_LOAN) {
            intent = new Intent(getActivity(), RePaymentActivity.class);
            intent.putExtra("MBRecord", mbr);
        } else {
            intent = new Intent(getActivity(), AnalyticsItemDetail.class);
            mbr.setType(mType);
            intent.putExtra("MBRecord", mbr);
        }
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(getActivity(), view,
                        getResources().getString(R.string.shared_anim_analytics_item)).toBundle());
    }

}
