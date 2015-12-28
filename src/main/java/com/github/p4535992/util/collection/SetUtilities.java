package com.github.p4535992.util.collection;

import java.util.*;

/**
 * Created by 4535992 on 21/12/2015.
 */
public class SetUtilities {

    /**
     * Method to create a Set collection with a single element.
     * @param object the single element.
     * @param <T> generic type.
     * @return set with a single element.
     */
    @SuppressWarnings("rawtypes")
    public static <T> Set createSingleton(T object){
        return Collections.singleton(object);
    }

    public static <T> Enumeration<T> toEnumeration(Set<T> set){
        return Collections.enumeration(set);
    }

    public static <T> List<T> toList(Set<T> set){
        return new ArrayList<>(set);
    }

    public static <T> T[] toArray(Set<T> set){
        return ListUtilities.toArray(toList(set));
    }
}
