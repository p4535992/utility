package com.github.p4535992.util.reflection.impl;

import com.github.p4535992.util.log.SystemLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;

/**
 * Created by 4535992 on 26/10/2015.
 */
public class ReflectionInvoke {

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
        if(param==null || param.length==0 )method = ReflectionKit3.getMethodByNameAndParam(MyObject, nameOfMethod, null);
        else method = ReflectionKit3.getMethodByNameAndParam(MyObject, nameOfMethod, param); //String.class
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
     * @throws IllegalAccessException throw any error if is occurred.
     * @throws InvocationTargetException throw any error if is occurred.
     * @throws NoSuchMethodException throw any error if is occurred.
     */
    public static Object invokeMethod(Object MyObject,Method method,Object[] param)
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
    public static <T> T invokeSetter(T MyObject,String methodName,Object value,Class<?> clazzValue)
    {
        try {
            Method method = ReflectionKit3.getMethodByNameAndParam(
                    MyObject.getClass(), methodName, new Class<?>[]{clazzValue});
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
            Method method = ReflectionKit3.getMethodByNameAndParam(
                    MyObject.getClass(), methodName, new Class<?>[0]);
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
     * OLD_NAME: invokeSetterMethodForObject.
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
    public static <T> T invokeSetter(T MyObject, Method method, Object value)
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
     * OLD_NAME: invokeSetterMethod.
     * @param MyObject object where invoke the getter method.
     * @param method the setter method.
     * @param value to set with the setter method.
     * @return the return value of the invoke on the setter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    /*public static Object invokeSetter(Object MyObject, Method method, Object value)
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
    }*/

    /**
     * Method to invoke a getter method from  a Object.
     * OLD_NAME: invokeGetterMethodForObject.
     * @param MyObject object where invoke the getter method.
     * @param method the getter method.
     * @param <T> generic type.
     * @return the return value of the invoke on the getter method.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws NoSuchMethodException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeGetter(T MyObject, Method method)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        T MyObject2;
        Class<T> aClazz = (Class<T>) MyObject.getClass();
        try{
            //if the method you try to invoke is static...
            MyObject2 = aClazz.cast(method.invoke(null));
        }catch(NullPointerException ne) {
            //...The method is not static
            MyObject2 =  aClazz.cast(method.invoke(MyObject));
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
    /*public static Object invokeGetter(Object MyObject,Method method)
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
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws InstantiationException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws IllegalArgumentException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeConstructor(T MyObject, Class<?>[] param, Object[] defaultValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
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

    //--------------------------------------------------------------
    // ADDED
    //--------------------------------------------------------------

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
        }
        catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
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
     * @throws SQLException the JDBC API SQLException to rethrow (if any)
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeJdbcMethod(Method method, Object target, Object... args) throws SQLException {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        }
        catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SQLException) {
                throw (SQLException) ex.getTargetException();
            }
            handleInvocationTargetException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Handle the given reflection exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause. Throws an
     * IllegalStateException with an appropriate message else.
     * @param ex the reflection exception to handle
     */
    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Handle the given invocation target exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of such a root
     * cause. Throws an IllegalStateException else.
     * @param ex the invocation target exception to handle
     */
    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the
     * target method.
     * <p>Rethrows the underlying exception cast to an {@link RuntimeException} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * @param ex the exception to rethrow
     * @throws RuntimeException the rethrown exception
     */
    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the
     * target method.
     * <p>Rethrows the underlying exception cast to an {@link Exception} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * @param ex the exception to rethrow
     * @throws Exception the rethrown exception (in case of a checked exception)
     */
    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Determine whether the given method explicitly declares the given
     * exception or one of its superclasses, which means that an exception of
     * that type can be propagated as-is within a reflective invocation.
     * @param method the declaring method
     * @param exceptionType the exception to throw
     * @return {@code true} if the exception can be thrown as-is;
     * {@code false} if it needs to be wrapped
     */
    public static boolean declaresException(Method method, Class<?> exceptionType) {
        if(method==null) SystemLog.error("Method must not be null");
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }
}
