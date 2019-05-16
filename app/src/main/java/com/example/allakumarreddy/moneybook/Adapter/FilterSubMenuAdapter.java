package com.example.allakumarreddy.moneybook.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.example.allakumarreddy.moneybook.Activities.FiltersAnalyticsActivity;
import com.example.allakumarreddy.moneybook.R;

import java.util.ArrayList;

public class FilterSubMenuAdapter extends RecyclerView.Adapter<FilterSubMenuAdapter.ViewHolder> {

    private final FiltersAnalyticsActivity mContext;
    private final ArrayList<String> mList;
    private final boolean mIsRadioOrCheck;
    private final ArrayList<Boolean> mSubMenuArrayList;

    public FilterSubMenuAdapter(FiltersAnalyticsActivity context, ArrayList<String> list, boolean isRadioOrCheck, ArrayList<Boolean> subMenuArrayList) {
        mContext = context;
        mList = list;
        mIsRadioOrCheck = isRadioOrCheck;
        mSubMenuArrayList = subMenuArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
        }
    }

    @Override
    public FilterSubMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_sub_menu_item, parent, false);
        FilterSubMenuAdapter.ViewHolder vh = new FilterSubMenuAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FilterSubMenuAdapter.ViewHolder holder, int position) {
        RadioButton radioButton = holder.mLinearLayout.findViewById(R.id.radioButton);
        CheckBox checkBox = holder.mLinearLayout.findViewById(R.id.checkBox);
        View view = null;
        if (mIsRadioOrCheck) {
            view = radioButton;
            checkBox.setVisibility(View.GONE);
            radioButton.setText(mList.get(position));
            radioButton.setChecked(mSubMenuArrayList.get(position));
        } else {
            view = checkBox;
            radioButton.setVisibility(View.GONE);
            checkBox.setText(mList.get(position));
            checkBox.setChecked(mSubMenuArrayList.get(position));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.subMenuItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
