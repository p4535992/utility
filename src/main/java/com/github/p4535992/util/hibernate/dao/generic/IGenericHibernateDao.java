package com.github.p4535992.util.hibernate.dao.generic;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 4535992 on 23/04/2015.
 */
public interface IGenericHibernateDao<T> {

    //SPRING SUPPORT (DISABLE)
    /*
    void setHibernateTemplate(HibernateTemplate hibernateTemplate);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setDataSource(DataSource ds);
    void loadSpringContext(String filePathXml) throws FileNotFoundException;

    DataSource getDataSource();
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setSessionFactory(DataSource dataSource);
    */


    //HIBERNATE BASE
    SessionFactory getSessionFactory();
    void setSessionFactory(SessionFactory sessionFactory);
    Session getSession();
    void setSession(Session session);
    void shutdown();
    void openSession();
    void closeSession();
    void restartSession();
    Session getCurrentSession();

    void setNewCriteria(Criterion criterion);
    void setNewServiceRegistry();

    //CRUD OPERATION ITH HIBERNATE/SPRING

    Serializable insertRow(T object);
    T selectRow(Serializable id);
    List<T> selectRows(String nameColumn, int limit, int offset);
    List<T> selectRows();
    int getCount();
    Serializable updateRow(T object);
    Serializable updateRow(String whereColumn, Object whereValue);
    Serializable deleteRow(T object);
    Serializable deleteRow(String whereColumn, Object whereValue);
    void setInterceptor(Class<? extends Interceptor> interceptor);


    //FINDER IN PROGRESS...
    //List<T> executeFinder(java.lang.reflect.Method method, final Object[] queryArgs);
    //Iterator<T> iterateFinder(java.lang.reflect.Method method, final Object[] queryArgs);
//    void updateAnnotationEntity(String nameOfAttribute, String newValueAttribute);
//    void updateAnnotationTable(String nameOfAttribute, String newValueAttribute);
//    void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
//    void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
//    List<Object[]> getAnnotationTable();
}


