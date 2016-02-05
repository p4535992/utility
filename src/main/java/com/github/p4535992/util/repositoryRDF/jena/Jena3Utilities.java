package com.github.p4535992.util.repositoryRDF.jena;

import com.github.p4535992.util.string.StringUtilities;

import org.apache.jena.atlas.lib.*;
import org.apache.jena.atlas.lib.Timer;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.*;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.graph.impl.LiteralLabelFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.DatatypePropertyImpl;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.system.*;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.resultset.RDFOutput;
import org.apache.jena.sparql.util.ModelUtils;
import org.apache.jena.sparql.util.NodeUtils;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.*;

/**
 * Class utility for Jena
 * Created by 4535992 in 2015-04-28.
 * href: https://gist.github.com/ijdickinson/3830267
 * NOTE: Work with Jena 2.
 * @author 4535992.
 * @version 2015-12-07.
 */
public class Jena3Utilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Jena3Utilities.class);

    //CONSTRUCTOR
    protected Jena3Utilities() {
    }

    private static Jena3Utilities instance = null;

    public static Jena3Utilities getInstance() {
        if (instance == null) {
            instance = new Jena3Utilities();
        }
        return instance;
    }

    //PRIVATE
    public static String INFORMAT, OUTFORMAT;
    public static Lang OUTLANGFORMAT, INLANGFORMAT;
    public static RDFFormat OUTRDFFORMAT, INRDFFORMAT;

    public static void setInput(RDFFormat INRDFFORMAT) {
        INFORMAT = INRDFFORMAT.getLang().getName();
        INLANGFORMAT = INRDFFORMAT.getLang();
    }

    public static void setOutput(RDFFormat OUTRDFFORMAT) {
        OUTFORMAT = OUTRDFFORMAT.getLang().getName();
        OUTLANGFORMAT = OUTRDFFORMAT.getLang();
    }


    private static Model model;
    private static final Map<String, String> namespaces = new HashMap<>();
    public static final String RDF_FORMAT = "RDF/XML-ABBREV";

    /**
     * Method  to Write large model jena to file of text.
     *
     * @param fullPath     string of the path to the file.
     * @param model        jena model to write.
     * @param outputFormat the output format you want to write the model.
     * @return if true all the operation are done.
     */
    public static boolean writeModelToFile(String fullPath, Model model, String outputFormat) {
        /*fullPath =
                FileUtilities.getPath(fullPath) +
                        File.separator +
                        FileUtilities.getFilenameWithoutExt(fullPath)
                        + "." + outputFormat.toLowerCase();*/

        File outputFile = new File(fullPath);
        fullPath = outputFile.getAbsolutePath()
                .replace(FileUtils.getFilenameExt(outputFile.getAbsolutePath()),outputFormat);
        outputFile = new File(fullPath);

        logger.info("Try to write the new file of triple from:" + fullPath + "...");

        OUTLANGFORMAT = toLang(outputFormat);
        OUTRDFFORMAT = toRDFFormat(outputFormat);
        OUTFORMAT = outputFormat.toUpperCase();
        try {
            try (FileWriter out = new FileWriter(fullPath)) {
                model.write(out, OUTLANGFORMAT.getName());
            }
        } catch (Exception e1) {
            logger.warn("...there is was a problem to try the write the triple file at the first tentative...");
            try {
                FileOutputStream outputStream = new FileOutputStream(fullPath);
                model.write(outputStream, OUTLANGFORMAT.getName());
            } catch (Exception e2) {
                logger.warn("...there is was a problem to try the write the triple file at the second tentative...");
                try {
                    Writer writer = new FileWriter(new File(fullPath));
                    model.write(writer, OUTFORMAT);
                } catch (Exception e3) {
                    logger.warn("...there is was a problem to try the write the triple file at the third tentative...");
                    try {
                        Charset ENCODING = StandardCharsets.UTF_8;
                        boolean b = outputFile.createNewFile();
                        if(!b) throw new Exception("... exception during the writing of the file of triples:");
                        //FileUtilities.createFile(fullPath);
                        Path path = Paths.get(fullPath);
                        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
                            model.write(writer, null, OUTLANGFORMAT.getName());
                        }
                    } catch (Exception e4) {
                        logger.error("... exception during the writing of the file of triples:" + fullPath);
                        logger.error(e4.getMessage(), e4);
                        return false;
                    }
                }
            }
        }
        logger.info("... the file of triple to:" + fullPath + " is been wrote!");
        return true;
    }

    public static boolean write(File file, Model model, String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, toLang(outputFormat).getName());
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(File file, Model model, String outputFormat, String baseUri) {
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, toLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(OutputStream stream, Model model, String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from stream ...");
            model.write(stream, toLang(outputFormat).getName());
            logger.info("... the file of triple to stream is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(OutputStream stream, Model model, String outputFormat, String baseUri) {
        try {
            logger.info("Try to write the new file of triple from stream ...");
            model.write(stream, toLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to stream is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(Writer writer, Model model, String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from write ...");
            model.write(writer, toLang(outputFormat).getName());
            logger.info("... the file of triple to write is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(Writer writer, Model model, String outputFormat, String baseUri) {
        try {
            logger.info("Try to write the new file of triple from write ...");
            model.write(writer, toLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to write is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

/*    public static void write(OutputStream s, Dataset d, String o) {
        RDFDataMgr.write(s, d, toLang(o));
    }

    public static void write(OutputStream s, Dataset d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(OutputStream s, Dataset d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(OutputStream s, DatasetGraph d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(OutputStream s, DatasetGraph d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(OutputStream s, Model m, Lang l) {
        RDFDataMgr.write(s, m, l);
    }

    public static void write(OutputStream s, Model m, RDFFormat f) {
        RDFDataMgr.write(s, m, f);
    }

    public static void write(OutputStream s, Graph g, Lang l) {
        RDFDataMgr.write(s, g, l);
    }

    public static void write(OutputStream s, Graph g, RDFFormat f) {
        RDFDataMgr.write(s, g, f);
    }

    public static void write(StringWriter s, Dataset d, String o) {
        RDFDataMgr.write(s, d, toLang(o));
    }

    public static void write(StringWriter s, Dataset d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(StringWriter s, Dataset d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(StringWriter s, DatasetGraph d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(StringWriter s, DatasetGraph d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(StringWriter s, Model m, Lang l) {
        RDFDataMgr.write(s, m, l);
    }

    public static void write(StringWriter s, Model m, RDFFormat f) {
        RDFDataMgr.write(s, m, f);
    }

    public static void write(StringWriter s, Graph g, Lang l) {
        RDFDataMgr.write(s, g, l);
    }

    public static void write(StringWriter s, Graph g, RDFFormat f) {
        RDFDataMgr.write(s, g, f);
    }
    */

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     *
     * @param sparql the String sparql query.
     * @param model  Jena Model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlOnModel(String sparql, Model model) {
        return execSparqlOn(sparql, model);
    }

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     *
     * @param sparql  the String sparql query.
     * @param dataset Jena Dataset.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlOnDataset(String sparql, Dataset dataset) {
        return execSparqlOn(sparql, dataset);
    }

    /**
     * Method to exec a SPARQL query to a remote service.
     *
     * @param sparql        the String sparql query.
     * @param remoteService the String address web page to the web service endpoint SPARQL.
     * @return the result of the query.
     */
    public static Model execSparqlOnRemote(String sparql, String remoteService) {
        /*HttpAuthenticator authenticator = new PreemptiveBasicAuthenticator(
                new ScopedAuthenticator(new URI(SPARQLR_ENDPOINT), SPARQLR_USERNAME, SPARQLR_PASSWORD.toCharArray())
        );*/
        return execSparqlOn(sparql, remoteService);
    }

    private static Model execSparqlOn(String sparql, Object onObject) {
        Query query = QueryFactory.create(sparql);
        Model resultModel;
        QueryExecution qexec;
        if (onObject instanceof Dataset) {
            qexec = QueryExecutionFactory.create(sparql, (Dataset) onObject);
        } else if (onObject instanceof Model) {
            qexec = QueryExecutionFactory.create(query, (Model) onObject);
        } else if (onObject instanceof String) {
            //QueryEngineHTTP qexec = new QueryEngineHTTP(remoteService, sparql);
            //qexec.setBasicAuthentication("siimobility", "siimobility".toCharArray());
            qexec = QueryExecutionFactory.sparqlService(String.valueOf(onObject), query);
        } else {
            qexec = QueryExecutionFactory.create(query);
        }

        if (query.isSelectType()) {
            ResultSet results;
            RDFOutput output = new RDFOutput();
            results = qexec.execSelect();
            //... make exit from the thread the result of query
            results = ResultSetFactory.copyResults(results);
            logger.info("Exec query SELECT SPARQL :" + sparql);
            return output.asModel(results);
        } else if (query.isConstructType()) {
            resultModel = qexec.execConstruct();
            logger.info("Exec query CONSTRUCT SPARQL :" + sparql);
            return resultModel;
        } else if (query.isDescribeType()) {
            resultModel = qexec.execDescribe();
            logger.info("Exec query DESCRIBE SPARQL :" + sparql);
            return resultModel;
        } else if (query.isAskType()) {
            logger.info("Exec query ASK SPARQL :" + sparql);
            logger.warn("ATTENTION the SPARQL query:" + sparql + ".\n is a ASK Query can't return a Model object");
            return null;
        } else if (query.isUnknownType()) {
            logger.info("Exec query UNKNOWN SPARQL :" + sparql);
            logger.warn("ATTENTION the SPARQL query:" + sparql + ".\n is a UNKNOWN Query can't return a Model object");
            return null;
        } else {
            logger.error("ATTENTION the SPARQL query:" + sparql + ".\n is a NULL Query can't return a Model object");
            return null;
        }
    }

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlConstructorOnModel(String sparql, Model model) {
        /*Query query = QueryFactory.create(sparql) ;
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        resultModel = qexec.execConstruct();
        logger.info("Exec query CONSTRUCT SPARQL :" + sparql);
        return  resultModel;*/
        return execSparqlOnModel(sparql, model);
    }

    /**
     * Method for execute a DESCRIIBE SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlDescribeOnModel(String sparql, Model model) {
        /*Query query = QueryFactory.create(sparql) ;
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        resultModel = qexec.execDescribe();
        logger.info("Exec query DESCRIBE SPARQL :" + sparql);
        return resultModel;*/
        return execSparqlOnModel(sparql, model);
    }

    /**
     * Method for execute a SELECT SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result set of the query.
     */
    public static ResultSet execSparqlSelectOnModel(String sparql, Model model) {
        ResultSet results;
        QueryExecution qexec = QueryExecutionFactory.create(sparql, model);
        results = qexec.execSelect();
        //... make exit from the thread the result of query
        results = ResultSetFactory.copyResults(results);
        logger.info("Exec query SELECT SPARQL :" + sparql);
         // iterate over the result set
         /*while(results.hasNext()) {
             QuerySolution sol = results.next();
             System.out.println("Solution:" + sol.toString() );
         }*/
        logger.info("Exec query SELECT SPARQL :" + sparql);
        return results;
    }

    /**
     * Method to convert a ResultSet to a Model.
     *
     * @param resultSet the ResultSet Jena to convert.
     * @return the Model Jena populate with the ResultSet.
     */
    public static Model toModel(ResultSet resultSet) {
        RDFOutput output = new RDFOutput();
        return output.asModel(resultSet);
        //or
       /* Model model = ModelFactory.createDefaultModel();
        ResultSetRewindable result = ResultSetFactory.makeRewindable(model);
        ResultSetFormatter.asText(resultSet);
        result.reset();
        return model;*/
    }

    /**
     * Method for execute a ASK SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result set of the query like a boolean value.
     */
    public static boolean execSparqlAskOnModel(String sparql, Model model) {
        Query query = QueryFactory.create(sparql);
        boolean result;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        //try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
        result = qexec.execAsk();
        logger.info("Exec query ASK SPARQL :" + sparql);
        //}
        return result;
    }

    /**
     * Method for load a file of tuples to a jena model.
     *
     * @param fileInput the {@link File} input file.
     * @return the jena model of the file.
     */
    public static Model toModel(File fileInput){
        return loadFileTripleToModel(fileInput,null,null);
    }

    /**
     * Method to load a a file of triple to a Jena Model.
     *
     * @param filePath string path to the file.
     * @return model model loaded with the file.
     * @throws java.io.FileNotFoundException throw if any "File Not Found" error is occurred.
     */
    public static Model toModel(String filePath) throws FileNotFoundException {
        return loadFileTripleToModel(filePath,null,null);
    }

    private static Model loadFileTripleToModel(Object filenameOrURI,String baseURI,String rdfSyntax){
        Model m = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in;
        if(filenameOrURI instanceof File){
            File file = ((File)filenameOrURI);
            logger.info("Try to read file of triples from the path:" + file.getAbsolutePath() + "...");
            if(isNullOrEmpty(rdfSyntax)) {
                rdfSyntax = toLang(FileUtils.getFilenameExt(file.getAbsolutePath())).getLabel();
                if(isNullOrEmpty(rdfSyntax)) rdfSyntax = Lang.RDFXML.getLabel().toUpperCase();
            }

            try {
                in = FileManager.get().open(file.getAbsolutePath());
            } catch (Exception e) {
                try {
                    in = file.toURI().toURL().openStream();
                }catch(Exception e2){
                    try {
                        in = new FileInputStream(file);
                    } catch (FileNotFoundException e1) {
                        logger.error("Can't set the InputStream for the File:"+file.getAbsolutePath());
                        return null;
                    }
                }
            }
            if (in == null || !file.exists())
                throw new IllegalArgumentException("File: " + filenameOrURI + " not found");

            try {
                //try load from resource folder...
                FileManager.get().addLocatorClassLoader(Jena3Utilities.class.getClassLoader());
                //try to load from uri...
                try {
                    m = FileManager.get().loadModel(file.toURI().toString(), null, rdfSyntax);
                }catch(Exception e){
                    m = FileManager.get().readModel(m,file.toURI().toString(),null,rdfSyntax);
                }
            } catch (Exception e) {
                //try to load from inputStream....
                try {
                    m.read(in, null, rdfSyntax);
                } catch (Exception e1) {
                   /* try {
                        RDFDataMgr.read(m, in, INLANGFORMAT);
                    } catch (Exception e2) {
                        try {
                            //If you are just opening the stream from a file (or URL) then Apache Jena
                            RDFDataMgr.read(m, fileInput.toURI().toString());
                        } catch (Exception e3) {
                            logger.error("Failed read the file of triples from the path:" +
                                    fileInput.getAbsolutePath() + ":" + e.getMessage(), e);
                        }
                    }*/
                    logger.error("Can't read the InputStream for the file:"+file.getAbsolutePath());
                    return null;
                }
            }
            logger.info("...file of triples from the path:" + file.getAbsolutePath() + " readed!!");
            return m;
        }else if(filenameOrURI instanceof URI){
            URI uri = ((URI)filenameOrURI);
            logger.info("Try to read URI of triples from the path:" + uri.toString() + "...");
            if(isNullOrEmpty(rdfSyntax)) {
                rdfSyntax = toLang(FileUtils.getFilenameExt(uri.toString())).getLabel();
                if(isNullOrEmpty(rdfSyntax)) rdfSyntax = Lang.RDFXML.getLabel().toUpperCase();
            }

            try {
                in = FileManager.get().open(uri.toString());
            } catch (Exception e) {
                try {
                    in = uri.toURL().openStream();
                }catch(Exception e2){
                    try {
                        in = new FileInputStream(uri.toURL().toString());
                    } catch (FileNotFoundException|MalformedURLException e1) {
                        logger.error("Can't set the InputStream for the URI:"+uri.toString());
                        return null;
                    }
                }
            }
            if (in == null)
                throw new IllegalArgumentException("URI: " + filenameOrURI + " not found");

            try {
                //try load from resource folder...
                FileManager.get().addLocatorClassLoader(Jena3Utilities.class.getClassLoader());
                //try to load from uri...
                try {
                    m = FileManager.get().loadModel(uri.toString(), null, rdfSyntax);
                }catch(Exception e){
                    m = FileManager.get().readModel(m,uri.toString(),null,rdfSyntax);
                }
            } catch (Exception e) {
                //try to load from inputStream....
                try {
                    m.read(in, null, rdfSyntax);
                }catch(Exception e1){
                    logger.error("Can't read the InputStream for the URI:"+uri.toString());
                    return null;
                }
            }
            logger.info("...URI of triples from the path:" + uri.toString() + " readed!!");
            return m;
        }
        else if(filenameOrURI instanceof String){
            return loadFileTripleToModel(new File(String.valueOf(filenameOrURI)),null,null);
        }else{
            logger.warn("Can't load the File of Triple to the Jena Model, make sure the input is a File or a String or a URI," +
                    " your current param is a :"+filenameOrURI.getClass().getName());
            return null;
        }
    }

    /**
     * Metodo per il caricamento di un file di triple in un'oggetto model di JENA.
     *
     * @param filename    name of the file of input.
     * @param filepath    path to the file of input wihtout the name.
     * @param inputFormat format of the file in input.
     * @return the jena model of the file.
     * @throws FileNotFoundException thriow if any "File Not Found" error is occurred.
     */
    public static Model toModel(String filename, String filepath, String inputFormat)
            throws FileNotFoundException {
        if(!filepath.endsWith(File.separator)) filepath = filepath + File.separator;
        if(filename.startsWith(File.separator)) filename = filename.substring(1,filename.length());
        if(inputFormat.startsWith(".")) inputFormat = inputFormat.substring(1,inputFormat.length());
        File fileInput = new File(filepath + filename + "." + inputFormat);
        return loadFileTripleToModel(fileInput,null, toLang(inputFormat).getLabel().toUpperCase());

    }

    /**
     * Helper method that splits up a URI into a namespace and a local part.
     * It uses the prefixMap to recognize namespaces, and replaces the
     * namespace part by a prefix.
     *
     * @param prefixMap the PremixMapping of Jena.
     * @param resource the Resource Jena.
     * @return the Array of String.
     */
    public static String[] split(PrefixMapping prefixMap, Resource resource) {
        String uri = resource.getURI();
        if (uri == null) {
            return new String[] {null, null};
        }
        Map<String,String> prefixMapMap = prefixMap.getNsPrefixMap();
        Set<String> prefixes = prefixMapMap.keySet();
        String[] split = { null, null };
        for (String key : prefixes){
            String ns = prefixMapMap.get(key);
            if (uri.startsWith(ns)) {
                split[0] = key;
                split[1] = uri.substring(ns.length());
                return split;
            }
        }
        split[1] = uri;
        return split;
    }




    /**
     * A list of org.apache.jena.riot.Lang file formats.
     * return all the language Lang supported from jena.
     * exception : "AWT-EventQueue-0" java.lang.NoSuchFieldError: RDFTHRIFT  or CSV.
     */
    private static final Lang allFormatsOfRiotLang[] = new Lang[]{
            Lang.NTRIPLES, Lang.N3, Lang.RDFXML,Lang.TURTLE, Lang.TRIG, Lang.TTL,
            Lang.NQUADS, Lang.NQ,Lang.JSONLD,Lang.NT, Lang.RDFJSON,Lang.RDFNULL,Lang.CSV,
            Lang.RDFTHRIFT
    };

    /**
     * A list of {@link RDFFormat} file formats used in jena.
     */
    private static final RDFFormat allFormatsOfRDFFormat[] = new RDFFormat[]{
            RDFFormat.TURTLE, RDFFormat.TTL,
            RDFFormat.JSONLD_FLAT,
            RDFFormat.JSONLD_PRETTY,
            RDFFormat.JSONLD,
            RDFFormat.RDFJSON, RDFFormat.RDFNULL, RDFFormat.NQUADS, RDFFormat.NQ,
            RDFFormat.NQUADS_ASCII,RDFFormat.NQUADS_UTF8,
            RDFFormat.NT, RDFFormat.NTRIPLES,
            RDFFormat.NTRIPLES_ASCII,RDFFormat.NTRIPLES_UTF8,
            RDFFormat.RDFXML, RDFFormat.RDFXML_ABBREV,
            RDFFormat.RDFXML_PLAIN, RDFFormat.RDFXML_PRETTY, RDFFormat.TRIG, RDFFormat.TRIG_BLOCKS,
            RDFFormat.TRIG_FLAT, RDFFormat.TRIG_PRETTY, RDFFormat.TURTLE_BLOCKS, RDFFormat.TURTLE_FLAT,
            RDFFormat.TURTLE_PRETTY
    };

    /**
     * Method to convert a URI {@link String} to a correct {@link RDFDatatype}  jena.
     *
     * @param uri the {@link String} of the uri resource.
     * @return the {@link RDFDatatype} of the uri resource.
     */
    public static RDFDatatype toRDFDatatype(String uri) {
        return TypeMapper.getInstance().getSafeTypeByName(toXSDDatatype(uri).getURI());
    }

    /**
     * Method to convert a {@link XSDDatatype} to a correct {@link RDFDatatype}  jena.
     *
     * @param xsdDatatype the {@link XSDDatatype} of the uri resource.
     * @return the {@link RDFDatatype} of the uri resource.
     */
    public static RDFDatatype toRDFDatatype(XSDDatatype xsdDatatype){
        return TypeMapper.getInstance().getSafeTypeByName(xsdDatatype.getURI());
    }

    public static RDFDatatype toRDFDatatype(XSD xsd){
        Resource resource = toResource(xsd);
        return TypeMapper.getInstance().getSafeTypeByName(resource.getURI());
    }

    /*
    public static XSSimpleType convertStringToXssSimpleType(String nameDatatype){
        SymbolHash fBuiltInTypes = new SymbolHash();
        return (XSSimpleType)fBuiltInTypes.get(nameDatatype);
    }

    public static XSDDatatype convertStringToXSDDatatype(String nameDatatype){
        XSSimpleType xss = convertStringToXssSimpleType(nameDatatype);
        return new XSDDatatype(xss,xss.getNamespace());
    }

    private static final short[] allFormatOfXSSimpleType = new short[]{
            XSSimpleType.PRIMITIVE_ANYURI,XSSimpleType.PRIMITIVE_BASE64BINARY,XSSimpleType.PRIMITIVE_BOOLEAN,
            XSSimpleType.PRIMITIVE_DATE,XSSimpleType.PRIMITIVE_DATETIME,XSSimpleType.PRIMITIVE_DECIMAL,XSSimpleType.PRIMITIVE_DOUBLE,
            XSSimpleType.PRIMITIVE_DURATION,XSSimpleType.PRIMITIVE_FLOAT,XSSimpleType.PRIMITIVE_GDAY,XSSimpleType.PRIMITIVE_GMONTH,
            XSSimpleType.PRIMITIVE_GMONTHDAY,XSSimpleType.PRIMITIVE_GYEAR,XSSimpleType.PRIMITIVE_GYEARMONTH,
            XSSimpleType.PRIMITIVE_HEXBINARY,XSSimpleType.PRIMITIVE_NOTATION,XSSimpleType.PRIMITIVE_PRECISIONDECIMAL,
            XSSimpleType.PRIMITIVE_QNAME,XSSimpleType.PRIMITIVE_STRING,XSSimpleType.PRIMITIVE_TIME,XSSimpleType.WS_COLLAPSE,
            XSSimpleType.WS_PRESERVE,XSSimpleType.WS_REPLACE
    };
    */

    /**
     * A list of com.hp.hpl.jena.datatypes.xsd.XSDDatatype.
     * return all the XSDDatatype supported from jena.
     */
    public static final XSDDatatype allFormatsOfXSDDataTypes[] = new XSDDatatype[]{
            XSDDatatype.XSDstring, XSDDatatype.XSDENTITY, XSDDatatype.XSDID, XSDDatatype.XSDIDREF,
            XSDDatatype.XSDanyURI, XSDDatatype.XSDbase64Binary, XSDDatatype.XSDboolean, XSDDatatype.XSDbyte,
            XSDDatatype.XSDdate, XSDDatatype.XSDdateTime, XSDDatatype.XSDdecimal, XSDDatatype.XSDdouble,
            XSDDatatype.XSDduration, XSDDatatype.XSDfloat, XSDDatatype.XSDgDay, XSDDatatype.XSDgMonth,
            XSDDatatype.XSDgMonthDay, XSDDatatype.XSDgYear, XSDDatatype.XSDgYearMonth, XSDDatatype.XSDhexBinary,
            XSDDatatype.XSDint, XSDDatatype.XSDinteger, XSDDatatype.XSDlanguage, XSDDatatype.XSDlong,
            XSDDatatype.XSDName, XSDDatatype.XSDNCName, XSDDatatype.XSDnegativeInteger, XSDDatatype.XSDNMTOKEN,
            XSDDatatype.XSDnonNegativeInteger, XSDDatatype.XSDnonPositiveInteger, XSDDatatype.XSDnormalizedString,
            XSDDatatype.XSDNOTATION, XSDDatatype.XSDpositiveInteger, XSDDatatype.XSDQName, XSDDatatype.XSDshort,
            XSDDatatype.XSDtime, XSDDatatype.XSDtoken, XSDDatatype.XSDunsignedByte, XSDDatatype.XSDunsignedInt,
            XSDDatatype.XSDunsignedLong, XSDDatatype.XSDunsignedShort
    };

    public static final Resource allFormatsOfXSD[] = new Resource[]{XSD.anyURI,XSD.base64Binary,XSD.date,XSD.dateTime,
    XSD.dateTimeStamp,XSD.dayTimeDuration,XSD.decimal,XSD.duration,XSD.ENTITIES,XSD.ENTITY,XSD.gDay,XSD.gMonth,
            XSD.gMonthDay,XSD.gYear,XSD.gYearMonth,XSD.hexBinary,XSD.ID,XSD.IDREF,XSD.IDREFS,XSD.integer,
            XSD.language,XSD.Name,XSD.NCName,XSD.NMTOKEN,XSD.NMTOKENS,XSD.negativeInteger,XSD.nonNegativeInteger,
    XSD.nonPositiveInteger,XSD.normalizedString,XSD.NOTATION,XSD.positiveInteger,XSD.QName,XSD.time,XSD.token,
    XSD.unsignedByte,XSD.unsignedInt,XSD.unsignedLong,XSD.unsignedShort,XSD.xboolean,XSD.xbyte,XSD.xdouble,
    XSD.xfloat,XSD.xint,XSD.xlong,XSD.xshort,XSD.xstring,XSD.yearMonthDuration};

    /**
     * Method convert a {@link String} to {@link XSDDatatype}.
     *
     * @param uri the {@link String} uri of the XSDDatatype.
     * @return the {@link XSDDatatype} of the string uri if exists.
     */
    public static XSDDatatype toXSDDatatype(String uri) {
        for (XSDDatatype xsdDatatype : allFormatsOfXSDDataTypes) {
            if (xsdDatatype.getURI().equalsIgnoreCase(XSDDatatype.XSD + "#" + uri)) return xsdDatatype;
            if (xsdDatatype.getURI().replace(XSDDatatype.XSD, "")
                    .toLowerCase().contains(uri.toLowerCase())) return xsdDatatype;
        }
        logger.error("The XSD Datatype '" + uri + "' is not recognised");
        throw new IllegalArgumentException("The XSD Datatype '" + uri + "' is not recognised");
    }

    /** Read RDF data..
     * @param uriResource  URI to read from (includes file: and a plain file name).
     * @return the {@link StreamRDF}.
     */
    public static StreamRDF  toStreamRDF(String uriResource){
        StreamRDF streamRDF =new StreamRDFBase();
        RDFDataMgr.parse(streamRDF, uriResource) ;
        return streamRDF;
    }

    /** Send the triples of graph and it's prefix mapping to a StreamRDF, 
     * enclosed in stream.start()/steram.finish() 
     * @param output the {@link OutputStream} for the destination .
     * @param lang the {@link Lang} of Jena.
     * @param model the {@link Model} of Jena.
     * @return the {@link StreamRDF}.
     */
    public static StreamRDF toStreamRDF(OutputStream output,Lang lang,Model model){
        StreamRDF writer = StreamRDFWriter.getWriterStream(output, lang) ;
        StreamOps.graphToStream(model.getGraph(), writer);
        return writer;
    }

    /*public static toStreamRDF(ResultSet resultSet,String sparqlService){
        OutputStream os = new ByteArrayOutputStream();
        StreamRDF stream = StreamRDFWriter.getWriterStream(os, Lang.RDFTHRIFT);
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://data.open.ac.uk/sparql", "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?A ?B ?C WHERE {?A a ?B . ?A rdf:type ?C} LIMIT 100");

        Transform<QuerySolution, Iterator<Triple>> m = new Transform<QuerySolution, Iterator<Triple>>() {
            Integer rowIndex = 0;

            @Override
            public Iterator<Triple> convert(QuerySolution qs) {
                rowIndex++;
                String ns = "http://www.example.org/test/row#";
                String pns = "http://www.example.org/test/col#";
                Resource subject = ResourceFactory.createResource(ns + Integer.toString(rowIndex));
                Property property;
                List<Triple> list = new ArrayList<Triple>();
                Iterator<String> cn = qs.varNames();
                while (cn.hasNext()) {
                    String c = cn.next();
                    property = ResourceFactory.createProperty(pns + c);
                    list.add(new Triple(subject.asNode(), property.asNode(), qs.get(c).asNode()));
                }
                return list.iterator();
            }
        };
        Iterator<Triple> iter = WrappedIterator.createIteratorIterator( Iter.map( qe.execSelect(), m ));
        stream.start();
        StreamOps.sendTriplesToStream(iter, stream);
        stream.finish();
    }*/

    /**
     * Method convert a string to a rdfformat.
     *
     * @param strFormat string name of the RDFFormat.
     * @return rdfformat the RDFFormat with the same name.
     */
    public static RDFFormat toRDFFormat(String strFormat) {
        if (strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES") || strFormat.toUpperCase().contains("N3")) {
            strFormat = "N-Triples";
        }
        if (strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")) {
            strFormat = "Turtle";
        }
        //Collection<RDFFormat> allFormatsOfRDFFormat = RDFWriterRegistry.registered();
        for (RDFFormat rdfFormat : allFormatsOfRDFFormat) {
            if (rdfFormat.getLang().getName().equalsIgnoreCase(strFormat))
                return rdfFormat;
        }
        logger.error("The RDF format '" + strFormat + "' is not recognised");
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
    }

    /**
     * Method convert a string name of a RDFFormat to a language Lang.
     *
     * @param strFormat string name of a RDFFormat.
     * @return lang the language Lang for the same name.
     */
    public static Lang toLang(String strFormat) {
        if(strFormat.contains(".")) strFormat = strFormat.replace(".","");
        if (strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES") || strFormat.toUpperCase().contains("N3")) {
            strFormat = "N-Triples";
        }
        if (strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")) {
            strFormat = "Turtle";
        }
        for (Lang lang : allFormatsOfRiotLang) {
            String label = lang.getLabel();
            String name = lang.getName();

            if (lang.getName().equalsIgnoreCase(strFormat))
                return lang;
        }
        logger.error("The LANG format '" + strFormat + "' is not recognised");
        throw new IllegalArgumentException("The LANG format '" + strFormat + "' is not recognised");
    }

    /**
     * Method convert a string name of a RDFFormat to a language Lang.
     *
     * @param file  {@link File} to inpect for the RDFFormat.
     * @return {@link Lang} the language Lang for the same name.
     */
    public static Lang toLang(File file){
        return toLang(FileUtils.guessLang(file.getAbsolutePath()));
    }

    /**
     * Method to create a JENA Query from a String SPARQL Query.
     *
     * @param querySPARQL the String SPARQL Query.
     * @return the JENA Query object.
     */
    public static Query toQuery(String querySPARQL) {
        return QueryFactory.create(querySPARQL);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param lang the String of the Language.
     * @param rdfDatatype the RDFDatatype.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(Object value, String lang, RDFDatatype rdfDatatype){
        return LiteralLabelFactory.createLiteralLabel(String.valueOf(value),lang,rdfDatatype);
    }
    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @return the LiteralLabel Jena Object.
     */

    public static LiteralLabel toLiteralLabel(Object value){
        return LiteralLabelFactory.createTypedLiteral(value);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param rdfDatatype the RDFDatatype.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(String value,RDFDatatype rdfDatatype){
        return LiteralLabelFactory.create(value,rdfDatatype);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param lang the String of the Language.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(String value,String lang){
        return LiteralLabelFactory.create(value,lang);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param lang the String of the Language.
     * @param isXML the boolean value if a XML or not.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(String value,String lang,boolean isXML){
        return LiteralLabelFactory.create(value,lang,isXML);
    }

    /**
     * Method to print the resultSet to a a specific format of output.
     *
     * @param sparql             sparql query.
     * @param model              jena model.
     * @param fullPathOutputFile string to the path of the output file.
     * @param outputFormat       stirng of the output format.
     */
    private static void formatTheResultSetAndPrint(
            String sparql, Model model, String fullPathOutputFile, String outputFormat) {
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
                }
                /** deprecated with jena 3 */
                /*  else if (outputFormat.toLowerCase().contains("bio")) {
                    ResultSetFormatter.outputAsBIO(fos, results);
                } else if (outputFormat.toLowerCase().contains("rdf")) {
                    ResultSetFormatter.outputAsRDF(fos, "RDF/XML", results);
                }*/
                logger.info("... the file of triple to:" + fullPathOutputFile + " is been wrote!");
            } else if (outputFormat.toLowerCase().contains("ttl")) {
                Model resultModel = execSparqlConstructorOnModel(sparql, model);
                OUTLANGFORMAT = toLang(outputFormat);
                OUTRDFFORMAT = toRDFFormat(outputFormat);
                OUTFORMAT = outputFormat.toUpperCase();
                //Writer writer = new FileWriter(new File(fullPathOutputFile));
                //model.write(writer, outputFormat);
                writeModelToFile(fullPathOutputFile, resultModel, OUTFORMAT);
                logger.info("... the file of triple to:" + fullPathOutputFile + " is been wrote!");
            }
        } catch (Exception e) {
            logger.error("error during the writing of the file of triples:" + fullPathOutputFile + ":" + e.getMessage(), e);
        }

    }

    /**
     * Method to convert a RDF fikle of triples to a another specific format.
     *
     * @param file         file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    public static void convertFileTripleToAnotherFormat(File file, String outputFormat) throws IOException {
        convertTo(file, outputFormat);
    }

    /**
     * Method to convert a RDF fikle of triples to a another specific format.
     *
     * @param file         file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    private static void convertTo(File file, String outputFormat) throws IOException {
        Model m = loadFileTripleToModel(file,null,null);
        String newName =
                file.getAbsolutePath().replace(FileUtils.getFilenameExt(file.getAbsolutePath()),outputFormat);
                //FileUtilities.getFilenameWithoutExt(file) + "." + outputFormat.toLowerCase();
        String fullPath = file.getAbsolutePath();
        String newPath = fullPath.substring(0, fullPath.lastIndexOf(File.separator));
        String sparql;
        if (outputFormat.toLowerCase().contains("csv") || outputFormat.toLowerCase().contains("xml")
                || outputFormat.toLowerCase().contains("json") || outputFormat.toLowerCase().contains("tsv")
                || outputFormat.toLowerCase().contains("sse") || outputFormat.toLowerCase().contains("bio")
                || outputFormat.toLowerCase().contains("rdf") || outputFormat.toLowerCase().contains("bio")
                ) {
            sparql = "SELECT * WHERE{?s ?p ?o}";
        } else {
            sparql = "CONSTRUCT {?s ?p ?o} WHERE{?s ?p ?o}";
        }
        formatTheResultSetAndPrint(sparql, m, newPath + File.separator + newName, outputFormat.toLowerCase());
    }

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
     *
     * @param model    jena model.
     * @param subject  subject of the statement you want to check.
     * @param property string of property of the statement you want to check.
     * @return boolean result if exists or not.
     */
    public static boolean findProperty(Model model, Resource subject, String property) {
        boolean foundLocal = false;
        try {
            int pos = property.indexOf(":");
            String prefix = property.substring(0, pos);
            property = property.substring(pos + 1);
            String uri = namespaces.get(prefix);
            Property p = null;
            if (!"".equals(property)) {
                p = model.createProperty(uri, property);
            }
            StmtIterator iter = model.listStatements(
                    new SelectorImpl(subject, p, (RDFNode) null));
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
            logger.warn("Exception while try to find a property:" + e.getMessage(), e);
        }
        return foundLocal;
    }

    /**
     * Method to copy a Model to another Model with different uri and specific resources.
     *
     * @param model   jena model for the copy.
     * @param subject the resoures you want to copy.
     * @param uri     the uri for the new subject copied new model.
     * @return the copied model.
     */
    public static Model copyModel(Model model, Resource subject, String uri) {
        try {
            Model newModel = ModelFactory.createDefaultModel();
            Resource newSubject = newModel.createResource(uri);
            // Copy prefix mappings to the new model...
            newModel.setNsPrefixes(model.getNsPrefixMap());
            newModel = copyToModel(model, subject, newModel, newSubject);
            return newModel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to copy a Model to another Model.
     *
     * @param srcModel  model for the copy.
     * @param srcRsrc   resource of the model for the copy.
     * @param destModel model copied.
     * @param destRsrc  resource of the model copied.
     * @return the copied model.
     */
    public static Model copyToModel(Model srcModel, Resource srcRsrc, Model destModel, Resource destRsrc) {
        try {
            if (srcModel != null && destModel != null) {
                StmtIterator iter = srcModel.listStatements(
                        new SelectorImpl(srcRsrc, null, (RDFNode) null));
                while (iter.hasNext()) {
                    Statement stmt = iter.next();
                    RDFNode obj = stmt.getObject();
                    if (obj instanceof Resource) {
                        Resource robj = (Resource) obj;
                        if (robj.isAnon() && destModel != null) {
                            Resource destSubResource = destModel.createResource();
                            destModel = copyToModel(srcModel, robj, destModel, destSubResource);
                            obj = destSubResource;
                        }
                    }
                    if (destModel != null) {
                        Statement newStmt = destModel.createStatement(destRsrc, stmt.getPredicate(), obj);
                        destModel.add(newStmt);
                    }
                }
                return destModel;
            } else {
                logger.error("try to copy a NULL Jena Model with another Jena Model:" + srcModel + "->" + destModel);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to merge two Jena Model.
     *
     * @param model    first jena model.
     * @param newModel second jena model.
     * @return merged jena model.
     */
    public static Model mergeModel(Model model, Model newModel) {
        try {
            if (model != null && newModel != null) {
                ResIterator ri = newModel.listSubjects();
                while (ri.hasNext()) {
                    Resource newSubject = ri.next();
                    Resource subject;
                    if (!newSubject.isAnon() && model != null) {
                        subject = model.createResource(newSubject.getURI());
                        model = copyToModel(newModel, newSubject, model, subject);
                    }
                    //else : nevermind; copyToModel will handle this case recursively
                }
                return model;
            } else {
                logger.error("try to merge a NULL Jena Model with another Jena Model:" + model + "<->" + newModel);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to delete literal on a Jena Model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @param value    value of the literal of the statement to remove from Jena model.
     */
    public static void deleteLiteral(Model model, Resource subject, String property, String value) {
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
            logger.warn("Exception while try to delete a literal:" + e.getMessage(), e);
        }
    }

    /**
     * Method to query/read for a literal on a Jena Model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @return string of the literal.
     */
    public static String queryLiteral(Model model, Resource subject, String property) {
        return findLiteral(model, subject, property);
    }

    /**
     * Method to query/read for a literal on a Jena Model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @return string of the literal.
     */
    public static String findLiteral(Model model, Resource subject, String property) {
        int pos = property.indexOf(":");
        String prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            Property p;
            String uri = namespaces.get(prefix);
            if (!isNullOrEmpty(uri)) {
                p = model.createProperty(uri, property);
            } else {
                p = model.createProperty(property);
            }
            StmtIterator iter = model.listStatements(new SelectorImpl(subject, p, (RDFNode) null));
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                RDFNode obj = stmt.getObject();
                if (obj instanceof Literal) {
                    return obj.toString();
                }
            }
        } catch (Exception e) {
            logger.warn("you got a error while try to find the literal with Subject '"
                    + subject.toString() + "' and Property '" + property + "':" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Method to update a literal on a Jena model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @param value    value of the literal of the statement to remove from Jena model.
     * @return if true all the operation are done.
     */
    public static boolean updateLiteral(Model model, Resource subject, String property, String value) {
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
                RDFNode v = model.createLiteral(value);
                Statement s = model.createStatement(subject, p, v);
                model.add(s);
                return true;
            }
            logger.warn("The value is:" + value + " while the rdfValue is:" + rdfValue);
            return false;
        } catch (Exception e) {
            logger.warn("Exception while try to update a literal:" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to get/find the namespaces on a model jena.
     *
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
            logger.warn("Internal error: this can't happen.");
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
     * Method convert a {@link XSDDatatype} or {@link Model} or {@link Node}
     * or {@link ResultSet} to a {@link String}.
     *
     * @param jenaObject the @link XSDDatatype} or {@link Model} or {@link Node} or {@link ResultSet}.
     * @return the {@link String} rappresentation of the Object.
     */
    public static String toString(Object jenaObject){
        return toString(jenaObject,null,null);
    }

    /**
     * Method convert a {@link XSDDatatype} or {@link Model} or {@link Node}
     * or {@link ResultSet} to a {@link String}.
     *
     * @param jenaObject the @link XSDDatatype} or {@link Model} or {@link Node} or {@link ResultSet}.
     * @param outputFormat the {@link String} of the output format.
     * @param baseURI the {@link String} URI Graph Base.
     * @return the {@link String} rappresentation of the Object.
     */
    public static String toString(Object jenaObject,String outputFormat,String baseURI){
        if(jenaObject instanceof XSDDatatype){
            return ((XSDDatatype)jenaObject).getURI();
        }else if(jenaObject instanceof Model){
            try {
                if(baseURI != null){
                    //StringOutputStreamKit stringOutput = new StringOutputStreamKit();
                    Writer stringOutput = new StringWriter();
                    if (!isNullOrEmpty(outputFormat)) {
                        RDFFormat rdfFormat = toRDFFormat(outputFormat);
                        if (rdfFormat == null) {
                            outputFormat = "RDF/XML-ABBREV";
                        }
                    } else {
                        outputFormat = "RDF/XML-ABBREV";
                    }
                    ((Model)jenaObject).write(stringOutput, outputFormat, baseURI);
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
                    return xmlEncode(rawString);
                }else{
                    if (!isNullOrEmpty(outputFormat)) {
                        try {
                            RDFFormat rdfFormat = toRDFFormat(outputFormat);
                            outputFormat = rdfFormat.getLang().getName();
                        } catch (IllegalArgumentException e) {
                            outputFormat = "RDF/XML-ABBREV";
                        }
                    } else {
                        outputFormat = "RDF/XML-ABBREV";
                    }
                    StringWriter stringOut = new StringWriter();
                    //setCommonPrefixes(model);
                    ((Model)jenaObject).write(stringOut, outputFormat, RSS.getURI());
                    // http://base
                    stringOut.flush();
                    stringOut.close();
                    return stringOut.toString();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return "N/A";
            }
        }else if(jenaObject instanceof Node) {
            Node n = (Node) jenaObject;
            if (n.isURI()) {
                return "<" + n + ">";
            } else if (n.isBlank()) {
                return "<_:" + n + ">";
            } else if (n.isLiteral()) {
                String s;
                StringBuilder sb = new StringBuilder();
                sb.append("'");
                sb.append(escapeString(n.getLiteralValue().toString()));
                sb.append("'");

                s = n.getLiteralLanguage();
                if (s != null && s.length() > 0) {
                    sb.append("@");
                    sb.append(s);
                }
                s = n.getLiteralDatatypeURI();
                if (s != null && s.length() > 0) {
                    sb.append("^^<");
                    sb.append(s);
                    sb.append(">");
                }
                return sb.toString();
            } else {
                return "<" + n + ">";
            }
        }else if(jenaObject instanceof ResultSet){
            ResultSet results = (ResultSet) jenaObject;
            StringBuilder b = new StringBuilder();
            if(results.hasNext()){
                ByteArrayOutputStream go = new ByteArrayOutputStream ();
                ResultSetFormatter.out(go,results);
                String s;
                try {
                    s = new String(go.toByteArray(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    s = new String(go.toByteArray());
                }
                b.append(s).append('\n');
            }
            return b.toString();
        }else{
            logger.error("Can't convert to String the Object:"+jenaObject.getClass().getName()+" this function " +
                    "support Model,Node,XSDDatatype,ResultSet ");
            return "N/A";
        }
    }

    /**
     * Method to encode the xml text.
     * @param rawtext string of the xml text.
     * @return the encode string.
     */
     @SuppressWarnings("Duplicates")
     private static String xmlEncode(String rawtext){
        // Now turn that UTF-8 string into something "safe"
        String rdfString ="<?xml version='1.0' encoding='ISO-8859-1'?>\n";
        char[] sbuf = rawtext.toCharArray();
        int lastPos = 0;
        int pos = 0;
        while(pos < sbuf.length){
            char ch = sbuf[pos];
            if(!(ch == '\n' || (ch >= ' ' && ch <= '~'))){
                if(pos > lastPos){
                    String range =new String(sbuf,lastPos,pos - lastPos);
                    rdfString += range;
                }
                rdfString += "&#" + (int) ch + ";";
                lastPos = pos + 1;
            }
            pos++;
        }
        if(pos > lastPos) {
            String range =  new String(sbuf, lastPos, pos - lastPos);
            rdfString += range;
        }
        return rdfString;
    }//xmlEncode

    /**
     * Method to delete a specific resource , property on model jena
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
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

        StmtIterator iter = model.listStatements(new SelectorImpl(subject, p, (RDFNode) null));
        while (iter.hasNext()) {
            Statement stmt = iter.next();
            p = stmt.getPredicate();
            if (p.getNameSpace() == null) {
                continue;
            }
            if (p.getNameSpace().equals(uri)) {
                String type = "literal";
                if (stmt.getObject() instanceof Resource) {
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
     * Method to update a property on a model jena.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement to set.
     * @param value    value of the object of the statement.
     * @return jena model.
     */
    public static Model updateProperty(Model model, Resource subject, Property property, Object value) {
        try {
            /*
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {iterator.next(); iterator.remove();}
            */
            //... you must already create the resources
            //subject = model.getResource(redirectionURI);
            //subject = model.toResource("");
            //...Delete all the statements with predicate p for this resource from its associated model.
            subject.removeAll(property);
            subject.addProperty(property, (RDFNode) value);
            return model;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method for delete statement with specific proprety and literal on a Jena model.
     *
     * @param model           jena model.
     * @param subject         subject of the statement.
     * @param property        property of the statement to set.
     * @param languageLiteral language of the literal.
     * @param valueLiteral    value of the literal.
     * @return a jena model.
     */
    public Model deletePropertyAndObject(Model model, Resource subject, Property property,
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
     *
     * @param subject  subject of the statement.
     * @param property property of the statement to set.
     * @return value of the literal.
     */
    public static RDFNode findFirstPropertyValue(Resource subject, Property property) {
        Statement statement = subject.getProperty(property);
        if (statement == null) {
            logger.warn("The statement found is NULL.");
            return null;
        }
        return statement.getObject();
    }

    /**
     * Method to get/find the rdf type from a Resource.
     *
     * @param subject subject of the statement.
     * @return the string of the RdfType.
     */
    public static String findRdfType(Resource subject) {
        if (subject.isAnon()) {
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
     *
     * @param resource resource uri.
     * @param uri      new uri for the resource.
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
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to show a statement on a model jena to the console.
     *
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
     *
     * @param resource resource to print to the console.
     */
    public static void show(Resource resource) {
        StmtIterator iterator = resource.listProperties();
        show(iterator);
    }

    /**
     * Method to show a iterators on a model jena to the console.
     *
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
     *
     * @param model        jena model to print tot the console.
     * @param outputFormat string of the output format.
     */
    public static void show(Model model, String outputFormat) {
        logger.info(toString(model,outputFormat,null));
    }

    /**
     * Method to set the prefix on a model jena.
     *
     * @param model      jena model.
     * @param namespaces map of namespace with prefix.
     * @return the model jena with the prefix of namespace.
     */
    public static Model setCommonPrefixes(Model model, Map<String, String> namespaces) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            //namespaces.entrySet().stream().forEach((entry) -> {
            //model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
            //model.setNsPrefix("vis", "http://ideagraph.org/xmlns/idea/graphic#");
            model.setNsPrefix(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            //});
        }
        return model;
    }

    /**
     * Method to replace a resource on a model jena.
     *
     * @param oldResource resource to replace.
     * @param newResource the new resource.
     * @return if true all the operation are done.
     */
    public static boolean updateResource(Resource oldResource, Resource newResource) {
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
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to replace/update a subject/resource on a model jena.
     *
     * @param statement  statement with the resource to replace/update
     * @param newSubject new resource to add tot he model.
     * @return if true all the operation are done.
     */
    public static boolean updateSubjectResource(Statement statement, Resource newSubject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement = m.createStatement(newSubject,
                    statement.getPredicate(), statement.getObject());
            m.remove(statement);
            m.add(newStatement);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to replace/update a object resource.
     *
     * @param statement statement with the object to replace/update
     * @param newObject new value of the object
     * @return if true all the operation are done.
     */
    public static boolean updateObjectResource(Statement statement, Resource newObject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement = m.createStatement(statement.getSubject(),
                    statement.getPredicate(), newObject);
            m.remove(statement);
            m.add(newStatement);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method copies all properties across to new resource, just replaces type.
     *
     * @param resource the resource to update the type.
     * @param newType  the new type for the resource.
     * @return if true all the operation are done.
     */
    public static boolean updateTypeResource(Resource resource, Resource newType) {
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
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method approximate : returns first match.
     *
     * @param model   jena model.
     * @param rdfNode property to find.
     * @return resource you found.
     */
    public static Resource findParent(Model model, RDFNode rdfNode) {
        if (rdfNode instanceof Property) {
            return findParentResource(model, (Property) rdfNode);
        }
        return findParentProperty(model, rdfNode);
    }

    /**
     * Method  returns predicate of first statement with matching object.
     *
     * @param model   jena model.
     * @param rdfNode property to find.
     * @return poroperty you found.
     */
    public static Property findParentProperty(Model model, RDFNode rdfNode) {
        Statement statement = findParentStatement(model, rdfNode);
        if (statement == null) {
            logger.warn("The Statement founded is NULL.");
            return null;
        }
        return statement.getPredicate();
    }

    /**
     * Method approximate : returns first statement with matching object.
     *
     * @param model   jena model.
     * @param rdfNode resource to find.
     * @return the statement you found.
     */
    public static Statement findParentStatement(Model model, RDFNode rdfNode) {
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
        logger.warn("The Statement founded is NULL.");
        return null;
    }

    /**
     * Method approximate : returns object of first statement with matching predicate.
     *
     * @param model    jena model.
     * @param property property to find.
     * @return resource you found.
     */
    public static Resource findParentResource(Model model, Property property) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            //changed for Jena 2
            if (property.equals(statement.getPredicate())) {
                return statement.getSubject();
            }
        }
        logger.warn("The Statement founded is NULL.");
        return null;
    }

    /**
     * Method approximate : gets first match (predicate and object).
     *
     * @param model    jena model.
     * @param property property to find.
     * @param object   object to find.
     * @return the subject you found.
     */
    public static Resource findSubject(Model model, Property property, RDFNode object) {
        Statement statement = findStatement(model, property, object);
        if (statement == null) {
            logger.warn("The Statement founded is NULL.");
            return null;
        }
        return statement.getSubject();
    }

    /**
     * Method approximate : gets first match (predicate and object).
     *
     * @param model    jena model.
     * @param property porperty to find.
     * @param object   object to find.
     * @return statemn you found.
     */
    public static Statement findStatement(Model model, Property property, RDFNode object) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (property.equals(statement.getPredicate()) && object.equals(statement.getObject())) {
                return statement;
            }
        }
        logger.warn("The Statement founded is NULL.");
        return null;
    }

    /**
     * Method to update a Property on a Specific Resource.
     * @param resource the resource to update.
     * @param property the Property to set.
     * @param object the Object value to reference with the new property.
     */
    public static void updateProperty(Resource resource, Property property, Resource object) {
        try {
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            resource.addProperty(property, object);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * Method approximate : gets first match (object).
     *
     * @param resource resource to find.
     * @param property property to find.
     * @return string of the proerty you found.
     */
    public static RDFNode findObject(Resource resource, Property property) {
        RDFNode node = findFirstPropertyValue(resource, property);
        if (node == null) {
            logger.warn("The RDFNode founded is NULL.");
            return null;
        }
        return node;
    }

    /**
     * Method to set/replace/update a property-object.
     *
     * @param resource resource to find.
     * @param property property to find.
     * @param object   value of the object you found.
     * @return if true all the operation are done.
     */
    public static boolean updatePropertyObject(Resource resource, Property property, Resource object) {
        try {
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            resource.addProperty(property, object);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method for replace/update a literal value on the jena model.
     *
     * @param model   jena model.
     * @param literal literal to update.
     * @param value   new value of the literal.
     * @return if true all the operation are done.
     */
    public static boolean updateLiteralValue(Model model, Literal literal, String value) {
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    private static RDFNode createRDFNodeBase(
            Object modelOrUri, Object subjectIri, Lang lang, RDFDatatype rdfDatatype, Boolean isXmL) {
        try {
            if(subjectIri == null){
                logger.warn("Try to create a DatatypeProperty from a 'NULL' value");
                return null;
            }
            if(subjectIri instanceof RDFNode){
                return (RDFNode) subjectIri;
            }
            if (subjectIri instanceof Node) {
                if (modelOrUri == null) return createModel().asRDFNode((Node) subjectIri);
                else if (modelOrUri instanceof Model)
                    return ModelUtils.convertGraphNodeToRDFNode(toNode(subjectIri), (Model) modelOrUri);
                //else return (RDFNode) subjectIri;
            } else if (subjectIri instanceof LiteralLabel) {
                return toRDFNode(toNode(subjectIri));
            } else if (lang != null && rdfDatatype != null) {
                return toRDFNode(toNode(String.valueOf(subjectIri), lang.getLabel(), rdfDatatype));
            } else if (lang != null && isXmL != null) {
                return toRDFNode(toNode(String.valueOf(subjectIri), lang.getLabel(), isXmL));
            } else if (rdfDatatype != null) {
                return toRDFNode(toNode(String.valueOf(subjectIri), rdfDatatype));
            }

            if(subjectIri instanceof String) {
                subjectIri = toId(subjectIri);
                if (isIRI(subjectIri) || isUri(subjectIri)) {
                    if (modelOrUri != null && modelOrUri instanceof Model) {
                        try {
                            //return ((Model)modelOrUri).asRDFNode(NodeUtils.asNode(toIri(subjectIri)));
                            return toRDFNode(toNode(toIri(subjectIri)), ((Model) modelOrUri));
                        } catch (Exception e) {
                            //return ((Model)modelOrUri).asRDFNode(NodeUtils.asNode(toString(subjectIri)));
                            return toRDFNode(toNode(String.valueOf(subjectIri)), ((Model) modelOrUri));
                        }
                    } else {
                        try {
                            return toRDFNode(toNode(String.valueOf(toIri(subjectIri))));
                        } catch (Exception e) {
                            return toRDFNode(toNode(String.valueOf(subjectIri)));
                            //return ((Model)modelOrUri).asRDFNode(NodeUtils.asNode(toString(subjectIri)));
                        }
                    }
                } else { //maybe is a Literal
                    if (modelOrUri != null && modelOrUri instanceof Model) {
                        //return ((Model) modelOrUri).asRDFNode(NodeFactory.createLiteral(String.valueOf(subjectIri)));
                        return toRDFNode(toNode(String.valueOf(subjectIri)), ((Model) modelOrUri));
                    } else{
                        return toRDFNode(toNode(String.valueOf(subjectIri)));
                    }
                }
            }else{
                logger.error("The RDFNode Datatype '" + subjectIri.getClass().getName() + "' is not recognised");
                return null;
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode toRDFNode(Object modelOrUri, Object subjectIri) {
        return createRDFNodeBase(null, subjectIri, null, null, null);
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode toRDFNode(Object modelOrUri, LiteralLabel subjectIri) {
        return createRDFNodeBase(null, subjectIri, null, null, null);
    }

    /**
     * Method utility: create new property for a Model.
     * href: http://willware.blogspot.it/2010/02/jena-node-versus-rdfnode.html
     *
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode toRDFNode(Object subjectIri) {
        return createRDFNodeBase(null, subjectIri, null, null, null);
    }

    /**
     * Method to convert a Node to a RDFNode.
     * href: http://willware.blogspot.it/2010/02/jena-node-versus-rdfnode.html
     *
     * @param node  the Node to convert.
     * @param model the Model fo reference for create the RDFNode.
     * @return the RDFNode result.
     */
    public static RDFNode toRDFNode(Object node,Model model) {
        return createRDFNodeBase(model,node, null, null, null);
    }

    /**
     * Method to create a Jena Resource.
     *
     * @param localNameOrUri the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    public static Resource toResource(Object localNameOrUri) {
        return createResourceBase(null, localNameOrUri);
    }

    public static Resource toResource(Object localNameOrUri,Model model) {
        return createResourceBase(model, localNameOrUri);
    }

    public static Resource toResource(Object graphUriOrModel, Object localNameOrUri) {
        return createResourceBase(graphUriOrModel, localNameOrUri);
    }

    /**
     * Method to create a Jena Resource.
     *
     * @param graphUriOrModel the String iri or the Jena Model.
     * @param localNameOrUri  the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    private static Resource createResourceBase(Object graphUriOrModel, Object localNameOrUri) {
        //if (!(localNameOrUri instanceof RDFNode)) localNameOrUri = toId(localNameOrUri);
        if(localNameOrUri == null){
            logger.warn("Try to create a Resource from a 'NULL' value");
            return null;
        }
        if(localNameOrUri instanceof Resource){
            return (Resource) localNameOrUri;
        }
        if (graphUriOrModel == null) {
           if(localNameOrUri instanceof XSDDatatype) {
               return ResourceFactory.createResource(((XSDDatatype) localNameOrUri).getURI());
           }else if(localNameOrUri instanceof XSD) {
               for (Resource xsd : allFormatsOfXSD) {
                   if (xsd.getURI().equalsIgnoreCase(XSD.NS + String.valueOf(localNameOrUri)))
                       return xsd;
                   if (xsd.getURI().replace(XSDDatatype.XSD, "")
                           .toLowerCase().contains(String.valueOf(localNameOrUri).toLowerCase()))
                       return xsd;
               }
           }else if(localNameOrUri instanceof  RDFDatatype){
               return ResourceFactory.createResource(((RDFDatatype) localNameOrUri).getURI());
           }else if (localNameOrUri instanceof RDFNode) {
                RDFNode rdfNode = (RDFNode) localNameOrUri;
                if (rdfNode.isResource() || rdfNode.isURIResource() || rdfNode.isAnon())
                    return rdfNode.asResource();
           } else {
                localNameOrUri = toId(localNameOrUri);
                if (isIRI(localNameOrUri) || isUri(localNameOrUri))
                    return ResourceFactory.createResource(String.valueOf(localNameOrUri));
           }
        } else {
            if (graphUriOrModel instanceof String) {
                String s = String.valueOf(graphUriOrModel);
                if (s.endsWith("/") || s.endsWith("#")) {
                    String uri = s + localNameOrUri;
                    if (isIRI(uri) || isUri(uri)) return ResourceFactory.createResource(uri);
                } else {
                    String uri = s + "/" + localNameOrUri;
                    if (isIRI(uri) || isUri(uri)) return ResourceFactory.createResource(uri);
                }
            } else if (graphUriOrModel instanceof Model) {
                return toRDFNode(graphUriOrModel, localNameOrUri).asResource();
            }
        }
        logger.error("The Resource Datatype '" + localNameOrUri.getClass().getName() + "' is not recognised");
        return null;
    }

    /**
     * Method to create a Jena Property.
     *
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param predicateUri       the String name local Graph or the String iri of the subject.
     * @param impl               if true use the PredicateImpl to create the predicate.
     * @return the Jena Predicate.
     */
    private static Property createPropertyBase(Object stringOrModelGraph, Object predicateUri, boolean impl) {
        try {
            if(predicateUri == null){
                logger.warn("Try to create a Property from a 'NULL' value");
                return null;
            }
            if(predicateUri instanceof Property){
                return (Property) predicateUri;
            }
            if (stringOrModelGraph == null) {
                if (impl) {
                    if (predicateUri instanceof RDFNode)
                        return (Property) ((RDFNode) predicateUri).asResource();
                    else if (isIRI(predicateUri) || isUri(predicateUri))
                        return new PropertyImpl(String.valueOf(predicateUri));
                } else {
                    if (predicateUri instanceof RDFNode)
                        return (Property) ((RDFNode) predicateUri).asResource();
                    else if (isIRI(predicateUri) || isUri(predicateUri))
                        return ResourceFactory.createProperty(String.valueOf(predicateUri));
                    else
                        return ResourceFactory.createProperty(String.valueOf(predicateUri));

                }
            }
            if (isStringNoEmpty(stringOrModelGraph)) {
                if (!String.valueOf(stringOrModelGraph).endsWith("/") || !String.valueOf(stringOrModelGraph).endsWith("#")) {
                    stringOrModelGraph = stringOrModelGraph + "/";
                }
                if (impl) {
                    if (isIRI(predicateUri) || isUri(predicateUri) || String.valueOf(stringOrModelGraph).isEmpty()) {
                        return new PropertyImpl(String.valueOf(predicateUri));
                    } else if (isStringNoEmpty(stringOrModelGraph) &&
                            isIRI(String.valueOf(stringOrModelGraph) + "/" + String.valueOf(predicateUri))) {
                        return new PropertyImpl(String.valueOf(stringOrModelGraph), String.valueOf(predicateUri));
                    }
                } else {
                    if (isIRI(predicateUri) || isUri(predicateUri) || String.valueOf(stringOrModelGraph).isEmpty()) {
                        return ResourceFactory.createProperty(String.valueOf(predicateUri));
                    } else if (isStringNoEmpty(stringOrModelGraph) &&
                            isIRI(String.valueOf(stringOrModelGraph) + "/" + String.valueOf(predicateUri))) {
                        return ResourceFactory.createProperty(String.valueOf(stringOrModelGraph), String.valueOf(predicateUri));
                    }
                }
            } else if (stringOrModelGraph instanceof Model && isStringNoEmpty(predicateUri)) {
                return toRDFNode(stringOrModelGraph, predicateUri).as(Property.class);
            }
            logger.warn("The Property Datatype '" + predicateUri.getClass().getName() + "' is not recognised");
            return null;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to create a Jena Property.
     *
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(Object stringOrModelGraph, String localNameOrSubject) {
        return createPropertyBase(stringOrModelGraph, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(Object stringOrModelGraph, Object localNameOrSubject) {
        return createPropertyBase(stringOrModelGraph, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(String localNameOrSubject) {
        return createPropertyBase(null, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(Object localNameOrSubject) {
        return createPropertyBase(null, localNameOrSubject, false);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param model          the Model jean where create the Literal.
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype       the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    private static Literal createLiteralBase(Model model, Object stringOrObject, RDFDatatype datatype) {
        try {
            if(stringOrObject == null){
                logger.warn("Try to create a Literal from a 'NULL' value");
                return null;
            }
            if(stringOrObject instanceof Literal){
                return (Literal) stringOrObject;
            }
            if (model == null) {
                if (stringOrObject instanceof RDFNode) {
                    RDFNode x = (RDFNode) stringOrObject;
                    if (x.asLiteral().getDatatype().equals(XSDDatatype.XSDstring)) {
                        return ResourceFactory.createPlainLiteral(x.asLiteral().getLexicalForm());
                        //return ((RDFNode) stringOrObject).asLiteral();
                    } else if (x.isURIResource()) {
                        //try to avoid this because is wrong but works....
                        return (Literal) x.asResource();
                    } else {
                        return ResourceFactory.createTypedLiteral(stringOrObject);
                    }
                } else if (stringOrObject instanceof String) {
                    if (datatype != null)
                        return ResourceFactory.createTypedLiteral(String.valueOf(stringOrObject), datatype);
                    else return ResourceFactory.createPlainLiteral(String.valueOf(stringOrObject));
                } else {
                    if (datatype != null)
                        return ResourceFactory.createTypedLiteral(String.valueOf(stringOrObject), datatype);
                }
                logger.warn("The Literal  Datatype '" + stringOrObject.getClass().getName() + "' is not recognised");
                return ResourceFactory.createTypedLiteral(stringOrObject);
            } else if (stringOrObject instanceof RDFNode) {
                if (datatype != null) return model.createTypedLiteral(stringOrObject, datatype);
                else {
                    RDFNode x = (RDFNode) stringOrObject;
                    if (x.asLiteral().getDatatype().equals(XSDDatatype.XSDstring)) {
                        return model.createLiteral(x.toString());
                    } else if (x.isURIResource()) {
                        //try to avoid this because is wrong but works....
                        return (Literal) x.asResource();
                    } else {
                        return model.createTypedLiteral(stringOrObject);
                    }
                }
            } else if (stringOrObject instanceof String) {
                if (datatype != null) return model.createTypedLiteral(String.valueOf(stringOrObject), datatype);
                else return model.createLiteral(String.valueOf(stringOrObject));
            } else {
                if (datatype != null) return model.createTypedLiteral(stringOrObject, datatype);
            }
            logger.warn("The Literal  Datatype '" + stringOrObject.getClass().getName() + "' is not recognised");
            return model.createTypedLiteral(stringOrObject);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param model          the Model Jena where create the Literal.
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype       the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Model model, Object stringOrObject, RDFDatatype datatype) {
        return createLiteralBase(model, stringOrObject, datatype);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype       the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Object stringOrObject, RDFDatatype datatype) {
        return createLiteralBase(null, stringOrObject, datatype);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype        the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Object stringOrObject, String datatype) {
        return createLiteralBase(null, stringOrObject, toRDFDatatype(datatype));
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Object stringOrObject) {
        return createLiteralBase(null, stringOrObject, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param model     the Jena Model.
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @return Statement.
     */
    public static Statement toStatement(Model model, Object subject, Object predicate, Object object) {
        return createStatementBase(model, subject, predicate, object, null, null);
    }

    public static Statement toStatement(
            Model model, Object subject, Object predicate, Object object, String graphUri, XSDDatatype xsdDatatype) {
        return createStatementBase(model, subject, predicate, object, graphUri, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param model       the Jena Model.
     * @param subject     the iri subject. String,RDFNode,URI
     * @param predicate   the iri predicate.
     * @param object      the iri object.
     * @param graphUri    the iri of the graph.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    private static Statement createStatementBase(
            Model model, Object subject, Object predicate, Object object, Object graphUri, XSDDatatype xsdDatatype) {
        try {
            if (subject == null || predicate == null || object == null) {
                logger.warn("Try to create a Statement from a 'NULL' value");
                return null;
            }

            if (subject instanceof Triple) {
                if (model != null) return ModelUtils.tripleToStatement(model, (Triple) subject);
                else return ModelUtils.tripleToStatement(createModel(), (Triple) subject);
            } else if (graphUri != null) {
                if (isStringOrUriEmpty(graphUri) && xsdDatatype == null) {
                    return ResourceFactory.createStatement(toResource(graphUri, subject),
                            toProperty(graphUri, predicate), toLiteral(object));
                } else if (xsdDatatype != null) {
                    return ResourceFactory.createStatement(toResource(graphUri, subject),
                            toProperty(graphUri, predicate), toLiteral(object, xsdDatatype));
                }
            } else {
                if (xsdDatatype != null) {
                    return ResourceFactory.createStatement(toResource(subject),
                            toProperty(predicate), toLiteral(object, xsdDatatype));
                } else {
                    return ResourceFactory.createStatement(toResource(subject),
                            toProperty(predicate), toLiteral(object));
                }
            }
            logger.error("The Statement Datatype '" + subject.getClass().getName() + "' is not recognised");
            return null;
        /*return model.createStatement(toResource(model, subject),
                toProperty(model, predicate), toLiteral(object, xsdDatatype));*/
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param model     the Jena Model.
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @param graphUri  the iri of the graph.
     * @return Statement.
     */
    public static Statement toStatement(Model model, Object subject, Object predicate, Object object, String graphUri) {
        return createStatementBase(model, subject, predicate, object, graphUri, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @param graphUri  the iri of the graph.
     * @return Statement.
     */
    public static Statement toStatement(Object subject, Object predicate, Object object, String graphUri) {
        return createStatementBase(null, subject, predicate, object, graphUri, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @return Statement.
     */
    public static Statement toStatement(Object subject,Object predicate, Object object) {
        return createStatementBase(null, subject, predicate, object, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject     the iri subject.
     * @param predicate   the iri predicate.
     * @param object      the iri object.
     * @param graphUri    the URI to the graph base of the ontology.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement toStatement(Object subject, Object predicate, Object object, String graphUri, XSDDatatype xsdDatatype) {
        return createStatementBase(null, subject, predicate, object, graphUri, xsdDatatype);
    }

    public static Statement toStatement(Object triple) {
        return createStatementBase(null, triple, null, null, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the {@link Object} iri subject.
     * @param predicate the {@link Object} iri predicate.
     * @param object    the {@link Object} iri object.
     * @param xsdDatatype the {@link XSDDatatype}
     * @return the {@link Statement} of Jena.
     */
    public static Statement toStatement(Object subject, Object predicate, Object object,XSDDatatype xsdDatatype) {
        return createStatementBase(null, subject, predicate, object, null, xsdDatatype);
    }


    public static Statement toStatement(Model model, Object triple) {
        return createStatementBase(model, triple, null, null, null, null);
    }

    /**
     * Method to create a Jena Dataset for the SPARQL query.
     *
     * @param dftGraphURI    the URI of the location of the resource with the triples.
     * @param namedGraphURIs the URI's of all locations with name.
     * @return the JENA Dataset.
     */
    public static Dataset createDataSet(String dftGraphURI, List<String> namedGraphURIs) {
       /* String dftGraphURI = "file:default-graph.ttl" ;
        List namedGraphURIs = new ArrayList() ;
        namedGraphURIs.add("file:named-1.ttl") ;
        namedGraphURIs.add("file:named-2.ttl") ;*/
        return DatasetFactory.create(dftGraphURI, namedGraphURIs);
    }

    /**
     * Method utility: create new default Jena Model.
     *
     * @return Jena Model.
     */
    public static Model createModel() {
        return ModelFactory.createDefaultModel();
    }

    /**
     * Method utility: create new default Jena Graph.
     *
     * @return Jena Graph.
     */
    public static Graph createGraph() {
        return GraphFactory.createDefaultGraph();
    }

    /**
     * Method to convert a Jena TripleMatch to the Jena Triple.
     *
     * @param statement the Statement Jena.
     * @return the Jena triple.
     */
    public static Triple toTriple(Statement statement) {
        return statement.asTriple();
    }

    /**
     * Method to convert a set of Jena Graph Nodes to a Jena Graph Triple Object.
     * old name : createTriple.
     *
     * @param subject   the Jena Graph Node Subject of the triple.
     * @param predicate the Jena Graph Node Predicate of the triple.
     * @param object    the Jena Graph Node Object of the triple.
     * @return the Jena Graph Triple Object setted with the content of the jena Graph Nodes.
     */
    public Triple toTriple(Object subject, Object predicate, Object object) {
        return new Triple(toNode(subject), toNode(predicate), toNode(object));
    }

    /**
     * Method to convert a String uri to a jena Graph Node.
     * old name : createNode.
     * href: http://willware.blogspot.it/2010/02/jena-node-versus-rdfnode.html
     *
     * @param resource any element on API Jena can be converted to a Node.
     * @return the Jena Graph Node.
     */
    private static Node createNodeBase(Object resource, String lang, RDFDatatype rdfDatatype, Boolean xml){
        try {
            if (resource == null) {
                logger.warn("Try to create a Node from a 'NULL' value");
                return null;
            }
            if (resource instanceof Node) {
                return (Node) resource;
            }
            if (lang != null && rdfDatatype != null) {
                return NodeFactory.createLiteral(String.valueOf(resource), lang, rdfDatatype);
            } else if (rdfDatatype != null) {
                return NodeFactory.createLiteral(String.valueOf(resource), rdfDatatype);
            } else if (lang != null && xml != null) {
                return NodeFactory.createLiteral(String.valueOf(resource), lang, xml);
            } else if (lang != null) {
                return NodeFactory.createLiteral(String.valueOf(resource), lang);
            } else {
                if (resource instanceof Literal) {
                    return ((Literal) resource).asNode();
                } else if (resource instanceof Resource) {
                    return ((Resource) resource).asNode();
                } else if (resource instanceof RDFNode) {
                    return ((RDFNode) resource).asNode();
                } else if (resource instanceof LiteralLabel) {
                    return NodeFactory.createLiteral((LiteralLabel) resource);
                } else if (resource instanceof virtuoso.sql.ExtendedString) {
                    virtuoso.sql.ExtendedString vs = (virtuoso.sql.ExtendedString) resource;
                    if (vs.getIriType() == virtuoso.sql.ExtendedString.IRI
                            && (vs.getStrType() & 0x01) == 0x01) {
                        if (vs.toString().indexOf("_:") == 0)
                            return NodeFactory.createBlankNode(BlankNodeId.create(String.valueOf(vs)
                                    .substring(2))); // _:
                        else
                            return NodeFactory.createURI(String.valueOf(vs));

                    } else if (vs.getIriType() == virtuoso.sql.ExtendedString.BNODE) {
                        return NodeFactory.createBlankNode(BlankNodeId.create(String.valueOf(vs).substring(9))); // nodeID://

                    } else {
                        return NodeFactory.createLiteral(String.valueOf(vs));
                    }
                } else if (resource instanceof virtuoso.sql.RdfBox) {
                    virtuoso.sql.RdfBox rb = (virtuoso.sql.RdfBox) resource;
                    String rb_type = rb.getType();
                    if (rb_type != null) {
                        return NodeFactory.createLiteral(String.valueOf(rb), rb.getLang(), toRDFDatatype(rb_type));
                    } else {
                        return NodeFactory.createLiteral(String.valueOf(rb), rb.getLang());
                    }
                } else if (resource instanceof java.lang.Integer) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDinteger));
                } else if (resource instanceof java.lang.Short) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDinteger));
                } else if (resource instanceof java.lang.Float) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDfloat));
                } else if (resource instanceof java.lang.Double) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDdouble));
                } else if (resource instanceof java.math.BigDecimal) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDdecimal));
                } else if (resource instanceof java.sql.Blob) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDhexBinary));
                } else if (resource instanceof java.sql.Date) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDdate));
                } else if (resource instanceof java.sql.Timestamp) {
               /* return NodeFactory.createLiteral(
                        Timestamp2String((java.sql.Timestamp) resource), toRDFDatatype(XSDDatatype.XSDdateTime));*/
                    return NodeFactory.createLiteral(
                            Timestamp2String((java.sql.Timestamp) resource), toRDFDatatype(XSDDatatype.XSDdateTimeStamp));
                } else if (resource instanceof java.sql.Time) {
                    return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDdateTime));
                } else if (resource instanceof String) {
                    if (isIRI(resource)) {
                        return NodeUtils.asNode(String.valueOf(resource));
                    } else if (StringUtilities.isURI(resource) || StringUtilities.isURL(resource)) {
                        return NodeFactory.createURI(String.valueOf(resource));
                    } else if (StringUtilities.isDouble(resource)) {
                        return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDdouble));
                    } else if (StringUtilities.isFloat(resource)) {
                        return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDfloat));
                    } else if (StringUtilities.isInt(resource)) {
                        return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDinteger));
                    } else if (StringUtilities.isNumeric(resource)) {
                        return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDinteger));
                    } else {
                        return NodeFactory.createLiteral(String.valueOf(resource), toRDFDatatype(XSDDatatype.XSDstring));
                    }
                }else{
                    logger.error("The Node Datatype '" + resource.getClass().getName() + "' is not recognised");
                    return null;
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static Node toNode(Object resource,String lang,RDFDatatype rdfDatatype){
        return createNodeBase(resource,null,rdfDatatype,null);
    }

    public static Node toNode(Object resource,String lang,boolean isXml){
        return createNodeBase(resource,null,null,isXml);
    }

    public static Node toNode(Object resource){
        return createNodeBase(resource,null,null,null);
    }

    public static Node toNode(Object resource,RDFDatatype rdfDatatype){
        return createNodeBase(resource,null,rdfDatatype,null);
    }

    /**
     * Method to convert a Jena Model to a Jena Graph.
     *
     * @param model the jena Model
     * @return the Jena Graph.
     */
    public Graph toGraph(Model model) {
        return model.getGraph();
    }

    /**
     * Method to convert a Jena DataSet to a Jena Graph.
     *
     * @param dataSet the Jena dataSet.
     * @return the Jena Graph.
     */
    public Graph toGraph(Dataset dataSet) {
        return dataSet.asDatasetGraph().getDefaultGraph();
    }

    /**
     * Method to convert a Jena DataSet to a Jena Graph.
     *
     * @param dataSet   the Jena dataSet.
     * @param nodeGraph the Node Jena .
     * @return the Jena Graph.
     */
    public Graph toGraph(Dataset dataSet, Node nodeGraph) {
        return dataSet.asDatasetGraph().getGraph(nodeGraph);
    }

    /**
     * Method to convert a Jena Graph to a Jena Model.
     *
     * @param graph the Jena Graph.
     * @return the Jena Model.
     */
    public static Model toModel(Graph graph) {
        return ModelFactory.createModelForGraph(graph);
    }

    /**
     * Method to load a {@link File} of Triple to a {@link Model}.
     * @param file the {@link File}.
     * @param lang the {@link Lang} of Jena.
     * @return the {@link Model}.
     */
    public static Model toModel(File file,Lang lang) {
       /* try {
            Model model = ModelFactory.createDefaultModel();
            InputStream stream = file.toURI().toURL().openStream();
            return  model.read(stream, null,lang.getLabel().toUpperCase());
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }*/
        return loadFileTripleToModel(file,null,lang.getLabel().toUpperCase());
    }

   /* public Model toModel(String uri) {
        Model m = Repository.Instance().getNamedModel(uri);
        if (m == null)return null;
        return m;
    }*/

    /**
     * Method to convert a Jena DataSet to a Jena Model.
     *
     * @param dataSet the Jena dataSet.
     * @return the Jena Model.
     */
    public static Model toModel(Dataset dataSet) {
        return dataSet.getDefaultModel();
    }

    /**
     * Method to convert a Jena DataSet to a Jena Model.
     *
     * @param dataSet the Jena dataSet.
     * @param uri     the String URI of the Graph base to use.
     * @return the Jena Model.
     */
    public static Model toModel(Dataset dataSet, String uri) {
        return dataSet.getNamedModel(uri);
    }

    /**
     * Method to load a OWL {@link File} to a {@link OntModel} of Jena.
     * @param owlFile the {@link File} OWL to load.
     * @return the {@link OntModel} of Jena.
     */
    public static OntModel toOntoModel(File owlFile){
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(owlFile.toURI().toString());
            ontoModel.read(in, null);
            logger.info("Ontology " + owlFile + " loaded.");
            return ontoModel;
        }
        catch (Exception je) {
            logger.error(je.getMessage(),je);
            return null;
        }
    }

    /**
     * Method to get a Dataset from a existent Jena Model.
     * old_name:getDataSetFromModel
     * @param model the Jena Model.
     * @return the Dataset extract from Jena Model.
     */
    public static Dataset toDataset(Model model) {
        Dataset dataset = DatasetFactory.createGeneral();
        dataset.setDefaultModel(model);
        return dataset;
    }

    /**
     * Method to get a Dataset from a existent List of Jena Models.
     * old_name: getDataSetFromListOfModel
     * @param baseModel the Jena Model.
     * @param listModel the Map of Model with the specific uri to add to the new Dataset.
     * @return the Dataset extract from a list of Jena Model.
     */
    public static Dataset toDataset(Model baseModel, Map<String, Model> listModel) {
        Dataset dataset = DatasetFactory.createGeneral();
        dataset.setDefaultModel(baseModel);
        for (Map.Entry<String, Model> entry : listModel.entrySet()) {
        //listModel.entrySet().stream().forEach((entry) -> {
            dataset.addNamedModel(entry.getKey(), entry.getValue());
        //});
        }
        return dataset;
    }

    /**
     * Method to convert a Model to a DataSet.
     *
     * @param graph the Graph Jena to convert.
     * @return the DataSet Jena .
     */
    public static Dataset toDataset(Graph graph) {
        return new DatasetImpl(toModel(graph));
    }

    /**
     * Method to add a List of jena Graph triple to a Jena Graph Object.
     *
     * @param triples the List of jena Graph triples.
     * @param graph   the jena Graph Object.
     */
    public void addTriplesToGraph(List<Triple> triples, Graph graph) {
        GraphUtil.add(graph,triples);
    }

    //----------------------------------------
    //NEW METHODS
    //----------------------------------------

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     *
     * @param query the Query Select to analyze.
     * @param model the Jena Model.
     * @return the Long execution time for evaluate the query.
     */
    public static Long getExecutionQueryTime(String query, Model model) {
        return getExecutionQueryTime(toQuery(query), model);
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     *
     * @param query the Query Select to analyze.
     * @param model the Jena Model.
     * @return the Long execution time for evaluate the query.
     */
    public static Long getExecutionQueryTime(Query query, Model model) {
        try {
            Timer timer = new Timer();
            //Dataset ds = qexec.getDataset();
            //Dataset ds = toDataset(model);
            long calculate;
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            if (query.isSelectType()) {
                timer.startTimer();
                qexec.execSelect();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else if (query.isConstructType()) {
                timer.startTimer();
                qexec.execConstruct();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else if (query.isAskType()) {
                timer.startTimer();
                qexec.execAsk();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else if (query.isDescribeType()) {
                timer.startTimer();
                qexec.execDescribe();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else {
                calculate = 0L;
            }
            logger.info("Query JENA Model result(s) in " + calculate + "ms.");
            return calculate;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return 0L;
        }
    }

    /**
     * This Jena example re-uses named graphs stored in a TDB model as the imports in an ontology.
     * href: https://gist.github.com/ijdickinson/3830267
     *
     * @param graphName the String Graph Name.
     * @param model     the Model Jena.
     * @return the OntologyModel.
     */
    public static OntModel importModelToTDBModel(String graphName, Model model) {
        //Model model = createModel();
        //Initialise the local TDB image if necessary.
        String tdbPath = "./target/data/tdb";
        File file = new File(tdbPath);
        boolean mkdirs = file.mkdirs();
        Dataset ds = TDBFactory.createDataset(tdbPath);
        //Load some test content into TDB, unless it has already been initialized.
        if (!ds.containsNamedModel(graphName)) {
            Model m = createModel();
            m.createResource(graphName)
                    .addProperty(RDF.type, OWL.Ontology)
                    .addProperty(DCTerms.creator, "test import");
            ds.addNamedModel(graphName, m);
            TDB.sync(m);
            //loadExampleGraph( ONT1, ds, "The Dread Pirate Roberts" );
        }
        return importingOntologyToModel(ds, model);
    }

    //--------------------------------
    //Ontology methods
    //--------------------------------

    /**
     * Method to convert a Jena Model to a Jena Ontology Model.
     *
     * @param model the Jena Base Model.
     * @return the Jena Ontology Model.
     */
    public static OntModel toOntoModel(Model model) {
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
    }

    /**
     * Method to convert a standard Jena Model to a Jena InfModel.
     *
     * @param model the Jena Model.
     * @return the Jena InfModel.
     */
    public static InfModel toInfModel(Model model) {
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        return ModelFactory.createInfModel(reasoner, model);
        //return ModelFactory.createRDFSModel(model);
    }
    /**
     * Method to convert a standard Jena Model to a Jena InfModel.
     *
     * @param model the Jena Model.
     * @param reasoner the Jena Reasoner to use.
     * @return the Jena InfModel.
     */
    public static InfModel toInfModel(Model model,Reasoner reasoner) {
        //Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        return ModelFactory.createInfModel(reasoner, model);
    }

    /**
     * Method to convert a dataset to a OntoModel, now you can add some rule to uor TDB Model.
     *
     * @param dataset   the DataSet Jena Object.
     * @param OwlOrSWRL the Rules to add to the DataSet Jena Object.
     * @return the Ontology Model.
     */
    public static OntModel toOntoModel(Dataset dataset, URL OwlOrSWRL) {
        Model m = dataset.getDefaultModel(); //the TDB data
        Model toto = ModelFactory.createDefaultModel();
        toto.read(OwlOrSWRL.toString()); // the OWL & SWRL rules inside
        Model union = ModelFactory.createUnion(m, toto); //Merging both
        OntModelSpec spec = OntModelSpec.RDFS_MEM_TRANS_INF;
        //return ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC ,union); // Use Pellet reasonner
        return ModelFactory.createOntologyModel(spec, union); // Use Pellet reasonner
    }

    /**
     * Now we create an ontology model that imports ont1 and ont2, but arrange
     * that these are obtained from the TDB image.
     * href: https://gist.github.com/ijdickinson/3830267
     * @param ds the DataStore Jena where allocate the import..
     * @param sourceModel the Model Jena to import.
     * @return  the OntModel Jena where is allocate dthe content of the <tt>Model</tt>.
     */
    public static OntModel importingOntologyToModel(Dataset ds, Model sourceModel) {
        // this is a test, so empty the base first just to be sure
        Model base = ds.getDefaultModel();
        base.removeAll();
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setImportModelGetter(new LocalTDBModelGetter(ds));
        OntModel om = ModelFactory.createOntologyModel(spec, base);
        // now read the source model
        StringReader in = new StringReader(toString(sourceModel,null,null));
        om.read(in, null, "Turtle");
        return om;
    }

    /**
     * Method to create a {@link DatatypeProperty}.
     * @param ontModel the {@link OntModel}
     * @param uriName the {@link Node} or {@link String} of the Resource.
     * @param domain the {@link Object} tdomain reference to the Resource.
     * @param xsd the {@link XSD} vocabulary used.
     * @return the {@link DatatypeProperty}.
     */
    private static DatatypeProperty createDatatypePropertyBase(
            OntModel ontModel,Object uriName,Object domain,XSD xsd){
        try {
            if (uriName == null) {
                logger.warn("Try to create a DatatypeProperty from a 'NULL' value");
                return null;
            }
            if (uriName instanceof DatatypeProperty) {
                return (DatatypeProperty) uriName;
            }
            if (ontModel != null) {
                DatatypeProperty dp = ontModel.createDatatypeProperty(String.valueOf(uriName));
                dp.setDomain(toResource(domain));
                dp.setRange(toResource(xsd));
                dp.setRDFType(toResource(xsd));
                return dp;
            } else {
                if (uriName instanceof Node && domain instanceof EnhGraph) {
                    return new DatatypePropertyImpl(toNode(uriName), null);
                } else {
                    logger.error("The DatatypeProperty Datatype '" + uriName.getClass().getName() + "' is not recognised");
                    return null;
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static String validateModel(Model model){
        return validateModel(toInfModel(model));
    }

    /**
     * Method Utility function that returns a String displays results of validation
     * @param infModel the {@link InfModel} of Jena.
     * @return the {@link String} with the result of the validation of the Model.
     */
    @SuppressWarnings("rawtypes")
    public static String validateModel(InfModel infModel){
        // VALIDITY CHECK against RDFS
        StringBuilder buf = new StringBuilder();
        ValidityReport validity = infModel.validate();
        if (validity.isValid()) {
            buf.append("The Model is VALID!");
        }else {
            buf.append("Model has CONFLICTS.");
            for (Iterator iter = validity.getReports(); iter.hasNext(); ) {
                buf.append(" - ").append(iter.next());
            }
        }
        return buf.toString();
    }

    /* link database */
   /* public static IDBConnection connectDB(String DB_URL, String DB_USER, String DB_PASSWD, String DB_NAME) {
        return new DBConnection(DB_URL, DB_USER, DB_PASSWD, DB_NAME);
    }*/

    /* Read ontology from filesystem and store it into database */
    /*public static OntModel createDBModelFromFile(IDBConnection con, String name, String filePath) {
        ModelMaker maker = ModelFactory.createModelRDBMaker(con);
        Model base = maker.createModel(name);
        OntModel newmodel = ModelFactory.createOntologyModel( getModelSpec(maker), base );
        newmodel.read(filePath);
        return newmodel;
    }
*/
    /* Get ontology from database */
   /* public static OntModel getModelFromDB(IDBConnection con, String name) {
        ModelMaker maker = ModelFactory.createModelRDBMaker(con);
        Model base = maker.getModel(name);
        OntModel newmodel = 	ModelFactory.createOntologyModel( getModelSpec(maker), base);
        return newmodel;
    }*/

    public static OntModelSpec getModelSpec(ModelMaker maker) {
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setImportModelMaker(maker);
        return spec;
    }

    public static List<String> simpleReadOntology(OntModel model) {
        List<String> list = new ArrayList<>();
        for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
            OntClass c = i.next();
            list.add(c.getLocalName());
        }
        return list;
    }

    //--------------------------------------------------------------------------
    // TDB
    //--------------------------------------------------------------------------
    public Boolean addModelToTDB(Dataset tdb,Model m, String name) {
        if (name == null) {
            logger.info("cannot add the model because the given name is null.");
            return false;
        }
        Model namedModel = tdb.getNamedModel(name);
        namedModel.removeAll();
        namedModel.add(m.listStatements());
        namedModel.setNsPrefixes(m.getNsPrefixMap());
        namedModel.commit();
        TDB.sync(tdb);
        return true;
    }

    //-------------------------------------------------------------------------

    public Model getNamedModel(Dataset dataset,String name) {
        if (name == null) {
            logger.info("cannot get the model because the given name is null.");
            return null;
        }
        if (!dataset.containsNamedModel(name)) {
            logger.info("The model: " + name + " does not exist in the repository.");
            return null;
        }
        return dataset.getNamedModel(name);
    }

    public void clearNamedModel(Dataset dataset,String name) {
        if (name == null) {
            logger.info("cannot clear the model because the given name is null.");
            return;
        }
        if (!dataset.containsNamedModel(name)) {
            logger.info("The model " + name + " does not exist in the repository.");
            return;
        }
        dataset.getNamedModel(name).removeAll();
        dataset.getNamedModel(name).commit();
    }

    /**
     * imports the named model from a directory or a file
     * @param dataset the {@link Dataset}.
     * @param lang The language of the file specified by the lang argument.
     * Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3".
     * The default value, represented by null is "RDF/XML".
     * @param file the {@link File} to import
     * @return the {@link Boolean}.
     */
    public Boolean importModel(Dataset dataset,File file, String lang) {
        if (!file.exists()) {
            logger.error("cannot find the file/dir at " + file.getAbsolutePath());
            return false;
        }
        if (file.isFile() && file.exists()) {
            return importModelFromSingleFile(dataset,file, lang);
        } else if (file.isDirectory() && file.exists()) {
            File[] files = file.listFiles();
            if(files != null && files.length >0) {
                for (File f : files) {
                    importModelFromSingleFile(dataset, f, lang);
                }
                return true;
            }else{
                logger.error("cannot find the any  file on the dir at " + file.getAbsolutePath());
                return false;
            }
        }
        return false;
    }

    private Boolean importModelFromSingleFile(Dataset dataset,File file, String lang) {
        Model m = ModelFactory.createDefaultModel();
        try {
            InputStream s = new FileInputStream(file);
            m.read(s, null);
            addModelToTDB(dataset,m, file.getName());
            logger.info("The model " + file.getPath() + " successfully imported to repository");
            return true;
        } catch (Throwable t) {
            logger.error("Error reading the model file!", t);
            return false;
        }
    }

    /**
     * Method to get the file extension of a triple file from the Language used ont it.
     * @param lang the {@link Lang} used on the File.
     * @return the {@link String} name of the extension.
     */
    public String getFileExtension(String lang) {
        String ext = ".rdf";
        if (lang.equalsIgnoreCase("RDF/XML")) ext = ".rdf";
        else if (lang.equalsIgnoreCase("RDF/XML-ABBREV")) ext = ".rdf";
        else if (lang.equalsIgnoreCase("N-TRIPLE")) ext = ".ntriple";
        else if (lang.equalsIgnoreCase("TURTLE")) ext = ".turtle";
        else if (lang.equalsIgnoreCase("TTL")) ext = ".ttl";
        else if (lang.equalsIgnoreCase("N3")) ext = ".n3";
        return ext;
    }

    public static XSDDatatype toXDDTypes(int sqlTypes){
        switch (sqlTypes) {
            case Types.BIT: return XSDDatatype.XSDbyte;
            case Types.TINYINT: return XSDDatatype.XSDint;
            case Types.SMALLINT: return XSDDatatype.XSDint;
            case Types.INTEGER: return XSDDatatype.XSDinteger;
            case Types.BIGINT: return XSDDatatype.XSDint;
            case Types.FLOAT: return XSDDatatype.XSDfloat;
            //case Types.REAL:return ;
            case Types.DOUBLE:return XSDDatatype.XSDdouble;
            case Types.NUMERIC:return XSDDatatype.XSDinteger;
            case Types.DECIMAL:return XSDDatatype.XSDdecimal;
            case Types.CHAR:return XSDDatatype.XSDstring;
            case Types.VARCHAR:return  XSDDatatype.XSDstring;
            case Types.LONGVARCHAR:return  XSDDatatype.XSDstring;
            case Types.DATE:return  XSDDatatype.XSDdate;
            case Types.TIME: return  XSDDatatype.XSDtime;
            case Types.TIMESTAMP:return  XSDDatatype.XSDdateTime;
            case Types.BINARY:return  XSDDatatype.XSDbase64Binary;
            case Types.VARBINARY:return XSDDatatype.XSDbase64Binary;
            case Types.LONGVARBINARY:return XSDDatatype.XSDbase64Binary;
            case Types.NULL:return XSDDatatype.XSDstring;
            //case Types.OTHER:return "";
            //case Types.JAVA_OBJECT:return "JAVA_OBJECT";
            //case Types.DISTINCT:return "DISTINCT";
            //case Types.STRUCT:return "STRUCT";
            //case Types.ARRAY:return "ARRAY";
            //case Types.BLOB:return "BLOB";
            //case Types.CLOB:return "CLOB";
            //case Types.REF:return "REF";
            //case Types.DATALINK:return "DATALINK";
            case Types.BOOLEAN: return XSDDatatype.XSDboolean;
            //case Types.ROWID:return "ROWID";
            case Types.NCHAR:return XSDDatatype.XSDstring;
            case Types.NVARCHAR:return XSDDatatype.XSDstring;
            case Types.LONGNVARCHAR:return XSDDatatype.XSDstring;
            //case Types.NCLOB:return "NCLOB";
            //case Types.SQLXML:return "SQLXML";
            default: return XSDDatatype.XSDstring;
        }
    }

    //--------------------------------
    //Utility private methods
    //--------------------------------

    /**
     * Method to check if a String uri is a IRI normalized.
     * http://stackoverflow.com/questions/9419658/normalising-possibly-encoded-uri-strings-in-java
     *
     * @param uri the String to verify.
     * @return if true the String is a valid IRI.
     */
    private static Boolean isIRI(Object uri) {
        try {
            IRIFactory factory = IRIFactory.uriImplementation();
            IRI iri = factory.construct(String.valueOf(uri));
           /* ArrayList<String> a = new ArrayList<>();
            a.add(iri.getScheme());
            a.add(iri.getRawUserinfo());
            a.add(iri.getRawHost());
            a.add(iri.getRawPath());
            a.add(iri.getRawQuery());
            a.add(iri.getRawFragment());*/
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to convert a URI os String reference to a resource to a good ID.
     * NOTE: URI string: scheme://authority/path?query#fragment
     *
     * @param uriResource the String or URI reference Resource.
     * @return the String id of the Resource.
     */
    private static String toId(Object uriResource) {
        return URI.create(String.valueOf(uriResource).replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", "_").trim()).toString();
    }

    private static boolean isUri(Object uriResource) {
        if (uriResource instanceof URI) return true;
        else {
            try {
                //noinspection ResultOfMethodCallIgnored
                URI.create(String.valueOf(uriResource));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Validates an URI using Jena
     * @param _uri URI String to be validated
     * @return Returns true iff URI is valid for Jena
     */
    public static boolean isValidJenaURI(String _uri) {
        String query = String.format("DESCRIBE <%s>", _uri);
        try {
            Query q = QueryFactory.create(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isStringNoEmpty(Object uriResource) {
        return (uriResource instanceof String && !isNullOrEmpty(String.valueOf(uriResource)));
    }

    private static boolean isStringEmpty(Object uriResource) {
        return (uriResource instanceof String && isNullOrEmpty(String.valueOf(uriResource)));
    }

    private static boolean isStringOrUriEmpty(Object uriResource) {
        return (
                (uriResource instanceof String && isNullOrEmpty(String.valueOf(uriResource))) ||
                        (uriResource instanceof URI && isNullOrEmpty(String.valueOf(uriResource)))
        );
    }

    private static IRI toIri(Object uriResource) {
        return IRIFactory.uriImplementation().construct(String.valueOf(uriResource));
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     *
     * @param text the {@link String} text.
     * @return the {@link Boolean} is true if the parameter is null or empty.
     */
    private static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty();
    }

    /**
     * Method to corrected replace all '\' for a Node URI String
     * @param s the {@link String} of the URI of the Node.
     * @return the {@link String} with escape correct.
     */
    private static String escapeString(String s) {
        StringBuilder buf = new StringBuilder(s.length());
        int i = 0;
        char ch;
        while (i < s.length()) {
            ch = s.charAt(i++);
            if (ch == '\'')
                buf.append('\\');
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * Method to convert a {@link java.sql.Timestamp} to a {@link String}
     * @param v the {@link java.sql.Timestamp}.
     * @return the {@link String}
     */
    private static String Timestamp2String(java.sql.Timestamp v) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(v);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int nanos = v.getNanos();

        String yearS,monthS,dayS,hourS,minuteS,secondS,nanosS;
        String zeros = "000000000";
        String yearZeros = "0000";
        StringBuffer timestampBuf;

        if (year < 1000) {
            yearS = "" + year;
            yearS = yearZeros.substring(0, (4 - yearS.length())) + yearS;
        } else {
            yearS = "" + year;
        }

        if (month < 10) monthS = "0" + month;
        else monthS = Integer.toString(month);

        if (day < 10) dayS = "0" + day;
        else dayS = Integer.toString(day);

        if (hour < 10) hourS = "0" + hour;
        else hourS = Integer.toString(hour);

        if (minute < 10) minuteS = "0" + minute;
        else minuteS = Integer.toString(minute);

        if (second < 10) secondS = "0" + second;
        else secondS = Integer.toString(second);

        if (nanos == 0) {
            nanosS = "0";
        } else {
            nanosS = Integer.toString(nanos);

            // Add leading 0
            nanosS = zeros.substring(0, (9 - nanosS.length())) + nanosS;

            // Truncate trailing 0
            char[] nanosChar = new char[nanosS.length()];
            nanosS.getChars(0, nanosS.length(), nanosChar, 0);
            int truncIndex = 8;
            while (nanosChar[truncIndex] == '0') {
                truncIndex--;
            }
            nanosS = new String(nanosChar, 0, truncIndex + 1);
        }

        timestampBuf = new StringBuffer();
        timestampBuf.append(yearS);
        timestampBuf.append("-");
        timestampBuf.append(monthS);
        timestampBuf.append("-");
        timestampBuf.append(dayS);
        timestampBuf.append("T");
        timestampBuf.append(hourS);
        timestampBuf.append(":");
        timestampBuf.append(minuteS);
        timestampBuf.append(":");
        timestampBuf.append(secondS);
        timestampBuf.append(".");
        timestampBuf.append(nanosS);
        return (timestampBuf.toString());
    }

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

    /**
     * <p>A type of model getter that loads models from a local TDB instance,
     * if they exist as named graphs using the model URI as the graph name.</p>
     */
    static class LocalTDBModelGetter implements ModelGetter {

        private final Dataset ds;

        public LocalTDBModelGetter(Dataset dataset) {
            ds = dataset;
        }

        @Override
        public Model getModel(String uri) {
            throw new NotImplemented("getModel( String  ) is not implemented");
        }

        @Override
        public Model getModel(String uri, ModelReader loadIfAbsent) {
            Model m = ds.getNamedModel(uri);
            // create the model if necessary. In actual fact, this example code
            // will not exercise this code path, since we pre-define the models
            // we want to see in TDB
            if (m == null) {
                m = ModelFactory.createDefaultModel();
                loadIfAbsent.readModel(m, uri);
                ds.addNamedModel(uri, m);
            }
            return m;
        }
    } // LocalTDBModelGetter

    /**
     * Method to convert a Ontolotgy file .rdf,.owl to a Vocabolary Jena.
     * e.g. -i input [-a namespaceURI] [-o output_file] [-c config_uri] [-e encoding]...
     * @param inputOntology the {@link File} input of the Ontology.
     * @param baseUri the {@link String} base uri.
     * @param outputJenaVocabulary the {@link File} output Vocabulary class ojava of API Jena.
     * @param alternativeConfig the {@link File} with an alternative configuration.
     */
    /*public static void invokeSchemaGen(
            File inputOntology,String baseUri,File outputJenaVocabulary,File alternativeConfig){
        //schemagen.SchemagenUtils.urlCheck("");
        String[] arrayParams = new String[]{
                "-i",inputOntology.getAbsolutePath(),
                "-a",baseUri,
                "-o",outputJenaVocabulary.getAbsolutePath(),
                "-c",alternativeConfig.getAbsolutePath(),
                "-e",StringUtilities.UTF_8.toString()
        };
        schemagen.main(arrayParams);
    }*/

    /*public static Collection<String[]> evalutateQueries(String manifestURI,File rqFile, String... excludes)
            throws URISyntaxException, IOException {

        Set<String> toExclude = new HashSet(Arrays.asList(excludes));
        FileManager fm = FileManager.get();
        Model manifest = fm.loadModel(manifestURI);
        Query manifestExtract = QueryFactory.read(rqFile.toURI().toURL().toString());
        Collection<String[]> tests = new ArrayList<>();
        QueryExecution qe = QueryExecutionFactory.create(manifestExtract, manifest);
        ResultSet results = qe.execSelect();

        if (!results.hasNext()) {
            throw new RuntimeException("No results");
        }
        while (results.hasNext()) {

            QuerySolution soln = results.next();
            String[] params = new String[6];
            params[0] = soln.getResource("test").getURI();
            params[1] = soln.getLiteral("title").getString();
            params[2] = soln.getLiteral("purpose").getString();
            params[3] = soln.getResource("input").getURI();
            params[4] = soln.getResource("query").getURI();
            // getBoolean not working??
            //boolean expected = (soln.contains("expect")) ?
            //    soln.getLiteral("expect").getBoolean() : true;
            params[5] = soln.contains("expect") ? soln.getLiteral("expect").getLexicalForm() : "true";
            if (toExclude.contains(params[0]) ||
                    toExclude.contains(params[3]) ||
                    toExclude.contains(params[4]) ) {
                logger.warn("Skipping test <" + params[0] + ">");
                continue;
            }
            tests.add(params);
        }
        return tests;
    }*/


}//end of the class JenaKit
