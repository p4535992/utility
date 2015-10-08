package com.github.p4535992.util.string;

import com.github.p4535992.util.log.SystemLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class with many utilities on String and Collection.
 * @author 4535992.
 * @version 2015-09-29.
 * href: http://stackoverflow.com/questions/9572795/convert-list-to-array-in-java
 * href: http://stackoverflow.com/questions/11404086/how-could-i-initialize-a-generic-array
 * href: https://github.com/ku-fpg/armatus/blob/master/Armatus%20Android%20App/src/edu/kufpg/armatus/util/StringUtils.java
 * @param <T> generic type.
 */
@SuppressWarnings("unused")
public class StringKit<T> {
    private Class<T> cl;
    private String clName;
    private static final Logger logger = LoggerFactory.getLogger(StringKit.class);

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
    private static final String LINE_FEED = "\r\n";
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String EMPTY_STR = "";
    private static final String LT = "<";
    private static final String GT = ">";
    private static final String AMP = "&";
    private static final String QUAT = "\"";
    private static final String SINGLE_QUAT = "'";
    private static final String ESC_LT = "&lt;";
    private static final String ESC_GT = "&gt;";
    private static final String ESC_AMP = "&amp;";
    private static final String CRLF = "\r\n";


    public enum special{
        WHITESPACE(0), NBSP(1),NEWLINE(2),PROJECTDIR(3),LINE_FEED(4),LINE_SEP(5),EMPTY_STR(6),
        LT(7),GT(8),AMP(9),QUAT(10),SINGLE_QUAT(11),ESC_LT(12),ESC_GT(13),ESC_AMP(14),CRLF(15);
        private final Integer value;
        special(Integer value) {
            this.value = value;
        }
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


//    public StringKit(){
//        java.lang.reflect.Type t = getClass().getGenericSuperclass();
//        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
//        this.cl = (Class) pt.getActualTypeArguments()[0];
//        this.clName = cl.getSimpleName();
//    }

    /**
     * Method to clean a html text to a string text.
     * @param stringHtml html string of text.
     * @return string text.
     */
    public static String cleanStringHTML(String stringHtml){
        return stringHtml.replaceAll("\\r\\n|\\r|\\n"," ").trim();
                //.replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim())
    }

    /**
     * Method to simplify the content of a string for a better vison of the content.
     * @param stringText string of the text.
     * @return string of text simplify.
     */
    public static String toStringInline(String stringText){
        return stringText.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", " ").trim();
        //return stringText.replaceAll("(?m)(^ *| +(?= |$))", "").replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");
    }

    /**
     * Remove/collapse multiple spaces.
     * @param argStr string to remove multiple spaces from.
     * @return String
     */
    public static String collapseSpaces(String argStr)
    {
        char last = argStr.charAt(0);
        StringBuilder argBuf = new StringBuilder();

        for (int cIdx = 0 ; cIdx < argStr.length(); cIdx++)
        {
            char ch = argStr.charAt(cIdx);
            if (ch != ' ' || last != ' ')
            {
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
    public static String convertInputStreamToStringNoEncoding(InputStream is, Charset encoding) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        StringBuilder sb = new StringBuilder(1024);
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
        } catch (IOException io) {
            System.out.println("Failed to read from Stream");
            SystemLog.exception(io);
        } finally {
            try {
                br.close();
            } catch (IOException ioex) {
                System.out.println("Failed to close Streams");
                SystemLog.exception(ioex);
            }
        }
        return sb.toString();
    }
    /**
     * Returns true if the parameter is null or empty. false otherwise.
     *
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    public static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty();
    }

    /**
     * Returns a String with the content of the InputStream.
     * @param is with the InputStream.
     * @return string with the content of the InputStream.
     * @throws IOException throw any error is occurred.
     */
    public static String convertInputStreamToString(InputStream is)
            throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Returns am InputStream with the parameter.
     *
     * @param string string input.
     * @return InputStream with the string value.
     */
    public static InputStream convertStringToInputStream(String string) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            SystemLog.exception(e);
        }
        return is;
    }



    
     /**
        * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
        * escaped sequence for characters outside of ASCII.
        * It is equivalent to: native2ascii -encoding utf-8
        * @param UTF8 string encoding utf8
        * @return ASCII string encoding ascii.
        * @throws UnsupportedEncodingException throw if any error is occurrred.  
        * @throws IOException throw if any error is occurrred.
        */
     public static List<String> convertUTF82UnicodeEscape(File UTF8) throws IOException{
         List<String> list = new ArrayList<>();
         if (UTF8==null) {
             System.out.println("Usage: java UTF8ToAscii <filename>");
             return null;
         }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(UTF8),"UTF-8" ))){
            String line = r.readLine();
            while (line != null) {
                //System.out.println(unicodeEscape(line));
                line = r.readLine();
                list.add(line);
            }
        }
         return list;
     }

    private static final char[] hexChar = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private static String unicodeEscape(String s) {
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
     * @param ASCII string encoding ascii.
     * @return UTF8 string encoding utf8.
     * @throws IOException thorw if any error is occurred.
      */
    public static List<String> convertUnicodeEscapeToUTF8(File ASCII) throws IOException {
        List<String> list = new ArrayList<>();
          if (ASCII == null) {
              //System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
              return null;
          }
        try (BufferedReader r = new BufferedReader(new FileReader(ASCII))) {
            String line = r.readLine();
            while (line != null) {
                line = convertUnicodeEscapeToASCII(line);
                byte[] bytes = line.getBytes("UTF-8");
                System.out.write(bytes, 0, bytes.length);
                System.out.println();
                line = r.readLine();
                list.add(line);
            }
        }
        return list;
    }

    enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}
    /**
     *  convert unicode escapes back to char.
     * @param s string to convert to ascii.
     * @return string ascii.
     */
    private static String convertUnicodeEscapeToASCII(String s) {
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
     * Read an input stream into a string.
     * @param in stream of the resource.
     * @return string of the content of the resource.
     * @throws IOException resource not found.
     */
    static public String convertStreamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
       
    /**
     *Creating a random UUID (Universally unique identifier).
     * @return string asuuid.
     */
    public static String randomUUID(){ return  java.util.UUID.randomUUID().toString(); }
   /**
    * Metodo che converte una stringa a un'oggetto UUID.
    * @param uuid string uuid.
    * @return java.util.UUID.
    */
    public static java.util.UUID convertStringToUUID(String uuid){return java.util.UUID.fromString(uuid); }
       
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

    /**
     * Methohs remove the symbol if exists in the first and last caracther of the string
     * @param stringToUpdate string of input.
     * @param symbol symbol to check.
     * @return the string update.
     */
    private static String removeFirstAndLast(String stringToUpdate, String symbol) {
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
    * Metodo che assegna attraverso un meccanismo di "mapping" ad ogni valore
    * distinto del parametro in questione un numero (la frequenza) prendeno il
    * valore con la massima frequenza abbiamo ricavato il valore più diffuso
    * per tale parametro.
    * @param al lista dei valori per il determianto parametro del GeoDocument.
    * @param <T> generic variable.
    * @return  il valore più diffuso per tale parametro.
    */
    public static <T> T getMoreCommonParameter(List<T> al){
       Map<T,Integer> map = new HashMap<>();
        for (T anAl : al) {
            Integer count = map.get(anAl);
            map.put(anAl, count == null ? 1 : count + 1);   //auto boxing and count
        }
       T keyParameter=null;
       Integer keyValue =0;
       for ( Map.Entry<T, Integer> entry : map.entrySet()) {
           T key = entry.getKey();
           Integer value = entry.getValue();
           if(value >= keyValue && setNullForEmptyString(key.toString())!=null && !key.toString().equalsIgnoreCase("null")){
               keyValue = value;
               keyParameter = key;
           }
       }return keyParameter;
    }//getMoreCommonParameter

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
     * Method for convert a cstrin to a OutputStream and print to a file.
     * @param content string to print on the file.
     * @param outputPathFileName file where i put the stream.
     */
    public void copyStringToFile(String content, File outputPathFileName) {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputPathFileName, true)))) {
            out.print(content);
            out.flush();
            //out.close();
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    /**
     * Method for read the input on the console.
     * @return string print to the console.
     * @throws IOException throw error if any occurred.
     */
    public String readConsole() throws IOException {
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
    public static String readFileWithStringBuilder(File filename) {
        String readFile = "";
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                StringBuilder sbFile = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sbFile.append(line);// append the line of the file
                    sbFile.append('@');// separate the line with a '@'
                    line = br.readLine();// read the next line of the file
                }
                readFile = sbFile.toString();// this string contains the character sequence
            }
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return  readFile;
    }


    /**
     * Method to  count the elements caracthers of a string.
     * @return string of int where the first element is the number of words,the second is the number of characters
     * and the third is the number of lines.
     * @throws IOException throw if any error is occurred.
     */
    public static int[] countElementOfAString() throws IOException {
        int i=0,j=0,k=0;
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        String s;
        s=br.readLine();//Enter File Name:
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
        }
        br.close();
        return new int[]{i,j,k}; //Number of Words:,Number of Characters:,Number of Lines:
    }


    /**
     * Method to Serializing an Object.
     * @param object object.
     * @param nameTempSer string serializable.
     * @param <T> generic type.
     */
    public static <T> void convertObjectToSerializable(T object,String nameTempSer){
        try{
            try (FileOutputStream fileOut = new FileOutputStream("/tmp/"+nameTempSer+".ser"); 
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(object);
            }
            SystemLog.console("Serialized data is saved in /tmp/" + nameTempSer + ".ser");
        }catch(IOException i){
           SystemLog.exception(i);
        }
    }

    /**
     * Method to Deserializing an Object.
     * @param object object.
     * @param nameTempSer string serializable.
     * @param <T> generic type.
     * @return object serializable.
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertSerializableToObject(T object,String nameTempSer){
        try{
            try (FileInputStream fileIn = new FileInputStream("/tmp/"+nameTempSer+".ser"); 
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {
                object = (T) in.readObject();
            }
        }catch(IOException i)
        {
            SystemLog.exception(i);
            return null;
        }catch(ClassNotFoundException c)
        {
            SystemLog.error(""+object.getClass().getName()+" class not found!!!");
            SystemLog.exception(c);
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
    public static <T> T convertInstanceOfObject(Object objectToCast,Class<T> clazz) {
        try {
            if (clazz.isInstance(objectToCast)) {
                return clazz.cast(objectToCast);
            } else {
                return (T) objectToCast;
            }
        } catch (ClassCastException e) {
            SystemLog.exception(e);
            return null;
        }
    }

    /**
     * Method for check if a string rappresent a numeric value.
     * @param str string rapresentative of a number.
     * @return booleanvalue if the string rappresent a number or not.
     */
    public static boolean isNumeric(String str) {
        //match a number with optional '-' and decimal.
        str = str.replace(",",".").replace(" ",".");
        return str.matches("(\\-|\\+)?\\d+(\\.\\d+)?");
    }


    /**
     * Method to convert a POJO java object to XML string.
     * @param object object.
     * @param clazz class.
     * @param <T> generic type.
     * @return string.
     * @throws JAXBException error.
     */
    public static <T> String convertPojoToXml(T object, Class<T> clazz)throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    /**
     * Method to convert a XML string to POJO java object.
     * @param xmlStringData string xml.
     * @param clazz class.
     * @param <T> generic type.
     * @return object T.
     * @throws JAXBException error.
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertXmlToPojo(String xmlStringData, Class<T> clazz)  throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        StringReader reader = new StringReader(xmlStringData);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(reader);
    }

    /**
     * Method to read a properties file.
     * @param file file.
     * @param thisClass the reference class.
     * @return map of properties.
     */
    public static Map<String,String> readPropertiesFile(File file,Class<?> thisClass){
        Map<String,String> map = new HashMap<>();
        Properties prop = new Properties();
        InputStream inputStream = thisClass.getClassLoader().getResourceAsStream(file.getAbsolutePath());
        try {
            prop.load(inputStream);
            for(Map.Entry<Object, Object> e : prop.entrySet()) {
                map.put(e.getKey().toString(),e.getValue().toString());
            }
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return map;
    }




    /**
     * Method to convert Strng to char.
     * @param string string.
     * @return Array of char.
     */
    public static char[] convertStringToChar(String string){
        return string.toCharArray();
    }


    /**
     * Method to check if a s tring is a url address web or not.
     * @param url the string address web.
     * @return if tru is a url address web.
     */
    public static boolean isURL(String url){
        return Patterns.isValidURL(url);
    }

    /**
     * Method to check if a s tring is a url address web or not.
     * @param url the string address web.
     * @return if tru is a url address web.
     */
    public static boolean isURLSimple(String url){
        return url.matches("^(https?|ftp)://.*$");
    }
    /**
     * Method to add a protocl to a strin to match a url.
     * @param url the string of the url.
     * @return he string of the url with protocol.
     */
    public static String addProtocolToURLString(String url) {
        if (!url.matches("^(https?|ftp)://.*$")) {
            return "http://" + url;
        }
        return url;
    }

    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    public static List<String> findWithRegex(String text,String expression,boolean justFirstResult){
        List<String> result =new ArrayList<>();
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
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern regular expression.
     * @return the first match on the text string.
     */
    public static String findWithRegex(String text,Pattern pattern){
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group(0);
        }
        return "?";
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
     * Method to convert a string to a  "MD5", "SHA-1", "SHA-256" hash.
     * suitable for small-sized message
     * @param message the string to convert.
     * @param algorithm the type has algorithm "MD5", "SHA-1", "SHA-256".
     * @return the string of the hash code of the inpu string.
     */
    private static String hashString(String message, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            //throw new HashGenerationException("Could not generate hash from String", ex);
            SystemLog.exception(ex);
            return null;
        }
    }

   /* public static String generateMD5(String message) {
        return hashString(message, "MD5");
    }

    public static String generateSHA1(String message) {
        return hashString(message, "SHA-1");
    }

    public static String generateSHA256(String message){
        return hashString(message, "SHA-256");
    }*/

    /**
     * Method to convert a string to a  "MD5", "SHA-1", "SHA-256" hash for very large file.
     * suitable for large-size message, i.e. large file
     * @param message the string to convert.
     * @param algorithm the type has algorithm "MD5", "SHA-1", "SHA-256".
     * @return the string of the hash.
     */
    private static String hashStringForLargeFile(String message, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] inputBytes = message.getBytes("UTF-8");
            digest.update(inputBytes);
            byte[] hashedBytes = digest.digest();
            return convertByteArrayToHexString(hashedBytes);// convert hash bytes to string (usually in hexadecimal form)
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            //throw new HashGenerationException("Could not generate hash from String", ex);
            SystemLog.exception(ex);
            return null;
        }
    }

    /**
     * Method to convert a array of bytes to a string.
     * @param arrayBytes array Collection of bytes.
     * @return the string of the hash.
     */
    public static String convertByteArrayToString(byte[] arrayBytes){
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
    @SuppressWarnings("")
    public static String convertByteArrayToHexString(byte[] arrayBytes) {
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
    public static String convertByteArrayToStringWithSpring(byte[] arrayBytes){
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
    public static String convertStringToMD5(String message) {
        return hashString(message, "MD5");
    }

    /**
     * Method to convert string to SHA-1 hash.
     * @param message string to codify to hash.
     * @return the string of the hash.
     */
    public static String convertStringToSHA1(String message) {
        return hashString(message, "SHA-1");
    }

    /**
     * Method to convert string to SHA-256 hash.
     * @param message string to codify to hash.
     * @return the string of the hash.
     */
    public static String convertStringToSHA256(String message) {
        return hashString(message, "SHA-256");
    }


    /**
     * Method to convert a Integer to a int primitive.
     * @param integer the integer to convert.
     * @return the int primitive of the integer object.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static int convertIntegerToInt(Integer integer){
        return integer.intValue();
    }

    /**
     * Method to convert a int primitive to the Integer object .
     * @param numInt the int primitive.
     * @return the the integer object of the int primitive .
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Integer convertIntToInteger(int numInt){
        return Integer.valueOf(numInt);
    }

    /**
     * Method to convert a int primitive to the Integer object .
     * @param numInt the int primitive.
     * @return the the double object of the int primitive .
     */
    public static Double convertIntToDouble(int numInt){ return (double) numInt;}

    /**
     * Method to convert a int primitive to the Integer object .
     * @param numInt the int primitive.
     * @return the the double object of the int primitive .
     */
    public static Double convertIntegerToDouble(Integer numInt){ return (double) numInt;}



    /**
     * Method to convert a String to a Integer.
     * @param numericText the numeric text string.
     * @return the integer object.
     */
    public static Integer convertStringToInteger(String numericText){
        if(isNumeric(numericText)){
            return Integer.parseInt(numericText);
        }else{
            SystemLog.warning("The string text:"+numericText+" is not a number!!!");
            return null;
        }
    }

    /**
     * Method to convert a String to a int.
     * @param numericText the numeric text string.
     * @return the int primitive.
     */
    public static int convertStringToInt(String numericText){
        if(isNumeric(numericText)){
            return convertIntegerToInt(Integer.parseInt(numericText));
        }else{
            SystemLog.warning("The string text:"+numericText+" is not a number!!!");
            return 0;
        }
    }

    /**
     * Method to convert a object to a String.
     * @param object the Object to convert.
     * @return the String of the object.
     */
    public static String convertObjectToString(Object object){
        if(object instanceof URL) return object.toString();
        return (String)object;
    }

    /**
     * Method to cnvert a OBject to a Integer.
     * @param object the Object to convert.
     * @return the Int of the object.
     */
    public static int convertObjectToInt(Object object){
        return Integer.parseInt((String)object);
    }


}//end of the class StringKit
       

