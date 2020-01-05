package com.pavanahv.allakumarreddy.moneybook.Adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.DashBoardAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/19/2017.
 */

public class DashBoardAdapter extends ArrayAdapter<DashBoardRecord> {

    private final DbHandler db;
    private final int type;
    private final PreferencesCus mPref;
    private DashBoardAdapterInterface mDashBoardAdapterInterface;
    private ArrayList<DashBoardRecord> dataSet;
    Context mContext;


    // View lookup cache
    private static class ViewHolder {
        TextView leftBal;
        View leftOutBalView;
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

    public DashBoardAdapter(ArrayList<DashBoardRecord> data, Context context,
                            DashBoardAdapterInterface dashBoardAdapterInterface, boolean isPaymentMethod) {
        super(context, R.layout.dash_board_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.db = new DbHandler(mContext);
        mDashBoardAdapterInterface = dashBoardAdapterInterface;
        if (isPaymentMethod)
            this.type = 2;
        else
            this.type = 1;
        mPref = new PreferencesCus(context);
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
            viewHolder.leftOutBalView = convertView.findViewById(R.id.left_bal_view);
            viewHolder.leftBal = (TextView) convertView.findViewById(R.id.left_bal);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        View finalConvertView = convertView;
        convertView.setOnClickListener(v -> mDashBoardAdapterInterface.viewOnClick(dataModel.getText(), finalConvertView));

        final PopupMenu popup = new PopupMenu(mContext, viewHolder.imageView);
        //Inflating the Popup using xml file
        if (type == 1) {
            popup.getMenuInflater()
                    .inflate(R.menu.dashboard_popmenu, popup.getMenu());
        } else {
            popup.getMenuInflater()
                    .inflate(R.menu.payment_popmenu, popup.getMenu());
        }

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.dashboardPopupDelete:
                    mDashBoardAdapterInterface.deleteCategoryPopMenu(dataModel.getText());
                    break;

                case R.id.dashboardPopupMT:
                    mDashBoardAdapterInterface.moneyTrasferPopMenu(dataModel.getText());
                    break;
            }
            return true;
        });

        convertView.setOnLongClickListener(v -> {
            if (dataModel.getText().compareToIgnoreCase("others") != 0) {
                popup.show();
            } else {
                Toast.makeText(mContext, "You Can't Delete Default Category", Toast.LENGTH_LONG).show();
            }
            return false;
        });

        if (dataModel.getText().compareToIgnoreCase("others") != 0) {
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show(); //showing popup menu
                }
            });
            viewHolder.imageButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageButton.setVisibility(View.GONE);
        }


        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/

        int dayColor = Utils.getDayWiseColor(mPref);
        viewHolder.mCircularProgressbarDay.setColor(dayColor);
        viewHolder.mCircularProgressbarDay.setBackgroundColor(dayColor);
        viewHolder.mPercentDay.setTextColor(dayColor);
        viewHolder.mPriceDay.setTextColor(dayColor);

        viewHolder.mCircularProgressbarDay.setProgress(dataModel.getPercentD());
        viewHolder.mPercentDay.setText(dataModel.getPercentD() + " %");
        viewHolder.mPriceDay.setText(Utils.getFormattedNumber(dataModel.getDay()));

        int monthColor = Utils.getMonthWiseColor(mPref);
        viewHolder.mCircularProgressbarMonth.setColor(monthColor);
        viewHolder.mCircularProgressbarMonth.setBackgroundColor(monthColor);
        viewHolder.mPercentMonth.setTextColor(monthColor);
        viewHolder.mPriceMonth.setTextColor(monthColor);

        viewHolder.mCircularProgressbarMonth.setProgress(dataModel.getPercentM());
        viewHolder.mPercentMonth.setText(dataModel.getPercentM() + " %");
        viewHolder.mPriceMonth.setText(Utils.getFormattedNumber(dataModel.getMonth()));

        int yearColor = Utils.getYearWiseColor(mPref);
        viewHolder.mCircularProgressbarYear.setColor(yearColor);
        viewHolder.mCircularProgressbarYear.setBackgroundColor(yearColor);
        viewHolder.mPercentYear.setTextColor(yearColor);
        viewHolder.mPriceYear.setTextColor(yearColor);

        viewHolder.mCircularProgressbarYear.setProgress(dataModel.getPercentY());
        viewHolder.mPercentYear.setText(dataModel.getPercentY() + " %");
        viewHolder.mPriceYear.setText(Utils.getFormattedNumber(dataModel.getYear()));

        viewHolder.mTextHead.setText(dataModel.getText());

        if (type == 1) {
            viewHolder.leftOutBalView.setVisibility(View.GONE);
        } else {
            if (mPref.getMessageParserLeftOutBal()) {
                viewHolder.leftOutBalView.setVisibility(View.VISIBLE);
                viewHolder.leftBal.setText(Utils.getFormattedNumber(dataModel.getBalanceLeft()));
            } else {
                viewHolder.leftOutBalView.setVisibility(View.GONE);
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
