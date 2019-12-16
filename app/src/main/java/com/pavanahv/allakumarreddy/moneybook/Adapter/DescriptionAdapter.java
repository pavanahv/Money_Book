package com.pavanahv.allakumarreddy.moneybook.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pavanahv.allakumarreddy.moneybook.Activities.AddActivity;
import com.pavanahv.allakumarreddy.moneybook.R;

import java.util.ArrayList;

public class DescriptionAdapter extends ArrayAdapter<String>{

    private ArrayList<String> dataSet;
    AddActivity mContext;

    private static class ViewHolder {
        TextView txtName;
    }

    public DescriptionAdapter(ArrayList<String> data, AddActivity context) {
        super(context, R.layout.row_des_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final int fpos = position;
        String dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_des_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(dataModel);
        return convertView;
    }
}
