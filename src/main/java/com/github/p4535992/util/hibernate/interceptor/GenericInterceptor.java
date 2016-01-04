package com.github.p4535992.util.hibernate.interceptor;

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
public class GenericInterceptor<T> extends EmptyInterceptor implements IGenericInterceptor{

    private static final long serialVersionUID = 13L;

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GenericInterceptor.class);

    protected Class<T> cl;

    protected Session session;
    protected SessionFactory sessionFactory;

    protected Set<Object> inserts = new HashSet<>();
    protected Set<Object> updates = new HashSet<>();
    protected Set<Object> deletes = new HashSet<>();
    protected Set<Object> saves = new HashSet<>();

    public enum InterceptorOperations{ INSERT,UPDATE,DELETE,SAVE}

    private static GenericInterceptor instance = null;

    protected GenericInterceptor(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class<T>) pt.getActualTypeArguments()[0];
    }

    public static GenericInterceptor getInstance(){
        if(instance == null) {
            instance = new GenericInterceptor();
        }
        return instance;
    }

    @Override
    public void setSession(Session session) {
        if(sessionFactory!=null) {
           this.session = sessionFactory.getCurrentSession();
        }else {
            this.session = session;
        }
    }

    @Override
    public Session getSession(){
        return session;
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * The postFlush method is  called after a flush that actually ends in execution of the
     * SQL statements required to synchronize in-memory state with the database.
     * The preFlush method on the other hand is called before a flush occurs.
     * The isTransient method is called to distinguish between transient and detached entities.
     * The return value determines the state of the entity with respect to the current session.
     * A true indicates the entity is transient and false that the the entity is detached.
     * The instantiate method is used to instantiate the entity class. It returns null to
     * indicate that Hibernate should use the default constructor of the class.
     * The identifier property of the returned instance should be initialized with the given
     * identifier.
     * The findDirty method is called by Hibernate from flush method. Its return value
     * determines whether the entity is updated. It returns an array of property indices
     * indicating the entity is dirty. An empty array indicates that object has not been modified.
     */

    //called before commit into database
    @SuppressWarnings("rawtypes")
    public void preFlush(Iterator entities) {
        logger.info("preFlush: List of objects to flush... ");
        int i =0;
        while (entities.hasNext()) {
            Object element = entities.next();
            logger.info("preFlush: " + (++i) + " : " + element);
        }
    }

    //called after committed into database
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator entities) {
        logger.info("postFlush: List of objects that have been flushed... ");
        try{
            int i =0;
            while (entities.hasNext()) {
                Object element = entities.next();
                logger.info("postFlush: " + (++i) + " : " + element);
            }

//            for (Iterator it = inserts.iterator(); it.hasNext();) {
//                GeoDocument entity = (GeoDocument) it.next();
//                System.out.println("postFlush - insert");
//                //hbs.setSessionFactory(sessionFactory);
//                //AuditLogUtil.LogIt("Saved",entity, session.connection());
//            }
//
//            for (Iterator it = updates.iterator(); it.hasNext();) {
//                GeoDocument entity = (GeoDocument) it.next();
//                System.out.println("postFlush - update");
//               // AuditLogUtil.LogIt("Updated",entity, session.connection());
//            }
//
//            for (Iterator it = deletes.iterator(); it.hasNext();) {
//                GeoDocument entity = (GeoDocument) it.next();
//                System.out.println("postFlush - delete");
//                //AuditLogUtil.LogIt("Deleted",entity, session.connection());
//            }

        } finally {
            inserts.clear();
            updates.clear();
            deletes.clear();
        }
    }


    public Boolean isTransient(Object entity) {
        logger.info("isTransient: Checking object for Transient state... " + entity);
        return null;
    }

    public Object instantiate(String entityName, EntityMode entityMode,
                              Serializable id) {
        logger.info("instantiate: Instantiating object " + entityName +
                " with id - " + id + " in mode " + entityMode);
        return null;
    }

    public int[] findDirty(Object entity, Serializable id,
                           Object[] currentState, Object[] previousState,
                           String[] propertyNames, Type[] types) {
        logger.info("findDirty: Detects if object is dirty " + entity + " with id " + id);
        final int length = currentState.length;
        logger.info("findDirty: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            logger.info("findDirty: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " , previous state : " + previousState[i]
                    + " , current state : " + currentState[i]);
        }
        return null;
    }

    /**
     * The getEntityName method gets the entity name for a persistent or transient instance. The getEntity method
     * get a fully loaded entity instance that is cached externally
     */

    public String getEntityName(Object entity) {
        logger.info("getEntityName: name for entity " + entity);
        return null;
    }

    public Object getEntity(String entityName, Serializable id) {
        logger.info("getEntity: Returns fully loaded cached entity with name  " + entityName + " and id " + id);
        return null;
    }

    /**
     * The above methods are a part of Hibernate's transaction mechanism.They are called before/ after transaction
     * completion and after transaction beginning.
     */

    public void afterTransactionBegin(Transaction tx) {
        logger.info("afterTransactionBegin: Called for transaction " + tx);
    }

    public void afterTransactionCompletion(Transaction tx) {
        logger.info("afterTransactionCompletion: Called for transaction " + tx);
    }

    public void beforeTransactionCompletion(Transaction tx) {
        logger.info("beforeTransactionCompletion: Called for transaction " + tx);
    }

    /**The onPrepareStatement method is called when sql queries are to be executed.
     * The other methods are called when collections are fetched/updated/deleted.
     */
    public String onPrepareStatement(String sql) {
        logger.info("onPrepareStatement: Called for statement " + sql);

        //String result = sql;

//        if (entity instanceof GeoDocument) {
//            for (int i = 0; i < propertyNames.length; i++) {
//                if (propertyNames[i].equalsIgnoreCase("url")) {
//                    if(state[i].toString().contains("://")){
//
//                    }else{
//                        state[i] = "http://"+state[i].toString();
//                    }
//                }
//            }
//        }
        return sql;
    }

    public void onCollectionRemove(Object collection, Serializable key)
            throws CallbackException {
        logger.info("onCollectionRemove: Removed object with key " + key
                + " from collection " + collection);
    }

    public void onCollectionRecreate(Object collection, Serializable key)
            throws CallbackException {
        logger.info("onCollectionRemove: Recreated collection " + collection + " for key " + key);
    }

    public void onCollectionUpdate(Object collection, Serializable key)
            throws CallbackException {
        logger.info("onCollectionUpdate: Updated collection " + collection + " for key " + key);
    }

    /**
     * Overriding this method allows us to plugin to the delete mechanism.
     * The method receives the Entity being deleted, its identifier and arrays
     * of details regarding the object. Similar intercept methods include:
     */

    public boolean onSave(Object entity,Serializable id,
                          Object[] state,String[] propertyNames,Type[] types)
            throws CallbackException {
        logger.info("onSave: Saving object " + entity + " with id " + id);
        final int length = state.length;
        logger.info("onSave: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            logger.info("onSave: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " , state : " + state[i]);
        }
        return tryToCastToCurrentObject(entity,cl,InterceptorOperations.INSERT);//as no change made to object here
    }

    public boolean onFlushDirty(Object entity,Serializable id,
                                Object[] currentState,Object[] previousState,
                                String[] propertyNames,Type[] types){
        try {
            logger.info("onFlushDirty: Detected dirty object " + entity + " with id " + id);
            final int length = currentState.length;
            logger.info("onFlushDirty: Object Details are as below: ");
            for (int i = 0; i < length; i++) {
                logger.info("onFlushDirty: propertyName : " + propertyNames[i]
                        + " ,type :  " + types[i]
                        + " , previous state : " + previousState[i]
                        + " , current state : " + currentState[i]);
            }
            return tryToCastToCurrentObject(entity,cl,InterceptorOperations.UPDATE);
        }catch(CallbackException e){
            logger.error(e.getMessage(),e);
        }
        return false;//as no change made to object here

    }

    public boolean onLoad(
            Object entity, Serializable id,
            Object[] state, String[] propertyNames, Type[] types) {
        logger.info("onLoad: Attempting to load an object " + entity + " with id "
                + id);
        final int length = state.length;
        logger.info("onLoad: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            logger.info("onLoad: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " ,state : " + state[i]);
        }
//        if (entity instanceof GeoDocument) {
//            for (int i = 0; i < propertyNames.length; i++) {
//                if (propertyNames[i].equalsIgnoreCase("url")) {
//                    if(state[i].toString().contains("://")){
//
//                    }else{
//                        state[i] = "http://"+state[i].toString();
//                    }
//                }
//            }
//        }
        return tryToCastToCurrentObject(entity,cl,InterceptorOperations.SAVE);
    }


    public void onDelete(Object entity, Serializable id,
                         Object[] state, String[] propertyNames,
                         Type[] types) {

        // Called before an object is deleted. It is not recommended that the
        // interceptor modify the state.
        logger.info("onDelete: Attempting to delete an object " + entity + " with id "
                + id);
        final int length = state.length;
        logger.info("onDelete: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            logger.info("onDelete: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " ,state : " + state[i]);
        }
        tryToCastToCurrentObject(entity,cl,InterceptorOperations.DELETE);
    }

    private <T> boolean tryToCastToCurrentObject(
            Object entity,Class<T> classToCast,InterceptorOperations interceptorOperations){
        try{
            T t2 = (T) entity;//entity instanceof t
            /* if (entity instanceof GeoDocument){*/
            switch(interceptorOperations.name()){
                case "SAVE": saves.add(entity);
                case "DELETE": deletes.add(entity);
                case "INSERT": inserts.add(entity);
                case "UPDATE": updates.add(entity);
                default: /*do nothing*/
            }
            /*}*/
            return true;
        }catch(Exception e){
            logger.warn(e.getMessage(),e);
            return false;
        }
    }

}
