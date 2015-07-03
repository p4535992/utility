package com.github.p4535992.util.http;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ProxySelector;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by 4535992 on 03/07/2015.
 * @author 4535992.
 * @version 2015-07-03.
 */
@SuppressWarnings("unused")
public class HttpUtilApache4 {

    public static String GETWithRetry(String URL) throws IOException {
        String content ="";

        // HttpClient httpclient;
        org.apache.http.impl.client.CloseableHttpClient httpclient = org.apache.http.impl.client.HttpClients.createDefault();
        DefaultHttpRequestRetryHandler def = new DefaultHttpRequestRetryHandler();
        org.apache.http.client.methods.HttpGet httpget = new org.apache.http.client.methods.HttpGet(URL);
        //httpget.getParams().setParameter("RETRY_HANDLER", def);
        try {
            //CloseableHttpResponse response = httpclient.execute(httpget);
            org.apache.http.HttpResponse response = httpclient.execute(httpget);
            System.out.println(response);
            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    // do something useful
                    content = StringKit.convertStreamToString(instream);
                }
            }
        } finally {
            httpget.releaseConnection();
        }
        return content;
    }

    public static String GET4(String url) throws IOException{
        String content ="";
        //HttpClient httpclient = new DefaultHttpClient();
        CloseableHttpClient httpclient = org.apache.http.impl.client.HttpClients.createDefault();
        DefaultHttpRequestRetryHandler def = new DefaultHttpRequestRetryHandler();
        HttpGet httpget = new org.apache.http.client.methods.HttpGet(url);
        //httpget.getParams().setParameter("RETRY_HANDLER", def);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            //HttpResponse response = httpclient.execute(httpget);
            System.out.println(response);
            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    // do something useful
                    content = HttpUtilApache.streamToString(instream);
                }
            }
        } finally {
            httpget.releaseConnection();
        }
        return content;
    }

    public static String GET42(String url) throws IOException {
        String content="";
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig);
        httpget.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Firefox/3.6.13");
        // Execution context can be customized locally.
        HttpClientContext context = HttpClientContext.create();
        // Contextual attributes set the local context level will take
        // precedence over those set at the client level.
        context.setAttribute("http.protocol.version", HttpVersion.HTTP_1_1);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Execute the method.
            HttpResponse response = httpClient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Method failed: " + response.getStatusLine());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            StringBuilder buf = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                buf.append(output);
            }
            content = buf.toString();
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        // Release the connection.

        return content;
    }

    public static void waiter() throws InterruptedException{
        Random generator = new Random();
        long stopRndm = (long) (generator.nextFloat() * 1000);
        stopExecution(stopRndm);
        Thread.sleep((generator.nextInt(5)*1000 + generator.nextInt(6)*1000));
        //Thread.sleep((generator.nextInt(6)+5)*1000);
    }

    /**
     * Method for set a waiting from a request to another.
     * @param millisec input in milliseconds.
     */
    public static void stopExecution(long millisec){
        long t0,t1;
        t0 = System.currentTimeMillis();
        do{
            t1=System.currentTimeMillis();
        }
        while (t1-t0<millisec);
    }

    /**
     * Method for read a resource allocated on a Reader object.
     * @param rd buffer reader where the reosurce is allocated.
     * @return return string of the response.
     * @throws IOException resource not found.
     */
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    /**
     * Method read the json response from a url resource.
     * @param url string as url.
     * @return json resposne from the url resource.
     * @throws IOException reosurce not found.
     * @throws JSONException json object error.
     */
    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    public static HttpClient createHttpClientOrProxy(boolean check) {
        HttpClientBuilder hcBuilder = HttpClients.custom();
        if(check) {
            // Set HTTP proxy, if specified in system properties
            if (System.getProperty("http.proxyHost") != null) {
                int port = 80;
                if (System.getProperty("http.proxyPort") != null) {
                    port = Integer.parseInt(System.getProperty("http.proxyPort"));
                }
                HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), port, "http");
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                hcBuilder.setRoutePlanner(routePlanner);
            }
            return hcBuilder.build(); //ClosableCLient
        }else {
            //or with SystemDefault
            //CloseableHttpClient client2 = HttpClients.createSystem();
            return hcBuilder.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                    .build();
        }
    }
}
