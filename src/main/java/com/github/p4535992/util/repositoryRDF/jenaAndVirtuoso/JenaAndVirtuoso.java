package com.github.p4535992.util.repositoryRDF.jenaAndVirtuoso;
import com.github.p4535992.util.log.SystemLog;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.shared.Command;
import com.hp.hpl.jena.sparql.util.NodeUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 02/07/2015.
 * Important: for work with this class you need the Jena FrameWork version 2.10.X
 * http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtJenaProvider
 * @author 4535992.
 * @version 2015-09-30.
 */
@SuppressWarnings("unused")
public class JenaAndVirtuoso {

    private static final String DEFAULT_URL_VIRTUOSO = "jdbc:virtuoso://localhost:1111/charset=UTF-8";
    private static final String DEFAUL_USERNAME= "dba";
    private static final String DEFAUL_PASSWORD= "dba";
    private static final String SPARQL_SELECT_ALL ="SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100";

    public static virtuoso.jena.driver.VirtGraph graph;

    private static JenaAndVirtuoso instance = null;
    public JenaAndVirtuoso(){}
    public static JenaAndVirtuoso getInstance(){
        if(instance == null) {
            instance = new JenaAndVirtuoso();
        }
        return instance;
    }

    /**
     * Method to connect to a virtuoso repository like a Jena Model.
     */
    public void connectToVirtuoso(){
        graph = new virtuoso.jena.driver.VirtGraph (DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        SystemLog.message("Connection to the Virtuoso Repository Successed!");
    }



    /**
     * Method to execute a SPARQL query on the virtuoso graph VirtGraph.
     * @param sparql string of the content of the SPARQL Query.
     * @return the Jena Model result of the SPARQL query.
     */
    public Model execSparqlToRepository(String sparql){
        Query query = QueryFactory.create(sparql);
        virtuoso.jena.driver.VirtuosoQueryExecution vqe =
                virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(sparql, graph);
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

    /**
     * Method to execute a Select SPARQL query on the virtuoso graph VirtGraph.
     * @param urlResourceGraph uri web address where is the endpoint SPARQL of the virtuoso repository.
     * @return the Jena Model result of the SPARQL query.
     */
    public Model execSparqlSelectAllFromRepository(String urlResourceGraph){
        String rdfFormat = "";
        graph.read(urlResourceGraph, null);
        Query sparql = QueryFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }");
        virtuoso.jena.driver.VirtuosoQueryExecution vqe =
                virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create (sparql, graph);
        ResultSet results = vqe.execSelect();
        /*while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode graph_name = result.get("graph");
            RDFNode s = result.get("s");
            RDFNode p = result.get("p");
            RDFNode o = result.get("o");
            System.out.println(graph_name + " { " + s + " " + p + " " + o + " . }");
        }*/
        return results.getResourceModel();
    }


    /*public Triple createTriple(Node subject,Node predicate,Node object){
        return new Triple(subject,predicate,object);
    }

    public Node createNode(String uriResource){
        return NodeFactory.createURI(uriResource);
    }*/

    /**
     * Method to add a List of jena Graph triple to a Virtuoso Graph Object.
     * @param triples the List of jena Graph triples.
     */
    @SuppressWarnings("deprecation")
    public void addTriplesToVirtuosoGraph(List<Triple> triples){
        graph.getBulkUpdateHandler().add(triples);
        for(Triple triple: triples){
            graph.add(triple);
        }
    }

    /**
     * Method to check if a Virtuoso Graph is empty.
     * @return if true the Virtuoso Graph is empty.
     */
    public boolean isEmpty(){ return graph.isEmpty();}

    /**
     * Method to check if a Virtuoso Graph contains a specific Jena Triple Object.
     * @param triple the Jena Triple Object.
     * @return if true the Virtuoso HGraph contains the specific Triple Object.
     */
    public boolean isContains(Triple triple){return graph.contains(triple);}

    /**
     * Method for count the number of triple in the Virtuoso graph.
     * @return the count of the triple in the virtuoso Graph.
     */
    public int getCount(){return graph.getCount();}

    /**
     * Method to clear a Virtuoso graph.
     */
    public void clear(){graph.clear();}

    /**
     * Method to delete Triples from Virtuoso graph.
     * @param triples List of Triple to delete.
     */
    @SuppressWarnings("deprecation")
    public void deleteTriplesFromVirtuosoGraph(List<Triple> triples){
       /* for(Triple triple: triples){
            graph.remove(triple);
        }*/
        graph.getBulkUpdateHandler().delete(triples);

    }

    /**
     * Method to check what Triple of a List are present in the Virtuoso Graph.
     * @param triples list of Jena Triple you want to found and read.
     * @return the List of Jena Triple you have founded.
     */
    public List<Triple> findTriplesFromVirtuosoGraph(List<Triple> triples) {
        List<Triple> found = new ArrayList<>();
        for(Triple triple: triples){
            if(isContains(triple)){
                found.add(triple);
            }
        }
        return found;
    }

    /**
     * Method to check what Triple with a specific subject are present in the Virtuoso Graph.
     * @param subject the Jena Node subject for the research.
     * @return the List of Jena Triple you have founded.
     */
    public List<Triple> findTriplesFromVirtuosoGraph(Node subject){
        return findTriplesFromVirtuosoGraph(new Triple(subject, Node.ANY, Node.ANY));
    }

    /**
     * Method to check what Triple with a specific subject and predictae or Object
     * are present in the Virtuoso Graph.
     * @param subject the Jena Node subject for the research.
     * @param predicateOrObject the Jena Node predicate or Object of the Triple for the research.
     * @param isPredicate if true the predicateOrObject is a predicate of the Triple else is a Object..
     * @return the List of Jena Triple you have founded.
     */
    public List<Triple> findTriplesFromVirtuosoGraph(Node subject,Node predicateOrObject,boolean isPredicate){
        if(isPredicate) {
            return findTriplesFromVirtuosoGraph(new Triple(subject, predicateOrObject, Node.ANY));
        }else{
            return findTriplesFromVirtuosoGraph(new Triple(subject, Node.ANY, predicateOrObject));
        }
    }

    /**
     /**
     * Method to check what Triple with a specific subject and predictae and Object
     * are present in the Virtuoso Graph.
     * @param subject the Jena Node subject for the research.
     * @param predicate the Jena Node predicate for the research.
     * @param object the Jena Node object for the research.
     * @return the List of Jena Triple you have founded.
     */
    public List<Triple> findTriplesFromVirtuosoGraph(Node subject,Node predicate,Node object){
        return findTriplesFromVirtuosoGraph(new Triple(subject, predicate, object));
    }


    /**
     * Method to check what Triple with a specific Triple are present in the Virtuoso Graph.
     * @param triple the Jena Triple for the research.
     * @return the List of Jena Triple you have founded.
     */
    public List<Triple> findTriplesFromVirtuosoGraph(Triple triple){
        List<Triple> found = new ArrayList<>();
        ExtendedIterator<Triple> iter = graph.find(
                triple.getSubject(),triple.getPredicate(),triple.getObject());
        for( ; iter.hasNext() ; ){
            found.add(iter.next());
        }
        return found;
    }

    /**
     * Method to begin a transaction in a Virtuoso Graph.
     */
    public void beginTransaction(){ graph.getTransactionHandler().begin(); }

    /**
     * Method to commit a transaction in a Virtuoso Graph.
     */
    public void commitTransaction(){graph.getTransactionHandler().commit();}

    /**
     * Method to abort a transaction in a Virtuoso Graph.
     */
    public void abortTransaction(){ graph.getTransactionHandler().abort();}

    /**
     * Method to check a if a Virtuoso Graph is in transaction.
     * @return if true the Virtuoso Graph is in transaction.
     */
    public boolean isInTransaction(){ return graph.getTransactionHandler().transactionsSupported();}

    /**
     * Method to execute a command on a Virtuoso Graph.
     * @param command the jena Command to execute.
     * @return the result of the command executed.
     */
    public Object executeInTransaction(Command command){
        return graph.getTransactionHandler().executeInTransaction(command);
    }

    /**
     * Method to exec a clear SPARQL query on a Virtuoso Graph.
     * @param uriGraph the uri location of the graph.
     */
    public void execSparqlClearOnVirtuosoGraph(String uriGraph){
        String str = "CLEAR GRAPH <"+uriGraph+">";
        virtuoso.jena.driver.VirtuosoUpdateRequest vur =
                virtuoso.jena.driver.VirtuosoUpdateFactory.create(str, graph);
        vur.exec();

    }

    /**
     * Method to exec insert SPARQL query on a Virtuoso Graph.
     * @param uriGraph the uri location of the graph.
     * @param subject the uri location of the specific subject.
     * @param predicate  the uri location of the specific predicate.
     * @param object the uri/literal location of the specific object.
     * @param isLiteral if true the object param is a literal and not a uri
     */
    public void execSparqlInsertOnVirtuosoGraph(
            String uriGraph,String subject,String predicate,String object,boolean isLiteral){
        String str = "INSERT INTO GRAPH <http://test1> { " ;
        if(isLiteral) {
            str += "<" + subject + "> <" + predicate + "> '" + object + "' . }";
        }else{
            str += "<" + subject + "> <" + predicate + "> <" + object + "> . }";
        }
        virtuoso.jena.driver.VirtuosoUpdateRequest vur =
                virtuoso.jena.driver.VirtuosoUpdateFactory.create(str, graph);
        vur.exec();
    }

    /**
     * Method to exec a select SPARQL query on a Virtuoso Graph.
     * @param uriGraph the uri location of the graph.
     * @return the Jena Model with the result of the query SPARQL.
     */
    public Model execSparqlSelectOnVirtuosoGraph(String uriGraph){
        Query sparql = QueryFactory.create("SELECT * FROM <"+uriGraph+"> WHERE { ?s ?p ?o }");
        virtuoso.jena.driver.VirtuosoQueryExecution vqe =
                virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create (sparql, graph);
        ResultSet results = vqe.execSelect();
        return results.getResourceModel();
    }

    /**
     * Method to exec a describe SPARQL query on a Virtuoso Graph.
     * @param uriGraph the uri location of the graph.
     * @param uriResource the uri location of the resource.
     * @return the Jena Model with the result of the query SPARQL.
     */
    public Model execSparqlDescribeOnVirtuosoGraph(String uriGraph,String uriResource) {
        Query sparql = QueryFactory.create("DESCRIBE <"+uriResource+"> FROM <"+uriGraph+">");
        virtuoso.jena.driver.VirtuosoQueryExecution vqe =
                virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(sparql, graph);
        return vqe.execDescribe();
        /*Graph g = model.getGraph();
        for (Iterator i = g.find(Node.ANY, Node.ANY, Node.ANY); i.hasNext(); ) {
            Triple t = (Triple) i.next();
        }*/
    }


    /**
     * Method to exec a construct SPARQL query on a Virtuoso Graph.
     * @param uriGraph the uri location of the graph.
     * @param uripredicate the uri of the predicate you iuse for search a specific type of triple..
     * @return the Jena Model with the result of the query SPARQL.
     */
    public Model execSparqlConstructOnVirtuosoGraph(String uriGraph,String uripredicate) {
        Query sparql = QueryFactory.create("CONSTRUCT { ?x <"+uripredicate+"> ?y } FROM <"+uriGraph+"> ");
                //"WHERE { ?x <"+uriWherePredicate+"> ?y }");
        virtuoso.jena.driver.VirtuosoQueryExecution vqe =
                virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(sparql, graph);
        return vqe.execConstruct();
        /*Graph g = model.getGraph();
        for (Iterator i = g.find(Node.ANY, Node.ANY, Node.ANY); i.hasNext(); ) {
            Triple t = (Triple) i.next();
        }*/
    }

    /**
     * Method to exec a ask SPARQL query on a Virtuoso Graph.
     * @param uriGraph the uri location of the graph.
     * @param triple the Jena Triple to search on the Virtuoso graph.
     * @return if true the Triple is present on the Virtuoso Graph.
     */
    public boolean execSparqlAskOnVirtuosoGraph(String uriGraph,Triple triple) {
        Query sparql = QueryFactory.create("ASK FROM <"+uriGraph+"> " +
                "WHERE { <"+triple.getSubject()+"> <"+triple.getPredicate()+"> <"+triple.getObject()+"> }");
        virtuoso.jena.driver.VirtuosoQueryExecution vqe = virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(sparql, graph);
        return vqe.execAsk();
    }

    /**
     * Method to import a List of Jena Statement on a Virtuoso Graph.
     * @param uriResourceGraph the uri location of the graph.
     * @param listStatement the Jena List of Statement to import tot the Virtuoso Graph.
     * @return the Jena Model of the new Virtuoso Grapg after the addition.
     */
    public Model importDataToRepository(String uriResourceGraph,List<Statement> listStatement){
        Model model = ModelFactory.createDefaultModel();
        virtuoso.jena.driver.VirtModel mdata = virtuoso.jena.driver.VirtModel.openDatabaseModel(
                uriResourceGraph, DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        mdata.add(listStatement);
        String queryString = "SELECT * WHERE {?s ?p ?o}" ;
        QueryExecution qexec = virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(queryString, mdata) ;
        try {
            ResultSet rs = qexec.execSelect() ;
            for ( ; rs.hasNext() ; ) {
                QuerySolution result = rs.nextSolution();
                Resource s = result.getResource("s");
                Property p = (Property)result.get("p");
                RDFNode o = result.get("o");
                model.add(new StatementImpl(s,p,o));
                //System.out.println(" { " + s + " " + p + " " + o + " . }");
            }
        } finally {
            qexec.close() ;
            mdata.close();
        }
        return model;
    }

    /**
     * Method to import a List of Jena Statement but like new rule on a Virtuoso Graph.
     * @param uriResourceGraph the uri location of the graph.
     * @param listStatement the Jena List of Statement to import tot the Virtuoso Graph.
     * @return the Jena Model of the new Virtuoso Grapg after the addition.
     */
    public Model importRuleToRepository(String uriResourceGraph,List<Statement> listStatement){
        Model model = ModelFactory.createDefaultModel();
        virtuoso.jena.driver.VirtModel mdata = virtuoso.jena.driver.VirtModel.openDatabaseModel(
                uriResourceGraph, DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        virtuoso.jena.driver.VirtModel mrule  = virtuoso.jena.driver.VirtModel.openDatabaseModel(
                uriResourceGraph, DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        mrule .add(listStatement);
        String queryString = "SELECT * WHERE {?s ?p ?o}" ;
        QueryExecution qexec = virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(queryString,  mrule) ;
        try {
            Resource r1 = mrule.createResource("http://rdfs.org/sioc/ns#Space") ;
            r1.addProperty(RDFS.subClassOf, rdfNode(mrule, "http://www.w3.org/2000/01/rdf-schema#Resource"));

            r1 = mrule.createResource("http://rdfs.org/sioc/ns#Container") ;
            r1.addProperty(RDFS.subClassOf, rdfNode(mrule, "http://rdfs.org/sioc/ns#Space"));

            r1 = mrule.createResource("http://rdfs.org/sioc/ns#Forum") ;
            r1.addProperty(RDFS.subClassOf, rdfNode(mrule, "http://rdfs.org/sioc/ns#Container"));

            r1 = mrule.createResource("http://rdfs.org/sioc/types#Weblog") ;
            r1.addProperty(RDFS.subClassOf, rdfNode(mrule, "http://rdfs.org/sioc/ns#Forum"));

            r1 = mrule.createResource("http://rdfs.org/sioc/types#MessageBoard") ;
            r1.addProperty(RDFS.subClassOf, rdfNode(mrule, "http://rdfs.org/sioc/ns#Forum"));

            r1 = mrule.createResource("http://rdfs.org/sioc/ns#link") ;
            r1.addProperty(RDFS.subPropertyOf, rdfNode(mrule, "http://rdfs.org/sioc/ns"));

            mrule.close();
            mdata.createRuleSet(uriResourceGraph.replace("http://",""),uriResourceGraph);

            virtuoso.jena.driver.VirtInfGraph infGraph = new virtuoso.jena.driver.VirtInfGraph(uriResourceGraph.replace("http://",""), false,
                    uriResourceGraph, DEFAULT_URL_VIRTUOSO,DEFAUL_USERNAME, DEFAUL_PASSWORD);
            InfModel infModel = ModelFactory.createInfModel(infGraph);
        } finally {
            qexec.close() ;
            mdata.close();
        }
        return model;
    }

    /*public static Statement statement( Model m, String fact ) {
        StringTokenizer st = new StringTokenizer( fact );
        Resource sub = resource( m, st.nextToken() );
        Property pred = property(m, st.nextToken());
        RDFNode obj = rdfNode(m, st.nextToken());
        return m.createStatement(sub, pred, obj);
    }

    public static Resource resource( Model m, String s ){
        return (Resource) rdfNode( m, s );
    }

    public static Property property( Model m, String s ){
        return rdfNode( m, s ).as( Property.class );
    }*/

    public static RDFNode rdfNode( Model m, String s ){
        return m.asRDFNode(NodeUtils.asNode(s));
    }






}
