package com.github.p4535992.util.file.csv.opencsv;

import com.github.p4535992.util.calendar.DateUtilities;
import com.github.p4535992.util.collection.ListUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.file.csv.univocity.UnivocityUtilities;
import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 28/12/2015.
 * href:http://www.journaldev.com/2544/java-csv-parserwriter-example-using-opencsv-apache-commons-csv-and-supercsv.
 */
@SuppressWarnings("unused")
public class OpenCsvUtilities extends FileUtilities{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(OpenCsvUtilities.class);

    /**
     * Method to write a CSV Data List of Beans to a String.
     * @param beans the List of Beans to convert.
     * @param separator the char separator.
     * @param <T> the generic variable.
     * @return the String content of the List of Beans.
     */
    public static <T> String writeCSVDataToStringWithBeans(List<T> beans,char separator){
        try {
            Writer writer = new StringWriter();
            try (CSVWriter csvWriter = new CSVWriter(writer, separator)) {
                List<String[]> data = toStringArray(beans);
                csvWriter.writeAll(data);
            }
            return writer.toString();
        }catch(IOException e) {
            logger.error("Can't write the CSV String from the Bean:" + beans.get(0).getClass().getName()
                    + " -> " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Method to write a CSV Data List of Array of String to a String.
     * @param content the List of Array of String to convert.
     * @param separator the char separator.
     * @return the String content of the List of Beans.
     */
    public static String writeCSVDataToString(List<String[]> content,char separator){
        try {
            Writer writer = new StringWriter();
            CSVWriter csvWriter;
            if (StringUtilities.NULL_CHAR2 == separator) {
                csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
            } else {
                csvWriter = new CSVWriter(writer, separator, CSVWriter.NO_QUOTE_CHARACTER);
            }
            csvWriter.writeAll(content);
            csvWriter.close();
            return writer.toString();
        }catch(IOException e){
            logger.error("Can't write the CSV String -> "+e.getMessage(),e);
            return "";
        }
    }

    /**
     * Method to write a CSV Data List of Array of String to a String.
     * @param content the List of Array of String to convert.
     * @return the String content of the List of Beans.
     */
    public static String writeCSVDataToString(List<String[]> content){
        return writeCSVDataToString(content,StringUtilities.NULL_CHAR2);
    }

    /**
     * Method to write a CSV File from List of Array of String.
     * @param content the List of Array of String to convert.
     * @param separator the char separator.
     * @param fileOutputCsv the output File Csv to create.
     * @return the File Csv created.
     */
    public static File writeCSVDataToFile(List<String[]> content,char separator,File fileOutputCsv){
        try{
            Writer writer = new FileWriter(fileOutputCsv,true);//the true value make append the result...
            CSVWriter csvWriter;
            if(StringUtilities.NULL_CHAR2 == separator){
                csvWriter =  new CSVWriter(writer,CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
            }else{
                csvWriter = new CSVWriter(writer,separator, CSVWriter.NO_QUOTE_CHARACTER);
            }
            csvWriter.writeAll(content);
            csvWriter.close();
            return fileOutputCsv;
        }catch(IOException e){
            logger.error("Can't write the CSV to File:"+fileOutputCsv+" -> "+e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to write a CSV File from List of Array of String.
     * @param content the List of Array of String to convert.
     * @param fileOutputCsv the output File Csv to create.
     * @return the File Csv created.
     */
    public static File writeCSVDataToFile(List<String[]> content,File fileOutputCsv){
         return writeCSVDataToFile(content,StringUtilities.NULL_CHAR2,fileOutputCsv);
    }

    /**
     * Method to write a CSV List of Array of String to System Console..
     * @param content the List of Array of String to convert.
     * @param separator the char separator.
     */
    public static void writeCSVDataToConsole(List<String[]> content,char separator){
        try {
            Writer writer = new OutputStreamWriter(System.out, StringUtilities.UTF_8);
            CSVWriter csvWriter;
            if (StringUtilities.NULL_CHAR2 == separator) {
                csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
            } else {
                csvWriter = new CSVWriter(writer, separator, CSVWriter.NO_QUOTE_CHARACTER);
            }
            csvWriter.writeAll(content, false);
            csvWriter.close();
        }catch(IOException e) {
            logger.error("Can't write the CSV to Console -> " + e.getMessage(), e);
        }
    }

    /**
     * Method to write a CSV List of Array of String to System Console..
     * @param content the List of Array of String to convert.
     */
    public static void writeCSVDataToConsole(List<String[]> content){
        writeCSVDataToConsole(content,StringUtilities.NULL_CHAR2);
    }

    /**
     * Method to convert a List of beans to a List of Array of Strings.
     * @param beans the List of Beans.
     * @param <T> generic value.
     * @return the List of Array of String content of the csv.
     */
    public static <T> List<String[]> toStringArray(List<T> beans) {
        return toStringArray(beans,null);
    }

    /**
     * Method to convert a List of beans to a List of Array of Strings.
     * @param beans the List of Beans.
     * @param addNewHeader the new String Array for the Header row.
     * @param <T> generic value.
     * @return the List of Array of String content of the csv.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<String[]> toStringArray(List<T> beans,String[] addNewHeader) {
        List<String[]> records = new ArrayList<>();
        //add header record
        //records.add(new String[]{"ID","Name","Role","Salary"});
        if(addNewHeader != null) records.add(addNewHeader);
        for(T bean : beans) {
        //beans.stream().map((bean) -> {
            List<String> record = new ArrayList<>();
            //invoke getter method and convert to String
            Class<T> clazz = (Class<T>) bean.getClass();
            //T t = ReflectionUtilities.invokeConstructor(clazz);
            List<Method> getter = (List<Method>) ReflectionUtilities.findGetters(clazz,true);

            for(Method method : getter){
                 record.add(String.valueOf(ReflectionUtilities.invokeGetter(bean,method)));
            }
            //getter.stream().forEach((method) -> record.add(String.valueOf(ReflectionUtilities.invokeGetter(bean,method))));
            //return record;
        //}).forEach((record) -> records.add(ListUtilities.toArray(record)));
            records.add(ListUtilities.toArray(record));
        }
        return records;
    }

    /**
     * Method use OpenCsv Library for
     * @param columnMapping Map allow the user to pass the column Names to a Field Names of the Class.
     * @param fileInputCsv the File CSV to parse.
     * @param <T> the generic variable.
     * @return the List of Bean parsed from the CSV file.
     */
    public static <T> List<T> parseCSVFileAsList(Map<String, String> columnMapping,File fileInputCsv){
        try {
            HeaderColumnNameTranslateMappingStrategy<T> beanStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
            //beanStrategy.setType(clazz); //deprecated
            /*Map<String, String> columnMapping = new HashMap<>();
            columnMapping.put("ID", "id");
            columnMapping.put("Name", "name");
            columnMapping.put("Role", "role");*/
            beanStrategy.setColumnMapping(columnMapping);
            CsvToBean<T> csvToBean = new CsvToBean<>();
            CSVReader reader = new CSVReader(new FileReader(fileInputCsv));
            return csvToBean.parse(beanStrategy, reader);
        }catch(IOException e){
            logger.error("Can't parse the CSV file:"+ fileInputCsv.getAbsolutePath()+" -> "+e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    /**
     * Method use OpenCsv Library for
     * @param clazz the Class of the Bean.
     * @param fileInputCsv the File CSV to parse.
     * @param separator the char separator.
     * @param <T> the generic variable.
     * @return the List of Bean parsed from the CSV file.
     */
    public static <T> List<T> parseCSVFileAsList(Class<T> clazz,File fileInputCsv,char separator){
        try {
            List<T> beans;
            try ( //create CSVReader object
                    CSVReader reader = new CSVReader(new FileReader(fileInputCsv), separator)) {
                beans = new ArrayList<>();
                //read line by line
                String[] record;
                //skip header row
                String[] headers = reader.readNext();
                //read content
                while ((record = reader.readNext()) != null) {
                    T t = ReflectionUtilities.invokeConstructor(clazz);
                    for(int i=0; i < record.length; i++){
                        String nameMethod = "set"+ org.apache.commons.lang3.StringUtils.capitalize(headers[i]);
                        //invoke setter method
                        if(ReflectionUtilities.checkMethod(clazz,nameMethod)){
                            ReflectionUtilities.invokeSetter(t,nameMethod,record[i]);
                        }else{
                            logger.warn("Not exists the Method with name:"+nameMethod+" on the Bean:" +
                                    t.getClass().getName());
                        }
                    }
                    beans.add(t);
                }
            }
            return beans;
        }catch(IOException e){
            logger.error("Can't parse the CSV file:"+ fileInputCsv.getAbsolutePath()+" -> "+e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    /**
     * Method use OpenCsv Library for
     * @param fileInputCsv the File CSV to parse.
     * @param separator the char separator.
     * @return the List of Bean parsed from the CSV file.
     */
    public static List<String[]> parseCSVFileAsList(File fileInputCsv, char separator){
        try {
            List<String[]> records;
            //read all lines at once
            try ( //create CSVReader object
                    CSVReader reader = new CSVReader(new FileReader(fileInputCsv), separator)) {
                //read all lines at once
                records = reader.readAll();
                Iterator<String[]> iterator = records.iterator();
                records.clear();
                //skip header row
                iterator.next();
                while (iterator.hasNext()) {
                    String[] record = iterator.next();
                    records.add(record);
                }
            }
            return records;
        }catch(IOException e){
            logger.error("Can't parse the CSV file:"+ fileInputCsv.getAbsolutePath()+" -> "+e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    /**
     * Method to get the content of a comma separated file (.csv,.input,.txt)
     *
     * @param CSV    the File comma separated.
     * @param noHeaders if true jump the first line of the content.
     * @return the List of Array of the content of the File comma separated.
     */
    public static List<String[]> parseCSVFileAsList(File CSV, boolean noHeaders) {
        List<String[]> content;
        try {
            CSVReader reader1 = new CSVReader(new FileReader(CSV));
            content = reader1.readAll();
            /* List<String[]> myDatas = reader1.readAll();
            String[] lineI = myDatas.get(i);
            for (String[] line : myDatas) {
                for (String value : line) {
                    //do stuff with value
                }
            }*/
            if (noHeaders) content.remove(0);
            if(content.get(0).length <= 1){
                logger.warn("Attention: You haven't parsed correctly the CSV file with OpenCSV API try with Univocity Method");
            }
            return content;
        } catch (IOException e) {
            logger.error("Can't find the CSV File:" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to get the String array of the columns of a CSV File.
     *
     * @param fileCSV      the File CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] getHeaders(File fileCSV, boolean hasFirstLine) {
        String[] columns = new String[0];
        try {
            CSVReader reader = new CSVReader(new FileReader(fileCSV));
            columns = reader.readNext(); // assuming first read
            if (!hasFirstLine) {
                int columnCount = 0;
                if (columns != null) columnCount = columns.length;
                columns = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columns[i] = "Column#" + i;
                }
            }
        } catch (IOException e) {
            logger.error("Can't find the Headers on the CSV File", e);
        }
        return columns;
    }



    /**
     * Method to get the String array of the columns of a CSV File.
     * @param contentWithHeaders the {@link List} of {@link String} array of the content of the csv.
     * @return a String Array of the columns.
     */
    public static String[] getHeaders(List<String[]> contentWithHeaders) {
        String[] columns = new String[0];
        try {
            String[] headers = contentWithHeaders.get(0);
            if (headers.length <= 1) {
                throw new Exception("Can't find the delimiter with openCSV try with Univicity method.");
            }
        } catch (Exception e) {
            logger.error("Can't find the Headers on the content", e);
        }
        return columns;
    }

    /**
     * Method to get the String array of the columns of a CSV File.
     *
     * @param fileCSV      the File CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] getHeadersWithUnivocity(File fileCSV, boolean hasFirstLine) {
        return UnivocityUtilities.getHeaders(fileCSV,hasFirstLine);
    }

    //------------------------------------------------------------------------------

    /**
     * Method use OpenCsv Library for
     * @param fileInputCsv the File CSV to parse.
     * @param noHeaders if true the headers are excluded from the cotent.
     * @return the List of Bean parsed from the CSV file.
     */
    public static List<String[]> parseCSVFileAsListWithUnivocity(File fileInputCsv,boolean noHeaders){
        return UnivocityUtilities.parseCSVFileAsList(fileInputCsv,noHeaders);
    }

   /* public static void main(String[] args) throws IOException {

        List<Employee> emps = parseCSVFileLineByLine();
        System.out.println("**********");
        parseCSVFileAsList();
        System.out.println("**********");
        parseCSVToBeanList();
        System.out.println("**********");
        writeCSVData(emps);
    }*/

    public static String getFieldLatitude(String[] headers){
        Pattern pattern = Pattern.compile("(L|l)(at)(itude)?",Pattern.CASE_INSENSITIVE);
        return getField(headers,pattern);
    }

    public static String getFieldLongitude(String[] headers){
        Pattern pattern = Pattern.compile("(L|l)(on|ng)(gitude)?",Pattern.CASE_INSENSITIVE);
        return getField(headers,pattern);
    }

    public static String getField(String[] headers,Pattern pattern){
        //String[] headers = CSVGetHeaders(headers,true);
        for(String s : headers){
            if(pattern.matcher(String.valueOf(s)).matches()){
                return s;
            }
        }
        return "NULL";
    }

    /**
     * Parse CSV file using OpenCSV library and load in
     * given database table.
     * href: http://viralpatel.net/blogs/java-load-csv-file-to-database/.
     * Modified by rammar: https://github.com/BaderLab/pharmacogenomics/blob/master/src/CSVLoader/CSVLoader.java
     *
     * @param connection the {@link Connection} SQL.
     * @param separator the {@link Character} separator.
     * @param csvFile Input CSV InputStream
     * @param tableName Database table name to import data
     * @param truncateBeforeLoad Truncate the table before inserting
     *          new records.
     * @throws SQLException if any error is occurred with the SQL Connection.
     * @throws java.io.IOException if any error is occurred with the file.
     */
    public static void loadCSVToSQLTable(Connection connection, char separator, InputStream csvFile, String tableName,
                                         boolean truncateBeforeLoad) throws SQLException, IOException {

        String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
        String TABLE_REGEX = "\\$\\{table\\}";
        String KEYS_REGEX = "\\$\\{keys\\}";
        String VALUES_REGEX = "\\$\\{values\\}";
        CSVReader csvReader;
        if(null == connection) {
            throw new SQLException("Not a valid connection.");
        }
        try {

			/* Modified by rammar.
			 *
			 * I was having issues with the CSVReader using the "\" to escape characters.
			 * A MySQL CSV file contains quote-enclosed fields and non-quote-enclosed NULL
			 * values written as "\N". The CSVReader was removing the "\". To detect "\N"
			 * I must remove the escape character, and the only character you can replace
			 * it with that you are pretty much guaranteed will not be used to escape
			 * text is '\0'.
			 * I read this on:
			 * http://stackoverflow.com/questions/6008395/opencsv-in-java-ignores-backslash-in-a-field-value
			 * based on:
			 * http://sourceforge.net/p/opencsv/support-requests/5/
			 */
            // PREVIOUS VERSION: csvReader = new CSVReader(new FileReader(csvFile), this.seprator);
            csvReader = new CSVReader(new InputStreamReader(csvFile), separator, '"', '\0');

        } catch (Exception e) {
            throw new IOException("Error occured while executing file. "
                    + e.getMessage());
        }

        String[] headerRow = csvReader.readNext();

        if (null == headerRow) {
            throw new FileNotFoundException(
                    "No columns defined in given CSV file." +
                            "Please check the CSV file format.");
        }

        String questionmarks = StringUtils.repeat("?,", headerRow.length);
        questionmarks = (String) questionmarks.subSequence(0, questionmarks
                .length() - 1);


		/* NOTE from Ron: Header column names must match SQL table fields */
        String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
        query = query
                .replaceFirst(KEYS_REGEX, StringUtils.join(headerRow, ","));
        query = query.replaceFirst(VALUES_REGEX, questionmarks);

        //System.out.println("Query: " + query); // Modified by rammar to suppress output

        String[] nextLine;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = connection;
            con.setAutoCommit(false);
            ps = con.prepareStatement(query);

            if(truncateBeforeLoad) {
                //delete data from table before loading csv
                con.createStatement().execute("DELETE FROM " + tableName);
            }
            final int batchSize = 1000;
            int count = 0;
            Date date;
            while ((nextLine = csvReader.readNext()) != null) {
                int index = 1;
                for (String string : nextLine) {
                    date = DateUtilities.convertToDate(string);
                    if (null != date) {
                        ps.setDate(index++, new java.sql.Date(date
                                .getTime()));
                    } else {

                        /* Section modified by rammar to allow NULL values
                         * to be input into the DB. */
                        if (string.length() > 0 && !string.equals("\\N")) {
                            ps.setString(index++, string);
                        } else {
                            ps.setNull(index++, Types.VARCHAR);
                            //ps.setString(index++, null); // can use this syntax also - not sure which is better
                        }
                    }
                }
                ps.addBatch();
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            logger.info(count + " records loaded into " + tableName + " DB table");
            con.commit();
        } catch (SQLException | IOException e) {
            con.rollback();
            throw new IOException(
                    "Error occured while loading data from file to database."
                            + e.getMessage());
        } finally {
            if (null != ps)ps.close();
            con.close();
            csvReader.close();
        }
    }



}
