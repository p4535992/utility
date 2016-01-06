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
import org.apache.jena.sparql.engine.http.Params;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;

import java.util.UUID;

import static org.apache.jena.riot.web.HttpOp.execHttpPostForm;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 * href: https://searchcode.com/codesearch/view/27861362/
 */
@SuppressWarnings("unused")
public class Fuseki3Utilities {

    public static org.apache.log4j.Logger logger;

    protected Fuseki3Utilities() {
        logger = org.apache.log4j.Logger.getLogger(this.getClass().getName());
    }

    private static Fuseki3Utilities instance = null;

    public static Fuseki3Utilities getInstance() {
        if (instance == null) {
            instance = new Fuseki3Utilities();
        }
        return instance;
    }


    private static final String DEFAULT_SELECT_URL_FUSEKI_SERVER = "http://localhost:3030/ds/query";
    private static final String DEFAULT_UPDATE_URL_FUSEKI_SERVER = "http://localhost:3030/ds/update";

    public static void execSparqlUpdateOnFuseki(String updateQuerySparql, String sparql) {
        String id = UUID.randomUUID().toString();
        System.out.println(String.format("Adding %s", id));
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(String.format(updateQuerySparql, id)),
                DEFAULT_UPDATE_URL_FUSEKI_SERVER);
        upp.execute();
    }

    public static Model execSparqlSelectOnFuseki(
            String hostFuseki, String selectQuerySparql, String filePathOutput) {
        Model model = Jena3Utilities.createModel();
        //Query the collection, dump output
        //OutputStream out = new FileOutputStream(filePathOutput);
        try (QueryExecution qe = QueryExecutionFactory.sparqlService(
                DEFAULT_SELECT_URL_FUSEKI_SERVER, selectQuerySparql)) {
            ResultSet results = qe.execSelect();
            //ResultSetFormatter.out(out,results);
            ResultSetRewindable result = ResultSetFactory.makeRewindable(model);
            ResultSetFormatter.asText(results);
            result.reset();
        }
        return model;
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

        /**n "";
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
