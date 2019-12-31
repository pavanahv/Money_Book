package com.pavanahv.allakumarreddy.moneybook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.BudgetAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetAdapter extends ArrayAdapter<HashMap<String, String>> {

    private final BudgetAdapterInterface mBudgetAdapterInterface;
    private final PreferencesCus mPref;
    private ArrayList<HashMap<String, String>> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView interval;
        TextView date;
        TextView limit;
        TextView spent;
        TextView balLeft;
        CircularProgressBar circularProgress;
        TextView avg;
        ImageView expired;
    }

    public BudgetAdapter(ArrayList<HashMap<String, String>> data, Context context,
                         BudgetAdapterInterface budgetAdapterInterface) {
        super(context, R.layout.record, data);
        this.dataSet = data;
        this.mContext = context;
        mBudgetAdapterInterface = budgetAdapterInterface;
        mPref = new PreferencesCus(context);
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HashMap<String, String> dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        BudgetAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new BudgetAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.budget_record, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.limit = (TextView) convertView.findViewById(R.id.limit);
            viewHolder.circularProgress = (CircularProgressBar) convertView.findViewById(R.id.cp);
            viewHolder.interval = (TextView) convertView.findViewById(R.id.interval);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.balLeft = (TextView) convertView.findViewById(R.id.bal_left);
            viewHolder.spent = (TextView) convertView.findViewById(R.id.spent);
            viewHolder.avg = (TextView) convertView.findViewById(R.id.avg);
            viewHolder.expired = (ImageView) convertView.findViewById(R.id.expired);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BudgetAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.name.setText(dataModel.get("name"));
        viewHolder.limit.setText(dataModel.get("limit"));
        viewHolder.interval.setText(dataModel.get("interval"));
        viewHolder.date.setText(dataModel.get("date"));
        viewHolder.spent.setText(dataModel.get("spent"));
        viewHolder.balLeft.setText(dataModel.get("balLeft"));
        viewHolder.circularProgress.setProgress(Float.parseFloat(dataModel.get("progress")));
        int tempColor = Utils.getSpentColor(mPref);
        viewHolder.circularProgress.setBackgroundColor(tempColor);
        viewHolder.circularProgress.setColor(tempColor);
        viewHolder.spent.setTextColor(tempColor);
        viewHolder.avg.setText(dataModel.get("avg"));
        int balLeft = Integer.parseInt(dataModel.get("balLeft"));
        if (balLeft < 0)
            viewHolder.expired.setVisibility(View.VISIBLE);
        else
            viewHolder.expired.setVisibility(View.GONE);


        // Return the completed view to render on screen
        convertView.setOnClickListener(v -> {
            HashMap<String, String> mbr = getItem(position);
            mBudgetAdapterInterface.onClickItem(mbr.get("name"));
        });
        return convertView;
    }
}
