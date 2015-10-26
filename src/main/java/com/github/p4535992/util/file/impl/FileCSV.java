package com.github.p4535992.util.file.impl;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by 4535992 on 23/10/2015.
 */
public class FileCSV {

    public static String[] getColumns(File fileCSV,boolean hasFirstLine){
        try {
            //csv file containing data
            //org.jooq.tools.csv.CSVReader reader = new org.jooq.tools.csv.CSVReader(new FileReader(fileCSV));
            CSVReader reader = new CSVReader(new FileReader(fileCSV));
            //List<String[]> content = reader.readAll();
            String[] header = reader.readNext(); // assuming first read
            if(hasFirstLine) return header;
            else{
                int columnCount =0;
                if (header != null)  columnCount = header.length;
                String[] columns = new String[columnCount];
                for(int  i=0; i< columnCount; i++){
                    columns[i] = "Column#"+i;
                }
                return columns;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
