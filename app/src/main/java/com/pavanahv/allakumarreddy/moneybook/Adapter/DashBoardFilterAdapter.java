package com.pavanahv.allakumarreddy.moneybook.Adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pavanahv.allakumarreddy.moneybook.Activities.MainActivity;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.DashBoardFilterAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

public class DashBoardFilterAdapter extends RecyclerView.Adapter<DashBoardFilterAdapter.ViewHolder> {
    private static final String TAG = "DashBoardFilterAdapter";
    private final Context mContext;
    private DashBoardFilterAdapterInterface mDashBoardFilterAdapterInterface;
    private String[][] mJsonDataTitle;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DashBoardFilterAdapter(String[][] jsonDataTitle, MainActivity context) {
        mJsonDataTitle = jsonDataTitle;
        mContext = context;
    }

    public DashBoardFilterAdapter(String[][] jsonDataTitle, Context context, DashBoardFilterAdapterInterface dashBoardFilterAdapterInterface) {
        mJsonDataTitle = jsonDataTitle;
        mContext = context;
        mDashBoardFilterAdapterInterface = dashBoardFilterAdapterInterface;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DashBoardFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dash_board_filter_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mLinearLayout.setText(mDataset[position]);
        View view = Utils.getGraphFromParelableJSONString(mJsonDataTitle[position][1], mContext);
        ((TextView) holder.mLinearLayout.findViewById(R.id.title)).setText(mJsonDataTitle[position][0].toUpperCase());
        LinearLayout linearLayout = (LinearLayout) holder.mLinearLayout.findViewById(R.id.cv);
        linearLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        final PopupMenu popup = new PopupMenu(mContext, ((ImageButton) holder.mLinearLayout.findViewById(R.id.menu)));
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.dashboard_filters_popmenu, popup.getMenu());

        ((ImageButton) holder.mLinearLayout.findViewById(R.id.menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.dashboardPopupDelete:
                    mDashBoardFilterAdapterInterface.delteFilter(mJsonDataTitle[position][0]);
                    break;

                case R.id.dashboardPopupFullScreen:
                    mDashBoardFilterAdapterInterface.fullScreen(mJsonDataTitle[position][1]);
                    break;
            }
            return true;
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mJsonDataTitle.length;
    }
}
