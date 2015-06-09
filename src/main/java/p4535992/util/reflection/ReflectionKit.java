package p4535992.util.reflection;


import p4535992.util.log.SystemLog;
import p4535992.util.string.StringKit;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class for help me with the first impact with java reflection library
 * To follow all the url help me to create this class:
 * @href: http://my.safaribooksonline.com/video/-/9780133038118?cid=2012-3-blog-video-java-socialmedia
 * @href: http://www.asgteach.com/blog/?p=559
 * @href: http://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
 * @href: http://roadtobe.com/supaldubey/creating-and-reading-annotations-reflection-in-java/ (other util)
 * @href: https://github.com/dancerjohn/LibEx/blob/master/libex/src/main/java/org/libex/reflect/ReflectionUtils.java (href)
 * @href: http://www.java2s.com/Code/Java/Reflection/Findasettermethodforthegiveobjectspropertyandtrytocallit.htm
 * @param <T>
 */
public class ReflectionKit<T>{

    private static final String GET = "get";
    private static final String IS = "is";
    private static final String SET = "set";
    private Class<T> cl;
    private static String clName;
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    /**
     * Method to check if a specific class is a primitve class
     * @param aClass
     * @return
     */
    public static boolean isWrapperType(Class<?> aClass) {
        return WRAPPER_TYPES.contains(aClass);
    }

    /**
     * List of all primitve class
     * @return
     */
    private static Set<Class<?>> getWrapperTypes(){
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

    public ReflectionKit(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    /** Method to get all basic information on a method */
    public static List<String[]> inspectFieldClass(Object obj) {
        return inspectFieldClass(obj.getClass());
    }

    public static List<String[]> inspectFieldClass(Class<?> aClass) {
        Field[] fields = aClass.getDeclaredFields();
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

    public static Map<Field,Annotation[]> inspectFieldAndAnnotationClass(Class<?> aClass){
        Map<Field,Annotation[]> map = new HashMap<>();
        for(Field field : aClass.getDeclaredFields()){
            Annotation[] annotations = field.getDeclaredAnnotations();
            map.put(field, annotations);
        }
        return map;
    }

    public static List<String[]> inspectTypesMethod(Class<?> aClass,String nameOfMethhod) throws NoSuchMethodException{
        List<String[]> list = null;
        Method method = aClass.getMethod(nameOfMethhod);//nameOfMethhod, null
        Type returnType = method.getGenericReturnType();
        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
            }
        }
        return list;
    }

    public static List<String[]> inspectTypesMethod(Class<?> aClass,Method method) throws NoSuchMethodException{
        List<String[]> list = null;
        Type returnType = method.getGenericReturnType();
        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
            }
        }
        return list;
    }

    public static Class inspectSimpleTypesMethod(Class<?> aClass,Method method) throws NoSuchMethodException{
        List<String[]> list = null;
        Type returnType = method.getGenericReturnType();
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
            }
        return returnType.getClass();
    }

    public static Map<String,Class> inspectAndLoadGetterObject(Object obj) throws NoSuchMethodException{
        List<Method> getter = getGettersClass(obj.getClass());
        Map<String,Class> map = new HashMap<>();
        for(Method met : getter){
            Class cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }

    public static Map<String,Class> inspectAndLoadSetterObject(Object obj) throws NoSuchMethodException{
        List<Method> setter = getSettersClass(obj.getClass());
        Map<String,Class> map = new HashMap<>();
        for(Method met : setter){
            Class cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }

    public static Integer countGetterAndsetter(Class<?> aClass){
        return getGettersSettersClass(aClass).size()/2;
    }

//    public static List<String[]> inspectMethods(Class<?> aClass){
//        Method[] methods = aClass.getMethods();
//        List<String[]> oMethod = new ArrayList<String[]>();
//        for (Method method : methods) {        
//            String modify = method.
//            String type = field.getType().getSimpleName();
//            String name = field.getName();
//            oMethod.add(new String[]{modify,type,name});
//        }
//        return oMethod;
//    }
//    
//    public static List<String[]> inspectAnnotations(Class<?> aClass) {
//        Annotation[] annotations = aClass.getAnnotations();
//        List<String[]> oAnn = new ArrayList<String[]>();
//        for (Annotation ann : annotations) {           
//            String test = ann.annotationType().
//            oAnn.add(new String[]{modify,type,name});
//        }
//        return oAnn;
//    }

    public static List<String[]> inspectConstructor(Class<?> aClass){
        Constructor[] constructors = aClass.getConstructors();
        List<String[]> oConst = new ArrayList<>();
        for (Constructor cons : constructors) {
            String modify = Modifier.toString(cons.getModifiers());
            String type = cons.getTypeParameters().toString();
            String name = cons.getName();
            oConst.add(new String[]{modify,type,name});
        }
        return oConst;
    }

    public static List<Method> getGettersSettersClass(Class<?> aClass) {
        List<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method) || isSetter(method))
                list.add(method);
        return list;
    }

    public static List<Method> getGettersClass(Class aClass){
        List<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method))
                list.add(method);
        return list;
    }

     public static List<Method> getSettersClass(Class aClass){
        List<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isSetter(method))
                list.add(method);
        return list;
    }

    public static List<Method> getSettersClassOrder(Class<?> clazz){
        List<Method> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Method method = findSetterMethod(clazz, field.getName(), field.getType());
            list.add(method);
        }
        return list;
    }

    public static List<Method> getGettersClassOrder(Class<?> clazz){
        List<Method> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Method method = findGetterMethod(clazz, field.getName(), field.getType());
            list.add(method);
        }
        return list;
    }

    public static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[A-Z].*") &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is[A-Z].*") &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return false;
    }

    public static boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getReturnType().equals(void.class) &&
                method.getParameterTypes().length == 1 &&
                method.getName().matches("^set[A-Z].*");
    }

    public static boolean isGetter2(Method method){
        if(!method.getName().startsWith("get"))      return false;
        if(method.getParameterTypes().length != 0)   return false;
        if(void.class.equals(method.getReturnType())) return false;
        return true;
    }

    public static boolean isSetter2(Method method){
        if(!method.getName().startsWith("set")) return false;
        if(method.getParameterTypes().length != 1) return false;
        return true;
    }

    /** Method to get all methods of a class */
    public static List<Method> getMethodsByClass(Class<?> aClass){
        Method[] methods = aClass.getMethods();
        return Arrays.asList(methods);
    }

    /**
     * Method to get a specific method from a class
     *If you know the precise parameter types of the method you want to access,
     * you can do so rather than obtain the array all methods. This example returns
     * the public method named "nameOfMethod", in the given class which takes a String as parameter:
     */
    public static Method getMethodByNameAndParam(Class<?> aClass, String nameOfMethod, Class[] param) throws NoSuchMethodException{
        Method method;
        //If the method you are trying to access takes no parameters, pass null as the parameter type array, like this:   
        if(StringKit.isArrayEmpty(param))method = aClass.getMethod(nameOfMethod,new Class[0]);//nameOfMethod, null
        else method = aClass.getMethod(nameOfMethod,param);// String.class
        return method;
    }

    /**
     * Method to get a specific mehtod form a the reference class of the object
     * @param MyObject
     * @param nameOfMethod
     * @param param
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     */
    public static <T> Method getMethodByNameAndParam(T MyObject, String nameOfMethod, Class[] param) throws NoSuchMethodException{
            return getMethodByNameAndParam(MyObject.getClass(), nameOfMethod, param); //String.class
    }

    /** Method Parameters : Method where you can read what parameters a given method takes like this: */
    public static Class[] getParametersTypeMethod(Method method){
        return method.getParameterTypes();
    }
    /** Return Types: Method where you can access the return type of a method like this: */
    public static Class getReturnTypeMethod(Method method){
        return method.getReturnType();
    }

    private static Field[] getFieldsClass(Class<?> aClass){
        return aClass.getDeclaredFields();
    }

    public static Field getFieldByName(Class<?> aClass,String fieldName) throws NoSuchFieldException {
        try {
            return aClass.getField(fieldName);
        }catch(java.lang.NoSuchFieldException e){
            return aClass.getDeclaredField(fieldName);
        }
    }

    public static List<String> getFieldsNameByClass(Class<?> aClass){
        List<String> names = new ArrayList<>();
        for(Field field : getFieldsClass(aClass)){
            names.add(field.getName());
        }
        return names;
    }

    public static Field[] getFieldsByAnnotation(Class<?> clazz,Class<? extends Annotation> aClass){
        Field[] fields = clazz.getDeclaredFields();
        List<Field> types = new ArrayList<>();
        for(Field field : fields){
            if(field.isAnnotationPresent(aClass)) {
                types.add(field);
            }
        }
        return StringKit.convertListToArray(types);
    }

    public static Class[] getClassesByFieldsByAnnotation(Class<?> clazz,Class<? extends Annotation> aClass){
        Field[] fields = getFieldsByAnnotation(clazz, aClass);
        List<Class> classes = new ArrayList<>();
        for(Field field : fields) {
            classes.add(field.getType());
        }
        return StringKit.convertListToArray(classes);
    }

    /**
     * Method to get the return type of a method in a specific class
     * get method that takes a String as argument
     * The null parameter is the object you want to invoke the method on. If the method is static you supply null instead
     * of an object instance. In this example, if doSomething(String.class) is not static, you need to supply a valid
     * MyObject instance instead of null;The Method.invoke(Object target, Object ... parameters) method takes an optional amount of parameters,
     * but you must supply exactly one parameter per argument in the method you are invoking. In this case it was
     * a method taking a String, so one String must be supplied.
     * @param MyObject
     * @param nameOfMethod
     * @param param
     * @return
     */
    public static <T> T invokeObjectMethod(T MyObject, String nameOfMethod, Class[] param)//4th parameter , Class<T> aClass
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Method method;
        if(param==null || param.length==0 )method = getMethodByNameAndParam(MyObject, nameOfMethod, null);
        else method = getMethodByNameAndParam(MyObject, nameOfMethod, param); //String.class
        try{
            //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
            MyObject = (T) method.invoke(param);
        }catch(java.lang.NullPointerException ne){
            //MyObject = method.invoke(MyObject, param); //...if the methos is non-static
            MyObject = (T) method.invoke(MyObject);
        }
        return MyObject;
    }

    public static Object invokeObjectMethod(Object MyObject,Method method,Class[] param)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        try{
            //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
            MyObject = method.invoke(param);
        }catch(java.lang.NullPointerException ne){
            //MyObject = method.invoke(MyObject, param); //...if the methos is non-static
            MyObject = method.invoke(MyObject);
        }
        return MyObject;
    }

    public static Object invokeObjectMethod(Object MyObject,Method method,Object[] param)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        try{
            //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
            MyObject = method.invoke(param);
        }catch(java.lang.NullPointerException ne){
            //MyObject = method.invoke(MyObject, param); //...if the methos is non-static
            MyObject = method.invoke(MyObject);
        }
        return MyObject;
    }

    public static <T> T invokeSetterClass(T MyObject,String methodName,Object values,Class<?> clazzValue)
    {
        try {
            Method method = getMethodByNameAndParam(
                    MyObject.getClass(),methodName,new Class[]{clazzValue});
            MyObject = (T) method.invoke(MyObject,values);
            return MyObject;
        } catch (InvocationTargetException|IllegalAccessException|
                SecurityException|NoSuchMethodException|
                ClassCastException|NullPointerException  e) {
            SystemLog.exception(e);
        }
        return null;
    }

    public static <T> Object invokeGetterClass(T MyObject,String methodName)
    {
        Object MyObject2;
        try {
            //Object MyObject = clazzValue.cast(new Object());
            Method method = getMethodByNameAndParam(
                   MyObject.getClass(),methodName,new Class[0]);
            MyObject2 = method.invoke(MyObject, new Object[0]);
            return MyObject2;
        } catch (InvocationTargetException|IllegalAccessException|
                SecurityException|NoSuchMethodException  e) {
            SystemLog.exception(e);
        }
        return null;
    }
    
    public static <T> T invokeSetterMethodForObject(T MyObject, Method method, Object values)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        try{
            //if the method you try to invoke is static...
            MyObject = (T) method.invoke(null, values);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject = (T) method.invoke(MyObject, values);
        }
        return MyObject;
    }

    public static Object invokeSetterMethod(Object MyObject, Method method, Object values)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Object MyObject2;
        try{
            //if the method you try to invoke is static...
            MyObject2 = method.invoke(null, values);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 = method.invoke(MyObject, values);
        }
        return MyObject2;
    }

    public static <T> T invokeGetterMethodForObject(T MyObject, Method method)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        try{
            //if the method you try to invoke is static...
            MyObject = (T) method.invoke(null,new Object[0]);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject = (T) method.invoke(MyObject,new Object[0]);
        }
        return MyObject;
    }

    public static Object invokeGetterMethod(Object MyObject,Method method)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Object MyObject2;
        try{
            //if the method you try to invoke is static...
            MyObject2 = method.invoke(null, new Object[0]);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 = method.invoke(MyObject, new Object[0]);
        }
        return MyObject2;
    }

    /**
     * Method to get constructor that takes a String as argument
     * @param MyObject
     * @param param
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> T invokeConstructor(T MyObject, Class[] param, Object[] defaultValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        T myObject = (T) invokeConstructor(MyObject.getClass(), param, defaultValues);
        return myObject;
    }

    public static <T> T invokeConstructor(Class<T> clazz, Class[] param, Object[] defaultValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Constructor constructor = clazz.getConstructor(param);
        T myObject = (T)constructor.newInstance(defaultValues);
        return myObject;
    }

    public static <T> T invokeConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }catch(IllegalAccessException|InstantiationException e){
            SystemLog.exception(e);
        }
        return null;
    }


    public static URL getCodeSourceLocation(Class aClass) {return aClass.getProtectionDomain().getCodeSource().getLocation(); }
    public static String getClassReference(Class aClass){ return aClass.getName();}

    /**Method for get all the class in a package with library reflections */
//    public Set<Class<? extends Object>> getClassesByPackage(String pathToPackage){
//        Reflections reflections = new Reflections(pathToPackage);
//        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
//    }

    public static List<Object[]> inspectAnnotation(Annotation annotation) throws InvocationTargetException, IllegalAccessException {
        List<Object[]> list = new ArrayList<>();
        Class<? extends Annotation> type = annotation.annotationType();
        for (Method method : type.getDeclaredMethods()) {
            Object[] aObj = new Object[]{
                    type.getName(),
                    method.getName(),
                    method.invoke(annotation, new Object[0])};
            list.add(aObj);
        }
        return list;
    }
    /**
     *
     * @param aClass
     * @return
     * @href: http://tutorials.jenkov.com/java-reflection/annotations.html
     * @href: http://stackoverflow.com/questions/20362493/how-to-get-annotation-class-name-attribute-values-using-reflection
     * @Usage: @Resource(name = "foo", description = "bar")
     * name: foo
     * type: class java.lang.Object
     * lookup:
     * description: bar
     * authenticationType: CONTAINER
     * mappedName:
     * shareable: true
     */
    public static List<Object[]> inspectAnnotationsClass(Class<?> aClass)
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
    }

    public static List<String[]> inspectAnnotationMethodClass(Class<?> aClass,String methodName)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
        Method method = aClass.getMethod(methodName);//methodName,null deprecated
        Annotation[] annotations = aClass.getAnnotations();
        List<String[]> list = new ArrayList<>();
        String[] array = new String[3];
        for (Annotation annotation : aClass.getAnnotations()){
            Class<? extends Annotation> type = annotation.annotationType();
            array[0] = type.getName();
            array[1] = type.getFields().toString();
            array[2] = method.getName();
            list.add(array);
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public static List<List<Object[]>> getAnnotationsFieldsOriginal(Class<?> aClass) throws SecurityException, NoSuchFieldException {
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
                        if (null != annotation) {
                            array[0] = annotation.annotationType().getName();//javax.persistence.column
                            for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                                array[1] = entry.getKey();
                                array[2] = entry.getValue();
                                list.add(array.clone());
                            }
                        }
                        listOfAnnotation.add(list);
                    }
                }
            }
        //}
        return listOfAnnotation;
    }

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * @param aClass
     * @return
     * @throws NoSuchFieldException
     */
    @SuppressWarnings("rawtypes")
    public static List<List<Object[]>> getAnnotationsFields(Class<?> aClass) throws SecurityException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        List<List<Object[]>> listOfAnnotation = new ArrayList<>();
          Field[] fields = aClass.getDeclaredFields();
          for(Field field  : fields ){
              List<Object[]> list = getAnnotationsField(aClass, field);
              if(list!=null) {
                  listOfAnnotation.add(list);
              }
          }
        return listOfAnnotation;
    }

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * @required hibernate
     * @param aClass
     * @param field
     * @return
     * @throws NoSuchFieldException
     */
    @SuppressWarnings("rawtypes")
    public static List<Object[]> getAnnotationsField(Class<?> aClass, Field field)
            throws SecurityException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        List<Object[]> list = new ArrayList<>();
        String fieldName = field.getName();
        Class fieldType = field.getType();
        Object[] fObj = new Object[]{fieldName,fieldType};
        for(Annotation annotation : field.getAnnotations()) {
            List<Object[]> aObj = inspectAnnotation(annotation);
            //final Annotation annotation = field.getAnnotation(annotationClass);
            //list = getAnnotationField(aClass, annotation, fieldName);
            if(aObj != null && !aObj.isEmpty()) {
                Object[] bObj = StringKit.concatenateArrays(fObj, aObj.toArray());
                list.add(bObj);
            }
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public static List<Object[]> getAnnotationField(Annotation annotation)
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
            if (null != annotation) {
                array[0] = annotation.annotationType().getName();//javax.persistence.column
                for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                    array[1] = entry.getKey();
                    array[2] = entry.getValue();
                    list.add(array.clone());
                }
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

    public static  List<List<Object[]>> getAnnotationsFields(Class<?> aClass,Class<? extends Annotation> clazz)
            throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> result = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for(Field field : fields){
            Annotation annotation = field.getAnnotation(clazz);
            List<Object[]> list = getAnnotationField(annotation);
            result.add(list);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static List<Object[]> getAnnotationField(Class<? extends Annotation> annotationClass,Field field)
            throws SecurityException, NoSuchFieldException {
        List<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        final Annotation annotation = field.getAnnotation(annotationClass);
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
            if (null != annotation) {
                array[0] = annotation.annotationType().getName();//javax.persistence.column
                for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                    array[1] = entry.getKey();
                    array[2] = entry.getValue();
                    list.add(array.clone());
                }
            }
            return list;
        }
        return null;
    }

    public static List<Object[]> getAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationClass,String fieldName )
            throws SecurityException, NoSuchFieldException {
        return getAnnotationField(annotationClass,getFieldByName(aClass, fieldName));
    }

    public static List<List<Object[]>> getAnnotationsClass(Class<?> aClass)
            throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> result = new ArrayList<List<Object[]>>();
        for(Annotation annotation : aClass.getAnnotations()){
            List<Object[]> list = getAnnotationClass(annotation);
            result.add(list);
        }
        return result;
    }

    public static List<Object[]> getAnnotationClass(Annotation annotation){
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
            if (null != annotation) {
                array[0] = annotation.annotationType().getName();//javax.persistence.column
                for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                    array[1] = entry.getKey();
                    array[2] = entry.getValue();
                    list.add(array.clone());
                }
            }
            return list;
        }
        return null;
    }


    /**
    * Changes the annotation value for the given key of the given annotation to newValue and returns
    * the previous value.
    * @Usage: final Something annotation = (Something) Foobar.class.getAnnotations()[0];
    *         System.out.println("oldAnnotation = " + annotation.someProperty());
    *         changeAnnotationValue(annotation, "someProperty", "another value");
    *         System.out.println("modifiedAnnotation = " + annotation.someProperty());
    * @href: http://stackoverflow.com/questions/14268981/modify-a-class-definitions-annotation-string-parameter-at-runtime/14276270#14276270
    */
   @SuppressWarnings("unchecked")
   private static Object updateAnnotationValue(Annotation annotation, String key, Object newValue){
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
       Object oldValue = memberValues.get(key);
       if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
           throw new IllegalArgumentException();
       }
       memberValues.put(key, newValue);
       return oldValue;
   }

    public static void updateAnnotationFieldValue(
        Class<?> aClass,Class<? extends Annotation> annotationClass,String fieldName,String attributeName,String attributeValue){
        try {
            Field fieldColumn = getFieldByName(aClass, fieldName);
            Annotation annColumn = fieldColumn.getAnnotation(annotationClass);
            if (annColumn != null) {
                ReflectionKit.updateAnnotationValue(annColumn, attributeName, attributeValue);
            }else{
                SystemLog.warning("No annotation "+annColumn+"for the class whit attribute:"+attributeName);
            }
        }catch(NoSuchFieldException e){
            SystemLog.exception(e);
        }
    }

    public static void updateAnnotationClassValue(Class<?> aClass,Class<? extends Annotation> annotationClass,String attributeName,String attributeValue){
        Annotation ann = aClass.getAnnotation(annotationClass);
        if(ann!=null) {
            ReflectionKit.updateAnnotationValue(ann, attributeName,attributeValue);
        }
    }

    public static void updateFieldClass(Class<?> aClass,Object myField,String fieldName)
            throws NoSuchFieldException,IllegalAccessException {
        //Class  aClass = MyObject.class
        Field field = aClass.getField(fieldName);
        //MyObject objectInstance = new MyObject();
        field.setAccessible(true);
        Object value = field.get(myField);
        field.set(myField, value);
    }

    public static void setField(Object object, String fieldName, Object value)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public static Object getFieldValueByName(Object object, String fieldName)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        return getFieldValueByName(object.getClass(), fieldName);
    }

    public static Object getFieldValueByName(Class<?> aClass, String fieldName)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = aClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object returnValue = (Object) field.get(aClass);
        field.setAccessible(false);
        return returnValue;
    }

    public static List<Object> getFieldsValueByClass(Class<?> aClass) throws NoSuchFieldException, IllegalAccessException {
        List<Object> list = new ArrayList<>();
        while(aClass.getSuperclass() != null){
            aClass = aClass.getSuperclass() ;
            //Fetch all fields ..
            for(Field field : aClass.getDeclaredFields()){
                list.add(getFieldValueByName(aClass, field.getName()));
            }
        }
        return list;
    }

    public static Object copyFieldToClass(Object sourceObject, Class targetClass) {
        Object targetValue = null;
        try {
            targetValue = targetClass.newInstance();
            for (Field field : sourceObject.getClass().getDeclaredFields()) {
                try {
                    setField(targetValue, field.getName(), getFieldValueByName(sourceObject, field.getName()));
                } catch (NoSuchFieldException e) {
                    throw new Exception("Ignored Field " + field.getName());
                }
            }
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return targetValue;
    }


    /**
     * Retrieves all fields (all access levels) from all classes up the class
     * hierarchy starting with {@code startClass} stopping with and not
     * including {@code exclusiveParent}.
     *
     * Generally {@code Object.class} should be passed as
     * {@code exclusiveParent}.
     *
     * @param startClass
     *            the class whose fields should be retrieved
     * @param exclusiveParent
     *            if not null, the base class of startClass whose fields should
     *            not be retrieved.
     * @return
     */
    public static Iterable<Field> getFieldsUpTo(Class<?> startClass,  Class<?> exclusiveParent) {
        List<Field> currentClassFields = new ArrayList<>();
        currentClassFields.addAll(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }
        return currentClassFields;
    }


    public static Field getFieldInClassUpTo(String fieldName,Class<?> startClass, Class<?> exclusiveParent) {
        Field resultField = null;
        try {
            resultField = startClass.getDeclaredField(fieldName);
        } catch (Exception e) {
            // no op
        }
        if (resultField == null) {
            Class<?> parentClass = startClass.getSuperclass();
            if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
                resultField = getFieldInClassUpTo(fieldName, parentClass, exclusiveParent);
            }
        }
        return resultField;
    }

//    public static Object runGetter(Field field, Class<?> o) {
//        // MZ: Find the correct method
//        for (Method method : o.getMethods()) {
//            if (isGetter(method)) {
//                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
//                    // MZ: Method found, run it
//                    try {
//                        return method.invoke(o);
//                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        SystemLog.exception(e);
//                    }
//
//                } else {
//                    return null;
//                }
//            }
//        }
//        return null;
//    }

//    public static <T> T invokeSetterMethodForObject(T MyObject, ResultSet rs) throws SQLException{
//        // MZ: Find the correct method
//        MyObject = (T) ReflectionKit.invokeConstructor(MyObject.getClass());
//        while (rs.next()) {
//            int size = rs.getFetchSize();
//            for (Field field : MyObject.getClass().getDeclaredFields()){
//                for (Method method : MyObject.getClass().getMethods())
//                {
//                    if (isSetter(method))
//                    {
//                        if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
//                        {
//                            // MZ: Method found, run it
//                            try
//                            {
//                                method.setAccessible(true);
//                                if(field.getType().getSimpleName().toLowerCase().endsWith("integer"))
//                                    method.invoke(MyObject,rs.getInt(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("long"))
//                                    method.invoke(MyObject,rs.getLong(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("string"))
//                                    method.invoke(MyObject,rs.getString(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("boolean"))
//                                    method.invoke(MyObject,rs.getBoolean(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("timestamp"))
//                                    method.invoke(MyObject,rs.getTimestamp(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("date"))
//                                    method.invoke(MyObject,rs.getDate(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("double"))
//                                    method.invoke(MyObject,rs.getDouble(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("float"))
//                                    method.invoke(MyObject,rs.getFloat(field.getName().toLowerCase()));
//                                else if(field.getType().getSimpleName().toLowerCase().endsWith("time"))
//                                    method.invoke(MyObject,rs.getTime(field.getName().toLowerCase()));
//                                else
//                                    method.invoke(MyObject,rs.getObject(field.getName().toLowerCase()));
//                            }
//                            catch (IllegalAccessException | InvocationTargetException | SQLException e)
//                            {
//                                System.err.println(e.getMessage());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return MyObject;
//    }

    /**
     * To speed up find setter methods, this map will be used.
     * The Key String will be of the format objectClass.property(valueclass)
     * Where:
     * objectClass = MyObject.getClass().getName
     * property = property (as passed in to callSetter), before set is appended
     * valueCLass = value.getClass().getName()
     * The Method will be either the method, or null if a search was not and no
     * method is found.
     */
    private static HashMap<String, Method> SETTERS_MAP = new HashMap<String, Method>();

    /**
     * Find a setter method for the give object's property and try to call it.
     * No exceptions are thrown. You typically call this method because either
     * you are sure no exceptions will be thrown, or to silently ignore
     * any that may be thrown.
     * This will also find a setter that accepts an interface that the value
     * implements.
     * <b>This is still not very effcient and should only be called if
     * performance is not of an issue.</b>
     * You can check the return value to see if the call was seuccessful or
     * not.
     * @param obj Object to receive the call
     * @param property property name (without set. First letter will be
     * capitalized)
     * @param value Value of the property.
     * @return
     */
    public static boolean callSetter(Object obj, String property, Object value) {
        String key = String.format("%s.%s(%s)", obj.getClass().getName(),
                property, value.getClass().getName());
        Method m = null;
        boolean result = false;
        if(!SETTERS_MAP.containsKey(key)) {
            m = findSetterMethod(obj, property, value);
            SETTERS_MAP.put(key, m);
        } else {
            m = SETTERS_MAP.get(key);
        }
        if(m != null) {
            try {
                m.invoke(obj, value);
                result = true;
            } catch (IllegalAccessException|IllegalArgumentException|
                    InvocationTargetException ex) {
                SystemLog.exception(ex);
            }
        }
        return result;
    }



    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    public static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }


    public static Method findSetterMethod(
            Object obj,String fieldName, Object value) {
        Class<?> clazz = obj.getClass();
        Class clazzField = value.getClass();
        Method m = findSetterMethod(clazz, fieldName, clazzField);
        return m;
    }

    public static Method findSetterMethod(Class<?> clazz,String fieldName,Class<?> clazzField) {
        Method m = null;
        String setter = String.format("set%C%s",
                fieldName.charAt(0), fieldName.substring(1));
        while (clazzField != null) {
            try {
                m =  clazz.getMethod(setter, clazzField);
                return m;
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class iface : clazzField.getInterfaces()) {
                    try {
                        m =  clazz.getMethod(setter, iface);
                        return m;
                    } catch (NoSuchMethodException ex1) {
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return m;
    }

    public static Method findGetterMethod(
            Object obj,String fieldName, Object value) {
        Class<?> clazz = obj.getClass();
        Class clazzField = value.getClass();
        Method m = findGetterMethod(clazz, fieldName, clazzField);
        return m;
    }

    public static Method findGetterMethod(Class<?> clazz,String fieldName,Class<?> clazzField) {
        Method m = null;
        String getter = String.format("get%C%s",
                fieldName.charAt(0), fieldName.substring(1));
        while (clazzField != null) {
            try {
                m =  clazz.getMethod(getter, new Class[0]);
                return m;
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class iface : clazzField.getInterfaces()) {
                    try {
                        m =  clazz.getMethod(getter, iface);
                        return m;
                    } catch (NoSuchMethodException ex1) {
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return m;
    }

    public static Method findMethod(Object obj,String methodName, Object value) {
        Method m = null;
        Class<?> clazz  = obj.getClass();
        Class clazzField = value.getClass();
        findMethod(clazz,methodName,clazzField);
        return m;
    }

    public static Method findMethod(Class<?> clazz,String methodName, Class<?> clazzField) {
        Method m = null;
        while (clazzField != null) {
            try {
                m = clazz.getMethod(methodName, clazzField);
                return m;
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class iface : clazzField.getInterfaces()) {
                    try {
                        m = clazz.getMethod(methodName, iface);
                        return m;
                    } catch (NoSuchMethodException ex1) {
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return m;
    }

    public static Class<?> getClassFromPath(String pakage,String clazz){
        //String pack = this.getClass().getPackage().getName()+".interceptor";
        //String sclazz = cl.getSimpleName()+"Interceptor";
        //String full = pack+"."+sclazz;
        try {
            return Class.forName(pakage+"."+clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    //OTHER METHODS
    /*public static Class createNewClass(String annotatedClassName,String pathPackageToAnnotatedClass)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        Class cls = Class.forName(pathPackageToAnnotatedClass+"."+annotatedClassName).getConstructor().newInstance().getClass();
        //You can use reflection : Class.forName(className).getConstructor(String.class).newInstance(arg);
        SystemLog.message("Create new Class Object con Name: " + cls.getName());
        return cls;
    }

    public static Class createNewClass(String pathPackageToAnnotatedClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        //e.home. oracle.jdbc.driver.OracleDriver#sthash.4rtwgiWJ.dpuf
        Class cls = Class.forName(pathPackageToAnnotatedClass).getConstructor().newInstance().getClass();
        SystemLog.message("Create new Class Object con Name: " + cls.getName());
        return cls;
    }

    public static Constructor castObjectToSpecificConstructor(Class newClass){
        Constructor constructor = null;
        try{
            constructor = newClass.getConstructor(new Class[]{});
        }catch(Exception e){
        }
        return constructor;
    }

    public static Object castObjectToSpecificClass(Class newClass,Object MyObject){
        Object obj2 = new Object();
        try{
            obj2 = newClass.cast(MyObject);
        }catch(Exception e){
        }
        return obj2;
    }

    public static Object castObjectToSpecificObject(Object MyObject,String pathPackageToObjectAnnotated)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        Class clsObjectAnnotated = createNewClass(pathPackageToObjectAnnotated);
        Constructor cons = castObjectToSpecificConstructor(clsObjectAnnotated);
        MyObject = (Object)cons.newInstance();
        return MyObject;
    }*/

}