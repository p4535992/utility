package com.github.p4535992.util.database.sql.query;

import com.github.p4535992.util.database.sql.SQLUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by 4535992 on 29/12/2015.
 * @author 4535992.
 * @version 2015-12-31.
 */
public class MySQLQuery extends SQLQuery{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(MySQLQuery.class);

    /**
     * Method to prepare the server MySQL  to use the performance utility.
     * @param conn the Connection to the MySQL server.
     * @return if true all the operation all corrected done.
     */
    private static boolean preparePerformanceSchema(Connection conn){
        try {
            //Ensure that statement and stage instrumentation is enabled by updating the
            //setup_instruments table. Some instruments may already be enabled by default.
            SQLUtilities.executeSQL(
                    "UPDATE performance_schema.setup_instruments SET ENABLED = 'YES', TIMED = 'YES'\n" +
                            "WHERE NAME LIKE '%statement/%';\n" +
                    "UPDATE performance_schema.setup_instruments SET ENABLED = 'YES', TIMED = 'YES'\n" +
                    "WHERE NAME LIKE '%stage/%';",conn
            );
            //Ensure that events_statements_* and events_stages_* consumers are enabled.
            //Some consumers may already be enabled by default.
            SQLUtilities.executeSQL(
                    "UPDATE performance_schema.setup_consumers SET ENABLED = 'YES'\n" +
                            "WHERE NAME LIKE '%events_statements_%';\n" +
                    "UPDATE performance_schema.setup_consumers SET ENABLED = 'YES'\n" +
                            "WHERE NAME LIKE '%events_stages_%';",conn
            );
            return true;
        }catch(SQLException e){
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to get execution directly from the server MySQL instead from the java api SQL.
     * href: http://dev.mysql.com/doc/refman/5.6/en/performance-schema-query-profiling.html
     * @param sql the String SQL query to execute and analyze.
     * @param conn the Connection SQL currrent active.
     * @return the Long time required for the execution of the query.
     */
    public static Long getExecutionTime(String sql,Connection conn){
        preparePerformanceSchema(conn);
        try {
            SQLUtilities.executeSQL(sql,conn);
            if(sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
            sql = sql.replaceAll("''","''''");
            ResultSet resultSet = SQLUtilities.executeSQL(
                    "SELECT EVENT_ID, TRUNCATE(TIMER_WAIT/1000000000000,6) as Duration, SQL_TEXT\n" +
                    "FROM performance_schema.events_statements_history_long WHERE SQL_TEXT like\n '%"+
                     sql+"%'"
            );
           /* ResultSet resultSet = SQLUtilities.executeSQL(
                    "SELECT EVENT_ID, TRUNCATE(TIMER_WAIT/1000000000000,6) as Duration, \n" +
                            "FROM performance_schema.events_statements_history_long"
            );*/
            String duration = "";

            //noinspection LoopStatementThatDoesntLoop
            while(resultSet.next()) {
                //String sql_text = resultSet.getString("SQL_TEXT");
                duration = resultSet.getString("Duration");
                break; // i'M SURE IS A UNIQUE RECORD.
            }
            return Math.round((Double.parseDouble(duration)*1000));
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return 0L;
        }
    }

}
