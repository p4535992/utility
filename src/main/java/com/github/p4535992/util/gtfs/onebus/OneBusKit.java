package com.github.p4535992.util.gtfs.onebus;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.serialization.GtfsWriter;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.onebusaway.gtfs.services.HibernateGtfsFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by 4535992 on 23/10/2015.
 */
@SuppressWarnings( "deprecation" )
public class OneBusKit {

    protected OneBusKit() {
    }

    private static OneBusKit instance = null;

    public static OneBusKit getInstance() {
        if (instance == null) {
            instance = new OneBusKit();
            //help with very large repository....
            System.setProperty("entityExpansionLimit", "1000000");
        }
        return instance;
    }

    public GtfsDaoImpl reader(File fileOrZipGtfs) throws IOException {
        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(fileOrZipGtfs);
        /**
         * You can register an entity handler that listens for new objects as they
         * are read
         */
        /*reader.addEntityHandler(new EntityHandler() {
            @Override
            public void handleEntity(Object bean) {
                if (bean instanceof Stop) {
                    Stop stop = (Stop) bean;
                    System.out.println("stop: " + stop.getName());
                }
            }
        });*/

        /**
         * Or you can use the internal entity store, which has references to all the
         * loaded entities
         */
        GtfsDaoImpl store = new GtfsDaoImpl();
        reader.setEntityStore(store);
        reader.run();
        // Access entities through the store
        /*for (Route route : store.getAllRoutes()) {
            System.out.println("route: " + route.getShortName());
        }*/
        return store;
    }

    public static void writer(File fileGtfs) throws IOException {

        GtfsWriter writer = new GtfsWriter();
        writer.setOutputLocation(fileGtfs);

        Agency agency = new Agency();
        agency.setName("My agency!");
        writer.handleEntity(agency);

        Route route = new Route();
        route.setShortName("A");
        route.setAgency(agency);
        writer.handleEntity(route);

        writer.close();
    }

    public static void readerHibernate(File resourceXML,File fileGtfs) throws IOException {
        // Check args and construct application resource paths
        /*String resource =
                "onebusaway-gtfs-hibernate/../hibernate-configuration-examples.xml";*/
        HibernateGtfsFactory factory = createHibernateGtfsFactory(resourceXML.getAbsolutePath());

        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(fileGtfs);

        GtfsMutableRelationalDao dao = factory.getDao();
        reader.setEntityStore(dao);
        reader.run();

        Collection<Stop> stops = dao.getAllStops();

        for (Stop stop : stops){
            System.out.println(stop.getName());
        }
    }


    private static HibernateGtfsFactory createHibernateGtfsFactory(String resource) {

        Configuration config = new Configuration();

       /* if (resource.startsWith(KEY_CLASSPATH)) {
            resource = resource.substring(KEY_CLASSPATH.length());
            config = config.configure(resource);
        } else if (resource.startsWith(KEY_FILE)) {
            resource = resource.substring(KEY_FILE.length());
            config = config.configure(new File(resource));
        } else {
            config = config.configure(new File(resource));
        }*/

        SessionFactory sessionFactory = config.buildSessionFactory();
        return new HibernateGtfsFactory(sessionFactory);
    }

    public static void readGtfsFile(InputStream gtfsFile)
            throws NumberFormatException, MalformedURLException, IOException {
        ZipInputStream zip = new ZipInputStream(gtfsFile);
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (entry.isDirectory()) {
                continue;
            }
            String filename = entry.getName();
            if(filename.equals("agency.txt")) {

            } else if(filename.equals("stops.txt")) {

            } else if(filename.equals("routes.txt")) {

            }
        }
    }
}
