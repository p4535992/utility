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

import java.io.*;
import java.net.ProxySelector;

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
        org.apache.http.impl.client.CloseableHttpClient httpclient =
                org.apache.http.impl.client.HttpClients.createDefault();
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
                    content = HttpUtil.streamToString(instream);
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
        return content;
    }

    /**
     * Method to prepare a HttpClient 4 from apache.http.
     * @param check if true try to find in the System the configuration of the proxy.
     * @return HttpClient object.
     */
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
