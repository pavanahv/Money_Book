package com.example.allakumarreddy.moneybook.Adapter;

import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.Activities.MainActivity;
import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.utils.Utils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/19/2017.
 */

public class DashBoardAdapter extends ArrayAdapter<DashBoardRecord> {

    private final DbHandler db;
    private ArrayList<DashBoardRecord> dataSet;
    MainActivity mContext;


    // View lookup cache
    private static class ViewHolder {
        CircularProgressBar mCircularProgressbarDay;
        TextView mPercentDay;
        TextView mPriceDay;

        CircularProgressBar mCircularProgressbarMonth;
        TextView mPercentMonth;
        TextView mPriceMonth;

        CircularProgressBar mCircularProgressbarYear;
        TextView mPercentYear;
        TextView mPriceYear;

        FrameLayout imageButton;
        ImageView imageView;
        TextView mTextHead;

    }

    public DashBoardAdapter(ArrayList<DashBoardRecord> data, MainActivity context) {
        super(context, R.layout.dash_board_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.db = new DbHandler(mContext);
    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DashBoardRecord dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dash_board_item, parent, false);
            viewHolder.mCircularProgressbarDay = (CircularProgressBar) convertView.findViewById(R.id.apd);
            viewHolder.mPercentDay = (TextView) convertView.findViewById(R.id.dashpercentaged);
            viewHolder.mPriceDay = (TextView) convertView.findViewById(R.id.dashpriced);

            viewHolder.mCircularProgressbarMonth = (CircularProgressBar) convertView.findViewById(R.id.apm);
            viewHolder.mPercentMonth = (TextView) convertView.findViewById(R.id.dashpercentagem);
            viewHolder.mPriceMonth = (TextView) convertView.findViewById(R.id.dashpricem);

            viewHolder.mCircularProgressbarYear = (CircularProgressBar) convertView.findViewById(R.id.apy);
            viewHolder.mPercentYear = (TextView) convertView.findViewById(R.id.dashpercentagey);
            viewHolder.mPriceYear = (TextView) convertView.findViewById(R.id.dashpricey);

            viewHolder.imageButton = (FrameLayout) convertView.findViewById(R.id.ib);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ibtn);
            viewHolder.mTextHead = (TextView) convertView.findViewById(R.id.dashhead);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.goToAnalytics(dataModel.getText());
            }
        });

        final PopupMenu popup = new PopupMenu(mContext, viewHolder.imageView);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.dashboard_popmenu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboardPopupDelete:
                        db.deleteCategory(dataModel.getText().toLowerCase());
                        break;
                }
                Intent intent = mContext.getIntent();
                mContext.finish();
                mContext.startActivity(intent);
                return true;
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (dataModel.getText().compareToIgnoreCase("others") != 0) {
                    popup.show();
                } else {
                    Toast.makeText(mContext, "You Can't Delete Default Category", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        if (dataModel.getText().compareToIgnoreCase("others") != 0) {
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show(); //showing popup menu
                }
            });
        } else {
            viewHolder.imageButton.setVisibility(View.GONE);
        }


        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.mCircularProgressbarDay.setProgress(dataModel.getPercentD());
        viewHolder.mPercentDay.setText(dataModel.getPercentD() + " %");
        viewHolder.mPriceDay.setText(Utils.getFormattedNumber(dataModel.getDay()));

        viewHolder.mCircularProgressbarMonth.setProgress(dataModel.getPercentM());
        viewHolder.mPercentMonth.setText(dataModel.getPercentM() + " %");
        viewHolder.mPriceMonth.setText(Utils.getFormattedNumber(dataModel.getMonth()));

        viewHolder.mCircularProgressbarYear.setProgress(dataModel.getPercentY());
        viewHolder.mPercentYear.setText(dataModel.getPercentY() + " %");
        viewHolder.mPriceYear.setText(Utils.getFormattedNumber(dataModel.getYear()));

        viewHolder.mTextHead.setText(dataModel.getText());
        // Return the completed view to render on screen
        return convertView;
    }
}
