package p4535992.util.html;
import java.net.URL;
import java.util.List;

/**
 * Created by Marco on 26/04/2015.
 */
public class Test_Jsoup {
    public static void main(String[] args) throws Exception {
        //TEST JSOUP
        URL url = new URL("http://finanzalocale.interno.it/apps/floc.php/certificati/index/codice_ente/1030577010/cod/4/anno/2013/md/0/cod_modello/CCMU/tipo_modello/U/cod_quadro/04");
        List<List<List<String>>> test = JSoupKit.TablesExtractor(url, true);
    }
}
