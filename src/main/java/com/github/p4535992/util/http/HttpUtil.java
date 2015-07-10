package com.github.p4535992.util.http;

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
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class HttpUtil {
	/**
	 * Send a get request.
	 * @param url string of the url resource.
	 * @return response string of the GET method response.
	 * @throws IOException resource not found.
	 */
	static public String get(String url) throws IOException {
		return get(url, null);
	}

	/**
	 * Send a get request.
	 * @param url         Url as string.
	 * @param headers     Optional map with headers.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String get(String url,
			Map<String, String> headers) throws IOException {
		return fetch("GET", url, null, headers);
	}

	/**
	 * Send a post request.
	 * @param url         Url as string.
	 * @param body        Request body as string.
	 * @param headers     Optional map with headers.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String post(String url, String body,
			Map<String, String> headers) throws IOException {
		return fetch("POST", url, body, headers);
	}

	/**
	 * Send a post request.
	 * @param url         Url as string.
	 * @param body        Request body as string.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String post(String url, String body) throws IOException {
		return post(url, body, null);
	}

	/**
	 * Post a form with parameters.
	 * @param url         Url as string.
	 * @param params      map with parameters/values.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String postForm(String url, Map<String, String> params)
			throws IOException {
		return postForm(url, params, null);
	}

	/**
	 * Post a form with parameters.
	 * @param url         Url as string.
	 * @param params      Map with parameters/values.
	 * @param headers     Optional map with headers.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String postForm(String url, Map<String, String> params,
			Map<String, String> headers) throws IOException {
		// set content type
		if (headers == null) {
			headers = new HashMap<>();
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
	 * Send a put request.
	 * @param url         Url as string.
	 * @param body        Request body as string.
	 * @param headers     Optional map with headers.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String put(String url, String body,
			Map<String, String> headers) throws IOException {
		return fetch("PUT", url, body, headers);
	}

	/**
	 * Send a put request.
	 * @param url Url as string.
         * @param body the boy of the html page.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String put(String url, String body) throws IOException {
		return put(url, body, null);
	}
	
	/**
	 * Send a delete request.
	 * @param url         Url as string.
	 * @param headers     Optional map with headers.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String delete(String url,
			Map<String, String> headers) throws IOException {
		return fetch("DELETE", url, null, headers);
	}
	
	/**
	 * Send a delete request.
	 * @param url         Url as string.
	 * @return response   Response as string.
	 * @throws IOException resource not found.
	 */
	static public String delete(String url) throws IOException {
		return delete(url, null);
	}
	
	/**
	 * Append query parameters to given url.
	 * @param url         Url as string.
	 * @param params      Map with query parameters.
	 * @return url        Url with query parameters appended.
	 * @throws IOException resource not found.
	 */
	static public String appendQueryParams(String url,Map<String, String> params) throws IOException {
		StringBuilder fullUrl = new StringBuilder();
		fullUrl.append(url);
		if (params != null) {
			boolean first = (url.indexOf('?') == -1);
			for (String param : params.keySet()) {
				if (first) {
					fullUrl.append('?');
					first = false;
				}
				else {
					fullUrl.append('&');
				}
				String value = params.get(param);
				fullUrl.append(URLEncoder.encode(param, "UTF-8")).append('=');
				fullUrl.append(URLEncoder.encode(value, "UTF-8"));
			}
		}
		return fullUrl.toString();
	}
	
	/**
	 * Retrieve the query parameters from given url.
	 * @param url         Url containing query parameters.
	 * @return params     Map with query parameters.
	 * @throws IOException resource not found.
	 */
	static public Map<String, String> getQueryParams(String url) 
			throws IOException {
		Map<String, String> params = new HashMap<>();
		int start = url.indexOf('?');
		while (start != -1) {
			// read parameter name
			int equals = url.indexOf('=', start);
			String param;
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
	 * Returns the url without query parameters.
	 * @param url         Url containing query parameters.
	 * @return url        Url without query parameters.
	 * @throws IOException resource not found.
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
	 * Send a request.
	 * @param method      HTTP method, for example "GET" or "POST".
	 * @param url         Url as string.
	 * @param body        Request body as string.
	 * @param headers     Optional map with headers.
	 * @return response   Response as string
	 * @throws IOException resource not found.
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
	 * Read an input stream into a string.
	 * @param in stream of the resource.
	 * @return string of the content of the resource.
	 * @throws IOException resource not found.
	 */
	static public String streamToString(InputStream in) throws IOException {
		StringBuilder out = new StringBuilder();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
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

        
       
    public static String getAuthorityName(String url) throws URISyntaxException{
		String provider = new URI(url).getHost();
		StringTokenizer split2 =  new StringTokenizer(provider,".");
		provider = split2.nextToken().toUpperCase();
		return provider;
    }


}
