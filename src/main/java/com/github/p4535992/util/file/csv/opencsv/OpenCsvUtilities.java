package com.github.p4535992.util.file.csv.opencsv;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.io.*;
import java.util.*;

/**
 * Created by 4535992 on 28/12/2015.
 * href:http://www.journaldev.com/2544/java-csv-parserwriter-example-using-opencsv-apache-commons-csv-and-supercsv.
 */
public class OpenCsvUtilities extends FileUtilities{

    public static <T> List<T> parseCSVToBeanList(Class<T> clazz,Map<String, String> columnMapping,File inputCSV)
            throws IOException {
        HeaderColumnNameTranslateMappingStrategy<T> beanStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
        //beanStrategy.setType(clazz); //deprecated
        /*Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("ID", "id");
        columnMapping.put("Name", "name");
        columnMapping.put("Role", "role");*/
        beanStrategy.setColumnMapping(columnMapping);
        CsvToBean<T> csvToBean = new CsvToBean<>();
        CSVReader reader = new CSVReader(new FileReader(inputCSV));
        return csvToBean.parse(beanStrategy, reader);
    }

    public static <T> void writeCSVDataWithBean(List<T> emps,char separator) throws IOException {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer,separator);
        List<String[]> data  = toStringArray(emps);
        csvWriter.writeAll(data);
        csvWriter.close();
        //System.out.println(writer);
    }

    public static void writeCSVData(List<String[]> content,char separator) throws IOException {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer,separator);
        csvWriter.writeAll(content);
        csvWriter.close();
        //System.out.println(writer);
    }

    public static void writeCSVDataToConsole(List<String[]> content,char separator) throws IOException {
        Writer writer = new OutputStreamWriter(System.out);
        CSVWriter csvWriter = new CSVWriter(writer,separator);
        csvWriter.writeAll(content);
        csvWriter.close();
        //System.out.println(writer);
    }


    public static void writeCSVDataToConsole(List<String[]> content) throws IOException {
        Writer writer = new OutputStreamWriter(System.out, StringUtilities.UTF_8);
        CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
        csvWriter.writeAll(content,false);
        csvWriter.close();
        //System.out.println(writer);
    }

    private static <T> List<String[]> toStringArray(List<T> emps) {
        return toStringArray(emps,null);
    }

    private static <T> List<String[]> toStringArray(List<T> emps,String[] addNewHeader) {
        List<String[]> records = new ArrayList<>();
        //add header record
        //records.add(new String[]{"ID","Name","Role","Salary"});
        if(addNewHeader != null) records.add(addNewHeader);
        Iterator<T> it = emps.iterator();
        while(it.hasNext()){
            T emp = it.next();
            //records.add(new String[]{emp.getId(),emp.getName(),emp.getRole(),emp.getSalary()});
        }
        return records;
    }

    private static <T> List<T> parseCSVFileLineByLine(Class<T> clazz,File inputCsv,char separator) throws IOException {
        //create CSVReader object
        CSVReader reader = new CSVReader(new FileReader(inputCsv), separator);
        List<T> emps = new ArrayList<>();
        //read line by line
        String[] record;
        //skip header row
        reader.readNext();
        while((record = reader.readNext()) != null){
            T t = ReflectionUtilities.invokeConstructor(clazz);

            //invoke setter method
            //.....................
            emps.add(t);
        }
        reader.close();
        return emps;
    }

    private static List<String[]> parseCSVFileAsList(File inputCsv,char separator) throws IOException {
        //create CSVReader object
        CSVReader reader = new CSVReader(new FileReader(inputCsv), separator);
        //read all lines at once
        List<String[]> records = reader.readAll();
        Iterator<String[]> iterator = records.iterator();
        records.clear();
        //skip header row
        iterator.next();
        while(iterator.hasNext()){
            String[] record = iterator.next();
            records.add(record);
        }
        reader.close();
        return records;
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
