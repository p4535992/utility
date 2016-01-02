package com.github.p4535992.util.reflection;

import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.collection.CollectionUtilities;
import com.github.p4535992.util.collection.ListUtilities;
import com.github.p4535992.util.file.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 4535992 on 26/10/2015.
 * Class for help with the first impact with java reflection library.
 * I don't own any right on the code, but i want to thank all the piece of code i
 * copied and modified for my purpose  from the internet and help me with this class.
 * href: http://my.safaribooksonline.com/video/-/9780133038118?cid=2012-3-blog-video-java-socialmedia
 * href: http://www.asgteach.com/blog/?p=559
 * href: http://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
 * href: http://roadtobe.com/supaldubey/creating-and-reading-annotations-reflection-in-java/ (other util)
 * href: https://github.com/dancerjohn/LibEx/blob/master/libex/src/main/java/org/libex/reflect/ReflectionUtils.java (href)
 * href: http://www.java2s.com/Code/Java/Reflection/Findasettermethodforthegiveobjectspropertyandtrytocallit.htm
 *
 * href: http://stackoverflow.com/questions/1555326/java-class-cast-vs-cast-operator
 * href: http://stackoverflow.com/questions/6271417/java-get-the-current-class-name
 * 
 * href: http://alvinalexander.com/java/jwarehouse/spring-framework-2.5.3/src/org/springframework/util/ReflectionUtils.java.shtml
 * href: https://github.com/p4535992/java-util/blob/master/src/main/java/com/cedarsoftware/util/ReflectionUtils.java#L39
 *
 * @author 4535992.
 * @version 2015-11-12.
 */
@SuppressWarnings("unused")
public class ReflectionUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ReflectionUtilities.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    // ---------------------------------------------------------------------
    // Members
    // ---------------------------------------------------------------------

    private static Object  object;
    private static boolean isClass;
    private static Class<?> clazz;
    private static class NULL {}

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public ReflectionUtilities(){
        //java.lang.reflect.Type t =getClass().getGenericSuperclass();
        //java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        //this.cl =  (Class<T>) pt.getActualTypeArguments()[0];
        //this.clName = cl.getSimpleName();
        ReflectionUtilities.isClass = false;
        ReflectionUtilities.object = null;
        ReflectionUtilities.clazz = null;
    }

    private ReflectionUtilities(Class<?> type) {
        ReflectionUtilities.object = type;
        ReflectionUtilities.isClass = true;
        ReflectionUtilities.clazz = type;
    }

    private ReflectionUtilities(Object object) {
        ReflectionUtilities.object = object;
        ReflectionUtilities.isClass = false;
        ReflectionUtilities.clazz = object.getClass();
    }

    // ---------------------------------------------------------------------
    // Static API used as entrance points to the fluent API
    // ---------------------------------------------------------------------

    /**
     * Call a constructor.
     * This is a convenience method for calling create(new Object[0])
     * @return The wrapped new object, to be used for further reflection.
     * @see #create(Object...)
     */
    private ReflectionUtilities create() {return create(new Object[0]);}

    /**
     * Wrap a class name.
     * This is the same as calling on (Class.forName(name))
     * @param name A fully qualified class name
     * @return A wrapped class object, to be used for further reflection.
     * @see #on(Class)
     */
    private static ReflectionUtilities on(String name) {return on(toClass(name));}

    /**
     * Wrap a class.
     * Use this when you want to access static fields and methods on a
     * {@link Class} object, or as a basis for constructing objects of that
     * class using {@link #create(Object...)}
     * @param clazz The class to be wrapped
     * @return A wrapped class object, to be used for further reflection.
     */
    private static ReflectionUtilities on(Class<?> clazz) {
        return new ReflectionUtilities(clazz);
    }

    /**
     * Wrap an object.
     * Use this when you want to access instance fields and methods on any object
     * @param object The object to be wrapped
     * @return A wrapped object, to be used for further reflection.
     */
    private static ReflectionUtilities on(Object object) {
        return new ReflectionUtilities(object);
    }

    /**
     * Wrap an object created from a constructor
     */
    private static ReflectionUtilities on(Constructor<?> constructor, Object... args){
        try {
            return on(accessible(constructor).newInstance(args));
        }catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            logger.warn("ReflectionUtilities::on ->", e);
            return null;
        }
    }

    /**
     * Wrap an object returned from a method
     */
    private static ReflectionUtilities on(Method method, Object object, Object... args) {
        try {
            accessible(method);
            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return on(object);
            }
            else return on(method.invoke(object, args));

        }catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.warn("ReflectionUtilities::on ->", e);
            return null;
        }
    }

    /**
     * Method to Call a method by its name.
     * This is a convenience method for calling call(name, new Object[0])
     * @param name The method name
     * @return The wrapped method result or the same wrapped object if the
     *         method returns void, to be used for further reflection.
     */
    private ReflectionUtilities call(String name){
        return call(name, new Object[0]);
    }

    /**
     * Method to Call a method by its name.
     * @param nameMethod The method name
     * @param args The method arguments
     * @return The wrapped method result or the same wrapped object if the
     *         method returns void, to be used for further reflection.
     */
    private ReflectionUtilities call(String nameMethod, Object... args) {
        Class<?>[] types = toClasses(args);
        // Try invoking the "canonical" method, i.e. the one with exact
        // matching argument types
        try {
            Method method = getExactMethod(nameMethod, types);
            return on(method, object, args);
        }
        // If there is no exact match, try to find a method that has a "similar"
        // signature if primitive argument types are converted to their wrappers
        catch (NoSuchMethodException e) {
            try {
                Method method = getSimilarMethod(nameMethod, types);
                return on(method, object, args);
            } catch (NoSuchMethodException e1) {
                logger.warn("ReflectionUtilities::call ->", e);
                return null;
            }
        }
    }

    /**
     * Method to call a Call a constructor.
     * @param args The constructor arguments
     * @return The wrapped new object, to be used for further reflection.
     */
     private ReflectionUtilities create(Object... args) {
        Class<?>[] types = toClasses(args);
        // Try invoking the "canonical" constructor, i.e. the one with exact
        // matching argument types
        try {
            Constructor<?> constructor = type().getDeclaredConstructor(types);
            return on(constructor, args);
        }
        // If there is no exact match, try to find one that has a "similar"
        // signature if primitive argument types are converted to their wrappers
        catch (NoSuchMethodException e) {
            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (isMatch(constructor.getParameterTypes(), types)) {
                    return on(constructor, args);
                }
            }
            logger.warn("ReflectionUtilities::create ->", e);
            return null;
        }
    }

    /**
     * Get a wrapped field.
     * This is roughly equivalent to {@link Field#get(Object)}. If the wrapped
     * object is a {@link Class}, then this will wrap a static member field. If
     * the wrapped object is any other {@link Object}, then this wrap an
     * instance member field.
     *
     * @param name The field name
     * @return The wrapped field
     */
    private ReflectionUtilities field(String name) {
        try {
            Field field = toField(name);
            return on(field.get(object));
        }
        catch (Exception e) {
            logger.warn("ReflectionUtilities::field ->", e);
            return null;
        }
    }

    /**
     * Get the wrapped object
     * @param <T> A convenience generic parameter for automatic unsafe casting
     * @return the Object on the reflection zone.
     */
    @SuppressWarnings("unchecked")
    private <T> T get() {
        return (T) object;
    }

    /**
     * Method to Get a field value.
     * @param <T> the generic type.
     * @param name The field name
     * @return The field value
     * @throws Exception If any reflection exception occurred.
     */
    private <T> T get(String name) throws Exception {
        return field(name).get();
    }

    /**
     * Method to Get the type of the wrapped object.
     * @return the Class of the Object.
     */
    private Class<?> type(){
        if (isClass)  return (Class<?>) ReflectionUtilities.object;
        else return ReflectionUtilities.object.getClass();
    }

    // ---------------------------------------------------------------------
    // Other Methods
    // ---------------------------------------------------------------------

    /**
     * Method to Get an array of types for an array of objects
     * @param values the array of Objects to convert to Classes.
     * @return the Array of Classes.
     * @see Object#getClass()
     */
    public static Class<?>[] toClasses(Object... values) {
        if (values == null) return new Class<?>[0];
        Class<?>[] result = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }
        return result;
    }

    /**
     * Determines if a method has a "similar" signature, especially if wrapping
     * primitive argument types would result in an exactly matching signature.
     * @param possiblyMatchingMethod the Method to inspect.
     * @param desiredMethodName the desired Method to find.
     * @param desiredParamTypes the Desired aprameters of the Method.
     * @return if true the two Methods are similar.
     */
    public static  boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) &&
                isMatch(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    /**
     * Check whether two arrays of types match, converting primitive types to
     * their corresponding wrappers.
     * @param declaredTypes the Classes declared.
     * @param actualTypes the actual Classes
     * @return if true the two arrays match and are converted to the correpsonding Warpper.
     */
    public static boolean isMatch(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)continue;
                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))continue;
                return false;
            }
            return true;
        }
        else return false;
    }

    /**
     * Get the POJO property name of an getter/setter
     * @param string the String name of the Method.
     * @return the Property of a Method.
     */
    public static String getProperty(String string) {
        int length = string.length();
        if (length == 0)  return "";
        else if (length == 1) return string.toLowerCase();
        else  return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    /**
     * Searches a method with a similar signature as desired using
     * {@link #isSimilarSignature(Method, String, Class[])}.
     * First public methods are searched in the class hierarchy, then private
     * methods on the declaring class. If a method could be found, it is
     * returned, otherwise a {@code NoSuchMethodException} is thrown.
     * @param methodName the String anme of the Method.
     * @param types the Array of Classes to check.
     * @return the Method with the parmaeters with same class check.
     * @throws java.lang.NoSuchMethodException throw if any error is occurred.
     */
    public static Method getSimilarMethod(String methodName, Class<?>[] types) 
            throws NoSuchMethodException {
        Class<?> type = toClass(methodName);
        // first priority: find a public method with a "similar" signature in class hierarchy
        // similar interpreted in when primitive argument types are converted to their wrappers
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, methodName, types))return method;
        }
        // second priority: find a non-public method with a "similar" signature on declaring class
        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, methodName, types)) return method;
            }
            type = type.getSuperclass();
        }
        while (type != null);
        logger.error("No similar method " + methodName + " with params "
                + Arrays.toString(types) +
                " could be found on type " + toClass(methodName) + ".");
        return null;
    }

    private static List<String> primitiveNames;
    private static List<String> wrapperNames;
    private static List<Class<?>> primitiveTypes;
    private static List<Class<?>> wrapperTypes;

    private static void initPrimitives() {
        if (primitiveNames == null) {
            primitiveNames = Arrays.asList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void");
            primitiveTypes = Arrays.asList(boolean.class, char.class, byte.class, short.class, int.class,
                    long.class, float.class, double.class, void.class);
        }
    }

    private static void initWrappers() {
        if (wrapperNames == null) {
            wrapperNames = Arrays.asList("Boolean", "Character", "Byte", "Short", "Integer", "Long", "Float", "Double", "Void");
            wrapperTypes = Arrays.asList(Boolean.class,Character.class,Byte.class,Short.class,Integer.class,
                    Long.class,Float.class,Double.class,Void.class);
        }
    }


    private static List<String> getPrimitiveNames() { initPrimitives(); return primitiveNames; }
    private static List<Class<?>> getPrimitiveTypes() { initPrimitives(); return primitiveTypes; }
    private static List<String> getWrapperNames() { initWrappers(); return wrapperNames; }
    private static List<Class<?>> getWrapperTypes() { initWrappers(); return wrapperTypes; }

    /**
     * Method to check if a specific class is a Wrapper class.
     * @param aClass class of the object you want to test.
     * @return boolean value if is a primite type or not.
     */
    public static boolean isWrapperType(Class<?> aClass) {
        getWrapperTypes();
        return wrapperTypes.contains(aClass);
    }

    /**
     * Method to check if a specific class is a primitive class.
     * @param aClass class of the object you want to test.
     * @return boolean value if is a primite type or not.
     */
    public static boolean isPrimitiveType(Class<?> aClass) {
        return primitiveTypes.contains(aClass);
    }

    /**
     * Get a wrapper type for a primitive type, or the argument type itself, if
     * it is not a primitive type.
     * @param type the primitive class.
     * @return the Wrapped Class.
     */
    public static Class<?> wrapper(Class<?> type) {
        if (type == null) return null;
        else if (type.isPrimitive()) {
            if (boolean.class == type) return Boolean.class;
            else if (int.class == type) return Integer.class;
            else if (long.class == type) return Long.class;
            else if (short.class == type) return Short.class;
            else if (byte.class == type)  return Byte.class;
            else if (double.class == type) return Double.class;
            else if (float.class == type) return Float.class;
            else if (char.class == type)  return Character.class;
            else if (void.class == type) return Void.class;
        }
        return type;
    }

    /**
     * Get a primitive type for a wrapper type, or the argument type itself, if
     * it is not a wrapper type.
     * @param type the primitive class.
     * @return the Wrapped Class.
     */
    public static Class<?> unWrapper(Class<?> type) {
        if (type == null) return null;
        else if (!type.isPrimitive()) {
            if (Boolean.class == type) return boolean.class;
            else if (Integer.class == type) return int.class;
            else if (Long.class == type) return long.class;
            else if (Short.class == type) return short.class;
            else if (Byte.class == type)  return byte.class;
            else if (Double.class == type) return double.class;
            else if (Float.class == type) return float.class;
            else if (Character.class == type)  return char.class;
            else if (Void.class == type) return void.class;
        }
        return type;
    }

    /**
     * Conveniently render an {@link AccessibleObject} accessible.
     * <p>
     * To prevent {@link SecurityException}, this is only done if the argument
     * object and its declaring class are non-public.
     * @param <T> the generic type.
     * @param accessible The object to render accessible
     * @return The argument object rendered accessible
     */
    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) return null;
        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers()) &&
                    Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }
        // Accessible flag is set to false by default, also for public members.
        if (!accessible.isAccessible())accessible.setAccessible(true);
        return accessible;
    }

    /**
     * Create a proxy for the wrapped object allowing to type safely invoke
     * methods on it using a custom interface
     * @param <P> the generic type.
     * @param proxyType The interface type that is implemented by the proxy
     * @return A proxy for the wrapped object
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public <P> P as(Class<P> proxyType) {
        final boolean isMap = (object instanceof Map);
        final InvocationHandler handler = new InvocationHandler() {
            @SuppressWarnings("null")
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                // Actual method name matches always come first
                try {
                    return on(object).call(name, args).get();
                }catch (Exception e) {
                    if (isMap) {
                        Map<String, Object> map = (Map<String, Object>) object;
                        int length = (args == null ? 0 : args.length);

                        if (length == 0 && name.startsWith("get")) {
                            return map.get(getProperty(name.substring(3)));
                        }
                        else if (length == 0 && name.startsWith("is")) {
                            return map.get(getProperty(name.substring(2)));
                        }
                        else if (length == 1 && name.startsWith("set")) {
                            map.put(getProperty(name.substring(3)), args[0]);
                            return null;
                        }
                    }
                    logger.warn(gm() + e.getMessage(),e);
                    throw e;
                }
            }
        };
        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[] { proxyType }, handler);
    }

    /**
     * Searches a method with the exact same signature as desired.
     * If a public method is found in the class hierarchy, this method is returned.
     * Otherwise a private method with the exact same signature is returned.
     * If no exact match could be found, we let the {@code NoSuchMethodException} pass through.
     */
    private Method getExactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();
        // first priority: find a public method with exact signature match in class hierarchy
        try {return type.getMethod(name, types);}
        // second priority: find a private method with exact signature match on declaring class
        catch (NoSuchMethodException e) {
            do {
                try {return type.getDeclaredMethod(name, types);}
                catch (NoSuchMethodException ignore) {}
                type = type.getSuperclass();
            }
            while (type != null);
            throw new NoSuchMethodException();
        }
    }

    /**
     * Get a Map containing field names and wrapped values for the fields' values.
     * If the wrapped object is a {@link Class}, then this will return static
     * fields. If the wrapped object is any other {@link Object}, then this will
     * return instance fields.
     * These two calls are equivalent
     * on(object).field("myField");
     * on(object).fields().get("myField");
     * @return A map containing field names and wrapped values.
     * @throws Exception throw if any error is occurrred.
     */
    public Map<String, ReflectionUtilities> getFields() throws Exception {
        Map<String, ReflectionUtilities> result = new LinkedHashMap<>();
        Class<?> type = type();
        do {
            for (Field field : type.getDeclaredFields()) {
                if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!result.containsKey(name))
                        result.put(name, field(name));
                }
            }
            type = type.getSuperclass();
        }
        while (type != null);
        return result;
    }

    /**
     * Load a class
     * @param name the String name of the Class.
     * @return the Class with the specific name if exists.
     */
    private static Class<?> toClass(String name){
        try {return Class.forName(name);
        } catch (NullPointerException|ClassNotFoundException e) {
            logger.error(gm() + e.getMessage());
            return null;
        }
    }

    /**
     * Method to count the number of Setter and getter Method.
     * @param aClass the Class to inpsect.
     * @return the number of Setter and Getter for the specific Class.
     */
    public static Integer countGetterAndSetter(Class<?> aClass){
        return findGettersAndSetters(aClass).size()/2;
    }


    /**
     * Method to check if a Method is a getter Method.
     * @param method the Method to check.
     * @return if true is a getter.
     */
    public static boolean isGetter(Method method) {
        if(!method.getName().startsWith("get")) return false;
        if(method.getParameterTypes().length != 0) return false;
        //if(void.class.equals(method.getReturnType())) return false;
        if (Modifier.isPublic(method.getModifiers()) /*&& method.getParameterTypes().length == 0*/) {
            if (method.getName().matches("^get[A-Z].*") &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is[A-Z].*") &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return !void.class.equals(method.getReturnType());
    }

    /**
     * Method to check if a Method is a setter Method.
     * @param method the Method to check.
     * @return if true is a gsetter.
     */
    public static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) return false;
        //if(method.getParameterTypes().length == 1) return true;
        return method.getParameterTypes().length == 1
                && Modifier.isPublic(method.getModifiers())
                && method.getReturnType().equals(void.class)
                && method.getParameterTypes().length == 1
                && method.getName().matches("^set[A-Z].*");
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
     * Method to get a specific method form a the reference class of the object.
     * If you know the precise parameter types of the method you want to access,
     * you can do so rather than obtain the array all methods. This example returns
     * the public method named "nameOfMethod", in the given class which takes a
     * String as parameter.
     * @param aClass class to inspect.
     * @param nameOfMethod name of th method you want to find.
     * @param param class of the parameter of the constructor of the method you wan to find.
     * @return method you found..
     */
    public static Method getMethodByNameAndParam(Class<?> aClass, String nameOfMethod, Class<?>[] param){
        Method method =null;
        try {
            //If the method you are trying to access takes no parameters, pass null as the parameter type array, like this:
            if (ArrayUtilities.isEmpty(param)) method = aClass.getMethod(nameOfMethod);//nameOfMethod, null
            else method = aClass.getMethod(nameOfMethod, param);// String.class
        }catch(NoSuchMethodException e){
            logger.error("ReflectionUtils::getMethodByNameAndParam ->",e);
        }
        return method;
    }

    /**
     * Method to get a specific method form a the reference class of the object.
     * @param MyObject generic object to inspect.
     * @param nameOfMethod name of the method you want to find.
     * @param param class of the parameter of the constructor of the method you wan to find.
     * @param <T> generic type.
     * @return Method you found.
     */
    public static <T> Method getMethodByNameAndParam(T MyObject, String nameOfMethod, Class<?>[] param){
        return getMethodByNameAndParam(MyObject.getClass(), nameOfMethod, param); //String.class
    }

    /**
     * Method to get a specific method form a the reference class of the object.
     * @param clazz the Class to inspect.
     * @param nameOfMethod name of the method you want to find.
     * @param types class of the parameter of the constructor of the method you wan to find.
     * @return Method you found.
     */
    public static Method getMethodByClassAndParam(Class<?> clazz, String nameOfMethod, Class<?>...types)  {
        return getMethodByNameAndParam(clazz, nameOfMethod, types); //String.class
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

    /**
     * Method very simple to get all declared fields.
     * @param clazz the class where search teh field.
     * @return the Array of Declared Field.
     */
    public static Field[] getFieldsClass(Class<?> clazz){
        return clazz.getDeclaredFields();
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
        }catch(NoSuchFieldException e){
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
        return ListUtilities.toArray(types);
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
        return CollectionUtilities.toArray(classes);
    }

    /**
     * Method to get the URL of the source file of the class.
     * @param aClass the Class to inspect.
     * @return the String URL of the location of the source of the file.
     */
    public static URL getCodeSourceLocation(Class<?> aClass) {return aClass.getProtectionDomain().getCodeSource().getLocation(); }

    /**
     * Method to get the URL of the source file of the class.
     * @param aClass the Class to inspect.
     * @return the String URL of the location of the source of the file.
     */
  /*  public static String getClassReference(Class<?> aClass) {
        return aClass.getName();
    }*/

    //Method for get all the class in a package with library reflections
//    public Set<Class<? extends Object>> getClassesByPackage(String pathToPackage){
//        Reflections reflections = new Reflections(pathToPackage);
//        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
//    }

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
            if (annColumn != null) ReflectionUtilities.updateAnnotationValue(annColumn, attributeName, attributeValue);
            else logger.warn("No annotation for the class whit attribute:"+attributeName);
        }catch(NoSuchFieldException e){
            logger.error("ReflectionUtilities::updateAnnotationFieldValue -> ",e);
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
        if(ann!=null)  ReflectionUtilities.updateAnnotationValue(ann, attributeName, attributeValue);
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
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    /**
     * Set the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object} to the specified {@code value}.
     * In accordance with {@link Field#set(Object, Object)} semantics, the new value
     * is automatically unwrapped if the underlying field has a primitive type.
     * @param field the field to set
     * @param target the target object on which to set the field
     * @param value the value to set; may be {@code null}
     */
    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        }catch (IllegalAccessException ex) {
            logger.error("Unexpected reflection exception - " +
                    ex.getClass().getName() + ": " + ex.getMessage(), ReflectionUtilities.class);
        }
    }

    /**
     * Set the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object} to the specified {@code value}.
     * In accordance with {@link Field#set(Object, Object)} semantics, the new value
     * is automatically unwrapped if the underlying field has a primitive type.
     * @param fieldName the field name to set
     * @param target the target object on which to set the field
     * @param value the value to set; may be {@code null}
     */
    public static void setField(String fieldName, Object target, Object value) {
        try {
            Field field = toClass(fieldName).getDeclaredField(fieldName);
            if(field!=null)setField(field,target, value);
            else  logger.error("The field you try to set is NULL, checlk if the Field name is correct");
        } catch (NullPointerException|NoSuchFieldException e) {
            logger.error(e.getMessage(),e);
        }

    }

    /**
     * Method to get a field value from a object.
     * @param object object to inspect.
     * @param fieldName string name of the field you want find.
     * @return the value of the declared field .
     */
    public static Object getFieldValueByName(Object object, String fieldName){
        return getFieldValueByName(object.getClass(), fieldName);
    }

    /**
     * Method to get a  field  value from a class.
     * @param aClass the class to inspect.
     * @param fieldName string name of the field you want find.
     * @return the value of the declared field .
     */
    public static Object getFieldValueByName(Class<?> aClass, String fieldName){
        try{
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object returnValue = field.get(aClass);
            field.setAccessible(false);
            return returnValue;
        }catch(SecurityException|NoSuchFieldException|IllegalArgumentException|IllegalAccessException e){
            logger.error("ReflectionUtilities::getFieldValueByName ->",e);
            return null;
        }
    }

    /**
     * Method to get a list of fields value from a class.
     * @param aClass the class to inspect.
     * @return a list of value of all declared field ont he class.
     */
    public static List<Object> getFieldsValueByClass(Class<?> aClass) {
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
            logger.error(e.getMessage(),e);
        }
        return targetValue;
    }


    /**
     * Method to Retrieves all fields (all access levels) from all classes up the class
     * @param startClass the class whose fields should be retrieved
     * @param exclusiveParent if not null, the base class of startClass whose fields should not be retrieved.
     * @return list of iterable field.
     */
    public static Iterable<Field> getFieldsByClass(Class<?> startClass,  Class<?> exclusiveParent) {
        List<Field> currentClassFields = new ArrayList<>();
        currentClassFields.addAll(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = (List<Field>) getFieldsByClass(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }
        return currentClassFields;
    }

    /**
     * Method for get a specific field on the superclass of the class [NOT TESTED].
     * @param fieldName string name of the field.
     * @param startClass start class.
     * @param exclusiveParent name of the class you have extended.
     * @return the filed on the superclass.
     */
    public static Field getFieldBySuperClass(String fieldName,Class<?> startClass, Class<?> exclusiveParent) {
        Field resultField = null;
        try {
            resultField = startClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            logger.error("ReflectionUtilities::getFieldBySuperClass ->",e);
        }
        if (resultField == null) {
            Class<?> parentClass = startClass.getSuperclass();
            if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
                resultField = getFieldBySuperClass(fieldName, parentClass, exclusiveParent);
            }
        }
        return resultField;
    }

    /**
     * Method to Find a setter method for the give object's property and try to call it.
     * @param obj Object to receive the call
     * @param property property name (without set. First letter will be capitalized)
     * @param value Value of the property.
     * @return boolean value is exists the setter method.
     */
    public static boolean callSetter(Object obj, String property, Object value) {
        HashMap<String, Method> SETTERS_MAP = new HashMap<>();
        String key = String.format("%s.%s(%s)", obj.getClass().getName(),
                property, value.getClass().getName());
        Method m;
        boolean result = false;
        if(!SETTERS_MAP.containsKey(key)) {
            m = findSetter(obj, property, value);
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
                logger.error("ReflectionUtilities::callSetter ->",ex);
            }
        }
        return result;
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
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
     * Method for get the class from a reference path to it.
     * @param pakage string of the reference pakage contatin the class.
     * @param clazz string of the name of the class you wan to find.
     * @return the class if found otherwise a null.
     */
    public static Class<?> getClassFromPath(String pakage,String clazz){
        try {
            return Class.forName(pakage+"."+clazz);
        } catch (ClassNotFoundException e) {
            logger.error("Reflection::getClassFromPath ->",e);
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD BASE 0
    //------------------------------------------------------------------------------------------------------------------

    /**
     *
     * Method to get the name of a class.
     * http://stackoverflow.com/questions/6271417/java-get-the-current-class-name
     * @param clazz the current clazz to analyze.
     * @return the name of the current class.
     */
    public static String nameOfClass(Class<?> clazz){
        String name;
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (enclosingClass != null) {
            name = enclosingClass.getName();
        } else {
            name = clazz.getName();
        }
        if(name.contains("$")) name = name.substring(0, name.indexOf("$"));
        return name;
    }

    /**
     *
     * Method to get the name of a anonymus class.
     * http://stackoverflow.com/questions/6271417/java-get-the-current-class-name
     * @param clazz the current clazz to analyze.
     * @return the name of the current class.
     */
    public static String nameOfAnonymusClass(Class<?> clazz){
        return clazz.getSuperclass().getName();
    }


    /**
     * Method to get the name of the current method,.
     * Input: Thread.currentThread().getStackTrace()
     * @param e the Array of TraceElement.
     * @return the String Name of the current Method.
     */
    public static String nameOfMethod(StackTraceElement e[]){
        boolean doNext = false;
        for (StackTraceElement s : e) {
            if (doNext) return s.getMethodName();
            doNext = s.getMethodName().equals("getStackTrace");
        }
        return null;
    }

    /**
     * Method to get the parent class in generic mode.
     * @param thisClass the current class to inpspect.
     * @return the Parent Class.
     */
    public static Class<?> getTheParentGenericClass(Class<?> thisClass){
        Type t = thisClass.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        return (Class) pt.getActualTypeArguments()[0];
    }

    //-----------------------------------------------------------------------------------------------------------------
    // METHOD BASE 1
    //-----------------------------------------------------------------------------------------------------------------

    /**
     * Determine whether the given field is a "public static final" constant.
     * @param field the field to check
     * @return if true the Field is public and static.
     */
    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }

    /**
     * Determine whether the given method is an "equals" method.
     * @param method the Method to inspect.
     * @return if true the Method is the "equals" Method.
     * @see java.lang.Object#equals(Object)
     */
    public static boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) return false;
        Class<?>[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }

    /**
     * Determine whether the given method is a "hashCode" method.
     * @param method the Method to inspect.
     * @return if true the Method is the "hashCode" Method.
     * @see java.lang.Object#hashCode()
     */
    public static boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode")
                && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is a "toString
     * @param method the Method to inspect.
     * @return if true the Method is the "toString" Method.
     * @see java.lang.Object#toString()
     */
    public static boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is originally declared by {@link java.lang.Object}.
     * @param method the Method to inspect.
     * @return if true the Method is originated froma Object.
     */
    public static boolean isObjectMethod(Method method) {
        if (method == null) return false;
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        }catch (NoSuchMethodException | SecurityException ex) {
            return false;
        }
    }

    /**
     * Naming prefix for CGLIB-renamed methods.
     * @see #isCglibRenamedMethod
     */
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";

    /**
     * Determine whether the given method is a CGLIB 'renamed' method,
     * following the pattern "CGLIB$methodName$0".
     * @param renamedMethod the method to check
     * @return if true the Method is a CgLib.
     * @see org.springframework.cglib.proxy.Enhancer#rename
     */
    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i = name.length() - 1;
            while (i >= 0 && Character.isDigit(name.charAt(i))) { i--;}
            return ((i > CGLIB_RENAMED_METHOD_PREFIX.length()) &&
                    (i < name.length() - 1) && name.charAt(i) == '$');
        }
        return false;
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * @param method the method to make accessible
     * @see java.lang.reflect.Method#setAccessible
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * Make the given constructor accessible, explicitly setting it accessible
     * if necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * @param ctor the constructor to make accessible
     * @see java.lang.reflect.Constructor#setAccessible
     */
    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * This variant retrieves {@link Class#getDeclaredFields()} from a local cache
     * in order to avoid the JVM's SecurityManager check and defensive array copying.
     * @param clazz the class to introspect
     * @return the cached array of fields
     * @see Class#getDeclaredFields()
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * This variant retrieves {@link Class#getDeclaredMethods()} from a local cache
     * in order to avoid the JVM's SecurityManager check and defensive array copying.
     * In addition, it also includes Java 8 default methods from locally implemented
     * interfaces, since those are effectively to be treated just like declared methods.
     * @param clazz the class to introspect
     * @return the cached array of methods.
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }

    /**
     * Get all declared methods on the leaf class and all superclasses.
     * Leaf class methods are included first.
     * @param leafClass the class to introspect
     * @return the Array of all declared Methods.
     */
    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<>(32);
        invokeWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(Method method) {
                methods.add(method);
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * Get the unique set of declared methods on the leaf class and all superclasses.
     * Leaf class methods are included first and while traversing the superclass hierarchy
     * any methods found with signatures matching a method already included are filtered out.
     * @param leafClass the class to introspect
     * @return the Array of all unique declared Methods.
     */
    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<>(32);
        invokeWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(Method method) {
                boolean knownSignature = false;
                Method methodBeingOverriddenWithCovariantReturnType = null;
                for (Method existingMethod : methods) {
                    if (method.getName().equals(existingMethod.getName()) &&
                            Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                        // Is this a covariant return type situation?
                        if (existingMethod.getReturnType() != method.getReturnType() &&
                                existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                            methodBeingOverriddenWithCovariantReturnType = existingMethod;
                        } else {
                            knownSignature = true;
                        }
                        break;
                    }
                }
                if (methodBeingOverriddenWithCovariantReturnType != null) {
                    methods.remove(methodBeingOverriddenWithCovariantReturnType);
                }
                if (!knownSignature && !isCglibRenamedMethod(method)) {
                    methods.add(method);
                }
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
     * semantics, the returned value is automatically wrapped if the underlying field
     * has a primitive type.
     * @param field the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     */
    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        }
        catch (IllegalAccessException ex) {
            logger.error("Unexpected reflection exception - "
                    + ex.getClass().getName() + ": "
                    + ex.getMessage(), ReflectionUtilities.class);
            return null;
        }
    }


    /**
     * Perform the given callback operation on all matching methods of the given
     * class, as locally declared or equivalent thereof (such as default methods
     * on Java 8 based interfaces that the given class implements).
     * @param clazz the class to introspect
     * @param mc the callback to invoke for each method
     * @since 4.2
     * @see #invokeWithMethods
     */
    public static void invokeWithLocalMethods(Class<?> clazz, MethodCallback mc) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            try {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex) {
                logger.error("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class and superclasses.
     * <p>The same named method occurring on subclass and superclass will appear
     * twice, unless excluded by a {@link MethodFilter}.
     * @param clazz the class to introspect
     * @param mc the callback to invoke for each method
     * @see #invokeWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void invokeWithMethods(Class<?> clazz, MethodCallback mc) {
        invokeWithMethods(clazz, mc, null);
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class and superclasses (or given interface and super-interfaces).
     * <p>The same named method occurring on subclass and superclass will appear
     * twice, unless excluded by the specified {@link MethodFilter}.
     * @param clazz the class to introspect
     * @param mc the callback to invoke for each method
     * @param mf the filter that determines the methods to apply the callback to
     */
    public static void invokeWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) continue;
            try {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex) {
                logger.error("Not allowed to access method '" + method.getName() + "': " + ex, ReflectionUtilities.class);
            }
        }
        if (clazz.getSuperclass() != null) {
            invokeWithMethods(clazz.getSuperclass(), mc, mf);
        }
        else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                invokeWithMethods(superIfc, mc, mf);
            }
        }
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     * @param clazz the target class to analyze
     * @param fc the callback to invoke for each field
     * @see #invokeWithFields
     */
    public static void invokeWithLocalFields(Class<?> clazz, FieldCallback fc) {
        for (Field field : getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            }
            catch (IllegalAccessException ex) {
                logger.error("Not allowed to access field '" + field.getName() + "': " + ex, ReflectionUtilities.class);
            }
        }
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     * @param clazz the target class to analyze
     * @param fc the callback to invoke for each field
     */
    public static void invokeWithFields(Class<?> clazz, FieldCallback fc) {
        invokeWithFields(clazz, fc, null);
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     * @param clazz the target class to analyze
     * @param fc the callback to invoke for each field
     * @param ff the filter that determines the fields to apply the callback to
     */
    public static void invokeWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff != null && !ff.matches(field))  continue;
                try {
                    fc.doWith(field);
                }catch (IllegalAccessException ex) {
                    logger.error("Not allowed to access field '" + field.getName() + "': " + ex, ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }


    /**
     * Given the source object and the destination, which must be the same class
     * or a subclass, copy all fields, including inherited fields. Designed to
     * work on objects with public no-arg constructors.
     * @param src the source Object to copy.
     * @param dest the destination Object copied.
     */
    public static void shallowCopyFieldState(final Object src, final Object dest) {
        if (src == null) {
            logger.warn("Source for field copy cannot be null");
            return;
        }
        if (dest == null){
            logger.warn("Destination for field copy cannot be null");
            return;
        }
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            logger.warn("Destination class [" + dest.getClass().getName() +
                    "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        invokeWithFields(src.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                makeAccessible(field);
                Object srcValue = field.get(src);
                field.set(dest, srcValue);
            }
        }, COPYABLE_FIELDS);
    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD BASE - 2
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Method to convert a String name to a Reflection Field
     * @param name the String anme of the Field.
     * @return the Filed with that specific name.
     */
    private Field toField(String name) throws Exception {
        Class<?> type = type();
        try {
            return type.getField(name); // Try getting a public field
        }catch (NoSuchFieldException e) {  // Try again, getting a non-public field
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                }
                catch (NoSuchFieldException ignore) {}
                type = type.getSuperclass();
            }
            while (type != null);
            logger.warn("ReflectionUtilities::toField -> ",e);
            throw new Exception(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //FIND
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Method to get all getter and Setter.
     * @param aClass the class to inspect.
     * @return the List of methods.
     */
    public static Collection<Method> findGettersAndSetters(Class<?> aClass) {
        Collection<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method) || isSetter(method))
                list.add(method);
        return list;
    }

    /**
     * Method to get the Setters of a Class.
     * OLD_NAME: getSettersClassOrder,findSettersClass..
     * @param clazz the Class to inspect.
     * @param ordered if true get the Method with the order.
     * @return the List of Methods.
     */
    public static Collection<Method> findSetters(Class<?> clazz,boolean ordered){
        Collection<Method> list = new ArrayList<>();
        if(ordered) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Method method = findSetter(clazz, field.getName(), field.getType());
                list.add(method);
            }
        }else{
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (isSetter(method))list.add(method);
            }
        }
        return list;
    }

    /**
     * Method to get the Getters of a Class.
     * OLD_NAME: getGettersClassOrder.
     * @param clazz the Class to inspect.
     * @param ordered if true get the Method with the order.
     * @return the List of Methods.
     */
    public static Collection<Method> findGetters(Class<?> clazz,boolean ordered){
        Collection<Method> list = new ArrayList<>();
        if(ordered) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Method method = findGetter(clazz, field.getName(), field.getType());
                list.add(method);
            }
        }else{
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (isGetter(method)) list.add(method);
            }
        }
        return list;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return list of class in the directory package.
     * @throws ClassNotFoundException throw if any error is occurred.
     */
    public static Collection<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        Collection<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        List<File> files = FileUtilities.getFilesFromDirectory(directory);
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
    public static Method findSetter(Object obj,String fieldName, Object value) {
        Class<?> clazzObject = obj.getClass();
        Class<?> clazzField = value.getClass();
        return findSetter(clazzObject, fieldName, clazzField);
    }

    /**
     * Method for get a specific setter method froma object
     * @param clazz class of the object to inspect.
     * @param fieldName string name of the field.
     * @param clazzField class of the field value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findSetter(Class<?> clazz,String fieldName,Class<?> clazzField) {
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
                        logger.error(ex.getMessage(),ex1);
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Method for get a specific getter method froma object.
     * OLD_NAME: findGetterMethod.
     * @param obj object to inspect.
     * @param fieldName string name of the field.
     * @param value object value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findGetter(Object obj,String fieldName, Object value) {
        Class<?> clazzObject = obj.getClass();
        Class<?> clazzField = value.getClass();
        return findGetter(clazzObject, fieldName, clazzField);
    }

    /**
     * Method for get a specific getter method froma object
     * OLD_NAME:findGetterMethod
     * @param clazz class of the object to inspect.
     * @param fieldName string name of the field.
     * @param clazzField class of the field value to set in the obj parameter.
     * @return the setter method.
     */
    public static Method findGetter(Class<?> clazz,String fieldName,Class<?> clazzField) {
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
                        logger.error("ReflectionUtilities::findGetter -> ",ex1);
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
        Class<?> clazzObject  = obj.getClass();
        Class<?> clazzField = value.getClass();
        return findMethod(clazzObject,methodName,clazzField);
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
                        logger.error("ReflectionUtilities::findMethod -> ",ex1);
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name}. Searches all superclasses up to {@link Object}.
     * @param clazz the class to introspect
     * @param name the name of the field
     * @return the corresponding Field object, or {@code null} if not found
     */
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name} and/or {@link Class type}. Searches all superclasses
     * up to {@link Object}.
     * @param clazz the class to introspect
     * @param name the name of the field (may be {@code null} if type is specified)
     * @param type the type of the field (may be {@code null} if name is specified)
     * @return the corresponding Field object, or {@code null} if not found
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        if(clazz==null) {
            logger.warn("Class must not be null");
            return null;
        }
        if(name==null){
            logger.warn("Method name must not be null");
            return null;
        }
        if(type == null) {
            logger.warn("Either name or type of the field must be specified");
            return null;
        }
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = getDeclaredFields(searchType);
            for (Field field : fields) {
                if (name.equals(field.getName()) && type.equals(field.getType())){
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and no parameters. Searches all superclasses up to {@code Object}.
     * <p>Returns {@code null} if no {@link Method} can be found.
     * @param clazz the class to introspect
     * @param name the name of the method
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, new Class<?>[0]);
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to {@code Object}.
     * <p>Returns {@code null} if no {@link Method} can be found.
     * @param clazz the class to introspect
     * @param name the name of the method
     * @param paramTypes the parameter types of the method
     * (may be {@code null} to indicate any signature)
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        if(clazz==null) {
            logger.warn("Class must not be null");
            return null;
        }
        if(name==null){
            logger.warn("Method name must not be null");
            return null;
        }
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (name.equals(method.getName()) &&
                        (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Method to Find concrete method on a Interface class.
     * @param clazz tha Class of the Interface to inspect.
     * @return the Collection of Method for the Interface clazz.
     */
    public static Collection<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        Collection<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new LinkedList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------------------------
    // FIND INFO
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Method to get the properties of a Annotation on a class.
     * OLD_NAME: getAnnotationClass
     * @param annotation the Annotation object to inpsect.
     * @return properteis of the specific annotation.
     */
    @SuppressWarnings("unchecked")
    public static Collection<Object[]> findInfoAnnotationClass(Annotation annotation){
        Collection<Object[]> list = new ArrayList<>();
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
    public static Collection<Object[]> findInfoAnnotationField(Annotation annotation)
            throws SecurityException, NoSuchFieldException {
        Collection<Object[]> list = new HashSet<>();
        Object[] array = new Object[3];
        if(annotation!=null) return getInfoAnnotation(annotation,list,array);
        else return null;
    }

    @SuppressWarnings("unchecked")
    private static Set<Object[]> getInfoAnnotation(
            Annotation annotation, Collection<Object[]> list, Object[] array){
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
        return (Set<Object[]>) list;

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
    public static  Collection<Collection<Object[]>> findInfoAnnotationsFields(Class<?> aClass,Class<? extends Annotation> classAnnotation)
            throws SecurityException, NoSuchFieldException {
        Collection<Collection<Object[]>> result = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for(Field field : fields){
            Annotation ann = field.getAnnotation(classAnnotation);
            Collection<Object[]> list = findInfoAnnotationField(ann);
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
    public static Collection<Object[]> findInfoAnnotationField(Class<? extends Annotation> annotationField,Field field)
            throws SecurityException, NoSuchFieldException {
        Collection<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        final Annotation annotation = field.getAnnotation(annotationField);
        if(annotation != null) {
            return getInfoAnnotation(annotation, list, array);
        }else return null;
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
    public static Collection<Object[]> findInfoAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationField,String fieldName )
            throws SecurityException, NoSuchFieldException {
        return findInfoAnnotationField(annotationField, getFieldByName(aClass, fieldName));
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
    public static Collection<Collection<Object[]>> findInfoAnnotationsFields(Class<?> aClass) throws SecurityException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Collection<Collection<Object[]>> listOfAnnotation = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for(Field field  : fields ){
            Collection<Object[]> list = findInfoAnnotationsField(aClass, field);
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
    public static Collection<Object[]> findInfoAnnotationsField(Class<?> aClass, Field field)
            throws SecurityException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Collection<Object[]> list = new ArrayList<>();
        String fieldName = field.getName();
        Class fieldType = field.getType();
        Object[] fObj = new Object[]{fieldName,fieldType};
        for(Annotation annotation : field.getAnnotations()) {
            Collection<Object[]> aObj = findInfoAnnotation(annotation);
            //final Annotation annotation = field.getAnnotation(annotationClass);
            //list = getAnnotationField(aClass, annotation, fieldName);
            if(aObj != null && !aObj.isEmpty()) {
                Object[] bObj = ArrayUtilities.concatenateArrays(fObj, aObj.toArray());
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
    public static Collection<Object[]> findInfoAnnotation(Annotation annotation) throws InvocationTargetException, IllegalAccessException {
        Collection<Object[]> list = new ArrayList<>();
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
    public static Collection<Collection<Object[]>> findInfoAnnotationsClass(Class<?> aClass)
            throws SecurityException, NoSuchFieldException {
        Collection<Collection<Object[]>> result = new ArrayList<>();
        for(Annotation annotation : aClass.getAnnotations()){
            Collection<Object[]> list = findInfoAnnotationClass(annotation);
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
    public static Collection<String[]> findInfoAnnotationsMethod(Class<?> aClass,String methodName)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
        Method method = aClass.getMethod(methodName);//methodName,null deprecated
        Collection<String[]> list = new ArrayList<>();
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
    public static Collection<String[]> findInfoFieldClass(Object obj) {
        return findInfoFieldClass(obj.getClass());
    }

    /**
     * Method to get all basic information on a method.
     * OLD_NAME: inspectFieldClass.
     * @param clazz Class to inspect.
     * @return list of array name-value for that object.
     */
    public static Collection<String[]> findInfoFieldClass(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Collection<String[]> oField = new ArrayList<>();
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
    public static Collection<String[]> findInfoTypes(Class<?> clazz,String nameOfMethhod) {
        Collection<String[]> list = new ArrayList<>();
        try {
            Method method = clazz.getMethod(nameOfMethhod);
            Type returnType = method.getGenericReturnType();
            if(returnType instanceof ParameterizedType){
                ParameterizedType type = (ParameterizedType) returnType;
                Type[] typeArguments = type.getActualTypeArguments();
                for(Type typeArgument : typeArguments){
                    Class<?> typeArgClass = (Class<?>) typeArgument;
                    list.add(new String[]{typeArgClass.getName(),typeArgClass.getName()});
                }
            }
        } catch (NullPointerException|NoSuchMethodException e) {
            logger.error("ReflectionUtilities::findInfoTypes ->",e);
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
    public static Map<Class<?>,Collection<String[]>> findInfoTypes(Class<?> aClass,Method method) {
        Map<Class<?>,Collection<String[]>> map = new HashMap<>();
        Collection<String[]> list = new ArrayList<>();
        Type returnType = method.getGenericReturnType();
        try {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class<?> typeArgClass = (Class<?>) typeArgument;
                logger.info("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(), typeArgClass.getName()});
            }
        } catch (NullPointerException e) {
            logger.error("ReflectionUtilities::findInfoTypes ->",e);
        }
        map.put(returnType.getClass(),list);
        return map;
    }

    /**
     * Method to get all Getter Method.
     * OLD_NAME: inspectAndLoadGetterObject.
     * @param obj the Object Class.
     * @return the getter methods.
     */
    public static Map<String,Class<?>> findInfoGetter(Object obj){
        Collection<Method> getter = findGetters(obj.getClass(), true);
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
        Collection<Method> setter = findSetters(obj.getClass(), true);
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
    public static Collection<String[]> findInfoConstructor(Class<?> aClass){
        Constructor<?>[] constructors = aClass.getConstructors();
        Collection<String[]> oConst = new ArrayList<>();
        for (Constructor<?> cons : constructors) {
            String modify = Modifier.toString(cons.getModifiers());
            String type = Arrays.toString(cons.getTypeParameters());
            String name = cons.getName();
            oConst.add(new String[]{modify,type,name});
        }
        return oConst;
    }

    //------------------------------------------------------------------------------------------------------------------
    //INVOKE
    //------------------------------------------------------------------------------------------------------------------

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
    public static <T> T invokeMethod(T MyObject, String nameOfMethod, Class<?>[] param)//4th parameter , Class<T> aClass
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Method method;
        T MyTObject2;
        if(param==null || param.length==0 )method = getMethodByNameAndParam(MyObject, nameOfMethod, null);
        else method = getMethodByNameAndParam(MyObject, nameOfMethod, param); //String.class
        try{
            //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
            MyTObject2 = (T) method.invoke(param);
        }catch(NullPointerException ne){
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
    public static Object invokeMethod(Object MyObject,Method method,Class<?>[] param)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        try{
            //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
            MyObject = method.invoke(param);
        }catch(NullPointerException ne){
            //MyObject = method.invoke(MyObject, param); //...if the methos is non-static
            MyObject = method.invoke(MyObject);
        }
        return MyObject;
    }

    /**
     * Method for invoke a method.
     * @param MyObject the object where the methods is applied.
     * @param method the method to invoke.
     * @param param the param for the method.
     * @return a object update with the result of the method.
     */
    public static Object invokeMethod(Object MyObject,Method method,Object[] param){
        try {
            try {
                //MyObject = method.invoke(null, param); //if the method you try to invoke is static...
                MyObject = method.invoke(param);
            } catch (NullPointerException ne) {
                //MyObject = method.invoke(MyObject, param); //...if the methos is non-static
                MyObject = method.invoke(MyObject);
            }
        }catch(IllegalAccessException|InvocationTargetException e){
            logger.error("ReflectionUtilities::invokeMethod ->",e);
            return null;
        }
        return MyObject;
    }

    /**
     * Method to invoke a getter method from a class.
     * OLD_NAME: invokeGetterClass.
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
                    MyObject.getClass(), methodName, new Class<?>[0]);
            MyObject2 = method.invoke(MyObject);
            return MyObject2;
        } catch (InvocationTargetException|IllegalAccessException|SecurityException  e) {
            logger.error("ReflectionUtilities::invokeGetterClass ->",e);
        }
        return null;
    }

    /**
     * Method to invoke a getter method from  a Object.
     * OLD_NAME: invokeSetterMethodForObject.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @param value to set with the setter method.
     * @param <T> generic type.
     * @return the return value of the invoke on the getter method.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeSetter(T MyObject, Method method,Object... value) {
        T MyObject2;
        try {
            try {
                //if the method you try to invoke is static...
                MyObject2 = (T) method.invoke(null, value);
            } catch (NullPointerException ne) {
                //...The method is not static
                MyObject2 = (T) method.invoke(MyObject, value);
            }
        }catch(IllegalAccessException|InvocationTargetException e){
            logger.error(e.getMessage(),e);
            return null;
        }
        return MyObject2;
    }

    /**
     * Method to invoke a getter method from a class.
     * OLD_NAME: invokeSetterClass.
     * @param MyObject object target.
     * @param methodName string name of the method getter.
     * @param value value to set with the setter method.
     * @param clazzValue class of the calue to set.
     * @param <T> generic type.
     * @return the returned value from the getter method is exists.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeSetter(T MyObject,String methodName,Object value,Class<?> clazzValue) {
        try {
            Method method = getMethodByNameAndParam(
                    MyObject.getClass(), methodName, new Class<?>[]{clazzValue});
            MyObject = (T) method.invoke(MyObject,value);
            return MyObject;
        } catch (InvocationTargetException|IllegalAccessException|
                SecurityException|ClassCastException|NullPointerException  e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * Method to invoke a getter method from a class.
     * @param MyObject object target.
     * @param methodName string name of the method getter.
     * @param value value to set with the setter method.
     * @param <T> generic type.
     * @return the returned value from the getter method is exists.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeSetter(T MyObject,String methodName,Object value) {
        return invokeSetter(MyObject,methodName,value,value.getClass());
    }

    /**
     * Method to invoke a getter method from  a Object.
     * OLD_NAME: invokeGetterMethodForObject.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @param <T> generic type.
     * @return the return value of the invoke on the getter method.
     */
    @SuppressWarnings("unchecked")
    public static <T> Object invokeGetter(T MyObject, Method method){
        Object MyObject2;
        try {
            Class<T> aClazz = (Class<T>) MyObject.getClass();
            try {
                //if the method you try to invoke is static...
                MyObject2 = method.invoke(null);
            } catch (NullPointerException ne) {
                //...The method is not static
            /*if( method.invoke(MyObject) instanceof URL){
                URL ee = (URL) method.invoke(MyObject);
                Class<?> clazz = ee.getClass();
                MyObject2 = clazz.cast(method.invoke(MyObject));
            }else{
                MyObject2 = method.invoke(MyObject);
            }*/
                try {
                    Class<?> clazz2 = method.invoke(MyObject).getClass();
                    MyObject2 = clazz2.cast(method.invoke(MyObject));
                } catch (NullPointerException ne1) {
                    MyObject2 = method.invoke(MyObject);
                }
            }
        }catch(IllegalAccessException|InvocationTargetException e){
            logger.error("ReflectionUtilities::invokeGetter ->",e);
            return null;
        }
        return MyObject2;
    }

    /**
     * Method to invoke a getter method from  a Object.
     * OLD_NAME: invokeGetterMethod.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @return the return value of the invoke on the getter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
   /* public static Object invokeGetter2(Object MyObject,Method method)
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
    }*/

    /**
     * Method to get the return value of a constructor that takes specific arguments.
     * @param <T> generic type.
     * @param MyObject object T in input.
     * @param param arrays of class for the constructor.
     * @param defaultValues array of object for the constructor.
     * @return T object return from the constructor.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeConstructor(T MyObject, Class<?>[] param, Object[] defaultValues){
        Class<T> aClazz= (Class<T>)MyObject.getClass();
        return aClazz.cast(invokeConstructor(MyObject.getClass(), param, defaultValues));
    }


    /**
     * Method to get the return value of a constructor that takes specific arguments.
     * @param <T> generic type.
     * @param clazz the class where is the constructor.
     * @param param arrays of class for the constructor.
     * @param defaultValues array of object for the constructor.
     * @return T object return from the constructor.
     * @throws IllegalArgumentException throw if any error is occurred.
     */
    public static <T> T invokeConstructor(Class<T> clazz, Class<?>[] param, Object[] defaultValues){
        try {
            if(param == null){
                return clazz.newInstance();
            }else {
                Constructor<?> constructor = clazz.getConstructor(param);
                return clazz.cast(constructor.newInstance(defaultValues));
            }
        }catch(NoSuchMethodException|InstantiationException|IllegalAccessException
                |IllegalArgumentException|InvocationTargetException e){
            logger.error("ReflectionUtilities::invokeConstructor ->",e);
            return null;
        }
    }

    /**
     * Method to get the return value of a constructor that takes specific arguments.
     * @param <T> generic type.
     * @param clazz the class where is the constructor.
     * @return T object return from the constructor.
     */
    public static <T> T invokeConstructor(Class<T> clazz) {
        return invokeConstructor(clazz,null,null);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with no arguments.
     * The target object can be {@code null} when invoking a static {@link Method}.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, new Object[0]);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        }catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error(ex.getMessage(),ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static <T> boolean checkMethod(T foo,Method method){
        return checkMethod(foo.getClass(),method.getName());
    }

    public static <T> boolean checkMethod(Class<T> clazz,Method method){
        return checkMethod(clazz,method.getName());
    }

    public static <T> boolean checkMethod(T foo,String nameMethod){
        return checkMethod(foo.getClass(),nameMethod);
    }

    public static <T> boolean checkMethod(Class<T> clazz,String nameMethod){
        /*boolean hasMethod = false;
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(nameMethod)) {
                hasMethod = true;
                break;
            }
        }
        return hasMethod;*/
        return findMethod(clazz,nameMethod) != null;
    }

    /**
     * Invoke the specified JDBC API {@link Method} against the supplied target
     * object with no arguments.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @throws SQLException the JDBC API SQLException to rethrow (if any)
     * @see #invokeJdbcMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
        return invokeJdbcMethod(method, target, new Object[0]);
    }

    /**
     * Invoke the specified JDBC API {@link Method} against the supplied target
     * object with the supplied arguments.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */
    public static Object invokeJdbcMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException ex) {
            logger.error(ex.getMessage(),ex);
        }
        catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SQLException) {
                logger.error(ex.getMessage(),ex);
            }
        }
        throw new IllegalStateException("Should never get here");
    }

    //------------------------------------------------------------------------------------------------------------------
    // OLD ReflectionUtils Method.
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Determine if the passed in class (classToCheck) has the annotation (annoClass) on itself,
     * any of its super classes, any of it's interfaces, or any of it's super interfaces.
     * This is a exhaustive check throughout the complete inheritance hierarchy.
     * @param classToCheck the Class to check.
     * @param annoClass the Class Annotation to find.
     * @return the Annotation if found, null otherwise.
     */
    public static Annotation getAnnotationByClass(
            final Class<?> classToCheck, final Class<? extends Annotation> annoClass){
        final Set<Class<?>> visited = new HashSet<>();
        final LinkedList<Class<?>> stack = new LinkedList<>();
        stack.add(classToCheck);

        while (!stack.isEmpty()) {
            Class<?> classToChk = stack.pop();
            if (classToChk == null || visited.contains(classToChk))
            {
                continue;
            }
            visited.add(classToChk);
            Annotation a = classToChk.getAnnotation(annoClass);
            if (a != null)
            {
                return a;
            }
            stack.push(classToChk.getSuperclass());
            addInterfaces(classToChk, stack);
        }
        return null;
    }

    private static void addInterfaces(final Class<?> classToCheck, final LinkedList<Class<?>> stack) {
        for (Class<?> interFace : classToCheck.getInterfaces()) {
            stack.push(interFace);
        }
    }

    /**
     * Method to get Annotation froma Method.
     * @param method the method to inspect.
     * @param annoClass the Annotation Class to search.
     * @return the Annotation you found.
     */
    public static Annotation getAnnotationByMethod(
            final Method method, final Class<? extends Annotation> annoClass) {
        final Set<Class<?>> visited = new HashSet<>();
        final LinkedList<Class<?>> stack = new LinkedList<>();
        stack.add(method.getDeclaringClass());
        while (!stack.isEmpty()) {
            Class<?> classToChk = stack.pop();
            if (classToChk == null || visited.contains(classToChk))continue;
            visited.add(classToChk);
            Method m = getMethodByClassAndParam(classToChk, method.getName(), method.getParameterTypes());
            if (m == null) continue;
            Annotation a = m.getAnnotation(annoClass);
            if (a != null)return a;
            stack.push(classToChk.getSuperclass());
            addInterfaces(method.getDeclaringClass(), stack);
        }
        return null;
    }

    /**
     * Get all non static, non transient, fields of the passed in class, including
     * private fields. Note, the special this$ field is also not returned.  The result
     * is cached in a static ConcurrentHashMap to benefit execution performance.
     * @param c Class instance
     * @return Collection of only the fields in the passed in class
     * that would need further processing (reference fields).  This
     * makes field traversal on a class faster as it does not need to
     * continually process known fields like primitives.
     */
    public static Collection<Field> getDeepDeclaredFields(Class<?> c){
        Map<Class<?>, Collection<Field>> _reflectedFields = new ConcurrentHashMap<>();
        if (_reflectedFields.containsKey(c)) {
            return _reflectedFields.get(c);
        }
        Collection<Field> fields = new ArrayList<>();
        Class<?> curr = c;
        while (curr != null) {
            getDeclaredFields(curr, fields);
            curr = curr.getSuperclass();
        }
        _reflectedFields.put(c, fields);
        return fields;
    }

    /**
     * Get all non static, non transient, fields of the passed in class, including
     * private fields. Note, the special this$ field is also not returned.  The
     * resulting fields are stored in a Collection.
     * @param c Class instance
     * that would need further processing (reference fields).  This
     * makes field traversal on a class faster as it does not need to
     * continually process known fields like primitives.
     * @param fields the collection of Fields to search.
     * @return the Array of declared fields of the Class.
     */
    public static Field[] getDeclaredFields(Class<?> c, Collection<Field> fields) {
        Field[] local = c.getDeclaredFields();
        try{
            for (Field field : local){
                if (!field.isAccessible()) {
                    try{
                        field.setAccessible(true);
                    }
                    catch (Exception ignored) { }
                }
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers) &&
                        !field.getName().startsWith("this$") &&
                        !Modifier.isTransient(modifiers)){
                        // speed up: do not count static fields, do not go back up
                        // to enclosing object in nested case, do not consider transients
                    fields.add(field);
                }
            }
        }catch (Throwable ignored){
            logger.warn(ignored.getMessage(),ignored);
        }
        return local;
    }

    /**
     * Return all Fields from a class (including inherited), mapped by
     * String field name to java.lang.reflect.Field.
     * @param c Class whose fields are being fetched.
     * @return Map of all fields on the Class, keyed by String field
     * name to java.lang.reflect.Field.
     */
    public static Map<String, Field> getDeepDeclaredFieldMap(Class<?> c) {
        Map<String, Field> fieldMap = new HashMap<>();
        Collection<Field> fields = getDeepDeclaredFields(c);
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldMap.containsKey(fieldName)){   // Can happen when parent and child class both have private field with same name
                fieldMap.put(field.getDeclaringClass().getName() + '.' + fieldName, field);
            }else{
                fieldMap.put(fieldName, field);
            }
        }
        return fieldMap;
    }

    /**
     * Return the name of the class on the object, or "null" if the object is null.
     * @param o Object to get the class name.
     * @return String name of the class or "null"
     */
    public static String getClassName(Object o) {
        return o == null ? "null" : o.getClass().getName();
    }


    //------------------------------------------------------------------------------------------------------------------
    //INTERFACE SUPPORT
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Action to take on each method.
     */
    public interface MethodCallback {

        /**
         * Perform an operation using the given method.
         * @param method the method to operate on
         * @throws java.lang.IllegalAccessException  throw if any error is occurred.
         */
        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }


    /**
     * Callback optionally used to filter methods to be operated on by a method callback.
     */
    public interface MethodFilter {

        /**
         * Determine whether the given method matches.
         * @param method the method to check
         * @return if true the Method is matched.
         */
        boolean matches(Method method);
    }


    /**
     * Callback interface invoked on each field in the hierarchy.
     */
    public interface FieldCallback {

        /**
         * Perform an operation using the given field.
         * @param field the field to operate on
         * @throws java.lang.IllegalAccessException throw if any error is occurred.
         */
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }


    /**
     * Callback optionally used to filter fields to be operated on by a field callback.
     */
    public interface FieldFilter {

        /**
         * Determine whether the given field matches.
         * @param field the field to check
         * @return if true the Field is Matched.
         */
        boolean matches(Field field);
    }


    /**
     * Pre-built FieldFilter that matches all non-static, non-final fields.
     */
    public static FieldFilter COPYABLE_FIELDS = new FieldFilter() {

        @Override
        public boolean matches(Field field) {
            return !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()));
        }
    };


    /**
     * Pre-built MethodFilter that matches all non-bridge methods.
     */
    public static MethodFilter NON_BRIDGED_METHODS = new MethodFilter() {

        @Override
        public boolean matches(Method method) {
            return !method.isBridge();
        }
    };


    /**
     * Pre-built MethodFilter that matches all non-bridge methods
     * which are not declared on {@code java.lang.Object}.
     */
    public static MethodFilter USER_DECLARED_METHODS = new MethodFilter() {

        @Override
        public boolean matches(Method method) {
            return (!method.isBridge() && method.getDeclaringClass() != Object.class);
        }
    };










}
