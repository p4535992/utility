package com.github.p4535992.util.file.csv.opencsv;

import com.github.p4535992.util.collection.ListUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

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
            CSVWriter csvWriter = new CSVWriter(writer, separator);
            List<String[]> data = toStringArray(beans);
            csvWriter.writeAll(data);
            csvWriter.close();
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
            List<String> record = new ArrayList<>();
            //invoke getter method and convert to String
            Class<T> clazz = (Class<T>) bean.getClass();
            //T t = ReflectionUtilities.invokeConstructor(clazz);
            List<Method> getter = (List<Method>) ReflectionUtilities.findGetters(clazz,true);
            for(Method method : getter){
                record.add(String.valueOf(ReflectionUtilities.invokeGetter(bean,method)));
            }
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
            //create CSVReader object
            CSVReader reader = new CSVReader(new FileReader(fileInputCsv), separator);
            List<T> beans = new ArrayList<>();
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
            reader.close();
            return beans;
        }catch(IOException e){
            logger.error("Can't parse the CSV file:"+ fileInputCsv.getAbsolutePath()+" -> "+e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    public static List<String[]> parseCSVFileAsList(File fileInputCsv, char separator){
        try {
            //create CSVReader object
            CSVReader reader = new CSVReader(new FileReader(fileInputCsv), separator);
            //read all lines at once
            List<String[]> records = reader.readAll();
            Iterator<String[]> iterator = records.iterator();
            records.clear();
            //skip header row
            iterator.next();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                records.add(record);
            }
            reader.close();
            return records;
        }catch(IOException e){
            logger.error("Can't parse the CSV file:"+ fileInputCsv.getAbsolutePath()+" -> "+e.getMessage(),e);
            return new ArrayList<>();
        }
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



}
