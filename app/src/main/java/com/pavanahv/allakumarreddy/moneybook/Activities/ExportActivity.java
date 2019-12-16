package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExportActivity extends BaseActivity {

    private static final String TAG = ExportActivity.class.getSimpleName();
    private CheckBox[] checkBox;
    private Spinner spinner;
    private String fileName = "Exported_Data";
    private boolean res = false;
    private ArrayList<MBRecord> list;
    private boolean[] indexChecked;
    private String[] rowHeader;
    private SimpleDateFormat smp;
    private File currentMediaFile;
    private View mainView;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Export To File");

        init();
        initData();
    }

    private void init() {
        mainView = findViewById(R.id.main_view);
        progress = findViewById(R.id.progress);
        smp = new SimpleDateFormat("dd / MM / yyyy");
        checkBox = new CheckBox[7];

        checkBox[0] = (CheckBox) findViewById(R.id.sno);
        checkBox[1] = (CheckBox) findViewById(R.id.des);
        checkBox[2] = (CheckBox) findViewById(R.id.amt);
        checkBox[3] = (CheckBox) findViewById(R.id.date);
        checkBox[4] = (CheckBox) findViewById(R.id.type);
        checkBox[5] = (CheckBox) findViewById(R.id.cat);
        checkBox[6] = (CheckBox) findViewById(R.id.paym);

        spinner = (Spinner) findViewById(R.id.type_ex);
        String type[] = new String[]{"Excel", "CSV", "JSON", "HTML", "TEXT"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, type);
        spinner.setAdapter(arrayAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                goBack();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            list = (ArrayList<MBRecord>) intent.getSerializableExtra("list");
            LoggerCus.d(TAG, "Size" + list.size());
        }
    }

    public void cancel(View view) {
        goBack();
    }

    private void goBack() {
        finish();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    public void export(View view) {
        indexChecked = new boolean[7];
        for (int i = 0; i < checkBox.length; i++) {
            indexChecked[i] = checkBox[i].isChecked();
        }

        showProgress();
        new Thread(() -> {
            rowHeader = new String[]{"S.NO", "Description", "Amount", "Date",
                    "Type", "Category", "Payment Method"};

            int pos = spinner.getSelectedItemPosition();
            switch (pos) {
                case 0:
                    // Excel
                    storeDataAsEXCEL();
                    break;
                case 1:
                    // CSV
                    storeDataAsCSV();
                    break;
                case 2:
                    // JSON
                    storeDataAsJSON();
                    break;
                case 3:
                    // HTML
                    storeDataAsHTML();
                    break;
                case 4:
                    // TEXT
                    storeDataAsTEXT();
                    break;
                default:
                    break;
            }
            runOnUiThread(() -> {
                hideProgress();
                if (res) {
                    Toast.makeText(ExportActivity.this, "Successfully Exported " + list.size() + " Records !\n" + currentMediaFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ExportActivity.this, "Something Went Wrong\nPlease Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void storeDataAsHTML() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"en\">");
        sb.append("<head>");
        sb.append("<title>Money Book Records</title>");
        sb.append("<meta charset=\"utf-8\">");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css\">");
        sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>");
        sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js\"></script>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div class=\"container\">");
        sb.append("<h2>Money Book Records</h2>");
        sb.append("<table class=\"table table-hover table-striped\">");
        sb.append("<thead>");
        sb.append("<tr>");
        for (int i = 0; i < rowHeader.length; i++) {
            if (indexChecked[i]) {
                sb.append("<th>");
                sb.append(rowHeader[i]);
                sb.append("</th>");
            }
        }
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        int rowCount = 1;
        for (MBRecord mbr : list) {
            String mbrStr[] = new String[rowHeader.length];
            mbrStr[0] = rowCount + "";
            mbrStr[1] = mbr.getDescription();
            mbrStr[2] = mbr.getAmount() + "";
            mbrStr[3] = smp.format(mbr.getDate());
            mbrStr[4] = getTypeString(mbr.getType());
            mbrStr[5] = mbr.getCategory();
            mbrStr[6] = mbr.getPaymentMethod();

            sb.append("<tr>");
            final int len = mbrStr.length;
            for (int i = 0; i < len; i++) {
                if (indexChecked[i]) {
                    sb.append("<td>");
                    sb.append(mbrStr[i]);
                    sb.append("</td>");
                }
            }
            sb.append("</tr>");
            rowCount++;
        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        try {
            RandomAccessFile raf = new RandomAccessFile(getMediaFile(".html"), "rw");
            raf.writeBytes(sb.toString());
            raf.close();
            res = true;
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        }
    }

    private void storeDataAsTEXT() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < rowHeader.length; i++) {
            if (indexChecked[i]) {
                sb.append(rowHeader[i]);
                sb.append(",");
            }
        }
        sb.append("\n");
        int rowCount = 1;
        for (MBRecord mbr : list) {
            String mbrStr[] = new String[rowHeader.length];
            mbrStr[0] = rowCount + "";
            mbrStr[1] = mbr.getDescription();
            mbrStr[2] = mbr.getAmount() + "";
            mbrStr[3] = smp.format(mbr.getDate());
            mbrStr[4] = getTypeString(mbr.getType());
            mbrStr[5] = mbr.getCategory();
            mbrStr[6] = mbr.getPaymentMethod();

            final int len = mbrStr.length;
            for (int i = 0; i < len; i++) {
                if (indexChecked[i]) {
                    sb.append(mbrStr[i]);
                    sb.append(";");
                }
            }
            sb.append("\n");
            rowCount++;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(getMediaFile(".txt"), "rw");
            raf.writeBytes(sb.toString());
            raf.close();
            res = true;
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        }
    }

    private void storeDataAsJSON() {
        JSONArray arr = new JSONArray();
        int rowCount = 1;
        for (MBRecord mbr : list) {
            String mbrStr[] = new String[rowHeader.length];
            mbrStr[0] = rowCount + "";
            mbrStr[1] = mbr.getDescription();
            mbrStr[2] = mbr.getAmount() + "";
            mbrStr[3] = smp.format(mbr.getDate());
            mbrStr[4] = getTypeString(mbr.getType());
            mbrStr[5] = mbr.getCategory();
            mbrStr[6] = mbr.getPaymentMethod();

            JSONObject obj = new JSONObject();
            final int len = mbrStr.length;
            for (int i = 0; i < len; i++) {
                if (indexChecked[i]) {
                    try {
                        obj.put(rowHeader[i], mbrStr[i]);
                    } catch (JSONException e) {
                        LoggerCus.d(TAG, e.getMessage());
                        res = false;
                    }
                }
            }
            arr.put(obj);
            rowCount++;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("Records", arr);
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(getMediaFile(".json"), "rw");
            raf.writeBytes(obj.toString());
            raf.close();
            res = true;
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        }
    }

    private void storeDataAsCSV() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < rowHeader.length; i++) {
            if (indexChecked[i]) {
                sb.append(rowHeader[i]);
                sb.append(",");
            }
        }
        sb.append("\n");
        int rowCount = 1;
        for (MBRecord mbr : list) {
            String mbrStr[] = new String[rowHeader.length];
            mbrStr[0] = rowCount + "";
            mbrStr[1] = mbr.getDescription();
            mbrStr[2] = mbr.getAmount() + "";
            mbrStr[3] = smp.format(mbr.getDate());
            mbrStr[4] = getTypeString(mbr.getType());
            mbrStr[5] = mbr.getCategory();
            mbrStr[6] = mbr.getPaymentMethod();

            final int len = mbrStr.length;
            for (int i = 0; i < len; i++) {
                if (indexChecked[i]) {
                    sb.append(mbrStr[i]);
                    sb.append(",");
                }
            }
            sb.append("\n");
            rowCount++;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(getMediaFile(".csv"), "rw");
            raf.writeBytes(sb.toString());
            raf.close();
            res = true;
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        }

    }

    private void showProgress() {
        mainView.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mainView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void storeDataAsEXCEL() {
        try {
            writeXL();
            res = true;
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
            res = false;
        }
    }

    private void writeXL() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int rowCount = 0;
        Row r = sheet.createRow(rowCount);
        rowCount++;

        int cellCount = 0;
        for (int i = 0; i < rowHeader.length; i++) {
            if (indexChecked[i]) {
                Cell cell = r.createCell(cellCount);
                cell.setCellValue(rowHeader[i]);
                cellCount++;
            }
        }

        for (MBRecord mbr : list) {
            String mbrStr[] = new String[rowHeader.length];
            mbrStr[0] = rowCount + "";
            mbrStr[1] = mbr.getDescription();
            mbrStr[2] = mbr.getAmount() + "";
            mbrStr[3] = smp.format(mbr.getDate());
            mbrStr[4] = getTypeString(mbr.getType());
            mbrStr[5] = mbr.getCategory();
            mbrStr[6] = mbr.getPaymentMethod();

            Row row = sheet.createRow(rowCount);
            writeBook(mbrStr, row);
            rowCount++;
        }
        FileOutputStream outputStream = new FileOutputStream(getMediaFile(".xls"));
        workbook.write(outputStream);
    }

    private void writeBook(String str[], Row row) {
        int cellCount = 0;
        final int len = str.length;
        for (int i = 0; i < len; i++) {
            if (indexChecked[i]) {
                Cell cell = row.createCell(cellCount);
                cell.setCellValue(str[i]);
                cellCount++;
            }
        }
    }

    private String getTypeString(int type) {
        switch (type) {
            case GlobalConstants.TYPE_SPENT:
                return "Spent";
            case GlobalConstants.TYPE_EARN:
                return "Earn";
            case GlobalConstants.TYPE_DUE:
                return "Due";
            case GlobalConstants.TYPE_LOAN:
                return "Loan";
            case GlobalConstants.TYPE_MONEY_TRANSFER:
                return "Money Transfer";
            case GlobalConstants.TYPE_DUE_PAYMENT:
                return "Due Paid";
            case GlobalConstants.TYPE_LOAN_PAYMENT:
                return "Loan Cleared";
            case GlobalConstants.TYPE_DUE_REPAYMENT:
                return "Due RePayment";
            case GlobalConstants.TYPE_LOAN_REPAYMENT:
                return "Loan RePayment";
            default:
                return "";
        }
    }

    private File getMediaFile(String type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MBStore");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerCus.d(TAG, "failed to create or open directory");
                return null;
            }
        }

        File mediaFile;
        String filename = this.fileName + type;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        if (mediaFile.exists()) {
            mediaFile.delete();
        }
        currentMediaFile = mediaFile;
        return mediaFile;
    }
}
