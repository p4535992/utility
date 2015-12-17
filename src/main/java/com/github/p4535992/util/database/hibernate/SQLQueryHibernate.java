package com.github.p4535992.util.database.hibernate;

import com.github.p4535992.util.calendar.DateKit;
import org.hibernate.Query;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.AbstractQueryImpl;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4535992 on 14/09/2015.
 * @author 4535992.
 * @version 2015-09-30.
 */
@SuppressWarnings("unused")
public class SQLQueryHibernate {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SQLQueryHibernate.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    /**
     * Method to convert a Query Hibernate Object to a String.
     * @param query the Query Hibernate Object.
     * @return the String of the Query.
     */
    private static String convertQueryHibernateToString(Query query){
        CriteriaImpl c = (CriteriaImpl)query;
        SessionImpl s = (SessionImpl)c.getSession();
        SessionFactoryImplementor factory = s.getSessionFactory();
        String[] implementors = factory.getImplementors( c.getEntityOrClassName() );
        CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable)factory.getEntityPersister(implementors[0]),
                factory, c, implementors[0], (LoadQueryInfluencers) s.getEnabledFilters());
        Field f;
        String sql = null;
        try {
            f = OuterJoinLoader.class.getDeclaredField("sql");
            f.setAccessible(true);
            sql = (String)f.get(loader);
        } catch (NoSuchFieldException|IllegalAccessException e) {
            logger.error(gm() + e.getMessage(),e);
        }
        return sql;
    }


    /**
     * Method to extract parameters from Hibernate query.
     * @param obj the Object for the Hibernate Query.
     * @return a Map of Parameters Key-Value of the Hibernate Query.
     */
    @SuppressWarnings({"unchecked","rawtype"})
    public static Map<String,String> getParameters(Object obj) {
        Map<String,String> parameters = new HashMap<>();
        Object query;
        if (obj instanceof AbstractQueryImpl) {
            query = obj;
        } else {
            return parameters;
        }
        try {
            AbstractQueryImpl q = (AbstractQueryImpl) query;
            Field f = AbstractQueryImpl.class.getDeclaredField("namedParameters");
            f.setAccessible(true);
            Map<String,Object> namedParameters = (Map<String, Object>) f.get(q);
            for (Map.Entry<String, Object> stringStringEntry : namedParameters.entrySet()) {
                //Map.Entry entry = (Map.Entry<String, Object>) stringStringEntry;
                //String name = (String) entry.getKey();
                String name = stringStringEntry.getKey();
                TypedValue value = (TypedValue) stringStringEntry.getValue();
                Object o = value.getValue();
                String valueStr;
                if (o instanceof Calendar) {
                    valueStr = DateKit.printCal((Calendar) o);
                } else {
                    valueStr = o.toString();
                }
                parameters.put(name, valueStr);
            }
            f = AbstractQueryImpl.class.getDeclaredField("namedParameterLists");
            f.setAccessible(true);
            namedParameters = (Map<String, Object>) f.get(q);
            for (Map.Entry<String, Object> stringStringEntry : namedParameters.entrySet()) {
                Map.Entry<String,Object> entry = (Map.Entry) stringStringEntry;
                //String name = (String) entry.getKey();
                String name = entry.getKey();
                TypedValue value = (TypedValue) entry.getValue();
                Object o = value.getValue();
                String valueStr;
                if (o instanceof Calendar) {
                    valueStr = DateKit.printCal((Calendar) o);
                } else {
                    valueStr = o.toString();
                }
                if (valueStr.length() > 500) {
                    valueStr = valueStr.substring(0, 500) + "...";
                }
                parameters.put(name, valueStr);
            }
        } catch (NoSuchFieldException | SecurityException | 
                IllegalArgumentException | IllegalAccessException e) {
            logger.error(gm() + e.getMessage(),e);
            /*if (logger.isDebugEnabled()) {
                logger.debug("Error intercepting query parameters", t);
            }*/
        }
        return parameters;
    }
}
