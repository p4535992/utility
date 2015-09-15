package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.log.SystemLog;

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
     * @param nameTableToCopy
     * @param nameTableCopied
     * @return
     */
    public static String copyTable(String nameTableToCopy,String nameTableCopied){
        String query = "CREATE TABLE " + nameTableCopied + " LIKE " + nameTableToCopy + ";";
        return query;
    }

    /**
     * ALTER TABLE yourTable ADD nameNewColumn [SQLTypes](size);
     * @param yourTable string name of the table.
     * @param nameNewColumn string name of the column to add.
     * @param SQLTypes java.sql.Types of the column to add.
     * @param size the siza of the column to add.
     * @return string query.
     */
    public static String addColumnToTable(String yourTable,String nameNewColumn,int SQLTypes,Integer size){
        String query = "ALTER TABLE " + yourTable + " ADD nameNewColumn "+SQLHelper.convertSQLTypes2String(SQLTypes)+"("+size+");";
        return query;
    }

    /**
     * UPDATE yourTable SET nameColumnCodified = MD5(nameColumnToCodify) WHERE nameColumnCodified IS NULL;
     * @param yourTable string name of the table.
     * @param nameColumnToCodify string name of the column to codify.
     * @param nameColumnCodified string name of the column to insert.
     * @return string query.
     */
    public static String setMD5ColumnToTable(
            String yourTable,String nameColumnToCodify,String nameColumnCodified,boolean ifNull){
        String query = "UPDATE "+yourTable+" \n" +
                "SET "+nameColumnCodified+" = MD5("+nameColumnToCodify+") \n";
        if(ifNull) query += "WHERE "+nameColumnCodified+" IS NULL; \n";
        else query += "; \n";
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
    public static String addCopyColumnWithPrefix(String yourTable,String nameColumnToCopy,String nameColumnToInsert,String prefix){
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
    public static String addPrefixToColumn(String yourTable,String nameColumnToUpdate,String prefix){
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
    public static String setColumnConcatenationFromTwoColumns(
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
    public static String setColumnConcatenationContent(
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
    public static String setColumnConcatenationSingleColumn(
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
    public static String setColumnConcatenationMultipleColumns(String yourTable,String nameColumnToUpdate,String[] arrayColumns){
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


    public static String deleteDuplicateRecord(String yourTable,String nameKeyColumn,String[] cols){
        return "WHILE EXISTS (SELECT COUNT(*) FROM "+yourTable+" GROUP BY "+cols+" HAVING COUNT(*) > 1)\n" +
                "BEGIN\n" +
                "    DELETE FROM "+yourTable+" WHERE "+nameKeyColumn+" IN \n" +
                "    (\n" +
                "        SELECT MIN("+nameKeyColumn+") as [DeleteID]\n" +
                "        FROM "+yourTable+"\n" +
                "        GROUP BY "+cols+"\n" +
                "        HAVING COUNT(*) > 1\n" +
                "    )\n" +
                "END";
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

    public static String prepareInsertIntoQuery(String myInsertTable,String[] columns, Object[] values, Integer[] types) {
        return prepareInsertIntoQuery(myInsertTable, columns, values, CollectionKit.convertIntegersToInt(types));
    }

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
                        //if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(Integer.class.getName())){
                        values[i] = null;
                    }
                    else if (values[i]!=null && values[i] instanceof String) {
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
        bQuery.append("DELETE ");
        bQuery.append("table1 FROM ").append(myDeleteTable).append(" table1,").append(myDeleteTable).append(" table2");
        bQuery.append(" WHERE ");
        for(int i=0; i< columns.length; i++){
            if(Arrays.asList(columns_where).contains(columns[i])){
                bQuery.append("table1.").append(columns[i]).append("= table2.").append(columns[i]);
                if (condition != null && i < columns.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
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
}
