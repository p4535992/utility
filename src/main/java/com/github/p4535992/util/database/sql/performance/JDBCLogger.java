package com.github.p4535992.util.database.sql.performance;

import java.util.*;

public class JDBCLogger
{
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JDBCLogger.class);
    
    private static Hashtable<Thread,Long> QueryTime = new Hashtable<>();

    private static Long time;

    @SuppressWarnings("unchecked")
    public static void startLogSqlQuery(Thread t, String sql) {
       if (QueryTime.get(t) != null)
       logger.warn("WARNING: overwriting sql query log time for " + sql);
       QueryTime.put(t, System.currentTimeMillis());
    }

    public static Long getTime() {
        return time;
    }

    public static void setTime(Long time) {
        JDBCLogger.time = time;
    }

    public static void endLogSqlQuery(Thread t, String sql) {
       time = System.currentTimeMillis();
       time -= (QueryTime.get(t));
       logger.info("Time: " + time + " millis for SQL query " + sql);
       QueryTime.remove(t);
    }

    public static void startLogSqlNext(Thread t, String sql) {}

    public static void endLogSqlNext(Thread t, String sql) {}

}
/*
public class JDBCLogger
{
  public static void startLogSqlQuery(Thread t, String sql) {}
  public static void endLogSqlQuery(Thread t, String sql) {}
  public static void startLogSqlNext(Thread t, String sql) {}
  public static void endLogSqlNext(Thread t, String sql) {}

}
*/
