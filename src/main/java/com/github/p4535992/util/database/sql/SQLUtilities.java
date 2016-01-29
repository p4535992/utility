package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.collection.ListUtilities;
import com.github.p4535992.util.collection.MapUtilities;
import com.github.p4535992.util.database.jooq.JOOQUtilities;
import com.github.p4535992.util.database.sql.datasource.DatabaseContextFactory;
import com.github.p4535992.util.database.sql.datasource.LocalContext;
import com.github.p4535992.util.database.sql.datasource.LocalContextFactory;
import com.github.p4535992.util.database.sql.performance.ConnectionWrapper;
import com.github.p4535992.util.database.sql.performance.JDBCLogger;
import com.github.p4535992.util.database.sql.runScript.ScriptRunner;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.github.p4535992.util.string.*;

import com.opencsv.CSVReader;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 14/05/2015.
 *
 * jdbcURL #
 * JDBC connection URL for the database. Refer to your JDBC driver documentation for the format for your database engine. Examples:
 * MySQL: jdbc:mysql://servername/databasename
 * PostgreSQL: jdbc:postgresql://servername/databasename
 * Oracle: jdbc:oracle:thin:@servername:1521:databasename
 * HSQLDB: jdbc:hsqldb:mem:databasename (in-memory database)
 * Microsoft SQL Server: jdbc:sqlserver://servername;databaseName=databasename
 * (due to the semicolon, the URL must be put in quotes when passed as a command-line argument in Linux/Unix shells)
 *
 * href: https://devcenter.heroku.com/articles/database-connection-pooling-with-java
 *
 * href: https://www.safaribooksonline.com/library/view/java-enterprise-best/0596003846/ch04.html (very useful)
 * href: http://penguindreams.org/blog/running-beans-that-use-application-server-datasources-locally/ (very useful)
 * href: http://www.codeproject.com/Articles/802383/Run-SQL-Script-sql-containing-DDL-DML-SELECT-state
 * @author 4535992
 * @version 2015-11-10.
 */
@SuppressWarnings("unused")
public class SQLUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SQLUtilities.class);

    private static DataSource dataSource;
    private static Connection conn;
    private static Statement stmt;

    //private static final Pattern NEW_DELIMITER_PATTERN = Pattern.compile("(?:--|\\/\\/|\\#)?!DELIMITER=(.+)");
    //private static final Pattern COMMENT_PATTERN = Pattern.compile("^(?:--|\\/\\/|\\#).+");

    private static SQLUtilities instance = null;

    protected SQLUtilities() {}

    public static SQLUtilities getInstance(){
        if(instance == null) {
            instance = new SQLUtilities();
        }
        return instance;
    }

    public static Connection getCurrentConnection() {
        return conn;
    }

    public static void setCurrentConnection(Connection conn) {
        SQLUtilities.conn = conn;
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
                logger.warn(e.getMessage(), e);
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
                logger.warn(e.getMessage(),e);
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
        logger.warn("There is not database type for the specific database dialect used:"+dialectDb);
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
            logger.warn("No connection database type detected fro this type;"+dialectDB);
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
            default: {logger.warn("No connection database type detected fro this type:"+dialectDB); return null;}
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
    public static Connection getHSQLDBConnection(String host,String port,String database,String username,String password) {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            invokeClassDriverForDbType(DBType.HSQLDB);
            String url = DBConnector.HSQLDB.getConnector() + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port; //jdbc:hsqldb:data/database
            }
            url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
            conn = DriverManager.getConnection(url, username, password);
        }catch (InstantiationException e) {
            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
        }catch(IllegalAccessException e){
            logger.error("Access problem while loading!:"+e.getMessage(),e);
        } catch(ClassNotFoundException e){
            logger.error("Unable to load driver class!:"+e.getMessage(),e);
        }catch (SQLException e) {
            logger.error("The URL is not correct:" + e.getMessage(), e);
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
            invokeClassDriverForDbType(DBType.MYSQL);
            String url = DBConnector.MYSQL.getConnector() + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/"  + database + "?noDatetimeStringSync=true"; //"jdbc:sql://localhost:3306/jdbctest"
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                conn = DriverManager.getConnection(url, username, password);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
            } catch (SQLException e) {
                logger.error("The URL is not correct:" + e.getMessage(), e);
            }
        }catch (InstantiationException e) {
            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
        }catch(IllegalAccessException e){
            logger.error("Access problem while loading!:"+e.getMessage(),e);
        } catch(ClassNotFoundException e){
            logger.error("Unable to load driver class!:"+e.getMessage(),e);
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
        if(hostAndDatabase.startsWith("/")) hostAndDatabase = split[1];
        else hostAndDatabase = split[0];
        return getMySqlConnection(hostAndDatabase,null,split[split.length-1],username,password);
    }

    public static Connection getMySqlConnection(String fullUrl) {
        //e.g. "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility"
        try {
            invokeClassDriverForDbType(DBType.MYSQL);
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                conn = DriverManager.getConnection(fullUrl);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
            } catch (SQLException e) {
                logger.error("The URL is not correct" + e.getMessage(), e);
            }
        }catch (InstantiationException e) {
            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
        }catch(IllegalAccessException e){
            logger.error("Access problem while loading!:"+e.getMessage(),e);
        } catch(ClassNotFoundException e){
            logger.error("Unable to load driver class!:"+e.getMessage(),e);
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
            invokeClassDriverForDbType(DBType.ORACLE);
            //String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
            String url = DBConnector.ORACLE.getConnector() + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
            conn = DriverManager.getConnection(url, username, password);
        } catch(ClassNotFoundException|IllegalAccessException|InstantiationException e){
            logger.error("Unable to load driver class!:"+e.getMessage(),e);
        } catch (SQLException e) {
            logger.error("The URL is not correct:" + e.getMessage(), e);
        }
        return conn;
    }

   /* private static Connection getMySqlConnection2(String fullUrl){
        //jdbc:mysql://localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
        //localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
        if(fullUrl.toLowerCase().contains("jdbc:mysql://")) fullUrl = fullUrl.replace("jdbc:mysql://","");
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
        return getMySqlConnection(hostAndDatabase,port,database,username,password);
    }*/

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
    public static Connection getH2RemoteConnection(
            String host,String port,String database,String username,String password) {
        try {
            invokeClassDriverForDbType(DBType.H2);
            /*
            jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName>
            jdbc:h2:tcp://localhost/~/test
            jdbc:h2:tcp://dbserv:8084/~/sample
            jdbc:h2:tcp://localhost/mem:test
            */
            String url = DBConnector.H2.getConnector() + host;
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/~/" + database;
            conn = DriverManager.getConnection(url, username, password);
        }catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {
            logger.error("Unable to load driver class!:" + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("The URL is not correct:" + e.getMessage(), e);
        }
        return conn;
    }

    /**
     * Method to get the columns from a specific Table.
     * @param host string name of the host where is it the database.
     * @param database string name of the database.
     * @param table the String name of the Table.
     * @param columnNamePattern the String Pattern name of the columnss to get.
     * @return the Map of all columns.
     * @throws SQLException throw if any error with the SQL is occurred.
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
                logger.warn("Can't get the connection for the database");
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Map<String,Integer> getColumns(Connection conn,String tablename) throws SQLException {
        conn.setAutoCommit(false);
        Map<String,Integer> map;
        //stmt.setFetchSize(DATABASE_TABLE_FETCH_SIZE);
        try (Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {
            //stmt.setFetchSize(DATABASE_TABLE_FETCH_SIZE);
            try (ResultSet r = statement.executeQuery("Select * FROM " + tablename)) {
                ResultSetMetaData meta = r.getMetaData();
                // Get the column names
                map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    map.put(meta.getColumnName(i), meta.getColumnType(i));
                }
            }
        }
        return map;
    }

    /**
     * Method to get the List of all Tables on a Specific connection.
     * href:http://www.javaroots.com/2013/09/print-tables-details-in-schema-jdbc.html.
     * @param connection the SQL Connection.
     * @param schemaName the String anme of the schema.
     * @return the List of Name of the Tables.
     * @throws SQLException throw if any error with the SQL is occurred.
     */
    public static List<String> getTablesFromConnection(Connection connection,String schemaName) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData md = connection.getMetaData();
        String SCHEMA_NAME="${"+schemaName+"}";
        //ResultSet rs = md.getTables(null, null, "%", null);
        ResultSet rs = md.getTables(null, null, "%", new String[]{"TABLE_TYPES"});
        while (rs.next()) {
            //tableNames.add(rs.getString(3)); //column 3 is TABLE_NAME
            /*String tableName = resultSet.getString(3);
            String tableCatalog = resultSet.getString(1);
            String tableSchema = resultSet.getString(2);*/
            tableNames.add(rs.getString("TABLE_NAME"));
            //tableNames.add(rs.getString("TABLE_SCHEMA"));
        }
        return tableNames;
    }

    public static Map<String,String[]> getTableAndColumn(Connection connection,String schemaName) throws SQLException {
        Map<String,String[]> tableNames = new HashMap<>();
        DatabaseMetaData md = connection.getMetaData();
        String SCHEMA_NAME="${"+schemaName+"}";
        String[] types = { "TABLE" };
        //ResultSet rs = md.getTables(null, null, "%", null);
        ResultSet rs = md.getTables(null,null,null, types);
        //rs = md.getTables(null, SCHEMA_NAME, "%", new String[]{"TABLE_TYPES"});
        while(rs.next()) {
            String tableName = rs.getString(3);
            ResultSet columns = md.getColumns(null,null,tableName,null);
            List<String> array = new ArrayList<>();
            while(columns.next()) {
                String columnName = columns.getString(4);
                array.add(columnName);
            }
            tableNames.put(tableName, ListUtilities.toArray(array));
            array.clear();
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
        logger.info("Set a new Connection.");
        return conn;
    }

    /**
     * Method to set the Connection to a specific database.
     * @param connection  the Connection to set.
     */
    public static void setConnection(Connection connection){
        SQLUtilities.conn = connection;
    }

    /**
     * Method to execute a query SQL.
     * @param sql the String query SQL.
     * @return the ResultSet of the Query SQL.
     * @throws SQLException throw if any error is occurred during the execution of the query SQL.
     */
    public static ResultSet executeSQL(String sql) throws SQLException {
       return executeSQL(sql,conn,null);
    }

    /**
     * Method to execute a query SQL.
     * @param sql the String query SQL.
     * @param conn the {@link Connection} SQL to the database.
     * @return the ResultSet of the Query SQL.
     * @throws SQLException throw if any error is occurred during the execution of the query SQL.
     */
    public static ResultSet executeSQL(String sql,Connection conn) throws SQLException {
        return executeSQL(sql,conn,null);
    }

    /**
     * Method to execute a query SQL.
     * @param sql the String query SQL.
     * @param conn the Connection to the Database where execute the query.
     * @param stmt the {@link Statement} SQL.
     * @return the ResultSet of the Query SQL.
     * @throws SQLException throw if any error is occurred during the execution of the query SQL.
     */
    public static ResultSet executeSQL(String sql,Connection conn,Statement stmt) throws SQLException  {
        //if the String query is a apth to a File batch
        if(sql.contains(FileUtilities.pathSeparatorReference)){ //Reference path
             if(FileUtilities.isFileValid(sql)){
                 executeSQL(new File(sql),conn);
                 logger.info("Exeute the File SQL:"+new File(sql).getAbsolutePath());
                 return null;
             }
        }
        // create the java statement
        if(stmt == null) {
            stmt = conn.createStatement();
            SQLUtilities.stmt = stmt;
        }
        //if are multiple statements
        if(sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
        if(sql.split(";").length > 1){
            for(String singleQuery : sql.split(";")){
                if(singleQuery.toLowerCase().startsWith("select")){
                    logger.warn("You execute a SELECT query:"+singleQuery+" with the 'executeBatch' ," +
                            " in this case we can't return a resultSet");
                }
                stmt.addBatch(singleQuery+";");
            }
            try {
                stmt.executeBatch();
            }catch (BatchUpdateException ex) {
                int[] updateCount = ex.getUpdateCounts();
                int count = 1;
                for (int i : updateCount) {
                    if (i == Statement.EXECUTE_FAILED) {
                        logger.error("Error on request " + count + ": Execute failed");
                    } else {
                        logger.warn("Request " + count + ": OK");
                    }
                    count++;
                }
            }
            stmt.clearBatch();
            logger.info("Execute the List of Queries SQL");
            return null;
        }else {
            // execute the query, and get a java resultset
            try {
                //Executes the given SQL statement, which returns a single ResultSet object.
                logger.info("Execute the Query SQL:"+sql);
                return stmt.executeQuery(sql);
            } catch (SQLException e) {
                try {
                    //Executes the given SQL statement, which may be an INSERT, UPDATE,
                    // or DELETE statement or an SQL statement that returns nothing,
                    // such as an SQL DDL statement.
                    stmt.executeUpdate(sql);
                    logger.info("Execute the Query SQL:"+sql);
                    return null;
                } catch (SQLException e1) {
                    //if a mix separated from ";"
                    try {
                        stmt.execute(sql);
                        logger.info("Execute the Query SQL:" + sql);
                        return null;
                    }catch(Exception e42){
                        logger.error(e42.getMessage(),e42);
                        return null;
                    }
                }
            }
        }
    }

    /**
     * Method to execute a query SQL.
     * @param aSQLScriptFilePath the File script SQL.
     * @param conn the Connection to the Database where execute the query.
     * @return the ResultSet of the Query SQL.
     */
    public static boolean executeSQL(File aSQLScriptFilePath,Connection conn){
        try {
            if(!aSQLScriptFilePath.exists()){
                throw new IOException("The File "+aSQLScriptFilePath.getAbsolutePath()+" not exists.");
            }
            // Give the input file to Reader
            Reader reader = new BufferedReader(new FileReader(aSQLScriptFilePath));
            //InputStream stream = new BufferedInputStream(new FileInputStream(aSQLScriptFilePath));
            // Execute script
            ScriptRunner scriptRunner = new ScriptRunner(conn, false, true);
            scriptRunner.runScript(reader);
            return true;
        } catch (SQLException|IOException e) {
            logger.error("Failed to Execute" + aSQLScriptFilePath.getAbsolutePath()
                    + " The error is " + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to print hte result of a query.
     * @param sql the String SQL.
     * @return if true all the operation are done.
     */
    public static boolean checkData(String sql){
        ResultSet rs ;
        try {
            rs = stmt.executeQuery(sql);

            java.sql.ResultSetMetaData metadata = rs.getMetaData();
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                logger.info("\t" + metadata.getColumnLabel(i + 1));
            }
            logger.info("\n----------------------------------");
            while (rs.next()) {
                for (int i = 0; i < metadata.getColumnCount(); i++) {
                    Object value = rs.getObject(i + 1);
                    if (value == null) {
                        logger.info("\t       ");
                    } else {
                        logger.info("\t" + value.toString().trim());
                    }
                }
                logger.info("");
            }
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
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
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to get a content of a table.
     * @param tableName the String name of the table.
     * @param connection the Connection to the Database.
     * @return the Vector String Arrays of the content.
     * @throws SQLException throw if the name if the table not exists on the Database.
     */
    public static List<String[]> getContentOfATable(String tableName, Connection connection)
            throws SQLException {
        String sqlQuery = "SELECT * FROM " + tableName;
        Statement statement = connection.createStatement(  );
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        int numColumns = resultSet.getMetaData(  ).getColumnCount(  );
        String[  ] aRow;
        List<String[]> allRows = new ArrayList<>();
        while(resultSet.next(  )) {
            aRow = new String[numColumns];
            for (int i = 0; i < numColumns; i++)
                //ResultSet access is 1-based, arrays are 0-based
                aRow[i] = resultSet.getString(i+1);
            allRows.add(aRow);
        }
        return allRows;
    }

    /**
     * Method to get the execution time of a SQL query with the Java API.
     * @param sql the String SQL Query.
     * @return the Long value of the time for execute the query.
     */
    public static Long getExecutionTime(String sql){
        return getExecutionTime(sql,conn);
    }

    /**
     * Method to get the execution time of a SQL query with the Java API.
     * @param sql the String SQL Query.
     * @param conn the Connection to the Database where execute the query.
     * @return the Long value of the time for execute the query.
     */
    public static Long getExecutionTime(String sql,Connection conn){
        //sql = sql.replaceAll("''","''''");
        //Connection dbConnection = getConnectionFromDriver(  );
        ConnectionWrapper dbConnection = new ConnectionWrapper(conn);
        Long calculate;
        try {
            stmt = dbConnection.createStatement();
            com.github.p4535992.util.string.Timer timer = new com.github.p4535992.util.string.Timer();
            timer.startTimer();
            ResultSet rs = stmt.executeQuery(sql);
            calculate = timer.endTimer();

        }catch (Exception e) {
            logger.error("Can't get the execution time for the query:"+sql,e);
            return 0L;
        }
        Long calculate2 = JDBCLogger.getTime()/1000;
        if(calculate > calculate2) calculate = calculate2;
        logger.info("Query SQL result(s) in "+calculate+"ms.");
        return calculate;
    }

    /**
     * Method to import a file CSV to a Database.
     * @param fileCSV the File CSv to import.
     * @param firstLine if true the first line of the file CSV contains the headers of the fields.
     * @param separator the Cgaracter of the separator field on the CSV file.
     * @param nameTable the String name of the table.
     * @param connection the Connection to the Database where execute the query.
     * @return if true all the operation are done.
     */
    public static boolean importCsvInsertInto(File fileCSV, boolean firstLine,Character separator,
                                       String nameTable,Connection connection) {
        String[] columns = FileUtilities.CSVGetHeaders(fileCSV, firstLine);
        if(columns == null){
            logger.error("Can't load the CSV because we need a first line of headers instead the columns is NULL");
            return false;
        }
        JOOQUtilities.setConnection(connection);
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
                        types[i] = SQLUtilities.convertStringToSQLTypes(values[i]);
                    }
                    insertQuery = JOOQUtilities.insert(nameTable, columns,values,types);
                    SQLUtilities.executeSQL(insertQuery,connection);
                }
            }
            logger.info("Data CSV File Successfully Uploaded");
            return true;
        } catch (SQLException |IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to get the String name of the host by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static String getHostFromUrl(String url) {
        try {
            /*String regexForHostAndPort = "[.\\w]+:\\d+";
            Pattern hostAndPortPattern = Pattern.compile(regexForHostAndPort);
            Matcher matcher = hostAndPortPattern.matcher(url);
            if(matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (start >= 0 && end >= 0) {
                    String hostAndPort = url.substring(start, end);
                    String[] array = hostAndPort.split(":");
                    if (array.length >= 2)
                        return array[0];
                }
            }
            throw new IllegalArgumentException("couldn't find pattern '" + regexForHostAndPort + "' in '" + url + "'");*/
            return supportGetHostAndPort(url,0);
        }catch(IllegalArgumentException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to get the Integer value of the port by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static Integer getPortFromUrl(String url) {
        try {
            /*String regexForHostAndPort = "[.\\w]+:\\d+";
            Pattern hostAndPortPattern = Pattern.compile(regexForHostAndPort);
            Matcher matcher = hostAndPortPattern.matcher(url);
            if(matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (start >= 0 && end >= 0) {
                    String hostAndPort = url.substring(start, end);
                    String[] array = hostAndPort.split(":");
                    if (array.length >= 2)
                        return Integer.parseInt(array[1]);
                }
            }
            throw new IllegalArgumentException("couldn't find pattern '" + regexForHostAndPort + "' in '" + url + "'");*/
            return Integer.parseInt(supportGetHostAndPort(url,1));
        }catch(IllegalArgumentException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    private static String supportGetHostAndPort(String url,int i){
        String regexForHostAndPort = "[.\\w]+:\\d+";
        Pattern hostAndPortPattern = Pattern.compile(regexForHostAndPort);
        Matcher matcher = hostAndPortPattern.matcher(url);
        if(matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start >= 0 && end >= 0) {
                String hostAndPort = url.substring(start, end);
                String[] array = hostAndPort.split(":");
                if (array.length >= 2) return array[i];
            }
        }
        throw new IllegalArgumentException("couldn't find pattern '" + regexForHostAndPort + "' in '" + url + "'");
    }

    /**
     * Method to get the String value of the username by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static String getUsernameFromUrl(String url){
        try {
            Pattern pat = Pattern.compile(
                    "(\\&|\\?|\\=|\\/)?(user|username)(\\=)(.*?)(\\&|\\?|\\=|\\/|\\s)+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pat.matcher(url + " ");
            if (matcher.find()) {
                String[] find = (matcher.group(0)).split("=");
                return find[1].substring(0, find[1].length() - 1);
            }
            throw new IllegalArgumentException("couldn't find pattern '" + pat.toString() + "' in '" + url + "'");
        }catch(IllegalArgumentException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to get the String value of the password by the String URL of the jdbc driver.
     * @param url the String of the URL of the jdbc driver.
     * @return the String name of the host.
     */
    public static String getPasswordFromUrl(String url){
        try {
            Pattern pat = Pattern.compile(
                    "(\\&|\\?|\\=|\\/)?(pass|password)(\\=)(.*?)(\\&|\\?|\\=|\\/|\\s)+", Pattern.CASE_INSENSITIVE
            );
            Matcher matcher = pat.matcher(url + " ");
            if (matcher.find()) {
                String[] find = (matcher.group(0)).split("=");
                return find[1].substring(0, find[1].length() - 1);
            }
            throw new IllegalArgumentException("couldn't find pattern '" + pat.toString() + "' in '" + url + "'");
        }catch(IllegalArgumentException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static String getURL(Connection conn){
        try {
            return conn.getMetaData().getURL();
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /*public enum DATABASE_URL{ HOST,PATH,USERNAME,PASSWORD,DRIVER,PORT}

    public static String getInfoFromUrl(String url,DATABASE_URL database_url){
        URI dbUri;
        String urlPart = database_url.name();
        url = url.split("://")[1];
        url = "http://"+url;
        try {
            dbUri = new URI(System.getenv(url));
            switch(urlPart){
                case "HOST": return dbUri.getHost();
                case "DRIVER":return dbUri.getScheme();
                case "USERNAME":return dbUri.getUserInfo().split(":")[0];
                case "PASSWORD": return dbUri.getUserInfo().split(":")[1];
                case "PATH": return dbUri.getPath();
                case "PORT": return String.valueOf(dbUri.getPort());
                default: return "N/A";
            }

        } catch (URISyntaxException e) {
            logger.error("Couldn't find pattern '" + urlPart + "' in '" + url + "':"+e.getMessage(),e);
            return "N/A";
        }
    }*/

    public static String getURL(DatabaseMetaData databaseMetadata){
        try {
            return databaseMetadata.getURL();
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static void invokeClassDriverForDbType(DBType dbType)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String name = dbType.name();
        switch(name){
            case "MYSQL": {
                try {
                    invokeClassDriver(DBDriver.MYSQL.getDriver()); //load driver//"com.sql.jdbc.Driver"
                    break;
                } catch (ClassNotFoundException e) {
                    invokeClassDriver(DBDriver.MYSQL_GJT.getDriver());
                    break;
                }
            }
            case "ORACLE":{
                invokeClassDriver(DBDriver.ORACLE.getDriver());//"oracle.jdbc.driver.OracleDriver"
                break;
            }
            case "H2":{
                invokeClassDriver(DBDriver.H2.getDriver()); //"org.h2.Driver"
                break;
            }
            case "HSQL":{
                invokeClassDriver(DBDriver.HSQLDB.getDriver());//"org.hsqldb.jdbcDriver"
                break;
            }
        }
    }

    public static void invokeClassDriver(String driverClassName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(driverClassName).newInstance(); //load driver//"com.sql.jdbc.Driver"
    }

    public static void invokeClassDriverForDbDriver(DBDriver driverClassName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(driverClassName.getDriver()).newInstance(); //load driver//"com.sql.jdbc.Driver"
    }


    //--------------------------------------------------------------------------------

    public static DataSource getLocalPooledConnection(
            String dataSourceName,String jdbcUrl,String driverDbClassName,
            String username,String password) {
        return getPooledLocalConnectionBase(dataSourceName,jdbcUrl,driverDbClassName,username,password);
    }

    public static DataSource createDataSource(
            String dataSourceName,String jdbcUrl,String driverDbClassName,
            String username,String password) {
        return getPooledLocalConnectionBase(dataSourceName,jdbcUrl,driverDbClassName,username,password);
    }

    public static DataSource getLocalPooledConnection(String dataSourceName) {
        return getPooledLocalConnectionBase(dataSourceName,null,null,null,null);
    }

    public static DataSource getDataSource(String dataSourceName) {
        return getPooledLocalConnectionBase(dataSourceName,null,null,null,null);
    }


    /**
     * https://www.safaribooksonline.com/library/view/java-enterprise-best/0596003846/ch04.html
     * http://penguindreams.org/blog/running-beans-that-use-application-server-datasources-locally/
     * http://www.java2s.com/Code/Java/Database-SQL-JDBC/MiniConnectionPoolManager.htm
     */
    private static DataSource getPooledLocalConnectionBase(
            String dataSourceName,String jdbcUrl,String driverDbClassName,
            String username,String password) {
        logger.info("Attempting to connect to the DataSource '" + dataSourceName+"'...");
        try {
            logger.info("...initializing the naming context...");
            if(jdbcUrl == null) {
                //Use DatabaseContext (all context set inner)
                try {
                    NamingManager.setInitialContextFactoryBuilder(new DatabaseContextFactory());
                }catch(java.lang.IllegalStateException e){
                    logger.warn("InitialContextFactoryBuilder already set");
                   /* DatabaseContextFactory factory = new DatabaseContextFactory();
                    Properties env = new Properties();
                    env.put(Context.INITIAL_CONTEXT_FACTORY, driverDbClassName);
                    env.put(Context.PROVIDER_URL, url);
                    factory.createInitialContextFactory(env);*/
                    return dataSource;
                }
            }else {
                //Use LocalContext (all context set outer)
               /* LocalContext ctx = LocalContextFactory.createLocalContext("com.mysql.jdbc.Driver");
                ctx.addDataSource("jdbc/js1","jdbc:mysql://dbserver1/dboneA", "username", "xxxpass");*/
                LocalContext ctx = LocalContextFactory.createLocalContext(driverDbClassName);
                ctx.addDataSource(dataSourceName, jdbcUrl, username, password);
                //callDataSource(dataSourceName);
            }
            logger.info("...establishing a context...");
        } catch (NamingException e) {
            logger.error(e.getMessage(),e);
        }

        try {
            dataSource = (DataSource) new InitialContext().lookup(dataSourceName);
            conn = dataSource.getConnection();
            logger.info("...establishing a connection to the datasource '"+dataSourceName+"', " +
                    "connect to the database '"+dataSource.getConnection().getCatalog()+"'");
        }catch (NamingException|SQLException e) {
            logger.error(e.getMessage(),e);
        }
        return dataSource;
    }

    public static Boolean exportData(Connection conn,String filename,String tableName) {
        Statement stmt;
        String query;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            //For comma separated file
            query = "SELECT * INTO OUTFILE '"+filename+"' FIELDS TERMINATED BY ',' FROM "+tableName+";";
            //stmt.executeQuery(query);
            // stmt.executeUpdate("SELECT * INTO OUTFILE \"" + filename + "\" FROM " + tablename);
            executeSQL(query,conn,stmt);
            return true;
        } catch(Exception e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public static Boolean importData(Connection conn,File file,String databaseName,String tableName){
        Statement stmt;
        String query;
        try {
            Map<String,String[]> map = getTableAndColumn(conn,databaseName);
            String[] columns = map.get(tableName);
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            query = "LOAD DATA INFILE '"+file+"' INTO TABLE "+tableName+" "+
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\\r' " +
                    "IGNORE 1 LINES " +
                    "("+ ArrayUtilities.toString(columns,',')+");";
            stmt.executeUpdate(query);
            return true;
        }catch(Exception e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public static void main(String[] args) throws IOException, SQLException, URISyntaxException {
        String userDir = new File(".").getCanonicalPath();
        String userDir2 = StringUtilities.PROJECT_DIR;  String userDir3 = LogBackUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        LogBackUtil.console();
        //test1 jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true
        //test2 jdbc:postgresql://host:port/database?user=userName&password=pass

        //WORK
        //Connection conn = getMySqlConnection("localhost","3306","geodb","siimobility","siimobility");
        //String url = "jdbc:postgresql://host:port/database?user=userName&password=pass";


        //WORK
      /* DataSource conn2 = getLocalConnection(
                "ds1","jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true",
                "com.mysql.jdbc.Driver","siimobility","siimobility");*/

        DataSource conn2 = getLocalPooledConnection(
                "ds1","jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true",
                "com.mysql.jdbc.Driver","siimobility","siimobility");

        executeSQL(LogBackUtil.getMySQLScript(),conn);
        //WORK
        /*DataSource conn3 = getLocalConnection("ds1");*/

        //NOT WORK
        /*Connection conn4 = getPooledConnection("ds1",
                "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true","com.mysql.jdbc.Driver");*/

        String test = "";

    }

    //UTILITY ENUMERATOR

    public enum DBType {MYSQL,H2,ORACLE,HSQLDB,SQL,DB2,HSQL,MARIADB}

    public enum DBDriver{MYSQL(0),MYSQL_GJT(1),H2(2),ORACLE(3),HSQLDB(4);

        private final Integer value;
        DBDriver(Integer value) {
            this.value = value;
        }

        public String getDriver(){
            return toString();
        }

        @Override
        public String toString() {
            String driver ="";
            switch (this) {
                case MYSQL: driver = "com.mysql.jdbc.Driver"; break;
                case MYSQL_GJT: driver = "org.gjt.mm.mysql.Driver"; break;
                case ORACLE: driver = "oracle.jdbc.driver.OracleDriver"; break;
                case H2: driver = "org.h2.Driver"; break;
                case HSQLDB: driver = "org.hsqldb.jdbcDriver"; break;
            }
            return driver;
        }
    }

    public enum DBConnector{MYSQL(0),H2(1),ORACLE(2),HSQLDB(3);

        private final Integer value;
        DBConnector(Integer value) {
            this.value = value;
        }

        public String getConnector(){
            return toString();
        }

        @Override
        public String toString() {
            String driver ="";
            switch (this) {
                case MYSQL: driver = "jdbc:mysql://"; break;
                case HSQLDB: driver = "jdbc:hsqldb:hsql://"; break;
                case H2: driver = "jdbc:h2:tcp://"; break;
                case ORACLE: driver = "jdbc:oracle:thin:@"; break;
            }
            return driver;
        }
    }

}
