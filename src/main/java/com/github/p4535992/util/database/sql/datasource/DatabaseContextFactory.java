package com.github.p4535992.util.database.sql.datasource;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import java.util.Hashtable;

public class DatabaseContextFactory implements InitialContextFactory, InitialContextFactoryBuilder {

    public Context getInitialContext(Hashtable<?, ?> environment)throws NamingException
    {
        return new DatabaseContext();
    }

    public InitialContextFactory createInitialContextFactory( Hashtable<?, ?> environment) throws NamingException
    {
        return new DatabaseContextFactory();
    }

}