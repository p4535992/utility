package com.github.p4535992.util.rdf;
import edu.kit.aifb.cumulus.store.CumulusStoreException;
import edu.kit.aifb.cumulus.store.QuadStore;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import edu.kit.aifb.cumulus.store.Store;
import edu.kit.aifb.cumulus.store.TripleStore;
//import edu.kit.aifb.cumulus.store.CassandraRdfHectorQuads;
//import edu.kit.aifb.cumulus.store.CassandraRdfHectorTriple;
import edu.kit.aifb.cumulus.store.sesame.CumulusRDFSail;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.semanticweb.yars.nx.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 * href: https://code.google.com/p/cumulusrdf/wiki/CodeExamples.
 */
@SuppressWarnings("unused")
public class CumulusRDFUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(CumulusRDFUtilities.class);

    protected CumulusRDFUtilities() {}

    private static CumulusRDFUtilities instance = null;

    public static CumulusRDFUtilities getInstance(){
        if(instance == null) {
            instance = new CumulusRDFUtilities();
        }
        return instance;
    }

    private static final String DEFAULT_KEYSPACE ="KeyspaceCumulus";
    private static final String DEFAULT_HOSTS ="localhost:9160";
    private static final String DEFAULT_HOSTS2 ="host1:9160,host2:9160";

    

    /**
     * Connecting to the store with Java/Sesame
     * @href https://raw.githubusercontent.com/wiki/agazzarini/cumulusrdf/CodeExamples.md
     * @return
     * @throws SailException 
     * @throws RepositoryException 
     */
    public RepositoryConnection toRDFConnection(boolean tripleStore) throws SailException, RepositoryException{
    	Sail sail = new CumulusRDFSail(tripleStore ? new TripleStore() : new QuadStore());
    	sail.initialize();

    	SailRepository repo = new SailRepository(sail);
    	RepositoryConnection connection = null;
//    	try {
    		return repo.getConnection();
    		// Do something with the connection... (see below)
//    	} 
//    	finally {
//    		if (connection != null) {
//    			try { connection.close(); } catch (final RepositoryException ignore) { }
//    		}   
//    	}
    }

    
    /**
     * Method to connect to a Cassandra Repository.
     * @href https://raw.githubusercontent.com/wiki/agazzarini/cumulusrdf/CodeExamples.md
     * @param isQuadStore if true the Cassandra repository is a quadStore else is a triple store.
     * @param hosts the String concatenation of the hosts.
     * @param keyspace the String of the your keySpace.
     * @return the OpenRDF Repository.
     * @throws SailException 
     */
    public org.openrdf.repository.Repository toRDFRepository(
            String hosts,String keyspace,boolean isQuadStore) throws SailException{
        //Store crdf = setNewCumulusStore(hosts,keyspace,isQuadStore);  
        //Sail sail = new CumulusRDFSail(crdf);
    	Sail sail = new CumulusRDFSail(isQuadStore ?  new QuadStore() : new TripleStore());
        try {
        	sail.initialize();
        	SailRepository repo = new SailRepository(sail);
//        	RepositoryConnection connection = null;
//        	try {
//        	    repo.getConnection();
//        	    // Do something with the connection... (see below)
//        	} finally {
//        	    if (connection != null) {
//        	        try { connection.close(); } catch (final RepositoryException ignore) { }
//        	    }   
//        	}
        	return repo;
        } catch (SailException e) {
           logger.error(e.getMessage(),e);
           throw e;
        }
    }

//    /**
//     * Method to get a new CumulusRDF Store.
//     * @param isQuadStore  if true the Cassandra repository is a quadStore else is a triple store.
//     * @return the CumulusRDF Store.
//     */
//    public Store getNewCumulusStore(boolean isQuadStore){
//        Store crdf;
//        // If you are using a quadstore
//        if(isQuadStore) crdf = new CassandraRdfHectorQuads("host1:9160,host2:9160", "YourKeyspace");
//        // If you are using a triplestore
//        else crdf = new CassandraRdfHectorTriple("host1:9160,host2:9160", "YourKeyspace");
//        return crdf;
//    }
//
//    /**
//     * Method to set a new CumulusRDF Store.
//     * @param hosts the String of the hosts .
//     * @param keyspace the String of the Keyspace of the CumulusRDF cluster.
//     * @param isQuadStore  if true the Cassandra repository is a quadStore else is a triple store.
//     * @return the CumulusRDF Store.
//     */
//    public Store setNewCumulusStore(String hosts,String keyspace,boolean isQuadStore){
//        Store crdf;
//        // If you are using a quadstore
//        if(isQuadStore) crdf = new CassandraRdfHectorQuads(hosts, keyspace);
//        // If you are using a triplestore
//        else crdf = new CassandraRdfHectorTriple(hosts, keyspace);
//        return crdf;
//    }
//
//    /**
//     * Method to set a new CumulusRDF Store.
//     * @param isQuadStore  if true the Cassandra repository is a quadStore else is a triple store.
//     * @return the CumulusRDF Store.
//     */
//    public Store setNewCumulusStore(boolean isQuadStore){
//        Store crdf;
//        // If you are using a quadstore
//        if(isQuadStore) crdf = new CassandraRdfHectorQuads(DEFAULT_HOSTS, DEFAULT_KEYSPACE);
//            // If you are using a triplestore
//        else crdf = new CassandraRdfHectorTriple(DEFAULT_HOSTS, DEFAULT_KEYSPACE);
//        return crdf;
//    }

    /**
     * Method to get all quads from a cassandra Store.
     * @param crdf the CumulusRDF Store.
     * @return a Iterator of OpenRDFStatement.
     * @throws CumulusStoreException throw if any error with the repository is occurred.
     */
    public Iterator<Statement> getAllQuadsfromCassandraRepository(Store crdf,String prefiYourUri) throws CumulusStoreException {
        //Store crdf = getNewCumulusStore(isQuadStore);
        // Requesting all quads with a given predicate and object.
        return crdf.query(
                new Value[]{
                        null,
                        (Value) new Resource(prefiYourUri+"/predicate"),
                        (Value) new Resource(prefiYourUri+"/object"), null}
                , 1000);
    }

    /**
     * Method to get all triples from a cassandra Store.
     * @param crdf the CumulusRDF Store.
     * @return a Iterator of OpenRDFStatement.
     * @throws CumulusStoreException throw if any error with the repository is occurred.
     */
    public Iterator<Statement> getAllTriplefromCassandraRepository(Store crdf,String prefiYourUri) throws CumulusStoreException {
        //Store crdf = getNewCumulusStore(isQuadStore);
        // Requesting all quads with a given predicate and object.
        return crdf.query(
                new Value[]{
                        (Value) new Resource(prefiYourUri+"/subject"),
                        (Value) new Resource(prefiYourUri+"/predicate"),
                        (Value) new Resource(prefiYourUri+"/object"), null}
                , 1000);
    }

    /**
     * Insert and Delete with Sesame
     * @href https://raw.githubusercontent.com/wiki/agazzarini/cumulusrdf/CodeExamples.md
     * @param connection
     * @param ntFile
     * @param rdfFormat
     * @return
     */
    public boolean insertFile(RepositoryConnection connection,File ntFile,RDFFormat rdfFormat) throws RDFParseException, RepositoryException, IOException{
    	try{
    		connection.add(ntFile, null, rdfFormat);  		
    		return true;
    	}catch(IOException |RepositoryException |RDFParseException ex){
    		logger.error(ex.getMessage(),ex);
    		return false;
    	}
    }
    
    /**
     * Querying (SPARQL) with Sesame
     * @href https://raw.githubusercontent.com/wiki/agazzarini/cumulusrdf/CodeExamples.md
     * @throws QueryEvaluationException 
     * @throws MalformedQueryException 
     * @throws RepositoryException 
     */
    public void query(RepositoryConnection connection,String queryString,QueryLanguage queryLanguage) throws QueryEvaluationException, RepositoryException, MalformedQueryException{
    	Query query = connection.prepareQuery(queryLanguage, queryString);
    	// ASK Query
    	if (query instanceof BooleanQuery) {
    	    boolean result = ((BooleanQuery) query).evaluate();
    	   logger.info(""+result);
    	}

    	// SELECT Query
    	if (query instanceof TupleQuery) {
    	    TupleQueryResult result = ((TupleQuery) query).evaluate();
    	    while (result.hasNext()) {
    	        // The result is an iterator of BindingSet, a binding is a name/value
    	        // pair, the names correspond to the variables used in the query.
    	        logger.info(result.next().getBinding("YourVariable").toString());
    	    }
    	    // Do not forget!
    	    result.close();
    	}

    	// CONSTRUCT Query
    	if (query instanceof GraphQuery) {
    	    GraphQueryResult result = ((GraphQuery) query).evaluate();
    	    while (result.hasNext()) {
    	        // The result is an iterator of Statement, which is a RDF triple or quad.
    	        logger.info(result.next().toString());
    	    }
    	    // Do not forget!
    	    result.close();
    	}
    }
    
    public void addFileToStore(Store store,File tripleFile,RDFFormat rdfFormat) throws CumulusStoreException, IOException{
    	store.open();
    	// Adding a n-triples file
    	store.bulkLoad(tripleFile, rdfFormat);
    	store.close();
    }
    
    public void addStatementsToStore(Store store,Collection<Statement> statements) throws CumulusStoreException{
    	store.open();
    	// Adding a set of triples or quads
    	// If you use a triple store, all arrays must have a length of 3,
    	// if you are using a quadstore, the length has to be 4.
    	Iterable<Statement> yourNodes = statements;
    	store.addData(yourNodes.iterator());
    	store.close();
    }
    
    public void removeStatementsToStore(Store store,Collection<Statement> statements) throws CumulusStoreException{
    	store.open();
    	// Removing a set of triples or quads
    	// If you use a triple store, all arrays must have a length of 3,
    	// if you are using a quadstore, the length has to be 4.
    	Iterable<Statement> yourNodes = statements;
    	store.removeData(yourNodes.iterator());
    	store.close();
    }
    
    

}
