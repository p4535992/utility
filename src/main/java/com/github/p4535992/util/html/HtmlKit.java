package com.github.p4535992.util.html;
import com.github.p4535992.util.html.parser.Outliner;
import com.github.p4535992.util.html.parser.PageSaver;
import com.github.p4535992.util.html.parser.ParserGetter;
import com.github.p4535992.util.string.StringUtilities;

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
 * href: http://www.java2s.com/Tutorial/Java/0120__Development/UsejavaxswingtexthtmlHTMLEditorKittoparseHTML.htm
 */
@SuppressWarnings("unused")
public class HtmlKit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(HtmlKit.class);

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
                    new ParserDelegator().parse(new StringReader(html), parserCallback, true); //true make ignore the charset
                } catch (Exception e2) {
                   logger.error(e2.getMessage(), e2);
                }
            }
        }
        return parser;
    }

    public static String toHtml( String string ) {
        if(StringUtilities.isNullOrEmpty(string) )
            return "<html><body></body></html>";
        BufferedReader st = new BufferedReader( new StringReader( string ) );
        StringBuilder buf = new StringBuilder( "<html><body>" );
        try{
            String str = st.readLine();
            while( str != null ){
                if( str.equalsIgnoreCase( "<br/>" ) ){
                    str = "<br>";
                }
                buf.append( str );
                if( !str.equalsIgnoreCase( "<br>" ) ){
                    buf.append( "<br>" );
                }
                str = st.readLine();
            }
        }catch( IOException e ) {
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
        StringBuilder sb = new StringBuilder(newLength);
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
     * @since ostermillerutils 1.00.00.
     */
    public static String unescapeHTML(String s){
        StringBuilder result = new StringBuilder(s.length());
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
                           // value = (htmlEntities.get(escape)).intValue();
                            value = htmlEntities.get(escape);
                        }
                    }
                } catch (NumberFormatException x){
                    //do nothing
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
        return text.replace("&nbsp;", " ").replace("\t", " ").replace("\r", " ").replace("\n", " ").replace("�", "à").trim();
    }


    /**
     * Filter the specified message string for characters that are sensitive
     * in HTML.  This avoids potential attacks caused by including JavaScript
     * codes in the request URL that is often reported in messages.
     *
     * @param message The message string to be filtered.
     * @return message filtered for hat are sensitive in HTML.
     */
    public static String filter(String message) {

        if (message == null)
            return (null);

        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuilder result = new StringBuilder(content.length + 50);
        for (char aContent : content) {
            switch (aContent) {
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
                    result.append(aContent);
            }
        }
        return (result.toString());

    }

    /**
     * Method to convert a text string to html string.
     * @param text string of text to convert.
     * @return string of text converted to HTML format.
     */
    public static String textToHTML(String text) {
        if(text == null) {
            return null;
        }
        int length = text.length();
        boolean prevSlashR = false;
        StringBuilder out = new StringBuilder();
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
            logger.error(e.getMessage(),e);
        }
        return elems;
    }

    public static HTML.Tag[] getListTag(){
        return HTML.getAllTags();
    }

    /**
     * This method is used to insert HTML block dynamically
     * @param source the HTML code to be processes
     * @param replaceNl  if true '\n' will be replaced by &lt;br
     * @param replaceTag if true '' will be replaced by &lt; and '' will be replaced by &gt;
     * @param replaceQuote if true '\"' will be replaced by &quot;
     * @return the formated html block
     */
    public static String formatHtml( String source, boolean replaceNl, boolean replaceTag,boolean replaceQuote ){
        StringBuilder buf = new StringBuilder();
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
        for (char character : characters) {
            switch (character) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                default:
                    builder.append(character);
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

    private static HashMap<String,Integer> htmlEntities = new HashMap<>();
    static {
        htmlEntities.put("nbsp", 160);
        htmlEntities.put("iexcl", 161);
        htmlEntities.put("cent", 162);
        htmlEntities.put("pound", 163);
        htmlEntities.put("curren", 164);
        htmlEntities.put("yen", 165);
        htmlEntities.put("brvbar", 166);
        htmlEntities.put("sect", 167);
        htmlEntities.put("uml", 168);
        htmlEntities.put("copy", 169);
        htmlEntities.put("ordf", 170);
        htmlEntities.put("laquo", 171);
        htmlEntities.put("not", 172);
        htmlEntities.put("shy", 173);
        htmlEntities.put("reg", 174);
        htmlEntities.put("macr", 175);
        htmlEntities.put("deg", 176);
        htmlEntities.put("plusmn", 177);
        htmlEntities.put("sup2", 178);
        htmlEntities.put("sup3", 179);
        htmlEntities.put("acute", 180);
        htmlEntities.put("micro", 181);
        htmlEntities.put("para", 182);
        htmlEntities.put("middot", 183);
        htmlEntities.put("cedil", 184);
        htmlEntities.put("sup1", 185);
        htmlEntities.put("ordm", 186);
        htmlEntities.put("raquo", 187);
        htmlEntities.put("frac14", 188);
        htmlEntities.put("frac12", 189);
        htmlEntities.put("frac34", 190);
        htmlEntities.put("iquest", 191);
        htmlEntities.put("Agrave", 192);
        htmlEntities.put("Aacute", 193);
        htmlEntities.put("Acirc", 194);
        htmlEntities.put("Atilde", 195);
        htmlEntities.put("Auml", 196);
        htmlEntities.put("Aring", 197);
        htmlEntities.put("AElig", 198);
        htmlEntities.put("Ccedil", 199);
        htmlEntities.put("Egrave", 200);
        htmlEntities.put("Eacute", 201);
        htmlEntities.put("Ecirc", 202);
        htmlEntities.put("Euml", 203);
        htmlEntities.put("Igrave", 204);
        htmlEntities.put("Iacute", 205);
        htmlEntities.put("Icirc", 206);
        htmlEntities.put("Iuml", 207);
        htmlEntities.put("ETH", 208);
        htmlEntities.put("Ntilde", 209);
        htmlEntities.put("Ograve", 210);
        htmlEntities.put("Oacute", 211);
        htmlEntities.put("Ocirc", 212);
        htmlEntities.put("Otilde", 213);
        htmlEntities.put("Ouml",214);
        htmlEntities.put("times",215);
        htmlEntities.put("Oslash",216);
        htmlEntities.put("Ugrave",217);
        htmlEntities.put("Uacute",218);
        htmlEntities.put("Ucirc",219);
        htmlEntities.put("Uuml",220);
        htmlEntities.put("Yacute",221);
        htmlEntities.put("THORN",222);
        htmlEntities.put("szlig",223);
        htmlEntities.put("agrave",224);
        htmlEntities.put("aacute",225);
        htmlEntities.put("acirc",226);
        htmlEntities.put("atilde",227);
        htmlEntities.put("auml",228);
        htmlEntities.put("aring",229);
        htmlEntities.put("aelig",230);
        htmlEntities.put("ccedil",231);
        htmlEntities.put("egrave",232);
        htmlEntities.put("eacute",233);
        htmlEntities.put("ecirc",234);
        htmlEntities.put("euml",235);
        htmlEntities.put("igrave",236);
        htmlEntities.put("iacute",237);
        htmlEntities.put("icirc",238);
        htmlEntities.put("iuml",239);
        htmlEntities.put("eth",240);
        htmlEntities.put("ntilde",241);
        htmlEntities.put("ograve",242);
        htmlEntities.put("oacute",243);
        htmlEntities.put("ocirc",244);
        htmlEntities.put("otilde",245);
        htmlEntities.put("ouml",246);
        htmlEntities.put("divide",247);
        htmlEntities.put("oslash",248);
        htmlEntities.put("ugrave",249);
        htmlEntities.put("uacute",250);
        htmlEntities.put("ucirc",251);
        htmlEntities.put("uuml",252);
        htmlEntities.put("yacute",253);
        htmlEntities.put("thorn",254);
        htmlEntities.put("yuml",255);
        htmlEntities.put("fnof",402);
        htmlEntities.put("Alpha",913);
        htmlEntities.put("Beta",914);
        htmlEntities.put("Gamma",915);
        htmlEntities.put("Delta",916);
        htmlEntities.put("Epsilon",917);
        htmlEntities.put("Zeta",918);
        htmlEntities.put("Eta",919);
        htmlEntities.put("Theta",920);
        htmlEntities.put("Iota",921);
        htmlEntities.put("Kappa",922);
        htmlEntities.put("Lambda",923);
        htmlEntities.put("Mu",924);
        htmlEntities.put("Nu",925);
        htmlEntities.put("Xi",926);
        htmlEntities.put("Omicron",927);
        htmlEntities.put("Pi",928);
        htmlEntities.put("Rho",929);
        htmlEntities.put("Sigma",931);
        htmlEntities.put("Tau",932);
        htmlEntities.put("Upsilon",933);
        htmlEntities.put("Phi",934);
        htmlEntities.put("Chi",935);
        htmlEntities.put("Psi",936);
        htmlEntities.put("Omega",937);
        htmlEntities.put("alpha",945);
        htmlEntities.put("beta",946);
        htmlEntities.put("gamma",947);
        htmlEntities.put("delta",948);
        htmlEntities.put("epsilon",949);
        htmlEntities.put("zeta",950);
        htmlEntities.put("eta",951);
        htmlEntities.put("theta",952);
        htmlEntities.put("iota",953);
        htmlEntities.put("kappa",954);
        htmlEntities.put("lambda",955);
        htmlEntities.put("mu",956);
        htmlEntities.put("nu",957);
        htmlEntities.put("xi",958);
        htmlEntities.put("omicron",959);
        htmlEntities.put("pi",960);
        htmlEntities.put("rho",961);
        htmlEntities.put("sigmaf",962);
        htmlEntities.put("sigma",963);
        htmlEntities.put("tau",964);
        htmlEntities.put("upsilon",965);
        htmlEntities.put("phi",966);
        htmlEntities.put("chi",967);
        htmlEntities.put("psi",968);
        htmlEntities.put("omega",969);
        htmlEntities.put("thetasym",977);
        htmlEntities.put("upsih",978);
        htmlEntities.put("piv",982);
        htmlEntities.put("bull",8226);
        htmlEntities.put("hellip",8230);
        htmlEntities.put("prime",8242);
        htmlEntities.put("Prime",8243);
        htmlEntities.put("oline",8254);
        htmlEntities.put("frasl",8260);
        htmlEntities.put("weierp",8472);
        htmlEntities.put("image",8465);
        htmlEntities.put("real",8476);
        htmlEntities.put("trade",8482);
        htmlEntities.put("alefsym",8501);
        htmlEntities.put("larr",8592);
        htmlEntities.put("uarr",8593);
        htmlEntities.put("rarr",8594);
        htmlEntities.put("darr",8595);
        htmlEntities.put("harr",8596);
        htmlEntities.put("crarr",8629);
        htmlEntities.put("lArr",8656);
        htmlEntities.put("uArr",8657);
        htmlEntities.put("rArr",8658);
        htmlEntities.put("dArr",8659);
        htmlEntities.put("hArr",8660);
        htmlEntities.put("forall",8704);
        htmlEntities.put("part",8706);
        htmlEntities.put("exist",8707);
        htmlEntities.put("empty",8709);
        htmlEntities.put("nabla",8711);
        htmlEntities.put("isin",8712);
        htmlEntities.put("notin",8713);
        htmlEntities.put("ni",8715);
        htmlEntities.put("prod",8719);
        htmlEntities.put("sum",8721);
        htmlEntities.put("minus",8722);
        htmlEntities.put("lowast",8727);
        htmlEntities.put("radic",8730);
        htmlEntities.put("prop",8733);
        htmlEntities.put("infin",8734);
        htmlEntities.put("ang",8736);
        htmlEntities.put("and",8743);
        htmlEntities.put("or",8744);
        htmlEntities.put("cap",8745);
        htmlEntities.put("cup",8746);
        htmlEntities.put("int",8747);
        htmlEntities.put("there4",8756);
        htmlEntities.put("sim",8764);
        htmlEntities.put("cong",8773);
        htmlEntities.put("asymp",8776);
        htmlEntities.put("ne",8800);
        htmlEntities.put("equiv",8801);
        htmlEntities.put("le",8804);
        htmlEntities.put("ge",8805);
        htmlEntities.put("sub",8834);
        htmlEntities.put("sup",8835);
        htmlEntities.put("nsub",8836);
        htmlEntities.put("sube",8838);
        htmlEntities.put("supe",8839);
        htmlEntities.put("oplus",8853);
        htmlEntities.put("otimes",8855);
        htmlEntities.put("perp",8869);
        htmlEntities.put("sdot",8901);
        htmlEntities.put("lceil",8968);
        htmlEntities.put("rceil",8969);
        htmlEntities.put("lfloor",8970);
        htmlEntities.put("rfloor",8971);
        htmlEntities.put("lang",9001);
        htmlEntities.put("rang",9002);
        htmlEntities.put("loz",9674);
        htmlEntities.put("spades",9824);
        htmlEntities.put("clubs",9827);
        htmlEntities.put("hearts",9829);
        htmlEntities.put("diams",9830);
        htmlEntities.put("quot",34);
        htmlEntities.put("amp",38);
        htmlEntities.put("lt",60);
        htmlEntities.put("gt",62);
        htmlEntities.put("OElig",338);
        htmlEntities.put("oelig",339);
        htmlEntities.put("Scaron",352);
        htmlEntities.put("scaron",353);
        htmlEntities.put("Yuml",376);
        htmlEntities.put("circ",710);
        htmlEntities.put("tilde",732);
        htmlEntities.put("ensp",8194);
        htmlEntities.put("emsp",8195);
        htmlEntities.put("thinsp",8201);
        htmlEntities.put("zwnj",8204);
        htmlEntities.put("zwj",8205);
        htmlEntities.put("lrm",8206);
        htmlEntities.put("rlm",8207);
        htmlEntities.put("ndash",8211);
        htmlEntities.put("mdash",8212);
        htmlEntities.put("lsquo",8216);
        htmlEntities.put("rsquo",8217);
        htmlEntities.put("sbquo",8218);
        htmlEntities.put("ldquo",8220);
        htmlEntities.put("rdquo",8221);
        htmlEntities.put("bdquo",8222);
        htmlEntities.put("dagger",8224);
        htmlEntities.put("Dagger",8225);
        htmlEntities.put("permil",8240);
        htmlEntities.put("lsaquo",8249);
        htmlEntities.put("rsaquo",8250);
        htmlEntities.put("euro",8364);
    }




}


