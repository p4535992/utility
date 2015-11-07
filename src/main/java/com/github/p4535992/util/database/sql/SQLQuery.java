package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.file.impl.FileCSV;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringUtil;
import com.github.p4535992.util.string.impl.StringIs;

import java.io.File;
import java.sql.Connection;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by 4535992 on 14/07/2015.
 * @author 4535992.
 * @version 2015-07-14.
 */
@SuppressWarnings("unused")
public class SQLQuery {

    /**
     * CREATE TABLE  nameTableCopied LIKE  nameTableToCopy;
     * @param nameTableToCopy string name of the table to copy.
     * @param nameTableCopied string name of the table copied.
     * @return string content of the query.
     */
    public static String copyTable(String nameTableToCopy,String nameTableCopied){
        return "CREATE TABLE " + nameTableCopied + " LIKE " + nameTableToCopy + ";";
    }

    /**
     * ALTER TABLE yourTable ADD nameNewColumn [SQLTypes](size);
     * @param yourTable string name of the table.
     * @param nameNewColumn string name of the column to add.
     * @param SQLTypes java.sql.Types of the column to add.
     * @param size the siza of the column to add.
     * @return string query.
     */
    public static String alterAddColumn(String yourTable,String nameNewColumn,int SQLTypes,Integer size){
        return "ALTER TABLE " + yourTable + " ADD nameNewColumn "+SQLHelper.convertSQLTypes2String(SQLTypes)+"("+size+");";
    }

    /**
     * UPDATE yourTable SET nameColumnCodified = MD5(nameColumnToCodify) WHERE nameColumnCodified IS NULL;
     * @param yourTable string name of the table.
     * @param nameColumnToCodify string name of the column to codify.
     * @param nameColumnToUpdate string name of the column to insert.
     * @param ifNull if true set the new Value only where the old value is NULL.
     * @return string query.
     */
    public static String updateColumnToMD5Hash(
        String yourTable,String nameColumnToCodify,String nameColumnToUpdate,boolean ifNull){
        String query = "UPDATE "+yourTable+" SET "+nameColumnToUpdate+" = MD5("+nameColumnToCodify+") ";
        if(ifNull) query += "WHERE "+nameColumnToUpdate+" IS NULL; ";
        else query += "; ";
        return query;
    }

    /**
     * SELECT nameColumnToCopy FROM yourTable;
     * UPDATE yourTable SET nameColumnToInsert = nameColumnToCopy;
     * UPDATE yourTable SET nameColumnToInsert = CONCAT('prefix',nameColumnToInsert);
     * @param yourTable string name of the table.
     * @param nameColumnToCopy string name of the column to copy.
     * @param nameColumnToInsert string name of the column to insert.
     * @param prefix string prefix on the new copied column.
     * @return string query.
     */
    public static String updateCopyAColumnWithPrefix(String yourTable,String nameColumnToCopy,String nameColumnToInsert,String prefix){
        return "SELECT "+nameColumnToCopy+" FROM " + yourTable + "; \n" +
                "UPDATE " + yourTable + " SET "+nameColumnToInsert+" = "+nameColumnToCopy+"; \n"+
                "UPDATE " + yourTable + " SET "+nameColumnToInsert+" = CONCAT('"+prefix+"',"+nameColumnToInsert+");"
                ;
    }

    /**
     * UPDATE yourTable  SET nameColumnToUpdate = CONCAT('+prefix+',nameColumnToUpdate);
     * @param yourTable string name of the table.
     * @param nameColumnToUpdate string name of the column to update.
     * @param prefix string of the prefix to add at the column to update.
     * @return string query.
     */
    public static String updateAddPrefixToColumn(String yourTable,String nameColumnToUpdate,String prefix){
        return "UPDATE " + yourTable + " SET "+nameColumnToUpdate+" = CONCAT('"+prefix+"',"+nameColumnToUpdate+");";
    }

    /**
     * SELECT nameColumnToSelect FROM yourTable WHERE nameColumnToSelect LIKE ='startWith% ;
     * @param yourTable string name of the table.
     * @param nameColumnToSelect string name of the column to select;
     * @param startWith string prefix of the value of the record.
     * @return string query.
     */
    public static String selectWhereColumnStartWith(String yourTable,String nameColumnToSelect,String startWith){
        return "SELECT " + nameColumnToSelect +" FROM "+yourTable+" WHERE "+nameColumnToSelect+ " LIKE ='"+startWith+"% ;";
    }

    /**
     * UPDATE geodb.infodocument_coord_omogeneo_05052014
     * SET indirizzo = CONCAT_WS(', ', indirizzoNoCAP, indirizzoHasNumber)
     * @param yourTable string name of the table.
     * @param nameColumnToUpdate string name of the column to update.
     * @param nameFirstColumnConcatenate string name of the first column.
     * @param nameSecondColumnConcatenate string name of the second column.
     * @param separator string of the separator.
     * @return string query.
     */
    public static String updateColumnConcatenationFromTwoColumns(
            String yourTable,String nameColumnToUpdate,
            String nameFirstColumnConcatenate,String nameSecondColumnConcatenate,
            String separator){
        return "UPDATE "+yourTable+" SET "+nameColumnToUpdate+" = CONCAT_WS('"+separator+"',"
                + nameFirstColumnConcatenate+", "+nameSecondColumnConcatenate+");";
    }

    //update t set data=concat(data, 'a');

    /**
     * UPDATE yourTable SET nameColumnToUpdate = CONCAT(nameColumnToUpdate,'content');"
     * @param yourTable string name of the table.
     * @param nameColumnToUpdate string name of the column to update.
     * @param content string to append to the column.
     * @return string query.
     */
    public static String updateColumnConcatenationContent(
            String yourTable,String nameColumnToUpdate,String content){
        return "UPDATE "+yourTable+" SET "+nameColumnToUpdate+" = CONCAT("+nameColumnToUpdate+",'"+content+"');";
    }

    /**
     * UPDATE yourTable SET nameColumnToUpdate = CONCAT(nameColumnToUpdate,nameFirstColumn);;
     * @param yourTable string name of the table.
     * @param nameColumnToUpdate string name of the column to update.
     * @param nameFirstColumn string name of the column.
     * @return string query.
     */
    public static String updateColumnConcatenationSingleColumn(
            String yourTable,String nameColumnToUpdate,String nameFirstColumn){
        return "UPDATE "+yourTable+" SET "+nameColumnToUpdate+" = CONCAT("+nameColumnToUpdate+","+nameFirstColumn+");";
    }

    /**
     * CONCAT(Title,' ',FirstName,' ',MiddleName,' ',LastName)
     * @param yourTable string name of the table.
     * @param nameColumnToUpdate string name of the column to update.
     * @param arrayColumns array of the strings names of the columns to concatenate.
     * @return string query.
     */
    public static String updateColumnConcatenationMultipleColumns(String yourTable,String nameColumnToUpdate,String[] arrayColumns){
        StringBuilder builder = new StringBuilder("UPDATE "+yourTable+" SET "+nameColumnToUpdate+" = CONCAT(");
        for(int i=0; i < arrayColumns.length; i++){
            builder.append(arrayColumns[i]);
            if(i < arrayColumns.length-1){
                builder.append(",' ',");
            }
        }
        builder.append(");");
        return builder.toString();
    }

    /**
     * Method to create a SQL String to delete all duplicate record for a specific key.
     * @param yourTable the name of the table.
     * @param nameKeyColumn the key column to analyze.
     * @param cols the Array of columns to check.
     * @return the Delete all duplicates from a Table String SQL.
     */
    public static String deleteDuplicateRecord(String yourTable,String nameKeyColumn,String[] cols){
        return
        "WHILE EXISTS (SELECT COUNT(*) FROM "+yourTable+" GROUP BY "+
                CollectionKit.convertArrayContentToSingleString(cols)+" HAVING COUNT(*) > 1)\n" +
        "BEGIN\n" +
        "    DELETE FROM "+yourTable+" WHERE "+nameKeyColumn+" IN \n" +
        "    (\n" +
        "        SELECT MIN("+nameKeyColumn+") as [DeleteID]\n" +
        "        FROM "+yourTable+"\n" +
        "        GROUP BY "+CollectionKit.convertArrayContentToSingleString(cols)+"\n" +
        "        HAVING COUNT(*) > 1\n" +
        "    )\n" +
        "END";
    }

    /**
     * Method to create a SQL String to delete all duplicate record for a specific key.
     * @param yourTable the name of the table.
     * @param cols the Array of columns to check.
     * @return the Delete all duplicates from a Table String SQL.
     */
    public String deleteDuplicateRecord(String yourTable,String[] cols){
        return "WITH "+yourTable+" AS ( " +
                "SELECT ROW_NUMBER() OVER(PARTITION BY "+CollectionKit.convertArrayContentToSingleString(cols)+
                " ORDER BY "+CollectionKit.convertArrayContentToSingleString(cols)+") AS ROW " +
                "FROM "+yourTable+") " +
                "DELETE FROM "+yourTable+" " +
                "WHERE ROW > 1;";
    }

    public static String prepareSelectQuery(String mySelectTable,
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
        StringBuilder bQuery = new StringBuilder();
        boolean statement = false;
        //PREPARE THE QUERY STRING
        bQuery.append("SELECT ");
        if(CollectionKit.isArrayEmpty(columns) || (columns.length==1 && columns[0].equals("*"))){
            bQuery.append(" * ");
        }else{
            for(int i = 0; i < columns.length; i++){
                bQuery.append(" ").append(columns[i]).append("");
                if(i < columns.length-1){
                    bQuery.append(", ");
                }
            }
        }
        bQuery.append(" FROM ").append(mySelectTable).append(" ");
        if(!CollectionKit.isArrayEmpty(columns_where)) {
            if(values_where==null){
                statement = true;
                //values_where = new Object[columns_where.length];
                //for(int i = 0; i < columns_where.length; i++){values_where[i]="?";}
            }
            bQuery.append(" WHERE ");
            for (int k = 0; k < columns_where.length; k++) {
                bQuery.append(columns_where[k]).append(" ");
                if(statement){
                    bQuery.append(" = ? ");
                }else {
                    if (values_where[k] == null) {
                        bQuery.append(" IS NULL ");
                    } else {
                        bQuery.append(" = '").append(values_where[k]).append("'");
                    }
                }
                if (condition != null && k < columns_where.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
                } else {
                    bQuery.append(" ");
                }
            }
        }
        if(limit != null && offset!= null) {
            bQuery.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
        }
        return bQuery.toString();
    }

    public static String prepareUpdateQuery(String myUpdateTable,
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition){
        StringBuilder bQuery = new StringBuilder();
        boolean statement = false;
        bQuery.append("UPDATE ").append(myUpdateTable).append(" SET ");
        int f = 0;
        for (int k = 0; k < columns.length; k++) {
            bQuery.append(columns[k]).append("=? ");
            if(CollectionKit.isArrayEmpty(values)) {
                if (values[k] == null) {
                    values[f] = "NULL";
                    f++;
                } else {
                    values[f] = values[k];
                    f++;
                }
            }
            if (k < columns.length - 1) {
                bQuery.append(", ");
            }
        }
        if(!CollectionKit.isArrayEmpty(columns_where)) {
            if(values_where==null){
                statement = true;
            }
            bQuery.append(" WHERE ");
            for (int k = 0; k < columns_where.length; k++) {
                bQuery.append(columns_where[k]).append(" ");
                if(statement){
                    bQuery.append(" = ? ");
                }else {
                    if (values_where[k] == null) {
                        bQuery.append(" IS NULL ");
                    } else {
                        bQuery.append(" = '").append(values_where[k]).append("'");
                    }
                }
                if (condition != null && k < columns_where.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
                }
            }
        }
        return  bQuery.toString();
    }

    /**
     * Method to create a String Query Insert Into.
     * @param myInsertTable the name of the table where insert.
     * @param columns the Array of columns of the table.
     * @param values the Array of values of the the columns.
     * @param types the array of SQL Types of the Values.
     * @return the String Insert Into SQL.
     */
    public static String prepareInsertIntoQuery(String myInsertTable,String[] columns, Object[] values, Integer[] types) {
        return prepareInsertIntoQuery(myInsertTable, columns, values, CollectionKit.convertIntegersToInt(types));
    }

    /**
     * Method to create a String Query Insert Into.
     * @param myInsertTable the name of the table where insert.
     * @param columns the Array of columns of the table.
     * @param values the Array of values of the the columns.
     * @param types the array of SQL Types of the Values.
     * @return the String Insert Into SQL.
     */
    public static String prepareInsertIntoQuery(String myInsertTable,String[] columns, Object[] values, int[] types){
        StringBuilder bQuery = new StringBuilder();
        try {
            boolean statement = false;
            bQuery.append("INSERT INTO ").append(myInsertTable).append("  (");
            for (int i = 0; i < columns.length; i++) {
                bQuery.append(columns[i]);
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(" ) VALUES ( ");
            if (values == null) {
                statement = true;
            }
            for (int i = 0; i < columns.length; i++) {
                if (statement) {
                    bQuery.append("?");
                } else {
                    if(values[i]== null || Objects.equals(values[i].toString(), "NULL")){
                        values[i] = null;
                    }else if (values[i]!=null && values[i] instanceof String) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else if (values[i]!=null && values[i] instanceof java.net.URL) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else{
                        values[i] = " " + values[i] + " ";
                    }
                    bQuery.append(values[i]);
                }
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(");");
        }catch (NullPointerException e){
            SystemLog.warning("Attention: you probably have forgotten  to put some column for the SQL query");
            SystemLog.exception(e);
        }
        return bQuery.toString();
    }


    /**
     * Method to create a String Query Insert Into.
     * @param myInsertTable the name of the table where insert.
     * @param columns the Array of columns of the table.
     * @param values the Array of values of the the columns.
     * @return the String Insert Into SQL.
     */
    public static String prepareInsertIntoQuery(String myInsertTable,String[] columns,Object[] values){
        StringBuilder bQuery = new StringBuilder();
        try {
            boolean statement = false;
            bQuery.append("INSERT INTO ").append(myInsertTable).append(" (");
            for (int i = 0; i < columns.length; i++) {
                bQuery.append(columns[i]);
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(") VALUES (");
            if (values == null) {
                statement = true;
            }
            for (int i = 0; i < columns.length; i++) {
                if (statement) {
                    bQuery.append("?");
                } else {
                    if(values[i]== null){
                        //values[i]= " NULL ";
                    }else if (values[i]!=null && values[i] instanceof String) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else if (values[i]!=null && values[i] instanceof java.net.URL) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else{
                        values[i] = " " + values[i] + " ";
                    }
                    bQuery.append(values[i]);
                }
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(");");
        }catch (NullPointerException e){
            SystemLog.warning("Attention: you probably have forgotten to put some column for the SQL query");
            SystemLog.exception(e);
        }
        return bQuery.toString();
    }

    /**
     * Method to create a query string for the delete operation.
     * http://stackoverflow.com/questions/3311903/remove-duplicate-rows-in-mysql
     * @param myDeleteTable String name of the table where execute the edelete operation.
     * @param columns string Array of columns name of the record.
     * @param values string Array of value of the record.
     * @param columns_where string Array of columns name for where condition of the record.
     * @param values_where string Array of value for where condition of the record.
     * @param condition string condition AND,OR.
     * @return the string for delete a query.
     */
    public String prepareDeleteQuery(String myDeleteTable,
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition){
       /* query ="ALTER IGNORE TABLE "+mySelectTable+" ADD UNIQUE INDEX idx_name ("+
                StringKit.convertArrayContentToSingleString(columns) +" );";*/
        StringBuilder bQuery = new StringBuilder();
        bQuery.append("DELETE FROM ").append(myDeleteTable);
        bQuery.append(" WHERE ");
        for(int i=0; i< columns.length; i++){
            if(Arrays.asList(columns_where).contains(columns[i])){
                bQuery.append(myDeleteTable).append(".").append(columns[i]).append("=")
                        .append(myDeleteTable).append(".").append(values_where[i]);
                if (condition != null && i < columns.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
                }else{
                    bQuery.append(" AND ");
                }
            }
        }
        return bQuery.toString();
    }
//   public Object[] prepareValues(Object[] values,int[] types){
//        for(int i = 0; i < values.length; i++){
//            if(values[i]== null){
//                if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(Integer.class.getName())){
//                    values[i] = "NULL";
//                    types[i] = Types.NULL;
//                }
//                else if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(int.class.getName())){
//                    values[i] = "NULL" ;
//                    types[i] = Types.NULL;
//                }
//                else if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(String.class.getName())){
//                    values[i] = "NULL";
//                    types[i] = Types.NULL;
//                }
//                else {
//                    values[i] = "NULL";
//                    types[i] = Types.NULL;
//                }
//            }else{
//                if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(String.class.getName())){
//                    values[i] = values[i].toString();
//                    types[i] = Types.VARCHAR;
//                }
//            }
//        }
//        return values;
//    }

    //-------------------------------------------
    // OTHER METHODS 2015-10-23
    //-------------------------------------------

    /**
     * Method to create a String SQL for create a table.
     * @param nameTable the name of the table.
     * @param columns the list of columns.
     * @return the String SQL for create a Table.
     */
    public static String createTableToInsertData(String nameTable,String[] columns){
        return createTableToInsertData(null,nameTable,columns);
    }

    /**
     * Method to create a String SQL for create a table.
     * @param database the name of the database to use.
     * @param nameTable the name of the table.
     * @param columns the list of columns.
     * @return the String SQL for create a Table.
     */
    public static String createTableToInsertData(String database,String nameTable,String[] columns){
        StringBuilder bQuery = new StringBuilder();
        //CREATE TABLE TO INSERT DATA
        if(database!=null) {
            bQuery.append("USE ").append(database).append("\n")
                    .append("GO \n");
        }
        bQuery.append("CREATE TABLE ").append(nameTable).append(" (").append("\n");
        for(int i=0; i < columns.length; i++){
            bQuery.append(columns[i]).append(" ") .append(
                    SQLHelper.convertSQLTypes2String(
                            SQLHelper.convertStringToSQLTypes(columns[i]))).append("(255)");
            if(i < columns.length) bQuery.append(", ");

        }
        bQuery.append(")\n");
        return bQuery.toString();
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

    public static String importCSVLocalLoad(File fileCSV,boolean firstLine,Character fieldSeparator,
                                            Character linesSeparator,String nameTable){
        StringBuilder loadQuery = new StringBuilder();
        try {
            String[] columns = FileCSV.getColumns(fileCSV,firstLine);
            loadQuery.append("LOAD DATA LOCAL INFILE '").append(fileCSV.getAbsolutePath())
                    .append("' INTO TABLE ").append(nameTable).append(" FIELDS TERMINATED BY '")
                    .append(fieldSeparator).append("'").append(" LINES TERMINATED BY '")
                    .append(linesSeparator).append(" ( ")
                    .append(CollectionKit.convertArrayContentToSingleString(columns)).append(") ");
            SystemLog.query(loadQuery.toString());
            //SQLHelper.executeSQL(loadQuery,connection);
        }
        catch (Exception e){
            SystemLog.exception(e, SQLQuery.class);
        }
        return loadQuery.toString();
    }
}
