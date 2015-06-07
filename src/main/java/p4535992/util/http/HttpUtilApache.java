/**
 * @file HttpUtil.java
 * 
 * @brief 
 * HttpUtil is a single class containing methods to conveniently perform HTTP 
 * requests. HttpUtil only uses regular java io and net functionality and does 
 * not depend on external libraries. 
 * The class contains methods to perform a get, post, put, and delete request,
 * and supports posting forms. Optionally, one can provide headers.
 *
 * Example usage:
 * 
 *     // get
 *     String res = HttpUtil.get("http://www.google.com");
 * 
 *     // post
 *     String res = HttpUtil.post("http://sendmedata.com", "This is the data");
 *
 *     // post form
 *     Map<String, String> params = new HashMap<String, String>();
 *     params.put("firstname", "Joe");
 *     params.put("lastname", "Smith");
 *     params.put("age", "28");
 *     String res = HttpUtil.postForm("http://site.com/newuser", params);
 *
 *     // append query parameters to url
 *     String url = "http://mydatabase.com/users";
 *     Map<String, String> params = new HashMap<String, String>();
 *     params.put("orderby", "name");
 *     params.put("limit", "10");
 *     String fullUrl = HttpUtil.appendQueryParams(url, params);
 *     // fullUrl = "http://mydatabase.com/user?orderby=name&limit=10"
 *
 * @license
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright (c) 2012 Almende B.V.
 *
 * @author 	Jos de Jong, <jos@almende.org>
 * @date	  2012-05-14
 */

package p4535992.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.*;
import java.nio.charset.Charset;

import java.util.HashMap;

import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

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
import p4535992.util.log.SystemLog;


public class HttpUtilApache {
	/**
	 * Send a get request
	 * @param url
	 * @return response
	 * @throws IOException 
	 */
	static public String get(String url) throws IOException {
		return get(url, null);
	}

	/**
	 * Send a get request
	 * @param url         Url as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String get(String url,
			Map<String, String> headers) throws IOException {
		return fetch("GET", url, null, headers);
	}

	/**
	 * Send a post request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String post(String url, String body,
			Map<String, String> headers) throws IOException {
		return fetch("POST", url, body, headers);
	}

	/**
	 * Send a post request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String post(String url, String body) throws IOException {
		return post(url, body, null);
	}

	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      map with parameters/values
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String postForm(String url, Map<String, String> params) 
			throws IOException {
		return postForm(url, params, null);
	}

	/**
	 * Post a form with parameters
	 * @param url         Url as string
	 * @param params      Map with parameters/values
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String postForm(String url, Map<String, String> params,
			Map<String, String> headers) throws IOException {
		// set content type
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put("Content-Type", "application/x-www-form-urlencoded");

		// parse parameters
		String body = "";
		if (params != null) {
			boolean first = true;
			for (String param : params.keySet()) {
				if (first) {
					first = false;
				}
				else {
					body += "&";
				}
				String value = params.get(param);
				body += URLEncoder.encode(param, "UTF-8") + "=";
				body += URLEncoder.encode(value, "UTF-8");
			}
		}

		return post(url, body, headers);
	}

	/**
	 * Send a put request
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String put(String url, String body,
			Map<String, String> headers) throws IOException {
		return fetch("PUT", url, body, headers);
	}

	/**
	 * Send a put request
	 * @param url         Url as string
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String put(String url, String body) throws IOException {
		return put(url, body, null);
	}
	
	/**
	 * Send a delete request
	 * @param url         Url as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String delete(String url,
			Map<String, String> headers) throws IOException {
		return fetch("DELETE", url, null, headers);
	}
	
	/**
	 * Send a delete request
	 * @param url         Url as string
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String delete(String url) throws IOException {
		return delete(url, null);
	}
	
	/**
	 * Append query parameters to given url
	 * @param url         Url as string
	 * @param params      Map with query parameters
	 * @return url        Url with query parameters appended
	 * @throws IOException 
	 */
	static public String appendQueryParams(String url, 
			Map<String, String> params) throws IOException {
		String fullUrl = new String(url);
		
		if (params != null) {
			boolean first = (fullUrl.indexOf('?') == -1);
			for (String param : params.keySet()) {
				if (first) {
					fullUrl += '?';
					first = false;
				}
				else {
					fullUrl += '&';
				}
				String value = params.get(param);
				fullUrl += URLEncoder.encode(param, "UTF-8") + '=';
				fullUrl += URLEncoder.encode(value, "UTF-8");
			}
		}
		
		return fullUrl;
	}
	
	/**
	 * Retrieve the query parameters from given url
	 * @param url         Url containing query parameters
	 * @return params     Map with query parameters
	 * @throws IOException 
	 */
	static public Map<String, String> getQueryParams(String url) 
			throws IOException {
		Map<String, String> params = new HashMap<String, String>();
	
		int start = url.indexOf('?');
		while (start != -1) {
			// read parameter name
			int equals = url.indexOf('=', start);
			String param = "";
			if (equals != -1) {
				param = url.substring(start + 1, equals);
			}
			else {
				param = url.substring(start + 1);
			}
			
			// read parameter value
			String value = "";
			if (equals != -1) {
				start = url.indexOf('&', equals);
				if (start != -1) {
					value = url.substring(equals + 1, start);
				}
				else {
					value = url.substring(equals + 1);
				}
			}
			
			params.put(URLDecoder.decode(param, "UTF-8"), 
				URLDecoder.decode(value, "UTF-8"));
		}
		
		return params;
	}

	/**
	 * Returns the url without query parameters
	 * @param url         Url containing query parameters
	 * @return url        Url without query parameters
	 * @throws IOException 
	 */
	static public String removeQueryParams(String url) 
			throws IOException {
		int q = url.indexOf('?');
		if (q != -1) {
			return url.substring(0, q);
		}
		else {
			return url;
		}
	}
	
	/**
	 * Send a request
	 * @param method      HTTP method, for example "GET" or "POST"
	 * @param url         Url as string
	 * @param body        Request body as string
	 * @param headers     Optional map with headers
	 * @return response   Response as string
	 * @throws IOException 
	 */
	static public String fetch(String method, String url, String body,
			Map<String, String> headers) throws IOException {
		// connection
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
		conn.setConnectTimeout(40000);
		conn.setReadTimeout(40000);
               
		// method
		if (method != null) {
			conn.setRequestMethod(method);
		}

		// headers
		if (headers != null) {
			for(String key : headers.keySet()) {
				conn.addRequestProperty(key, headers.get(key));
			}
		}

		// body
		if (body != null) {
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			os.close();
		}
		
		// response
		InputStream is = conn.getInputStream();
		String response = streamToString(is);
		is.close();
		
		// handle redirects
		if (conn.getResponseCode() == 301) {
			String location = conn.getHeaderField("Location");
			return fetch(method, location, body, headers);
		}
		
		return response;
	}
	
	/**
	 * Read an input stream into a string
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static public String streamToString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
        
       
        
        
        public static String GETWithRetry(String URL) throws IOException{
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
                    InputStream instream = entity.getContent();
                    try {
                        // do something useful
                        content = HttpUtilApache.streamToString(instream);
                    } finally {
                        instream.close();                     
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
                InputStream instream = entity.getContent();
                try {
                    // do something useful
                    content = HttpUtilApache.streamToString(instream);
                } finally {
                    instream.close();
                }
            }
        } finally {
            httpget.releaseConnection();
        }
        return content;
    }

    public static String GET42(String url) throws IOException {
        String content="";
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
        try {
            // Execute the method.
            HttpResponse response = httpClient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Method failed: " + response.getStatusLine());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            StringBuffer buf = new StringBuffer();
            String output;
            while ((output = br.readLine()) != null) {
                buf.append(output);
            }
            content = buf.toString();
        } catch (Exception e) {
            SystemLog.exception(e);
        } finally {
            // Release the connection.
            httpClient.close();
        }
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
        * Metodo che stoppa il conteggio del temporizzatore
        * @param millisec in millisecondi fornito di input
        */
        public static void stopExecution(long millisec){
            long t0,t1;
            t0 = System.currentTimeMillis();
            do{
                t1=System.currentTimeMillis();
            }
            while (t1-t0<millisec);
        }
        
   
        /*
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://targethost/homepage");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager. 
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }

        HttpPost httpPost = new HttpPost("http://targethost/login");
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }
        */
        
        /*
        // The fluent API relieves the user from having to deal with manual deallocation of system
        // resources at the cost of having to buffer response content in memory in some cases.

        Request.Get("http://targethost/homepage")
            .execute().returnContent();
        Request.Post("http://targethost/login")
            .bodyForm(Form.form().add("username",  "vip").add("password",  "secret").build())
            .execute().returnContent();
        */
        
        /**
        * Metodo che permette di leggere il contenuto della risposta Json.
        * @param rd identifica il buffer eader in cui immagazziniamo la risposta Json
        * @return restituisce la risposta Json appena letta come una stringa.
        * @throws IOException 
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
        * Metodo che legge la risposta Json restituita da un URL fornito di input.
        * @param url lt'indirizzo org.p4535992.mvc.webapp di input
        * @return ritorna la risposta Json restituita dall url
        * @throws IOException
        * @throws JSONException 
        */
       private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
         InputStream is = new URL(url).openStream();    
         try {
           BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
           String jsonText = readAll(rd);
           JSONObject json = new JSONObject(jsonText);
           return json;
         } finally {
           is.close();
         }
       }
       
        /**
        * Semplice metodo che estare il domino org.p4535992.mvc.webapp di appartenenza dell'url analizzato
        * @param url url di ingresso in fromato stringa
        * @return il dominio org.p4535992.mvc.webapp dell'url in formato stringa
        * @throws URISyntaxException 
        */
       public static String getDomainName(String url) {

           String domain = "";
           try {
               URI uri = new URI(url);
				   domain = uri.getHost();
				   //return domain.startsWith("www.") ? domain.substring(4) : domain;
			   }catch(URISyntaxException ue){
				   String[] ss = url.split("/");
				   domain = ss[0]+"/";
			   }
			   return domain;
       }//getDomainName
        
       
       public static String getAuthorityName(String url) throws URISyntaxException{
            URI uri = new URI(url);
            String provider = uri.getHost().toString(); 
            StringTokenizer split2 =  new StringTokenizer(provider,".");
            provider = split2.nextToken();
            provider = split2.nextToken().toUpperCase();
            return provider;
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
            CloseableHttpClient httpClient = hcBuilder.build();
            return httpClient;
        }else {
            CloseableHttpClient client = hcBuilder.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                    .build();
            //or with SystemDefault
            //CloseableHttpClient client2 = HttpClients.createSystem();
            return client;
        }
	}
}
