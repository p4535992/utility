package com.github.p4535992.util.collection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class utility for a Map collection who accept duplicate key.This extension of HashMap support duplicate keys
 * href: http://www.java2s.com/Code/Java/Collections-Data-Structure/ThisextensionofHashMapsupportduplicatekeys.htm
 * @author 4535992.
 * @version 2015-10-05.
 */
@SuppressWarnings({"unused","rawtypes","unchecked"})
public class Bag extends LinkedHashMap {
    
    private static final long serialVersionUID = 342L;
    
  public List getValues(Object key) {
    if (super.containsKey(key)) {
      return (List) super.get(key);
    } else {
      return new ArrayList();
    }
  }


  public Object get(Object key) {
    ArrayList values = (ArrayList) super.get(key);
    if (values != null && !values.isEmpty()) {
      return values.get(0);
    } else {
      return null;
    }
  }


  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  public int size() {
    int size = 0;
    for (Object o : super.keySet()) {
      ArrayList values = (ArrayList) super.get(o);
      size = size + values.size();
    }

    return size;
  }

  public Object put(Object key, Object value) {
    ArrayList values = new ArrayList();

    if (super.containsKey(key)) {
      values = (ArrayList) super.get(key);
      values.add(value);

    } else {
      values.add(value);
    }

    super.put(key, values);

    return null;
  }


  public void removeAt(Object key, Object value) {
    List values = getValues(key);
    if (values != null) {
      values.remove(value);
      if (values.isEmpty()) {
        remove(key);
      }
    }
  }

  public Collection values() {
    List values = new ArrayList();

    for (Object o : super.keySet()) {
      List keyValues = (List) super.get(o);
      values.addAll(keyValues);
    }

    return values;
  }
}
