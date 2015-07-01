package com.github.p4535992.util.reflection;


import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
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
import java.util.*;

/**
 * Class for help me with the first impact with java reflection library
 * To follow all the url help me to create this class:
 * href: http://my.safaribooksonline.com/video/-/9780133038118?cid=2012-3-blog-video-java-socialmedia
 * href: http://www.asgteach.com/blog/?p=559
 * href: http://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
 * href: http://roadtobe.com/supaldubey/creating-and-reading-annotations-reflection-in-java/ (other util)
 * href: https://github.com/dancerjohn/LibEx/blob/master/libex/src/main/java/org/libex/reflect/ReflectionUtils.java (href)
 * href: http://www.java2s.com/Code/Java/Reflection/Findasettermethodforthegiveobjectspropertyandtrytocallit.htm
 *
 * http://stackoverflow.com/questions/1555326/java-class-cast-vs-cast-operator
 *
 */
@SuppressWarnings("unused")
public class ReflectionKit{

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    /**
     * Method to check if a specific class is a primitve class.
     * @param aClass class of the object you want to test.
     * @return boolean value if is a primite type or not.
     */
    public static boolean isWrapperType(Class<?> aClass) {
        return WRAPPER_TYPES.contains(aClass);
    }

    /**
     * List of all primitve class.
     * @return all the primitve class on java.
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
        //java.lang.reflect.Type t =getClass().getGenericSuperclass();
        //java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        //this.cl =  (Class<T>) pt.getActualTypeArguments()[0];
        //this.clName = cl.getSimpleName();
    }

    /**
     * Method to get all basic information on a method
     * @param obj object to inspect.
     * @return list of array name-value for that object.
     */
    public static List<String[]> inspectFieldClass(Object obj) {
        return inspectFieldClass(obj.getClass());
    }

    public static List<String[]> inspectFieldClass(Class<?> clazz) {
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

    public static Map<Field,Annotation[]> inspectFieldAndAnnotationClass(Class<?> clazz){
        Map<Field,Annotation[]> map = new HashMap<>();
        for(Field field : clazz.getDeclaredFields()){
            Annotation[] annotations = field.getDeclaredAnnotations();
            map.put(field, annotations);
        }
        return map;
    }

    public static List<String[]> inspectTypesMethod(Class<?> clazz,String nameOfMethhod) {
        List<String[]> list = new ArrayList<>();
        try {
            Method method = clazz.getMethod(nameOfMethhod);
            Type returnType = method.getGenericReturnType();
            if(returnType instanceof ParameterizedType){
                ParameterizedType type = (ParameterizedType) returnType;
                Type[] typeArguments = type.getActualTypeArguments();
                for(Type typeArgument : typeArguments){
                    Class<?> typeArgClass = (Class<?>) typeArgument;
                    list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
                }
            }
        } catch (NullPointerException|NoSuchMethodException e) {
            SystemLog.exception(e);
        }
        return list;
    }

    public static List<String[]> inspectTypesMethod(Class<?> aClass,Method method){
        List<String[]> list = new ArrayList<>();
        try {
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) returnType;
                Type[] typeArguments = type.getActualTypeArguments();
                for (Type typeArgument : typeArguments) {
                    Class<?> typeArgClass = (Class<?>) typeArgument;
                    System.out.println("typeArgClass = " + typeArgClass);
                    list.add(new String[]{typeArgClass.getName(), getClassReference(typeArgClass)});

                }
            }
        } catch (NullPointerException e) {
            SystemLog.exception(e);
        }
        return list;
    }

    /**
     * Method for inpsect a method on a class.
     * @param aClass the class java where is put the method.
     * @param method the method to inspect.
     * @return a map with all information on the method.
     */
    public static Map<Class<?>,List<String[]>> inspectSimpleTypesMethod(Class<?> aClass,Method method) {
        Map<Class<?>,List<String[]>> map = new HashMap<>();
        List<String[]> list = new ArrayList<>();
        Type returnType = method.getGenericReturnType();
        try {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class<?> typeArgClass = (Class<?>) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(), getClassReference(typeArgClass)});
            }
        } catch (NullPointerException e) {
            SystemLog.exception(e);
        }
        map.put(returnType.getClass(),list);
        return map;
    }

    public static Map<String,Class<?>> inspectAndLoadGetterObject(Object obj) throws NoSuchMethodException{
        List<Method> getter = getGettersClass(obj.getClass());
        Map<String,Class<?>> map = new HashMap<>();
        for(Method met : getter){
            Class<?> cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }

    public static Map<String,Class<?>> inspectAndLoadSetterObject(Object obj) throws NoSuchMethodException{
        List<Method> setter = getSettersClass(obj.getClass());
        Map<String,Class<?>> map = new HashMap<>();
        for(Method met : setter){
            Class<?> cl = met.getReturnType();
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

    public static List<Method> getGettersSettersClass(Class<?> aClass) {
        List<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method) || isSetter(method))
                list.add(method);
        return list;
    }

    public static List<Method> getGettersClass(Class<?> aClass){
        List<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method))
                list.add(method);
        return list;
    }

     public static List<Method> getSettersClass(Class<?> aClass){
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
        //if(void.class.equals(method.getReturnType())) return false;
        return !void.class.equals(method.getReturnType());
    }

    public static boolean isSetter2(Method method){
        if(!method.getName().startsWith("set")) return false;
        //if(method.getParameterTypes().length != 1) return false;
        return method.getParameterTypes().length == 1;
    }

    /**
     * Method to get all methods of a class
     * @param aClass class to inspect.
     * @return array of methof for that object.
     */
    public static List<Method> getMethodsByClass(Class<?> aClass){
        Method[] methods = aClass.getMethods();
        return Arrays.asList(methods);
    }

    /**
     * Method to get a specific method from a class
     * If you know the precise parameter types of the method you want to access,
     * you can do so rather than obtain the array all methods. This example returns
     * the public method named "nameOfMethod", in the given class which takes a
     * String as parameter.
     * @param aClass class to inspect.
     * @param nameOfMethod name of th method you want to find.
     * @param param class of the parameter of the constructor of the method you wan to find.
     * @return method you found..
     * @throws NoSuchMethodException throw if any No Such Method error is occurred.
     */
    public static Method getMethodByNameAndParam(Class<?> aClass, String nameOfMethod, Class<?>[] param) throws NoSuchMethodException{
        Method method;
        //If the method you are trying to access takes no parameters, pass null as the parameter type array, like this:   
        if(CollectionKit.isArrayEmpty(param))method = aClass.getMethod(nameOfMethod);//nameOfMethod, null
        else method = aClass.getMethod(nameOfMethod,param);// String.class
        return method;
    }

    /**
     * Method to get a specific mehtod form a the reference class of the object.
     * @param MyObject generic object to inspect.
     * @param nameOfMethod name of the method you want to find.
     * @param param class of the parameter of the constructor of the method you wan to find.
     * @param <T> generic type.
     * @return method you found.
     * @throws NoSuchMethodException throw if any error is occured.
     */
    public static <T> Method getMethodByNameAndParam(T MyObject, String nameOfMethod, Class<?>[] param) throws NoSuchMethodException{
            return getMethodByNameAndParam(MyObject.getClass(), nameOfMethod, param); //String.class
    }

    /**
     * Method Parameters : Method where you can read what parameters a given method takes like this.
     * @param method method you want to inspect.
     * @return  array of class of the parameters of that method.
     */
    public static Class<?>[] getParametersTypeMethod(Method method){
        return method.getParameterTypes();
    }
    
    /**
     * Return Types: Method where you can access the return type of a method like this.
     * @param method method to inspect.
     * @return the class of the returned type of the method.
     */
    public static Class<?> getReturnTypeMethod(Method method){
        return method.getReturnType();
    }

    private static Field[] getFieldsClass(Class<?> aClass){
        return aClass.getDeclaredFields();
    }

    /**
     * Method to get a Field object from a class with the string of the name.
     * @param aClass the class where is the field to get.
     * @param fieldName string name of the field.
     * @return the Field if you found it or null.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    public static Field getFieldByName(Class<?> aClass,String fieldName) throws NoSuchFieldException {
        try {
            return aClass.getField(fieldName);
        }catch(java.lang.NoSuchFieldException e){
            return aClass.getDeclaredField(fieldName);
        }
    }

    /**
     * Method to get a Fields object from a class .
     * @param aClass the class target.
     * @return all the fileds you found on the class.
     */
    public static List<String> getFieldsNameByClass(Class<?> aClass){
        List<String> names = new ArrayList<>();
        for(Field field : getFieldsClass(aClass)){
            names.add(field.getName());
        }
        return names;
    }

    /**
     * Method to get all fields with a specific annotations.
     * @param clazz the class target.
     * @param aClass the annotation class to search.
     * @return array of fields annotated with that specific annotation class.
     */
    public static Field[] getFieldsByAnnotation(Class<?> clazz,Class<? extends Annotation> aClass){
        Field[] fields = clazz.getDeclaredFields();
        List<Field> types = new ArrayList<>();
        for(Field field : fields){
            if(field.isAnnotationPresent(aClass)) {
                types.add(field);
            }
        }
        return CollectionKit.convertListToArray(types);
    }

    /**
     * Method to get all class with a specific annotations.
     * @param clazz the class target.
     * @param aClass the annotation class to search.
     * @return array of class annotated with that specific annotation class.
     */
    public static Class<?>[] getClassesByFieldsByAnnotation(Class<?> clazz,Class<? extends Annotation> aClass){
        Field[] fields = getFieldsByAnnotation(clazz, aClass);
        List<Class<?>> classes = new ArrayList<>();
        for(Field field : fields) {
            classes.add(field.getType());
        }
        return CollectionKit.convertListToArray(classes);
    }

    /**
     * Method to get the return type of a method in a specific class
     * get method that takes a String as argument
     * The null parameter is the object you want to invoke the method on. If the method is static you supply null instead
     * of an object instance. In this example, if doSomething(String.class) is not static, you need to supply a valid
     * MyObject instance instead of null;The Method.invoke(Object target, Object ... parameters) method takes an optional amount of parameters,
     * but you must supply exactly one parameter per argument in the method you are invoking. In this case it was
     * a method taking a String, so one String must be supplied.
     * @param MyObject T object to inspec
     * @param nameOfMethod name of the methof you want ot find.
     * @param param class of the parameter of the constructor of the method you wan to find.
     * @param <T> generic type.
     * @return result of the return of the method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeObjectMethod(T MyObject, String nameOfMethod, Class<?>[] param)//4th parameter , Class<T> aClass
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Method method;
        T MyTObject2;
        if(param==null || param.length==0 )method = getMethodByNameAndParam(MyObject, nameOfMethod, null);
        else method = getMethodByNameAndParam(MyObject, nameOfMethod, param); //String.class
        try{
            //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
            MyTObject2 = (T) method.invoke(param);
        }catch(java.lang.NullPointerException ne){
            //MyObject = method.invoke(MyObject, param); //...if the methos is non-static
            MyTObject2 = (T) method.invoke(MyObject);
        }
        return MyTObject2;
    }

    /**
     * Method for invoke a method.
     * @param MyObject the object where the methos is applied.
     * @param method the method to invoke.
     * @param param the param for the method.
     * @return a object update with the result of the method.
     * @throws IllegalAccessException throw any error if is occurrred.
     * @throws InvocationTargetException throw any error if is occurrred.
     * @throws NoSuchMethodException throw any error if is occurrred.
     */
    public static Object invokeObjectMethod(Object MyObject,Method method,Class<?>[] param)
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

    /**
     * Method for invoke a method.
     * @param MyObject the object where the methos is applied.
     * @param method the method to invoke.
     * @param param the param for the method.
     * @return a object update with the result of the method.
     * @throws IllegalAccessException throw any error if is occurrred.
     * @throws InvocationTargetException throw any error if is occurrred.
     * @throws NoSuchMethodException throw any error if is occurrred.
     */
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

    /**
     * Method to invoke a getter method from a class.
     * @param MyObject object target.
     * @param methodName string name of the method getter.
     * @param value value to set with the setter method.
     * @param clazzValue class of the calue to set.
     * @param <T> generic type.
     * @return the returned value from the getter method is exists.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeSetterClass(T MyObject,String methodName,Object value,Class<?> clazzValue)
    {
        try {
            Method method = getMethodByNameAndParam(
                    MyObject.getClass(),methodName,new Class<?>[]{clazzValue});
            MyObject = (T) method.invoke(MyObject,value);
            return MyObject;
        } catch (InvocationTargetException|IllegalAccessException|
                SecurityException|NoSuchMethodException|
                ClassCastException|NullPointerException  e) {
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Method to invoke a getter method from a class.
     * @param MyObject object target.
     * @param methodName string name of the method getter.
     * @param <T> generic type.
     * @return the returned value from the getter method is exists.
     */
    public static <T> Object invokeGetterClass(T MyObject,String methodName) {
        Object MyObject2;
        try {
            //Object MyObject = clazzValue.cast(new Object());
            Method method = getMethodByNameAndParam(
                   MyObject.getClass(),methodName,new Class<?>[0]);
            MyObject2 = method.invoke(MyObject);
            return MyObject2;
        } catch (InvocationTargetException|IllegalAccessException|
                SecurityException|NoSuchMethodException  e) {
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Method to invoke a getter method from  a Object.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @param value to set with the setter method.
     * @param <T> generic type.
     * @return the return value of the invoke on the getter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeSetterMethodForObject(T MyObject, Method method, Object value)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        T MyObject2;
        try{
            //if the method you try to invoke is static...
            MyObject2 = (T) method.invoke(null, value);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 = (T) method.invoke(MyObject, value);
        }
        return MyObject2;
    }

    /**
     * Method to invoke a setter method from  a Object.
     * @param MyObject object where invoke the getter method.
     * @param method the setter method.
     * @param value to set with the setter method.
     * @return the return value of the invoke on the setter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    public static Object invokeSetterMethod(Object MyObject, Method method, Object value)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Object MyObject2;
        try{
            //if the method you try to invoke is static...
            MyObject2 = method.invoke(null, value);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 = method.invoke(MyObject, value);
        }
        return MyObject2;
    }

    /**
     * Method to invoke a getter method from  a Object.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @param <T> generic type.
     * @return the return value of the invoke on the getter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeGetterMethodForObject(T MyObject, Method method)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        T MyObject2;
        Class<T> clazz = (Class<T>) MyObject.getClass();
        try{
            //if the method you try to invoke is static...
            MyObject2 = clazz.cast(method.invoke(null));
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 =  clazz.cast(method.invoke(MyObject));
        }
        return MyObject2;
    }

    /**
     * Method to invoke a getter method from  a Object.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @return the return value of the invoke on the getter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    public static Object invokeGetterMethod(Object MyObject,Method method)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Object MyObject2;
        try{
            //if the method you try to invoke is static...
            MyObject2 = method.invoke(null);
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 = method.invoke(MyObject);
        }
        return MyObject2;
    }

   /**
    * Method to get the return value of a constructor that takes specific arguments.
    * @param <T> generic type.
    * @param MyObject object T in input.
    * @param param arrays of class for the constructor.
    * @param defaultValues array of object for the constructor.
    * @return T object return from the constructor.
    * @throws NoSuchMethodException throw if any error is occurred.
    * @throws InstantiationException throw if any error is occurred.
    * @throws IllegalAccessException throw if any error is occurred.
    * @throws IllegalArgumentException throw if any error is occurred.
    * @throws InvocationTargetException throw if any error is occurred.
    */
    @SuppressWarnings("unchecked")
    public static <T> T invokeConstructor(T MyObject, Class<?>[] param, Object[] defaultValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Class<T> clazz= (Class<T>)MyObject.getClass();
        return clazz.cast(invokeConstructor(MyObject.getClass(), param, defaultValues));
    }


    /**
     * Method to get the return value of a constructor that takes specific arguments.
     * @param <T> generic type.
     * @param clazz the class where is the constructor.
     * @param param arrays of class for the constructor.
     * @param defaultValues array of object for the constructor.
     * @return T object return from the constructor.
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws InstantiationException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws IllegalArgumentException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    public static <T> T invokeConstructor(Class<T> clazz, Class<?>[] param, Object[] defaultValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Constructor<?> constructor = clazz.getConstructor(param);
        return clazz.cast(constructor.newInstance(defaultValues));
    }

    public static <T> T invokeConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }catch(IllegalAccessException|InstantiationException e){
            SystemLog.exception(e);
        }
        return null;
    }


    public static URL getCodeSourceLocation(Class<?> aClass) {return aClass.getProtectionDomain().getCodeSource().getLocation(); }
    public static String getClassReference(Class<?> aClass){ return aClass.getName();}

    //Method for get all the class in a package with library reflections 
//    public Set<Class<? extends Object>> getClassesByPackage(String pathToPackage){
//        Reflections reflections = new Reflections(pathToPackage);
//        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
//    }

    /**
     * Method to inspect a Annotation object java.
     * @param annotation the anntation to inspect.
     * @return a list of properties of the annotation.
     * @throws InvocationTargetException thow if any error is occurrred.
     * @throws IllegalAccessException thow if any error is occurrred.
     */
    public static List<Object[]> inspectAnnotation(Annotation annotation) throws InvocationTargetException, IllegalAccessException {
        List<Object[]> list = new ArrayList<>();
        Class<? extends Annotation> type = annotation.annotationType();
        for (Method method : type.getDeclaredMethods()) {
            Object[] aObj = new Object[]{type.getName(),method.getName(),method.invoke(annotation)};
            list.add(aObj);
        }
        return list;
    }
    /**
     * Method for inspect/find annottion for the class java.
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
     * Method for get all the field with at least a annotation on a specific class.
     * @param aClass class to inspect.
     * @return list of all the properties of the fields with a annotation.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
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
    }

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * @param aClass class you want to inspect.
     * @return list of list of arrays name-value of all annotation on the declareted types.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws SecurityException throw if any error is occurrred.
     * @throws InvocationTargetException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
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
     * @param aClass class you wan to inspect.
     * @param field field you want o find.
     * @return list of array name-value with all information on the type field.
     * @throws NoSuchFieldException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
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
                Object[] bObj = CollectionKit.concatenateArrays(fObj, aObj.toArray());
                list.add(bObj);
            }
        }
        return list;
    }

    /**
     * Method for get the properties of a Annotation form a Field.
     * @param annotation Annotation object to inspect.
     * @return a list of properties of the Annotation.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

    public static List<Object[]> getAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationClass,String fieldName )
            throws SecurityException, NoSuchFieldException {
        return getAnnotationField(annotationClass,getFieldByName(aClass, fieldName));
    }

    /**
     * Method to get the properties of a Annotation on a class.
     * @param aClass classs to inspect.
     * @return properteis of the specific annotation.
     * @throws SecurityException throw if any error is occurred.
     * @throws NoSuchFieldException throw if any error is occurred.
     */
    public static List<List<Object[]>> getAnnotationsClass(Class<?> aClass)
            throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> result = new ArrayList<>();
        for(Annotation annotation : aClass.getAnnotations()){
            List<Object[]> list = getAnnotationClass(annotation);
            result.add(list);
        }
        return result;
    }

    /**
     * Method to get the properties of a Annotation on a class.
     * @param annotation the Annotation object to inpsect.
     * @return properteis of the specific annotation.
     */
    @SuppressWarnings("unchecked")
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
    * Changes the annotation value for the given key of the given annotation to newValue and returns
    * the previous value.
    * Usage: final Something annotation = (Something) Foobar.class.getAnnotations()[0];
    *         System.out.println("oldAnnotation = " + annotation.someProperty());
    *         changeAnnotationValue(annotation, "someProperty", "another value");
    *         System.out.println("modifiedAnnotation = " + annotation.someProperty());
    * href: http://stackoverflow.com/questions/14268981/modify-a-class-definitions-annotation-string-parameter-at-runtime/14276270#14276270
    * @param annotation annotation you want ot updte.
    * @param key key of the attribute you want to update.
    * @param newValue value of the attribute with the spceific key you want to update.
    * @return the new object with the annotation update in runtime.
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

    /**
     * Method to update a annotation value of a field in runtime.
     * @param aClass the class target.
     * @param annotationClass the annotation class to update.
     * @param fieldName string field name.
     * @param attributeName the attribute name of the annotation to update.
     * @param attributeValue the new value for the specific attribute of the specific annotation.
     */
    public static void updateAnnotationFieldValue(
        Class<?> aClass,Class<? extends Annotation> annotationClass,String fieldName,String attributeName,String attributeValue){
        try {
            Field fieldColumn = getFieldByName(aClass, fieldName);
            Annotation annColumn = fieldColumn.getAnnotation(annotationClass);
            if (annColumn != null) {
                ReflectionKit.updateAnnotationValue(annColumn, attributeName, attributeValue);
            }else{
                SystemLog.warning("No annotation for the class whit attribute:"+attributeName);
            }
        }catch(NoSuchFieldException e){
            SystemLog.exception(e);
        }
    }

    /**
     * Method to update a annotation value of a class in runtime.
     * @param aClass the class target.
     * @param annotationClass the annotation class to update.
     * @param attributeName the attribute name of the annotation to update.
     * @param attributeValue the new value for the specific attribute of the specific annotation.
     */
    public static void updateAnnotationClassValue(Class<?> aClass,Class<? extends Annotation> annotationClass,String attributeName,String attributeValue){
        Annotation ann = aClass.getAnnotation(annotationClass);
        if(ann!=null) {
            ReflectionKit.updateAnnotationValue(ann, attributeName,attributeValue);
        }
    }

    /**
     * Method to update a field on class.
     * @param aClass the class target.
     * @param objField the field object to update.
     * @param fieldName string name of the field.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
    public static void updateFieldClass(Class<?> aClass,Object objField,String fieldName)
            throws NoSuchFieldException,IllegalAccessException {
        //Class  aClass = MyObject.class
        Field field = aClass.getField(fieldName);
        //MyObject objectInstance = new MyObject();
        field.setAccessible(true);
        Object value = field.get(objField);
        field.set(objField, value);
    }

    /**
     * Method to set a field of a object.
     * @param object object to update.
     * @param fieldName string name of the field.
     * @param value value to set on the correspondent setter method.
     * @throws SecurityException throw if any error is occurrred.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws IllegalArgumentException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
    public static void setField(Object object, String fieldName, Object value)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    /**
     * Method to get a field value from a object.
     * @param object object to inspect.
     * @param fieldName string name of the field you want find.
     * @return the value of the declared field .
     * @throws SecurityException throw if any error is occurrred.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws IllegalArgumentException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
    public static Object getFieldValueByName(Object object, String fieldName)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        return getFieldValueByName(object.getClass(), fieldName);
    }

    /**
     * Method to get a  field  value from a class.
     * @param aClass the class to inspect.
     * @param fieldName string name of the field you want find.
     * @return the value of the declared field .
     * @throws SecurityException throw if any error is occurrred.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws IllegalArgumentException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
    public static Object getFieldValueByName(Class<?> aClass, String fieldName)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = aClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object returnValue = field.get(aClass);
        field.setAccessible(false);
        return returnValue;
    }

    /**
     * Method to get a list of fields value from a class.
     * @param aClass the class to inspect.
     * @return a list of value of all declared field ont he class.
     * @throws NoSuchFieldException throw if any error is occurrred.
     * @throws IllegalAccessException throw if any error is occurrred.
     */
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

    /**
     * Method for a field object to a class in runtime [NOT TESTED].
     * @param sourceObject the field source object.
     * @param targetClass the class target.
     * @return a new field is in the class target.
     */
    public static Object copyFieldToClass(Object sourceObject, Class<?> targetClass) {
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
     * @return list of iterable field.
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

    /**
     * Method for get a specific field on the superclass of the class [NOT TESTED].
     * @param fieldName string name of the field.
     * @param startClass start classs.
     * @param exclusiveParent name of the class you have extended.
     * @return the filed on the superclass.
     */
    public static Field getFieldInClassUpTo(String fieldName,Class<?> startClass, Class<?> exclusiveParent) {
        Field resultField = null;
        try {
            resultField = startClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            SystemLog.exception(e);
        }
        if (resultField == null) {
            Class<?> parentClass = startClass.getSuperclass();
            if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
                resultField = getFieldInClassUpTo(fieldName, parentClass, exclusiveParent);
            }
        }
        return resultField;
    }

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
    private static final HashMap<String, Method> SETTERS_MAP = new HashMap<>();

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
     * @return boolean value is exists the setter method.
     */
    public static boolean callSetter(Object obj, String property, Object value) {
        String key = String.format("%s.%s(%s)", obj.getClass().getName(),
                property, value.getClass().getName());
        Method m;
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
     * Scans all classes accessible from the context class loader which belong 
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return the array of class in the package.
     * @throws ClassNotFoundException throw if any error is occurred.
     * @throws IOException throw if any error is occurred.
     */
    public static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return list of class in the directory package.
     * @throws ClassNotFoundException throw if any error is occurred.
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        List<File> files = FileUtil.readDirectory(directory);
        if(files!= null && files.size()>0){
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }


    /**
     * Method for get a specific setter method froma object.
     * @param obj object to inspect.
     * @param fieldName string name of the field.
     * @param value object value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findSetterMethod(Object obj,String fieldName, Object value) {
        Class<?> clazz = obj.getClass();
        Class<?> clazzField = value.getClass();
        return findSetterMethod(clazz, fieldName, clazzField);
    }

    /**
     * Method for get a specific setter method froma object
     * @param clazz class of the object to inspect.
     * @param fieldName string name of the field.
     * @param clazzField class of the field value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findSetterMethod(Class<?> clazz,String fieldName,Class<?> clazzField) {
        String setter = String.format("set%C%s",fieldName.charAt(0), fieldName.substring(1));
        while (clazzField != null) {
            try {
                return  clazz.getMethod(setter, clazzField);
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class<?> iface : clazzField.getInterfaces()) {
                    try {
                        return clazz.getMethod(setter, iface);
                    } catch (NoSuchMethodException ex1) {
                        SystemLog.exception(ex1);
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Method for get a specific getter method froma object.
     * @param obj object to inspect.
     * @param fieldName string name of the field.
     * @param value object value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findGetterMethod(Object obj,String fieldName, Object value) {
        Class<?> clazz = obj.getClass();
        Class<?> clazzField = value.getClass();
        return findGetterMethod(clazz, fieldName, clazzField);

    }

    /**
     * Method for get a specific getter method froma object
     * @param clazz class of the object to inspect.
     * @param fieldName string name of the field.
     * @param clazzField class of the field value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findGetterMethod(Class<?> clazz,String fieldName,Class<?> clazzField) {
        String getter = String.format("get%C%s",fieldName.charAt(0), fieldName.substring(1));
        while (clazzField != null) {
            try {
                return  clazz.getMethod(getter);
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class<?> iface : clazzField.getInterfaces()) {
                    try {
                        return  clazz.getMethod(getter, iface);
                    } catch (NoSuchMethodException ex1) {
                        SystemLog.exception(ex1);
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Method for find a specific Method object on a object.
     * @param obj object where fin the method.
     * @param methodName string of name of the method.
     * @param value object value of get the class of the field used on the method.
     * @return  the method if found otherwise a null.
     */
    public static Method findMethod(Object obj,String methodName, Object value) {
        Class<?> clazz  = obj.getClass();
        Class<?> clazzField = value.getClass();
        return findMethod(clazz,methodName,clazzField);
    }

    /**
     * Method for find a specific Method object on a object.
     * @param clazz class where find the method.
     * @param methodName string of name of the method.
     * @param clazzField class of the filed used on the method
     * @return the method if found otherwise a null.
     */
    public static Method findMethod(Class<?> clazz,String methodName, Class<?> clazzField) {
        while (clazzField != null) {
            try {
                return clazz.getMethod(methodName, clazzField);
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class<?> iface : clazzField.getInterfaces()) {
                    try {
                        return clazz.getMethod(methodName, iface);
                    } catch (NoSuchMethodException ex1) {
                        SystemLog.exception(ex1);
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Method for get the class from a reference path to it.
     * @param pakage string of the reference pakage contatin the class.
     * @param clazz string of the name of the class you wan to find.
     * @return the class if found otherwise a null.
     */
    public static Class<?> getClassFromPath(String pakage,String clazz){
        //String pack = this.getClass().getPackage().getName()+".interceptor";
        //String sclazz = cl.getSimpleName()+"Interceptor";
        //String full = pack+"."+sclazz;
        try {
            return Class.forName(pakage+"."+clazz);
        } catch (ClassNotFoundException e) {
            SystemLog.exception(e);
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

    public static Class<?> getTheParentGenericClass(Class<?> thisClass){
        java.lang.reflect.Type t = thisClass.getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        return (Class) pt.getActualTypeArguments()[0];
    }

}