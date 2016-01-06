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

     @SuppressWarnings("rawtypes")
    private static GenericInterceptor instance = null;

    @SuppressWarnings("unchecked")
    protected GenericInterceptor(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        cl = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T> GenericInterceptor<T> getInstance(){
        if(instance == null) {
            instance = new GenericInterceptor<>();
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
    @Override
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
    @Override
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


    @Override
    public Boolean isTransient(Object entity) {
        logger.info("isTransient: Checking object for Transient state... " + entity);
        return null;
    }

    @Override
    public Object instantiate(String entityName, EntityMode entityMode,
                              Serializable id) {
        logger.info("instantiate: Instantiating object " + entityName +
                " with id - " + id + " in mode " + entityMode);
        return null;
    }

    @Override
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

    @Override
    public String getEntityName(Object entity) {
        logger.info("getEntityName: name for entity " + entity);
        return null;
    }

    @Override
    public Object getEntity(String entityName, Serializable id) {
        logger.info("getEntity: Returns fully loaded cached entity with name  " + entityName + " and id " + id);
        return null;
    }

    /**
     * The above methods are a part of Hibernate's transaction mechanism.They are called before/ after transaction
     * completion and after transaction beginning.
     */

    @Override
    public void afterTransactionBegin(Transaction tx) {
        logger.info("afterTransactionBegin: Called for transaction " + tx);
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        logger.info("afterTransactionCompletion: Called for transaction " + tx);
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        logger.info("beforeTransactionCompletion: Called for transaction " + tx);
    }

    /**
     * The onPrepareStatement method is called when sql queries are to be executed.
     * The other methods are called when collections are fetched/updated/deleted.
     * Called when sql string is being prepared. 
     * @param sql sql to be prepared
     * @return original or modified sql
     */
    @Override
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
    
    /**
    * Called before a collection is deleted.
    *
    * @param collection The collection instance.
    * @param key The collection key value.
    *
    * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
    */
    @Override
    public void onCollectionRemove(Object collection, Serializable key)
            throws CallbackException {
        logger.info("onCollectionRemove: Removed object with key " + key
                + " from collection " + collection);
    }

    /**
    * Called before a collection is (re)created.
    *
    * @param collection The collection instance.
    * @param key The collection key value.
    *
    * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
    */
    @Override
    public void onCollectionRecreate(Object collection, Serializable key)
            throws CallbackException {
        logger.info("onCollectionRemove: Recreated collection " + collection + " for key " + key);
    }
    
    /**
    * Called before a collection is updated.
    *
    * @param collection The collection instance.
    * @param key The collection key value.
    *
    * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
    */
    @Override
    public void onCollectionUpdate(Object collection, Serializable key)
            throws CallbackException {
        logger.info("onCollectionUpdate: Updated collection " + collection + " for key " + key);
    }

   /**
    * Called just before an object is initialized. The interceptor may change the <tt>state</tt>, which will
    * be propagated to the persistent object. Note that when this method is called, <tt>entity</tt> will be
    * an empty uninitialized instance of the class.
    * NOTE: The indexes across the <tt>state</tt>, <tt>propertyNames</tt> and <tt>types</tt> arrays match.
    *
    * @param entity The entity instance being loaded
    * @param id The identifier value being loaded
    * @param state The entity state (which will be pushed into the entity instance)
    * @param propertyNames The names of the entity properties, corresponding to the <tt>state</tt>.
    * @param types The types of the entity properties, corresponding to the <tt>state</tt>.
    *
    * @return {@code true} if the user modified the <tt>state</tt> in any way.
    *
    * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
    */
    @Override
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

    @Override
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

    @Override
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


    @Override
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

    @SuppressWarnings("unchecked")
    private boolean tryToCastToCurrentObject(
            Object entity,Class<T> classToCast,InterceptorOperations interceptorOperations){
        try{
            T t2 = (T) entity;//entity instanceof t
            /* if (entity instanceof GeoDocument){*/
            switch(interceptorOperations.name()){
                case "SAVE": saves.add(entity);     break;
                case "DELETE": deletes.add(entity); break;
                case "INSERT": inserts.add(entity); break;
                case "UPDATE": updates.add(entity); break;
                default: return false;
            }
            /*}*/
            return true;
        }catch(Exception e){
            logger.warn(e.getMessage(),e);
            return false;
        }
    }

}
