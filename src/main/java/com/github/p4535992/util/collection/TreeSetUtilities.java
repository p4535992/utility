package com.github.p4535992.util.collection;

import java.util.TreeSet;

/**
 * Created by 4535992 on 21/12/2015.
 */
public class TreeSetUtilities {

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(TreeSet<T> treeSet){
        //SortedSet<T> sTreeSet = new TreeSet<>();
        //http://www.codemiles.com/collections/convert-treeset-content-to-array-t10599.html#sthash.xTW3pw80.dpuf
        return (T[]) treeSet.toArray();
    }
}
