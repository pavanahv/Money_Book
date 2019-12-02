package com.example.allakumarreddy.moneybook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.interfaces.HomeAdapterInterface;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/19/2017.
 */

public class MyAdapter extends ArrayAdapter<MBRecord> {

    private final int type;
    private final HomeAdapterInterface mHomeAdapterInterface;
    private ArrayList<MBRecord> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView paymentMethod;
        TextView catName;
        TextView txtName;
        TextView txtRs;
        ImageView imageView;
        View statusView;
    }

    public MyAdapter(ArrayList<MBRecord> data, Context context, int type, HomeAdapterInterface homeAdapterInterface) {
        super(context, R.layout.record, data);
        this.dataSet = data;
        this.mContext = context;
        this.type = type;
        mHomeAdapterInterface = homeAdapterInterface;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MBRecord dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.record, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.desc);
            viewHolder.txtRs = (TextView) convertView.findViewById(R.id.price);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.homeiv);
            viewHolder.catName = (TextView) convertView.findViewById(R.id.categoryhome);
            viewHolder.paymentMethod = (TextView) convertView.findViewById(R.id.payment_method);
            viewHolder.statusView = convertView.findViewById(R.id.status_view);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getDescription());
        viewHolder.txtRs.setText("Rs. " + dataModel.getAmount());
        viewHolder.catName.setText(dataModel.getCategory());
        viewHolder.paymentMethod.setText(dataModel.getPaymentMethod());
        viewHolder.statusView.setVisibility(View.GONE);
        switch (dataModel.getType()) {
            case GlobalConstants.TYPE_SPENT:
                viewHolder.imageView.setImageResource(R.drawable.spent);
                break;

            case GlobalConstants.TYPE_EARN:
                viewHolder.imageView.setImageResource(R.drawable.earn);
                break;

            case GlobalConstants.TYPE_DUE:
                viewHolder.imageView.setImageResource(R.drawable.due);
                break;

            case GlobalConstants.TYPE_LOAN:
                viewHolder.imageView.setImageResource(R.drawable.ic_loan);
                break;

            case GlobalConstants.TYPE_DUE_PAYMENT:
                viewHolder.imageView.setImageResource(R.drawable.due);
                viewHolder.statusView.setVisibility(View.VISIBLE);
                break;

            case GlobalConstants.TYPE_LOAN_PAYMENT:
                viewHolder.imageView.setImageResource(R.drawable.ic_loan);
                viewHolder.statusView.setVisibility(View.VISIBLE);
                break;
        }
        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MBRecord mbr = getItem(position);
                mHomeAdapterInterface.onClickItem(mbr);
            }
        });
        return convertView;
    }
}
