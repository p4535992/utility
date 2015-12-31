package com.github.p4535992.util.collection;

import java.util.*;

/**
 * Created by 4535992 on 21/12/2015.
 * @author 4535992.
 * @version 2015-12-29.
 */
public class EnumerationUtilities {

    public static <T> Enumeration<T> create(){
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {return false;}
            @Override
            public T nextElement() {return null;}
        };
    }

    public static <T> Set<T> toSet(Enumeration<T> enumeration){
        return new HashSet<>(Collections.list(enumeration));
    }

    /**
     * Method to convert a Enumeration collection to a Array Collection.
     * @param enums the Enumeration Collection.
     * @param <T> the generic variable.
     * @return the Array Collection.
     */
    public static <T> T[] toArray(Enumeration<T> enums) {
        List<T> retString = new ArrayList<>();
        while(enums.hasMoreElements()){
            retString.add(enums.nextElement());
        }
        return ListUtilities.toArray(retString);
    }

}
