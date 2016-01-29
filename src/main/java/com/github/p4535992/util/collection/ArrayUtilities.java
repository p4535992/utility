package com.github.p4535992.util.collection;

import com.github.p4535992.util.string.StringUtilities;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by 4535992 on 21/12/2015.
 * href: https://github.com/azeckoski/reflectutils/blob/master/src/main/java/org/azeckoski/reflectutils/ArrayUtils.java
 * @author 4535992.
 */
public class ArrayUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ArrayUtilities.class);

    /**
     * Resize an array and return the new one,
     * this will truncate an array or expand it depending on the size given
     *
     * @param <T> generic variable.
     * @param array any array
     * @param newSize the new size for the array
     * @return the resized array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] resize(T[] array, int newSize) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, newSize);
        int toCopy = Math.min(array.length, newArray.length);
        System.arraycopy( array, 0, newArray, 0, toCopy );
        return newArray;
    }

    /**
     * @param <T> generic variable.
     * @param type any class type (including primitives)
     * @param newSize the initial size for the new array
     * @return the new array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] create(Class<T> type, int newSize) {
        return (T[]) Array.newInstance(type, newSize);
    }

    /**
     * Method to create a Array collection with a single element.
     * @param object the single element.
     * @param size the int size for the Array.
     * @param <T> generic type
     * @return array with a single element.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createSingleton(T object,int size){
        T[] newArray = (T[]) Array.newInstance(object.getClass(), size);
        Arrays.fill(newArray, object);
        return newArray;
    }

    /**
     * Method to create a Array collection with a single element.
     * @param object the Collection of elements.
     * @param <T> generic type
     * @return Array populate with the Collection.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createAndPopulate(Collection<T> object){
        T[] newArray = (T[]) Array.newInstance(object.getClass(), object.size());
        int i =0;
        for(T t : object) {
            Arrays.fill(newArray, i, i, t);
        }
        return newArray;
    }

    /**
     * Method to convert a null array to a empty array.
     * @param array the Array to analyze.
     * @return the new Array.
     */
    public static Object[] createEmptyArrayIfNull(final Object[] array) {
        if (array == null || array.length == 0) return new Object[0];
        return array;
    }

    /**
     * @param <T> generic variable.
     * @param array any array
     * @return the size of the array
     */
    public static <T> int size(T[] array) {
        return Array.getLength(array);
    }

    /**
     * @param <T> generic variable.
     * @param array any array
     * @return the component type of the items in the array
     */
    public static <T> Class<?> type(T[] array) {
        return array.getClass().getComponentType();
    }

    /**
     * Make a copy of an array
     *
     * @param <T> generic variable.
     * @param array an array of objects
     * @return a copy of the array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] array) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length);
        System.arraycopy( array, 0, newArray, 0, array.length );
        return newArray;
    }

    /**
     * Make a template copy of an array (empty copy which is the same size and type)
     * @param <T> generic variable.
     * @param array any array
     * @return the template copy (empty but same size as input)
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] template(T[] array) {
        Class<?> type = array.getClass().getComponentType();
        return (T[]) Array.newInstance(type, array.length);
    }

    /**
     * Checks to see if an array contains a value,
     * will return false if a null value is supplied
     *
     * @param <T> generic variable.
     * @param array any array of objects
     * @param value the value to check for
     * @return true if the value is found, false otherwise
     */
    public static <T> boolean contains(T[] array, T value) {
       /* boolean foundValue = false;
        if (value != null) {
            for (T anArray : array) {
                if (value.equals(anArray)) {
                    foundValue = true;
                    break;
                }
            }
        }
        return foundValue;*/
        return indexOf(array, value) != -1;
    }

    /**
     * Append an item to the end of an array and return the new array
     *
     * @param <T> generic variable.
     * @param array an array of items
     * @param value the item to append to the end of the new array
     * @return a new array with value in the last spot
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] appendArray(T[] array, T value) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy( array, 0, newArray, 0, array.length );
        newArray[newArray.length-1] = value;
        return newArray;
    }

    /**
     * Append an array to another array
     *
     * @param <T> generic variable.
     * @param array1 an array of items
     * @param array2 an array of items
     * @return a new array with array1 first and array2 appended on the end
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] appendArrays(T[] array1, T[] array2) {
        Class<?> type = array1.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array1.length + array2.length);
        System.arraycopy( array1, 0, newArray, 0, array1.length );
        System.arraycopy( array2, 0, newArray, array1.length, array2.length );
        return newArray;
    }

    /**
     * Prepend an item to the front of an array and return the new array
     *
     * @param <T> generic variable.
     * @param array an array of items
     * @param value the item to prepend to the front of the new array
     * @return a new array with value in the first spot
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] prependArray(T[] array, T value) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy( array, 0, newArray, 1, array.length );
        newArray[0] = value;
        return newArray;
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
        try {
            System.arraycopy(a, 0, c, 0, aLen);
            System.arraycopy(b, 0, c, aLen, bLen);
        }catch(java.lang.ArrayStoreException e){
            List<T> list = new ArrayList<>();
            Collections.addAll(list, a);
            //for(int i=0; i < a.length; i++){list.add(a[i]);}
            Collections.addAll(list, b);
            c = ListUtilities.toArray(list);
        }
        return c;
    }

    /**
     * Merge the content of two arrays of string with same size for
     * make the args for a main method java class with option e home.
     * @param param array of parameter.
     * @param value array of values.
     * @param <T> generic type.
     * @param <E> generic type.
     * @return merged array.
     */
    @SuppressWarnings("unchecked")
    public static <T,E> Object[] concatenateDifferentArraysForInput(T[] param, E[] value) {
        Object[] array = (Object[]) Array.newInstance(value[0].getClass(),param.length+value.length);
        int j = 0;
        if(param.length==value.length) {
            //array = new T[param.length+value.length];
            //array = (T[]) Array.newInstance(param[0].getClass(),param.length+value.length);
            for (int i = 0; i < param.length; i++) {
                if (i == 0) j = j + i;
                else j = j + 1;
                array[j] = param[i];
                j = j + 1;
                array[j] = value[i];
            }
        }else{
            logger.error("WARNING: Check your array size");
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T,E> Object[] concatenateDifferentArrays(T[] a,E[] b) {
        int aLen = a.length;
        int bLen = b.length;
        Object[] c = (Object[]) Array.newInstance(b.getClass().getComponentType(), aLen + bLen);
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(Arrays.asList(a));
        System.arraycopy(ListUtilities.toArray(objectList), 0, c, 0, aLen);
        objectList.clear();
        objectList.addAll(Arrays.asList(b));
        System.arraycopy(ListUtilities.toArray(objectList), 0, c, aLen, bLen);
        return c;
    }

    /**
     * Merge the content of two arrays of string with same size for
     * make the args for a main method java class with option e home.
     * @param param array of parameter.
     * @param value array of values.
     * @param <T> generic type.
     * @return merged array.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] concatenateArraysForInput(T[] param, T[] value) {
       return (T[]) concatenateDifferentArraysForInput(param,value);
    }

    /**
     * Method to copy a part of the array.
     * @param oldArray the Array Colecction you wan tot copy.
     * @param startIndex the start index.
     * @param endIndex the end index.
     * @param <T> generic type.
     * @return the subArray.
     */
    public static <T> T[] copy(T[] oldArray,int startIndex,int endIndex){
        return Arrays.copyOfRange(oldArray, startIndex, endIndex);
    }

    /**
     * Method for copy the content of a array to another array of the same type.
     * @param baseArray array.
     * @param <T> generic type.
     * @return array copied.
     */
 /*   @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] baseArray){
        T[] newArray = (T[]) Array.newInstance(baseArray.getClass().getComponentType(), baseArray.length);
        System.arraycopy(baseArray, 0, newArray, 0, newArray.length);
        return newArray;
    }*/

    /**
     * Method to clone a Array.
     * @param array the array to copy.
     * @param <T> the generic type.
     * @return the Copy of the Array.
     */
    public static <T> T[] clone(final T[] array) {
        if (array == null)  return null;
        return array.clone();
    }

    /**
     * Method to check is a array is empty or with all value null or empty.
     * @param array array.
     * @param <T> generic type.
     * @return boolean value.
     */
    public static <T> boolean isEmpty(T[] array){
        //  return array == null || array.length == 0;
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
     * Method to order a int array with a BubbleSort.
     * @param data Array Collection of int.
     */
    public static void bubbleSortArray(int[] data){
        boolean swapDone;
        do {
            swapDone = false;
            for (int i=0; i<data.length-1; i++) {
                if (data[i]>data[i+1]) {
                    swapDone = true;
                    int tmp = data[i];
                    data[i] = data[i+1];
                    data[i+1] = tmp;
                }
            }
        } while (swapDone);

    }

    /**
     * Method to convert a Array to a Map.
     * @param array the Array to convert.
     * @return the Map.
     */
    public static Map<Object, Object> toMap(final Object[] array) {
        if (array == null) return null;
        final Map<Object, Object> map = new HashMap<>((int) (array.length * 1.5));
        for (int i = 0; i < array.length; i++) {
            final Object object = array[i];
            if (object instanceof Map.Entry<?, ?>) {
                final Map.Entry<?,?> entry = (Map.Entry<?,?>) object;
                map.put(entry.getKey(), entry.getValue());
            } else if (object instanceof Object[]) {
                final Object[] entry = (Object[]) object;
                if (entry.length < 2) {
                    throw new IllegalArgumentException("Array element " + i + ", '"
                            + object
                            + "', has a length less than 2");
                }
                map.put(entry[0], entry[1]);
            } else {
                throw new IllegalArgumentException("Array element " + i + ", '"
                        + object
                        + "', is neither of type Map.Entry nor an Array");
            }
        }
        return map;
    }

    public static boolean isSameLength(final Object[] array1, final Object[] array2) {
        return !((array1 == null && array2 != null && array2.length > 0) ||
                (array2 == null && array1 != null && array1.length > 0) ||
                (array1 != null && array2 != null && array1.length != array2.length));
    }

    public static boolean isSameType(final Object array1, final Object array2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        return array1.getClass().getName().equals(array2.getClass().getName());
    }

    public static void reverse(final Object[] array) {
        if (array == null) return;
        reverse(array, 0, array.length);
    }

    public static void reverse(final Object[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        Object tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    @SuppressWarnings("unchecked") // remove() always creates an array of the same type as its input
    public static <T> T[] remove(final T[] array, final int index) {
        final int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }

    @SuppressWarnings("unchecked") // removeAll() always creates an array of the same type as its input
    private static <T> T[] removeAll(final T[] array, final Integer... indices) {
        final int length = getLength(array);
        int diff = 0; // number of distinct indexes, i.e. number of entries that will be removed

        if (!isEmpty(indices)) {
            Arrays.sort(indices);
            int i = indices.length;
            int prevIndex = length;
            while (--i >= 0) {
                final int index = indices[i];
                if (index < 0 || index >= length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
                }
                if (index >= prevIndex) continue;
                diff++;
                prevIndex = index;
            }
        }
        final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), length - diff);
        if (diff < length) {
            int end = length; // index just after last copy
            int dest = length - diff; // number of entries so far not copied
            for (int i = indices.length - 1; i >= 0; i--) {
                final int index = indices[i];
                if (end - index > 1) { // same as (cp > 0)
                    final int cp = end - index - 1;
                    dest -= cp;
                    System.arraycopy(array, index + 1, result, dest, cp);
                    // Afer this copy, we still have room for dest items.
                }
                end = index;
            }
            if (end > 0) {
                System.arraycopy(array, 0, result, 0, end);
            }
        }
        return result;
    }


    public static <T> int getLength(final T[] array) {
        if (array == null)  return 0;
        return Array.getLength(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] subarray(T[] array,int offset,int length){
        if (array == null) {
            throw new NullPointerException();
        } else if ((offset < 0) || (offset > array.length) || (length < 0) ||
                ((offset + length) > array.length) || ((offset + length) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (length == 0) {
            return null;
        }
        T[] newArray = (T[]) Array.newInstance(Object.class, length);
        System.arraycopy(array, offset, newArray, 0, length);
        return newArray;

    }


    /**
     * Method to get the index of a specific object in a Array.
     * @param <T> generic variable.
     * @param array  the array to search through for the object, may be null.
     * @param objectToFind  the object to find, may be null.
     * @return the index of the object within the array starting at the index.
     */
    public static <T> int indexOf(final T[] array, final Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * Method to get the index of a specific object in a Array.
     * @param <T> generic variable.
     * @param array  the array to search through for the object, may be null.
     * @param objectToFind  the object to find, may be null.
     * @param startIndex  the index to start searching at.
     * @return the index of the object within the array starting at the index.
     */
    public static <T> int indexOf(final T[] array, final Object objectToFind, int startIndex) {
        if (array == null) return -1;
        if (startIndex < 0)  startIndex = 0;
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null)  return i;
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i]))  return i;
            }
        }
        return -1;
    }

    /**
     * Method to convert a array to a string with a specific separator
     * @param array the Array Collection.
     * @param separator the char separator.
     * @param <T> generic type.
     * @return the String of the content of the array.
     */
    public static <T> String toString(T[] array,char separator){
        if(StringUtilities.isNullOrEmpty(Character.toString(separator))){
            String s = Arrays.toString(array);
            s = s.substring(1,s.length()-1);
            return s;
        }else {
            StringBuilder strBuilder = new StringBuilder();
            if (array != null && array.length > 0) {
                for (int i = 0; i < array.length; i++) {
                    if (array[i] != null){
                        strBuilder.append(array[i].toString());
                        if (i < array.length - 1) strBuilder.append(separator);
                    }
                }
            }
            return strBuilder.toString();
        }

    }

    /**
     * Take an array of anything and turn it into a string
     *
     * @param array any array
     * @return a string representing that array
     */
    /*public static String toString(Object[] array) {
        StringBuilder result = new StringBuilder();
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    result.append(",");
                }
                if (array[i] != null) {
                    result.append(array[i].toString());
                }
            }
        }
        return result.toString();
    }*/

    /**
     * Method to convert a array to a string.
     * @param array the Array Collection.
     * @param <T> generic type.
     * @return the String of the content of the array.
     */
    public static <T> String toString(T[] array){
        return toString(array, ' ');
    }


    /**
     * Create a set from any array
     *
     * @param <T> generic variable.
     * @param array any array (including null or empty)
     * @return the set with the values from the array
     */
    public static <T> Set<T> toSet(T[] array) {
        Set<T> set = new HashSet<>();
        if (array != null) {
            for (T anArray : array) {
                if (anArray != null) {
                    set.add(anArray);
                }
            }
        }
        return set;
    }

    public static <T> List<T> toList(T[] array){
        return toList(array,false);
    }

    public static <T> List<T> toList(T[] array,boolean mutableList){
        if(mutableList)return new ArrayList<>(Arrays.asList(array)); //List Mutable
        else return Arrays.asList(array); //List Immutable
    }

    /**
     * Method to convert two array keys and values tp a HashMap.
     * @param keys array of keys.
     * @param values array of values.
     * @param <K> the generic key.
     * @param <V> the generic value.
     * @return the hasmap fulled with array.
     */
    public static <K,V> HashMap<K,V> toMap(K[] keys, V[] values) {
        int keysSize = (keys != null) ? keys.length : 0;
        int valuesSize = (values != null) ? values.length : 0;
        if (keysSize == 0 && valuesSize == 0) {
            // return mutable map
            return new HashMap<>();
        }
        if (keysSize != valuesSize) {
            throw new IllegalArgumentException(
                    "The number of keys doesn't match the number of values.");
        }
        HashMap<K, V> map = new HashMap<>();
        for (int i = 0; i < keysSize; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    public static <T> Integer getIndexField(T[] array,T field){
        return Arrays.asList(array).indexOf(field);
    }

}
