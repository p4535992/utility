package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import org.jooq.SQLDialect;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 14/05/2015.
 * @author 4535992
 * @version 2015-09-30.
 */
@SuppressWarnings("unused")
public class SQLHelper {

    private static Connection conn;
    private static Statement stmt;
    private static String query;

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
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return  String.class;
            case Types.NUMERIC:
            case Types.DECIMAL:
                return  java.math.BigDecimal.class;
            case Types.BIT:
                return  Boolean.class;
            case Types.TINYINT:
                return  Byte.class;
            case Types.SMALLINT:
                return  Short.class;
            case Types.INTEGER:
                return  Integer.class;
            case Types.BIGINT:
                return  Long.class;
            case Types.REAL:
            case Types.FLOAT:
                return  Float.class;
            case Types.DOUBLE:
                return  Double.class;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return  Byte[].class;
            case Types.DATE:
                return  java.sql.Date.class;
            case Types.TIME:
                return  java.sql.Time.class;
            case Types.TIMESTAMP:
                return  java.sql.Timestamp.class;
            case Types.NULL:
                return  Object.class.getSuperclass();
            default:
                return Object.class;
        }
    }

    /**
     * Method for convert a java class to a SQLTypes.
     * @param aClass the correspondent java class.
     * @return the identificator for the SQL java types.
     */
    public static int convertClass2SQLTypes(Class<?> aClass) {
        if(aClass.getName().equals(String.class.getName()))return  Types.VARCHAR;
        else if(aClass.getName().equals(java.math.BigDecimal.class.getName()))return  Types.NUMERIC;
        else if(aClass.getName().equals(Boolean.class.getName()))return  Types.BIT;
        else if(aClass.getName().equals(int.class.getName()))return  Types.INTEGER;
        else if(aClass.getName().equals(Byte.class.getName()))return  Types.TINYINT;
        else if(aClass.getName().equals(Short.class.getName()))return  Types.SMALLINT;
        else if(aClass.getName().equals(Integer.class.getName()))return  Types.INTEGER;
        else if(aClass.getName().equals(Long.class.getName())) return  Types.BIGINT;
        else if(aClass.getName().equals(Float.class.getName()))return  Types.REAL;
        else if(aClass.getName().equals(Double.class.getName()))return  Types.DOUBLE;
        else if(aClass.getName().equals(Byte[].class.getName()))return  Types.VARBINARY;
        else if(aClass.getName().equals(java.sql.Date.class.getName())) return  Types.DATE;
        else if(aClass.getName().equals(java.sql.Time.class.getName()))return  Types.TIME;
        else if(aClass.getName().equals(java.sql.Timestamp.class.getName()))return  Types.TIMESTAMP;
        else if(aClass.getName().equals(java.net.URL.class.getName()))return  Types.VARCHAR;
        return Types.NULL;
    }

    /**
     * Method for convert a SQLTypes to a Stirng name of the types.
     * @param type SQL types.
     * @return the string name of the SQL types.
     */
    public static String convertSQLTypes2String(int type) {
        switch (type) {
            case Types.BIT: return "BIT";
            case Types.TINYINT: return "TINYINT";
            case Types.SMALLINT: return "SMALLINT";
            case Types.INTEGER: return "INTEGER";
            case Types.BIGINT: return "BIGINT";
            case Types.FLOAT: return "FLOAT";
            case Types.REAL:return "REAL";
            case Types.DOUBLE:return "DOUBLE";
            case Types.NUMERIC:return "NUMERIC";
            case Types.DECIMAL:return "DECIMAL";
            case Types.CHAR:return "CHAR";
            case Types.VARCHAR:return "VARCHAR";
            case Types.LONGVARCHAR:return "LONGVARCHAR";
            case Types.DATE:return "DATE";
            case Types.TIME: return "TIME";
            case Types.TIMESTAMP:return "TIMESTAMP";
            case Types.BINARY:return "BINARY";
            case Types.VARBINARY:return "VARBINARY";
            case Types.LONGVARBINARY:return "LONGVARBINARY";
            case Types.NULL:return "NULL";
            case Types.OTHER:return "OTHER";
            case Types.JAVA_OBJECT:return "JAVA_OBJECT";
            case Types.DISTINCT:return "DISTINCT";
            case Types.STRUCT:return "STRUCT";
            case Types.ARRAY:return "ARRAY";
            case Types.BLOB:return "BLOB";
            case Types.CLOB:return "CLOB";
            case Types.REF:return "REF";
            case Types.DATALINK:return "DATALINK";
            case Types.BOOLEAN:return "BOOLEAN";
            case Types.ROWID:return "ROWID";
            case Types.NCHAR:return "NCHAR";
            case Types.NVARCHAR:return "NVARCHAR";
            case Types.LONGNVARCHAR:return "LONGNVARCHAR";
            case Types.NCLOB:return "NCLOB";
            case Types.SQLXML:return "SQLXML";
            default: return "NULL";
        }
    }

    public static String convertDialectDatabaseToTypeNameId(String dialectDb){
        if(dialectDb.toLowerCase().contains("mysql"))return "mysql";
        SystemLog.warning("There is not database type for the specific database dialect used.");
        return "?";
    }

    public static SQLDialect convertDialectDBToSQLDialectJOOQ(String dialectDb){
        return convertStringToSQLDialectJOOQ(convertDialectDatabaseToTypeNameId(dialectDb));

    }

    public static SQLDialect convertStringToSQLDialectJOOQ(String sqlDialect) {
        switch (sqlDialect.toLowerCase()) {
            case "cubrid":return SQLDialect.CUBRID;
            case "derby": return SQLDialect.DERBY;
            case "firebird": return SQLDialect.FIREBIRD;
            case "h2": return SQLDialect.H2;
            case "hsqldb": return SQLDialect.HSQLDB;
            case "mariadb": return SQLDialect.MARIADB;
            case "mysql": return SQLDialect.MYSQL;
            case "postgres": return SQLDialect.POSTGRES;
            case "postgres93": return SQLDialect.POSTGRES_9_3;
            case "postgres94": return SQLDialect.POSTGRES_9_4;
            case "sqlite": return SQLDialect.SQLITE;
            default: return SQLDialect.DEFAULT;
        }
    }

    public static Connection chooseAndGetConnection(String dialectDB,
                                String host,String port,String database,String username,String password) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if(StringKit.isNullOrEmpty(username) || StringKit.isNullOrEmpty(password)){
            username = "root";
            password = "";
        }
        if(!StringKit.isNullOrEmpty(port) || !StringKit.isNumeric(port)) port = "";
        if(StringKit.isNullOrEmpty(dialectDB)) dialectDB = "mysql";

        if(convertDialectDatabaseToTypeNameId(dialectDB).equalsIgnoreCase("mysql")){
            return getMySqlConnection(host,port,database,username,password);
        }
        //other connection to implement.....
        SystemLog.warning("No connection database type detected fro this type.");
        return null;
    }

    /**
     * Method to get a HSQL connection.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     * @throws ClassNotFoundException if any error class is occurred.
     * @throws SQLException if any error SQL is occurred.
     */
    public static Connection getHSQLConnection(String host,String port,String database,String username,String password)
            throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        // The newInstance() call is a work around for some broken Java implementations
        Class.forName("org.hsqldb.jdbcDriver").newInstance();
        //String url = "jdbc:hsqldb:data/"+database;
        String url = "jdbc:hsqldb:hsql" + "://" + host;
        if(port != null && StringKit.isNumeric(port)){
            url +=  ":"+port;
        }
        url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
        return conn = DriverManager.getConnection(url, username, password);
    }

    /**
     * Method to get a MySQL connection.
     * @param host host where the server is.
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     * @throws ClassNotFoundException if any error class is occurred.
     * @throws SQLException if any error SQL is occurred.
     */
    public static Connection getMySqlConnection(
                    String host,String port,String database,String username,String password)
            throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance(); //load driver//"com.sql.jdbc.Driver"
        }catch(ClassNotFoundException e){
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
        }
        String url = "jdbc:mysql" + "://" + host;
        if(port != null && StringKit.isNumeric(port)){
            url +=  ":"+port;
        }
        url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
        try {
            conn = DriverManager.getConnection(url, username, password);
        }catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e){
            SystemLog.error("You forgot to turn on your MySQL Server!!!");
            SystemLog.abort(0);
        }
        return conn;
    }

    /**
     * Method to get a MySQL connection.
     * @param host string name of the host where is it the database
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     * @throws ClassNotFoundException if any error class is occurred.
     * @throws SQLException if any error SQL is occurred.
     * @throws IllegalAccessException if any error SQL is occurred.
     * @throws InstantiationException if any error SQL is occurred.
     */
    public static Connection getMySqlConnection(
            String host,String database,String username,String password)
            throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        return getMySqlConnection(host,null,database,username,password);
    }

    /**
     * Method to get a Oracle connection.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     * @throws ClassNotFoundException if any error class is occurred.
     * @throws SQLException if any error SQL is occurred.
     */
    public static Connection getOracleConnection(String host,String port,String database,String username,String password)
            throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        //String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
        String url = "jdbc:oracle:thin:@" + host;
        if(port != null && StringKit.isNumeric(port)){
            url +=  ":"+port;
        }
        url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
        return conn = DriverManager.getConnection(url, username, password);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Map<String,Integer> getColumns(String host,String database,String table,String column)
            throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        DatabaseMetaData metaData;
        if(conn!=null)metaData = conn.getMetaData();
        else metaData = chooseAndGetConnection(null,host,null,database,null,null).getMetaData();

        ResultSet result = metaData.getColumns( null, database, table, column );
        Map<String,Integer> map = new HashMap<>();
        while(result.next()){
            String columnName = result.getString(4);
            Integer columnType = result.getInt(5);
            map.put(columnName,columnType);
        }
        return map;
    }

    public static List<String> getTablesFromConnection(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            tableNames.add(rs.getString(3)); //column 3 is TABLE_NAME
        }
        return tableNames;
    }



    public static void openConnection(String url,String user,String pass) throws SQLException, ClassNotFoundException{
        //Class.forName("org.h2.Driver"); //Loading driver connection
        conn = DriverManager.getConnection(url, user, pass);
    }

    public static void closeConnection() throws SQLException{ conn.close();}

    public static Connection setConnection(String classDriverName,String dialectDB,
                                           String host,String port,String database,String user,String pass) throws ClassNotFoundException, SQLException {
        //"org.hsqldb.jdbcDriver","jdbc:hsqldb:data/tutorial"
        Class.forName(classDriverName); //load driver//"com.sql.jdbc.Driver"
        String url = ("" + dialectDB + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Got Connection.");
        return conn;
    }

    public static Connection getConnection(){
        return conn;
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


}
