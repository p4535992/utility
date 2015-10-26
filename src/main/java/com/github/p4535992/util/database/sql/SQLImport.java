package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.database.jooq.SQLJooqKit2;
import com.github.p4535992.util.file.impl.FileCSV;
import com.github.p4535992.util.log.SystemLog;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by 4535992 on 24/10/2015.
 */
public class SQLImport {

    public static void importCsvInsert(File fileCSV, boolean firstLine,Character separator,
                               String nameTable,Connection connection) {

        String[] columns = FileCSV.getColumns(fileCSV,firstLine);
        SQLJooqKit2.setConnection(connection);
        try {
            CSVReader reader = new CSVReader(new FileReader(fileCSV), separator);
            //String insertQuery =
            String insertQuery;
            //PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            String[] rowData = null;
            while((rowData = reader.readNext()) != null){
                if(firstLine) {continue;}
                else {
                    String[] values = new String[columns.length];
                    int[] types = new int[values.length];
                    for(int i = 0; i < rowData.length; i++){
                        values[i] = rowData[i];
                        types[i] = SQLHelper.convertStringToSQLTypes(values[i]);
                    }
                    insertQuery = SQLJooqKit2.insert(nameTable, columns,values,types);
                    SQLHelper.executeSQL(insertQuery,connection);
                }
            }
            //System.out.println("Data Successfully Uploaded");
        } catch (Exception e) {
            SystemLog.exception(e,SQLImport.class);
        }
    }

    public static void importCSVLocalLoad(File fileCSV,boolean firstLine,Character fieldSeparator,
                                          Character linesSeparator,String nameTable,Connection connection){
        try {
            String[] columns = FileCSV.getColumns(fileCSV,firstLine);
            String loadQuery = "LOAD DATA LOCAL INFILE '" +fileCSV.getAbsolutePath() +
                    "' INTO TABLE "+nameTable+" FIELDS TERMINATED BY '"+fieldSeparator+"'"+
                    " LINES TERMINATED BY '"+linesSeparator+" ( " +
                    CollectionKit.convertArrayContentToSingleString(columns)+
                    ") ";
            SystemLog.query(loadQuery);
            SQLHelper.executeSQL(loadQuery,connection);
        }
        catch (Exception e){
            SystemLog.exception(e, SQLImport.class);
        }
    }

    public static String importCSVBulk(File fileCSV,String nameTable,String database,boolean hasFirstLine,
                                       String fieldTerminator,String rowTerminator){
        StringBuilder bQuery = new StringBuilder();
        String[] columns;
        if(hasFirstLine) columns = FileCSV.getColumns(fileCSV,true);
        else  columns = FileCSV.getColumns(fileCSV,false);

        bQuery.append(SQLQuery.createTableToInsertData(database,nameTable,columns));
        bQuery.append("GO \n");
        //Insert the content on the table.
        bQuery.append("BULK INSERT ").append(nameTable)
                .append(" FROM '").append(fileCSV.getAbsolutePath()).append("'")
                .append(" WITH ( ").append("FIELDTERMINATOR = '").append(fieldTerminator).append("',")
                .append("ROWTERMINATOR = '").append(rowTerminator).append("')\n");
        bQuery.append("GO \n");
        return bQuery.toString();
    }
}
