package com.github.p4535992.util.database.sql2;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jooq.*;
import org.jooq.impl.*;

import com.github.p4535992.util.file.csv.CsvUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

/**
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
 */
@SuppressWarnings("unused")
public class SQL2Utilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SQL2Utilities.class);

    private static DSLContext dslContext;
    private static SQL2Utilities instance = null;
    private static SQLDialect sqlDialect;
    private static Connection connection;
    private static ConnectionProvider connProvider;
    //private static org.hibernate.engine.jdbc.connections.spi.ConnectionProvider connHibernateProvider;
    
    //private static DataSource dataSource;
    private static Connection conn;
    private static Statement stmt;

    //private static final Pattern NEW_DELIMITER_PATTERN = Pattern.compile("(?:--|\\/\\/|\\#)?!DELIMITER=(.+)");
    //private static final Pattern COMMENT_PATTERN = Pattern.compile("^(?:--|\\/\\/|\\#).+");

    protected SQL2Utilities(){}

    public static SQL2Utilities getIstance(){
        if(instance == null){
            instance = new SQL2Utilities();
        }
        return instance;
    }
    public static SQL2Utilities getNewIstance(){
        return new SQL2Utilities();
    }

    public static DSLContext getDslContext() {
        return dslContext;
    }

    public static void setDslContext(DSLContext dslContext) {
        SQL2Utilities.dslContext = dslContext;
    }

    public static SQLDialect getSqlDialect() {
        return sqlDialect;
    }

    public static void setSqlDialect(SQLDialect sqlDialect) {
        SQL2Utilities.sqlDialect = sqlDialect;
    }

    public static Connection getConnection() {
        return connection;
    }
    
    public static Connection getCurrentConnection() {
        return conn;
    }

    public static void setConnection(Connection conn) {
        connection = conn;
        connProvider= new DefaultConnectionProvider(connection);
        sqlDialect = getSQLDialect(connection);
        dslContext = DSL.using(connection,sqlDialect);        
        SQL2Utilities.conn = connection;
    }
    
    /**
     * Method to set the connection with a specific SQLDialect.
     * @param connection the connection object.
     * @param dialect the SQLDialect object.
     */
    public static void setConnection(Connection conn,SQLDialect dialect){
    	 connection = conn;
         connProvider= new DefaultConnectionProvider(connection);
         sqlDialect = dialect;
         dslContext = DSL.using(connection,sqlDialect);
    }

    /**
     * Method to set the connection with a specific SQLDialect.
     * @param configuration configuration object.
     */
    public static void setConnection(org.jooq.Configuration configuration) {
        dslContext = DSL.using(configuration);
        connection = configuration.connectionProvider().acquire();
        connProvider= configuration.connectionProvider();
        sqlDialect = configuration.dialect();        
    }


    /**
     * Method to close the connection to the database.
     * @throws SQLException 
     */
    public static void closeConnection() throws SQLException{
        if(connection!=null) {
            if (connProvider != null) connProvider.release(connection);
            //if (connSpringProvider != null) connSpringProvider.release(connection);
        }
        
        if(conn != null){
        	conn.close();
        }
    }
    
    public static void main(String[] args) throws IOException, SQLException, URISyntaxException {

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
        //executeSQL(LogBackUtil.getMySQLScript(),conn);
        //WORK
        /*DataSource conn3 = getLocalConnection("ds1");*/

        //NOT WORK
        /*Connection conn4 = getPooledConnection("ds1",
                "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true","com.mysql.jdbc.Driver");*/

        String test = "";
        
        
        String[] columns = new String[]{"col1","col2","col3"};
        Object[] values =new Object[]{1,"test",null};
        int[] types = new int[]{Types.INTEGER,Types.VARCHAR,Types.NULL};
        prepareConnection("localhost", "3306", "geodb", "siimobility", "siimobility",DBType.MYSQL);
        //INSERT
        String query = insert("tabl1",columns,values,types);
        System.out.println(toStringInline(query));
        //query = insert("tabl1",columns,values,types,true);
        //System.out.println(StringKit.toStringInline(query));

        query = insert("tabl1", columns, values, types, true);
        System.out.println(toStringInline(query));
        //SELECT
        query = select("tabl1", columns);
        System.out.println(toStringInline(query));
        query = select("tabl1",columns,true);
        System.out.println(toStringInline(query));
        org.jooq.Field<String> f1 = (org.jooq.Field<String>) createFieldValue("col1");
        org.jooq.Field<String> f2 = (org.jooq.Field<String>) createFieldValue("col2");
        /*final Condition cond1 = f1.eq(f2);
        final Condition cond2 = f1.isNotNull();
        final Condition cond3 = f2.isNotNull();
        List<Condition> cinds = new ArrayList<Condition>(){{
            add(cond1);
            add(cond2);
            add(cond3);
        }};*/
        List<Condition> cinds;
        cinds = convertToListConditionEqualsWithAND(new String[]{"col1","col2"},new Object[]{null,"43"});

        query = select("tabl1", columns, false, cinds);
        System.out.println(toStringInline(query));
        query = select("tabl1", columns,true,cinds);
        System.out.println(toStringInline(query));
        cinds = convertToListConditionEqualsWithAND(new String[]{"col1","col2"},new Object[]{null,43});
        query = select("tabl1", columns, false, cinds);
        System.out.println(toStringInline(query));
        query = select("tabl1", columns,true,cinds);
        System.out.println(toStringInline(query));

        //UPDATE
        query = update("tabl1", columns, values);
        System.out.println(toStringInline(query));
        query = update("tabl1",columns,values,true);
        System.out.println(toStringInline(query));
        query = update("tabl1", columns, values, false, cinds);
        System.out.println(toStringInline(query));
        //DELETE
        query = delete("tabl1",false,cinds);
        System.out.println(toStringInline(query));

    }

    /**
     * Method to convert a DialectDB to a SQLDialect of JOOQ.
     * @param dialectDb String name of a dialectDb.
     * @return the SQLDialect of JOOQ.
     */
    public static SQLDialect convertDialectDBToSQLDialectJOOQ(String dialectDb){
        return convertStringToSQLDialectJOOQ(dialectDb);
    }

    /**
     * Method to convert a String to a SQLDialect of JOOQ.
     * @param sqlDialect the String name of the SQLDialect.
     * @return the SQLDialect of JOOQ.
     */
    public static SQLDialect convertStringToSQLDialectJOOQ(String sqlDialect) {
        sqlDialect = convertDialectDatabaseToTypeNameId(sqlDialect);
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
            default: {
                logger.warn("Can't found a correct SQLDialect for the String:"+sqlDialect);
                return SQLDialect.DEFAULT;
            }
        }
    }

    /**
     * Method get SQLDialect JOOQ from Connection.
     * @param conn the Connection java SQL.
     * @return the JOOQ SQLDialect.
     */
    public static SQLDialect getSQLDialect(Connection conn){
        DatabaseMetaData m;
        if(conn!=null) {
            try {
                m = conn.getMetaData();
                return convertDialectDBToSQLDialectJOOQ(m.getDatabaseProductName());
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
                return null;
            }
        }else{
            logger.error("No SQL Connection open,can't create a JOOQ SQLDialect the Connection is NULL");
            return null;
        }
    }

    /**
     * Method to create a string query SQL for insert into operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param values arrays of object of the values of the columns.
     * @param types arrays of int of the type of the values of the columns.
     * @return string query SQL for the insert into operation.
     */
    public static String insert(String nameTable,String[] columns,Object[] values,int[] types){
        return insert(nameTable, columns, values, types, false);
    }

    /**
     * Method to create a string query SQL for insert into operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param values arrays of object of the values of the columns.
     * @param types arrays of int of the type of the values of the columns.
     * @param preparedStatement if true print all value with the symbol "?".
     * @return string query SQL for the insert into operation.
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static String insert(String nameTable,String[] columns,Object[] values,int[] types, boolean preparedStatement){
    	org.jooq.Table<Record> table = new TableImpl<>(nameTable);
        /** SOLUTION 2 with default values */
        /*InsertQuery<Record> iQuery = dslContext.insertQuery(table);
        //Map<Field<String>,?> map = convertArraysToMapJOOQField2(columns, values);
        //iQuery.addValuesForUpdate(map);
        Map<String,?> map = CollectionKit.convertTwoArrayToMap(columns,values);
        Record record = new TableRecordImpl<>(new TableImpl<TableRecord>(nameTable));
        //record.from(table,columns);
       *//* for(int i = 0; i < columns.length; i++){
            record.setValue(createField(columns[i]), (String) values[i]);
        }*//*
        record = record.into(convertObjecyArrayToFieldValueArray(values));
        //record.fromMap(map,columns);
        iQuery.addRecord(record);
        *//*for(int i=0; i < columns.length; i++) {
            iQuery.addValueForUpdate(createField(columns[i]),createField(values[i],types[i]));
        }*/
        //WORK
        /** SOLUTION 1 with specific values */
        Map<org.jooq.Field<String>,?> map = convertArraysToMapJOOQField(columns, values, types);
        Query iQuery = dslContext.insertInto(table).set(map);
        if(preparedStatement){
            String query = toStringInline(iQuery.toString());
            query = getQueryInsertValuesParam(query, columns);
            //return StringKit.toStringInline(iQuery.getSQL(ParamType.NAMED_OR_INLINED));
            return toStringInline(query);
        }
        else return toStringInline(iQuery.toString());
    }


    /**
     * Method to create a string query SQL for select operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @return string query SQL for the select operation.
     */
    public static String select(String nameTable,String[] columns){
        return select(nameTable, columns, false);
    }

    /**
     * Method to create a string query SQL for select operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param preparedStatement if true print all value with the symbol "?".
     * @return string query SQL for the select operation.
     */
    public static String select(String nameTable,String[] columns,boolean preparedStatement){
       return select(nameTable, columns, preparedStatement, null);
    }

    /**
     * Method to create a string query SQL for select operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param preparedStatement if true print all value with the symbol "?".
     * @param conditions list of jooq Condition for filter the result of the select operation.
     * @return string query SQL for the select operation.
     */
    public static String select(String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions){
        return select(nameTable,columns,preparedStatement,conditions, "","");
    }

    /**
     * Method to create a string query SQL for select operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param preparedStatement if true print all value with the symbol "?".
     * @param conditions list of jooq Condition for filter the result of the select operation.
     * @param limit Integer of the limit to add tot the query SQL.
     * @param offset Integer of the offset to add tot the query SQL.
     * @return string query SQL for the select operation.
     */
    public static String select(
            String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions, Integer limit,Integer offset){
        if(limit != null && offset != null) {
            return select(nameTable,columns,preparedStatement,conditions,limit.toString(),offset.toString());
        }else if(limit != null){
            return select(nameTable,columns,preparedStatement,conditions,limit.toString(),null);
        }else if(offset != null){
            return select(nameTable,columns,preparedStatement,conditions,null,offset.toString());
        }
        return select(nameTable,columns,preparedStatement,conditions, "", "");
    }

    /**
     * Method to create a string query SQL for select operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param preparedStatement if true print all value with the symbol "?".
     * @param conditions list of jooq Condition for filter the result of the select operation.
     * @param limit String of the limit to add tot the query SQL.
     * @param offset String of the offset to add tot the query SQL.
     * @return string query SQL for the select operation.
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static String select(String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions,
                                  String limit,String offset){
    	org.jooq.Field[] fields = new org.jooq.Field[columns.length];
        for(int i=0; i < columns.length; i++){
        	org.jooq.Field<String> field = DSL.field(columns[i],String.class);
            fields[i] = field;
        }
        org.jooq.Table<Record> table = new TableImpl<>(nameTable);
        SelectQuery<Record> sQuery = dslContext.selectQuery();
        sQuery.addSelect(fields);
        sQuery.addFrom(table);
        if(conditions!=null && !conditions.isEmpty()) sQuery.addConditions(conditions);
        if(!isNullOrEmpty(limit) && !isNullOrEmpty(offset)) {
            if (isNumeric(limit) && isNumeric(offset)) {
                sQuery.addLimit(toInt(offset), toInt(limit));
            }
        }else if(!isNullOrEmpty(limit)){
            if (isNumeric(limit)) {
                sQuery.addLimit(toInt(limit));
            }
        }else if(!isNullOrEmpty(offset)){
            if (isNumeric(offset)) {
                sQuery.addLimit(toInt(offset), 1000000);
            }
        }
        if(preparedStatement ) {
            if (conditions==null || conditions.isEmpty()) {
                return toStringInline(sQuery.toString());
            }else {
                String query = getQueryInsertWhereParam(toStringInline(sQuery.toString()));
                return toStringInline(query);
            }
        } else{
            /*if(sqlDialect.equals(SQLDialect.MYSQL)) {
                String sss = StringKit.findWithRegex(StringKit.cleanStringHTML(sQuery.toString()),
                        "(select)(\\s)+((\\'([A-Za-z0-9])+\\')(\\,\\s+)?)*(\\s+(FROM|from|From))");
                String s = StringKit.cleanStringHTML(sQuery.toString()).replace(sss, "");
                for (int i = 0; i < columns.length; i++) {
                    sss = sss.replace("'" + columns[i] + "'", "`" + columns[i] + "`");
                }
                return StringKit.toStringInline(sss +" "+ s);
            }*/
            return toStringInline(sQuery.toString());
        }
    }

    /**
     * Method to create a string query SQL for update operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param values arrays of object of the values of the columns.
     * @return string query SQL for the update operation.
     */
    public static String update(String nameTable,String[] columns,Object[] values){
        return update(nameTable, columns, values, false, null);
    }

    /**
     * Method to create a string query SQL for update operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param values arrays of object of the values of the columns.
     * @param preparedStatement if true print all value with the symbol "?".
     * @return string query SQL for the update operation.
     */
    public static String update(String nameTable,String[] columns,Object[] values, boolean preparedStatement){
       return update(nameTable, columns, values, preparedStatement, null);
    }

    /**
     * Method to create a string query SQL for update operation.
     * @param nameTable string name of the table.
     * @param columns arrays of string of the columns names.
     * @param values arrays of object of the values of the columns.
     * @param preparedStatement if true print all value with the symbol "?".
     * @param conditions list of jooq Condition for filter the result of the update operation.
     * @return string query SQL for the update operation.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static String update(String nameTable,String[] columns,Object[] values, boolean preparedStatement,List<Condition> conditions){
    	org.jooq.Table<Record> table = new org.jooq.impl.TableImpl<>(nameTable);
    	org.jooq.UpdateQuery<Record> uQuery = dslContext.updateQuery(table);
        for(int i=0; i < columns.length; i++){
        	org.jooq.Field<String> field = DSL.field(columns[i],String.class);
        	org.jooq.Field fv = createFieldValue(values[i]);
            uQuery.addValue(field, fv);
        }
        //uQuery.addFrom(table);
        if(conditions!=null && !conditions.isEmpty()) uQuery.addConditions(conditions);

        if(preparedStatement)return toStringInline(uQuery.getSQL());
        else return toStringInline(uQuery.toString());
    }

    /**
     * Method to create a string query SQL for update operation.
     * @param nameTable string name of the table.
     * @param preparedStatement if true print all value with the symbol "?".
     * @param conditions list of jooq Condition for filter the result of the update operation.
     * @return string query SQL for the delete operation.
     */
    public static String delete(String nameTable,boolean preparedStatement,List<Condition> conditions){
    	org.jooq.Table<Record> table = new org.jooq.impl.TableImpl<>(nameTable);
        org.jooq.DeleteQuery<Record> dQuery = dslContext.deleteQuery(table);
        if(conditions!=null && !conditions.isEmpty()) dQuery.addConditions(conditions);
        if(preparedStatement)return toStringInline(dQuery.getSQL());
        else return toStringInline(dQuery.toString());
    }

    /**
     * Method to create a JOOQ Table Object.
     * @param nameTable string name of the table to convert.
     * @return a JOOQ Table.
     */
    public static org.jooq.Table<Record> createTable(String nameTable){ return new TableImpl<>(nameTable);}

    /**
     * Method to create a object JOOQ Field.
     * @param value object to convert.
     * @return a object JOOQ Field.
     */
    public static org.jooq.Field<?> createFieldValue(Object value){
        if(value instanceof URL) return DSL.val(String.valueOf(value), String.class);
        if(value instanceof URI) return DSL.val(String.valueOf(value), String.class);
        if(value instanceof String) return DSL.val(String.valueOf(value), String.class);
        if(value instanceof Condition) return DSL.val((Condition) value);
        if(value instanceof Boolean) return DSL.val(value,Boolean.class);
        if(value instanceof Integer) return DSL.val(value,Integer.class);
        if(value instanceof Long) return DSL.val(value,Long.class);
        if(value instanceof Short) return DSL.val(value,Short.class);
        if(value instanceof Byte) return DSL.val(value,Byte.class);
        if(value instanceof Double) return DSL.val(value,Double.class);
        if(value instanceof Float) return DSL.val(value,Float.class);
        if(value instanceof Character) return DSL.val(value,Character.class);
        if(value instanceof Void) return DSL.val(value,Void.class);
        return DSL.val(value);
        //return DSL.field(StringKit.convertObjectToString(value));
    }

    /**
     * Method to create a object JOOQ Field.
     * @param value object to convert.
     * @return a object JOOQ Field.
     */
    @SuppressWarnings("rawtypes")
    public static org.jooq.Field createFieldValueCapture(Object value) {
         return createFieldValue(value);
    }

    /**
     * Method to create a object JOOQ Field.
     * @param value object to convert.
     * @param dataType the JOOQ Datatype.
     * @return a object JOOQ Field.
     */
    public static org.jooq.Field<?> createFieldValue(Object value,DataType<?> dataType){
        if(value instanceof URL) return DSL.val(String.valueOf(value), dataType);
        if(value instanceof URI) return DSL.val(String.valueOf(value), dataType);
        if(value instanceof String) return DSL.val(String.valueOf(value), dataType);
        //if(value instanceof Condition) return DSL.field((Condition) value);
        if(value instanceof Boolean) return DSL.val(value,dataType);
        if(value instanceof Integer) return DSL.val(value,dataType);
        if(value instanceof Long) return DSL.val(value,dataType);
        if(value instanceof Short) return DSL.val(value,dataType);
        if(value instanceof Byte) return DSL.val(value,dataType);
        if(value instanceof Double) return DSL.val(value,dataType);
        if(value instanceof Float) return DSL.val(value,dataType);
        if(value instanceof Character) return DSL.val(value,dataType);
        if(value instanceof Void) return DSL.val(value,dataType);
        return DSL.val(value);
        //return DSL.field(StringKit.convertObjectToString(value),dataType);
    }

    /**
     * Method to create a object JOOQ Field.
     * @param value object to convert.
     * @param dataType the JOOQ Datatype.
     * @return a object JOOQ Field.
     */
    @SuppressWarnings("rawtypes")
    public static org.jooq.Field createFieldValueCapture(Object value,DataType<?> dataType){
        return createFieldValue(value, dataType);
    }

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param clazzType class type of the object.
     * @return a JOOQ Field.
     */
    public static org.jooq.Field<?> createFieldValue(Object value,Class<?> clazzType){return DSL.val(value, clazzType); }

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param clazzType class type of the object.
     * @return a JOOQ Field.
     */
    @SuppressWarnings("rawtypes")
    public static org.jooq.Field createFieldValueCapture(Object value,Class<?> clazzType){return DSL.val(value, clazzType); }

    /**
     * Method to create a JOOQ Field of a object.
     * @param value object to convert.
     * @param sqlTypes java.sql.Type related to the object.
     * @return a JOOQ Field.
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static org.jooq.Field<?> createField(Object value,int sqlTypes){
        if(value == null || sqlTypes == Types.NULL) return null;
        return createFieldValue(value, createDataType(sqlTypes));
    }

    /**
     * Method to create a JOOQ Field of a object.
     * @param value object to convert.
     * @param sqlTypes java.sql.Type related to the object.
     * @return a JOOQ Field.
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static org.jooq.Field createFieldValueCapture(Object value,int sqlTypes){
        if(value == null || sqlTypes == Types.NULL) return null;
        return createFieldValue(value, createDataType(sqlTypes));
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param clazzType the class type of the datatype.
     * @param typeName the string of the SQL type of the datatype.
     * @return a JOOQ DataType
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(Class<?> clazzType,String typeName){
        return new DefaultDataType(sqlDialect,clazzType,typeName );
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param clazzType the class type of the datatype.
     * @return a JOOQ DataType
     */
    @SuppressWarnings("rawtypes")
    public static DataType createDataType(Class<?> clazzType){
        return DefaultDataType.getDataType(sqlDialect,clazzType);
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param sqlTypes java.sql.Type related to the object.
     * @return a JOOQ DataType
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(int sqlTypes){
        return new DefaultDataType(
                sqlDialect,
                convertSQLTypes2JavaClass(sqlTypes),
                convertSQLTypes2String(sqlTypes)
        );
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param clazzType the class type of the datatype.
     * @param typeName the string of the SQL type of the datatype.
     * @param sqlDialect the SQLDialect JOOQ.
     * @return a JOOQ DataType
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(Class<?> clazzType,String typeName,SQLDialect sqlDialect){
        return new DefaultDataType(sqlDialect,clazzType,typeName );
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param clazzType the class type of the datatype.
     * @param sqlDialect the SQLDialect JOOQ.
     * @return a JOOQ DataType
     */
    @SuppressWarnings("rawtypes")
    public static DataType createDataType(Class<?> clazzType,SQLDialect sqlDialect){
        return DefaultDataType.getDataType(sqlDialect,clazzType);
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param sqlTypes java.sql.Type related to the object.
     * @param sqlDialect the SQLDialect JOOQ.
     * @return a JOOQ DataType
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(int sqlTypes,SQLDialect sqlDialect){
        return new DefaultDataType(
                sqlDialect,
                convertSQLTypes2JavaClass(sqlTypes),
                convertSQLTypes2String(sqlTypes)
        );
    }
    
    /**
     * Method to create a new DSLContext.
     * @return the JOOQ DSLContext.
     */
    public static DSLContext createDSLContext(){
        if(connection==null){
            logger.error("No Connection is initialized for this operation, the Connection is NULL");
            return null;
        }
        if(sqlDialect==null){
            logger.error("No SQLDialect is initialized for this operation, the SQLDialect is NULL");
            return null;
        }
        setConnection(connection, sqlDialect);
        return dslContext;
    }

    /**
     * Method to create a new DSLContext.
     * @param connection the Connection SQL.
     * @param sqlDialect the SQLDialect JOOQ.
     * @return the JOOQ DSLContext.
     */
    public static DSLContext createDSLContext(Connection connection,SQLDialect sqlDialect){
        if(connection==null){
            logger.error("No Connection is initialized for this operation, the Connection is NULL");
            return null;
        }
        if(sqlDialect==null){
            logger.error("No SQLDialect is initialized for this operation, the SQLDialect is NULL");
            return null;
        }
        //setConnection(connection, sqlDialect);
        return  DSL.using(connection, sqlDialect);
    }

    /**
     * Method to convert a set of arrays to a specific map of JOOQ Field.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @param types Collection Array of java.sql.Types int, types of column.
     * @return a Map of JOOQ Field to JOOQ Field.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static Map<org.jooq.Field<String>,org.jooq.Field<Object>> convertArraysToMapJOOQField(
            String[] columns,Object[] values,int[] types){
    	org.jooq.Field<String>[] fields = new org.jooq.Field[columns.length];
    	org.jooq.Field<Object>[] fv = new org.jooq.Field[columns.length];
        for(int i=0; i < columns.length; i++){
            //return Field<String>
        	org.jooq.Field<String> field = DSL.field(columns[i],String.class);
            fields[i] = field;
            fv[i] = createFieldValueCapture(values[i], types[i]);
        }
        return toMap(fields, fv);
    }

    /**
     * Method to convert a set of arrays to a specific map of JOOQ Field.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @return a Map of JOOQ Field to JOOQ Field.
     */
    @SuppressWarnings("rawtypes")
    public static Map<org.jooq.Field,org.jooq.Field> convertArraysToMapJOOQField(
            String[] columns,Object[] values){
    	org.jooq.Field[] fields = new org.jooq.Field[columns.length];
        org.jooq.Field[] fv = new org.jooq.Field[columns.length];
        for(int i=0; i < columns.length; i++){
        	org.jooq.Field<String> field = DSL.val(columns[i]);
            fields[i] = field;
            fv[i] = createFieldValueCapture(values[i]);
        }
        return toMap(fields, fv);
    }

    /*
    public static Map<Field<String>, ?> convertArraysToMapJOOQField2(
            String[] columns, Object[] values){
        //Unorder list columns<->values
        Map<Field<String>, Object> map = new HashMap<>();
        Field[] fields = new Field[columns.length];
        Field[] fv = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            org.jooq.Field<String> field = createField(columns[i]);
            fields[i] = field;
            map.put(field,values[i]);
        }
        return map;
    }*/

    /**
     * Method to convert a set of arrays to a list of JOOQ Condition with AND condition.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @param types Collection Array of java.sql.Type values.
     * @return a List of JOOQ Condition with the AND
     */
    @SuppressWarnings("unchecked")
    public static List<org.jooq.Condition> convertToListConditionEqualsWithAND(String[] columns,Object[] values,int[] types){
        List<org.jooq.Condition> conds = new ArrayList<>();
        for(int i=0; i < columns.length; i++){
            if(values[i]==null) {
                conds.add(createFieldValue(columns[i]).isNull());
            }else {
                if(values[i] instanceof String) {
                    conds.add(createFieldValue(columns[i]).eq(createFieldValueCapture(values[i], types[i])));
                }
            }
        }
        return conds;
    }

    /**
     * Method to convert a set of arrays to a list of JOOQ Condition with AND condition.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @return a List of JOOQ Condition with the AND
     */
    @SuppressWarnings("unchecked")
    public static List<org.jooq.Condition> convertToListConditionEqualsWithAND(String[] columns,Object[] values) {
        List<org.jooq.Condition> conds = new ArrayList<>();
        if (columns != null && !isEmpty(columns)) {
            for (int i = 0; i < columns.length; i++) {
                if (values[i] == null) {
                    conds.add(createFieldValue(columns[i]).isNull());
                } else if(values[i] instanceof String){
                    conds.add(createFieldValue(columns[i]).eq(createFieldValueCapture(values[i])));
                /*} else if(values_where[i] instanceof Integer){
                    conds.add(createField(columns_where[i]).eq(createFieldValue(values_where[i])));*/
                } else {
                    conds.add(createFieldValue(columns[i]).eq((org.jooq.Field) createFieldValue(values[i])));
                }
            }
        }else {
            return null;
        }
        return conds;
    }

    /**
     * Method to convert a object array to a JOOQ Field array.
     * @param arrayObj the object array to convert.
     * @return the array of Field JOOQ converted.
     */
    @SuppressWarnings("rawtypes")
    public static org.jooq.Field<?>[] convertObjecyArrayToFieldValueArray(Object[] arrayObj){
    	org.jooq.Field[] fields = new org.jooq.Field[arrayObj.length];
        for(int i=0; i < arrayObj.length; i++){
            fields[i] = createFieldValue(arrayObj[i]);
        }
        return fields;
    }

    /**
     * Method to create a JOOQ Record.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @param nameTable string name of the table you use.
     * @return a JOOQ Record.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static Record createRecord(
            String[] columns,Object[] values,String nameTable){
        org.jooq.Field[] fields = new org.jooq.Field[columns.length];
        Map<String,Object> map = new HashMap<>();
        Record rec = new TableRecordImpl<>(new TableImpl(nameTable));
        for(int i=0; i < columns.length; i++){
            fields[i] = createFieldValue(columns[i]);
            if(values[i] == null){
                map.put(columns[i],null);
            }else {
                map.put(columns[i], values[i]);
            }
        }
        rec.into(fields).fromMap(map);
        return rec;
    }

    /**
     * Method to convert a JOOQ Field to a JOOQ DataType.
     * @param field the JOOQ Field to convert.
     * @param <T> the generic variable.
     * @return the JOOQ Datatype.
     */
    @SuppressWarnings("unchecked")
    public static <T> DataType<T> convertFieldToDataType(org.jooq.Field<T> field){
        return (DataType<T>) (field == null ? SQLDataType.OTHER : field.getDataType());
    }

    /**
     * Method to fetch the result of a query .
     * @param sql the String SQL.
     * @return the Result of the Query.
     */
    public static Result<Record> fetchQuery(String sql) {
        // Fetch results using jOOQ
        return dslContext.fetch(sql);
    }

    /**
     * Method to execute and fetch the result of a query .
     * @param sql the String SQL.
     * @return the Result of the Query.
     * @throws java.sql.SQLException if the query SQL is wrong.
     */
    public static Result<Record> executeAndFetchQuery(String sql) throws SQLException {
        // Or execute that SQL with JDBC, fetching the ResultSet with jOOQ:
        ResultSet rs = connProvider.acquire().createStatement().executeQuery(sql);
        return dslContext.fetch(rs);
    }

    /**
     * Method utility for get a connection with JOOQ.
     * @param host host where the server is.
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the DSLContext set with Connection.
     */
    public static DSLContext prepareDSLContext(String host,String port,String database,String username,String password,DBType dbType){
        connection = prepareConnection(host, port, database, username, password,dbType);
        connProvider= new DefaultConnectionProvider(connection);
        connProvider.acquire();
        sqlDialect = dbType.getJooqSqlDialect();
        dslContext = DSL.using(connection,SQLDialect.MYSQL);
        return dslContext;
    }

    //Method to support the integration with SpringFramework JDBC

    //http://stackoverflow.com/questions/4474365/jooq-and-spring/14243136#14243136
    /*public static convertSpringDataSourceToJooqFactory(DataSource dataSource){
        //your actual data source (optional) make access lazy
        final DataSource lazyDS = new LazyConnectionDataSourceProxy(dataSource);
        // make spring transactions available in plain jdbc context
        final DataSource txDS = new TransactionAwareDataSourceProxy(lazyDS);
        // create jOOQ factory
        org.jooq.impl.Factory jooq = new org.jooq.impl.Factory(txDS, *//* dialect *//*, *//* settings *//*);

    }*/
    
    
    //===================================================
    // SQL UTILITIES
    //===================================================

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
    public static Connection chooseAndGetConnection(
    		String dialectDB,String host,String port,String database,String username,String password){
        if(isNullOrEmpty(username) || isNullOrEmpty(password)){
            username = "root";
            password = "";
        }
        if(!isNullOrEmpty(port) || !isNumeric(port)) port = "";
        if(isNullOrEmpty(dialectDB)){
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
            case "mysql": return prepareConnection(host,port,database,username,password,DBType.MYSQL);
            case "postgres": return null;
            case "postgres93": return null;
            case "postgres94": return null;
            case "sqlite": return null;
            default: {logger.warn("No connection database type detected fro this type:"+dialectDB); return null;}
        }
    }

    /**
     * Method to get a connection.
     * @param host the {@link String} host where the server is.
     * @param port the {@link String} {}number of the port of the server.
     * @param database the {@link String} name of the database.
     * @param username the {@link String} username.
     * @param password the {@link String} password.
     * @return the {@link Connection}.
     */
    public static Connection prepareConnection(String host,String port,String database,String username,String password,DBType dbType) {
        return prepareConnection(host,port,database,username,password,false,false,false,dbType);
    }

    /**
     * Method to get a connection.
     * @param host the {@link String} host where the server is.
     * @param port the {@link String} {}number of the port of the server.
     * @param database the {@link String} name of the database.
     * @param username the {@link String} username.
     * @param password the {@link String} password.
     * @param verifyServerCertificate  the {@link boolean} if true refuse to connect if the host certificate cannot be verified
     * @param useSSL  the {@link boolean} if true connect using SSL
     * @param requireSSL  the {@link boolean} if true refuse to connect if the MySQL server does not support SSL
     * @return the {@link Connection}.
     */
    public static Connection prepareConnection(
                    String host,String port,String database,String username,String password,
                    boolean verifyServerCertificate,boolean useSSL,boolean requireSSL,DBType dbType) {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            invokeClassDriverForDbType(dbType);
            String url = dbType.getConnector() + host;
            if (port != null && isNumeric(port)) {
                url += ":" + port;
            }
            if(dbType == DBType.H2){
            	url += "/~/";
            }
            url += "/"  + database + "?noDatetimeStringSync=true" +
                    "&verifyServerCertificate="+String.valueOf(verifyServerCertificate)+
                    "&useSSL="+String.valueOf(useSSL)+
                    "&requireSSL="+String.valueOf(requireSSL)+""; //"jdbc:sql://localhost:3306/jdbctest"
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
     * Method to get a connection.
     * @param host string name of the host where is it the database
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection prepareConnection(String host,String database,String username,String password,DBType dbType) {
        return prepareConnection(host,null,database,username,password,dbType);
    }

    /**
     * Method to get a connection.
     * @param hostAndDatabase string name of the host where is it the database
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public static Connection prepareConnection( String hostAndDatabase,String username,String password,DBType dbType) {
        String[] split = hostAndDatabase.split("/");
        if(hostAndDatabase.startsWith("/")) hostAndDatabase = split[1];
        else hostAndDatabase = split[0];
        return prepareConnection(hostAndDatabase,null,split[split.length-1],username,password,dbType);
    }

    public static Connection prepareConnection(String fullUrl,DBType dbType) {
        //e.g. "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility"
        try {
            invokeClassDriverForDbType(dbType);
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                conn = DriverManager.getConnection(fullUrl);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.error("You forgot to turn on your " +dbType.name()+ " Server:" + e.getMessage(), e);
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
    
//    /**
//     * Method to get a Connection from a List to possible choice.
//     * @param dialectDB the String of the dialectDb.
//     * @param host String name of the host where is the server.
//     * @param port String number of the port where the server communicate.
//     * @param database string name of the database.
//     * @param username string username.
//     * @param password string password.
//     * @return the connection.
//     */
//    public static Connection chooseAndGetConnection(String dialectDB,
//                                String host,String port,String database,String username,String password){
//        if(StringUtilities.isNullOrEmpty(username) || StringUtilities.isNullOrEmpty(password)){
//            username = "root";
//            password = "";
//        }
//        if(!StringUtilities.isNullOrEmpty(port) || !StringUtilities.isNumeric(port)) port = "";
//        if(StringUtilities.isNullOrEmpty(dialectDB)){
//            logger.warn("No connection database type detected fro this type;"+dialectDB);
//            return null;
//        }else dialectDB = SQLConverter.convertDialectDatabaseToTypeNameId(dialectDB);
//        switch (dialectDB) {
//            case "cubrid": return null;
//            case "derby": return null;
//            case "firebird": return null;
//            case "h2": return null;
//            case "hsqldb": return null;
//            case "mariadb": return null;
//            case "mysql": return getMySqlConnection(host,port,database,username,password);
//            case "postgres": return null;
//            case "postgres93": return null;
//            case "postgres94": return null;
//            case "sqlite": return null;
//            default: {logger.warn("No connection database type detected fro this type:"+dialectDB); return null;}
//        }
//    }
//
//    /**
//     * Method to get a HSQL connection.
//     * @param host String name of the host where is the server.
//     * @param port String number of the port where the server communicate.
//     * @param database string name of the database.
//     * @param username string username.
//     * @param password string password.
//     * @return the connection.
//     */
//    public static Connection getHSQLDBConnection(String host,String port,String database,String username,String password) {
//        // The newInstance() call is a work around for some broken Java implementations
//        try {
//            invokeClassDriverForDbType(SQLEnum.DBType.HSQLDB);
//            String url = SQLEnum.DBConnector.HSQLDB.getConnector() + host;
//            if (port != null && StringUtilities.isNumeric(port)) {
//                url += ":" + port; //jdbc:hsqldb:data/database
//            }
//            url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
//            conn = DriverManager.getConnection(url, username, password);
//        }catch (InstantiationException e) {
//            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
//        }catch(IllegalAccessException e){
//            logger.error("Access problem while loading!:"+e.getMessage(),e);
//        } catch(ClassNotFoundException e){
//            logger.error("Unable to load driver class!:"+e.getMessage(),e);
//        }catch (SQLException e) {
//            logger.error("The URL is not correct:" + e.getMessage(), e);
//        }
//        return conn;
//    }
//
//    /**
//     * Method to get a MySQL connection.
//     * @param host the {@link String} host where the server is.
//     * @param port the {@link String} {}number of the port of the server.
//     * @param database the {@link String} name of the database.
//     * @param username the {@link String} username.
//     * @param password the {@link String} password.
//     * @return the {@link Connection}.
//     */
//    public static Connection getMySqlConnection(
//            String host,String port,String database,String username,String password) {
//        return getMySqlConnection(
//                host,port,database,username,password,false,false,false);
//    }
//
//    /**
//     * Method to get a MySQL connection.
//     * @param host the {@link String} host where the server is.
//     * @param port the {@link String} {}number of the port of the server.
//     * @param database the {@link String} name of the database.
//     * @param username the {@link String} username.
//     * @param password the {@link String} password.
//     * @param verifyServerCertificate  the {@link boolean} if true refuse to connect if the host certificate cannot be verified
//     * @param useSSL  the {@link boolean} if true connect using SSL
//     * @param requireSSL  the {@link boolean} if true refuse to connect if the MySQL server does not support SSL
//     * @return the {@link Connection}.
//     */
//    public static Connection getMySqlConnection(
//                    String host,String port,String database,String username,String password,
//                    boolean verifyServerCertificate,boolean useSSL,boolean requireSSL) {
//        // The newInstance() call is a work around for some broken Java implementations
//        try {
//            invokeClassDriverForDbType(SQLEnum.DBType.MYSQL);
//            String url = SQLEnum.DBConnector.MYSQL.getConnector() + host;
//            if (port != null && StringUtilities.isNumeric(port)) {
//                url += ":" + port;
//            }
//            url += "/"  + database + "?noDatetimeStringSync=true" +
//                    "&verifyServerCertificate="+String.valueOf(verifyServerCertificate)+
//                    "&useSSL="+String.valueOf(useSSL)+
//                    "&requireSSL="+String.valueOf(requireSSL)+""; //"jdbc:sql://localhost:3306/jdbctest"
//            try {
//                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
//                conn = DriverManager.getConnection(url, username, password);
//            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
//                logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
//            } catch (SQLException e) {
//                logger.error("The URL is not correct:" + e.getMessage(), e);
//            }
//        }catch (InstantiationException e) {
//            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
//        }catch(IllegalAccessException e){
//            logger.error("Access problem while loading!:"+e.getMessage(),e);
//        } catch(ClassNotFoundException e){
//            logger.error("Unable to load driver class!:"+e.getMessage(),e);
//        }
//        return conn;
//    }
//
//    /**
//     * Method to get a MySQL connection.
//     * @param host string name of the host where is it the database
//     * @param database string name of the database.
//     * @param username string username.
//     * @param password string password.
//     * @return the connection.
//     */
//    public static Connection getMySqlConnection(
//            String host,String database,String username,String password) {
//        return getMySqlConnection(host,null,database,username,password);
//    }
//
//    /**
//     * Method to get a MySQL connection.
//     * @param hostAndDatabase string name of the host where is it the database
//     * @param username string username.
//     * @param password string password.
//     * @return the connection.
//     */
//    public static Connection getMySqlConnection( String hostAndDatabase,String username,String password) {
//        String[] split = hostAndDatabase.split("/");
//        if(hostAndDatabase.startsWith("/")) hostAndDatabase = split[1];
//        else hostAndDatabase = split[0];
//        return getMySqlConnection(hostAndDatabase,null,split[split.length-1],username,password);
//    }
//
//    public static Connection getMySqlConnection(String fullUrl) {
//        //e.g. "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility"
//        try {
//            invokeClassDriverForDbType(SQLEnum.DBType.MYSQL);
//            try {
//                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
//                conn = DriverManager.getConnection(fullUrl);
//            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
//                logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
//            } catch (SQLException e) {
//                logger.error("The URL is not correct" + e.getMessage(), e);
//            }
//        }catch (InstantiationException e) {
//            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
//        }catch(IllegalAccessException e){
//            logger.error("Access problem while loading!:"+e.getMessage(),e);
//        } catch(ClassNotFoundException e){
//            logger.error("Unable to load driver class!:"+e.getMessage(),e);
//        }
//        return conn;
//    }
//
//    /**
//     * Method to get a Oracle connection.
//     * @param host string name of the host where is it the database
//     * @param port number of the port of the server.
//     * @param database string name of the database.
//     * @param username string username.
//     * @param password string password.
//     * @return the connection.
//     */
//    public static Connection getOracleConnection(String host,String port,String database,String username,String password){
//        try {
//            invokeClassDriverForDbType(SQLEnum.DBType.ORACLE);
//            //String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
//            String url = SQLEnum.DBConnector.ORACLE.getConnector() + host;
//            if (port != null && StringUtilities.isNumeric(port)) {
//                url += ":" + port;
//            }
//            url += "/" + database; //"jdbc:sql://localhost:3306/jdbctest"
//            conn = DriverManager.getConnection(url, username, password);
//        } catch(ClassNotFoundException|IllegalAccessException|InstantiationException e){
//            logger.error("Unable to load driver class!:"+e.getMessage(),e);
//        } catch (SQLException e) {
//            logger.error("The URL is not correct:" + e.getMessage(), e);
//        }
//        return conn;
//    }
//
//   /* private static Connection getMySqlConnection2(String fullUrl){
//        //jdbc:mysql://localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
//        //localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
//        if(fullUrl.toLowerCase().contains("jdbc:mysql://")) fullUrl = fullUrl.replace("jdbc:mysql://","");
//        String[] split = fullUrl.split("\\?");
//        String hostAndDatabase = split[0];//localhost:3306/geodb
//        Pattern pat = Pattern.compile("(\\&|\\?)?(user|username)(\\=)(.*?)(\\&|\\?)?", Pattern.CASE_INSENSITIVE);
//        String username = StringUtilities.findWithRegex(fullUrl, pat);
//        if(Objects.equals(username, "?")) username = "root";
//        pat = Pattern.compile("(\\&|\\?)?(pass|password)(\\=)(.*?)(\\&|\\?)?", Pattern.CASE_INSENSITIVE);
//        String password = StringUtilities.findWithRegex(fullUrl, pat);
//        if(Objects.equals(password, "?")) password ="";
//        split = hostAndDatabase.split("/");
//        String database = split[split.length-1];
//        hostAndDatabase = hostAndDatabase.replace(database,"");
//        pat = Pattern.compile("([0-9])+", Pattern.CASE_INSENSITIVE);
//        String port = StringUtilities.findWithRegex(hostAndDatabase, pat);
//        if(Objects.equals(port, "?")) port = null;
//        else  hostAndDatabase = hostAndDatabase.replace(port, "").replace(":","").replace("/","");
//        return getMySqlConnection(hostAndDatabase,port,database,username,password);
//    }*/
//
//    /**
//     * Method to connect to a h2  database.
//     * href: http://www.h2database.com/html/features.html.
//     * @param host string name of the host where is it the database.
//     * @param port number of the port of the server.
//     * @param database string name of the database.
//     * @param username string username.
//     * @param password string password.
//     * @return the Connection to the H2 database.
//     */
//    public static Connection getH2RemoteConnection(
//            String host,String port,String database,String username,String password) {
//        try {
//            invokeClassDriverForDbType(SQLEnum.DBType.H2);
//            /*
//            jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName>
//            jdbc:h2:tcp://localhost/~/test
//            jdbc:h2:tcp://dbserv:8084/~/sample
//            jdbc:h2:tcp://localhost/mem:test
//            */
//            String url = SQLEnum.DBConnector.H2.getConnector() + host;
//            if (port != null && StringUtilities.isNumeric(port)) {
//                url += ":" + port;
//            }
//            url += "/~/" + database;
//            conn = DriverManager.getConnection(url, username, password);
//        }catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {
//            logger.error("Unable to load driver class!:" + e.getMessage(), e);
//        } catch (SQLException e) {
//            logger.error("The URL is not correct:" + e.getMessage(), e);
//        }
//        return conn;
//    }

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

    public static List<String> getColumnsName(Connection conn,String tablename) throws SQLException {
        conn.setAutoCommit(false);
        List<String> columnsName = new ArrayList<>();
        //stmt.setFetchSize(DATABASE_TABLE_FETCH_SIZE);
        try (Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {
            //stmt.setFetchSize(DATABASE_TABLE_FETCH_SIZE);
            try (ResultSet r = statement.executeQuery("Select * FROM " + tablename)) {
                ResultSetMetaData meta = r.getMetaData();
                // Get the column names
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    columnsName.add(meta.getColumnName(i));
                }
            }
        }
        return columnsName;
    }

    public static String getColumnsNameLikeString(Connection conn,String tablename) throws SQLException {
        List<String> headers = getColumnsName(conn,tablename);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < headers.size(); i++){
            sb.append("'").append(headers.get(i)).append("'");
            if(i < headers.size()-1) sb.append(",");
        }
        return sb.toString();
    }

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
        if(getRowCount(rs)==0)rs = md.getTables(null, null, "%", null);
        if(getRowCount(rs)==0) throw new SQLException("Can't get the Tables from the current connection!!!");
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
            tableNames.put(tableName,(String[])array.toArray());
            array.clear();
        }
        return tableNames;
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
        //if the String query is a path to a File batch
        if(sql.contains(File.separator) || sql.endsWith(".sql")){ //Reference path
             if(isFileValid(sql)){
                 executeSQL(new File(sql),conn);
                 logger.info("Exeute the File SQL:"+new File(sql).getAbsolutePath());
                 return null;
             }
        }
        // create the java statement
        if(stmt == null) {
            stmt = conn.createStatement();
            SQL2Utilities.stmt = stmt;
        }
        //if are multiple statements
        if(sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
        if(sql.split(";").length > 1){
            for(String singleQuery : sql.split(";")){
                if(singleQuery.toLowerCase().startsWith("select")){
                    logger.warn("You execute a SELECT query:"+singleQuery+" with the 'executeBatch' ," +
                            " in this case we can't return a resultSet");
                }
                if(singleQuery.toLowerCase().startsWith("show")){
                    logger.warn("You execute a SHOW query:"+singleQuery+" with the 'executeBatch' ," +
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
                        try {
                            stmt.execute(sql);
                            logger.info("Execute the Query SQL:" + sql);
                        }catch(SQLException e2){
                            try {
                                stmt.executeQuery(sql);
                                logger.info("Execute the Query SQL:" + sql);
                            }catch(Exception e3){
                                throw new SQLException(e3);
                            }
                        }
                        return null;
                    }catch(Exception e42){
                        //logger.error(e42.getMessage(),e42);
                        throw new SQLException(e42);
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

    public static String getDatabaseName(Connection conn){
        try {
            return conn.getCatalog();
        } catch (SQLException e) {
            try {
                return conn.getSchema();
            } catch (SQLException e1) {
                logger.error(e.getMessage(),e);
                return "N/A";
            }
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
            Timer timer = new Timer();
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
     * Method to get the execution time of a SQL query with the Java API.
     * @param sql the String SQL Query.
     * @param conn the Connection to the Database where execute the query.
     * @return the Long value of the time for execute the query.
     */
    
    public static Long getExecutionTime(String sql,Connection conn,String providerSql){
        switch(providerSql){
        	case "mysql":{
        		   preparePerformanceSchema(conn);
        	        String duration = "0";
        	        try {
        	            executeSQL(sql,conn);
        	            if(sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
        	            ResultSet resultSet;
        	            try{
        	                //sql = sql.replaceAll("''","''''");
        	                resultSet = executeSQL(
        	                        "SELECT EVENT_ID, TRUNCATE(TIMER_WAIT/1000000000000,6) as Duration, SQL_TEXT\n" +
        	                        "FROM performance_schema.events_statements_history_long WHERE SQL_TEXT like\n '%"+
        	                         sql+"%'");
        	            }catch(Exception e){
        	                sql = sql.replaceAll("''","''''");
        	                resultSet = executeSQL(
        	                        "SELECT EVENT_ID, TRUNCATE(TIMER_WAIT/1000000000000,6) as Duration, SQL_TEXT\n" +
        	                                "FROM performance_schema.events_statements_history_long WHERE SQL_TEXT like\n '%"+
        	                                sql+"%'");
        	            }
        	           /* ResultSet resultSet = SQLUtilities.executeSQL(
        	                    "SELECT EVENT_ID, TRUNCATE(TIMER_WAIT/1000000000000,6) as Duration, \n" +
        	                            "FROM performance_schema.events_statements_history_long"
        	            );*/
        	            //noinspection LoopStatementThatDoesntLoop
        	            if(resultSet.getFetchSize()==0){
        	                resultSet = executeSQL(
        	                        "SELECT EVENT_ID, TRUNCATE(TIMER_WAIT/1000000000000,6) as Duration, SQL_TEXT\n" +
        	                                "FROM performance_schema.events_statements_history_long");
        	            }

        	            //logger.info("Size:"+resultSet.getFetchSize());
        	            while(resultSet.next()) {
        	                String sql_text2 = resultSet.getString("SQL_TEXT");
        	                //logger.info(sql_text2);
        	                if(sql !=null && sql_text2  != null &&  sql.contains(sql_text2)){
        	                    duration = resultSet.getString("Duration");
        	                    //if(!StringUtilities.isNullOrEmpty(duration)) break;
        	                }
        	            }
        	            long calculate = 0L;
        	            if(!StringUtils.isBlank(duration)) calculate = Math.round((Double.parseDouble(duration)*1000));
        	            logger.info("Query MYSQL result(s) in "+calculate+"ms.");
        	            return calculate;
        	        } catch(java.lang.NumberFormatException e){
        	            logger.error("The duration String is:'"+duration+"' -> "+e.getMessage(),e);
        	            return 0L;
        	        }catch (SQLException e) {
        	            logger.error("Can't execute the query:"+sql+" -> "+e.getMessage(),e);
        	            return 0L;
        	        }      	
    	        }
        	default:{
    		  Long calculate;
    	        try {
    	            stmt = conn.createStatement();
    	            Timer timer = new Timer();
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
        }
      
    }
    
    /**
     * Method to prepare the server MySQL  to use the performance utility.
     * @param conn the Connection to the MySQL server.
     * @return if true all the operation all corrected done.
     */
    private static boolean preparePerformanceSchema(Connection conn){
        try {
            //Ensure that statement and stage instrumentation is enabled by updating the
            //setup_instruments table. Some instruments may already be enabled by default.
            executeSQL(
                    "UPDATE performance_schema.setup_instruments SET ENABLED = 'YES', TIMED = 'YES'\n" +
                            "WHERE NAME LIKE '%statement/%';\n" +
                    "UPDATE performance_schema.setup_instruments SET ENABLED = 'YES', TIMED = 'YES'\n" +
                    "WHERE NAME LIKE '%stage/%';",conn
            );
            //Ensure that events_statements_* and events_stages_* consumers are enabled.
            //Some consumers may already be enabled by default.
            executeSQL(
                    "UPDATE performance_schema.setup_consumers SET ENABLED = 'YES'\n" +
                            "WHERE NAME LIKE '%events_statements_%';\n" +
                    "UPDATE performance_schema.setup_consumers SET ENABLED = 'YES'\n" +
                            "WHERE NAME LIKE '%events_stages_%';",conn
            );
            return true;
        }catch(SQLException e){
            logger.error(e.getMessage(),e);
            return false;
        }
    }


    /*
     * Method to import a file CSV to a Database.
     * @param fileCSV the File CSv to import.
     * @param firstLine if true the first line of the file CSV contains the headers of the fields.
     * @param separator the Cgaracter of the separator field on the CSV file.
     * @param nameTable the String name of the table.
     * @param connection the Connection to the Database where execute the query.
     * @return if true all the operation are done.
     */
    /*
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
                        types[i] = convertStringToSQLTypes(values[i]);
                    }
                    insertQuery = JOOQUtilities.insert(nameTable, columns,values,types);
                    executeSQL(insertQuery,connection);
                }
            }
            logger.info("Data CSV File Successfully Uploaded");
            return true;
        } catch (SQLException |IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }
	*/
    
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

    public static int getRowCount(ResultSet resultSet) {
        try {
            resultSet.last();
            int rows = resultSet.getRow();
            resultSet.beforeFirst();
            return rows;
        }catch (SQLException e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    public static String getURL(DatabaseMetaData databaseMetadata){
        try {
            return databaseMetadata.getURL();
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return "";
        }
    }

    public static void invokeClassDriverForDbType(DBType dbType)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        switch(dbType.name()){
            case "MYSQL": {
                try {
                    invokeClassDriver(dbType.getDriver()); //load driver//"com.sql.jdbc.Driver"
                    break;
                } catch (ClassNotFoundException e) {
                    invokeClassDriver(dbType.getDriver());
                    break;
                }
            }
            case "ORACLE":{
                invokeClassDriver(dbType.getDriver());//"oracle.jdbc.driver.OracleDriver"
                break;
            }
            case "H2":{
                invokeClassDriver(dbType.getDriver()); //"org.h2.Driver"
                break;
            }
            case "HSQLDB":{
                invokeClassDriver(dbType.getDriver());//"org.hsqldb.jdbcDriver"
                break;
            }
        }
    }

    public static void invokeClassDriver(String driverClassName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(driverClassName).newInstance(); //load driver//"com.sql.jdbc.Driver"
    }

    public static void invokeClassDriverForDbDriver(DBType driverClassName)
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
    public static Boolean exportData(Connection conn,String fileOutput,String tableName) {
        return exportData(conn,fileOutput,tableName,true);
    }

    public static Boolean exportData(Connection conn,String fileOutput,String tableName,boolean witHeader) {
        Statement stmt;
        String query;
        try {
            //ResultSet.CONCUR_READ_ONLY
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            //For comma separated file
            fileOutput = fileOutput.replace("\\","\\\\");
            if(witHeader) {
                /*http://stackoverflow.com/questions/5941809/include-headers-when-using-select-into-outfile*/
                String headers = getColumnsNameLikeString(conn,tableName);
                String headers2 = headers.replace("'","");
                query = "SELECT " + headers + "\n" +
                        "UNION ALL " +
                        "SELECT " + headers2 + " FROM " + tableName + " INTO OUTFILE \"" + fileOutput + "\" FIELDS TERMINATED BY ',' " +
                        "ENCLOSED BY '\"'  LINES TERMINATED BY '" + System.getProperty("line.separator") + "';";
            }else{
                query = "SELECT * FROM " + tableName + " INTO OUTFILE \"" + fileOutput + "\" FIELDS TERMINATED BY ',' " +
                        "ENCLOSED BY '\"'  LINES TERMINATED BY '" + System.getProperty("line.separator") + "';";
            }
            try {
                executeSQL(query,conn,stmt);
            }catch(SQLException e){
                if(e.getMessage().contains("is running with the --secure-file-priv option")){
                    stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
                    query = "SHOW VARIABLES LIKE 'secure_file_priv'";
                    //SELECT @@secure_file_priv;
                    ResultSet rs2 = executeSQL(query);
                    if(getRowCount(rs2) == 0)throw new SQLException(e);
                    /*https://coderwall.com/p/609ppa/printing-the-result-of-resultset*/
                    Map<String,String> map =  getInfoResultSet(rs2,true);
                    String privFileDir = map.get("VARIABLE_VALUE");
                    privFileDir =  privFileDir + getFilename(fileOutput);
                    privFileDir = privFileDir.replace("\\","\\\\");
                    if(isFileExists(privFileDir)) delete(privFileDir);
                    if(witHeader) {
                        /*http://stackoverflow.com/questions/5941809/include-headers-when-using-select-into-outfile*/
                        String headers = getColumnsNameLikeString(conn,tableName);
                        String headers2 = headers.replace("'","");
                        query = "SELECT " + headers + "\n" +
                                "UNION ALL " +
                                "SELECT " + headers2 + " FROM " + tableName + " INTO OUTFILE \"" + privFileDir + "\" FIELDS TERMINATED BY ',' " +
                                "ENCLOSED BY '\"'  LINES TERMINATED BY '" + System.getProperty("line.separator") + "';";
                    }else {
                        query = "SELECT * FROM " + tableName + " INTO OUTFILE \"" + privFileDir + "\" FIELDS TERMINATED BY ',' " +
                                "ENCLOSED BY '\"'  LINES TERMINATED BY '" + System.getProperty("line.separator") + "';";
                    }
                    executeSQL(query,conn,stmt);
                    if(!copy(privFileDir,fileOutput))throw new SQLException(e);
                    delete(privFileDir);
                }else{
                    throw new SQLException(e);
                }
            }
            return true;
        } catch(Exception e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public static Map<String,String> getInfoResultSet(ResultSet rs,boolean showOnConsole) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        Map<String,String> map = new LinkedHashMap<>();
        if(showOnConsole)System.out.println("SHOW ResultSet Information)");
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(";");
                String columnValue = rs.getString(i);
                if(showOnConsole)System.out.print("["+i+"]Column:"+ rsmd.getColumnName(i) + " ,Value:" + columnValue );
                map.put(rsmd.getColumnName(i),columnValue);
            }
            if(showOnConsole)System.out.println("");
        }
        return map;
    }

    /**
     * @throws IOException 
     * @deprecated
     */
    public static Boolean importData(File file,char delimiter,String databaseName,String tableName) throws IOException{
        return importData(conn,file,delimiter,databaseName,tableName);
    }
    
    /**
     * @throws IOException 
     * @deprecated
     */
    public static Boolean importData(Connection conn,File file,char delimiter,String databaseName,String tableName) throws IOException{
        Statement stmt;
        String query = "";
        try {
            if(!file.exists()) return false;
            //Map<String,String[]> map = getTableAndColumn(conn,databaseName);
            //String[] columns = map.get(tableName);
            String[] columns = CsvUtilities.getHeadersWithUnivocity(file,true);
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String filePath = file.getAbsolutePath().replace("\\","\\\\");
            //NOT WORK
//           query = "LOAD DATA INFILE '"+file+"' INTO TABLE "+tableName+" "+
//                    "FIELDS TERMINATED BY ',' " +
//                    "LINES TERMINATED BY '\\r' " +
//                    "IGNORE 1 LINES " +
//                    "("+ toString(columns,',')+");";
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");
            query = "LOAD DATA INFILE '"+filePath+"' INTO TABLE "+databaseName+"."+tableName+" "+
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\r\n'"+
                    "IGNORE 1 LINES " +
                    "("+ toString(columns,delimiter)+");";
            try {
                stmt.executeUpdate(query);
            }catch(SQLException e){
                if(e.getMessage().contains("is running with the --secure-file-priv option")){
                    query = "LOAD DATA LOCAL INFILE '"+filePath+"' INTO TABLE "+databaseName+"."+tableName+" "+
                            "FIELDS TERMINATED BY ',' " +
                            "LINES TERMINATED BY '\r\n'"+
                            "IGNORE 1 LINES " +
                            "("+ toString(columns,delimiter)+");";
                    stmt.executeUpdate(query);
                }else{
                    if(e.getMessage().contains("is running with the --secure-file-priv option")){
                        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
                        query = "SHOW VARIABLES LIKE 'secure_file_priv'";
                        //SELECT @@secure_file_priv;
                        ResultSet rs2 = executeSQL(query);
                        if(getRowCount(rs2) == 0)throw new SQLException(e);
                        //https://coderwall.com/p/609ppa/printing-the-result-of-resultset
                        Map<String,String> map =  getInfoResultSet(rs2,true);
                        String privFileDirS = map.get("VARIABLE_VALUE");
                        File  privFileDir =  new File(privFileDirS + file.getName());
                        FileUtils.copyFileToDirectory(file,privFileDir);

                        filePath = privFileDir.getAbsolutePath().replace("\\","\\\\");
                        if(privFileDir.exists()) FileUtils.deleteQuietly(privFileDir);
                        query = "LOAD DATA INFILE '"+filePath+"' INTO TABLE "+databaseName+"."+tableName+" "+
                                "FIELDS TERMINATED BY ',' " +
                                "LINES TERMINATED BY '\r\n'"+
                                "IGNORE 1 LINES " +
                                "("+ StringUtils.join(columns,delimiter)+");";
                        executeSQL(query,conn,stmt);
                    }else{
                        throw new SQLException(e);
                    }
                }
            }
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1;");
            logger.info("Execute the Query SQL:"+query);
            return true;
        }catch(SQLException e) {
            logger.error("Can't execute query:"+query,e);
            return false;
        }
    }
   
    /**
     * @deprecated
     */
    public static Boolean cleanSQLScriptForOldVersion(File sqlScript){
        try {
            List<String> lines = FileUtils.readLines(sqlScript);
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
            FileUtils.writeLines(new File(FilenameUtils.getBaseName(sqlScript.getAbsolutePath())+TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis())+FilenameUtils.getExtension(sqlScript.getAbsolutePath())),newLines);
        }catch(Exception e){
            return false;
        }
        return true;
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
    
    //===================================================
    // SQL CONVERTER
    //===================================================
    
    /**
     * Method for get a mapt with all SQL java types.
     * href: http://www.java2s.com/Code/Java/Database-SQL-JDBC/convertingajavasqlTypesintegervalueintoaprintablename.htm.
     * @param jdbcType code int of the type sql.
     * @return map of SQL Types with name
     */
    public static Map<Integer,String> convertIntToJdbcTypeName(int jdbcType) {
        Map<Integer,String> map = new HashMap<>();
        // Get all field in java.sql.Types
        java.lang.reflect.Field[] fields = java.sql.Types.class.getFields();
        for (java.lang.reflect.Field field : fields) {
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
        if(isFloat(value)) return Types.FLOAT;
        if(isDouble(value)) return Types.DOUBLE;
        if(isDecimal(value)) return Types.DECIMAL;
        if(isInt(value)) return Types.INTEGER;
        if(isURL(value)) return Types.VARCHAR;
        if(isNumeric(value)) return Types.NUMERIC;
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
    /*
    public static org.apache.jena.datatypes.xsd.XSDDatatype convertSQLTypesToXDDTypes(int type){
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
    */
    
    
    //==================================================
    // ENUMERATOR
    //================================================== 
    public enum DBType{
    	MYSQL {
			@Override
			public String getDriver() {
				return "com.mysql.jdbc.Driver";			
				//return "org.gjt.mm.mysql.Driver";
			}

			@Override
			public String getConnector() {				
				return "jdbc:mysql://";
			}

			@Override
			public org.jooq.SQLDialect getJooqSqlDialect() {			
				return org.jooq.SQLDialect.MYSQL;
			}
		},
    	H2 {
			@Override
			public String getDriver() {
				return "org.h2.Driver";
			}

			@Override
			public String getConnector() {
				return "jdbc:h2:tcp://";
			}

			@Override
			public SQLDialect getJooqSqlDialect() {				
				return org.jooq.SQLDialect.H2;
			}
		},
    	ORACLE {
			@Override
			public String getDriver() {			
				return "oracle.jdbc.driver.OracleDriver";
			}

			@Override
			public String getConnector() {			
				return "jdbc:oracle:thin:@";
			}

			@Override
			public SQLDialect getJooqSqlDialect() {			
				//return org.jooq.SQLDialect.ORACLE; //only professional mode
				return null;
			}
		},
    	HSQLDB {
			@Override
			public String getDriver() {
				return "org.hsqldb.jdbcDriver";
			}

			@Override
			public String getConnector() {
				return "jdbc:hsqldb:hsql://";
			}

			@Override
			public SQLDialect getJooqSqlDialect() {				
				return org.jooq.SQLDialect.HSQLDB;
			}
		},
//    	SQL {
//			@Override
//			public String getDriver() {
//				return null;
//			}
//
//			@Override
//			public String getConnector() {
//				return null;
//			}
//
//			@Override
//			public SQLDialect getJooqSqlDialect() {			
//				return org.jooq.SQLDialect.SQL99;
//			}
//		},
//    	DB2 {
//			@Override
//			public String getDriver() {
//				return null;
//			}
//
//			@Override
//			public String getConnector() {
//				return null;
//			}
//
//			@Override
//			public SQLDialect getJooqSqlDialect() {				
//				return org.jooq.SQLDialect.;
//			}
//		},   
    	MARIADB {
			@Override
			public String getDriver() {
				return null;
			}

			@Override
			public String getConnector() {
				return null;
			}

			@Override
			public SQLDialect getJooqSqlDialect() {				
				return org.jooq.SQLDialect.MARIADB;
			}
		};
    	
    	public abstract org.jooq.SQLDialect getJooqSqlDialect();
    	public abstract String getDriver();
    	public abstract String getConnector();
    }
    
    //===================================================
    // JOOQ SUPPORT
    //===================================================
    /**
     * Method utility convert a insert query of JOOQ in a insert query for springframework jdbc.
     * @param queryString the String of the query JOOQ.
     * @param columns the Array of String with names of columns.
     * @return the String of the Query SpringFramework JDBC.
     */
    public static String getQueryInsertValuesParam(String queryString, String[] columns){
        String preQuery = findWithRegex(queryString, MANAGE_SQL_PREQUERY_INSERT);
        if(preQuery != null) {
            String postQuery = queryString.replace(preQuery, "");
            if (isMatch(postQuery, MANAGE_SQL_QUERY_INSERT_CHECK_WHERE)) {
                String[] val = postQuery.split(MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_3.pattern());
                if (val.length > 2) {
                    String postQuery0 = val[0].replace("(", "").replace(")", "");
                    String postQuery1 = val[1].replace("(", "").replace(")", "");
                }
            } else {
                //queryString = queryString.replace(preQuery, "");
                postQuery = "";
            }
            preQuery = findWithRegex(preQuery, MANAGE_SQL_QUERY_INSERT_GET_VALUES_PARAM_2v2).trim();
            //values = values.substring(0, values.length() - 1);
            //String[] param = values.split(",");
            //for(String s: param)values = values.replace(s.trim(),"?");
            String[] array = createSingleton("?", columns.length);
            String values = toString(array);
            //return queryString + " values (" + values +")" + supportQuery;
            return preQuery + toString(columns)
                    + ") values (" + values + ")" + postQuery;
        }else{
            return null;
        }
    }

    /**
     * Method utility convert a insert query of JOOQ in a insert query for springframework jdbc.
     * @param queryString the String of the query JOOQ.
     * @return the String of the Query SpringFramework JDBC.
     */
    public static String getQueryInsertWhereParam(String queryString){
        String values = findWithRegex(queryString,MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_1);
        String supportQuery = queryString.replace(values, "");
        if (supportQuery.toLowerCase().contains(" order by ")){
            String[] val = queryString.split(values);
            queryString = val[0];
            supportQuery = val[1];
        }else {
            queryString = queryString.replace(values, "");
            supportQuery = "";
        }
        values = values.replace(findWithRegex(values,MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_2),"");
        values = values.substring(0,values.length()-1);
        String[] paramCond = values.split("(and|or)");
        for(String s: paramCond){
            String[] paramWhere = s.split("(is|=|>=)");
            values = values.replace(paramWhere[1].trim(), "?");
        }
        return queryString + " where (" + values +")" + supportQuery;
    }

    //-------------------------------------------------------------------------------
    //Utility for JOOQSupport
    //-------------------------------------------------------------------------------
    public static final Pattern MANAGE_SQL_QUERY_GET_VALUES_PARAM_1
            = Pattern.compile("(values)\\s*(\\(|\\{)\\s*(.*?)\\s*(\\)|\\})+",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_PREQUERY_INSERT
            = Pattern.compile("(insert into)(\\s*(.*?)\\s*)\\s*(\\(|\\{)\\s*(.*?)\\s*(\\)|\\})+",Pattern.CASE_INSENSITIVE);
    /*public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_VALUES_PARAM_2
            = Pattern.compile("(values)\\s*\\(",Pattern.CASE_INSENSITIVE);*/
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_VALUES_PARAM_2v2
            = Pattern.compile("(insert into)\\s*(.*?)\\(\\s*",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_1
            = Pattern.compile("(where)\\s*(\\(|\\{)\\s*(.*?)\\s*(\\)|\\})+",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_2
            = Pattern.compile("(where)\\s*\\(",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_CHECK_WHERE
            = Pattern.compile("(values)(\\s*[(])((.*?)|\\s*)(\\s*[)])(\\s*)(where)",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_3 =
            Pattern.compile("(\\s*[)])(\\s*)(where)",Pattern.CASE_INSENSITIVE);

    //===============================================
    // OTHER METHODS 
    //===============================================
    
    /**
     * Method for check if a string rappresent a Double value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    private static boolean isDouble(Object str) {
        if(str instanceof String)return IS_DOUBLE.matcher(String.valueOf(str)).matches();
        else return isDouble(String.valueOf(str));
    }

    /**
     * Method for check if a string rappresent a Float value.
     * @param str string rapresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    private static boolean isFloat(Object str) {
        if(str instanceof String)return IS_FLOAT.matcher(String.valueOf(str)).matches();
        else return isFloat(String.valueOf(str));
    }

    /**
     * Method for check if a string rappresent a Float value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    private static boolean isDecimal(Object str) {
       /* try {Float.parseFloat(str);return true;
        } catch (NumberFormatException e) {return false;}*/
        if(str instanceof String) return IS_DECIMAL.matcher(String.valueOf(str)).matches();
        else return isDecimal(String.valueOf(str));
    }
    
    /**
     * Method for check if a string rappresent a int value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    private static boolean isInt(Object str) {
        if(str instanceof String) return IS_INT.matcher(String.valueOf(str)).matches();
        else return isInt(String.valueOf(str));
    }
    
    /**
     * Method to check if a string is a url address web or not.
     * @param url the string address web.
     * @return if tru is a url address web.
     */
    private static boolean isURL(Object url){
        if(url == null) return false;
        if(url instanceof String)return WEB_URL.matcher(String.valueOf(url)).matches();
        else return isURL(String.valueOf(url));
    }
    
    /**
     * Method to convert a array to a string with a specific separator
     * @param array the Array Collection.
     * @param separator the char separator.
     * @param <T> generic type.
     * @return the String of the content of the array.
     */
    private static <T> String toString(T[] array,char separator){
        if(isNullOrEmpty(Character.toString(separator))){
            String s = Arrays.toString(array);
            s = s.substring(1,s.length()-1);
            return s;
        }else {
            StringBuilder strBuilder = new StringBuilder();
            if (array != null && array.length > 0) {
                for (int i = 0; i < array.length; i++) {
                    if (array[i] != null){
                        strBuilder.append(array[i].toString());
                        if (i < array.length - 1) strBuilder.append(separator);
                    }
                }
            }
            return strBuilder.toString();
        }

    }

    /**
     * Method to convert a array to a string.
     * @param array the Array Collection.
     * @param <T> generic type.
     * @return the String of the content of the array.
     */
    private static <T> String toString(T[] array){
        return toString(array, ' ');
    }
    
    /**
     * Method to create a Array collection with a single element.
     * @param object the single element.
     * @param size the int size for the Array.
     * @param <T> generic type
     * @return array with a single element.
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] createSingleton(T object,int size){
        T[] newArray = (T[]) Array.newInstance(object.getClass(), size);
        Arrays.fill(newArray, object);
        return newArray;
    }

    
    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     * @param text the {@link String} text.
     * @return the {@link Boolean} is true if the parameter is null or empty.
     */
    private static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty() ;
    }
    
    /**
     * Method to simplify the content of a string for a better vision of the content.
     * @param stringText string of the text.
     * @return string of text simplify.
     */
    private static String toStringInline(String stringText){
        return stringText.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", " ").trim();
        //return stringText.replaceAll("(?m)(^ *| +(?= |$))", "").replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");
    }
    
    /**
     * Method to convert two array keys and values tp a HashMap.
     * @param keys array of keys.
     * @param values array of values.
     * @param <K> the generic key.
     * @param <V> the generic value.
     * @return the hasmap fulled with array.
     */
    private static <K,V> HashMap<K,V> toMap(K[] keys, V[] values) {
        int keysSize = (keys != null) ? keys.length : 0;
        int valuesSize = (values != null) ? values.length : 0;
        if (keysSize == 0 && valuesSize == 0) {
            // return mutable map
            return new HashMap<>();
        }
        if (keysSize != valuesSize) {
            throw new IllegalArgumentException(
                    "The number of keys doesn't match the number of values.");
        }
        HashMap<K, V> map = new HashMap<>();
        for (int i = 0; i < keysSize; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
    
    /**
     * Method to convert a int primitive to the Integer object .
     * @param object the int primitive.
     * @return the the integer object of the int primitive .
     */
    private static Integer toInteger(Object object){
        if(object != null){
            try {
                if (object instanceof Integer) return (int) object;
                else if (object instanceof String && isNumeric(object)) {
                    return Integer.parseInt(String.valueOf(object));
                }
                logger.warn("The string text " + String.valueOf(object) + " with class :"
                        + object.getClass().getName() + " is not a number!!!");
                return 0;
            }catch(NumberFormatException e){
                logger.warn("The string text " + String.valueOf(object) + " with class :"
                        + object.getClass().getName() + " is not a number!!!");
                return 0;
            }
        }else{
            logger.warn("The string text NULL is not a number!!!");
            return 0;
        }
        //return Integer.valueOf(numInt);
    }

    /**
     * Method to cnvert a OBject to a Integer.
     * @param object the Object to convert.
     * @return the Int of the object.
     */
    private static int toInt(Object object){
       return toInteger(object);
    }

    /**
     * Method for check if a string rappresent a numeric value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    private static boolean isNumeric(Object str) {
        //match a number with optional '-' and decimal.
        if(str instanceof String){
            String str2 = String.valueOf(str).replace(",",".").replace(" ",".");
            return IS_NUMERIC.matcher(str2).matches();
        }
        else return isNumeric(String.valueOf(str));
    }
    
    /**
     * Method to check is a array is empty or with all value null or empty.
     * @param array array.
     * @param <T> generic type.
     * @return boolean value.
     */
    private static <T> boolean isEmpty(T[] array){
        //  return array == null || array.length == 0;
        boolean empty = true;
        if(array!=null && array.length > 0) {
            for (T anArray : array) {
                if (anArray != null) {
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }
    
    
    //--------------------------------------------------------
    //StringRegex
    //--------------------------------------------------------
    
    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    private static List<String> findWithRegex(String text,String expression,boolean justFirstResult){
        Pattern pattern = Pattern.compile(expression);
        return findWithRegex(text,pattern,justFirstResult);
    }

    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern of regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    private static List<String> findWithRegex(String text,Pattern pattern,boolean justFirstResult){
        List<String> result =new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            result.add(matcher.group());
            if(justFirstResult)break;
        }
        return result;
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return the first match on the text string.
     */
    private static String findWithRegex(String text,String expression){
        Pattern pat = Pattern.compile(expression);
        return findWithRegex(text,pat);
    }

    /**
     * Checks if the name filters through an including and an excluding
     * regular expression.
     * @param name The <code>String</code> that will be filtered.
     * @param included The regular expressions that needs to succeed.
     * @param excluded The regular expressions that needs to fail.
     * @return true if the name filtered through correctly; or false otherwise.
     */
    private static boolean isMatch(String name, Pattern included, Pattern excluded){
        Pattern[] included_array = null;
        if (included != null)included_array = new Pattern[] {included};
        Pattern[] excluded_array = null;
        if (excluded != null)excluded_array = new Pattern[] {excluded};
        return isMatch(name, included_array, excluded_array);
    }

    /**
     * Checks if the name filters through a series of including and excluding
     * regular expressions.
     * @param name The String that will be filtered.
     * @param included An array of regular expressions that need to succeed
     * @param excluded An array of regular expressions that need to fail
     * @return true if the name filtered through correctly; or false otherwise.
     */
    private static boolean isMatch(String name, Pattern[] included, Pattern[] excluded) {
        if (null == name)return false;
        boolean accepted = false;
        // retain only the includes
        if (null == included) accepted = true;
        else {
            Pattern pattern;
            for (Pattern anIncluded : included) {
                pattern = anIncluded;
                if (pattern != null && pattern.matcher(name).matches()) {
                    accepted = true;
                    break;
                }
            }
        }
        // remove the excludes
        if (accepted && excluded != null){
            Pattern pattern;
            for(Pattern anExcluded : excluded) {
                pattern = anExcluded;
                if (pattern != null && pattern.matcher(name).matches()) {
                    accepted = false;
                    break;
                }
            }
        }
        return accepted;
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern regular expression.
     * @return the first match on the text string.
     */
    private static String findWithRegex(String text,Pattern pattern){
        Matcher matcher = pattern.matcher(text);
        if(matcher.find())return matcher.group(0);
        else return null;
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    private static boolean isMatch(String text,String expression){
        return text != null && Pattern.compile(expression).matcher(text).matches();
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param pattern the pattern of the regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    private static boolean isMatch(String text,Pattern pattern){
        return text != null && pattern.matcher(text).matches();
    }

    /**
     * Metodo che matcha e sostituisce determinati parti di una stringa attraverso le regular expression.
     * @param input stringa di input.
     * @param expression regular expression da applicare.
     * @param replace setta la stringa con cui sostituire il risultato del match.
     * @return il risultato in formato stringa della regular expression.
     */
    private static String regexAndReplace(String input,String expression,String replace){
        String result ="";
        if(replace==null){
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(input);
            while(matcher.find()){
                result = matcher.group();
                if(!isNullOrEmpty(result)){break;}
            }
            return result;
        }else{
            return input.replaceAll(expression, replace);
        }
    }
    
    //=========================
    // PATTERNS
    //===========================
    private static int radix = 10;
    private static String groupSeparator = "\\,";
    private static String decimalSeparator = "\\.";
    private static String nanString = "NaN";
    private static String infinityString = "Infinity";
    private static String positivePrefix = "";
    private static String negativePrefix = "\\-";
    private static String positiveSuffix = "";
    private static String negativeSuffix = "";
    private static String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static String non0Digit = "[\\p{javaDigit}&&[^0]]";
    
    private static final Pattern IS_INT = Pattern.compile("(\\d)+");
    private static Pattern IS_NUMERIC = Pattern.compile("(\\-|\\+)?\\d+(\\.\\d+)?");
    
    private static final String GOOD_IRI_CHAR =
            "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

    private static final Pattern IP_ADDRESS
            = Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                + "|[1-9][0-9]|[0-9]))");

       
    private static final String IRI
            = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR + "]){0,1}";

    private static final String GOOD_GTLD_CHAR =
            "a-zA-Z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";
    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";
    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;

    private static final Pattern DOMAIN_NAME
            = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");

    private static final Pattern WEB_URL = Pattern.compile(
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
            + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
            + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
            + "(?:" + DOMAIN_NAME + ")"
            + "(?:\\:\\d{1,5})?)" // plus option port number
            + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
            + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
            + "(?:\\b|$)"); // and finally, a word boundary or end of
                            // input.  This is to stop foo.sure from
                            // matching as foo.su

    
    private static String Digits     = "(\\p{Digit}+)";
    private static String HexDigits  = "(\\p{XDigit}+)";
    private static String Exp        = "[eE][+-]?"+Digits;
    private static String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                    "[+-]?(" + // Optional sign character
                    "NaN|" +           // "NaN" string
                    "Infinity|" +      // "Infinity" string

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from the Java Language Specification, 2nd
                    // edition, section 3.10.2.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.("+Digits+")("+Exp+")?)|"+

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing "whitespace"

    private static final Pattern IS_DOUBLE = Pattern.compile(fpRegex);
    
    private static Pattern floatPattern;
    private static Pattern decimalPattern;

    private static void  buildFloatAndDecimalPattern() {
        // \\p{javaDigit} may not be perfect, see above
        String digit = "([0-9]|(\\p{javaDigit}))";
        String exponent = "([eE][+-]?"+digit+"+)?";
        String groupedNumeral = "("+non0Digit+digit+"?"+digit+"?("+groupSeparator+digit+digit+digit+")+)";
        // Once again digit++ is used for performance, as above
        String numeral = "(("+digit+"++)|"+groupedNumeral+")";
        String decimalNumeral = "("+numeral+"|"+numeral +decimalSeparator + digit + "*+|"+ decimalSeparator +digit + "++)";
        String nonNumber = "(NaN|"+nanString+"|Infinity|"+infinityString+")";
        String positiveFloat = "(" + positivePrefix + decimalNumeral +positiveSuffix + exponent + ")";
        String negativeFloat = "(" + negativePrefix + decimalNumeral +negativeSuffix + exponent + ")";
        String decimal = "(([-+]?" + decimalNumeral + exponent + ")|"+positiveFloat + "|" + negativeFloat + ")";
        String hexFloat = "[-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?";
        String positiveNonNumber = "(" + positivePrefix + nonNumber +positiveSuffix + ")";
        String negativeNonNumber = "(" + negativePrefix + nonNumber + negativeSuffix + ")";
        String signedNonNumber = "(([-+]?"+nonNumber+")|" + positiveNonNumber + "|" + negativeNonNumber + ")";
        floatPattern = Pattern.compile(decimal + "|" + hexFloat + "|" + signedNonNumber);
        decimalPattern = Pattern.compile(decimal);
    }

    private static Pattern integerPattern;

    private static String  buildIntegerPatternString() {
        String radixDigits = digits.substring(0, radix);
        // \\p{javaDigit} is not guaranteed to be appropriate
        // here but what can we do? The final authority will be
        // whatever parse method is invoked, so ultimately the
        // Scanner will do the right thing
        String digit = "((?i)["+radixDigits+"]|\\p{javaDigit})";
        String groupedNumeral = "("+non0Digit+digit+"?"+digit+"?("+groupSeparator+digit+digit+digit+")+)";
        // digit++ is the possessive form which is necessary for reducing
        // backtracking that would otherwise cause unacceptable performance
        String numeral = "(("+ digit+"++)|"+groupedNumeral+")";
        String javaStyleInteger = "([-+]?(" + numeral + "))";
        String negativeInteger = negativePrefix + numeral + negativeSuffix;
        String positiveInteger = positivePrefix + numeral + positiveSuffix;
        return "("+ javaStyleInteger + ")|(" + positiveInteger + ")|(" + negativeInteger + ")";
    }

    // A cache of the last few recently used Patterns
    /*
    private static final sun.misc.LRUCache<String,Pattern> patternCache =
            new sun.misc.LRUCache<String,Pattern>(7) {
        @Override
        protected Pattern create(String s) {
            return Pattern.compile(s);
        }
        @Override
        protected boolean  hasName(Pattern p, String s) {
            return p.pattern().equals(s);
        }
    };
    */

    private static final Pattern IS_INTEGER = isInteger();

    private static Pattern isInteger() {
        //if (integerPattern == null) {integerPattern = patternCache.forName(buildIntegerPatternString());}
        if (integerPattern == null) {integerPattern = Pattern.compile(buildIntegerPatternString(),Pattern.CASE_INSENSITIVE);}
        return integerPattern;
    }

    private static final Pattern IS_FLOAT = isFloat();

    private static Pattern isFloat(){
        if (floatPattern == null) {buildFloatAndDecimalPattern();}
        return floatPattern;
    }

    private static final Pattern IS_DECIMAL = isDecimal();

    private static Pattern isDecimal(){
        if (decimalPattern == null) {buildFloatAndDecimalPattern();}
        return decimalPattern;
    }
    
    /**
     * Method to convert a Array Collection of Integer to a Array Collection of int.
     * @param integerArray the Array Collection of Integers.
     * @return Array Collection of int.
     */
    private static int[] toPrimitive(Integer[] integerArray) {
        //return org.apache.commons.lang3.ArrayUtils.toPrimitive(integerArray);
        if (integerArray == null) return null;
        else if (integerArray.length == 0) return new int[0];
        final int[] result = new int[integerArray.length];
        for (int i = 0; i < integerArray.length; i++) { result[i] = integerArray[i];}
        return result;
    }
    //============================
    // FILE UTILITIES
    //=============================
    
    /**
     * Method to check if a String path to a File is valid.
     * href: http://stackoverflow.com/questions/468789/is-there-a-way-in-java-to-determine-if-a-path-is-valid-without-attempting-to-cre
     * @param file the String path to the File.
     * @return if true the String path reference to a File.
     */
    private static boolean isFileValid(String file) {
        /*try {
            File f = new File(file);
            if (f.isFile() && !f.isDirectory()) return true;
            f = new File(getDirectoryUser() + file);
            if (f.isFile() && !f.isDirectory()) return true;
            *//*f = new File(getDirectoryUser() + file);
            if (f.isFile() && !f.isDirectory()) return true;*//*
            if (f.exists())return f.canWrite();
            //else
            if(f.createNewFile()){
                if(f.delete())return true;
            }
            if (f.isDirectory()) logger.warn("The path:" + file + " is a directory");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }*/
        try {
            //The toRealPath() method returns the real path of an existing file.
            //Note that a real file is required in the file system, otherwise there will be an exception:
            Path path = Paths.get(file);
            Path real = path.toRealPath();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Method for get the name of the file (with extensions).
     *
     * @param fullPath the {@link String} of the path to the file
     * @return the {@link String} name of the file
     */
    private static String getFilename(String fullPath) {
        String name;
        if (fullPath.contains(File.separator)) name = fullPath.replace(getPath(fullPath), "");
        else name = fullPath;
        name = name.replace(File.separator, "");
        return name;
    }
    
    /**
     * Method for get the path of a file.
     *
     * @param fullPath the {@link String} of the path to the file
     * @return the {@link String} to the file
     */
    private static String getPath(String fullPath) {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }
    
    /**
     * Method to check is a file exists and is valid.
     * I would recommend using isFile() instead of exists().
     * Most of the time you are looking to check if the path points to a file not only that it exists.
     * Remember that exists() will return true if your path points to a directory.
     *
     * If both exists and notExists return false, the existence of the file cannot be verified.
     * (maybe no access right to this path)
     * @param pathToFile tje {@link String} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    private static Boolean isFileExists(String  pathToFile) {
        return isFileExists(Paths.get(pathToFile));
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param file tje {@link File} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    private static Boolean isFileExists(File file) {
        return file!=null && file.isFile() && file.exists();
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param path tje {@link Path} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    private static Boolean isFileExists(Path path) {
        return path !=null && Files.exists(path) && Files.isRegularFile(path);
    }
    
    private static Boolean delete(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    private static Boolean delete(String path) {
        return delete(Paths.get(path));
    }
    
    /**
     * Method to copy the content from a file to another in char format.
     *
     * @param fullPathInput  string path to the file you want to read the copy.
     * @param fullPathOutput string path to the file you want write the copy.
     * @return if true all the operation are done.
     */
    private static boolean copy(String fullPathInput, String fullPathOutput) {
        return copy(Paths.get(fullPathInput), Paths.get(fullPathOutput));
    }

    /**
     * Method to copy a file.
     * @param source      the {@link String} path to the source of the File to copy.
     * @param destination the {@link String} path to the destination for the copy of the file.
     * @return the {@link Boolean} if true all the operation are done.
     */
    private static boolean copy(Path source,Path destination) {
        return copy(source,destination,false);
    }

    /**
    * Method to copy a file.
    * @param source      the {@link String} path to the source of the File to copy.
    * @param destination the {@link String} path to the destination for the copy of the file.
    * @param replaceExistingDestination the {@link Boolean} is true if you want replace the existing file.
    * @return the {@link Boolean} if true all the operation are done.
    */
    private static boolean copy( Path source,Path destination,boolean replaceExistingDestination) {
        //if (!Files.exists(destination)) createNewFile(destination);
      /*  try (OutputStream out = Files.newOutputStream(destination);
             InputStream in = Files.newInputStream(source)) {
            IOUtilities.copy(in, out, StandardCharsets.UTF_8);*/
        try{
            try {
                if(replaceExistingDestination)Files.copy(source, destination, StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
                else Files.copy(source, destination, StandardCopyOption.ATOMIC_MOVE);
            }catch(java.lang.UnsupportedOperationException e){
                if(replaceExistingDestination)Files.copy(source, destination, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                else Files.copy(source, destination, StandardCopyOption.COPY_ATTRIBUTES);
            }
            logger.info("Done copying contents of " + source.getFileName().toString()+
                    " to " + destination.getFileName().toString());
            return true;
        } catch(java.nio.file.FileAlreadyExistsException ef){
            logger.warn(ef.getMessage()+", by defauult the file:"+destination+" is been replaced!!!");
            return copy(source,destination,true);
        } catch (IOException e) {
            logger.error("Copying file/folder: " + source + " to " + destination + ":"+e.getMessage(), e);
            return false;
        }
    }
 
    //=================
    // CLASSI
    //===================
    
    private static class Timer
    {

        private static final org.slf4j.Logger logger =
                org.slf4j.LoggerFactory.getLogger(Timer.class);

        protected long timeFinish = -1 ;
        protected boolean inTimer = false ;
        protected long timeStart  = 0 ;

        public Timer() { }

        public void startTimer() throws Exception {
            if ( inTimer )
                throw new Exception("Already in timer") ;

            timeStart = System.currentTimeMillis() ;
            timeFinish = -1 ;
            inTimer = true ;
        }

        /** 
         * Return time in millisecods.
         * @return the Long value of the Timer when stopped.
         * @throws java.lang.Exception throw if any error is occurred.
         */
        public long endTimer() throws Exception {
            if ( ! inTimer )
                throw new Exception("Not in timer") ;
            timeFinish = System.currentTimeMillis() ;
            inTimer = false ;
            return getTimeInterval() ;
        }

        public long readTimer() throws Exception {
            if ( ! inTimer )
                throw new Exception("Not in timer") ;
            return System.currentTimeMillis()-timeStart  ;
        }

        public long getTimeInterval() throws Exception {
            if ( inTimer )
                throw new Exception("Still timing") ;
            if ( timeFinish == -1 )
                throw new Exception("No valid interval") ;

            return  timeFinish-timeStart ;
        }

        static public String timeStr(long timeInterval)
        {
//            DecimalFormat f = new DecimalFormat("#0.###") ;
//            String s = f.format(timeInterval/1000.0) ;
//            return s ;
            //Java5
            return String.format("%.3f", timeInterval/1000.0) ;
        }

        protected String timeStr(long timePoint, long startTimePoint)
        {
            return timeStr(timePoint-startTimePoint) ;
        }
    }
    
    //=============================================
    
    /**
    *
    * @author Debopam Pal, Software Developer, National Informatics Center (NIC), India
    * @version  July 29, 2014
    *
    * http://www.codeproject.com/Articles/802383/Run-SQL-Script-sql-containing-DDL-DML-SELECT-state
    *
    * Warnings:
    *  1. Remember, ROLLBACK cannot be possible for DDL Statements (CREATE, UPDATE, DELETE).
    *  So, be careful during writing SQL Queries.
    *  Rules to Obey:
    *  1. You must end every SQL Statement with ; (Semicolon)
    *  2. You must end every statement within PL/SQL Block with + (Plus)
    *  3. You must end every PL/SQL Block with # (Hash)
    */
   @SuppressWarnings("unused")
   private static class ScriptRunner {

       private static final org.slf4j.Logger logger =
               org.slf4j.LoggerFactory.getLogger(ScriptRunner.class);

       public static final String DEFAULT_DELIMITER = ";";
       public static final String PL_SQL_BLOCK_SPLIT_DELIMITER = "+";
       public static final String PL_SQL_BLOCK_END_DELIMITER = "#";

       private final boolean autoCommit, stopOnError;
       private final Connection connection;
       //private String delimiter = ScriptRunner.DEFAULT_DELIMITER;
       //private final PrintWriter out, err;

       /* To Store any 'SELECT' queries output */
       private List<Table> tableList;

       /* To Store any SQL Queries output except 'SELECT' SQL */
       private List<String> sqlOutput;

       public ScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
           if (connection == null) {
               throw new RuntimeException("ScriptRunner requires an SQL Connection");
           }

           this.connection = connection;
           this.autoCommit = autoCommit;
           this.stopOnError = stopOnError;
           //this.out = new PrintWriter(System.out);
           //this.err = new PrintWriter(System.err);

           tableList = new ArrayList<>();
           sqlOutput = new ArrayList<>();
       }

       public void runScript(final Reader reader) throws SQLException, IOException {
           final boolean originalAutoCommit = this.connection.getAutoCommit();
           try {
               if (originalAutoCommit != this.autoCommit) {
                   this.connection.setAutoCommit(this.autoCommit);
               }
               this.runScript(this.connection, reader);
           } finally {
               this.connection.setAutoCommit(originalAutoCommit);
           }
       }

       private void runScript(final Connection conn, final Reader reader) throws SQLException, IOException {
           StringBuffer command = null;

           Table table = null;
           try {
               final LineNumberReader lineReader = new LineNumberReader(reader);
               String line;
               while ((line = lineReader.readLine()) != null) {
                   if (command == null) {
                       command = new StringBuffer();
                   }

                   if (table == null) {
                       table = new Table();
                   }

                   String trimmedLine = line.trim();

                   // Interpret SQL Comment & Some statement that are not executable
                   if(trimmedLine.startsWith("--")
                           || trimmedLine.startsWith("//")
                           || trimmedLine.startsWith("#")
                           || trimmedLine.toLowerCase().startsWith("rem inserting into")
                           || trimmedLine.toLowerCase().startsWith("set define off")) {

                       // do nothing...
                   } else if (trimmedLine.endsWith(DEFAULT_DELIMITER) || trimmedLine.endsWith(PL_SQL_BLOCK_END_DELIMITER)) {
                       // Line is end of statement

                       // Append
                       if (trimmedLine.endsWith(DEFAULT_DELIMITER)) {
                           command.append(line.substring(0, line.lastIndexOf(DEFAULT_DELIMITER)));
                           command.append(" ");
                       } else if (trimmedLine.endsWith(PL_SQL_BLOCK_END_DELIMITER)) {
                           command.append(line.substring(0, line.lastIndexOf(PL_SQL_BLOCK_END_DELIMITER)));
                           command.append(" ");
                       }

                       Statement stmt = null;
                       ResultSet rs = null;
                       try {
                           stmt = conn.createStatement();
                           boolean hasResults = false;
                           if (this.stopOnError) {
                               hasResults = stmt.execute(command.toString());
                           } else {
                               try {
                                   stmt.execute(command.toString());
                                   logger.info("Execute SQL:"+command.toString());
                               } catch (final SQLException e) {
                                   logger.error("Error executing SQL Command: \"" + command + "\"",e);
                               }
                           }

                           rs = stmt.getResultSet();
                           if (hasResults && rs != null) {

                               List<String> headerRow = new ArrayList<>();
                               List<List<String>> toupleList = new ArrayList<>();

                               // Print & Store result column names
                               final ResultSetMetaData md = rs.getMetaData();
                               final int cols = md.getColumnCount();
                               StringBuilder sb = new StringBuilder();
                               for (int i = 0; i < cols; i++) {
                                   final String name = md.getColumnLabel(i + 1);
                                   sb.append(name).append("\t");

                                   headerRow.add(name);
                               }
                               table.setHeaderRow(headerRow);
                               logger.info(sb.toString());

                               sb = new StringBuilder();
                               sb.append(StringUtils.repeat("---------", md.getColumnCount()));
                               logger.info(sb.toString());
                               // Print & Store result rows
                               sb = new StringBuilder();
                               while (rs.next()) {
                                   List<String> touple = new ArrayList<>();
                                   for (int i = 1; i <= cols; i++) {
                                       final String value = rs.getString(i);
                                       sb.append(value).append("\t");

                                       touple.add(value);
                                   }
                                   toupleList.add(touple);
                               }
                               table.setToupleList(toupleList);
                               logger.info(sb.toString());
                               this.tableList.add(table);
                               table = null;
                           } else {
                               sqlOutput.add(stmt.getUpdateCount() + " row(s) affected.");
                               //logger.info(stmt.getUpdateCount() + " row(s) affected.");
                           }
                           command = null;
                       } finally {
                           if (rs != null) {
                               try {
                                   rs.close();
                               } catch (final Exception e) {
                                   logger.error("Failed to close result: " + e.getMessage(),e);
                               }
                           }
                           if (stmt != null) {
                               try {
                                   stmt.close();
                               } catch (final Exception e) {
                                   logger.error("Failed to close statement: " + e.getMessage(),e);
                               }
                           }
                       }
                   } else if (trimmedLine.endsWith(PL_SQL_BLOCK_SPLIT_DELIMITER)) {
                       command.append(line.substring(0, line.lastIndexOf(PL_SQL_BLOCK_SPLIT_DELIMITER)));
                       command.append(" ");
                   } else { // Line is middle of a statement

                       // Append
                       command.append(line);
                       command.append(" ");
                   }
               }
               if (!this.autoCommit) {
                   conn.commit();
               }
           } catch (final SQLException e) {
               conn.rollback();
               logger.error("Error executing SQL Command: \"" + command + "\"",e);
           } catch (final IOException e) {
               logger.error("Error reading SQL Script.",e);
           }
       }

       /**
        * @return the tableList
        */
       public List<Table> getTableList() {
           return tableList;
       }

       /**
        * @param tableList the tableList to set
        */
       public void setTableList(List<Table> tableList) {
           this.tableList = tableList;
       }

       /**
        * @return the sqlOutput
        */
       public List<String> getSqlOutput() {
           return sqlOutput;
       }

       /**
        * @param sqlOutput the sqlOutput to set
        */
       public void setSqlOutput(List<String> sqlOutput) {
           this.sqlOutput = sqlOutput;
       }
   }
   
   private static class Table {
	    private String name;
	    private List<String> headerRow;
	    private List<List<String>> toupleList;

	    /**
	     * @return the headerRow
	     */
	    public List<String> getHeaderRow() {
	        return headerRow;
	    }

	    /**
	     * @param headerRow the headerRow to set
	     */
	    public void setHeaderRow(List<String> headerRow) {
	        this.headerRow = headerRow;
	    }

	    /**
	     * @return the toupleList
	     */
	    public List<List<String>> getToupleList() {
	        return toupleList;
	    }

	    /**
	     * @param toupleList the toupleList to set
	     */
	    public void setToupleList(List<List<String>> toupleList) {
	        this.toupleList = toupleList;
	    }

	    /**
	     * @return the name
	     */
	    public String getName() {
	        return name;
	    }

	    /**
	     * @param name the name to set
	     */
	    public void setName(String name) {
	        this.name = name;
	    }
	}
   
   //==========================================================================
   // QUERIES UTILTITIES
   //========================================================================
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
       return "ALTER TABLE " + yourTable + " ADD nameNewColumn "+ convertSQLTypes2String(SQLTypes)+"("+size+");";
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
               toString(cols,',')+" HAVING COUNT(*) > 1) " +
       "BEGIN " +
       "    DELETE FROM "+yourTable+" WHERE "+nameKeyColumn+" IN  " +
       "    ( " +
       "        SELECT MIN("+nameKeyColumn+") as [DeleteID] " +
       "        FROM "+yourTable+" " +
       "        GROUP BY "+toString(cols,',')+" " +
       "        HAVING COUNT(*) > 1 " +
       "    ) " +
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
               "SELECT ROW_NUMBER() OVER(PARTITION BY "+toString(cols,',')+
               " ORDER BY "+toString(cols,',')+") AS ROW " +
               " FROM "+yourTable+") " +
               " DELETE FROM "+yourTable+" " +
               " WHERE ROW > 1;";
   }

   /**
    * Method to create a SQL String to delete all duplicate record for a specific key.
    * href: http://stackoverflow.com/questions/18390574/how-to-delete-duplicate-rows-in-sql-server
    * @param yourTable the {@link String} name of the table
    * @param columns the {@link String[]} array of columns.
    * @param nameKeyColumn the {@link String nameKey}
    * @return the {@link String} query for Delete all duplicates from a Table String SQL.
    */
   public String deleteDuplicateRecord2(String yourTable,String nameKeyColumn,String[] columns){
       return  "WITH CTE AS( SELECT "+toString(columns,',')+
               " RN = ROW_NUMBER()OVER(PARTITION BY "+nameKeyColumn+" " +
               " ORDER BY "+nameKeyColumn+")" +
               " FROM "+yourTable+");" +
               "DELETE FROM CTE WHERE RN > 1;";
   }

   public static String prepareSelectQuery(String mySelectTable,
           String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
       StringBuilder bQuery = new StringBuilder();
       boolean statement = false;
       //PREPARE THE QUERY STRING
       bQuery.append("SELECT ");
       if(isEmpty(columns) || (columns.length==1 && columns[0].equals("*"))){
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
       if(!isEmpty(columns_where)) {
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
           if(isEmpty(values)) {
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
       if(!isEmpty(columns_where)) {
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
       return prepareInsertIntoQuery(myInsertTable, columns, values, toPrimitive(types));
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
           logger.error("Attention: you probably have forgotten  to put some column for the SQL query:"
                   +e.getMessage(),e);
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
           logger.error("Attention: you probably have forgotten to put some column for the SQL query:"
                   +e.getMessage(),e);
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
//  public Object[] prepareValues(Object[] values,int[] types){
//       for(int i = 0; i < values.length; i++){
//           if(values[i]== null){
//               if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(Integer.class.getName())){
//                   values[i] = "NULL";
//                   types[i] = Types.NULL;
//               }
//               else if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(int.class.getName())){
//                   values[i] = "NULL" ;
//                   types[i] = Types.NULL;
//               }
//               else if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(String.class.getName())){
//                   values[i] = "NULL";
//                   types[i] = Types.NULL;
//               }
//               else {
//                   values[i] = "NULL";
//                   types[i] = Types.NULL;
//               }
//           }else{
//               if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(String.class.getName())){
//                   values[i] = values[i].toString();
//                   types[i] = Types.VARCHAR;
//               }
//           }
//       }
//       return values;
//   }

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
                   convertSQLTypes2String(
                           convertStringToSQLTypes(columns[i]))).append("(255)");
           if(i < columns.length) bQuery.append(", ");

       }
       bQuery.append(")\n");
       return bQuery.toString();
   }
   /*
   public static String importCSVBulk(File fileCSV,String nameTable,String database,boolean hasFirstLine,
                                      String fieldTerminator,String rowTerminator){
       StringBuilder bQuery = new StringBuilder();
       String[] columns;
       if(hasFirstLine) columns = FileUtilities.CSVGetHeaders(fileCSV, true);
       else  columns = FileUtilities.CSVGetHeaders(fileCSV,false);

       bQuery.append(createTableToInsertData(database,nameTable,columns));
       bQuery.append("GO \n");
       //Insert the content on the table.
       bQuery.append("BULK INSERT ").append(nameTable)
               .append(" FROM '").append(fileCSV.getAbsolutePath()).append("'")
               .append(" WITH ( ").append("FIELDTERMINATOR = '").append(fieldTerminator).append("',")
               .append("ROWTERMINATOR = '").append(rowTerminator).append("')\n");
       bQuery.append("GO \n");
       return bQuery.toString();
   }
   */
   /*
   public static String importCSVLocalLoad(File fileCSV,boolean firstLine,Character fieldSeparator,
                                           Character linesSeparator,String nameTable){
       StringBuilder loadQuery = new StringBuilder();
       try {
           String[] columns = FileUtilities.CSVGetHeaders(fileCSV,firstLine);
           loadQuery.append("LOAD DATA LOCAL INFILE '").append(fileCSV.getAbsolutePath())
                   .append("' INTO TABLE ").append(nameTable).append(" FIELDS TERMINATED BY '")
                   .append(fieldSeparator).append("'").append(" LINES TERMINATED BY '")
                   .append(linesSeparator).append(" ( ")
                   .append(toString(columns,',')).append(") ");
           logger.info(loadQuery.toString());
           //SQLHelper.executeSQL(loadQuery,connection);
       }
       catch (Exception e){
           logger.error("Cannot import the CSV file to the Database:"+e.getMessage(),e);
       }
       return loadQuery.toString();
   }
	*/
	
}
