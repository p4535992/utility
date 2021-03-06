package old_test;

import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.base.RepositoryConnectionWrapper;

import com.github.p4535992.util.rdf.Sesame2Utilities;

import java.io.UnsupportedEncodingException;

/**
 * Created by Marco on 02/05/2015.
 */
public class Test_Sesame {

    private static String SPARQL  = "CONSTRUCT {?service ?p ?o.} "
            + "WHERE {?service a <http://www.disit.org/km4city/schema#Service>;"
            + "       ?p ?o . } LIMIT 600000 OFFSET 0 ";

    public static void main(String args[]) throws RepositoryException, MalformedQueryException, QueryEvaluationException, UnsupportedEncodingException {
        Sesame2Utilities sesame = Sesame2Utilities.getInstance();
        /*sesame.setParameterLocalRepository(
                "owlim",
                "C:\\Users\\tenti\\AppData\\Roaming\\Aduna\\OpenRDF Sesame\\repositories",
                "C:\\Users\\tenti\\AppData\\Roaming\\Aduna\\OpenRDF Sesame\\repositories\\km4city04",
                "owl-horst-optimized",
                "siimobility",
                "siimobility"
                );*/

        sesame.setOutput("C:\\Users\\tenti\\Documents\\GitHub\\EAT\\utility\\src\\test\\java\\util\\testSesame.ttl", "ttl", true);
        //sesame.setURLRepositoryId("km4city04");
        Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/km4city04");
        RepositoryConnectionWrapper wrap = sesame.createRepositoryConnectionWrapper(rep);
        QueryLanguage sparql = sesame.toQueryLanguage("SPARQL");
        //String query = (SparqlKit.preparePrefix()+SPARQL).replaceAll("[\uFEFF-\uFFFF]", "").trim();
        //String query2 = new String(query.getBytes(), "UTF-8").replaceAll("[\uFEFF-\uFFFF]", "").trim();
        /*GraphQuery xx = wrap.prepareGraphQuery(
                sparql,
                query,
                "http://www.disit.org/km4city/schema");*/
        //String ww = xx.toString();
        sesame.execSparqlFromStringOnRepository(SPARQL);

    }
}
