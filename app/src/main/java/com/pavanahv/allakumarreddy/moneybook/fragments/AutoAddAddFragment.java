package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AutoAddRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AutoAddAddFragment extends Fragment {

    private static final String TAG = AutoAddAddFragment.class.getSimpleName();
    private static final String AUTO_ADD_RECORD = "AUTO_ADD_RECORD";
    private OnAutoAddAddFragmentInteractionListener mListener;
    private boolean isAddMode;
    private View nameTextView;
    private EditText name;
    private View desTextView;
    private EditText des;
    private View amountTextView;
    private EditText amount;
    private View typeTextView;
    private Spinner type;
    private View categroyTextView;
    private Spinner category;
    private String[] catArr;
    private DbHandler db;
    private View paymentMethodTextView;
    private Spinner paymentMethod;
    private View freqTextView;
    private Spinner freq;
    private String[] payMethArr;
    private View freqDataTextView;
    private Spinner freqData;
    private CalendarView calenderView;
    private EditText calText;
    private AutoAddRecord mAutoAddRecordOld;
    private Button addButton;
    private Button canelButton;
    private boolean isEditable = false;

    public AutoAddAddFragment() {
        // Required empty public constructor
    }


    public static AutoAddAddFragment newInstance(String name) {
        AutoAddAddFragment fragment = new AutoAddAddFragment();
        Bundle args = new Bundle();
        args.putString(AUTO_ADD_RECORD, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new DbHandler(getContext());
        isAddMode = true;
        if (getArguments() != null) {
            String temp = getArguments().getString(AUTO_ADD_RECORD);
            if (temp.compareToIgnoreCase("") != 0) {
                isAddMode = false;
                mAutoAddRecordOld = db.getAutoAddRecord(temp);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auto_add_add, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        nameTextView = view.findViewById(R.id.namet);
        name = (EditText) view.findViewById(R.id.name);

        desTextView = view.findViewById(R.id.dest);
        des = (EditText) view.findViewById(R.id.des);

        amountTextView = view.findViewById(R.id.amountt);
        amount = (EditText) view.findViewById(R.id.amount);

        typeTextView = view.findViewById(R.id.typet);
        type = (Spinner) view.findViewById(R.id.type);
        ArrayAdapter ta = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Spent", "Earn", "Due", "Loan"});
        type.setAdapter(ta);

        categroyTextView = view.findViewById(R.id.categoryt);
        category = (Spinner) view.findViewById(R.id.category);
        initCategory(GlobalConstants.TYPE_SPENT, GlobalConstants.OTHERS_CAT);

        paymentMethodTextView = view.findViewById(R.id.payment_methodt);
        paymentMethod = (Spinner) view.findViewById(R.id.payment_method);
        initPaymentMethod(null);

        freqDataTextView = view.findViewById(R.id.freq2t);
        freqData = (Spinner) view.findViewById(R.id.freq2);
        calenderView = (CalendarView) view.findViewById(R.id.calendarView);
        calText = (EditText) view.findViewById(R.id.calText);
        initCalenderView();
        setCalText(new Date().getTime());

        freqTextView = view.findViewById(R.id.freq1t);
        freq = (Spinner) view.findViewById(R.id.freq1);
        ArrayAdapter fa = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Daily", "Weekly", "Monthly", "Yearly"});
        freq.setAdapter(fa);

        addButton = ((Button) view.findViewById(R.id.add));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked();
            }
        });

        canelButton = ((Button) view.findViewById(R.id.cancel));
        canelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });
        if (isAddMode) {
            nameTextView.setVisibility(View.GONE);
            desTextView.setVisibility(View.GONE);
            amountTextView.setVisibility(View.GONE);
            addSelectors();
        } else {
            addButton.setVisibility(View.GONE);
            nameTextView.setVisibility(View.VISIBLE);
            desTextView.setVisibility(View.VISIBLE);
            amountTextView.setVisibility(View.VISIBLE);
            name.setText(mAutoAddRecordOld.getName());
            des.setText(mAutoAddRecordOld.getDesciption());
            amount.setText(mAutoAddRecordOld.getAmount() + "");
            type.setSelection(mAutoAddRecordOld.getType());
            initCategory(mAutoAddRecordOld.getType(), mAutoAddRecordOld.getCategory());
            freq.setSelection(mAutoAddRecordOld.getFreq());
            initFreqData(mAutoAddRecordOld.getFreq());
            initPaymentMethod(mAutoAddRecordOld.getPaymentMethod());
            switch (mAutoAddRecordOld.getFreq()) {
                case 1:
                case 2:
                    int temp = Integer.parseInt(mAutoAddRecordOld.getFreqData());
                    freqData.setSelection(temp);
                    break;
                case 3:
                    long time = Long.parseLong(mAutoAddRecordOld.getFreqData());
                    calenderView.setDate(time);
                    setCalText(time);
                    break;
            }
            setEnable();
            removeSelectors();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.auto_add_add_fragment_menu, menu);
        MenuItem saveMenu = menu.findItem(R.id.save);
        if (isAddMode || isEditable) {
            saveMenu.setVisible(true);
        } else {
            saveMenu.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.delete:
                deleteClicked();
                break;
            case R.id.save:
                saveClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteClicked() {
        boolean res = db.deleteAutoRecord(mAutoAddRecordOld.getName());
        if (res) {
            Toast.makeText(getContext(), "Deleted Successfully !", Toast.LENGTH_SHORT).show();
            cancelClicked();
        } else {
            Toast.makeText(getContext(), "Error In Deleting !", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEnable() {
        name.setEnabled(isEditable);
        des.setEnabled(isEditable);
        calenderView.setEnabled(isEditable);
        freqData.setEnabled(isEditable);
        freq.setEnabled(isEditable);
        amount.setEnabled(isEditable);
        type.setEnabled(isEditable);
        paymentMethod.setEnabled(isEditable);
        category.setEnabled(isEditable);
    }

    private void removeSelectors() {
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // do nothing
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        freq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // do nothing
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addSelectors() {

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initCategory(position, GlobalConstants.OTHERS_CAT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        freq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initFreqData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCalenderView() {
        calenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setCalText(cal.getTimeInMillis());
            }
        });
    }

    private void setCalText(long date) {
        calText.setText(new SimpleDateFormat("dd - MM").format(new Date(date)));
    }

    private void cancelClicked() {
        mListener.onCancel();
    }

    private void saveClicked() {
        boolean res = saveData();
        if (res) {
            Toast.makeText(getContext(), "Successfully Saved !", Toast.LENGTH_SHORT).show();
            mListener.onSave();
        } else
            Toast.makeText(getContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
    }

    private boolean saveData() {
        AutoAddRecord autoAddRecord = new AutoAddRecord();
        autoAddRecord.setName(name.getText().toString());
        autoAddRecord.setDesciption(des.getText().toString());
        autoAddRecord.setAmount(Integer.parseInt(amount.getText().toString()));
        autoAddRecord.setType(type.getSelectedItemPosition());
        autoAddRecord.setCategory(catArr[category.getSelectedItemPosition()]);
        autoAddRecord.setPaymentMethod(payMethArr[paymentMethod.getSelectedItemPosition()]);
        int tempPos = freq.getSelectedItemPosition();
        autoAddRecord.setFreq(tempPos);
        switch (tempPos) {
            case 0:
                autoAddRecord.setFreqData(null);
                break;
            case 1:
            case 2:
                autoAddRecord.setFreqData(freqData.getSelectedItemPosition() + "");
                break;
            case 3:
                try {
                    long time = new SimpleDateFormat("dd - MM").parse(calText.getText().toString()).getTime();
                    autoAddRecord.setFreqData(time + "");
                } catch (ParseException e) {
                    LoggerCus.d(TAG, e.getMessage());
                    autoAddRecord.setFreqData(null);
                }
                break;
            default:
                break;

        }
        return db.addAutoAddRecord(autoAddRecord);
    }

    private void initFreqData(int type) {
        String data[] = new String[]{"None"};
        switch (type) {
            case 1:
                data = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                break;
            case 2:
                data = new String[28];
                for (int i = 0; i < 28; i++) {
                    data[i] = (i + 1) + "";
                }
                break;
            case 3:
                break;
            default:
                break;
        }

        if (type == 3) {
            hideFreqData();
            showCalender();
        } else if (type == 0) {
            hideFreqData();
            hideCalender();
        } else if (type == 1 || type == 2) {
            ArrayAdapter fa = new ArrayAdapter(getContext(),
                    android.R.layout.simple_spinner_dropdown_item, data);
            freqData.setAdapter(fa);
            hideCalender();
            showFreqData();
        }
    }

    private void hideFreqData() {
        freqDataTextView.setVisibility(View.GONE);
        freqData.setVisibility(View.GONE);
    }

    private void showCalender() {
        calenderView.setVisibility(View.VISIBLE);
        calText.setVisibility(View.VISIBLE);
    }

    private void hideCalender() {
        calenderView.setVisibility(View.GONE);
        calText.setVisibility(View.GONE);
    }

    private void showFreqData() {
        freqDataTextView.setVisibility(View.VISIBLE);
        freqData.setVisibility(View.VISIBLE);
    }

    private void initPaymentMethod(String name) {
        String temp = GlobalConstants.OTHERS_CAT;
        if (name != null) {
            temp = name;
        }
        payMethArr = db.getPaymentMethods();
        int pCurind = -1;

        // initialize with others payment method
        for (int i = 0; i < payMethArr.length; i++) {
            if (payMethArr[i].compareToIgnoreCase(temp) == 0) {
                pCurind = i;
                break;
            }
        }

        ArrayAdapter paa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, payMethArr);
        paymentMethod.setAdapter(paa);
        paymentMethod.setSelection(pCurind);
    }

    private void initCategory(int type, String cat) {
        catArr = db.getCategeories(type);
        int curind = -1;
        // initialize with others category
        for (int i = 0; i < catArr.length; i++) {
            if (catArr[i].compareToIgnoreCase(GlobalConstants.OTHERS_CAT) == 0) {
                curind = i;
                break;
            }
        }

        if (!(cat.compareToIgnoreCase(GlobalConstants.OTHERS_CAT) == 0)) {
            if (cat != null) {
                for (int i = 0; i < catArr.length; i++) {
                    if (catArr[i].compareToIgnoreCase(cat) == 0) {
                        curind = i;
                        break;
                    }
                }
            }
        }

        ArrayAdapter caa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, catArr);
        caa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(caa);
        category.setSelection(curind);
        caa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAutoAddAddFragmentInteractionListener) {
            mListener = (OnAutoAddAddFragmentInteractionListener) context;
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

    public interface OnAutoAddAddFragmentInteractionListener {
        void onSave();

        void onCancel();
    }

}
