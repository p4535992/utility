package com.github.p4535992.util.repositoryRDF.jenaAndSesame;

import com.github.p4535992.util.repositoryRDF.jena.Jena2Kit;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame28Kit;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.util.NodeUtils;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by 4535992 on 10/06/2015.
 * href: https://github.com/mhgrove/Empire/blob/master/jena/main/src/com/clarkparsia/empire/jena/util/JenaSesameUtils.java
 * href: https://github.com/afs/JenaSesame/blob/master/src/org/openjena/jenasesame/impl/Convert.java
 * href: https://github.com/semoss/semoss/blob/master/src/prerna/util/JenaSesameUtils.java
 * @author 4535992.
 * @version 2015-07-02.
 */
@SuppressWarnings("unused")
public class JenaAndSesame {

    private static JenaAndSesame instance = null;
    protected JenaAndSesame(){}

    public static JenaAndSesame getInstance(){
        if(instance == null) {
            instance = new JenaAndSesame();
        }
        return instance;
    }

    /*
     * Method to convert a Sesame Dataset to a JenaModel
     * @param repository
     * @return
     * @throws org.openrdf.repository.RepositoryException
     */
  /*  public static com.hp.hpl.jena.rdf.model.Model convertSesameDataSetToJenaModel(
            org.openrdf.repository.Repository repository )
            throws org.openrdf.repository.RepositoryException{
        org.openrdf.repository.RepositoryConnection repositoryConnection = repository.getConnection();
        // finally, insert the DatasetGraph instance
        com.ontotext.jena.SesameDataset dataset = new SesameDataset(repositoryConnection);
        //From now on the SesameDataset object can be used through the Jena API
        //as regular Dataset, e.home. to add some data into it one could something like the
        //following:
        com.hp.hpl.jena.rdf.model.Model model =
                com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        return model;
    }*/


    /**
     * Internal model used to create instances of Jena API objects
     */
    private static final Model mInternalModel = ModelFactory.createDefaultModel();

    /**
     * Sesame value factory for creating instances of Sesame API objects
     */
    private static final org.openrdf.model.ValueFactory FACTORY =
            new org.openrdf.model.impl.ValueFactoryImpl();

    /**
     * Convert the given Jena Resource into a Sesame Resource
     * @param theRes the jena resource to convert
     * @return the jena resource as a sesame resource
     */
    public org.openrdf.model.Resource convertJenaResourceToOpenRDFResource(Resource theRes) {
        return asSesameResource(theRes);
    }

    /**
     * Convert the given Jena Resource into a Sesame Resource
     * @param theRes the jena resource to convert
     * @return the jena resource as a sesame resource
     */
    public static org.openrdf.model.Resource asSesameResource(Resource theRes) {
        if (theRes == null) return null;
        else if (theRes.canAs(Property.class))  return asSesameURI(theRes.as(Property.class));
        else return FACTORY.createBNode(theRes.getId().getLabelString());
    }

    /**
     * Convert the given Sesame Resource to a Jena Resource
     * @param theRes the sesame resource to convert
     * @return the sesame resource as a jena resource
     */
    public com.hp.hpl.jena.rdf.model.Resource convertOpenRDFResourceToJenaResource(
            org.openrdf.model.Resource theRes) {
        return asJenaResource(theRes);
    }

    /**
     * Convert the given Sesame Resource to a Jena Resource
     * @param theRes the sesame resource to convert
     * @return the sesame resource as a jena resource
     */
    public static com.hp.hpl.jena.rdf.model.Resource asJenaResource(org.openrdf.model.Resource theRes) {
        if (theRes == null) {
            return null;
        }
        else if (theRes instanceof org.openrdf.model.URI) {
            return asJenaURI( (org.openrdf.model.URI) theRes);
        }
        else {
            return mInternalModel.createResource(new AnonId(((org.openrdf.model.BNode) theRes).getID()));
        }
    }

    /**
     * Convert the given Jena Property instance to a Sesame URI instance
     * @param theProperty the Jena Property to convert
     * @return the Jena property as a Sesame Instance
     */
    public org.openrdf.model.URI convertJenaPropertyToOpenRDFURI(Property theProperty){
        return asSesameURI(theProperty);
    }

    /**
     * Convert the given Jena Property instance to a Sesame URI instance
     * @param theProperty the Jena Property to convert
     * @return the Jena property as a Sesame Instance
     */
    public static org.openrdf.model.URI asSesameURI(Property theProperty) {
        if (theProperty == null) {
            return null;
        }
        else {
            return FACTORY.createURI(theProperty.getURI());
        }
    }

    /**
     * Convert the given Jena Literal to a Sesame Literal
     * @param theLiteral the Jena Literal to convert
     * @return the Jena Literal as a Sesame Literal
     */
    public org.openrdf.model.Literal convertJenaLiteralToOpenRDFLiteral(Literal theLiteral) {
        return asSesameLiteral(theLiteral);
    }

    /**
     * Convert the given Jena Literal to a Sesame Literal
     * @param theLiteral the Jena Literal to convert
     * @return the Jena Literal as a Sesame Literal
     */
    public static org.openrdf.model.Literal asSesameLiteral(Literal theLiteral) {
        if (theLiteral == null) {
            return null;
        }
        else if (theLiteral.getLanguage() != null && !theLiteral.getLanguage().equals("")) {
            return FACTORY.createLiteral(theLiteral.getLexicalForm(),
                    theLiteral.getLanguage());
        }
        else if (theLiteral.getDatatypeURI() != null) {
            return FACTORY.createLiteral(theLiteral.getLexicalForm(),
                    FACTORY.createURI(theLiteral.getDatatypeURI()));
        }
        else {
            return FACTORY.createLiteral(theLiteral.getLexicalForm());
        }
    }

    /**
     * Convert the given Jena node as a Sesame Value
     * @param theNode the Jena node to convert
     * @return the jena node as a Sesame Value
     */
    public org.openrdf.model.Value convertJenaRDFNodeToOpenRDFValue(RDFNode theNode) {
        return asSesameValue(theNode);
    }

    /**
     * Convert the given Jena node as a Sesame Value
     * @param theNode the Jena node to convert
     * @return the jena node as a Sesame Value
     */
    public static org.openrdf.model.Value asSesameValue(RDFNode theNode) {
        if (theNode == null)return null;
        else if (theNode.canAs(Literal.class)) return asSesameLiteral(theNode.as(Literal.class));
        else return asSesameResource(theNode.as(Resource.class));
    }


    /**
     * Convert the sesame value to a Jena Node
     * @param theValue the Sesame value
     * @return the sesame value as a Jena node
     */
    public RDFNode convertOpenRDFValueToJenaRDFNode(org.openrdf.model.Value theValue) {
        return asJenaNode(theValue);
    }

    /**
     * Convert the sesame value to a Jena Node
     * @param theValue the Sesame value
     * @return the sesame value as a Jena RDFNode
     */
    public static RDFNode asJenaNode(org.openrdf.model.Value theValue) {
        if (theValue instanceof org.openrdf.model.Literal)return asJenaLiteral( (org.openrdf.model.Literal) theValue);
        else if (theValue instanceof org.openrdf.model.URI)return asJenaURI((org.openrdf.model.URI) theValue);
        else if (theValue instanceof org.openrdf.model.BNode)return asJenaBNode(theValue);
        else return asJenaResource((org.openrdf.model.Resource) theValue);
    }

    /**
     * Convert the sesame value to a Jena Blank Node
     * @param theValue the Sesame value
     * @return the sesame value as a Jena RDFNode
     */
    public static RDFNode asJenaBNode(org.openrdf.model.Value theValue) {
        return (RDFNode) NodeFactory.createAnon(new AnonId(theValue.stringValue()));
    }


    /**
     * Convert the Sesame URI to a Jena Property
     * @param theURI the sesame URI
     * @return the URI as a Jena property
     */
    public Property convertOpenRDFURIToJenaProperty(org.openrdf.model.URI theURI) {
        return asJenaURI(theURI);
    }

    /**
     * Convert the Sesame URI to a Jena Property
     * @param theURI the sesame URI
     * @return the URI as a Jena property
     */
    public static Property asJenaURI(org.openrdf.model.URI theURI) {
        if (theURI == null) return null;
        else return ResourceFactory.createProperty(theURI.toString());

    }

    /**
     * Convert a Sesame Literal to a Jena Literal
     * @param theLiteral the Sesame literal
     * @return the sesame literal converted to Jena
     */
    public com.hp.hpl.jena.rdf.model.Literal convertOpenRDFLiteralToJenaLiteral(org.openrdf.model.Literal theLiteral) {
        return asJenaLiteral(theLiteral);
    }

    /**
     * Convert a Sesame Literal to a Jena Literal
     * @param theLiteral the Sesame literal
     * @return the sesame literal converted to Jena
     */
    public static com.hp.hpl.jena.rdf.model.Literal asJenaLiteral(org.openrdf.model.Literal theLiteral) {
        if (theLiteral == null)  return null;
        else if (theLiteral.getLanguage() != null) {
            //return mInternalModel.createLiteral(theLiteral.getLabel(),theLiteral.getLanguage());
            return ResourceFactory.createLangLiteral(theLiteral.getLabel(),theLiteral.getLanguage());
        }
        else if (theLiteral.getDatatype() != null) {
            //return mInternalModel.createTypedLiteral(theLiteral.getLabel(),theLiteral.getDatatype().toString());
            RDFDatatype rdft = Jena2Kit.convertStringToRDFDatatype(theLiteral.getDatatype().toString());
            return ResourceFactory.createTypedLiteral(theLiteral.getLabel(),rdft);
        }
        else {
            //return mInternalModel.createLiteral(theLiteral.getLabel());
            return ResourceFactory.createPlainLiteral(theLiteral.getLabel());
        }
    }

    /**
     * Convert the Sesame Graph to a Jena Model
     * @param theGraph the Graph to convert
     * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
     */
    public Model convertOpenRDFGraphToJenaModel(org.openrdf.model.Graph theGraph) {
        return asJenaModel(theGraph);
    }

    /**
     * Convert the Sesame Graph to a Jena Model
     * @param theGraph the Graph to convert
     * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
     */
    public static Model asJenaModel(org.openrdf.model.Graph theGraph) {
        Model aModel = ModelFactory.createDefaultModel();
        for (final org.openrdf.model.Statement aStmt : theGraph) {
            aModel.add(asJenaStatement(aStmt));
        }
        return aModel;
    }

    /**
     * Convert the Jena Model to a Sesame Graph
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public org.openrdf.model.Graph convertJenaModelToOpenRDFGraph(Model theModel) {
        return asSesameGraph(theModel);
    }

    /**
     * Convert the Jena Model to a Sesame Graph
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public static org.openrdf.model.Graph asSesameGraph(Model theModel) {
        //org.openrdf.model.Graph aGraph = new org.openrdf.model.impl.GraphImpl();
        org.openrdf.model.Graph aGraph = new org.openrdf.model.impl.TreeModel();
        StmtIterator sIter = theModel.listStatements();
        while (sIter.hasNext()) {
            aGraph.add(asSesameStatement(sIter.nextStatement()));
        }
        sIter.close();
        return aGraph;
    }

    /**
     * Convert the Sesame Graph to a Jena Model
     * @param theGraph the Graph to convert
     * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
     */
    /*public Model convertOpenRDFGraphToJenaGraph(org.openrdf.model.Graph theGraph) {
        Graph gg = theGraph.
        return
    }*/


    /**
     * Convert the Jena Model to a Sesame Model
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public org.openrdf.model.Model convertJenaModelToOpenRDFModel(Model theModel) {
        return asSesameModel(theModel);
    }

    /**
     * Convert the Jena Model to a Sesame Model
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public static org.openrdf.model.Model asSesameModel(Model theModel) {
        org.openrdf.model.Model sesameModel = new org.openrdf.model.impl.TreeModel();
        StmtIterator sIter = theModel.listStatements();
        while (sIter.hasNext()) {
            sesameModel.add(asSesameStatement(sIter.nextStatement()));
        }
        sIter.close();
        return sesameModel;
    }

    /**
     * Convert a Jena Statement to a Sesame statement
     * @param theStatement the statement to convert
     * @return the equivalent Sesame statement
     */
    public org.openrdf.model.Statement convertJenaStatementToOpenRDFStatement(Statement theStatement) {
        return asSesameStatement(theStatement);
    }

    /**
     * Convert a Jena Statement to a Sesame statement
     * @param theStatement the statement to convert
     * @return the equivalent Sesame statement
     */
    public static  org.openrdf.model.Statement asSesameStatement(Statement theStatement) {
        return new org.openrdf.model.impl.StatementImpl(
                asSesameResource(theStatement.getSubject()),
                asSesameURI(theStatement.getPredicate()),
                asSesameValue(theStatement.getObject()));
    }

    /**
     * Convert a Sesame statement to a Jena statement
     * @param theStatement the statemnet to convert
     * @return the equivalent Jena statement
     */
    public Statement convertOpenRDFStatementToJenaStatement(org.openrdf.model.Statement theStatement) {
        return asJenaStatement(theStatement);
    }

    /**
     * Convert a Sesame statement to a Jena statement
     * @param theStatement the OpenRDF Statement to convert
     * @return the equivalent Jena statement
     */
    public static Statement asJenaStatement(org.openrdf.model.Statement theStatement) {
        /*return mInternalModel.createStatement(asJenaResource(theStatement.getSubject()),
                asJenaURI(theStatement.getPredicate()),
                asJenaNode(theStatement.getObject()));*/
        return ResourceFactory.createStatement(
                asJenaResource(theStatement.getSubject()),
                asJenaURI(theStatement.getPredicate()),
                asJenaNode(theStatement.getObject())
        );
    }

    //-----------------------------------------------
    //ADDED by 4535992.
    //-----------------------------------------------

    /**
     * Method to convert the OPenRDF Model to a Jena Model.
     * @param theModel the OpenRDF Model to convert.
     * @return the Jena Model converted.
     */
    public Model convertOpenRDFModelToJenaModel(org.openrdf.model.Model theModel){
        Model jenaModel = ModelFactory.createDefaultModel();
        for(org.openrdf.model.Statement stmt: theModel){
                Node node = NodeUtils.asNode(stmt.getObject().stringValue());
                RDFNode rdfNode;
                if(node.isURI()){
                    try {
                        rdfNode = ResourceFactory.createTypedLiteral(new URI(stmt.getObject().stringValue()));
                    }catch (URISyntaxException e) {
                        if(node.isLiteral()) rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());
                        else if(node.isBlank()) rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject());
                        else rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());
                    }
                }
                else if(node.isLiteral()) rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());
                else if(node.isBlank()) rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject());
                else rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());
                //RDFNode rdfNode = jenaModel.asRDFNode(node);
                Statement ss =  ResourceFactory.createStatement(
                        ResourceFactory.createResource(stmt.getSubject().toString()),
                        ResourceFactory.createProperty(stmt.getPredicate().getNamespace(),stmt.getPredicate().getLocalName()),
                        rdfNode);
                jenaModel.add(ss);

        }
        return jenaModel;
        //return asJenaModel(theModel);
    }

    /**
     * Method to convert the OPenRDF Model to a Jena Model.
     * @param theModel the OpenRDF Model to convert.
     * @return the Jena Model converted.
     */
   /* private Model asJenaModel(org.openrdf.model.Model theModel){
        Model jenaModel = ModelFactory.createDefaultModel();
        for(org.openrdf.model.Statement stmt: theModel){
            jenaModel.add(asJenaStatement(stmt));
        }
        return jenaModel;
    }*/

    /** converts vector to string for sesame */
  /*  public String convertVectorToSesameString(java.util.Vector <String> inputVector) {
        String subjects = "";
        for(int subIndex = 0;subIndex < inputVector.size();subIndex++)
            subjects = subjects + "(<" + inputVector.elementAt(subIndex) + ">)";

        return subjects;

    }*/

    /** converts vector to string for jena */
   /* public String convertVectorToJenaString(java.util.Vector <String> inputVector) {
        String subjects = "";
        for(int subIndex = 0;subIndex < inputVector.size();subIndex++)
            subjects = subjects + "<" + inputVector.elementAt(subIndex) + ">";
        return subjects;
    }*/



	/**
	 * An implementation of the Sesame ValueFactory interface which relaxes Sesame's opressive constraint that
	 * the URI *must* be a valid URI, ie something with a namespace & a local name.
	 */
	/*private static class LaxSesameValueFactory extends org.openrdf.model.impl.ValueFactoryImpl {
		*//**
		 * Creates a new Sesame URI object from the URI string, which can be just a local name, in which case the
		 * namespace of the URI object will be the empty string.
		 * @inheritDoc
		 *//*
		@Override
		public org.openrdf.model.URI createURI(String theURI) {
			try {
				return super.createURI(theURI);
			}
			catch (IllegalArgumentException e) {
				return new org.openrdf.model.impl.URIImpl(theURI);
			}
		}
	}*/

    /**
     * Method to convert a OpenRDF value to a Jena Nonde.
     * @param value the OpenRDF Value.
     * @return the Jena Node
     */
    public Node convertOpenRDFValueToJenaNode(org.openrdf.model.Value value){
        return asNode(value);
    }

    /**
     * Method to convert a OpenRDF value to a Jena Node.
     * @param value the OpenRDF Value.
     * @return the Jena Node
     */
    public static Node asNode(org.openrdf.model.Value value) {
        if ( value instanceof org.openrdf.model.Literal )return asNode((org.openrdf.model.Literal) value) ;
        if ( value instanceof org.openrdf.model.URI )return asNode((org.openrdf.model.URI)value) ;
        if ( value instanceof org.openrdf.model.BNode )return asNode((org.openrdf.model.BNode) value) ;
        throw new IllegalArgumentException("Not a concrete value") ;
    }

    /**
     * Method to convert a OpenRDF BNode to a Jena Node.
     * @param value the OpenRDF BNode.
     * @return the Jena Node.
     */
    public Node convertOpenRDFBNodeToJenaNode(org.openrdf.model.BNode value){
        return asNode(value);
    }

    /**
     * Method to convert a OpenRDF BNode to a Jena Node.
     * @param value the OpenRDF BNode.
     * @return the Jena Node.
     */
    public static Node asNode(org.openrdf.model.BNode value) {
        return NodeFactory.createAnon(new AnonId(value.getID())) ;
    }

    /**
     * Method to convert OpenRDF URI to a Jena Node.
     * @param value the OpenRDF URI.
     * @return the Jena Node.
     */
    public Node convertOpenRDFURIToJenaNode(org.openrdf.model.URI value){
        return asNode(value);
    }

    /**
     * Method to convert OpenRDF URI to a Jena Node.
     * @param value the OpenRDF URI.
     * @return the Jena Node.
     */
    public static Node asNode(org.openrdf.model.URI value) {
        return NodeFactory.createURI(value.stringValue()) ;
    }

    /**
     * Method to convert a OpenRDF Literal To Jena Node
     * @param value the OpenRDF Literal.
     * @return the Jena Node.
     */
    public Node convertOpenRDFLiteralToJenaNode(org.openrdf.model.Literal value){
        return asNode(value);
    }

    /**
     * Method to convert a OpenRDF Literal To Jena Node
     * @param value the OpenRDF Literal.
     * @return the Jena Node.
     */
    public static Node asNode(org.openrdf.model.Literal value) {
        if ( value.getLanguage() != null )
            return NodeFactory.createLiteral(value.getLabel(), value.getLanguage(), false);
        if ( value.getDatatype() != null )
            return NodeFactory.createLiteral(value.getLabel(),null, NodeFactory.getType(value.getDatatype().stringValue())) ;
        // Plain literal
        return NodeFactory.createLiteral(value.getLabel()) ;
    }

    /**
     * Method to convert a OpenRDF Statement to a Jena Triple.
     * @param stmt the OpenRDF statement.
     * @return the Jena Triple.
     */
    public Triple convertOpenRDFStatementToJenaTriple(org.openrdf.model.Statement stmt){
        return statementToTriple(stmt);
    }
    /**
     * Method to convert a OpenRDF Statement to a Jena Triple.
     * @param stmt the OpenRDF statement.
     * @return the Jena Triple.
     */
    public static Triple statementToTriple(org.openrdf.model.Statement stmt) {
        Node s = asNode(stmt.getSubject()) ;
        Node p = asNode(stmt.getPredicate()) ;
        Node o = asNode(stmt.getObject()) ;
        return new Triple(s,p,o) ;
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Value.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF Value.
     */
    public org.openrdf.model.Value convertJenaNodeToOpenRDFValue(org.openrdf.model.ValueFactory factory, Node node) {
        return asValue(factory, node);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Value.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF Value.
     */
    public static org.openrdf.model.Value asValue(org.openrdf.model.ValueFactory factory, Node node) {
        if ( node.isLiteral() )return asValueLiteral(factory, node) ;
        if ( node.isURI() ) return asURI(factory, node) ;
        if ( node.isBlank() )return asBNode(factory, node) ;
        throw new IllegalArgumentException("Not a concrete node") ;
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Resource.
     * @param factory the Jena Node.
     * @param node the OpenRDF ValueFactory.
     * @return the OpenRDF Resource..
     */
    public org.openrdf.model.Resource convertJenaNodeToOpenRDFResource(org.openrdf.model.ValueFactory factory, Node node) {
        return asResource(factory, node);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Resource.
     * @param factory the Jena Node.
     * @param node the OpenRDF ValueFactory.
     * @return the OpenRDF Resource..
     */
    public static org.openrdf.model.Resource asResource(org.openrdf.model.ValueFactory factory, Node node) {
        if ( node.isURI() ) return asURI(factory, node);
        if ( node.isBlank() )return asBNode(factory, node);
        throw new IllegalArgumentException("Not a URI nor a blank node") ;
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Blank Node.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF BNode.
     */
    public org.openrdf.model.BNode convertJenaNodeBlankToOpenRDFBNode(
            org.openrdf.model.ValueFactory factory, Node node) {
        return asBNode(factory, node);
    }


    /**
     * Method to convert a Jena Node to a OpenRDF Blank Node.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF BNode.
     */
    public static org.openrdf.model.BNode asBNode( org.openrdf.model.ValueFactory factory, Node node) {
        return factory.createBNode(node.getBlankNodeLabel()) ;
    }

    /**
     * Method to convert a Jena Node to a OpenRDF URI.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF URI.
     */
    public org.openrdf.model.URI convertJenaNodeURIToOpenRDFURI(
            org.openrdf.model.ValueFactory factory, Node node) {
        return asURI(factory, node);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF URI.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF URI.
     */
    public static org.openrdf.model.URI asURI(org.openrdf.model.ValueFactory factory, Node node) {
        return factory.createURI(node.getURI()) ;
    }

    /**
     * Method to convert a Jena Node Literal to a OpenRDF Value.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF Value.
     */
    public org.openrdf.model.Value convertJenaNodeLiteralToOpenRDFValue(
            org.openrdf.model.ValueFactory factory, Node node){
        return asValueLiteral(factory, node);

    }

    /**
     * Method to convert a Jena Node Literal to a OpenRDF Value.
     * @param factory the OpenRDF ValueFactory.
     * @param node the Jena Node.
     * @return the OpenRDF Value.
     */
    public static org.openrdf.model.Value asValueLiteral(
            org.openrdf.model.ValueFactory factory, Node node) {
        if ( node.getLiteralDatatype() != null ) {
            org.openrdf.model.URI uri = factory.createURI(node.getLiteralDatatypeURI()) ;
            return factory.createLiteral(node.getLiteralLexicalForm(), uri) ;
        }
        if ( ! node.getLiteralLanguage().equals("") ) {
            return factory.createLiteral(node.getLiteralLexicalForm(), node.getLiteralLanguage()) ;
        }
        return factory.createLiteral(node.getLiteralLexicalForm()) ;
    }

    /**
     * Method to convert a OpenRDF Repository to a Jena Model
     * @param repository the OpenRdf Repository.
     * @return the Jena Model.
     */
   public Model convertSesameRepositoryToJenaModel(
            org.openrdf.repository.Repository repository){
       Sesame28Kit sesame = Sesame28Kit.getInstance();
       return convertOpenRDFModelToJenaModel(sesame.convertRepositoryToModel(repository));
   }

    /** Create a dataset that is backed by a repository *//*
    public static Dataset createDataset(RepositoryConnection connection)
    {
        DatasetGraph dsg = new JenaSesameDatasetGraph(connection) ;
        return DatasetFactory.create(dsg) ;
    }*/




}
