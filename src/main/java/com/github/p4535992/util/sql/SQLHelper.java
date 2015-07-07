package com.github.p4535992.util.sql;

import com.github.p4535992.util.log.SystemLog;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4535992 on 14/05/2015.
 * @author 4535992
 * @version 2015-06-26
 */
@SuppressWarnings("unused")
public class SQLHelper {

    private static Connection conn;

    public static Map<Integer, String> getAllJdbcTypeNames() throws IllegalArgumentException, IllegalAccessException {
        Map<Integer, String> result = new HashMap<>();
        for (Field field : Types.class.getFields()) {
            result.put((Integer)field.get(null), field.getName());
        }
        return result;
    }

    /**
     * Method for get a mapt with all SQL java types.
     * href: http://www.java2s.com/Code/Java/Database-SQL-JDBC/convertingajavasqlTypesintegervalueintoaprintablename.htm.
     * @param jdbcType code int of the type sql.
     * @return map of SQL Types with name
     */
    public static Map<Integer,String> getJdbcTypeName(int jdbcType) {
        Map<Integer,String> map = new HashMap<>();
        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                Integer value = (Integer) field.get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
                SystemLog.exception(e);
            }
        }
        return map;
    }

    /**
     * Method for convert a SQLTypes to a java class.
     * @param type identificator for the SQL java types.
     * @return the corespondetn java class.
     */
    public static Class<?> convertSQLTypes2JavaClass(int type) {
        Class<?> result = Object.class;
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = String.class;
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;
            case Types.BIT:
                result = Boolean.class;
                break;
            case Types.TINYINT:
                result = Byte.class;
                break;
            case Types.SMALLINT:
                result = Short.class;
                break;
            case Types.INTEGER:
                result = Integer.class;
                break;
            case Types.BIGINT:
                result = Long.class;
                break;
            case Types.REAL:
            case Types.FLOAT:
                result = Float.class;
                break;
            case Types.DOUBLE:
                result = Double.class;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;
            case Types.DATE:
                result = java.sql.Date.class;
                break;
            case Types.TIME:
                result = java.sql.Time.class;
                break;
            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
            case Types.NULL:
                result = Object.class.getSuperclass();
        }
        return result;
    }

    /**
     * Method for convert a java class to a SQLTypes.
     * @param aClass the corespondetn java class.
     * @return the identificator for the SQL java types.
     */
    public static int convertClass2SQLTypes(Class<?> aClass) {
        int result;
        if(aClass.getName().equals(String.class.getName()))result = Types.VARCHAR;
        else if(aClass.getName().equals(java.math.BigDecimal.class.getName()))result = Types.NUMERIC;
        else if(aClass.getName().equals(Boolean.class.getName()))result = Types.BIT;
        else if(aClass.getName().equals(int.class.getName()))result = Types.INTEGER;
        else if(aClass.getName().equals(Byte.class.getName()))result = Types.TINYINT;
        else if(aClass.getName().equals(Short.class.getName()))result = Types.SMALLINT;
        else if(aClass.getName().equals(Integer.class.getName()))result = Types.INTEGER;
        else if(aClass.getName().equals(Long.class.getName())) result = Types.BIGINT;
        else if(aClass.getName().equals(Float.class.getName()))result = Types.REAL;
        else if(aClass.getName().equals(Double.class.getName()))result = Types.DOUBLE;
        else if(aClass.getName().equals(Byte[].class.getName()))result = Types.VARBINARY;
        else if(aClass.getName().equals(java.sql.Date.class.getName())) result = Types.DATE;
        else if(aClass.getName().equals(java.sql.Time.class.getName()))result = Types.TIME;
        else if(aClass.getName().equals(java.sql.Timestamp.class.getName()))result = Types.TIMESTAMP;
        else if(aClass.getName().equals(java.net.URL.class.getName()))result = Types.VARCHAR;
        else result = Types.NULL;
        return result;
    }

    public static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    public static java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }




    public static Connection getHSQLConnection(String database,String username,String password) throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        String url = "jdbc:hsqldb:data/"+database;
        return  conn = DriverManager.getConnection(url, username, password);
    }

    public static Connection getMySqlConnection(String database,String username,String password) throws Exception {
        Class.forName("org.gjt.mm.mysql.Driver");
        String url = "jdbc:mysql://localhost/"+database;
        return conn = DriverManager.getConnection(url, username, password);
    }

    public static Connection getOracleConnection(String database,String username,String password) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
        return conn = DriverManager.getConnection(url, username, password);
    }

    /**
     * UPDATE yourTable SET nameColumnToInsert = MD5(nameColumnToCodify) WHERE nameColumnToInsert IS NULL;
     * @param yourTable
     * @param nameColumnToCodify
     * @param nameColumnToInsert
     * @return
     */
    public static String addMD5ColumnToTheTable(String yourTable,String nameColumnToCodify,String nameColumnToInsert,boolean ifNull){
        String query = "UPDATE "+yourTable+" \n" +
                "SET "+nameColumnToInsert+" = MD5("+nameColumnToCodify+") \n";
                if(ifNull) query += "WHERE "+nameColumnToInsert+" IS NULL; \n";
                 else query += "; \n";
        return query;
    }

    /**
     * SELECT nameColumnToCopy FROM yourTable;
     * UPDATE yourTable SET nameColumnToInsert = nameColumnToCopy;
     * UPDATE yourTable SET nameColumnToInsert = CONCAT('prefix',nameColumnToInsert);
     * @param yourTable
     * @param nameColumnToCopy
     * @param nameColumnToInsert
     * @param prefix
     * @return
     */
    public static String addCopyColumnWithPrefix(String yourTable,String nameColumnToCopy,String nameColumnToInsert,String prefix){
        String query = "SELECT "+nameColumnToCopy+" FROM " + yourTable + "; \n" +
                "UPDATE " + yourTable + " SET "+nameColumnToInsert+" = "+nameColumnToCopy+"; \n"+
                "UPDATE " + yourTable + " SET "+nameColumnToInsert+" = CONCAT('"+prefix+"',"+nameColumnToInsert+");"
                ;
        return query;
    }

    /**
     * UPDATE yourTable  SET nameColumn = CONCAT('+prefix+',nameColumn);
     * @param yourTable
     * @param nameColumn
     * @param prefix
     * @return
     */
    public static String addPrefixToColumn(String yourTable,String nameColumn,String prefix){
        return "UPDATE " + yourTable + " SET "+nameColumn+" = CONCAT('"+prefix+"',"+nameColumn+");";
    }

    /**
     * SELECT nameColumn FROM yourTable WHERE nameColumn LIKE ='startWith% ;
     * @param yourTable
     * @param nameColumn
     * @param startWith
     * @return
     */
    public static String selecRecordWhereColumnStartWith(String yourTable,String nameColumn,String startWith){
        return "SELECT " + nameColumn +" FROM "+yourTable+" WHERE "+nameColumn+ " LIKE ='"+startWith+"% ;";
    }

    /**
     * UPDATE geodb.infodocument_coord_omogeneo_05052014
     * SET indirizzo = CONCAT_WS(', ', indirizzoNoCAP, indirizzoHasNumber)
     * @param nameColumnToUpdate
     * @param nameColumnConcatenate
     * @return
     */
    public static String etColumnLikeCOncatentationOfOtherColumns(String nameColumnToUpdate,String[] nameColumnConcatenate){
        return null;
    }
}
