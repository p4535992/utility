package com.github.p4535992.util.collection;

import com.github.p4535992.util.reflection.ReflectionUtilities;

import java.util.*;

/**
 * Created by 4535992 on 21/12/2015.
 */
@SuppressWarnings("unused")
public class ListUtilities {

    /**
     * Method to create a List collection with a single element.
     * @param object the single element.
     * @param <T> generic type.
     * @return list with a single element.
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> createSingleton(T object){
        return Collections.singletonList(object);
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
     * Remove all duplicate objects from a list
     *
     * @param <T> generic variable.
     * @param list any list
     * @return the original list with the duplicate objects removed
     */
    public static <T> List<T> removeDuplicates(List<T> list) {
        Set<T> s = new HashSet<>();
        for (Iterator<T> iter = list.iterator(); iter.hasNext();) {
            T element = iter.next();
            if (! s.add(element)) {
                iter.remove();
            }
        }
        return list;
    }

    /**
     * Method to convert a List to a Set Collection.
     * @param list the List to convert.
     * @param <T> the generic variable.
     * @return the Set Collection.
     */
    public static <T> Set<T> toSet(List<T> list){
        return new HashSet<>(list);
    }

    /**
     * Method to convert a List to a Array Collection.
     * @param list the List to convert.
     * @param <T> the generic variable.
     * @return the Array Collection.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list){
        T[] array = (T[]) ArrayUtilities.create(list.get(0).getClass(),list.size());
        if(ReflectionUtilities.isWrapperType(list.get(0).getClass())){ //if is a primitive class
            for(int i = 0; i < list.size(); i++) array[i] = list.get(i);
        }else{ //is is not a primitive class
            list.toArray(array);
        }
        return array;
    }

    /**
     * Take a list of number objects and return an int[] array
     * @param list any list of {@link Number}
     * @return an array of int
     */
    /*public static int[] toArray(List<Number> list) {
        int[] newArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArray[i] = list.get(i).intValue();
        }
        return newArray;
    }*/
}
