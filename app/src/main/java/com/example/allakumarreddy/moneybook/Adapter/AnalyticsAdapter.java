package com.example.allakumarreddy.moneybook.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Activities.AnalyticsActivity;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/19/2017.
 */

public class AnalyticsAdapter extends ArrayAdapter<MBRecord> {

    private final SimpleDateFormat format;
    private ArrayList<MBRecord> dataSet;
    AnalyticsActivity mContext;
    private boolean groupBy;

    public void setGroupBy(boolean groupBy) {
        this.groupBy = groupBy;
    }

    // View lookup cache
    private static class ViewHolder {
        public View subInfoLayout;
        public View groupByLayout;
        public TextView totalPrice;
        public TextView recordCount;
        TextView paymentMethod;
        TextView category;
        TextView txtName;
        TextView txtRs;
        TextView txtDate;
        ImageView im;
    }

    public AnalyticsAdapter(ArrayList<MBRecord> data, AnalyticsActivity context) {
        super(context, R.layout.record, data);
        this.dataSet = data;
        this.mContext = context;
        format = new SimpleDateFormat("dd - MM - yyyy");
        groupBy = false;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MBRecord dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final int pos = position;
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.analytics_record, parent, false);

            viewHolder.subInfoLayout = convertView.findViewById(R.id.sub_info_layout);
            viewHolder.groupByLayout = convertView.findViewById(R.id.group_by_layout);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.adesc);
            viewHolder.txtRs = (TextView) convertView.findViewById(R.id.aprice);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.adate);
            viewHolder.im = (ImageView) convertView.findViewById(R.id.imv);
            viewHolder.category = (TextView) convertView.findViewById(R.id.acategory);
            viewHolder.paymentMethod = (TextView) convertView.findViewById(R.id.apayment_method);
            viewHolder.totalPrice = (TextView) convertView.findViewById(R.id.tprice);
            viewHolder.recordCount = (TextView) convertView.findViewById(R.id.record_count);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startDetailActivity(pos);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        if (groupBy) {
            viewHolder.subInfoLayout.setVisibility(View.GONE);
            viewHolder.groupByLayout.setVisibility(View.VISIBLE);

            viewHolder.totalPrice.setText(""+dataModel.getAmount());
            String[] arr = dataModel.getDescription().split(" ");
            String temp = arr[arr.length - 1].trim();
            viewHolder.recordCount.setText(temp.substring(1, temp.length() - 1));
            temp = "";
            for(int i=0;i<arr.length-1;i++)
                temp += arr[i];
            temp.trim();
            viewHolder.txtName.setText(temp);
            viewHolder.im.setImageResource(R.drawable.ic_group_by);
        } else {
            viewHolder.subInfoLayout.setVisibility(View.VISIBLE);
            viewHolder.groupByLayout.setVisibility(View.GONE);

            viewHolder.txtName.setText(dataModel.getDescription());
            viewHolder.txtRs.setText("" + dataModel.getAmount());
            viewHolder.txtDate.setText(format.format(dataModel.getDate()));
            viewHolder.category.setText(dataModel.getCategory());
            viewHolder.paymentMethod.setText(dataModel.getPaymentMethod());
            switch (dataModel.getType()) {
                case 0:
                    viewHolder.im.setImageResource(R.drawable.spent);
                    break;

                case 1:
                    viewHolder.im.setImageResource(R.drawable.earn);
                    break;

                case 2:
                    viewHolder.im.setImageResource(R.drawable.due);
                    break;

                case 3:
                    viewHolder.im.setImageResource(R.drawable.ic_loan);
                    break;

                case 4:
                    viewHolder.im.setImageResource(R.drawable.ic_money_transfer);
                    break;
                default:
                    break;
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
