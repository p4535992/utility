package com.github.p4535992.util.reflection.impl;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.log.SystemLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by 4535992 on 25/10/2015.
 */
public class ReflectionFindInfo {


    //-----------------------------------------------------------

    /**
     * Method to get the properties of a Annotation on a class.
     * OLD_NAME: getAnnotationClass
     * @param annotation the Annotation object to inpsect.
     * @return properteis of the specific annotation.
     */
    @SuppressWarnings("unchecked")
    public static List<Object[]> findInfoAnnotationClass(Annotation annotation){
        List<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        if(annotation!=null) {
            Object handler = Proxy.getInvocationHandler(annotation);
            Field f;
            try {
                //This is the name of the field.
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            f.setAccessible(true);
            Map<String, Object> memberValues;
            try {
                memberValues = (Map<String, Object>) f.get(handler);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            array[0] = annotation.annotationType().getName();//javax.persistence.column
            for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                array[1] = entry.getKey();
                array[2] = entry.getValue();
                list.add(array.clone());
            }
            return list;
        }
        return null;
    }

    /**
     * Method for get the properties of a Annotation form a Field.
     * OLD_NAME: getAnnotationField
     * @param annotation Annotation object to inspect.
     * @return a list of properties of the Annotation.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static List<Object[]> findInfoAnnotationField(Annotation annotation)
            throws SecurityException, NoSuchFieldException {
        List<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        if(annotation!=null) {
            Object handler = Proxy.getInvocationHandler(annotation);
            Field f;
            try {
                //This is the name of the field.
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            f.setAccessible(true);
            Map<String, Object> memberValues;
            try {
                memberValues = (Map<String, Object>) f.get(handler);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            array[0] = annotation.annotationType().getName();//javax.persistence.column
            for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                array[1] = entry.getKey();
                array[2] = entry.getValue();
                list.add(array.clone());
            }
            return list;
        }
        return null;
    }

    //    public static List<Object[]> getAnnotationField(Class<?> aClass, Annotation annotation,String fieldName )
//            throws SecurityException, NoSuchFieldException {
//        Field field = getFieldByName(aClass,fieldName);
//        Annotation annotation2 = field.getAnnotations();
//        return getAnnotationField(annotation2);
//    }

    /**
     * Method to get all Annotated Field on a specific Class.
     * OLD_NAME: getAnnotationsFields.
     * @param aClass the Class where search the Annotated Field.
     * @param classAnnotation the Annotation to Search.
     * @return tha List of all properties of the annotation on the Field.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    public static  List<List<Object[]>> findInfoAnnotationsFields(Class<?> aClass,Class<? extends Annotation> classAnnotation)
            throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> result = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for(Field field : fields){
            Annotation ann = field.getAnnotation(classAnnotation);
            List<Object[]> list = findInfoAnnotationField(ann);
            result.add(list);
        }
        return result;
    }

    /**
     * Method to get the properties of a Annotation on a Field.
     * OLD_NAME: getAnnotationField.
     * @param annotationField the Annotation Field to search.
     * @param field the the Field.
     * @return tha List of all properties of the annotation on the Field.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static List<Object[]> findInfoAnnotationField(Class<? extends Annotation> annotationField,Field field)
            throws SecurityException, NoSuchFieldException {
        List<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        final Annotation annotation = field.getAnnotation(annotationField);
        if(annotation!=null) {
            Object handler = Proxy.getInvocationHandler(annotation);
            Field f;
            try {
                //This is the name of the field.
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            f.setAccessible(true);
            Map<String, Object> memberValues;
            try {
                memberValues = (Map<String, Object>) f.get(handler);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            array[0] = annotation.annotationType().getName();//javax.persistence.column
            for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                array[1] = entry.getKey();
                array[2] = entry.getValue();
                list.add(array.clone());
            }
            return list;
        }
        return null;
    }

    /**
     * Method to get the properties of a Annotation on a Field.
     * OLD_NAME: getAnnotationField.
     * @param aClass the Class of the Field.
     * @param annotationField the Annotation Field to search.
     * @param fieldName the name of the Field.
     * @return tha List of all properties of the annotation on the Field.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    public static List<Object[]> findInfoAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationField,String fieldName )
            throws SecurityException, NoSuchFieldException {
        return findInfoAnnotationField(annotationField, ReflectionKit3.getFieldByName(aClass, fieldName));
    }

    /**
     * Method for get all the field with at least a annotation on a specific class.
     * OLD_NAME: getAnnotationsFieldsOriginal.
     * @param aClass class to inspect.
     * @return list of all the properties of the fields with a annotation.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    /*@SuppressWarnings("unchecked")
    public static List<List<Object[]>> findAnnotationsFieldsOriginal(Class<?> aClass) throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> listOfAnnotation = new ArrayList<>();
        //for(Annotation ann : aClass.getAnnotations()){
        Field[] fields = aClass.getDeclaredFields();
        for(Field field  : fields ){
            Object[] array = new Object[3];
            //final Annotation annotation = field.getAnnotation(ann.getClass());//??????????
            for(Annotation annotation : field.getAnnotations()) {
                if (annotation != null) {
                    Object handler = Proxy.getInvocationHandler(annotation);
                    Field f;
                    try {
                        //This is the name of the field.
                        f = handler.getClass().getDeclaredField("memberValues");
                    } catch (NoSuchFieldException | SecurityException e) {
                        throw new IllegalStateException(e);
                    }
                    f.setAccessible(true);
                    Map<String, Object> memberValues;
                    try {
                        memberValues = (Map<String, Object>) f.get(handler);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                    List<Object[]> list = new ArrayList<>();
                    array[0] = annotation.annotationType().getName();//javax.persistence.column
                    for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                        array[1] = entry.getKey();
                        array[2] = entry.getValue();
                        list.add(array.clone());
                    }
                    listOfAnnotation.add(list);
                }
            }
        }
        //}
        return listOfAnnotation;
    }*/

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * OLD_NAME:  getAnnotationsFields.
     * @param aClass class you want to inspect.
     * @return list of list of arrays name-value of all annotation on the declareted types.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws SecurityException throw if any error is occurrred.
     * @throws InvocationTargetException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
    public static List<List<Object[]>> findInfoAnnotationsFields(Class<?> aClass) throws SecurityException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        List<List<Object[]>> listOfAnnotation = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for(Field field  : fields ){
            List<Object[]> list = findInfoAnnotationsField(aClass, field);
            if(list!=null) {
                listOfAnnotation.add(list);
            }
        }
        return listOfAnnotation;
    }

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * OLD_NAME: getAnnotationsField
     * @param aClass class you wan to inspect.
     * @param field field you want o find.
     * @return list of array name-value with all information on the type field.
     * @throws NoSuchFieldException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
    @SuppressWarnings("rawtypes")
    public static List<Object[]> findInfoAnnotationsField(Class<?> aClass, Field field)
            throws SecurityException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        List<Object[]> list = new ArrayList<>();
        String fieldName = field.getName();
        Class fieldType = field.getType();
        Object[] fObj = new Object[]{fieldName,fieldType};
        for(Annotation annotation : field.getAnnotations()) {
            List<Object[]> aObj = findInfoAnnotation(annotation);
            //final Annotation annotation = field.getAnnotation(annotationClass);
            //list = getAnnotationField(aClass, annotation, fieldName);
            if(aObj != null && !aObj.isEmpty()) {
                Object[] bObj = CollectionKit.concatenateArrays(fObj, aObj.toArray());
                list.add(bObj);
            }
        }
        return list;
    }

    /**
     * Method to inspect a Annotation object java.
     * OLD_NAME: inspectAnnotation.
     * @param annotation the annotation to inspect.
     * @return a list of properties of the annotation.
     * @throws InvocationTargetException thow if any error is occurrred.
     * @throws IllegalAccessException thow if any error is occurrred.
     */
    public static List<Object[]> findInfoAnnotation(Annotation annotation) throws InvocationTargetException, IllegalAccessException {
        List<Object[]> list = new ArrayList<>();
        Class<? extends Annotation> type = annotation.annotationType();
        for (Method method : type.getDeclaredMethods()) {
            Object[] aObj = new Object[]{type.getName(),method.getName(),method.invoke(annotation)};
            list.add(aObj);
        }
        return list;
    }

    /**
     * Method for inspect/find Annotation for the class java.
     * OLD_NAME: inspectAnnotationsClass.
     * href: http://tutorials.jenkov.com/java-reflection/annotations.html
     * href: http://stackoverflow.com/questions/20362493/how-to-get-annotation-class-name-attribute-values-using-reflection
     * Usage: @Resource(name = "foo", description = "bar")
     * name: foo
     * type: class java.lang.Object
     * lookup:
     * description: bar
     * authenticationType: CONTAINER
     * mappedName:
     * shareable: true
     * @param aClass class you want to inspect.
     * @return list of arrays name-value of the annotation on the class.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws IllegalArgumentException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    /*public static List<Object[]> findAnnotationsClass(Class<?> aClass)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        //Annotation[] annotations = aClass.getAnnotations();
        List<Object[]> list = new ArrayList<>();
        for (Annotation annotation : aClass.getAnnotations()) {
            Object[] array = new Object[3];
            Class<? extends Annotation> type = annotation.annotationType();
            array[0] = type.getName();
            for (Method method : type.getDeclaredMethods()){
                array[1] = method.getName();
                array[2] = method.invoke(annotation);//annotation,null deprecated
                list.add(array);
            }
        }
        return list;
    }*/

    /**
     * Method to get the properties of a Annotation on a class.
     * OLD_NAME: getAnnotationsClass
     * @param aClass classs to inspect.
     * @return properties of the specific annotation.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    public static List<List<Object[]>> findInfoAnnotationsClass(Class<?> aClass)
            throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> result = new ArrayList<>();
        for(Annotation annotation : aClass.getAnnotations()){
            List<Object[]> list = findInfoAnnotationClass(annotation);
            result.add(list);
        }
        return result;
    }

    /**
     * Method for inspect/find Annotation for the Method java.
     * OLD_NAME: inspectAnnotationMethodClass.
     * @param aClass class you want to inspect.
     * @param methodName the name of the Method.
     * @return List of all String Array of Properties of the Annotation on the Methods.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws IllegalArgumentException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    public static List<String[]> findInfoAnnotationsMethod(Class<?> aClass,String methodName)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
        Method method = aClass.getMethod(methodName);//methodName,null deprecated
        List<String[]> list = new ArrayList<>();
        String[] array = new String[3];
        for (Annotation annotation : aClass.getAnnotations()){
            Class<? extends Annotation> type = annotation.annotationType();
            array[0] = type.getName();
            array[1] = Arrays.toString(type.getFields());
            array[2] = method.getName();
            list.add(array);
        }
        return list;
    }

    /**
     * Method to get all basic information on a method.
     * OLD_NAME: inspectFieldClass.
     * @param obj object to inspect.
     * @return list of array name-value for that object.
     */
    public static List<String[]> findInfoFieldClass(Object obj) {
        return findInfoFieldClass(obj.getClass());
    }

    /**
     * Method to get all basic information on a method
     * @return list of array name-value for that object.
     */
  /*  public static List<String[]> inspectFieldClass() {
        return inspectFieldClass(object.getClass());
    }*/

    /**
     * Method to get all basic information on a method.
     * OLD_NAME: inspectFieldClass.
     * @param clazz Class to inspect.
     * @return list of array name-value for that object.
     */
    public static List<String[]> findInfoFieldClass(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String[]> oField = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true); // if you want to modify private fields
            String modify = Modifier.toString(field.getModifiers());
            String type = field.getType().getSimpleName();
            String name = field.getName();
            oField.add(new String[]{modify,type,name});
        }
        return oField;
    }

    /**
     * Method to get all Filed with all the property annotation on them.
     * OLD_NAME: inspectFieldAndAnnotationClass.
     * @param clazz Class to inspect.
     * @return Map of Field and Array of Annotation.
     */
    public static Map<Field,Annotation[]> findInfoFieldAndAnnotationClass(Class<?> clazz){
        Map<Field,Annotation[]> map = new HashMap<>();
        for(Field field : clazz.getDeclaredFields()){
            Annotation[] annotations = field.getDeclaredAnnotations();
            map.put(field, annotations);
        }
        return map;
    }

    /**
     * Method to find all the Types on a Method.
     * OLD_NAME: inspectTypesMethod.
     * @param clazz the Class where you inspect the Method.
     * @param nameOfMethhod the name of the Method to inspect.
     * @return the list of Array Properties of the Method.
     */
    public static List<String[]> findInfoTypes(Class<?> clazz,String nameOfMethhod) {
        List<String[]> list = new ArrayList<>();
        try {
            Method method = clazz.getMethod(nameOfMethhod);
            Type returnType = method.getGenericReturnType();
            if(returnType instanceof ParameterizedType){
                ParameterizedType type = (ParameterizedType) returnType;
                Type[] typeArguments = type.getActualTypeArguments();
                for(Type typeArgument : typeArguments){
                    Class<?> typeArgClass = (Class<?>) typeArgument;
                    list.add(new String[]{typeArgClass.getName(),ReflectionKit3.getClassReference(typeArgClass)});
                }
            }
        } catch (NullPointerException|NoSuchMethodException e) {
            SystemLog.exception(e);
        }
        return list;
    }

    /**
     * Method to find all the Types on a Method.
     * OLD_NAME: inspectTypesMethod.
     * @param aClass the Class where you inspect the Method.
     * @param method the Method to inspect.
     * @return the list of Array Properties of the Method.
     */
    /*public static List<String[]> findTypes(Class<?> aClass,Method method){
        List<String[]> list = new ArrayList<>();
        try {
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) returnType;
                Type[] typeArguments = type.getActualTypeArguments();
                for (Type typeArgument : typeArguments) {
                    Class<?> typeArgClass = (Class<?>) typeArgument;
                    System.out.println("typeArgClass = " + typeArgClass);
                    list.add(new String[]{typeArgClass.getName(), ReflectionKit3.getClassReference(typeArgClass)});

                }
            }
        } catch (NullPointerException e) {
            SystemLog.exception(e);
        }
        return list;
    }*/

    /**
     * Method for inpsect a method on a class.
     * OLD_NAME: inspectSimpleTypesMethod
     * @param aClass the class java where is put the method.
     * @param method the method to inspect.
     * @return a map with all information on the method.
     */
    public static Map<Class<?>,List<String[]>> findInfoTypes(Class<?> aClass,Method method) {
        Map<Class<?>,List<String[]>> map = new HashMap<>();
        List<String[]> list = new ArrayList<>();
        Type returnType = method.getGenericReturnType();
        try {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class<?> typeArgClass = (Class<?>) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(), ReflectionKit3.getClassReference(typeArgClass)});
            }
        } catch (NullPointerException e) {
            SystemLog.exception(e);
        }
        map.put(returnType.getClass(),list);
        return map;
    }

    /**
     * Method to get all Getter Method.
     * OLD_NAME: inspectAndLoadGetterObject.
     * @param obj the Object Class.
     * @return the getter methods.
     * @throws NoSuchMethodException
     */
    public static Map<String,Class<?>> findInfoGetter(Object obj) throws NoSuchMethodException{
        List<Method> getter = ReflectionFind.findGetters(obj.getClass(), true);
        Map<String,Class<?>> map = new HashMap<>();
        for(Method met : getter){
            Class<?> cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }

    /**
     * Method to find Setter on a Object Class.
     * OLD_NAME:inspectAndLoadSetterObject.
     * @param obj the object Class.
     * @return the Setter Methods on the Class.
     * @throws NoSuchMethodException throw if any error is occurrred.
     */
    public static Map<String,Class<?>> findInfoSetter(Object obj) throws NoSuchMethodException{
        List<Method> setter = ReflectionFind.findSetters(obj.getClass(), true);
        Map<String,Class<?>> map = new HashMap<>();
        for(Method met : setter){
            Class<?> cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }

    /**
     * Method to find a specific constructor.
     * OLD_NAME: inspectConstructor
     * @param aClass the Class where search the constructor.
     * @return the List of array of String property of the constructor.
     */
    public static List<String[]> findInfoConstructor(Class<?> aClass){
        Constructor<?>[] constructors = aClass.getConstructors();
        List<String[]> oConst = new ArrayList<>();
        for (Constructor<?> cons : constructors) {
            String modify = Modifier.toString(cons.getModifiers());
            String type = Arrays.toString(cons.getTypeParameters());
            String name = cons.getName();
            oConst.add(new String[]{modify,type,name});
        }
        return oConst;
    }







}
