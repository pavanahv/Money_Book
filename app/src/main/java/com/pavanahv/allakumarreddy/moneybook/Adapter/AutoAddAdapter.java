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

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.AutoAddAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.AutoAddRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.util.ArrayList;

public class AutoAddAdapter extends ArrayAdapter<AutoAddRecord> {
    private final AutoAddAdapterInterface mAutoAddAdapterInterface;
    private final PreferencesCus mPref;
    private ArrayList<AutoAddRecord> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView frequency;
        TextView paymentMethod;
        TextView catName;
        TextView txtName;
        TextView txtRs;
        ImageView imageView;
    }

    public AutoAddAdapter(ArrayList<AutoAddRecord> data, Context context,
                          AutoAddAdapterInterface autoAddAdapterInterface) {
        super(context, R.layout.record, data);
        this.dataSet = data;
        this.mContext = context;
        mAutoAddAdapterInterface = autoAddAdapterInterface;
        mPref = new PreferencesCus(context);
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AutoAddRecord dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        AutoAddAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new AutoAddAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.auto_add_record, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.desc);
            viewHolder.txtRs = (TextView) convertView.findViewById(R.id.price);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.homeiv);
            viewHolder.catName = (TextView) convertView.findViewById(R.id.categoryhome);
            viewHolder.paymentMethod = (TextView) convertView.findViewById(R.id.payment_method);
            viewHolder.frequency = (TextView) convertView.findViewById(R.id.freqeuncy);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AutoAddAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtRs.setText("Rs. " + dataModel.getAmount());
        viewHolder.catName.setText(dataModel.getCategory());
        viewHolder.paymentMethod.setText(dataModel.getPaymentMethod());
        String freq[] = new String[]{"Daily", "Weekly", "Monthly", "Yearly"};
        viewHolder.frequency.setText(freq[dataModel.getFreq()]);
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
        }
        Utils.setTint(mPref, viewHolder.imageView, dataModel.getType());
        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoAddRecord aar = getItem(position);
                mAutoAddAdapterInterface.onClickItem(aar);
            }
        });
        return convertView;
    }
}
