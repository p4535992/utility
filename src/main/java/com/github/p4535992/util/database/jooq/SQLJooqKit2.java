package com.github.p4535992.util.database.jooq;

import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.database.sql.SQLUtilities;

import com.github.p4535992.util.string.StringUtilities;
import org.jooq.*;
import org.jooq.impl.*;

import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 16/07/2015.
 * @author 4535992.
 * @version 2015-09-30.
 */
@SuppressWarnings("unused")
public class SQLJooqKit2 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SQLJooqKit2.class);

    private static DSLContext dslContext;
    private static SQLJooqKit2 instance = null;
    private static SQLDialect sqlDialect;
    private static Connection connection;
    private static ConnectionProvider connProvider;
    private static ConnectionProvider connSpringProvider;

    protected SQLJooqKit2(){}

    public static SQLJooqKit2 getIstance(){
        if(instance == null){
            instance = new SQLJooqKit2();
        }
        return instance;
    }
    public static SQLJooqKit2 getNewIstance(){
        return new SQLJooqKit2();
    }

    public static DSLContext getDslContext() {
        return dslContext;
    }

    public static void setDslContext(DSLContext dslContext) {
        SQLJooqKit2.dslContext = dslContext;
    }

    public static SQLDialect getSqlDialect() {
        return sqlDialect;
    }

    public static void setSqlDialect(SQLDialect sqlDialect) {
        SQLJooqKit2.sqlDialect = sqlDialect;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection conn) {
        connection = conn;
        connProvider= new DefaultConnectionProvider(connection);
        sqlDialect = getSQLDialect(connection);
        dslContext = DSL.using(connection,sqlDialect);
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
        sqlDialect = SQLUtilities.convertDialectDatabaseToTypeNameId(sqlDialect);
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
        Table<Record> table = new TableImpl<>(nameTable);
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
        Map<Field<String>,?> map = convertArraysToMapJOOQField(columns, values, types);
        Query iQuery = dslContext.insertInto(table).set(map);
        if(preparedStatement){
            String query = StringUtilities.toStringInline(iQuery.toString());
            query = JOOQSupport.getQueryInsertValuesParam(query, columns);
            //return StringKit.toStringInline(iQuery.getSQL(ParamType.NAMED_OR_INLINED));
            return StringUtilities.toStringInline(query);
        }
        else return StringUtilities.toStringInline(iQuery.toString());
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
            String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions,
                                Integer limit,Integer offset){
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
        Field[] fields = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            Field<String> field = DSL.field(columns[i],String.class);
            fields[i] = field;
        }
        Table<Record> table = new TableImpl<>(nameTable);
        SelectQuery<Record> sQuery = dslContext.selectQuery();
        sQuery.addSelect(fields);
        sQuery.addFrom(table);
        if(conditions!=null && !conditions.isEmpty()) sQuery.addConditions(conditions);
        if(!StringUtilities.isNullOrEmpty(limit) && !StringUtilities.isNullOrEmpty(offset)) {
            if (StringUtilities.isNumeric(limit) && StringUtilities.isNumeric(offset)) {
                sQuery.addLimit(StringUtilities.toInt(offset), StringUtilities.toInt(limit));
            }
        }else if(!StringUtilities.isNullOrEmpty(limit)){
            if (StringUtilities.isNumeric(limit)) {
                sQuery.addLimit(StringUtilities.toInt(limit));
            }
        }else if(!StringUtilities.isNullOrEmpty(offset)){
            if (StringUtilities.isNumeric(offset)) {
                sQuery.addLimit(StringUtilities.toInt(offset), 1000000);
            }
        }
        if(preparedStatement ) {
            if (conditions==null || conditions.isEmpty()) {
                return StringUtilities.toStringInline(sQuery.toString());
            }else {
                String query = JOOQSupport.getQueryInsertWhereParam(StringUtilities.toStringInline(sQuery.toString()));
                return StringUtilities.toStringInline(query);
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
            return StringUtilities.toStringInline(sQuery.toString());
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
        Table<Record> table = new TableImpl<>(nameTable);
        UpdateQuery<Record> uQuery = dslContext.updateQuery(table);
        for(int i=0; i < columns.length; i++){
            Field<String> field = DSL.field(columns[i],String.class);
            Field fv = createFieldValue(values[i]);
            uQuery.addValue(field, fv);
        }
        //uQuery.addFrom(table);
        if(conditions!=null && !conditions.isEmpty()) uQuery.addConditions(conditions);

        if(preparedStatement)return StringUtilities.toStringInline(uQuery.getSQL());
        else return StringUtilities.toStringInline(uQuery.toString());
    }

    /**
     * Method to create a string query SQL for update operation.
     * @param nameTable string name of the table.
     * @param preparedStatement if true print all value with the symbol "?".
     * @param conditions list of jooq Condition for filter the result of the update operation.
     * @return string query SQL for the delete operation.
     */
    public static String delete(String nameTable,boolean preparedStatement,List<Condition> conditions){
        Table<Record> table = new TableImpl<>(nameTable);
        DeleteQuery<Record> dQuery = dslContext.deleteQuery(table);
        if(conditions!=null && !conditions.isEmpty()) dQuery.addConditions(conditions);
        if(preparedStatement)return StringUtilities.toStringInline(dQuery.getSQL());
        else return StringUtilities.toStringInline(dQuery.toString());
    }

    /**
     * Method to set the connection with a specific SQLDialect.
     * @param connection the connection object.
     * @param dialect the SQLDialect object.
     */
    public static void setConnection(Connection connection,SQLDialect dialect){
        dslContext = DSL.using(connection, dialect);
        sqlDialect = dialect;
    }

    /**
     * Method to set the connection with a specific SQLDialect.
     * @param configuration configuration object.
     */
    public static void setConnection(Configuration configuration) {
        dslContext = DSL.using(configuration);
    }


    /**
     * Method to close the connection to the database.
     */
    public static void closeConnection(){
        if(connection!=null) {
            if (connProvider != null) connProvider.release(connection);
            if (connSpringProvider != null) connSpringProvider.release(connection);
        }
    }

    /**
     * Method to create a JOOQ Table Object.
     * @param nameTable string name of the table to convert.
     * @return a JOOQ Table.
     */
    public static Table<Record> createTable(String nameTable){ return new TableImpl<>(nameTable);}

    /**
     * Method to create a object JOOQ Field.
     * @param value object to convert.
     * @return a object JOOQ Field.
     */
    public static Field<?> createFieldValue(Object value){
        if(value instanceof URL) return DSL.val(StringUtilities.toString(value), String.class);
        if(value instanceof URI) return DSL.val(StringUtilities.toString(value), String.class);
        if(value instanceof String) return DSL.val(StringUtilities.toString(value), String.class);
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
    public static Field createFieldValueCapture(Object value) {
         return createFieldValue(value);
    }

    /**
     * Method to create a object JOOQ Field.
     * @param value object to convert.
     * @param dataType the JOOQ Datatype.
     * @return a object JOOQ Field.
     */
    public static Field<?> createFieldValue(Object value,DataType<?> dataType){
        if(value instanceof URL) return DSL.val(StringUtilities.toString(value), dataType);
        if(value instanceof URI) return DSL.val(StringUtilities.toString(value), dataType);
        if(value instanceof String) return DSL.val(StringUtilities.toString(value), dataType);
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
    public static Field createFieldValueCapture(Object value,DataType<?> dataType){
        return createFieldValue(value, dataType);
    }

    /**
     * Method to create a string JOOQ Field.
     * @param value string to convert.
     * @return a string JOOQ Field.
     */
    //public static Field<String> createFieldValue(String value){return DSL.field(value, String.class);}

    /**
     * Method to create a boolean JOOQ Field.
     * @param condition a JOOQ condition.
     * @return a boolean JOOQ Field.
     */
    //public static Field<Boolean> createField(Condition condition){return DSL.field(condition);}

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @return a JOOQ Field.
     */
    //public static Field<?> createFieldValue(Object value){return DSL.val(value);}

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param clazzType class type of the object.
     * @return a JOOQ Field.
     */
    public static Field<?> createFieldValue(Object value,Class<?> clazzType){return DSL.val(value, clazzType); }

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param clazzType class type of the object.
     * @return a JOOQ Field.
     */
    @SuppressWarnings("rawtypes")
    public static Field createFieldValueCapture(Object value,Class<?> clazzType){return DSL.val(value, clazzType); }

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param fieldType JOOQ Field Type of the object.
     * @return a JOOQ Field.
     */
    //public static Field<?> createFieldValue(Object value,Field<?> fieldType){return DSL.val(value, fieldType);}

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param dataType JOOQ DataType of the object.
     * @return a JOOQ Field.
     */
    //public static Field<?> createFieldValue(Object value,DataType<?> dataType){return DSL.val(value, dataType);}

    /**
     * Method to create a JOOQ Field of a numeric value.
     * @param value object to convert.
     * @return a Integer JOOQ Field.
     */
    //public static Field<Integer> createFieldValueInt(Object value){return DSL.val(value,Integer.class);}

    /**
     * Method to create a JOOQ Field of a string value.
     * @param value object to convert.
     * @return a string JOOQ Field.
     */
    /*public static Field<String> createFieldString(Object value){
        return DSL.val(StringKit.convertObjectToString(value),String.class);
    }*/

    /**
     * Method to create a JOOQ Field of a string value.
     * @param value object to convert.
     * @return a string JOOQ Field.
     */
   /* public static Field<String> createFieldValueString(Object value){
        return DSL.val(StringKit.convertObjectToString(value),String.class);
    }*/


    /**
     * Method to create a JOOQ Field of a object.
     * @param value object to convert.
     * @param sqlTypes java.sql.Type related to the object.
     * @return a JOOQ Field.
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static Field<?> createField(Object value,int sqlTypes){
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
    public static Field createFieldValueCapture(Object value,int sqlTypes){
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
                SQLUtilities.convertSQLTypes2JavaClass(sqlTypes),
                SQLUtilities.convertSQLTypes2String(sqlTypes)
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
                SQLUtilities.convertSQLTypes2JavaClass(sqlTypes),
                SQLUtilities.convertSQLTypes2String(sqlTypes)
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
    public static Map<Field<String>,Field<Object>> convertArraysToMapJOOQField(
            String[] columns,Object[] values,int[] types){
        Field<String>[] fields = new Field[columns.length];
        Field<Object>[] fv = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            //return Field<String>
            Field<String> field = DSL.field(columns[i],String.class);
            fields[i] = field;
            fv[i] = createFieldValueCapture(values[i], types[i]);
        }
        return ArrayUtilities.toMap(fields, fv);
    }

    /**
     * Method to convert a set of arrays to a specific map of JOOQ Field.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @return a Map of JOOQ Field to JOOQ Field.
     */
    @SuppressWarnings("rawtypes")
    public static Map<Field,Field> convertArraysToMapJOOQField(
            String[] columns,Object[] values){
        Field[] fields = new Field[columns.length];
        Field[] fv = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            Field<String> field = DSL.val(columns[i]);
            fields[i] = field;
            fv[i] = createFieldValueCapture(values[i]);
        }
        return ArrayUtilities.toMap(fields, fv);
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
    public static List<Condition> convertToListConditionEqualsWithAND(String[] columns,Object[] values,int[] types){
        List<Condition> conds = new ArrayList<>();
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
    public static List<Condition> convertToListConditionEqualsWithAND(String[] columns,Object[] values) {
        List<Condition> conds = new ArrayList<>();
        if (columns != null && !ArrayUtilities.isEmpty(columns)) {
            for (int i = 0; i < columns.length; i++) {
                if (values[i] == null) {
                    conds.add(createFieldValue(columns[i]).isNull());
                } else if(values[i] instanceof String){
                    conds.add(createFieldValue(columns[i]).eq(createFieldValueCapture(values[i])));
                /*} else if(values_where[i] instanceof Integer){
                    conds.add(createField(columns_where[i]).eq(createFieldValue(values_where[i])));*/
                } else {
                    conds.add(createFieldValue(columns[i]).eq((Field) createFieldValue(values[i])));
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
    public static Field<?>[] convertObjecyArrayToFieldValueArray(Object[] arrayObj){
        Field[] fields = new Field[arrayObj.length];
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
    public static <T> DataType<T> convertFieldToDataType(Field<T> field){
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
     * Method utility for get a MySQL connection with JOOQ.
     * @param host host where the server is.
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the DSLContext set with Connection.
     */
    public static DSLContext getMySQLConnection(String host,String port,String database,String username,String password){
        connection = SQLUtilities.getMySqlConnection(host, port, database, username, password);
        connProvider= new DefaultConnectionProvider(connection);
        connProvider.acquire();
        sqlDialect = SQLDialect.MYSQL;
        dslContext = DSL.using(connection,SQLDialect.MYSQL);
        return dslContext;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String[] columns = new String[]{"col1","col2","col3"};
        Object[] values =new Object[]{1,"test",null};
        int[] types = new int[]{Types.INTEGER,Types.VARCHAR,Types.NULL};
        getMySQLConnection("localhost", "3306", "geodb", "siimobility", "siimobility");
        //INSERT
        String query = insert("tabl1",columns,values,types);
        System.out.println(StringUtilities.toStringInline(query));
        //query = insert("tabl1",columns,values,types,true);
        //System.out.println(StringKit.toStringInline(query));

        query = insert("tabl1", columns, values, types, true);
        System.out.println(StringUtilities.toStringInline(query));
        //SELECT
        query = select("tabl1", columns);
        System.out.println(StringUtilities.toStringInline(query));
        query = select("tabl1",columns,true);
        System.out.println(StringUtilities.toStringInline(query));
        Field<String> f1 = (Field<String>) createFieldValue("col1");
        Field<String> f2 = (Field<String>) createFieldValue("col2");
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
        System.out.println(StringUtilities.toStringInline(query));
        query = select("tabl1", columns,true,cinds);
        System.out.println(StringUtilities.toStringInline(query));
        cinds = convertToListConditionEqualsWithAND(new String[]{"col1","col2"},new Object[]{null,43});
        query = select("tabl1", columns, false, cinds);
        System.out.println(StringUtilities.toStringInline(query));
        query = select("tabl1", columns,true,cinds);
        System.out.println(StringUtilities.toStringInline(query));

        //UPDATE
        query = update("tabl1", columns, values);
        System.out.println(StringUtilities.toStringInline(query));
        query = update("tabl1",columns,values,true);
        System.out.println(StringUtilities.toStringInline(query));
        query = update("tabl1", columns, values, false, cinds);
        System.out.println(StringUtilities.toStringInline(query));
        //DELETE
        query = delete("tabl1",false,cinds);
        System.out.println(StringUtilities.toStringInline(query));



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


}
