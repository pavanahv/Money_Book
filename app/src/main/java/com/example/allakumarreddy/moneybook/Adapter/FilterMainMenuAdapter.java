package com.example.allakumarreddy.moneybook.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Activities.FiltersAnalyticsActivity;
import com.example.allakumarreddy.moneybook.R;

import java.util.ArrayList;

public class FilterMainMenuAdapter extends RecyclerView.Adapter<FilterMainMenuAdapter.ViewHolder> {
    private final FiltersAnalyticsActivity mContext;
    private final ArrayList<String> mList;

    public FilterMainMenuAdapter(FiltersAnalyticsActivity context, ArrayList<String> list) {
        mContext = context;
        mList = list;
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
    public FilterMainMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_main_menu_item, parent, false);
        FilterMainMenuAdapter.ViewHolder vh = new FilterMainMenuAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView tv = holder.mLinearLayout.findViewById(R.id.menutitle);
        tv.setText(mList.get(position));
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.mainMenuItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
