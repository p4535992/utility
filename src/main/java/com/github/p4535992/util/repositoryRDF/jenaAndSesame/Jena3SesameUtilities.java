package com.github.p4535992.util.repositoryRDF.jenaAndSesame;

import com.github.p4535992.util.repositoryRDF.jena.Jena3Utilities;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame2Utilities;

/** if you use jena 2 */
/*import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.util.NodeUtils;*/

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by 4535992 on 10/06/2015.
 * href: https://github.com/mhgrove/Empire/blob/master/jena/main/src/com/clarkparsia/empire/jena/util/JenaSesameUtils.java
 * href: https://github.com/afs/JenaSesame/blob/master/src/org/openjena/jenasesame/impl/Convert.java
 * href: https://github.com/semoss/semoss/blob/master/src/prerna/util/JenaSesameUtils.java
 *
 * @author 4535992.
 * @version 2015-07-02.
 */
@SuppressWarnings("unused")
public class Jena3SesameUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Jena3SesameUtilities.class);
    /**
     * Internal model used to create instances of Jena API objects
     */
    private static final org.apache.jena.rdf.model.Model mInternalModel =
            org.apache.jena.rdf.model.ModelFactory.createDefaultModel();
    /**
     * Sesame value factory for creating instances of Sesame API objects
     */
    private static final org.openrdf.model.ValueFactory FACTORY =
            new org.openrdf.model.impl.ValueFactoryImpl();
    private static Jena3SesameUtilities instance = null;

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


    protected Jena3SesameUtilities() {
    }

    public static Jena3SesameUtilities getInstance() {
        if (instance == null) {
            instance = new Jena3SesameUtilities();
        }
        return instance;
    }

    /**
     * Convert the given Jena Resource into a Sesame Resource
     *
     * @param theRes the jena resource to convert
     * @return the jena resource as a sesame resource
     */
    public static org.openrdf.model.Resource asSesameResource(
            org.apache.jena.rdf.model.Resource theRes) {
        if (theRes == null) return null;
        else if (theRes.canAs(org.apache.jena.rdf.model.Property.class))
            return asSesameURI(theRes.as(org.apache.jena.rdf.model.Property.class));
        else
            return FACTORY.createBNode(theRes.getId().getLabelString());
    }

    /**
     * Convert the given Sesame Resource to a Jena Resource
     *
     * @param theRes the sesame resource to convert
     * @return the sesame resource as a jena resource
     */
    public static org.apache.jena.rdf.model.Resource asJenaResource(org.openrdf.model.Resource theRes) {
        if (theRes == null) {
            return null;
        } else if (theRes instanceof org.openrdf.model.URI) {
            return asJenaURI((org.openrdf.model.URI) theRes);
        } else {
            return mInternalModel.createResource(
                    new org.apache.jena.rdf.model.AnonId(((org.openrdf.model.BNode) theRes).getID()));
        }
    }

    /**
     * Convert the given Jena Property instance to a Sesame URI instance
     *
     * @param theProperty the Jena Property to convert
     * @return the Jena property as a Sesame Instance
     */
    public static org.openrdf.model.URI asSesameURI(
            org.apache.jena.rdf.model.Property theProperty) {
        if (theProperty == null) {
            return null;
        } else {
            return FACTORY.createURI(theProperty.getURI());
        }
    }

    /**
     * Convert the given Jena Literal to a Sesame Literal
     *
     * @param theLiteral the Jena Literal to convert
     * @return the Jena Literal as a Sesame Literal
     */
    public static org.openrdf.model.Literal asSesameLiteral(
            org.apache.jena.rdf.model.Literal theLiteral) {
        if (theLiteral == null) {
            return null;
        } else if (theLiteral.getLanguage() != null && !theLiteral.getLanguage().equals("")) {
            return FACTORY.createLiteral(theLiteral.getLexicalForm(),
                    theLiteral.getLanguage());
        } else if (theLiteral.getDatatypeURI() != null) {
            return FACTORY.createLiteral(theLiteral.getLexicalForm(),
                    FACTORY.createURI(theLiteral.getDatatypeURI()));
        } else {
            return FACTORY.createLiteral(theLiteral.getLexicalForm());
        }
    }

    /**
     * Convert the given Jena node as a Sesame Value
     *
     * @param theNode the Jena node to convert
     * @return the jena node as a Sesame Value
     */
    public static org.openrdf.model.Value asSesameValue(org.apache.jena.rdf.model.RDFNode theNode) {
        if (theNode == null) return null;
        else if (theNode.canAs(org.apache.jena.rdf.model.Literal.class))
            return asSesameLiteral(theNode.as(org.apache.jena.rdf.model.Literal.class));
        else
            return asSesameResource(theNode.as(org.apache.jena.rdf.model.Resource.class));
    }

    /**
     * Convert the sesame value to a Jena Node
     *
     * @param theValue the Sesame value
     * @return the sesame value as a Jena RDFNode
     */
    public static org.apache.jena.rdf.model.RDFNode asJenaNode(org.openrdf.model.Value theValue) {
        if (theValue instanceof org.openrdf.model.Literal) return asJenaLiteral((org.openrdf.model.Literal) theValue);
        else if (theValue instanceof org.openrdf.model.URI) return asJenaURI((org.openrdf.model.URI) theValue);
        else if (theValue instanceof org.openrdf.model.BNode) return asJenaBNode(theValue);
        else return asJenaResource((org.openrdf.model.Resource) theValue);
    }

    /**
     * Convert the sesame value to a Jena Blank Node
     *
     * @param theValue the Sesame value
     * @return the sesame value as a Jena RDFNode
     */
    public static org.apache.jena.rdf.model.RDFNode asJenaBNode(org.openrdf.model.Value theValue) {
        return (org.apache.jena.rdf.model.RDFNode)
                org.apache.jena.graph.NodeFactory.createBlankNode(
                        new org.apache.jena.graph.BlankNodeId(theValue.stringValue()));
    }

    /**
     * Convert the Sesame URI to a Jena Property
     *
     * @param theURI the sesame URI
     * @return the URI as a Jena property
     */
    public static org.apache.jena.rdf.model.Property asJenaURI(org.openrdf.model.URI theURI) {
        if (theURI == null) return null;
        else return org.apache.jena.rdf.model.ResourceFactory.createProperty(theURI.toString());

    }

    /**
     * Convert a Sesame Literal to a Jena Literal
     *
     * @param theLiteral the Sesame literal
     * @return the sesame literal converted to Jena
     */
    public static org.apache.jena.rdf.model.Literal asJenaLiteral(org.openrdf.model.Literal theLiteral) {
        if (theLiteral == null) return null;
        else if (theLiteral.getLanguage() != null) {
            //return mInternalModel.createLiteral(theLiteral.getLabel(),theLiteral.getLanguage());
            return org.apache.jena.rdf.model.ResourceFactory.createLangLiteral(theLiteral.getLabel(), theLiteral.getLanguage());
        } else if (theLiteral.getDatatype() != null) {
            //return mInternalModel.createTypedLiteral(theLiteral.getLabel(),theLiteral.getDatatype().toString());
            org.apache.jena.datatypes.RDFDatatype rdft = Jena3Utilities.toRDFDatatype(theLiteral.getDatatype().toString());
            return org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral(theLiteral.getLabel(), rdft);
        } else {
            //return mInternalModel.createLiteral(theLiteral.getLabel());
            return org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral(theLiteral.getLabel());
        }
    }

    /**
     * Convert the Sesame Graph to a Jena Model
     *
     * @param theGraph the Graph to convert
     * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
     */
    public static org.apache.jena.rdf.model.Model asJenaModel(org.openrdf.model.Graph theGraph) {
        org.apache.jena.rdf.model.Model aModel = org.apache.jena.rdf.model.ModelFactory.createDefaultModel();
        for (final org.openrdf.model.Statement aStmt : theGraph) {
            aModel.add(asJenaStatement(aStmt));
        }
        return aModel;
    }

    /**
     * Convert the Jena Model to a Sesame Graph
     *
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public static org.openrdf.model.Graph asSesameGraph(org.apache.jena.rdf.model.Model theModel) {
        //org.openrdf.model.Graph aGraph = new org.openrdf.model.impl.GraphImpl();
        org.openrdf.model.Graph aGraph = new org.openrdf.model.impl.TreeModel();
        org.apache.jena.rdf.model.StmtIterator sIter = theModel.listStatements();
        while (sIter.hasNext()) {
            aGraph.add(asSesameStatement(sIter.nextStatement()));
        }
        sIter.close();
        return aGraph;
    }

    /**
     * Convert the Jena Model to a Sesame Model
     *
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public static org.openrdf.model.Model asSesameModel(org.apache.jena.rdf.model.Model theModel) {
        org.openrdf.model.Model sesameModel = new org.openrdf.model.impl.TreeModel();
        org.apache.jena.rdf.model.StmtIterator sIter = theModel.listStatements();
        while (sIter.hasNext()) {
            sesameModel.add(asSesameStatement(sIter.nextStatement()));
        }
        sIter.close();
        return sesameModel;
    }

    /**
     * Convert a Jena Statement to a Sesame statement
     *
     * @param theStatement the statement to convert
     * @return the equivalent Sesame statement
     */
    public static org.openrdf.model.Statement asSesameStatement(
            org.apache.jena.rdf.model.Statement theStatement) {
        return new org.openrdf.model.impl.StatementImpl(
                asSesameResource(theStatement.getSubject()),
                asSesameURI(theStatement.getPredicate()),
                asSesameValue(theStatement.getObject()));
    }

    /**
     * Convert a Sesame statement to a Jena statement
     *
     * @param theStatement the OpenRDF Statement to convert
     * @return the equivalent Jena statement
     */
    public static org.apache.jena.rdf.model.Statement asJenaStatement(
            org.openrdf.model.Statement theStatement) {
        /*return mInternalModel.toStatement(asJenaResource(theStatement.getSubject()),
                asJenaURI(theStatement.getPredicate()),
                asJenaNode(theStatement.getObject()));*/
        return org.apache.jena.rdf.model.ResourceFactory.createStatement(
                asJenaResource(theStatement.getSubject()),
                asJenaURI(theStatement.getPredicate()),
                asJenaNode(theStatement.getObject())
        );
    }

    /**
     * Method to convert a OpenRDF value to a Jena Node.
     *
     * @param value the OpenRDF Value.
     * @return the Jena Node
     */
    public static org.apache.jena.graph.Node asNode(org.openrdf.model.Value value) {
        if (value instanceof org.openrdf.model.Literal) return asNode((org.openrdf.model.Literal) value);
        if (value instanceof org.openrdf.model.URI) return asNode((org.openrdf.model.URI) value);
        if (value instanceof org.openrdf.model.BNode) return asNode((org.openrdf.model.BNode) value);
        else {
            logger.error("Not a concrete value:" + value.stringValue());
            return null;
        }
    }

    /**
     * Method to convert a OpenRDF BNode to a Jena Node.
     *
     * @param value the OpenRDF BNode.
     * @return the Jena Node.
     */
    public static org.apache.jena.graph.Node asNode(org.openrdf.model.BNode value) {
        //NodeFactory.createAnon(new AnonId(value.toString()));
        return org.apache.jena.graph.NodeFactory.createBlankNode(
                new org.apache.jena.graph.BlankNodeId(value.toString()));
    }

    /**
     * Method to convert OpenRDF URI to a Jena Node.
     *
     * @param value the OpenRDF URI.
     * @return the Jena Node.
     */
    public static org.apache.jena.graph.Node asNode(org.openrdf.model.URI value) {
        return org.apache.jena.graph.NodeFactory.createURI(value.stringValue());
    }

    /**
     * Method to convert a OpenRDF Literal To Jena Node
     *
     * @param value the OpenRDF Literal.
     * @return the Jena Node.
     */
    public static org.apache.jena.graph.Node asNode(org.openrdf.model.Literal value) {
        if (value.getLanguage() != null)
            return org.apache.jena.graph.NodeFactory.createLiteral(value.getLabel(), value.getLanguage(), false);
        if (value.getDatatype() != null)
            return org.apache.jena.graph.NodeFactory.createLiteral(value.getLabel(), null, org.apache.jena.graph.NodeFactory.getType(value.getDatatype().stringValue()));
        // Plain literal
        return org.apache.jena.graph.NodeFactory.createLiteral(value.getLabel());
    }

    /**
     * Method to convert a OpenRDF Statement to a Jena Triple.
     *
     * @param stmt the OpenRDF statement.
     * @return the Jena Triple.
     */
    public static org.apache.jena.graph.Triple statementToTriple(org.openrdf.model.Statement stmt) {
        org.apache.jena.graph.Node s = asNode(stmt.getSubject());
        org.apache.jena.graph.Node p = asNode(stmt.getPredicate());
        org.apache.jena.graph.Node o = asNode(stmt.getObject());
        if (s != null && o != null) return new org.apache.jena.graph.Triple(s, p, o);
        else return null;
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Value.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF Value.
     */
    public static org.openrdf.model.Value asValue(org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        if (node.isLiteral()) return asValueLiteral(factory, node);
        if (node.isURI()) return asURI(factory, node);
        if (node.isBlank()) return asBNode(factory, node);
        else if (node.matches(org.apache.jena.graph.Node.ANY) || node.isVariable()) return null;
        else {
            logger.error("Not a concrete value:" + node.toString());
            return null;
        }
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Resource.
     *
     * @param factory the Jena Node.
     * @param node    the OpenRDF ValueFactory.
     * @return the OpenRDF Resource..
     */
    public static org.openrdf.model.Resource asResource(org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        if (node.isURI()) return asURI(factory, node);
        if (node.isBlank()) return asBNode(factory, node);
        throw new IllegalArgumentException("Not a URI nor a blank node");
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
     * Method to convert a Jena Node to a OpenRDF Blank Node.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF BNode.
     */
    public static org.openrdf.model.BNode asBNode(org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        // In Sesame Rio, the node can not begin with digits. But Jena blank node begins
        // with digits. We need to fix this.
        // A Sesame blank node looks like: node10jofjuktx143
        // A Jena blank node looks like: 1fe1feb:104f0e31293:-7ff9
        // We change it to: node1fe1febC104f0e31293CM7ff9
        // : becomes C, - becomes M
        // prefix should be "node". Because when Sesame reads triples from the file, it will change
        // the name of any blank node to its own format.
        String s = node.getBlankNodeId().toString();
        String prefix = "node";
        if (s.startsWith(prefix)) return factory.createBNode(s);
        else return factory.createBNode(prefix + s.replaceAll(":", "C").replaceAll("\\-", "M"));
        //return factory.createBNode(node.getBlankNodeLabel()) ;
    }

    /**
     * Method to convert a Jena Node to a OpenRDF URI.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF URI.
     */
    public static org.openrdf.model.URI asURI(org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        return factory.createURI(node.getURI());
    }

    /**
     * Method to convert a Jena Node Literal to a OpenRDF Value.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF Value.
     */
    public static org.openrdf.model.Value asValueLiteral(
            org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        if (node.getLiteralDatatype() != null) {
            org.openrdf.model.URI uri = factory.createURI(node.getLiteralDatatypeURI());
            return factory.createLiteral(node.getLiteralLexicalForm(), uri);
        }
        if (!node.getLiteralLanguage().equals("")) {
            return factory.createLiteral(node.getLiteralLexicalForm(), node.getLiteralLanguage());
        }
        return factory.createLiteral(node.getLiteralLexicalForm());
    }

    /**
     * Convert the given Jena Resource into a Sesame Resource
     *
     * @param theRes the jena resource to convert
     * @return the jena resource as a sesame resource
     */
    public org.openrdf.model.Resource convertJenaResourceToOpenRDFResource(org.apache.jena.rdf.model.Resource theRes) {
        return asSesameResource(theRes);
    }

    /**
     * Convert the given Sesame Resource to a Jena Resource
     *
     * @param theRes the sesame resource to convert
     * @return the sesame resource as a jena resource
     */
    public org.apache.jena.rdf.model.Resource convertOpenRDFResourceToJenaResource(
            org.openrdf.model.Resource theRes) {
        return asJenaResource(theRes);
    }

    /**
     * Convert the given Jena Property instance to a Sesame URI instance
     *
     * @param theProperty the Jena Property to convert
     * @return the Jena property as a Sesame Instance
     */
    public org.openrdf.model.URI convertJenaPropertyToOpenRDFURI(
            org.apache.jena.rdf.model.Property theProperty) {
        return asSesameURI(theProperty);
    }

    //-----------------------------------------------
    //ADDED by 4535992.
    //-----------------------------------------------

    /**
     * Convert the given Jena Literal to a Sesame Literal
     *
     * @param theLiteral the Jena Literal to convert
     * @return the Jena Literal as a Sesame Literal
     */
    public org.openrdf.model.Literal convertJenaLiteralToOpenRDFLiteral(
            org.apache.jena.rdf.model.Literal theLiteral) {
        return asSesameLiteral(theLiteral);
    }

    /**
     * Convert the given Jena node as a Sesame Value
     *
     * @param theNode the Jena node to convert
     * @return the jena node as a Sesame Value
     */
    public org.openrdf.model.Value convertJenaRDFNodeToOpenRDFValue(
            org.apache.jena.rdf.model.RDFNode theNode) {
        return asSesameValue(theNode);
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
     * Convert the sesame value to a Jena Node
     *
     * @param theValue the Sesame value
     * @return the sesame value as a Jena node
     */
    public org.apache.jena.rdf.model.RDFNode convertOpenRDFValueToJenaRDFNode(
            org.openrdf.model.Value theValue) {
        return asJenaNode(theValue);
    }

    /**
     * Convert the Sesame URI to a Jena Property
     *
     * @param theURI the sesame URI
     * @return the URI as a Jena property
     */
    public org.apache.jena.rdf.model.Property convertOpenRDFURIToJenaProperty(org.openrdf.model.URI theURI) {
        return asJenaURI(theURI);
    }

    /**
     * Convert a Sesame Literal to a Jena Literal
     *
     * @param theLiteral the Sesame literal
     * @return the sesame literal converted to Jena
     */
    public org.apache.jena.rdf.model.Literal convertOpenRDFLiteralToJenaLiteral(org.openrdf.model.Literal theLiteral) {
        return asJenaLiteral(theLiteral);
    }

    /**
     * Convert the Sesame Graph to a Jena Model
     *
     * @param theGraph the Graph to convert
     * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
     */
    public org.apache.jena.rdf.model.Model convertOpenRDFGraphToJenaModel(org.openrdf.model.Graph theGraph) {
        return asJenaModel(theGraph);
    }

    /**
     * Convert the Jena Model to a Sesame Graph
     *
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public org.openrdf.model.Graph convertJenaModelToOpenRDFGraph(org.apache.jena.rdf.model.Model theModel) {
        return asSesameGraph(theModel);
    }

    /**
     * Convert the Jena Model to a Sesame Model
     *
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public org.openrdf.model.Model convertJenaModelToOpenRDFModel(org.apache.jena.rdf.model.Model theModel) {
        return asSesameModel(theModel);
    }

    /**
     * Convert a Jena Statement to a Sesame statement
     *
     * @param theStatement the statement to convert
     * @return the equivalent Sesame statement
     */
    public org.openrdf.model.Statement convertJenaStatementToOpenRDFStatement(
            org.apache.jena.rdf.model.Statement theStatement) {
        return asSesameStatement(theStatement);
    }

    /**
     * Convert a Sesame statement to a Jena statement
     *
     * @param theStatement the statemnet to convert
     * @return the equivalent Jena statement
     */
    public org.apache.jena.rdf.model.Statement convertOpenRDFStatementToJenaStatement(org.openrdf.model.Statement theStatement) {
        return asJenaStatement(theStatement);
    }

    /**
     * Method to convert the OPenRDF Model to a Jena Model.
     *
     * @param theModel the OpenRDF Model to convert.
     * @return the Jena Model converted.
     */
    public org.apache.jena.rdf.model.Model convertOpenRDFModelToJenaModel(org.openrdf.model.Model theModel) {
        org.apache.jena.rdf.model.Model jenaModel =
                org.apache.jena.rdf.model.ModelFactory.createDefaultModel();
        try {
            for (org.openrdf.model.Statement stmt : theModel) {
                org.openrdf.model.Value value = stmt.getObject();
                org.apache.jena.graph.Node node =
                        org.apache.jena.sparql.util.NodeUtils.asNode(stmt.getObject().stringValue());
                org.apache.jena.rdf.model.RDFNode rdfNode;
                if (node.isURI()) {
                    try {
                        rdfNode =
                                org.apache.jena.rdf.model.ResourceFactory.
                                        createTypedLiteral(new URI(stmt.getObject().stringValue()));
                    } catch (URISyntaxException e) {
                       /* if (node.isLiteral())
                            rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());
                        else if (node.isBlank()) rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject());
                        else rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());*/
                        rdfNode = asRDfNode(value);
                    }
                }
               /* else if (node.isLiteral())
                    rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());
                else if (node.isBlank()) rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject());
                else rdfNode = ResourceFactory.createTypedLiteral(stmt.getObject().stringValue());*/
                else {
                    rdfNode = asRDfNode(value);
                }
                //RDFNode rdfNode = jenaModel.asRDFNode(node);
                org.apache.jena.rdf.model.Statement ss =
                        org.apache.jena.rdf.model.ResourceFactory.createStatement(
                                org.apache.jena.rdf.model.ResourceFactory.
                                        createResource(stmt.getSubject().toString()),
                                org.apache.jena.rdf.model.ResourceFactory.
                                        createProperty(
                                                stmt.getPredicate().getNamespace(), stmt.getPredicate().getLocalName()),
                                rdfNode);
                jenaModel.add(ss);
            }
            return jenaModel;
        } catch (java.lang.NullPointerException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private org.apache.jena.rdf.model.RDFNode asRDfNode(org.openrdf.model.Value objectValue) {
        org.apache.jena.graph.Node node =
                org.apache.jena.sparql.util.NodeUtils.asNode(objectValue.stringValue());
        org.apache.jena.rdf.model.RDFNode rdfNode;
        if (node.isLiteral())
            rdfNode = org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral(objectValue.stringValue());
        else if (node.isBlank()) rdfNode = org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral(objectValue);
        else rdfNode = org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral(objectValue.stringValue());
        return rdfNode;
    }

    /**
     * Method to convert a OpenRDF value to a Jena Nonde.
     *
     * @param value the OpenRDF Value.
     * @return the Jena Node
     */
    public org.apache.jena.graph.Node convertOpenRDFValueToJenaNode(org.openrdf.model.Value value) {
        return asNode(value);
    }

    /**
     * Method to convert a OpenRDF BNode to a Jena Node.
     *
     * @param value the OpenRDF BNode.
     * @return the Jena Node.
     */
    public org.apache.jena.graph.Node convertOpenRDFBNodeToJenaNode(org.openrdf.model.BNode value) {
        return asNode(value);
    }

    /**
     * Method to convert OpenRDF URI to a Jena Node.
     *
     * @param value the OpenRDF URI.
     * @return the Jena Node.
     */
    public org.apache.jena.graph.Node convertOpenRDFURIToJenaNode(org.openrdf.model.URI value) {
        return asNode(value);
    }

    /**
     * Method to convert a OpenRDF Literal To Jena Node
     *
     * @param value the OpenRDF Literal.
     * @return the Jena Node.
     */
    public org.apache.jena.graph.Node convertOpenRDFLiteralToJenaNode(org.openrdf.model.Literal value) {
        return asNode(value);
    }

    /**
     * Method to convert a OpenRDF Statement to a Jena Triple.
     *
     * @param stmt the OpenRDF statement.
     * @return the Jena Triple.
     */
    public org.apache.jena.graph.Triple convertOpenRDFStatementToJenaTriple(org.openrdf.model.Statement stmt) {
        return statementToTriple(stmt);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Value.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF Value.
     */
    public org.openrdf.model.Value convertJenaNodeToOpenRDFValue(org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        return asValue(factory, node);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Resource.
     *
     * @param factory the Jena Node.
     * @param node    the OpenRDF ValueFactory.
     * @return the OpenRDF Resource..
     */
    public org.openrdf.model.Resource convertJenaNodeToOpenRDFResource(org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        return asResource(factory, node);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF Blank Node.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF BNode.
     */
    public org.openrdf.model.BNode convertJenaNodeBlankToOpenRDFBNode(
            org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        return asBNode(factory, node);
    }

    /**
     * Method to convert a Jena Node to a OpenRDF URI.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF URI.
     */
    public org.openrdf.model.URI convertJenaNodeURIToOpenRDFURI(
            org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        return asURI(factory, node);
    }

    /**
     * Method to convert a Jena Node Literal to a OpenRDF Value.
     *
     * @param factory the OpenRDF ValueFactory.
     * @param node    the Jena Node.
     * @return the OpenRDF Value.
     */
    public org.openrdf.model.Value convertJenaNodeLiteralToOpenRDFValue(
            org.openrdf.model.ValueFactory factory, org.apache.jena.graph.Node node) {
        return asValueLiteral(factory, node);

    }

    /**
     * Method to convert a OpenRDF Repository to a Jena Model
     *
     * @param repository the OpenRdf Repository.
     * @return the Jena Model.
     */
    public org.apache.jena.rdf.model.Model convertSesameRepositoryToJenaModel(
            org.openrdf.repository.Repository repository) {
        Sesame2Utilities sesame = Sesame2Utilities.getInstance();
        return convertOpenRDFModelToJenaModel(sesame.convertRepositoryToModel(repository));
    }

    /** Create a dataset that is backed by a repository *//*
    public static Dataset createDataset(RepositoryConnection connection)
    {
        DatasetGraph dsg = new JenaSesameDatasetGraph(connection) ;
        return DatasetFactory.create(dsg) ;
    }*/



    /**
     * Created by 4535992 on 02/10/2015.
     *
     * @author 4535992.
     * @version 2015.10.02.
     *//*
    static class RepositoryResultIterator extends NiceIterator<Triple> {
        org.openrdf.repository.RepositoryResult<org.openrdf.model.Statement> iter;

        public RepositoryResultIterator(
                org.openrdf.repository.RepositoryResult<org.openrdf.model.Statement> iter1) {
            iter = iter1;
        }

        @Override
        public void close() {
            try {
                iter.close();
            } catch (org.openrdf.repository.RepositoryException ex) {
                throw new JenaException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            try {
                return iter.hasNext();
            } catch (org.openrdf.repository.RepositoryException ex) {
                throw new JenaException(ex);
            }
        }

        @Override
        public Triple next() {
            try {
                org.openrdf.model.Statement stmt = iter.next();
                return Jena2SesameUtilities.statementToTriple(stmt);
            } catch (org.openrdf.repository.RepositoryException ex) {
                throw new JenaException(ex);
            }
        }

        @Override
        public void remove() {
            try {
                iter.remove();
            } catch (org.openrdf.repository.RepositoryException ex) {
                throw new JenaException(ex);
            }
        }
    }*/



}
