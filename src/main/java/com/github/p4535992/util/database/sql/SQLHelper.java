package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.database.jooq.SQLJooqKit2;
import com.github.p4535992.util.database.sql.performance.ConnectionWrapper;
import com.github.p4535992.util.database.sql.performance.JDBCLogger;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringUtilities;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.opencsv.CSVReader;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 14/05/2015.
 * @author 4535992
 * @version 2015-11-10.
 */
@SuppressWarnings("unused")
public class SQLHelper {

    private static Connection conn;
    private static Statement stmt;
    private static String query;

    public static Connection getCurrentConnection() {
        return conn;
    }

    public static void setCurrentConnection(Connection conn) {
        SQLHelper.conn = conn;
    }

    /**
     * Method to get all JDBC type name of JAVA.
     * @return the Map of all JDBC type present in java.
     */
    public static Map<Integer, String> getAllJdbcTypeNames(){
        Map<Integer, String> result = new HashMap<>();
        for (Field field : Types.class.getFields()) {
            try {
                result.put((Integer)field.get(null), field.getName());
            } catch (IllegalAccessException e) {
                SystemLog.warning(e.getMessage(), e, SQLHelper.class);
            }
        }
        return result;
    }

    /**
     * Method for get a mapt with all SQL java types.
     * href: http://www.java2s.com/Code/Java/Database-SQL-JDBC/convertingajavasqlTypesintegervalueintoaprintablename.htm.
     * @param jdbcType code int of the type sql.
     * @return map of SQL Types with name
     */
    public static Map<Integer,String> convertIntToJdbcTypeName(int jdbcType) {
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
     * method to convert a String to a SQL Type.
     * @param value the String value to convert.
     * @return the SQL Type of the value.
     */
    public static int convertStringToSQLTypes(String value){
        if(value == null) return Types.NULL;
        if(StringUtilities.isFloat(value)) return Types.FLOAT;
        if(StringUtilities.isDouble(value)) return Types.DOUBLE;
        if(StringUtilities.isDecimal(value)) return Types.DECIMAL;
        if(StringUtilities.isInt(value)) return Types.INTEGER;
        if(StringUtilities.isURL(value)) return Types.VARCHAR;
        if(StringUtilities.isNumeric(value)) return Types.NUMERIC;
        else  return Types.VARCHAR;
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

    /**
     * Method to convert a DialectDatabase of JOOQ to a String name for a correct cast.
     * @param dialectDb the String to cast to a correct format.
     * @return the String with correct format.
     */
    public static String convertDialectDatabaseToTypeNameId(String dialectDb){
        if(dialectDb.toLowerCase().contains("mysql"))return "mysql";
        if(dialectDb.toLowerCase().contains("cubrid"))return "cubrid";
        if(dialectDb.toLowerCase().contains("derby"))return "derby";
        if(dialectDb.toLowerCase().contains("firebird"))return "firebird";
        if(dialectDb.toLowerCase().contains("h2"))return "h2";
        if(dialectDb.toLowerCase().contains("hsqldb"))return "hsqldb";
        if(dialectDb.toLowerCase().contains("hsql"))return "hsqldb";
        if(dialectDb.toLowerCase().contains("mariadb"))return "mariadb";
        if(dialectDb.toLowerCase().contains("postgres"))return "postgres";
        if(dialectDb.toLowerCase().contains("postgresql"))return "postgres";
        if(dialectDb.toLowerCase().contains("postgres93"))return "postgres93";
        if(dialectDb.toLowerCase().contains("postgres94"))return "postgres94";
        if(dialectDb.toLowerCase().contains("sqlite"))return "sqlite";
        SystemLog.warning("There is not database type for the specific database dialect used.");
        return "?";
    }


    public static XSDDatatype convertSQLTypesToXDDTypes(int type){
        switch (type) {
            case Types.BIT: return XSDDatatype.XSDbyte;
            case Types.TINYINT: return XSDDatatype.XSDint;
            case Types.SMALLINT: return XSDDatatype.XSDint;
            case Types.INTEGER: return XSDDatatype.XSDinteger;
            case Types.BIGINT: return XSDDatatype.XSDint;
            case Types.FLOAT: return XSDDatatype.XSDfloat;
            //case Types.REAL:return ;
            case Types.DOUBLE:return XSDDatatype.XSDdouble;
            case Types.NUMERIC:return XSDDatatype.XSDinteger;
            case Types.DECIMAL:return XSDDatatype.XSDdecimal;
            case Types.CHAR:return XSDDatatype.XSDstring;
            case Types.VARCHAR:return  XSDDatatype.XSDstring;
            case Types.LONGVARCHAR:return  XSDDatatype.XSDstring;
            case Types.DATE:return  XSDDatatype.XSDdate;
            case Types.TIME: return  XSDDatatype.XSDtime;
            case Types.TIMESTAMP:return  XSDDatatype.XSDdateTime;
            case Types.BINARY:return  XSDDatatype.XSDbase64Binary;
            case Types.VARBINARY:return XSDDatatype.XSDbase64Binary;
            case Types.LONGVARBINARY:return XSDDatatype.XSDbase64Binary;
            case Types.NULL:return XSDDatatype.XSDstring;
            //case Types.OTHER:return "";
            //case Types.JAVA_OBJECT:return "JAVA_OBJECT";
            //case Types.DISTINCT:return "DISTINCT";
            //case Types.STRUCT:return "STRUCT";
            //case Types.ARRAY:return "ARRAY";
            //case Types.BLOB:return "BLOB";
            //case Types.CLOB:return "CLOB";
            //case Types.REF:return "REF";
            //case Types.DATALINK:return "DATALINK";
            case Types.BOOLEAN: return XSDDatatype.XSDboolean;
            //case Types.ROWID:return "ROWID";
            case Types.NCHAR:return XSDDatatype.XSDstring;
            case Types.NVARCHAR:return XSDDatatype.XSDstring;
            case Types.LONGNVARCHAR:return XSDDatatype.XSDstring;
            //case Types.NCLOB:return "NCLOB";
            //case Types.SQLXML:return "SQLXML";
            default: return XSDDatatype.XSDstring;
        }
    }

    /**
     * Method to get a Connection from a List to possible choice.
     * @param dialectDB the String of the dialectDb.
     * @param host String name of the host where is the server.
     * @param port String number of the port where the server communicate.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection chooseAndGetConnection(String dialectDB,
                                String host,String port,String database,String username,String password){
        if(StringUtilities.isNullOrEmpty(username) || StringUtilities.isNullOrEmpty(password)){
            username = "root";
            password = "";
        }
        if(!StringUtilities.isNullOrEmpty(port) || !StringUtilities.isNumeric(port)) port = "";
        if(StringUtilities.isNullOrEmpty(dialectDB)){
            SystemLog.warning("No connection database type detected fro this type.");
            return null;
        }else dialectDB = convertDialectDatabaseToTypeNameId(dialectDB);
        switch (dialectDB) {
            case "cubrid": return null;
            case "derby": return null;
            case "firebird": return null;
            case "h2": return null;
            case "hsqldb": return null;
            case "mariadb": return null;
            case "mysql": return getMySqlConnection(host,port,database,username,password);
            case "postgres": return null;
            case "postgres93": return null;
            case "postgres94": return null;
            case "sqlite": return null;
            default: {SystemLog.warning("No connection database type detected fro this type."); return null;}
        }
    }

    /**
     * Method to get a HSQL connection.
     * @param host String name of the host where is the server.
     * @param port String number of the port where the server communicate.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection getHSQLConnection(String host,String port,String database,String username,String password) {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            String url = "jdbc:hsqldb:hsql://" + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port; //jdbc:hsqldb:data/database
            }
            url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
            conn = DriverManager.getConnection(url, username, password);
        }catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            SystemLog.error("The Class.forName is not present on the classpath of the project", e, SQLHelper.class);
        } catch (SQLException e) {
        SystemLog.error("The URL is not correct", e, SQLHelper.class);
        }
        return conn;
    }

    /**
     * Method to get a MySQL connection.
     * @param host host where the server is.
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection getMySqlConnection(
                    String host,String port,String database,String username,String password) {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance(); //load driver//"com.sql.jdbc.Driver"
            } catch (ClassNotFoundException e) {
                Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            }
            String url = "jdbc:mysql://" + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/"  + database + "?noDatetimeStringSync=true"; //"jdbc:sql://localhost:3306/jdbctest"
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                conn = DriverManager.getConnection(url, username, password);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                SystemLog.error("You forgot to turn on your MySQL Server!!!");
                SystemLog.abort(0);
            } catch (SQLException e) {
                SystemLog.error("The URL is not correct", e, SQLHelper.class);
            }
        }catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
            SystemLog.error("The Class.forName is not present on the classpath of the project", e1, SQLHelper.class);
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
     */
    public static Connection getMySqlConnection(
            String host,String database,String username,String password) {
        return getMySqlConnection(host,null,database,username,password);
    }

    /**
     * Method to get a MySQL connection.
     * @param hostAndDatabase string name of the host where is it the database
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection getMySqlConnection( String hostAndDatabase,String username,String password) {
        String[] split = hostAndDatabase.split("/");
        //localhost:3306/geodb
        if(hostAndDatabase.startsWith("/")) hostAndDatabase = split[1];
        else hostAndDatabase = split[0];
        return getMySqlConnection(hostAndDatabase,null,split[split.length-1],username,password);
    }

    public static Connection getMySqlConnection(String fullUrl) {
        //jdbc:mysql://localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
        //localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
        /*if(fullUrl.toLowerCase().contains("jdbc:mysql://")) fullUrl = fullUrl.replace("jdbc:mysql://","");
        String[] split = fullUrl.split("\\?");
        String hostAndDatabase = split[0];//localhost:3306/geodb
        Pattern pat = Pattern.compile("(\\&|\\?)?(user|username)(\\=)(.*?)(\\&|\\?)?", Pattern.CASE_INSENSITIVE);
        String username = StringUtilities.findWithRegex(fullUrl, pat);
        if(Objects.equals(username, "?")) username = "root";
        pat = Pattern.compile("(\\&|\\?)?(pass|password)(\\=)(.*?)(\\&|\\?)?", Pattern.CASE_INSENSITIVE);
        String password = StringUtilities.findWithRegex(fullUrl, pat);
        if(Objects.equals(password, "?")) password ="";
        split = hostAndDatabase.split("/");
        String database = split[split.length-1];
        hostAndDatabase = hostAndDatabase.replace(database,"");
        pat = Pattern.compile("([0-9])+", Pattern.CASE_INSENSITIVE);
        String port = StringUtilities.findWithRegex(hostAndDatabase, pat);
        if(Objects.equals(port, "?")) port = null;
        else  hostAndDatabase = hostAndDatabase.replace(port, "").replace(":","").replace("/","");
        return getMySqlConnection(hostAndDatabase,port,database,username,password);*/
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance(); //load driver//"com.sql.jdbc.Driver"
            } catch (ClassNotFoundException e) {
                Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            }
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                conn = DriverManager.getConnection(fullUrl);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                SystemLog.error("You forgot to turn on your MySQL Server!!!");
                SystemLog.abort(0);
            } catch (SQLException e) {
                SystemLog.error("The URL is not correct", e, SQLHelper.class);
            }
        }catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
            SystemLog.error("The Class.forName is not present on the classpath of the project", e1, SQLHelper.class);
        }
        return conn;
    }

    /**
     * Method to get a Oracle connection.
     * @param host string name of the host where is it the database
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection getOracleConnection(String host,String port,String database,String username,String password){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
            String url = "jdbc:oracle:thin:@" + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
            conn = DriverManager.getConnection(url, username, password);
        }catch (ClassNotFoundException e) {
            SystemLog.error("The Class.forName is not present on the classpath of the project", e, SQLHelper.class);
        } catch (SQLException e) {
            SystemLog.error("The URL is not correct", e, SQLHelper.class);
        }
        return conn;
    }


    /**
     * Method to connect to a h2  database.
     * href: http://www.h2database.com/html/features.html.
     * @param host string name of the host where is it the database.
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the Connection to the H2 database.
     */
    public static Connection getH2RemoteConnection(String host,String port,String database,String username,String password)
    {
        try {
            Class.forName("org.h2.Driver"); //Loading driver connection
            /*
            jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName>
            jdbc:h2:tcp://localhost/~/test
            jdbc:h2:tcp://dbserv:8084/~/sample
            jdbc:h2:tcp://localhost/mem:test
            */
            String url = "jdbc:h2:tcp://" + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/~/" + database;
            conn = DriverManager.getConnection(url, username, password);
        }catch (ClassNotFoundException e) {
            SystemLog.error("The Class.forName is not present on the classpath of the project", e, SQLHelper.class);
        } catch (SQLException e) {
            SystemLog.error("The URL is not correct", e, SQLHelper.class);
        }
        return conn;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to get the columns from a specific Table.
     * @param host string name of the host where is it the database.
     * @param database string name of the database.
     * @param table the String name of the Table.
     * @param columnNamePattern the String Pattern name of the columnss to get.
     * @return the Map of all columns.
     * @throws SQLException throw if any erro with the SQL is occurred.
     */
    public static Map<String,Integer> getColumns(
            String host,String database,String table,String columnNamePattern) throws SQLException{
        Map<String,Integer> map = new HashMap<>();
        DatabaseMetaData metaData;
        if(conn!=null)metaData = conn.getMetaData();
        else {
            conn = chooseAndGetConnection(null,host,null,database,null,null);
            if(conn!=null)metaData = conn.getMetaData();
            else{
                SystemLog.warning("Can't get the connection for the database",
                        new Throwable("Can't get the connection for the database"),SQLHelper.class);
                return map;
            }
        }
        ResultSet result = metaData.getColumns( null, database, table, columnNamePattern );
        while(result.next()){
            String columnName = result.getString(4);
            Integer columnType = result.getInt(5);
            String type = convertSQLTypes2String(columnType);
            map.put(columnName,columnType);
        }
        return map;
    }

    public static Map<String,Integer> getColumns(Connection conn,String tablename) throws SQLException {
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        //stmt.setFetchSize(DATABASE_TABLE_FETCH_SIZE);
        String query = "Select * FROM " + tablename;
        ResultSet r = stmt.executeQuery(query);
        ResultSetMetaData meta = r.getMetaData();
        // Get the column names
        Map<String,Integer> map = new HashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            map.put(meta.getColumnName(i), meta.getColumnType(i));
        }
        r.close();
        stmt.close();
        return map;
    }

    /**
     * Method to get the List of all Tables on a Specific connection.
     * @param connection the SQL Connection.
     * @return the List of Name of the Tables.
     * @throws SQLException throw if any error with the SQL is occurred.
     */
    public static List<String> getTablesFromConnection(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            tableNames.add(rs.getString(3)); //column 3 is TABLE_NAME
        }
        return tableNames;
    }

    /**
     * Method to close the actual connectiom.
     * @throws SQLException throw if any error with the SQL is occurred.
     */
    public static void closeConnection() throws SQLException{ conn.close();}

    /**
     * Method to set a New Connection.
     * @param classDriverName hte class package driver to use e.g. com.sql.jdbc.Driver.
     * @param dialectDB the dialect of the database e.g. jdbc:sql.
     * @param host the host where is allocate the database.
     * @param port the port to communicate with the database.
     * @param database the String name of the database.
     * @param user the String of the username.
     * @param pass the String of the password.
     * @return the new SQL Connection
     * @throws SQLException throw if any error with the SQL is occurred.
     * @throws ClassNotFoundException throw if any error with the Class Driver is occurred.
     */
    public static Connection setNewConnection(
            String classDriverName,String dialectDB,String host,String port,
            String database,String user,String pass)throws SQLException, ClassNotFoundException {
        //"org.hsqldb.jdbcDriver","jdbc:hsqldb:data/tutorial"
        Class.forName(classDriverName); //load driver//"com.sql.jdbc.Driver"
        String url = (dialectDB  + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        conn = DriverManager.getConnection(url, user, pass);
        SystemLog.message("SQLHelper::SetNewConnection -> Set a new Connection.", SQLHelper.class);
        return conn;
    }

    /**
     * Method to set the Connection to a specific database.
     * @param connection  the Connection to set.
     */
    public static void setConnection(Connection connection){
        SQLHelper.conn = connection;
    }

    /**
     * Method to execute a query SQL.
     * @param sql the Stirng query SQL.
     * @return the ResukltSet of the Query SQL.
     * @throws SQLException thorw if any error is occurred during the execution of the query SQL.
     */
    public static ResultSet executeSQL(String sql) throws SQLException {
        stmt = conn.createStatement();
        return stmt.executeQuery(query);
        //stmt.executeUpdate(sql);
    }

    /**
     * Method to execute a query SQL.
     * @param sql the Stirng query SQL.
     * @param conn the Connection to the Database where execute the query.
     * @return the ResukltSet of the Query SQL.
     * @throws SQLException thorw if any error is occurred during the execution of the query SQL.
     */
    public static ResultSet executeSQL(String sql,Connection conn) throws SQLException  {
        // create the java statement
        stmt = conn.createStatement();
        // execute the query, and get a java resultset
        return stmt.executeQuery(query);
        //stmt.executeUpdate(sql);
    }

    /**
     * Method to print hte result of a query.
     * @param sql the String SQL.
     */
    public static void checkData(String sql){
        ResultSet rs ;
        try {
            rs = stmt.executeQuery(sql);

            java.sql.ResultSetMetaData metadata = rs.getMetaData();
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                SystemLog.messageInline("\t" + metadata.getColumnLabel(i + 1));
            }
            SystemLog.messageInline("\n----------------------------------");
            while (rs.next()) {
                for (int i = 0; i < metadata.getColumnCount(); i++) {
                    Object value = rs.getObject(i + 1);
                    if (value == null) {
                        SystemLog.messageInline("\t       ");
                    } else {
                        SystemLog.messageInline("\t" + value.toString().trim());
                    }
                }
                SystemLog.message("");
            }
        } catch (SQLException e) {
            SystemLog.exception("SQLHelper::checkData",e,SQLHelper.class);
        }
    }

    /**
     * Method to get the type of the Database used for the current connection
     * "MySQL","PostgreSQL","H2","Oracle","HSQL Database Engine".
     * @return the Name type of the database.
     */
    public static String getNameTypeDatabase(){
        DatabaseMetaData m;
        try {
            m = conn.getMetaData();
            return m.getDatabaseProductName();
        } catch (SQLException e) {
            SystemLog.exception(e);
            return null;
        }
    }

    /**
     * Method to get a content of a table.
     * @param tablename the String name of the table.
     * @param connection the Connection to the Database.
     * @return the Vector String Arrays of the content.
     * @throws SQLException throw if the name if the table not exists on the Database.
     */
    public static Vector<String[]> getContentOfATable(String tablename, Connection connection)
            throws SQLException {
        String sqlQuery = "SELECT * FROM " + tablename;
        Statement statement = connection.createStatement(  );
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        int numColumns = resultSet.getMetaData(  ).getColumnCount(  );
        String[  ] aRow;
        Vector<String[]> allRows = new Vector<>();
        while(resultSet.next(  )) {
            aRow = new String[numColumns];
            for (int i = 0; i < numColumns; i++)
                //ResultSet access is 1-based, arrays are 0-based
                aRow[i] = resultSet.getString(i+1);
            allRows.addElement(aRow);
        }
        return allRows;
    }

    /**
     * Method to get the execution time of a SQL query with the Java API.
     * @param sql the String SQL Query.
     * @return the Long value of the time for execute the query.
     */
    public static Long getExcecutionTime(String sql){
        long startTime = 0,endTime = 0;
        try {
            stmt = conn.createStatement();
            startTime = System.currentTimeMillis();
            ResultSet rs = stmt.executeQuery(sql);
            endTime   = System.currentTimeMillis();
        }catch (SQLException e) {
            SystemLog.exception(e,SQLHelper.class);
        }
        return endTime - startTime;
    }

    /**
     * Method to get the execution time of a SQL query with the Java API.
     * @param sql the String SQL Query.
     * @param conn the Connection to the Database where execute the query.
     * @return the Long value of the time for execute the query.
     */
    public static Long getExcecutionTime(String sql,Connection conn){
        long startTime = 0,endTime = 0;
        //Connection dbConnection = getConnectionFromDriver(  );
        ConnectionWrapper dbConnection = new ConnectionWrapper(conn);
        try {
            Statement statement = dbConnection.createStatement();
            startTime = System.currentTimeMillis();
            ResultSet rs = statement.executeQuery(sql);
            endTime   = System.currentTimeMillis();
        }catch (SQLException e) {
            SystemLog.exception(e,SQLHelper.class);
        }
        Long calculate = endTime - startTime;
        Long calculate2 = JDBCLogger.getTime();
        if(calculate < calculate2) return calculate;
        else return calculate2;
    }

    /**
     * Method to import a file CSV to a Database.
     * @param fileCSV the File CSv to import.
     * @param firstLine if true the firstline of the file CSV contains the headers of the fields.
     * @param separator the Cgaracter of the separator field on the CSV file.
     * @param nameTable the String name of the table.
     * @param connection the Connection to the Database where execute the query.
     */
    public static void importCsvInsertInto(File fileCSV, boolean firstLine,Character separator,
                                       String nameTable,Connection connection) {
        String[] columns = FileUtilities.CSVGetHeaders(fileCSV, firstLine);
        SQLJooqKit2.setConnection(connection);
        try {
            CSVReader reader = new CSVReader(new FileReader(fileCSV), separator);
            //String insertQuery =
            String insertQuery;
            //PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            String[] rowData;
            while((rowData = reader.readNext()) != null){
                if (!firstLine) {
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
        } catch (SQLException |IOException e) {
            SystemLog.exception(e,SQLHelper.class);
        } 
    }

    /**
     * Method to get the String name of the host by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static String getHostFromUrl(String url) {
        String regexForHostAndPort = "[.\\w]+:\\d+";
        Pattern hostAndPortPattern = Pattern.compile(regexForHostAndPort);
        Matcher matcher = hostAndPortPattern.matcher(url);
        matcher.find();
        int start = matcher.start();
        int end = matcher.end();
        if(start >= 0 && end >= 0) {
            String hostAndPort = url.substring(start, end);
            String [] array = hostAndPort.split(":");
            if(array.length >= 2)
                return array[0];
        }
        throw new IllegalArgumentException("couldn't find pattern '" + regexForHostAndPort + "' in '" + url + "'");
    }

    /**
     * Method to get the Integer value of the port by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static Integer getPortFromUrl(String url) {
        String regexForHostAndPort = "[.\\w]+:\\d+";
        Pattern hostAndPortPattern = Pattern.compile(regexForHostAndPort);
        Matcher matcher = hostAndPortPattern.matcher(url);
        matcher.find();
        int start = matcher.start();
        int end = matcher.end();
        if(start >= 0 && end >= 0) {
            String hostAndPort = url.substring(start, end);
            String [] array = hostAndPort.split(":");
            if(array.length >= 2)
                return Integer.parseInt(array[1]);
        }
        throw new IllegalArgumentException("couldn't find pattern '" + regexForHostAndPort + "' in '" + url + "'");
    }

    /**
     * Method to get the String value of the username by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static String getUsernameFromUrl(String url){
        Pattern pat = Pattern.compile("(\\&|\\?|\\=|\\/)?(user|username)(\\=)(.*?)(\\&|\\?|\\=|\\/|\\s)+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(url  + " ");
        if(matcher.find())  {
            String[] find = (matcher.group(0)).split("=");
            return find[1].substring(0,find[1].length()-1);
        }
        throw new IllegalArgumentException("couldn't find pattern '" + pat.toString() + "' in '" + url + "'");
    }

    /**
     * Method to get the String value of the password by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static String getPasswordFromUrl(String url){
        Pattern pat = Pattern.compile("(\\&|\\?|\\=|\\/)?(pass|password)(\\=)(.*?)(\\&|\\?|\\=|\\/|\\s)+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(url + " ");
        if(matcher.find())  {
            String[] find = (matcher.group(0)).split("=");
            return find[1].substring(0,find[1].length()-1);
        }
        throw new IllegalArgumentException("couldn't find pattern '" + pat.toString() + "' in '" + url + "'");
    }








}
