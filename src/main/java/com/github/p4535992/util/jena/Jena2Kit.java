package com.github.p4535992.util.jena;

import com.github.p4535992.util.collection.CollectionKit;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;

import com.github.p4535992.util.string.StringOutputStreamKit;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import com.github.p4535992.util.xml.XMLKit;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriterRegistry;

/**
 * Class utility for Jena
 * Created by 4535992 in 2015-04-28
 * @author 4535992
 * @version 2015-06-26
 */
@SuppressWarnings("unused")
public class Jena2Kit {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Jena2Kit.class);
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
    public static SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    /**
     * Method  to Write large model jena to file of text.
     * @param fullPath string of the path to the file.
     * @param model jena model to write.
     * @param outputFormat the output format you want to write the model.
     * @throws IOException throw if any I/O error is occured.
     */
    public static void writeModelToFile(String fullPath, com.hp.hpl.jena.rdf.model.Model model, String outputFormat) throws IOException {
        fullPath =  FileUtil.path(fullPath) + File.separator + FileUtil.filenameNoExt(fullPath)+"."+outputFormat.toLowerCase();
        SystemLog.message("Try to write the new file of triple from:" + fullPath + "...");
        OUTLANGFORMAT = stringToRiotLang(outputFormat);
        OUTRDFFORMAT = stringToRDFFormat(outputFormat);
        OUTFORMAT = outputFormat.toUpperCase();
        try {
            writeModelToFile2(fullPath, model);
        }catch(Exception e1){
            SystemLog.warning("...there is was a problem to try the write the triple file at the first tentative...");
            try {
                writeModelToFile3(fullPath, model);
            }catch(Exception e2){
                SystemLog.warning("...there is was a problem to try the write the triple file at the second tentative...");
                try {
                    writeModelToFile4(fullPath, model);
                } catch (Exception e3) {
                    SystemLog.warning("...there is was a problem to try the write the triple file at the third tentative...");
                    try {
                        writeModelToFile1(fullPath, model);//unmappable character exception
                    } catch(Exception e4) {
                        SystemLog.error("... exception during the writing of the file of triples:" + fullPath);
                        SystemLog.exception(e4);
                    }
                }
            }
        }
        SystemLog.message("... the file of triple to:" + fullPath + " is been wrote!");
    }

    /**
     * Method  to Write large model jena to file of text.
     * @param fullPath string of the path to the file.
     * @param model jena model to write.
     * @throws IOException throw if any I/O error is occured.
     */
	private static void writeModelToFile1(String fullPath,Model model) throws IOException {
        Charset ENCODING = StandardCharsets.UTF_8;
        FileUtil.createFile(fullPath);
        Path path = Paths.get(fullPath);
	    try (BufferedWriter writer = Files.newBufferedWriter(path,ENCODING)) {
            //org.apache.jena.riot.RDFDataMgr.write(writer, model, OUTLANGFORMAT);
            model.write(writer, null, OUTLANGFORMAT.getName());
        }
	  }

    /**
     * Method  to Write large model jena to file of text.
     * @param fullPath string of the path to the file.
     * @param model jena model to write.
     * @throws IOException throw if any I/O error is occured.
     */
    private static void writeModelToFile2(String fullPath,Model model) throws IOException {
        FileWriter out = new FileWriter(fullPath);
        try {
            model.write(out, OUTLANGFORMAT.getName());
        }
        finally {
            try {
                out.close();
            }
            catch (IOException closeException) {
                // ignore
            }
        }
    }

    /**
     * Method  to Write large model jena to file of text.
     * @param fullPath string of the path to the file.
     * @param model jena model to write.
     * @throws FileNotFoundException throw if any "File Not Found error" is occured.
     */
    private static void writeModelToFile3(String fullPath, Model model) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(fullPath);
        model.write(outputStream, OUTLANGFORMAT.getName());
    }
    /**
     * Method  to Write large model jena to file of text.
     * @param fullPath string of the path to the file.
     * @param model jena model to write.
     * @throws IOException throw if any I/O error is occured.
     */
    private static void writeModelToFile4(String fullPath, com.hp.hpl.jena.rdf.model.Model model)throws IOException{
        Writer writer = new FileWriter(new File(fullPath));
        model.write(writer, OUTFORMAT);
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
        try ( //QueryExecutionFactory.create(query, model) ;
                QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            resultModel = qexec.execConstruct();
            SystemLog.sparql(sparql);
        }
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
        //QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        Model resultModel ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            resultModel = qexec.execDescribe();
            SystemLog.sparql(sparql);
        }
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
        try ( //QueryExecution qexec = QueryExecutionFactory.create(sparql, model);
                QueryExecution qexec = QueryExecutionFactory.create(sparql, model)) {
            results = qexec.execSelect();
            //... make exit from the thread the result of query
            results = ResultSetFactory.copyResults(results) ;
            SystemLog.sparql(sparql);
        }
        return results;
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
        //QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            result = qexec.execAsk();
            SystemLog.sparql(sparql);
        }
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
    public static Model loadFileTriple(String filename,String filepath,String inputFormat) throws FileNotFoundException {
        Model m = ModelFactory.createDefaultModel();
        INLANGFORMAT = stringToRiotLang(inputFormat);
        INRDFFORMAT = stringToRDFFormat(inputFormat);
        INFORMAT = INLANGFORMAT.getLabel().toUpperCase();

        // use the FileManager to find the input file
        File fileInput = new File(filepath +File.separator+ filename + "." + inputFormat);
        InputStream in;
        try {
            in = com.hp.hpl.jena.util.FileManager.get().open(fileInput.getAbsolutePath());
        }catch(Exception e){
            in = new FileInputStream(fileInput);
        }

        if (in == null || ! fileInput.exists()) {
            throw new IllegalArgumentException( "File: " +  fileInput + " not found");
        }
        SystemLog.message("Try to read file of triples from the path:" + fileInput.getAbsolutePath()+"...");
        try {
            com.hp.hpl.jena.util.FileManager.get().addLocatorClassLoader(Jena2Kit.class.getClassLoader());
            m = com.hp.hpl.jena.util.FileManager.get().loadModel(fileInput.toURI().toString(),null,INFORMAT);
        }catch(Exception e){
            try {
                m.read(in, null, INFORMAT);
            } catch (Exception e1) {
                try {
                    org.apache.jena.riot.RDFDataMgr.read(m, in, INLANGFORMAT);
                } catch (Exception e2) {
                    try {
                        //If you are just opening the stream from a file (or URL) then Apache Jena
                        org.apache.jena.riot.RDFDataMgr.read(m,fileInput.toURI().toString());
                    } catch (Exception e3) {
                        SystemLog.exception(e3);
                        SystemLog.abort(0, "Failed read the file of triples from the path:" + fileInput.getAbsolutePath());
                    }
                }
            }
        }
        SystemLog.message("...file of triples from the path:" + fileInput.getAbsolutePath()+" readed!!");
        return m;
    }
    /**
     * Method for load a file of tuples to a jena model.
     * @param file a input file.
     * @return the jena model of the file.
     * @throws FileNotFoundException thriow if any "File Not Found" error is occurred.
     */
     public static Model loadFileTriple(File file) throws FileNotFoundException {
         String filename = FileUtil.filenameNoExt(file);
         String filepath = FileUtil.path(file);
         String inputFormat = FileUtil.extension(file);
         return loadFileTriple(filename,filepath,inputFormat);
     }

    /**
     * A list of org.apache.jena.riot.Lang file formats.
     * return all the language Lang supported from jena.
     * exception : "AWT-EventQueue-0" java.lang.NoSuchFieldError: RDFTHRIFT  or CSV.
     */
 	private static final org.apache.jena.riot.Lang allFormatsOfRiotLang[] = new org.apache.jena.riot.Lang[] { 
            org.apache.jena.riot.Lang.NTRIPLES, org.apache.jena.riot.Lang.N3,org.apache.jena.riot.Lang.RDFXML,
            org.apache.jena.riot.Lang.TURTLE, org.apache.jena.riot.Lang.TRIG, org.apache.jena.riot.Lang.TTL,
            org.apache.jena.riot.Lang.NQUADS ,
            org.apache.jena.riot.Lang.NQ,
 		    org.apache.jena.riot.Lang.JSONLD,
            org.apache.jena.riot.Lang.NT,org.apache.jena.riot.Lang.RDFJSON,
 		    org.apache.jena.riot.Lang.RDFNULL
            //org.apache.jena.riot.Lang.CSV,
            //org.apache.jena.riot.Lang.RDFTHRIFT
        };
    /**
     * A list of org.apache.jena.riot.RDFFormat file formats used in jena.
     * @return all the RDFFormat supported from jena.
     *
 	private static final RDFFormat allFormatsOfRDFFormat[] = new RDFFormat[] { 	
 		RDFFormat.TURTLE, RDFFormat.TTL,RDFFormat.JSONLD_FLAT,RDFFormat.JSONLD_PRETTY,
 		RDFFormat.JSONLD,RDFFormat.RDFJSON,RDFFormat.RDFNULL,RDFFormat.NQUADS,RDFFormat.NQ,
 		RDFFormat.NQUADS_ASCII,RDFFormat.NQUADS_UTF8,RDFFormat.NT,RDFFormat.NTRIPLES,
 		RDFFormat.NTRIPLES_ASCII,RDFFormat.NTRIPLES_UTF8,RDFFormat.RDFXML,RDFFormat.RDFXML_ABBREV,
 		RDFFormat.RDFXML_PLAIN,RDFFormat.RDFXML_PRETTY,RDFFormat.TRIG,RDFFormat.TRIG_BLOCKS,
 		RDFFormat.TRIG_FLAT,RDFFormat.TRIG_PRETTY,RDFFormat.TURTLE_BLOCKS,RDFFormat.TURTLE_FLAT,
 		RDFFormat.TURTLE_PRETTY};
        //org.apache.jena.riot.RDFFormat.RDF_THRIFT,org.apache.jena.riot.RDFFormat.RDF_THRIFT_VALUES,
      */
    /*
    public static com.hp.hpl.jena.datatypes.RDFDatatype convertXSDDatatypeToRDFDatatype(
            com.hp.hpl.jena.datatypes.xsd.XSDDatatype xsdDatatype){
        com.hp.hpl.jena.datatypes.RDFDatatype rdfDatatype = null;
        return rdfDatatype;
    }
    */

    /**
     * A list of com.hp.hpl.jena.datatypes.xsd.XSDDatatype.
     * return all the XSDDatatype supported from jena.
     */
    private static final XSDDatatype allFormatsOfXSDDataTypes[] = new XSDDatatype[]{
            XSDDatatype.XSDstring,XSDDatatype.XSDENTITY,XSDDatatype.XSDID,XSDDatatype.XSDIDREF
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
    public static XSDDatatype stringToXSDDatatypeToString(String uri) {
            for (XSDDatatype xsdDatatype : allFormatsOfXSDDataTypes) {
                   if(xsdDatatype.getURI().equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#"+uri)){
                       return xsdDatatype;
                   }
             }
            throw new IllegalArgumentException("The XSD Datatype '" + uri + "' is not recognised");
 	}

    /**
     * Method convert a string to a rdfformat.
     * @param strFormat string name of the RDFFormat.
     * @return rdfformat the RDFFormat with the same name.
     */
    public static RDFFormat stringToRDFFormat(String strFormat) {
        if(strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES")|| strFormat.toUpperCase().contains("N3")){
            strFormat="N-Triples";
        }
        if(strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")){
            strFormat="Turtle";
        }
        Collection<RDFFormat> allFormatsOfRDFFormat = RDFWriterRegistry.registered();
        for(RDFFormat rdfFormat : allFormatsOfRDFFormat) {
                if (rdfFormat.getLang().getName().equalsIgnoreCase(strFormat))
                        return rdfFormat;
        }
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
 	}

    /**
     * Method convert a string name of a RDFFormat to a language Lang.
     * @param strFormat string name of a RDFFormat.
     * @return lang the language Lang for the same name.
     */
 	public static org.apache.jena.riot.Lang stringToRiotLang(String strFormat) {	
            if(strFormat.toUpperCase().contains("NT") ||
                    strFormat.toUpperCase().contains("NTRIPLES")|| strFormat.toUpperCase().contains("N3")){
                 strFormat="N-Triples";
             }
            if(strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")){
                 strFormat="Turtle";
             }
            for (org.apache.jena.riot.Lang lang : allFormatsOfRiotLang) {
                String label = lang.getLabel();
                String name = lang.getName();

                if (lang.getName().equalsIgnoreCase(strFormat))
                      return lang;
 		}
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
                SystemLog.message("Try to write the new file of triple of infodocument to:" + fullPathOutputFile + "...");
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
//                }else if(outputFormat.toLowerCase().contains("rdf")){
//                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsRDF(fos, "RDF/XML", results);
                }
                SystemLog.message("... the file of triple Infodoument to:" + fullPathOutputFile + " is been wrote!");
            } else if (outputFormat.toLowerCase().contains("ttl")) {
                Model resultModel = execSparqlConstructorOnModel(sparql, model);
                OUTLANGFORMAT = stringToRiotLang(outputFormat);
                OUTRDFFORMAT = stringToRDFFormat(outputFormat);
                OUTFORMAT = outputFormat.toUpperCase();
                //Writer writer = new FileWriter(new File(fullPathOutputFile));
                //model.write(writer, outputFormat);
                writeModelToFile(fullPathOutputFile, resultModel, OUTFORMAT);
                SystemLog.message("... the file of triple Infodoument to:" + fullPathOutputFile + " is been wrote!");
            }
        }catch(Exception e){
            SystemLog.error("... exception during the writing of the file of triples:" + fullPathOutputFile);
            SystemLog.exception(e);
        }

    }

    /**
     * Method to convert to RDF to a specific format.
     * @param file file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    public static void convertTo(File file, String outputFormat) throws IOException{
         Model m = loadFileTriple(file);
         String newName = FileUtil.filenameNoExt(file)+"."+outputFormat.toLowerCase();
         String newPath = FileUtil.path(file);
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
   /**
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
                com.hp.hpl.jena.rdf.model.Property sp = stmt.getPredicate();

                if (uri.equals(sp.getNameSpace())
                        && ("".equals(property)
                        || sp.getLocalName().equals(
                        property))) {
                    foundLocal = true;
                }
            }
        } catch (Exception e) {
            // nop
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
            SystemLog.exception(e);
        }
        return null;
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
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return destModel;
    }

    /**
     * Method to load a a file to a Jena Model.
     * @param filePath string path to the file.
     * @return model model loaded with the file.
     */
    public static Model loadFileToModel(String filePath) {
        // I used to pass encoding in here, but that's dumb. I'm reading XML
        // which is self-describing.
        Model m = ModelFactory.createDefaultModel();
        SystemLog.message("Loading " + filePath + "...");
        try {
            File inputFile = new File(filePath);
            try (FileInputStream input = new FileInputStream(inputFile)) {
                m.read(input, FileUtil.convertFileToStringUriWithPrefix(inputFile));
            }     
        } catch (IOException e) {
            SystemLog.warning("Failed to open " + filePath);
            SystemLog.exception(e);
        }
        return m;
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
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return model;
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
            SystemLog.warning("Exception while try to delete a literal:"+e.getMessage());
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
            if(!StringKit.isNullOrEmpty(uri)) {
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
            SystemLog.warning("Exception while try to find a literal:"+e.getMessage());
        }
        return null;
    }

    /**
     * Method to update a literal on a Jena model.
     * @param model jena model.
     * @param subject subject of the statement.
     * @param property property of the statement.
     * @param value value of the literal of the statement to remove from Jena model.
     */
    public static void updateLiteral(Model model,Resource subject,String property,String value) {
        try {
            //int pos = property.indexOf(":");
            //String prefix = property.substring(0, pos);
            String rdfValue = queryLiteral(model, subject, property);
            if (value != null && !value.equals(rdfValue)) {
                SystemLog.message("Updating " + property + "=" + value);
                deleteLiteral(model, subject, property, rdfValue);
                int pos = property.indexOf(":");
                String prefix = property.substring(0, pos);
                property = property.substring(pos + 1);
                String uri = namespaces.get(prefix);
                Property p = model.createProperty(uri, property);
                RDFNode v =  model.createLiteral(value);
                Statement s = model.createStatement(subject, p, v);
                model.add(s);
            }
        } catch (Exception e) {
            SystemLog.exception(e);
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
            Iterator<String> keys =
                    CollectionKit.convertSetToIterator(namespaces.keySet());
            while (keys.hasNext()) {
                String prefix = keys.next();
                if (namespace.equals(namespaces.get(prefix))) {
                    return prefix;
                }
            }
            System.err.println("Internal error: this can't happen.");
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
        StringOutputStreamKit stringOutput = new StringOutputStreamKit();
        if(!StringKit.isNullOrEmpty(outputFormat)){
             RDFFormat rdfFormat = stringToRDFFormat(outputFormat);
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
        return XMLKit.xmlEncode(rawString);
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
                SystemLog.message("\tdelete " + type + ": " + prefix + ":" + p.getLocalName()
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
            StmtIterator iterator =
                    resource.listProperties(property);
            while (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
            }
            */
            //... you must already create the resources
            //subject = model.getResource(redirectionURI);
            //subject = model.createResource("");
            //...Delete all the statements with predicate p for this resource from its associated model.
            subject.removeAll(property);
            subject.addProperty(property, (RDFNode) value);         
            return model;
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return null;
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
     */
    public static void updateUri(Resource resource, URI uri) {
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
        } catch (Exception e) {
            SystemLog.exception(e);
        }
    }

    /**
     * Method to get the title from a ID.
     * @param title string of the title of the resource.
     * @return string index of the resource.
     */
    public static String convertTitleToID(String title) {
        return title.replace(' ', '_');
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
            System.out.println(statement.getObject());
        }
    }

    /**
     * Method to show a resource on a model jena to the console.
     * @param resource resource to print to the console.
     */
    public static void show(Resource resource) {
        try {
            StmtIterator iterator =resource.listProperties();
            show(iterator);
        } catch (Exception e) {
             SystemLog.exception(e);
        }
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
        System.out.println(buffer);
    }

    /**
     * Method to show a model jena to the console.
     * @param model jena model to print tot the console.
     * @param outputFormat string of the output format.
     */
    public static void show(Model model,String outputFormat) {
        System.out.println(convertModelToString(model,outputFormat));
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
        if(!StringKit.isNullOrEmpty(outputFormat)){
            try {
                RDFFormat rdfFormat = stringToRDFFormat(outputFormat);
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
        } catch (Exception e) {
           SystemLog.exception(e);
        }
        return stringOut.toString();
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
     */
    public static void updateResource(Resource oldResource,Resource newResource) {
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
        } catch (Exception e) {
            SystemLog.exception(e);
        }      
    }

    /**
     * Method to replace/update a subject/resource on a model jena.
     * @param statement statement with the resource to replace/update
     * @param newSubject new resource to add tot he model.
     */
    public static void updateSubjectResource(Statement statement,Resource newSubject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement = m.createStatement(newSubject,
                    statement.getPredicate(),statement.getObject());
            m.remove(statement);
            m.add(newStatement);
        } catch (Exception e) {
             SystemLog.exception(e);
        }
    }

    /**
     * Method to replace/update a object resource.
     * @param statement statement with the object to replace/update
     * @param newObject new value of the object
     */
    public static void updateObjectResource(Statement statement,Resource newObject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement =m.createStatement(statement.getSubject(),
                    statement.getPredicate(),newObject);
            m.remove(statement);
            m.add(newStatement);
        } catch (Exception e) {
             SystemLog.exception(e);
        }
    }

    /**
     * Method copies all properties across to new resource, just replaces type.
     * @param resource the resource to update the type.
     * @param newType the new type for the resource.
     */
    public static void updateTypeResource(Resource resource,Resource newType) {
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
        } catch (Exception e) {
            SystemLog.exception(e);
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
        try {
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
        } catch (Exception e) {
            SystemLog.exception(e);
        }
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
        try {
            StmtIterator iterator = model.listStatements();
            while (iterator.hasNext()) {
                statement = iterator.next();
                //changed for Jena 2
                if (property.equals(statement.getPredicate())) {
                    return statement.getSubject();
                }
            }
        } catch (Exception e) {
            SystemLog.exception(e);
        }
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
        try {
            StmtIterator iterator = model.listStatements();
            while (iterator.hasNext()) {
                statement = iterator.next();
                if (property.equals(statement.getPredicate())&& object.equals(statement.getObject())) {
                    return statement;
                }
            }
        } catch (Exception e) {
            SystemLog.exception(e);
        }
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
        if (node == null) {
            return null;
        }
        return node;
    }

    /**
     * Method to set/replace/update a property-object.
     * @param resource resource to find.
     * @param property property to find.
     * @param object value of the object you found.
     */
    public static void updatePropertyObject(Resource resource,Property property,Resource object) {
        try {
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            resource.addProperty(property, object);
        } catch (Exception e) {
            SystemLog.exception(e);
        }
    }

    /**
     * Method for replace/update a literal value on the jena model.
     * @param model jena model.
     * @param literal literal to update.
     * @param value new value of the literal.
     */
    public static void updateLiteralValue(Model model,Literal literal, String value){
        Literal newLiteral = model.createLiteral(value);
        Set<Statement> statements = new HashSet<>();
        StmtIterator iterator =model.listStatements(null,null,literal);
        while (iterator.hasNext()) {
            statements.add(iterator.next());
        }
        Iterator<Statement> setIterator = statements.iterator();
        Statement statement;
        while (setIterator.hasNext()) {
            statement =  setIterator.next();
            model.add(statement.getSubject(),statement.getPredicate(),newLiteral);
            model.remove(statement);
        }
    }


    /**
     * Method to convert a date to a ISO date.
     * @param date date to convert.
     * @return the dat in format iso.
     */
    public static String convertDateToIsoDate(Date date) {
        return isoDate.format(date);
    }

    /**
     * Method to convert a string date to a  ISO Date.
     * e.g. 2003-10-29T10:05:35-05:00.
     * @param string sting of a date eg 2003-10-29.
     * @return sring of a date in iso date format.
     */
    public static Date convertStringDateToIsoDate(String string) {
        Date date = null;
        string =string.substring(0, 19)+ "GMT"+ string.substring(19);
        try {
            date = isoDate.parse(string);
        } catch (ParseException e) {
           SystemLog.exception(e);
        }
        return date;
    }


    /**
     * Method utility: create new resource from uri.
     * @param BASE base uri.
     * @param localname local name resource uri.
     * @return resource uri.
     */
    public static Resource r (String BASE, String localname ) {
        return ResourceFactory.createResource ( BASE + localname );
    }

    /**
     * Method utility: create new property from uri.
     * @param BASE base uri.
     * @param localname local name resource uri.
     * @return property.
     */
    public static Property p (String BASE, String localname ) {
        return ResourceFactory.createProperty(BASE, localname);
    }

    /**
     * Method utility: create new property from uri.
     * @param uriref resource uri.
     * @return property.
     */
    public static Property p (String uriref) {
        return ResourceFactory.createProperty(uriref);
    }

    /**
     * Method utility: create new property impl from uri.
     * @param uriref resource uri.
     * @return property.
     */
    public static Property pi (String uriref) {
        return new PropertyImpl(uriref);
    }

    /**
     * Method utility: create new property impl from uri.
     * @param BASE base uri.
     * @param localname local name resource uri.
     * @return property.
     */
    public static Property pi (String BASE, String localname) {
        return new PropertyImpl(BASE, localname);
    }

    /**
     * Method utility: create new plain literal from uri.
     * @param value string of uri.
     * @return literal.
     */
    public static Literal lp(String value) {
        return ResourceFactory.createPlainLiteral(value);
    }

    /**
     * Method utility: create new typed literal from uri.
     * @param value object of uri.
     * @return literal.
     */
    public static Literal lt(Object value) {
        return ResourceFactory.createTypedLiteral ( value );
    }
    
    /**
     * Method utility: create new typed literal from uri.
     * @param lexicalform lexicalform of the literal.
     * @param datatype datatype of the literal.
     * @return literal.
     */
    public static Literal lt(String lexicalform, RDFDatatype datatype) {
        return ResourceFactory.createTypedLiteral ( lexicalform, datatype );
    }
    
    /**
     * Method utility: create new defautl jena model.
     * @return jena model.
     */
    public static Model m(){
       return ModelFactory.createDefaultModel();      
    }

    /**
     * Method for load a file in the resource folder like a inpustream.
     * @param filename string of path to the file.
     * @param thisClass this class.
     * @return inputstream of the file.
     */
    public static InputStream loadResourceAsStream(String filename,Class<?> thisClass) {
        return thisClass.getClassLoader().getResourceAsStream(filename);
    }
}//end of the class JenaKit
