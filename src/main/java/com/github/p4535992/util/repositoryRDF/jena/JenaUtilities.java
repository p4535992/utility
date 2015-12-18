package com.github.p4535992.util.repositoryRDF.jena;


import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.xml.XMLUtilities;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.resultset.RDFOutput;
import com.hp.hpl.jena.sparql.util.NodeUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class utility for Jena
 * Created by 4535992 in 2015-04-28.
 * @author 4535992.
 * @version 2015-12-07.
 */
@SuppressWarnings("unused")
public class JenaUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JenaUtilities.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    //CONSTRUCTOR
    protected JenaUtilities() {}

    private static JenaUtilities instance = null;

    public static JenaUtilities getInstance(){
        if(instance == null) {
            instance = new JenaUtilities();
        }
        return instance;
    }

    //PRIVATE
    public static String INFORMAT,OUTFORMAT;
    public static Lang OUTLANGFORMAT,INLANGFORMAT;
    public static RDFFormat OUTRDFFORMAT,INRDFFORMAT;

    public static void setInput(RDFFormat INRDFFORMAT){
        INFORMAT = INRDFFORMAT.getLang().getName();
        INLANGFORMAT = INRDFFORMAT.getLang();
    }

    public static void setOutput(RDFFormat OUTRDFFORMAT){
        OUTFORMAT = OUTRDFFORMAT.getLang().getName();
        OUTLANGFORMAT = OUTRDFFORMAT.getLang();
    }


    private static Model model;
    private static final Map<String,String> namespaces = new HashMap<>();
    public static final String RDF_FORMAT ="RDF/XML-ABBREV";

    /**
     * Method  to Write large model jena to file of text.
     * @param fullPath string of the path to the file.
     * @param model jena model to write.
     * @param outputFormat the output format you want to write the model.
     * @return if true all the operation are done.
     */
    public static boolean writeModelToFile(String fullPath, Model model, String outputFormat){
        fullPath =  FileUtilities.getPath(fullPath) + File.separator + FileUtilities.getFilenameWithoutExt(fullPath)+"."+outputFormat.toLowerCase();
        logger.info("Try to write the new file of triple from:" + fullPath + "...");
        OUTLANGFORMAT = createToRiotLang(outputFormat);
        OUTRDFFORMAT = createToRDFFormat(outputFormat);
        OUTFORMAT = outputFormat.toUpperCase();
        try {
            try (FileWriter out = new FileWriter(fullPath)) {
                model.write(out, OUTLANGFORMAT.getName());
            }
        }catch(Exception e1){
            logger.warn("...there is was a problem to try the write the triple file at the first tentative...");
            try {
                FileOutputStream outputStream = new FileOutputStream(fullPath);
                model.write(outputStream, OUTLANGFORMAT.getName());
            }catch(Exception e2){
                logger.warn("...there is was a problem to try the write the triple file at the second tentative...");
                try {
                    Writer writer = new FileWriter(new File(fullPath));
                    model.write(writer, OUTFORMAT);
                } catch (Exception e3) {
                    logger.warn("...there is was a problem to try the write the triple file at the third tentative...");
                    try {
                        Charset ENCODING = StandardCharsets.UTF_8;
                        FileUtilities.createFile(fullPath);
                        Path path = Paths.get(fullPath);
                        try (BufferedWriter writer = Files.newBufferedWriter(path,ENCODING)) {
                            model.write(writer, null, OUTLANGFORMAT.getName());
                        }
                    } catch(Exception e4) {
                        logger.error("... exception during the writing of the file of triples:" + fullPath);
                        logger.error(gm() + e4.getMessage(),e4);
                        return false;
                    }
                }
            }
        }
        logger.info("... the file of triple to:" + fullPath + " is been wrote!");
        return true;
    }

    public static boolean write(File file, Model model,String outputFormat){
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, createToRiotLang(outputFormat).getName());
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        }catch(FileNotFoundException e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    public static boolean write(File file, Model model,String outputFormat,String baseUri){
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, createToRiotLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        }catch(FileNotFoundException e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    public static boolean write(OutputStream stream, Model model,String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from stream ...");
            model.write(stream, createToRiotLang(outputFormat).getName());
            logger.info("... the file of triple to stream is been wrote!");
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    public static boolean write(OutputStream stream, Model model,String outputFormat,String baseUri) {
        try {
            logger.info("Try to write the new file of triple from stream ...");
            model.write(stream, createToRiotLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to stream is been wrote!");
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    public static boolean write(Writer writer, Model model,String outputFormat){
        try {
            logger.info("Try to write the new file of triple from write ...");
            model.write(writer, createToRiotLang(outputFormat).getName());
            logger.info("... the file of triple to write is been wrote!");
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    public static boolean write(Writer writer, Model model,String outputFormat,String baseUri) {
        try {
            logger.info("Try to write the new file of triple from write ...");
            model.write(writer, createToRiotLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to write is been wrote!");
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    public static void write(OutputStream s,Dataset d,String o){RDFDataMgr.write(s,d,createToRiotLang(o));}

    public static void write(OutputStream s,Dataset d,Lang l){RDFDataMgr.write(s,d,l);}

    public static void write(OutputStream s,Dataset d,RDFFormat f){ RDFDataMgr.write(s, d, f);}

    public static void write(OutputStream s,DatasetGraph d,Lang l){RDFDataMgr.write(s,d,l);}

    public static void write(OutputStream s,DatasetGraph d,RDFFormat f){RDFDataMgr.write(s,d,f);}

    public static void write(OutputStream s,Model m,Lang l){RDFDataMgr.write(s, m, l);}

    public static void write(OutputStream s,Model m,RDFFormat f){RDFDataMgr.write(s,m,f); }

    public static void write(OutputStream s,Graph g,Lang l){RDFDataMgr.write(s,g,l);}

    public static void write(OutputStream s,Graph g,RDFFormat f){RDFDataMgr.write(s,g,f);}

    public static void write(StringWriter s,Dataset d,String o){RDFDataMgr.write(s,d,createToRiotLang(o));}

    public static void write(StringWriter  s,Dataset d,Lang l){RDFDataMgr.write(s,d,l);}

    public static void write(StringWriter  s,Dataset d,RDFFormat f){ RDFDataMgr.write(s,d,f);}

    public static void write(StringWriter  s,DatasetGraph d,Lang l){RDFDataMgr.write(s,d,l);}

    public static void write(StringWriter  s,DatasetGraph d,RDFFormat f){RDFDataMgr.write(s,d,f);}

    public static void write(StringWriter  s,Model m,Lang l){RDFDataMgr.write(s, m, l);}

    public static void write(StringWriter  s,Model m,RDFFormat f){RDFDataMgr.write(s,m,f); }

    public static void write(StringWriter  s,Graph g,Lang l){RDFDataMgr.write(s,g,l);}

    public static void write(StringWriter  s,Graph g,RDFFormat f){RDFDataMgr.write(s,g,f);}

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param model Jena Model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlOnModel(String sparql,Model model) {
        Query query = QueryFactory.create(sparql);
        Model resultModel;
        if (query.isSelectType()) {
            ResultSet results;
            RDFOutput output = new RDFOutput();
            QueryExecution qexec = QueryExecutionFactory.create(sparql, model);
            results = qexec.execSelect();
            //... make exit from the thread the result of query
            results = ResultSetFactory.copyResults(results);
            logger.info(gm() +"Exec query SELECT SPARQL :"+sparql);
            return output.toModel(results);
        } else if (query.isConstructType()) {
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            resultModel = qexec.execConstruct();
            logger.info(gm() +"Exec query CONSTRUCT SPARQL :" + sparql);
            return resultModel;
        } else if (query.isDescribeType()) {
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            resultModel = qexec.execDescribe();
            logger.info(gm() +"Exec query DESCRIBE SPARQL :"+sparql);
            return resultModel;
        } else if (query.isAskType()) {
            logger.info(gm() +"Exec query ASK SPARQL :"+sparql);
            logger.warn(gm() +"ATTENTION the SPARQL query:" + sparql + ".\n is a ASK Query can't return a Model object");
            return null;
        } else if (query.isUnknownType()) {
            logger.info(gm() +"Exec query UNKNOWN SPARQL :"+sparql);
            logger.warn(gm() +"ATTENTION the SPARQL query:" + sparql + ".\n is a UNKNOWN Query can't return a Model object");
            return null;
        }else{
            return null;
        }
    }

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param dataset Jena Dataset.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlOnDataset(String sparql,Dataset dataset) {
        Query query = QueryFactory.create(sparql);
        Model resultModel;
        if (query.isSelectType()) {
            ResultSet results;
            RDFOutput output = new RDFOutput();
            QueryExecution qexec = QueryExecutionFactory.create(sparql, dataset);
            results = qexec.execSelect();
            //... make exit from the thread the result of query
            results = ResultSetFactory.copyResults(results);
            logger.info(gm() +"Exec query SELECT SPARQL :" + sparql);
            return output.toModel(results);
        } else if (query.isConstructType()) {
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            resultModel = qexec.execConstruct();
            logger.info(gm() +"Exec query CONSTRUCT SPARQL :" + sparql);
            return resultModel;
        } else if (query.isDescribeType()) {
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            resultModel = qexec.execDescribe();
            logger.info(gm() +"Exec query DESCRIBE SPARQL :" + sparql);
            return resultModel;
        } else if (query.isAskType()) {
            logger.info(gm() +"Exec query ASK SPARQL :"+sparql);
            logger.warn(gm() +"ATTENTION the SPARQL query:" + sparql + ".\n is a ASK Query can't return a Model object");
            return null;
        } else if (query.isUnknownType()) {
            logger.info(gm() +"Exec query UNKNOWN SPARQL :"+sparql);
            logger.warn(gm() +"ATTENTION the SPARQL query:" + sparql + ".\n is a UNKNOWN Query can't return a Model object");
            return null;
        }else return null;
    }

    public static Model execSparqlOnRemote(String sparql,String remoteService){
        /*HttpAuthenticator authenticator = new PreemptiveBasicAuthenticator(
                new ScopedAuthenticator(new URI(SPARQLR_ENDPOINT), SPARQLR_USERNAME, SPARQLR_PASSWORD.toCharArray())
        );*/
        Query query = QueryFactory.create(sparql);
        Model resultModel;
        if (query.isSelectType()) {
            ResultSet results;
            RDFOutput output = new RDFOutput();
            QueryExecution qexec = QueryExecutionFactory.sparqlService(remoteService,query);
            //QueryEngineHTTP qexec = new QueryEngineHTTP(remoteService, sparql);
            //qexec.setBasicAuthentication("siimobility", "siimobility".toCharArray());
            results = qexec.execSelect();
            //... make exit from the thread the result of query
            results = ResultSetFactory.copyResults(results);
            logger.info(gm() + "Exec query SELECT SPARQL :" + sparql);
            return output.toModel(results);
        } else if (query.isConstructType()) {
            QueryExecution qexec = QueryExecutionFactory.sparqlService(remoteService,query);
            resultModel = qexec.execConstruct();
            logger.info(gm() + "Exec query CONSTRUCT SPARQL :" + sparql);
            return resultModel;
        } else if (query.isDescribeType()) {
            QueryExecution qexec = QueryExecutionFactory.sparqlService(remoteService,query);
            resultModel = qexec.execDescribe();
            logger.info(gm() + "Exec query DESCRIBE SPARQL :" + sparql);
            return resultModel;
        } else if (query.isAskType()) {
            logger.info(gm() +"Exec query ASK SPARQL :"+sparql);
            logger.warn(gm() +"ATTENTION the SPARQL query:" + sparql + ".\n is a ASK Query can't return a Model object");
            return null;
        } else if (query.isUnknownType()) {
            logger.info(gm() +"Exec query UNKNOWN SPARQL :" + sparql);
            logger.warn(gm() +"ATTENTION the SPARQL query:" + sparql + ".\n is a UNKNOWN Query can't return a Model object");
            return null;
        }else return null;

    }

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param model jena model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlConstructorOnModel(String sparql,Model model) {
        Query query = QueryFactory.create(sparql) ;
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        resultModel = qexec.execConstruct();
        logger.info(gm() +"Exec query CONSTRUCT SPARQL :" + sparql);
        return  resultModel;
    }

    /**
     * Method for execute a DESCRIIBE SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param model jena model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlDescribeOnModel(String sparql,Model model) {
        Query query = QueryFactory.create(sparql) ;
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        resultModel = qexec.execDescribe();
        logger.info(gm() + "Exec query DESCRIBE SPARQL :" + sparql);
        return resultModel;
    }

    /**
     * Method for execute a SELECT SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param model jena model.
     * @return the result set of the query.
     */
    public static ResultSet execSparqlSelectOnModel(String sparql,Model model) {
        ResultSet results;
        QueryExecution qexec = QueryExecutionFactory.create(sparql, model);
        results = qexec.execSelect();
        //... make exit from the thread the result of query
        results = ResultSetFactory.copyResults(results) ;
        logger.info(gm() + "Exec query SELECT SPARQL :" + sparql);
        return results;
    }

    /**
     * Method for execute a SELECT SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param model jena model.
     * @return the result set of the query.
     */
    public static Model execSparqlSelectOnModel2(String sparql,Model model) {
        RDFOutput output = new RDFOutput();
        return output.toModel(execSparqlSelectOnModel(sparql,model));
    }

     /**
     * Method for execute a ASK SPARQL on a Jena Model.
     * @param sparql sparql query.
     * @param model jena model.
     * @return the result set of the query like a boolean value.
     */
    public static boolean execSparqlAskOnModel(String sparql,Model model) {
        Query query = QueryFactory.create(sparql) ;
        boolean result ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        //try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            result = qexec.execAsk();
            logger.info(gm() + "Exec query ASK SPARQL :" + sparql);
        //}
        return result;
    }

    /**
     * Metodo per il caricamento di un file di triple in un'oggetto model di JENA.
     * @param filename name of the file of input.
     * @param filepath path to the file of input wihtout the name.
     * @param inputFormat format of the file in input.
     * @return the jena model of the file.
     * @throws FileNotFoundException thriow if any "File Not Found" error is occurred.
     */
    public static Model loadFileTripleToModel(String filename,String filepath,String inputFormat) throws FileNotFoundException {
        Model m = ModelFactory.createDefaultModel();
        INLANGFORMAT = createToRiotLang(inputFormat);
        INRDFFORMAT = createToRDFFormat(inputFormat);
        INFORMAT = INLANGFORMAT.getLabel().toUpperCase();
        // use the FileManager to find the input file
        File fileInput = new File(filepath +File.separator+ filename + "." + inputFormat);
        InputStream in;
        try { in = com.hp.hpl.jena.util.FileManager.get().open(fileInput.getAbsolutePath());
        }catch(Exception e){in = new FileInputStream(fileInput); }

        if (in == null || ! fileInput.exists()) throw new IllegalArgumentException( "File: " +  fileInput + " not found");

        logger.info("Try to read file of triples from the path:" + fileInput.getAbsolutePath() + "...");
        try {
            com.hp.hpl.jena.util.FileManager.get().addLocatorClassLoader(JenaUtilities.class.getClassLoader());
            m = com.hp.hpl.jena.util.FileManager.get().loadModel(fileInput.toURI().toString(),null,INFORMAT);
        }catch(Exception e){
            try {
                m.read(in, null, INFORMAT);
            } catch (Exception e1) {
                try {
                    RDFDataMgr.read(m, in, INLANGFORMAT);
                } catch (Exception e2) {
                    try {
                        //If you are just opening the stream from a file (or URL) then Apache Jena
                        RDFDataMgr.read(m,fileInput.toURI().toString());
                    } catch (Exception e3) {
                        logger.error(gm() + "Failed read the file of triples from the path:" +
                                fileInput.getAbsolutePath() + ":" + e.getMessage(), e);
                    }
                }
            }
        }
        logger.info("...file of triples from the path:" + fileInput.getAbsolutePath() + " readed!!");
        return m;
    }
    /**
     * Method for load a file of tuples to a jena model.
     * @param file a input file.
     * @return the jena model of the file.
     * @throws FileNotFoundException thriow if any "File Not Found" error is occurred.
     */
     public static Model loadFileTripleToModel(File file) throws FileNotFoundException {
         String filename = FileUtilities.getFilenameWithoutExt(file);
         String filepath = FileUtilities.getPath(file);
         String inputFormat = FileUtilities.getExtension(file);
         return loadFileTripleToModel(filename,filepath,inputFormat);
     }

    /**
     * A list of org.apache.jena.riot.Lang file formats.
     * return all the language Lang supported from jena.
     * exception : "AWT-EventQueue-0" java.lang.NoSuchFieldError: RDFTHRIFT  or CSV.
     */
 	private static final Lang allFormatsOfRiotLang[] = new Lang[] {
            Lang.NTRIPLES, Lang.N3, Lang.RDFXML,
            Lang.TURTLE, Lang.TRIG, Lang.TTL,
            Lang.NQUADS ,
            Lang.NQ,
 		    //org.apache.jena.riot.Lang.JSONLD,
            Lang.NT, Lang.RDFJSON,
 		    Lang.RDFNULL
            //org.apache.jena.riot.Lang.CSV,
            //org.apache.jena.riot.Lang.RDFTHRIFT
        };

    /**
     * A list of org.apache.jena.riot.RDFFormat file formats used in jena.
     * if you are not using the last version of jena you can found in build:
     * "AWT-EventQueue-0" java.lang.NoSuchFieldError: JSONLD_FLAT
     */
 	private static final RDFFormat allFormatsOfRDFFormat[] = new RDFFormat[] {
        RDFFormat.TURTLE, RDFFormat.TTL,
        //RDFFormat.JSONLD_FLAT,
        //RDFFormat.JSONLD_PRETTY,
        //RDFFormat.JSONLD,
        RDFFormat.RDFJSON,RDFFormat.RDFNULL,RDFFormat.NQUADS,RDFFormat.NQ,
       // RDFFormat.NQUADS_ASCII,RDFFormat.NQUADS_UTF8,
            RDFFormat.NT,RDFFormat.NTRIPLES,
       // RDFFormat.NTRIPLES_ASCII,RDFFormat.NTRIPLES_UTF8,
            RDFFormat.RDFXML,RDFFormat.RDFXML_ABBREV,
        RDFFormat.RDFXML_PLAIN,RDFFormat.RDFXML_PRETTY,RDFFormat.TRIG,RDFFormat.TRIG_BLOCKS,
        RDFFormat.TRIG_FLAT,RDFFormat.TRIG_PRETTY,RDFFormat.TURTLE_BLOCKS,RDFFormat.TURTLE_FLAT,
        RDFFormat.TURTLE_PRETTY};
        //org.apache.jena.riot.RDFFormat.RDF_THRIFT,org.apache.jena.riot.RDFFormat.RDF_THRIFT_VALUES,

    /**
     * A list of com.hp.hpl.jena.datatypes.RDFDatatype file formats used in jena.
     * @return all the RDFFormat supported from jena.
     */
   /* private static final RDFFormat allFormatsOfRDFFormat[] = new RDFFormat[] {
            RDFDatatype.
    };*/

    /**
     * A list of com.hp.hpl.jena.datatypes.xsd.XSDDatatype.
     * return all the com.hp.hpl.jena.datatypes.RDFDatatype supported from jena.
     * @param uri the String of the uri resource.
     * @return  the RDFDatatype of the uri resource.
     */
    public static RDFDatatype convertStringToRDFDatatype(String uri){
        return createToXSDDatatype(uri);
    }


    /*public static com.hp.hpl.jena.datatypes.RDFDatatype convertXSDDatatypeToRDFDatatype(XSDDatatype xsdD){
        return xsdD;
    }

    public static XSSimpleType convertStringToXssSimpleType(String nameDatatype){
        SymbolHash fBuiltInTypes = new SymbolHash();
        return (XSSimpleType)fBuiltInTypes.get(nameDatatype);
    }

    public static XSDDatatype convertStringToXSDDatatype(String nameDatatype){
        XSSimpleType xss = convertStringToXssSimpleType(nameDatatype);
        return new XSDDatatype(xss,xss.getNamespace());
    }*/

    /**
     * A list of org.apache.xerces.impl.dv.XSSimpleType.
     * return all the XSSimpleType supported from jena.
     */
   /* private static final short[] allFormatOfXSSimpleType = new short[]{
            XSSimpleType.PRIMITIVE_ANYURI,XSSimpleType.PRIMITIVE_BASE64BINARY,XSSimpleType.PRIMITIVE_BOOLEAN,
            XSSimpleType.PRIMITIVE_DATE,XSSimpleType.PRIMITIVE_DATETIME,XSSimpleType.PRIMITIVE_DECIMAL,XSSimpleType.PRIMITIVE_DOUBLE,
            XSSimpleType.PRIMITIVE_DURATION,XSSimpleType.PRIMITIVE_FLOAT,XSSimpleType.PRIMITIVE_GDAY,XSSimpleType.PRIMITIVE_GMONTH,
            XSSimpleType.PRIMITIVE_GMONTHDAY,XSSimpleType.PRIMITIVE_GYEAR,XSSimpleType.PRIMITIVE_GYEARMONTH,
            XSSimpleType.PRIMITIVE_HEXBINARY,XSSimpleType.PRIMITIVE_NOTATION,XSSimpleType.PRIMITIVE_PRECISIONDECIMAL,
            XSSimpleType.PRIMITIVE_QNAME,XSSimpleType.PRIMITIVE_STRING,XSSimpleType.PRIMITIVE_TIME,XSSimpleType.WS_COLLAPSE,
            XSSimpleType.WS_PRESERVE,XSSimpleType.WS_REPLACE
    };*/

    /**
     * A list of com.hp.hpl.jena.datatypes.xsd.XSDDatatype.
     * return all the XSDDatatype supported from jena.
     */
    public static final XSDDatatype allFormatsOfXSDDataTypes[] = new XSDDatatype[]{
            XSDDatatype.XSDstring,XSDDatatype.XSDENTITY,XSDDatatype.XSDID,XSDDatatype.XSDIDREF,
            XSDDatatype.XSDanyURI,XSDDatatype.XSDbase64Binary,XSDDatatype.XSDboolean,XSDDatatype.XSDbyte,
            XSDDatatype.XSDdate,XSDDatatype.XSDdateTime,XSDDatatype.XSDdecimal,XSDDatatype.XSDdouble,
            XSDDatatype.XSDduration,XSDDatatype.XSDfloat,XSDDatatype.XSDgDay,XSDDatatype.XSDgMonth,
            XSDDatatype.XSDgMonthDay,XSDDatatype.XSDgYear,XSDDatatype.XSDgYearMonth,XSDDatatype.XSDhexBinary,
            XSDDatatype.XSDint,XSDDatatype.XSDinteger,XSDDatatype.XSDlanguage,XSDDatatype.XSDlong,
            XSDDatatype.XSDName,XSDDatatype.XSDNCName,XSDDatatype.XSDnegativeInteger,XSDDatatype.XSDNMTOKEN,
            XSDDatatype.XSDnonNegativeInteger,XSDDatatype.XSDnonPositiveInteger,XSDDatatype.XSDnormalizedString,
            XSDDatatype.XSDNOTATION,XSDDatatype.XSDpositiveInteger,XSDDatatype.XSDQName,XSDDatatype.XSDshort,
            XSDDatatype.XSDtime,XSDDatatype.XSDtoken,XSDDatatype.XSDunsignedByte,XSDDatatype.XSDunsignedInt,
            XSDDatatype.XSDunsignedLong,XSDDatatype.XSDunsignedShort
    };

    /**
     * Method convert a XSDDatatype to a string.
     * @param xsdDatatype XSDDatatype of input.
     * @return string uri of the XSDDatatype.
     */
    public static String XSDDatatypeToString(XSDDatatype xsdDatatype) {
        return xsdDatatype.getURI();
 	}

    /**
     * Method convert a string to XSDDatatype.
     * @param uri string uri of the XSDDatatype.
     * @return xsdDatatype of the string uri if exists.
     */
    public static XSDDatatype createToXSDDatatype(String uri) {
        for (XSDDatatype xsdDatatype : allFormatsOfXSDDataTypes) {
               if(xsdDatatype.getURI().equalsIgnoreCase(XSDDatatype.XSD+"#"+uri)) return xsdDatatype;
            if(xsdDatatype.getURI().replace(XSDDatatype.XSD,"")
                    .toLowerCase().contains(uri.toLowerCase())) return xsdDatatype;
        }
        logger.error(gm() + "The XSD Datatype '" + uri + "' is not recognised");
        throw new IllegalArgumentException("The XSD Datatype '" + uri + "' is not recognised");
 	}

    /**
     * Method convert a string to a rdfformat.
     * @param strFormat string name of the RDFFormat.
     * @return rdfformat the RDFFormat with the same name.
     */
    public static RDFFormat createToRDFFormat(String strFormat) {
        if(strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES")|| strFormat.toUpperCase().contains("N3")){
            strFormat="N-Triples";
        }
        if(strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")){
            strFormat="Turtle";
        }
        //Collection<RDFFormat> allFormatsOfRDFFormat = RDFWriterRegistry.registered();
        for(RDFFormat rdfFormat : allFormatsOfRDFFormat) {
                if (rdfFormat.getLang().getName().equalsIgnoreCase(strFormat))
                        return rdfFormat;
        }
        logger.error(gm() + "The RDF format '" + strFormat + "' is not recognised");
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
 	}

    /**
     * Method convert a string name of a RDFFormat to a language Lang.
     * @param strFormat string name of a RDFFormat.
     * @return lang the language Lang for the same name.
     */
 	public static Lang createToRiotLang(String strFormat) {
            if(strFormat.toUpperCase().contains("NT") ||
                    strFormat.toUpperCase().contains("NTRIPLES")|| strFormat.toUpperCase().contains("N3")){
                 strFormat="N-Triples";
             }
            if(strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")){
                 strFormat="Turtle";
             }
            for (Lang lang : allFormatsOfRiotLang) {
                String label = lang.getLabel();
                String name = lang.getName();

                if (lang.getName().equalsIgnoreCase(strFormat))
                      return lang;
 		}
        logger.error(gm() + "The LANG format '" + strFormat + "' is not recognised");
 		throw new IllegalArgumentException("The LANG format '" + strFormat + "' is not recognised");
 	}


    /**
     * Method to print the resultSet to a a specific format of output.
     * @param sparql sparql query.
     * @param model jena model.
     * @param fullPathOutputFile string to the path of the output file.
     * @param outputFormat stirng of the output format.
     */
    private static void formatTheResultSetAndPrint(
            String sparql,Model model,String fullPathOutputFile,String outputFormat){
        try {
            //JSON,CSV,TSV,,RDF,SSE,XML
            ResultSet results;
            if (outputFormat.toLowerCase().contains("csv") || outputFormat.toLowerCase().contains("xml")
                    || outputFormat.toLowerCase().contains("json") || outputFormat.toLowerCase().contains("tsv")
                    || outputFormat.toLowerCase().contains("sse") || outputFormat.toLowerCase().contains("bio")
                    || outputFormat.toLowerCase().contains("rdf") || outputFormat.toLowerCase().contains("bio")
                    ) {
//               try (com.hp.hpl.jena.query.QueryExecution qexec2 =
//                          com.hp.hpl.jena.query.QueryExecutionFactory.insert(sparql, model)) {
//                      results = qexec2.execSelect() ;
//               }
                results = execSparqlSelectOnModel(sparql, model);
                //PRINT THE RESULT
                logger.info("Try to write the new file of triple to:" + fullPathOutputFile + "...");
                FileOutputStream fos = new FileOutputStream(new File(fullPathOutputFile));
                if (outputFormat.toLowerCase().contains("csv")) {
                    ResultSetFormatter.outputAsCSV(fos, results);
                } else if (outputFormat.toLowerCase().contains("xml")) {
                    ResultSetFormatter.outputAsXML(fos, results);
                } else if (outputFormat.toLowerCase().contains("json")) {
                    ResultSetFormatter.outputAsJSON(fos, results);
                } else if (outputFormat.toLowerCase().contains("tsv")) {
                    ResultSetFormatter.outputAsTSV(fos, results);
                } else if (outputFormat.toLowerCase().contains("sse")) {
                    ResultSetFormatter.outputAsSSE(fos, results);
                } else if (outputFormat.toLowerCase().contains("bio")) {
                    ResultSetFormatter.outputAsBIO(fos, results);
                }else if(outputFormat.toLowerCase().contains("rdf")){
                    ResultSetFormatter.outputAsRDF(fos, "RDF/XML", results);
                }
                logger.info("... the file of triple to:" + fullPathOutputFile + " is been wrote!");
            } else if (outputFormat.toLowerCase().contains("ttl")) {
                Model resultModel = execSparqlConstructorOnModel(sparql, model);
                OUTLANGFORMAT = createToRiotLang(outputFormat);
                OUTRDFFORMAT = createToRDFFormat(outputFormat);
                OUTFORMAT = outputFormat.toUpperCase();
                //Writer writer = new FileWriter(new File(fullPathOutputFile));
                //model.write(writer, outputFormat);
                writeModelToFile(fullPathOutputFile, resultModel, OUTFORMAT);
                logger.info("... the file of triple to:" + fullPathOutputFile + " is been wrote!");
            }
        }catch(Exception e){
            logger.error(gm() + "error during the writing of the file of triples:" + fullPathOutputFile + ":" + e.getMessage(), e);
        }

    }

    /**
     * Method to convert a RDF fikle of triples to a another specific format.
     * @param file file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    public static void convertFileTripleToAnotherFormat(File file, String outputFormat) throws IOException {
        convertTo(file,outputFormat);
    }

    /**
     * Method to convert a RDF fikle of triples to a another specific format.
     * @param file file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    private static void convertTo(File file, String outputFormat) throws IOException{
         Model m = loadFileTripleToModel(file);
         String newName = FileUtilities.getFilenameWithoutExt(file)+"."+outputFormat.toLowerCase();
         String newPath = FileUtilities.getPath(file);
         String sparql;
        if(outputFormat.toLowerCase().contains("csv")||outputFormat.toLowerCase().contains("xml")
             ||outputFormat.toLowerCase().contains("json")||outputFormat.toLowerCase().contains("tsv")
             ||outputFormat.toLowerCase().contains("sse")||outputFormat.toLowerCase().contains("bio")
             ||outputFormat.toLowerCase().contains("rdf")||outputFormat.toLowerCase().contains("bio")
           ){
             sparql ="SELECT * WHERE{?s ?p ?o}";}
        else{
             sparql ="CONSTRUCT {?s ?p ?o} WHERE{?s ?p ?o}";}
        formatTheResultSetAndPrint(sparql,m,newPath+File.separator+newName,outputFormat.toLowerCase()  );
    }

	/*
		There are                               To go in the reverse direction:
		    Resource.asNode() -> Node               Model.asRDFNode(Node)
		    Literal.asNode() -> Node                Model.asStatement(Triple)
        Statement.asTriple() -> Triple              ModelFactory.createModelForGraph(Graph)
		    Model.getGraph() -> Graph

		If you do an ARQ query, you can get the Node-level result ResultSet.nextBinding()
		which maps Var to Node. Var is ARQ's extension of Node_Variable. Create with Var.alloc(...)
		You don't need to cast to Node_URI (it's an implementation class really) - at the SPI, there
		are "Nodes" as generic items (and you can insert Triples that aren't RDF like ones with variables).*/
   /*
    * Get the dc:relation property
    * @return dc:relation property
    */
    /*
   public List<Enhancement> getRelation(){
     if (relation == null && resource.hasProperty(DCTerms.relation)) {
       relation=new ArrayList<Enhancement>();
       final StmtIterator relationsIterator=resource.listProperties(DCTerms.relation);
       while (relationsIterator.hasNext()) {
         final Statement relationStatement=relationsIterator.next();
         relation.add(EnhancementParser.parse(relationStatement.getObject().asResource()));
       }
     }
     return relation;
   }
   */

  //-----------------------------------------------------------------------------------------------------------------

    /**
     * Method to find if exists some statement with a specific property.
     * @param model jena model.
     * @param subject subject of the statement you want to check.
     * @param property string of property of the statement you want to check.
     * @return boolean result if exists or not.
     */
    public static boolean findProperty(Model model,Resource subject,String property){
        boolean foundLocal = false;
        try {
            int pos = property.indexOf(":");
            String prefix = property.substring(0, pos);
            property = property.substring(pos + 1);
            String uri =  namespaces.get(prefix);
            Property p = null;
            if (!"".equals(property)) {
                p = model.createProperty(uri, property);
            }
            StmtIterator iter =model.listStatements(
                            new SelectorImpl( subject, p,(RDFNode) null));
            while (iter.hasNext() && !foundLocal) {
                Statement stmt = iter.next();
                Property sp = stmt.getPredicate();

                if (uri.equals(sp.getNameSpace())
                        && ("".equals(property)
                        || sp.getLocalName().equals(
                        property))) {
                    foundLocal = true;
                }
            }
        } catch (Exception e) {
            logger.warn(gm() + "Exception while try to find a property:"+e.getMessage(),e);
        }
        return foundLocal;
    }

    /**
     * Method to copy a Model to another Model with different uri and specific reources.
     * @param model jena model for the copy.
     * @param subject the resoures you want to copy.
     * @param uri the uri for the new subject copied new model.
     * @return the copied model.
     */
    public static Model copyModel(Model model,Resource subject,String uri) {
        try {
            Model newModel = ModelFactory.createDefaultModel();
            Resource newSubject = newModel.createResource(uri);
            // Copy prefix mappings to the new model...
            newModel.setNsPrefixes(model.getNsPrefixMap());
            newModel = copyToModel(model, subject, newModel, newSubject);
            return newModel;
        } catch (Exception e) {
            logger.error(gm()+ e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to copy a Model to another Model.
     * @param srcModel model for the copy.
     * @param srcRsrc resource of the model for the copy.
     * @param destModel model copied.
     * @param destRsrc resource of the model copied.
     * @return the copied model.
     */
    public static Model copyToModel(Model srcModel,Resource srcRsrc,Model destModel,Resource destRsrc) {
        try {
            StmtIterator iter = srcModel.listStatements(
                    new SelectorImpl(srcRsrc,null,(RDFNode) null));
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                RDFNode obj = stmt.getObject();
                if (obj instanceof Resource) {
                    Resource robj = (Resource) obj;
                    if (robj.isAnon()) {
                        Resource destSubResource = destModel.createResource();
                        destModel = copyToModel(srcModel, robj, destModel, destSubResource);
                        obj = destSubResource;
                    }
                }
                Statement newStmt =destModel.createStatement(destRsrc,stmt.getPredicate(),obj);
                destModel.add(newStmt);
            }
            return destModel;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to load a a file of triple to a Jena Model.
     * @param filePath string path to the file.
     * @return model model loaded with the file.
     */
    public static Model loadFileTripleToModel(String filePath) {
        // I used to pass encoding in here, but that's dumb. I'm reading XML
        // which is self-describing.
        Model m = ModelFactory.createDefaultModel();
        logger.info("Loading " + filePath + "...");
        try {
            File inputFile = new File(filePath);
            try (FileInputStream input = new FileInputStream(inputFile)) {
                m.read(input, FileUtilities.toStringUriWithPrefix(inputFile));
            }
            return m;
        } catch (IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to merge two Jena Model.
     * @param model first jena model.
     * @param newModel second jena model.
     * @return merged jena model.
     */
    public static Model mergeModel(Model model,Model newModel) {
        try {
           ResIterator ri = newModel.listSubjects();
            while (ri.hasNext()) {
                Resource newSubject = ri.next();
                Resource subject;
                if (!newSubject.isAnon()) {
                    subject = model.createResource(newSubject.getURI());
                    model = copyToModel(newModel, newSubject, model, subject);
                }
                //else : nevermind; copyToModel will handle this case recursively
            }
            return model;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to delete literal on a Jena Model.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement.
     * @param value value of the literal of the statement to remove from Jena model.
     */
    public static void deleteLiteral(Model model,Resource subject,String property,String value) {
        int pos = property.indexOf(":");
        String prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            String uri = namespaces.get(prefix);
            Property p = model.createProperty(uri, property);
            RDFNode v = model.createLiteral(value);
            Statement s = model.createStatement(subject, p, v);
            model.remove(s);
        } catch (Exception e) {
            // nop;
            logger.warn(gm() + "Exception while try to delete a literal:"+e.getMessage(),e);
        }
    }

    /**
     * Method to query/read for a literal on a Jena Model.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement.
     * @return string of the literal.
     */
    public static String queryLiteral(Model model,Resource subject,String property){
        return findLiteral(model, subject, property);
    }

    /**
     * Method to query/read for a literal on a Jena Model.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement.
     * @return string of the literal.
     */
    public static String findLiteral(Model model,Resource subject,String property){
        int pos = property.indexOf(":");
        String prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            Property p;
            String uri = namespaces.get(prefix);
            if(!isNullOrEmpty(uri)) {
                p = model.createProperty(uri, property);
            }else{
                p = model.createProperty(property);
            }
            StmtIterator iter = model.listStatements( new SelectorImpl(subject, p,(RDFNode) null));
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                RDFNode obj = stmt.getObject();
                if (obj instanceof Literal) {
                    return obj.toString();
                }
            }
        } catch (Exception e){
           logger.warn(gm() + "Exception while try to find a literal:"+e.getMessage(),e);
        }
        return null;
    }

    /**
     * Method to update a literal on a Jena model.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement.
     * @param value value of the literal of the statement to remove from Jena model.
     * @return if true all the operation are done.
     */
    public static boolean updateLiteral(Model model,Resource subject,String property,String value) {
        try {
            //int pos = property.indexOf(":");
            //String prefix = property.substring(0, pos);
            String rdfValue = queryLiteral(model, subject, property);
            if (value != null && !value.equals(rdfValue)) {
                logger.info("Updating " + property + "=" + value);
                deleteLiteral(model, subject, property, rdfValue);
                int pos = property.indexOf(":");
                String prefix = property.substring(0, pos);
                property = property.substring(pos + 1);
                String uri = namespaces.get(prefix);
                Property p = model.createProperty(uri, property);
                RDFNode v =  model.createLiteral(value);
                Statement s = model.createStatement(subject, p, v);
                model.add(s);
                return true;
            }
            logger.warn(gm() + "The value is:"+ value + " while the rdfValue is:"+ rdfValue);
            return false;
        } catch (Exception e) {
            logger.warn(gm() + "Exception while try to update a literal:"+e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to get/find the namespaces on a model jena.
     * @param namespace string as uri of a namespace.
     * @return the prefix for the namespace.
     */
    public static String findNamespacePrefix(String namespace) {
        if (namespaces.containsValue(namespace)) {
            // find it...
            for (String prefix : namespaces.keySet()) {
                if (namespace.equals(namespaces.get(prefix))) {
                    return prefix;
                }
            }
            logger.warn(gm() + "Internal error: this can't happen.");
            return null;
        } else {
            // add it...
            String p = "dp";
            int num = 0;
            String prefix = p + num;
            while (namespaces.containsKey(prefix)) {
                num++;
                prefix = p + num;
            }
            namespaces.put(prefix, namespace);
            return prefix;
        }
    }

    /**
     * Method to convert a model to a string object.
     * @param showRDF jena model.
     * @param baseURI uri prefix of the ontology on the jena model.
     * @param outputFormat string of the output format.
     * @return content string of the jena model.
     */
    public static String convertModelToString(Model showRDF, String baseURI,String outputFormat) {
        //StringOutputStreamKit stringOutput = new StringOutputStreamKit();
        Writer stringOutput = new StringWriter();
        if(!isNullOrEmpty(outputFormat)){
             RDFFormat rdfFormat = createToRDFFormat(outputFormat);
             if(rdfFormat==null){outputFormat = "RDF/XML-ABBREV";}
        }else{
            outputFormat = "RDF/XML-ABBREV";
        }
        showRDF.write(stringOutput,outputFormat, baseURI);
        String rawString = stringOutput.toString();
        // The rawString contains the octets of the utf-8 representation of the
        // data as individual characters. This is really unusual, but it's true.
        byte[] utf8octets = new byte[rawString.length()];
        for (int i = 0; i < rawString.length(); i++) {
            utf8octets[i] = (byte) rawString.charAt(i);
        }
        // Turn these octets back into a proper utf-8 string.
        try {
            rawString = new String(utf8octets, "utf-8");
        } catch (UnsupportedEncodingException uee) {
            // this can't happen
        }
        // Now encode it "safely" as XML
        return XMLUtilities.xmlEncode(rawString);
    }

    /**
     * Method to delete a specific resource , property on model jena
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement.
     * @return jena model.
     */
    public static Model deleteProperty(Model model, Resource subject, String property) {
        String prefix;
        int pos = property.indexOf(":");
        prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        Property p = null;
        String uri = namespaces.get(prefix);
        if (!"".equals(property)) {
            p = model.createProperty(uri, property);
        }

        StmtIterator iter =model.listStatements(new SelectorImpl(subject,p,(RDFNode) null));
        while (iter.hasNext()) {
            Statement stmt = iter.next();
            p = stmt.getPredicate();
            if (p.getNameSpace() == null) {
                continue;
            }
            if (p.getNameSpace().equals(uri)) {
                String type = "literal";
                if (stmt.getObject()instanceof Resource) {
                    type = "resource";
                }
                logger.info("\tdelete " + type + ": " + prefix + ":" + p.getLocalName()
                        + "=" + stmt.getObject().toString());
                model.remove(stmt);
                return model;
            }
        }
        return null;
    }

    /**
     * Taken from com.idea.io.RdfUtils, modified for Jena 2
     */
    /*
     public static Resource updateProperties(Resource resource, GraphVertexChangeEvent vertex){
         setProperty(resource, RSS.title, vertex.getVertex())); return resource;
     }
    */

    /**
     * Method to update a property on a model jena.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement to set.
     * @param value value of the object of the statement.
     * @return jena model.
     */
    public static Model updateProperty(Model model,Resource subject,Property property,Object value) {
        try {
            /*
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {iterator.next(); iterator.remove();}
            */
            //... you must already create the resources
            //subject = model.getResource(redirectionURI);
            //subject = model.createResource("");
            //...Delete all the statements with predicate p for this resource from its associated model.
            subject.removeAll(property);
            subject.addProperty(property, (RDFNode) value);
            return model;
        } catch (Exception e) {
            logger.error(gm()+ e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method for delete statement with specific proprety and literal on a Jena model.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement to set.
     * @param languageLiteral language of the literal.
     * @param valueLiteral value of the literal.
     * @return a jena model.
     */
    public Model deletePropertyAndObject(Model model,Resource subject,Property property,
            String languageLiteral, String valueLiteral) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(property);
        RDFNode foundToDelete = null;
        while (nodeIterator.hasNext()) {
            RDFNode next = nodeIterator.next();
            boolean langsAreIdentical = next.asLiteral().getLanguage().equals(languageLiteral);
            boolean valuesAreIdentical = next.asLiteral().getLexicalForm().equals(valueLiteral);
            if (langsAreIdentical && valuesAreIdentical) {
                foundToDelete = next;
                break;
            }
        }
        model.remove(subject, property, foundToDelete);
        return model;
    }

    /**
     * Method to get/find first the value of the property
     * can use model.getProperty() directly now.
     * @param subject subject of the statement.
     * @param property property of the statement to set.
     * @return value of the literal.
     */
    public static RDFNode findFirstPropertyValue(Resource subject,Property property) {
        Statement statement = subject.getProperty(property);
        if(statement == null){
            logger.warn(gm() + "The statement found is NULL.");
            return null;
        }
        return statement.getObject();
    }

    /**
     * Method to get/find the rdf type from a Resource.
     * @param subject subject of the statement.
     * @return the string of the RdfType.
     */
    public static String findRdfType(Resource subject) {
        if (subject.isAnon()) {
            // @@TODO this whole lot needs improving
            return "anon";
        }
        //show(resource);
        RDFNode type = findFirstPropertyValue(subject, RDF.type);
        if (type == null) {
            return "untyped";
        }
        return type.toString();
    }

    /**
     * Method to get the uri from a Reosurce on a model jena.
     * @param resource resource uri.
     * @param uri new uri for the resource.
     * @return if true all the operation are done.
     */
    public static boolean updateUri(Resource resource, URI uri) {
        try {
            Model m = resource.getModel();
            Resource newResource = m.createResource(uri.toString());
            StmtIterator iterator = resource.listProperties();
            // copy properties from old resource
            // buffer used to avoid concurrent modification
            Set<Statement> statements = new HashSet<>();
            while (iterator.hasNext()) {
                Statement stmt = iterator.next();
                statements.add(stmt);
                // changed for Jena 2
                newResource.addProperty(stmt.getPredicate(), stmt.getObject());
                //model.remove(stmt);
            }
            Iterator<Statement> setIterator = statements.iterator();
            Statement statement;
            while (setIterator.hasNext()) {
                statement = setIterator.next();
                if (m.contains(statement)) {
                    m.remove(statement);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to show a statement on a model jena to the console.
     * @param statement statement to print to the console.
     */
    public static void show(Statement statement) {
        show(statement.getSubject());
        show(statement.getPredicate());
        if (statement.getObject() instanceof Resource) {
            show((Resource) statement.getObject());
        } else {
            logger.info(statement.getObject().toString());
        }
    }

    /**
     * Method to show a resource on a model jena to the console.
     * @param resource resource to print to the console.
     */
    public static void show(Resource resource) {
        StmtIterator iterator =resource.listProperties();
        show(iterator);
    }

    /**
     * Method to show a iterators on a model jena to the console.
     * @param iterator list of the statement to print on the console.
     */
    public static void show(StmtIterator iterator) {
        StringBuilder buffer = new StringBuilder("\n--v--");
        //StmtIterator iterator = resource.listProperties();
        while (iterator.hasNext()) buffer.append("\n").append(iterator.next().toString());
        buffer.append("\n--^--");
        logger.info(buffer.toString());
    }

    /**
     * Method to show a model jena to the console.
     * @param model jena model to print tot the console.
     * @param outputFormat string of the output format.
     */
    public static void show(Model model,String outputFormat) {
        logger.info(convertModelToString(model, outputFormat));
    }

    /**
     * Method to convert a model jena to string.
     * @param model jena model.
     * @param outputFormat string of the output format.
     * @return string of the jena model.
     */
    public static String convertModelToString(Model model,String outputFormat) {
        if (model == null) {
            return "Null Model.";
        }
        if (!isNullOrEmpty(outputFormat)){
            try {
                RDFFormat rdfFormat = createToRDFFormat(outputFormat);
                outputFormat = rdfFormat.getLang().getName();
            }catch(IllegalArgumentException e) {
                outputFormat = "RDF/XML-ABBREV";
            }
        }else{
            outputFormat = "RDF/XML-ABBREV";
        }
        StringWriter stringOut = new StringWriter();
        try {
            //setCommonPrefixes(model);
            model.write(stringOut,outputFormat,RSS.getURI());
            // http://base
            stringOut.flush();
            stringOut.close();
            return stringOut.toString();
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to set the prefix on a model jena.
     * @param model jena model.
     * @param namespaces map of namespace with prefix.
     * @return the model jena with the prefix of namespace.
     */
    public static Model setCommonPrefixes(Model model,Map<String,String> namespaces) {
        for(Map.Entry<String,String> entry : namespaces.entrySet()) {
            //model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
            //model.setNsPrefix("vis", "http://ideagraph.org/xmlns/idea/graphic#");
            model.setNsPrefix(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return model;
    }

    /**
     * Method to replace a resource on a model jena.
     * @param oldResource resource to replace.
     * @param newResource the new resource.
     * @return if true all the operation are done.
     */
    public static boolean updateResource(Resource oldResource,Resource newResource) {
        try {
            StmtIterator statements = model.listStatements();
            Statement statement;
            Resource subject;
            RDFNode object;
            // buffer in List to avoid concurrent modification exception
            List<Statement> statementList = new ArrayList<>();
            while (statements.hasNext()) {
                statementList.add(statements.next());
            }
            for (Statement aStatementList : statementList) {
                statement = aStatementList;
                subject = statement.getSubject();
                object = statement.getObject();
                if (subject.equals(oldResource)) {
                    updateSubjectResource(statement, newResource);
                }
                if ((object instanceof Resource) && (oldResource.equals(object))) {
                    updateObjectResource(statement, newResource);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to replace/update a subject/resource on a model jena.
     * @param statement statement with the resource to replace/update
     * @param newSubject new resource to add tot he model.
     * @return if true all the operation are done.
     */
    public static boolean updateSubjectResource(Statement statement,Resource newSubject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement = m.createStatement(newSubject,
                    statement.getPredicate(),statement.getObject());
            m.remove(statement);
            m.add(newStatement);
            return true;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to replace/update a object resource.
     * @param statement statement with the object to replace/update
     * @param newObject new value of the object
     * @return if true all the operation are done.
     */
    public static boolean updateObjectResource(Statement statement,Resource newObject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement =m.createStatement(statement.getSubject(),
                    statement.getPredicate(),newObject);
            m.remove(statement);
            m.add(newStatement);
            return true;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method copies all properties across to new resource, just replaces type.
     * @param resource the resource to update the type.
     * @param newType the new type for the resource.
     * @return if true all the operation are done.
     */
    public static boolean updateTypeResource(Resource resource,Resource newType) {
        try {
            StmtIterator iterator = resource.listProperties();
            Property property = null;
            Statement statement = null;
            while (iterator.hasNext()) {
                statement = iterator.next();
                property = statement.getPredicate();
                if (property.equals(RDF.type)) {
                    break; // to stop concurrent mod exc
                }
            }
            if (property != null) {
                if (property.equals(RDF.type)) {
                    resource.getModel().remove(statement);
                    resource.addProperty(RDF.type, newType);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method approximate : returns first match.
     * @param model jena model.
     * @param rdfNode property to find.
     * @return resource you found.
     */
    public static Resource findParent(Model model,RDFNode rdfNode) {
        if (rdfNode instanceof Property) {
            return findParentResource(model,(Property) rdfNode);
        }
        return findParentProperty(model, rdfNode);
    }

    /**
     * Method  returns predicate of first statement with matching object.
     * @param model jena model.
     * @param rdfNode property to find.
     * @return poroperty you found.
     */
    public static Property findParentProperty(Model model,RDFNode rdfNode) {
        Statement statement = findParentStatement(model, rdfNode);
        if (statement == null) {
            logger.warn(gm() + "The Statement founded is NULL.");
            return null;
        }
        return statement.getPredicate();
    }

    /**
     * Method approximate : returns first statement with matching object.
     * @param model jena model.
     * @param rdfNode resource to find.
     * @return the statement you found.
     */
    public static Statement findParentStatement(Model model,RDFNode rdfNode) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (rdfNode.equals(statement.getObject())) {
                //parent = statement.getSubject();
                if (!(RDF.type).equals(statement.getPredicate())) {
                    return statement;
                }
            }
        }
        logger.warn(gm() + "The Statement founded is NULL.");
        return null;
    }

    /**
     * Method approximate : returns object of first statement with matching predicate.
     * @param model jena model.
     * @param property property to find.
     * @return resource you found.
     */
    public static Resource findParentResource(Model model,Property property) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            //changed for Jena 2
            if (property.equals(statement.getPredicate())) {
                return statement.getSubject();
            }
        }
        logger.warn(gm() + "The Statement founded is NULL.");
        return null;
    }

    /**
     * Method approximate : gets first match (predicate and object).
     * @param model jena model.
     * @param property property to find.
     * @param object object to find.
     * @return the subject you found.
     */
    public static Resource findSubject(Model model,Property property,RDFNode object) {
        Statement statement =findStatement(model, property, object);
        if (statement == null) {
            logger.warn(gm() + "The Statement founded is NULL.");
            return null;
        }
        return statement.getSubject();
    }

    /**
     * Method approximate : gets first match (predicate and object).
     * @param model jena model.
     * @param property porperty to find.
     * @param object object to find.
     * @return statemn you found.
     */
    public static Statement findStatement(Model model,Property property,RDFNode object) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (property.equals(statement.getPredicate())&& object.equals(statement.getObject())) {
                return statement;
            }
        }
        logger.warn(gm() + "The Statement founded is NULL.");
        return null;
    }

    /*
     * public static void setPropertyObject( Resource resource, Property
     * property, Resource object) { try { StmtIterator iterator =
     * resource.listProperties(property);
     *
     * while (iterator.hasNext()) { iterator.next(); iterator.remove(); }
     *
     * resource.addProperty(property, object); } catch (Exception exception) {
     * exception.printStackTrace(); } }
     */

    /**
     * Method approximate : gets first match (object).
     * @param resource resource to find.
     * @param property property to find.
     * @return string of the proerty you found.
     */
    public static RDFNode findObject(Resource resource,Property property) {
        RDFNode node = findFirstPropertyValue(resource, property);
        if (node == null){
            logger.warn(gm() + "The RDFNode founded is NULL.");
            return null;
        }
        return node;
    }

    /**
     * Method to set/replace/update a property-object.
     * @param resource resource to find.
     * @param property property to find.
     * @param object value of the object you found.
     * @return if true all the operation are done.
     */
    public static boolean updatePropertyObject(Resource resource,Property property,Resource object) {
        try {
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            resource.addProperty(property, object);
            return true;
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method for replace/update a literal value on the jena model.
     * @param model jena model.
     * @param literal literal to update.
     * @param value new value of the literal.
     * @return if true all the operation are done.
     */
    public static boolean updateLiteralValue(Model model,Literal literal, String value){
        try {
            Literal newLiteral = model.createLiteral(value);
            Set<Statement> statements = new HashSet<>();
            StmtIterator iterator = model.listStatements(null, null, literal);
            while (iterator.hasNext()) {
                statements.add(iterator.next());
            }
            Iterator<Statement> setIterator = statements.iterator();
            Statement statement;
            while (setIterator.hasNext()) {
                statement = setIterator.next();
                model.add(statement.getSubject(), statement.getPredicate(), newLiteral);
                model.remove(statement);
            }
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method utility: create new property for a Model.
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode createRDFNode(Object modelOrUri,Object subjectIri){
        Model jenaModel;
        subjectIri = toId(subjectIri);
        if(isIRI(subjectIri) || isUri(subjectIri)) {
            if (modelOrUri != null && modelOrUri instanceof Model) {
                jenaModel = (Model) modelOrUri;
                try {
                    return jenaModel.asRDFNode(NodeUtils.asNode(toIri(subjectIri)));
                }catch(Exception e){
                    return jenaModel.asRDFNode(NodeUtils.asNode(toString(subjectIri)));
                }
            } else {
                jenaModel = createModel();
                try {
                    return jenaModel.asRDFNode(NodeUtils.asNode(toIri(subjectIri)));
                }catch(Exception e){
                    return jenaModel.asRDFNode(NodeUtils.asNode(toString(subjectIri)));
                }
            }
        }else{
            //SystemLog.warning(subjectIri + " is not a IRI normalized!!!");
            return null;
        }
    }

    /**
     * Method utility: create new property for a Model.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode createRDFNode(Object subjectIri){
        return createRDFNode(null, toString(subjectIri));
    }

    /**
     * Method to create a Jena Resource.
     * @param localNameOrUri the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    public static Resource createResource(String localNameOrUri){
        return createResource(null, localNameOrUri);
    }

    /**
     * Method to create a Jena Resource.
     * @param localNameOrUri the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    public static Resource createResource(Object localNameOrUri){
        return createResource(null, localNameOrUri);
    }

    /**
     * Method to create a Jena Resource.
     * @param graphUriAndModel the String iri or the Jena Model.
     * @param localNameorUri the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    public static Resource createResource(Object graphUriAndModel,Object localNameorUri){
        localNameorUri = toId(localNameorUri);
        if(graphUriAndModel == null){
            if(isIRI(localNameorUri) || isUri(localNameorUri))return ResourceFactory.createResource(toString(localNameorUri));
            else return null;
        }else {
            if (isString(graphUriAndModel)) {
                if (toString(graphUriAndModel).endsWith("/") || toString(graphUriAndModel).endsWith("#")) {
                    String uri = toString(graphUriAndModel) + localNameorUri;
                    if(isIRI(uri) || isUri(uri))return ResourceFactory.createResource(uri);
                    else return null;
                } else {
                    String uri = toString(graphUriAndModel)  + "/" +  localNameorUri;
                    if(isIRI(uri) || isUri(uri))return ResourceFactory.createResource(uri);
                    else return null;
                }
            }
            else if (graphUriAndModel instanceof Model) {
                return (Resource) createRDFNode(graphUriAndModel, localNameorUri);
            }
            else return null;
        }
    }

    /**
     * Method to create a Jena Property.
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param predicateUri the String name local Graph or the String iri of the subject.
     * @param impl if true use the PredicateImpl to create the predicate.
     * @return the Jena Predicate.
     */
    private static Property createPropertyBase(Object stringOrModelGraph, Object predicateUri,boolean impl){
        if(stringOrModelGraph == null){
            if(impl){
                if(predicateUri!=null){
                    if(isIRI(predicateUri) || isUri(predicateUri))return new PropertyImpl(toString(predicateUri));
                    else return null;
                }
                else return null;
            }else{
                if(predicateUri!=null) {
                    if (isIRI(predicateUri) || isUri(predicateUri))  return ResourceFactory.createProperty(toString(predicateUri));
                    else return ResourceFactory.createProperty(toString(predicateUri));
                }
                else return null;
            }
        }
        if(isStringNoEmpty(stringOrModelGraph)){
            if(!toString(stringOrModelGraph).endsWith("/")|| !toString(stringOrModelGraph).endsWith("#")){
                stringOrModelGraph = stringOrModelGraph + "/";
            }
            if(impl){
                if(predicateUri!=null){
                    if(isIRI(predicateUri) || isUri(predicateUri) || toString(stringOrModelGraph).isEmpty()) {
                        return new PropertyImpl(toString(predicateUri));
                    }else if(isStringNoEmpty(stringOrModelGraph) &&
                            isIRI(toString(stringOrModelGraph)+"/"+toString(predicateUri))){
                        return new PropertyImpl(toString(stringOrModelGraph), toString(predicateUri));
                    }
                    else return null;
                }
                else return null;
            }else{
                if(predicateUri!=null) {
                    if (isIRI(predicateUri) || isUri(predicateUri) || toString(stringOrModelGraph).isEmpty()) {
                        return ResourceFactory.createProperty(toString(predicateUri));
                    }else if(isStringNoEmpty(stringOrModelGraph) &&
                            isIRI(toString(stringOrModelGraph)+"/"+toString(predicateUri))){
                        return ResourceFactory.createProperty(toString(stringOrModelGraph), toString(predicateUri));
                    }
                    else return null;
                }
                else return null;
            }
        }
        else if(stringOrModelGraph instanceof Model && isStringNoEmpty(predicateUri)) {
            return createRDFNode(stringOrModelGraph, predicateUri).as(Property.class);
        }
        else return null;
    }

    /**
     * Method to create a Jena Property.
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property createProperty(Object stringOrModelGraph, String localNameOrSubject){
        return createPropertyBase(stringOrModelGraph, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property createProperty(Object stringOrModelGraph, Object localNameOrSubject){
        return createPropertyBase(stringOrModelGraph, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property createProperty(String localNameOrSubject){
        return  createPropertyBase(null, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property createProperty(Object localNameOrSubject){
        return  createPropertyBase(null, localNameOrSubject, false);
    }


    /**
     * Method utility: create new typed literal from uri.
     * @param model the Model jean where create the Literal.
     * @param stringOrObject  the value of the Jena Literal.
     * @param datatype the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    private static Literal createLiteralBase(Model model,Object stringOrObject,RDFDatatype datatype){
        if(model == null) {
            if (isString(stringOrObject)) {
                if (datatype != null) return ResourceFactory.createTypedLiteral(toString(stringOrObject), datatype);
                else return ResourceFactory.createPlainLiteral(toString(stringOrObject));
            } else {
                if (datatype != null) return ResourceFactory.createTypedLiteral(toString(stringOrObject), datatype);
                return ResourceFactory.createTypedLiteral(stringOrObject);
            }
        }else{
            if (isString(stringOrObject)) {
                if (datatype != null) return model.createTypedLiteral(toString(stringOrObject), datatype);
                else return model.createLiteral(toString(stringOrObject));
            } else {
                if (datatype != null) return model.createTypedLiteral(stringOrObject, datatype);
                return model.createTypedLiteral(stringOrObject);
            }
        }
    }

    /**
     * Method utility: create new typed literal from uri.
     * @param model the Model Jena where create the Literal.
     * @param stringOrObject  the value of the Jena Literal.
     * @param datatype the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal createLiteral(Model model,Object stringOrObject,RDFDatatype datatype){
        return createLiteralBase(model, stringOrObject, datatype);
    }


    /**
     * Method utility: create new typed literal from uri.
     * @param stringOrObject  the value of the Jena Literal.
     * @param datatype the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal createLiteral(Object stringOrObject,RDFDatatype datatype){
        return createLiteralBase(null, stringOrObject, datatype);
    }

    /**
     * Method utility: create new typed literal from uri.
     * @param stringOrObject  the value of the Jena Literal.
     * @param typeUri the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal createLiteral(Object stringOrObject,String typeUri){
        return createLiteralBase(null, stringOrObject, convertStringToRDFDatatype(typeUri));
    }

    /**
     * Method utility: create new typed literal from uri.
     * @param stringOrObject  the value of the Jena Literal.
     * @return the Jena Literal.
     */
    public static Literal createLiteral(Object stringOrObject){
        return createLiteralBase(null, stringOrObject, null);
    }

    /**
     * Method utility: create new typed literal from uri.
     * @param stringOrObject the value of the Jena Literal.
     * @return the Jena Literal.
     */
    public static Literal createLiteral(String stringOrObject){
        return createLiteralBase(null, stringOrObject, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param model the Jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @return Statement.
     */
    public static Statement createStatement( Model model, String subject,String predicate,String object) {
        return  createStatementBase(model, subject, predicate, object, null, null);
    }

    public static Statement createStatement(
            Model model, String subject,String predicate,Object object,String graphUri,XSDDatatype xsdDatatype) {
        return  createStatementBase(model, subject, predicate, object, graphUri, xsdDatatype);
    }

    public static Statement createStatement(
            Model model, URI subject,URI predicate,URI object,String graphUri,XSDDatatype xsdDatatype) {
        return  createStatementBase(model, subject, predicate, object, graphUri, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param model the Jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @param graphUri the iri of the graph.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    private static Statement createStatementBase(
        Model model, Object subject,Object predicate,Object object,Object graphUri,XSDDatatype xsdDatatype) {
        if (model == null) {
            if(graphUri == null || isStringOrUriEmpty(graphUri)) {
                return ResourceFactory.createStatement(createResource(subject),
                        createProperty(predicate), createLiteral(object, xsdDatatype));
            }else{
                return ResourceFactory.createStatement(createResource(graphUri,subject),
                        createProperty(graphUri,predicate),createLiteral(object, xsdDatatype));
            }
        } else {
            return model.createStatement(createResource(model,subject),
                    createProperty(model, predicate),createLiteral(object, xsdDatatype));
        }

    }

    /**
     * Method utility: create statement form a jena Model.
     * @param model the Jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @param graphUri the iri of the graph.
     * @return Statement.
     */
    public static Statement createStatement( Model model, String subject,String predicate,Object object,String graphUri) {
        return createStatementBase(model, subject, predicate, object, graphUri, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @param graphUri the iri of the graph.
     * @return Statement.
     */
        public static Statement createStatement(String subject,String predicate,String object,String graphUri){
            return createStatementBase(null, subject, predicate, object, graphUri, null);
        }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @return Statement.
     */
    public static Statement createStatement(String subject,String predicate,String object){
        return createStatementBase(null, subject, predicate, object, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @return Statement.
     */
    public static Statement createStatement(String subject,String predicate,Object object){
        return createStatementBase(null, subject, predicate, object, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement createStatement(String subject,String predicate,Object object,XSDDatatype xsdDatatype){
        return createStatementBase(null, subject, predicate, object, null, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @param graphUri  the URI to the graph base of the ontology.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement createStatement(String subject,String predicate,Object object,String graphUri,XSDDatatype xsdDatatype){
        return createStatementBase(null, subject, predicate, object, graphUri, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @param graphUri  the URI to the graph base of the ontology.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement createStatement(String subject,String predicate,Object object,String graphUri,String xsdDatatype){
        return createStatementBase(null, subject, predicate, object, graphUri, createToXSDDatatype(xsdDatatype));
    }

    /**
     * Method to create a Jena Dataset for the SPARQL query.
     * @param dftGraphURI the URI of the location of the resource with the triples.
     * @param namedGraphURIs the URI's of all locations with name.
     * @return the JENA Dataset.
     */
    public static Dataset createDataSet(String dftGraphURI,List<String> namedGraphURIs){
       /* String dftGraphURI = "file:default-graph.ttl" ;
        List namedGraphURIs = new ArrayList() ;
        namedGraphURIs.add("file:named-1.ttl") ;
        namedGraphURIs.add("file:named-2.ttl") ;*/
        return DatasetFactory.create(dftGraphURI, namedGraphURIs) ;
    }

    /**
     * Method to get a Dataset from a existent Jena Model.
     * @param model the Jena Model.
     * @return the Dataset extract from Jena Model.
     */
    public static Dataset getDataSetFromModel(Model model){
        Dataset dataset = DatasetFactory.createMem() ;
        dataset.setDefaultModel(model) ;
        return dataset;
    }

    /**
     * Method to get a Dataset from a existent List of Jena Models.
     * @param baseModel the Jena Model.
     * @param listModel the Map of Model with the specific uri to add to the new Dataset.
     * @return the Dataset extract from a list of Jena Model.
     */
    public static Dataset getDataSetFromListOfModel(Model baseModel,Map<String,Model> listModel){
        Dataset dataset = DatasetFactory.createMem() ;
        dataset.setDefaultModel(baseModel) ;
        for(Map.Entry<String,Model> entry : listModel.entrySet()){
            dataset.addNamedModel(entry.getKey(),entry.getValue());
        }
        return dataset;
    }


    /**
     * Method utility: create new default Jena Model.
     * @return Jena Model.
     */
    public static Model createModel(){
       return ModelFactory.createDefaultModel();
    }

    /**
     * Method utility: create new default Jena Graph.
     * @return Jena Graph.
     */
    public static Graph createGraph(){
        return  com.hp.hpl.jena.sparql.graph.GraphFactory.createDefaultGraph();
    }

    /**
     * Method to convert a Jena Model to a Jena Ontology Model.
     * @param model the Jena Base Model.
     * @return the Jena Ontology Model.
     */
    public static OntModel createOntologyModel(Model model){
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
    }

    /**
     * Method to create a Jena Triple Match.
     * @param subject the Jena Node of the Subject.
     * @param predicate the Jena Node of the Predicate.
     * @param object the Jena Node of the Object.
     * @return the Jena TripleMatch.
     */
    public static TripleMatch createTripleMatch(Node subject,Node predicate,Node object){
        return new Triple(subject,predicate,object);
    }

    /**
     * Method to create a Jena Triple Match.
     * @param triple the Jena Triple Object.
     * @return the Jena TripleMatch.
     */
    public static TripleMatch createTripleMatch(Triple triple){ return triple;}

    /**
     * Method to convert a Jena TripleMatch to the Jena Triple.
     * @param tm the Jena TripleMatch.
     * @return the Jena triple.
     */
    public static Triple convertTripleMatchToTriple(TripleMatch tm){ return tm.asTriple();}

    /**
     * Method to convert a Jena TripleMatch to the Jena Subject Node.
     * @param tm the Jena TripleMatch.
     * @return the Jena Node.
     */
    public static Node convertTripleMatchToSubjectNode(TripleMatch tm){return tm.getMatchSubject();}

    /**
     * Method to convert a Jena TripleMatch to the Jena Predicate Node.
     * @param tm the Jena TripleMatch.
     * @return the Jena Node.
     */
    public static Node convertTripleMatchToPredicateNode(TripleMatch tm){return tm.getMatchPredicate();}

    /**
     * Method to convert a Jena TripleMatch to the Jena Object Node.
     * @param tm the Jena TripleMatch.
     * @return the Jena Node.
     */
    public static Node convertTripleMatchToObjectNode(TripleMatch tm){return tm.getMatchObject();}



    /**
     * Method for load a file in the resource folder like a inpustream.
     * @param filename string of path to the file.
     * @param thisClass this class.
     * @return inputstream of the file.
     */
    public static InputStream loadResourceAsStream(String filename,Class<?> thisClass) {
        return thisClass.getClassLoader().getResourceAsStream(filename);
    }

    /**
     * Method to converts vector to string for jena
     * @param inputVector java.util.Vector to convert string.
     * @return string of the jena vector.
     */
    public static String convertVectorToJenaString(Vector<String> inputVector) {
        String subjects = "";
        for(int subIndex = 0;subIndex < inputVector.size();subIndex++)
            subjects = subjects + "<" + inputVector.elementAt(subIndex) + ">";
        return subjects;
    }

    /**
     * Method to convert a Jena Model to a Jena Graph.
     * @param model the jena Model
     * @return the Jena Graph.
     */
    public Graph convertModelToGraph(Model model){
        return model.getGraph();
    }

    /**
     * Method to convert a Jena Grpah to a Jena Model.
     * @param graph the Jena Graph.
     * @return the Jena Model.
     */
    public static Model convertGraphToModel(Graph graph){
        return ModelFactory.createModelForGraph(graph);
    }



    //////////////////////////////////////////////////////7
    //Some method with the deprecated Graph package.
    ////////////////////////////////////////////////////////77
    /**
     * Method to convert a set of Jena Graph Nodes to a Jena Graph Triple Object.
     * old name : createTriple.
     * @param subject the Jena Graph Node Subject of the triple.
     * @param predicate the Jena Graph Node Predicate of the triple.
     * @param object the Jena Graph Node Object of the triple.
     * @return the Jena Graph Triple Object setted with the content of the jena Graph Nodes.
     */
    public Triple convertGraphNodesToGraphTriple(
            Node subject,Node predicate,Node object){
        return new Triple(subject,predicate,object);
    }

    /**
     * Method to convert a String uri to a jena Graph Node.
     * old name : createNode.
     * @param uriResource string of the uri resource to convert.
     * @return the Jena Graph Node converted.
     */
    public Node convertStringUriToGraphNode(String uriResource){
        return com.hp.hpl.jena.graph.NodeFactory.createURI(uriResource);
    }

    /**
     * Method to add a List of jena Graph triple to a Jena Graph Object.
     * @param triples the List of jena Graph triples.
     * @param graph the jena Graph Object.
     */
    @SuppressWarnings("deprecation")
    public void addListOfTriplesToJenaGraph(List<Triple> triples,Graph graph){
        graph.getBulkUpdateHandler().add(triples);
       /* for(Triple triple: triples){
            graph.add(triple);
        }*/
    }

    //----------------------------------------
    //NEW METHODS
    //----------------------------------------

    /**
     * Method to create a JENA Query from a String SPARQL Query.
     * @param querySPARQL the String SPARQL Query.
     * @return the JENA Query object.
     */
    public static Query createQuery(String querySPARQL){
        return QueryFactory.create(querySPARQL) ;
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param query the Query Select to analyze.
     * @param model the Jena Model.
     * @return the Long execution time for evaluate the query.
     */
    public static Long getExecutionQueryTime(String query,Model model){
        return getExecutionQueryTime(createQuery(query), model);
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     * @param query the Query Select to analyze.
     * @param model the Jena Model.
     * @return the Long execution time for evaluate the query.
     */
    public static Long getExecutionQueryTime(Query query,Model model){
        com.hp.hpl.jena.sparql.util.Timer timer = new com.hp.hpl.jena.sparql.util.Timer() ;
        //Dataset ds = qexec.getDataset();
        Dataset ds = getDataSetFromModel(model);
        QueryExecution qexec = QueryExecutionFactory.create(query,ds);
        if (query.isSelectType()) {
            timer.startTimer() ;
            ResultSet results = qexec.execSelect();
            //ResultSetFormatter.consume(results) ;
            return timer.endTimer();   // Time in milliseconds.
        } else if (query.isConstructType()) {
            timer.startTimer() ;
            Model results = qexec.execConstruct();
            return timer.endTimer();   // Time in milliseconds.
        }else if (query.isAskType()) {
            timer.startTimer() ;
            boolean results = qexec.execAsk();
            return timer.endTimer();   // Time in milliseconds.
        }else if (query.isDescribeType()) {
            timer.startTimer() ;
            Model results = qexec.execDescribe();
            return timer.endTimer();   // Time in milliseconds.
        }else{
            return null;
        }
    }

    /**
     * Mathof to convert a standard Jena Model to a Jena InfModel.
     * @param model the Jena Model.
     * @return the Jena InfModel.
     */
    public static InfModel createInfModel(Model model){
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        return ModelFactory.createInfModel(reasoner, model);
    }

    /**
     * Method to convert a dataset to a OntoModel, now you can add some rule to uor TDB Model.
     * @param dataset the DataSet Jena Object.
     * @param OwlOrSWRL the Rules to add to the DataSet Jena Object.
     * @return the Ontology Model.
     */
    public static OntModel createOntoModel(Dataset dataset,URL OwlOrSWRL){
        Model     m = dataset.getDefaultModel(); //the TDB data
        Model toto =  ModelFactory.createDefaultModel();
        toto.read(OwlOrSWRL.toString()); // the OWL & SWRL rules inside
        Model union = ModelFactory.createUnion(m, toto); //Merging both
        OntModelSpec spec = OntModelSpec.RDFS_MEM_TRANS_INF;
        //return ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC ,union); // Use Pellet reasonner
        return ModelFactory.createOntologyModel(spec,union); // Use Pellet reasonner
    }


    //--------------------------------
    //Utility private methods
    //--------------------------------

    /**
     * Method to check if a String uri is a IRI normalized.
     * http://stackoverflow.com/questions/9419658/normalising-possibly-encoded-uri-strings-in-java
     * @param uri the String to verify.
     * @return if true the String is a valid IRI.
     */
    public static Boolean isIRI(Object uri){
        try {
            if(isString(uri)) {
                IRIFactory factory = IRIFactory.uriImplementation();
                IRI iri = factory.construct(toString(uri));
           /* ArrayList<String> a = new ArrayList<>();
            a.add(iri.getScheme());
            a.add(iri.getRawUserinfo());
            a.add(iri.getRawHost());
            a.add(iri.getRawPath());
            a.add(iri.getRawQuery());
            a.add(iri.getRawFragment());*/
                return true;
            }else return false;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Method to convert a URI os String reference to a resource to a good ID.
     * NOTE: URI string: scheme://authority/path?query#fragment
     * @param uriResource the String or URI reference Resource.
     * @return the String id of the Resource.
     */
    private static String toId(Object uriResource){
        return URI.create(toString(uriResource).replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", "_").trim()).toString();
    }

    private static String toString(Object uriResource){
        return String.valueOf(uriResource);
    }

    private static boolean isUri(Object uriResource){
        if(uriResource instanceof URI)return true;
        else{
            try { URI.create(String.valueOf(uriResource));return true;
            }catch(Exception e){ return false;}
        }
    }

    private static boolean isStringNoEmpty(Object uriResource){
        return (uriResource instanceof String && !isNullOrEmpty(String.valueOf(uriResource)));
    }

    private static boolean isStringEmpty(Object uriResource){
        return (uriResource instanceof String && isNullOrEmpty(toString(uriResource)));
    }

    private static boolean isStringOrUriEmpty(Object uriResource){
        return (
                (uriResource instanceof String && isNullOrEmpty(toString(uriResource))) ||
                (uriResource instanceof URI && isNullOrEmpty(toString(uriResource)))
        );
    }

    private static boolean isString(Object uriResource){
        return (uriResource instanceof String);
    }

    public static IRI toIri(Object uriResource){
        return IRIFactory.uriImplementation().construct(toString(uriResource));
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    private static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty() ;
    }




}//end of the class JenaKit
