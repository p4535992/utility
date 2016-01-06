package com.github.p4535992.util.hibernate.interceptor.impl;

import com.github.p4535992.util.hibernate.interceptor.GenericInterceptor;
import com.github.p4535992.util.object.GeoDocument;
import org.hibernate.*;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 4535992 on 14/05/2015.
 * @author 4535992.
 * @version 2015-07-01.
 */
@SuppressWarnings("unused")
public class GeoDocumentInterceptor extends GenericInterceptor<GeoDocument>{
    
    private static final long serialVersionUID = 12L;

    public void setSession(Session session) {
        if(sessionFactory!=null) {
           this.session = sessionFactory.getCurrentSession();
        }else {
            this.session = session;
        }
    }

    public Session getSession(){
        return session;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    //called before commit into database
    @SuppressWarnings("rawtypes")
    public void preFlush(Iterator entities) {
        super.preFlush(entities);
    }

    //called after committed into database
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator entities) {
        super.postFlush(entities);
    }

    public Boolean isTransient(Object entity) {
       return super.isTransient(entity);
    }

    public Object instantiate(String entityName, EntityMode entityMode,Serializable id) {
        return super.instantiate(entityName,entityMode,id);
    }

    public int[] findDirty(Object entity, Serializable id,
                           Object[] currentState, Object[] previousState,
                           String[] propertyNames, Type[] types) {
       return super.findDirty(entity,id,currentState,previousState,propertyNames,types);
    }

    /**
     * The getEntityName method gets the entity name for a persistent or transient instance. The getEntity method
     * get a fully loaded entity instance that is cached externally
     */

    public String getEntityName(Object entity) {
        return super.getEntityName(entity);
    }

    public Object getEntity(String entityName, Serializable id) {
        return super.getEntity(entityName,id);
    }

    /**
     * The above methods are a part of Hibernate's transaction mechanism.They are called before/ after transaction
     * completion and after transaction beginning.
     */

    public void afterTransactionBegin(Transaction tx) {
        super.afterTransactionBegin(tx);
    }

    public void afterTransactionCompletion(Transaction tx) {
        super.afterTransactionCompletion(tx);
    }

    public void beforeTransactionCompletion(Transaction tx) {
        super.beforeTransactionCompletion(tx);
    }

    /**The onPrepareStatement method is called when sql queries are to be executed.
     * The other methods are called when collections are fetched/updated/deleted.
     */
    public String onPrepareStatement(String sql) {
        return super.onPrepareStatement(sql);
    }

    public void onCollectionRemove(Object collection, Serializable key)
            throws CallbackException {
       super.onCollectionRemove(collection,key);
    }

    public void onCollectionRecreate(Object collection, Serializable key)
            throws CallbackException {
       super.onCollectionRecreate(collection,key);
    }

    public void onCollectionUpdate(Object collection, Serializable key)
            throws CallbackException {
       super.onCollectionUpdate(collection,key);
    }

    /**
     * Overriding this method allows us to plugin to the delete mechanism.
     * The method receives the Entity being deleted, its identifier and arrays
     * of details regarding the object. Similar intercept methods include:
     */

    public boolean onSave(Object entity,Serializable id,
                          Object[] state,String[] propertyNames,Type[] types)
            throws CallbackException {
        return super.onSave(entity,id,state,propertyNames,types);
    }

    public boolean onFlushDirty(Object entity,Serializable id,
                                Object[] currentState,Object[] previousState,
                                String[] propertyNames,Type[] types)
            throws CallbackException {
        return super.onFlushDirty(entity,id,currentState,previousState,propertyNames,types);
    }

    public boolean onLoad(
            Object entity, Serializable id,
            Object[] state, String[] propertyNames, Type[] types) {
       return super.onLoad(entity, id, state, propertyNames, types);
    }


    public void onDelete(Object entity, Serializable id,
                         Object[] state, String[] propertyNames,
                         Type[] types) {
        super.onDelete(entity, id, state, propertyNames, types);
    }


}
