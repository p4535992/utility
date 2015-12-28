package com.github.p4535992.util.database.sql.datasource;

import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

public class LocalContextFactory {
    /**
     * do not instantiate this class directly. Use the factory method.
     */
    private LocalContextFactory() {}

    public static LocalContext createLocalContext(String databaseDriver) throws NamingException {

        try {
            LocalContext ctx = new LocalContext();
            Class.forName(databaseDriver);
            NamingManager.setInitialContextFactoryBuilder(ctx);
            return ctx;
        }
        catch(Exception e) {
            throw new NamingException("Error Initializing Context: " + e.getMessage());
        }
    }
}
