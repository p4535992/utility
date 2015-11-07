package com.github.p4535992.util.reflection.impl;

import com.github.p4535992.util.file.impl.FileUtilities;
import com.github.p4535992.util.log.SystemLog;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by 4535992 on 26/10/2015.
 */
public class ReflectionFind {

    /**
     * Method to get all getter and Setter.
     * @param aClass the class to inspect.
     * @return the List of methods.
     */
    public static List<Method> findGettersAndSetters(Class<?> aClass) {
        List<Method> list = new ArrayList<>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (ReflectionKit3.isGetter(method) || ReflectionKit3.isSetter(method))
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
    public static List<Method> findSetters(Class<?> clazz,boolean ordered){
        List<Method> list = new ArrayList<>();
        if(ordered) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Method method = findSetter(clazz, field.getName(), field.getType());
                list.add(method);
            }
        }else{
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (ReflectionKit3.isSetter(method))list.add(method);
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
    public static List<Method> findGetters(Class<?> clazz,boolean ordered){
        List<Method> list = new ArrayList<>();
        if(ordered) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Method method = findGetter(clazz, field.getName(), field.getType());
                list.add(method);
            }
        }else{
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (ReflectionKit3.isGetter(method)) list.add(method);
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
    public static Set<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        List<File> files = FileUtilities.readDirectory(directory);
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
                        SystemLog.exception(ex1);
                    }
                }
                clazzField = clazzField.getSuperclass();
            }
        }
        return null;
    }

    //-----------------------------------------------------------
    // ADDED
    //-----------------------------------------------------------

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
        if(clazz==null) SystemLog.error("Class must not be null");
        if(name == null && type == null)  SystemLog.error("Either name or type of the field must be specified");
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = ReflectionKit3.getDeclaredFields(searchType);
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) &&
                        (type == null || type.equals(field.getType()))) {
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
        if(clazz==null) SystemLog.error("Class must not be null");
        if(name==null) SystemLog.error("Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : ReflectionKit3.getDeclaredMethods(searchType));
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

    public static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new LinkedList<Method>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }



}
