package com.github.p4535992.util.object;

import com.github.p4535992.util.html.JSoupKit;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tenti on 12/10/2015.
 */
public class RPC {

    private String region;
    private String province;
    private String city;

    private Float lat;
    private Float lng;

    private GeoDocument geoDocument;

    private String codeISTAT;
    private String codeCatastale;
    private String italyZone;
    private String CAP;

    public RPC(){}

    public RPC(String region,String province, String city,Float lat,Float lng,GeoDocument geoDocument){
        this.region = region;
        this.province = province;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
        this.geoDocument = geoDocument;
    }

    public RPC(String region,String province, String city,Float lat,Float lng,GeoDocument geoDocument,
               String codeISTAT,String codeCatastale, String italyZone,String CAP){
        this.region = region;
        this.province = province;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
        this.geoDocument = geoDocument;
        this.codeISTAT = codeISTAT;
        this.codeCatastale = codeCatastale;
        this.italyZone = italyZone;
        this.CAP = CAP;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public GeoDocument getGeoDocument() {
        return geoDocument;
    }

    public void setGeoDocument(GeoDocument geoDocument) {
        this.geoDocument = geoDocument;
    }

    public String getCodeISTAT() {
        return codeISTAT;
    }

    public void setCodeISTAT(String codeISTAT) {
        this.codeISTAT = codeISTAT;
    }

    public String getCodeCatastale() {
        return codeCatastale;
    }

    public void setCodeCatastale(String codeCatastale) {
        this.codeCatastale = codeCatastale;
    }

    public String getItalyZone() {
        return italyZone;
    }

    public void setItalyZone(String italyZone) {
        this.italyZone = italyZone;
    }

    public String getCAP() {
        return CAP;
    }

    public void setCAP(String CAP) {
        this.CAP = CAP;
    }

    @Override
    public String toString() {
        return "RPC{" +
                "region='" + region + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", geoDocument=" + geoDocument +
                ", codeISTAT='" + codeISTAT + '\'' +
                ", codeCatastale='" + codeCatastale + '\'' +
                ", italyZone='" + italyZone + '\'' +
                ", CAP='" + CAP + '\'' +
                '}';
    }

    public static List<RPC>getInfoRPC1() throws Exception {
        String urlBaseComuni = "http://www.comuni-italiani.it/alfa/";
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
        }
        return listRpc;
    }
}
