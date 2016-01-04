package com.github.p4535992.util.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 4535992 on 27/04/2015.
 */
public interface IHibernateKit<T> {


    SessionFactory getSessionFactory();
    void setSessionFactory(SessionFactory sessionFactory);
    Session getSession();
    void shutdown();
    void openSession();
    void closeSession();
    void restartSession();
    Session getCurrentSession();

    Serializable insertRow(T newInstance);
    T selectRow(Serializable id);
    List<T> selectRows(String nameColumn, int limit, int offset);
    List<T> selectRows();
    int getCount();
    Serializable updateRow(String whereColumn, Object whereValue);
    Serializable updateRow(T object);
    Serializable deleteRow(String whereColumn, Object whereValue);
    Serializable deleteRow(T object);

    void updateAnnotationTable(String nameOfAttribute, String newValueAttribute);
    void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
    void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;

    //FINDER IN PROGRESS...
    List<T> executeFinder(java.lang.reflect.Method method, final Object[] queryArgs);
    Iterator<T> iterateFinder(java.lang.reflect.Method method, final Object[] queryArgs);
}
