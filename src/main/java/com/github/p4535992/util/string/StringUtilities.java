package com.github.p4535992.util.string;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.regex.pattern.Patterns;
import org.apache.tika.exception.TikaException;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by 4535992 on 06/11/2015.
 * href: http://stackoverflow.com/questions/9572795/convert-list-to-array-in-java
 * href: http://stackoverflow.com/questions/11404086/how-could-i-initialize-a-generic-array
 * href: https://github.com/ku-fpg/armatus/blob/master/Armatus%20Android%20App/src/edu/kufpg/armatus/util/StringUtils.java
 * @author 4535992.
 * @version 2016-01-01.
 */
@SuppressWarnings("unused")
public class StringUtilities {
    
    private static final org.slf4j.Logger logger = 
            org.slf4j.LoggerFactory.getLogger(StringUtilities.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

     public enum special{
        WHITESPACE(0), NBSP(1),NEWLINE(2),PROJECTDIR(3),LINE_FEED(4),LINE_SEP(5),EMPTY_STR(6),
        LT(7),GT(8),AMP(9),QUAT(10),SINGLE_QUAT(11),ESC_LT(12),ESC_GT(13),ESC_AMP(14),CRLF(15);
        private final Integer value;
        special(Integer value) {this.value = value;}
        @Override
        public String toString() {
            String svalue="";
            switch (this) {
                case WHITESPACE: svalue = "\\s+"; break;
                case NBSP: svalue ="\u00A0"; break;
                ///case NBSP_CHAR: value ='\u00A0'; break;
                case NEWLINE: svalue = System.lineSeparator(); break;
                case PROJECTDIR: svalue = System.getProperty("user.dir");break;
                case LINE_FEED: svalue = "\r\n";break;
                case LINE_SEP: System.getProperty("line.separator");break;
                case EMPTY_STR:  svalue ="";break;
                case LT:  svalue ="<";break;
                case GT:  svalue =">";break;
                case AMP:  svalue ="&";break;
                case QUAT:  svalue ="\"";break;
                case SINGLE_QUAT: svalue = "'";break;
                case ESC_LT:  svalue ="&lt;";break;
                case ESC_GT:  svalue ="&gt;";break;
                case ESC_AMP:  svalue ="&amp;";break;
                case CRLF: svalue = "\r\n"; break;
            }
            return svalue;
        }
    }

    /**
     * A regular expression that matches several kinds of whitespace characters, including and newlines.
     */
    public static final String WHITESPACE = "\\s+";
    /**
     * A non-breaking space string. Using this instead of a regular space string (" ") will
     * prevent from applying their normal line-breaking behavior.
     */
    public static final String NBSP = "\u00A0";
    /**
     * A non-breaking space character. Using this instead of a regular space character (' ')
     * will prevent from applying their normal line-breaking behavior.
     */
    public static final char NBSP_CHAR = '\u00A0';
    public static final char NULL_CHAR1 = '\u0000';
    public static final char NULL_CHAR2 = '\0';
    public static final String LINE_FEED = "\r\n";
    public static final String LINE_SEP = System.getProperty("line.separator");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    //public static final String PROJECT_DIR = System.getProperty("user.dir");
    public static final String PROJECT_DIR = FileUtilities.getDirectoryUser();
    public static final String EMPTY_STR = "";
    public static final String LT = "<";
    public static final String GT = ">";
    public static final String AMP = "&";
    public static final String QUAT = "\"";
    public static final String SINGLE_QUAT = "'";
    public static final String ESC_LT = "&lt;";
    public static final String ESC_GT = "&gt;";
    public static final String ESC_AMP = "&amp;";
    public static final String CRLF = "\r\n";

    /*Set of Charset used on java app*/
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_16 = Charset.forName("UTF-16");
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset CP1252 = Charset.forName("Cp1252");
    public static final Charset DEFAULT_ENCODING = Charset.forName(System.getProperty("file.encoding"));
    

    public static Charset toCharset(Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    public static Charset toCharset(String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }

    public static String toString(Collection<String> collection){
        StringBuilder builder = new StringBuilder();
        for(String line: collection){
            builder.append(line).append(LINE_SEPARATOR);
        }
        return builder.toString();
    }
    
    //-------------------------------------------
    // StringIs
    //-------------------------------------------
    /**
     * Uses androids android.util.Patterns.EMAIL_ADDRESS to check if an email address is valid.
     * @param email Address to check
     * @return true if the <code>email</code> is a valid email address.
     */
    public static boolean isEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Uses androids android.telephony.PhoneNumberUtils to check if an phone number is valid.
     * @param number Phone number to check
     * @return true if the <code>number</code> is a valid phone number.
     */
    public static boolean isPhoneNumber(String number) {
        return number != null && Patterns.PHONE.matcher(number).matches();
        //return Patterns.isGlobalPhoneNumber(number);
    }

    /**
     * Uses androids android.util.Patterns.WEB_URL to check if an url is valid.
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
   /* public  static boolean isValidURL(String url) {
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }
*/
    /**
     * Method to  check if an url is valid.
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
    public  static boolean isURLSimple(String url) {
        return url != null && url.matches("^(https?|ftp)://.*$");
    }

    /**
     * Method to check if an url has the valid protocol.
     * @param url Address to check
     * @return true if the url is a valid web address.
     */
    public static boolean isURLWithProtocol(String url) {
        return isURL(url) && (Patterns.Protocol_URL.matcher(url).matches() || url.matches("^(https?|ftp)://.*$"));
    }

    /**
     * Method to check if an url has the valid protocol.
     * @param url Address to check
     * @return true if the url is a valid web address.
     */
    public static boolean isURLWithoutProtocol(String url) {
        return isURL(url) &&
                (Patterns.WEB_URL_NO_PROTOCOL.matcher(url).matches() || Patterns.WEB_URL_NO_PROTOCOL.matcher(url).matches() &&
                 !(Patterns.Protocol_URL.matcher(url).matches() || url.matches("^(https?|ftp)://.*$")));
    }

    /**
     * Method to check if a string is a url address web or not.
     * @param url the string address web.
     * @return if tru is a url address web.
     */
    public static boolean isURL(String url){
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * Method for check if a string rappresent a numeric value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    public static boolean isNumeric(String str) {
        //match a number with optional '-' and decimal.
        str = str.replace(",",".").replace(" ",".");
        return Patterns.IS_NUMERIC.matcher(str).matches();
    }

    /**
     * Method for check if a string rappresent a int value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    public static boolean isInt(String str) {
        return Patterns.IS_INT.matcher(str).matches();
    }

    /**
     * Method for check if a string rappresent a Double value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    public static boolean isDouble(String str) {
        return Patterns.IS_DOUBLE.matcher(str).matches();
    }

    /**
     * Method for check if a string rappresent a Float value.
     * @param str string rapresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    public static boolean isFloat(String str) {
        return Patterns.IS_FLOAT.matcher(str).matches();
    }

    /**
     * Method for check if a string rappresent a Float value.
     * @param str string rappresentative of a number.
     * @return boolean value if the string rappresent a number or not.
     */
    public static boolean isDecimal(String str) {
       /* try {Float.parseFloat(str);return true;
        } catch (NumberFormatException e) {return false;}*/
        return Patterns.IS_DECIMAL.matcher(str).matches();
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    public static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty() ;
    }

    /**
     * Method for check if a String rappresent a line separator.
     * @param str string rappresentative a line separator.
     * @return true if the String rappresent a line separator or not.
     */
    public static boolean isLineSeparator(String str){
        return Patterns.IS_LINE_SEPARATOR().matcher(str).matches();
    }

    /**
     * Method for check if a String rappresent a URI.
     * @param text the Object to check, can be a String or a URI, or a IRI.
     * @return true if the text rappresent a line separator or not.
     */
    public static boolean isURI(Object text){
        if(text instanceof URI)return true;
        else if(isIRI(text)) return true;
        else{
            try {
                //noinspection ResultOfMethodCallIgnored
                URI.create(String.valueOf(text));
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }

    /**
     * Method for check if a String not empty or Null.
     * @param text the Object to check, can be a String.
     * @return true if the text rappresent a String not empty or Null.
     */
    public static boolean isStringNoEmpty(Object text){
        return (text instanceof String && !isNullOrEmpty(String.valueOf(text)));
    }

    /**
     * Method for check if a String empty or Null.
     * @param text the Object to check, can be a String.
     * @return true if the text rappresent a String empty or Null.
     */
    public static boolean isStringEmpty(Object text){
        return (text instanceof String && isNullOrEmpty(toString(text)));
    }

    /**
     * Method for check if a String or URI is empty or Null.
     * @param text the Object to check, can be a String.
     * @return true if the text rappresent a String or URI is empty or Null.
     */
    public static boolean isStringOrUriEmpty(Object text){
        return (
                (text instanceof String && isNullOrEmpty(toString(text))) ||
                        (text instanceof URI && isNullOrEmpty(toString(text)))
        );
    }

    /**
     * Method to check if a String uri is a IRI normalized.
     * http://stackoverflow.com/questions/9419658/normalising-possibly-encoded-uri-strings-in-java
     * @param uri the String to verify.
     * @return if true the String is a valid IRI.
     */
    public static Boolean isIRI(Object uri){
        try {
            if(isString(uri)) {
                org.apache.jena.iri.IRIFactory.uriImplementation().construct(toString(uri));
           /* ArrayList<String> a = new ArrayList<>();
            a.add(iri.getScheme());
            a.add(iri.getRawUserinfo());
            a.add(iri.getRawHost());
            a.add(iri.getRawPath());
            a.add(iri.getRawQuery());
            a.add(iri.getRawFragment());*/
                return true;
            }else return false;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Method for check if a String .
     * @param text the Object to check, can be a String.
     * @return true if the text rappresent a String.
     */
    public  static boolean isString(Object text){
        return (text instanceof String);
    }

    //--------------------------------------------------------
    //StringRegex
    //--------------------------------------------------------
    
    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    public static List<String> findWithRegex(String text,String expression,boolean justFirstResult){
        Pattern pattern = Pattern.compile(expression);
        return findWithRegex(text,pattern,justFirstResult);
    }

    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern of regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    public static List<String> findWithRegex(String text,Pattern pattern,boolean justFirstResult){
        List<String> result =new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            result.add(matcher.group());
            if(justFirstResult)break;
        }
        return result;
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return the first match on the text string.
     */
    public static String findWithRegex(String text,String expression){
        Pattern pat = Pattern.compile(expression);
        return findWithRegex(text,pat);
    }

    /**
     * Checks if the name filters through an including and an excluding
     * regular expression.
     * @param name The <code>String</code> that will be filtered.
     * @param included The regular expressions that needs to succeed.
     * @param excluded The regular expressions that needs to fail.
     * @return true if the name filtered through correctly; or false otherwise.
     */
    public static boolean isMatch(String name, Pattern included, Pattern excluded){
        Pattern[] included_array = null;
        if (included != null)included_array = new Pattern[] {included};
        Pattern[] excluded_array = null;
        if (excluded != null)excluded_array = new Pattern[] {excluded};
        return isMatch(name, included_array, excluded_array);
    }

    /**
     * Checks if the name filters through a series of including and excluding
     * regular expressions.
     * @param name The String that will be filtered.
     * @param included An array of regular expressions that need to succeed
     * @param excluded An array of regular expressions that need to fail
     * @return true if the name filtered through correctly; or false otherwise.
     */
    public static boolean isMatch(String name, Pattern[] included, Pattern[] excluded) {
        if (null == name)return false;
        boolean accepted = false;
        // retain only the includes
        if (null == included) accepted = true;
        else {
            Pattern pattern;
            for (Pattern anIncluded : included) {
                pattern = anIncluded;
                if (pattern != null && pattern.matcher(name).matches()) {
                    accepted = true;
                    break;
                }
            }
        }
        // remove the excludes
        if (accepted && excluded != null){
            Pattern pattern;
            for(Pattern anExcluded : excluded) {
                pattern = anExcluded;
                if (pattern != null && pattern.matcher(name).matches()) {
                    accepted = false;
                    break;
                }
            }
        }
        return accepted;
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern regular expression.
     * @return the first match on the text string.
     */
    public static String findWithRegex(String text,Pattern pattern){
        Matcher matcher = pattern.matcher(text);
        if(matcher.find())return matcher.group(0);
        else return null;
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    public static boolean isMatch(String text,String expression){
        return text != null && Pattern.compile(expression).matcher(text).matches();
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param pattern the pattern of the regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    public static boolean isMatch(String text,Pattern pattern){
        return text != null && pattern.matcher(text).matches();
    }

    /**
     * Metodo che matcha e sostituisce determinati parti di una stringa attraverso le regular expression.
     * @param input stringa di input.
     * @param expression regular expression da applicare.
     * @param replace setta la stringa con cui sostituire il risultato del match.
     * @return il risultato in formato stringa della regular expression.
     */
    public static String regexAndReplace(String input,String expression,String replace){
        String result ="";
        if(replace==null){
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(input);
            while(matcher.find()){
                result = matcher.group();
                if(!isNullOrEmpty(result)){break;}
            }
            return result;
        }else{
            return input.replaceAll(expression, replace);
        }
    }
    
    //---------------------------------------------------
    //StringStream
    //---------------------------------------------------
    
    /**
    * Converts a String to a byte array, taking the
    * eight lower bits of each char as the eight bits of the bytes
    * for the byte array.
    * @param Str the String to convert to byte array.
    * @return the new byte array converted from a String.
    */
    public static byte[] toBytes(String Str){
            char[] NewChr = Str.toCharArray();
            byte[] NewByt = new byte[NewChr.length];
            for (int i=0; i < NewByt.length; i++){
                    int Ci = NewChr[i] & 255;
                    NewByt[i] = (byte) Ci;
            }
            return NewByt;	
    }

    
    /**
     * Converts a byte array to a char array, taking the
     * eight bits of each byte as the lower eight bits of the char.
     * @param bytes the byte array to convert to char array.
     * @return the new char array converted from a byte array.
     */
    public static char[] toChars(byte[] bytes){
            char[] NewChr = new char[bytes.length];
            for (int i=0; i < NewChr.length; i++){
                int Ci = bytes[i] & 255;
                NewChr[i] = (char) Ci;
            }
            return NewChr;	
    }
    
    /**
    * Returns a String with the content of the InputStream.
    * @param is with the InputStream.
    * @return string with the content of the InputStream.
    * @throws IOException error.
    */
   /*public static String toString(InputStream is)throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,UTF_8));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else  return "";    
   }*/

    /**
     * Read an input stream into a string.
     * @param in stream of the resource.
     * @return string of the content of the resource.
     * @throws IOException resource not found.
     */
    public static String toString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

   /**
    * Returns am InputStream with the parameter.
    * @param string string.
    * @param encoding the Carset of the encoding String.
    * @return InputStream with the string value.
    */
   public static InputStream toStream(String string,Charset encoding) {
        return new ByteArrayInputStream(string.getBytes(encoding));
   }

    /**
     * Method to Returns am InputStream with the parameter.
     * @param string string input.
     * @return InputStream with the string value.
     */
    public static InputStream toStream(String string) {
       return toStream(string, StringUtilities.UTF_8);
    }

    /**
     * Method to convert a Reader to a String.
     * href: http://www.baeldung.com/java-convert-reader-to-string.
     * @param reader the Reader object to convert.
     * @return the String content of the Reader.
     */
    public static String toString(Reader reader){
        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        try {
            while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, numCharsRead);
            }
            reader.close();
            return buffer.toString();
        }catch(IOException e){
            //With commons Apache
            try {
                return org.apache.commons.io.IOUtils.toString(reader);
            }catch(IOException e1){
                logger.error(e1.getMessage(),e1);
                return null;
            }
        }
    }

   /**
    * Method to convert a Stream to a Array of Bytes.
    * @param is the InputStream.
    * @return the Array of Bytes.
    */
   public static byte[] toBytes(InputStream is){
        try {
            return new byte[is.available()];
        }catch (IOException e){
           logger.error(e.getMessage(),e);
           return null;
        }
   }

   /**
    * method to convert a array of Bytes to a InputStream.
    * @param bbuf the Array of Bytes.
    * @return the InputStream.
    */
   public static InputStream toStream(byte[] bbuf){
        return  new ByteArrayInputStream(bbuf);
   }
   
   /**
    * Replaces all occurences of char of one type with another 
    * char in a given byte array and returns it.
    * Change is made to the byte array and it is also returned.
    * @param Bytes the byte array to change bytes in.
    * @param Old the char to find, converted to a byte by the
    * lower eight bits, ignoring the higher eight bits.
    * @param New the char to replace all occurences of Old char
    * converted to a byte by the
    * lower eight bits, ignoring the higher eight bits. 
    * @return the changed byte array.
    */
    public static byte[] replace(byte[] Bytes, char Old, char New){
        for(int i = 0; i < Bytes.length; i++){
            int bint = Bytes[i] & 255; //full byte is byte & 255 - converts to int
            if (bint == (Old & 127)) Bytes[i] = (byte)(New & 127); //ASCII is char & 127
        }			
        return Bytes;
    }
    
    /**
     * Replaces one String with another where it occurs of a byte array
     * making a new array due to the possibility of different size.
     * Goes through the array just once, so any new occurances of Old 
     * String that appear due to the New String replacement are not replaced.
     * Does no change to the byte array parameter Bytes.
     * @param Bytes the byte array copy and search through but does no
     * change to this parameter, returning the resulting byte array.
     * @param Old the old String to replace.
     * @param New the new String to replace Old String with.
     * @return the new byte array with replacements done.
     */
    public static byte[] replace(byte[] Bytes, String Old, String New){
        String NewStr = replace(new String(Bytes), Old, New);
        char[] NewChr = NewStr.toCharArray();
        byte[] NewByt = new byte[NewChr.length];
        for (int i=0; i < NewByt.length; i++){
            int Ci = NewChr[i] & 255;
            NewByt[i] = (byte) Ci;
        }
        return NewByt;
    }
   
    /**
     * Used to replace one String segment with
     * another String segment inside a String.
     * Similar to the replace method in String but instead of using
     * char it uses String for replacing old with new.
     * @param Text The String from which is produced the new String with which replacement has occurred.
     * @param Old  The old String that is replaced by the new one in The Text String.
     * @param New  The new String to replace the old String  in the Text String.
     * @return The new String with replacement having occurred.
     */
    public static String replace(String Text, String Old, String New){
        if (Old.length() == 0) return Text;
        StringBuilder buf = new StringBuilder();
        int i, j=0;
        while((i = Text.indexOf(Old, j)) > -1){
                buf.append(Text.substring(j,i)).append(New);
                j = i + Old.length();
        }
        if (j < Text.length())
                buf.append(Text.substring(j));
        return buf.toString();
    }

    //--------------------------------------------------------------------
    // StringKit
    //--------------------------------------------------------------------
    /**
     * Method to clean a html text to a string text.
     * @param stringHtml html string of text.
     * @return string text.
     */
    public static String cleanHTML(String stringHtml){
        return stringHtml.replaceAll("\\r\\n|\\r|\\n", " ").trim();
        //.replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim())
    }

    /**
     * Method to simplify the content of a string for a better vision of the content.
     * @param stringText string of the text.
     * @return string of text simplify.
     */
    public static String toStringInline(String stringText){
        return stringText.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", " ").trim();
        //return stringText.replaceAll("(?m)(^ *| +(?= |$))", "").replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");
    }

    /**
     * Method to Remove/collapse multiple spaces.
     * @param argStr string to remove multiple spaces from.
     * @return String
     */
    public static String cleanSpaces(String argStr) {
        char last = argStr.charAt(0);
        StringBuilder argBuf = new StringBuilder();
        for (int cIdx = 0 ; cIdx < argStr.length(); cIdx++) {
            char ch = argStr.charAt(cIdx);
            if (ch != ' ' || last != ' ') {
                argBuf.append(ch);
                last = ch;
            }
        }
        return argBuf.toString();
    }

    /**
     * Method Read String from InputStream and closes it.
     * @param is input stream.
     * @param encoding charset for the encoding.
     * @return string.
     */
    public static String toString(InputStream is, Charset encoding) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        StringBuilder sb = new StringBuilder(1024);
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
        } catch (IOException io) {   
            logger.error("Failed to read from Stream",io);
        } finally {
            try {
                br.close();
            } catch (IOException ioex) {
                logger.error("Failed to close Streams",ioex);
            }
        }
        return sb.toString();
    }

    /**
     * Returns a String with the content of the InputStream.
     * @param is with the InputStream.
     * @param encoding the Charset of the String.
     * @return string with the content of the InputStream.
     */
    public static String toString(InputStream is,String encoding){
        return toString(is, toCharset(encoding));
    }

    /**
     * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
     * escaped sequence for characters outside of ASCII.
     * It is equivalent to: native2ascii -encoding utf-8
     * @param stringUTF8 string encoding utf8
     * @return ASCII string encoding ascii.
     */
    public static String toASCII(String stringUTF8) {
        if (stringUTF8==null) return null;
        Reader reader = new StringReader(toHexString(stringUTF8.getBytes(UTF_8)));
        return unicodeEscape(reader.toString());

    }

    private static final char[] hexChar = 
    {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    /**
     * Method for convert a string UTF-8 to HEX
     * @param s string of text you want to convert to HEX
     * @return the text in HEX encoding
     */
    public static String unicodeEscape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >> 7) > 0) {
                sb.append("\\u");
                sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character for the left-most 4-bits
                sb.append(hexChar[(c >> 8) & 0xF]); // hex for the second group of 4-bits from the left
                sb.append(hexChar[(c >> 4) & 0xF]); // hex for the third group
                sb.append(hexChar[c & 0xF]); // hex for the last group, e.home., the right most 4-bits
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Reads file with unicode escaped characters and write them out to
     * stdout in UTF-8
     * This utility is equivalent to: native2ascii -reverse -encoding utf-8
     * @param stringASCII string encoding ascii.
     * @return UTF8 string encoding utf8.
     */
    public static String toUTF8(String stringASCII) {
        if (stringASCII == null) return null;
        Reader reader = new StringReader(toHexString(stringASCII.getBytes(US_ASCII)));
        String line = convertUnicodeEscapeToASCII(reader.toString());
        byte[] bytes = line.getBytes(UTF_8);
        return toHexString(bytes);
    }

    public static String encodeTo(String text,Charset charset){
        return new String(text.getBytes(charset), charset);
    }

    enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}
    /**
     *  convert unicode escapes back to char.
     * @param s string to convert to ascii.
     * @return string ascii.
     */
    public static String convertUnicodeEscapeToASCII(String s) {
        char[] out = new char[s.length()];
        ParseState state = ParseState.NORMAL;
        int j = 0, k = 0, unicode = 0;
        char c = ' ';
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (state == ParseState.ESCAPE) {
                if (c == 'u') {
                    state = ParseState.UNICODE_ESCAPE;
                    unicode = 0;
                }
                else { // we don't care about other escapes
                    out[j++] = '\\';
                    out[j++] = c;
                    state = ParseState.NORMAL;
                }
            }
            else if (state == ParseState.UNICODE_ESCAPE) {
                if ((c >= '0') && (c <= '9')) {
                    unicode = (unicode << 4) + c - '0';
                }
                else if ((c >= 'a') && (c <= 'f')) {
                    unicode = (unicode << 4) + 10 + c - 'a';
                }
                else if ((c >= 'A') && (c <= 'F')) {
                    unicode = (unicode << 4) + 10 + c - 'A';
                }
                else {
                    throw new IllegalArgumentException("Malformed unicode escape");
                }
                k++;
                if (k == 4) {
                    out[j++] = (char) unicode;
                    k = 0;
                    state = ParseState.NORMAL;
                }
            }
            else if (c == '\\') {
                state = ParseState.ESCAPE;
            }
            else {
                out[j++] = c;
            }
        }//for
        if (state == ParseState.ESCAPE) {
            out[j++] = c;
        }
        return new String(out, 0, j);
    }



    /**
     *Creating a random UUID (Universally unique identifier).
     * @return string asuuid.
     */
    public static String createUUID(){ return  java.util.UUID.randomUUID().toString(); }
    
    /**
     * Metodo che converte una stringa a un'oggetto UUID.
     * @param uuid string uuid.
     * @return java.util.UUID.
     */
    public static java.util.UUID toUUID(String uuid){return java.util.UUID.fromString(uuid); }
    
     /**
     * Metodo che converte una stringa a un'oggetto UUID.
     * @param uuid Byte array of the uuid.
     * @return java.util.UUID.
     */
    public static java.util.UUID toUUID(byte[] uuid){return java.util.UUID.nameUUIDFromBytes(uuid); }

    /**
     * Methohs remove the symbol if exists in the first and last character of the string.
     * OLD_NAME: removeFirstAndLast;
     * @param stringToUpdate string of input.
     * @param symbol symbol to check.
     * @return the string update.
     */
    public static String removeFirstAndLast(String stringToUpdate, String symbol) {
        if (!isNullOrEmpty(stringToUpdate)) {
            stringToUpdate = stringToUpdate.replaceAll("(\\" + symbol + ")\\1+", symbol);
            if (stringToUpdate.substring(0, 1).contains(symbol)) {
                stringToUpdate = stringToUpdate.substring(1, stringToUpdate.length());
            }
            if (stringToUpdate.substring(stringToUpdate.length() - 1, stringToUpdate.length()).contains(symbol)) {
                stringToUpdate = stringToUpdate.substring(0, stringToUpdate.length() - 1);
            }
        }
        return stringToUpdate;
    }

    /**
     * Setta a null se verifica che la stringa non è
     * nulla, non è vuota e non è composta da soli spaceToken (white space).
     * @param s stringa di input.
     * @return  il valore della stringa se null o come è arrivata.
     */
    public static String setNullForEmptyString(String s){
        if(isNullOrEmpty(s)){return null;}
        else{return s;}
    } //setNullforEmptyString

    /**
     * Metodo che "taglia" la descrizione dell'edificio al minimo indispensabile.
     * @param content stringa del contenuto da tokenizzare.
     * @param symbol simbolo del tokenizer.
     * @return la stringa tokenizzata.
     */
    public static String getTheFirstTokenOfATokenizer(String content,String symbol){
        StringTokenizer st = new StringTokenizer(content, symbol);
        while (st.hasMoreTokens()) {
            content = st.nextToken();
            if(!isNullOrEmpty(content)){
                break;
            }
        }
        return content;
    }

    /**
     * Method for convert a strinG to a OutputStream and print to a file.
     * @param content string to print on the file.
     * @param outputPathFileName file where i put the stream.
     * @return the File.
     */
    public static File copyStringToFile(String content, File outputPathFileName) {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputPathFileName, true)))) {
            out.print(content);
            out.flush();
            //out.close();
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return outputPathFileName;
    }

    /**
     * Method for read the input on the console.
     * @return string print to the console.
     * @throws IOException throw error if any occurred.
     */
    public static String readConsole() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //System.out.print("Enter String");
        String input;
        do {
            input = br.readLine();
            //System.out.print("Enter Integer:");
        }while(br.read() > 0);
        return input;
    }


    /**
     * read from a file and append into a StringBuilder every new line.
     * @param filename string path tot the file.
     * @return string content of file.
     */
    public static String readFile(File filename) {
        String readFile = "";
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                StringBuilder sbFile = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sbFile.append(line);// append the line of the file
                    sbFile.append('\n');// separate the line with a '@'
                    line = br.readLine();// read the next line of the file
                }
                readFile = sbFile.toString();// this string contains the character sequence
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return  readFile;
    }


    /**
     * Method to  count the elements characters of a string.
     * @param text the String text to parse.
     * @return string of int where the first element is the number of words,the second is the number of characters
     * and the third is the number of lines.
     */
    public static int[] countElement(String text){
        try {
            int i=0,j=0,k=0;
            BufferedReader br = new BufferedReader(new InputStreamReader(toStream(text)));
            String s;
            s = br.readLine();//Enter File Name:
            br=new BufferedReader(new FileReader(s));
            while((s=br.readLine())!=null)
            {
                k++;
                StringTokenizer st=new StringTokenizer(s," .,:;!?");
                while(st.hasMoreTokens())
                {
                    i++;
                    s=st.nextToken();
                    j+=s.length();
                }
            }   br.close();
            return new int[]{i,j,k}; //Number of Words:,Number of Characters:,Number of Lines:
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            return new int[3];
        }
    }


    /**
     * Method to Serializing an Object.
     * @param object object.
     * @param nameTempSer string serializable.
     * @param <T> generic type.
     */
    public static <T> void toSerializable(T object,String nameTempSer){
        try{
            try (FileOutputStream fileOut = new FileOutputStream("/tmp/"+nameTempSer+".ser");
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(object);
            }
            logger.info("Serialized data is saved in /tmp/" + nameTempSer + ".ser");
        }catch(IOException i){
            logger.error(i.getMessage(),i);
        }
    }

    /**
     * Method to Deserializing an Object.
     * @param nameTempSer string serializable.
     * @param <T> generic type.
     * @return object serializable.
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String nameTempSer){
        T object ;
        try{
            try (FileInputStream fileIn = new FileInputStream("/tmp/"+nameTempSer+".ser");
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                 object = (T) in.readObject();
            }
        }catch(IOException|ClassNotFoundException i){
            logger.error(i.getMessage(),i);
            return null;
        }
        return object;
    }

    /**
     * Method to convert a object by the clazz is equivalent to public T cast(Object o)
     * but more powerful.
     * @param objectToCast object to cast.
     * @param clazz a generic class
     * @param <T> generic variable.
     * @return object casted to soefic vairable.
     */
    @SuppressWarnings("unchecked")
    public static <T> T toInstanceOfObject(Object objectToCast,Class<T> clazz) {
        try {
            if (clazz.isInstance(objectToCast)) {
                return clazz.cast(objectToCast);
            } else {
                return (T) objectToCast;
            }
        } catch (ClassCastException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to convert a POJO java object to XML string.
     * @param object object.
     * @param clazz class.
     * @param <T> generic type.
     * @return string.
     */
    public static <T> String toXml(T object, Class<T> clazz){
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException ex) {
            logger.error(ex.getMessage(),ex);
            return null;
        }
    }

    /**
     * Method to convert a XML string to POJO java object.
     * @param xmlStringData string xml.
     * @param clazz class.
     * @param <T> generic type.
     * @return object T.
     */
    @SuppressWarnings("unchecked")
    public static <T> T toPojo(String xmlStringData, Class<T> clazz){
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            StringReader reader = new StringReader(xmlStringData);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException ex) {
            logger.error(ex.getMessage(),ex);
            return null;
        }
    }

    /**
     * Method to convert Strng to char.
     * @param string string.
     * @return Array of char.
     */
    public static char[] toChars(String string){
        return string.toCharArray();
    }

    /**
     * Method to add a protocl to a strin to match a url.
     * @param url the string of the url.
     * @return he string of the url with protocol.
     */
    public static String toURLWithProtocol(String url) {
        if(isURL(url)) {
            if (!(isURLWithProtocol(url))) {
                url = "http://" + url;
                if (isURL(url)) return url;
                else{
                   logger.warn("After add the protocol this is not a URL!");
                   return null; 
                }
            }
            return url;
        }else{
            logger.warn("Before add the protocol this is not a URL!");
            return null;
        }
       
    }

    /**
     * Method to convert a String rapperesent a URi toa URI Objwect.
     * @param uri the String rappresent a URI.
     * @return the URI Object.
     */
    public static URI toURI(String uri){
        try {
            return URI.create(uri);
        }catch(Exception e){
            logger.warn("Can\'t convert the String:"+uri+" to a URI Object:"+e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to normalize a URI.
     * @param inputUri the URI String of input.
     * @return the URI Normalized.
     */
    public static String normalizeUri(String inputUri) {
        boolean foundIssue = false;
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < inputUri.length(); i++){
            char value = inputUri.charAt(i);
            if(value == ' '){
                if(!foundIssue){
                    foundIssue = true;
                    sb.append(inputUri.substring(0, i));
                }
                //continue;
            }else if(value == ',' || value == '`' || value == '\'' ){
                if(!foundIssue) {
                    foundIssue = true;
                    sb.append(inputUri.substring(0, i));
                }
                else sb.append('_');

            }else if(foundIssue) sb.append(value);
        }
        if(foundIssue) return sb.toString();
        else return inputUri;
    }

    /**
     * Method to convert a string to a  "MD5", "SHA-1", "SHA-256" hash.
     * suitable for small-sized message
     * @param message the string to convert.
     * @param algorithm the type has algorithm "MD5", "SHA-1", "SHA-256".
     * @return the string of the hash code of the inpu string.
     */
    private static String hashString(String message, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes(UTF_8));
            /*
            digest.update(hashedBytes);
            byte[] hashedBytes = digest.digest();
            */
            return toHexString(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not generate hash from String", ex);  
            return null;
        }
    }

    /**
     * Method to convert a string to a  "MD5", "SHA-1", "SHA-256" hash.
     * suitable for small-sized message
     * @param message the string to convert.
     * @param algorithm the type has algorithm "MD5", "SHA-1", "SHA-256".
     * @return the string of the hash code of the inpu string.
     */
    private static String hashString(String message, Charset algorithm) {
        return hashString(message,algorithm.name());
    }

    /**
     * Method to convert a array of bytes to a string.
     * @param arrayBytes array Collection of bytes.
     * @return the string of the hash.
     */
    public static String toString(byte[] arrayBytes){
        /*
         * Converts a byte array to a String, taking the
         * eight bits of each byte as the lower eight bits of the chars
         * in the String.
         * @param bytes the byte array to convert to char array.
         * @return the new String converted from a byte array.
         */
         //return new String(toChars(bytes));
        StringBuilder sb = new StringBuilder(2*arrayBytes.length);
        for (byte b : arrayBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * Method to convert a array of bytes to a string.
     * @param arrayBytes array Collection of bytes.
     * @return the string of the hash.
     */
    public static String toHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    /**
     * Spring framework also provides overloaded md5 methods. You can pass input
     * as String or byte array and Spring can return hash or digest either as byte
     * array or Hex String. Here we are passing String as input and getting
     * MD5 hash as hex String.
     * @param arrayBytes array Collection of bytes.
     * @return the string of the hash.
     */
    public static String toMD5(byte[] arrayBytes){
        return org.springframework.util.DigestUtils.md5DigestAsHex(arrayBytes);
    }

    /*
     * Apache commons code provides many overloaded methods to generate md5 hash. It contains
     * md5 method which can accept String, byte[] or InputStream and can return hash as 16 element byte
     * array or 32 character hex String.
     */
  /*  public static String convertByteArrayToStringWithApacheCommonCodec(byte[] arrayBytes){
        return DigestUtils.md5Hex(arrayBytes);

    }*/

    /**
     * Method to convert string to MD5 hash.
     * @param message string to codify to hash.
     * @return the string of the hash.
     */
    public static String toMD5(String message) {
        return hashString(message, "MD5");
    }

    /**
     * Method to convert string to SHA-1 hash.
     * @param message string to codify to hash.
     * @return the string of the hash.
     */
    public static String toSHA1(String message) {
        return hashString(message, "SHA-1");
    }

    /**
     * Method to convert string to SHA-256 hash.
     * @param message string to codify to hash.
     * @return the string of the hash.
     */
    public static String toSHA256(String message) {
        return hashString(message, "SHA-256");
    }

    /**
     * Method to convert a Integer to a int primitive.
     * @param integer the integer to convert.
     * @return the int primitive of the integer object.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static int toInt(Integer integer){
        return integer.intValue();
    }

    /**
     * Method to convert a int primitive to the Integer object .
     * @param numInt the int primitive.
     * @return the the integer object of the int primitive .
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Integer toInteger(int numInt){
        return Integer.valueOf(numInt);
    }

    /**
     * Method to convert a int primitive to the Integer object .
     * @param numInt the int primitive.
     * @return the the double object of the int primitive .
     */
    public static Double toDouble(int numInt){ return (double) numInt;}

    /**
     * Method to convert a int primitive to the Integer object .
     * @param numInt the int primitive.
     * @return the the double object of the int primitive .
     */
    public static Double toDouble(Integer numInt){ return (double) numInt;}

    /**
     * Method to convert a String to a Integer.
     * @param numericText the numeric text string.
     * @return the integer object.
     */
    public static Integer toInteger(String numericText){
        if(isNumeric(numericText)){
            return Integer.parseInt(numericText);
        }else{
            logger.warn("The string text:"+numericText+" is not a number!!!");
            return null;
        }
    }

    /**
     * Method to convert a String to a int.
     * @param numericText the numeric text string.
     * @return the int primitive.
     */
    public static int toInt(String numericText){
        if(isNumeric(numericText)){
            return toInt(Integer.parseInt(numericText));
        }else{
            logger.warn("The string text:"+numericText+" is not a number!!!");
            return 0;
        }
    }

    /**
     * Method to convert a String URI to a Sting IRI.
     * @param uriResource the String URI to the Resource.
     * @return the String IRI.
     */
    public static String toIri(String uriResource){
        return org.apache.jena.iri.IRIFactory.uriImplementation().construct(uriResource).toString();
    }

    /**
     * Method to convert a object to a String.
     * @param object the Object to convert.
     * @return the String of the object.
     */
    public static String toString(Object object){
        if(object instanceof URL) return object.toString();
        if(object instanceof URI) return object.toString();
        //TODO add all the other constructor.
        return String.valueOf(object);
    }

    /**
     * Method to convert a URI os String reference to a resource to a good ID.
     * NOTE: URI string: scheme://authority/path?query#fragment
     * @param uriResource the String or URI reference Resource.
     * @return the String id of the Resource.
     */
    public static String toId(String uriResource){
        return URI.create(uriResource.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", "_").trim()).toString();
    }

    /**
     * Method to cnvert a OBject to a Integer.
     * @param object the Object to convert.
     * @return the Int of the object.
     */
    public static int toInt(Object object){
        return Integer.parseInt((String)object);
    }

    //-------------------------------------------------
    // Method need commons lang 3
    //-------------------------------------------------
    public static String generateMD5Token(int lengthToken){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        StringBuilder hexString = new StringBuilder();
        byte[] data = md.digest(
                org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(lengthToken).getBytes());
        for (byte aData : data) {
            hexString.append(Integer.toHexString((aData >> 4) & 0x0F));
            hexString.append(Integer.toHexString(aData & 0x0F));
        }
        return hexString.toString();
    }


    //-----------------------------------------------------
    // NEW METHOD 4
    //-----------------------------------------------------

    /**
     * Transforms a provided <code>String</code> object into a new string,
     * using the mapping that are provided through the supplied encoding table.
     * @param source The string that has to be transformed into a valid string,
     * using the mappings that are provided through the supplied encoding table.
     * @param encodingTable A Map object containing the mappings to
     * transform characters into valid entities. The keys of this map should be
     * Character objects and the values String objects.
     * @return The encoded String object.
     */
    private static String encode(String source, HashMap<Character,String> encodingTable) {
        if (null == source) return null;
        if (null == encodingTable)return source;

        StringBuffer encoded_string = null;
        char[] string_to_encode_array = source.toCharArray();
        int last_match = -1;
        int difference; // = 0;
        for (int i = 0; i < string_to_encode_array.length; i++) {
            char char_to_encode = string_to_encode_array[i];
            if (encodingTable.containsKey(char_to_encode)) {
                if (null == encoded_string) {
                    encoded_string = new StringBuffer(source.length());
                }
                difference = i - (last_match + 1);
                if (difference > 0) {
                    encoded_string.append(string_to_encode_array, last_match + 1, difference);
                }
                encoded_string.append(encodingTable.get(char_to_encode));
                last_match = i;
            }
        }
        if (null == encoded_string)return source;
        else{
            difference = string_to_encode_array.length - (last_match + 1);
            if (difference > 0) {
                encoded_string.append(string_to_encode_array, last_match + 1, difference);
            }
            return encoded_string.toString();
        }
    }

    /**
     * Transforms a provided <code>String</code> object into a new string,
     * containing only valid Html characters.
     * @param source The string that has to be transformed into a valid Html string.
     * @return The encoded String object.
     */
    public static String encodeToHtml(String source){
        return encode(source, mHtmlEncodeMap);
    }

    /**
     * Method to check the encoding charset with JDK code.
     * href: http://www.java2s.com/Code/Java/I18N/Howtoautodetectafilesencoding.htm
     * @param text the String to check.
     * @return the Encoding Charset.
     */
    public static Charset getCharset(String text){
        //with Apache Tika...
        try {
            org.apache.tika.detect.AutoDetectReader adr =
                    new org.apache.tika.detect.AutoDetectReader(new ByteArrayInputStream(text.getBytes()));
            return adr.getCharset();
        } catch (IOException|TikaException e) {
            //wihtout any external lirary....
            String[] charsetsToBeTested = {"UTF-8", "windows-1253", "ISO-8859-7"};
            return detectCharset(new ByteArrayInputStream(text.getBytes()), charsetsToBeTested);
        }
    }

    //DETECT CHARSET WITHOUT ANY EXTERNAL LIBRARY
    private static Charset detectCharset(InputStream inputStream, String[] charsets) {
        Charset charset = null;
        for (String charsetName : charsets) {
            charset = detectCharset(inputStream, Charset.forName(charsetName));
            if (charset != null) break;
        }
        return charset;
    }

    private static Charset detectCharset(InputStream inputStream, Charset charset) {
        try {
            BufferedInputStream input = new BufferedInputStream(inputStream);
            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();
            byte[] buffer = new byte[512];
            boolean identified = false;
            while ((input.read(buffer) != -1) && (!identified)) {
                identified = identify(buffer, decoder);
            }
            input.close();
            if (identified) return charset;
            else  return null;
        } catch (Exception e) {return null;}
    }

    private static  boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {return false;}
        return true;
    }


    //------------------------------------------------------------------------------------------------
    // MAP
    //-------------------------------------------------------------------------------------------------

    private static final HashMap<Character,String> mHtmlEncodeMap = new HashMap<>();

    static
    {
        // Html encoding mapping according to the HTML 4.0 spec
        // http://www.w3.org/TR/REC-html40/sgml/entities.html

        // Special characters for HTML
        mHtmlEncodeMap.put('\u0026', "&amp;");
        mHtmlEncodeMap.put('\u003C', "&lt;");
        mHtmlEncodeMap.put('\u003E', "&gt;");
        mHtmlEncodeMap.put('\u0022', "&quot;");

        mHtmlEncodeMap.put('\u0152', "&OElig;");
        mHtmlEncodeMap.put('\u0153', "&oelig;");
        mHtmlEncodeMap.put('\u0160', "&Scaron;");
        mHtmlEncodeMap.put('\u0161', "&scaron;");
        mHtmlEncodeMap.put('\u0178', "&Yuml;");
        mHtmlEncodeMap.put('\u02C6', "&circ;");
        mHtmlEncodeMap.put('\u02DC', "&tilde;");
        mHtmlEncodeMap.put('\u2002', "&ensp;");
        mHtmlEncodeMap.put('\u2003', "&emsp;");
        mHtmlEncodeMap.put('\u2009', "&thinsp;");
        mHtmlEncodeMap.put('\u200C', "&zwnj;");
        mHtmlEncodeMap.put('\u200D', "&zwj;");
        mHtmlEncodeMap.put('\u200E', "&lrm;");
        mHtmlEncodeMap.put('\u200F', "&rlm;");
        mHtmlEncodeMap.put('\u2013', "&ndash;");
        mHtmlEncodeMap.put('\u2014', "&mdash;");
        mHtmlEncodeMap.put('\u2018', "&lsquo;");
        mHtmlEncodeMap.put('\u2019', "&rsquo;");
        mHtmlEncodeMap.put('\u201A', "&sbquo;");
        mHtmlEncodeMap.put('\u201C', "&ldquo;");
        mHtmlEncodeMap.put('\u201D', "&rdquo;");
        mHtmlEncodeMap.put('\u201E', "&bdquo;");
        mHtmlEncodeMap.put('\u2020', "&dagger;");
        mHtmlEncodeMap.put('\u2021', "&Dagger;");
        mHtmlEncodeMap.put('\u2030', "&permil;");
        mHtmlEncodeMap.put('\u2039', "&lsaquo;");
        mHtmlEncodeMap.put('\u203A', "&rsaquo;");
        mHtmlEncodeMap.put('\u20AC', "&euro;");

        // Character entity references for ISO 8859-1 characters
        mHtmlEncodeMap.put('\u00A0', "&nbsp;");
        mHtmlEncodeMap.put('\u00A1', "&iexcl;");
        mHtmlEncodeMap.put('\u00A2', "&cent;");
        mHtmlEncodeMap.put('\u00A3', "&pound;");
        mHtmlEncodeMap.put('\u00A4', "&curren;");
        mHtmlEncodeMap.put('\u00A5', "&yen;");
        mHtmlEncodeMap.put('\u00A6', "&brvbar;");
        mHtmlEncodeMap.put('\u00A7', "&sect;");
        mHtmlEncodeMap.put('\u00A8', "&uml;");
        mHtmlEncodeMap.put('\u00A9', "&copy;");
        mHtmlEncodeMap.put('\u00AA', "&ordf;");
        mHtmlEncodeMap.put('\u00AB', "&laquo;");
        mHtmlEncodeMap.put('\u00AC', "&not;");
        mHtmlEncodeMap.put('\u00AD', "&shy;");
        mHtmlEncodeMap.put('\u00AE', "&reg;");
        mHtmlEncodeMap.put('\u00AF', "&macr;");
        mHtmlEncodeMap.put('\u00B0', "&deg;");
        mHtmlEncodeMap.put('\u00B1', "&plusmn;");
        mHtmlEncodeMap.put('\u00B2', "&sup2;");
        mHtmlEncodeMap.put('\u00B3', "&sup3;");
        mHtmlEncodeMap.put('\u00B4', "&acute;");
        mHtmlEncodeMap.put('\u00B5', "&micro;");
        mHtmlEncodeMap.put('\u00B6', "&para;");
        mHtmlEncodeMap.put('\u00B7', "&middot;");
        mHtmlEncodeMap.put('\u00B8', "&cedil;");
        mHtmlEncodeMap.put('\u00B9', "&sup1;");
        mHtmlEncodeMap.put('\u00BA', "&ordm;");
        mHtmlEncodeMap.put('\u00BB', "&raquo;");
        mHtmlEncodeMap.put('\u00BC', "&frac14;");
        mHtmlEncodeMap.put('\u00BD', "&frac12;");
        mHtmlEncodeMap.put('\u00BE', "&frac34;");
        mHtmlEncodeMap.put('\u00BF', "&iquest;");
        mHtmlEncodeMap.put('\u00C0', "&Agrave;");
        mHtmlEncodeMap.put('\u00C1', "&Aacute;");
        mHtmlEncodeMap.put('\u00C2', "&Acirc;");
        mHtmlEncodeMap.put('\u00C3', "&Atilde;");
        mHtmlEncodeMap.put('\u00C4', "&Auml;");
        mHtmlEncodeMap.put('\u00C5', "&Aring;");
        mHtmlEncodeMap.put('\u00C6', "&AElig;");
        mHtmlEncodeMap.put('\u00C7', "&Ccedil;");
        mHtmlEncodeMap.put('\u00C8', "&Egrave;");
        mHtmlEncodeMap.put('\u00C9', "&Eacute;");
        mHtmlEncodeMap.put('\u00CA', "&Ecirc;");
        mHtmlEncodeMap.put('\u00CB', "&Euml;");
        mHtmlEncodeMap.put('\u00CC', "&Igrave;");
        mHtmlEncodeMap.put('\u00CD', "&Iacute;");
        mHtmlEncodeMap.put('\u00CE', "&Icirc;");
        mHtmlEncodeMap.put('\u00CF', "&Iuml;");
        mHtmlEncodeMap.put('\u00D0', "&ETH;");
        mHtmlEncodeMap.put('\u00D1', "&Ntilde;");
        mHtmlEncodeMap.put('\u00D2', "&Ograve;");
        mHtmlEncodeMap.put('\u00D3', "&Oacute;");
        mHtmlEncodeMap.put('\u00D4', "&Ocirc;");
        mHtmlEncodeMap.put('\u00D5', "&Otilde;");
        mHtmlEncodeMap.put('\u00D6', "&Ouml;");
        mHtmlEncodeMap.put('\u00D7', "&times;");
        mHtmlEncodeMap.put('\u00D8', "&Oslash;");
        mHtmlEncodeMap.put('\u00D9', "&Ugrave;");
        mHtmlEncodeMap.put('\u00DA', "&Uacute;");
        mHtmlEncodeMap.put('\u00DB', "&Ucirc;");
        mHtmlEncodeMap.put('\u00DC', "&Uuml;");
        mHtmlEncodeMap.put('\u00DD', "&Yacute;");
        mHtmlEncodeMap.put('\u00DE', "&THORN;");
        mHtmlEncodeMap.put('\u00DF', "&szlig;");
        mHtmlEncodeMap.put('\u00E0', "&agrave;");
        mHtmlEncodeMap.put('\u00E1', "&aacute;");
        mHtmlEncodeMap.put('\u00E2', "&acirc;");
        mHtmlEncodeMap.put('\u00E3', "&atilde;");
        mHtmlEncodeMap.put('\u00E4', "&auml;");
        mHtmlEncodeMap.put('\u00E5', "&aring;");
        mHtmlEncodeMap.put('\u00E6', "&aelig;");
        mHtmlEncodeMap.put('\u00E7', "&ccedil;");
        mHtmlEncodeMap.put('\u00E8', "&egrave;");
        mHtmlEncodeMap.put('\u00E9', "&eacute;");
        mHtmlEncodeMap.put('\u00EA', "&ecirc;");
        mHtmlEncodeMap.put('\u00EB', "&euml;");
        mHtmlEncodeMap.put('\u00EC', "&igrave;");
        mHtmlEncodeMap.put('\u00ED', "&iacute;");
        mHtmlEncodeMap.put('\u00EE', "&icirc;");
        mHtmlEncodeMap.put('\u00EF', "&iuml;");
        mHtmlEncodeMap.put('\u00F0', "&eth;");
        mHtmlEncodeMap.put('\u00F1', "&ntilde;");
        mHtmlEncodeMap.put('\u00F2', "&ograve;");
        mHtmlEncodeMap.put('\u00F3', "&oacute;");
        mHtmlEncodeMap.put('\u00F4', "&ocirc;");
        mHtmlEncodeMap.put('\u00F5', "&otilde;");
        mHtmlEncodeMap.put('\u00F6', "&ouml;");
        mHtmlEncodeMap.put('\u00F7', "&divide;");
        mHtmlEncodeMap.put('\u00F8', "&oslash;");
        mHtmlEncodeMap.put('\u00F9', "&ugrave;");
        mHtmlEncodeMap.put('\u00FA', "&uacute;");
        mHtmlEncodeMap.put('\u00FB', "&ucirc;");
        mHtmlEncodeMap.put('\u00FC', "&uuml;");
        mHtmlEncodeMap.put('\u00FD', "&yacute;");
        mHtmlEncodeMap.put('\u00FE', "&thorn;");
        mHtmlEncodeMap.put('\u00FF', "&yuml;");

        // Mathematical, Greek and Symbolic characters for HTML
        mHtmlEncodeMap.put('\u0192', "&fnof;");
        mHtmlEncodeMap.put('\u0391', "&Alpha;");
        mHtmlEncodeMap.put('\u0392', "&Beta;");
        mHtmlEncodeMap.put('\u0393', "&Gamma;");
        mHtmlEncodeMap.put('\u0394', "&Delta;");
        mHtmlEncodeMap.put('\u0395', "&Epsilon;");
        mHtmlEncodeMap.put('\u0396', "&Zeta;");
        mHtmlEncodeMap.put('\u0397', "&Eta;");
        mHtmlEncodeMap.put('\u0398', "&Theta;");
        mHtmlEncodeMap.put('\u0399', "&Iota;");
        mHtmlEncodeMap.put('\u039A', "&Kappa;");
        mHtmlEncodeMap.put('\u039B', "&Lambda;");
        mHtmlEncodeMap.put('\u039C', "&Mu;");
        mHtmlEncodeMap.put('\u039D', "&Nu;");
        mHtmlEncodeMap.put('\u039E', "&Xi;");
        mHtmlEncodeMap.put('\u039F', "&Omicron;");
        mHtmlEncodeMap.put('\u03A0', "&Pi;");
        mHtmlEncodeMap.put('\u03A1', "&Rho;");
        mHtmlEncodeMap.put('\u03A3', "&Sigma;");
        mHtmlEncodeMap.put('\u03A4', "&Tau;");
        mHtmlEncodeMap.put('\u03A5', "&Upsilon;");
        mHtmlEncodeMap.put('\u03A6', "&Phi;");
        mHtmlEncodeMap.put('\u03A7', "&Chi;");
        mHtmlEncodeMap.put('\u03A8', "&Psi;");
        mHtmlEncodeMap.put('\u03A9', "&Omega;");
        mHtmlEncodeMap.put('\u03B1', "&alpha;");
        mHtmlEncodeMap.put('\u03B2', "&beta;");
        mHtmlEncodeMap.put('\u03B3', "&gamma;");
        mHtmlEncodeMap.put('\u03B4', "&delta;");
        mHtmlEncodeMap.put('\u03B5', "&epsilon;");
        mHtmlEncodeMap.put('\u03B6', "&zeta;");
        mHtmlEncodeMap.put('\u03B7', "&eta;");
        mHtmlEncodeMap.put('\u03B8', "&theta;");
        mHtmlEncodeMap.put('\u03B9', "&iota;");
        mHtmlEncodeMap.put('\u03BA', "&kappa;");
        mHtmlEncodeMap.put('\u03BB', "&lambda;");
        mHtmlEncodeMap.put('\u03BC', "&mu;");
        mHtmlEncodeMap.put('\u03BD', "&nu;");
        mHtmlEncodeMap.put('\u03BE', "&xi;");
        mHtmlEncodeMap.put('\u03BF', "&omicron;");
        mHtmlEncodeMap.put('\u03C0', "&pi;");
        mHtmlEncodeMap.put('\u03C1', "&rho;");
        mHtmlEncodeMap.put('\u03C2', "&sigmaf;");
        mHtmlEncodeMap.put('\u03C3', "&sigma;");
        mHtmlEncodeMap.put('\u03C4', "&tau;");
        mHtmlEncodeMap.put('\u03C5', "&upsilon;");
        mHtmlEncodeMap.put('\u03C6', "&phi;");
        mHtmlEncodeMap.put('\u03C7', "&chi;");
        mHtmlEncodeMap.put('\u03C8', "&psi;");
        mHtmlEncodeMap.put('\u03C9', "&omega;");
        mHtmlEncodeMap.put('\u03D1', "&thetasym;");
        mHtmlEncodeMap.put('\u03D2', "&upsih;");
        mHtmlEncodeMap.put('\u03D6', "&piv;");
        mHtmlEncodeMap.put('\u2022', "&bull;");
        mHtmlEncodeMap.put('\u2026', "&hellip;");
        mHtmlEncodeMap.put('\u2032', "&prime;");
        mHtmlEncodeMap.put('\u2033', "&Prime;");
        mHtmlEncodeMap.put('\u203E', "&oline;");
        mHtmlEncodeMap.put('\u2044', "&frasl;");
        mHtmlEncodeMap.put('\u2118', "&weierp;");
        mHtmlEncodeMap.put('\u2111', "&image;");
        mHtmlEncodeMap.put('\u211C', "&real;");
        mHtmlEncodeMap.put('\u2122', "&trade;");
        mHtmlEncodeMap.put('\u2135', "&alefsym;");
        mHtmlEncodeMap.put('\u2190', "&larr;");
        mHtmlEncodeMap.put('\u2191', "&uarr;");
        mHtmlEncodeMap.put('\u2192', "&rarr;");
        mHtmlEncodeMap.put('\u2193', "&darr;");
        mHtmlEncodeMap.put('\u2194', "&harr;");
        mHtmlEncodeMap.put('\u21B5', "&crarr;");
        mHtmlEncodeMap.put('\u21D0', "&lArr;");
        mHtmlEncodeMap.put('\u21D1', "&uArr;");
        mHtmlEncodeMap.put('\u21D2', "&rArr;");
        mHtmlEncodeMap.put('\u21D3', "&dArr;");
        mHtmlEncodeMap.put('\u21D4', "&hArr;");
        mHtmlEncodeMap.put('\u2200', "&forall;");
        mHtmlEncodeMap.put('\u2202', "&part;");
        mHtmlEncodeMap.put('\u2203', "&exist;");
        mHtmlEncodeMap.put('\u2205', "&empty;");
        mHtmlEncodeMap.put('\u2207', "&nabla;");
        mHtmlEncodeMap.put('\u2208', "&isin;");
        mHtmlEncodeMap.put('\u2209', "&notin;");
        mHtmlEncodeMap.put('\u220B', "&ni;");
        mHtmlEncodeMap.put('\u220F', "&prod;");
        mHtmlEncodeMap.put('\u2211', "&sum;");
        mHtmlEncodeMap.put('\u2212', "&minus;");
        mHtmlEncodeMap.put('\u2217', "&lowast;");
        mHtmlEncodeMap.put('\u221A', "&radic;");
        mHtmlEncodeMap.put('\u221D', "&prop;");
        mHtmlEncodeMap.put('\u221E', "&infin;");
        mHtmlEncodeMap.put('\u2220', "&ang;");
        mHtmlEncodeMap.put('\u2227', "&and;");
        mHtmlEncodeMap.put('\u2228', "&or;");
        mHtmlEncodeMap.put('\u2229', "&cap;");
        mHtmlEncodeMap.put('\u222A', "&cup;");
        mHtmlEncodeMap.put('\u222B', "&int;");
        mHtmlEncodeMap.put('\u2234', "&there4;");
        mHtmlEncodeMap.put('\u223C', "&sim;");
        mHtmlEncodeMap.put('\u2245', "&cong;");
        mHtmlEncodeMap.put('\u2248', "&asymp;");
        mHtmlEncodeMap.put('\u2260', "&ne;");
        mHtmlEncodeMap.put('\u2261', "&equiv;");
        mHtmlEncodeMap.put('\u2264', "&le;");
        mHtmlEncodeMap.put('\u2265', "&ge;");
        mHtmlEncodeMap.put('\u2282', "&sub;");
        mHtmlEncodeMap.put('\u2283', "&sup;");
        mHtmlEncodeMap.put('\u2284', "&nsub;");
        mHtmlEncodeMap.put('\u2286', "&sube;");
        mHtmlEncodeMap.put('\u2287', "&supe;");
        mHtmlEncodeMap.put('\u2295', "&oplus;");
        mHtmlEncodeMap.put('\u2297', "&otimes;");
        mHtmlEncodeMap.put('\u22A5', "&perp;");
        mHtmlEncodeMap.put('\u22C5', "&sdot;");
        mHtmlEncodeMap.put('\u2308', "&lceil;");
        mHtmlEncodeMap.put('\u2309', "&rceil;");
        mHtmlEncodeMap.put('\u230A', "&lfloor;");
        mHtmlEncodeMap.put('\u230B', "&rfloor;");
        mHtmlEncodeMap.put('\u2329', "&lang;");
        mHtmlEncodeMap.put('\u232A', "&rang;");
        mHtmlEncodeMap.put('\u25CA', "&loz;");
        mHtmlEncodeMap.put('\u2660', "&spades;");
        mHtmlEncodeMap.put('\u2663', "&clubs;");
        mHtmlEncodeMap.put('\u2665', "&hearts;");
        mHtmlEncodeMap.put('\u2666', "&diams;");
    }
    


}
