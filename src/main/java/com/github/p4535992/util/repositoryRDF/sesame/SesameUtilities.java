package com.github.p4535992.util.repositoryRDF.sesame;


import com.github.p4535992.util.collection.CollectionUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.repositoryRDF.jenaAndSesame.JenaSesameUtilities;
import com.github.p4535992.util.repositoryRDF.sparql.SparqlUtilities;
import com.github.p4535992.util.string.*;
import com.github.p4535992.util.string.Timer;
import org.openrdf.OpenRDFException;
import org.openrdf.http.client.SesameClient;
import org.openrdf.http.client.SesameClientImpl;
import org.openrdf.model.*;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.*;
import org.openrdf.repository.*;
import org.openrdf.repository.base.RepositoryConnectionWrapper;
import org.openrdf.repository.config.*;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.DirectTypeHierarchyInferencer;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Class of utility for Sesame Server and Owlim Server
 * @author 4535992.
 * @version 2015-12-17.
 * NOTE: Work with Sesame openrdf version 2.8.X  and java 1.7
 *       Not Work with openrdf 4.0.X you need to use java 8.
 */
@SuppressWarnings("unused")
public class SesameUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SesameUtilities.class);
    
    private static String nameClass;

    protected SesameUtilities() {}

    private static SesameUtilities instance = null;

    public static SesameUtilities getInstance(){
        if(instance == null) {
            instance = new SesameUtilities();
            //nameClass = instance.getClass().getSimpleName()+"::";
            //help with very large repository....
            System.setProperty("entityExpansionLimit", "1000000");
            VERIFY = true;
            STOP_ON_ERROR = true;
            PRESERVE_BNODES = true;
        }
        return instance;
    }

    private static boolean VERIFY,STOP_ON_ERROR,PRESERVE_BNODES;
    private boolean SHOWSTATS,UPDATES,PRINT_RESULT_QUERY;
    //output parameter
    private String OUTPUTFILE,OUTPUTFORMAT,URL_SESAME,URL_REPOSITORIES,URL_REPOSITORY_ID;
    boolean isManagedRepository = false;

    // A map of namespace-to-prefix
    //protected static Map<String, String> namespacePrefixes = new HashMap<>();
    // The repository manager
    protected static RepositoryManager mRepositoryManager;
    protected static RemoteRepositoryManager mRemoteRepositoryManager;
    protected static Repository mRepository;
    protected static RepositoryConnection mRepositoryConnection;
    protected static String  mRepositoryLocation;
    protected static String  mRepositoryName;
    protected static RepositoryProvider mRepositoryProvider;
    protected static RepositoryConnectionWrapper mRepositoryConnectionWrapper;

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{%[\\p{Print}&&[^\\}]]+%\\}");
    private static final String URI_DEFAULT_SESAME_WORKBENCH =
            "http://localhost:8080/openrdf-workbench/repositories/NONE/repositories";
    private static String[] types = new String[]{"http","inferencing","native","memory","owlim"};

    public void setOutput(String outputPathfile,String outputformat,boolean printResultQuery){
        this.OUTPUTFILE = outputPathfile;
        this.OUTPUTFORMAT = outputformat;
        this.PRINT_RESULT_QUERY = printResultQuery;
        this.SHOWSTATS = !PRINT_RESULT_QUERY;
    }

    public void setOutput(String outputPathfile,String outputformat,boolean printResultQuery,boolean showOnConsole){
        this.OUTPUTFILE = outputPathfile;
        this.OUTPUTFORMAT = outputformat;
        this.PRINT_RESULT_QUERY = printResultQuery;
        this.SHOWSTATS = showOnConsole;
    }


    private void setRepositoryConnection() throws RepositoryException {
        if(mRepository!=null){
            isRepositoryInitialized();
            mRepositoryConnection = mRepository.getConnection();
            logger.info("The RepositoryConnection:"+mRepositoryConnection.toString()+" is setted!");
        }else{
            logger.warn("Attention, you try to set a RepositoryConnection on a inexistent Repository!");
        }
    }

    public void setRepository(RepositoryManager manager,String repositoryId) throws RepositoryException, RepositoryConfigException {
        if( mRepositoryManager==null && manager!=null)  mRepositoryManager = manager;
        if (mRepositoryManager != null) {
            isRepositoryManagerInitialized(); //set repository manager
            mRepository = mRepositoryManager.getRepository(repositoryId);
            isRepositoryInitialized();
            logger.info("The Repository:"+mRepository.getDataDir()+File.separator+repositoryId+" is setted!");
        }else{
            logger.warn("Attention, you try to set a Repository on a inexistent RepositoryManager!");
        }
    }

    /*
     * Setter and getter
     */

    /**
     * Method to get RepositoryManager.
     * @return the RepositoryManager.
     */
    public RepositoryManager getRepositoryManager() {
        return mRepositoryManager;
    }

    /**
     * Method to set RepositoryManager.
     * @param repositoryManager the Repository manager.
     */
    public void setRepositoryManager(RepositoryManager repositoryManager) {
        mRepositoryManager = repositoryManager;
        isRepositoryManagerInitialized();
    }

    /**
     * Method to get RemoteRepositoryManager.
     * @return the RemoteRepositoryManager.
     */
    public RemoteRepositoryManager getRemoteRepositoryManager() {
        return mRemoteRepositoryManager;
    }

    /**
     * Method to set RemoteRepositoryManager.
     * @param remoteRepositoryManager the Remote Repository Manager.
     */
    public void setRemoteRepositoryManager(RemoteRepositoryManager remoteRepositoryManager) {
        mRemoteRepositoryManager = remoteRepositoryManager;
    }

    /**
     * Method to get Repository.
     * @return the Repository.
     */
    public Repository getRepository() {
        return mRepository;
    }

     /**
     * Method to set Repository.
     * @param repository the Repository.
     */
    public void setRepository(Repository repository) {
        mRepository = repository;
    }

    /**
     * Method to get RepositoryConnection.
     * @return the RepositoryConnection.
     */
    public RepositoryConnection getRepositoryConnection() {
        return mRepositoryConnection;
    }

    /**
     * Method to set RepositoryConnection.
     * @param repositoryConnection the Repository Connection.
     */
    public void setRepositoryConnection(RepositoryConnection repositoryConnection) {mRepositoryConnection = repositoryConnection;}

    /**
     * Method to get RepositoryLocation.
     * @return the RepositoryLocation.
     */
    public String getRepositoryLocation() {
        return mRepositoryLocation;
    }

    /**
     * Method to set RepositoryLocation.
     * @param repositoryLocation the Repository Location.
     */
    public void setRepositoryLocation(String repositoryLocation) {
        SesameUtilities.mRepositoryLocation = repositoryLocation;
    }

    /**
     * Method to get RepositoryName.
     * @return the RepositoryName.
     */
    public String getRepositoryName() {
        return mRepositoryName;
    }

     /**
     * Method to set RepositoryName.
     * @param repositoryName the repository Name.
     */
    public void setRepositoryName(String repositoryName) {
        SesameUtilities.mRepositoryName = repositoryName;
    }

    /**
     * Method to get RepositoryProvider.
     * @return the RepositoryProvider.
     */
    public RepositoryProvider getRepositoryProvider() {
        return mRepositoryProvider;
    }

    /**
     * Method to set RepositoryProvider.
     * @param repositoryProvider the Repository Provider.
     */
    public void setRepositoryProvider(RepositoryProvider repositoryProvider) {
        SesameUtilities.mRepositoryProvider = repositoryProvider;
    }

    /**
     * Method to get RepositoryConnectionWrapper.
     * @return the RepositoryConnectionWrapper.
     */
    public RepositoryConnectionWrapper getRepositoryConnectionWrapper() {
        return mRepositoryConnectionWrapper;
    }

     /**
     * Method to set RepositoryConnectionWrapper.
     * @param repositoryConnectionWrapper the Repository Connection Wrapper.
     */
    public void setRepositoryConnectionWrapper(RepositoryConnectionWrapper repositoryConnectionWrapper) {
        mRepositoryConnectionWrapper = repositoryConnectionWrapper;
    }

    /**
     * Method to get ValueFactory obtained from Repository you are working with (recommend).
     * @return the ValueFactory of the Repository where you working.
     */
    /*public ValueFactory getValueFactory() {
        return mRepository.getValueFactory();
    }*/

    /**
     * Method to get RepositoryConnectionWrapper.
     * @param repository the Repository OpenRDF to Wrapper.
     * @return the RepositoryConnectionWrapper.
     */

    public RepositoryConnectionWrapper createRepositoryConnectionWrapper(Repository repository) {
        try {
            mRepositoryConnectionWrapper = new RepositoryConnectionWrapper(repository);
            mRepositoryConnectionWrapper.setDelegate(repository.getConnection());
            return mRepositoryConnectionWrapper;
        } catch (RepositoryException e) {
            logger.error("Can't create the RepositoryConnectionWrapper Sesame, " +
                    "maybe you not have set a repository!",e);
            return null;
        }
    }

    /**
     * Method to get RepositoryConnectionWrapper.
     * @param repository the Repository OpenRDF to Wrapper.
     * @param repositoryConnection the RepositoryConnection OpenRDF to Wrapper.
     * @return the RepositoryConnectionWrapper.
     */
    public RepositoryConnectionWrapper createRepositoryConnectionWrapper(
            Repository repository,RepositoryConnection repositoryConnection) {
        mRepositoryConnectionWrapper = new RepositoryConnectionWrapper(repository,repositoryConnection);
        return mRepositoryConnectionWrapper;
    }

    /**
     * Method to get a new OpenRDF Model.
     * @return the OpenRDF Model.
     */
    public Model createModel(){return new LinkedHashModel();}

    /**
     * Method to get a new OpenRDF Graph.
     * @return the OpenRDF Graph.
     */
    public Graph createGraph(){return new TreeModel();}

    /**
     * Method to get a new OpenRDF ValueFactory.
     * @return the OpenRDF ValueFactory.
     */
    public ValueFactory createValueFactory() {
        return ValueFactoryImpl.getInstance();
    }

    /**
     * Method to create a new RepositoryManager.
     * @param baseDirectory the File base directory where are stored the repositories.
     * @return the RepositoryManager created.
     */
    public RepositoryManager createRepositoryManagerLocal(File baseDirectory){
        try {
            // Create a manager for local repositories and initialise it
            mRepositoryManager = new LocalRepositoryManager(baseDirectory);
            mRepositoryManager.initialize();
            return mRemoteRepositoryManager;
        } catch (RepositoryException e) {
            logger.error("Can't create the RepositoryManager Sesame, " +
                    "maybe you not have set a correct path to the folder!",e);
            return null;
        }
    }

    /**
     * Method to create a new RepositoryManager.
     * @param serverUrl the URL address to the Sesame Server.
     * @return the RepositoryManager created.
     */
    public RemoteRepositoryManager createRepositoryManagerRemote(String serverUrl){
        try {
            // Create a manager for local repositories and initialise it
            mRemoteRepositoryManager = new RemoteRepositoryManager(serverUrl);
            mRemoteRepositoryManager.initialize();
            return mRemoteRepositoryManager;
        } catch (RepositoryException e) {
            logger.error("Can't create the RepositoryManager Sesame, " +
                    "maybe you not have set a correct the URL address to the Sesame Server!",e);
            return null;
        }
    }

    /**
     * Method to get RepositoryConnectionWrapper.
     * @param mRepository the Repository OpenRDF to Wrapper.
     * @param mRepositoryConnection the RepositoryConnection OpenRDF to Wrapper.
     * @return the RepositoryConnectionWrapper.
     */
    public RepositoryConnectionWrapper setRepositoryConnectionWrapper(
            Repository mRepository,RepositoryConnection mRepositoryConnection) {
        mRepositoryConnectionWrapper =
                new RepositoryConnectionWrapper(mRepository,mRepositoryConnection);
        return mRepositoryConnectionWrapper;
    }

    public RepositoryConnectionWrapper setRepositoryConnectionWrapper(Repository mRepository) {
        try {
            mRepositoryConnectionWrapper =
                    new RepositoryConnectionWrapper(mRepository,mRepository.getConnection());
        } catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        return mRepositoryConnectionWrapper;
    }

    //------------------------------------------
    // Setter and getter addition
    //------------------------------------------

    /**
     * Method to get the String of the url where are located the  repositories
     * @return the String url.
     */
    public String getURL_REPOSITORIES() {return URL_REPOSITORIES;}

     /**
     * Method to get the String of the url where are located the  sesame server.
     * @return the String url.
     */
    public String getURL_SESAME() {return URL_SESAME;}

     /**
     * Method to get the String of the url where are located the specific repository.
     * @return the String url.
     */
    public String getURL_REPOSITORY_ID() {return URL_REPOSITORY_ID;}

    /**
     * Method to set the URL of the repository.
     * @param ID_REPOSITORY the String name of the ID of the repository.
     */
    public void setURLRepositoryId(String ID_REPOSITORY){
        this.URL_SESAME = "http://localhost:8080/openrdf-sesame/";
        this.URL_REPOSITORIES = "http://localhost:8080/openrdf-sesame/repositories/";
        //this.URL_REPOSITORY_ID = "http://www.openrdf.org/repository/"+ ID_REPOSITORY;
        this.URL_REPOSITORY_ID = "http://localhost:8080/openrdf-sesame/repositories/"+ ID_REPOSITORY;
    }

    public void setURLRepositoryId(String ID_REPOSITORY,String server,String port){
        this.URL_SESAME = "http://"+server+":"+port+"/openrdf-sesame/";
        this.URL_REPOSITORIES = "http://"+server+":"+port+"/openrdf-sesame/repositories/";
        //this.URL_REPOSITORY_ID = "http://www.openrdf.org/repository/"+ ID_REPOSITORY;
        this.URL_REPOSITORY_ID = "http://"+server+":"+port+"/openrdf-sesame/repositories/"+ ID_REPOSITORY;
    }

    public void setPrefixes(Map<String,String> mapPrefixes,RepositoryConnection repositoryConnection){
        try {
            for(Map.Entry<String,String> entry : mapPrefixes.entrySet()){
                repositoryConnection.setNamespace(entry.getKey(),entry.getValue());
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void setPrefixes(Map<String,String> mapPrefixes){
        setPrefixes(mapPrefixes,mRepositoryConnection);
    }

    public void setPrefixes(){
        setPrefixes(SparqlUtilities.getDefaultNamespacePrefixes(),mRepositoryConnection);
    }

    /**
     * Method for Close the currently opened repository. This works for managed and unmanaged repositories.
     */
    public void  closeRepository() {
        logger.info("===== Shutting down ==========");
        if (mRepositoryConnection != null) {
            try {
                logger.info("Commiting the connection");
                //mRepositoryConnection.commit();
                logger.info("Closing the connection");
                mRepositoryConnection.close();
                logger.info("Connection closed");
                // the following is NOT needed as the manager shutDown method
                // shuts down all repositories
                // mRepository.shutDown();
                // SystemLog.message("Repository shut down");
            } catch (RepositoryException e) {
                logger.error("Could not close Repository",e);
            }
            mRepositoryConnection = null;
            mRepository = null;
            mRepositoryName = null;
            logger.info("connection, repository and repositoryID set to null");
        }
    }

    /**
     * Method for connect to a local Sesame Repository with a config turtle file.
     * @param repositoryId the String ID of the Sesame Repository.
     * @param username the String username of the Sesame Repository.
     * @param password the String password of the Sesame Repository.
     * @return repository manager sesame.
     */
    public Repository connectToLocalWithConfigFile(String repositoryId,String username,String password){
        if (repositoryId == null) {
            logger.warn("No repository ID specified. When using the '" + URL_REPOSITORY_ID
                    + "' parameter to specify a Sesame server, you must also use the 'null' " +
                    "parameter to specify a repository on that server.");
            System.exit(-5);
        }
        try {
            mRemoteRepositoryManager = new RemoteRepositoryManager(URL_REPOSITORY_ID);
            if ( username != null || password != null) {
                if (username == null) username = "";
                if (password == null) password = "";
                mRemoteRepositoryManager.setUsernameAndPassword(username,password);
            }
            mRepositoryManager = mRemoteRepositoryManager;
            mRepositoryManager.initialize();
        } catch (RepositoryException e) {
            logger.error("Unable to establish a connection with the Sesame server '"
                    + URL_REPOSITORY_ID + "': "
                    + e.getMessage(),e);
            System.exit(-5);
        }
        // Get the repository to use
        try {
            mRepository = mRepositoryManager.getRepository(repositoryId);
            if (mRepository == null) {
                logger.warn("Unknown repository '" + repositoryId + "'");
                String message = "Please make sure that the value of the 'repository' "
                        + "parameter (current value '" + repositoryId + "') ";
                if (URL_REPOSITORY_ID == null) {
                    message += "corresponds to the repository ID given in the configuration file identified by the '"
                            + "CONFIGFILENAME' parameter (current value '????????????')";
                } else {
                    message += "identifies an existing repository on the Sesame server located at " + URL_REPOSITORY_ID;
                }
                logger.warn(message);
                System.exit(-6);
            }
            // Open a connection to this repository
            mRepositoryConnection = mRepository.getConnection();
            //repositoryConnection.setAutoCommit(false);//deprecated
        } catch (OpenRDFException e) {
            logger.error("Unable to establish a connection to the repository '" + repositoryId + "': "
                    + e.getMessage(),e);
            System.exit(-7);
        }
        return mRepository;
    }

    /**
     * Parse the given RDF file and return the contents as a Graph.
     * @param configurationFile the file containing the RDF data.
     * @param format RDFFormat of configurationFile.
     * @param defaultNamespace base URI of the configurationFile.
     * @return The contents of the file as an RDF graph.
     */
    public static Model convertFileTripleToSesameModel(File configurationFile, RDFFormat format, String defaultNamespace) {
        try{
            Reader reader = new FileReader(configurationFile);
           /* final Graph graph = new GraphImpl();*/
            RDFParser parser = Rio.createParser(format);
            final Model model =  Rio.parse(reader,defaultNamespace, format);
            RDFHandler handler = new RDFHandler() {
                @Override
                public void endRDF() throws RDFHandlerException {
                }

                @Override
                public void handleComment(String arg0) throws RDFHandlerException {
                }

                @Override
                public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
                }

                @Override
                public void handleStatement(Statement statement) throws RDFHandlerException {
                    model.add(statement);
                }

                @Override
                public void startRDF() throws RDFHandlerException {
                }
            };
            parser.setRDFHandler(handler);
            parser.parse(reader, defaultNamespace);
            return model;
        }catch(RDFParseException|RDFHandlerException|IOException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parses and loads all files specified in PARAM_PRELOAD.
     * @param preloadFolder e.home.  "./preload".
     */
    public void importIntoRepositoryDirectoryChunked(String preloadFolder){
        if(!new File(preloadFolder).exists()) {
            logger.warn("The '" + preloadFolder + "' not exists, can't make the import to the sesame repository!");
        }else {
            logger.info("===== Load Files (from the '" + preloadFolder + "' parameter) ==========");
            logger.info("Start the import of the Data on the repository...");
            final AtomicLong statementsLoaded = new AtomicLong();
            // Load all the files from the pre-load folder
            //String preload = preloadFolder;
                //SystemLog.message("No pre-load directory/filename provided.");

            FileUtilities.FileWalker.Handler handler = new FileUtilities.FileWalker.Handler() {

                @Override
                public void file(File file) throws Exception {
                    statementsLoaded.addAndGet(importIntoRepositoryFileChunked(file));
                }

                @Override
                public void directory(File directory) throws Exception {
                    logger.info("Loading files from: " + directory.getAbsolutePath());
                }
            };
            FileUtilities.FileWalker walker = new FileUtilities.FileWalker();
            walker.setHandler(handler);
            try {
                walker.walk(new File(preloadFolder));
            } catch (Exception e) {
                logger.error("Can't go to the other file the method FileWalker has failed!", e);
                return;
            }
            logger.info("...end the import of the Data on the repository...");
            logger.warn("TOTAL: " + statementsLoaded.get() + " statements loaded");
        }
    }

    /**
     * Method for Show some initialisation statistics.
     * @param startupTime range of  illiseconds before start the inizialization.
     */
    public void showInitializationStatistics(long startupTime){
        long explicitStatements = numberOfExplicitStatements();
        long implicitStatements = numberOfImplicitStatements();
        logger.info("Loaded: " + explicitStatements + " explicit statements.");
        logger.info("Inferred: " + implicitStatements + " implicit statements.");
        if (startupTime > 0) {
            double loadSpeed = explicitStatements / (startupTime / 1000.0);
            logger.info(" in " + startupTime + "ms.");
            logger.info("Loading speed: " + loadSpeed + " explicit statements per second.");
        } else {
            logger.info(" in less than 1 second.");
        }
        logger.info("Total number of statements: " + (explicitStatements + implicitStatements));
    }

    /**
     * Method to finding the total number of explicit statements in a repository.
     * @return The number of explicit statements.
     */
    public Long numberOfExplicitStatements() {
        return numberOfExplicitStatements(mRepositoryConnection);
    }

    /**
     * ethod to finding the total number of explicit statements in a repository.
     * @param repConn the RepositoryConnection object of Sesame.
     * @return The number of explicit statements.
     */
    public Long numberOfExplicitStatements(RepositoryConnection repConn) {
        try{
            // This call should return the number of explicit statements.
            long explicitStatements =repConn.size();
            // Another approach is to get an iterator to the explicit statements(by setting the includeInferred parameter
            // to false) and then counting them.
            RepositoryResult<Statement> statements = repConn.getStatements(null, null, null, false);
            while (statements.hasNext()) {
                statements.next();
                explicitStatements++;
            }
            statements.close();
            return explicitStatements;
        }catch(RepositoryException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * A method to count only the inferred statements in the repository. No method for this is available
     * through the Sesame API, so OWLIM uses a special context that is interpreted as instruction to retrieve
     * only the implicit statements, i.e. not explicitly asserted in the repository.
     * @return The number of implicit statements.
     */
    public Long numberOfImplicitStatements() {
        return numberOfImplicitStatements(mRepositoryConnection);
    }

    public Long numberOfImplicitStatements(RepositoryConnection repConn) {
        try {
            // Retrieve all inferred statements
            RepositoryResult<Statement> statements =
                    repConn.getStatements(null, null, null, true,
                        new URIImpl("http://www.ontotext.com/implicit"));
            long implicitStatements = 0;

            while (statements.hasNext()) {
                statements.next();
                implicitStatements++;
            }
            statements.close();
            return implicitStatements;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Iterates and collects the list of the namespaces, used in URIs in the repository.
     * @return map of all namespace used on the repository.
     */
    public Map<String,String> getNamespacePrefixesFromRepository(){
        Map<String, String> namespacePrefixes = new HashMap<>();
        try{
            logger.info("===== Namespace List ==================================");
            logger.info("Namespaces collected in the repository:");
            RepositoryResult<Namespace> iter = mRepositoryConnection.getNamespaces();
            while (iter.hasNext()) {
                Namespace namespace = iter.next();
                String prefix = namespace.getPrefix();
                String name = namespace.getName();
                namespacePrefixes.put(name, prefix);
                System.out.println(prefix + ":\t" + name);
            }
            iter.close();
            return namespacePrefixes;
        }catch(RepositoryException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to evaluate a Query on aFile.
     * @param queryFile the File with the QUERY/IES
     */
    public void executeQuerySPARQLFromFile(File queryFile){
        evaluateQueries(queryFile);
    }

    /**
     * Demonstrates query evaluation. First parse the query file. Each of the queries is executed against the
     * prepared repository. If the printResults is set to true the actual values of the bindings are output to
     * the console. We also count the time for evaluation and the number of results per query and output this
     * information.
     * @param queryFile file with multiple SPARQL queries.
     */
    private void evaluateQueries(File queryFile){
        logger.info("===== Query Evaluation ======================");
        if (queryFile == null) {
            logger.warn("No query file given in parameter 'null'.");
            return;
        }
        //long startQueries = System.currentTimeMillis();
        // process the query file to get the queries
        String[] queries =collectQueries(queryFile.getAbsolutePath());
        if(queries == null){
            //if is not a file but a String ->  queries = new String[]{queryFile};
            logger.info("Executing query '" + queryFile + "'");
            executeSingleQuery(queryFile.getAbsolutePath());
        }else{
            // evaluate each query and print the bindings if appropriate
            for (String querie : queries) {
                final String name = querie.substring(0, querie.indexOf(":"));
                final String query = querie.substring(name.length() + 2).trim();
                logger.info("Executing query '" + name + "'");
                executeSingleQuery(query);
            }
        }
        //long endQueries = System.currentTimeMillis();
        //SystemLog.message("Queries run in " + (endQueries - startQueries) + " ms.");
    }

    /**
     * The purpose of this method is to try to parse an operation locally in order to determine if it is a
     * tuple (SELECT), boolean (ASK) or graph (CONSTRUCT/DESCRIBE) query, or even a SPARQL update.
     * This happens automatically if the repository is local, but for a remote repository the local
     * HTTPClient-side can not work it out.
     * Therefore a temporary in memory SAIL is used to determine the operation type.
     * @param query Query string to be parsed.
     * @param language The query language to assume.
     * @param tempLocalConnection temporary local connection.
     * @return A parsed query object or null if not possible.
     */
    private static Query prepareQuery(String query, QueryLanguage language,RepositoryConnection tempLocalConnection){
        //Repository tempLocalRepository = new SailRepository(new MemoryStore());
        //tempLocalRepository.initialize();
        //RepositoryConnection tempLocalConnection = tempLocalRepository.getConnection();
        try {
            tempLocalConnection.prepareTupleQuery(language, query);
            logger.info("Query Sesame is a tuple query!");
            return mRepositoryConnection.prepareTupleQuery(language, query);
        } catch (RepositoryException | MalformedQueryException e) {
            logger.error(e.getMessage(), e);
        }
        try {
            tempLocalConnection.prepareBooleanQuery(language, query);
            //BooleanQuery booleanQuery = mRepositoryConnection.prepareBooleanQuery(language, query);
            //if(booleanQuery!=null){ return booleanQuery;}
            logger.info("Query Sesame is a boolean query!");
            return mRepositoryConnection.prepareBooleanQuery(language, query);
        } catch (RepositoryException | MalformedQueryException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            tempLocalConnection.prepareGraphQuery(language, query);
            //GraphQuery graphQuery = mRepositoryConnection.prepareGraphQuery(language, query);
            //if(graphQuery!=null){return graphQuery;}
            logger.info("Query Sesame is a graph query!");
            return mRepositoryConnection.prepareGraphQuery(language, query);
        } catch (RepositoryException | MalformedQueryException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Method utility: cast a query SPARQL/SERQL to a Operation Object for decide if
     * is a SELECT/DESCRIBE/CONSTRUCTOR/ASK/UPDATE/ecc.
     * @param query string content of the query SPARQL/SERQL.
     * @return the OpenRDF Operation.
     */
    private static Operation prepareOperation(String query){
        try {
            Repository tempLocalRepository = new SailRepository(new MemoryStore());
            tempLocalRepository.initialize();
            RepositoryConnection tempLocalConnection = tempLocalRepository.getConnection();
            try {
                for (QueryLanguage language : queryLanguages) {
                    try {
                        tempLocalConnection.prepareUpdate(language, query);
                        logger.info("Query SPARQL is a update query");
                        return mRepositoryConnection.prepareUpdate(language, query);
                    } catch (Exception ignored) {
                        //TODO manage the parse exception of sesame
                        logger.warn("Make sure the server sesame is up:"+ignored.getMessage().replace("\n"," "));
                    }
                }
                for (QueryLanguage language : queryLanguages) {
                    try {
                        Query result = prepareQuery(query, language, tempLocalConnection);
                        if (result != null) return result;
                    } catch (Exception ignored) {
                        //continue;
                        logger.warn(ignored.getMessage(),ignored);
                    }
                }
                logger.error("Can't prepare this query SPARQL/SERQL in any language");
                return null;
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            } finally {
                tempLocalConnection.close();
                tempLocalRepository.shutDown();
            }
            return null;
        }catch(RepositoryException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method utility: List of all QUERY languahe supported from OpenRDF API.
     */
    private static final QueryLanguage[] queryLanguages = new QueryLanguage[] {
            QueryLanguage.SPARQL,QueryLanguage.SERQL, QueryLanguage.SERQO };

    /**
     * Metho to convert a String of the language, to a queryLanguage.
     * @param queryLanguage the string QueryLanguage of OpenRDF.
     * @return the QueryLanguage of OpenRDF.
     */
    public QueryLanguage stringToQueryLanguage(String queryLanguage){
        String strLang="";
        if(queryLanguage.equalsIgnoreCase("SPARQL")) strLang = "SPARQL";
        if(queryLanguage.equalsIgnoreCase("SeRQL")) strLang = "SeRQL";
        if(queryLanguage.equalsIgnoreCase("SeRQO")) strLang = "SeRQO";
        for (QueryLanguage lang : queryLanguages) {
            if (lang.getName().equalsIgnoreCase(strLang))
                return lang;
        }
        logger.warn("The Query Language '" + strLang + "' is not recognised");
        throw new IllegalArgumentException("The Query Language '" + strLang + "' is not recognised");
    }

    /**
     * Method to execute a query SPARQL/SERQL on the Sesame Repository.
     * @param query string content of the query SPARQL/SERQL.
     */
    public void executeQuerySPARQLFromString(String query){
        executeSingleQuery(query);
    }

    /**
     * Method to execute a query SPARQL/SERQL on the Sesame Repository.
     * @param query string content of the query SPARQL/SERQL.
     */
    private void executeSingleQuery(String query) {
        try {
            Operation preparedOperation = prepareOperation(query);
            if (preparedOperation == null) {
                logger.warn("Unable to parse query: " + query);
                return;
            }
            //If the Query is a Update..........
            if( preparedOperation instanceof Update) {
                ( (Update) preparedOperation).execute();
                mRepositoryConnection.commit();
                logger.info("Execute Update Query: " + preparedOperation.toString() );
                return;
            }
            //If the Query is a Ask..........
            if (preparedOperation instanceof BooleanQuery) {
                logger.info("Result Boolean Query: " + ((BooleanQuery) preparedOperation).evaluate());
                return;
            }
            //If the Query is a Constructor..........
            if (preparedOperation instanceof GraphQuery) {
                GraphQuery q = (GraphQuery) preparedOperation;
                if(PRINT_RESULT_QUERY){
                    writeGraphQueryResultToFile(query, OUTPUTFILE, OUTPUTFORMAT);
                }
                //long queryBegin = System.nanoTime();
                GraphQueryResult result = q.evaluate();
                int rows = 0;
                while (result.hasNext()) {
                    rows++;
                    if (SHOWSTATS) {
                        Statement statement = result.next();
                        Resource context = statement.getContext();
                        if (rows == 0 && context != null) {
                            logger.info(" ===== " + beautifyRDFValue(context) + " ===== \t");
                        }
                        logger.info(beautifyStatement(statement));
                    }
                }
                logger.info("\n---------------------------------------------\n");
                result.close();
                //long queryEnd = System.nanoTime();
                //SystemLog.message(rows + " result(s) in " + (queryEnd - queryBegin) / 1000000 + "ms.");
            }
            //If the Query is a Select or a Describe..........
            if (preparedOperation instanceof TupleQuery) {
                TupleQuery q = (TupleQuery) preparedOperation;
                if(PRINT_RESULT_QUERY){
                    writeTupleQueryResultToFile(query, OUTPUTFILE, OUTPUTFORMAT);
                }
                //long queryBegin = System.nanoTime();
                TupleQueryResult result = q.evaluate();
                int rows = 0;
                while (result.hasNext()) {
                    if (SHOWSTATS) {
                        BindingSet bindingSetTuples = result.next();
                        if (rows == 0) {
                            for (Binding bindingSetTuple : bindingSetTuples) {
                                logger.info(" ===== " + bindingSetTuple.getName() + " ===== \t");
                            }
                        }
                        for (Binding aTuple : bindingSetTuples) {
                            try {
                                logger.info(beautifyRDFValue(aTuple.getValue()) + "\t");
                            } catch (Exception e) {
                                logger.error(e.getMessage(),e);
                            }
                        }
                    }
                    rows++;
                }
                logger.info("\n---------------------------------------------\n");
                result.close();
                //long queryEnd = System.nanoTime();
                //SystemLog.message(rows + " result(s) in " + (queryEnd - queryBegin) / 1000000 + "ms.");
            }
        } catch (UpdateExecutionException|QueryEvaluationException e) {
           logger.error("An error occurred during query execution", e);
        } catch (RepositoryException e){
            logger.error("An error occurred during the writing of the result of SPARQL query", e);
        }
    }

    /**
     * Creates a statement and adds it to the repository.
     * Then deletes this statement and checks to make sure it is gone.
     * @param subjURI string uri of the resousrce subject of the statement.
     * @param pred uri of the predicate of the statement.
     * @param objURI string uri of the object of the statement.
     */
    public void insertAndDeleteStatement(String subjURI,URI pred,String objURI){
        try{
            if (UPDATES) {
                logger.info("===== Upload and Delete Statements ====================");
                // Add a statement directly to the SAIL
                logger.info("----- Upload and check --------------------------------");
                // first, insert the RDF nodes for the statement
                URI subj = mRepository.getValueFactory().createURI(subjURI);
                //URI pred = RDF.TYPE;
                URI obj = mRepository.getValueFactory().createURI(objURI);

                mRepositoryConnection.add(subj, pred, obj);
                mRepositoryConnection.commit();

                // Now check whether the new statement can be retrieved
                RepositoryResult<Statement> iter = mRepositoryConnection.getStatements(subj, null, obj, true);
                boolean retrieved = false;
                while (iter.hasNext()) {
                    retrieved = true;
                    System.out.println(beautifyStatement(iter.next()));
                }
                iter.close();

                if (!retrieved)logger.warn("Failed to retrieve the statement that was just added.");
                // Remove the above statement in a separate transaction
                logger.info("----- Remove and check --------------------------------");
                mRepositoryConnection.remove(subj, pred, obj);
                mRepositoryConnection.commit();
                // Check whether there is some statement matching the subject of the
                // deleted one
                iter = mRepositoryConnection.getStatements(subj, null, null, true);
                retrieved = false;
                while (iter.hasNext()) {
                    retrieved = true;
                    System.out.println(beautifyStatement(iter.next()));
                }
                // CLOSE the iterator to avoid memory leaks
                iter.close();
                if (retrieved)logger.warn("Statement was not deleted properly in last step.");
            }
        }catch(RepositoryException e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Export the contents of the repository (explicit, implicit or all statements) to the given filename in
     * the given RDF format.
     *  This approach to making a backup of a repository by using RepositoryConnection.exportStatements()
     * will work even for very large remote repositories, because the results are streamed to the client
     * and passed directly to the RDFHandler.
     * However, it is not possible to give any indication of progress using this method.
     * @param outputPathFile string path to the output file.
     * @param outputFormat string format for the output file.
     * @param exportType e.home //explicit,implicit,all,specific.
     * @return the File of triple extract form the Sesame Repository.
     */
    public File export(String outputPathFile,String outputFormat,String exportType){
        try{
            if (outputPathFile != null) {
                if(new File(outputPathFile).exists()) {
                    logger.info("===== Export ====================");
                    RDFFormat exportFormat = stringToRDFFormat(outputFormat);
                    //String type = exportType;
                    logger.info("Exporting " + exportType + " statements to " +
                            outputPathFile + " (" + exportFormat.getName() + ")");
                    Writer writer = new BufferedWriter(new FileWriter(outputPathFile), 256 * 1024);
                    RDFWriter rdfWriter = Rio.createWriter(exportFormat, writer);
                    try {
                        if (exportType == null || exportType.equalsIgnoreCase("explicit"))
                            mRepositoryConnection.exportStatements(null, null, null, false, rdfWriter);
                        else if (exportType.equalsIgnoreCase("all"))
                            mRepositoryConnection.exportStatements(null, null, null, true, rdfWriter);
                        else if (exportType.equalsIgnoreCase("implicit"))
                            mRepositoryConnection.exportStatements(null, null, null, true, rdfWriter,
                                    new URIImpl("http://www.ontotext.com/implicit"));
                        else {
                            logger.warn(
                                    "Unknown export type '" + exportType +
                                            "' - valid values are: explicit, implicit, all, by default we use 'all'"
                            );
                            mRepositoryConnection.exportStatements(null, null, null, true, rdfWriter);
                        }
                    } catch (RepositoryException|RDFHandlerException e) {
                        logger.error(e.getMessage(), e);
                        return null;
                    } finally {
                        writer.close();
                    }
                    return new File(outputPathFile);
                }else throw new IOException("The File where export the result not exists, create it!");
            }//end if
            else throw new IOException("The File where export the result not exists, create it!");
        }catch(UnsupportedRDFormatException|IOException e){
            logger.error(e.getMessage(), e);
            logger.warn("Attention the export File of the Sesame repository return a null File Object");
        }
        return null;
    }

    /**
     * Parse the query file and return the queries defined there for further evaluation. The file can contain
     * several queries; each query starts with an id enclosed in square brackets '[' and ']' on a single line;
     * the text in between two query ids is treated as a SeRQL query. Each line starting with a '#' symbol
     * will be considered as a single-line comment and ignored. Query file syntax example:
     *
     * #some comment [queryid1] <query line1> <query line2> ... <query linen> #some other comment
     * [nextqueryid] <query line1> ... <EOF>
     *
     * @param queryFile string path to the file test.rq with the SPARQL queries to evalutate.
     * @return an array of strings containing the queries. Each string starts with the query id followed by
     *         ':', then the actual query string.
     */
    private static String[] collectQueries(String queryFile){
        try {
            List<String> queries = new ArrayList<>();
            String[] result;
            try (BufferedReader input = new BufferedReader(new FileReader(queryFile))) {
                String nextLine = null;
                for (;;) {
                    String line = nextLine;
                    nextLine = null;
                    if (line == null) {
                        line = input.readLine();
                    }
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (line.startsWith("^[") && line.endsWith("]")) {
                        StringBuilder buff = new StringBuilder(line.substring(2, line.length() - 1));
                        buff.append(": ");

                        for (;;) {
                            line = input.readLine();
                            if (line == null) {
                                break;
                            }
                            line = line.trim();
                            if (line.length() == 0) {
                                continue;
                            }
                            if (line.startsWith("#")) {
                                continue;
                            }
                            if (line.startsWith("^[")) {
                                nextLine = line;
                                break;
                            }
                            buff.append(line);
                            buff.append(System.getProperty("line.separator"));
                        }
                        queries.add(buff.toString());
                    }
                }   result = new String[queries.size()];
                for (int i = 0; i < queries.size(); i++) {
                    result[i] = queries.get(i);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }


    /**
     * Method utility: nicely format an RDF statement.
     *
     * @param statement
     *            The statement to be formatted.
     * @return The beautified statement.
     */
    private String beautifyStatement(Statement statement){
        return beautifyRDFValue(statement.getSubject()) + " " + beautifyRDFValue(statement.getPredicate())
                + " " + beautifyRDFValue(statement.getObject())+". ";
    }

    /**
     * Method utility: printing an RDF value in a "fancy" manner.
     * In case of URI, qnames are printed for better readability
     *
     * @param value The value to beautify.
     * @return string to print on a file on to the console.
     */
    private String beautifyRDFValue(Value value){
        if (value instanceof URI) {
            URI u = (URI) value;
            String namespace = u.getNamespace();
            String prefix = getNamespacePrefixesFromRepository().get(namespace);
            if (prefix == null) {
                prefix = u.getNamespace();
            } else {
                prefix += ":";
            }
            String sReturn = prefix + u.getLocalName();
            if(sReturn.contains("<") && sReturn.contains(">")){
                return sReturn;
            }else{
                //sReturn = sReturn.replaceAll("|", "").replaceAll("^", "").replaceAll("\n", "").replaceAll("'", "");
                return "<"+sReturn+">";
            }
        } else {
            return value.toString().trim().replaceAll("\n", "");
        }
    }


    /**
     * Method to write a Sesame Model to a specific file.
     * @param model the Sesame Model.
     * @param outputPathFile string path the output file of triple.
     * @param outputFormat string of the RDFFormat you choose.
     */
    public void writeSesameModelToFile(Model model,String outputPathFile,String outputFormat) {
        // a collection of several RDF statements
        try{
            FileOutputStream out = new FileOutputStream(outputPathFile);
            RDFWriter writer = Rio.createWriter(stringToRDFFormat(outputFormat), out);
            writer.startRDF();
            for (Statement st: model) {
                writer.handleStatement(st);
            }
            writer.endRDF();
        } catch (RDFHandlerException|FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * Method to "prepare a query" from a string value to a Query parsed value.
     * @param query the string query SPARQL or SERQL.
     * @param language SPAQL or SERQL.
     * @return the result Query object of the String.
     */
    private static Query prepareQuery(String query, QueryLanguage language){
        try{
            Repository tempRepository = new SailRepository(new MemoryStore());
            tempRepository.initialize();
            RepositoryConnection tempConnection = tempRepository.getConnection();
            try {
                try {
                    tempConnection.prepareTupleQuery(language, query);
                    return mRepositoryConnection.prepareTupleQuery(language, query);
                } catch (MalformedQueryException|RepositoryException e) {
                    logger.error(e.getMessage(), e);
                }

                try {
                    tempConnection.prepareBooleanQuery(language, query);
                    return mRepositoryConnection.prepareBooleanQuery(language, query);
                } catch (MalformedQueryException|RepositoryException e) {
                    logger.error(e.getMessage(),e);
                }

                try {
                    tempConnection.prepareGraphQuery(language, query);
                    return mRepositoryConnection.prepareGraphQuery(language, query);
                } catch (MalformedQueryException|RepositoryException e) {
                    logger.error(e.getMessage(), e);
                }
                return null;
            } finally {
                tempConnection.close();
                tempRepository.shutDown();
            }
        }catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    //Method to write more pretty your model

  /*  public static RDFWriter getPrettyWriter() {
        org.openrdf.rio.RDFWriter rdfWriter = null;
        com.hp.hpl.jena.rdf.model.RDFWriterF rdfWriterF = new com.hp.hpl.jena.rdf.model.impl.RDFWriterFImpl();
        try { //
            rdfWriter = (org.openrdf.rio.RDFWriter) rdfWriterF.getWriter("RDF/XML-ABBREV"); //
            rdfWriter.handleNamespace("rdf", com.hp.hpl.jena.vocabulary.RDF.getURI());
            rdfWriter.handleNamespace("dc", com.hp.hpl.jena.vocabulary.DC_10.getURI()); //
            rdfWriter.handleNamespace("rss", com.hp.hpl.jena.vocabulary.RSS.getURI()); //
            //rdfWriter.handleNamespacex("idea", IDEA.getURI()); //
            //rdfWriter.handleNamespace("graphic", GRAPHIC.getURI()); //
            //rdfWriter.handleNamespace("fs", FILESYSTEM.getURI()); //
            //rdfWriter.handleNamespace("prj", PROJECT.getURI()); //
            rdfWriter.handleNamespace("foaf", "http://xmlns.com/foaf/0.1/"); //
            rdfWriter.handleNamespace("owl", "http://www.w3.org/2002/07/owl#"); //
            rdfWriter.handleNamespace("ibis", "http://purl.org/ibis#"); //
            rdfWriter.handleNamespace("fs","http://ideagraph.org/xmlns/idea/filesystem#");
            // the encoding was screwing up, so declaration removed
            //rdfWriter.setProperty("showXmlDeclaration", Boolean.FALSE);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return rdfWriter;
    }*/

    /**
     * Method to "prepare a query" from a string value to a Query parsed value.
     * @param query  the string query SPARQL or SERQL.
     * @return the result Query object of the String.
     */
    private static Query prepareQuery(String query) {
        for (QueryLanguage language : queryLanguages) {
            Query result = prepareQuery(query, language);
            if (result != null)
                return result;
        }
        logger.error("Can't prepare this query in any language");
        return null;
    }


    /**
     * Method for open many different repository type of Sesame/Owlim memorized on disk.
     * @param typeRepository choose your type of reposiotry ['owlim,'memory','native','inferencing','http'].
     * @param directory string to the path folder where are stored your repositories.
     * @param repositoryId string id of the repository.
     * @return repository sesame for the specific connection.
     */
    public Repository connectToLocal(
            String typeRepository,String directory,String repositoryId){
        return connectToSpecificRepository(typeRepository, directory, repositoryId);
    }

    /**
     * Method for open many different repository type of Sesame/Owlim memorized on server.
     * @param typeRepository  choose your type of reposiotry ['owlim,'memory','native','inferencing','http'].
     * @param sesameServer url address to the sesame setver eg http://localhost:8080/openrdf-sesame/.
     * @param repositoryId string id of the repository.
     * @return repository sesame for the specific connection.
     */
    public Repository connectToRemote(
            String typeRepository,String sesameServer,String repositoryId){
        return connectToSpecificRepository(typeRepository,sesameServer,repositoryId);
    }

    /**
     * Method for make the connection to a repository.
     * @return repository sesame for the specific connection.
     */
    private Repository connectToSpecificRepository(String typeRepository,String directoryOrServer,String idRepository){
        try{
            mRepository = null;
            logger.info(
                    "Try to open a connection to a repository Sesame of TYPE:" + typeRepository + " and ID:" + idRepository + "...");
            //if(typeRepository.toLowerCase().contains("owlim")){
                 /*
                 com.ontotext.trree.OwlimSchemaRepository schema = new com.ontotext.trree.OwlimSchemaRepository();
                 schema.setDataDir(Datadir);
                 schema.setParameter("storage-folder", directoryOrServer);
                 schema.setParameter("repository-type", "owlim");
                 schema.setParameter("ruleset", RULESET);*
                 mRepository  = new org.openrdf.repository.sail.SailRepository(schema);
                */
            //}
            //Create and initialize a non-inferencing main-memory repository
            //the MemoryStore will write its contents to the directory so that
            //it can restore it when it is re-initialized in a future session
            if(typeRepository.toLowerCase().contains("memory")){
                mRepository =  new SailRepository(
                        new MemoryStore(new File(directoryOrServer))
                        // .setDataDir(Datadir)
                );
                //or
                /*
                MemoryStore memStore= new org.openrdf.sail.memory.MemoryStore(new File(PATH_FOLDER_REPOSITORY) );
                Repository repo = new org.openrdf.repository.sail.SailRepository(memStore);
                */
            }
            //Creating a Native RDF Repository
            //does not keep data in main memory, but instead stores it directly to disk
            else if(typeRepository.toLowerCase().contains("native")){
                String indexes = "spoc,posc,cosp";
                mRepository = new SailRepository(new NativeStore(new File(directoryOrServer),indexes)
                );
            }
            //Creating a repository with RDF Schema inferencing
            //ForwardChainingRDFSInferencer is a generic RDF Schema
            //inferencer (MemoryStore and NativeStore support it)
            else if(typeRepository.toLowerCase().contains("inferencing")){
                mRepository = new SailRepository( new ForwardChainingRDFSInferencer(new MemoryStore() ));
            }
            else if(typeRepository.toLowerCase().contains("http")){
                //Accessing a server-side repository
                mRepository= new HTTPRepository(directoryOrServer, idRepository);
            } else{
                logger.warn("Attention type a correct String typeRepository:" + Arrays.toString(types));
            }
            // wrap it into a Sesame SailRepository
            if(mRepository != null && !mRepository.isInitialized()){
                try {
                    mRepository.initialize();
                    return mRepository;
                } catch (RepositoryException e) {
                    logger.error(e.getMessage(),e);
                    // Something went wrong during the transaction, so we roll it back
                    mRepository.getConnection().rollback();
                }finally{
                    // Whatever happens, we want to close the connection when we are done.
                    mRepository.getConnection().close();
                }
            }
            return mRepository;
        }catch(RepositoryException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    // Method to convert a Sesame Dataset to a JenaModel

//    public static com.hp.hpl.jena.rdf.model.Model convertSesameDataSetToJenaModel(
//                org.openrdf.repository.Repository repository) throws org.openrdf.repository.RepositoryException{
//        mRepositoryConnection = repository.getConnection();
//        // finally, insert the DatasetGraph instance
//        SesameDataset dataset = new SesameDataset(mRepositoryConnection);
//        //From now on the SesameDataset object can be used through the Jena API
//        //as regular Dataset, e.home. to add some data into it one could something like the
//        //following:
//        com.hp.hpl.jena.rdf.model.Model model =
//                com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(dataset.getDefaultGraph());
//        return model;
//    }

    /**
     * Method to support the evalutation o the Tuple query
     * @param queryString the query sparql SELECT or ASK.
     * @param bindingName the Array of String of Filed of the Tuple Query.
     * @return the List of OpenRDF Statement.
     */
    public List<String[]> TupleQueryEvalutation(String queryString,String[] bindingName){
        List<String[]> list = new ArrayList<>();
        try {
            if(!(mRepository.isInitialized() && mRepository.getConnection().isOpen())){
                mRepository.initialize();
                mRepositoryConnection = mRepository.getConnection();
            }
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            TupleQuery tupleQuery = mRepositoryConnection.prepareTupleQuery(
                    lang, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
                int L;
                if(bindingName.length ==0){ //is empty
                    L = result.getBindingNames().size();
                    bindingName = new String[result.getBindingNames().size()];
                    bindingName = result.getBindingNames().toArray(bindingName);
                }
                else L = bindingName.length;

                String[] info;
                while (result.hasNext()) {
                    info = new String[L];
                    BindingSet bindingSet = result.next();
                    for(int i =0; i < info.length;) {
                        Value firstValue = bindingSet.getValue(bindingName[i]); //get ?x
                        info[i] = firstValue.stringValue();
                    }
                    list.add(info);
                }
            } finally {
                result.close();
            }
        } catch (OpenRDFException e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * Method to support the evalutation o the Graph query.
     * @param queryString the query sparql CONSTRUCTOR or DESCRIBE.
     * @return the List of OpenRDF Statement.
     */
    public List<Statement> GraphQueryEvalutation(String queryString){
        List<Statement> list = new ArrayList<>();
        try {
            if(!(mRepository.isInitialized() && mRepository.getConnection().isOpen())){
                mRepository.initialize();
                mRepositoryConnection = mRepository.getConnection();
            }
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            GraphQueryResult result = mRepositoryConnection.prepareGraphQuery(
                    lang,queryString).evaluate();
            try {
                while (result.hasNext()) {
                    Statement stmt = result.next();
                    list.add(stmt);
                    // ... do something with the resulting statement here.
                }
            } finally {
                result.close();
            }
        } catch (OpenRDFException e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * Method to convert the result of a GraphQuery to a Model Sesame.
     * @param repository repository where you want evalutate the quey SPARQL.
     * @param queryString the query sparql CONSTRUCTOR or DESCRIBE.
     * @return model filled with the result of the quey on the repository connection.
     */
    public Model convertGraphQueryEvalutationToSesameModel(Repository repository,String queryString){
        Model resultModel = new TreeModel();
        try {
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            GraphQueryResult result = mRepositoryConnection.prepareGraphQuery(
                    lang,queryString ).evaluate();
            try {
                resultModel = QueryResults.asModel(result);
            } finally {
                result.close();
            }
        } catch (OpenRDFException e) {
            logger.error(e.getMessage(), e);
        }
        return resultModel;
    }



    /**
     * Method to print the query result of Graph Query on a Sesame Repository.
     * @param queryString string SPARQL/SERQL query.
     * @param filePath string file path to the output file.
     * @param outputFormat string of the output format.
     * @param repositoryConnection the current RepositoryConnection of Sesame.
     */
    public void writeGraphQueryResultToFile(String queryString,String filePath,
                                            String outputFormat,RepositoryConnection repositoryConnection){
        try {
            outputFormat = outputFormat.replaceAll("[^A-Za-z0-9]", "");
            String nameFileOut = filePath+"."+outputFormat.toLowerCase();
            OutputStream fileOut = new FileOutputStream( filePath+"."+outputFormat.toLowerCase());
            logger.info("Try to write the query graph result in the format:" + stringToRDFFormat(outputFormat).toString() +
                    " int o the file " + nameFileOut + "...");
            RDFWriter writer;
            if(filePath == null){
                writer = Rio.createWriter(stringToRDFFormat(outputFormat), System.out);
            }else {
                writer = Rio.createWriter(stringToRDFFormat(outputFormat), fileOut);
            }
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            repositoryConnection.prepareGraphQuery(lang, queryString).evaluate(writer);
            logger.info("... the file " + nameFileOut + " is been written!!!");
        } catch (FileNotFoundException|RepositoryException|MalformedQueryException|RDFHandlerException|QueryEvaluationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Method to print the query result of Graph Query on a Sesame Repository.
     * @param queryString string SPARQL/SERQL query.
     * @param filePath string file path to the output file.
     * @param outputFormat string of the output format.
     */
    public void writeGraphQueryResultToFile(String queryString,String filePath,String outputFormat){
        writeGraphQueryResultToFile(queryString,filePath,outputFormat,mRepositoryConnection);
    }

    /**
     * Method to print the query result of Graph Query on a Sesame Repository.
     * @param graphQuery Query Graph OpenRDF SPARQL/SERQL .
     * @param filePath string file path to the output file.
     * @param outputFormat string of the output format.
     */
    public void writeGraphQueryResultToFile(GraphQuery graphQuery,String filePath,String outputFormat){
            writeGraphQueryResultToFile(graphQuery.toString(),filePath,outputFormat);
    }

    /**
     * Method to print the query result of Graph Query on a Sesame Repository.
     * @param graphQuery Query Graph OpenRDF SPARQL/SERQL .
     * @param file the File path to the output file.
     * @param outputFormat string of the output format.
     */
    public void writeGraphQueryResultToFile(GraphQuery graphQuery,File file,String outputFormat){
        writeGraphQueryResultToFile(graphQuery.toString(),file.getAbsolutePath(),outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param graphQuery Query Graph OpenRDF SPARQL/SERQL .
     * @param outputFormat string of the output format.
     */
    public void writeGraphQueryResultToConsole(GraphQuery graphQuery,String outputFormat) {
        writeGraphQueryResultToFile(graphQuery.toString(), null, outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param queryString string of SPARQL/SERQL query.
     * @param outputFormat string of the output format.
     */
    public void writeGraphQueryResultToConsole(String queryString,String outputFormat) {
        writeGraphQueryResultToFile(queryString, null, outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param tupleQuery the OpenRDF TupleQuery query.
     * @param filePath string of the path to the output file.
     * @param outputFormat string of the output format.
     */
    public void writeTupleQueryResultToFile(TupleQuery tupleQuery,String filePath,String outputFormat){
        writeTupleQueryResultToFile(tupleQuery.toString(), filePath, outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param tupleQuery the OpenRDF TupleQuery query.
     * @param file the File path to the output file.
     * @param outputFormat string of the output format.
     */
    public void writeTupleQueryResultToFile(TupleQuery tupleQuery,File file,String outputFormat){
        writeTupleQueryResultToFile(tupleQuery.toString(), file.getAbsolutePath(), outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param tupleQuery the OpenRDF TupleQuery query.
     * @param outputFormat string of the output format.
     */
    public void writeTupleQueryResultToConsole(TupleQuery tupleQuery,String outputFormat) {
        writeTupleQueryResultToFile(tupleQuery.toString(), null, outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param queryString string of SPARQL/SERQL query.
     * @param outputFormat string of the output format.
     */
    public void writeTupleQueryResultToConsole(String queryString,String outputFormat) {
        writeTupleQueryResultToFile(queryString, null, outputFormat);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param queryString string of SPARQL/SERQL query.
     * @param filePath string of the path to the file of output.
     * @param outputFormat string of the output format.
     */
    public void writeTupleQueryResultToFile(String queryString,String filePath,String outputFormat){
        writeTupleQueryResultToFile(queryString,filePath,outputFormat,mRepositoryConnection);
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param queryString string of SPARQL/SERQL query.
     * @param filePath string of the path to the file of output.
     * @param outputFormat string of the output format.
     * @param repositoryConnection the current RepositoryConnection of Sesame.
     */
    public void writeTupleQueryResultToFile(String queryString,String filePath,
                                            String outputFormat,RepositoryConnection repositoryConnection){
        try {
            outputFormat = outputFormat.replaceAll("[^A-Za-z0-9]", "");
            String nameFileOut = filePath+"."+outputFormat.toLowerCase();
            logger.info("Try to write the query tuple result in the format:" +outputFormat +
                    " int o the file " + nameFileOut + "...");
            OutputStream out;
            TupleQueryResultHandler trh=null;
            if(filePath==null){
                out = System.out;
            }else{
                out = new FileOutputStream(new File(filePath+"."+outputFormat));
            }

            if(outputFormat.equalsIgnoreCase("csv")){
                trh = new org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter(out);
            }else if(outputFormat.equalsIgnoreCase("json")){
                trh = new org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter(out);
            }else if(outputFormat.equalsIgnoreCase("tsv")){
                trh = new org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter(out);
            }else if(outputFormat.equalsIgnoreCase("xml")){
                trh = new org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter(out);
            }else if(outputFormat.equalsIgnoreCase("tsv")) {
                trh = new org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter(out);
//                }else if(outputFormat.equalsIgnoreCase("tablehtml")){
//                    trh = new org.openrdf.query.resultio.TupleQueryResultFormat.TSV;
//                }
            }else {
                RDFWriter writer = Rio.createWriter(stringToRDFFormat(outputFormat), out);
            }
            QueryLanguage lang = checkLanguageOfQuery(queryString, repositoryConnection);
            repositoryConnection.prepareTupleQuery(lang, queryString).evaluate(trh);
            logger.info("...the result of the tuple query is been written " + filePath);
        } catch (OpenRDFException|FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Method to convert a file to another specific format
     * @param urlFile string url to the file.
     * @param inputFormat string check the input format null.
     * @param outputFormat string of the output format.
     */
    public void convertFileNameToRDFFormat(String urlFile,String inputFormat,String outputFormat) {
        try {
            if(StringUtilities.isNullOrEmpty(inputFormat)) inputFormat ="n3";
            // open our input document
            URL documentUrl;
            RDFFormat format;
            InputStream inputStream;
            if(StringUtilities.isURL(urlFile)){
                documentUrl = new URL(urlFile);
                //AutoDetecting the file format
                format = convertFileNameToRDFFormat(documentUrl.toString());
                //RDFFormat format2 = Rio.getParserFormatForMIMEType("contentType");
                // RDFParser rdfParser = Rio.createParser(format);
                inputStream = documentUrl.openStream();
            }else{
                urlFile = FileUtilities.toStringUriWithPrefix(urlFile);
                //documentUrl = new URL("file::///"+FileUtil.convertFileToUri(urlFile));
                documentUrl = new URL(urlFile);
                format = stringToRDFFormat(inputFormat) ;
                inputStream = documentUrl.openStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            // insert a parser for Turtle and a writer for RDF/XML
            RDFParser rdfParser =  Rio.createParser(format);
            RDFWriter rdfWriter =  Rio.createWriter(stringToRDFFormat(outputFormat),
                    new FileOutputStream(urlFile + "." + outputFormat));
            // link our parser to our writer...
            rdfParser.setRDFHandler(rdfWriter);
            // ...and start the conversion!
            rdfParser.parse(in, documentUrl.toString());
        } catch (IOException|RDFParseException|RDFHandlerException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Method utility: Get RDFFormat from the file name.
     * @param filePath String to the path to the file.
     * @return correspondent RDFFormat.
     */
    public RDFFormat convertFileNameToRDFFormat(String filePath){
        //version 2.8.X
        return Rio.getParserFormatForFileName(filePath,RDFFormat.RDFXML);
        //version 4.0.X
        /*Rio.createParser(RDFFormat.RDFXML);
        return Rio.getParserFormatForFileName(filePath);*/
    }

    /**
     * Method utility: Get RDFFormat from the file name.
     * @param filePath File to the path to the file.
     * @return correspondent RDFFormat.
     */
    public RDFFormat convertFileNameToRDFFormat(File filePath){
        return convertFileNameToRDFFormat(filePath.getAbsolutePath());
    }

    /**
     * Field a list of RDF file formats used in loadFile().
     */
    private static final RDFFormat allFormats[] =
            new RDFFormat[] {
                    RDFFormat.NTRIPLES, RDFFormat.N3,
                    RDFFormat.RDFXML, RDFFormat.TURTLE,
                    RDFFormat.TRIG, RDFFormat.TRIX,
                    RDFFormat.NQUADS, RDFFormat.JSONLD ,
                    RDFFormat.RDFA, RDFFormat.RDFJSON};

    /**
     * Method to convert a string to a or.openrdf.rio.RDFFormat.
     * @param strFormat string of format.
     * @return correspondent RDFORMAT.
     */
    private static RDFFormat stringToRDFFormat(String strFormat) {
        if(strFormat.equalsIgnoreCase("NT")||strFormat.equalsIgnoreCase("N3")
                ||strFormat.equalsIgnoreCase("NTRIPLES")||strFormat.equalsIgnoreCase("N-TRIPLES")){
            strFormat = "N-Triples";
        }
        if(strFormat.equalsIgnoreCase("TTL")||strFormat.equalsIgnoreCase("TURTLE")){
            strFormat = "TURTLE";
        }
        for (RDFFormat format : allFormats) {
            if (format.getName().equalsIgnoreCase(strFormat))
                return format;
        }
        logger.error("The RDF format '" + strFormat + "' is not recognised");
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
    }

    /**
     * Method for import to the repository a very large file of triple
     * pre-chunked for the import.
     * @param file file to import ot the sesame repository.
     * @return long result of the import.
     */
    public long importIntoRepositoryFileChunked(File file){
        String CHUNK_SIZE = "500000";
        String CONTEXT = "context";
        try {
            logger.info("Loading " + file.getName() + " ");
            //Creating the right parser for the right format
            //RDFFormat format = RDFFormat.forFileName(file.getName());
            RDFFormat format = convertFileNameToRDFFormat(file.getName());
            if (format == null) {
                logger.warn("Unknown RDF format for file: " + file);
                return 0;
            }

            URI dumyBaseUrl = new URIImpl(file.toURI().toString());

            URI context = null;
            if (!format.equals(RDFFormat.NQUADS) && !format.equals(RDFFormat.TRIG) && !format.equals(RDFFormat.TRIX)) {
                context = new URIImpl(CONTEXT);
            }
            InputStream reader = null;
            try {
                if (file.getName().endsWith("gz")) {
                    reader = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file), 256 * 1024));
                } else {
                    reader = new BufferedInputStream(new FileInputStream(file), 256 * 1024);
                }
                // create a parser home.home.initializer.org.p4535992.mvc.config with preferred settings

                //RioSetting settings = new RioS
                RioSetting<Boolean> verifyDataSet = BasicParserSettings.VERIFY_DATATYPE_VALUES;
                RioSetting<Boolean> stopAtFirstErrorSet = BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES;
                RioSetting<Boolean> preserveBnodeIdsSet = BasicParserSettings.PRESERVE_BNODE_IDS;

                //ParserConfig config = new ParserConfig(verifyData, stopAtFirstError, preserveBnodeIds, RDFParser.DatatypeHandling.VERIFY);
                ParserConfig config = new ParserConfig();
                config.set(verifyDataSet, VERIFY);
                config.set(stopAtFirstErrorSet, STOP_ON_ERROR);
                config.set(preserveBnodeIdsSet, PRESERVE_BNODES);

                long chunkSize = Long.parseLong(CHUNK_SIZE);
                long start = System.currentTimeMillis();
                // set the parser configuration for our connection
                mRepositoryConnection.setParserConfig(config);
                RDFParser parser = Rio.createParser(format);
                parser.setParserConfig(config);

                // add our own custom RDFHandler to the parser. This handler takes care of adding
                // triples to our repository and doing intermittent commits
                ChunkCommitter handler = new ChunkCommitter(mRepositoryConnection, context, chunkSize);
                parser.setRDFHandler(handler);
                mRepositoryConnection.commit();
                mRepositoryConnection.begin();

                //Mitac hack: use parallel update for owlim repository
                /*if (PARALLEL_LOAD) {
                    URI up = new URIImpl("http://www.ontotext.com/useParallelInsertion");
                    mRepositoryConnection.add(up, up, up);
                }*/
                parser.parse(reader, context == null ? dumyBaseUrl.toString() : context.toString());
                mRepositoryConnection.commit();
                long statementsLoaded = handler.getStatementCount();
                long time = System.currentTimeMillis() - start;
                System.out.println("Loaded " + statementsLoaded + " statements in " + time + " ms; avg speed = "
                        + (statementsLoaded * 1000 / time) + " st/s");
                return statementsLoaded;
            } catch (RepositoryException|RDFParseException|RDFHandlerException e) {
                mRepositoryConnection.rollback();
                System.out.println();
                logger.warn("Failed to load '" + file.getName() + "' (" + format.getName() + ")." + e);
                return 0;
            } finally {
                if (reader != null)reader.close();
                mRepositoryConnection.close();
            }
        }catch(RepositoryException|IOException e){
            logger.error(e.getMessage(), e);
        }
        return 0;
    }


    /**
     * Method for try to create a repository programmatically intead from the web interface
     * @param model Sesame model.
     * @param repositoryNode resource where put the sesame model.
     * @param directory String path to the folder.
     * @param repositoryID String id of the repository.
     * @return if true all the operation are done.
     */
    public static boolean createSesameRepository(Model model,Resource repositoryNode,String directory,String repositoryID) {
        try{
            // Create a manager for local repositories
            RepositoryManager repositoryManager = new LocalRepositoryManager(new File(directory));
            repositoryManager.initialize();
            // Create a configuration object from the configuration graph
            // and add it to the repositoryManager
            RepositoryConfig repositoryConfig = RepositoryConfig.create(model, repositoryNode);
            repositoryManager.addRepositoryConfig(repositoryConfig);
            // Get the repository to use
            Repository repository = repositoryManager.getRepository(repositoryID);
            // Open a connection to this repository
            RepositoryConnection repositoryConnection = repository.getConnection();
            // ... use the repository
            // Shutdown connection, repository and manager
            repositoryConnection.close();
            repository.shutDown();
            repositoryManager.shutDown();
            return true;
        }catch(RepositoryException|RepositoryConfigException e){
            logger.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * This class is inspired by Jeen Broekstra.
     * http://rivuli-development.com/further-reading/sesame-cookbook/loading-large-file-in-sesame-native/.
     */
    static class ChunkCommitter implements RDFHandler {

        private long chunkSize;
        private final RDFInserter inserter;
        private final RepositoryConnection conn;
        private URI context;
        private ValueFactory factory;

        private long count = 0L;

        public ChunkCommitter(RepositoryConnection conn) {
            this.inserter = new RDFInserter(conn);
            this.conn = conn;
        }

        public ChunkCommitter(RepositoryConnection conn, URI context, long chunkSize) {
            this.chunkSize = chunkSize;
            this.context = context;
            this.conn = conn;
            this.factory = conn.getValueFactory();
            this.inserter = new RDFInserter(conn);
        }

        public long getStatementCount() {
            return count;
        }

        @Override
        public void startRDF() throws RDFHandlerException {
            inserter.startRDF();
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            inserter.endRDF();
        }

        @Override
        public void handleNamespace(String prefix, String uri)
                throws RDFHandlerException {
            inserter.handleNamespace(prefix, uri);
        }

        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            if(context !=null) {
                st = factory.createStatement(st.getSubject(), st.getPredicate(), st.getObject(), context);
            }
            inserter.handleStatement(st);
            count++;
            // do an intermittent commit whenever the number of triples
            // has reached a multiple of the chunk size
            if (count % chunkSize == 0) {
                try {
                    conn.commit();
                    System.out.print(".");
                    conn.begin();
                } catch (RepositoryException e) {
                    throw new RDFHandlerException(e);
                }
            }
        }

        @Override
        public void handleComment(String comment) throws RDFHandlerException {
            inserter.handleComment(comment);
        }
    }

    /**
     * Method for get statements on a specific repository
     * @param statement statements you wan tot find.
     * @param remove true if you want remove from the repository
     * @param includeInfered true if you want inlcude in the research all inferrred .
     * @return list of statement.
     */
    public static List<Statement> findSpecificStatement(Statement statement,boolean remove,boolean includeInfered){
        RepositoryResult<Statement> statements;
        List<Statement> about;
        try {
            if(statement!=null) {
                statements = mRepositoryConnection.getStatements(
                        statement.getSubject(), statement.getPredicate(), statement.getObject(), includeInfered);
            }else {
                statements = mRepositoryConnection.getStatements(null, null, null, includeInfered);
            }
            //about = info.aduna.iteration.Iterations.addAll(statements, new ArrayList<Statement>());
            about = convertRepositoryResultToListStatements(statements);
            if(remove){ // Then, remove them from the repository
                mRepositoryConnection.remove(about);
            }
            return about;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<Statement> convertRepositoryResultToListStatements(
            RepositoryResult<Statement> iter){
        List<Statement> collection = new ArrayList<>();
        try {
            while (iter.hasNext()) {
                collection.add(iter.next());
            }
            return collection;
        } catch (RepositoryException ex) {
            logger.error("Can't convert the RepositoryResult to a List of Statement");
            return null;
        }
    }

    /**
     * Connect to a managed repository located at the given location
     * and connect to the repository with the given repositoryID.
     * @param repositoryLocation string file path to the repository location.
     * @param repositoryID repsoitory name/ID .
     * @return repository manager.
     */
    public RepositoryManager connectToRepository(String repositoryLocation, String repositoryID) {
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryID);
        return mRepositoryManager;
    }

    /**
     * Connect to a managed repository located at the given location.
     * and connect to the repository with the given repositoryID.
     * @param repositoryLocation URL path to the repository location.
     * @param repositoryID repsoitory name/ID .
     * @return repository manager.
     */
    public RepositoryManager connectToRepository(URL repositoryLocation, String repositoryID) {
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryID);
        return mRepositoryManager;
    }

    /**
     * Connect to a managed repository located at the given location
     * and connect to the repository with the given repositoryID.
     * @param repositoryLocation URL path to the repository location.
     * @param repositoryID repsoitory name/ID .
     * @return repository manager.
     */
    public RepositoryManager connectToRepository(File repositoryLocation, String repositoryID) {
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryID);
        return mRepositoryManager;
    }

    /**
     * Connect to a managed repository location.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local directory repositoryID.
     * @param urlOrDirectory String file path to the repository location.
     * @return repository manager.
     */
    public RepositoryManager connectToLocation(String urlOrDirectory) {
        logger.info("Calling with String: " + urlOrDirectory);
        if(StringUtilities.isURL(urlOrDirectory)) {
            connectToRemoteLocation(urlOrDirectory);
            return mRepositoryManager;
        } else if(new File(urlOrDirectory).exists()){
            connectToLocalLocation(urlOrDirectory);
            return mRepositoryManager;
        } else{
            logger.warn("Not exists the url or the File with path:" + urlOrDirectory);
            return null;
        }
    }

    /**
     * Connect to a managed repository location.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local directory repositoryID.
     * @param urlOrDirectory URL path to the repository location.
     * @return repository manager.
     */
    public RepositoryManager connectToLocation(URL urlOrDirectory) {
        //logger.info("Calling with URL: " + urlOrDirectory);
        try {
            if (StringUtilities.isURL(urlOrDirectory.toString())) {
                connectToRemoteLocation(urlOrDirectory.toString());
                return mRepositoryManager;
            } else if (FileUtilities.toFile(urlOrDirectory).exists()) {
                connectToLocalLocation(urlOrDirectory);
                return mRepositoryManager;
            } else {
                logger.warn("Not exists the url or the File with path:" + urlOrDirectory);
                return null;
            }
        }catch(URISyntaxException|MalformedURLException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Connect to a managed repository location.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local directory repositoryID.
     * @param urlOrDirectory File path to the repository location.
     * @return repository manager.
     */
    public RepositoryManager connectToLocation(File urlOrDirectory) {
        //logger.info("Calling SesameManager.connectToLocation with URL: " + urlOrDirectory);
        if(urlOrDirectory.exists()){
            connectToLocalLocation(urlOrDirectory);
            return mRepositoryManager;
        } else{
            logger.warn("Not exists the url or the File with path:"+urlOrDirectory);
            return null;
        }
    }

    /**
     * Connect to a remote managed repository location.
     * @param url string to the url service.
     * @return reposiotry manager.
     */
    private RepositoryManager connectToRemoteLocation(String url) {
        isManagedRepository = true;
        SesameClient sesameClient = new SesameClientImpl();
        sesameClient.createSparqlSession(url,url);
        mRemoteRepositoryManager =  new RemoteRepositoryManager(url);
        try {
            URL javaurl = new URL(url);
            String userpass = javaurl.getUserInfo();
            if(userpass != null) {
                String[] userpassfields = userpass.split(":");
                if(userpassfields.length != 2) {
                    logger.error("URL has login data but not username and password");
                } else {
                    mRemoteRepositoryManager.setUsernameAndPassword(userpassfields[0], userpassfields[1]);
                }
            }
        } catch(MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Success to connect to the Sesame Repository to the url location:"+url);
        setRepositoryManager(mRemoteRepositoryManager, url);
        return mRepositoryManager;
    }

    /**
     * Method to Connect to a local repository location at the given directory.
     * If mustexist is true, it is an error if the directory is not found.
     * @param directory string file path to the folder/file with all the repositeries.
     * @return Repository Manager.
     */
    private RepositoryManager  connectToLocalLocation(String directory) {
        return connectToLocalLocation(new File(directory));
    }

    /**
     * Method to Connect to a local repository location at the given directory.
     * @param directory string file path to the folder with all the repositeries.
     * @return Repository Manager.
     */
    private RepositoryManager connectToLocalLocation(File directory) {
        try {
            return connectToLocalLocation(FileUtilities.toURL(directory));
        } catch (MalformedURLException e) {
            logger.error("The URL directory not exists or is wrong:" + directory.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     *  Method to Connect to a local repository location at the given directory.
     * @param directory url location of the directory.
     * @return Repository Manager.
     */
    private RepositoryManager connectToLocalLocation(URL directory) {
        isManagedRepository = true;
        logger.info("Called the RepositoryManager to " + directory);
        File dir;
        try {
            dir = new File(directory.toURI());
        } catch (URISyntaxException e) {
            logger.error("Specified URL is invalid: "+directory, e);
            return null;
        }
        if (!dir.exists()) {
            logger.error("Specified path does not exist: " +dir.getAbsolutePath());
            return null;
        }
        if (!dir.isDirectory()) {
            logger.error("Specified path is not a directory: " + dir.getAbsolutePath());
            return null;
        }
        if(!dir.getAbsolutePath().endsWith(File.separator)){
            dir = new File(dir.getAbsolutePath()+File.separator);
        }
        setRepositoryManager(new LocalRepositoryManager(dir));
        logger.info("Success to connect to the Sesame Repository to the url location:"+directory);
        return mRepositoryManager;
    }

    /**
     *  Method to Connect to a local repository location at the given directory.
     * @param directory url location of the directory.
     * @param repositoryID string id of the repository.
     * @return Repository Manager.
     */
    public Repository connectToMemoryRepository(String directory,String repositoryID) {
        try {
            if(directory.startsWith(File.separator)) directory = directory.substring(1, directory.length());
            if(!directory.endsWith(File.separator)) directory = directory + File.separator;
            if(repositoryID.startsWith(File.separator))repositoryID = repositoryID.substring(1, repositoryID.length());
            if(!repositoryID.endsWith(File.separator))repositoryID = repositoryID + File.separator;
            File dataDir2 = new File(directory + repositoryID);
            mRepository = new SailRepository(new MemoryStore(dataDir2));
            mRepository.initialize();
            setRepositoryConnection();
            return mRepository;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * method to connect to a remote Repository with the HTTP protocol.
     * @param sesameServer the string uri of the sesame server.
     * @param repositoryID string name id of the repository.
     * @return the Repository OpenRDF object.
     */
    public Repository connectToHTTPRepository(String sesameServer,String repositoryID){
        if(!sesameServer.endsWith(File.separator))sesameServer = sesameServer + File.separator;
        if(repositoryID.startsWith(File.separator)) repositoryID = repositoryID.substring(1,repositoryID.length());
        if(repositoryID.endsWith(File.separator)) repositoryID = repositoryID.substring(0,repositoryID.length()-1);
        return connectToHTTPRepository(sesameServer + repositoryID);
    }

    /**
     * method to connect to a remote Repository with the HTTP protocol.
     * @param urlAddressRepositoryId the string uri of the specific repository.
     * @return the Repository OpenRDF object.
     */
    public Repository connectToHTTPRepository(String urlAddressRepositoryId){
        try {
            mRepository = new HTTPRepository(urlAddressRepositoryId);
            //mRepository.initialize(); //include in the setReposiotryConnection method.
            setRepositoryConnection();
            logger.info("Connected to the repository at the url:" + urlAddressRepositoryId);
            return mRepository;
        } catch (RepositoryException e) {
            logger.warn("Can't connected to the repository at the url:" + urlAddressRepositoryId);
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to connect to a aive Repository OpenRDF.
     * Creating a Native RDF Repository does not keep data in main memory, but instead stores it directly to disk.
     * @param directory the String path to the directory of Repositories.
     * @param indexes An index strings, e.g. spoc,posc or spoc,posc,cosp.
     * @return the OpenRDF Native Repository.
     */
    public Repository connectToNativeRepository(String directory,String indexes){
        if (directory.startsWith(File.separator)) directory = directory.substring(1, directory.length());
        if (!directory.endsWith(File.separator)) directory = directory + File.separator;
        return connectToNativeRepository(new File(directory),indexes);
    }

    /**
     * Method to connect to a aive Repository OpenRDF.
     * Creating a Native RDF Repository does not keep data in main memory, but instead stores it directly to disk.
     * @param directory the File directory of Repositories.
     * @param indexes An index strings, e.g. spoc,posc or spoc,posc,cosp.
     * @return the OpenRDF Native Repository.
     */
    public Repository connectToNativeRepository(File directory,String indexes){
        String sDirectory = directory.getAbsolutePath();
        try{
            if(StringUtilities.isNullOrEmpty(indexes)){indexes = "spoc,posc,cosp";}
            mRepository = new SailRepository(new NativeStore(directory,indexes));
            mRepository.initialize();
            setRepositoryConnection();
            logger.info("Connected to the repository at the directory:" + directory.getAbsolutePath());
            return mRepository;
        } catch (RepositoryException e) {
            logger.warn("Can't connected to the repository at the directory:" + directory.getAbsolutePath());
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to connect to a Inferencing Repository Sesame.
     * @param directory the String path to the directory of Repositories.
     * @param repositoryID the String path to the File of the Id Repository.
     * @return the OpenRDF Inferencing Repository.
     */
    public Repository connectToInferencingRepository(String directory,String repositoryID){
        if(directory.startsWith(File.separator)) directory = directory.substring(1, directory.length());
        if(!directory.endsWith(File.separator)) directory = directory + File.separator;
        if(repositoryID.startsWith(File.separator))repositoryID = repositoryID.substring(1, repositoryID.length());
        if(!repositoryID.endsWith(File.separator))repositoryID = repositoryID + File.separator;
        return connectToInferencingRepository(new File(directory + repositoryID));
    }

    /**
     * Method to connect to a Inferencing Repository Sesame.
     * @param directory the File directory of Repositories.
     * @param repositoryID the String path to the File of the Id Repository.
     * @return the OpenRDF Inferencing Repository.
     */
    public Repository connectToInferencingRepository(File directory,String repositoryID){
        String sDirectory = directory.getAbsolutePath();
        if (sDirectory.startsWith(File.separator)) sDirectory = sDirectory.substring(1, sDirectory.length());
        if (!sDirectory.endsWith(File.separator)) sDirectory = sDirectory + File.separator;
        if(repositoryID.startsWith(File.separator))repositoryID = repositoryID.substring(1, repositoryID.length());
        if(!repositoryID.endsWith(File.separator))repositoryID = repositoryID + File.separator;
        return connectToInferencingRepository(new File(sDirectory + repositoryID));
    }

    /**
     * Method to connect to a Inferencing Repository Sesame.
     * Creating a repository with RDF Schema inferencing ForwardChainingRDFSInferencer is a generic RDF Schema
     * inferencer (MemoryStore and NativeStore support it)
     * @param repositoryID the String path to the File of the Id Repository.
     * @return the OpenRDF Inferencing Repository.
     */
    public Repository connectToInferencingRepository(File repositoryID) {
        try {
            mRepository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore(repositoryID)));
            mRepository.initialize();
            setRepositoryConnection();
            logger.info("Connected to the repository at the repository:" + repositoryID.getAbsolutePath());
            return mRepository;
        } catch (RepositoryException e) {
            logger.warn("Can't connected to the repository at the directory:" + repositoryID.getAbsolutePath());
            logger.error(e.getMessage(),e);
            return null;
        }

    }


    /**
     * Disconnect from a local or remote repository manager.
     */
    public void disconnect() {
        closeRepository();
        if (mRepositoryManager != null) {
            logger.info("Shutting down the repository manager");
            mRepositoryManager.shutDown();
            logger.info("manager is shut down");
            mRepositoryManager = null;
            mRepositoryLocation = null;
            logger.info("manager and location set to null");
        }
    }


    private void setRepositoryManager(RepositoryManager manager, String location) {
        try {
            disconnect();
            manager.initialize();
            mRepositoryManager = manager;
            mRepositoryLocation = location;
            isRepositoryManagerInitialized(); //set repository manager
            setRepository(mRepositoryManager,location); //set repository with the repository manager
        } catch (RepositoryException|RepositoryConfigException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * Open a repository with the given repositoryID at the remote or local location
     * previously connected to.
     * An error is raised if no local or remote location was set prior to
     * calling this method.
     * @param repositoryID string name/id of the repository.
     * @return repository connection.
     */
    public RepositoryConnection openRepository(String repositoryID) {
        logger.info("Called "+" with ID " + repositoryID);
        if(mRepositoryManager != null) {
            try {
                mRepository = mRepositoryManager.getRepository(repositoryID);
            } catch (RepositoryException|RepositoryConfigException e) {
                logger.error("Could not get repository " + repositoryID + "because " + e.getMessage(), e);
                return null;
            }
            if(mRepository == null) {
                logger.error("Getting repository failed - no repository of this repositoryID found: "+ repositoryID);
                return null;
            }
            try {
                setRepositoryConnection();
                logger.info("repository connection set");
                return mRepositoryConnection;
            } catch (Exception e) {
                logger.error("Could not get connection " + repositoryID + "because " + e.getMessage(), e);
                return null;
            }
        } else {
            logger.error( "Not connected to a repository location for openRepository " + repositoryID);
            return null;
        }
    }

    /**
     * Create a new managed repository at the current remote or local location
     * using the configuration information passed on as a string.
     * Create repository from a template, no substitution of variables also opens the newly created repository
     * @param config string file path to the config file.
     * @return if true the Config File has create a new reposiotry on the Sesame Server.
     */
    public boolean createRepository(String config) {
        if(mRepositoryManager == null) {
            logger.error("No connect the ReposiotryManager is NULL");
            return false;
        }
        Repository systemRepo = mRepositoryManager.getSystemRepository();
        // read the config file and parse to a Model
        try {
            //old deprecated code
            /*ValueFactory vf = systemRepo.getValueFactory();
            Graph graph = new GraphImpl();
            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE, vf);
            rdfParser.setRDFHandler(new StatementCollector(graph));
            rdfParser.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE);*/
            Model model = Rio.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE, RDFFormat.TURTLE);
            try {
                // get the unique subject
                //old deprecated code before 2.8.X
              /*  Resource repositoryNode  = org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE,RepositoryConfigSchema.REPOSITORY);
                RepositoryConfig repConfig = RepositoryConfig.parse(graph, repositoryNode);*/
                //method with version 2.8.X
                Resource repositoryNode = model.filter(null, RDF.TYPE,RepositoryConfigSchema.REPOSITORY).subjectResource();
                //method with 4.0.X and java 8
                //model = model.filter(null, RDF.TYPE,RepositoryConfigSchema.REPOSITORY).subjectResource();
                RepositoryConfig repConfig = new RepositoryConfig();
                repConfig.parse(model, repositoryNode);
                repConfig.validate();
                if (RepositoryConfigUtil.hasRepositoryConfig(systemRepo, repConfig.getID())) {
                    logger.error("Repository already exists with ID " + repConfig.getID());
                    return false;
                } else {
                    RepositoryConfigUtil.updateRepositoryConfigs(systemRepo, repConfig);
                    mRepository = mRepositoryManager.getRepository(repConfig.getID());
                    // Sesame complains about the repository already being initialized
                    // for native but not for OWLIM here ... can we always not initialize
                    // here????
                    try {
                        mRepository.initialize();
                    } catch (IllegalStateException e) {
                        logger.error("Got an IllegalStateException, ignored: "+e.getMessage(),e);
                        // we get this if the SAIL has already been initialized, just
                        // ignore and be happy that we can be sure that indeed it has
                        return false;
                    }
                    openRepository(repConfig.getID());
                    return true;
                }
            } catch (RepositoryException|RepositoryConfigException e) {
                logger.error("Error creating repository: " + e.getMessage(), e);
                return false;
            }
        } catch (IOException|RDFParseException e) {
            logger.error("Error parsing the config string: "+ e.getMessage(), e);
            return false;
        }
    }

    /**
     * Create an unManaged repository with files stored in the directory
     * given from the configuration passed as a string.
     * @param repositoryDirFile string file path to the directory of repsoisotry.
     * @param configFile string file path to the config file.
     * @return the Repository created with the config file.
     */
    public Repository createRepositoryUnManaged(File repositoryDirFile,File configFile) {
        isManagedRepository = false;
        Repository repo;
        try {
            /*ValueFactory vf = new MemValueFactory();
            Graph graph = parseRdf(configstring, vf, RDFFormat.TURTLE);*/
            Model model = Rio.parse(new FileReader(configFile), RepositoryConfigSchema.NAMESPACE, RDFFormat.TURTLE);
            /*Resource repositoryNode = org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);*/
            Resource repositoryNode = model.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY).subjectResource();
            RepositoryConfig repConfig;
            try {
                /*repConfig = RepositoryConfig.create(model,repositoryNode);*/
                repConfig = new RepositoryConfig();
                repConfig.parse(model, repositoryNode);
            } catch (RepositoryConfigException e) {
                logger.error("Could not create repository from RDF graph:" + e.getMessage(), e);
                return null;
            }

            try {
                repConfig.validate();
            } catch (RepositoryConfigException e) {
                logger.error("Could not validate repository: " + e.getMessage(), e);
                return null;
            }
            RepositoryImplConfig rpc = repConfig.getRepositoryImplConfig();
            repo = createRepositoryStack(rpc);
            if (repo != null) {
                repo.setDataDir(repositoryDirFile);
                try {
                    repo.initialize();
                } catch (RepositoryException e) {
                    logger.error("Could not initialize repository: " + e.getMessage(),e);
                    return null;
                }
                try {
                    RepositoryConnection conn = repo.getConnection();
                    logger.info("Repo dir is " + repo.getDataDir().getAbsolutePath());
                    logger.info("Repo is writable " + repo.isWritable());
                    return repo;
                } catch (RepositoryException e) {
                    logger.error("Could not get connection for unmanaged repository: " + e.getMessage(),e);
                    return null;
                }
            }
            else{
                logger.warn(" the repository is NULL");
                return null;
            }
        }catch(RDFParseException e){
            logger.error("Could not get subject of config RDF: " + e.getMessage(), e);
            return null;
        }catch(IOException e){
            logger.error("Not found the directory file: " + e.getMessage(), e);
            return null;
        }
    }

    /*private Graph parseRdf(String config, ValueFactory vf, RDFFormat lang) {
        Graph graph = new org.openrdf.model.impl.GraphImpl(vf);
        RDFParser rdfParser = Rio.createParser(lang, vf);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        try {
            rdfParser.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE);
        } catch (Exception e) {
            SystemLog.error("Could not parse rdf: " + e);
        }
        return graph;
    }*/

    /**
     * Method to convert a Configuration file to a new Created Repository Sesame.
     * @param filePathConfig the configuration file.
     * @return the new RepositoryConfig.
     */
    private RepositoryConfig convertFileConfigToRepositoryConfig(String filePathConfig) {
        Repository myRepository = new SailRepository(new MemoryStore());
        RepositoryConfig repConfig;
        try {
            try {
                myRepository.initialize();
            } catch (RepositoryException e) {
                logger.error("Error initializing memory store: " + e.getMessage(),e);
                return null;
            }
            Model model = Rio.parse(new StringReader(filePathConfig), RepositoryConfigSchema.NAMESPACE, RDFFormat.TURTLE);
            Resource repositoryNode = model.filter(null, RDF.TYPE,RepositoryConfigSchema.REPOSITORY).subjectResource();
            repConfig = new RepositoryConfig();
            repConfig.parse(model, repositoryNode);
            repConfig.validate();
            return repConfig;
        } catch (RepositoryConfigException|IOException|RDFParseException e) {
            logger.error("Error parsing the config string " + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to create a Repository from a Configuration.
     * @param config RepositoryImplConfig.
     * @return the created Repository.
     */
    public Repository createRepositoryStack(RepositoryImplConfig config) {
        RepositoryFactory factory = RepositoryRegistry.getInstance().get(config.getType());
        if (factory == null) {
            logger.error("Unsupported repository type: " + config.getType());
            return null;
        }
        Repository repository;
        try {
            repository = factory.getRepository(config);
        } catch (RepositoryConfigException e) {
            logger.error("Could not get repository from factory: " + e.getMessage(),e);
            return null;
        }
        if (config instanceof DelegatingRepositoryImplConfig) {
            RepositoryImplConfig delegateConfig = ((DelegatingRepositoryImplConfig)config).getDelegate();
            Repository delegate = createRepositoryStack(delegateConfig);
            if(repository ==null){
                logger.error("The dDelegate Repository or the Repository is NULL");
                return null;
            }
            if(delegate!=null) {
                try {
                    ((DelegatingRepository) repository).setDelegate(delegate);
                } catch (ClassCastException e) {
                    logger.error(
                            "Delegate specified for repository that is not a DelegatingRepository: "
                                    + delegate.getClass());
                }
            }
        }
        return repository;
    }

    /**
     * Method for Substitute variables in a configuration template string.
     * @param configTemplate string file path to the config file.
     * @param variables map of all variables you wan substitute or update.
     * @return new string file apth to the confic path.
     */
    private String substituteConfigTemplate(String configTemplate, Map<String,String> variables) {
        // replace all variables in the template then do the actual createRepository
        StringBuffer result = new StringBuffer(configTemplate.length()*2);
        Matcher matcher = TOKEN_PATTERN.matcher(configTemplate);
        while (matcher.find()) {
            String group = matcher.group();
            // get the variable repositoryID and default
            String[] tokensArray = group.substring(2, group.length() - 2).split("\\|");
            String var = tokensArray[0].trim();
            String value = variables.get(var);
            if(value == null) {
                // try to get the default
                if(tokensArray.length > 1) {
                    value = tokensArray[1].trim();
                } else {
                    value = "";
                }
            }
            matcher.appendReplacement(result, value);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Method to update a configuration template File/String
     * @param configtemplate the configurationTemplate.
     * @param variables a Map of all the variables you want ot put in that.
     * @return the configTemplate update.
     */
    public String updateConfigTemplate(String configtemplate, Map<String,String> variables) {
        return substituteConfigTemplate(configtemplate, variables);
    }

    /**
     * Method for Delete the managed repository with that repositoryID.
     * @param name string repository name/id to delete.
     */
    public void deleteRepository(String name) {
        if(mRepositoryManager != null) {
            closeRepository();
            try {
                boolean done = mRepositoryManager.removeRepository(name);
            } catch (RepositoryException|RepositoryConfigException e) {
                logger.error("Could not delete repository " + name + ": " + e.getMessage(), e);
            }
        } else {
           logger.error(" Can't delete the Repository the RepositoryManager is NULL");
        }
    }

    /**
     * Clear the current repository and remove all data from it.
     */
    public void clearRepository() {
        try {
            mRepositoryConnection.clear();
        } catch (RepositoryException e) {
            logger.error("Could not clear repository: " + e.getMessage(), e);
        }
    }

    /**
     * Load data into the current repository from a file.
     * @param filePath string file path .
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     * @return if true all the operation are done.
     */
    public boolean importIntoRepository(File filePath, String baseURI, String inputFormat) {
        return importIntoRepositoryBase(filePath,baseURI,inputFormat);
    }

    /**
     * Load data into the current repository from a stream.
     * @param filePath InputStream of file path .
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     * @return if true all the operation are done.
     */
    public boolean importIntoRepository(InputStream filePath, String baseURI, String inputFormat) {
        return importIntoRepositoryBase(filePath, baseURI, inputFormat);
    }

    /**
     * Load data into the current repository from a stream.
     * @param filePath URL of file path .
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     * @return if true all the operation are done.
     */
    public boolean importIntoRepository(URL filePath, String baseURI, String inputFormat) {
        return importIntoRepositoryBase(filePath, baseURI, inputFormat);
    }

    /**
     * Method to Load data into the current repository from a reader
     * @param filePath reader of the file path .
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     * @return if true all the operation are done.
     */
    public boolean importIntoRepository(Reader filePath, String baseURI, String inputFormat) {
        return importIntoRepositoryBase(filePath,baseURI,inputFormat);
    }

    /**
     * Method to Load data into the current repository from a reader
     * @param statement Statement Open RDF to Import.
     * @return if true all the operation are done.
     */
    public boolean importIntoRepository(Statement statement) {
        return importIntoRepositoryBase(statement,null,null);
    }

    /**
     * Method to import a File of triple in a Repository Sesame.
     * @param fileOrDirectory the file or directories of triples to import.
     * @return if true all the operation are done.
     */
    public boolean importIntoRepository(File fileOrDirectory){
        try {
            if (!mRepository.isInitialized()) mRepository.initialize();
            if(fileOrDirectory.isDirectory()){
                List<File> files = FileUtilities.getFilesFromDirectory(fileOrDirectory);
                for (File file: files)  {
                    if (!mRepository.isInitialized()) mRepository.initialize();
                    try {
                        logger.info("Start the import of the Data on the repository...");
                        mRepositoryConnection.begin();
                        mRepositoryConnection.add(file, "file://" + file.getAbsolutePath(),
                                convertFileNameToRDFFormat(file));
                        mRepositoryConnection.commit();
                        logger.info("...end the import of the Data on the repository...");
                    } finally {
                        mRepositoryConnection.close();
                    }
                }
                return true;
            }else{
                if (!mRepository.isInitialized()) mRepository.initialize();
                try {
                    logger.info("Start the import of the Data on the repository...");
                    mRepositoryConnection.begin();
                    mRepositoryConnection.add(fileOrDirectory, "file://" + fileOrDirectory.getAbsolutePath(),
                            convertFileNameToRDFFormat(fileOrDirectory));
                    mRepositoryConnection.commit();
                    logger.info("... end the import of the Data on the repository...");
                } finally {
                    mRepositoryConnection.close();
                }
                return true;
            }
        } catch (RepositoryException|IOException|RDFParseException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method fro manage all the constructor already present on the Open RDF API and all the constructor of this class.
     * @param objectToImport the Statement,File,InputStream,Reader,URL,List
     * @param baseURI the String baseUri of a Grapg or the URI path of the file .
     * @param inputFormat the String of a RDFFormat OpenRDF.
     * @param contexts the Array of Context to add tot the data to import.
     * @return if true all the operations are done.
     *
     */
    private boolean importIntoRepositoryBase(Object objectToImport, String baseURI, String inputFormat,Resource... contexts) {
        if(mRepositoryConnection != null) {
            try {
                if (!mRepository.isInitialized()) mRepository.initialize();
                logger.info("Start the import of the Data on the repository...");
                mRepositoryConnection.begin();
                if(!StringUtilities.isNullOrEmpty(baseURI)) {
                    RDFFormat sesameFormat;
                    try {
                        if (inputFormat == null) {
                            logger.warn("Could not import - format not supported: 'NULL', use the RDF/XML");
                            sesameFormat = RDFFormat.RDFXML;
                        } else {
                            sesameFormat = stringToRDFFormat(inputFormat);
                        }
                    } catch (IllegalArgumentException e) {
                        logger.warn("Could not import - format not supported: " + inputFormat + " use the RDF/XML");
                        sesameFormat = RDFFormat.RDFXML;
                    }
                    if (objectToImport instanceof InputStream) {
                        if (contexts != null && contexts.length > 0)
                            mRepositoryConnection.add((InputStream) objectToImport, baseURI, sesameFormat, contexts);
                        else mRepositoryConnection.add((InputStream) objectToImport, baseURI, sesameFormat);
                    } else if (objectToImport instanceof Reader) {
                        if (contexts != null && contexts.length > 0)
                            mRepositoryConnection.add((Reader) objectToImport, baseURI, sesameFormat, contexts);
                        else mRepositoryConnection.add((Reader) objectToImport, baseURI, sesameFormat);
                    } else if (objectToImport instanceof URL) {
                        if (contexts != null && contexts.length > 0)
                            mRepositoryConnection.add((URL) objectToImport, baseURI, sesameFormat, contexts);
                        else mRepositoryConnection.add((URL) objectToImport, baseURI, sesameFormat);
                    } else if (objectToImport instanceof File) {
                        if (contexts != null && contexts.length > 0)
                            mRepositoryConnection.add((File) objectToImport, baseURI, sesameFormat, contexts);
                        else mRepositoryConnection.add((File) objectToImport, baseURI, sesameFormat);
                    }
                }else if(objectToImport instanceof File){
                    if(StringUtilities.isNullOrEmpty(baseURI) || StringUtilities.isNullOrEmpty(inputFormat)){
                        File fileOrDirectory = (File) objectToImport;
                        if(fileOrDirectory.isDirectory()){
                            List<File> files = FileUtilities.getFilesFromDirectory(fileOrDirectory);
                            for (File file: files)  {
                                addFileDataToRepositoryConnection(file);
                            }
                        }else{
                            addFileDataToRepositoryConnection(fileOrDirectory);
                        }
                    }else {
                        if (contexts != null && contexts.length > 0)
                            mRepositoryConnection.add((Statement) objectToImport, contexts);
                        else mRepositoryConnection.add((Statement) objectToImport);
                    }
                }else if(objectToImport instanceof Statement){
                    if (contexts != null && contexts.length > 0)mRepositoryConnection.add((Statement)objectToImport, contexts);
                    else mRepositoryConnection.add((Statement)objectToImport);
                }else if(objectToImport instanceof List){
                    Iterable<? extends Statement> iter = CollectionUtilities.toIterable(objectToImport);
                    if (contexts != null && contexts.length > 0)mRepositoryConnection.add(iter, contexts);
                    else mRepositoryConnection.add(iter);
                }
                mRepositoryConnection.commit();
                logger.info("...end the import of the Data on the repository");
            } catch(RepositoryException|IOException|RDFParseException e) {
                logger.error("Could not import: " + e.getMessage(), e);
                return false;
            }finally {
                try {
                    mRepositoryConnection.close();
                } catch (RepositoryException e) {
                    logger.warn("Cannot close the connection: " + e.getMessage(), e);
                }
            }
            return true;
        } else {
            logger.error("Cannot import, no connection open!");
            return false;
        }
    }

    private void addFileDataToRepositoryConnection(File file) throws RepositoryException, RDFParseException, IOException {
        try {
            mRepositoryConnection.begin();
            mRepositoryConnection.add(file, "file://" + file.getAbsolutePath(),
                    convertFileNameToRDFFormat(file));
            mRepositoryConnection.commit();
        } finally {
            mRepositoryConnection.close();
        }
    }

    //Create a query object for the current repository.
//    public OntologyTupleQuery createQuery(String query) {
//        if(mRepositoryConnection != null) {
//            return new UtilTupleQueryIterator(
//                    this,
//                    query,
//                    OConstants.QueryLanguage.SPARQL);
//        } else {
//            throw new SesameManagerException("Cannot create a query, no connection");
//        }
//    }

    /**
     * Method to ASK at the repository where you are connected with a query SPARQL or SERQL.
     * @param query query a string query SPARQL or SERQL.
     * @return the result of the ASK query.
     */
    public Boolean execSparqlAskOnRepository(String query) {
        if(mRepositoryConnection != null) {
            try {
                QueryLanguage lang = checkLanguageOfQuery(query);
                BooleanQuery bQuery = mRepositoryConnection.prepareBooleanQuery(lang, query);
                boolean result = bQuery.evaluate();
                logger.info("Execute Ask Query: " + query);
                return result;
            } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
                logger.error("Could not prepare BooleanQuery:"+ e.getMessage(),e);
                return null;
            }
        } else {
            logger.error("Could not create an ask query, no connection, the RepositoryConnection is NULL");
            return null;
        }
    }

    /**
     * Method to update the repository where you are connected with a query SPARQL or SERQL.
     * @param query a string query SPARQL or SERQL.
     * @return if true all the operations are done.
     */
    public Boolean execSparqlUpdateOnRepository(String query) {
        if(mRepositoryConnection != null) {
            try {
                QueryLanguage lang = checkLanguageOfQuery(query);
                Update update =  mRepositoryConnection.prepareUpdate(lang, query);
                update.execute();
                mRepositoryConnection.commit();
                logger.info("Execute Update Query: " + query);
                return true;
            } catch (RepositoryException | MalformedQueryException | UpdateExecutionException e) {
                logger.error("Could not prepare an Update operation:"+ e.getMessage(),e);
                return false;
            }
        } else {
            logger.error("Cannot create an update operation, no connection, the RepositoryConnection is NULL");
            return false;
        }
    }

    /**
     * Method to get a Collection of all repositories on the directory set on the manager
     * @return the Set Collection of names of all repository in the current manager.
     */
    public List<String> getRepositories() {
        if(mRepositoryManager == null) {
            logger.warn("You must set the Repository Manager for avoid the empty list,the Repository Manager is NULL");
            return null;
        }
        try {
            return new ArrayList<>(mRepositoryManager.getRepositoryIDs());
        } catch (RepositoryException e) {
            logger.error("Could not get repository IDs:" + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method for see on the console the resul of a tuplequery
     * @param query string SPARQL/SERQL query.
     * @return if true all the operations are done.
     */
    public Boolean showStatements(TupleQuery query) {
        try{
            TupleQueryResult currentState = query.evaluate();
            while (currentState.hasNext()) {
                BindingSet set = currentState.next();
                for (Binding binding : set) {
                    //System.out.printf("%s = %s \n", binding.getName(), binding.getValue());
                    logger.info(binding.getName() + "=" +  binding.getValue());
                }
                //System.out.println();
            }
            return true;
            //System.out.println("============================================================");
        }catch(QueryEvaluationException e){
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method utility: Check if the string query is written in SPARQL or SERQL format.
     * @param queryString string of the query.
     * @return language of the query.
     */
    public QueryLanguage checkLanguageOfQuery(String queryString){
        return checkLanguageOfQuery(queryString,mRepositoryConnection);
    }

    /**
     * Method utility: Check if the string query is written in SPARQL or SERQL format.
     * @param queryString String of the query.
     * @param repositoryConnection the current RepositoryConnection of Sesame.
     * @return language of the query.
     */
    public QueryLanguage checkLanguageOfQuery(String queryString,RepositoryConnection repositoryConnection){
        //CHECK the language of the uery string if SPARQL or SERQL
        QueryLanguage lang = new QueryLanguage("");
        for (QueryLanguage language : queryLanguages) {
            Query result = prepareQuery(queryString, language, repositoryConnection);
            if (result != null) {
                lang = language;
                break;
            }
        }
        return lang;
    }

    /**
     * Method to check if a repository is initialized.
     * @return if true the repository is initialized.
     */
    public Boolean isRepositoryInitialized(){
        return isAnyRepositoryInitialized(mRepository);
    }

    public Boolean isRepositoryInitialized(Repository repository){
        return isAnyRepositoryInitialized(repository);
    }

    public Boolean isRepositoryManagerInitialized(){
        return isRepositoryManagerInitialized(mRepositoryManager);
    }

    public Boolean isRepositoryManagerInitialized(RepositoryManager repositoryManager){
        return isAnyRepositoryInitialized(repositoryManager);
    }

    public Boolean isRemoteRepositoryManagerInitialized(){
        return isRemoteRepositoryManagerInitialized(mRemoteRepositoryManager);
    }

    public Boolean isRemoteRepositoryManagerInitialized(RemoteRepositoryManager remoteRepositoryManager){
        return isAnyRepositoryInitialized(remoteRepositoryManager);
    }


    private Boolean isAnyRepositoryInitialized(Object repoObject){
        try{
            if(repoObject instanceof Repository){
                if(!(((Repository) repoObject).isInitialized())){
                    logger.warn("The Repository is not Initialized , try to automatically initialized.");
                    mRepository.initialize();
                    return true;
                }
            }else if(repoObject instanceof RemoteRepositoryManager) {
                if (!(((RemoteRepositoryManager) repoObject).isInitialized())) {
                    logger.warn("The RemoteRepositoryManager is not Initialized , try to automatically initialized.");
                    mRemoteRepositoryManager.initialize();
                    for(Repository repo: mRemoteRepositoryManager.getAllRepositories()){
                        isAnyRepositoryInitialized(repo);
                    }
                    return true;
                }
            }else if(repoObject instanceof RepositoryManager) {
                if (!(((RepositoryManager) repoObject).isInitialized())) {
                    logger.warn("The RepositoryManager is not Initialized , try to automatically initialized.");
                    mRepositoryManager.initialize();
                    for(Repository repo: mRepositoryManager.getAllRepositories()){
                        isAnyRepositoryInitialized(repo);
                    }
                    return true;
                }
            }else{
                return false;
            }
        }catch(RepositoryException|RepositoryConfigException e){
            logger.error("Can't initialized the "+repoObject.getClass().getName()+":" + e.getMessage(), e);
            return false;
        }
        return false;
    }




    /**
     * Method to check if a repository is connected.
     * @return if true the repository is connected.
     */
    public Boolean isRepositoryConnected(){
        try {
            return mRepositoryConnection.isOpen();
        }catch(RepositoryException e){
            logger.error(e.getMessage(),e);
            return  false;
        }
    }

    /**
     * Method to check if a repository is active.
     * @return if true the repository is active.
     */
    public Boolean isRepositoryActive(){
        try {
            return mRepositoryConnection.isActive();
        }catch(RepositoryException e){
            logger.error(e.getMessage(),e);
            return  false;
        }
    }

    /**
     * Method to check if a repository is empty.
     * @return if true the repository is empty.
     */
    public Boolean isRepositoryEmpty(){
        try {
            return mRepositoryConnection.isEmpty();
        }catch(RepositoryException e){
            logger.error(e.getMessage(),e);
            return  false;
        }
    }


    /**
     * Method to converts vector to string for sesame.
     * @param inputVector java.util.Vector to convert string.
     * @return string of the sesame vector.
     */
    /*public static String convertVectorToSesameString(java.util.Vector <String> inputVector) {
        String subjects = "";
        for(int subIndex = 0;subIndex < inputVector.size();subIndex++)
            subjects = subjects + "(<" + inputVector.elementAt(subIndex) + ">)";

        return subjects;

    }*/

    /**
     * Method to remove a jena tripel from a Sesame Repository.
     * @param triple the Jena triple to remove.
     * @param contexts The contexts to add statements to.
     * @return if true all the operations are done.
     */
    public Boolean removeJenaTripleFromSesameRepository(com.hp.hpl.jena.graph.Triple triple,Resource contexts) {
        com.hp.hpl.jena.graph.Node s = triple.getSubject() ;
        com.hp.hpl.jena.graph.Node p = triple.getPredicate() ;
        com.hp.hpl.jena.graph.Node o = triple.getObject() ;
        ValueFactory valueFactory = mRepositoryConnection.getValueFactory();
        Resource subj = JenaSesameUtilities.asResource(valueFactory, s) ;
        URI pred = JenaSesameUtilities.asURI(valueFactory, p) ;
        Value obj = JenaSesameUtilities.asValue(valueFactory, o) ;
        try {
            Statement stmt = valueFactory.createStatement(subj, pred, obj) ;
            mRepositoryConnection.remove(stmt, contexts) ;
            return true;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to add a jena triple from a Sesame Repository.
     * @param triple the Jena triple to remove.
     * @param contexts The contexts to add statements to.
     * @return if true all the operations are done.
     */
    public Boolean addJenaTripleToSesameRepository(com.hp.hpl.jena.graph.Triple triple,Resource contexts) {
        com.hp.hpl.jena.graph.Node s = triple.getSubject() ;
        com.hp.hpl.jena.graph.Node p = triple.getPredicate() ;
        com.hp.hpl.jena.graph.Node o = triple.getObject() ;
        ValueFactory valueFactory = mRepositoryConnection.getValueFactory();
        Resource subj  = JenaSesameUtilities.asResource(valueFactory, s) ;
        URI pred       = JenaSesameUtilities.asURI(valueFactory, p) ;
        Value obj      = JenaSesameUtilities.asValue(valueFactory, o) ;
        try {
            Statement stmt = valueFactory.createStatement(subj, pred, obj) ;
            mRepositoryConnection.add(stmt, contexts) ;
            return true;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to add a jena triple from a Sesame Repository.
     * @param triple the Jena triple to remove.
     * @param contexts The contexts to add statements to.
     * @return the List of Jena Triple.
     */
    public List<com.hp.hpl.jena.graph.Triple> findJenaTripleFromSesameRepository(
            com.hp.hpl.jena.graph.Triple triple,
            Resource contexts) {

        ValueFactory valueFactory = mRepositoryConnection.getValueFactory();
        com.hp.hpl.jena.graph.Node s = triple.getMatchSubject() ;
        com.hp.hpl.jena.graph.Node p = triple.getMatchPredicate() ;
        com.hp.hpl.jena.graph.Node o = triple.getMatchObject() ;
        Resource subj =
                ( s==null ? null : JenaSesameUtilities.asResource(valueFactory, s) ) ;
        URI pred   =
                ( p==null ? null : JenaSesameUtilities.asURI(valueFactory, p) ) ;
        Value obj  =
                ( o==null ? null : JenaSesameUtilities.asValue(valueFactory, o) ) ;
        List<com.hp.hpl.jena.graph.Triple> list = new ArrayList<>();
        try {
            RepositoryResult<Statement> iter1 =
                    mRepositoryConnection.getStatements(subj, pred, obj, true, contexts) ;
            com.hp.hpl.jena.util.iterator.ExtendedIterator<com.hp.hpl.jena.graph.Triple> ext =
                    new com.github.p4535992.util.repositoryRDF.jenaAndSesame.impl.RepositoryResultIterator(iter1);
            int i=0;
            while(ext.hasNext()){
                list.add(i,ext.next());
                i++;
            }
            return list;
        } catch (RepositoryException ex) {
            logger.error("Can't execute the research on the repository:"+ ex.getMessage(),ex);
            return null;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //NEW METHODS
    ///////////////////////////////////////////////////////////////////////////////////7

    /**
     * Method to set a Map of Namespaces to  a repository.
     * @param namespacePrefixes map of namespaces.
     * @return if true all the operations are done.
     */
    public Boolean setNamespacePrefixesToRepository(Map<String,String> namespacePrefixes)  {
        try {
            mRepositoryConnectionWrapper = new RepositoryConnectionWrapper(mRepository,mRepositoryConnection);
            mRepositoryConnectionWrapper.begin();
            for(Map.Entry<String,String> entry: namespacePrefixes.entrySet()){
                mRepositoryConnectionWrapper.setNamespace(entry.getKey(), entry.getValue());
            }
            mRepositoryConnectionWrapper.commit();
            mRepositoryConnectionWrapper.close();
            return true;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to set a Map of Namespaces to  a repository.
     * @param namespacePrefixes map of namespaces.
     * @param model the OpenRDF Model where add the new namespace.
     * @return the OpenRDF Model.
     */
    public Model setNamespacePrefixesToModel(Map<String,String> namespacePrefixes,Model model){
        for(Map.Entry<String,String> entry: namespacePrefixes.entrySet()){
            model.setNamespace(entry.getKey(),entry.getValue());
        }
        return model;
    }

    /**
     * Initialize the Wrapper with a NativeStore as a backend.
     * @param dir Data file that the native store will use.
     * @param indexes If not null, the store will use the given indexes to speed up queries
     * @param inferencing If true (and not null), it will activate rdfs inferencing
     * @return the OpenRDF Repository.
     */
     public Repository connectToNativeRepository(File dir, String indexes, boolean inferencing){
         Sail sailStack;
         if(indexes == null) {
             sailStack = new NativeStore(dir);
         } else {
             sailStack = new NativeStore(dir, indexes);
         }
         if (inferencing) {
             sailStack = new ForwardChainingRDFSInferencer((NotifyingSail) sailStack);
             sailStack = new DirectTypeHierarchyInferencer((NotifyingSail) sailStack);
         }
         return connectToXXXRepository(sailStack);
     }


    /**
     * Initialize the Wrapper with a MemoryStore as a backend
     * @param inferencing If true (and not null), it will activate rdfs inferencing
     * @return the OpenRDF Repository.
     */
    public Repository connectToMemoryRepository(boolean inferencing){
        Sail sailStack = new MemoryStore();
        if (inferencing) {
            sailStack = new ForwardChainingRDFSInferencer((NotifyingSail) sailStack);
            sailStack = new DirectTypeHierarchyInferencer((NotifyingSail) sailStack);
        }
        return connectToXXXRepository(sailStack);
    }

    private Repository connectToXXXRepository(Sail sailStack){
        try {
            mRepository = new SailRepository(sailStack);
            mRepository.initialize();
            return  mRepository;
        } catch (Exception e) {
            logger.error(e.getMessage(),new RuntimeException(e));
            return null;
        }
    }


    /**
     * Initialize the Wrapper with a RDBMS as a backend
     * @param driver JDBC driver to use
     * @param url JDBC connect URL
     * @param user Username for the database, or null
     * @param password Password for the database user, or null
     * @param inferencing If true (and not null), it will activate rdfs inferencing
     */
    /*public Repository connectToRDBMS(String driver, String url, String user,
                                     String password, boolean inferencing){
        Sail sailStack;
        if(user == null) {
            sailStack = new RdbmsStore(driver, url);
        } else {
            sailStack = new RdbmsStore(driver, url, user, password);
        }
        return initFromSail(sailStack, inferencing);
    }*/


    /**
     * Method to Initialize the Wrapper with a connection to a remote HTTP repository
     * @param url the url of Repository.
     * @param user the username onthe repository.
     * @param password the password on the repository.
     * @return the Repository.
     */
    public Repository connectToHTTPRepository(String url, String user, String password){
        try {
            HTTPRepository httpRepository = new HTTPRepository(url);
            if (user != null) {
                httpRepository.setUsernameAndPassword(user, password);
            }
            httpRepository.initialize();
            mRepository = httpRepository;
            setRepositoryConnection();
            return mRepository;
        }catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Load data from file. This is a thin wrapper on the
     * add method of the connection, creating only the File object for
     * it to work on. And yes, we throw everything and let the Ruby
     * side deal with it.
     * @param file File to load.
     * @param baseUri baseUri of the triple in the file to load.
     * @param dataFormat the RDFFormat of the triple on the file to load
     * @param contexts Array of resource context.
     * @return if true every is gone ok.
     */
    public Boolean importIntoRepository(String file, String baseUri, RDFFormat dataFormat, Resource... contexts) {
        boolean result = false;
        try {
            //if(!mRepositoryConnection.isOpen()) mRepositoryConnection.isOpen();
            mRepositoryConnection.begin();
            mRepositoryConnection.add(new File(file), baseUri, dataFormat, contexts);
            mRepositoryConnection.commit();
            result = true;
        } catch (IOException|RDFParseException|RepositoryException e) {
            logger.error( e.getMessage(),e);
        }finally {
            try {
                mRepositoryConnection.close();
            } catch (RepositoryException e) {
                logger.error("Cannot close the connection:" + e.getMessage(),e);
                result = false;
            }
        }
        return result;
    }


    //////////////////////////////////////////////////////////////
    //OTHER NEW METHODS
    //////////////////////////////////////////////////////////////

    /**
     * Method to convert the result of a GraphQuery to a Sesame Model.
     * @param graphQuery the OpenRDF GraphQuery.
     * @return the OpenRDF Model.
     * @throws QueryEvaluationException throw if the Query is malformed.
     */
    public Model convertGraphQueryToModel(GraphQuery graphQuery) throws QueryEvaluationException {
        GraphQueryResult graphQueryResult = graphQuery.evaluate();
        return QueryResults.asModel(graphQueryResult);
    }

    /**
     * Method to convert the Sesame Repository to a Sesame Model.
     * @param repository the OpenRDF Repository.
     * @return the OpenRDF Model.
     */
    public Model convertRepositoryToModel(Repository repository){
        return convertRepositoryToModel(repository,null);
    }

    /**
     * Method to convert the Sesame Repository to a Sesame Model.
     * @param repository the OpenRDF Repository.
     * @param limit the Integer limit of Statement to get from the OpenRDF Repository.
     * @return the OpenRDF Model.
     */
    public Model convertRepositoryToModel(Repository repository,Integer limit){
        Model model = createModel();
        RepositoryConnection conn ;
        try {
            conn = repository.getConnection();
            //this method retrieves all statements that appear in the repository
            RepositoryResult<Statement> rri = conn.getStatements(null, null, null, true);
            if(limit!=null){
                int i = 0;
                while(rri.hasNext()){
                    if(i > limit) break;
                    model.add(rri.next());
                    i++;
                }
            }else{
                while(rri.hasNext()){
                    model.add(rri.next());
                }
            }
            return model;
        } catch (RepositoryException e) {
            logger.error( "The connection to the Repository:"+repository+" is not possible:" +e.getMessage(),e);
            return  null;
        }
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param graphQuery the OpenRDF GraphQuery to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(GraphQuery graphQuery) {
        Long calculate = calculateExecutionTime(graphQuery);
        if(calculate == null) logger.warn("Query Graph result(s) in 'ERROR CAN'T CALCULATE THE EXECUTION TIME'");
        else logger.info("Query Graph result(s) in " + calculate + "ms.");
        return calculate;

    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param tupleQuery the OpenRDF TupleQuery to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(TupleQuery tupleQuery){
        Long calculate = calculateExecutionTime(tupleQuery);
        if(calculate == null) logger.warn("Query Tuple result(s) in 'ERROR CAN'T CALCULATE THE EXECUTION TIME'");
        else logger.info("Query Tuple result(s) in " + calculate + "ms.");
        return calculate;
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param booleanQuery the OpenRDF BooleanQuery to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(BooleanQuery booleanQuery){
        try {
            Timer timer = new Timer();
            timer.startTimer();
            booleanQuery.evaluate();
            long result = timer.endTimer();
            logger.info("Query Boolean result(s) in " + result + "ms.");
            return result;
        } catch (Exception e) {
            logger.error("Query Boolean result(s) in 'ERROR CAN'T CALCULATE THE EXECUTION TIME':"+e.getMessage(),e);
            return 0L;
        }
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param updateQuery the OpenRDF updateQuery to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(Update updateQuery){
        try {
            Timer timer = new Timer();
            timer.startTimer();
            updateQuery.execute();
            long result = timer.endTimer();
            logger.info("Query Update result(s) in " + result + "ms.");
            return result;
        } catch (Exception e) {
            logger.error("Query Update result(s) in 'ERROR CAN'T CALCULATE THE EXECUTION TIME':"+e.getMessage(),e);
            return 0L;
        }
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param query the String of the Query to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(String query){
        return getExecutionQueryTime(convertQueryToOperation(query));
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param query the OpenRDF Query to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(Query query){
        return getExecutionQueryTime(convertQueryToOperation(query));
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param preparedOperation the OpenRDF Operation to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    private Long getExecutionQueryTime2(Operation preparedOperation){
        long timeConnection = 150; //all the connection to a repository in a tomcat server are around the 250ms.
        if (preparedOperation == null) {
            logger.warn("Unable to parse SPARQL query the preparedOperation is NULL");
            return null;
        }
        //If the Query is a Update..........
        if( preparedOperation instanceof Update) return getExecutionQueryTime((Update) preparedOperation);
        //If the Query is a Ask..........
        if (preparedOperation instanceof BooleanQuery) return getExecutionQueryTime((BooleanQuery) preparedOperation);
        //If the Query is a Constructor..........
        if (preparedOperation instanceof GraphQuery) return getExecutionQueryTime((GraphQuery) preparedOperation);
        //If the Query is a Select or a Describe..........
        if (preparedOperation instanceof TupleQuery) return getExecutionQueryTime((TupleQuery) preparedOperation);
        //SystemLog.message("Query result(s) in " + (queryEnd - queryBegin) / 1000000 + "ms.");
        return null;
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param typeOfQueryOrOperation the OpenRDF Operation,Query,String,Update,GraphQuery,TupleQuery,BooleanQuery to evaluate.
     * @return the Long execution time for evaluate the query.
     */
    public Long getExecutionQueryTime(Object typeOfQueryOrOperation){
        if(typeOfQueryOrOperation instanceof Update) return getExecutionQueryTime2((Update) typeOfQueryOrOperation);
        if(typeOfQueryOrOperation instanceof BooleanQuery) return getExecutionQueryTime2((BooleanQuery) typeOfQueryOrOperation);
        if(typeOfQueryOrOperation instanceof GraphQuery) return getExecutionQueryTime2((GraphQuery) typeOfQueryOrOperation);
        if(typeOfQueryOrOperation instanceof TupleQuery) return getExecutionQueryTime2((TupleQuery) typeOfQueryOrOperation);
        if(typeOfQueryOrOperation instanceof String) return getExecutionQueryTime2(String.valueOf(typeOfQueryOrOperation));
        if(typeOfQueryOrOperation instanceof Query) return getExecutionQueryTime2((Query) typeOfQueryOrOperation);
        if(typeOfQueryOrOperation instanceof Operation) return getExecutionQueryTime2((Operation) typeOfQueryOrOperation);
        else return null;
    }

    /**
     * Method to calculate the Query execution time of SPARQL or SeRQL query on a sesame repository
     * @param query  the TupleQuery to analyze
     * @return the Long value of the execution time of the query less the close query time.
     */
    private Long calculateExecutionTime(final TupleQuery query){
        try {
            long QUERY_TIME = 500; //time reference for sesame...
            if (query == null) {
                logger.warn("Unable to calculate the execution time, the TupleQuery is NULL");
                return null;
            }
            /*final TupleQueryResult[] result = new TupleQueryResult[1];
            final AtomicBoolean stop = new AtomicBoolean(false);
            final long[] times = new long[]{-1, -1};
            Runnable queryRunner = new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (result) {
                            result[0] = query.evaluate();
                            //logger.info(">>>>>>>> query evaluating");
                        }
                        while (result[0].hasNext()) {
                            //logger.info(">>>>>>>> query found result");
                            result[0].next();
                        }
                        times[0] = System.currentTimeMillis();
                        //logger.info(">>>>>>>> query finished:" + times[0]);
                        try {
                            result[0].close();
                        } catch (QueryEvaluationException e) {
                            logger.error(e.getMessage(), e);
                        }
                    } catch (QueryEvaluationException e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        stop.set(true);
                    }
                }
            };
            Runnable closeRunner = new Runnable() {
                @Override
                public void run() {
                    //logger.info("<<<<<<<<< waiting for query");
                    boolean doClose = false;
                    while (true) {
                        if (stop.get()) {
                            break;
                        }
                        synchronized (result) {
                            if (result[0] != null) {
                                doClose = true;
                                break;
                            }
                        }
                        sleep(100);
                    }
                    if (doClose) {
                        sleep(200);
                        try {
                            //logger.info("<<<<<<<<< closing query");
                            result[0].close();
                            times[1] = System.currentTimeMillis();
                            //logger.info("<<<<<<<<< query closed:"+times[1]);
                            stop.set(true);
                        } catch (QueryEvaluationException ignored) {
                            logger.error(ignored.getMessage());
                        }
                    } else {
                        stop.set(true);
                    }
                }
            };*/
           /* try {run(queryRunner, "<QUERY>"); } catch (NullPointerException ne) {*//*do nothing*//*}
            try {run(closeRunner, "<CLOSER>");} catch (NullPointerException ne) {*//*do nothing*//*}
            long start = System.currentTimeMillis();
            while (!stop.get()) {sleep(100);}
            logger.info("QUERY RUNNER: took = " + (times[0] - start) + "ms");
            logger.info("CLOSE RUNNER: took = " + (times[1] - start) + "ms");
            if (times[0] < QUERY_TIME)
                logger.info("the query should have been closed within the query timeout:" + times[0] + "ms");
            if (-1 == times[0])
                logger.info("the query runner should not have set an end time as it should have been cancelled");
                 logger.info("the query should have been closed within the query timeout:" + (times[0] - start) + "ms");*/

            Timer timer = new Timer();
            timer.startTimer();
            query.evaluate();
            return avoidTimeLostForTheCommunication(timer.endTimer());
            //return avoidTimeLostForTheCommunication((times[0] - start));
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return 0L;
        }
    }

    /**
     * Method to calculate the Query execution time of SPARQL or SeRQL query on a sesame repository
     * @param query  the GraphQuery to analyze
     * @return the Long value of the execution time of the query less the close query time.
     */
    private Long calculateExecutionTime(final GraphQuery query){

        try {
            long QUERY_TIME = 500; //time reference for sesame...
            if (query == null) {
                logger.warn("Unable to calculate the execution time, the GraphQuery is NULL");
                return null;
            }
            /*final GraphQueryResult[] result = new GraphQueryResult[1];
            final AtomicBoolean stop = new AtomicBoolean(false);
            final long[] times = new long[]{-1, -1};
            Runnable queryRunner = new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (result) {
                            result[0] = query.evaluate();
                            //logger.info(">>>>>>>> query evaluating");
                        }
                        while (result[0].hasNext()) {
                            //logger.info(">>>>>>>> query found result");
                            result[0].next();
                        }
                        times[0] = System.currentTimeMillis();
                        //logger.info(">>>>>>>> query finished:" + times[0]);
                        try {
                            result[0].close();
                        } catch (QueryEvaluationException e) {
                            logger.error(e.getMessage(), e);
                        }
                    } catch (QueryEvaluationException e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        stop.set(true);
                    }
                }
            };
            Runnable closeRunner = new Runnable() {
                @Override
                public void run() {
                    //logger.info("<<<<<<<<< waiting for query");
                    boolean doClose = false;
                    while (true) {
                        if (stop.get()) {
                            break;
                        }
                        synchronized (result) {
                            if (result[0] != null) {
                                doClose = true;
                                break;
                            }
                        }
                        sleep(100);
                    }
                    if (doClose) {
                        sleep(200);
                        try {
                            //logger.info("<<<<<<<<< closing query");
                            result[0].close();
                            times[1] = System.currentTimeMillis();
                            //logger.info("<<<<<<<<< query closed:"+times[1]);
                            stop.set(true);
                        } catch (QueryEvaluationException e) {
                            logger.error(e.getMessage(), e);
                        }
                    } else {
                        stop.set(true);
                    }
                }
            };*/
          /*  try {run(queryRunner, "<QUERY>");} catch (NullPointerException ne) {*//*do nothing*//*}
            try {run(closeRunner, "<CLOSER>");} catch (NullPointerException ne) {*//*do nothing*//*}
            long start = System.currentTimeMillis();
            while (!stop.get()) {sleep(100);}
            logger.info("QUERY RUNNER: took = " + (times[0] - start) + "ms");
            logger.info("CLOSE RUNNER: took = " + (times[1] - start) + "ms");
            if (times[0] < QUERY_TIME)
                logger.info("the query should have been closed within the query timeout:" + times[0] + "ms");
            if (-1 == times[0])
                logger.info("the query runner should not have set an end time as it should have been cancelled");
            logger.info("the query should have been closed within the query timeout:" + (times[0] - start) + "ms");*/

            Timer timer = new Timer();
            timer.startTimer();
            query.evaluate();
            return avoidTimeLostForTheCommunication(timer.endTimer());
            //return avoidTimeLostForTheCommunication((times[0] - start));
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return 0L;
        }
    }

    /**
     * Method utility for calculate the execution time.
     * @param time the input long time of the execution query.
     */
    private static void sleep(long time) {
        try { Thread.sleep(time);}
        catch (InterruptedException ex) { /* .... */ }
    }

    private static Long avoidTimeLostForTheCommunication(Long time){
        //these value are taken manually from the multiple test on the running of the project....
        logger.warn("Time before:"+time);
        //over 100ms there some time lost on connection on the server...
        if(time > 100 ) time = time - ThreadLocalRandom.current().nextInt(80, 90 + 1);
        else if(time >= 200) time = time - ThreadLocalRandom.current().nextInt(150, 190 + 1);
        else if(time >= 300) time = time - ThreadLocalRandom.current().nextInt(250, 290 + 1);
        else if(time >= 400) time = time - ThreadLocalRandom.current().nextInt(350, 390 + 1);
        else if(time >= 500) time = time - ThreadLocalRandom.current().nextInt(450, 490 + 1);
        else if(time >= 600) time = time - ThreadLocalRandom.current().nextInt(550, 590 + 1);
        else if(time >= 1000) time = time - ThreadLocalRandom.current().nextInt(1050, 1090 + 1);
        else if(time >= 2000) time = time - ThreadLocalRandom.current().nextInt(1950, 1990 + 1);
        else if(time >= 3000) time = time - ThreadLocalRandom.current().nextInt(2950, 2990 + 1);
        else if(time >= 4000) time = time - ThreadLocalRandom.current().nextInt(3950, 3990 + 1);
        else if(time >= 4500) time = time - ThreadLocalRandom.current().nextInt(4950, 4990 + 1);
        logger.warn("Time after:"+time);
        return time;
    }

    /**
     * Method utility for calculate the execution time.
     * @param runnable the Runnable object.
     * @param name the String name of the runnable.
     */
    private static void run(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setName(name);
        thread.start();
    }

    /**
     * Method to convert a String Query to a OpenRDF Operation.
     * @param query the String of the Query to analyze.
     * @return the OpenRDF Operation.
     */
    public Operation convertQueryToOperation(String query){
        return prepareOperation(query);
    }


    /**
     * Method to convert a String Query to a OpenRDF Operation.
     * @param query the OpenRDF Query to analyze.
     * @return the OpenRDF Operation.
     */
    public Operation convertQueryToOperation(Query query){
        return convertQueryToOperation(query.toString());
    }

    /**
     * Method to create a OpenRDF Statement.
     * @param subject the uri of the subject.
     * @param predicate the uri of the predicate.
     * @param objectOrUri the Object Value of the Literal Object.
     * @param context the uri of the context.
     * @return the OpenRDF Statement Object.
     */
    public Statement createStatement(Object subject,Object predicate,Object objectOrUri,Object context){
        ValueFactory factory = createValueFactory();
        Resource sub  = createResource(subject);
        URI pred = createURI(predicate);
        Value obj = createValue(objectOrUri);
        Resource cont = createResource(context);
        return factory.createStatement(sub,pred,obj,cont);
    }

    /**
     * Method to create OpenRDF URI
     * @param stringOrUri the String or OpenRDF Uri.
     * @return the OpenRDF URI.
     */
    public URI createPredicate(Object stringOrUri){
       return createURI(stringOrUri);
    }

    /**
     * Method toc reate a OpenRDF Literal.
     * @param literalObject the Object value of the Literal.
     * @return the OpenRDF Literal Object.
     */
    public Literal createLiteral(Object literalObject){
        ValueFactory factory = createValueFactory();
        if(literalObject instanceof String) return factory.createLiteral((String) literalObject);
        if(literalObject instanceof Boolean) return factory.createLiteral((Boolean) literalObject);
        if(literalObject instanceof Byte) return factory.createLiteral((Byte) literalObject);
        if(literalObject instanceof Short) return factory.createLiteral((Short) literalObject);
        if(literalObject instanceof Integer) return factory.createLiteral((Integer) literalObject);
        if(literalObject instanceof Long) return factory.createLiteral((Long) literalObject);
        if(literalObject instanceof Float) return factory.createLiteral((Float) literalObject);
        if(literalObject instanceof Double) return factory.createLiteral((Double) literalObject);
        if(literalObject instanceof XMLGregorianCalendar) return factory.createLiteral((XMLGregorianCalendar) literalObject);
        if(literalObject instanceof Date) return factory.createLiteral((Date) literalObject);
        else return null;
    }

    /**
     * Method to create a Resource OpenRDF.
     * @param uriOrString the String URI.
     * @return the OpenRDF resource.
     */
    public Resource createResource(Object uriOrString){
        ValueFactory factory = createValueFactory();
        if(uriOrString instanceof BNode) return (Resource) uriOrString;
        else if(uriOrString instanceof String){
            //if (uriOrString instanceof BNode) return (BNode) uriOrString;
            return factory.createURI(String.valueOf(uriOrString));
        }
        else if(uriOrString instanceof URI) return (Resource) uriOrString;
        else if(uriOrString instanceof Literal) return (Resource) uriOrString;
        //else return new URIImpl(String.valueOf(uriOrString));
        else {
            logger.warn("Can't create the Resource with the object specified.");
            return null;
        }
    }

    /**
     * Method to create a OpenRDF Value.
     * @param resourceOrLiteral the resource or the Literal OpenRDF.
     * @return the OpenRDF Value.
     */
    public Value createValue(Object resourceOrLiteral){
        //if(resourceOrLiteral instanceof URI) return (URI) resourceOrLiteral;
        if(resourceOrLiteral instanceof String) {
            if(StringUtilities.isURL(String.valueOf(resourceOrLiteral))) return createURI(resourceOrLiteral);
            else return createLiteral(resourceOrLiteral);
        }
        if(resourceOrLiteral instanceof Resource) return (Value) resourceOrLiteral;
        if(resourceOrLiteral instanceof Literal) return (Value) resourceOrLiteral;
        else return null;
    }

    /**
     * Method to create OpenRDF URI
     * @param uri the String or OpenRDF Uri.
     * @return the OpenRDF URI.
     */
    public URI createURI(Object uri){
        ValueFactory factory = createValueFactory();
        if(uri instanceof String) return factory.createURI(String.valueOf(uri));
        if(uri instanceof URI) return (URI) uri;
        else return null;
    }




    /*public OntologyTupleQuery  createQuery(String query) {
        if(mRepositoryConnection != null) {
            return new UtilTupleQueryIterator(
                    this,
                    query,
                    OConstants.QueryLanguage.SPARQL);
        } else {
            throw new SesameManagerException("Cannot create a query, no connection");
        }
    }*/


}

