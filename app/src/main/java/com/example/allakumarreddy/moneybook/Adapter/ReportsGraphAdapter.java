package com.example.allakumarreddy.moneybook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.interfaces.ReportsAdapterInterface;
import com.example.allakumarreddy.moneybook.utils.ReportData;

import java.util.ArrayList;

public class ReportsGraphAdapter extends ArrayAdapter<ReportData> {

    private final ArrayList<ReportData> dataSet;
    private final Context mContext;
    private final ReportsAdapterInterface mReportsAdapterInterface;

    private static class ViewHolder {
        LinearLayout mainView;
        View button;
    }

    public ReportsGraphAdapter(ArrayList<ReportData> data, Context context, ReportsAdapterInterface reportsAdapterInterface) {
        super(context, R.layout.report_list_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.mReportsAdapterInterface = reportsAdapterInterface;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ReportData dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ReportsGraphAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ReportsGraphAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.report_list_item, parent, false);
            viewHolder.mainView = convertView.findViewById(R.id.main_view);
            viewHolder.button = convertView.findViewById(R.id.imv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReportsGraphAdapter.ViewHolder) convertView.getTag();
        }
        if (viewHolder.mainView.getChildCount() > 0)
            viewHolder.mainView.removeAllViews();

        viewHolder.mainView.addView(dataModel.getGraphView(),
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.report_graph_height)));

        // Return the completed view to render on screen
        View finalConvertView = convertView;
        viewHolder.button.setOnClickListener(v -> mReportsAdapterInterface.onGraphClicked(position, finalConvertView, dataModel));
        return convertView;
    }

}
