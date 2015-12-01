package com.github.p4535992.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 09/07/2015.
 * @author 4535992.
 * @version 2015-07-09.
 */
@SuppressWarnings("unused")
public class HttpUtilApache3 {

    public enum HTTP_METHOD {
        GET, POST, PUT, DELETE, HEAD
    }

    public enum HTTP_HEADERS {
        Accept,
    }

    public static String executeHTTPPostRequest(String serviceURL, String contentType,
                                                String acceptContentType, HttpEntity entity)
            throws IOException {
        HttpPost httpPost = new HttpPost(serviceURL);
        httpPost.setEntity(entity);
        return invokeHTTPRequest(httpPost, contentType, acceptContentType);
    }

    public static String executeHTTPPostRequest(String serviceURL, String contentType,
                                                String acceptContentType, Map<String, String> formParameters)
            throws IOException {

        // Prepare the message body parameters
        List<NameValuePair> formParams = new ArrayList<>();
        for (String param:formParameters.keySet()) {
            formParams.add(new BasicNameValuePair(param, formParameters.get(param)));
        }

        return executeHTTPPostRequest(serviceURL, contentType, acceptContentType, new UrlEncodedFormEntity(formParams, "UTF-8"));
    }

    public static String executeHTTPPostRequest(String serviceURL, String contentType,
                                                String acceptContentType, String rawPostBodyData)
            throws IOException {

        // Prepare the headers
        HttpPost httpPost = new HttpPost(serviceURL);
        httpPost.setEntity(new StringEntity(rawPostBodyData));
        return invokeHTTPRequest(httpPost, contentType, acceptContentType);
    }

    @SuppressWarnings("deprecation")
    private static String invokeHTTPRequest(HttpPost httpPost, String contentType,
                                            String acceptContentType) throws IOException {
        HttpClient httpClient = new org.apache.http.impl.client.DefaultHttpClient();

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
            BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));

            String line = buf.readLine();
            while(line != null) {
                responseString.append(line);
                responseString.append('\n');
                line = buf.readLine();
            }
        }
        return responseString.toString();
    }

    @SuppressWarnings("deprecation")
    public static String executeHTTPGetRequest(String uri, String acceptContentType)
            throws IOException {
        HttpClient httpClient = new org.apache.http.impl.client.DefaultHttpClient();

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

    @SuppressWarnings("deprecation")
    public static String executeHTTPDeleteRequest(String uri)
            throws IOException {
        HttpClient httpClient = new org.apache.http.impl.client.DefaultHttpClient();

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
}
