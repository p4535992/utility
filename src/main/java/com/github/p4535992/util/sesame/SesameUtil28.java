package com.github.p4535992.util.sesame;

import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import info.aduna.iteration.Iterations;

import org.openrdf.OpenRDFException;
import org.openrdf.http.client.SesameClient;
import org.openrdf.http.client.SesameClientImpl;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.*;
import org.openrdf.repository.*;
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
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.model.impl.TreeModel;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
/**
 * Class of utility for Sesame Server and Owlim Server
 * @author 4535992.
 * @version 2015-07-02.
 * Work with Seame openrdf version 2.8.0
 */
@SuppressWarnings("unused")
public class SesameUtil28 {

    private static SesameUtil28 instance = null;
    protected SesameUtil28(){}
    public static SesameUtil28 getInstance(){
        if(instance == null) {
            instance = new SesameUtil28();
        }
        return instance;
    }

    private String PATH_FOLDER_REPOSITORY, SESAMESERVER,TYPE_REPOSITORY,ID_REPOSITORY,USER_REPOSITORY,PASSWORD_REPOSITORY;
    private boolean PARALLEL_LOAD,VERIFY,STOP_ON_ERROR,PRESERVE_BNODES,SHOWSTATS,UPDATES,PRINT_RESULT_QUERY;
    //output parameter
    private String OUTPUTFILE,OUTPUTFORMAT,URL_REPOSITORY,URL_SESAME,URL_REPOSITORY_ID;
    //other parameter
    //private String CONFIGFILENAME;
    boolean isManagedRepository = false;

    // A map of namespace-to-prefix
    protected static Map<String, String> namespacePrefixes = new HashMap<>();
    // The repository manager
    protected static RepositoryManager mRepositoryManager;
    protected static RemoteRepositoryManager mRemoteRepositoryManager;
    // From repositoryManager.getRepository(...) - the actual repository we will work with
    protected static Repository mRepository;
    // From repositoryManager.getRepository(...) - the actual repository we will work with
    protected static RepositoryConnection mRepositoryConnection;
    protected static String  mRepositoryLocation;
    protected static String  mRepositoryName;
    protected static RepositoryProvider mRepositoryProvider;

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{%[\\p{Print}&&[^\\}]]+%\\}");

    public void setOutput(String outputPathfile,String outputformat,boolean printResultQuery){
        this.OUTPUTFILE = outputPathfile;
        this.OUTPUTFORMAT = outputformat;
        this.PRINT_RESULT_QUERY = printResultQuery;
    }

    public void setParameterLocalRepository(
            String TYPE_REPOSITORY,String PATH_FOLDER_STORAGE,String PATH_FOLDER_REPOSITORY,
            String RULESET,String USER_REPOSITORY,String PASSWORD_REPOSITORY
    ){
        this.TYPE_REPOSITORY=TYPE_REPOSITORY;
        //String PATH_FOLDER_STORAGE1 = PATH_FOLDER_STORAGE;
        this.PATH_FOLDER_REPOSITORY=PATH_FOLDER_REPOSITORY;
        //this.RULESET=RULESET;
        this.USER_REPOSITORY= USER_REPOSITORY;
        this.PASSWORD_REPOSITORY= PASSWORD_REPOSITORY;
        this.ID_REPOSITORY = new File(PATH_FOLDER_REPOSITORY).getName();
       // setURLRepository();
    }

    public void setParameterRemoteRepository(
            String TYPE_REPOSITORY,String SESAMESERVER,String ID_REPOSITORY,String USER_REPOSITORY,String PASSWORD_REPOSITORY){
        this.TYPE_REPOSITORY=TYPE_REPOSITORY;
        this.SESAMESERVER=SESAMESERVER;
        this.ID_REPOSITORY=ID_REPOSITORY;
        this.USER_REPOSITORY= USER_REPOSITORY;
        this.PASSWORD_REPOSITORY= PASSWORD_REPOSITORY;
        setURLRepository();
    }

    public String getURL_REPOSITORY() {return URL_REPOSITORY;}

    public String getURL_SESAME() {return URL_SESAME;}

    public String getURL_REPOSITORY_ID() {return URL_REPOSITORY_ID;}

    private void setURLRepository(){
        this.URL_SESAME = "http://localhost:8080/openrdf-sesame/" + ID_REPOSITORY;
        this.URL_REPOSITORY = "http://www.openrdf.org/repository#"+ ID_REPOSITORY;
        this.URL_REPOSITORY_ID = "http://www.openrdf.org/repository#"+ ID_REPOSITORY;
    }

    /**
     * Method for Close the currently opened repository. This works for managed and unmanaged repositories.
     */
    public static void  closeRepository() {
        SystemLog.message("===== Shutting down ==========");
        if (mRepositoryConnection != null) {
            try {
                SystemLog.message("Commiting the connection");
                //mRepositoryConnection.commit();
                SystemLog.message("Closing the connection");
                mRepositoryConnection.close();
                SystemLog.message("Connection closed");
                // the following is NOT needed as the manager shutDown method
                // shuts down all repositories
                // mRepository.shutDown();
                SystemLog.message("Repository shut down");
            } catch (RepositoryException e) {
                SystemLog.exception(e);
                // TODO: do not throw exception, might still need to disconnect
                // manager!
                // throw new SesameManagerException("Could not close Repository: "+e);
            }
            mRepositoryConnection = null;
            mRepository = null;
            mRepositoryName = null;
            SystemLog.message("connection, repository and repositoryID set to null");
        }
    }

    /**
     * Method for connect to a loacl Sesame Repository with a config turtle file.
     * @return repository manager sesame.
     */
    public RepositoryManager connectToLocalWithConfigFile(){
            /*
            try {
                // Create a manager for local repositories and initialise it
                repositoryManager = new LocalRepositoryManager(new File("."));
                repositoryManager.initialize();
            } catch (RepositoryException e) {
                log("");
                System.exit(-3);
            }
            */
        //repositoryId = parameters.get(PARAM_REPOSITORY);
        if (ID_REPOSITORY == null) {
            SystemLog.warning("No repository ID specified. When using the '" + URL_REPOSITORY
                    + "' parameter to specify a Sesame server, you must also use the 'null' " +
                    "parameter to specify a repository on that server.");
            System.exit(-5);
        }
        try {
            // Create a manager for the remote Sesame server and initialise it
            //org.openrdf.repository.manager.RemoteRepositoryManager remote =
            //        new org.openrdf.repository.manager.RemoteRepositoryManager(URL_REPOSITORY);
            mRemoteRepositoryManager = new RemoteRepositoryManager(URL_REPOSITORY);
            if ( USER_REPOSITORY != null || PASSWORD_REPOSITORY != null) {
                if (USER_REPOSITORY == null)
                    USER_REPOSITORY = "";
                if (PASSWORD_REPOSITORY == null)
                    PASSWORD_REPOSITORY = "";
                mRemoteRepositoryManager.setUsernameAndPassword(USER_REPOSITORY,PASSWORD_REPOSITORY);
            }
            mRepositoryManager = mRemoteRepositoryManager;
            mRepositoryManager.initialize();
        } catch (RepositoryException e) {
            SystemLog.warning("Unable to establish a connection with the Sesame server '" + URL_REPOSITORY + "': "
                    + e.getMessage());
            System.exit(-5);
        }

        // Get the repository to use
        try {
            mRepository = mRepositoryManager.getRepository(ID_REPOSITORY);

            if (mRepository == null) {
                SystemLog.warning("Unknown repository '" + ID_REPOSITORY + "'");
                String message = "Please make sure that the value of the 'repository' "
                        + "parameter (current value '" + ID_REPOSITORY + "') ";
                if (URL_REPOSITORY == null) {
                    message += "corresponds to the repository ID given in the configuration file identified by the '"
                            + "CONFIGFILENAME' parameter (current value '????????????')";
                } else {
                    message += "identifies an existing repository on the Sesame server located at " + URL_REPOSITORY;
                }
                SystemLog.warning(message);
                System.exit(-6);
            }

            // Open a connection to this repository
            mRepositoryConnection = mRepository.getConnection();
            //repositoryConnection.setAutoCommit(false);//deprecated
        } catch (OpenRDFException e) {
            SystemLog.warning("Unable to establish a connection to the repository '" + ID_REPOSITORY + "': "
                    + e.getMessage());
            System.exit(-7);
        }
        return mRepositoryManager;
    }

    /**
     * Parse the given RDF file and return the contents as a Graph.
     * @param configurationFile the file containing the RDF data.
     * @param format RDFFormat of configurationFile.
     * @param defaultNamespace base URI of the configurationFile.
     * @return The contents of the file as an RDF graph.  
     */
    public static Model convertFileToSesameModel(File configurationFile, RDFFormat format, String defaultNamespace) {
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
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Parses and loads all files specified in PARAM_PRELOAD.
     * @param preloadFolder e.home.  "./preload".
     * @throws Exception thorw if any error is occurred.
     */
    public void importIntoRepositoryDirectoryChunked(String preloadFolder) throws Exception {
        SystemLog.message("===== Load Files (from the '" + preloadFolder + "' parameter) ==========");
        final AtomicLong statementsLoaded = new AtomicLong();
        // Load all the files from the pre-load folder
        //String preload = preloadFolder;
        if (preloadFolder == null)
            SystemLog.message("No pre-load directory/filename provided.");
        else {
            FileUtil.FileWalker.Handler handler = new FileUtil.FileWalker.Handler() {

                @Override
                public void file(File file) throws Exception {
                    statementsLoaded.addAndGet( importIntoRepositoryFileChunked(file) );
                }

                @Override
                public void directory(File directory) throws Exception {
                    SystemLog.message("Loading files from: " + directory.getAbsolutePath());
                }
            };
            FileUtil.FileWalker walker = new FileUtil.FileWalker();
            walker.setHandler(handler);
            walker.walk(new File(preloadFolder));
        }
        SystemLog.warning("TOTAL: " + statementsLoaded.get() + " statements loaded");
    }

    /**
     * Method for Show some initialisation statistics.
     * @param startupTime range of  illiseconds before start the inizialization.
     * @throws Exception throw if any error is occurrred.
     */
    public void showInitializationStatistics(long startupTime) throws Exception {
        if (SHOWSTATS) {
            long explicitStatements = numberOfExplicitStatements();
            long implicitStatements = numberOfImplicitStatements();
            SystemLog.message("Loaded: " + explicitStatements + " explicit statements.");
            SystemLog.message("Inferred: " + implicitStatements + " implicit statements.");
            if (startupTime > 0) {
                double loadSpeed = explicitStatements / (startupTime / 1000.0);
                SystemLog.message(" in " + startupTime + "ms.");
                SystemLog.message("Loading speed: " + loadSpeed + " explicit statements per second.");
            } else {
                SystemLog.message(" in less than 1 second.");
            }
            SystemLog.message("Total number of statements: " + (explicitStatements + implicitStatements));
        }
    }

    /**
     * Two approaches for finding the total number of explicit statements in a repository.
     *
     * @return The number of explicit statements.
     */
    public long numberOfExplicitStatements() {
        try{
            // This call should return the number of explicit statements.
            long explicitStatements = mRepositoryConnection.size();
                    // Another approach is to get an iterator to the explicit statements(by setting the includeInferred parameter
            // to false) and then counting them.
            RepositoryResult<Statement> statements = mRepositoryConnection.getStatements(null, null, null, false);
            while (statements.hasNext()) {
                statements.next();
                explicitStatements++;
            }
            statements.close();
            return explicitStatements;
        }catch(RepositoryException e){
            SystemLog.exception(e);
        }
        return 0;
    }

    /**
     * A method to count only the inferred statements in the repository. No method for this is available
     * through the Sesame API, so OWLIM uses a special context that is interpreted as instruction to retrieve
     * only the implicit statements, i.e. not explicitly asserted in the repository.
     *
     * @return The number of implicit statements.
     * @throws Exception throw if any error is occurrred.
     */
    private long numberOfImplicitStatements() throws Exception {
        // Retrieve all inferred statements
        RepositoryResult<Statement> statements = mRepositoryConnection.getStatements(null, null, null, true,
                new URIImpl("http://www.ontotext.com/implicit"));
        long implicitStatements = 0;

        while (statements.hasNext()) {
            statements.next();
            implicitStatements++;
        }
        statements.close();
        return implicitStatements;
    }

    /**
     * Iterates and collects the list of the namespaces, used in URIs in the repository.
     * @return map of all namespace used on the repository.
     */
    public Map<String,String> iterateNamespaces(){
        try{
            SystemLog.message("===== Namespace List ==================================");
            SystemLog.message("Namespaces collected in the repository:");
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
            //If the local repository used to test the query type failed for some reason.
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Demonstrates query evaluation. First parse the query file. Each of the queries is executed against the
     * prepared repository. If the printResults is set to true the actual values of the bindings are output to
     * the console. We also count the time for evaluation and the number of results per query and output this
     * information.
     * @param queryFile file with multiple SPARQL queries.
     */
    public void evaluateQueries(File queryFile){
        SystemLog.message("===== Query Evaluation ======================");
        if (queryFile == null) {
            SystemLog.warning("No query file given in parameter 'null'.");
            return;
        }
        long startQueries = System.currentTimeMillis();
        // process the query file to get the queries
        String[] queries =collectQueries(queryFile.getAbsolutePath());
        if(queries == null){
            //se non Ã¯Â¿Â½ un file ma una stringa fornita queries = new String[]{queryFile};
            SystemLog.message("Executing query '" + queryFile + "'");
            executeSingleQuery(queryFile.getAbsolutePath());
        }else{
            // evaluate each query and print the bindings if appropriate
            for (String querie : queries) {
                final String name = querie.substring(0, querie.indexOf(":"));
                final String query = querie.substring(name.length() + 2).trim();
                SystemLog.message("Executing query '" + name + "'");
                executeSingleQuery(query);
            }
        }
        long endQueries = System.currentTimeMillis();
        SystemLog.message("Queries run in " + (endQueries - startQueries) + " ms.");
    }

    /**
     * The purpose of this method is to try to parse an operation locally in order to determine if it is a
     * tuple (SELECT), boolean (ASK) or graph (CONSTRUCT/DESCRIBE) query, or even a SPARQL update.
     * This happens automatically if the repository is local, but for a remote repository the local
     * HTTPClient-side can not work it out.
     * Therefore a temporary in memory SAIL is used to determine the operation type.
     *
     * @param query
     *            Query string to be parsed.
     * @param language
     *            The query language to assume.
     * @param tempLocalConnection temporary local connection.
     * @return A parsed query object or null if not possible.             
     */
    private static Query prepareQuery(String query, QueryLanguage language,RepositoryConnection tempLocalConnection){
        //Repository tempLocalRepository = new SailRepository(new MemoryStore());
        //tempLocalRepository.initialize();
        //RepositoryConnection tempLocalConnection = tempLocalRepository.getConnection();
        try {
            tempLocalConnection.prepareTupleQuery(language, query);
            //TupleQuery tupleQuery = mRepositoryConnection.prepareTupleQuery(language, query);
            //if(tupleQuery!=null){return tupleQuery;}
            return mRepositoryConnection.prepareTupleQuery(language, query);
        } catch (RepositoryException|MalformedQueryException e) {
            SystemLog.warning(e.getMessage());
        }

        try {
            tempLocalConnection.prepareBooleanQuery(language, query);
            //BooleanQuery booleanQuery = mRepositoryConnection.prepareBooleanQuery(language, query);
            //if(booleanQuery!=null){ return booleanQuery;}
            return mRepositoryConnection.prepareBooleanQuery(language, query);
        } catch (RepositoryException|MalformedQueryException e) {
            SystemLog.warning(e.getMessage());
        }

        try {
            tempLocalConnection.prepareGraphQuery(language, query);
            //GraphQuery graphQuery = mRepositoryConnection.prepareGraphQuery(language, query);
            //if(graphQuery!=null){return graphQuery;}  
            return mRepositoryConnection.prepareGraphQuery(language, query);
        } catch (RepositoryException|MalformedQueryException e) {
            SystemLog.warning(e.getMessage());
        }
        return null;
    }

    private static Operation prepareOperation(String query) throws RepositoryException{
        Repository tempLocalRepository = new SailRepository(new MemoryStore());
        tempLocalRepository.initialize();
        RepositoryConnection tempLocalConnection = tempLocalRepository.getConnection();
        try {
            tempLocalConnection.prepareUpdate(QueryLanguage.SPARQL, query);
            return mRepositoryConnection.prepareUpdate(QueryLanguage.SPARQL, query);
        }
        catch(RepositoryException|MalformedQueryException e ) {
            SystemLog.exception(e);
        }
        try {
            for (QueryLanguage language : queryLanguages) {
                Query result = prepareQuery(query, language, tempLocalConnection);
                if (result != null)
                    return result;
            }
            // Can't prepare this query in any language
            return null;
        }
        finally {
            try {
                tempLocalConnection.close();
                tempLocalRepository.shutDown();
            }
            catch(Exception e ) {
                SystemLog.exception(e);
            }
        }
    }

    private static final QueryLanguage[] queryLanguages = new QueryLanguage[] {
            QueryLanguage.SPARQL,QueryLanguage.SERQL, QueryLanguage.SERQO };

    public void executeSingleQuery(String query) {
        try {
            Operation preparedOperation = prepareOperation(query);
            if (preparedOperation == null) {
                SystemLog.warning("Unable to parse query: " + query);
                return;
            }

            if( preparedOperation instanceof Update) {
                ( (Update) preparedOperation).execute();
                mRepositoryConnection.commit();
                return;
            }

            if (preparedOperation instanceof BooleanQuery) {
                SystemLog.message("Result: " + ((BooleanQuery) preparedOperation).evaluate());
                return;
            }

            if (preparedOperation instanceof GraphQuery) {
                GraphQuery q = (GraphQuery) preparedOperation;
                if(PRINT_RESULT_QUERY){
                   writeGraphQueryResultToFile(query, OUTPUTFILE, OUTPUTFORMAT);
                }
                long queryBegin = System.nanoTime();

                GraphQueryResult result = q.evaluate();
                
                int rows = 0;
                while (result.hasNext()) {
                    Statement statement = result.next();
                    rows++;
                    
                    if (SHOWSTATS) {
                        System.out.print(beautifyRDFValue(statement.getSubject()));
                        System.out.print(" " + beautifyRDFValue(statement.getPredicate()) + " ");
                        System.out.print(" " + beautifyRDFValue(statement.getObject()) + " ");
                        Resource context = statement.getContext();
                        if (context != null)
                            System.out.print(" " + beautifyRDFValue(context) + " ");
                        System.out.println();
                    }
                    
                }
                if (SHOWSTATS)System.out.println();
                             
                result.close();

                long queryEnd = System.nanoTime();
                SystemLog.message(rows + " result(s) in " + (queryEnd - queryBegin) / 1000000 + "ms.");
            }

            if (preparedOperation instanceof TupleQuery) {
                TupleQuery q = (TupleQuery) preparedOperation;
                if(PRINT_RESULT_QUERY){
                    writeTupleQueryResultToFile(query, OUTPUTFILE, OUTPUTFORMAT);
                }

                long queryBegin = System.nanoTime();

                TupleQueryResult result = q.evaluate();

                int rows = 0;
                while (result.hasNext()) {
                    BindingSet bindingSetTuples = result.next();
                    if (rows == 0) {
                        for (Binding bindingSetTuple : bindingSetTuples) {
                            System.out.print(bindingSetTuple.getName());
                            System.out.print("\t");
                        }
                        System.out.println();
                        System.out.println("---------------------------------------------");
                    }
                    rows++;
                    if (SHOWSTATS) {
                        for (Binding aTuple : bindingSetTuples) {
                            try {
                                System.out.print(beautifyRDFValue(aTuple.getValue()) + "\t");
                            } catch (Exception e) {
                                SystemLog.exception(e);
                            }
                        }
                        System.out.println();
                    }
                }
                if (SHOWSTATS)
                    System.out.println();

                result.close();

                long queryEnd = System.nanoTime();
                SystemLog.message(rows + " result(s) in " + (queryEnd - queryBegin) / 1000000 + "ms.");
            }
        } catch (UpdateExecutionException|RepositoryException|QueryEvaluationException e) {
            SystemLog.error("An error occurred during query execution: " + e.getMessage());
        } catch (FileNotFoundException e){
            SystemLog.error("An error occurred during the writing of the result of SPARQL query: " + e.getMessage());
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
            SystemLog.message("===== Upload and Delete Statements ====================");

            // Add a statement directly to the SAIL
            SystemLog.message("----- Upload and check --------------------------------");
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
            // CLOSE the iterator to avoid memory leaks
            iter.close();

            if (!retrieved)SystemLog.message("**** Failed to retrieve the statement that was just added. ****");

            // Remove the above statement in a separate transaction
            SystemLog.message("----- Remove and check --------------------------------");
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
            if (retrieved)SystemLog.message("**** Statement was not deleted properly in last step. ****");
        }
      }catch(RepositoryException e){
          SystemLog.exception(e);
      }
    }

    /**
     * Export the contents of the repository (explicit, implicit or all statements) to the given filename in
     * the given RDF format.
     * @param outputPathFile string path to the output file.
     * @param outputFormat string format for the output file.
     * @param exportType e.home //explicit,implicit,all,specific.
     */
    public void export(String outputPathFile,String outputFormat,String exportType){      
        //String filename = parameters.get(PARAM_EXPORT_FILE);
        //String filename = outputPathFile;
        try{
            if (outputPathFile != null) {
                SystemLog.message("===== Export ====================");
                RDFFormat exportFormat = stringToRDFFormat(outputFormat);
                //String type = exportType;
                SystemLog.message("Exporting " + exportType + " statements to " + outputPathFile + " (" + exportFormat.getName() + ")");
                Writer writer = new BufferedWriter(new FileWriter(outputPathFile), 256 * 1024);
                RDFWriter rdfWriter = Rio.createWriter(exportFormat, writer);
                // This approach to making a backup of a repository by using RepositoryConnection.exportStatements()
                // will work even for very large remote repositories, because the results are streamed to the client
                // and passed directly to the RDFHandler.
                // However, it is not possible to give any indication of progress using this method.
                try {
                    if (exportType == null || exportType.equalsIgnoreCase("explicit"))
                        mRepositoryConnection.exportStatements(null, null, null, false, rdfWriter);
                    else if (exportType.equalsIgnoreCase("all"))
                        mRepositoryConnection.exportStatements(null, null, null, true, rdfWriter);
                    else if (exportType.equalsIgnoreCase("implicit"))
                        mRepositoryConnection.exportStatements(null, null, null, true, rdfWriter,
                                new URIImpl( "http://www.ontotext.com/implicit"));
                    else {
                        SystemLog.warning("Unknown export type '" + exportType + "' - valid values are: explicit, implicit, all");
                    }
                }
                finally {
                    writer.close();
                }
            }//end if
        }catch(RepositoryException|UnsupportedRDFormatException|RDFHandlerException|IOException e){
                SystemLog.exception(e);
        }
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
            SystemLog.exception(e);
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
    public String beautifyStatement(Statement statement){
        return beautifyRDFValue(statement.getSubject()) + " " + beautifyRDFValue(statement.getPredicate())
                + " " + beautifyRDFValue(statement.getObject());
    }

    /**
     * Method utility: printing an RDF value in a "fancy" manner. 
     * In case of URI, qnames are printed for better readability
     *
     * @param value The value to beautify.
     * @return string to print on a file on to the console.
     */
    public static String beautifyRDFValue(Value value){
        if (value instanceof URI) {
            URI u = (URI) value;
            String namespace = u.getNamespace();
            String prefix = namespacePrefixes.get(namespace);
            if (prefix == null) {
                prefix = u.getNamespace();
            } else {
                prefix += ":";
            }
            String sReturn = prefix + u.getLocalName();
            //sReturn = sReturn.replaceAll("|", "").replaceAll("^", "").replaceAll("\n", "").replaceAll("'", "");
            if(sReturn.contains("<") && sReturn.contains(">")){
                return sReturn;
            }else{
                sReturn = sReturn.replaceAll("|", "").replaceAll("^", "").replaceAll("\n", "").replaceAll("'", "");
                return "<"+sReturn+">";
            }
        } else {
            return value.toString().trim().replaceAll("\n", "");
        }
    }


    public void writeSesameModelToFile(Model myGraph,String outpuPathtFile,String outputFormat) {
        // a collection of several RDF statements
        try{
            FileOutputStream out = new FileOutputStream(outpuPathtFile);
            RDFWriter writer = Rio.createWriter(stringToRDFFormat(outputFormat), out);
            writer.startRDF();
            for (Statement st: myGraph) {
                writer.handleStatement(st);
            }
            writer.endRDF();
            
        } catch (RDFHandlerException|FileNotFoundException e) {
            SystemLog.exception(e);
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
                    SystemLog.exception(e);
                }

                try {
                    tempConnection.prepareBooleanQuery(language, query);
                    return mRepositoryConnection.prepareBooleanQuery(language, query);
                } catch (MalformedQueryException|RepositoryException e) {
                    SystemLog.exception(e);
                }

                try {
                    tempConnection.prepareGraphQuery(language, query);
                    return mRepositoryConnection.prepareGraphQuery(language, query);
                } catch (MalformedQueryException|RepositoryException e) {
                    SystemLog.exception(e);
                }
                return null;  
            } finally {
                try {
                    tempConnection.close();
                    tempRepository.shutDown();
                } catch (RepositoryException e) {
                SystemLog.exception(e);    
                } 
            }
        }catch (RepositoryException e) {
            SystemLog.exception(e);    
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
        // Can't prepare this query in any language
        return null;
    }


    /**
     * Method for open many different repository type of Sesame/Owlim memorized on disk.
     * @param TYPE_REPOSITORY choose your type of reposiotry ['owlim,'memory','native','inferencing','http'].
     * @param PATH_FOLDER_STORAGE string to the path folder where are stored your repositories.
     * @param PATH_FOLDER_REPOSITORY string to the path folder where is stored your specific repository.
     * @param RULESET just for owlim repsoitory eg 'owlim-se'.
     * @param USER_REPOSITORY username for the repository sesame.
     * @param PASSWORD_REPOSITORY password for the repository sesame.
     * @return repository sesame for the specific connection.    
     */
    public Repository connectToLocal(
            String TYPE_REPOSITORY,String PATH_FOLDER_STORAGE,String PATH_FOLDER_REPOSITORY,
            String RULESET,String USER_REPOSITORY,String PASSWORD_REPOSITORY){
        setParameterLocalRepository(
                TYPE_REPOSITORY, PATH_FOLDER_STORAGE, PATH_FOLDER_REPOSITORY, RULESET, USER_REPOSITORY, PASSWORD_REPOSITORY);
        return connectToSpecificRepository();
    }

    /**
     * Method for open many different repository type of Sesame/Owlim memorized on server.
     * @param TYPE_REPOSITORY  choose your type of reposiotry ['owlim,'memory','native','inferencing','http'].
     * @param SESAMESERVER url address to the sesame setver eg http://localhost:8080/openrdf-sesame/.
     * @param ID_REPOSITORY string id of the repository.
     * @param USER_REPOSITORY username for the repository sesame.
     * @param PASSWORD_REPOSITORY password for the repository sesame.
     * @return repository sesame for the specific connection.    
     */
    public Repository connectToRemote(
            String TYPE_REPOSITORY,String SESAMESERVER,String ID_REPOSITORY,String USER_REPOSITORY,String PASSWORD_REPOSITORY){
        setParameterRemoteRepository(TYPE_REPOSITORY,SESAMESERVER,ID_REPOSITORY,USER_REPOSITORY,PASSWORD_REPOSITORY);
        return connectToSpecificRepository();
    }

    /**
     * Method for make the connection to a repository.
     * @return repository sesame for the specific connection.
     */
    private Repository connectToSpecificRepository(){
        try{
            mRepository = null;
            SystemLog.message("Try to open a connection to a repository Sesame of TYPE:"+TYPE_REPOSITORY+" and ID:"+ID_REPOSITORY+"...");
            File Datadir = new File(PATH_FOLDER_REPOSITORY) ;
            /*
            if(TYPE_REPOSITORY.toLowerCase().contains("owlim")){
                 com.ontotext.trree.OwlimSchemaRepository schema = new com.ontotext.trree.OwlimSchemaRepository();
                 schema.setDataDir(Datadir);
                 schema.setParameter("storage-folder", PATH_FOLDER_STORAGE);
                 schema.setParameter("repository-type", TYPE_REPOSITORY);
                 schema.setParameter("ruleset", RULESET);
                 // wrap it into a Sesame SailRepository
                 //SailRepository repository = new SailRepository(schema);
                mRepository  = new org.openrdf.repository.sail.SailRepository(schema);
            }*/
            if(TYPE_REPOSITORY.toLowerCase().contains("memory")){
                //Create and initialize a non-inferencing main-memory repository
                //the MemoryStore will write its contents to the directory so that 
                //it can restore it when it is re-initialized in a future session
                mRepository =  new org.openrdf.repository.sail.SailRepository(
                        new org.openrdf.sail.memory.MemoryStore(Datadir)
                               // .setDataDir(Datadir)
                     );
                //or
                /*
                org.openrdf.sail.memory.MemoryStore memStore=
                       new org.openrdf.sail.memory.MemoryStore(
                                     new File(PATH_FOLDER_REPOSITORY) 
                        ); 
                Repository repo = new org.openrdf.repository.sail.SailRepository(memStore); 
                */
            }      
            else if(TYPE_REPOSITORY.toLowerCase().contains("native")){
                //Creating a Native RDF Repository
                //does not keep data in main memory, but instead stores it directly to disk
                String indexes = "spoc,posc,cosp";
                mRepository = new org.openrdf.repository.sail.SailRepository(
                            new org.openrdf.sail.nativerdf.NativeStore(Datadir,indexes)
                    );
            }
            else if(TYPE_REPOSITORY.toLowerCase().contains("inferencing")){
                //Creating a repository with RDF Schema inferencing
                //ForwardChainingRDFSInferencer is a generic RDF Schema 
                //inferencer (MemoryStore and NativeStore support it)
                mRepository = new SailRepository( new ForwardChainingRDFSInferencer(
                                    new org.openrdf.sail.memory.MemoryStore() )
                    );
            }
            else if(TYPE_REPOSITORY.toLowerCase().contains("http")){
                //Accessing a server-side repository
                mRepository= new HTTPRepository(SESAMESERVER, ID_REPOSITORY);

            }
            // wrap it into a Sesame SailRepository
            // initialize
            if(mRepository != null && !mRepository.isInitialized()){
                try {
                    mRepository.initialize();
                    return mRepository;
                } catch (RepositoryException e) {
                    SystemLog.error(e.getMessage());
                    // Something went wrong during the transaction, so we roll it back
                    mRepository.getConnection().rollback();
                }finally{
                    // Whatever happens, we want to close the connection when we are done.
                    mRepository.getConnection().close();
                }
                return null;
            }else{
                return null;
            }
        }catch(RepositoryException e){
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
     */
    public static void TupleQueryEvalutation(String queryString){
        try {
            //RepositoryConnection con = repo.getConnection();
            //repositoryConnection = repo.getConnection();
            if(!(mRepository.isInitialized() && mRepository.getConnection().isOpen())){
                mRepository.initialize();
                 mRepositoryConnection = mRepository.getConnection();
            }
            //String queryString = " SELECT ?x ?y WHERE { ?x ?p ?y } ";
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            TupleQuery tupleQuery = mRepositoryConnection.prepareTupleQuery(
                    lang, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
                List<String> bindingNames = result.getBindingNames();
                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    Value firstValue = bindingSet.getValue(bindingNames.get(0)); //get X
                    Value secondValue = bindingSet.getValue(bindingNames.get(1));//get Y
                    // do something interesting with the values here...
                }

            } finally {
                result.close();
            }
        } catch (OpenRDFException e) {
            SystemLog.exception(e);
        }
    }

    /**
     * Method to support the evalutation o the Graph query.    
     * @param queryString the query sparql CONSTRUCTOR or DESCRIBE.
     */
     public static void GraphQueryEvalutation(String queryString){
          try { 
            //org.openrdf.repository.RepositoryConnection con = repo.getConnection();
            //repositoryConnection = repo.getConnection();
            if(!(mRepository.isInitialized() && mRepository.getConnection().isOpen())){
                mRepository.initialize();
                 mRepositoryConnection = mRepository.getConnection();
            }
             //String queryString = "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }";
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            GraphQueryResult result = mRepositoryConnection.prepareGraphQuery(
                lang,queryString ).evaluate();
            try {
                while (result.hasNext()) {
                    Statement stmt = result.next();
                    // ... do something with the resulting statement here.
                }
            } finally {
                result.close();
            }
        } catch (OpenRDFException e) {
              SystemLog.exception(e);
        }
      }

    /**
     * Method to convert the result of a GraphQuery to a Model Sesame.
     * @param repository repository where you want evalutate the quey SPARQL.
     * @param queryString the query sparql CONSTRUCTOR or DESCRIBE.
     * @return model filled with the result of the quey on the repository connection.
     */
     public static Model convertGraphQueryEvalutationToSesameModel(Repository repository,String queryString){
        Model resultModel = new TreeModel();
        //org.openrdf.repository.RepositoryConnection
        //repositoryConnection = repo.getConnection();
        try {
             //String queryString = "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }";
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            GraphQueryResult result = mRepositoryConnection.prepareGraphQuery(
                lang,queryString ).evaluate();
            try {
                resultModel = QueryResults.asModel(result);
            } finally {
                result.close();
            }
            } catch (OpenRDFException e) {
                SystemLog.exception(e);
            }
         return resultModel;
     }

    /**
     * Method to print the query result of Graph Query on a Sesame Repository.
     * @param queryString string SPARQL/SERQL query.
     * @param filePath string file path to the output file.
     * @param outputFormat string of the output format.
     */
    public static void writeGraphQueryResultToFile(String queryString,String filePath,String outputFormat){
        try {
            String nameFileOut = filePath+"."+outputFormat.toLowerCase();
            OutputStream fileOut = new FileOutputStream( filePath+"."+outputFormat.toLowerCase());
            SystemLog.message("Try to write the query graph result in the format:" + stringToRDFFormat(outputFormat).toString() +
                    " int o the file " + nameFileOut + "...");
            RDFWriter writer = Rio.createWriter(stringToRDFFormat(outputFormat), fileOut);
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            mRepositoryConnection.prepareGraphQuery(lang, queryString).evaluate(writer);
            //query.evaluate(writer);
            SystemLog.message("... the file " + nameFileOut + " is been written!!!");
            //connection.prepareGraphQuery(QueryLanguage.SPARQL,sparql).evaluate(writer);
        } catch (FileNotFoundException|RepositoryException|MalformedQueryException|RDFHandlerException|QueryEvaluationException e) {
            SystemLog.exception(e);
        }
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository.
     * @param queryString string of SPARQL/SERQL query.
     * @param filePath string of the patht to the file of output.
     * @param outputFormat string of the output format.
     * @throws FileNotFoundException throw if you not find the file.
     */
     public static void writeTupleQueryResultToFile(String queryString,String filePath,String outputFormat) throws FileNotFoundException{
         try {
            SystemLog.message("Try to write with the format:" + outputFormat.toUpperCase() + " into the  file " + filePath);
            //org.openrdf.rio.RDFWriter writer = org.openrdf.rio.Rio.createWriter(
            //        org.openrdf.rio.RDFFormat.TURTLE, System.out);
            //OutputStream out = new FileOutputStream(pathOutputXmlFileName+".xml");
            //TupleQueryResultHandler writerXML = new SPARQLResultsXMLWriter(out);
            OutputStream out;
            TupleQueryResultHandler trh=null;
            if(filePath==null){
                out = System.out;
            }else{
                out = new FileOutputStream(new File("."));
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
            //CHECK the language of the uery string if SPARQL or SERQL
            QueryLanguage lang = new QueryLanguage("");
            for (QueryLanguage language : queryLanguages) {
                Query result = prepareQuery(queryString, language,  mRepositoryConnection);
                if (result != null) {
                    lang = language;
                    break;
                }
            }
            mRepositoryConnection.prepareTupleQuery(lang, queryString).evaluate(trh);
            SystemLog.message("...the result of the tuple query is been written " + filePath);
        } catch (OpenRDFException e) {
         // handle exception
        }
     }

    /**
     * Method to convert a file to another specific format
     * @param urlFile string url to the file.
     * @param inputFormat string check the input format null.
     * @param outputFormat string of the output format.
     */
     public void convertTo(String urlFile,String inputFormat,String outputFormat) {
        try {
            // open our input document
             URL documentUrl;
             RDFFormat format;
             InputStream inputStream;
            if(urlFile.startsWith("http://")){
                documentUrl = new URL(urlFile);
                //AutoDetecting the file format
                format = convertFileNameToRDFFormat(documentUrl.toString());
                //RDFFormat format2 = Rio.getParserFormatForMIMEType("contentType");
               // RDFParser rdfParser = Rio.createParser(format);
                inputStream = documentUrl.openStream();
            }else{
                urlFile = FileUtil.convertFileToStringUriWithPrefix(urlFile);
               //documentUrl = new URL("file::///"+FileUtil.convertFileToUri(urlFile));
               documentUrl = new URL(urlFile);
               format = stringToRDFFormat(inputFormat) ;               
               inputStream = documentUrl.openStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            // insert a parser for Turtle and a writer for RDF/XML
            RDFParser rdfParser =  Rio.createParser(format);
            RDFWriter rdfWriter =  Rio.createWriter(stringToRDFFormat(outputFormat),
                         new FileOutputStream(urlFile+ "." +outputFormat));
            // link our parser to our writer...
            rdfParser.setRDFHandler(rdfWriter);
            // ...and start the conversion!       
            rdfParser.parse(in, documentUrl.toString());
        } catch (IOException|RDFParseException|RDFHandlerException ex) {
            SystemLog.exception(ex);
        }
     }
     
     /**
      * Method utility: Get RDFFormat from the file name.
      * @param filePath string to the path to the file.
      * @return correspondent RDFFormat.
      */
     public RDFFormat convertFileNameToRDFFormat(String filePath){
         return Rio.getParserFormatForFileName(filePath, RDFFormat.RDFXML);
     }

    /**
     * Field a list of RDF file formats used in loadFile().
     */
    private static final org.openrdf.rio.RDFFormat allFormats[] = 
            new org.openrdf.rio.RDFFormat[] {
        org.openrdf.rio.RDFFormat.NTRIPLES, org.openrdf.rio.RDFFormat.N3,
        org.openrdf.rio.RDFFormat.RDFXML, org.openrdf.rio.RDFFormat.TURTLE, 
        org.openrdf.rio.RDFFormat.TRIG, org.openrdf.rio.RDFFormat.TRIX, 
        org.openrdf.rio.RDFFormat.NQUADS };

    /**
     * Method to convert a string to a or.openrdf.rio.RDFFormat.
     * @param strFormat string of format.
     * @return correspondent RDFORMAT.
     */
    private static org.openrdf.rio.RDFFormat stringToRDFFormat(String strFormat) {
        if(strFormat.equalsIgnoreCase("NT")||strFormat.equalsIgnoreCase("N3")
                ||strFormat.equalsIgnoreCase("NTRIPLES")||strFormat.equalsIgnoreCase("N-TRIPLES")){
            strFormat = "N-Triples";
        }
        for (org.openrdf.rio.RDFFormat format : allFormats) {
                if (format.getName().equalsIgnoreCase(strFormat))
                        return format;
        }
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
    }

    
     //Field a list of datatype handling strategies
    
   /* private static final org.openrdf.rio.RDFParser.DatatypeHandling allDatatypeHandling[] =
            new org.openrdf.rio.RDFParser.DatatypeHandling[] {
        org.openrdf.rio.RDFParser.DatatypeHandling.IGNORE,
        org.openrdf.rio.RDFParser.DatatypeHandling.NORMALIZE,
        org.openrdf.rio.RDFParser.DatatypeHandling.VERIFY
    };*/

    
     // Method to convert a string to a or.openrdf.rio.RDFFormat
     
   /* private static org.openrdf.rio.RDFParser.DatatypeHandling stringToDatatypeHandling(String strHandling) {
        for (org.openrdf.rio.RDFParser.DatatypeHandling handling : allDatatypeHandling) {
                if (handling.repositoryID().equalsIgnoreCase(strHandling))
                        return handling;
        }
        throw new IllegalArgumentException("Datatype handling strategy for parsing '" + strHandling + "' is not recognised");
    }*/

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
            SystemLog.message("Loading " + file.getName() + " ");
            //Creating the right parser for the right format
            //RDFFormat format = RDFFormat.forFileName(file.getName());
            RDFFormat format = convertFileNameToRDFFormat(file.getName());
            if (format == null) {
                System.out.println();
                SystemLog.warning("Unknown RDF format for file: " + file);
                return 0;
            }

            URI dumyBaseUrl = new URIImpl(file.toURI().toString());

            URI context = null;
            if (!format.equals(RDFFormat.NQUADS) && !format.equals(RDFFormat.TRIG) && !format.equals(RDFFormat.TRIX)) {
                //String contextParam = CONTEXT;
                //if (CONTEXT == null) {
                //    context = new URIImpl(file.toURI().toString());
                //} else {
                //    if (CONTEXT.length() > 0) {
                        context = new URIImpl(CONTEXT);
                //    }
                //}
            }
            InputStream reader = null;
            try {
                if (file.getName().endsWith("gz")) {
                    reader = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file), 256 * 1024));
                } else {
                    reader = new BufferedInputStream(new FileInputStream(file), 256 * 1024);
                }
                // create a parser home.home.initializer.org.p4535992.mvc.config with preferred settings
           /* boolean verifyData = VERIFY;
            boolean stopAtFirstError = STOP_ON_ERROR;
            boolean preserveBnodeIds = PRESERVE_BNODES;*/
                //NEW METHOD
                //RioSetting settings = new RioS
                RioSetting<Boolean> verifyDataSet = BasicParserSettings.VERIFY_DATATYPE_VALUES;
                RioSetting<Boolean> stopAtFirstErrorSet = BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES;
                RioSetting<Boolean> preserveBnodeIdsSet = BasicParserSettings.PRESERVE_BNODE_IDS;

           /* RioConfig configs = new RioConfig();
            configs.set(verifyDataSet,VERIFY);
            configs.set(stopAtFirstErrorSet,STOP_ON_ERROR);
            configs.set(preserveBnodeIdsSet,PRESERVE_BNODES);*/

                //ParserConfig config = new ParserConfig(verifyData, stopAtFirstError, preserveBnodeIds, RDFParser.DatatypeHandling.VERIFY);
                ParserConfig config = new ParserConfig();
                config.set(verifyDataSet, VERIFY);
                config.set(stopAtFirstErrorSet, STOP_ON_ERROR);
                config.set(preserveBnodeIdsSet, PRESERVE_BNODES);
            /*RDFParser.DatatypeHandling datatypeHandling =  org.openrdf.rio.RDFParser.DatatypeHandling.NORMALIZE;*/
                long chunkSize = Long.parseLong(CHUNK_SIZE);
                long start = System.currentTimeMillis();
                // set the parser configuration for our connection
            /*ParserConfig home.home.initializer.org.p4535992.mvc.config = new ParserConfig(verifyData, stopAtFirstError, preserveBnodeIds, datatypeHandling);*/

                mRepositoryConnection.setParserConfig(config);
                RDFParser parser = Rio.createParser(format);
                parser.setParserConfig(config);

                // add our own custom RDFHandler to the parser. This handler takes care of adding
                // triples to our repository and doing intermittent commits
                ChunkCommitter handler = new ChunkCommitter(mRepositoryConnection, context, chunkSize);
                parser.setRDFHandler(handler);
                mRepositoryConnection.commit();
                mRepositoryConnection.begin();

                //Mitac hack: use parallel update
//            if (PARALLEL_LOAD) {
//                URI up = new URIImpl("http://www.ontotext.com/useParallelInsertion");
//                mRepositoryConnection.add(up, up, up);
//            }
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
                SystemLog.warning("Failed to load '" + file.getName() + "' (" + format.getName() + ")." + e);
                return 0;
            } finally {
                if (reader != null)
                    reader.close();
            }
        }catch(RepositoryException|IOException e){
            SystemLog.exception(e);
        }
        return 0;
    }


    /**
     * Method for try to create a repository programmatically intead from the web interface
     * @param model Sesame model.
     * @param repositoryNode resource where put the sesame model.
     * @param repositoryID string id of the repository.
     */
    public static void createSesameRepository(Model model,Resource repositoryNode,String repositoryID) {
        try{
            // Create a manager for local repositories
            RepositoryManager repositoryManager = new LocalRepositoryManager(new File("."));
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
        }catch(RepositoryException|RepositoryConfigException e){
            SystemLog.exception(e);
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
     * Method for update your repository with a SPARQL query
     * @param queryString string of the SPARQL/SERQL query.
     */
    public static void updateRepository(String queryString){
        Update update;
        try {
            QueryLanguage lang = checkLanguageOfQuery(queryString);
            update = mRepositoryConnection.prepareUpdate(lang, queryString);
            update.execute();
        } catch (RepositoryException|MalformedQueryException|UpdateExecutionException e) {
            SystemLog.exception(e);
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
        // Retrieve all statements about Alice and put them in a list
        RepositoryResult<Statement> statements;
        List<Statement> about = new ArrayList<>();
        try {
            if(statement!=null) {
                statements = mRepositoryConnection.getStatements(
                        statement.getSubject(), statement.getPredicate(), statement.getObject(), includeInfered);
            }else {
                statements = mRepositoryConnection.getStatements(null, null, null, includeInfered);
            }
            about = Iterations.addAll(statements, new ArrayList<Statement>());
            if(remove){
                // Then, remove them from the repository
                mRepositoryConnection.remove(about);
            }
        } catch (RepositoryException e) {
            SystemLog.exception(e);
        }
        return about;
    }


    //*****************************************************************************************************
    /**
     * Connect to a managed repository located at the given location
     * and connect to the repository with the given repositoryID.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local
     * directory repositoryID.
     *
     * @param repositoryLocation string file path to the repository location.
     * @param repositoryID repsoitory name/ID .
     * @return repository manager.
     */
    public RepositoryManager connectToRepository(String repositoryLocation, String repositoryID) {
        // connect to location and get the manager
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryID);
        return mRepositoryManager;
    }
    public RepositoryManager connectToRepository(URL repositoryLocation, String repositoryID) {
        // connect to location and get the manager
        closeRepository();
        connectToLocation(repositoryLocation);
        openRepository(repositoryID);
        return mRepositoryManager;
    }

    /**
     * Connect to a managed repository location.
     * The repository connection is assumed to be remote if it starts with
     * http:// or https://, otherwise the location is assumed to be a local
     * directory repositoryID.
     *
     * @param repositoryLocation string file path to the repository location.
     * @return repository manager.
     */
    public RepositoryManager connectToLocation(String repositoryLocation) {
        // if the location starts with http:// it will be assumed that this
        // is a remote location, otherwise it will be regarded as a directory
        // repositoryID.
        SystemLog.message("Calling SesameManager.connectToLocation with String: " + repositoryLocation);
        if(repositoryLocation.startsWith("http://") ||
                repositoryLocation.startsWith("https://")) {
            connectToRemoteLocation(repositoryLocation);
        } else {
            connectToLocalLocation(repositoryLocation);
        }
        return mRepositoryManager;
    }
    
    public RepositoryManager connectToLocation(URL repositoryLocation) {
        // if the location starts with http:// it will be assumed that this
        // is a remote location, otherwise it will be regarded as a directory
        // repositoryID.
        SystemLog.message("Calling SesameManager.connectToLocation with URL: " + repositoryLocation);
        SystemLog.message("Protocol is: " + repositoryLocation.getProtocol());
        if(repositoryLocation.getProtocol().startsWith("http")) {
            connectToRemoteLocation(repositoryLocation.toString());
        } else {
            connectToLocalLocation(repositoryLocation);
        }
        return mRepositoryManager;
    }

    /**
     * Connect to a remote managed repository location.
     *
     * @param url string to the url service.
     * @return reposiotry manager.
     */
    public RepositoryManager connectToRemoteLocation(String url) {
        isManagedRepository = true;
        SesameClient sesameClient = new SesameClientImpl();
        sesameClient.createSparqlSession(url,url);
//        HTTPClient httpClient = new HTTPClient();
//        httpClient.setQueryURL(url);
//        httpClient.setUpdateURL(url);
        RemoteRepositoryManager mgr = new RemoteRepositoryManager(url);
        try {
            URL javaurl = new URL(url);
            String userpass = javaurl.getUserInfo();
            if(userpass != null) {
                String[] userpassfields = userpass.split(":");
                if(userpassfields.length != 2) {
                    throw new SesameManagerException("URL has login data but not username and password");
                } else {
                    mgr.setUsernameAndPassword(userpassfields[0], userpassfields[1]);
                }
            }
        } catch(MalformedURLException ex) {
            throw new SesameManagerException("Problem processing remote URL: "+ex);
        }
        setManager(mgr, url);
        return mRepositoryManager;
    }

    /**
     * Method to Connect to a local repository location at the given directory.
     * If mustexist is true, it is an error if the directory is not found.
     * @param filePathToDirectory string file path to the folder/file with all the repositeries.
     * @return Repository Manager.
     */
    public RepositoryManager  connectToLocalLocation(String filePathToDirectory) {
       return connectToLocalLocation(new File(filePathToDirectory));
    }

    /**
     * Method to Connect to a local repository location at the given directory.
     * @param directory string file path to the folder with all the repositeries.
     * @return Repository Manager.
     */
    public RepositoryManager connectToLocalLocation(File directory) {
        try {
            return connectToLocalLocation(FileUtil.convertFileToURL(directory));
        } catch (MalformedURLException e) {
            SystemLog.error("The URL directory not exists or is erract:" + directory.getAbsolutePath());
            return null;
        }
    }

    /**
     *  Method to Connect to a local repository location at the given directory.
     * @param dirname url location of the directory.
     * @return Repository Manager.
     */
    public RepositoryManager connectToLocalLocation(URL dirname) {
        isManagedRepository = true;
        SystemLog.message("Called connectToLocalLocation to " + dirname);
        File directory;
        try {
            directory = new File(dirname.toURI());
        } catch (URISyntaxException ex) {
            throw new SesameManagerException("Specified URL is invalid: "+dirname,ex);
        }
        if (!directory.exists()) {
            throw new SesameManagerException("Specified path does not exist: " +
                    directory.getAbsolutePath());
        }
        if (!directory.isDirectory()) {
            throw new SesameManagerException("Specified path is not a directory: " +
                    directory.getAbsolutePath());
        }
        setManager(new LocalRepositoryManager(directory), directory.toString());
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
            mRepositoryConnection = mRepository.getConnection();
        } catch (RepositoryException e) {
            SystemLog.exception(e);
        }
        return mRepository;
    }

    public Repository connectToHTTPRepository(String sesameServer,String repositoryID){
        //String sesameServer = "http://localhost:8080/openrdf-sesame/";
        if(!sesameServer.endsWith(File.separator))sesameServer = sesameServer + File.separator;
        return connectToHTTPRepository(sesameServer + repositoryID);
    }

    public Repository connectToHTTPRepository(String urlAddressRepositoryId){
        try {
            //String sesameServer = "http://localhost:8080/openrdf-sesame/";
            mRepository = new HTTPRepository(urlAddressRepositoryId);
            mRepository.initialize();
            mRepositoryConnection = mRepository.getConnection();
        } catch (RepositoryException e) {
            SystemLog.exception(e);
        }
        return mRepository;
    }

    public Repository connectToNativeRepository(String filePathDirectory,String indexes){
        return connectToNativeRepository(new File(filePathDirectory),indexes);
    }

    public Repository connectToNativeRepository(File directory,String indexes){
        try{
            //Creating a Native RDF Repository
            //does not keep data in main memory, but instead stores it directly to disk
            if(StringKit.isNullOrEmpty(indexes)){indexes = "spoc,posc,cosp";}
            mRepository = new SailRepository(new NativeStore(directory,indexes));
            mRepository.initialize();
            mRepositoryConnection = mRepository.getConnection();
        } catch (RepositoryException e) {
            SystemLog.exception(e);
        }
        return mRepository;
    }

    public Repository connectToInferencingRepository(String directory,String repositoryID){
        if(directory.startsWith(File.separator)) directory = directory.substring(1, directory.length());
        if(!directory.endsWith(File.separator)) directory = directory + File.separator;
        if(repositoryID.startsWith(File.separator))repositoryID = repositoryID.substring(1, repositoryID.length());
        if(!repositoryID.endsWith(File.separator))repositoryID = repositoryID + File.separator;
        return connectToInferencingRepository(new File(directory + repositoryID));
    }

    public Repository connectToInferencingRepository(File directory,String repositoryID){
        String sDirectory = directory.getAbsolutePath();
        if (sDirectory.startsWith(File.separator)) sDirectory = sDirectory.substring(1, sDirectory.length());
        if (!sDirectory.endsWith(File.separator)) sDirectory = sDirectory + File.separator;
        if(repositoryID.startsWith(File.separator))repositoryID = repositoryID.substring(1, repositoryID.length());
        if(!repositoryID.endsWith(File.separator))repositoryID = repositoryID + File.separator;
        return connectToInferencingRepository(new File(sDirectory + repositoryID));
    }

    /**
     * Method to connect to a INferencing Repository Sesame.
     * @param repositoryID the String id Repository.
     * @return the Repository.
     */
    public Repository connectToInferencingRepository(File repositoryID) {
        try {
            //Creating a repository with RDF Schema inferencing
            //ForwardChainingRDFSInferencer is a generic RDF Schema
            //inferencer (MemoryStore and NativeStore support it)
            mRepository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore(repositoryID)));
            mRepository.initialize();
            mRepositoryConnection = mRepository.getConnection();
        } catch (RepositoryException e) {
            SystemLog.exception(e);
        }
        return mRepository;
    }


    /**
     * Disconnect from a local or remote repository manager.
     *
     */
    public void disconnect() {
        closeRepository();
        if (mRepositoryManager != null) {
            SystemLog.message("Shutting down the repository manager");
            mRepositoryManager.shutDown();
            SystemLog.message("manager is shut down");
            mRepositoryManager = null;
            mRepositoryLocation = null;
            SystemLog.message("manager and location set to null");
        }
    }


    private void setManager(RepositoryManager manager, String location) {
        SystemLog.message("setManager called");
        try {
            disconnect();
            manager.initialize();
            mRepositoryManager = manager;
            mRepositoryLocation = location;
        } catch (RepositoryException e) {
            throw new SesameManagerException("Error initializing manager: "+e);
        }
    }

    /**
     * Open a repository with the given repositoryID at the remote or local location
     * previously connected to.
     * An error is raised if no local or remote location was set prior to
     * calling this method.
     *
     * @param repositoryID string name/id of the repository.
     * @return repository connection.
     */
    public static RepositoryConnection openRepository(String repositoryID) {
        SystemLog.message("Called openRespository with ID " + repositoryID);
        if(mRepositoryManager != null) {
            try {
                mRepository = mRepositoryManager.getRepository(repositoryID);
            } catch (RepositoryException|RepositoryConfigException e) {
                throw new SesameManagerException("Could not get repository "+ repositoryID +" error is "+e);
            }
            if(mRepository == null) {
                throw new SesameManagerException("Getting repository failed - no repository of this repositoryID found: "+ repositoryID);
            }
            try {
                mRepositoryConnection = mRepository.getConnection();
                SystemLog.message("repository connection set");
            } catch (Exception e) {
                throw new SesameManagerException("Could not get connection "+ repositoryID +" error is "+e);
            }
        } else {
            throw new SesameManagerException("Not connected to a repository location for openRepository "+ repositoryID);
        }
        return mRepositoryConnection;
    }


    // create repository from a template, no substitution of variables
    // also opens the newly created repository
    /**
     * Create a new managed repository at the current remote or local location
     * using the configuration information passed on as a string.
     *
     * @param config string file path to the config file.
     */
    public void createRepository(String config) {
        SystemLog.message("createRepository called");
        if(mRepositoryManager == null) {
            throw new SesameManagerException("No connect prior to createRepository");
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
            Model model = Rio.parse(new StringReader(config),RepositoryConfigSchema.NAMESPACE, RDFFormat.TURTLE);
            try {
                // get the unique subject
                //old deprecated code
              /*  Resource repositoryNode  = org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE,RepositoryConfigSchema.REPOSITORY);
                RepositoryConfig repConfig = RepositoryConfig.parse(graph, repositoryNode);*/
                Resource repositoryNode = model.filter(null, RDF.TYPE,RepositoryConfigSchema.REPOSITORY).subjectResource();
                RepositoryConfig repConfig = new RepositoryConfig();
                repConfig.parse(model, repositoryNode);
                repConfig.validate();
                if (RepositoryConfigUtil.hasRepositoryConfig(systemRepo, repConfig.getID())) {
                    throw new SesameManagerException("Repository already exists with ID "+repConfig.getID());
                } else {
                    RepositoryConfigUtil.updateRepositoryConfigs(systemRepo, repConfig);
                    mRepository = mRepositoryManager.getRepository(repConfig.getID());
                    // Sesame complains about the repository already being initialized
                    // for native but not for OWLIM here ... can we always not initialize
                    // here????
                    try {
                        mRepository.initialize();
                    } catch (IllegalStateException ex) {
                        System.err.println("Got an IllegalStateException, ignored: "+ex);
                        // we get this if the SAIL has already been initialized, just
                        // ignore and be happy that we can be sure that indeed it has
                    }
                    openRepository(repConfig.getID());
                }
            } catch (RepositoryException|RepositoryConfigException e) {
                throw new SesameManagerException("Error creating repository",e);
            }
        } catch (IOException|RDFParseException e) {
            throw new SesameManagerException("Error parsing the config string: ",e);
        }
    }

    /**
     * Create an unmanaged repository with files stored in the directory
     * given from the configuration passed as a string.
     * @param repositoryDirFile string file path to the directory of repsoisotry.
     * @param configstring string file path to the config file.
     */
    public void createUnmanagedRepository(File repositoryDirFile, String configstring) {
        isManagedRepository = false;
        SystemLog.message("SesameManager: creating unmanaged repo, dir is " + repositoryDirFile.getAbsolutePath());
        try {
        /*ValueFactory vf = new MemValueFactory();
        Graph graph = parseRdf(configstring, vf, RDFFormat.TURTLE);*/
            Model model = Rio.parse(new StringReader(configstring), RepositoryConfigSchema.NAMESPACE, RDFFormat.TURTLE);
        /*Resource repositoryNode = org.openrdf.model.util.GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);*/
            Resource repositoryNode = model.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY).subjectResource();
            RepositoryConfig repConfig;
            try {
                /*repConfig = RepositoryConfig.create(model,repositoryNode);*/
                repConfig = new RepositoryConfig();
                repConfig.parse(model, repositoryNode);
            } catch (RepositoryConfigException ex) {
                throw new SesameManagerException("Could not create repository from RDF graph", ex);
            }
            try {
                repConfig.validate();
            } catch (RepositoryConfigException ex) {
                throw new SesameManagerException("Could not validate repository", ex);
            }
            RepositoryImplConfig rpc = repConfig.getRepositoryImplConfig();
            Repository repo = createRepositoryStack(rpc);
            repo.setDataDir(repositoryDirFile);
            try {
                repo.initialize();
            } catch (RepositoryException ex) {
                throw new SesameManagerException("Could not initialize repository", ex);
            }
            try {
                mRepositoryConnection = repo.getConnection();
                SystemLog.message("Repo dir is " + repo.getDataDir().getAbsolutePath());
                SystemLog.message("Repo is writable " + repo.isWritable());
            } catch (RepositoryException ex) {
                throw new SesameManagerException("Could not get connection for unmanaged repository", ex);
            }
        }catch(RDFParseException ex){
            throw new SesameManagerException("Could not get subject of config RDF",ex);
        }catch(IOException ex){
            throw new SesameManagerException("Not found the directory file",ex);
        }
    }

  /*  private Graph parseRdf(String config, ValueFactory vf, RDFFormat lang) {
        Graph graph = new org.openrdf.model.impl.GraphImpl(vf);
        RDFParser rdfParser = Rio.createParser(lang, vf);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        try {
            rdfParser.parse(new StringReader(config), RepositoryConfigSchema.NAMESPACE);
        } catch (Exception e) {
            throw new SesameManagerException("Could not parse rdf: " + e);
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
                throw new SesameManagerException("Error initializing memory store: "+e);
            }
            Model model = Rio.parse(new StringReader(filePathConfig), RepositoryConfigSchema.NAMESPACE, RDFFormat.TURTLE);
            Resource repositoryNode = model.filter(null, RDF.TYPE,RepositoryConfigSchema.REPOSITORY).subjectResource();
            repConfig = new RepositoryConfig();
            repConfig.parse(model, repositoryNode);
            repConfig.validate();
        } catch (RepositoryConfigException|IOException|RDFParseException e) {
            throw new SesameManagerException("Error parsing the config string "+e);
        }
        return repConfig;
    }

    /**
     * Method to create a Repository from a Configuration.
     * @param config RepositoryImplConfig.
     * @return the created Repository.
     */
    private Repository createRepositoryStack(RepositoryImplConfig config) {
        RepositoryFactory factory = RepositoryRegistry.getInstance().get(config.getType());
        if (factory == null) {
            throw new SesameManagerException("Unsupported repository type: " + config.getType());
        }
        Repository repository;
        try {
            repository = factory.getRepository(config);
        } catch (RepositoryConfigException ex) {
            throw new SesameManagerException("Could not get repository from factory",ex);
        }
        if (config instanceof DelegatingRepositoryImplConfig) {
            RepositoryImplConfig delegateConfig = ((DelegatingRepositoryImplConfig)config).getDelegate();
            Repository delegate = createRepositoryStack(delegateConfig);
            try {
                ((DelegatingRepository)repository).setDelegate(delegate);
            }
            catch (ClassCastException e) {
                throw new SesameManagerException(
                        "Delegate specified for repository that is not a DelegatingRepository: "
                                + delegate.getClass());
            }
        }
        return repository;
    }

    /**
     * Method for Substitute variables in a configuration template string.
     *
     * @param configtemplate string file path to the config file.
     * @param variables map of all variables you wan substitute or update.
     * @return new string file apth to the confic path.
     */
    public static String substituteConfigTemplate(String configtemplate, Map<String,String> variables) {
        // replace all variables in the template then do the actual createRepository
        StringBuffer result = new StringBuffer(configtemplate.length()*2);
        Matcher matcher = TOKEN_PATTERN.matcher(configtemplate);
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
    public static String updateConfigTemplate(String configtemplate, Map<String,String> variables) {
       return substituteConfigTemplate(configtemplate,variables);
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
                throw new SesameManagerException("Could not delete repository "+name+": "+e);
            } 
        } else {
            throw new SesameManagerException("Must be connected to a location");
        }
    }

    /**
     * Clear the current repository and remove all data from it.
     */
    public void clearRepository() {
        try {
            mRepositoryConnection.clear();
        } catch (RepositoryException e) {
            throw new SesameManagerException("Could not clear repository: "+e);
        }
    }


    /**
     * Load data into the current repository from a file.
     * @param filePath string file path . 
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     */
    public void importIntoRepository(File filePath, String baseURI, String inputFormat) {
        if(mRepositoryConnection != null) {
            RDFFormat sesameFormat = stringToRDFFormat(inputFormat);
            if(sesameFormat==null) {
                throw new SesameManagerException(
                        "Could not import - format not supported: "+inputFormat);
            }
            try {
                mRepositoryConnection.add(filePath,baseURI,sesameFormat);
            } catch(RepositoryException|IOException|RDFParseException e) {
                throw new SesameManagerException("Could not import",e);
            }
        } else {
            throw new SesameManagerException("Cannot import, no connection");
        }
    }

    /**
     * Method to import a File of triple in a Repository Sesame.
     * @param fileOrDirectory the file or directories of triples to import.
     */
    public void importIntoRepository(File fileOrDirectory){
        try {
            if (!mRepository.isInitialized()) mRepository.initialize();
            if(fileOrDirectory.isDirectory()){
                List<File> files = FileUtil.readDirectory(fileOrDirectory);
                for (File file: files)  {
                    if (!mRepository.isInitialized()) mRepository.initialize();
                    try {
                        mRepositoryConnection.add(file, "file://" + file.getAbsolutePath(),
                                convertFileNameToRDFFormat(file.getAbsolutePath()));
                    } finally {
                        mRepositoryConnection.close();
                    }
                }
            }else{
                if (!mRepository.isInitialized()) mRepository.initialize();
                try {
                    mRepositoryConnection.add(fileOrDirectory, "file://" + fileOrDirectory.getAbsolutePath(),
                            convertFileNameToRDFFormat(fileOrDirectory.getAbsolutePath()));
                } finally {
                    mRepositoryConnection.close();
                }
            }
        } catch (RepositoryException|IOException|RDFParseException e) {
            SystemLog.exception(e);
        }
    }



    /**
     * Load data into the current repository from a stream.
     *
     * @param filePath inputstream file path . 
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     */
    public void importIntoRepository(InputStream filePath, String baseURI, String inputFormat) {
        if(mRepositoryConnection != null) {
            RDFFormat sesameFormat = stringToRDFFormat(inputFormat);
            try {
                mRepositoryConnection.add(filePath,baseURI,sesameFormat);
            } catch(RepositoryException|IOException|RDFParseException e) {
                throw new SesameManagerException("Could not import: "+e);
            }
        } else {
            throw new SesameManagerException("Cannot import, no connection");
        }
    }

    /**
     * Method to Load data into the current repository from a reader
     * @param filePath reader of the file path . 
     * @param baseURI string of base uri .
     * @param inputFormat  string input format of the file.
     */
    public void importIntoRepository(Reader filePath, String baseURI, String inputFormat) {
        if(mRepositoryConnection != null) {
            RDFFormat sesameFormat = stringToRDFFormat(inputFormat);
            try {
                mRepositoryConnection.add(filePath,baseURI,sesameFormat);
            } catch(RepositoryException|IOException|RDFParseException e) {
                throw new SesameManagerException("Could not import: "+e);
            }
        } else {
            throw new SesameManagerException("Cannot import, no connection");
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
     * Method to ASKat the repository where you are connected with a query SPARQL or SERQL.
     * @param query query a string query SPARQL or SERQL.
     * @return the result of the  ASK query.
     */
    public BooleanQuery createAskQuery(String query) {
        if(mRepositoryConnection != null) {
            try {
                QueryLanguage lang = checkLanguageOfQuery(query);
                return mRepositoryConnection.prepareBooleanQuery(lang, query);
            } catch (RepositoryException|MalformedQueryException ex) {
                throw new SesameManagerException("Could not prepare BooleanQuery",ex);
            }
        } else {
            throw new SesameManagerException("Could not create an ask query, no connection");
        }
    }

    /**
     * Method to update the repository where you are connected with a query SPARQL or SERQL.
     * @param query a string query SPARQL or SERQL.
     * @return the result of update and the reposiotry updated.
     */
    public Update createUpdate(String query) {
        if(mRepositoryConnection != null) {
            try {
                QueryLanguage lang = checkLanguageOfQuery(query);
                return mRepositoryConnection.prepareUpdate(lang, query);
            } catch (RepositoryException|MalformedQueryException ex) {
                throw new SesameManagerException("Could not prepare an Update operation",ex);
            }
        } else {
            throw new SesameManagerException("Cannot create an update operation, no connection");
        }
    }

    /**
     * Method to get a Collection of all repositoruies on the directory setted on the manager
     * @return the Set Collection of namses of all repository in the current manager.
     */
    public Set<String> getRepositories() {
        if(mRepositoryManager == null) {
            SystemLog.warning("You must set the Reposiotry Manager for avoif the empty list!!!");
            return new HashSet<>();
        }
        try {
            return mRepositoryManager.getRepositoryIDs();
        } catch (RepositoryException ex) {
            throw new SesameManagerException("Could not get repository IDs: ",ex);
        }
    }

    /**
     * Method for see on the console the resul of a tuplequery
     * @param query string SPARQL/SERQL query.
     */
    public  void showStatements(TupleQuery query) {
        try{
            TupleQueryResult currentState = query.evaluate();
            while (currentState.hasNext()) {
                BindingSet set = currentState.next();
                for (Binding binding : set) {
                    System.out.printf("%s = %s \n", binding.getName(), binding.getValue());
                }
                System.out.println();
            }
            System.out.println("============================================================");
        }catch(QueryEvaluationException e){
            SystemLog.exception(e);
        }
    }

    /**
     * Method utility: Check if the string query is written in SPARQL or SERQL format.
     * @param queryString string of the query.
     * @return language of the query.
     */
    public static QueryLanguage checkLanguageOfQuery(String queryString){
         //CHECK the language of the uery string if SPARQL or SERQL
        QueryLanguage lang = new QueryLanguage("");
        for (QueryLanguage language : queryLanguages) {
            Query result = prepareQuery(queryString, language,  mRepositoryConnection);
            if (result != null) {
                lang = language;
                break;
            }
        }
        return lang;
    }

    public boolean isRepositoryInitialized(){
        return mRepository.isInitialized();
    }

    public boolean isRepositoryConnected(){
        try {
            return mRepositoryConnection.isOpen();
        }catch(RepositoryException e){
            return  false;
        }
    }

    public boolean isRepositoryActive(){
        try {
            return mRepositoryConnection.isActive();
        }catch(RepositoryException e){
            return  false;
        }
    }

    public boolean isRepositoryEmpty(){
        try {
            return mRepositoryConnection.isEmpty();
        }catch(RepositoryException e){
            return  false;
        }
    }

}
