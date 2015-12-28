package com.github.p4535992.util.collection;

import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;
import org.apache.commons.lang.enums.Enum;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by 4535992 on 29/06/2015.
 * @author 4535992.
 * @version 2015-09-14.
 * NOTE: i must say some method is very similar to the apache common utility.
 */
@SuppressWarnings("unused")
public class CollectionUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(CollectionUtilities.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    /**
     * Method to check if a Class is a Collection or not.
     * @param c the Class to inspect.
     * @return if true the class extend or implememnt Collection.
     */
    public static boolean isClassCollection(Class<?> c) {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c);
    }

    /**
     * Method to check if a Object is a Collection or not.
     * @param ob the Object to inspect.
     * @return if true the class extend or implememnt Collection.
     */
    public static boolean isCollection(Object ob) {
        return ob instanceof Collection || ob instanceof Map;
        //return ob != null && isClassCollection(ob.getClass());
    }


    /**
     * Method to create a List collection with a single element.
     * @param object the single element.
     * @param <T> generic type.
     * @return list with a single element.
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static <T> Collection<T> createListWithSingleElement(T object){
        if(object instanceof List)return ListUtilities.createSingleton(object);
        else if(object instanceof Array) return Arrays.asList(ArrayUtilities.createSingleton(object,1));
        else if(object instanceof Set) return SetUtilities.createSingleton(object);
        logger.warn("Not a valid input collection for this method.");
        return null;
    }

    /**
     * Method to convert a Collection to a List collection.
     * @param <T> generic type.
     * @param collection the Collection object.
     * @return the List object.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Object collection){
        if(isCollection(collection)) {
            if(collection instanceof Enumeration) return Collections.list((Enumeration<T>) collection);
            if(collection instanceof TreeSet) return new ArrayList<>((TreeSet<T>)collection);
            if(collection instanceof Set) return SetUtilities.toList((Set<T>) collection);
            if(collection instanceof Iterable){
                if(collection instanceof List) return (List<T>) collection;
                else {
                    List<T> list = new ArrayList<>();
                    for (T e : (Iterable<T>)collection) {list.add(e);}
                    return list;
                }
            }
            if(collection instanceof Object[])return ArrayUtilities.toList((T[])collection);
            if(collection instanceof Iterator){
                List<T> list = new ArrayList<>();
                Iterator<T> iterator = (Iterator<T>) collection;
                while (iterator.hasNext()) { list.add(iterator.next());}
                return list;
            }
            else return new ArrayList<>();
        }else{
            logger.error("The object:"+collection+ " is not a Collection");
            return new ArrayList<>();
        }
    }

    /**
     * Method to convert a Enumeration collection to a Set collection.
     * @param <T> generic type.
     * @param collection the Collection object.
     * @return the Set object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> toSet(Object collection){
        if(isCollection(collection)) {
            if(collection instanceof Enumeration) return EnumerationUtilities.toSet((Enumeration<T>) collection);
            else if(collection instanceof List) return ListUtilities.toSet((List<T>) collection);
            else if(collection instanceof Array) return ArrayUtilities.toSet((T[]) collection);
            else return new HashSet<>();
        }else{
            logger.error("The object:"+collection+ " is not a Collection");
            return new HashSet<>();
        }
    }

    /**
     * Method to convert a Collection to a Enumeration collection.
     * @param <T> generic type.
     * @param collection the Collection object.
     * @return the Enumeration object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Enumeration<T> toEnumeration(Object collection){
        if(isCollection(collection)) {
            if (collection instanceof Set) return SetUtilities.toEnumeration((Set<T>) collection);
            else return EnumerationUtilities.create();
        }else{
            logger.error("The object:"+collection+ " is not a Collection");
            return EnumerationUtilities.create();
        }
    }

    /**
     * Method to convert a List Collection to Iterable object.
     * @param <T> generic type.
     * @param collection the Collection.
     * @return the Iterable object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> toIterable(final Object collection) {
        if(isCollection(collection)) {
            if(collection instanceof List)return toSet((List<T>)collection);
            if(collection instanceof Set)return toList(((Set<T>) collection));
            if(collection instanceof Object[])return  Arrays.asList((T[]) collection);
            if(collection instanceof Iterator){
                return new Iterable<T>() {
                    @Override
                    public Iterator<T> iterator() {return (Iterator<T>) collection;}
                };
            }
            else{
                return new Iterable<T>() {
                    @Override
                    public Iterator<T> iterator() {return null;}
                };
            }
        }else{
            logger.error("The object:"+collection+ " is not a Collection");
            return new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {return null;}
            };
        }
    }

    /**
     * Method to convert a Collection to Iterator object.
     * @param <T> generic type.
     * @param collection the Collection.
     * @return the Iterator object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> toIterator(Object collection){
        if(isCollection(collection)) {
            if(collection instanceof List) return ((List<T>)collection).iterator();
            if(collection instanceof Set)   return ((Set<T>)collection).iterator();
            else{
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() { return false;}
                    @Override
                    public T next() {return null;}
                    @Override
                    public void remove() { }
                };
            }
        }else{
            logger.error("The object:"+collection+ " is not a Collection");
            return new Iterator<T>() {
                @Override
                public boolean hasNext() { return false;}
                @Override
                public T next() {return null;}
                @Override
                public void remove() { }
            };
        }
    }

    /**
     * Method to convert a Set Collection to a Array Collection
     * @param collection the Collection.
     * @param <T> generic type.
     * @return the Array Collection.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection) {
        if(isCollection(collection)) {
            if(collection instanceof List) return ListUtilities.toArray((List<T>) collection);
            else if(collection instanceof TreeSet) return TreeSetUtilities.toArray((TreeSet<T>) collection);
            else if(collection instanceof Set) return SetUtilities.toArray((Set<T>) collection);
            else return (T[]) Array.newInstance(Object.class, 0);
        }else{
            return  ArrayUtilities.createAndPopulate(collection);
        }
    }


    /**
     * Method to convert a aarray to a Object array.
     * @param primitiveArray the primitive array to convert.
     * @param <T> generic type.
     * @return a Object array.
     */
    public static <T> Object[] toObjectArray(T[] primitiveArray){
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(Arrays.asList(primitiveArray));
        return toArray(objectList);
    }

    public static <T> Object[] toObjectArray(List<T> primitiveList){
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(primitiveList);
        return toArray(objectList);
    }

    /**
     * Method to convert a list to a array object.
     * @param list List Collection..
     * @param <T> generic variable.
     * @return Array Collection .
     */
    /*@SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list){
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        if(ReflectionKit.isWrapperType(list.get(0).getClass())){ //if is a primitve class
            for(int i = 0; i < list.size(); i++) array[i] = list.get(i);
        }else{ //is is not a primitive class
            list.toArray(array);
        }
        return array;
    }*/

    /**
     * Method to convert a List Colection To array Collection.
     * @param list list collection.
     * @param clazz the Clazz of the elements of the Arrays.
     * @param <T> generic variable.
     * @return Array Collection.
     */
   /* @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list,Class<T> clazz){
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        if(ReflectionKit.isWrapperType(list.get(0).getClass())){ //if is a primitve class
            for(int i = 0; i < list.size(); i++) array[i] = clazz.cast(list.get(i));
        }else{ //is is not a primitive class
            list.toArray(array);
        }
        return array;
    }*/

    /**
     * Method to convert a Array Collection of Integer to a Array Collection of int.
     * @param integerArray the Array Collection of Integers.
     * @return Array Collection of int.
     */
    public static int[] toPrimitive(Integer[] integerArray) {
        //return org.apache.commons.lang3.ArrayUtils.toPrimitive(integerArray);
        if (integerArray == null) return null;
        else if (integerArray.length == 0) return new int[0];
        final int[] result = new int[integerArray.length];
        for (int i = 0; i < integerArray.length; i++) { result[i] = integerArray[i];}
        return result;
    }

    /**
     * Method to convert a Array Collection of int to a Array Collection of Integer.
     * @param <T> the generic variable.
     * @param objArray the Array Collection of int.
     * @return Array Collection of Integer.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toObject(Object... objArray) {
        //return org.apache.commons.lang3.ArrayUtils.toObject(intArray);
        if (objArray == null)return null;
        else if(objArray[0].getClass() == int.class) {
            if (objArray.length == 0) return (T[]) new Integer[0];
            final Integer[] result = new Integer[objArray.length];
            for (int i = 0; i < objArray.length; i++) {
                result[i] = (Integer) objArray[i];
            }
            return (T[]) result;
        }
        else return null;
    }






}
