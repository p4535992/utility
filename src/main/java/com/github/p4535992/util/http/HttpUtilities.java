package com.github.p4535992.util.http;

import com.github.p4535992.util.regex.pattern.Patterns;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 08/12/2015.
 * @author 4535992.
 * @version 2015-12-17.
 */
@SuppressWarnings("unused")
public class HttpUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(HttpUtilities.class);
    
    public enum HTTP_METHOD {
        GET, POST, PUT, DELETE, HEAD
    }

    public enum HTTP_HEADERS {
        Accept,
    }

    private static final Pattern IP_PATTERN = Patterns.IP_ADDRESS;

    private static String boundary;
    private static HttpURLConnection httpConn;
    private static final String LINE_FEED = "\r\n";
    ///private String charset;
    //private OutputStream outputStream;
    //private PrintWriter writer;

    private static HttpUtilities instance = null;

    protected HttpUtilities(){}

    public static HttpUtilities getInstance(){
        if(instance == null) {
            instance = new HttpUtilities();
        }
        return instance;
    }

    //----------------------------------------------------------------------------------------------------------------
    // Create
    //----------------------------------------------------------------------------------------------------------------

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

    //----------------------------------------------------------------------------------------------------------------
    // Execute Method HTTP
    //----------------------------------------------------------------------------------------------------------------

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

    public static String invokeHTTPRequest(HttpPost httpPost, String contentType,
                                            String acceptContentType) throws IOException {
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

    public static String executeHTTPGetRequest(String uri, String acceptContentType)
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

    public static String executeHTTPDeleteRequest(String uri)
            throws IOException {
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

    //---------------------------------------------------------------------------------------------------------
    //OLD METHOD
    //---------------------------------------------------------------------------------------------------------

    /**
     * Resolve relative URL-s and fix a java.net.URL org.p4535992.mvc.error in handling of URLs
     * with pure query targets.
     * @param base base url.
     * @param target target url (may be relative).
     * @return resolved absolute url.
     * @throws MalformedURLException thrown if any MalformedURL error occurred.
     */
    public static URL resolveURL(URL base, String target) throws MalformedURLException {
        target = target.trim();
        if (target.startsWith("?")) return fixPureQueryTargets(base, target);
        return new URL(base, target);
    }

    /** Handle the case in RFC3986 section 5.4.1 example 7, and similar.
     * @param base url of the site web.
     * @param target string of the reosurce on the site web.
     * @return url to the reosurce web.
     */
    static URL fixPureQueryTargets(URL base, String target)throws MalformedURLException {
        if (!target.startsWith("?")) return new URL(base, target);
        String basePath = base.getPath();
        String baseRightMost = "";
        int baseRightMostIdx = basePath.lastIndexOf("/");
        if (baseRightMostIdx != -1) {
            baseRightMost = basePath.substring(baseRightMostIdx + 1);
        }
        if (target.startsWith("?")) target = baseRightMost + target;
        return new URL(base, target);
    }


    /**
     * Returns the domain name of the url. The domain name of a url is the
     * substring of the url's hostname, w/o subdomain names. As an example <br>
     * <code>
     *  getDomainName(conf, new URL(http://lucene.apache.org/))
     *  </code><br>
     * will return <br>
     * <code> apache.org</code>
     * @param url url to the resource web.
     * @return domain name of the resource web.
     * */

    public static String getDomainName(URL url) {
        String host = url.getHost();
        // it seems that java returns hostnames ending with .
        if (host.endsWith(".")) host = host.substring(0, host.length() - 1);
        if (IP_PATTERN.matcher(host).matches()) return host;

        try {
            return new URI(url.toString()).getHost();
        } catch (URISyntaxException e) {
            int index = 0;
            String candidate = host;
            for (; index >= 0;) {
                index = candidate.indexOf('.');
                String subCandidate = candidate.substring(index + 1);
                if (url.getHost().equalsIgnoreCase(subCandidate))return candidate;
                candidate = subCandidate;
            }
            return candidate;
        }
    }

    /**
     * Returns the domain name of the url. The domain name of a url is the
     * substring of the url's hostname, w/o subdomain names. As an example <br>
     * <code>
     *  getDomainName(conf, new http://lucene.apache.org/)
     *  </code><br>
     * will return <br>
     * <code> apache.org</code>
     * @param url url to the resource web.
     * @return domain name of the resource web.
     * @throws MalformedURLException throw if any MalformedURL error is occured.
     */
    public static String getDomainName(String url) throws MalformedURLException {
        return getDomainName(new URL(url));}

    /**
     * Returns the top level domain name of the url. The top level domain name of
     * a url is the substring of the url's hostname, w/o subdomain names. As an
     * example <br>
     * <code>
     *  getTopLevelDomainName(conf, new http://lucene.apache.org/)
     *  </code><br>
     * will return <br>
     * <code> org</code>
     * @param url url to the resource web.
     * @return domain name of the resource web.
     * @throws MalformedURLException throw if any MalformedURL error is occured.
     */
    public static String getTopLevelDomainName(URL url)throws MalformedURLException {
        //String suffix = getDomainSuffix(url).toString();
        String suffix = url.getAuthority();//??????
        int idx = suffix.lastIndexOf(".");
        if (idx != -1) {
            return suffix.substring(idx + 1);
        } else {
            return suffix;
        }
    }

    /**
     * Returns the top level domain name of the url. The top level domain name of
     * a url is the substring of the url's hostname, w/o subdomain names. As an
     * example <br>
     * <code>
     *  getTopLevelDomainName(conf, new http://lucene.apache.org/)
     *  </code><br>
     * will return <br>
     * <code> org</code>
     * @param url url to the resource web.
     * @return domain name of the resource web.
     * @throws MalformedURLException throw if any MalformedURL error is occured.
     */
    public static String getTopLevelDomainName(String url)throws MalformedURLException {
        return getTopLevelDomainName(new URL(url));
    }

    /**
     * Returns whether the given urls have the same domain name. As an example, <br>
     * <code> isSameDomain(new URL("http://lucene.apache.org")
     * , new URL("http://people.apache.org/"))
     * <br> will return true. </code>
     * @param url1 url to the first resource web.
     * @param url2 url to the second resource web.
     * @return true if the domain names are equal.
     */
    public static boolean isSameDomainName(URL url1, URL url2) {
        return getDomainName(url1).equalsIgnoreCase(getDomainName(url2));
    }

    public static boolean isSameDomainName(String url1, String url2)throws MalformedURLException {
        return isSameDomainName(new URL(url1), new URL(url2));
    }

  /*
   * Returns the {@link DomainSuffix} corresponding to the last public part of
   * the hostname
   */
  /*
  public static DomainSuffix getDomainSuffix(URL url) {
    DomainSuffixes tlds = DomainSuffixes.getInstance();
    String host = url.getHost();
    if (IP_PATTERN.matcher(host).matches())
      return null;

    int index = 0;
    String candidate = host;
    for (; index >= 0;) {
      index = candidate.indexOf('.');
      String subCandidate = candidate.substring(index + 1);
      DomainSuffix d = tlds.get(subCandidate);
      if (d != null) {
        return d;
      }
      candidate = subCandidate;
    }
    return null;
  }
*/
  /*
   * Returns the {@link DomainSuffix} corresponding to the last public part of
   * the hostname
   */
  /*
  public static DomainSuffix getDomainSuffix(String url)
      throws MalformedURLException {
    return getDomainSuffix(new URL(url));
  }
 */
    /** Partitions of the hostname of the url by "." .
     * @param url url of the resource web.
     * @return array of string with the full host segment to the resource web.
     */
    public static String[] getHostSegments(URL url) {
        String host = url.getHost();
        // return whole hostname, if it is an ipv4
        // TODO : handle ipv6
        if (IP_PATTERN.matcher(host).matches())
            return new String[] { host };
        return host.split("\\.");
    }

    /**
     * Partitions of the hostname of the url by ".".
     * @param url string url of input.
     * @throws MalformedURLException throw if any MalformedURL error is occured.
     * @return array of string with the stack of the full uri host segment.
     */
    public static String[] getHostSegments(String url) throws MalformedURLException {
        return getHostSegments(new URL(url));
    }

    /**
     * Method for get the representative url of the resource web.
     * href: http://help.yahoo.com/l/nz/yahooxtra/search/webcrawler/slurp-11.html.
     * @param src The source url.
     * @param dst The destination url.
     * @param temp Is the redirect a temporary redirect.
     * @return String The representative url.
     */
    public static String chooseRepresentativeUrl(String src, String dst, boolean temp) {
        // validate both are well formed urls
        URL srcUrl;
        URL dstUrl;
        try {
            srcUrl = new URL(src);
            dstUrl = new URL(dst);
        } catch (MalformedURLException e) {
            return dst;
        }
        // get the source and destination domain, host, and page
        String srcDomain = getDomainName(srcUrl);
        String dstDomain = getDomainName(dstUrl);
        String srcHost = srcUrl.getHost();
        String dstHost = dstUrl.getHost();
        String srcFile = srcUrl.getFile();
        String dstFile = dstUrl.getFile();

        // are the source and destination the root path url.com/ or url.com
        boolean srcRoot = (srcFile.equals("/") || srcFile.length() == 0);
        boolean destRoot = (dstFile.equals("/") || dstFile.length() == 0);

        // 1) different domain them keep dest, temp or perm
        // a.com -> b.com*
        //
        // 2) permanent and root, keep src
        // *a.com -> a.com?y=1 || *a.com -> a.com/xyz/index.html
        //
        // 3) permanent and not root and dest root, keep dest
        // a.com/xyz/index.html -> a.com*
        //
        // 4) permanent and neither root keep dest
        // a.com/xyz/index.html -> a.com/abc/page.html*
        //
        // 5) temp and root and dest not root keep src
        // *a.com -> a.com/xyz/index.html
        //
        // 7) temp and not root and dest root keep dest
        // a.com/xyz/index.html -> a.com*
        //
        // 8) temp and neither root, keep shortest, if hosts equal by path else by
        // hosts. paths are first by length then by number of / separators
        // a.com/xyz/index.html -> a.com/abc/page.html*
        // *www.a.com/xyz/index.html -> www.news.a.com/xyz/index.html
        //
        // 9) temp and both root keep shortest sub domain
        // *www.a.com -> www.news.a.com

        // if we are dealing with a redirect from one domain to another keep the
        // destination
        if (!srcDomain.equals(dstDomain)) {
            return dst;
        }

        // if it is a permanent redirect
        if (!temp) {

            // if source is root return source, otherwise destination
            if (srcRoot) {
                return src;
            } else {
                return dst;
            }
        } else { // temporary redirect

            // source root and destination not root
            if (srcRoot && !destRoot) {
                return src;
            } else if (!srcRoot && destRoot) { // destination root and source not
                return dst;
            } else if (!srcRoot && (srcHost.equals(dstHost))) {

                // source and destination hosts are the same, check paths, host length
                int numSrcPaths = srcFile.split("/").length;
                int numDstPaths = dstFile.split("/").length;
                if (numSrcPaths != numDstPaths) {
                    return (numDstPaths < numSrcPaths ? dst : src);
                } else {
                    int srcPathLength = srcFile.length();
                    int dstPathLength = dstFile.length();
                    return (dstPathLength < srcPathLength ? dst : src);
                }
            } else {

                // different host names and both root take the shortest
                int numSrcSubs = srcHost.split("\\.").length;
                int numDstSubs = dstHost.split("\\.").length;
                return (numDstSubs < numSrcSubs ? dst : src);
            }
        }
    }

    /**
     * Returns the lowercased hostname for the url or null if the url is not well formed.
     * @param url The url to check.
     * @return String The hostname for the url.
     */
    public static String getHost(String url) {
        try {
            return new URL(url).getHost().toLowerCase();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Returns the page for the url. The page consists of the protocol, host, and
     * path, but does not include the query string. The host is lowercased but the
     * path is not.
     * @param url The url to check.
     * @return String The page for the url.
     */
    public static String getPage(String url) {
        try {
            // get the full url, and replace the query string with and empty string
            url = url.toLowerCase();
            String queryStr = new URL(url).getQuery();
            return (queryStr != null) ? url.replace("?" + queryStr, "") : url;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getProtocol(String url) {
        try {
            return getProtocol(new URL(url));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getProtocol(URL url) {
        return url.getProtocol();
    }

    public static String toASCII(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            if (host == null || host.isEmpty()) {
                // no host name => no punycoded domain name
                // also do not add additional slashes for file: URLs (NUTCH-1880)
                return url;
            }
            URI p = new URI(u.getProtocol(), u.getUserInfo(), IDN.toASCII(host),
                    u.getPort(), u.getPath(), u.getQuery(), u.getRef());

            return p.toString();
        } catch (MalformedURLException | URISyntaxException | NullPointerException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static String toUNICODE(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            if (host == null || host.isEmpty()) {
                // no host name => no punycoded domain name
                // also do not add additional slashes for file: URLs (NUTCH-1880)
                return url;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(u.getProtocol());
            sb.append("://");
            if (u.getUserInfo() != null) {
                sb.append(u.getUserInfo());
                sb.append('@');
            }
            sb.append(IDN.toUnicode(host));
            if (u.getPort() != -1) {
                sb.append(':');
                sb.append(u.getPort());
            }
            sb.append(u.getFile()); // includes query
            if (u.getRef() != null) {
                sb.append('#');
                sb.append(u.getRef());
            }

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    //-------------------------------------------------------------------------------------------------------------
    /**
     * Send a get request.
     * @param url string of the url resource.
     * @return response string of the GET method response.
     * @throws IOException resource not found.
     */
    public static String executeHTTPGetRequest(String url) throws IOException {
        return executeHTTPGetRequest(url,new HashMap<String,String>());
    }

    /**
     * Send a get request.
     * @param url         Url as string.
     * @param headers     Optional map with headers.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPGetRequest(String url,
                             Map<String, String> headers) throws IOException {
        return invokeHTTPRequest(HTTP_METHOD.GET.name(), url, null, headers);
    }

    /**
     * Send a post request.
     * @param url         Url as string.
     * @param body        Request body as string.
     * @param headers     Optional map with headers.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPPostRequest(String url, String body,
                              Map<String, String> headers) throws IOException {
        return invokeHTTPRequest(HTTP_METHOD.POST.name(), url, body, headers);
    }

    /**
     * Send a post request.
     * @param url         Url as string.
     * @param body        Request body as string.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPPostRequest(String url, String body) throws IOException {
        return executeHTTPPostRequest(url, body, null);
    }

    /**
     * Post a form with parameters.
     * @param url         Url as string.
     * @param params      map with parameters/values.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPPostRequest(String url, Map<String, String> params)
            throws IOException {
        return executeHTTPPostRequest(url, params, null);
    }

    /**
     * Post a form with parameters.
     * @param url         Url as string.
     * @param params      Map with parameters/values.
     * @param headers     Optional map with headers.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPPostRequest(String url, Map<String, String> params,
                                  Map<String, String> headers) throws IOException {
        // set content type
        if (headers == null) headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        // parse parameters
        String body = "";
        if (params != null) {
            boolean first = true;
            for (String param : params.keySet()) {
                if (first) first = false;
                else body += "&";
                String value = params.get(param);
                body += URLEncoder.encode(param, "UTF-8") + "=";
                body += URLEncoder.encode(value, "UTF-8");
            }
        }
        return executeHTTPPostRequest(url, body, headers);
    }

    /**
     * Send a put request.
     * @param url         Url as string.
     * @param body        Request body as string.
     * @param headers     Optional map with headers.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPPutRequest(String url, String body,
                             Map<String, String> headers) throws IOException {
        return invokeHTTPRequest(HTTP_METHOD.PUT.name(), url, body, headers);
    }

    /**
     * Send a put request.
     * @param url Url as string.
     * @param body the boy of the html page.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPPutRequest(String url, String body) throws IOException {
        return executeHTTPPutRequest(url, body, null);
    }

    /**
     * Send a delete request.
     * @param url         Url as string.
     * @param headers     Optional map with headers.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPDeleteRequest(String url,
                                Map<String, String> headers) throws IOException {
        return invokeHTTPRequest(HTTP_METHOD.DELETE.name(), url, null, headers);
    }

    /**
     * Send a delete request.
     * @param url         Url as string.
     * @return response   Response as string.
     * @throws IOException resource not found.
     */
    public static String executeHTTPDeleteRequest(URL url) throws IOException {
        return executeHTTPDeleteRequest(url.toString(), null);
    }

    /**
     * Append query parameters to given url.
     * @param url         Url as string.
     * @param params      Map with query parameters.
     * @return url        Url with query parameters appended.
     * @throws IOException resource not found.
     */
    public static String appendQueryParams(String url,Map<String, String> params) throws IOException {
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
    public static Map<String, String> getQueryParams(String url)
            throws IOException {
        Map<String, String> params = new HashMap<>();
        int start = url.indexOf('?');
        while (start != -1) {
            // read parameter name
            int equals = url.indexOf('=', start);
            String param;
            if (equals != -1) param = url.substring(start + 1, equals);
            else param = url.substring(start + 1);
            // read parameter value
            String value = "";
            if (equals != -1) {
                start = url.indexOf('&', equals);
                if (start != -1) value = url.substring(equals + 1, start);
                else value = url.substring(equals + 1);
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
    public static String removeQueryParams(String url) throws IOException {
        int q = url.indexOf('?');
        if (q != -1) return url.substring(0, q);
        else return url;
    }

    /**
     * Send a request.
     * OLD_NAME: fetch
     * @param method      HTTP method, for example "GET" or "POST".
     * @param url         Url as string.
     * @param body        Request body as string.
     * @param headers     Optional map with headers.
     * @return response   Response as string
     * @throws IOException resource not found.
     */
    public static String invokeHTTPRequest(String method, String url, String body,
                               Map<String, String> headers) throws IOException {
        // connection
        URL u = new URL(url);
        httpConn = (HttpURLConnection)u.openConnection();
        httpConn.setConnectTimeout(40000);
        httpConn.setReadTimeout(40000);
        // method
        if (method != null) httpConn.setRequestMethod(method);
        // headers
        if (headers != null) {
            for(String key : headers.keySet()) {
                httpConn.addRequestProperty(key, headers.get(key));
            }
        }
        // body
        if (body != null) {
            httpConn.setDoOutput(true);
            try (OutputStream os = httpConn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }
        }
        // response
        String response;
        try (InputStream is = httpConn.getInputStream()) {
            response = streamToString(is);
        }
        // handle redirects
        if (httpConn.getResponseCode() == 301) {
            String location = httpConn.getHeaderField("Location");
            return invokeHTTPRequest(method, location, body, headers);
        }
        return response;
    }

    /**
     * Read an input stream into a string.
     * @param in stream of the resource.
     * @return string of the content of the resource.
     * @throws IOException resource not found.
     */
    public static String streamToString(InputStream in) throws IOException {
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
        do{t1=System.currentTimeMillis();}
        while (t1-t0<millisec);
    }

    /**
     * Method for read a resource allocated on a Reader object.
     * @param rd buffer reader where the resource is allocated.
     * @return return string of the response.
     * @throws IOException resource not found.
     */
    private static String readAll(Reader rd) throws IOException {
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
     * @return json response from the url resource.
     * @throws IOException reosurce not found.
     * @throws JSONException json object error.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    /**
     * Method to get the authority name of a web page.
     * @param url the web adrress to the web page.
     * @return the string name of the authority of the web page.
     * @throws URISyntaxException throw exception if the web page is not reachable.
     */
    public static String getAuthorityName(String url) throws URISyntaxException{
        String provider = new URI(url).getHost();
        StringTokenizer split2 =  new StringTokenizer(provider,".");
        provider = split2.nextToken().toUpperCase();
        return provider;
    }

    //---------------------------------------------------------------------------------------

    /**
     * Makes an HTTP request using GET method to the specified URL.
     * @param requestURL the URL of the remote server.
     * @return An HttpURLConnection object.
     */
    public static HttpURLConnection executeHTTPGetRequestWithReturn(String requestURL){
        try {
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true); // true if we want to read server's response
            httpConn.setDoOutput(false); // false indicates this is a GET request
            return httpConn;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Makes an HTTP request using POST method to the specified URL.
     * @param requestURL the URL of the remote server.
     * @param params A map containing POST data in form of key-value pairs.
     * @return An HttpURLConnection object.
     */
    public static HttpURLConnection executeHTTPPostRequestWithReturn(
            String requestURL,Map<String, String> params){
        try {
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true); // true indicates the server returns response
            StringBuilder requestParams = new StringBuilder();
            if (params != null && params.size() > 0) {
                httpConn.setDoOutput(true); // true indicates POST request
                // creates the params string, encode them using URLEncoder
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
                    requestParams.append("&");
                }
                // sends POST data
                OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
                writer.write(requestParams.toString());
                writer.flush();
            }
            return httpConn;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Returns only one line from the server's response.
     * This method should be used if the server returns only a single line of St
     * @param inputStream the InputStream response to Read.
     * @return a String of the server's response.
     */
    public static String readSingleLineRespone(InputStream inputStream) {
        try {
            if (httpConn != null) inputStream = httpConn.getInputStream();
            if(inputStream == null) throw new IOException("Connection is not established.");
            String response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                response = reader.readLine();
            }
            return response;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Returns an array of lines from the server's response. This method should
     * be used if the server returns multiple lines of String.
     * @param inputStream the InputStream response to Read.
     * @return an array of Strings of the server's response.
     */
    public static String[] readMultipleLinesRespone(InputStream inputStream){
        try {
            if (httpConn != null)inputStream = httpConn.getInputStream();
            if(inputStream==null) throw new IOException("Connection is not established.");
            List<String> response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                response = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
            }
            return response.toArray(new String[response.size()]);
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Closes the connection if opened.
     */
    public static void disconnect() {
        if (httpConn != null) httpConn.disconnect();
    }

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data.
     * @param requestURL url for the request.
     * @return the OutputStream of the Response to the Request.
     */
    public static OutputStream MultipartUtility(String requestURL){
        try {
            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);	// indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            httpConn.setRequestProperty("Test", "Bonjour");
            return httpConn.getOutputStream();
            //return new PrintWriter(new OutputStreamWriter(outputStream, charset),true);
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Adds a form field to the request.
     * @param name field name.
     * @param value field value.
     * @param charset the Charset of the Writer.
     * @param writer the Writer to use for append the form.
     * @return if true all the operation are done.
     */
    public static boolean addFormField(String name, String value,String charset, Writer writer){
        try {
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
            return true;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Adds a upload file section to the request.
     * @param fieldName name attribute in input type="file" name="..." /.
     * @param uploadFile a File to be uploaded.
     * @param outputStream the OutputStream to write to the File.
     * @param writer the Writer to use for append the form.
     * @return if true all the operation are done.
     */
    public static boolean addFilePart(
            String fieldName, File uploadFile,
            OutputStream outputStream,Writer writer) {
        try {
            String fileName = uploadFile.getName();
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                    .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            
            try (FileInputStream inputStream = new FileInputStream(uploadFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            
            writer.append(LINE_FEED);
            writer.flush();
            return true;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field.
     * @param value - value of the header field.
     * @param writer the Writer to use for append the form.
     * @return if true all the operation are done. 
     */
    public static boolean addHeaderField(String name, String value,Writer writer){
        try {
            writer.append(name).append(": ").append(value).append(LINE_FEED);
            writer.flush();
            return true;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Completes the request and receives response from the server.
     * @param writer the Writer to for populate the List of String.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     */
    public static List<String> finish(Writer writer) {
        try {
            List<String> response = new ArrayList<>();
            writer.append(LINE_FEED).flush();
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
            writer.close();
            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.add(line);
                    }
                }
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }
            return response;
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    private static int tentativi = 0;
    public static boolean isWebPageExists(String url){
        Document doc;
        try {
            try {
                if (tentativi < 3) {
                    //doc = Jsoup.connect(url).get();
                    //doc = Jsoup.parse(Jsoup.connect(url).ignoreContentType(true).execute().contentType());
                    doc = Jsoup.connect(url).timeout(10 * 1000).get();
                    tentativi = 0;
                    return doc != null;
                } else {
                    tentativi = 0;
                }
            } catch (Exception e) {
                tentativi++;
                try {
                    waiter();
                } catch (InterruptedException e1) {
                    if (tentativi < 3) isWebPageExists(url);
                }
                if (tentativi < 3) isWebPageExists(url);
            }
            try {
                String html = executeHTTPGetRequest(url);
                doc = Jsoup.parse(html);
                logger.info("HTTP HAS SUCCEDED!");
                tentativi = 0;
                return doc != null;
            } catch (Exception en) {
                tentativi = 0;
                return false;
            }
        }finally{
            tentativi = 0;
        }

    }

    //---------------------------------------------------------------------------------------------------------
    //New Methods
    //---------------------------------------------------------------------------------------------------------

    public static File downloadFileFromHTTPRequest(HttpServletRequest request,File destinationDir) {
        // Download the file to the upload file folder
        //logger.debug("File upload destination directory: " + destinationDir.getAbsolutePath());
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        // Set the size threshold, above which content will be stored on disk.
        fileItemFactory.setSizeThreshold(1024 * 1024); //1 MB
        //Set the temporary directory to store the uploaded files of size above threshold.
        fileItemFactory.setRepository(destinationDir);
        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        File uploadedFile = null;
        try {
            // Parse the request
            @SuppressWarnings("rawtypes")
            List items = uploadHandler.parseRequest(request);
            for (Object item1 : items) {
                FileItem item = (FileItem) item1;
                // Ignore Form Fields.
                if(!item.isFormField()) {
                    //Handle Uploaded files. Write file to the ultimate location.
                    uploadedFile = new File(destinationDir, item.getName());
                    if (item instanceof DiskFileItem) {
                        DiskFileItem t = (DiskFileItem) item;
                        if (!t.getStoreLocation().renameTo(uploadedFile))
                            item.write(uploadedFile);
                    } else
                        item.write(uploadedFile);
                }
            }
        } catch (FileUploadException ex) {
            logger.error("Error encountered while parsing the request", ex);
        } catch (Exception ex) {
            logger.error("Error encountered while uploading file", ex);
        }
        return uploadedFile;
    }

    public static String readURL(URL url){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Process each line.
                //System.out.println(inputLine);
                sb.append(inputLine);
            }
            in.close();
            return sb.toString();
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(),e);
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

}
