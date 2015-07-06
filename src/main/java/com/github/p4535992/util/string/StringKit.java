package com.github.p4535992.util.string;

import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.reflection.ReflectionKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class with many utilities on String and Collection.
 * @author 4535992
 * @version 2015-06-26
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


    public enum special{
        WHITESPACE(0), NBSP(1),NEWLINE(2),PROJECTDIR(3),LINE_FEED(4);
        private final Integer value;
        special(Integer value) {
            this.value = value;
        }
        @Override
        public String toString() {
            String value="";
            switch (this) {
                case WHITESPACE: value = "\\s+"; break;
                case NBSP: value ="\u00A0"; break;
                ///case NBSP_CHAR: value ='\u00A0'; break;
                case NEWLINE: value = System.lineSeparator(); break;
                case PROJECTDIR: value = System.getProperty("user.dir");break;
                case LINE_FEED: value = "\r\n";
            }
            return value;
        }
    }


//    public StringKit(){
//        java.lang.reflect.Type t = getClass().getGenericSuperclass();
//        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
//        this.cl = (Class) pt.getActualTypeArguments()[0];
//        this.clName = cl.getSimpleName();
//    }

    public static String cleanStringHTML(String stringHtml){
        return stringHtml.replaceAll("\\r\\n|\\r|\\n"," ").trim();
                //.replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim())
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
     * Get the current GMT time for user notification.
     *
     * @return timestamp value as string.
     */
    public static String getGMTime()
    {
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        return gmtDateFormat.format(new java.util.Date());
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
     public static List<String> UTF82UnicodeEscape(File UTF8) throws IOException{
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
     * @throws IOException thorw if any error is occurrred.
      */
    public static List<String> UnicodeEscape2UTF8(File ASCII) throws IOException {
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
    public static String RandomUUID(){ return  java.util.UUID.randomUUID().toString(); }
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
       }else{
           input = input.replaceAll(expression, replace);
           result = input;
       }
       return result;
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
    private String getTheFirstTokenOfATokenizer(String content,String symbol){
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
     * Method to cconvert a object by the clazz is equivalent to public T cast(Object o)
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
     * Method to convert a array to a string with a specific separator
     * @param array the Array Collection.
     * @param separator the char separator.
     * @param <T> generic type.
     * @return the String of the content of the array.
     */
    public static <T> String convertArrayContentToSingleString(T[] array,char separator){
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            strBuilder.append( array[i].toString() );
            if(isNullOrEmpty(Character.toString(separator))&& i < array.length-1){
                strBuilder.append(separator);
            }
        }
        return strBuilder.toString();
    }

    /**
     * Method to convert a array to a string.
     * @param array the Array Collection.
     * @param <T> generic type.
     * @return the String of the content of the array.
     */
    public static <T> String convertArrayContentToSingleString(T[] array){
        String s = Arrays.toString(array);
        s = s.substring(1,s.length()-1);
        return s;
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
     * @return if tru is a url addresss web.
     */
    public static boolean isURL(String url){
        return Patterns.isValidURL(url);
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
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            result.add(matcher.group().replace("x","0"));
            if(justFirstResult)break;

        }
        return result;
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return
     */
    public static String findWithRegex(String text,String expression){
        final Pattern pat = Pattern.compile(expression);
        return pat.matcher(text).group();
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    public static boolean checkWithRegex(String text,String expression){
        final Pattern pat = Pattern.compile(expression);
        return text != null && pat.matcher(text).matches();
    }



}//end of the class StringKit
       

