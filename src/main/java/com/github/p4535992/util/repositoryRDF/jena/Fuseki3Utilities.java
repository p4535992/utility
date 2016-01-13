package com.github.p4535992.util.repositoryRDF.jena;
/** if you use jena 2 */
/*import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.Params;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;*/
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.Params;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

import java.util.UUID;

import static org.apache.jena.riot.web.HttpOp.execHttpPostForm;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 * href: https://searchcode.com/codesearch/view/27861362/
 * href: http://www.programcreek.com/java-api-examples/index.php?source_dir=BBAW_CMS-master/src/org/bbaw/wsp/cms/mdsystem/metadata/rdfmanager/fuseki/FusekiClient.java
 */
@SuppressWarnings("unused")
public class Fuseki3Utilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Fuseki3Utilities.class);

    protected Fuseki3Utilities() {}

    /**
     * The select-mode for a SparQl query.
     */
    public static final String MODE_SELECT = "SELECT";
    /**
     * This is the standard query, no named graph is specified within the incoming query.
     */
    private static final String NONE_GRAPH_SPECIFIED = "";
    /**
     * The fuseki endpoint to do a SparQl query. Will be concatenated to the dataset URL.
     */
    public static String ENDPOINT_QUERY = "/query";
    /**
     * The fuseki endpoint to do an update on a dataset. Will be concatenated to the dataset URL.
     */
    public static String ENDPOINT_UPDATE = "/update";
    /**
     * The fuseki endpoint to do a manipulate the dataset. Will be concatenated to the dataset URL.
     */
    public static String ENDPOINT_DATA = "/data";
    /**
     * The name of the default model (if not specifying a name for the graph).
     */
    public static String DEFAULT_NAMED_MODEL = "default";

    public static String DEFAULT_SELECT_URL_FUSEKI_SERVER = "http://localhost:3030/ds/query";
    public static String DEFAULT_UPDATE_URL_FUSEKI_SERVER = "http://localhost:3030/ds/update";


    private static Fuseki3Utilities instance = null;

    public static Fuseki3Utilities getInstance() {
        if (instance == null) {
            instance = new Fuseki3Utilities();
        }
        return instance;
    }

    /**
     * Execute a SparQl query on a remote fuseki server.
     * @param datasetUrl - the URL to the dataset on which the query will be done.
     * @param queryCommand - the SparQl query.
     * @return a {@link ResultSet} or null, e.g. if the mode doesn't return anything
     */
    public ResultSet execSparqlSelect(final String datasetUrl, final String queryCommand) {
        // perform a query on an unspecified (default) graph
        return execSparqlSelect(datasetUrl, queryCommand, NONE_GRAPH_SPECIFIED);
    }

    /**
     * Execute a SparQl query on a remote fuseki server.
     * @param datasetUrl - the URL to the dataset on which the query will be done.
     * @param queryCommand - the SparQl query.
     * @param defaultGraphUri - the Uri of the default graph which will be queried.
     * @return a {@link ResultSet} or null, e.g. if the mode doesn't return anything
     */
    public ResultSet execSparqlSelect(final String datasetUrl, final String queryCommand, final String defaultGraphUri) {
        String pathToQueryEndpoint;

        if(datasetUrl.endsWith(ENDPOINT_QUERY)) pathToQueryEndpoint = datasetUrl;
        else pathToQueryEndpoint = datasetUrl+ENDPOINT_QUERY;

        return queryServerWithDefaultGraph(pathToQueryEndpoint, queryCommand, MODE_SELECT, defaultGraphUri);
    }

    /**
     * Execute an update (manipulation) on a remote fuseki server.
     * @param datasetUrl - the URL to the dataset on which the update will be done.
     * @param updateQuerySparql - the update Command (e.g. CLEAR DEFAULT)
     */
    public void execSparqlUpdate(final String datasetUrl, final String updateQuerySparql) {
        String id = UUID.randomUUID().toString();
        String pathToUpdateEndpoint;
        if(datasetUrl.endsWith(ENDPOINT_UPDATE)) pathToUpdateEndpoint = datasetUrl;
        else pathToUpdateEndpoint = datasetUrl+ENDPOINT_UPDATE;

        UpdateRequest request = UpdateFactory.create(String.format(updateQuerySparql, id));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, pathToUpdateEndpoint);
        proc.execute(); // perform the update
    }

    public Model setResultSetToAModel(ResultSet results ){
        Model model = ModelFactory.createDefaultModel();
        ResultSetRewindable result = ResultSetFactory.makeRewindable(model);
        ResultSetFormatter.asText(results);
        result.reset();
        return model;
    }

    /**
     * Perform the select query.
     * @param pathToQueryEndpoint the String path tot the endpoint url SPARQL web service.
     * @param querySparql the String query SPARQL.
     * @param resultFormat the String resultFormat of the Query..
     * @param defaultGraph the String of the Grap URI.
     * @return a {@link ResultSet} containing the specified response.
     */
    private ResultSet queryServerWithDefaultGraph(final String pathToQueryEndpoint, final String querySparql,
                                                  final String resultFormat, final String defaultGraph) {
        Query q = QueryFactory.create(querySparql);
        try (QueryExecution queryEx = QueryExecutionFactory.sparqlService(
                pathToQueryEndpoint, q, defaultGraph)) {
            //QueryExecution queryEx = QueryExecutionFactory.sparqlService(pathToQueryEndpoint, q, defaultGraph);
            if (resultFormat.equals(MODE_SELECT)) {
                return queryEx.execSelect(); // SELECT returns a ResultSet
            }else{
                logger.warn("This is not a Select query for the fuseki server.");
            }
            return null;
        }
    }

    /**
     * Put a model to a remote dataset. Consider, that an existing dataset will be replaced if you don't specify a modelName (for a named model).
     * If you don't prefer a named model, use {@link Fuseki3Utilities}.DEFAULT_NAMED_MODEL.
     * @param url - the URL to the dataset on which the model will be putted.
     * @param model - the Jena model
     * @param modelName - the name of the model.
     */
    public void putModel(final String url, final Model model, final String modelName) {
        String pathToQueryEndpoint = url+ENDPOINT_DATA;
        DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(pathToQueryEndpoint);
        accessor.putModel(modelName, model);
    }

    /**
     * Method to create a new dtaabase on Fuseki server.
     *
     * @param host          the String of the host.
     * @param port          the String of the port of Fuseki.
     * @param nameWarFuseki the String of the name of the War.
     * @param datasetName   the String name of the dataset.
     * @param dbType        the String type of the database.
     */
    public static void createNewDatabase(
            String host, String port, String nameWarFuseki, String datasetName, String dbType) {
        // find our Fuseki instance
        //final String fusekiUrl = "http://localhost:" + PORT + "/jena-fuseki-war/";
        final String fusekiUrl = host + ":" + port + "/" + nameWarFuseki + "/";
        // build a dataset to work with
        //final String datasetName = "testNormalOperation";
        final Params params = new Params();
        // we’re using an in-memory dataset here, but you could use a TDB-backed dataset instead
        //params.addParam("dbType", "mem");
        params.addParam("dbType", dbType);
        params.addParam("dbName", datasetName);
        /*Map<String, HttpResponseHandler> map = new HashMap<>();
        HttpResponseHandler handler = new HttpResponseHandler() {
            @Override
            public void handle(String s, HttpResponse httpResponse) throws IOException {

            }
        };*/
        //execHttpPostForm(fusekiUrl + "$/datasets", params, "",handler);
        execHttpPostForm(fusekiUrl + "$/datasets", params);
        // The important point is to realize that the administrative forms are at the
        // “{your-fuseki-instance}/$” url. In your case you want the  “{your-fuseki-instance}/$/datasets”
        // section, to which you can POST your request for a new dataset.
    }

    /**
     * This (helper-)enum encapsules special sparql commands and returns the commands as String for often required commands.
     *
     * @author Sascha Feldmann (wsp-shk1)
     */
    public enum SparQlStore {
        CLEAR_DATASET, CLEAR_GRAPH, CLEAR_DEFAULT, SELECT_DEFAULT, SELECT_NAMED;

        /**
         * @return the spaql command as {@link String}.
         */
        public String getUpdateCommandString() {
            switch (this) {
                case CLEAR_DATASET:
                    return "CLEAR ALL";
                case CLEAR_GRAPH:
                    return "CLEAR GRAPH ";
                case CLEAR_DEFAULT:
                    return "CLEAR DEFAULT";
                default:
                    return "";
            }
        }

        /**
         * Method to build the String of a Select SPARQL query. 
         * @param toSelect the single element to Select.
         * @param graphName the String name of the Graph to Select.
         * @param graphPattern the String Graph pattern to Search.
         * @return the sparql query command as {@link String}
         */
        public String getSelectQueryString(final String toSelect, final String graphName, final String graphPattern) {
            switch (this) {
                case SELECT_DEFAULT:
                    return "SELECT " + toSelect + " { " + graphPattern + "}";
                case SELECT_NAMED:
                    return "SELECT " + toSelect + " FROM NAMED <" + graphName + "> { " + graphPattern + "}";
                default:
                    return "";
            }
        }
    }
}
