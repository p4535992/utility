package com.github.p4535992.util.sparql;

/**
 * Created by 4535992 on 07/07/2015.
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class SparqlKit {

    private static final String SPARQL_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
    private static final String a = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";

    private static SparqlKit instance = null;
    protected SparqlKit(){}
    public static SparqlKit getInstance(){
        if(instance == null) {
            instance = new SparqlKit();
        }
        return instance;
    }
}
