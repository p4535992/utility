package com.github.p4535992.util.office.excel;

import com.github.p4535992.util.file.FileUtilities;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 28/12/2015.
 * href: https://github.com/semoss/semoss/blob/master/src/prerna/poi/main/RelationshipLoadingSheetWriter.java
 * href: https://github.com/semoss/semoss/blob/master/src/prerna/poi/main/POIWriter.java
 * href: https://github.com/semoss/semoss/blob/master/src/prerna/poi/main/POIReader.java
 */
@SuppressWarnings("unused")
public class ExcelUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExcelUtilities.class);

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

    public static Workbook getWorkbook(String excelFilePath) {
        return getWorkbook(null,excelFilePath);
    }

    public static  Workbook getWorkbook(InputStream inputStream,String excelFilePath) {
        try {
            Workbook workbook;
            File inFile = new File(excelFilePath);
            if(inFile.exists()){
                if (inputStream == null) {
                    //inputStream = FileUtilities.toStream(excelFilePath);
                    inputStream = new FileInputStream(inFile);
                }
                if (excelFilePath.endsWith("xlsx")) {
                    workbook = new XSSFWorkbook(inputStream);
                } else if (excelFilePath.endsWith("xls")) {
                    workbook = new HSSFWorkbook(inputStream);
                } else {
                    logger.warn("The specified file is not Excel file");
                    workbook = new XSSFWorkbook();
                }
            }else{
                throw new IOException("The File:"+excelFilePath+" not exists!");
            }
            inputStream.close();
            return workbook;
        }catch(NullPointerException|IOException e){
            logger.error(e.getMessage(),e);
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

    //-------------------------------------------------------------------------------------------------------------

    public boolean exportLoadingSheets(String fileLoc, Hashtable<String, List<String[]>> hash,
                                       String readFileLoc) {
        //create file
        Workbook wb;
        if(readFileLoc != null) {
            wb = getWorkbook(readFileLoc);
            if(wb == null)return false;
        } else {
            wb = new XSSFWorkbook();
        }
        Hashtable<String, List<String[]>> preparedHash = prepareLoadingSheetExport(hash);
        Sheet sheet = wb.createSheet("Loader");
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Sheet Name", "Type"});
        for(String key : hash.keySet()) {
            data.add(new String[]{key, "Usual"});
        }
        int count=0;
        for(String[] aData : data) {
            Row row1 = sheet.createRow(count);
            count++;
            for (int col = 0; col < aData.length; col++) {
                Cell cell = row1.createCell(col);
                if (aData[col] != null) {
                    cell.setCellValue(aData[col].replace("\"", ""));
                }
            }
        }
        Set<String> keySet = preparedHash.keySet();
        for(String key: keySet){
            List<String[]> sheetVector = preparedHash.get(key);
            writeSheet(key, sheetVector, wb);
        }
        writeFile(wb, fileLoc);
        logger.info("Export successful: " + fileLoc);
        return true;
    }

    public void exportLoadingSheets(String fileLoc, Hashtable<String, List<String[]>> hash,
                                    String readFileLoc, boolean formatData){
        //create file
        Workbook wb = getWorkbook(readFileLoc);
        if(wb == null) return;
        Hashtable<String, List<String[]>> preparedHash;
        if(formatData) {
            preparedHash = prepareLoadingSheetExport(hash);
        } else {
            preparedHash = hash;
        }
        Sheet sheet = wb.createSheet("Loader");
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Sheet Name", "Type"});
        for(String key : preparedHash.keySet()) {
            data.add(new String[]{key, "Usual"});
        }
        int count=0;
        for (String[] aData : data) {
            Row row1 = sheet.createRow(count);
            count++;
            for (int col = 0; col < aData.length; col++) {
                Cell cell = row1.createCell(col);
                if (aData[col] != null) {
                    cell.setCellValue(aData[col].replace("\"", ""));
                }
            }
        }
        Set<String> keySet = preparedHash.keySet();
        for(String key: keySet){
            List<String[]> sheetVector = preparedHash.get(key);
            writeSheet(key, sheetVector, wb);
        }
        writeFile(wb, fileLoc);
        logger.info("Export successful: " + fileLoc);
    }

    public static void writeSheet(String key, List<String[]> data, Workbook workbook) {
        Sheet worksheet = workbook.createSheet(key);
        int count=0;//keeps track of rows; one below the row int because of header row
        final Pattern NUMERIC = Pattern.compile("^\\d+.?\\d*$");
        //for each row, create the row in excel
        for (String[] aData : data) {
            Row row1 = worksheet.createRow(count);
            count++;
            //for each col, write it to that row.7
            for (int col = 0; col < aData.length; col++) {
                Cell cell = row1.createCell(col);
                if (aData[col] != null) {
                    String val = aData[col];
                    //Check if entire value is numeric - if so, set cell type and parseDouble, else write normally
                    if (val != null && !val.isEmpty() && NUMERIC.matcher(val).find()) {
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(Double.parseDouble(val));
                    } else {
                        cell.setCellValue(aData[col].replace("\"", ""));
                    }
                }
            }
        }
    }

    /*public void runExport(Hashtable hash, String writeFile, String readFile, boolean formatData) {
        //this function will write a hashtable to an xlsx sheet
        //keys from the hastable become sheet names
        //objects must be in format Vector<String[]>
        //writeFileName is the name of the file this function will create
        //readFileName is the file that this function will add to
        //if readFileName is null, it will create a new workbook
        String workingDir = System.getProperty("user.dir");
        if(writeFile == null || writeFile.isEmpty()) {
            writeFile = "";
        }
        if(readFile == null || readFile.isEmpty()) {
            readFile = "BaseGILoadingSheets.xlsx";
        }
        String folder = "\\export\\";
        String fileLoc = workingDir + folder + writeFile;
        String readFileLoc = workingDir + folder + readFile;

        ExportLoadingSheets(fileLoc, hash, readFileLoc, formatData);
    }*/

    public static void writeFile(Workbook wb, String fileLoc) {
        try {
            FileOutputStream newExcelFile = new FileOutputStream(fileLoc);
            wb.write(newExcelFile);
            newExcelFile.close();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    //pass in a hashtable that has the first or of every sheet as {"Relation", "*the relation*"}
    //turns it to look like a loading sheet
    public Hashtable<String, List<String[]>> prepareLoadingSheetExport(Hashtable<String, List<String[]>> oldHash) {
        Hashtable<String, List<String[]>> newHash = new Hashtable<>();
        for (String key : oldHash.keySet()) {
            List<String[]> sheetV = oldHash.get(key);
            List<String[]> newSheetV = new ArrayList<>();
            String[] oldTopRow = sheetV.get(0);//this should be {Relation, *the relation, "", "" ...}
            String[] oldHeaderRow = sheetV.get(1);//this should be {*header1, *header2....}
            String[] oldSecondRow = new String[oldHeaderRow.length];//this is in case the sheet is null (other than the headers)
            if (sheetV.size() > 2) oldSecondRow = sheetV.get(2);//this should be {*value1, *value2....}
            String[] newTopRow = new String[oldHeaderRow.length + 1];
            String prevSubj;
            String prevObj;
            newTopRow[0] = oldTopRow[0];
            System.arraycopy(oldHeaderRow, 0, newTopRow, 1, oldHeaderRow.length);
            newSheetV.add(newTopRow);
            ArrayList<String> headers = new ArrayList<>();
            Collections.addAll(headers, newTopRow);
            String[] newSecondRow = new String[oldHeaderRow.length + 1];
            newSecondRow[0] = oldTopRow[1];
            int headerIndex = -1;
            if (oldSecondRow[3] != null) {
                headerIndex = headers.indexOf(oldSecondRow[3]);
            }
            if (headerIndex != -1) {
                newSecondRow[headerIndex] = oldSecondRow[4];
            }
            newSecondRow[1] = oldSecondRow[0];
            newSecondRow[2] = oldSecondRow[2];
            prevSubj = oldSecondRow[0];
            prevObj = oldSecondRow[2];
            //newSecondRow should now be {*the relation, *value1....}
            newSheetV.add(newSecondRow);
            //now to run through the rest of the sheet
            for (int i = 3; i < sheetV.size(); i++) {
                String[] row = sheetV.get(i);
                if (prevSubj.equals(row[0]) && prevObj.equals(row[2])) {
                    headerIndex = -1;
                    if (row[3] != null) {
                        headerIndex = headers.indexOf(row[3]);
                    }
                    if (headerIndex != -1) {
                        //get last element
                        newSheetV.get(newSheetV.size()-1)[headerIndex] = row[4];
                    }
                    continue;
                }

                String[] newRow = new String[headers.size() + 1];
                headerIndex = -1;
                if (row[3] != null) {
                    headerIndex = headers.indexOf(row[3]);
                }
                if (headerIndex != -1) {
                    newRow[headerIndex] = row[4];
                }
                newRow[1] = row[0];
                newRow[2] = row[2];
                prevSubj = row[0];
                prevObj = row[2];
                newSheetV.add(newRow);
            }

            //now add the completed sheet to the new hash
            newHash.put(key, newSheetV);
        }
        return newHash;
    }

    public void runExport(Hashtable hash, String writeFile, String readFile, boolean formatData) {
        //this function will write a hashtable to an xlsx sheet
        //keys from the hastable become sheet names
        //objects must be in format Vector<String[]>
        //writeFileName is the name of the file this function will create
        //readFileName is the file that this function will add to
        //if readFileName is null, it will create a new workbook
        String workingDir = System.getProperty("user.dir");
        if(writeFile == null || writeFile.isEmpty()) {
            writeFile = "LoadingSheets1.xlsx";
        }
        if(readFile == null || readFile.isEmpty()) {
            readFile = "BaseGILoadingSheets.xlsx";
        }
        String folder = "\\export\\";
        String fileLoc = workingDir + folder + writeFile;
        String readFileLoc = workingDir + folder + readFile;
        exportLoadingSheets(fileLoc, hash, readFileLoc, formatData);
    }

    //------------------------------------------------------------------------------------------------

    public void importFile(String fileName) throws Exception {

        XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(fileName));
        // System.out.println("Number of sheets " + book.getNumberOfSheets());
        // System.out.println("Sheet Name ::::: " + book.getSheetAt(0).getSheetName());
        // load the sheets to be loaded first
        Sheet lSheet = book.getSheet("Loader");
        // assumption is all the sheets are in the first column starting from row 2
        // need a procedure here to load the base relationships first
        int lastRow = lSheet.getLastRowNum();
        for (int rIndex = 1; rIndex <= lastRow; rIndex++) {
            // get thr sheet first
            Row row = lSheet.getRow(rIndex);
            Cell cell = row.getCell(0);
            Cell cell2 = row.getCell(1);
            if (cell != null && cell2 != null) {
                String sheetToLoad = cell.getStringCellValue();
                System.out.println("Cell Content is " + sheetToLoad);
                // this is a relationship
                if (cell2 != null  && cell2.getStringCellValue().contains("Matrix")) {
                    if (cell2.getStringCellValue().contains("Dynamic")) {
                        loadMatrixSheet(sheetToLoad, book, true);
                        //createBaseRelations();
                        //sc.commit();
                    } else {
                        loadMatrixSheet(sheetToLoad, book, false);
                        //createBaseRelations();
                        //sc.commit();
                    }
                } else if (cell2 != null
                        && cell2.getStringCellValue().contains("Dynamic")) {
                    loadSheet(sheetToLoad, book, true);
                    //createBaseRelations();
                    //sc.commit();
                } else {
                    loadSheet(sheetToLoad, book, false);
                    //createBaseRelations();
                    //sc.commit();
                }
            }
        }
    }

    public void loadSheet(String sheetToLoad, Workbook book, boolean dynamic) throws Exception{
        Sheet lSheet = book.getSheet(sheetToLoad);
        // assumption is all the sheets are in the first column starting from row 2
        int lastRow = lSheet.getLastRowNum()+1;
        // System.out.println("Last row " + systemSheet.getLastRowNum());
        // Get the first row to get column names
        Row row = lSheet.getRow(0);
        System.out.println("Max columns " + row.getLastCellNum());
        // get the column names
        String data = null;
        int count = 0;
        String idxName = null;
        String nodeType = null;
        String otherIdx = null;
        Vector propNames = new Vector();
        String relName = null;
        nodeType = row.getCell(0).getStringCellValue();
        idxName = row.getCell(1).getStringCellValue();
        int curCol = 1;
        if (nodeType.equalsIgnoreCase("Relation")) {
            otherIdx = row.getCell(2).getStringCellValue();
            curCol++;
        }
        // adds all the property names
        // if relationship then starts with 2 else starts at 1, starting column is 0
        // loads it into vector propNames
        for (int colIndex = curCol + 1; colIndex < row.getLastCellNum(); propNames
                .addElement(row.getCell(colIndex).getStringCellValue()), colIndex++)
            ;
        // now process the remaining nodes
        // finally the graph db YAY !!
        //org.neo4j.graphdb.Transaction tx = svc.beginTx();
        try {
            System.out.println("Last Row is " + lastRow);

            // processing starts here
            for (int rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                //System.out.println("Processing " + rowIndex);
                // first cell is the name of relationship
                Row nextRow = lSheet.getRow(rowIndex);

                // get the name of the relationship
                if (rowIndex == 1)
                    relName = nextRow.getCell(0).getStringCellValue();

                // get the name of the node
                String thisNode = null;
                if (nextRow.getCell(1) != null
                        && nextRow.getCell(1).getCellType() != Cell.CELL_TYPE_BLANK)
                    nextRow.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                thisNode = nextRow.getCell(1).getStringCellValue();
                // get the second element - this is the name
                String otherNode = null;
                Hashtable propHash = new Hashtable();
                int startCol = 2;
                int offset = 2;
                if (nodeType.equalsIgnoreCase("Relation")) {
                    nextRow.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    otherNode = nextRow.getCell(2).getStringCellValue();
                    startCol++;
                    offset++;
                }

                // System.out.println(" ROw Index " + rowIndex);
                for (int colIndex = startCol; colIndex < nextRow
                        .getLastCellNum(); colIndex++) {
                    // System.out.println(colIndex + "<<>>" + nextRow.getLastCellNum());
                    if(propNames.size() <= (colIndex-offset)) {
                        continue;
                    }
                    String propName = (String) propNames.elementAt(colIndex
                            - offset);
                    String propValue = null;
                    if (nextRow.getCell(colIndex) == null || nextRow.getCell(colIndex).getCellType() == Cell.CELL_TYPE_BLANK || nextRow.getCell(colIndex).toString().isEmpty()) {
                        continue;
                    }
                    if (nextRow.getCell(colIndex).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if(DateUtil.isCellDateFormatted(nextRow.getCell(colIndex))){
                            Date date = (Date) nextRow.getCell(colIndex).getDateCellValue();
                            propHash.put(propName, date);
                        }
                        else{
                            Double dbl = new Double(nextRow.getCell(colIndex)
                                    .getNumericCellValue());
                            // propValue = nextRow.getCell(colIndex).getNumericCellValue() + "";
                            propHash.put(propName, dbl);
                        }
                    } else {
                        propValue = nextRow.getCell(colIndex)
                                .getStringCellValue();
                        propHash.put(propName, propValue);
                    }
                }

                /*if (nodeType.equalsIgnoreCase("Relation")) {
                    //System.out.println("Adding " + thisNode + "<<>>"
                    //	+ otherNode + "<<>>" + rowIndex);
                    System.out.println("Processing " + sheetToLoad + " Row " + rowIndex);
                    if (!dynamic)
                        addRelation(idxName, otherIdx, thisNode, otherNode,
                                relName, propHash);
                    else
                        System.out.println("Method for loading dynamic relationship does not exist");
                } else
                    addNode(idxName, thisNode, propHash);*/
                //tx.success();
                count++;
            }
        } finally {
            //tx.finish();
        }
    }

    public void loadMatrixSheet(String sheetToLoad, Workbook book,boolean dynamic) throws Exception{
        // these sheets are typically of the form
        // First column - entity 1
        // First Row - entity 2 - Need to find how to get this entity name, may be by tokenizing with hiphen in between
        Sheet lSheet = book.getSheet(sheetToLoad);
        // assumption is all the sheets are in the first column starting from row 2
        int lastRow = lSheet.getLastRowNum();
        // System.out.println("Last row " + systemSheet.getLastRowNum());
        // Get the first row to get column names
        Row row = lSheet.getRow(0);
        System.out.println("Max columns " + row.getLastCellNum());
        // get the column names
        String data = null;
        int count = 0;
        String idxName,nodeType,otherIdx, relName;
        List<String> propNames = new ArrayList<>();

        nodeType = row.getCell(0).getStringCellValue();
        String complexName = row.getCell(1).getStringCellValue();
        StringTokenizer tokens = new StringTokenizer(complexName, "-");
        idxName = tokens.nextToken();
        int curCol = 1;
        if (nodeType.equalsIgnoreCase("Relation")) {
            otherIdx = tokens.nextToken();
        }
        // load all the columns first
        List<String> colNames = new ArrayList<>();
        for(
                int colIndex = curCol + 1;
                colIndex < row.getLastCellNum();
                colNames.add(row.getCell(colIndex).getStringCellValue()), colIndex++
        )
        //org.neo4j.graphdb.Transaction tx = svc.beginTx();
        try {
            // now process the remaining nodes
            for (int rowIndex = 1; rowIndex < lastRow; rowIndex++) {
                // first cell is the name of relationship
                Row nextRow = lSheet.getRow(rowIndex);
                // get the name of the relationship
                if (rowIndex == 1) relName = nextRow.getCell(0).getStringCellValue();
                // get the name of the node
                String thisNode = nextRow.getCell(1).getStringCellValue();
                // get the second element - this is the name
                // need to run through all of the columns and put the value
                for (int colIndex2 = curCol + 1, cnIndex = 0; colIndex2 < nextRow
                        .getLastCellNum() && cnIndex < colNames.size(); colIndex2++, cnIndex++) {
                    Hashtable propHash = new Hashtable();
                    String otherNode = colNames.get(cnIndex);
                    // XSSFCell.
                    if (nextRow.getCell(colIndex2).getCellType() == Cell.CELL_TYPE_NUMERIC)
                        propHash.put("weight",
                                new Double(nextRow.getCell(colIndex2)
                                        .getNumericCellValue()));
                    // finally the graph db YAY !!
                   /* if (nodeType.equalsIgnoreCase("Relation"))
                        addRelation(idxName, otherIdx, thisNode, otherNode,
                                relName, propHash);
                    else
                        addNode(idxName, thisNode, propHash);*/
                    //tx.success();
                }
            }
        } finally {
            //tx.finish();
        }
    }
}
