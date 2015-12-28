package com.github.p4535992.util.collection;

import java.util.*;

/**
 * Created by 4535992 on 21/12/2015.
 */
public class MapUtilities {

    /**
     * Method for chek if a specific key on the map is absent or not existsa value for that key.
     * @param <K> generic key.
     * @param <V> generic value.
     * @param map the map to inspect.
     * @param key the key valur of the map to search.
     * @return if tru exists a value not empty for that key.
     */
    public static <K,V> boolean isMapValueNullOrInexistent(Map<K,V> map, K key){
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

    public <K,V> V getValueAt(LinkedHashMap<K,V> map,int i){
        Map.Entry<K, V>entry = getEntryAt(map, i);
        if(entry == null) return null;
        return entry.getValue();
    }

    public <K,V> Map.Entry<K, V> getEntryAt(LinkedHashMap<K,V> map,int i){
        // check if negetive index provided
        Set<Map.Entry<K,V>>entries = map.entrySet();
        int j = 0;

        for(Map.Entry<K, V>entry : entries)
            if(j++ == i)return entry;

        return null;

    }

    public <K,V> V getElementAt(LinkedHashMap<K,V> map, int index) {
        for (Map.Entry<K,V> entry : map.entrySet()) {
            if (index-- == 0) {
                return entry.getValue();
            }
        }
        return null;
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


}
