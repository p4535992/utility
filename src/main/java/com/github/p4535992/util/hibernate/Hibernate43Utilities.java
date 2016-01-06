package com.github.p4535992.util.hibernate;

import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;
import org.hibernate.*;
import org.hibernate.InstantiationException;
import org.hibernate.criterion.Criterion;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Little Class for help with first steps to Hibernate
 * @author 4535992
 * @param <T> generic type super class.
 * @version 2015-09-15.
 */
@SuppressWarnings("unused")
public class Hibernate43Utilities<T> {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( Hibernate43Utilities.class);
    
    protected String myInsertTable,mySelectTable,myUpdateTable;
    protected SessionFactory sessionFactory;
    protected Session session;
    protected SessionBuilder sessionBuilder;
    protected org.hibernate.service.ServiceRegistry serviceRegistry;
    protected org.hibernate.cfg.Configuration configuration;
    protected File PATH_CFG_HIBERNATE;
    protected boolean cfgXML;
    protected Criteria criteria;
    protected Criteria specificCriteria;
    protected Transaction trns;

    protected Class<T> cl;
    protected String clName,sql;
    protected Class<? extends Interceptor> interceptorClass;
    protected Interceptor inter; //support parameter
    protected static Connection connection;
    //@PersistenceContext(unitName=UtilitiesModel.JPA_PERSISTENCE_UNIT)
    protected EntityManager entityManager;

    @SuppressWarnings({"unchecked","rawtypes"})
    public Hibernate43Utilities(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    public Hibernate43Utilities(Class<T> cl){
        this.cl = cl;
        this.clName = cl.getSimpleName();
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    public Hibernate43Utilities(Session session, SessionFactory sessionFactory){
        this.session = session;
        this.sessionFactory = sessionFactory;
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }



    /**
     * Method for pass a personal Criteria object to the CRUD operation of Hibernate
     * @param criterion criterion for the query hibernate.
     */
    public void setNewCriteria(Criterion criterion){
        specificCriteria = session.createCriteria(cl);
        specificCriteria.add(criterion);
    }

    /**
     * Method to set a interceptor to the current Session.
     * @param newSession the Session Hibernate to cretae.
     * @param newSessionFactory the SessionFactory to use.
     * @param interceptorClass the class interceptor you want to use.
     * @return the current session update with a interceptor class.
     */
    public Session createNewSessionWithInterceptor(
            Session newSession,SessionFactory newSessionFactory,Class<? extends Interceptor> interceptorClass){
        try {

            /**deprecated on hibernate 4  */
            //interceptor for global, set interceptor when create sessionFactory with Configure
            /*sessionFactory =
                    new AnnotationConfiguration().configure()
                            .setInterceptor((Interceptor) interceptor.newInstance())
                           .buildSessionFactory();*/
            /**deprecated on hibernate 4 */
            //interceptor for per Session
            /*Session session = sessionFactory.openSession(interceptor.newInstance());*/
            if(newSession == null && session == null){
                setNewSessionFactory();
            }
            //is work but is better use SessionBuilder
//            session = sessionFactory.withOptions().interceptor(
//                   (Interceptor) interceptor.newInstance()).openSession();
            this.inter = createInterceptor(interceptorClass);
            this.interceptorClass = interceptorClass;
            if(newSessionFactory == null && sessionFactory == null){
                throw new InstantiationException("The SessionFactory is NULL",Hibernate43Utilities.class);
            }else if(newSessionFactory != null && newSession != null){
                newSession = newSessionFactory.withOptions().interceptor(inter).openSession();
                ReflectionUtilities.invokeSetter(inter, "setSession", newSession, Session.class);
                ReflectionUtilities.invokeSetter(inter, "setSessionFactory", newSessionFactory, SessionFactory.class);
                return newSession;
            }else if(newSessionFactory != null){
                session = newSessionFactory.withOptions().interceptor(inter).openSession();
                ReflectionUtilities.invokeSetter(inter, "setSession", session, Session.class);
                ReflectionUtilities.invokeSetter(inter, "setSessionFactory", newSessionFactory, SessionFactory.class);
                return session;
            }else if(newSession != null){
                newSession = sessionFactory.withOptions().interceptor(inter).openSession();
                ReflectionUtilities.invokeSetter(inter, "setSession", newSession, Session.class);
                ReflectionUtilities.invokeSetter(inter, "setSessionFactory", sessionFactory, SessionFactory.class);
                return newSession;
            }else {
                session = sessionFactory.withOptions().interceptor(inter).openSession();
                ReflectionUtilities.invokeSetter(inter, "setSession", session, Session.class);
                ReflectionUtilities.invokeSetter(inter, "setSessionFactory", sessionFactory, SessionFactory.class);
                return session;
            }
            //session = sessionFactory.withOptions().interceptor(inter).openSession();
            //ReflectionUtilities.invokeSetter(inter, "setSession", session, Session.class);
            //ReflectionUtilities.invokeSetter(inter, "setSessionFactory", sessionFactory, SessionFactory.class);

            //session = (SessionImpl) ReflectionKit.invokeGetterClass(inter,"getSession");
            //sessionFactory = (SessionFactoryImpl) ReflectionKit.invokeGetterClass(inter,"getSessionFactory");
            //createSessionWithInterceptor(interceptor);

        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
       // return session;
    }

    public void setSessionWithInterceptor(Class<? extends Interceptor> interceptorClass){
        createNewSessionWithInterceptor(session,sessionFactory,interceptorClass);
    }

    public Session createNewSessionWithInterceptor(Class<? extends Interceptor> interceptorClass){
        return createNewSessionWithInterceptor(session,sessionFactory,interceptorClass);
    }

    public Interceptor createInterceptor(Class<? extends Interceptor> interceptorClass){
        try {
            return interceptorClass.newInstance();
        } catch (java.lang.InstantiationException|IllegalAccessException  e) {
           logger.error("Can't create the Interceptor Hibernate object with the class:"+interceptorClass.getSimpleName());
            return null;
        }
    }

    /***
     * Method for try into o many ways to set a configuration object from a file XML configuration.
     * This method is a overkill, is try to load the configuration file in many ways before give up
     * @param PATH_CFG_HIBERNATE the String path to the XML configuration file.
     * @param configuration the Configuration of Hibernate.
     * @param interceptorClass the Class Interceptor of Hibernate.
     * @return the new Configuration of Hibernate with a Interceptor.
     */
    public org.hibernate.cfg.Configuration createNewConfiguration(File PATH_CFG_HIBERNATE,
            org.hibernate.cfg.Configuration configuration,
            Class<? extends Interceptor> interceptorClass){
        //configuration = new Configuration();
        //URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(PATH_CFG_HIBERNATE.getAbsolutePath());
        //configuration.configure(urlStatic);
        logger.info("Try to set a new configuration...");
        if(PATH_CFG_HIBERNATE.exists()) {
            org.hibernate.cfg.Configuration config =  new org.hibernate.cfg.Configuration();

            if(interceptorClass != null){
                if(session != null){
                    setSessionWithInterceptor(interceptorClass);
                }
                else{
                    Session localSession = createNewSessionWithInterceptor(interceptorClass);
                    config = createNewConfiguration(interceptorClass);
                }
            }
            Interceptor interceptor = createInterceptor(interceptorClass);
            config = config.setInterceptor(interceptor);

            try {
                //You can put the configuration file where you want but you must pay attention
                //where you put the java class with jpa annotation
                //Web-project -> WEB-INF/pojo.hbm.xml
                //Maven-project -> resources/pojo.hbm.xml ->
                //THis piece of code can be better
                configuration = config;
                configuration = config.configure(PATH_CFG_HIBERNATE);
            }catch(Exception ex6){
                try {
                    configuration = config;
                    configuration = config.configure(PATH_CFG_HIBERNATE.getAbsolutePath()); //work on Netbeans
                } catch (HibernateException ex) {
                    try {
                        configuration = config;
                        configuration = config.configure(PATH_CFG_HIBERNATE.getCanonicalPath());
                    } catch (IOException | HibernateException ex3) {
                        try {
                            configuration = config;
                            configuration = config.configure(PATH_CFG_HIBERNATE.getPath());
                        } catch (HibernateException ex4) {
                            try {
                                configuration = config;
                                configuration = config.configure(PATH_CFG_HIBERNATE.getAbsoluteFile());
                            }catch(HibernateException e){
                                logger.warn("...failed to load the configuration file to the path:" +
                                        PATH_CFG_HIBERNATE.getAbsolutePath());
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }else{
            try {
                /**deprecated in Hibernate 4.3*/
                //configuration = new AnnotationConfiguration();
                configuration = configuration.configure();
            }catch(HibernateException e){
                logger.error( e.getMessage(), e);
            }
        }
        return configuration;
    }

    /***
     * Method for try into o many ways to set a configuration object from a file XML configuration.
     * This method is a overkill, is try to load the configuration file in many ways before give up
     * @return the new Configuration Hibernate.
     */
    public org.hibernate.cfg.Configuration createNewConfiguration(){
       return createNewConfiguration(null,configuration,interceptorClass);
    }

    public void setNewConfiguration(){
        createNewConfiguration(null,configuration,interceptorClass);
    }

    public org.hibernate.cfg.Configuration createNewConfiguration(org.hibernate.cfg.Configuration  configuration){
        return createNewConfiguration(null,configuration,null);
    }

    public org.hibernate.cfg.Configuration createNewConfiguration(Class<? extends Interceptor> interceptorClass){
        return createNewConfiguration(null,configuration,interceptorClass);
    }

    public org.hibernate.cfg.Configuration createNewConfiguration(
            org.hibernate.cfg.Configuration  configuration,Class<? extends Interceptor> interceptorClass){
        return createNewConfiguration(null,configuration,interceptorClass);
    }

    /**
     * Set the Service Registry.
     * @param configuration the new Configuration Hibernate to registered.
     */
    public void setNewServiceRegistry(org.hibernate.cfg.Configuration configuration) {
        /**deprecated in Hibernate 4.3*/
        //serviceRegistry = new ServiceRegistryBuilder().applySettings(
        //        configuration.getProperties()). buildServiceRegistry();
        if(configuration != null) {
            serviceRegistry = new org.hibernate.boot.registry.StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
        }else{
           logger.warn("Try to set a ServiceRegistry without have configurate the Configuration");
        }
    }

    public void setNewServiceRegistry() {
        setNewServiceRegistry(configuration);
    }

    /**
     * Get the ServiceRegistry
     * @return serviceRegistry the new service registry for hibernate 4 configuration.
     */
    public org.hibernate.service.ServiceRegistry getServiceRegistry(){
        return serviceRegistry;
    }

    /**
     * Method to Get the mapping of the selected class.
     * @param entityClass class of the entity.
     * @return the class mapping of the class for hibernate 4 configuration.
     */
    public org.hibernate.mapping.PersistentClass getClassMapping(Class<?> entityClass){
        return configuration.getClassMapping(entityClass.getName());
    }

    /**
     * Method to Get the  SessionFactory.
     * @return sessionFactory the new sessionfactory.
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory( EntityManager entityManager){
//        EntityManagerFactory entityManagerFactory =
//                javax.persistence.Persistence.createEntityManagerFactory("YOUR PU");
        this.sessionFactory=  entityManager.unwrap(SessionFactory.class);
        //org.hibernate.Session session = sessionFactory.withOptions().interceptor(new MyInterceptor()).openSession();
    }

    /**
     * Method to set the current SessionFactory from external resource.
     * @param sessionFactory the externalSessionFactory.
     */
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Method to create a new SessionFactory from internal resource.
     */
    public void setNewSessionFactory(){
        buildSessionFactoryBase();
    }

    /**
     * Method to create a new SessionFactory from internal resource.
     * @param configXMLFile the File XML of configuration of Hibernate.
     */
    public void setNewSessionFactory(File configXMLFile){
        buildSessionFactory(configXMLFile);
    }

    /**
     * Method to Get the Session.
     * @return the actual session..
     */
    public Session getSession(){
        return session;
    }

    /**
     * Method to Set the Session from external resource.
     * @param session set the new session for hibernate.
     */
    public void setSession(Session session){
        this.session = session;
    }

    /**
     * Method to Set the Session from external resource.
     * @param entityManager object entitymaanager can use to set a new Session.
     */
    public void setSession(EntityManager entityManager){
        session = entityManager.unwrap(Session.class);
    }

    public  void setNewEntityManager(){
        javax.persistence.EntityManagerFactory entityManagerFactory =
                javax.persistence.Persistence.createEntityManagerFactory("JavaStackOver");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public Connection getConnection(Session session) {
        try {
            if(entityManager != null) {
                session = (Session) entityManager.getDelegate();
                org.hibernate.internal.SessionFactoryImpl sessionFactoryImpl =
                        (org.hibernate.internal.SessionFactoryImpl) session.getSessionFactory();
                connection = sessionFactoryImpl.getConnectionProvider().getConnection();
            }else if(session != null){
                session.doWork(new org.hibernate.jdbc.Work() {
                    @Transactional
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        //connection, finally!, note: you need a trasnaction
                        //if you are using Spring, @Transactional or TransactionTemplate is enough
                        Hibernate43Utilities.setNewConnection(connection);
                    }

                });
            }

        }catch(SQLException e){logger.error( e.getMessage(), e);}
        return connection;
    }

    private static void setNewConnection(Connection conn){
        connection = conn;
    }

    public Session getSession(Connection connection){
        //SessionBuilder sb = SessionFactory.withOptions();
        //sessionBuilder = SessionFactory.SessionFactoryOptions;
        //this.session = sessionBuilder.connection(connection).openSession();
        return session;
    }

    /**
     * Method to Close caches and connection pool.
     */
    public void shutdown() {
        logger.info("try to closing session ... ");
        if (getCurrentSession() != null) {
            getCurrentSession().flush();
            session.flush();
            if (session.isOpen()) {
                session.close();
                getCurrentSession().close();
            }
        }
        logger.info("...session is closed! try to close the SessionFactory ... ");
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        logger.info("... the SessionFactory is closed!");
        this.configuration = null;
        this.sessionFactory = null;
        this.session = null;
    }

    /**
     * Method for suppor the reset of specific parameter of the class
     */
    protected void reset(){
       /* if (getCurrentSession() != null) {
            getCurrentSession().flush();
            if (getCurrentSession().isOpen()) {
                getCurrentSession().close();
            }
        }*/
        if (session != null) {
            session.flush();
            if(session.isOpen()) {
                session.close();
            }
        }
        criteria = null;
        specificCriteria = null;
    }

    /**
     * Method Opens a session and will not bind it to a session context
     */
    public void openSession() {
        if(inter != null || interceptorClass !=null){
            //...avoid the reset of the interceptor
            if(!session.isOpen()){
                throw new HibernateException("The session loaded with the interceptor is not open!!");
            }
        }else {
            if(!session.isOpen()){session = sessionFactory.openSession();}
        }
    }

    /** Close a Session*/
    public void closeSession() {
        session.close();
    }

    /**Close and Open a Session*/
    public void restartSession() {
        openSession();
        closeSession();
    }

    /**
     * Returns a session from the session context. If there is no session in the context it opens a session,
     * stores it in the context and returns it.
     * This context is intended to be used with a hibernate.cfg.xml including the following property
     * property name="current_session_context_class" thread /property
     * This would return the current open session or if this does not exist, will insert a new session.
     * @return the session.
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Method to build the sessionFactory for Hibernate froma configuration file or from
     * a java code.
     */
    private void buildSessionFactoryBase(File PATH_CFG_HIBERNATE) {
        try {
            logger.info("Try to build a Hibernate SessionFactory with config file:"
                    +PATH_CFG_HIBERNATE.getAbsolutePath()+"...");
            if(PATH_CFG_HIBERNATE.exists()) {
                cfgXML = true;
                if(configuration==null) createNewConfiguration();//new Configuration
                if(serviceRegistry == null)setNewServiceRegistry(); //new ServiceRegistry
                /**deprecated Hibernate 4.0, 4.1, 4.2*/
                //sessionFactory = configuration.configure().buildSessionFactory();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            }else{
                setNewConfiguration();//new Configuration
                setNewServiceRegistry(); //new ServiceRegistry
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            }
            logger.info("... finish to build the SessionFactory.");
        } catch (Exception e) {
            logger.warn("Initial SessionFactory creation failed:"+e.getMessage(), e);
        }
    }

    private void buildSessionFactoryBase() {
        buildSessionFactoryBase(PATH_CFG_HIBERNATE);
    }

    /**
     * Method to build a Session Factory on a remote configuration file XML
     * @param uri url urito the configuration file.
     * note NOT WORK NEED UPDATE.
     */
    public void buildSessionFactory(URL uri) {
        try {
            logger.info("Try to build a Hibernate SessionFactory with config file:"
                    +uri+"...");
            if(uri != null){
                //URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(cfgFile.getAbsolutePath());
                buildSessionFactory(new File(uri.toString()));
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Method to build a Session Factory on a local configuration file XML
     * @param filePath string file to the configuration file.
     */
     public void buildSessionFactory(String filePath) {
         try{
             logger.info("Try to build a Hibernate SessionFactory with config file:"
                     +filePath+"...");
            if(StringUtilities.isNullOrEmpty(filePath)){
               File cfgFile = new File(filePath);                   
               if(cfgFile.exists()){
                   buildSessionFactory(cfgFile);
               }
            }else{
                throw new HibernateError("The string path to the file in input is null or empty");
            }
         }catch(HibernateError e){
             logger.error(e.getMessage(), e);
         }
    }

    /**
     * Method to build a Session Factory on a local configuration file XML
     * @param cfgFile file hibernate configuration.
     */
    public void buildSessionFactory(File cfgFile) {
        try {
            logger.info("Try to build a Hibernate SessionFactory with config file:"
                    +cfgFile.getAbsolutePath()+"...");
            if(cfgFile.exists()){
                buildSessionFactoryBase(cfgFile);
            }else{
                logger.error("The file configuration for hibernate:"+cfgFile+" not exists.");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Method to build a Session Factory with code and the annotation class
     * @param DB_OUTPUT_HIBERNATE name database
     * @param USER_HIBERNATE user name database
     * @param PASS_HIBERNATE password database
     * @param DRIVER_DATABASE driver for database
     * @param DIALECT_DATABASE dialect for database
     * @param DIALECT_DATABASE_HIBERNATE specific HSQL dialect for database
     * @param HOST_DATABASE host database
     * @param PORT_DATABASE port database
     * @param LIST_ANNOTATED_CLASS list of all annotated classes
     */
    public void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,String USER_HIBERNATE,String PASS_HIBERNATE,String DRIVER_DATABASE,
            String DIALECT_DATABASE,String DIALECT_DATABASE_HIBERNATE,String HOST_DATABASE,String PORT_DATABASE,
            List<Class<?>> LIST_ANNOTATED_CLASS
    ) {
        logger.info("Try to build a Hibernate SessionFactory with config code embedded...");
        cfgXML = false;
        setNewConfiguration();
        try {
            configuration
            //DATABASE PARAMETER
            .setProperty("hibernate.dialect", DIALECT_DATABASE_HIBERNATE)
            .setProperty("hibernate.connection.driver_class", DRIVER_DATABASE)
            .setProperty("hibernate.connection.url", "" + DIALECT_DATABASE + "://" + HOST_DATABASE + ":" + PORT_DATABASE + "/" + DB_OUTPUT_HIBERNATE + "")
            .setProperty("hibernate.connection.username", USER_HIBERNATE)
            .setProperty("hibernate.connection.password", PASS_HIBERNATE)

            //DEFAULT PARAMETER
            //.setProperty("connection.pool_size", "1")
            //.setProperty("current_session_context_class", "thread")
            //.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
            .setProperty("hibernate.show_sql", "true")

            //.setProperty("hibernate.hbm2ddl.auto","update")
            //.setProperty("hibernate.format_sql","true")
            //.setProperty("hibernate.hbm2ddl.auto","insert-drop")

            //OTHER PROPERTIES
            /*
            .setProperty("hibernate.c3p0.acquire_increment","1")
            .setProperty("hibernate.c3p0.idle_test_period","100")
            .setProperty("hibernate.c3p0.maxIdleTime","300")
            .setProperty("hibernate.c3p0.max_size","75")
            .setProperty("hibernate.c3p0.max_statements","0")
            .setProperty("hibernate.c3p0.min_size","20")
            .setProperty("hibernate.c3p0.timeout","180")
            .setProperty("hibernate.cache.user_query_cache","true")
            */
            ;
            //ADD ANNOTATED CLASS
            for (Class<?> cls : LIST_ANNOTATED_CLASS) {
                configuration.addAnnotatedClass(cls);
            }
            buildSessionFactoryBase();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }//buildSessionFactory


    /**
     * Method to build a Session Factory with code and the annotation class
     * @param DB_OUTPUT_HIBERNATE name database
     * @param USER_HIBERNATE user name database
     * @param PASS_HIBERNATE password database
     * @param DRIVER_DATABASE driver for database
     * @param DIALECT_DATABASE dialect for database
     * @param DIALECT_DATABASE_HIBERNATE specific HSQL dialect for database
     * @param HOST_DATABASE host database
     * @param PORT_DATABASE port database
     * @param LIST_ANNOTATED_CLASS list of all annotated classes
     * @param LIST_RESOURCE_XML list of the XML file resource
     */
    public void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,String USER_HIBERNATE,String PASS_HIBERNATE,String DRIVER_DATABASE,
            String DIALECT_DATABASE,String DIALECT_DATABASE_HIBERNATE, String HOST_DATABASE,String PORT_DATABASE,
            List<Class<?>> LIST_ANNOTATED_CLASS,List<File> LIST_RESOURCE_XML
    ){
        logger.info("Try to build a Hibernate SessionFactory with config code embedded...");
        cfgXML = false;
        setNewConfiguration();
        try {
            buildSessionFactory(DB_OUTPUT_HIBERNATE,USER_HIBERNATE,PASS_HIBERNATE,DRIVER_DATABASE,
                    DIALECT_DATABASE,DIALECT_DATABASE_HIBERNATE,HOST_DATABASE,PORT_DATABASE,
                   LIST_ANNOTATED_CLASS);
            //Specifying the mapping files directly
            for(File resource : LIST_RESOURCE_XML){
                configuration.addResource(resource.getAbsolutePath());
            }
            buildSessionFactoryBase();
        }catch (Throwable e) {
            logger.error( e.getMessage(), e);
        }
    }//buildSessionFactory

    ///////////////////////////////////////////////////////////////////////////////////////////7
    ////////////////////////////////////////////////////////////////////////////////////////////

    /*public  <T> Serializable insertRow(T object){return null; }
    public <T> T selectRow(Serializable id){return null;}
    public List<T> selectRows() {return null;}
    public List<T> selectRows(String nameColumn,int limit,int offset){return null;}
    public int getCount() {return 0;}
    public Serializable updateRow(String whereColumn, Object whereValue) {return null;}
    public Serializable updateRow(T object) {return null;}
    public Serializable deleteRow(String whereColumn, Object whereValue) {return null;}
    public Serializable deleteRow(T object) {return null;};*/

    @Transactional
    public <T> Serializable insertRow(T object) {
        Serializable id = null;
        try {
            openSession();
            trns = session.beginTransaction();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            logger.info("Insert the item:" + object);
            id = session.getIdentifier(object);
            logger.info("Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    public  T selectRow(Serializable id){
        T object = null;
        try {
            openSession();
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            //WORK
            try {
                criteria.add(org.hibernate.criterion.Restrictions.eq("doc_id", id));
                List<T> results = criteria.list();
                logger.info("Select the item:" + results.get(0));
            }catch(Exception e) {
                logger.warn( e.getMessage(), e);
                if (trns != null) { trns.rollback();}
                //retry for specific exception....
                if(e.getMessage().contains("java.net.MalformedURLException: no protocol")){
                    String url = StringUtilities.findWithRegex(e.getMessage(), "\\[(.*?)\\]").replace("[","").replace("]","");
                    updateRow("url","http://"+url,"doc_id", id);
                }
            }
            //NOT WORK
            //object = (T) criteria.setFirstResult((Integer) id);
            //SystemLog.message("[HIBERNATE] Select the item:" + object.toString());
            object = (T) session.load(cl, id);
            logger.info("GeoDocument you find:" + object.toString());
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return object;
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public List<T> selectRows() {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            trns = session.beginTransaction();
            if(specificCriteria==null){
                criteria = session.createCriteria(cl);
            }else{
                criteria =specificCriteria;
            }
            listT =  criteria.list();
            if(listT.isEmpty()){
                logger.warn("The returned list is empty!1");
            }
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return listT;
    }



    @SuppressWarnings("unchecked")
    @Transactional
    public List<T> selectRows(String nameColumn,int limit,int offset) {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            sql = "SELECT "+nameColumn+" FROM "+mySelectTable+"";
            org.hibernate.SQLQuery SQLQuery = session.createSQLQuery(sql);
            Query query = SQLQuery;
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            listT = query.list();
            if(listT.isEmpty()){
                logger.warn("The returned list is empty!1");
            }
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return listT;
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public int getCount() {
        Object result = null;
        try {
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.setProjection(org.hibernate.criterion.Projections.rowCount());
            result = criteria.uniqueResult();
            logger.info("The count of employees is :" + result);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return (int)result;
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public Serializable updateRow(String nameColumn,Object newValue,String whereColumn, Object whereValue) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            /*
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            //t.setName("Abigale");
            //t = object;
            session.saveOrUpdate(t);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Update the item:" + t.toString());
            id = session.getIdentifier(t);
            SystemLog.message("[HIBERNATE] Get the identifier:" + id);
            */
            String hqlUpdate = "update "+getTableNameFromSessionFactory(sessionFactory,cl)+" set " + nameColumn +
                    " = '"+ newValue +"' where " + whereColumn + " = "+whereValue+"";
            int updatedEntities = session.createQuery( hqlUpdate ).executeUpdate();
            session.getTransaction().commit();
            logger.info("Update the item:" + cl.getSimpleName());
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public Serializable updateRow(T object) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
            logger.info("Update the item:" + object.toString());
            id = session.getIdentifier(object);
            logger.info("Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Serializable deleteRow(String whereColumn, Object whereValue) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            session.delete(t);
            session.getTransaction().commit();
            logger.info("Delete the item:" + t);
            id = session.getIdentifier(t);
            logger.info("Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public Serializable deleteRow(T object) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
            logger.info("Delete the item:" + object);
            id = session.getIdentifier(object);
            logger.info("Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error( e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }

    //NEW FUNCTION
    public String getTableNameFromSessionFactory(SessionFactory sessionFactory,Class<?> thisClass){
        org.hibernate.metadata.ClassMetadata hibernateMetadata = sessionFactory.getClassMetadata(thisClass);
        if (hibernateMetadata == null){
            return null;
        }
        if (hibernateMetadata instanceof org.hibernate.persister.entity.AbstractEntityPersister){
            org.hibernate.persister.entity.AbstractEntityPersister persister =
                    (org.hibernate.persister.entity.AbstractEntityPersister) hibernateMetadata;
            //String tableName = persister.getTableName();
            return persister.getTableName();
        }
        return null;
    }

    public String[] getColumnsNamesFromSessionFactory(SessionFactory sessionFactory,Class<?> thisClass){
        org.hibernate.metadata.ClassMetadata hibernateMetadata = sessionFactory.getClassMetadata(thisClass);
        if (hibernateMetadata == null){
            return null;
        }
        if (hibernateMetadata instanceof org.hibernate.persister.entity.AbstractEntityPersister){
            org.hibernate.persister.entity.AbstractEntityPersister persister =
                    (org.hibernate.persister.entity.AbstractEntityPersister) hibernateMetadata;
            //String[] columnNames = persister.getKeyColumnNames();
            return persister.getKeyColumnNames();
        }
        return null;
    }

    public void updateAnnotationEntity(
            String nameOfAttribute, String newValueAttribute,Class<?> clazzToInspect) {
        ReflectionUtilities.updateAnnotationClassValue(
                clazzToInspect, javax.persistence.Entity.class, nameOfAttribute, newValueAttribute);
    }

    public void updateAnnotationTable(
            String nameOfAttribute, String newValueAttribute,Class<?> clazzToInspect){
        ReflectionUtilities.updateAnnotationClassValue(
                clazzToInspect, javax.persistence.Table.class, nameOfAttribute, newValueAttribute);
    }

    public void updateAnnotationColumn(
            String nameField, String nameOfAttribute, String newValueAttribute,Class<?> clazzToInspect){
        ReflectionUtilities.updateAnnotationFieldValue(
                clazzToInspect, javax.persistence.Column.class, nameField, nameOfAttribute, newValueAttribute);
    }

    public void updateAnnotationJoinColumn(
            String nameField, String nameOfAttribute, String newValueAttribute,Class<?> clazzToInspect){
        ReflectionUtilities.updateAnnotationFieldValue(
                clazzToInspect, javax.persistence.JoinColumn.class, nameField, nameOfAttribute, newValueAttribute);
    }

    public List<Object[]> getAnnotationTable(Class<?> clazzToInspect) {
        Annotation ann = clazzToInspect.getAnnotation(javax.persistence.Table.class);
        return (List<Object[]>) ReflectionUtilities.findInfoAnnotationClass(ann);
    }
}//end of the class

