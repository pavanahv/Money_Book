package com.example.allakumarreddy.moneybook.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.interfaces.ReportHeaderAdapterListener;

import java.util.ArrayList;

public class ReportHeaderAdapter extends ArrayAdapter<String[]> {
    private final ReportHeaderAdapterListener mReportHeaderAdapterListener;
    private ArrayList<String[]> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView amount;
        CardView circleColor;
        CardView barColor;
    }

    public ReportHeaderAdapter(ArrayList<String[]> data, Context context,
                               ReportHeaderAdapterListener reportHeaderAdapterListener) {
        super(context, R.layout.record, data);
        this.dataSet = data;
        this.mContext = context;
        mReportHeaderAdapterListener = reportHeaderAdapterListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String[] dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ReportHeaderAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ReportHeaderAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.repors_header, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.amount = (TextView) convertView.findViewById(R.id.amount);
            viewHolder.circleColor = (CardView) convertView.findViewById(R.id.cicle_color);
            viewHolder.barColor = (CardView) convertView.findViewById(R.id.bar_color);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReportHeaderAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(dataModel[0]);
        String amt = dataModel[1];
        if (amt != null) {
            if (dataModel[2] != null) {
                viewHolder.circleColor.setCardBackgroundColor(Integer.parseInt(dataModel[2]));
            }
            viewHolder.circleColor.setVisibility(View.VISIBLE);
            viewHolder.barColor.setVisibility(View.GONE);
            viewHolder.amount.setText(amt);
        } else {
            if (dataModel[2] != null)
                viewHolder.barColor.setCardBackgroundColor(Integer.parseInt(dataModel[2]));
            viewHolder.barColor.setVisibility(View.VISIBLE);
            viewHolder.circleColor.setVisibility(View.GONE);
            viewHolder.amount.setVisibility(View.GONE);
        }


        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReportHeaderAdapterListener.itemClicked(position);
            }
        });
        return convertView;
    }
}
