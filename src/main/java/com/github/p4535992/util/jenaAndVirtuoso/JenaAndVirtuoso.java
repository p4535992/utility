package com.github.p4535992.util.jenaAndVirtuoso;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.shared.Command;
import com.hp.hpl.jena.sparql.util.NodeUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import virtuoso.jena.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by 4535992 on 02/07/2015.
 * for work with this class you need the Jena FrameWOrk version 2.10.X
 * http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtJenaProvider
 * @author 4535992.
 * @version 2015-07-02.
 */
@SuppressWarnings("unused")
public class JenaAndVirtuoso {

    private static final String DEFAULT_URL_VIRTUOSO = "jdbc:virtuoso://localhost:1111/charset=UTF-8";
    private static final String DEFAUL_USERNAME= "dba";
    private static final String DEFAUL_PASSWORD= "dba";
    private static final String SPARQL_SELECT_ALL ="SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100";

    public static VirtGraph graph;

    private static JenaAndVirtuoso instance = null;
    public JenaAndVirtuoso(){}
    public static JenaAndVirtuoso getInstance(){
        if(instance == null) {
            instance = new JenaAndVirtuoso();
        }
        return instance;
    }

    public VirtGraph connectToVirtuoso(){
        graph = new VirtGraph (DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        return graph;

    }

    public Model execSparqlToRepository(String sparql){
        Query query = QueryFactory.create(sparql);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, graph);
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

    public Model execSparqlSelectAllToRepository(String urlResourceGraph,String rdfFormat){
        graph.read(urlResourceGraph,rdfFormat);
        Query sparql = QueryFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }");
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
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

    public Triple createTriple(Node subject,Node predicate,Node object){
        return new Triple(subject,predicate,object);
    }

    public Node createNode(String uriResource){
        return NodeFactory.createURI(uriResource);
    }

    @SuppressWarnings("deprecation")
    public void addTriplesToGraph(List<Triple> triples){
        graph.getBulkUpdateHandler().add(triples);
       /* for(Triple triple: triples){
            graph.add(triple);
        }*/
    }




    public boolean isEmpty(){
        return graph.isEmpty();
    }

    public boolean isContains(Triple triple){
        return graph.contains(triple);
    }

    public int getCount(){
        return graph.getCount();
    }

    public void clear(){
        graph.clear();
    }

    @SuppressWarnings("deprecation")
    public void deleteTriplesFromRepository(List<Triple> triples){
       /* for(Triple triple: triples){
            graph.remove(triple);
        }*/
        graph.getBulkUpdateHandler().delete(triples);

    }

    public List<Triple> readTriplesFromRepository(List<Triple> triples) {
        List<Triple> found = new ArrayList<>();
        for(Triple triple: triples){
            if(isContains(triple)){
                found.add(triple);
            }
        }
        return found;
    }

    public List<Triple> findTriplesFromRepository(Node subject){
        return findTriplesFromRepository(new Triple(subject, Node.ANY, Node.ANY));
    }

    public List<Triple> findTriplesFromRepository(Node subject,Node predicate,boolean isPredicate){
        if(isPredicate) {
            return findTriplesFromRepository(new Triple(subject, predicate, Node.ANY));
        }else{
            return findTriplesFromRepository(new Triple(subject,Node.ANY,predicate));
        }
    }

    public List<Triple> findTriplesFromRepository(Node subject,Node predicate,Node object){
        return findTriplesFromRepository(new Triple(subject,predicate,object));
    }


    public List<Triple> findTriplesFromRepository(Triple triple){
        List<Triple> found = new ArrayList<>();
        ExtendedIterator<Triple> iter = graph.find(
                triple.getSubject(),triple.getPredicate(),triple.getObject());
        for( ; iter.hasNext() ; ){
            found.add(iter.next());
        }
        return found;
    }

    public void beginTransaction(){
        graph.getTransactionHandler().begin();
    }

    public void commitTransaction(){
        graph.getTransactionHandler().commit();
    }

    public void abortTransaction(){
        graph.getTransactionHandler().abort();
    }

    public void isInTransaction(){
        graph.getTransactionHandler().transactionsSupported();
    }

    public void executeInTransaction(Command command){
        graph.getTransactionHandler().executeInTransaction(command);
    }

    public void clearGraphWithSPARQL(String uriGraph){
        String str = "CLEAR GRAPH <"+uriGraph+">";
        VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(str, graph);
        vur.exec();

    }

    public void insertGraphWithSPARQL(String uriGraph,String uriResource,String uriPredicate,String object,boolean isLiteral){
        String str = "INSERT INTO GRAPH <http://test1> { " ;
        if(isLiteral) {
            str += "<" + uriResource + "> <" + uriPredicate + "> '" + object + "' . }";
        }else{
            str += "<" + uriResource + "> <" + uriPredicate + "> <" + object + "> . }";
        }
        VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(str, graph);
        vur.exec();
    }

    public Model execSparqlSelectOnGraph(String uriGraph){
        Query sparql = QueryFactory.create("SELECT * FROM <"+uriGraph+"> WHERE { ?s ?p ?o }");
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
        ResultSet results = vqe.execSelect();
        return results.getResourceModel();
    }

    public Model execSparqlDescribeOnGraph(String uriGraph,String uriResource) {
        Query sparql = QueryFactory.create("DESCRIBE <"+uriResource+"> FROM <"+uriGraph+">");
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, graph);
        return vqe.execDescribe();
        /*Graph g = model.getGraph();
        for (Iterator i = g.find(Node.ANY, Node.ANY, Node.ANY); i.hasNext(); ) {
            Triple t = (Triple) i.next();
        }*/
    }


    public Model execSparqlConstructOnGraph(String uriGraph,String uripredicate) {
        Query sparql = QueryFactory.create("CONSTRUCT { ?x <"+uripredicate+"> ?y } FROM <"+uriGraph+"> ");
                //"WHERE { ?x <"+uriWherePredicate+"> ?y }");
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, graph);
        return vqe.execConstruct();
        /*Graph g = model.getGraph();
        for (Iterator i = g.find(Node.ANY, Node.ANY, Node.ANY); i.hasNext(); ) {
            Triple t = (Triple) i.next();
        }*/
    }

    public boolean execSparqlAskOnGraph(String uriGraph,Triple triple) {
        Query sparql = QueryFactory.create("ASK FROM <"+uriGraph+"> " +
                "WHERE { <"+triple.getSubject()+"> <"+triple.getPredicate()+"> <"+triple.getObject()+"> }");
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, graph);
        return vqe.execAsk();
    }

    public Model importDataToRepository(String uriResourceGraph,List<Statement> listStatement){
        Model model = ModelFactory.createDefaultModel();
        VirtModel mdata = VirtModel.openDatabaseModel(
                uriResourceGraph, DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        mdata.add(listStatement);
        String queryString = "SELECT * WHERE {?s ?p ?o}" ;
        QueryExecution qexec = VirtuosoQueryExecutionFactory.create(queryString, mdata) ;
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

    public Model importRuleToRepository(String uriResourceGraph,List<Statement> listStatement){
        Model model = ModelFactory.createDefaultModel();
        VirtModel mdata = VirtModel.openDatabaseModel(
                uriResourceGraph, DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        VirtModel mrule  = VirtModel.openDatabaseModel(
                uriResourceGraph, DEFAULT_URL_VIRTUOSO, DEFAUL_USERNAME, DEFAUL_PASSWORD);
        mrule .add(listStatement);
        String queryString = "SELECT * WHERE {?s ?p ?o}" ;
        QueryExecution qexec = VirtuosoQueryExecutionFactory.create(queryString,  mrule) ;
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

            VirtInfGraph infGraph = new VirtInfGraph(uriResourceGraph.replace("http://",""), false,
                    uriResourceGraph, DEFAULT_URL_VIRTUOSO,DEFAUL_USERNAME, DEFAUL_PASSWORD);
            InfModel infModel = ModelFactory.createInfModel(infGraph);
        } finally {
            qexec.close() ;
            mdata.close();
        }
        return model;
    }

    public static Statement statement( Model m, String fact ) {
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
    }

    public static RDFNode rdfNode( Model m, String s ){
        return m.asRDFNode(NodeUtils.asNode(s));
    }






}
