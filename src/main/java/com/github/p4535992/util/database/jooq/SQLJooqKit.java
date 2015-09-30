package com.github.p4535992.util.database.jooq;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.database.sql.SQLHelper;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.Patterns;
import com.github.p4535992.util.string.StringKit;
import org.jooq.*;
import org.jooq.impl.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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
public class SQLJooqKit {

    private static DSLContext dslContext;
    private static SQLJooqKit instance = null;
    private static SQLDialect sqlDialect;
    private static Connection connection;
    private static ConnectionProvider connProvider;
    private static ConnectionProvider connSpringProvider;

    protected SQLJooqKit(){}

    public static SQLJooqKit getIstance(){
        if(instance == null){
            instance = new SQLJooqKit();
        }
        return instance;
    }
    public static SQLJooqKit getNewIstance(){
        return new SQLJooqKit();
    }

    public static DSLContext getDslContext() {
        return dslContext;
    }

    public static void setDslContext(DSLContext dslContext) {
        SQLJooqKit.dslContext = dslContext;
    }

    public static SQLDialect getSqlDialect() {
        return sqlDialect;
    }

    public static void setSqlDialect(SQLDialect sqlDialect) {
        SQLJooqKit.sqlDialect = sqlDialect;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        SQLJooqKit.connection = connection;
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
            String query = StringKit.toStringInline(iQuery.toString());
            query = Patterns.getQueryInsertValuesParam(query, columns);
            //return StringKit.toStringInline(iQuery.getSQL(ParamType.NAMED_OR_INLINED));
            return StringKit.toStringInline(query);
        }
        else return StringKit.toStringInline(iQuery.toString());
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
    public static String select(String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions,
                                  String limit,String offset){
        Field[] fields = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            Field<String> field = createField(columns[i]);
            fields[i] = field;
        }
        Table<Record> table = new TableImpl<>(nameTable);
        SelectQuery<Record> sQuery = dslContext.selectQuery();
        sQuery.addSelect(fields);
        sQuery.addFrom(table);
        if(conditions!=null && !conditions.isEmpty()) sQuery.addConditions(conditions);
        if(!StringKit.isNullOrEmpty(limit) && !StringKit.isNullOrEmpty(offset)) {
            if (StringKit.isNumeric(limit) && StringKit.isNumeric(offset)) {
                sQuery.addLimit(StringKit.convertStringToInt(offset), StringKit.convertStringToInt(limit));
            }
        }else if(!StringKit.isNullOrEmpty(limit)){
            if (StringKit.isNumeric(limit)) {
                sQuery.addLimit(StringKit.convertStringToInt(limit));
            }
        }else if(!StringKit.isNullOrEmpty(offset)){
            if (StringKit.isNumeric(offset)) {
                sQuery.addLimit(StringKit.convertStringToInt(offset), 1000000);
            }
        }
        if(preparedStatement ) {
            if (conditions==null || conditions.isEmpty()) {
                return StringKit.toStringInline(sQuery.toString());
            }else {
                String query = Patterns.getQueryInsertWhereParam(StringKit.toStringInline(sQuery.toString()));
                return StringKit.toStringInline(query);
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
            return StringKit.toStringInline(sQuery.toString());
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
    @SuppressWarnings("unchecked")
    public static String update(String nameTable,String[] columns,Object[] values, boolean preparedStatement,List<Condition> conditions){
        Table<Record> table = new TableImpl<>(nameTable);
        UpdateQuery<Record> uQuery = dslContext.updateQuery(table);
        for(int i=0; i < columns.length; i++){
            Field<String> field = createField(columns[i]);
            Field fv = createFieldValue(values[i]);
            uQuery.addValue(field, fv);
        }
        //uQuery.addFrom(table);
        if(conditions!=null && !conditions.isEmpty()) uQuery.addConditions(conditions);
        if(preparedStatement)return StringKit.toStringInline(uQuery.getSQL());
        else return StringKit.toStringInline(uQuery.toString());
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
        if(preparedStatement)return StringKit.toStringInline(dQuery.getSQL());
        else return StringKit.toStringInline(dQuery.toString());
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
    public static Field<Object> createField(Object value){return DSL.val(value);}

    /**
     * Method to create a string JOOQ Field.
     * @param value string to convert.
     * @return a string JOOQ Field.
     */
    public static Field<String> createField(String value){return DSL.field(value, String.class);}

    /**
     * Method to create a boolean JOOQ Field.
     * @param condition a JOOQ condition.
     * @return a boolean JOOQ Field.
     */
    public static Field<Boolean> createField(Condition condition){return DSL.field(condition);}

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @return a JOOQ Field.
     */
    public static Field<?> createFieldValue(Object value){return DSL.val(value);}

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
     * @param fieldType JOOQ Field Type of the object.
     * @return a JOOQ Field.
     */
    public static Field<?> createFieldValue(Object value,Field<?> fieldType){return DSL.val(value, fieldType);}

    /**
     * Method to create a JOOQ Field.
     * @param value object to convert.
     * @param dataType JOOQ DataType of the object.
     * @return a JOOQ Field.
     */
    public static Field<?> createFieldValue(Object value,DataType<?> dataType){return DSL.val(value, dataType);}

    /**
     * Method to create a JOOQ Field of a numeric value.
     * @param value object to convert.
     * @return a Integer OOQ Field.
     */
    public static Field<Integer> createFieldValueInt(Object value){
        return DSL.val(value,Integer.class);
    }

    /**
     * Method to create a JOOQ Field of a string value.
     * @param value object to convert.
     * @return a string JOOQ Field.
     */
    public static Field<String> createFieldValueString(Object value){
        return DSL.val(StringKit.convertObjectToString(value),String.class);
    }

    /**
     * Method to create a JOOQ Field of a object.
     * @param value object to convert.
     * @param sqlTypes java.sql.Type related to the object.
     * @return a JOOQ Field.
     */
    public static Field createFieldValue(Object value,int sqlTypes){
        if(value == null || sqlTypes == Types.NULL) return null;
        return DSL.val(value,createDataType(sqlTypes));
    }

    /**
     * Method to cretae a JOOQ DataType.
     * @param clazzType the class type of the datatype.
     * @param typeName the string of the SQL type of the datatype.
     * @return a JOOQ DataType
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(Class<?> clazzType,String typeName){
        return new DefaultDataType(
                sqlDialect,
                clazzType,
                typeName
        );
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
                SQLHelper.convertSQLTypes2JavaClass(sqlTypes),
                SQLHelper.convertSQLTypes2String(sqlTypes)
        );
    }


    /**
     * Method to create a new DSLContext.
     * @return the JOOQ DSLContext.
     */
    public static DSLContext createDSLContext(){
        if(connection==null){
            SystemLog.warning("No Connection is initialized for this operation");
        }
        if(sqlDialect==null){
            SystemLog.warning("No SQLDialect is initialized for this operation");
        }
        setConnection(connection, sqlDialect);
        return dslContext;
    }

    /**
     * Method to convert a set of arrays to a specific map of JOOQ Field.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @param types Collection Array of java.sql.Types int, types of column.
     * @return a Map of JOOQ Field to JOOQ Field.
     */
    @SuppressWarnings("unchecked")
    public static Map<org.jooq.Field<String>,org.jooq.Field<Object>> convertArraysToMapJOOQField(
            String[] columns,Object[] values,int[] types){
        Field<String>[] fields = new Field[columns.length];
        Field<Object>[] fv = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            Field<String> field = createField(columns[i]);
            fields[i] = field;
            fv[i] = createFieldValue(values[i], types[i]);
        }
        return CollectionKit.convertTwoArrayToMap(fields,fv);
    }

    /**
     * Method to convert a set of arrays to a specific map of JOOQ Field.
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @return a Map of JOOQ Field to JOOQ Field.
     */
    public static Map<org.jooq.Field,org.jooq.Field> convertArraysToMapJOOQField(
            String[] columns,Object[] values){
        org.jooq.Field[] fields = new org.jooq.Field[columns.length];
        org.jooq.Field[] fv = new org.jooq.Field[columns.length];
        for(int i=0; i < columns.length; i++){
            org.jooq.Field<String> field = DSL.val(columns[i]);
            fields[i] = field;
            fv[i] = createField(values[i]);
        }
        return CollectionKit.convertTwoArrayToMap(fields,fv);
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
                conds.add(createField(columns[i]).isNull());
            }else {
                conds.add(createField(columns[i]).eq(createFieldValue(values[i], types[i])));
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
        if (columns != null && !CollectionKit.isArrayEmpty(columns)) {
            for (int i = 0; i < columns.length; i++) {
                if (values[i] == null) {
                    conds.add(createField(columns[i]).isNull());
                } else if(values[i] instanceof String){
                    conds.add(createField(columns[i]).eq(createFieldValueString(values[i])));
                /*} else if(values_where[i] instanceof Integer){
                    conds.add(createField(columns_where[i]).eq(createFieldValue(values_where[i])));*/
                } else {
                    conds.add(createField(columns[i]).eq((Field) createFieldValue(values[i])));
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
    public static Field<?>[] convertObjecyArrayToFieldValueArray(Object[] arrayObj){
        Field<?>[] fields = new Field[arrayObj.length];
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
    @SuppressWarnings("unchecked")
    public static Record createRecord(
            String[] columns,Object[] values,String nameTable){
        org.jooq.Field[] fields = new org.jooq.Field[columns.length];
        Map<String,Object> map = new HashMap<>();
        Record rec = new TableRecordImpl<>(new TableImpl(nameTable));
        for(int i=0; i < columns.length; i++){
            fields[i] = createField(columns[i]);
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
     * Method utility for get a MySQL connection with JOOQ.
     * @param host host where the server is.
     * @param port number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     */
    public static void getMySQLConnection(String host,String port,String database,String username,String password){
        try {
            connection = SQLHelper.getMySqlConnection(host, port, database, username, password);
            connProvider= new DefaultConnectionProvider(connection);
            connProvider.acquire();
            sqlDialect = SQLDialect.MYSQL;
            dslContext = DSL.using(connection,SQLDialect.MYSQL);
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            SystemLog.exceptionAndAbort(e);
        }
    }

    public static void main(String[] args) {
        String[] columns = new String[]{"col1","col2","col3"};
        Object[] values =new Object[]{1,"test , 33",null};
        int[] types = new int[]{Types.INTEGER,Types.VARCHAR,Types.NULL};
        getMySQLConnection("localhost", "3306", "geodb", "siimobility", "siimobility");
        //INSERT
        String query = insert("tabl1",columns,values,types);
        System.out.println(StringKit.toStringInline(query));
        //query = insert("tabl1",columns,values,types,true);
        //System.out.println(StringKit.toStringInline(query));

        query = insert("tabl1", columns, values, types, true);
        System.out.println(StringKit.toStringInline(query));
        //SELECT
        query = select("tabl1", columns);
        System.out.println(StringKit.toStringInline(query));
        query = select("tabl1",columns,true);
        System.out.println(StringKit.toStringInline(query));
        Field<String> f1 = createField("col1");
        Field<String> f2 = createField("col2");
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
        System.out.println(StringKit.toStringInline(query));
        query = select("tabl1", columns,true,cinds);
        System.out.println(StringKit.toStringInline(query));
        cinds = convertToListConditionEqualsWithAND(new String[]{"col1","col2"},new Object[]{null,43});
        query = select("tabl1", columns, false, cinds);
        System.out.println(StringKit.toStringInline(query));
        query = select("tabl1", columns,true,cinds);
        System.out.println(StringKit.toStringInline(query));

        //UPDATE
        query = update("tabl1", columns, values);
        System.out.println(StringKit.toStringInline(query));
        query = update("tabl1",columns,values,true);
        System.out.println(StringKit.toStringInline(query));
        query = update("tabl1", columns, values, false, cinds);
        System.out.println(StringKit.toStringInline(query));
        //DELETE
        query = delete("tabl1",false,cinds);
        System.out.println(StringKit.toStringInline(query));



    }
}