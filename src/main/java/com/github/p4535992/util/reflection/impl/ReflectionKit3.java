package com.github.p4535992.util.reflection.impl;


import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.log.SystemLog;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
 * href: http://stackoverflow.com/questions/1555326/java-class-cast-vs-cast-operator
 * href:
 *
 */
@SuppressWarnings("unused")
public final class ReflectionKit3 {

    // ---------------------------------------------------------------------
    // Members
    // ---------------------------------------------------------------------

    /**The wrapped object*/
    private static Object  object;
    /**
     * A flag indicating whether the wrapped object is a {@link Class} (for
     * accessing static fields and methods), or any other type of {@link Object}
     * (for accessing instance fields and methods).
     */
    private static boolean isClass;

    private static Class<?> clazz;

    /** The Map of Wrapper Class */
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public ReflectionKit3(){
        //java.lang.reflect.Type t =getClass().getGenericSuperclass();
        //java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        //this.cl =  (Class<T>) pt.getActualTypeArguments()[0];
        //this.clName = cl.getSimpleName();
        ReflectionKit3.isClass = false;
        ReflectionKit3.object = null;
        ReflectionKit3.clazz = null;
    }

    private ReflectionKit3(Class<?> type) {
        ReflectionKit3.object = type;
        ReflectionKit3.isClass = true;
        ReflectionKit3.clazz = type();
    }

    private ReflectionKit3(Object object) {
        ReflectionKit3.object = object;
        ReflectionKit3.isClass = false;
        ReflectionKit3.clazz = type();
    }

    // ---------------------------------------------------------------------
    // Static API used as entrance points to the fluent API
    // ---------------------------------------------------------------------

    /**
     * Set a field value.
     * <p>
     * This is roughly equivalent to {@link Field#set(Object, Object)}. If the
     * wrapped object is a {@link Class}, then this will set a value to a static
     * member field. If the wrapped object is any other {@link Object}, then
     * this will set a value to an instance member field.
     *
     * @param name The field name
     * @param value The new field value
     * @return The same wrapped object, to be used for further reflection.
     * @throws Exception If any reflection exception occurred.
     */
    public ReflectionKit3 set(String name, Object value) throws Exception {
        try {
            Field field = convertStringToReflectionField(name);
            field.set(object, unwrappper(value));
            return this;
        }
        catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Call a constructor.
     * <p>
     * This is a convenience method for calling
     * <code>create(new Object[0])</code>
     *
     * @return The wrapped new object, to be used for further reflection.
     * @throws Exception throw if any error is occurred.
     * @see #create(Object...)
     */
    public ReflectionKit3 create() throws Exception {return create(new Object[0]);}

    /**
     * Wrap a class name.
     * <p>
     * This is the same as calling <code>on(Class.forName(name))</code>
     *
     * @param name A fully qualified class name
     * @return A wrapped class object, to be used for further reflection.
     * @throws Exception throw if any error is occurred.
     * @see #on(Class)
     */
    public static ReflectionKit3 on(String name) throws Exception {
        return on(forName(name));
    }

    /**
     * Wrap a class.
     * <p>
     * Use this when you want to access static fields and methods on a
     * {@link Class} object, or as a basis for constructing objects of that
     * class using {@link #create(Object...)}
     *
     * @param clazz The class to be wrapped
     * @return A wrapped class object, to be used for further reflection.
     */
    public static ReflectionKit3 on(Class<?> clazz) {
        return new ReflectionKit3(clazz);
    }

    /**
     * Wrap an object.
     * <p>
     * Use this when you want to access instance fields and methods on any
     * {@link Object}
     *
     * @param object The object to be wrapped
     * @return A wrapped object, to be used for further reflection.
     */
    public static ReflectionKit3 on(Object object) {
        return new ReflectionKit3(object);
    }


    /**
     * Wrap an object created from a constructor
     */
    private static ReflectionKit3 on(Constructor<?> constructor, Object... args){
        try {return on(accessible(constructor).newInstance(args));}
        catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            SystemLog.warning(e);
            return null;
        }
    }

    /**
     * Wrap an object returned from a method
     */
    private static ReflectionKit3 on(Method method, Object object, Object... args) throws Exception {
        try {
            accessible(method);
            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return on(object);
            }
            else {
                return on(method.invoke(object, args));
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            SystemLog.warning(e);
            throw new Exception(e);
        }
    }

    /**
     * Call a method by its name.
     * <p>
     * This is a convenience method for calling
     * <code>call(name, new Object[0])</code>
     *
     * @param name The method name
     * @return The wrapped method result or the same wrapped object if the
     *         method returns <code>void</code>, to be used for further
     *         reflection.
     * @throws Exception throw if any error is occurred.
     * @see #call(String, Object...)
     */
    public ReflectionKit3 call(String name) throws Exception {
        return call(name, new Object[0]);
    }

    /**
     * Call a method by its name.
     *
     * This is roughly equivalent to {@link Method#invoke(Object, Object...)}.
     * If the wrapped object is a {@link Class}, then this will invoke a static
     * method. If the wrapped object is any other {@link Object}, then this will
     * invoke an instance method.
     *
     * Just like {@link Method#invoke(Object, Object...)}, this will try to wrap
     * primitive types or unwrap primitive type wrappers if applicable. If
     * several methods are applicable, by that rule, the first one encountered
     * is called. i.e. when calling
     * on(...).call("method", 1, 1);
     * The first of the following methods will be called:
     *
     * public void method(int param1, Integer param2);
     * public void method(Integer param1, int param2);
     * public void method(Number param1, Number param2);
     * public void method(Number param1, Object param2);
     * public void method(int param1, Object param2);
     *
     *
     * The best matching method is searched for with the following strategy:
     *
     * public method with exact signature match in class hierarchy
     * non-public method with exact signature match on declaring class
     * public method with similar signature in class hierarchy
     * non-public method with similar signature on declaring class
     *
     *
     * @param name The method name
     * @param args The method arguments
     * @return The wrapped method result or the same wrapped object if the
     *         method returns void, to be used for further
     *         reflection.
     * @throws Exception throw if any error is occurred.
     *
     */
    public ReflectionKit3 call(String name, Object... args) throws Exception {
        Class<?>[] types = types(args);
        // Try invoking the "canonical" method, i.e. the one with exact
        // matching argument types
        try {
            Method method = exactMethod(name, types);
            return on(method, object, args);
        }
        // If there is no exact match, try to find a method that has a "similar"
        // signature if primitive argument types are converted to their wrappers
        catch (NoSuchMethodException e) {
            try {
                Method method = similarMethod(name, types);
                return on(method, object, args);
            } catch (NoSuchMethodException e1) {
                throw new Exception(e);
            }
        }
    }

    /**
     * Call a constructor.
     *
     * This is roughly equivalent to {@link Constructor#newInstance(Object...)}.
     * If the wrapped object is a {@link Class}, then this will create a new
     * object of that class. If the wrapped object is any other {@link Object},
     * then this will create a new object of the same type.
     *
     * Just like {@link Constructor#newInstance(Object...)}, this will try to
     * wrap primitive types or unwrap primitive type wrappers if applicable. If
     * several constructors are applicable, by that rule, the first one
     * encountered is called. i.e. when calling
     * on(C.class).create(1, 1);
     * The first of the following constructors will be applied:
     *
     * public C(int param1, Integer param2);
     * public C(Integer param1, int param2);
     * public C(Number param1, Number param2);
     * public C(Number param1, Object param2);
     * public C(int param1, Object param2);
     *
     *
     * @param args The constructor arguments
     * @return The wrapped new object, to be used for further reflection.
     * @throws Exception throw if any error is occurred.
     */
    public ReflectionKit3 create(Object... args) throws Exception {
        Class<?>[] types = types(args);
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
                if (match(constructor.getParameterTypes(), types)) {
                    return on(constructor, args);
                }
            }
            SystemLog.warning(e);
            throw new Exception(e);
        }
    }

    /**
     * Get a wrapped field.
     * <p>
     * This is roughly equivalent to {@link Field#get(Object)}. If the wrapped
     * object is a {@link Class}, then this will wrap a static member field. If
     * the wrapped object is any other {@link Object}, then this wrap an
     * instance member field.
     *
     * @param name The field name
     * @return The wrapped field
     * @throws Exception throw if any error is occurred.
     */
    public ReflectionKit3 field(String name) throws Exception {
        try {
            Field field = convertStringToReflectionField(name);
            return on(field.get(object));
        }
        catch (Exception e) {
            SystemLog.warning(e);
            throw new Exception(e);
        }
    }

    // ---------------------------------------------------------------------
    // Methods
    // ---------------------------------------------------------------------

    /**
     * Get the wrapped object
     * @param <T> A convenience generic parameter for automatic unsafe casting
     * @return the Object on the reflection zone.
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) object;
    }

    /**
     * Get a field value.
     * <p>
     * This is roughly equivalent to {@link Field#get(Object)}. If the wrapped
     * object is a {@link Class}, then this will get a value from a static
     * member field. If the wrapped object is any other {@link Object}, then
     * this will get a value from an instance member field.
     * <p>
     * If you want to "navigate" to a wrapped version of the field, use
     * {@link #field(String)} instead.
     *
     * @param <T> the generic type.
     * @param name The field name
     * @return The field value
     * @throws Exception If any reflection exception occurred.
     * @see #field(String)
     */
    public <T> T get(String name) throws Exception {
        return field(name).<T>get();
    }


    /**
     * Method to check if a specific class is a Wrapper class.
     * @param aClass class of the object you want to test.
     * @return boolean value if is a primite type or not.
     */
    public static boolean isWrapperType(Class<?> aClass) {
        return WRAPPER_TYPES.contains(aClass);
    }

    /**
     * Method to check if a specific class is a primitive class.
     * @param aClass class of the object you want to test.
     * @return boolean value if is a primite type or not.
     */
    public static boolean isPrimitiveType(Class<?> aClass) {
        return !WRAPPER_TYPES.contains(aClass);
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

    /**
     * Method to convert a primitive class to a Wrapper class.
     * @param clazz the primitive class
     * @return the Wrapper class.
     */
    public static Class<?> convertPrimitiveClassToWrapperClass(Class<?> clazz){
        return wrapper(clazz);
    }

    /**
     * Method to convert a String name to a Reflection Field
     * @param name the String anme of the Field.
     * @return the Filed with that specific name.
     */
    private Field convertStringToReflectionField(String name) throws Exception {
        Class<?> type = type();
        // Try getting a public field
        try {
            return type.getField(name);
        }
        // Try again, getting a non-public field
        catch (NoSuchFieldException e) {
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                }
                catch (NoSuchFieldException ignore) {}
                type = type.getSuperclass();
            }
            while (type != null);
            SystemLog.warning(e);
            throw new Exception(e);
        }
    }

    /**
     * Unwrap an object
     */
    private static Object unwrappper(Object object) {
        if (object instanceof ReflectionKit3) return ((ReflectionKit3) object).get();
        return object;
    }

    /**
     * Get a wrapper type for a primitive type, or the argument type itself, if
     * it is not a primitive type.
     */
    private static Class<?> wrapper(Class<?> type) {
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
     * Conveniently render an {@link AccessibleObject} accessible.
     * <p>
     * To prevent {@link SecurityException}, this is only done if the argument
     * object and its declaring class are non-public.
     *
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
     * Create a proxy for the wrapped object allowing to typesafely invoke
     * methods on it using a custom interface
     *
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
                }
                catch (Exception e) {
                    if (isMap) {
                        Map<String, Object> map = (Map<String, Object>) object;
                        int length = (args == null ? 0 : args.length);

                        if (length == 0 && name.startsWith("get")) {
                            return map.get(property(name.substring(3)));
                        }
                        else if (length == 0 && name.startsWith("is")) {
                            return map.get(property(name.substring(2)));
                        }
                        else if (length == 1 && name.startsWith("set")) {
                            map.put(property(name.substring(3)), args[0]);
                            return null;
                        }
                    }
                    SystemLog.warning(e);
                    throw e;
                }
            }
        };
        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[] { proxyType }, handler);
    }

    /**
     * Searches a method with the exact same signature as desired.
     * <p>
     * If a public method is found in the class hierarchy, this method is returned.
     * Otherwise a private method with the exact same signature is returned.
     * If no exact match could be found, we let the {@code NoSuchMethodException} pass through.
     */
    private Method exactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
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
     * Get a Map containing field names and wrapped values for the fields'
     * values.
     *
     * If the wrapped object is a {@link Class}, then this will return static
     * fields. If the wrapped object is any other {@link Object}, then this will
     * return instance fields.
     *
     * These two calls are equivalent
     * on(object).field("myField");
     * on(object).fields().get("myField");
     *
     *
     * @return A map containing field names and wrapped values.
     * @throws Exception throw if any error is occurrred.
     */
    public Map<String, ReflectionKit3> fields() throws Exception {
        Map<String, ReflectionKit3> result = new LinkedHashMap<>();
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
     *
     * @see Class#forName(String)
     */
    private static Class<?> forName(String name) throws Exception {
        try {
            return Class.forName(name);
        }
        catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Get the type of the wrapped object.
     * @return the Class of the Object.
     * @see Object#getClass()
     */
    public Class<?> type() {
        if (isClass) {
            return (Class<?>) ReflectionKit3.object;
        }
        else {
            return ReflectionKit3.object.getClass();
        }
    }

    /**
     * Get an array of types for an array of objects
     *
     * @see Object#getClass()
     */
    @SuppressWarnings("rawtypes")
    private static Class<?>[] types(Object... values) {
        if (values == null) {
            return new Class[0];
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }

        return result;
    }

    /**
     * Determines if a method has a "similar" signature, especially if wrapping
     * primitive argument types would result in an exactly matching signature.
     */
    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    private static class NULL {}

    /**
     * Check whether two arrays of types match, converting primitive types to
     * their corresponding wrappers.
     */
    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)
                    continue;
                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
                    continue;

                return false;
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the POJO property name of an getter/setter
     */
    private static String property(String string) {
        int length = string.length();

        if (length == 0) {
            return "";
        }
        else if (length == 1) {
            return string.toLowerCase();
        }
        else {
            return string.substring(0, 1).toLowerCase() + string.substring(1);
        }
    }

    /**
     * Searches a method with a similar signature as desired using
     * {@link #isSimilarSignature(Method, String, Class[])}.
     * <p>
     * First public methods are searched in the class hierarchy, then private
     * methods on the declaring class. If a method could be found, it is
     * returned, otherwise a {@code NoSuchMethodException} is thrown.
     */
    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();
        // first priority: find a public method with a "similar" signature in class hierarchy
        // similar interpreted in when primitive argument types are converted to their wrappers
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }
        // second priority: find a non-public method with a "similar" signature on declaring class
        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }
            type = type.getSuperclass();
        }
        while (type != null);
        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type() + ".");
    }

    // ---------------------------------------------------------------------
    // Other Methods
    // ---------------------------------------------------------------------



    public static Integer countGetterAndsetter(Class<?> aClass){
        return ReflectionFind.findGettersAndSetters(aClass).size()/2;
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




    public static URL getCodeSourceLocation(Class<?> aClass) {return aClass.getProtectionDomain().getCodeSource().getLocation(); }
    public static String getClassReference(Class<?> aClass){ return aClass.getName();}

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
            if (annColumn != null) {
                ReflectionKit3.updateAnnotationValue(annColumn, attributeName, attributeValue);
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
            ReflectionKit3.updateAnnotationValue(ann, attributeName, attributeValue);
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
            m = ReflectionFind.findSetter(obj, property, value);
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
            classes.addAll(ReflectionFind.findClasses(directory, packageName));
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

    //--------------------------------------------------------------------------
    // NEW METHODS 1
    //-------------------------------------------------------------------------

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
        Type t = thisClass.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        return (Class) pt.getActualTypeArguments()[0];
    }

    //-----------------------------------------------------------------------
    // ADDED
    //-----------------------------------------------------------------------


    /**
     * Determine whether the given field is a "public static final" constant.
     * @param field the field to check
     */
    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }

    /**
     * Determine whether the given method is an "equals" method.
     * @see java.lang.Object#equals(Object)
     */
    public static boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }

    /**
     * Determine whether the given method is a "hashCode" method.
     * @see java.lang.Object#hashCode()
     */
    public static boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is a "toString" method.
     * @see java.lang.Object#toString()
     */
    public static boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is originally declared by {@link java.lang.Object}.
     */
    public static boolean isObjectMethod(Method method) {
        if (method == null) {
            return false;
        }
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        }
        catch (Exception ex) {
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
     * @see org.springframework.cglib.proxy.Enhancer#rename
     */
    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i = name.length() - 1;
            while (i >= 0 && Character.isDigit(name.charAt(i))) {
                i--;
            }
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
     */
    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
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
     */
    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
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
                        }
                        else {
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
        }
        catch (IllegalAccessException ex) {
            ReflectionInvoke.handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
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
            ReflectionInvoke.handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }


    /**
     * Perform the given callback operation on all matching methods of the given
     * class, as locally declared or equivalent thereof (such as default methods
     * on Java 8 based interfaces that the given class implements).
     * @param clazz the class to introspect
     * @param mc the callback to invoke for each method
     * @since 4.2
     * @see #doWithMethods
     */
    public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            try {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
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
     * @see #doWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
        doWithMethods(clazz, mc, null);
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
    public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) {
                continue;
            }
            try {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        }
        else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     * @param clazz the target class to analyze
     * @param fc the callback to invoke for each field
     * @since 4.2
     * @see #doWithFields
     */
    public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
        for (Field field : getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            }
            catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     * @param clazz the target class to analyze
     * @param fc the callback to invoke for each field
     */
    public static void doWithFields(Class<?> clazz, FieldCallback fc) {
        doWithFields(clazz, fc, null);
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     * @param clazz the target class to analyze
     * @param fc the callback to invoke for each field
     * @param ff the filter that determines the fields to apply the callback to
     */
    public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff != null && !ff.matches(field)) {
                    continue;
                }
                try {
                    fc.doWith(field);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
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
     */
    public static void shallowCopyFieldState(final Object src, final Object dest) {
        if (src == null) {
            throw new IllegalArgumentException("Source for field copy cannot be null");
        }
        if (dest == null) {
            throw new IllegalArgumentException("Destination for field copy cannot be null");
        }
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() +
                    "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        doWithFields(src.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                makeAccessible(field);
                Object srcValue = field.get(src);
                field.set(dest, srcValue);
            }
        }, COPYABLE_FIELDS);
    }




    //------------------------------------------------
    //INTERFACE SUPPORT
    //-----------------------------------------------

    /**
     * Action to take on each method.
     */
    public interface MethodCallback {

        /**
         * Perform an operation using the given method.
         * @param method the method to operate on
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