package com.github.p4535992.util.database.sql.datasource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Properties;

public class DatabaseContext extends InitialContext {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(DatabaseContext.class);

    private static Properties getDataSources(){ initDataSources(); return dataSources; }

    private static Properties dataSources;

    private static void initDataSources(){
        if(dataSources==null){
             dataSources = new Properties();
        }
    }

    public DatabaseContext() throws NamingException {}

    @Override
    public Object lookup(String name) throws NamingException
    {
        try {
            //our connection strings
            Class.forName("com.mysql.jdbc.Driver");
            DataSource ds1 = new LocalDataSource("jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true",
                    "siimobility", "siimobility");
            //DataSource ds2 = new LocalDataSource("jdbc:mysql://dbserver1/dboneB", "username", "xxxpass");

            //Properties prop = new Properties();
            getDataSources();
            dataSources.put("ds1", ds1);
            //prop.put("jdbc/ds2", new LocalDataSource("jdbc:mysql://dbserver1/dboneB", "username", "xxxpass"));

            Object value = dataSources.get(name);
            return (value != null) ? value : super.lookup(name);
        }
        catch(Exception e) {
            logger.error("Lookup Problem " + e.getMessage(),e);
        }
        return null;
    }

    public void addNewDataSource(String dataSourceName,String jdbcUrl,String username,String password){
        dataSources.put(dataSourceName, new LocalDataSource(jdbcUrl, username, password));
    }

}