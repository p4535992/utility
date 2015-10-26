package com.github.p4535992.util.file;

import com.github.p4535992.util.file.impl.FileUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by 4535992 on 08/06/2015.
 * Exposing the spring properties bean in java
 * To allow our Java classes to access the properties from the same object as spring, we’ll need to
 * extend the PropertyPlaceholderConfigurer so that we can provide a more convenient method for
 * retrieving the properties (there is no direct method of retrieving properties!).
 * We can extend the spring provided class to allow us to reuse spring’s property resolver in our Java classes:
 * href: http://www.java2s.com/Tutorial/Java/0140__Collections/Propertyaccessutilitymethods.htm
 * href: http://www.java2s.com/Tutorial/Java/0140__Collections/UseXMLwithProperties.htm
 */
@SuppressWarnings("unused")
public class PropertiesKit extends PropertyPlaceholderConfigurer {

    private static Map<String,String> propertiesMap;
    private static final HashMap<Class<?>, HashMap<String,PropertyDescriptor>> descriptorCache = new HashMap<>();

    /**
     *
     * @param beanFactory the org.springframework.beans.factory.config.ConfigurableListableBeanFactory
     * @param props the properties file.
     * @throws BeansException throw if any error is occurred.
     */
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        propertiesMap = new HashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            propertiesMap.put(keyStr, resolvePlaceholder(props.getProperty(keyStr), props));
        }
    }

    /**
     * Method to return the properties map already setted.
     * @param name the string name of the specific properties from name id.
     * @return the specific value string of the properties.
     */
    public static String getValueProperty(String name) {
        if(propertiesMap!=null && !propertiesMap.isEmpty()) return propertiesMap.get(name);
        else return null;
    }

    /**
     * Method to return the properties map already setted.
     * @param prop the Properties name file e.g. "test.properties".
     * @param nameProperty the string name of the specific properties from name id.
     * @return the specific value string of the properties.
     */
    public static String getValueProperty(Properties prop,String nameProperty) {
        return prop.getProperty(nameProperty);
    }

    /**
     * Method to convert a File int the resource package to a Properties object
     * @param fileResourceProperties the String name of the file in the resource package.
     * @return the Properties object with all the content of the file .
     * @throws IOException throw if any file not exists.
     */
    public Properties convertFileResourceToProperties(String fileResourceProperties) throws IOException {
         return convertFileResourceToProperties(new File(fileResourceProperties));
    }

    /**
     * Method to convert a File int the resource package to a Properties object
     * @param fileResourceProperties the String name of the file in the resource package.
     * @return the Properties object with all the content of the file .
     * @throws IOException throw if any file not exists.
     */
    public Properties convertFileResourceToProperties(File fileResourceProperties) throws IOException {
        InputStream inputStream = null;
        Properties prop = new Properties();
        try {
            String nameFile = FileUtil.filename(fileResourceProperties);
            inputStream = getClass().getClassLoader().getResourceAsStream(nameFile);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + nameFile + "' not found in the classpath");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return prop;
    }

    /**
     * Get map with property descriptors for the specified bean class
     * @param beanClazz the Class of the Bean.
     * @return the Iterator of Property.
     */
    @SuppressWarnings("unchecked")
    private static Map<String,PropertyDescriptor> getPropertyDescriptors(Class<?> beanClazz) {
        HashMap<String,PropertyDescriptor> map = descriptorCache.get(beanClazz);
        if (map == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(beanClazz);
            } catch (IntrospectionException e) {
                return Collections.EMPTY_MAP;
            }
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            if (descriptors == null)
                descriptors = new PropertyDescriptor[0];
            map = new HashMap<>(descriptors.length);
            for (PropertyDescriptor descriptor : descriptors) map.put(descriptor.getName(), descriptor);
            descriptorCache.put(beanClazz, map);
        }
        return map;
    }

    /**
     * Get property names of the specified bean class
     * @param beanClazz the Class of the Bean.
     * @return the Iterator of Property.
     */
    public static Iterator<String> getPropertyNames(Class<?> beanClazz) {
        return getPropertyDescriptors(beanClazz).keySet().iterator();
    }

    /**
     * Get specified property descriptor
     * @param beanClazz the Class of the Bean.
     * @param property the String name of the Property.
     * @return the PropertyDescriptor of the Property.
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClazz, String property) {
        return getPropertyDescriptors(beanClazz).get(property);
    }

    /**
     * Get specified property value
     * @param bean the Bean Object.
     * @param property the String name of the Property.
     * @return the Property of the Bean.
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    public static Object getProperty(Object bean, String property)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), property);
        if (descriptor == null)
            throw new NoSuchMethodException("Cannot find property " + bean.getClass().getName() + "." + property);
        Method method = descriptor.getReadMethod();
        if (method == null)
            throw new NoSuchMethodException("Cannot find getter for " + bean.getClass().getName() + "." + property);
        //return method.invoke(bean, null);
        return method.invoke(bean, new Object[]{null});
    }

    /**
     * Get specified nested property value
     * @param bean the Bean Object.
     * @param property the String name of the Property.
     * @return the Property of the Bean.
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    public static Object getNestedProperty(Object bean, String property)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (property.indexOf('.') > 0) {
            String[] path = property.split("\\.");
            for (int i = 0; i < path.length && bean != null; i++) {
                bean = getProperty(bean, path[i]);
            }
            return bean;
        } else {
            return getProperty(bean, property);
        }
    }

    /**
     * Set specified property value
     * @param bean the Bean Object.
     * @param property the String name of the Property.
     * @param value the new Object Value of the property.
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    public static void setProperty(Object bean, String property, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), property);
        if (descriptor == null)
            throw new NoSuchMethodException("Cannot find property " + bean.getClass().getName() + "." + property);
        Method method = descriptor.getWriteMethod();
        if (method == null)
            throw new NoSuchMethodException("Cannot find setter for " + bean.getClass().getName() + "." + property);
        //method.invoke(bean, new Object[]{ value });
        method.invoke(bean, value);
    }

    /**
     * Set nested property given by property path, starting at specified
     * index. Dynamically create beans if necessary. Take care not to
     * leave the bean changed if an exception occurs.
     * @param bean the Bean Object.
     * @param path String path package to the Class of the Bean.
     * @param value the new Object Value of the property.
     * @return the Property of the Bean.
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     */
    private static void setNestedPropertyWithCreate(Object bean, String[] path, int start, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (int i = start; i < path.length - 1; i++) {
            Object object = getProperty(bean, path[i]);
            if (object == null) {
                PropertyDescriptor descr =
                        getPropertyDescriptor(bean.getClass(), path[i]);
                object = descr.getPropertyType().newInstance();
                setNestedPropertyWithCreate(object, path, i + 1, value);
                setProperty(bean, path[i], object);
                return;
            }
            bean = object;
        }
        setProperty(bean, path[path.length - 1], value);
    }

    /**
     * Set specified nested property value
     * @param bean the Bean Object.
     * @param property the String name of the Property.
     * @param value the new Object Value of the property.
     * @throws NoSuchMethodException throw if any error is occurred.
     * @throws IllegalAccessException throw if any error is occurred.
     * @throws InvocationTargetException throw if any error is occurred.
     * @throws java.lang.InstantiationException throw if any error is occurred.
     */
    public static void setNestedProperty(Object bean, String property, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int lastDot = property.lastIndexOf('.');
        if (lastDot > 0) {
            setNestedPropertyWithCreate(bean, property.split("\\."), 0, value);
        } else {
            setProperty(bean, property, value);
        }
    }

    /**
     * Method to store a Properties object like a XML File.
     * @param pathInputResourceFile the String path to the resource file.
     * @throws IOException throw if any error is occurred.
     */
    public static void savePropertiesAsXML(String pathInputResourceFile) throws IOException {
        savePropertiesAsXML(null, pathInputResourceFile);
    }

    /**
     * Method to store a Properties object like a XML File.
     * @param p the  Properties object to covert toa XML.
     * @param pathOutputResourceFile the String path to the resource file.
     * @throws IOException throw if any error is occurred.
     */
    public static void savePropertiesAsXML(Properties p,String pathOutputResourceFile)
            throws IOException {
        if(p==null) p = new Properties();
        FileOutputStream out = new FileOutputStream(pathOutputResourceFile);
        p.storeToXML(out, "updated");
    }

    /**
     * Method to read a XML Properties File.
     * @param pathResourceFile the String Path tot the resource File.
     * @return the Properties Object.
     * @throws IOException throw if any error is occurred.
     */
    public static Properties readPropertiesAsXML(String pathResourceFile) throws IOException {
        Properties p = new Properties();
        FileInputStream in = new FileInputStream(pathResourceFile);
        p.loadFromXML(in);
        return p;
    }


}
