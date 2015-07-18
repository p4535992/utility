package com.github.p4535992.util.database.jooq;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.database.sql.SQLHelper;
import org.jooq.*;
import org.jooq.impl.CustomCondition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.TableImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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
    private static Table table;
    private static SQLJooqKit instance = null;
    private static Query query;
    private static Condition condition;

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
        return insert(nameTable,columns,values,types,false);
    }


    public static String insert(String nameTable,String[] columns,Object[] values,int[] types,boolean preparedStatement){
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
    }
    public static String select(String nameTable,String[] columns){
        return select(nameTable,columns,false);
    }

    public static String select(String nameTable,String[] columns,boolean preparedStatement){
        Field[] fields = new Field[columns.length];
        for(int i=0; i < columns.length; i++){
            Field<String> field = DSL.val(columns[i]);
            fields[i] = field;
        }
        table = new TableImpl(nameTable);
        query =  dslContext.select(fields).from(nameTable);
        if(preparedStatement){return query.getSQL();
        }else {return query.toString();
        }

    }

    public static void createCondition(Field field1,Field field2){
        Condition cond = field1.eq(field2);
    }

    public static SelectQuery addConditionToSelectQuery(List<Condition> condition){
        SelectQuery sQuery = (SelectQuery) query;
        sQuery.addConditions(condition);
        query = sQuery;
        return sQuery;
    }

    public static UpdateQuery addConditionToUpdateQuery(List<Condition> condition){
        UpdateQuery sQuery = (UpdateQuery) query;
        sQuery.addConditions(condition);
        query = sQuery;
        return sQuery;
    }

    public static DeleteQuery addConditionToDeleteQuery(List<Condition> condition){
        DeleteQuery sQuery = (DeleteQuery) query;
        sQuery.addConditions(condition);
        query = sQuery;
        return sQuery;
    }

    public static InsertQuery getInsertQuery(List<Condition> condition){
        InsertQuery sQuery = (InsertQuery) query;
        query = sQuery;
        return sQuery;
    }

    public void setConnection(Connection connection,SQLDialect dialect){
        dslContext = DSL.using(connection, dialect);
    }

    public static void setConnection(Configuration configuration) {
        dslContext = DSL.using(configuration);
    }

    public static void getMySQLConnection(String host,String port,String database,String username,String password){
        try {
            Connection connection = SQLHelper.getMySqlConnection(host,port,database,username,password);
            dslContext = DSL.using(connection, SQLDialect.MYSQL);
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Table createTable(String nameTable){
        table = new TableImpl(nameTable);
        return table;
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

    public static void main(String[] args) {
        String[] columns = new String[]{"col1","col2","col3"};
        Object[] values =new Object[]{1,"test",null};
        int[] types = new int[]{Types.INTEGER,Types.VARCHAR,Types.NULL};
        getMySQLConnection("localhost","3306","geodb","siimobility","siimobility");
        String query = insert("tabl1",columns,values,types);
        System.out.println(query);
        query = insert("tabl1",columns,values,types,true);
        System.out.println(query);
        query = select("tabl1", columns);
        System.out.println(query);
        query = select("tabl1",columns,true);
        System.out.println(query);
    }
}
