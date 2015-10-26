package com.github.p4535992.util.http;
import com.github.p4535992.util.regex.pattern.Patterns;

import java.net.MalformedURLException;
import java.net.*;
import java.util.regex.Pattern;

/** Utility class for URL analysis */
@SuppressWarnings("unused")
public class URLUtil {

  /**
   * Resolve relative URL-s and fix a java.net.URL org.p4535992.mvc.error in handling of URLs
   * with pure query targets.
   * 
   * @param base
   *          base url.
   * @param target
   *          target url (may be relative).
   * @return resolved absolute url.
   * @throws MalformedURLException thrown if any MalformedURL error occurred.
   */
  public static URL resolveURL(URL base, String target) throws MalformedURLException {
    target = target.trim();
    if (target.startsWith("?")) {
      return fixPureQueryTargets(base, target);
    }
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

  //private static Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}(\\d{1,3})");

    private static Pattern IP_PATTERN = Patterns.IP_ADDRESS;
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
   * @param src
   *          The source url.
   * @param dst
   *          The destination url.
   * @param temp
   *          Is the redirect a temporary redirect.
   * 
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
    String srcDomain = URLUtil.getDomainName(srcUrl);
    String dstDomain = URLUtil.getDomainName(dstUrl);
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
   * Returns the lowercased hostname for the url or null if the url is not well
   * formed.
   * 
   * @param url
   *          The url to check.
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
   * 
   * @param url
   *          The url to check.
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
    } catch (Exception e) {
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

}
