/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import com.github.p4535992.util.object.model.GeoDocument;
import com.github.p4535992.util.reflection.ReflectionKit;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

/**
 *
 * @author esd91martent
 */
public class Test_Reflection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
         GeoDocument geo = new GeoDocument(
                new URL("http://www.url.com"), "regione", "provincia", "city",
                "indirizzo", "iva", "email", "telefono", "fax",
                "edificio", (Double) 0.0, (Double) 0.0, "nazione", "description",
                "postalCode", "indirizzoNoCAP", "indirizzoHasNumber"
        );

        //CHECK IF FIELD HAS A SPECIFIC ANNOTATION (additional for hibernate)

        List<String> ddd = ReflectionKit.getFieldsNameByClass(GeoDocument.class);
        //List<String[]> test1  = ReflectionKit.getAnnotationsFields(GeoDocument.class);
        //String[] test2 = ReflectionKit.getAnnotationHibernateClass(GeoDocument.class);

        //WORK
//        Field fieldColumn = ReflectionKit.getFieldByName(GeoDocument.class,"url");
//        Class clazz = fieldColumn.getType();
//        Annotation annColumn = fieldColumn.getAnnotation(javax.persistence.Column.class);
//        if(annColumn!=null) {
//            ReflectionKit.updateAnnotationValue(annColumn, "name", "yyyy");
//        }
////////////////////////////////////////////////////////////////////////////////
        //WORK
        /*
        Annotation ann = GeoDocument.class.getAnnotation(javax.persistence.Table.class);
        ReflectionKit.updateAnnotationClassValue(GeoDocument.class,javax.persistence.Table.class, "name", "xxxx");
        String[] test3 = ReflectionKit.getAnnotationHibernateClass(GeoDocument.class);
        */

    ////////////////////////////////////////////////////
        //WORK
        /*
    Map<String,Class> l = ReflectionKit.inspectAndLoadGetterObject(geo);
    Object[] aObj = new Object[l.size()];
    int[] types = new int[l.size()];
    String[] columns = new String[l.size()];
    int i = 0;
    for(Map.Entry<String,Class> entry : l.entrySet()) {
        //Class[] arrayClass = new Class[]{entry.getValue()};
        aObj[i] = ReflectionKit.invokeObjectMethod(geo, entry.getKey().toString(), null, entry.getValue());
        types[i] = SQLKit.convertClass2SQLTypes(entry.getValue());
        i++;
    }
        i = 0;
    List<List<Object[]>> ssc = ReflectionKit.getAnnotationsFields(GeoDocument.class);
    for(List<Object[]> list : ssc){
        int j =0;
        boolean flag = false;
        while(j < list.size()){
            int k = 0;
            while(k < list.get(j).length) {
                if (list.get(j)[k].equals("name")) {
                    columns[i] = list.get(j)[++k].toString();
                    flag = true;
                    break;
                }
                k++;
            }
            if(flag==true)break;
            j++;
        }
        i++;
    }
        */
///////////////////////////////////////////////////////////////////


        //List<List<Object[]>> ssb = ReflectionKit.getAnnotationsFieldsOriginal(GeoDocument.class);



        List<Object[]> ssd = ReflectionKit.getAnnotationField(GeoDocument.class, javax.persistence.Column.class, "url");

        //Annotation annColumn = fieldColumn.getAnnotation(javax.persistence.Column.class);
        //List<Object[]> ssd = ReflectionKit.getAnnotationField(GeoDocument.class,javax.persistence.Column.class,"url");
//        Object[] ssa = ReflectionKit.getAnnotationField(
//                GeoDocument.class,new Anno javax.persistence.Column,"url");
//    for(int j = 0; j < ssa.size(); j++){
//        columns[j] = ssa.get(j)[2].toString();
//    }
       /////////////////////////////////////////////////////////

        //URL url = ReflectionKit.getCodeSourceLocation(GeoDocument.class);
         //List<String[]> ll2 = ReflectionKit.inspectTypesMethod(GeoDocument.class);
         List<String[]> ll = ReflectionKit.inspectFieldClass(geo);
         
         
        String path = ReflectionKit.getClassReference(GeoDocument.class);
        List<Method> methods = ReflectionKit.getGettersSettersClass(GeoDocument.class);
        
        methods = ReflectionKit.getMethodsByClass(GeoDocument.class);
        
        Class<?>[] param = new Class<?>[]{String.class};
        
        Method met1 = ReflectionKit.getMethodByNameAndParam(GeoDocument.class, "setCity", param);
        
        Method met2 =ReflectionKit.getMethodByNameAndParam(GeoDocument.class, "getCity", null);
        
        Class<?>[] param2 = ReflectionKit.getParametersTypeMethod(met1);
        
        Class<?> aclass2 = ReflectionKit.getReturnTypeMethod(met2);
        Class<?> aclass1 = ReflectionKit.getReturnTypeMethod(met1);

    }
}



