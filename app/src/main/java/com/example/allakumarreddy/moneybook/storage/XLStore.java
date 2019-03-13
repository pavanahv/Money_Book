package com.example.allakumarreddy.moneybook.storage;

import android.os.Environment;

import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/26/2017.
 */

public class XLStore {

    private final static String TAG = "XLStore";
    private String fileName;
    private ArrayList<MBRecord> mb;
    private int count = 1;

    public XLStore(String fileName, ArrayList<MBRecord> mb) throws IOException {
        this.fileName = fileName;
        this.mb = mb;
        write();
    }

    private File getMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MBStore");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerCus.d(TAG, "failed to create or open directory");
                return null;
            }
        }

        File mediaFile;
        String filename = this.fileName + ".xls";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        if (mediaFile.exists()) {
            mediaFile.delete();
        }
        return mediaFile;
    }

    private void write() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row r = sheet.createRow(0);
        Cell cell = r.createCell(0);
        cell.setCellValue("S.No");

        cell = r.createCell(1);
        cell.setCellValue("Description");

        cell = r.createCell(2);
        cell.setCellValue("Amount");

        cell = r.createCell(3);
        cell.setCellValue("Date");

        cell = r.createCell(4);
        cell.setCellValue("Transaction Type");


        int rowCount = 0;

        for (MBRecord mbr : this.mb) {
            Row row = sheet.createRow(++rowCount);
            writeBook(mbr, row);
        }
        FileOutputStream outputStream = new FileOutputStream(getMediaFile());
        workbook.write(outputStream);
    }

    private void writeBook(MBRecord aBook, Row row) {
        SimpleDateFormat smp = new SimpleDateFormat("dd / MM / yyyy");
        Cell cell = row.createCell(0);
        cell.setCellValue((count++) + "");

        cell = row.createCell(1);
        cell.setCellValue(aBook.getDescription());

        cell = row.createCell(2);
        cell.setCellValue(aBook.getAmount());

        cell = row.createCell(3);
        cell.setCellValue(smp.format(aBook.getDate()));

        cell = row.createCell(4);
        switch (aBook.getType()) {
            case 0:
                cell.setCellValue("Spent");
                break;
            case 1:
                cell.setCellValue("Earn");
                break;
            case 2:
                cell.setCellValue("Due");
                break;
            case 3:
                cell.setCellValue("Loan");
                break;
        }
    }
}
