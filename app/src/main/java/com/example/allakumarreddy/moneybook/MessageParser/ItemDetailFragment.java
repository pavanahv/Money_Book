package com.example.allakumarreddy.moneybook.MessageParser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;

public class ItemDetailFragment extends Fragment {

    private ChunksFragmentInteractionListener mListener;
    private TextView chunks;
    private EditText des;
    private EditText amount;
    private Spinner type;
    private Spinner cate;
    private String[] catArr;
    private DbHandler db;
    private EditText balLeft;

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    private String chunkText;

    public ItemDetailFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemDetailFragment newInstance() {
        ItemDetailFragment fragment = new ItemDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        chunks = (TextView) view.findViewById(R.id.chunks);
        chunks.append("\n" + chunkText);

        des = (EditText) view.findViewById(R.id.descitem);
        amount = (EditText) view.findViewById(R.id.amountitem);
        type = (Spinner) view.findViewById(R.id.typeitem);
        balLeft = (EditText) view.findViewById(R.id.leftOutBalance);
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Spent", "Earn", "Due", "Loan"});
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);

        cate = (Spinner) view.findViewById(R.id.catitem);
        catArr = db.getCategeories();
        ArrayAdapter caa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, catArr);
        caa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate.setAdapter(caa);

        ((ImageButton) view.findViewById(R.id.savbtn)).setOnClickListener(v -> save());

        return view;
    }

    private void save() {
        String desStr = des.getText().toString();
        String amountStr = amount.getText().toString();
        int typeInt = type.getSelectedItemPosition();
        String cateStr = catArr[cate.getSelectedItemPosition()];
        String balLeftStr = balLeft.getText().toString();
        mListener.saveFields(desStr,amountStr,typeInt,cateStr,balLeftStr);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChunksFragmentInteractionListener) {
            mListener = (ChunksFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
