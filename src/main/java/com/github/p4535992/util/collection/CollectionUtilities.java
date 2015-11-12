package com.github.p4535992.util.collection;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;

import java.io.IOException;
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
     * Method to create a Array collection with a single element.
     * @param object the single element.
     * @param size the int size for the Array.
     * @param <T> generic type
     * @return array with a single element.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createArrayWithSingleElement(T object,int size){
        T[] newArray = (T[]) Array.newInstance(object.getClass(), size);
        Arrays.fill(newArray, object);
        return newArray;
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
        if (value != null) return false;
        else {
            if (map.containsKey(key)) {// Key might be present...
                return true; // Okay, there's a key but the value is null
            }
            return true; // Definitely no such key
        }
    }

    /**
     * Method to convert a null array to a empty array.
     * @param array the Array to analyze.
     * @return the new Array.
     */
    public static Object[] nullToEmpty(final Object[] array) {
        if (array == null || array.length == 0) return new Object[0];
        return array;
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
            if(collection instanceof Set) return new ArrayList<>((Set<T>)collection);
            if(collection instanceof Iterable){
                if(collection instanceof List) return (List<T>) collection;
                else {
                    List<T> list = new ArrayList<>();
                    for (T e : (Iterable<T>)collection) {list.add(e);}
                    return list;
                }
            }
            if(collection instanceof Object[]){
                //return Arrays.asList((T[]) collection); //List Immutable
                return new ArrayList<>(Arrays.asList((T[]) collection)); //List Mutable

            }
            if(collection instanceof Iterator){
                List<T> list = new ArrayList<>();
                Iterator<T> iterator = (Iterator<T>) collection;
                while (iterator.hasNext()) { list.add(iterator.next());}
                return list;
            }
            else return new ArrayList<>();
        }else{
            SystemLog.error("The object:"+collection+ " is not a Collection");
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
            if(collection instanceof Enumeration) return new HashSet<>(Collections.list((Enumeration<T>) collection));
            if(collection instanceof List) return new HashSet<>((List<T>)collection);
            else return new HashSet<>();
        }else{
            SystemLog.error("The object:"+collection+ " is not a Collection");
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
            if (collection instanceof Set) return Collections.enumeration((Set<T>) collection);
            else return new Enumeration<T>() {
                @Override
                public boolean hasMoreElements() {return false;}
                @Override
                public T nextElement() {return null;}
            };
        }else{
            SystemLog.error("The object:"+collection+ " is not a Collection");
            return new Enumeration<T>() {
                @Override
                public boolean hasMoreElements() {return false;}
                @Override
                public T nextElement() {return null;}
            };
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
            SystemLog.error("The object:"+collection+ " is not a Collection");
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
            if (collection instanceof List) return ((List<T>)collection).iterator();
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
            SystemLog.error("The object:"+collection+ " is not a Collection");
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
            if (collection instanceof List) {
                List<T> list = (List<T>) collection;
                T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
                if(ReflectionUtilities.isWrapperType(list.get(0).getClass())){ //if is a primitive class
                    for(int i = 0; i < list.size(); i++) array[i] = list.get(i);
                }else{ //is is not a primitive class
                    list.toArray(array);
                }
                return array;
            }
            if(collection instanceof TreeSet){
                TreeSet<T> treeSet = (TreeSet<T>) collection;
                //SortedSet<T> sTreeSet = new TreeSet<>();
                //http://www.codemiles.com/collections/convert-treeset-content-to-array-t10599.html#sthash.xTW3pw80.dpuf
                return (T[]) treeSet.toArray();
            }
            if(collection instanceof Set) return (T[])toArray(toList(collection));
            else return (T[]) Array.newInstance(Object.class, 0);
        }else{
            return  (T[]) Array.newInstance(Object.class, 0);
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
            c = toArray(list);
        }
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
    public static <T> T[] mergeArraysForInput(T[] param, T[] value) {
        T[] array = (T[]) Array.newInstance(param[0].getClass(),param.length+value.length);
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
            SystemLog.warning("WARNING: Check your array size");
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T,E> Object[] concatenateArraysDifferent(T[] a,E[] b) {
        int aLen = a.length;
        int bLen = b.length;
        Object[] c = (Object[]) Array.newInstance(b.getClass().getComponentType(), aLen + bLen);
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(Arrays.asList(a));
        System.arraycopy(toArray(objectList), 0, c, 0, aLen);
        objectList.clear();
        objectList.addAll(Arrays.asList(b));
        System.arraycopy(toArray(objectList), 0, c, aLen, bLen);
        return c;
    }

    /**
     * Method for copy the content of a array to aniother array of the same type.
     * @param baseArray array.
     * @param <T> generic type.
     * @return array copied.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] baseArray){
        T[] newArray = (T[]) Array.newInstance(baseArray.getClass().getComponentType(), baseArray.length);
        System.arraycopy(baseArray, 0, newArray, 0, newArray.length);
        return newArray;
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
     * Merge the content of two arrays of string with same size for
     * make the args for a main method java class with option e home.
     * @param param array of parameter.
     * @param value array of values.
     * @param <T> generic type.
     * @param <E> generic type.
     * @return merged array.
     */
    @SuppressWarnings("unchecked")
    public static <T,E> Object[] concatenateArraysForCommandInput(T[] param, E[] value) {
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
            SystemLog.error("WARNING: Check your array size");
        }
        return array;
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
     * Method to convert a List of arguments to a Array.
     * @param items the Arguments.
     * @param <T> the Generic Type.
     * @return the Array.
     */
   /* public static <T> T[] toArray(final T... items) {
        return items;
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
        for (int i = 0; i < integerArray.length; i++) { result[i] = integerArray[i].intValue();}
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

    /**
     * Method to Split List in n°. SubLIst of length L,chops a list into non-view sublists of length L.
     * @param list the List Collection To Split.
     * @param L the Size of all the SubList.
     * @param <T> generic type.
     * @return A List Collection of Lists with the same size
     */
    public static <T> List<List<T>> choppedList(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(list.subList(i, Math.min(N, i + L))));
        }
        return parts;
    }

    /**
     * Sorts a HashMap based on the values with Double data type
     * @param <T> the generic variable.
     * @param input hashMap where order.
     * @return the hashMap sorted.
     */
    public static <T> HashMap<T, Double> sortHashMap(HashMap<T, Double> input) {
        Map<T, Double> tempMap = new HashMap<>();
        for (T wsState : input.keySet()) {
            tempMap.put(wsState, input.get(wsState));
        }
        List<T> mapKeys = new ArrayList<>(tempMap.keySet());
        List<Double> mapValues = new ArrayList<>(tempMap.values());
        HashMap<T, Double> sortedMap = new LinkedHashMap<>();
        TreeSet<Double> sortedSet = new TreeSet<>(mapValues);
        Object[] sortedArray = sortedSet.toArray();
        int size = sortedArray.length;
        for (int i = size - 1; i >= 0; i--) {
            sortedMap.put(
                    mapKeys.get(mapValues.indexOf(sortedArray[i])), (Double) sortedArray[i]);
        }
        return sortedMap;
    }

    /**
     * Method to convert a HashTable Collection of Integer to a TreeMap Collection of int.
     * @param hashTable the HashTable Collection.
     * @param <K> generic type.
     * @param <V> generic type.
     * @return TreeMap Collection.
     */
    public static <K,V> TreeMap<K,V> toTreeMap(Hashtable<K,V> hashTable){
        return new TreeMap<>(hashTable);
    }

    /**
     * Method to convert a HashTable Collection of Integer to a TreeMap Collection of int.
     * @param hMap the Map Collection.
     * @param <K> generic type.
     * @param <V> generic type.
     * @return Hashtable Collection.
     */
    public static <K,V> Hashtable<K,V> toHashTable(Map<K,V> hMap){
        Hashtable<K,V> ht = new Hashtable<>();
        ht.putAll(hMap);
        return ht;
    }

    /**
     * Method to convert a HashTable Collection of Integer to a TreeMap Collection of int.
     * @param hashTable the HashTable Collection.
     * @param <K> generic type.
     * @param <V> generic type.
     * @return Map Collection.
     */
    public static <K,V> Map<K,V> toHashMap(Hashtable<K,V> hashTable){
        return new HashMap<>(hashTable);
    }

    /**
     * Method to convert a HashTable Collection of Integer to a TreeMap Collection of int.
     * @param hashTable the HashTable Collection.
     * @param <K> generic type.
     * @param <V> generic type.
     * @return LinkedHashMap Collection.
     */
    public static <K,V> LinkedHashMap<K,V> toLinkedHashMap(Hashtable<K,V> hashTable){
        return new LinkedHashMap<>(hashTable);
    }


    /**
     * Method that assigns through a mechanism of " mapping" to each value
     * Separate the parameter in question a number ( the frequency ) the prendeno
     * Value with the highest frequency we obtained the value most popular
     * For this parameter.
     * @param al List to analyze.
     * @param <T> generic type.
     * @return  the most common element on the List.
     */
    public static <T> T moreCommon(List<T> al){
        Map<T,Integer> map = new HashMap<>();
        for (T anAl : al) {
            Integer count = map.get(anAl);
            map.put(anAl, count == null ? 1 : count + 1);   //auto boxing and count
        }
        T keyParameter=null;
        Integer keyValue =0;
        for ( Map.Entry<T, Integer> entry : map.entrySet()) {
            T key = entry.getKey();
            Integer value = entry.getValue();
            if(value >= keyValue && key!=null && !String.valueOf(key).equals("null") && !String.valueOf(key).equals("NULL")){
                keyValue = value;
                keyParameter = key;
            }
        }
        return keyParameter;
    }//getMoreCommonParameter

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
            for (int i = 0; i < array.length; i++) {
                strBuilder.append(array[i].toString());
                if (i < array.length - 1) strBuilder.append(separator);
            }
            return strBuilder.toString();
        }

    }

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


    /**
     * Method to check if  a Array contains a specific element.
     * @param array the Array to analyze.
     * @param objectToFind the object to find.
     * @return if true the element is contained on the Array.
     */
    public static boolean contains(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }

    /**
     * Method to get the index of a specific object in a Array.
     * @param array  the array to search through for the object, may be null.
     * @param objectToFind  the object to find, may be null.
     * @return the index of the object within the array starting at the index.
     */
    public static int indexOf(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * Method to get the index of a specific object in a Array.
     * @param array  the array to search through for the object, may be null.
     * @param objectToFind  the object to find, may be null.
     * @param startIndex  the index to start searching at.
     * @return the index of the object within the array starting at the index.
     */
    public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
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
        return (T[]) remove((Object) array, index);
    }

    @SuppressWarnings("unchecked") // removeAll() always creates an array of the same type as its input
    public static <T> T[] removeAll(final T[] array, final int... indices) {
        return (T[]) removeAll((Object) array, toPrimitive(clone((Integer[]) toObject(indices))));
    }

    private static Object removeAll(final Object array, final int... indices) {
        final int length = getLength(array);
        int diff = 0; // number of distinct indexes, i.e. number of entries that will be removed

        if (!isEmpty(toObject(indices))) {
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
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - diff);
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

    private static Object remove(final Object array, final int index) {
        final int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }

    public static int getLength(final Object array) {
        if (array == null)  return 0;
        return Array.getLength(array);
    }

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
        for (int i = 0 ; i < length ; i++) {
            newArray[i] = array[offset + i];
        }
        return newArray;

    }


}
