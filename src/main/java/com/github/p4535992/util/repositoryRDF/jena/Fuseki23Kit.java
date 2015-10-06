package com.github.p4535992.util.repositoryRDF.jena;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 * href: https://searchcode.com/codesearch/view/27861362/
 */
@SuppressWarnings("unused")
public class Fuseki23Kit {

    public static org.apache.log4j.Logger logger;

    protected Fuseki23Kit() {
        logger = org.apache.log4j.Logger.getLogger(this.getClass().getName());
    }

    private static Fuseki23Kit instance = null;

    public static Fuseki23Kit getInstance(){
        if(instance == null) {
            instance = new Fuseki23Kit();
        }
        return instance;
    }


    private static final String DEFAULT_SELECT_URL_FUSEKI_SERVER = "http://localhost:3030/ds/query";
    private static final String DEFAULT_UPDATE_URL_FUSEKI_SERVER = "http://localhost:3030/ds/update";

    public static  void execSparqlUpdateOnFuseki(String updateQuerySparql,String sparql) {
        String id = UUID.randomUUID().toString();
        System.out.println(String.format("Adding %s", id));
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(String.format(updateQuerySparql, id)),
                DEFAULT_UPDATE_URL_FUSEKI_SERVER);
        upp.execute();
    }

    public static Model execSparqlSelectOnFuseki(String hostFuseki,String selectQuerySparql,String filePathOutput) {
        Model model = Jena2Kit.createModel();
        //Query the collection, dump output
        //OutputStream out = new FileOutputStream(filePathOutput);
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                DEFAULT_SELECT_URL_FUSEKI_SERVER, selectQuerySparql);
        ResultSet results = qe.execSelect();
        //ResultSetFormatter.out(out,results);
        ResultSetFormatter.asRDF(model,results);
        qe.close();
        return model;
    }
}
