package com.github.p4535992.util.database.sql;


import com.github.p4535992.util.collection.CollectionUtilities;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.reflection.ReflectionUtilities;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by 4535992 on 06/05/2015.
 * @author 4535992
 * @version 2015-06-26
 * @param <T> generic tag.
 */
@SuppressWarnings("unused")
public class SQLSupport<T>{
    private Class<? extends T> cl;

    //CONSTRUCTOR
    public SQLSupport() {}

    private String[] COLUMNS;
    private Object[] VALUES;
    private int[] TYPES;

    public String[] getCOLUMNS() {
        return COLUMNS;
    }

    public void setCOLUMNS(String[] COLUMNS) {
        this.COLUMNS = COLUMNS;
    }

    public Object[] getVALUES() {
        return VALUES;
    }

    public void setVALUES(Object[] VALUES) {
        this.VALUES = VALUES;
    }

    public int[] getTYPES() {
        return TYPES;
    }

    public void setTYPES(int[] TYPES) {
        this.TYPES = TYPES;
    }

    @SuppressWarnings("rawtypes")
    protected <T> SQLSupport(String[] columns,Object[] values,int[] types){
        //this.cl = ReflectionKit.getTheParentGenericClass(object.getClass());
        this.COLUMNS=columns;
        this.VALUES = values;
        this.TYPES = types;
    }

    @SuppressWarnings("rawtypes")
    protected <T> SQLSupport(T object){
        //this.cl = ReflectionKit.getTheParentGenericClass(object.getClass());
        this.COLUMNS = null;
        this.VALUES = null;
        this.TYPES = null;
        SQLSupport support = SQLSupport.insertSupport(object);
        this.COLUMNS= support.getCOLUMNS();
        this.VALUES = support.getVALUES();
        this.TYPES = support.getTYPES();
    }

    @SuppressWarnings("rawtypes")
    protected SQLSupport(List<String> columns,List<Object> values,List<Integer> types){
        //this.cl = ReflectionKit.getTheParentGenericClass(object.getClass());
        this.COLUMNS= CollectionUtilities.toArray(columns);
        this.VALUES = CollectionUtilities.toArray(values);
        this.TYPES = CollectionUtilities.toPrimitive(CollectionUtilities.toArray(types));
    }

    @SuppressWarnings("rawtypes")
    private static SQLSupport instance = null;
    @SuppressWarnings("rawtypes")
    public static <T> SQLSupport getInstance(T object,boolean isNull){
        if(isNull)instance=null;
        return getInstance(object);
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    public static <T> SQLSupport getInstance(T object){
        if(instance == null) {
            instance = new SQLSupport(object);
        }
        return instance;
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    public static <T> SQLSupport getInstance(String[] columns,Object[] values,Integer[] types){
        if(instance == null) {
            instance = new SQLSupport(columns,values,CollectionUtilities.toPrimitive(types));
        }
        return instance;
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    public static <T> SQLSupport getInstance(List<String> columns,List<Object> values,List<Integer> types){
        if(instance == null) {
            instance = new SQLSupport(columns,values,types);
        }
        return instance;
    }

    @Override
    public String toString() {
        return "SQLSupport{" +
                "COLUMNS=" + Arrays.toString(COLUMNS) +
                ", VALUES=" + Arrays.toString(VALUES) +
                ", TYPES=" + Arrays.toString(TYPES) +
                '}';
    }

    /**
     * Method for get from a java class all the information you need for insert
     * a data in a database a homemade very very very base similar hibernate usage
     * ATTENTION: you need to be sure all the getter have reference to a field with a hibernate annotation and the attribute column.
     * ATTENTION: you need all field of the object class have a hibernate annotation and the attribute column, or at least
     * a personal annotation with the attribute column and a value who is the column of the column.
     * @param object complex object ot inspect.
     * @param <T>  generic variable.
     * @return obecjt SQLSupport.
     */
    
    @SuppressWarnings("rawtypes")
    public static <T> SQLSupport insertSupport(T object){
        SQLSupport support = new SQLSupport();
        String attributeAnnotationKey ="name";
        try {
            Field[] fields = ReflectionUtilities.getFieldsByAnnotation(object.getClass(), Column.class);
            Object[] values = new Object[fields.length];
            int[] types = new int[fields.length];
            String[] columns = new String[fields.length];
            Collection<Method> methods = ReflectionUtilities.findGetters(object.getClass(), true);
            //Field[] fields = object.getClass().getDeclaredFields();
            int i = 0;
            for (Method method: methods) {
                if(method!=null){
                    values[i] = ReflectionUtilities.invokeGetter(object, method);
                    Class<?> clazz = fields[i].getType();
                    types[i] = SQLHelper.convertClass2SQLTypes(clazz);
                    i++;
                }
            }
            i=0;
            Collection<Collection<Object[]>> ssc = ReflectionUtilities.findInfoAnnotationsFields(object.getClass());
            for (Collection<Object[]> col : ssc) {
                List<Object[]> list = (List<Object[]>) col;
                if(list.size() >0) {
                    int j = 0;
                    boolean flag = false;
                    while (j < list.size()) {
                        int k = 0;
                        while (k < list.get(j).length) {
                            if (list.get(j)[k] instanceof Object[]) {
                                Object[] obj = (Object[]) list.get(j)[k];
                                int g = 0;
                                while (g < obj.length) {
                                    if (obj[g].equals(attributeAnnotationKey)) {
                                        columns[i] = obj[++g].toString();
                                        flag = true;
                                        break;
                                    }
                                    g++;
                                }
                            }
                            if (flag) break;
                            k++;
                        }
                        if (flag) break;
                        j++;
                    }
                    i++;
                }//if list.size() > 0
            }//for each ssc
        support = new SQLSupport(columns,values,types);
        }catch(IllegalAccessException|NoSuchMethodException|
                InvocationTargetException|NoSuchFieldException e){
            SystemLog.exception(e);
        }
        return support;
    }


    public static Class<?>[] getArrayClassesTypes(Class<?> clazz, Class<? extends Annotation> aClass){
        return ReflectionUtilities.getClassesByFieldsByAnnotation(clazz,aClass);
    }

    public static Integer[] getArrayTypes(Class<?> clazz, Class<? extends Annotation> aClass){
        List<Integer> types = new ArrayList<>();
        Class<?>[] classes =  ReflectionUtilities.getClassesByFieldsByAnnotation(clazz, aClass);
        //GET TYPES SQL
        for(Class<?> cl: classes){
            types.add(SQLHelper.convertClass2SQLTypes(cl));
        }
        return CollectionUtilities.toArray(types);
    }

    public static String[] getArrayColumns(Class<?> clazz, Class<? extends Annotation> aClass,String attributeNameColumnAnnotation)
            throws NoSuchFieldException {
        Collection<Collection<Object[]>> test4 = ReflectionUtilities.findInfoAnnotationsFields(clazz,aClass);
        int j,i,x;
        boolean found;
        String[] columns = new String[test4.size()];
        j=0;
        for(Collection<Object[]> col : test4){
            List<Object[]> list = (List<Object[]>) col;
            i = 0;
            found = false;
            while(i < list.size()){
                x = 0;
                while (x < list.get(i).length) {
                    if (list.get(i)[x].toString().equals(attributeNameColumnAnnotation)) {
                        columns[j] = String.valueOf(list.get(i)[++x]);
                        j++;
                        found = true;
                        break;
                    }
                    x++;
                }
                if(found) break;
                else i++;
            }
        }
        return  columns;
    }

    public static <T> T invokeSetterSupport(T iClass, String column, Object value) throws NoSuchFieldException {
        try {
            Method method = ReflectionUtilities.findSetter(iClass, column, value);
            Object[] values = new Object[]{value};
            iClass = ReflectionUtilities.invokeSetter(iClass, method, values);
            return iClass;
        } catch (IllegalAccessException|
                InvocationTargetException|NoSuchMethodException e) {
            SystemLog.exception(e);
        }
        return null;
    }

    /*public static Map<org.jooq.Field,org.jooq.Field> convertSQLSupportToMapJOOQField(
            String[] columns,Object[] values,int[] types,SQLDialect sqlDialect){
        org.jooq.Field[] fields = new org.jooq.Field[columns.length];
        org.jooq.Field[] fv = new org.jooq.Field[columns.length];
        //DataType[] dataTypes = new DataType[types.length];
        for(int i=0; i < columns.length; i++){
            org.jooq.Field<String> field = DSL.val(columns[i]);
            fields[i] = field;
            if(types[i]== Types.NULL && values[i]==null) {
                fv[i] = null;
            }else if(values[i]==null){
                fv[i] = null;
            }else {
                DataType dataType = new DefaultDataType(
                        sqlDialect,
                        SQLHelper.convertSQLTypes2JavaClass(types[i]),
                        SQLHelper.convertSQLTypes2String(types[i])
                );
                fv[i] = DSL.val(values[i], dataType);
            }
        }
        Map<org.jooq.Field, org.jooq.Field> map = CollectionKit.convertTwoArrayToMap(fields,fv);
        return map;
    }*/
}
