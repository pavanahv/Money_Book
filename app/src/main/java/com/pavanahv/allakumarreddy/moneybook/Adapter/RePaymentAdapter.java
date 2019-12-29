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
import com.pavanahv.allakumarreddy.moneybook.interfaces.RePaymentAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RePaymentAdapter extends ArrayAdapter<MBRecord> {

    private final SimpleDateFormat format;
    private final RePaymentAdapterInterface mRePaymentAdapterInterface;
    private final Context mContext;
    private final PreferencesCus mPref;

    // View lookup cache
    private static class ViewHolder {
        TextView paymentMethod;
        TextView category;
        TextView txtRs;
        TextView txtDate;
        ImageView im;
    }

    public RePaymentAdapter(Context context, ArrayList<MBRecord> data,
                            RePaymentAdapterInterface rePaymentAdapterInterface) {
        super(context, R.layout.repyament_record, data);
        this.mContext = context;
        format = new SimpleDateFormat("dd - MM - yyyy");
        mRePaymentAdapterInterface = rePaymentAdapterInterface;
        mPref = new PreferencesCus(context);
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MBRecord dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        RePaymentAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final int pos = position;
        final View result;

        if (convertView == null) {

            viewHolder = new RePaymentAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.repyament_record, parent, false);

            viewHolder.txtRs = (TextView) convertView.findViewById(R.id.aprice);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.adate);
            viewHolder.im = (ImageView) convertView.findViewById(R.id.imv);
            viewHolder.category = (TextView) convertView.findViewById(R.id.acategory);
            viewHolder.paymentMethod = (TextView) convertView.findViewById(R.id.apayment_method);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RePaymentAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        View finalConvertView = convertView;
        convertView.setOnClickListener(v -> mRePaymentAdapterInterface.onClickItem(dataModel, finalConvertView));

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtRs.setText("" + dataModel.getAmount());
        viewHolder.txtDate.setText(format.format(dataModel.getDate()));
        viewHolder.category.setText(dataModel.getCategory());
        viewHolder.paymentMethod.setText(dataModel.getPaymentMethod());
        switch (dataModel.getType()) {
            case 7:
                viewHolder.im.setImageResource(R.drawable.due_payment);
                break;

            case 8:
                viewHolder.im.setImageResource(R.drawable.loan_payment);
                break;

            default:
                break;
        }
        Utils.setTint(mPref, viewHolder.im, dataModel.getType());
        // Return the completed view to render on screen
        return convertView;
    }
}
