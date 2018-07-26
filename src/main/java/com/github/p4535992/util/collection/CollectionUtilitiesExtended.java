package com.github.p4535992.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CollectionUtilitiesExtended {
	
    public static <T> boolean containsElementInstanceOfClass(final Collection<T> collection, final Class<? extends T> clazz) {
        if (collection != null) {
            final Iterator<T> iterator = collection.iterator();
            while (iterator.hasNext()) {
                if (clazz.isInstance(iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static <T> List<T> getElementsInstanceOfClass(final Collection<T> collection, final Class<? extends T> clazz) {
        final List<T> list = new ArrayList<T>();
        if (collection != null) {
            for (final T next : collection) {
                if (clazz.isInstance(next)) {
                    list.add(next);
                }
            }
        }
        return list;
    }
    
    public static String listToString(final Collection<String> lista, final String separator) {
        String string = "";
        if (lista == null || lista.isEmpty()) {
            return string;
        }
        final Iterator<String> iterator = lista.iterator();
        while (iterator.hasNext()) {
            string = string + separator + iterator.next();
        }
        return string.substring(separator.length());
    }
    
    /**
     * https://e.printstacktrace.blog/2017/09/divide-a-list-to-lists-of-n-size-in-Java-8/
     */
    public static  <T> Collection<List<T>> partition(List<T> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

}
