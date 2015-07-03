package com.github.p4535992.util.http;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

/**
 * Created by 4535992 on 03/07/2015.
 * @author 4535992.
 * @version 2015-07-03.
 */
@SuppressWarnings("unused")
public class HttpKit {

    private static HttpKit instance = null;

    protected HttpKit(){}

    public static  HttpKit getInstance(){
        if(instance == null) {
            instance = new HttpKit();
        }
        return instance;
    }

    public URL resolveURL(URL base, String target){
        try {
            return URLUtil.resolveURL(base,target);
        } catch (MalformedURLException e) {
            SystemLog.warning("The "+base+ File.separator+target+" is not a correct url !!!");
            return null;
        }
    }

    public String getDomainName(String url){
        try {
            if (StringKit.isURL(url)) {
                return getDomainName(new URL(url));
            } else {
                SystemLog.warning("The " + url + " is not a correct url !!!");
                return null;
            }
        }catch (MalformedURLException e) {
            SystemLog.warning("The " + url + " is not a correct url !!!");
            return null;
        }

    }

    public String getDomainName(URL url){
        return URLUtil.getDomainName(url);
    }

    public String getTopLevelDomainName(String url){
        try {
            if (StringKit.isURL(url)) {
                return getTopLevelDomainName(new URL(url));
            } else {
                SystemLog.warning("The " + url + " is not a correct url !!!");
                return null;
            }
        }catch (MalformedURLException e) {
            SystemLog.warning("The " + url + " is not a correct url !!!");
            return null;
        }
    }

    public String getTopLevelDomainName(URL url){
        try {
            return URLUtil.getTopLevelDomainName(url);
        } catch (MalformedURLException e) {
            SystemLog.warning("The " + url + " is not a correct url !!!");
            return null;
        }
    }

    public boolean isSameDomainName(String url1,String url2){
        try {
            if (StringKit.isURL(url1) && StringKit.isURL(url2)) {
                return isSameDomainName(new URL(url1), new URL(url2));
            } else {
                SystemLog.warning("The " + url1 + " or "+url2+" is not a correct url !!!");
                return false;
            }
        }catch (MalformedURLException e) {
            SystemLog.warning("The " + url1 + " or "+url2+" is not a correct url !!!");
            return false;
        }
    }

    public  boolean isSameDomainName(URL url1,URL url2){
            return URLUtil.isSameDomainName(url1,url2);
    }

    public String[] getHostSegments(String url){
        try {
            if (StringKit.isURL(url)) {
                return getHostSegments(new URL(url));
            } else {
                SystemLog.warning("The " + url + " is not a correct url !!!");
                return null;
            }
        } catch (MalformedURLException e) {
            SystemLog.warning("The " + url + " is not a correct url !!!");
            return null;
        }
    }

    public String[] getHostSegments(URL url){
        return URLUtil.getHostSegments(url);
    }

    public String chooseRepresentativeUrl(String src, String dst, boolean temp){
        return URLUtil.chooseRepresentativeUrl(src, dst, temp);
    }

    public String getPage(String url){
        return URLUtil.getPage(url);
    }

    public String getProtocol(String url) {
        return URLUtil.getProtocol(url);
    }

    public String getProtocol(URL url) {
        return URLUtil.getProtocol(url);
    }

    public String convertUrlToASCII(String url){
        return URLUtil.toASCII(url);
    }

    public String convertUrlToUNICODE(String url){
        return URLUtil.toUNICODE(url);
    }

    public String get(String url,Map<String, String> headers){
        try {
            return HttpUtilApache.get(url,headers);
        } catch (IOException e) {
            return null;
        }
    }

    public String get(String url){
        try {
            return HttpUtilApache.get(url);
        } catch (IOException e) {
            return null;
        }
    }

    public String post(String url, String body,Map<String, String> headers){
        try {
            return HttpUtilApache.post(url, body, headers);
        } catch (IOException e) {
            return null;
        }
    }

    public String post(String url, String body){
        try {
            return HttpUtilApache.post(url, body);
        } catch (IOException e) {
            return null;
        }
    }

    public String postForm(String url, Map<String, String> params){
        try {
            return HttpUtilApache.postForm(url, params);
        } catch (IOException e) {
            return null;
        }
    }

    public String postForm(String url, Map<String, String> params,Map<String,String> headers){
        try {
            return HttpUtilApache.postForm(url, params, headers);
        } catch (IOException e) {
            return null;
        }
    }

    public String put(String url, String body,Map<String, String> headers){
        try {
            return HttpUtilApache.put(url, body, headers);
        } catch (IOException e) {
            return null;
        }
    }

    public String put(String url, String body){
        try {
            return HttpUtilApache.put(url, body);
        } catch (IOException e) {
            return null;
        }
    }

    public String delete(String url){
        try {
            return HttpUtilApache.delete(url);
        } catch (IOException e) {
            return null;
        }
    }

    public String delete(String url, Map<String, String> headers){
        try {
            return HttpUtilApache.delete(url, headers);
        } catch (IOException e) {
            return null;
        }
    }

    public String appendQueryParams(String url,Map<String, String> params){
        try {
            return HttpUtilApache.appendQueryParams(url, params);
        } catch (IOException e) {
            return null;
        }
    }

    public Map<String, String> getQueryParams(String url){
        try {
            return HttpUtilApache.getQueryParams(url);
        } catch (IOException e) {
            return null;
        }
    }

    public String removeQueryParams(String url){
        try {
            return HttpUtilApache.removeQueryParams(url);
        } catch (IOException e) {
            return null;
        }
    }

    int tentativi = 0;
    public boolean isWebPageExists(String url){
        Document doc;
        try{
            if(tentativi < 3){
                //doc = Jsoup.connect(url).get();
                //doc = Jsoup.parse(Jsoup.connect(url).ignoreContentType(true).execute().contentType());
                doc = Jsoup.connect(url).timeout(10*1000).get();
                tentativi = 0;
                return doc != null;
            }else{
                tentativi = 0;
            }
        }catch(Exception e){
            tentativi++;
            try {
                HttpUtilApache.waiter();
            } catch (InterruptedException e1) {
                if(tentativi < 3) isWebPageExists(url);
            }
            if(tentativi < 3) isWebPageExists(url);
        }
        try{
            String html = HttpUtilApache.get(url);
            doc = Jsoup.parse(html);
            SystemLog.message("HTTP GET HA AVUTO SUCCESSO");
            tentativi = 0;
            return doc != null;
        }catch(Exception en){
            tentativi = 0;
            return false;
        }

    }




}
