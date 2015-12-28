package util;

import com.github.p4535992.util.repositoryRDF.sesame.SesameUtilities;

public class Test_GraphDB6 {

	/**
	 * This is the entry point of the example application. First, the command-line parameters are intialised.
	 * Then these parameters are passed to an instance of the GettingStarted application and used to insert,
	 * initialise and login to the local instance of Sesame.
	 * 
	 * @param args
	 *            Command line parameters
	 */
	public static void main(String[] args) {
		
		// Special handling for JAXP XML parser that limits entity expansion
		// see
		// http://java.sun.com/j2se/1.5.0/docs/guide/xml/jaxp/JAXP-Compatibility_150.html#JAXP_security
		System.setProperty("entityExpansionLimit", "1000000");
		// Set default values for missing parameters
		/*
		params.setDefaultValue(PARAM_CONFIG, "./owlim.ttl");
		params.setDefaultValue(PARAM_SHOWRESULTS, "true");
		params.setDefaultValue(PARAM_SHOWSTATS, "false");
		params.setDefaultValue(PARAM_UPDATES, "false");
		params.setDefaultValue(PARAM_QUERYFILE, "./queries/sample.sparql");
		params.setDefaultValue(PARAM_EXPORT_FORMAT, RDFFormat.NTRIPLES.getName());

		params.setDefaultValue(PARAM_PRELOAD, "./preload");
		params.setDefaultValue(PARAM_VERIFY, "true");
		params.setDefaultValue(PARAM_STOP_ON_ERROR, "true");
		params.setDefaultValue(PARAM_PRESERVE_BNODES, "true");
		params.setDefaultValue(PARAM_DATATYPE_HANDLING, DatatypeHandling.VERIFY.name());
		params.setDefaultValue(PARAM_CHUNK_SIZE, "500000");

		params.setDefaultValue(PARAM_PARALLEL_LOAD, "false");
        
		params.setDefaultValue(PARAM_SHOWRESULTS, "true");
		//params.setDefaultValue(PARAM_EXPORT_SUFFIX, "n3");
		
		params.setDefaultValue(PARAM_EXPORT_FORMAT,RDFFormat.N3.getName()); //null di default il valore e' NTRIPLES
		params.setDefaultValue(PARAM_EXPORT_FILE, "export"+params.getValue(PARAM_EXPORT_FORMAT));
		//explicit,implicit,all
		params.setDefaultValue(PARAM_EXPORT_TYPE,"specific");
		params.setValue(PARAM_REPOSITORY, "km4city04");//km4c_test4	
		params.setDefaultValue(PARAM_URL, "http://localhost:8080/openrdf-sesame/");
			
		*/
//		String queryString = ""
//				+ "CONSTRUCT {?service ?p ?o.}  "
//				+ "WHERE {?service a <http://www.disit.org/km4city/schema#Service>;"
//				+ "                  ?p ?o ."
//				+ "} LIMIT 10";	
		//580493
		String queryString = ""
				+ "CONSTRUCT {?service ?p ?o.}  "
				+ "WHERE {?service a <http://www.disit.org/km4city/schema#Service>;"
				+ "       ?p ?o . } LIMIT 600000 OFFSET 0 ";

		SesameUtilities s = null;
		try {
			// The ontologies and datasets specified in the 'import' parameter
			// of the Sesame configuration file are loaded during
			// initialization.
			// Thus, for large datasets the initialisation could take
			// considerable time.
			/*long initializationStart = System.currentTimeMillis();
			s = new home.home.utils.sesame.SesameUtil(
                                "http://localhost:8080/openrdf-sesame/", //url
                                "km4city04", //repositoryid
                                null,        //fileconfig
                                null, //user
                                null //pass
                        );
                        */
		/*	s.setOutput("outputxxx", "nt", true);
			s.connect(
					"http://localhost:8080/openrdf-sesame/",
					"km4city04",
					null,
					null);*/
			//s.evaluateQueries("queryFile");
			s.executeQuerySPARQLFromString(queryString);
			// Demonstrate the basic operations on a repository
			/*
			gettingStartedApplication.loadFiles();
			gettingStartedApplication.showInitializationStatistics(System.currentTimeMillis()
					- initializationStart);
			gettingStartedApplication.iterateNamespaces();
			gettingStartedApplication.evaluateQueries();
			gettingStartedApplication.insertAndDeleteStatement();
			gettingStartedApplication.export();
			
			gettingStartedApplication.connect();
			gettingStartedApplication.evaluateQueries();
			*/
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		//finally {//if (s != null){}s.shutdown();}
	}

}
