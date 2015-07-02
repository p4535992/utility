package com.github.p4535992.util.jenaAndVirtuoso;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

/**
 * Created by Marco on 02/07/2015.
 *
 * @author 4535992.
 * @version 2015-07-02.
 */
@SuppressWarnings("unused")
public class JenaAndVirtuoso {

    private static final String DEFAULT_URL_VIRTUOSO = "jdbc:virtuoso://localhost:1111/charset=UTF-8";
    private static final String DEFAUL_USERNAME= "dba";
    private static final String DEFAUL_PASSWORD= "dba";
    private static final String SPARQL_SELECT_ALL ="SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100";

    private VirtGraph set;

    private static JenaAndVirtuoso instance = null;
    protected JenaAndVirtuoso(){}
    public static JenaAndVirtuoso getInstance(){
        if(instance == null) {
            instance = new JenaAndVirtuoso();
        }
        return instance;
    }

    public VirtGraph connectToVirtuoso(){
        this.set = new VirtGraph (DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        return set;

    }

    public Model execSparql(String sparql){
        Query query = QueryFactory.create(sparql);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
        ResultSet results = vqe.execSelect();
        /*while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode graph = result.get("graph");
            RDFNode s = result.get("s");
            RDFNode p = result.get("p");
            RDFNode o = result.get("o");
            //System.out.println(graph + " { " + s + " " + p + " " + o + " . }");
        }*/
        return results.getResourceModel();
    }
}
