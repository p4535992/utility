package com.github.p4535992.util.file.csv.univocity;

import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.univocity.parsers.common.processor.ColumnProcessor;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by 4535992 on 23/01/2016.
 * @author 4535992.
 */
public class UnivocityUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(UnivocityUtilities.class);


    /**
     * Method to get the String array of the columns of a CSV File.
     *
     * @param fileInputCsv     the {@link File} CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] getHeaders(File fileInputCsv, boolean hasFirstLine) {
        String[] columns = new String[0];
        try {
            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setLineSeparatorDetectionEnabled(true);
            RowListProcessor rowProcessor = new RowListProcessor();
            parserSettings.setRowProcessor(rowProcessor);
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.setDelimiterDetectionEnabled(true);
            CsvParser parser = new CsvParser(parserSettings);
            parser.parse(fileInputCsv);
            try {
                columns = rowProcessor.getRows().get(0); // assuming first read
            }catch(java.lang.IndexOutOfBoundsException e){
                return rowProcessor.getHeaders();
            }
            if (!hasFirstLine) {
                int columnCount = 0;
                if (columns != null) columnCount = columns.length;
                columns = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columns[i] = "Column#" + i;
                }
            }else {
                return rowProcessor.getHeaders();
            }
        } catch(java.lang.NullPointerException e){
            logger.warn("The file:"+fileInputCsv.getAbsolutePath()+" is empty or not exists");
            return columns;
        }catch (Exception e) {
            logger.error("Can't find the Headers on the CSV File", e);
        }
        return columns;
    }


    /**
     * Method use OpenCsv Library for
     * @param fileInputCsv the {@link File} CSV to parse.
     * @param noHeaders if true not include the headers on the result.
     * @return the {@link List} of Bean parsed from the CSV file.
     */
    public static List<String[]> parseCSVFileAsList(File fileInputCsv,boolean noHeaders){
        try {
            // The settings object provides many configuration options
            CsvParserSettings parserSettings = new CsvParserSettings();
            //You can configure the parser to automatically detect what line separator sequence is in the input
            parserSettings.setLineSeparatorDetectionEnabled(true);
            // A RowListProcessor stores each parsed row in a List.
            RowListProcessor rowProcessor = new RowListProcessor();
            // You can configure the parser to use a RowProcessor to process the values of each parsed row.
            // You will find more RowProcessors in the 'com.univocity.parsers.common.processor' package, but you can also create your own.
            parserSettings.setRowProcessor(rowProcessor);
            // Let's consider the first parsed row as the headers of each column in the file.
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.setDelimiterDetectionEnabled(true);
            // creates a parser instance with the given settings
            CsvParser parser = new CsvParser(parserSettings);
            // the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
            parser.parse(fileInputCsv);
            // get the parsed records from the RowListProcessor here.
            // Note that different implementations of RowProcessor will provide different sets of functionalities.
            //List<String[]> rows = rowProcessor.getRows();
            List<String[]> content =  new ArrayList<>();
            if(!noHeaders) content.add(rowProcessor.getHeaders()); //add headers
            content.addAll(rowProcessor.getRows());
            return content;
        }catch(Exception e){
            logger.error("Can't parse the CSV file:"+ fileInputCsv.getAbsolutePath()+" -> "+e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    /**
     * Method use OpenCsv Library for
     * @param fileInputCsv the {@link File} CSV to parse.
     * @return the {@link Map} of Bean parsed from the CSV file.
     */
    public static Map<String, List<String>> parseCSVFileAsMap(File fileInputCsv){
        //##CODE_START
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setHeaderExtractionEnabled(true);
        // To get the values of all columns, use a column processor
        ColumnProcessor rowProcessor = new ColumnProcessor();
        parserSettings.setRowProcessor(rowProcessor);
        CsvParser parser = new CsvParser(parserSettings);
        //This will kick in our column processor
        parser.parse(fileInputCsv);
        //Finally, we can get the column values:
        return new TreeMap<>(rowProcessor.getColumnValuesAsMapOfNames());
    }

    public static char getDelimiterField(File fileInputCsv){
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setDelimiterDetectionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(fileInputCsv);
        return parser.getDetectedFormat().getDelimiter();
    }

    private void printRows(List<String[]> rows) {
        logger.info("Printing " + rows.size() + " rows:\n");
        int rowCount = 0;
        for (String[] row : rows) {
            logger.info("Row " + ++rowCount + " (length " + row.length + "): " + Arrays.toString(row));
            int valueCount = 0;
            for (String value : row) {
                logger.info("\tvalue " + ++valueCount + ": " + value+"\n");
            }
        }
    }

    public static void main(String[] args) {
        LogBackUtil.console();

        char c = getDelimiterField(new File("C:\\Users\\tenti\\Documents\\GitHub\\repositoryForTest\\testWitSources\\fileForTest\\data.csv"));

        List<String[]> cotnent =
                OpenCsvUtilities.parseCSVFileAsListWithUnivocity(
                        new File("C:\\Users\\tenti\\Documents\\GitHub\\repositoryForTest\\testWitSources\\fileForTest\\data.csv")
                        ,true
                );

        List<String[]> cotnent2 =
                OpenCsvUtilities.parseCSVFileAsList(
                        new File("C:\\Users\\tenti\\Documents\\GitHub\\repositoryForTest\\testWitSources\\fileForTest\\data.csv")
                        ,true
                );

       String[] header =
                OpenCsvUtilities.getHeaders(
                        new File("C:\\Users\\tenti\\Documents\\GitHub\\repositoryForTest\\testWitSources\\fileForTest\\data.csv")
                        ,true
                );

        String[] header2 =
                OpenCsvUtilities.getHeadersWithUnivocity(
                        new File("C:\\Users\\tenti\\Documents\\GitHub\\repositoryForTest\\testWitSources\\fileForTest\\data.csv")
                        ,true
                );

        String lat = OpenCsvUtilities.getFieldLatitude(header2);
        String lon = OpenCsvUtilities.getFieldLongitude(header2);

    }
}
