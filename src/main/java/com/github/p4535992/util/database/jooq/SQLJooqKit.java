package com.github.p4535992.util.database.jooq;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.database.sql.SQLHelper;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.Patterns;
import com.github.p4535992.util.string.StringKit;
import org.jooq.*;
import org.jooq.conf.ParamType;
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
 * @version 2015-07-16.
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

    public static String insert(String nameTable,String[] columns,Object[] values,int[] types){
        return insert(nameTable, columns, values, types, false);
    }


   /* public static String insert(String nameTable,String[] columns,Object[] values,int[] types,boolean preparedStatement){
        Field[] fields = new Field[columns.length];
        Field[] fv = new Field[columns.length];
        //DataType[] dataTypes = new DataType[types.length];

        for(int i=0; i < columns.length; i++){
            Field<String> field = DSL.val(columns[i]);
            fields[i] = field;
            if(types[i]==Types.NULL && values[i]==null) {
                fv[i] = null;
            }else if(values[i]==null){
                fv[i] = null;
            }else {
                DataType dataType = new DefaultDataType(
                        SQLDialect.MYSQL,
                        SQLHelper.convertSQLTypes2JavaClass(types[i]),
                        SQLHelper.convertSQLTypes2String(types[i])
                );
                fv[i] = DSL.val(values[i], dataType);
            }
        }
        table = new TableImpl(nameTable);
        //query = dslContext.insertInto(table,fields).values(values).getSQL();
        //Map<Field,Object> map = CollectionKit.convertTwoArrayToMap(fields,values);
        Map<Field,Field> map2 = CollectionKit.convertTwoArrayToMap(fields,fv);
        //query = dslContext.insertInto(table).set(map).toString();
        //query = dslContext.insertInto(table).set(map).getSQL();
        query = dslContext.insertInto(table).set(map2);
        if(preparedStatement){return query.getSQL();
        }else {return query.toString();
        }
    }*/

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
            query = Patterns.getQueryValuesParam(query,columns);
            //return StringKit.toStringInline(iQuery.getSQL(ParamType.NAMED_OR_INLINED));
            return StringKit.toStringInline(query);
        }
        else return StringKit.toStringInline(iQuery.toString());
    }


    public static String select(String nameTable,String[] columns){
        return select(nameTable, columns, false);
    }

    public static String select(String nameTable,String[] columns,boolean preparedStatement){
       return select(nameTable, columns, preparedStatement, null);
    }

    public static String select(String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions){
        return select(nameTable,columns,preparedStatement,conditions, null,null);
    }

    /*public static String select(String nameTable,String[] columns,boolean preparedStatement,List<Condition> conditions,
                                 String limit,String offset){
        String query;
        DSLContext context = DSL.using(sqlDialect);
        Table table = new TableImpl<>(nameTable);
        Field[] fields = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            Field field = createField(columns[i]);
            fields[i] = field;
        }
        if(conditions!=null && !conditions.isEmpty()) {
            if (limit != null && offset != null) {
                if (StringKit.isNumeric(limit) && StringKit.isNumeric(offset)) {
                    //condition + limit + offset
                    if(preparedStatement) query = context.select(fields).from(table).where(conditions).limit(
                            StringKit.convertStringToInt(offset), StringKit.convertStringToInt(limit)).getSQL();
                    else query = context.select(fields).from(table).where(conditions).limit(
                            StringKit.convertStringToInt(offset), StringKit.convertStringToInt(limit)).toString();
                }else{
                    if(preparedStatement) query = context.select(fields).from(table).where(conditions).getSQL();
                    else query = context.select(fields).from(table).where(conditions).toString();
                }
            } else if (limit != null) {
                if (StringKit.isNumeric(limit)) {
                    if(preparedStatement) query = context.select(fields).from(table).where(conditions).limit(
                            StringKit.convertStringToInt(limit)).getSQL();
                    else query = context.select(fields).from(table).where(conditions).limit(
                            StringKit.convertStringToInt(limit)).toString();
                }else{
                    if(preparedStatement) query = context.select(fields).from(table).where(conditions).getSQL();
                    else query = context.select(fields).from(table).where(conditions).toString();
                }
            } else if (offset != null) {
                if (StringKit.isNumeric(offset)) {
                    if(preparedStatement) query = context.select(fields).from(table).where(conditions).limit(
                            StringKit.convertStringToInt(offset), 100000).getSQL();
                    else query = context.select(fields).from(table).where(conditions).limit(
                            StringKit.convertStringToInt(offset), 100000).toString();
                }else{
                    if(preparedStatement) query = context.select(fields).from(table).where(conditions).getSQL();
                    else query = context.select(fields).from(table).where(conditions).toString();
                }
            } else{
                if(preparedStatement) query = context.select(fields).from(table).where(conditions).getSQL();
                else  query = context.select(fields).from(table).where(conditions).toString();
            }
        }else{//.... no conditions
            if (limit != null && offset != null) {
                if (StringKit.isNumeric(limit) && StringKit.isNumeric(offset)) {
                    //condition + limit + offset
                    if(preparedStatement)query = context.select(fields).from(table).limit(
                            StringKit.convertStringToInt(offset), StringKit.convertStringToInt(limit)).getSQL();
                    else query = context.select(fields).from(table).limit(
                            StringKit.convertStringToInt(offset), StringKit.convertStringToInt(limit)).toString();
                }else{
                    if(preparedStatement)query = context.select(fields).from(table).getSQL();
                    else query = context.select(fields).from(table).toString();
                }
            } else if (limit != null) {
                if (StringKit.isNumeric(limit)) {
                    if(preparedStatement)query = context.select(fields).from(table).limit(StringKit.convertStringToInt(limit)).getSQL();
                    else query = context.select(fields).from(table).limit(StringKit.convertStringToInt(limit)).toString();
                }else{
                    if(preparedStatement)query = context.select(fields).from(table).getSQL();
                    else query = context.select(fields).from(table).toString();
                }
            } else if (offset != null) {
                if (StringKit.isNumeric(offset)) {
                    if(preparedStatement) query = context.select(fields).from(table).limit(StringKit.convertStringToInt(offset), 100000).getSQL();
                    else  query = context.select(fields).from(table).limit(StringKit.convertStringToInt(offset), 100000).toString();
                } else{
                    if(preparedStatement)query = context.select(fields).from(table).getSQL();
                    else query = context.select(fields).from(table).toString();
                }
            } else{
                if(preparedStatement)query = context.select(fields).from(table).getSQL();
                else query = context.select(fields).from(table).toString();
            }
        }
        if(StringKit.isNullOrEmpty(query)) return "<?>";
        else return query;

    }*/

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
        if(limit != null && offset != null) {
            if (StringKit.isNumeric(limit) && StringKit.isNumeric(offset)) {
                sQuery.addLimit(StringKit.convertStringToInt(offset), StringKit.convertStringToInt(limit));
            }
        }else if(limit != null){
            if (StringKit.isNumeric(limit)) {
                sQuery.addLimit(StringKit.convertStringToInt(limit));
            }
        }else if(offset != null){
            if (StringKit.isNumeric(offset)) {
                sQuery.addLimit(StringKit.convertStringToInt(offset), 1000000);
            }
        }
        if(preparedStatement ) {
            if (conditions==null || conditions.isEmpty()) {
                return StringKit.toStringInline(sQuery.toString());
            }else {
                String query = Patterns.getQueryWhereParam(StringKit.toStringInline(sQuery.toString()));
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

    public static String update(String nameTable,String[] columns,Object[] values){
        return update(nameTable, columns, values, false, null);
    }

    public static String update(String nameTable,String[] columns,Object[] values, boolean preparedStatement){
       return update(nameTable, columns, values, preparedStatement, null);
    }

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

    public static String delete(String nameTable,boolean preparedStatement,List<Condition> conditions){
        Table<Record> table = new TableImpl<>(nameTable);
        DeleteQuery<Record> dQuery = dslContext.deleteQuery(table);
        if(conditions!=null && !conditions.isEmpty()) dQuery.addConditions(conditions);
        if(preparedStatement)return StringKit.toStringInline(dQuery.getSQL());
        else return StringKit.toStringInline(dQuery.toString());
    }

    public static void setConnection(Connection connection,SQLDialect dialect){
        dslContext = DSL.using(connection, dialect);
        sqlDialect = dialect;
    }

    public static void setConnection(Configuration configuration) {
        dslContext = DSL.using(configuration);
    }

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

    public static void closeConnection(){
        if(connection!=null) {
            if (connProvider != null) connProvider.release(connection);
            if (connSpringProvider != null) connSpringProvider.release(connection);
        }
    }

    public static Table<Record> createTable(String nameTable){ return new TableImpl<>(nameTable);}


    public static Field<Object> createField(Object value){return DSL.val(value);}
    public static Field<String> createField(String value){return DSL.field(value, String.class);}
    public static Field<Boolean> createField(Condition condition){return DSL.field(condition);}

    public static Field<?> createFieldValue(Object value){return DSL.val(value);}
    public static Field<?> createFieldValue(Object value,Class<?> clazzType){return DSL.val(value, clazzType); }
    public static Field<?> createFieldValue(Object value,Field<?> fieldType){return DSL.val(value, fieldType);}
    public static Field<?> createFieldValue(Object value,DataType<?> dataType){return DSL.val(value, dataType);}

    public static Field<Integer> createFieldValueInt(Object value){
        return DSL.val(value,Integer.class);
    }
    public static Field<String> createFieldValueString(Object value){
        return DSL.val(StringKit.convertObjectToString(value),String.class);
    }


    public static Field createFieldValue(Object value,int sqlTypes){
        if(value == null || sqlTypes == Types.NULL) return null;
        return DSL.val(value,createDataType(sqlTypes));
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(Class<?> clazzType,String typeName){
        return new DefaultDataType(
                sqlDialect,
                clazzType,
                typeName
        );
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static DataType createDataType(int sqlTypes){
        return new DefaultDataType(
                sqlDialect,
                SQLHelper.convertSQLTypes2JavaClass(sqlTypes),
                SQLHelper.convertSQLTypes2String(sqlTypes)
        );
    }

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

   /* public <T> List<T> select() {
        return transactionTemplate.execute(new TransactionCallback<List<T>>() {
            @Override
            public List<ImageProductDTO> doInTransaction(TransactionStatus transactionStatus) {
                return jooqContext
                        .select(IMAGE.NAME, PRODUCT.NAME)
                        .from(IMAGE)
                        .join(PRODUCT).on(IMAGE.PRODUCT_ID.equal(PRODUCT.ID))
                        .where(PRODUCT.NAME.likeIgnoreCase("%tv%"))
                        .and(IMAGE.INDEX.greaterThan(0))
                        .orderBy(IMAGE.NAME.asc())
                        .fetch().into(ImageProductDTO.class);
            }
        });
    }*/

    /**
     * Method to convert a SQL
     * @param columns Collection Array of String name of columns.
     * @param values Collection Array of Object values.
     * @param types Collection Array of java.sql.Types int, types of column.
     * @return a Map of Field to Field.
     */
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

    public static Map<Field<String>, ?> convertArraysToMapJOOQField2(
            String[] columns, Object[] values){
        Map<Field<String>, Object> map = new HashMap<>();
        Field[] fields = new Field[columns.length];
        Field[] fv = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            org.jooq.Field<String> field = createField(columns[i]);
            fields[i] = field;
            map.put(field,values[i]);
        }
        return map;
    }

    public static List<Condition> convertToListConditionEqualsWithAND(String[] columns_where,Object[] values_where,int[] types){
        List<Condition> conds = new ArrayList<>();
        for(int i=0; i < columns_where.length; i++){
            if(values_where[i]==null) {
                conds.add(createField(columns_where[i]).isNull());
            }else {
                conds.add(createField(columns_where[i]).eq(createFieldValue(values_where[i], types[i])));
            }
        }
        return conds;
    }

    public static List<Condition> convertToListConditionEqualsWithAND(String[] columns_where,Object[] values_where) {
        List<Condition> conds = new ArrayList<>();
        if (columns_where != null && !CollectionKit.isArrayEmpty(columns_where)) {
            for (int i = 0; i < columns_where.length; i++) {
                if (values_where[i] == null) {
                    conds.add(createField(columns_where[i]).isNull());
                } else if(values_where[i] instanceof String){
                    conds.add(createField(columns_where[i]).eq(createFieldValueString(values_where[i])));
                /*} else if(values_where[i] instanceof Integer){
                    conds.add(createField(columns_where[i]).eq(createFieldValue(values_where[i])));*/
                } else {
                    conds.add(createField(columns_where[i]).eq((Field) createFieldValue(values_where[i])));
                }
            }
        }else {
             return null;
        }
        return conds;
    }

    public static Field<?>[] convertObjecyArrayToFieldValueArray(Object[] arrayObj){
        Field<?>[] fields = new Field[arrayObj.length];
        for(int i=0; i < arrayObj.length; i++){
            fields[i] = createFieldValue(arrayObj[i]);
        }
        return fields;
    }


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
