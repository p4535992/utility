package com.github.p4535992.util.office.excel;

import com.github.p4535992.util.file.FileUtilities;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Created by 4535992 on 28/12/2015.
 */
public class ExcelUtilities {

    Map<String,Object[]> data =new TreeMap<>();


    public static List<String> readExcel(File inputExcel) {
        List<String> list = new ArrayList<>();
        FileInputStream fis;
        StringBuilder sb = new StringBuilder();
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = getWorkbook(inputExcel.getAbsolutePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        //looping over each workbook sheet
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            //iterating over each row
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();
                //Iterating over each cell (column wise)  in a particular row.
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    //The Cell Containing String will is name.
                    sb.append(String.valueOf(getCellValue(cell))).append(" \t\t ");
                }
                list.add(sb.toString());
                //end iterating a row, add all the elements of a row in list
            }
        }
        return list;
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            //The Cell Containing String will is name.
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            //The Cell Containing Boolean will is name.
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            //The Cell Containing numeric value will contain marks
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
        }

        return null;
    }

    private static  Workbook getWorkbook(String excelFilePath) {
        return getWorkbook(null,excelFilePath);
    }

    private static  Workbook getWorkbook(InputStream inputStream,String excelFilePath) {
        try {
            Workbook workbook;
            if (inputStream == null) {
                inputStream = FileUtilities.toStream(excelFilePath);
            }
            if (excelFilePath.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (excelFilePath.endsWith("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("The specified file is not Excel file");
            }
            inputStream.close();
            return workbook;
        }catch(NullPointerException|IOException e){
            return null;
        }
    }

    public static void writeExcel(Map<String,Object[]> data,File outputFile) {
        Workbook workbook = getWorkbook(outputFile.getAbsolutePath());
        Sheet sheet = workbook.createSheet("Java Books");
    /*    Object[][] bookData = {
                {"Head First Java", "Kathy Serria", 79},
                {"Effective Java", "Joshua Bloch", 36},
                {"Clean Code", "Robert martin", 42},
                {"Thinking in Java", "Bruce Eckel", 35},
        };*/
        writedataToSheet(sheet,data);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void createHeaderRow(Sheet sheet,String[] headers) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight((short) 2);
        font.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font);
        Row row = sheet.createRow(0);
        int indexHeader = 1;
        for(String header : headers){
            Cell cellTitle = row.createCell(indexHeader);
            cellTitle.setCellStyle(cellStyle);
            cellTitle.setCellValue(header);
            indexHeader++;
        }
    }

    public static void writedataToSheet(Sheet sheet,Map<String,Object[]> data){
        //Iterate over data and write to sheet
        Set<String> keyid = data.keySet();
        int rowid = 0;
        Row row;
        for (String key : keyid){
            row = sheet.createRow(rowid++);
            Object[] objectArr = data.get(key);
            int cellid = 0;
            for (Object field : objectArr)
            {
                /*Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);*/
                Cell cell = row.createCell(cellid++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }
    }
}
