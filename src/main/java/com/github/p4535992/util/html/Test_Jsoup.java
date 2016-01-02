package com.github.p4535992.util.html;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 26/04/2015.
 */
public class Test_Jsoup {
    public static void main(String[] args) throws Exception {
        //TEST JSOUP
        //URL url = new URL("http://finanzalocale.interno.it/apps/floc.php/certificati/index/codice_ente/1030577010/cod/4/anno/2013/md/0/cod_modello/CCMU/tipo_modello/U/cod_quadro/04");
        /*String urlBaseComuni = "http://www.comuni-italiani.it/alfa/";
        URL url = new URL(urlBaseComuni);
        List<List<List<String>>> listaComuni = JSoupKit.TablesExtractor(url, false);
        List<String> support = new ArrayList<>();
        boolean flag = false;
        for (List<List<String>> listListHtml : listaComuni) {
            if (listListHtml.size() > 0) {
                for (List<String> listHtml : listListHtml) {
                    for(String html : listHtml){
                        if(html.contains("[HREF=") && !html.contains("..")){
                            support.add(html);
                        }
                    }
                }
            }

        }
        List<RPC> listRpc = new ArrayList<>();
        for (String html : support) {
            //http://www.comuni-italiani.it/alfa/001.html
            String urlLetter = urlBaseComuni + html.replace("[HREF=", "").replace("]", "");
            List<List<List<String>>> listaComuniForLetter = JSoupKit.TablesExtractor(urlLetter, false);
            for (List<String> listHtml : listaComuniForLetter.get(5)) {
                for (int i =0; i < listHtml.size(); i++) {
                    RPC rpc = new RPC();
                    rpc.setCity(listHtml.get(i));
                    i++;
                    String urlName = "http://www.comuni-italiani.it/" + listHtml.get(i).replace("[HREF=", "").replace("]", "").replace("../","");
                    List<List<List<String>>> listaComuneForName = JSoupKit.TablesExtractor(urlName, false);
                    for(List<String> codici : listaComuneForName.get(6)){
                        if(codici.get(0).equalsIgnoreCase("Regione")) rpc.setRegion(codici.get(1));
                        if(codici.get(0).equalsIgnoreCase("Provincia")) rpc.setProvince(codici.get(1));
                        if(codici.get(0).equalsIgnoreCase("Zona")) rpc.setItalyZone(codici.get(1));
                        if(codici.get(0).equalsIgnoreCase("CAP")) rpc.setCAP(codici.get(1));
                        //if(codici.get(0).equalsIgnoreCase("Prefisso Telefonico")) rpc.set(codici.get(1));
                        if(codici.get(0).equalsIgnoreCase("Codice Istat")) rpc.setCodeISTAT(codici.get(1));
                        if(codici.get(0).equalsIgnoreCase("Codice Catastale")) rpc.setCodeCatastale(codici.get(1));
                    }
                    listRpc.add(rpc);
                }

            }
        }*/

        String url = "http://www.dossier.net/utilities/coordinate-geografiche/";
        List<List<List<String>>> listaComuni = JSoupUtilities.TablesExtractor(url, false);
        List<String> support = new ArrayList<>();
    }
}
