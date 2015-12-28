package com.github.p4535992.util.collection;

import java.util.*;

/**
 * Created by 4535992 on 21/12/2015.
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

}
