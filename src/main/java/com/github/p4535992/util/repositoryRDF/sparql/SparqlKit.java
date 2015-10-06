package com.github.p4535992.util.repositoryRDF.sparql;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4535992 on 07/07/2015.
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class SparqlKit {

    public static final String SPARQL_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
    public static final String a = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";

    public static final String bbcpont = "http://purl.org/ontology/po/";
    public static final String dbpedia = "http://dbpedia.org/resource/";
    public static final String dbpont = "http://dbpedia.org/ontology/";
    public static final String dbpprop = "http://dbpedia.org/property/";
    public static final String dbtune = "http://dbtune.org/bbc/peel/work/";
    public static final String dc = "http://purl.org/dc/elements/1.1/";
    public static final String dct = "http://purl.org/dc/terms/#";
    public static final String dcterm = "http://purl.org/dc/terms/";
    public static final String factbook = "http://www.daml.org/2001/12/factbook/factbook-ont#";
    public static final String fb = "http://rdf.freebase.com/ns/";
    public static final String ff = "http://factforge.net/";
    public static final String foaf = "http://xmlns.com/foaf/0.1/";
    public static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String geonames = "http://sws.geonames.org/";
    public static final String geoont = "http://www.geonames.org/ontology#";
    public static final String geopos = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String gis = "http://www.opengis.net/ont/geosparql#";
    public static final String gr = "http://purl.org/goodrelations/v1#";
    public static final String km4c = "http://www.disit.org/km4city/schema#";
    public static final String km4cr = "http://www.disit.org/km4city/resource#";
    public static final String lingvoj = "http://www.lingvoj.org/ontology#";
    public static final String musicont = "http://purl.org/ontology/mo/";
    public static final String nytimes = "http://data.nytimes.com/";
    public static final String oasis = "http://psi.oasis-open.org/iso/639/#";
    public static final String om = "http://www.ontotext.com/owlim/";
    public static final String onto = "http://www.ontotext.com/";
    public static final String opencyc = "http://sw.opencyc.org/concept/";
    public static final String opencycen = "http://sw.opencyc.org/2008/06/10/concept/en/";
    public static final String org = "http://www.w3.org/ns/org#";
    public static final String ot = "http://www.ontotext.com/";
    public static final String otn = "http://www.pms.ifi.uni-muenchen.de/OTN#";
    public static final String owl = "http://www.w3.org/2002/07/owl#";
    public static final String pext = "http://proton.semanticweb.org/protonext#";
    public static final String pkm = "http://proton.semanticweb.org/protonkm#";
    public static final String psys = "http://proton.semanticweb.org/protonsys#";
    public static final String ptop = "http://proton.semanticweb.org/protontop#";
    public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String schema = "http://schema.org/#";
    public static final String skos="http://www.w3.org/2004/02/skos/core#";
    public static final String swvocab = "http://www.w3.org/2003/06/sw-vocab-status/ns#";
    public static final String time = "http://www.w3.org/2006/time#";
    public static final String ub = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#";
    public static final String umbel = "http://umbel.org/umbel#";
    public static final String umbelac = "http://umbel.org/umbel/ac/";
    public static final String umbelen = "http://umbel.org/umbel/ne/wikipedia/";
    public static final String umbelsc = "http://umbel.org/umbel/sc/";
    public static final String vann = "http://purl.org/vocab/vann/#";
    public static final String wordnet = "http://www.w3.org/2006/03/wn/wn20/instances/";
    public static final String wordnet16 = "http://xmlns.com/wordnet/1.6/";
    public static final String wordnsc = "http://www.w3.org/2006/03/wn/wn20/schema/";
    public static final String xsd = "http://www.w3.org/2001/XMLSchema#";
    public static final String yago = "http://mpii.de/yago/resource/";

    private static SparqlKit instance = null;
    protected SparqlKit(){}
    public static SparqlKit getInstance(){
        if(instance == null) {
            instance = new SparqlKit();
        }
        return instance;
    }

    private static final Map<String,String> namespacePrefixes = new HashMap<>();

    /**
     * Method utility set some knowed namespace prefix.
     * @return the Map of namespace prefix.
     */
    public static Map<String,String> getDefaultNamespacePrefixes(){
        namespacePrefixes.put("bbc-pont","http://purl.org/ontology/po/");
        namespacePrefixes.put("dbpedia","http://dbpedia.org/resource/");
        namespacePrefixes.put("dbp-ont","http://dbpedia.org/ontology/");
        namespacePrefixes.put("dbp-prop","http://dbpedia.org/property/");
        namespacePrefixes.put("dbtune","http://dbtune.org/bbc/peel/work/");
        namespacePrefixes.put("dc","http://purl.org/dc/elements/1.1/");
        namespacePrefixes.put("dct","http://purl.org/dc/terms/#");
        namespacePrefixes.put("dc-term","http://purl.org/dc/terms/");
        namespacePrefixes.put("factbook","http://www.daml.org/2001/12/factbook/factbook-ont#");
        namespacePrefixes.put("fb","http://rdf.freebase.com/ns/");
        namespacePrefixes.put("ff","http://factforge.net/");
        namespacePrefixes.put("foaf","http://xmlns.com/foaf/0.1/");
        namespacePrefixes.put("geo","http://www.w3.org/2003/01/geo/wgs84_pos#");
        namespacePrefixes.put("geonames","http://sws.geonames.org/");
        namespacePrefixes.put("geo-ont","http://www.geonames.org/ontology#");
        namespacePrefixes.put("geo-pos","http://www.w3.org/2003/01/geo/wgs84_pos#");
        namespacePrefixes.put("gis","http://www.opengis.net/ont/geosparql#");
        namespacePrefixes.put("gr","http://purl.org/goodrelations/v1#");
        namespacePrefixes.put("km4c","http://www.disit.org/km4city/schema#");
        namespacePrefixes.put("km4cr","http://www.disit.org/km4city/resource#");
        namespacePrefixes.put("lingvoj","http://www.lingvoj.org/ontology#");
        namespacePrefixes.put("music-ont","http://purl.org/ontology/mo/");
        namespacePrefixes.put("nytimes","http://data.nytimes.com/");
        namespacePrefixes.put("oasis","http://psi.oasis-open.org/iso/639/#");
        namespacePrefixes.put("om","http://www.ontotext.com/owlim/");
        namespacePrefixes.put("onto","http://www.ontotext.com/");
        namespacePrefixes.put("opencyc","http://sw.opencyc.org/concept/");
        namespacePrefixes.put("opencyc-en","http://sw.opencyc.org/2008/06/10/concept/en/");
        namespacePrefixes.put("org","http://www.w3.org/ns/org#");
        namespacePrefixes.put("ot","http://www.ontotext.com/");
        namespacePrefixes.put("otn","http://www.pms.ifi.uni-muenchen.de/OTN#");
        namespacePrefixes.put("owl","http://www.w3.org/2002/07/owl#");
        namespacePrefixes.put("pext","http://proton.semanticweb.org/protonext#");
        namespacePrefixes.put("pkm","http://proton.semanticweb.org/protonkm#");
        namespacePrefixes.put("psys","http://proton.semanticweb.org/protonsys#");
        namespacePrefixes.put("ptop","http://proton.semanticweb.org/protontop#");
        namespacePrefixes.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        namespacePrefixes.put("rdfs","http://www.w3.org/2000/01/rdf-schema#");
        namespacePrefixes.put("schema","http://schema.org/#");
        namespacePrefixes.put("skos","http://www.w3.org/2004/02/skos/core#");
        namespacePrefixes.put("sw-vocab","http://www.w3.org/2003/06/sw-vocab-status/ns#");
        namespacePrefixes.put("time","http://www.w3.org/2006/time#");
        namespacePrefixes.put("ub","http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#");
        namespacePrefixes.put("umbel","http://umbel.org/umbel#");
        namespacePrefixes.put("umbel-ac","http://umbel.org/umbel/ac/");
        namespacePrefixes.put("umbel-en","http://umbel.org/umbel/ne/wikipedia/");
        namespacePrefixes.put("umbel-sc","http://umbel.org/umbel/sc/");
        namespacePrefixes.put("vann","http://purl.org/vocab/vann/#");
        namespacePrefixes.put("wordnet","http://www.w3.org/2006/03/wn/wn20/instances/");
        namespacePrefixes.put("wordnet16","http://xmlns.com/wordnet/1.6/");
        namespacePrefixes.put("wordn-sc","http://www.w3.org/2006/03/wn/wn20/schema/");
        namespacePrefixes.put("xsd","http://www.w3.org/2001/XMLSchema#");
        namespacePrefixes.put("yago","http://mpii.de/yago/resource/");
        return namespacePrefixes;
    }

    /**
     * Method to prepare the part of teh query with all prefix.
     * @return string part of the query with the prefixes.
     */
    public static String preparePrefix(){
        Map<String,String> map = getDefaultNamespacePrefixes();
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : map.entrySet()){
            sb.append("prefix ").append(entry.getKey()).append(": <").append(entry.getValue()).append("> \n");
        }
        return sb.toString();
    }

    /**
     * Method to prepare the part of teh query with all prefix.
     * @param map the Map of namespace.
     * @return string part of the query with the prefixes.
     */
    public static String preparePrefix(Map<String,String> map){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : map.entrySet()){
            sb.append("prefix ").append(entry.getKey()).append(": <").append(entry.getValue()).append("> \n");
        }
        return sb.toString();
    }

    /**
     * Method to prepare a query SPARQL for count the number of triple in a repository.
     * @param baseUri the String of the basic graph .
     * @return the String of the query SPARQL for count the number of triple.
     */
    public static String countNumberOfTriple(String baseUri){
        return "SELECT (COUNT(*) as ?count) " +
                "FROM <"+baseUri+"> " +
                "WHERE { ?s ?p ?o .}";
    }

    /**
     * Method to prepare a query SPARQL for count the number of triple in a repository.
     * @return the String of the query SPARQL for count the number of triple.
     */
    public static String countNumberOfTriple(){
        return "SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o .}";
    }



}
