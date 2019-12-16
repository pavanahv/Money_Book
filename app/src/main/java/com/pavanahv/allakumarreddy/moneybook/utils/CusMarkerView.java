package com.pavanahv.allakumarreddy.moneybook.utils;

import android.content.Context;
import android.widget.TextView;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CusMarkerView extends MarkerView {
    private final TextView tvContent;

    public CusMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(com.github.mikephil.charting.utils.Utils.formatNumber(ce.getHigh(),
                    0, true));
        } else {
            tvContent.setText(com.github.mikephil.charting.utils.Utils.formatNumber(e.getY(),
                    0, true, ','));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
