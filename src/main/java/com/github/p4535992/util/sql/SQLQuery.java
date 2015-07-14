package com.github.p4535992.util.sql;

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
}
