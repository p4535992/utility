package com.github.p4535992.util.sql;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.log.SystemLog;

import java.sql.*;
import java.util.*;

/**
 * Converts database types to Java class types.
 */
public class  SQLKit<T> {
    private static Connection connection;
    private static Statement stmt;

    private static Class<?> cl;
    private static String clName;
    private static String query;

    @SuppressWarnings("unchecked")
    public SQLKit() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        SQLKit.cl = (Class) pt.getActualTypeArguments()[0];
        SQLKit.clName = cl.getSimpleName();
    }

    public static Map<String,Integer> getColumns(String database,String table,String column) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet result = metaData.getColumns( null, database, table, column );
        Map<String,Integer> map = new HashMap<>();
        while(result.next()){
            String columnName = result.getString(4);
            Integer columnType = result.getInt(5);
            map.put(columnName,columnType);
        }
        return map;
    }

    public static void openConnection(String url,String user,String pass) throws SQLException, ClassNotFoundException{
        //Class.forName("org.h2.Driver"); //Loading driver connection
        connection = DriverManager.getConnection(url, user, pass);
    }

    public static void closeConnection() throws SQLException{ connection.close();}

    public static Connection setConnection(String classDriverName,String dialectDB,
                  String host,String port,String database,String user,String pass) throws ClassNotFoundException, SQLException {
       //"org.hsqldb.jdbcDriver","jdbc:hsqldb:data/tutorial"
        Class.forName(classDriverName); //load driver//"com.sql.jdbc.Driver"
        String url = ("" + dialectDB + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        Connection conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Got Connection.");
        connection = conn;
        return conn;
    }

    public static Connection getConnection(){
        return connection;
    }

    public static void executeSQLCommand(String sql) throws Exception {
        stmt.executeUpdate(sql);
    }

    public static void checkData(String sql) throws Exception {
        java.sql.ResultSet rs = stmt.executeQuery(sql);
        java.sql.ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 0; i < metadata.getColumnCount(); i++) {
            System.out.print("\t"+ metadata.getColumnLabel(i + 1));
        }
        System.out.println("\n----------------------------------");

        while (rs.next()) {
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                Object value = rs.getObject(i + 1);
                if (value == null) {
                    System.out.print("\t       ");
                } else {
                    System.out.print("\t"+value.toString().trim());
                }
            }
            System.out.println("");
        }
    }

    public static String prepareInsertIntoQuery(String nameOfTable,String[] columns,Object[] values){
        try {
            boolean statement = false;
            query = "INSERT INTO " +  nameOfTable + "  (";
            for (int i = 0; i < columns.length; i++) {
                query += columns[i];
                if (i < columns.length - 1) {
                    query += ",";
                }
            }
            query += " ) VALUES ( ";
            if (values == null) {
                statement = true;
            }
            for (int i = 0; i < columns.length; i++) {
                if (statement) {
                    query += "?";
                } else {
                    if(values[i]== null){
                        values[i]= " NULL ";
                    }else if (values[i]!=null && values[i] instanceof String) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else if (values[i]!=null && values[i] instanceof java.net.URL) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else{
                        values[i] = " " + values[i] + " ";
                    }
                    query += "" + values[i] + "";
                }
                if (i < columns.length - 1) {
                    query += ",";
                }
            }
            query += ");";
        }catch (NullPointerException e){
            SystemLog.warning("Attention: you problably have forgotten  to put some column for the SQL query");
            SystemLog.exception(e);
        }
        return query;
    }


    public static String prepareSelectQuery(String nameOfTable,String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
        boolean statement = false;
        //PREPARE THE QUERY STRING
        query = "SELECT ";
        if(columns.length==0 || (columns.length==1 && columns[0]=="*")){
            query += " * ";
        }else{
            for(int i = 0; i < columns.length; i++){
                query += " "+columns[i]+"";
                if(i < columns.length-1){
                    query += ", ";
                }
            }
        }
        query +=" FROM "+ nameOfTable +" ";
        if(!CollectionKit.isArrayEmpty(columns_where)) {
            if(values_where==null){
                statement = true;
                //values_where = new Object[columns_where.length];
                //for(int i = 0; i < columns_where.length; i++){values_where[i]="?";}
            }
            query += " WHERE ";
            for (int k = 0; k < columns_where.length; k++) {
                query += columns_where[k] + " ";
                if(statement){
                    query += " = ? ";
                }else {
                    if (values_where[k] == null) {
                        query += " IS NULL ";
                    } else {
                        query += " = '" + values_where[k] + "'";
                    }
                }
                if (condition != null && k < columns_where.length - 1) {
                    query += " " + condition.toUpperCase() + " ";
                } else {
                    query += " ";
                }
            }
        }
        if(limit != null && offset!= null) {
            query += " LIMIT " + limit + " OFFSET " + offset + "";
        }
        return query;
    }


//    public String getJavaType( String schema, String object, String column )throws Exception {
//        Connection con = null;
//        String fullName = schema + '.' + object + '.' + column;
//        String javaType = null;
//        if(columnMeta.first() ) {
//          int dataType = columnMeta.getInt( "DATA_TYPE" );
//          javaType = SQLTypeMap.convert( dataType );
//        }
//        else {
//          throw new Exception( "Unknown database column " + fullName + '.' );
//        }
//
//    return javaType;
//  }






}

