package com.github.p4535992.util.hibernate.impl.generic;

import com.github.p4535992.util.bean.BeansKit;
import com.github.p4535992.util.hibernate.Hibernate43Utilities;
import com.github.p4535992.util.hibernate.dao.generic.IGenericHibernateDao;
import com.github.p4535992.util.string.StringUtilities;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 23/04/2015.
 * @version 2015-09-15.
 */
@SuppressWarnings("unused")
public class GenericHibernateDaoImpl<T> implements IGenericHibernateDao<T> {

    //BASIC FIELD
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenericHibernateDaoImpl.class);

    protected Class<T> cl;
    protected String clName;
    protected String query;
    //HIBERNATE FIELD

    protected String myInsertTable,mySelectTable;
    protected DataSource dataSource;
    @Autowired
    protected SessionFactory sessionFactory;
    protected Session session;
    protected Criterion criterion;
    protected boolean newServiceRegistry = false;


    //SPRING FIELD
    protected org.springframework.orm.hibernate4.HibernateTemplate hibernateTemplate;
    protected org.springframework.orm.hibernate4.LocalSessionFactoryBuilder sessionBuilder;
    protected org.springframework.orm.hibernate4.SessionHolder sessionHolder;
    protected DriverManagerDataSource driverManag;
    @PersistenceContext
    protected EntityManager em;
    protected ApplicationContext context;
    protected String beanIdSessionFactory;
    protected String beanIdSpringContext;

    protected Hibernate43Utilities<T> hbs;
    protected File contextFile;

    //CONSTRUCTOR
    @SuppressWarnings({"unchecked","rawtypes"})
    public GenericHibernateDaoImpl() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
        this.hbs = new Hibernate43Utilities(cl);
    }
   @SuppressWarnings({"unchecked","rawtypes"})
   public GenericHibernateDaoImpl(Object s) throws FileNotFoundException {
       //super(s); //extend test case????
       this.hbs = new Hibernate43Utilities(cl);
       if(context==null){
           loadSpringContext(contextFile.getAbsolutePath());
       }else{
           //..do nothing
           logger.error("Can't load a Sprinc context because the File context is NULL.");
       }

   }

    //GETTER AND SETTER BASE
   public String getBeanIdSpringContext() {
        return beanIdSpringContext;
   }

   public void setBeanIdSpringContext(String beanIdSpringContext) {
       this.beanIdSpringContext = beanIdSpringContext;
   }

   public String getBeanIdSessionFactory() {
       return beanIdSessionFactory;
   }

   public void setBeanIdSessionFactory(String beanIdSessionFactory) {
        this.beanIdSessionFactory = beanIdSessionFactory;
   }

   public ApplicationContext getContextFile() {
        return context;
   }

   public void setContextFile(ApplicationContext context) {
        this.context = context;
   }

   public void setTableInsert(String nameOfTable) {this.myInsertTable= nameOfTable;}

   public void setTableSelect(String nameOfTable) {this.mySelectTable= nameOfTable;}

   @Override
   public Session getSession() {
       // session = getCurrentSession();
        session =  sessionFactory.getCurrentSession();
        return session;
   }

   @Override
   public void setSession(Session session) {
        this.session =  session;
   }

    //@Override
   public void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.sql.jdbc.Driver"
        driverManag.setUrl("" + typeDb + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.dataSource = driverManag;
   }

   // @Override
   public void setDataSource(DataSource ds) {
       this.dataSource= ds;
   }

    //@Override
   public void setHibernateTemplate(
           org.springframework.orm.hibernate4.HibernateTemplate hibernateTemplate) {
       this.hibernateTemplate = hibernateTemplate;
   }

   // @Override
   public void setNewHibernateTemplate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new org.springframework.orm.hibernate4.HibernateTemplate(getSessionFactory());
   }

   //@Override
   public void loadSpringContext(String filePathXml){
        try {
            context = BeansKit.tryGetContextSpring(filePathXml,GenericHibernateDaoImpl.class);
            if(context!=null){
                setSessionFactory(context);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
   }

    @Override
    public SessionFactory getSessionFactory() {
        //if(context==null && StringKit.setNullForEmptyString(beanIdSessionFactory) == null){
            return this.sessionFactory;
        //}else{
            //return (SessionFactory) context.getBean("sessionFactory");
        //    return (org.hibernate.SessionFactory) context.getBean(beanIdSessionFactory);
        //}
    }
    /**
     * Method needed for the configuration bean file (context file)
     */
    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
    }

    //@Override
    public void setSessionFactory(ApplicationContext context) {
        try {
            if (StringUtilities.isNullOrEmpty(beanIdSessionFactory)) {
                throw new NullPointerException("The id for the sessionFactory must be not null," +
                        " invoke the method setBeanIdSessionFactory(\"beanIdSesssionFactory\" before invoke " +
                        "the loadSpringContextMethod");
            }
            this.sessionFactory = (SessionFactory) context.getBean(beanIdSessionFactory);
        }catch(NullPointerException e){
            logger.error(e.getMessage(),e);
        }
    }

    //@Override
    public void setSessionFactory(DataSource dataSource) {
        sessionBuilder = new org.springframework.orm.hibernate4.LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(cl);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }


    //@Override
    public DataSource getDataSource() {
        if(dataSource==null) {
            return org.springframework.orm.hibernate4.SessionFactoryUtils.getDataSource(sessionFactory);
        }else {
            return this.dataSource;
        }
    }


    @Override
    public void shutdown(){
        closeSession();
    }

    @Override
    public void openSession(){
        Session session = getSessionFactory().openSession();
        org.springframework.transaction.support.TransactionSynchronizationManager.bindResource(sessionFactory,
                new org.springframework.orm.hibernate4.SessionHolder(session));
    }

    @Override
    public void closeSession()
    {
        org.springframework.orm.hibernate4.SessionHolder sessionHolder =
                (org.springframework.orm.hibernate4.SessionHolder)
                        org.springframework.transaction.support.TransactionSynchronizationManager.unbindResource(sessionFactory);
        sessionHolder.getSession().flush();
        sessionHolder.getSession().close();
        //SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
        org.springframework.orm.hibernate4.SessionFactoryUtils.closeSession(sessionHolder.getSession());
    }

    @Override
    public void restartSession() {
        openSession();
        closeSession();
    }

    @Override
    public  Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void setNewCriteria(Criterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public void setNewServiceRegistry() {
        this.newServiceRegistry = true;
    }

    //METHOD FOR CRUD OPERATION WITH HIBERNATE
    @Override
    public Serializable insertRow(T object) {
        if(hibernateTemplate!=null) {
            hibernateTemplate.save(object);
            return null;
        }else if(sessionFactory !=null){
            doInHibernate();
            hbs.setSessionFactory(sessionFactory);
            return hbs.insertRow(object);
        }else{
            //...??????
            return null;
        }
    }

    @Override
    public List<T> selectRows(String nameColumn, int limit, int offset) {

        return null;
    }

    @Override
     public Serializable updateRow(T object) {
        if(hibernateTemplate!=null) {
           hibernateTemplate.update(object);
        }else if(session !=null){
          session.update(object);
        }
        return null;
     }

    @Override
    public Serializable updateRow(String whereColumn, Object whereValue) {
        return null;
    }

    @Override
    public Serializable deleteRow(T object) {

        hibernateTemplate.delete(object);
        return null;
    }

    @Override
    public Serializable deleteRow(String whereColumn, Object whereValue) {
        return null;
    }

    @Override
    public List<T> selectRows() {
        List<T> list = new ArrayList<>();
        if(hibernateTemplate!=null) {
            list = hibernateTemplate.loadAll(cl);
        }
        return list;
    }

    @Override  
    public T selectRow(Serializable serial){
        setNewHibernateTemplate(sessionFactory);
        doInHibernate();
        return hbs.selectRow(serial);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void setInterceptor(Class<? extends Interceptor> interceptor){
        doInHibernate();
        hbs.createNewSessionWithInterceptor(interceptor);
    }

    //SUPPORT
    /**
     * Method for prepare the Dao Implement generic class to work with
     * standard Hibernate operational kit.
     * Attention: you can just use the hibernate template with a {@code new HibernateCallback()}
     * inner class.
     */
    private void  doInHibernate(){
        if(hbs.getSession()==null) {
            session = getCurrentSession();
            hbs.setSession(session);
        }
        if(hbs.getSessionFactory()==null) {
            hbs.setSessionFactory(sessionFactory);
        }
        if(criterion!=null)hbs.setNewCriteria(criterion);
        if(newServiceRegistry){
            hbs.setNewServiceRegistry();
            newServiceRegistry = false;
        }
    }


//    @Override
//    public List selectAllSimpleH(final String limit,final String offset) {
//        List<GeoDocument> docs = new ArrayList<GeoDocument>();
//        //METHOD 1 (Boring)
//        /*
//        Query q = getHibernateTemplate().getSession().createQuery("from User");
//        q.setFirstResult(0); // modify this to adjust paging
//        q.setMaxResults(limit);
//        return (List<User>) q.list();
//        */
//        if(limit != null && offset != null) {
//            //METHOD 2 (Probably the best)
//            docs =
//                    (List<GeoDocument>) hibernateTemplate.execute(new HibernateCallback() {
//                        public Object doInHibernate(Session session) throws HibernateException {
//                            Query query = session.createQuery("from " + mySelectTable + "");
//                            query.setFirstResult(Integer.parseInt(offset));
//                            query.setMaxResults(Integer.parseInt(limit));
//                            return (List<GeoDocument>) query.list();
//                        }
//                    });
//        }
//        return docs;
//    }


//
//    //method to return all
//    @Override
//    @Transactional
//    public List<T>  selectAllH(){
//        @SuppressWarnings("unchecked")
//        List<T> list = (List<T>) sessionFactory.getCurrentSession()
//        .createCriteria(GeoDocument.class)
//        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
//        return list;
//    }


    // If you'd like to use HibernateTemplate you can do something like this:

    /*
    @SuppressWarnings("unchecked")
    public List<T> get(final int limit) {
        return hibernateTemplate.executeFind(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                return session.createCriteria(cl).setMaxResults(limit).list();
            }
        });
    }
    */

    /*
    @SuppressWarnings("unchecked")
    public List<T> find(final int limit) {
        return hibernateTemplate.executeFind(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                return session.createQuery("FROM "+mySelectTable+"").setMaxResults(limit).list();
            }
        });
    }
    */




}

