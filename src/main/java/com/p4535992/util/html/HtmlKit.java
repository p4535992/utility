package com.p4535992.util.html;
import com.p4535992.util.html.parser.Outliner;
import com.p4535992.util.html.parser.PageSaver;
import com.p4535992.util.html.parser.ParserGetter;
import com.p4535992.util.string.StringKit;
import com.p4535992.util.log.SystemLog;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Element;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Created by 4535992 on 05/05/2015.
 * @href: http://www.java2s.com/Tutorial/Java/0120__Development/UsejavaxswingtexthtmlHTMLEditorKittoparseHTML.htm
 */
public class HtmlKit {

    public static void callPageSaver(InputStream is,URL url) throws IOException {
        ParserGetter kit = new ParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        InputStreamReader r = new InputStreamReader(is);
        String remoteFileName = url.getFile();
        if (remoteFileName.endsWith("/")) {
            remoteFileName += "index.html";
        }
        if (remoteFileName.startsWith("/")) {
            remoteFileName = remoteFileName.substring(1);
        }
        File localDirectory = new File(url.getHost());
        while (remoteFileName.indexOf('/') > -1) {
            String part = remoteFileName.substring(0, remoteFileName.indexOf('/'));
            remoteFileName = remoteFileName.substring(remoteFileName.indexOf('/') + 1);
            localDirectory = new File(localDirectory, part);
        }
        if (localDirectory.mkdirs()) {
            File output = new File(localDirectory, remoteFileName);
            FileWriter out = new FileWriter(output);
            HTMLEditorKit.ParserCallback callback = new PageSaver(out, url);
            parser.parse(r, callback, false);
        }
    }

    public static HTMLEditorKit.Parser getParser(InputStream is,String encoding) throws Exception {
        ParserGetter kit = new ParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        InputStreamReader r = new InputStreamReader(is, encoding);
        // parse once just to detect the encoding
        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback();
        try {
            parser.parse(r, parserCallback, false);
        }catch (javax.swing.text.ChangedCharSetException e) {
            try {
                parser.parse(r, parserCallback, true);
            }catch (Exception e1) {
                try {
                    String html = "";
                    StringBuffer temp = new StringBuffer(html);
                    new ParserDelegator().parse(new StringReader(temp.toString()), parserCallback, true); //true make ignore the charset
                } catch (Exception e2) {
                    SystemLog.exception(e2);
                }
            }
        }
        return parser;
    }

    public static String toHtml( String string ) {
        if(StringKit.isNullOrEmpty(string) )
            return "<html><body></body></html>";

        BufferedReader st = new BufferedReader( new StringReader( string ) );
        StringBuffer buf = new StringBuffer( "<html><body>" );

        try
        {
            String str = st.readLine();

            while( str != null )
            {
                if( str.equalsIgnoreCase( "<br/>" ) )
                {
                    str = "<br>";
                }

                buf.append( str );

                if( !str.equalsIgnoreCase( "<br>" ) )
                {
                    buf.append( "<br>" );
                }

                str = st.readLine();
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        buf.append( "</body></html>" );
        string = buf.toString();
        return string;
    }


    /**
     * Replaces characters that may be confused by a HTML
     * parser with their equivalent character entity references.
     *
     * Any data that will appear as text on a org.p4535992.mvc.webapp page should
     * be be escaped.  This is especially important for data
     * that comes from untrusted sources such as Internet users.
     * A common mistake in CGI programming is to ask a user for
     * data and then put that data on a org.p4535992.mvc.webapp page.  For example:<pre>
     * Server: What is your name?
     * User: &lt;b&gt;Joe&lt;b&gt;
     * Server: Hello <b>Joe</b>, Welcome</pre>
     * If the name is put on the page without checking that it doesn't
     * contain HTML code or without sanitizing that HTML code, the user
     * could reformat the page, insert scripts, and control the the
     * content on your org.p4535992.mvc.webapp server.
     *
     * This method will replace HTML characters such as &gt; with their
     * HTML entity reference (&amp;gt;) so that the html parser will
     * be sure to interpret them as plain text rather than HTML or script.
     *
     * This method should be used for both data to be displayed in text
     * in the html document, and data put in form elements. For example:<br>
     * <code>&lt;html&gt;&lt;body&gt;<i>This in not a &amp;lt;tag&amp;gt;
     * in HTML</i>&lt;/body&gt;&lt;/html&gt;</code><br>
     * and<br>
     * <code>&lt;form&gt;&lt;input type="hidden" name="date" value="<i>This data could
     * be &amp;quot;malicious&amp;quot;</i>"&gt;&lt;/form&gt;</code><br>
     * In the second example, the form data would be properly be resubmitted
     * to your cgi script in the URLEncoded format:<br>
     * <code><i>This data could be %22malicious%22</i></code>
     *
     * @param s String to be escaped
     * @return escaped String
     * @throws NullPointerException if s is null.
     *
     * @since ostermillerutils 1.00.00
     */
    public static String escapeHTML(String s){
        int length = s.length();
        int newLength = length;
        boolean someCharacterEscaped = false;
        // first check for characters that might
        // be dangerous and calculate a length
        // of the string that has escapes.
        for (int i=0; i<length; i++){
            char c = s.charAt(i);
            int cint = 0xffff & c;
            if (cint < 32){
                switch(c){
                    case '\r':
                    case '\n':
                    case '\t':
                    case '\f':{
                    } break;
                    default: {
                        newLength -= 1;
                        someCharacterEscaped = true;
                    }
                }
            } else {
                switch(c){
                    case '\"':{
                        newLength += 5;
                        someCharacterEscaped = true;
                    } break;
                    case '&':
                    case '\'':{
                        newLength += 4;
                        someCharacterEscaped = true;
                    } break;
                    case '<':
                    case '>':{
                        newLength += 3;
                        someCharacterEscaped = true;
                    } break;
                }
            }
        }
        if (!someCharacterEscaped){
            // nothing to escape in the string
            return s;
        }
        StringBuffer sb = new StringBuffer(newLength);
        for (int i=0; i<length; i++){
            char c = s.charAt(i);
            int cint = 0xffff & c;
            if (cint < 32){
                switch(c){
                    case '\r':
                    case '\n':
                    case '\t':
                    case '\f':{
                        sb.append(c);
                    } break;
                    default: {
                        // Remove this character
                    }
                }
            } else {
                switch(c){
                    case '\"':{
                        sb.append("&quot;");
                    } break;
                    case '\'':{
                        sb.append("&#39;");
                    } break;
                    case '&':{
                        sb.append("&amp;");
                    } break;
                    case '<':{
                        sb.append("&lt;");
                    } break;
                    case '>':{
                        sb.append("&gt;");
                    } break;
                    default: {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Turn any HTML escape entities in the string into
     * characters and return the resulting string.
     *
     * @param s String to be unescaped.
     * @return unescaped String.
     * @throws NullPointerException if s is null.
     *
     * @since ostermillerutils 1.00.00
     */
    public static String unescapeHTML(String s){
        StringBuffer result = new StringBuffer(s.length());
        int ampInd = s.indexOf("&");
        int lastEnd = 0;
        while (ampInd >= 0){
            int nextAmp = s.indexOf("&", ampInd+1);
            int nextSemi = s.indexOf(";", ampInd+1);
            if (nextSemi != -1 && (nextAmp == -1 || nextSemi < nextAmp)){
                int value = -1;
                String escape = s.substring(ampInd+1,nextSemi);
                try {
                    if (escape.startsWith("#")){
                        value = Integer.parseInt(escape.substring(1), 10);
                    } else {
                        if (htmlEntities.containsKey(escape)){
                            value = ((Integer)(htmlEntities.get(escape))).intValue();
                        }
                    }
                } catch (NumberFormatException x){
                }
                result.append(s.substring(lastEnd, ampInd));
                lastEnd = nextSemi + 1;
                if (value >= 0 && value <= 0xffff){
                    result.append((char)value);
                } else {
                    result.append("&").append(escape).append(";");
                }
            }
            ampInd = nextAmp;
        }
        result.append(s.substring(lastEnd));
        return result.toString();
    }


    public static String cleanHtmlText(String text){
        return text = text.replace("&nbsp;", " ").replace("\t", " ").replace("\r", " ").replace("\n", " ").replace("�", "à").trim();
    }


    /**
     * Filter the specified message string for characters that are sensitive
     * in HTML.  This avoids potential attacks caused by including JavaScript
     * codes in the request URL that is often reported in org.p4535992.mvc.error messages.
     *
     * @param message The message string to be filtered
     */
    public static String filter(String message) {

        if (message == null)
            return (null);

        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(content[i]);
            }
        }
        return (result.toString());

    }

    /**
     * Method to convert a text string to html string
     * @param text
     * @return
     */
    public static String textToHTML(String text) {
        if(text == null) {
            return null;
        }
        int length = text.length();
        boolean prevSlashR = false;
        StringBuffer out = new StringBuffer();
        for(int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            switch(ch) {
                case '\r':
                    if(prevSlashR) {
                        out.append("<br>");
                    }
                    prevSlashR = true;
                    break;
                case '\n':
                    prevSlashR = false;
                    out.append("<br>");
                    break;
                case '"':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&quot;");
                    break;
                case '<':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&lt;");
                    break;
                case '>':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&gt;");
                    break;
                case '&':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&amp;");
                    break;
                default:
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append(ch);
                    break;
            }
        }
        return out.toString();
    }

    public static void parse(URL url, String encoding) throws IOException {
        ParserGetter kit = new ParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        InputStream in = url.openStream();
        InputStreamReader r = new InputStreamReader(in, encoding);
        HTMLEditorKit.ParserCallback callback = new Outliner(new OutputStreamWriter(System.out));
        parser.parse(r, callback, true);
    }

    public static List<Element> DTDParser(){
        List<Element> elems = new ArrayList<>();
        try {
            DTD d1 = DTD.getDTD("html");
            for (int i = 0; i < 14; i++) {
                elems.add(d1.getElement(i));
            }
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return elems;
    }

    public static HTML.Tag[] getListTag(){
        return HTML.getAllTags();
    }

    /**
     * This method is used to insert HTML block dynamically
     *
     * @param source
     *            the HTML code to be processes
     * @param replaceNl
     *            if true '\n' will be replaced by &lt;br>
     * @param replaceTag
     *            if true '<' will be replaced by &lt; and '>' will be replaced
     *            by &gt;
     * @param replaceQuote
     *            if true '\"' will be replaced by &quot;
     * @return the formated html block
     */
    public static final String formatHtml( String source, boolean replaceNl, boolean replaceTag,boolean replaceQuote ){
        StringBuffer buf = new StringBuffer();
        int len = source.length();

        for ( int ii = 0; ii < len; ii++ )
        {
            char ch = source.charAt( ii );

            switch ( ch )
            {
                case '\"':
                    if ( replaceQuote )
                    {
                        buf.append( "&quot;" );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;

                case '<':
                    if ( replaceTag )
                    {
                        buf.append( "&lt;" );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;

                case '>':
                    if ( replaceTag )
                    {
                        buf.append( "&gt;" );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;

                case '\n':
                    if ( replaceNl )
                    {
                        if ( replaceTag )
                        {
                            buf.append( "&lt;br&gt;" );
                        }
                        else
                        {
                            buf.append( "<br>" );
                        }
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;

                case '\r':
                    break;

                case '&':
                    buf.append( "&amp;" );
                    break;

                default:
                    buf.append( ch );
                    break;
            }
        }

        return buf.toString();
    }


    private static final String FIELD_DELIMITER_STRING = "||";
    private static final Pattern FIELD_DELIMITER_PATTERN = Pattern.compile("\\|\\|");

    /**
     * Replaces special HTML characters from the set {@literal [<, >, ", ', &]} with their HTML
     * escape codes.  Note that because the escape codes are multi-character that the returned
     * String could be longer than the one passed in.
     *
     * @param fragment a String fragment that might have HTML special characters in it
     * @return the fragment with special characters escaped
     */
    public static String encode(String fragment) {
        // If the input is null, then the output is null
        if (fragment == null) return null;

        StringBuilder builder = new StringBuilder(fragment.length() + 10); // a little wiggle room
        char[] characters = fragment.toCharArray();

        // This loop used to also look for and replace single ticks with &apos; but it
        // turns out that it's not strictly necessary since Stripes uses double-quotes
        // around all form fields, and stupid IE6 will render &apos; verbatim instead
        // of as a single quote.
        for (int i=0; i<characters.length; ++i) {
            switch (characters[i]) {
                case '<'  : builder.append("&lt;"); break;
                case '>'  : builder.append("&gt;"); break;
                case '"'  : builder.append("&quot;"); break;
                case '&'  : builder.append("&amp;"); break;
                default: builder.append(characters[i]);
            }
        }

        return builder.toString();
    }

    /**
     * One of a pair of methods (the other is splitValues) that is used to combine several
     * un-encoded values into a single delimited, encoded value for placement into a
     * hidden field.
     *
     * @param values One or more values which are to be combined
     * @return a single HTML-encoded String that contains all the values in such a way that
     *         they can be converted back into a Collection of Strings with splitValues().
     */
    public static String combineValues(Collection<String> values) {
        if (values == null || values.size() == 0) {
            return "";
        }
        else {
            StringBuilder builder = new StringBuilder(values.size() * 30);
            for (String value : values) {
                builder.append(value).append(FIELD_DELIMITER_STRING);
            }

            return encode(builder.toString());
        }
    }

    /**
     * Takes in a String produced by combineValues and returns a Collection of values that
     * contains the same values as originally supplied to combineValues.  Note that the order
     * or items in the collection (and indeed the type of Collection used) are not guaranteed
     * to be the same.
     *
     * @param value a String value produced by
     * @return a Collection of zero or more Strings
     */
    public static Collection<String> splitValues(String value) {
        if (value == null || value.length() == 0) {
            return Collections.emptyList();
        }
        else {
            String[] splits = FIELD_DELIMITER_PATTERN.split(value);
            return Arrays.asList(splits);
        }
    }

    private static HashMap<String,Integer> htmlEntities = new HashMap<String,Integer>();
    static {
        htmlEntities.put("nbsp", new Integer(160));
        htmlEntities.put("iexcl", new Integer(161));
        htmlEntities.put("cent", new Integer(162));
        htmlEntities.put("pound", new Integer(163));
        htmlEntities.put("curren", new Integer(164));
        htmlEntities.put("yen", new Integer(165));
        htmlEntities.put("brvbar", new Integer(166));
        htmlEntities.put("sect", new Integer(167));
        htmlEntities.put("uml", new Integer(168));
        htmlEntities.put("copy", new Integer(169));
        htmlEntities.put("ordf", new Integer(170));
        htmlEntities.put("laquo", new Integer(171));
        htmlEntities.put("not", new Integer(172));
        htmlEntities.put("shy", new Integer(173));
        htmlEntities.put("reg", new Integer(174));
        htmlEntities.put("macr", new Integer(175));
        htmlEntities.put("deg", new Integer(176));
        htmlEntities.put("plusmn", new Integer(177));
        htmlEntities.put("sup2", new Integer(178));
        htmlEntities.put("sup3", new Integer(179));
        htmlEntities.put("acute", new Integer(180));
        htmlEntities.put("micro", new Integer(181));
        htmlEntities.put("para", new Integer(182));
        htmlEntities.put("middot", new Integer(183));
        htmlEntities.put("cedil", new Integer(184));
        htmlEntities.put("sup1", new Integer(185));
        htmlEntities.put("ordm", new Integer(186));
        htmlEntities.put("raquo", new Integer(187));
        htmlEntities.put("frac14", new Integer(188));
        htmlEntities.put("frac12", new Integer(189));
        htmlEntities.put("frac34", new Integer(190));
        htmlEntities.put("iquest", new Integer(191));
        htmlEntities.put("Agrave", new Integer(192));
        htmlEntities.put("Aacute", new Integer(193));
        htmlEntities.put("Acirc", new Integer(194));
        htmlEntities.put("Atilde", new Integer(195));
        htmlEntities.put("Auml", new Integer(196));
        htmlEntities.put("Aring", new Integer(197));
        htmlEntities.put("AElig", new Integer(198));
        htmlEntities.put("Ccedil", new Integer(199));
        htmlEntities.put("Egrave", new Integer(200));
        htmlEntities.put("Eacute", new Integer(201));
        htmlEntities.put("Ecirc", new Integer(202));
        htmlEntities.put("Euml", new Integer(203));
        htmlEntities.put("Igrave", new Integer(204));
        htmlEntities.put("Iacute", new Integer(205));
        htmlEntities.put("Icirc", new Integer(206));
        htmlEntities.put("Iuml", new Integer(207));
        htmlEntities.put("ETH", new Integer(208));
        htmlEntities.put("Ntilde", new Integer(209));
        htmlEntities.put("Ograve", new Integer(210));
        htmlEntities.put("Oacute", new Integer(211));
        htmlEntities.put("Ocirc", new Integer(212));
        htmlEntities.put("Otilde", new Integer(213));
        htmlEntities.put("Ouml", new Integer(214));
        htmlEntities.put("times", new Integer(215));
        htmlEntities.put("Oslash", new Integer(216));
        htmlEntities.put("Ugrave", new Integer(217));
        htmlEntities.put("Uacute", new Integer(218));
        htmlEntities.put("Ucirc", new Integer(219));
        htmlEntities.put("Uuml", new Integer(220));
        htmlEntities.put("Yacute", new Integer(221));
        htmlEntities.put("THORN", new Integer(222));
        htmlEntities.put("szlig", new Integer(223));
        htmlEntities.put("agrave", new Integer(224));
        htmlEntities.put("aacute", new Integer(225));
        htmlEntities.put("acirc", new Integer(226));
        htmlEntities.put("atilde", new Integer(227));
        htmlEntities.put("auml", new Integer(228));
        htmlEntities.put("aring", new Integer(229));
        htmlEntities.put("aelig", new Integer(230));
        htmlEntities.put("ccedil", new Integer(231));
        htmlEntities.put("egrave", new Integer(232));
        htmlEntities.put("eacute", new Integer(233));
        htmlEntities.put("ecirc", new Integer(234));
        htmlEntities.put("euml", new Integer(235));
        htmlEntities.put("igrave", new Integer(236));
        htmlEntities.put("iacute", new Integer(237));
        htmlEntities.put("icirc", new Integer(238));
        htmlEntities.put("iuml", new Integer(239));
        htmlEntities.put("eth", new Integer(240));
        htmlEntities.put("ntilde", new Integer(241));
        htmlEntities.put("ograve", new Integer(242));
        htmlEntities.put("oacute", new Integer(243));
        htmlEntities.put("ocirc", new Integer(244));
        htmlEntities.put("otilde", new Integer(245));
        htmlEntities.put("ouml", new Integer(246));
        htmlEntities.put("divide", new Integer(247));
        htmlEntities.put("oslash", new Integer(248));
        htmlEntities.put("ugrave", new Integer(249));
        htmlEntities.put("uacute", new Integer(250));
        htmlEntities.put("ucirc", new Integer(251));
        htmlEntities.put("uuml", new Integer(252));
        htmlEntities.put("yacute", new Integer(253));
        htmlEntities.put("thorn", new Integer(254));
        htmlEntities.put("yuml", new Integer(255));
        htmlEntities.put("fnof", new Integer(402));
        htmlEntities.put("Alpha", new Integer(913));
        htmlEntities.put("Beta", new Integer(914));
        htmlEntities.put("Gamma", new Integer(915));
        htmlEntities.put("Delta", new Integer(916));
        htmlEntities.put("Epsilon", new Integer(917));
        htmlEntities.put("Zeta", new Integer(918));
        htmlEntities.put("Eta", new Integer(919));
        htmlEntities.put("Theta", new Integer(920));
        htmlEntities.put("Iota", new Integer(921));
        htmlEntities.put("Kappa", new Integer(922));
        htmlEntities.put("Lambda", new Integer(923));
        htmlEntities.put("Mu", new Integer(924));
        htmlEntities.put("Nu", new Integer(925));
        htmlEntities.put("Xi", new Integer(926));
        htmlEntities.put("Omicron", new Integer(927));
        htmlEntities.put("Pi", new Integer(928));
        htmlEntities.put("Rho", new Integer(929));
        htmlEntities.put("Sigma", new Integer(931));
        htmlEntities.put("Tau", new Integer(932));
        htmlEntities.put("Upsilon", new Integer(933));
        htmlEntities.put("Phi", new Integer(934));
        htmlEntities.put("Chi", new Integer(935));
        htmlEntities.put("Psi", new Integer(936));
        htmlEntities.put("Omega", new Integer(937));
        htmlEntities.put("alpha", new Integer(945));
        htmlEntities.put("beta", new Integer(946));
        htmlEntities.put("gamma", new Integer(947));
        htmlEntities.put("delta", new Integer(948));
        htmlEntities.put("epsilon", new Integer(949));
        htmlEntities.put("zeta", new Integer(950));
        htmlEntities.put("eta", new Integer(951));
        htmlEntities.put("theta", new Integer(952));
        htmlEntities.put("iota", new Integer(953));
        htmlEntities.put("kappa", new Integer(954));
        htmlEntities.put("lambda", new Integer(955));
        htmlEntities.put("mu", new Integer(956));
        htmlEntities.put("nu", new Integer(957));
        htmlEntities.put("xi", new Integer(958));
        htmlEntities.put("omicron", new Integer(959));
        htmlEntities.put("pi", new Integer(960));
        htmlEntities.put("rho", new Integer(961));
        htmlEntities.put("sigmaf", new Integer(962));
        htmlEntities.put("sigma", new Integer(963));
        htmlEntities.put("tau", new Integer(964));
        htmlEntities.put("upsilon", new Integer(965));
        htmlEntities.put("phi", new Integer(966));
        htmlEntities.put("chi", new Integer(967));
        htmlEntities.put("psi", new Integer(968));
        htmlEntities.put("omega", new Integer(969));
        htmlEntities.put("thetasym", new Integer(977));
        htmlEntities.put("upsih", new Integer(978));
        htmlEntities.put("piv", new Integer(982));
        htmlEntities.put("bull", new Integer(8226));
        htmlEntities.put("hellip", new Integer(8230));
        htmlEntities.put("prime", new Integer(8242));
        htmlEntities.put("Prime", new Integer(8243));
        htmlEntities.put("oline", new Integer(8254));
        htmlEntities.put("frasl", new Integer(8260));
        htmlEntities.put("weierp", new Integer(8472));
        htmlEntities.put("image", new Integer(8465));
        htmlEntities.put("real", new Integer(8476));
        htmlEntities.put("trade", new Integer(8482));
        htmlEntities.put("alefsym", new Integer(8501));
        htmlEntities.put("larr", new Integer(8592));
        htmlEntities.put("uarr", new Integer(8593));
        htmlEntities.put("rarr", new Integer(8594));
        htmlEntities.put("darr", new Integer(8595));
        htmlEntities.put("harr", new Integer(8596));
        htmlEntities.put("crarr", new Integer(8629));
        htmlEntities.put("lArr", new Integer(8656));
        htmlEntities.put("uArr", new Integer(8657));
        htmlEntities.put("rArr", new Integer(8658));
        htmlEntities.put("dArr", new Integer(8659));
        htmlEntities.put("hArr", new Integer(8660));
        htmlEntities.put("forall", new Integer(8704));
        htmlEntities.put("part", new Integer(8706));
        htmlEntities.put("exist", new Integer(8707));
        htmlEntities.put("empty", new Integer(8709));
        htmlEntities.put("nabla", new Integer(8711));
        htmlEntities.put("isin", new Integer(8712));
        htmlEntities.put("notin", new Integer(8713));
        htmlEntities.put("ni", new Integer(8715));
        htmlEntities.put("prod", new Integer(8719));
        htmlEntities.put("sum", new Integer(8721));
        htmlEntities.put("minus", new Integer(8722));
        htmlEntities.put("lowast", new Integer(8727));
        htmlEntities.put("radic", new Integer(8730));
        htmlEntities.put("prop", new Integer(8733));
        htmlEntities.put("infin", new Integer(8734));
        htmlEntities.put("ang", new Integer(8736));
        htmlEntities.put("and", new Integer(8743));
        htmlEntities.put("or", new Integer(8744));
        htmlEntities.put("cap", new Integer(8745));
        htmlEntities.put("cup", new Integer(8746));
        htmlEntities.put("int", new Integer(8747));
        htmlEntities.put("there4", new Integer(8756));
        htmlEntities.put("sim", new Integer(8764));
        htmlEntities.put("cong", new Integer(8773));
        htmlEntities.put("asymp", new Integer(8776));
        htmlEntities.put("ne", new Integer(8800));
        htmlEntities.put("equiv", new Integer(8801));
        htmlEntities.put("le", new Integer(8804));
        htmlEntities.put("ge", new Integer(8805));
        htmlEntities.put("sub", new Integer(8834));
        htmlEntities.put("sup", new Integer(8835));
        htmlEntities.put("nsub", new Integer(8836));
        htmlEntities.put("sube", new Integer(8838));
        htmlEntities.put("supe", new Integer(8839));
        htmlEntities.put("oplus", new Integer(8853));
        htmlEntities.put("otimes", new Integer(8855));
        htmlEntities.put("perp", new Integer(8869));
        htmlEntities.put("sdot", new Integer(8901));
        htmlEntities.put("lceil", new Integer(8968));
        htmlEntities.put("rceil", new Integer(8969));
        htmlEntities.put("lfloor", new Integer(8970));
        htmlEntities.put("rfloor", new Integer(8971));
        htmlEntities.put("lang", new Integer(9001));
        htmlEntities.put("rang", new Integer(9002));
        htmlEntities.put("loz", new Integer(9674));
        htmlEntities.put("spades", new Integer(9824));
        htmlEntities.put("clubs", new Integer(9827));
        htmlEntities.put("hearts", new Integer(9829));
        htmlEntities.put("diams", new Integer(9830));
        htmlEntities.put("quot", new Integer(34));
        htmlEntities.put("amp", new Integer(38));
        htmlEntities.put("lt", new Integer(60));
        htmlEntities.put("gt", new Integer(62));
        htmlEntities.put("OElig", new Integer(338));
        htmlEntities.put("oelig", new Integer(339));
        htmlEntities.put("Scaron", new Integer(352));
        htmlEntities.put("scaron", new Integer(353));
        htmlEntities.put("Yuml", new Integer(376));
        htmlEntities.put("circ", new Integer(710));
        htmlEntities.put("tilde", new Integer(732));
        htmlEntities.put("ensp", new Integer(8194));
        htmlEntities.put("emsp", new Integer(8195));
        htmlEntities.put("thinsp", new Integer(8201));
        htmlEntities.put("zwnj", new Integer(8204));
        htmlEntities.put("zwj", new Integer(8205));
        htmlEntities.put("lrm", new Integer(8206));
        htmlEntities.put("rlm", new Integer(8207));
        htmlEntities.put("ndash", new Integer(8211));
        htmlEntities.put("mdash", new Integer(8212));
        htmlEntities.put("lsquo", new Integer(8216));
        htmlEntities.put("rsquo", new Integer(8217));
        htmlEntities.put("sbquo", new Integer(8218));
        htmlEntities.put("ldquo", new Integer(8220));
        htmlEntities.put("rdquo", new Integer(8221));
        htmlEntities.put("bdquo", new Integer(8222));
        htmlEntities.put("dagger", new Integer(8224));
        htmlEntities.put("Dagger", new Integer(8225));
        htmlEntities.put("permil", new Integer(8240));
        htmlEntities.put("lsaquo", new Integer(8249));
        htmlEntities.put("rsaquo", new Integer(8250));
        htmlEntities.put("euro", new Integer(8364));
    }




}


