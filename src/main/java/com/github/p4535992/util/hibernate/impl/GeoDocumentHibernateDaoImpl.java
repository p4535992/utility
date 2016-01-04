package com.github.p4535992.util.hibernate.impl;

import com.github.p4535992.util.hibernate.dao.IGeoDocumentHibernateDao;
import com.github.p4535992.util.hibernate.impl.generic.GenericHibernateDaoImpl;
import com.github.p4535992.util.object.GeoDocument;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 4535992 on 01/04/2015.
 */
@org.springframework.stereotype.Repository
@org.springframework.stereotype.Component("GeoDocumentHibernateDao")
public class GeoDocumentHibernateDaoImpl extends GenericHibernateDaoImpl<GeoDocument> implements IGeoDocumentHibernateDao {

    public GeoDocumentHibernateDaoImpl(){}
    public GeoDocumentHibernateDaoImpl(String s) throws FileNotFoundException {
        super(s);
        super.loadSpringContext(contextFile.getAbsolutePath());
    }

    @Override
    public void setContext(ApplicationContext context){
        super.setContextFile(context);
    }

    @Override
    public ApplicationContext getContext(){
        return super.getContextFile();
    }

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, typeDb, host, port, user, pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) {
       super.setDataSource(ds);
    }

  /*  @Override
    public List<GeoDocument> findByName(String name) {
        Object[] args = new Object[]{name};
        return null;
    }

    @Override
    public Iterator<GeoDocument> iterateByWeight(int weight) {
        return null;
    }*/

    @Override
    public GeoDocument getDao(String beanIdName) {
        if(context!=null){
            return (GeoDocument)context.getBean(beanIdName);
        }else{
            return null;
        }
    }

    @Override
    public void loadSpringContext(String filePathXml){
        super.contextFile = new File(filePathXml);
        super.loadSpringContext(filePathXml);
    }

    @Override
    public void setTableSelect(String nameOfTable){super.setTableSelect(nameOfTable);}

    @Override
    public void setTableInsert(String nameOfTable){
        super.setTableInsert(nameOfTable);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
       super.setSessionFactory(sessionFactory);
    }

    @Override
    public String getBeanIdSessionFactory() {
        return super.getBeanIdSessionFactory();
    }
    @Override
    public void setBeanIdSessionFactory(String beanIdSessionFactory) {
        super.setBeanIdSessionFactory(beanIdSessionFactory);
    }

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() {
        return super.getSessionFactory();
    }

    @Override
    public Session getSession() {
        return super.getSession();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void openSession() {

    }

    @Override
    public void closeSession() {

    }

    @Override
    public Session getCurrentSession() {
        return super.getCurrentSession();
    }

    @Override
    public void setNewCriteria(Criterion criterion) {
        super.setNewCriteria(criterion);
    }

    @Override
    public void setNewServiceRegistry() {
        super.setNewServiceRegistry();
    }

    @Override
    public Serializable insertRow(GeoDocument newInstance) {
        return super.insertRow(newInstance);
    }

    @Override
    public  GeoDocument selectRow(Serializable serial){
        return super.selectRow(serial);
    }

    @Override
    public List<GeoDocument> selectRows() {
        return super.selectRows();
    }


    @Override
    public List<GeoDocument> selectRows(String nameColumn, int limit, int offset) {
        return super.selectRows(nameColumn, limit, offset);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Serializable deleteRow(GeoDocument geo) {
        return super.deleteRow(geo);
    }

    @Override
    public void setInterceptor(Class<? extends Interceptor> interceptor){
        super.setInterceptor(interceptor);
    }







}
