package com.github.p4535992.util.database.sql;

import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.collection.ListUtilities;
import com.github.p4535992.util.database.jooq.JOOQUtilities;
import com.github.p4535992.util.database.sql.runScript.ScriptRunner;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.github.p4535992.util.string.*;

import com.opencsv.CSVReader;

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
        }else dialectDB = SQLConverter.convertDialectDatabaseToTypeNameId(dialectDB);
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
            invokeClassDriverForDbType(SQLEnum.DBType.HSQLDB);
            String url = SQLEnum.DBConnector.HSQLDB.getConnector() + host;
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
            invokeClassDriverForDbType(SQLEnum.DBType.MYSQL);
            String url = SQLEnum.DBConnector.MYSQL.getConnector() + host;
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
            invokeClassDriverForDbType(SQLEnum.DBType.MYSQL);
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
            invokeClassDriverForDbType(SQLEnum.DBType.ORACLE);
            //String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
            String url = SQLEnum.DBConnector.ORACLE.getConnector() + host;
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
            invokeClassDriverForDbType(SQLEnum.DBType.H2);
            /*
            jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName>
            jdbc:h2:tcp://localhost/~/test
            jdbc:h2:tcp://dbserv:8084/~/sample
            jdbc:h2:tcp://localhost/mem:test
            */
            String url = SQLEnum.DBConnector.H2.getConnector() + host;
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
            String type = SQLConverter.convertSQLTypes2String(columnType);
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
                ResultSet rs = stmt.executeQuery(sql);
                logger.info("Execute the Query SQL:"+sql);
                return rs;
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
        //ConnectionWrapper dbConnection = new ConnectionWrapper(conn);
        Long calculate;
        try {
            stmt = conn.createStatement();
            com.github.p4535992.util.string.Timer timer = new com.github.p4535992.util.string.Timer();
            timer.startTimer();
            ResultSet rs = stmt.executeQuery(sql);
            calculate = timer.endTimer();

        }catch (Exception e) {
            logger.error("Can't get the execution time for the query:"+sql,e);
            return 0L;
        }
        //Long calculate2 = JDBCLogger.getTime()/1000;
        //if(calculate > calculate2) calculate = calculate2;
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
                        types[i] = SQLConverter.convertStringToSQLTypes(values[i]);
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
            return "";
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
            return "";
        }
    }

    public static void invokeClassDriverForDbType(SQLEnum.DBType dbType)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String name = dbType.name();
        switch(name){
            case "MYSQL": {
                try {
                    invokeClassDriver(SQLEnum.DBDriver.MYSQL.getDriver()); //load driver//"com.sql.jdbc.Driver"
                    break;
                } catch (ClassNotFoundException e) {
                    invokeClassDriver(SQLEnum.DBDriver.MYSQL_GJT.getDriver());
                    break;
                }
            }
            case "ORACLE":{
                invokeClassDriver(SQLEnum.DBDriver.ORACLE.getDriver());//"oracle.jdbc.driver.OracleDriver"
                break;
            }
            case "H2":{
                invokeClassDriver(SQLEnum.DBDriver.H2.getDriver()); //"org.h2.Driver"
                break;
            }
            case "HSQL":{
                invokeClassDriver(SQLEnum.DBDriver.HSQLDB.getDriver());//"org.hsqldb.jdbcDriver"
                break;
            }
        }
    }

    public static void invokeClassDriver(String driverClassName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(driverClassName).newInstance(); //load driver//"com.sql.jdbc.Driver"
    }

    public static void invokeClassDriverForDbDriver(SQLEnum.DBDriver driverClassName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(driverClassName.getDriver()).newInstance(); //load driver//"com.sql.jdbc.Driver"
    }


    //--------------------------------------------------------------------------------

    /*public static DataSource getLocalPooledConnection(
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
    }*/


    /*
     * https://www.safaribooksonline.com/library/view/java-enterprise-best/0596003846/ch04.html
     * http://penguindreams.org/blog/running-beans-that-use-application-server-datasources-locally/
     * http://www.java2s.com/Code/Java/Database-SQL-JDBC/MiniConnectionPoolManager.htm
     */
    /*private static DataSource getPooledLocalConnectionBase(
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
                   *//* DatabaseContextFactory factory = new DatabaseContextFactory();
                    Properties env = new Properties();
                    env.put(Context.INITIAL_CONTEXT_FACTORY, driverDbClassName);
                    env.put(Context.PROVIDER_URL, url);
                    factory.createInitialContextFactory(env);*//*
                    return dataSource;
                }
            }else {
                //Use LocalContext (all context set outer)
               *//* LocalContext ctx = LocalContextFactory.createLocalContext("com.mysql.jdbc.Driver");
                ctx.addDataSource("jdbc/js1","jdbc:mysql://dbserver1/dboneA", "username", "xxxpass");*//*
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
    }*/

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

    public static Boolean importData(File file,char delimiter,String databaseName,String tableName){
        return importData(conn,file,delimiter,databaseName,tableName);
    }

    public static Boolean importData(Connection conn,File file,char delimiter,String databaseName,String tableName){
        Statement stmt;
        String query = "";
        try {
            if(!FileUtilities.isFileExists(file)) return false;
            //Map<String,String[]> map = getTableAndColumn(conn,databaseName);
            //String[] columns = map.get(tableName);
            String[] columns = OpenCsvUtilities.getHeadersWithUnivocity(file,true);
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String filePath = file.getAbsolutePath().replace("\\","\\\\");
            //NOT WORK
           /* query = "LOAD DATA INFILE '"+file+"' INTO TABLE "+tableName+" "+
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\\r' " +
                    "IGNORE 1 LINES " +
                    "("+ ArrayUtilities.toString(columns,',')+");";*/
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");
            query = "LOAD DATA INFILE '"+filePath+"' INTO TABLE "+databaseName+"."+tableName+" "+
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\r\n'"+
                    "IGNORE 1 LINES " +
                    "("+ ArrayUtilities.toString(columns,delimiter)+");";
            stmt.executeUpdate(query);
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1;");
            logger.info("Execute the Query SQL:"+query);
            return true;
        }catch(SQLException e) {
            logger.error("Can't execute query:"+query,e);
            return false;
        }
    }

    public static Boolean cleanSQLScriptForOldVersion(File sqlScript){
        try {
            List<String> lines = FileUtilities.read(sqlScript);
            List<String> newLines = new ArrayList<>();
            for (String line : lines) {
                if (line == null || line.isEmpty()) continue;
                //Regex r = new Regex(symbol+"(.+?)"+symbol, RegexOptions.Compiled);
                StringBuilder sb = new StringBuilder();
                String newLine = line;
                boolean endWith = newLine.endsWith(");") || newLine.contains("ENGINE=InnoDB");
                for (char c : newLine.toCharArray()) {
                    if (c == ';') sb.append("");
                    else if (c == '\\') sb.append("");
                    else sb.append(c);
                }
                newLine = sb.toString();
                //v = v.Replace("'", "");
                newLine = newLine.replace(";", "");
                newLine = newLine.replace("\\", "");
                newLine = newLine.replaceAll("\\s+", " "); //collapse whitespace
                newLine = newLine.trim();
                if (endWith) newLine = newLine + ";";
                newLines.add(newLine);
            }
            FileUtilities.write(newLines, new File(FileUtilities.addSuffixTimeStampToFileName(sqlScript.getAbsolutePath())));
        }catch(Exception e){
            return false;
        }
        return true;
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

        /*DataSource conn2 = getLocalPooledConnection(
                "ds1","jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true",
                "com.mysql.jdbc.Driver","siimobility","siimobility");
*/
        executeSQL(LogBackUtil.getMySQLScript(),conn);
        //WORK
        /*DataSource conn3 = getLocalConnection("ds1");*/

        //NOT WORK
        /*Connection conn4 = getPooledConnection("ds1",
                "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true","com.mysql.jdbc.Driver");*/

        String test = "";

    }

    public static Map<String, String> loadQueriesFromPropertiesFile(File queriesProperties) {
        Properties properties = new Properties();
        Map<String, String> queries = new TreeMap<>();
        try {
            properties.load(new FileInputStream(queriesProperties));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error loading query properties from: " + queriesProperties, e);
        }
        for (Object key : properties.keySet()) {
            String property = String.valueOf(key).trim();
            String queryName = null;
            String query = null;
            String propertyName;
            int dotIndex = property.indexOf('.');
            if (dotIndex != -1) {
                propertyName = property.substring(0, dotIndex);
            } else {
                throw new IllegalArgumentException("Invalid property in " + queriesProperties + ": " + property);
            }
            if (property.endsWith(".name")) {
                queryName = properties.getProperty(property);
                query = properties.getProperty(propertyName + ".query");
                if (query == null) {
                    throw new IllegalArgumentException("No query defined for name " + queryName);
                }
            } else if (property.endsWith(".query")) {
                query = properties.getProperty(property);
                queryName = properties.getProperty(propertyName + ".name");
                if (queryName == null) {
                    throw new IllegalArgumentException("No name defined for query " + query);
                }
            }
            if (queryName == null || query == null) {
                throw new IllegalArgumentException("Invalid property in " + queriesProperties + ": " + property);
            }
            queries.put(queryName, query);
        }
        return queries;
    }


}
