package com.github.p4535992.util.repositoryRDF.cumulusrdf;
import com.github.p4535992.util.log.SystemLog;
import edu.kit.aifb.cumulus.store.CumulusStoreException;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import edu.kit.aifb.cumulus.store.Store;
import edu.kit.aifb.cumulus.store.CassandraRdfHectorQuads;
import edu.kit.aifb.cumulus.store.CassandraRdfHectorTriple;
import edu.kit.aifb.cumulus.store.sesame.CumulusRDFSail;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.semanticweb.yars.nx.Resource;

import java.util.Iterator;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 * href: https://code.google.com/p/cumulusrdf/wiki/CodeExamples.
 */
@SuppressWarnings("unused")
public class CumulusRDFKit{

    public static org.apache.log4j.Logger logger;

    protected CumulusRDFKit() {
        logger = org.apache.log4j.Logger.getLogger(this.getClass().getName());
    }

    private static CumulusRDFKit instance = null;

    public static CumulusRDFKit getInstance(){
        if(instance == null) {
            instance = new CumulusRDFKit();
        }
        return instance;
    }

    private static final String DEFAULT_KEYSPACE ="KeyspaceCumulus";
    private static final String DEFAULT_HOSTS ="localhost:9160";
    private static final String DEFAULT_HOSTS2 ="host1:9160,host2:9160";

    /**
     * Method to connect to a Cassandra Repository.
     * @param isQuadStore if true the Cassandra repository is a quadStore else is a triple store.
     * @param hosts the String concatenation of the hosts.
     * @param keyspace the String of the your keySpace.
     * @return the OpenRDF Repository.
     * @throws SailException throw if any error with the repository is occurred.
     */
    public org.openrdf.repository.Repository connectToCassandraRepository(String hosts,String keyspace,boolean isQuadStore)  {
        Store crdf = setNewCumulusStore(hosts,keyspace,isQuadStore);
        Sail sail = new CumulusRDFSail(crdf);
        try {
            sail.initialize();
        } catch (SailException e) {
            SystemLog.exception(e,CumulusRDFKit.class);
        }
        return  new SailRepository(sail);
    }

    /**
     * Method to get a new CumulusRDF Store.
     * @param isQuadStore  if true the Cassandra repository is a quadStore else is a triple store.
     * @return the CumulusRDF Store.
     */
    /*public Store getNewCumulusStore(boolean isQuadStore){
        Store crdf;
        // If you are using a quadstore
        if(isQuadStore) crdf = new CassandraRdfHectorQuads("host1:9160,host2:9160", "YourKeyspace");
        // If you are using a triplestore
        else crdf = new CassandraRdfHectorTriple("host1:9160,host2:9160", "YourKeyspace");
        return crdf;
    }*/

    /**
     * Method to set a new CumulusRDF Store.
     * @param hosts the String of the hosts .
     * @param keyspace the String of the Keyspace of the CumulusRDF cluster.
     * @param isQuadStore  if true the Cassandra repository is a quadStore else is a triple store.
     * @return the CumulusRDF Store.
     */
    public Store setNewCumulusStore(String hosts,String keyspace,boolean isQuadStore){
        Store crdf;
        // If you are using a quadstore
        if(isQuadStore) crdf = new CassandraRdfHectorQuads(hosts, keyspace);
        // If you are using a triplestore
        else crdf = new CassandraRdfHectorTriple(hosts, keyspace);
        return crdf;
    }

    /**
     * Method to set a new CumulusRDF Store.
     * @param isQuadStore  if true the Cassandra repository is a quadStore else is a triple store.
     * @return the CumulusRDF Store.
     */
    public Store setNewCumulusStore(boolean isQuadStore){
        Store crdf;
        // If you are using a quadstore
        if(isQuadStore) crdf = new CassandraRdfHectorQuads(DEFAULT_HOSTS, DEFAULT_KEYSPACE);
            // If you are using a triplestore
        else crdf = new CassandraRdfHectorTriple(DEFAULT_HOSTS, DEFAULT_KEYSPACE);
        return crdf;
    }

    /**
     * Method to get all quads from a cassandra Store.
     * @param crdf the CumulusRDF Store.
     * @return a Iterator of OpenRDFStatement.
     * @throws CumulusStoreException throw if any error with the repository is occurred.
     */
    public Iterator<Statement> getAllQuadsfromCassandraRepository(Store crdf) throws CumulusStoreException {
        //Store crdf = getNewCumulusStore(isQuadStore);
        // Requesting all quads with a given predicate and object.
        return crdf.query(
                new Value[]{
                        null,
                        (Value) new Resource("http://example.com/predicate"),
                        (Value) new Resource("http://example.com/object"), null}
                , 1000);
    }

    /**
     * Method to get all triples from a cassandra Store.
     * @param crdf the CumulusRDF Store.
     * @return a Iterator of OpenRDFStatement.
     * @throws CumulusStoreException throw if any error with the repository is occurred.
     */
    public Iterator<Statement> getAllTriplefromCassandraRepository(Store crdf) throws CumulusStoreException {
        //Store crdf = getNewCumulusStore(isQuadStore);
        // Requesting all quads with a given predicate and object.
        return crdf.query(
                new Value[]{
                        (Value) new Resource("http://example.com/subject"),
                        (Value) new Resource("http://example.com/predicate"),
                        (Value) new Resource("http://example.com/object"), null}
                , 1000);
    }


}
