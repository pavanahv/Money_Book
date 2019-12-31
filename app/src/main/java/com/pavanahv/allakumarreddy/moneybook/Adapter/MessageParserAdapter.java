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
import com.pavanahv.allakumarreddy.moneybook.interfaces.MessageParserAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageParserAdapter extends ArrayAdapter<HashMap<String, String>> {
    private MessageParserAdapterInterface mMessageParserAdapterInterface;
    private final PreferencesCus mPref;
    private ArrayList<HashMap<String, String>> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView paymentMethod;
        TextView catName;
        TextView txtName;
        ImageView imageView;
    }

    public MessageParserAdapter(ArrayList<HashMap<String, String>> data, Context context,
                                MessageParserAdapterInterface messageParserAdapterInterface) {
        super(context, R.layout.msg_parser_record, data);
        this.dataSet = data;
        this.mContext = context;
        mMessageParserAdapterInterface = messageParserAdapterInterface;
        mPref = new PreferencesCus(context);
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HashMap<String, String> dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        MessageParserAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new MessageParserAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.msg_parser_record, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.desc);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.homeiv);
            viewHolder.catName = (TextView) convertView.findViewById(R.id.categoryhome);
            viewHolder.paymentMethod = (TextView) convertView.findViewById(R.id.payment_method);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MessageParserAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.get("name"));
        viewHolder.catName.setText(dataModel.get("cat"));
        viewHolder.paymentMethod.setText(dataModel.get("paym"));
        int type = Integer.parseInt(dataModel.get("type"));
        switch (type) {
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

            case GlobalConstants.TYPE_MONEY_TRANSFER:
                viewHolder.imageView.setImageResource(R.drawable.ic_money_transfer);
                break;

            case GlobalConstants.TYPE_DUE_PAYMENT:
                viewHolder.imageView.setImageResource(R.drawable.due);
                break;

            case GlobalConstants.TYPE_LOAN_PAYMENT:
                viewHolder.imageView.setImageResource(R.drawable.ic_loan);
                break;
        }
        Utils.setTint(mPref, viewHolder.imageView, type);

        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = getItem(position);
                mMessageParserAdapterInterface.onClickItem(map.get("name"));
            }
        });
        return convertView;
    }
}
