package com.github.p4535992.util.hibernate.interceptor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by 4535992 on 04/01/2016.
 */
public interface IGenericInterceptor {


    void setSession(Session session);

    Session getSession();

    void setSessionFactory(SessionFactory sessionFactory);

    SessionFactory getSessionFactory();
}
