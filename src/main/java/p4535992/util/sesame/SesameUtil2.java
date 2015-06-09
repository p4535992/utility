package p4535992.util.sesame;
import com.ontotext.trree.OwlimSchemaRepository;
import com.ontotext.jena.SesameDataset;
import org.openrdf.OpenRDFException;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.sail.memory.MemoryStore;
import p4535992.util.file.FileUtil;
import p4535992.util.log.SystemLog;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;

/**
 * Class of utility for Sesame Server and Owlim Server
 * @author 4535992
 */
public class SesameUtil2 {
    
    public SesameUtil2(){}

    //input parameter
    private String PATH_FOLDER_STORAGE,PATH_FOLDER_REPOSITORY,
            RULESET,SESAMESERVER,TYPE_REPOSITORY,ID_REPOSITORY,USER_REPOSITORY,PASSWORD_REPOSITORY;
    
    //loading parameter
    private String CONTEXT = "context";
    private boolean PARALLEL_LOAD,VERIFY,STOP_ON_ERROR,PRESERVE_BNODES,SHOWSTATS,UPDATES,PRINT_RESULT_QUERY;
    private String CHUNK_SIZE ="500000";


    //output parameter
    private String OUTPUTFILE,OUTPUTFORMAT,URL_REPOSITORY,URL_SESAME,URL_REPOSITORY_ID;

    //other parameter
    private String CONFIGFILENAME;

    public void setOutput(String outputfile,String outputformat,boolean printResultQuery){
        this.OUTPUTFILE = outputfile;
        this.OUTPUTFORMAT = outputformat;
        this.PRINT_RESULT_QUERY = printResultQuery;
    }

    public void setParameterLocalRepository(
            String TYPE_REPOSITORY,String PATH_FOLDER_STORAGE,String PATH_FOLDER_REPOSITORY,
            String RULESET,String USER_REPOSITORY,String PASSWORD_REPOSITORY
    ){
        this.TYPE_REPOSITORY=TYPE_REPOSITORY;
        this.PATH_FOLDER_STORAGE=PATH_FOLDER_STORAGE;
        this.PATH_FOLDER_REPOSITORY=PATH_FOLDER_REPOSITORY;
        this.RULESET=RULESET;
        this.USER_REPOSITORY= USER_REPOSITORY;
        this.PASSWORD_REPOSITORY= PASSWORD_REPOSITORY;
        this.ID_REPOSITORY = new File(PATH_FOLDER_REPOSITORY).getName();
        setURLRepository();
    }
    
    public void setParameterLocalRepository(
            String type_repository,String PATH_FOLDER_STORAGE,String PATH_FOLDER_REPOSITORY,String RULESET
            ){
        this.TYPE_REPOSITORY=TYPE_REPOSITORY;
        this.PATH_FOLDER_STORAGE=PATH_FOLDER_STORAGE;
        this.PATH_FOLDER_REPOSITORY=PATH_FOLDER_REPOSITORY;
        this.RULESET=RULESET;
        this.USER_REPOSITORY= null;
        this.PASSWORD_REPOSITORY=null;
        this.ID_REPOSITORY = new File(PATH_FOLDER_REPOSITORY).getName();
        setURLRepository();
    }
            
    public void setParameterRemoteRepository(
            String TYPE_REPOSITORY,String SESAMESERVER,String ID_REPOSITORY){
        this.TYPE_REPOSITORY=TYPE_REPOSITORY;
        this.SESAMESERVER=SESAMESERVER;
        this.ID_REPOSITORY=ID_REPOSITORY;
        this.USER_REPOSITORY= null;
        this.PASSWORD_REPOSITORY=null;
        setURLRepository();
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

    // A map of namespace-to-prefix
    private static Map<String, String> namespacePrefixes = new HashMap<>();
    // The repository manager
    private static org.openrdf.repository.manager.RepositoryManager repositoryManager;
    // From repositoryManager.getRepository(...) - the actual repository we will work with
    private static Repository repository;
    // From repositoryManager.getRepository(...) - the actual repository we will work with
    private static org.openrdf.repository.RepositoryConnection repositoryConnection;

    private void setURLRepository(){
        this.URL_SESAME = "http://localhost:8080/openrdf-sesame/" + ID_REPOSITORY;
        this.URL_REPOSITORY = "http://www.openrdf.org/repository#"+ ID_REPOSITORY;
        this.URL_REPOSITORY_ID = "http://www.openrdf.org/repository#"+ ID_REPOSITORY;
    }
    private void setURLRepository(String URL_SESAME,String URL_REPOSITORY_ID){
        this.URL_SESAME = URL_SESAME;
        this.URL_REPOSITORY = URL_REPOSITORY_ID;
        this.URL_REPOSITORY_ID = URL_REPOSITORY_ID;
    }

    private static void openConnection(org.openrdf.repository.Repository repository)
            throws org.openrdf.repository.RepositoryException{
        repositoryConnection = repository.getConnection();
    }

    private static void closeConnection(org.openrdf.repository.Repository repository)
            throws org.openrdf.repository.RepositoryException{
        repositoryConnection.close();
    }

    /**
     * Shutdown the repository and flush unwritten data.
     */
    public static void shutdown() {
        SystemLog.message("===== Shutting down ==========");
        if (repository != null) {
            try {
                System.out.println("NumberOfStatements=" + repositoryConnection.size());
                repositoryConnection.close();
                repository.shutDown();
                repositoryManager.shutDown();
            } catch (Exception e) {
                SystemLog.message("An exception occurred during shutdown: " + e.getMessage());
            }
        }
    }

    public void connectWithConfigFile(){
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
                    + "' parameter to specify a Sesame server, you must also use the '"
                    + ID_REPOSITORY + "' parameter to specify a repository on that server.");
            System.exit(-5);
        }
        try {
            // Create a manager for the remote Sesame server and initialise it
            org.openrdf.repository.manager.RemoteRepositoryManager remote =
                    new org.openrdf.repository.manager.RemoteRepositoryManager(URL_REPOSITORY);
            if ( USER_REPOSITORY != null || PASSWORD_REPOSITORY != null) {
                if (USER_REPOSITORY == null)
                    USER_REPOSITORY = "";
                if (PASSWORD_REPOSITORY == null)
                    PASSWORD_REPOSITORY = "";
                remote.setUsernameAndPassword(USER_REPOSITORY,PASSWORD_REPOSITORY);
            }
            repositoryManager = remote;
            repositoryManager.initialize();
        } catch (RepositoryException e) {
            SystemLog.warning("Unable to establish a connection with the Sesame server '" + URL_REPOSITORY + "': "
                    + e.getMessage());
            System.exit(-5);
        }

        // Get the repository to use
        try {
            repository = repositoryManager.getRepository(ID_REPOSITORY);

            if (repository == null) {
                SystemLog.warning("Unknown repository '" + ID_REPOSITORY + "'");
                String message = "Please make sure that the value of the 'repository' "
                        + "parameter (current value '" + ID_REPOSITORY + "') ";
                if (URL_REPOSITORY == null) {
                    message += "corresponds to the repository ID given in the configuration file identified by the '"
                            + "CONFIGFILENAME' parameter (current value '"+CONFIGFILENAME+"')";
                } else {
                    message += "identifies an existing repository on the Sesame server located at " + URL_REPOSITORY;
                }
                SystemLog.warning(message);
                System.exit(-6);
            }

            // Open a connection to this repository
            repositoryConnection = repository.getConnection();
            //repositoryConnection.setAutoCommit(false);//deprecated
        } catch (OpenRDFException e) {
            SystemLog.warning("Unable to establish a connection to the repository '" + ID_REPOSITORY + "': "
                    + e.getMessage());
            System.exit(-7);
        }
    }

    /**
     * Parse the given RDF file and return the contents as a Graph
     * @param configurationFile,The file containing the RDF data
     * @param format, RDFFormat of configurationFile
     * @param defaultNamespace, base URI of the configurationFile
     * @return The contents of the file as an RDF graph
     * @throws RDFHandlerException
     * @throws RDFParseException
     * @throws IOException
     */
    public static Graph parseFile(File configurationFile, RDFFormat format, String defaultNamespace)
            throws RDFParseException, RDFHandlerException, IOException {
        Reader reader = new FileReader(configurationFile);

        //final Graph graph = new GraphImpl();//GrapgImpl is deprecated
        final Graph graph = null;
        RDFParser parser = Rio.createParser(format);
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
                graph.add(statement);
            }

            @Override
            public void startRDF() throws RDFHandlerException {
            }
        };
        parser.setRDFHandler(handler);
        parser.parse(reader, defaultNamespace);
        return graph;
    }

    /**
     * Parses and loads all files specified in PARAM_PRELOAD
     * @param preloadFolder e.home.  "./preload"
     * @throws Exception
     */
    public void loadFiles(String preloadFolder) throws Exception {
        SystemLog.message("===== Load Files (from the '" + preloadFolder + "' parameter) ==========");
        final AtomicLong statementsLoaded = new AtomicLong();
        // Load all the files from the pre-load folder
        String preload = preloadFolder;
        if (preload == null)
            SystemLog.message("No pre-load directory/filename provided.");
        else {
            FileUtil.FileWalker.Handler handler = new FileUtil.FileWalker.Handler() {

                @Override
                public void file(File file) throws Exception {
                    statementsLoaded.addAndGet( loadFileChunked(file) );
                }

                @Override
                public void directory(File directory) throws Exception {
                    SystemLog.message("Loading files from: " + directory.getAbsolutePath());
                }
            };
            FileUtil.FileWalker walker = new FileUtil.FileWalker();
            walker.setHandler(handler);
            walker.walk(new File(preload));
        }
        SystemLog.warning("TOTAL: " + statementsLoaded.get() + " statements loaded");
    }

    /**
     * Show some initialisation statistics
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
     * @return The number of explicit statements
     */
    public long numberOfExplicitStatements() throws Exception {
        // This call should return the number of explicit statements.
        long explicitStatements = repositoryConnection.size();
        // Another approach is to get an iterator to the explicit statements(by setting the includeInferred parameter
        // to false) and then counting them.
        RepositoryResult<Statement> statements = repositoryConnection.getStatements(null, null, null, false);
        explicitStatements = 0;
        while (statements.hasNext()) {
            statements.next();
            explicitStatements++;
        }
        statements.close();
        return explicitStatements;
    }

    /**
     * A method to count only the inferred statements in the repository. No method for this is available
     * through the Sesame API, so OWLIM uses a special context that is interpreted as instruction to retrieve
     * only the implicit statements, i.e. not explicitly asserted in the repository.
     *
     * @return The number of implicit statements.
     */
    private long numberOfImplicitStatements() throws Exception {
        // Retrieve all inferred statements
        RepositoryResult<Statement> statements = repositoryConnection.getStatements(null, null, null, true,
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
     * Iterates and collects the list of the namespaces, used in URIs in the repository
     */
    public void iterateNamespaces() throws RepositoryException{
        SystemLog.message("===== Namespace List ==================================");
        SystemLog.message("Namespaces collected in the repository:");
        RepositoryResult<Namespace> iter = repositoryConnection.getNamespaces();
        while (iter.hasNext()) {
            Namespace namespace = iter.next();
            String prefix = namespace.getPrefix();
            String name = namespace.getName();
            namespacePrefixes.put(name, prefix);
            System.out.println(prefix + ":\t" + name);
        }
        iter.close();
    }

    /**
     * Demonstrates query evaluation. First parse the query file. Each of the queries is executed against the
     * prepared repository. If the printResults is set to true the actual values of the bindings are output to
     * the console. We also count the time for evaluation and the number of results per query and output this
     * information.
     */
    public void evaluateQueries(File queryFile){
        SystemLog.message("===== Query Evaluation ======================");
        if (queryFile == null) {
            SystemLog.warning("No query file given in parameter '" + queryFile + "'.");
            return;
        }

        long startQueries = System.currentTimeMillis();

        // process the query file to get the queries
        String[] queries =collectQueries(queryFile.getAbsolutePath());

        if(queries == null){
            //se non � un file ma una stringa fornita queries = new String[]{queryFile};
            SystemLog.message("Executing query '" + queryFile + "'");
            executeSingleQuery(queryFile.getAbsolutePath());
        }else{
            // evaluate each query and print the bindings if appropriate
            for (int i = 0; i < queries.length; i++) {
                final String name = queries[i].substring(0, queries[i].indexOf(":"));
                final String query = queries[i].substring(name.length() + 2).trim();
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
     *            Query string to be parsed
     * @param language
     *            The query language to assume
     * @return A parsed query object or null if not possible
     * @throws RepositoryException
     *             If the local repository used to test the query type failed for some reason
     */
    private static Query prepareQuery(String query, QueryLanguage language, RepositoryConnection tempLocalConnection) throws RepositoryException {
        try {
            tempLocalConnection.prepareTupleQuery(language, query);
            return repositoryConnection.prepareTupleQuery(language, query);
        } catch (Exception e) {
            SystemLog.warning(e.getMessage());
        }

        try {
            tempLocalConnection.prepareBooleanQuery(language, query);
            return repositoryConnection.prepareBooleanQuery(language, query);
        } catch (Exception e) {
            SystemLog.warning(e.getMessage());
        }

        try {
            tempLocalConnection.prepareGraphQuery(language, query);
            return repositoryConnection.prepareGraphQuery(language, query);
        } catch (Exception e) {
            SystemLog.warning(e.getMessage());
        }

        return null;
    }

    private static Operation prepareOperation(String query) throws Exception {
        Repository tempLocalRepository = new SailRepository(new MemoryStore());
        tempLocalRepository.initialize();
        RepositoryConnection tempLocalConnection = tempLocalRepository.getConnection();
        try {
            tempLocalConnection.prepareUpdate(QueryLanguage.SPARQL, query);
            return repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, query);
        }
        catch(Exception e ) {
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
                repositoryConnection.commit();
                return;
            }

            if (preparedOperation instanceof BooleanQuery) {
                SystemLog.message("Result: " + ((BooleanQuery) preparedOperation).evaluate());
                return;
            }

            if (preparedOperation instanceof GraphQuery) {
                GraphQuery q = (GraphQuery) preparedOperation;
                if(PRINT_RESULT_QUERY == true){
                    printGraphQueryResult(query, OUTPUTFILE, OUTPUTFORMAT);
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
                if (SHOWSTATS)
                    System.out.println();

                result.close();

                long queryEnd = System.nanoTime();
                SystemLog.message(rows + " result(s) in " + (queryEnd - queryBegin) / 1000000 + "ms.");
            }

            if (preparedOperation instanceof TupleQuery) {
                TupleQuery q = (TupleQuery) preparedOperation;
                if(PRINT_RESULT_QUERY){
                    printTupleQueryResult(query, OUTPUTFILE, OUTPUTFORMAT);
                }

                long queryBegin = System.nanoTime();

                TupleQueryResult result = q.evaluate();

                int rows = 0;
                while (result.hasNext()) {
                    BindingSet tuple = result.next();
                    if (rows == 0) {
                        for (Iterator<Binding> iter = tuple.iterator(); iter.hasNext();) {
                            System.out.print(iter.next().getName());
                            System.out.print("\t");
                        }
                        System.out.println();
                        System.out.println("---------------------------------------------");
                    }
                    rows++;
                    if (SHOWSTATS) {
                        for (Iterator<Binding> iter = tuple.iterator(); iter.hasNext();) {
                            try {
                                System.out.print(beautifyRDFValue(iter.next().getValue()) + "\t");
                            } catch (Exception e) {
                                e.printStackTrace();
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
        } catch (Throwable e) {
            SystemLog.message("An org.p4535992.mvc.error occurred during query execution: " + e.getMessage());
        }
    }

    /**
     * Creates a statement and adds it to the repository. Then deletes this statement and checks to make sure
     * it is gone.
     */
    public void insertAndDeleteStatement(String subjURI,URI pred,String objURI) throws Exception {
        if (UPDATES) {
            SystemLog.message("===== Upload and Delete Statements ====================");

            // Add a statement directly to the SAIL
            SystemLog.message("----- Upload and check --------------------------------");
            // first, insert the RDF nodes for the statement
            URI subj = repository.getValueFactory().createURI(subjURI);
            //URI pred = RDF.TYPE;
            URI obj = repository.getValueFactory().createURI(objURI);

            repositoryConnection.add(subj, pred, obj);
            repositoryConnection.commit();

            // Now check whether the new statement can be retrieved
            RepositoryResult<Statement> iter = repositoryConnection.getStatements(subj, null, obj, true);
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
            repositoryConnection.remove(subj, pred, obj);
            repositoryConnection.commit();

            // Check whether there is some statement matching the subject of the
            // deleted one
            iter = repositoryConnection.getStatements(subj, null, null, true);
            retrieved = false;
            while (iter.hasNext()) {
                retrieved = true;
                System.out.println(beautifyStatement(iter.next()));
            }
            // CLOSE the iterator to avoid memory leaks
            iter.close();
            if (retrieved)SystemLog.message("**** Statement was not deleted properly in last step. ****");
        }
    }

    /**
     * Export the contents of the repository (explicit, implicit or all statements) to the given filename in
     * the given RDF format,
     * @param exportType e.home //explicit,implicit,all,specific
     */
    public void export(String outputPathFile,String outputFormat,String exportType) throws RepositoryException, UnsupportedRDFormatException, IOException,
            RDFHandlerException {
        //String filename = parameters.get(PARAM_EXPORT_FILE);
        String filename = outputPathFile;
        if (filename != null) {
            SystemLog.message("===== Export ====================");
            RDFFormat exportFormat = stringToRDFFormat(outputFormat);
            String type = exportType;
            SystemLog.message("Exporting " + type + " statements to " + filename + " (" + exportFormat.getName() + ")");
            Writer writer = new BufferedWriter(new FileWriter(filename), 256 * 1024);
            RDFWriter rdfWriter = Rio.createWriter(exportFormat, writer);
            // This approach to making a backup of a repository by using RepositoryConnection.exportStatements()
            // will work even for very large remote repositories, because the results are streamed to the client
            // and passed directly to the RDFHandler.
            // However, it is not possible to give any indication of progress using this method.
            try {
                if (type == null || type.equalsIgnoreCase("explicit"))
                    repositoryConnection.exportStatements(null, null, null, false, rdfWriter);
                else if (type.equalsIgnoreCase("all"))
                    repositoryConnection.exportStatements(null, null, null, true, rdfWriter);
                else if (type.equalsIgnoreCase("implicit"))
                    repositoryConnection.exportStatements(null, null, null, true, rdfWriter,
                            new URIImpl( "http://www.ontotext.com/implicit"));
                else {
                    SystemLog.warning("Unknown export type '" + type + "' - valid values are: explicit, implicit, all");
                    return;
                }
            }
            finally {
                writer.close();
            }
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
     * @param queryFile
     * @return an array of strings containing the queries. Each string starts with the query id followed by
     *         ':', then the actual query string
     */
    private static String[] collectQueries(String queryFile){
        try {
            List<String> queries = new ArrayList<>();
            BufferedReader input = new BufferedReader(new FileReader(queryFile));
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
                    StringBuffer buff = new StringBuffer(line.substring(2, line.length() - 1));
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
            }

            String[] result = new String[queries.size()];
            for (int i = 0; i < queries.size(); i++) {
                result[i] = queries.get(i);
            }
            input.close();
            return result;
        } catch (Exception e) {
            SystemLog.exception(e);
            return null;
        }
    }


    /**
     * Auxiliary method, nicely format an RDF statement.
     *
     * @param statement
     *            The statement to be formatted.
     * @return The beautified statement.
     */
    public String beautifyStatement(Statement statement) throws Exception {
        return beautifyRDFValue(statement.getSubject()) + " " + beautifyRDFValue(statement.getPredicate())
                + " " + beautifyRDFValue(statement.getObject());
    }

    /**
     * Auxiliary method, printing an RDF value in a "fancy" manner. In case of URI, qnames are printed for
     * better readability
     *
     * @param value
     *            The value to beautify
     */
    public static String beautifyRDFValue(Value value) throws Exception {
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
            String svalue = value.toString().trim().replaceAll("\n", "");
            return svalue;
        }
    }

    public void printSesameModelToFile( org.openrdf.model.Model myGraph,String outpuPathtFile,String outputFormat) throws FileNotFoundException{
        // a collection of several RDF statements
        FileOutputStream out = new FileOutputStream(outpuPathtFile);
        RDFWriter writer = Rio.createWriter(stringToRDFFormat(outputFormat), out);
        try { writer.startRDF();
            for (Statement st: myGraph) {
                writer.handleStatement(st);
            }
            writer.endRDF();
        } catch (RDFHandlerException e) {}
    }

    private static Query prepareQuery(String query, QueryLanguage language) throws RepositoryException {
        Repository tempRepository = new SailRepository(new MemoryStore());
        tempRepository.initialize();

        RepositoryConnection tempConnection = tempRepository.getConnection();

        try {
            try {
                tempConnection.prepareTupleQuery(language, query);
                return repositoryConnection.prepareTupleQuery(language, query);
            } catch (Exception e) {
            }

            try {
                tempConnection.prepareBooleanQuery(language, query);
                return repositoryConnection.prepareBooleanQuery(language, query);
            } catch (Exception e) {
            }

            try {
                tempConnection.prepareGraphQuery(language, query);
                return repositoryConnection.prepareGraphQuery(language, query);
            } catch (Exception e) {
            }
            return null;
        } finally {
            try {
                tempConnection.close();
                tempRepository.shutDown();
            } catch (Exception e) {
            }
        }
    }
    /**
     * Method to write more pretty your model
     * @see //JenaKit.getPrettyWriter().write( model, new FileWriter(filename), uri);
     * @return
     */
    public static org.openrdf.rio.RDFWriter getPrettyWriter() {
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
    }
    private static Query prepareQuery(String query) throws Exception {

        for (QueryLanguage language : queryLanguages) {
            Query result = prepareQuery(query, language);
            if (result != null)
                return result;
        }
        // Can't prepare this query in any language
        return null;
    }


    /**
     * Method for open many differnet type of Sesame/Owlim Server
     * @return
     * @throws org.openrdf.repository.RepositoryException
     */
    public org.openrdf.repository.Repository connect()
            throws org.openrdf.repository.RepositoryException{
        repository = null;
        SystemLog.message("Try to open a connection to a repository Sesame of TYPE:"+TYPE_REPOSITORY+" and ID:"+ID_REPOSITORY+"...");
        File Datadir = new File(PATH_FOLDER_REPOSITORY) ;
        if(TYPE_REPOSITORY.toLowerCase().contains("owlim")){              
             OwlimSchemaRepository schema = new OwlimSchemaRepository();
             schema.setDataDir(Datadir);
             schema.setParameter("storage-folder", PATH_FOLDER_STORAGE);
             schema.setParameter("repository-type", TYPE_REPOSITORY);
             schema.setParameter("ruleset", RULESET);
             // wrap it into a Sesame SailRepository
             //SailRepository repository = new SailRepository(schema);
             repository  = new org.openrdf.repository.sail.SailRepository(schema);
        }
        else if(TYPE_REPOSITORY.toLowerCase().contains("memory")){
            //Create and initialize a non-inferencing main-memory repository
            //the MemoryStore will write its contents to the directory so that 
            //it can restore it when it is re-initialized in a future session
             repository =  new org.openrdf.repository.sail.SailRepository(
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
            repository = new org.openrdf.repository.sail.SailRepository(
                        new org.openrdf.sail.nativerdf.NativeStore(Datadir,indexes)
                );
        }
        else if(TYPE_REPOSITORY.toLowerCase().contains("inferecing")){
            //Creating a repository with RDF Schema inferencing
            //ForwardChainingRDFSInferencer is a generic RDF Schema 
            //inferencer (MemoryStore and NativeStore support it)
            repository = new org.openrdf.repository.sail.SailRepository(
                        new org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer( 
                                new org.openrdf.sail.memory.MemoryStore() )
                );
        }
        else if(TYPE_REPOSITORY.toLowerCase().contains("server")){
            //Accessing a server-side repository
            repository= new org.openrdf.repository.http.HTTPRepository(SESAMESERVER, ID_REPOSITORY);

        }
        // wrap it into a Sesame SailRepository
        // initialize
        if(repository != null && !repository.isInitialized()){
            repository.initialize();             
            return repository;   
        }else{
            return null;
        }
    }

    /**
     * Method to convert a Sesame Dataset to a JenaModel
     * @param repository
     * @return
     * @throws org.openrdf.repository.RepositoryException
     */
    public static com.hp.hpl.jena.rdf.model.Model convertSesameDataSetToJenaModel(
                org.openrdf.repository.Repository repository) throws org.openrdf.repository.RepositoryException{
        openConnection(repository);
        // finally, insert the DatasetGraph instance
        SesameDataset dataset = new SesameDataset(repositoryConnection);
        //From now on the SesameDataset object can be used through the Jena API 
        //as regular Dataset, e.home. to add some data into it one could something like the
        //following:
        com.hp.hpl.jena.rdf.model.Model model = 
                com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        return model;
    }

    /**
     * Method to support the evalutation o the Tuple query
     * @param repo
     * @param queryString
     */
    public static void TupleQueryEvalutation(
            org.openrdf.repository.Repository repo,String queryString,boolean write){
        try { 
            //org.openrdf.repository.RepositoryConnection con = repo.getConnection();
            //repositoryConnection = repo.getConnection();
            if(write)
            try {
                //String queryString = " SELECT ?x ?y WHERE { ?x ?p ?y } ";
                org.openrdf.query.TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(
                        org.openrdf.query.QueryLanguage.SPARQL, queryString);
                org.openrdf.query.TupleQueryResult result = tupleQuery.evaluate();
                try {
                    List<String> bindingNames = result.getBindingNames();
                    while (result.hasNext()) {
                        org.openrdf.query.BindingSet bindingSet = result.next();
                        org.openrdf.model.Value firstValue = bindingSet.getValue(bindingNames.get(0)); //get X
                        org.openrdf.model.Value secondValue = bindingSet.getValue(bindingNames.get(1));//get Y
                        // do something interesting with the values here...
                    }
                } finally { 
                    result.close(); 
                }
            } finally { 
                //repositoryConnection.close();
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
    }

    /**
     * Method to support the evalutation o the Graph query
     * @param repo
     * @param queryString
     */
     public static void GraphQueryEvalutation(
             org.openrdf.repository.Repository repo,String queryString){     
          try { 
            //org.openrdf.repository.RepositoryConnection con = repo.getConnection();
            //repositoryConnection = repo.getConnection();
            try {
                 //String queryString = "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }";
                org.openrdf.query.GraphQueryResult result = repositoryConnection.prepareGraphQuery(
                    org.openrdf.query.QueryLanguage.SPARQL,queryString ).evaluate();               
                try {                 
                    while (result.hasNext()) {
                        org.openrdf.model.Statement stmt = result.next();
                        // ... do something with the resulting statement here.
                    }
                } finally { 
                    result.close(); 
                }
            } finally { 
                //repositoryConnection.close();
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
      }

    /**
     * Method to conver the result of a GraphQuery to a Model Sesame
     * @param repo
     * @param queryString
     * @return
     */
     public static org.openrdf.model.Model convertGraphQueryEvalutationToSesameModel(
               org.openrdf.repository.Repository repo,String queryString){             
           org.openrdf.model.Model resultModel = null;
           try {             
             //org.openrdf.repository.RepositoryConnection 
             //repositoryConnection = repo.getConnection();
            try {
                 //String queryString = "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }";
                org.openrdf.query.GraphQueryResult result = repositoryConnection.prepareGraphQuery(
                    org.openrdf.query.QueryLanguage.SPARQL,queryString ).evaluate();               
                try {                 
                    resultModel = org.openrdf.query.QueryResults.asModel(result);
                } finally { 
                    result.close(); 
                }
            } finally { 
                //repositoryConnection.close();
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
        return resultModel;
     }

    /**
     * Method to print the query result of Graph Query on a Sesame Repository
     * @param queryString
     * @param filePath
     * @param outputFormat
     * @throws FileNotFoundException
     */
    public static void printGraphQueryResult(String queryString,String filePath,String outputFormat) throws FileNotFoundException{
        try {
            String nameFileOut = filePath+"."+outputFormat.toLowerCase();
            OutputStream fileOut = new FileOutputStream( filePath+"."+outputFormat.toLowerCase());
            SystemLog.message("Try to write the query graph result in the format:" + stringToRDFFormat(outputFormat).toString() +
                    " int o the file " + nameFileOut + "...");
            RDFWriter writer = Rio.createWriter(stringToRDFFormat(outputFormat), fileOut);
            //CHECK the language of the uery string if SPARQL or SERQL
            QueryLanguage lang = new QueryLanguage("");
            for (QueryLanguage language : queryLanguages) {
                Query result = prepareQuery(queryString, language,  repositoryConnection);
                if (result != null) {
                    lang = language;
                    break;
                }
            }
            repositoryConnection.prepareGraphQuery(lang, queryString).evaluate(writer);
            //query.evaluate(writer);
            SystemLog.message("... the file " + nameFileOut + " is been written!!!");
            //connection.prepareGraphQuery(QueryLanguage.SPARQL,sparql).evaluate(writer);
        } catch (  Exception e) {
            SystemLog.exception(e);
        }
    }

    /**
     * Method to print the query result of Tuple Query on a Sesame Repository
     * @param queryString
     * @param filePath
     * @param outputFormat
     * @throws FileNotFoundException
     */
     public static void printTupleQueryResult(String queryString,String filePath,String outputFormat) throws FileNotFoundException{
         try {
             SystemLog.message("Try to write with the format:" + outputFormat.toUpperCase() + " into the  file " + filePath);
            try {
                //org.openrdf.rio.RDFWriter writer = org.openrdf.rio.Rio.createWriter( 
                //        org.openrdf.rio.RDFFormat.TURTLE, System.out);
                //OutputStream out = new FileOutputStream(pathOutputXmlFileName+".xml");
                //TupleQueryResultHandler writerXML = new SPARQLResultsXMLWriter(out);
                OutputStream out;
                org.openrdf.query.TupleQueryResultHandler trh=null;
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
                    Query result = prepareQuery(queryString, language,  repositoryConnection);
                    if (result != null) {
                        lang = language;
                        break;
                    }
                }
                repositoryConnection.prepareTupleQuery(lang, queryString).evaluate(trh);
                SystemLog.message("...the result of the tuple query is been written " + filePath);
            } finally {                
                //repositoryConnection.close();
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
     }

    /**
     * Method to convert a file to another specific format
     * @param filePath
     * @param inputFormat
     * @param outputFormat
     * @param urlFile
     */
     public static void connvertTo(String filePath,String inputFormat,String outputFormat,String urlFile) {
        try {
            // open our input document
             URL documentUrl = null;
             org.openrdf.rio.RDFFormat format;
             InputStream inputStream = null;
            if(urlFile.contains("http://")){
                documentUrl = new URL(urlFile);
                //AutoDetecting the file format
                format = org.openrdf.rio.Rio.getParserFormatForFileName(documentUrl.toString());
                //RDFFormat format2 = Rio.getParserFormatForMIMEType("contentType");
               // RDFParser rdfParser = Rio.createParser(format);
                inputStream = documentUrl.openStream();
            }else{
               documentUrl = new URL("file::///"+filePath); 
               format = stringToRDFFormat(inputFormat) ;               
               inputStream = documentUrl.openStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            // insert a parser for Turtle and a writer for RDF/XML
            org.openrdf.rio.RDFParser rdfParser =  org.openrdf.rio.Rio.createParser(format);
            org.openrdf.rio.RDFWriter rdfWriter =  org.openrdf.rio.Rio.createWriter(stringToRDFFormat(outputFormat),
                         new FileOutputStream(filePath+ "." +outputFormat));
            // link our parser to our writer...
            rdfParser.setRDFHandler(rdfWriter);
            // ...and start the conversion!       
            rdfParser.parse(in, documentUrl.toString());
        } catch (IOException ex) {
            SystemLog.exception(ex);
        } catch (org.openrdf.rio.RDFParseException ex) {
            SystemLog.exception(ex);
        } catch (org.openrdf.rio.RDFHandlerException ex) {
            SystemLog.exception(ex);
        }
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
     * Method to convert a string to a or.openrdf.rio.RDFFormat
     * @param strFormat
     * @return
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

    /**
     * Field a list of datatype handling strategies
     * @deprecated
     */
   /* private static final org.openrdf.rio.RDFParser.DatatypeHandling allDatatypeHandling[] =
            new org.openrdf.rio.RDFParser.DatatypeHandling[] {
        org.openrdf.rio.RDFParser.DatatypeHandling.IGNORE,
        org.openrdf.rio.RDFParser.DatatypeHandling.NORMALIZE,
        org.openrdf.rio.RDFParser.DatatypeHandling.VERIFY
    };*/

    /**
     * Method to convert a string to a or.openrdf.rio.RDFFormat
     * @return
     * @deprecated
     */
   /* private static org.openrdf.rio.RDFParser.DatatypeHandling stringToDatatypeHandling(String strHandling) {
        for (org.openrdf.rio.RDFParser.DatatypeHandling handling : allDatatypeHandling) {
                if (handling.name().equalsIgnoreCase(strHandling))
                        return handling;
        }
        throw new IllegalArgumentException("Datatype handling strategy for parsing '" + strHandling + "' is not recognised");
    }*/

    public static void loadFile(File file) throws RepositoryException,IOException,RDFParseException {
        repository.initialize();
        try {
            repositoryConnection.add(file, "file://" + file.getAbsolutePath(), RDFFormat.forFileName(file.getAbsolutePath()));
        }
        finally {
            repositoryConnection.close();
        }
    }

    public static void loadMultipleFile(File directoryOfFiles)throws RepositoryException,IOException,RDFParseException{
        repository.initialize();
        File[] files = directoryOfFiles.listFiles();
        try {
            for (File file: files)  {
                //repositoryConnection.add(file, "file://" + file.getAbsolutePath(), RDFFormat.forFileName(file.getAbsolutePath()));//deprecated
                repositoryConnection.add(file, "file://" + file.getAbsolutePath(),Rio.getParserFormatForFileName(file.getAbsolutePath()));
            }
        }
        finally {
            repositoryConnection.close();
        }
    }


    public long loadFileChunked(File file) throws RepositoryException, IOException {

        SystemLog.message("Loading " + file.getName() + " ");
        //Creating the right parser for the right format
        //RDFFormat format = RDFFormat.forFileName(file.getName());
        RDFFormat format = Rio.getParserFormatForFileName(file.getName());
        if(format == null) {
            System.out.println();
            SystemLog.warning("Unknown RDF format for file: " + file);
            return 0;
        }

        URI dumyBaseUrl = new URIImpl(file.toURI().toString());

        URI context = null;
        if(!format.equals(RDFFormat.NQUADS) && !format.equals(RDFFormat.TRIG) && ! format.equals(RDFFormat.TRIX)) {
            String contextParam = CONTEXT;
            if (contextParam == null) {
                context = new URIImpl(file.toURI().toString());
            } else {
                if (contextParam.length() > 0) {
                    context = new URIImpl(contextParam);
                }
            }
        }
        InputStream reader = null;
        try {
            if(file.getName().endsWith("gz")) {
                reader = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file), 256 * 1024));
            }
            else {
                reader = new BufferedInputStream(new FileInputStream(file), 256 * 1024);
            }
            // create a parser home.home.initializer.org.p4535992.mvc.config with preferred settings
           /* boolean verifyData = VERIFY;
            boolean stopAtFirstError = STOP_ON_ERROR;
            boolean preserveBnodeIds = PRESERVE_BNODES;*/
            //NEW MTHOD
            //RioSetting settings = new RioS
            RioSetting verifyDataSet = BasicParserSettings.VERIFY_DATATYPE_VALUES;
            RioSetting stopAtFirstErrorSet = BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES;
            RioSetting preserveBnodeIdsSet = BasicParserSettings.PRESERVE_BNODE_IDS;

           /* RioConfig configs = new RioConfig();
            configs.set(verifyDataSet,VERIFY);
            configs.set(stopAtFirstErrorSet,STOP_ON_ERROR);
            configs.set(preserveBnodeIdsSet,PRESERVE_BNODES);*/

            //ParserConfig home.home.initializer.org.p4535992.mvc.config = new ParserConfig(verifyData, stopAtFirstError, preserveBnodeIds, RDFParser.DatatypeHandling.VERIFY);
            ParserConfig config = new ParserConfig();
            config.set(verifyDataSet,VERIFY);
            config.set(stopAtFirstErrorSet,STOP_ON_ERROR);
            config.set(preserveBnodeIdsSet,PRESERVE_BNODES);
            /*RDFParser.DatatypeHandling datatypeHandling =  org.openrdf.rio.RDFParser.DatatypeHandling.NORMALIZE;*/
            long chunkSize = Long.parseLong(CHUNK_SIZE);
            long start = System.currentTimeMillis();
            // set the parser configuration for our connection
            /*ParserConfig home.home.initializer.org.p4535992.mvc.config = new ParserConfig(verifyData, stopAtFirstError, preserveBnodeIds, datatypeHandling);*/

            repositoryConnection.setParserConfig(config);
            RDFParser parser = Rio.createParser(format);
            parser.setParserConfig(config);

            // add our own custom RDFHandler to the parser. This handler takes care of adding
            // triples to our repository and doing intermittent commits
            ChunkCommitter handler = new ChunkCommitter(repositoryConnection, context, chunkSize);
            parser.setRDFHandler(handler);
            repositoryConnection.commit();
            repositoryConnection.begin();

            //Mitac hack: use parallel update
            if (PARALLEL_LOAD) {
                URI up = new URIImpl("http://www.ontotext.com/useParallelInsertion");
                repositoryConnection.add(up, up, up);
            }

            parser.parse(reader, context == null ? dumyBaseUrl.toString() : context.toString());
            repositoryConnection.commit();
            long statementsLoaded = handler.getStatementCount();
            long time = System.currentTimeMillis() - start;
            System.out.println("Loaded " + statementsLoaded + " statements in " + time + " ms; avg speed = "
                    + (statementsLoaded * 1000 / time) + " st/s");
            return statementsLoaded;
        } catch (Exception e) {
            repositoryConnection.rollback();
            System.out.println();
            SystemLog.warning("Failed to load '" + file.getName() + "' (" + format.getName() + ")." + e);
            e.printStackTrace();
            return 0;
        } finally {
            if (reader != null)
                reader.close();
        }
    }


    /**
     * Method for try to create a reposiotry programmatically intead from the web interface
     * @param graph
     * @param repositoryNode
     * @throws RepositoryException
     * @throws RepositoryConfigException
     */
    public static void createSesameRepository(Graph graph,Resource repositoryNode) throws RepositoryException,RepositoryConfigException {
        // Create a manager for local repositories
        org.openrdf.repository.manager.RepositoryManager repositoryManager =
                new org.openrdf.repository.manager.LocalRepositoryManager(new File("."));
        repositoryManager.initialize();
        // Create a configuration object from the configuration graph
        // and add it to the repositoryManager
        org.openrdf.repository.config.RepositoryConfig repositoryConfig =
                org.openrdf.repository.config.RepositoryConfig.create(graph, repositoryNode);
        repositoryManager.addRepositoryConfig(repositoryConfig);
        // Get the repository to use
        Repository repository = repositoryManager.getRepository("owlim");
        // Open a connection to this repository
        RepositoryConnection repositoryConnection = repository.getConnection();
        // ... use the repository
        // Shutdown connection, repository and manager
        repositoryConnection.close();
        repository.shutDown();
        repositoryManager.shutDown();
    }


    /**
     * This class is inspired by Jeen Broekstra
     * http://rivuli-development.com/further-reading/sesame-cookbook/loading-large-file-in-sesame-native/
     */
    static class ChunkCommitter implements RDFHandler {

        private static long chunkSize;
        private final RDFInserter inserter;
        private final RepositoryConnection conn;
        private static URI context;
        private static ValueFactory factory;

        private long count = 0L;

        public ChunkCommitter(RepositoryConnection conn) {
            inserter = new RDFInserter(conn);
            this.conn = conn;
        }

        public ChunkCommitter(RepositoryConnection conn, URI context, long chunkSize) {
            this.chunkSize = chunkSize;
            this.context = context;
            this.conn = conn;
            this.factory = conn.getValueFactory();
            inserter = new RDFInserter(conn);
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
     * @param queryString
     */
    public static void updateRepository(String queryString){
        Update update = null;
        try {
            update = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, queryString);
            update.execute();
        } catch (RepositoryException|MalformedQueryException|UpdateExecutionException e) {
            SystemLog.exception(e);
        }
    }


}
