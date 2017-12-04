package com.example.allakumarreddy.moneybook;

import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alla.kumarreddy on 7/26/2017.
 */

public class XLStore {

    private final static String TAG="XLStore";
    private String fileName;
    private ArrayList<MBRecord> mb;
    private int count=1;

    public XLStore(String fileName, ArrayList<MBRecord> mb) {
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
        String filename = this.fileName+".xls";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        return mediaFile;
    }

    private void write()
    {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row r=sheet.createRow(0);
        Cell cell = r.createCell(0);
        cell.setCellValue("S.No");

        cell = r.createCell(1);
        cell.setCellValue("Description");

        cell = r.createCell(2);
        cell.setCellValue("Amount");

        cell = r.createCell(3);
        cell.setCellValue("Date");


        int rowCount = 0;

        for (MBRecord mbr : this.mb) {
            Row row = sheet.createRow(++rowCount);
            writeBook(mbr, row);
        }

        try (FileOutputStream outputStream = new FileOutputStream(getMediaFile())) {
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG,e.getMessage());
        } catch (IOException e) {
            LoggerCus.d(TAG,e.getMessage());
        }
    }

    private void writeBook(MBRecord aBook, Row row) {
        SimpleDateFormat smp=new SimpleDateFormat("yyyy/MM/dd");
        Cell cell = row.createCell(0);
        cell.setCellValue((count++)+"");

        cell = row.createCell(1);
        cell.setCellValue(aBook.getDescription());

        cell = row.createCell(2);
        cell.setCellValue(aBook.getAmount());

        cell = row.createCell(3);
        cell.setCellValue(smp.format(aBook.getDate()));
    }
}
