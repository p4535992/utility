package com.github.p4535992.util.repositoryRDF.sparql;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Dataset;
import org.slf4j.LoggerFactory;

/**
 * Created by 4535992 on 07/07/2015.
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class SparqlUtilities {
    
    private static final org.slf4j.Logger logger =  
            LoggerFactory.getLogger(SparqlUtilities.class);


    private static SparqlUtilities instance = null;
    protected SparqlUtilities(){}
    public static SparqlUtilities getInstance(){
        if(instance == null) {
            instance = new SparqlUtilities();
        }
        return instance;
    }

    public enum SPARQL_PREFIX{
        SPARQL_TYPE(0),a(1),bbcpont(2),dbpedia(3),dbpont(4),dbpprop(5),dbtune(6),dc(7),dct(8),
        dcterm(9), factbook(10), fb(11),ff(12), foaf(13), geo(14), geonames(15), geoont(16) , geopos(17),
        gis(18), gr(19), km4c(20), km4cr(21),lingvoj(22), musicont(23),nytimes(24),oasis(25),om(26),
        onto(27),opencyc(28),opencycen(29), org(30), ot(31),otn(32), owl(33),pext(34),
        pkm(35), psys(36),ptop(37),rdf(38),rdfs(39),schema(40),skos(41),swvocab(42),
        time(43),ub(44),umbel(45),umbelac(46),umbelen(47),umbelsc(48),vann(49),wordnet(50),wordnet16(51),
        wordnsc(52),xsd(53),yago(54),wgs84(55);

        private final Integer value;
        SPARQL_PREFIX(Integer value) {
            this.value = value;
        }

        public URI getURI(){
            return URI.create(toString());
        }

        public String getPrefix(){
            return this.name();
        }

        @Override
        public String toString() {
            String uri ="";
            switch (this) {
                case SPARQL_TYPE:
                case a:
                    uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"; break;
                case bbcpont: uri = "http://purl.org/ontology/po/"; break;
                case dbpedia: uri = "http://dbpedia.org/resource/"; break;
                case dbpont: uri = "http://dbpedia.org/ontology/"; break;
                case dbpprop: uri = "http://dbpedia.org/property/"; break;
                case dbtune: uri = "http://dbtune.org/bbc/peel/work/"; break;
                case dc: uri = "http://purl.org/dc/elements/1.1/"; break;
                case dct: uri = "http://purl.org/dc/terms/#"; break;
                case dcterm: uri = "http://purl.org/dc/terms/"; break;
                case factbook: uri = "http://www.daml.org/2001/12/factbook/factbook-ont#"; break;
                case fb: uri  = "http://rdf.freebase.com/ns/"; break;
                case ff: uri = "http://factforge.net/"; break;
                case foaf: uri = "http://xmlns.com/foaf/0.1/"; break;
                case geo:
                case geopos:
                case wgs84:
                    uri = "http://www.w3.org/2003/01/geo/wgs84_pos#"; break;
                case geonames: uri = "http://sws.geonames.org/"; break;
                case geoont: uri = "http://www.geonames.org/ontology#"; break;
                case gis: uri  = "http://www.opengis.net/ont/geosparql#"; break;
                case gr: uri  = "http://purl.org/goodrelations/v1#"; break;
                case km4c: uri = "http://www.disit.org/km4city/schema#"; break;
                case km4cr: uri = "http://www.disit.org/km4city/resource#"; break;
                case lingvoj: uri = "http://www.lingvoj.org/ontology#"; break;
                case musicont: uri = "http://purl.org/ontology/mo/"; break;
                case nytimes: uri = "http://data.nytimes.com/"; break;
                case oasis: uri = "http://psi.oasis-open.org/iso/639/#"; break;
                case om: uri  = "http://www.ontotext.com/owlim/"; break;
                case onto: uri = "http://www.ontotext.com/"; break;
                case opencyc: uri = "http://sw.opencyc.org/concept/"; break;
                case opencycen: uri = "http://sw.opencyc.org/2008/06/10/concept/en/"; break;
                case org: uri  = "http://www.w3.org/ns/org#"; break;
                case ot: uri = "http://www.ontotext.com/"; break;
                case otn: uri = "http://www.pms.ifi.uni-muenchen.de/OTN#"; break;
                case owl: uri = "http://www.w3.org/2002/07/owl#"; break;
                case pext: uri = "http://proton.semanticweb.org/protonext#"; break;
                case pkm: uri = "http://proton.semanticweb.org/protonkm#"; break;
                case psys: uri = "http://proton.semanticweb.org/protonsys#"; break;
                case ptop: uri = "http://proton.semanticweb.org/protontop#"; break;
                case rdf: uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"; break;
                case rdfs: uri = "http://www.w3.org/2000/01/rdf-schema#"; break;
                case schema: uri = "http://schema.org/"; break;
                case skos: uri ="http://www.w3.org/2004/02/skos/core#"; break;
                case swvocab: uri = "http://www.w3.org/2003/06/sw-vocab-status/ns#"; break;
                case time: uri = "http://www.w3.org/2006/time#"; break;
                case ub: uri = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#"; break;
                case umbel: uri = "http://umbel.org/umbel#"; break;
                case umbelac: uri = "http://umbel.org/umbel/ac/"; break;
                case umbelen: uri = "http://umbel.org/umbel/ne/wikipedia/"; break;
                case umbelsc: uri = "http://umbel.org/umbel/sc/"; break;
                case vann: uri = "http://purl.org/vocab/vann/#"; break;
                case wordnet: uri = "http://www.w3.org/2006/03/wn/wn20/instances/"; break;
                case wordnet16: uri = "http://xmlns.com/wordnet/1.6/"; break;
                case wordnsc: uri = "http://www.w3.org/2006/03/wn/wn20/schema/"; break;
                case xsd: uri = "http://www.w3.org/2001/XMLSchema#"; break;
                case yago: uri = "http://mpii.de/yago/resource/"; break;

            }
            return uri;
        }
    }

    /*public static final String SPARQL_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String a = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
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
    public static final String schema = "http://schema.org/";
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
    public static final String yago = "http://mpii.de/yago/resource/";*/

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
        namespacePrefixes.put("wgs84","http://www.w3.org/2003/01/geo/wgs84_pos#");
        return namespacePrefixes;
    }

    private static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }catch(URISyntaxException e){
            logger.warn(e.getMessage(),e);
            url = url.replace("http://","");
            return url;
        }
    }

    /**
     * Method to prepare the part of teh query with all prefix.
     * @return string part of the query with the prefixes.
     * e.g. PREFIX foaf:  http://xmlns.com/foaf/0.1/
     */
    public static String preparePrefix(){
       return preparePrefix("",false);
    }

    public static String preparePrefixNoPoint(){
        return preparePrefix("",true);
    }

    public static String preparePrefix(Character characterPrefix){
        /*StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : getDefaultNamespacePrefixes().entrySet()){
            sb.append(characterPrefix).append("prefix ")
                    .append(entry.getKey()).append(": <").append(entry.getValue()).append("> .\n");
        }
        return sb.toString();*/
        return preparePrefix(characterPrefix.toString(),false);
    }

    /**
     * Method to prepare the part of teh query with all prefix.
     * @param map the Map of namespace.
     * @return string part of the query with the prefixes.
     */
    public static String preparePrefix(Map<String,String> map){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : map.entrySet()){
            sb.append("PREFIX ").append(entry.getKey()).append(": <").append(entry.getValue()).append(">  \n");//"\n"
        }
        return sb.toString();
    }

    public static String preparePrefix(String domainUri,boolean noPoint){
        if(domainUri.length() <= 1) return preparePrefixNoUri(domainUri,noPoint);
        else return preparePrefixUri(domainUri);
    }

    private static String preparePrefixNoUri(String symbolPrefix,boolean noPoint){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : getDefaultNamespacePrefixes().entrySet()){
            sb.append(symbolPrefix).append("PREFIX ")
                    .append(entry.getKey()).append(": <").append(entry.getValue()).append(">")
                    .append(noPoint ? " \n" : ". \n");//"\n"
        }
        return sb.toString();
    }

    private static String preparePrefixUri(String domainUri){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : getDefaultNamespacePrefixes().entrySet()){
            if (entry.getValue().contains(domainUri)) {
                return entry.getKey();
               /* return  sb.append("PREFIX ").append(entry.getKey())
                        .append(": <").append(entry.getValue()).append("> .\n").toString();*/
            }
        }
        logger.warn("No prefix found on the map for the domain uri you have specified we create a automatic" +
                "by getting the first two Caracther after the protocol on the uri.");
        return getDomainName(domainUri).substring(0,2);
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

    public static String count(){
        return "SELECT (COUNT(*) as ?count) FROM <http://www.ontotext.com/implicit> WHERE { ?s ?p ?o .}";
    }

    public static String countGraphes() {
        // select all FROM
        return "SELECT (Count(Distinct(?g)) AS ?numberGraphes) { GRAPH ?g { ?s ?p ?o }}";
    }

    /**
     * ;ethod to count triples in the default graph and all triples in
     * named graphs. To account for them, you would need something like
     * @return the String of the query SPARQL for get All triple in all graphs.
     */
    public static String getAllTriple(){
        return "SELECT (COUNT(*) AS ?no) { { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } } }";
    }

    /**
     * Method to count triples in the default graph and omit triples in named graphs
     * @return the String of the query SPARQL for get triple in the default graph.
     */
    public static String getAllDefaultTriple(){
        return "SELECT (COUNT(*) AS ?no) { ?s ?p ?o }";
        //return "SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o .}";
    }

    public static String cleanTriple(String uriGraph){
        /*
        In the first branch simply select any triples directly off of the root.
        In the second branch find any triples one/more steps away from the root
        and the associated triples.  Then in the DELETE template simply delete all
        the matched triples.

        The second branch uses Joshua Taylor's trick from
        http://stackoverflow.com/a/26707541/107591 of using a property path via a
        URI and its negation to find any subject that is reachable by 1 or more
        steps via any property from your root.  You can then simply grab things
        that are directly connected to that subject.
        */
        return "DELETE { ?s ?p ?o }\n" +
                "WHERE {\n" +
                "  {\n" +
                "    <"+uriGraph+"> ?p ?o .\n" + // Find things directly connected to the root
                "    BIND(<"+uriGraph+"> AS ?s)\n" +
                "  }\n" +
                "  UNION\n" +
                "  {\n" +
                "    <"+uriGraph+"> (<>|!<>)+ ?s .\n" + //Find everything indirectly connected to the root
                "    ?s ?p ?o .\n" +
                "  }\n" +
                "}";
    }

    public static String cleanTriple(String uriGraph,String propertyGraphChild){
        return "DELETE { ?s ?p ?o }\n" +
                "WHERE {\n" +
                "  {\n" +
                "    <"+uriGraph+"> ?p ?o .\n" + // Find things directly connected to the root
                "    BIND(<"+uriGraph+"> AS ?s)\n" +
                "  }\n" +
                "  UNION\n" +
                "  {\n" +
                "    <"+uriGraph+"> :"+propertyGraphChild+"+ ?s .\n" + //Find everything indirectly connected to the root
                "    ?s ?p ?o .\n" +
                "  }\n" +
                "}";
    }

    /**
     * Given a root, return me the tree graph associated to that root.
     * @param uriGraph the String Uri of the Graph.
     * @return the String SPARQL Query.
     */
    public static String getGraph(String uriGraph){
        return "CONSTRUCT {?s ?p ?o} { GRAPH <"+uriGraph+"> { ?s ?p ?o } }";
    }

    /**
     * Given a root, delete the tree graph associated to that root.
     * @param uriGraph the String Uri of the Graph.
     * @return the String SPARQL Query.
     */
    public static String clearGraph(String uriGraph){
        return "CLEAR GRAPH <"+uriGraph+">";
    }

    /**
     * Method to insert data in a specific Graph.
     * @param uriGraph the String Uri of the Graph.
     * @param listTriple the list of Triple to insert.
     * @return the String SPARQL Query.
     */
    public static String insertData(String uriGraph,List<String[]> listTriple){
        return "INSERT DATA { GRAPH <"+uriGraph+"> { ... } }";
    }

    /**
     * Method to  cast a literal to an IRI within SPARQL in jena.
     * @param uriGraph the String uri of the Subject og the Triple.
     * @param uriProperty the String uri of the Property og the Triple.
     * @param asIriName the String name assigned to the IRI Literal.
     * @return the String SPARQL Query.
     */
    public static String bindObjectToIRI(String uriGraph,String uriProperty,String asIriName){
        return ""+uriGraph+" "+uriProperty+" ?baz " +
                "BIND(IRI(str(?baz)) as ?"+asIriName+") ";
                //"?"+asIriName+" rdfs:label ?tiz";
    }


    //Validation from web service
    /**
     * <form accept-charset="UTF-8" method="post" action="validate/query">
     <p>
     <textarea rows="30" cols="70" name="query">PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX fn: <http://www.w3.org/2005/xpath-functions#> PREFIX apf: <http://jena.hpl.hp.com/ARQ/property#> PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT ?book ?title WHERE { ?book dc:title ?title }</textarea>
     Input syntax:
     <input type="radio" checked="checked" value="SPARQL" name="languageSyntax">
     SPARQL
     <input type="radio" value="ARQ" name="languageSyntax">
     SPARQL extended syntax
     Output:
     <input type="checkbox" checked="checked" value="sparql" name="outputFormat">
     SPARQL
     <input type="checkbox" value="algebra" name="outputFormat">
     SPARQL algebra
     <input type="checkbox" value="quads" name="outputFormat">
     SPARQL algebra (quads)
     <input type="checkbox" value="opt" name="outputFormat">
     SPARQL algebra (general optimizations)
     <input type="checkbox" value="optquads" name="outputFormat">
     SPARQL algebra (quads, general optimizations)
     Line numbers:
     <input type="radio" checked="checked" value="true" name="linenumbers">
     Yes
     <input type="radio" value="false" name="linenumbers">
     No
     <br>
     <input type="submit" value="Validate SPARQL Query">
     </p>
     </form>
     */
   /*  public boolean validateSparqlQuery(String query){
        String url = "" ;
        HttpUtilities.execute
    }*/

}
