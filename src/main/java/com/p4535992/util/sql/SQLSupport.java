package com.p4535992.util.sql;

import com.p4535992.util.log.SystemLog;
import com.p4535992.util.reflection.ReflectionKit;
import com.p4535992.util.string.StringKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 06/05/2015.
 */
public class SQLSupport<T>{
    private Class<T> cl;
    private String clName;

    //CONSTRUCTOR
    public SQLSupport() {
    }

    private String[] COLUMNS;
    private Object[] VALUES;
    private int[] TYPES;
    private SQLSupport support;

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

    public SQLSupport(String[] columns,Object[] values,int[] types){
        this.COLUMNS=columns;
        this.VALUES = values;
        this.TYPES = types;
    }

    public SQLSupport(T object){
        support = insertSupport(object);
        this.COLUMNS= support.getCOLUMNS();
        this.VALUES = support.getVALUES();
        this.TYPES = support.getTYPES();
    }

    public SQLSupport(List<String> columns,List<Object> values,List<Integer> types){
        String[] acolumns = new String[columns.size()];
        Object[] avalues = new Object[values.size()];
        int[] atypes = new int[types.size()];
        for(int i = 0; i < columns.size(); i++) {
            acolumns[i] = columns.get(i);
            avalues[i] = values.get(i);
            atypes[i] = types.get(i);
        }
        this.COLUMNS=acolumns;
        this.VALUES = avalues;
        this.TYPES = atypes;
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
     * @ATTENTION: you need to be sure all the getter have reference to a field with a hibernate annotation and the attribute column.
     * @ATTENTION: you need all field of the object class have a hibernate annotation and the attribute column, or at least
     * a personal annotation with the attribute column and a value who is the column of the column.
     * @param object
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public static <T> SQLSupport insertSupport(T object){
        SQLSupport support = new SQLSupport();
        try {
            Map<String, Class> l = ReflectionKit.inspectAndLoadGetterObject(object);
            Object[] values = new Object[l.size()];
            int[] types = new int[l.size()];
            String[] columns = new String[l.size()];
            List<Method> methods = ReflectionKit.getGettersClassOrder(object.getClass());
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < l.size(); i++) {
                Method method = methods.get(i);
                //Method method = ReflectionKit.getMethodByNameAndParam(object, entry.getKey().toString(), null);
                values[i] = ReflectionKit.invokeGetterMethod(object, method);
                Class clazz = fields[i].getType();
                types[i] = SQLHelper.convertClass2SQLTypes(clazz);
                //System.out.println(method+","+values[i]+","+types[i]);
            }
            int i = 0;
            List<List<Object[]>> ssc = ReflectionKit.getAnnotationsFields(object.getClass());
            for (List<Object[]> list : ssc) {
                int j = 0;
                boolean flag = false;
                while (j < list.size()) {
                    int k = 0;
                    while (k < list.get(j).length) {
                       if(list.get(j)[k] instanceof Object[]) {
                           Object[] obj = (Object[]) list.get(j)[k];
                           int g = 0;
                           while (g < obj.length) {
                               if (obj[g].equals("name")) {
                                   columns[i] = obj[++g].toString();
                                   flag = true;
                                   break;
                               }
                               g++;
                           }
                       }
                        if (flag == true) break;
                        k++;
                    }
                    if (flag == true) break;
                    j++;
                }
                i++;
            }
        support = new SQLSupport(columns,values,types);
        }catch(IllegalAccessException|NoSuchMethodException|
                InvocationTargetException|NoSuchFieldException e){
            SystemLog.exception(e);
        }
        return support;
    }

    public static Class[] getArrayClassesTypes(Class<?> clazz, Class<? extends Annotation> aClass){
        return ReflectionKit.getClassesByFieldsByAnnotation(clazz,aClass);
    }

    public static Integer[] getArrayTypes(Class<?> clazz, Class<? extends Annotation> aClass){
        List<Integer> types = new ArrayList<>();
        Class[] classes = ReflectionKit.getClassesByFieldsByAnnotation(clazz, aClass);
        //GET TYPES SQL
        for(Class cl: classes){
            types.add(SQLHelper.convertClass2SQLTypes(cl));
        }
        return StringKit.convertListToArray(types);
    }

    public static String[] getArrayColumns(Class<?> clazz, Class<? extends Annotation> aClass,String attributeNameColumnAnnotation) throws NoSuchFieldException {
        List<List<Object[]>> test4 = ReflectionKit.getAnnotationsFields(clazz,aClass);
        int j,i,x;
        boolean found = false;
        String[] columns = new String[test4.size()];
        j=0;
        for(List<Object[]> list : test4){
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
            Method method = ReflectionKit.findSetterMethod(iClass,column,value);
            Object[] values = new Object[]{value};
            iClass = ReflectionKit.invokeSetterMethodForObject(iClass, method, values);
            return iClass;
        } catch (IllegalAccessException|
                InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
