package util;

import org.openrdf.repository.RepositoryException;
import com.github.p4535992.util.sesame.SesameUtil28;

/**
 * Created by Marco on 02/05/2015.
 */
public class Test_Sesame {

    private static String SPARQL =  ""
            + "CONSTRUCT {?service ?p ?o.}  "
            + "WHERE {?service a <http://www.disit.org/km4city/schema#Service>;"
            + "       ?p ?o . } LIMIT 10 OFFSET 0 ";

    public static void main(String args[]) throws RepositoryException {
        SesameUtil28 sesame = new SesameUtil28();
        sesame.setParameterLocalRepository(
                "owlim",
                "C:\\Users\\Marco\\AppData\\Roaming\\Aduna\\OpenRDF Sesame\\repositories",
                "C:\\Users\\Marco\\AppData\\Roaming\\Aduna\\OpenRDF Sesame\\repositories\\km4c_test4",
                "owl-horst-optimized",
                "siimobility",
                "siimobility"
                );

//        sesame.setOutput("testSesame", "ttl", true);
//        Repository repository = sesame.connectToRemoteLocation();
//        sesame.executeSingleQuery(SPARQL);

    }
}
