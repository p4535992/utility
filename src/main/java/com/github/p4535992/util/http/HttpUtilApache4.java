package com.github.p4535992.util.http;

import com.github.p4535992.util.http.helper.DefaultHttpRequestRetryHandler;
import com.github.p4535992.util.string.StringUtilities;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.ProxySelector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 03/07/2015.
 * @author 4535992.
 * @version 2015-07-03.
 */
@SuppressWarnings("unused")
public class HttpUtilApache4 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(HttpUtilApache4.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    public enum HTTP_METHOD { GET, POST, PUT, DELETE, HEAD }

    public enum HTTP_HEADERS { Accept,}

    /**
     * Method to create a default HTTP Apache Client.
     * @return the HttpClient Apache.
     */
    public static HttpClient createHTTPClient(){
        //Apache HTTP  3
        //HttpClient httpClient = new org.apache.http.impl.client.DefaultHttpClient();
        //Apache HTTP  4
        return org.apache.http.impl.client.HttpClients.createDefault();
    }

    /**
     * Method for make a HTTP POST with Apache HTTP version 3.X.X
     * @param uri the String address Web where make the request.
     * @param contentType the String of the contentType.
     * @param acceptContentType the value of the header.
     * @param entity the HTTP Apache Entity object.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String post(String uri, String contentType,
           String acceptContentType, HttpEntity entity)throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return invokeHTTPRequest(httpPost, contentType, acceptContentType);
    }

    /**
     * Method for make a HTTP POST with Apache HTTP version 3.X.X
     * @param uri the String address Web where make the request.
     * @param contentType the String of the contentType.
     * @param acceptContentType the value of the header.
     * @param formParameters the Map of all parameter to set.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String post(String uri, String contentType,
           String acceptContentType, Map<String, String> formParameters)throws IOException {
        // Prepare the message body parameters
        List<NameValuePair> formParams = new ArrayList<>();
        for (String param:formParameters.keySet()) {
            formParams.add(new BasicNameValuePair(param, formParameters.get(param)));
        }
        return post(uri, contentType, acceptContentType, new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));
    }

    /**
     * Method for make a HTTP POST with Apache HTTP version 3.X.X
     * @param uri the String address Web where make the request.
     * @param contentType the String of the contentType.
     * @param acceptContentType the value of the header.
     * @param rawPostBodyData String of the Http Apche Entity.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String post(String uri, String contentType,
                  String acceptContentType, String rawPostBodyData)throws IOException {
        // Prepare the headers
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(rawPostBodyData));
        return invokeHTTPRequest(httpPost, contentType, acceptContentType);
    }

    /**
     * Method to invoke a HTTP POST request on a page.
     * @param httpPost the HTTP Apache Post.
     * @param contentType the String of the contentType.
     * @param acceptContentType the value of the header.
     * @return the String response of the POST.
     * @throws IOException throw if any error is occurred.
     */
    private static String invokeHTTPRequest(
            HttpPost httpPost, String contentType,String acceptContentType) throws IOException {
        HttpClient httpClient = createHTTPClient();
        if (acceptContentType != null && !acceptContentType.isEmpty()) {
            httpPost.setHeader(HTTP_HEADERS.Accept.name(), acceptContentType);
        }
        if (contentType != null && !contentType.isEmpty()) {
            httpPost.setHeader("Content-Type", contentType);
        }
        // Execute the request
        HttpResponse response = httpClient.execute(httpPost);
        // Parse the response and store it in a String
        HttpEntity entity = response.getEntity();
        StringBuilder responseString = new StringBuilder();
        if (entity != null) {
            BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent(),StandardCharsets.UTF_8));
            String line = buf.readLine();
            while(line != null) {
                responseString.append(line);
                responseString.append('\n');
                line = buf.readLine();
            }
        }
        return responseString.toString();
    }

    /**
     * Method for make a HTTP GET with Apache HTTP version 3.X.X
     * @param uri the String address Web where make the request.
     * @param acceptContentType the value of the header.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String get(String uri, String acceptContentType)
            throws IOException {
        HttpClient httpClient = createHTTPClient();
        HttpGet request = new HttpGet(uri);
        if(acceptContentType != null && !acceptContentType.isEmpty()) {
            request.setHeader(HTTP_HEADERS.Accept.name(), acceptContentType);
        }
        HttpResponse response = httpClient.execute(request);
        // Parse the response and store it in a String
        HttpEntity entity = response.getEntity();
        StringBuilder responseString = new StringBuilder();
        if (entity != null) {
            BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line = buf.readLine();
            while(line != null) {
                responseString.append(line);
                responseString.append('\n');
                line = buf.readLine();
            }
        }
        return responseString.toString();
    }

    /**
     * Method for make a HTTP DELETE with Apache HTTP version 3.X.X
     * @param uri the String address Web where make the request.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String deleteRequest(String uri)throws IOException {
        HttpClient httpClient = createHTTPClient();
        HttpDelete request = new HttpDelete(uri);
        HttpResponse response = httpClient.execute(request);
        // Parse the response and store it in a String
        HttpEntity entity = response.getEntity();
        StringBuilder responseString = new StringBuilder();
        if (entity != null) {
            BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line = buf.readLine();
            while(line != null) {
                responseString.append(line);
                responseString.append('\n');
                line = buf.readLine();
            }
        }
        return responseString.toString();
    }

    /**
     * Method for make a HTTP GET with Apache HTTP version 4.X.X
     * @param url the String address Web where make the request.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String getWithRetry(String url) throws IOException {
        String content ="";
        // HttpClient httpclient;
        org.apache.http.impl.client.CloseableHttpClient httpclient =
                org.apache.http.impl.client.HttpClients.createDefault();
        DefaultHttpRequestRetryHandler def = new DefaultHttpRequestRetryHandler();
        org.apache.http.client.methods.HttpGet httpget = new org.apache.http.client.methods.HttpGet(url);
        //httpget.getParams().setParameter("RETRY_HANDLER", def);
        try {
            //CloseableHttpResponse response = httpclient.execute(httpget);
            org.apache.http.HttpResponse response = httpclient.execute(httpget);
            System.out.println(response);
            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    // do something useful
                    content = StringUtilities.toString(instream);
                }
            }
        } finally {
            httpget.releaseConnection();
        }
        return content;
    }

    /**
     * Method for make a HTTP GET with Apache HTTP version 4.X.X
     * @param url the String address Web where make the request.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String get4(String url) throws IOException{
        String content ="";
        //HttpClient httpclient = new DefaultHttpClient();
        CloseableHttpClient httpclient = org.apache.http.impl.client.HttpClients.createDefault();
        DefaultHttpRequestRetryHandler def = new DefaultHttpRequestRetryHandler();
        HttpGet httpget = new org.apache.http.client.methods.HttpGet(url);
        //httpget.getParams().setParameter("RETRY_HANDLER", def);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            //HttpResponse response = httpclient.execute(httpget);
           logger.info("Response GET:"+response.toString());
            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    // do something useful
                    content = HttpUtilities.streamToString(instream);
                }
            }
        } finally {
            httpget.releaseConnection();
        }
        return content;
    }

    /**
     * Method for make a HTTP GET with Apache HTTP version 4.2.X
     * @param url the String address Web where make the request.
     * @return the content of the page with the url address web.
     * @throws IOException throw if any error is occurred.
     */
    public static String get42(String url) throws IOException {
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
           logger.error(gm()+e.getMessage(),e);
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
            return hcBuilder.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
        }
    }


}
