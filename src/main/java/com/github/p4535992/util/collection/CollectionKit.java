package com.github.p4535992.util.collection;

import com.github.p4535992.util.reflection.ReflectionKit;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by 4535992 on 29/06/2015.
 * @author 4535992.
 * @version 2015-06-29.
 */
@SuppressWarnings("unused")
public class CollectionKit {

    /**
     * Method to create a List collection with a single element.
     * @param object the single element.
     * @param <T> generic type.
     * @return list with a single element.
     */
    @SuppressWarnings("rawtypes")
    public static <T> List createListWithSingleElement(T object){
        return Collections.singletonList(object);
    }

    /**
     * Method to create a Set collection with a single element.
     * @param object the single element.
     * @param <T> generic type.
     * @return set with a single element.
     */
    @SuppressWarnings("rawtypes")
    public static <T> Set createSetWithSingleElement(T object){
        return Collections.singleton(object);
    }

    /**
     * Method for chek if a specific key on the map is absent or not existsa value for that key.
     * @param <K> generic key.
     * @param <V> generic value.
     * @param map the map to inspect.
     * @param key the key valur of the map to search.
     * @return if tru exists a value not empty for that key.
     */
    public static <K,V> boolean isMapValueNullOrInexistent(Map<K,V> map,K key){
        V value = map.get(key);
        if (value != null) {
            return false;
        } else {
            // Key might be present...
            if (map.containsKey(key)) {
                // Okay, there's a key but the value is null
                return true;
            }
            // Definitely no such key
            return true;

        }
    }

    /**
     * Method to convert a Enumeration collection to a List collection.
     * @param <T> generic type.
     * @param enumeration enumeration object.
     * @return the list object.
     */
    public static <T> List<T> convertEnumerationToList(Enumeration<T> enumeration){
        return Collections.list(enumeration);
    }

    /**
     * Method to convert a Enumeration collection to a Set collection.
     * @param <T> generic type.
     * @param enumeration enumeration object.
     * @return the Set object.
     */
    public static <T> Set<T> convertEnumerationToSet(Enumeration<T> enumeration){
        return new HashSet<>(Collections.list(enumeration));
    }

    /**
     * Method to convert a Set collection to a Enumeration collection.
     * @param <T> generic type.
     * @param set the set object.
     * @return the Enumeration object.
     */
    public static <T> Enumeration<T> convertSetToEnumaretion(Set<T> set){
        return Collections.enumeration(set);
    }

    /**
     * Method to convert a Set collection to a Enumeration collection.
     * @param <T> generic type.
     * @param set the set object.
     * @return the List object.
     */
    public static <T> List<T> convertSetToList(Set<T> set){
        return new ArrayList<>(set);
    }

    /**
     * Method to convert a List collection to a Setcollection.
     * @param <T> generic type.
     * @param list the List collection.
     * @return the Set collection.
     */
    public static <T> Set<T> convertListToSet(List<T> list){
        return new HashSet<>(list);
    }

    /**
     * Method to convert a Iterable object to a List Collection.
     * @param <T> generic type.
     * @param iterable theItreable object.
     * @return the List collection.
     */
    public static <T> List<T> convertIterableToList(Iterable<T> iterable){
        if(iterable instanceof List) {
            return (List<T>) iterable;
        }
        List<T> list = new ArrayList<>();
        if(iterable != null) {
            for(T e: iterable) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Method to convert a List Collection to Iterable object.
     * @param <T> generic type.
     * @param list the List collection.
     * @return the Iterable object.
     */
    public static <T> Iterable<T> convertListToIterable(List<T> list){
        return convertSetToIterable(convertListToSet(list));
    }

    /**
     * Method to convert a Array Collection to Iterable object.
     * @param <T> generic type.
     * @param array the Array collection.
     * @return the Iterable object.
     */
    @SuppressWarnings("rawtypes")
    public static <T> Iterable convertArrayToIterable(T[] array){
        return Arrays.asList(array);
    }

    /**
     * Method to convert a Array Collection to a List Collection Immutable.
     * @param array the Array Collection.
     * @param <T> generic type.
     * @return the List Collection.
     */
    public static <T> List<T> convertArrayToListImmutable(T[] array){
        return Arrays.asList(array);
    }

    /**
     * Method to convert a Array Collection to a List Collection Mutable.
     * @param array the Array Collection.
     * @param <T> generic type.
     * @return the List Collection.
     */
    public static <T> List<T> convertArrayToListMutable(T[] array){
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * Method to convert a Set Collection to Iterable object.
     * @param <T> generic type.
     * @param set the set collection.
     * @return the Iterable object.
     */
    public static <T> Iterable<T> convertSetToIterable(Set<T> set){
        return convertListToIterable(convertSetToList(set));
    }
    /**
     * Method to convert a Iterator Collection to Iterable object.
     * @param <T> generic type.
     * @param iterator the Iterator collection.
     * @return the Iterable object.
     */
    public static <T> Iterable<T> convertIteratorToIterable(final Iterator<T> iterator){
        if (iterator == null) {
            throw new NullPointerException();
        }
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

    /**
     * Method to convert a Iterator Collection to List collection.
     * @param <T> generic type.
     * @param iterator the Iterator collection.
     * @return the List collection.
     */
    public static <T> List<T> convertIteratorToList(Iterator<T> iterator){
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    /**
     * Method to convert a List Collection to Iterator object.
     * @param <T> generic type.
     * @param list the List collection.
     * @return the Iterator object.
     */
    public static <T> Iterator<T> convertListToIterator(List<T> list){
        return list.iterator();
    }

    /**
     * Method to convert a Set Collection to Iterator object.
     * @param <T> generic type.
     * @param set the Set collection.
     * @return the Iterator object.
     */
    public static <T> Iterator<T> convertSetToIterator(Set<T> set){
        return set.iterator();
    }

    /**
     * Method to convert a Set Collection to a Array Collection
     * @param set the Set collection.
     * @param <T> generic type.
     * @return the Array Collection.
     */
    public static <T> T[] convertSetToArray(Set<T> set){
        return convertListToArray(convertSetToList(set));
    }


    /**
     * Method for concatenate the content of two arrays in a single array.
     * @param a first array.
     * @param b second array.
     * @param <T> generic variable.
     * @return array merged.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] concatenateArrays(T[] a,T[] b) {
        int aLen = a.length;
        int bLen = b.length;
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


    /*
     * Method to concatenate the content of n arrays toa single array.
     * @param first first array to merge.
     * @param rest other arrays to merge.
     * @param <T> generic variable.
     * @return merged array.

    public static <T> T[] concatenateArrays(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
    */


    /**
     * Method for copy the content of a array to aniother array of the same type.
     * @param baseArray array.
     * @param <T> generic type.
     * @return array copied.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copyContentArray(T[] baseArray){
        T[] newArray = (T[]) Array.newInstance(baseArray.getClass().getComponentType(), baseArray.length);
        //b = Arrays.copyOf(baseArray, baseArray.length);
        //b = new T[baseArray.length];
        System.arraycopy(baseArray, 0, newArray, 0, newArray.length);
        //b = baseArray.clone();
        return newArray;
    }

    /**
     * Method to copy apart of the array.
     * @param oldArray the Array Colecction you wan tot copy.
     * @param startIndex the start index.
     * @param endIndex the end index.
     * @param <T> generic type.
     * @return the subArray.
     */
    public static <T> T[] copyContentArray(T[] oldArray,int startIndex,int endIndex){
        return Arrays.copyOfRange(oldArray, startIndex, endIndex);
    }

    /**
     * Method to check is a array is empty or with all value null or empty.
     * @param array array.
     * @param <T> generic type.
     * @return boolean value.
     */
    public static <T> boolean isArrayEmpty(T[] array){
        boolean empty = true;
        if(array!=null && array.length > 0) {
            for (T anArray : array) {
                if (anArray != null) {
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }

    /**
     * Merge the content of two arrays of string with same size for
     * make the args for a main method java class with option e home.
     * @param param array of parameter.
     * @param value array of values.
     * @param <T> generic type.
     * @return merged array.
     * @throws Exception error.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] mergeArraysForCommandInput(T[] param, T[] value) throws Exception {
        T[] array;
        int j = 0;
        if(param.length==value.length) {
            //array = new T[param.length+value.length];
            array = (T[]) Array.newInstance(param[0].getClass(),param.length+value.length);
            for (int i = 0; i < param.length; i++) {
                if (i == 0)
                    j = j + i;
                else
                    j = j + 1;

                array[j] = param[i];
                j = j + 1;
                array[j] = value[i];
            }
        }else{
            //logger.org.p4535992.mvc.error("WARNING: Check your array size");
            throw new Exception("WARNING: Check your array size");
        }
        return array;
    }

    /**
     * Method to convert a list to a array object.
     * @param list List Collection..
     * @param <T> generic variable.
     * @return Array Collection .
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] convertListToArray(List<T> list){
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        if(ReflectionKit.isWrapperType(list.get(0).getClass())){ //if is a primitve class
            for(int i = 0; i < list.size(); i++) array[i] = list.get(i);
        }else{ //is is not a primitive class
            list.toArray(array);
        }
        return array;
    }

    /**
     * Method to convert a List Colection To array Collection.
     * @param list list collection.
     * @param clazz the Clazz of the elements of the Arrays.
     * @param <T> generic variable.
     * @return Array Collection.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] convertListToArray(List<T> list,Class<T> clazz){
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        if(ReflectionKit.isWrapperType(list.get(0).getClass())){ //if is a primitve class
            for(int i = 0; i < list.size(); i++) array[i] = clazz.cast(list.get(i));
        }else{ //is is not a primitive class
            list.toArray(array);
        }
        return array;
    }

    /**
     * Method to convert a Array Collection of Integer to a Array Collection of int.
     * @param IntegerArray the Array Collection of Integers.
     * @return Array Collection of int.
     */
    public static int[] convertIntegersToInt(Integer[] IntegerArray) {
       /* int[] result = new int[IntegerArray.length];
        for (int i = 0; i < IntegerArray.length; i++) {
            result[i] = IntegerArray[i].intValue();
        }
        return result;*/
        return ArrayUtils.toPrimitive(IntegerArray);
    }

    /**
     * Method to convert a Array Collection of int to a Array Collection of Integer.
     * @param intArray the Array Collection of int.
     * @return Array Collection of Integer.
     */
    public static Integer[] convertIntToIntegers(int[] intArray) {
        /*Integer[] result = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = Integer.valueOf(intArray[i]);
        }
        return result;*/
        return ArrayUtils.toObject(intArray);
    }


    // chops a list into non-view sublists of length L

    /**
     * Method to Split List in n°. SubLIst of length L.
     * @param list the List Collection To Split.
     * @param L the Size of all the SubList.
     * @param <T> generic type.
     * @return A List Collection of Lists with the same size
     */
    public static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(list.subList(i, Math.min(N, i + L))));
        }
        return parts;
    }










}
