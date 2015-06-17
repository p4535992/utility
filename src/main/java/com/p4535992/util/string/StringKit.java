/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.p4535992.util.string;

import com.p4535992.util.reflection.ReflectionKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.p4535992.util.log.SystemLog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2015-04-25
 * @author 4535992
 * href: http://stackoverflow.com/questions/9572795/convert-list-to-array-in-java
 * href: http://stackoverflow.com/questions/11404086/how-could-i-initialize-a-generic-array
 */
public class StringKit<T> {
    private Class<T> cl;
    private String clName;
    private static final Logger logger = LoggerFactory.getLogger(StringKit.class);

//    public StringKit(){
//        java.lang.reflect.Type t = getClass().getGenericSuperclass();
//        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
//        this.cl = (Class) pt.getActualTypeArguments()[0];
//        this.clName = cl.getSimpleName();
//    }

    /*
     * Read String from InputStream and closes it
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
            io.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ioex) {
                System.out.println("Failed to close Streams");
                ioex.printStackTrace();
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
        if (text == null || text.equals("") || text.isEmpty() || text.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
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
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
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
            e.printStackTrace();
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
         BufferedReader r = new BufferedReader(
                 new InputStreamReader(new FileInputStream(UTF8),"UTF-8" )
         );
         String line = r.readLine();

         while (line != null) {
             //System.out.println(unicodeEscape(line));
             line = r.readLine();
             list.add(line);
         }
         r.close();
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
        BufferedReader r = new BufferedReader(new FileReader(ASCII));
        String line = r.readLine();
        while (line != null) {
            line = convertUnicodeEscapeToASCII(line);
            byte[] bytes = line.getBytes("UTF-8");
            System.out.write(bytes, 0, bytes.length);
            System.out.println();
            line = r.readLine();
            list.add(line);
        }
        r.close();
        return list;
    }

    static enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}


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
   public static String RegexAndReplace(String input,String expression,String replace){
       String result ="";
       if(replace==null){
           Pattern pattern = Pattern.compile(expression);
           Matcher matcher = pattern.matcher(input);
           while(matcher.find()){
                result = matcher.group().toString();
                if(result != null && result != ""){break;}
           }
       }else{
           input = input.replaceAll(expression, replace);
           result = input;
       }
       return result;
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
    public static <T> T getMoreCommonParameter(ArrayList<T> al){
       Map<T,Integer> map = new HashMap<>();
       for(int i=0;i<al.size();i++){
           Integer count = map.get(al.get(i));
           map.put(al.get(i), count==null?1:count+1);   //auto boxing and count
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
            if(setNullForEmptyString(content)==null){
            }else{ break;}
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
        try{
            //int i = Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            throw new NumberFormatException("Error while read in console:"+ nfe.getMessage());
        }
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
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sbFile = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sbFile.append(line);// append the line of the file
                sbFile.append('@');// separate the line with a '@'
                line = br.readLine();// read the next line of the file
            }
            readFile = sbFile.toString();// this string contains the character sequence
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  readFile;
    }

    /**
     * Merge the content of two arrays of string with same size for
     * make the args for a main method java class with option e home.
     * @param param array of parameter.
     * @param value array of values.
     * @param <T> generic type.
     * @return merged array.
     * @throws Exception error.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] mergeArrays(T[] param, T[] value) throws Exception {
        T[] array;
        int j = 0;
        if(param.length==value.length) {
            //array = new T[param.length+value.length];
            array = (T[]) Array.newInstance(param[0].getClass(),param.length+value.length);
            for (int i = 0; i < param.length; i++) {
                if (i == 0)
                    j = j + i;
                else
                    j = j + 1;

                array[j] = param[i];
                j = j + 1;
                array[j] = value[i];
            }
        }else{
            //logger.org.p4535992.mvc.error("WARNING: Check your array size");
            throw new Exception("WARNING: Check your array size");
        }
       return array;
    }

    /**
     * Method to convert a list to a array object.
     * @param list list.
     * @param <T> generic variable.
     * @return array .
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] convertListToArray(List<T> list){
        //return list.toArray(new Object[ list.size()]);
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        //T[] items=(T[]) new Object[size]
        if(ReflectionKit.isWrapperType(list.get(0).getClass())){ //if is a primitve class
            for(int i = 0; i < list.size(); i++) array[i] = list.get(i);
        }else{ //is is not a primitve class
            list.toArray(array);
        }
        return array;
    }

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
        try
        {
            FileOutputStream fileOut = new FileOutputStream("/tmp/"+nameTempSer+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            SystemLog.console("Serialized data is saved in /tmp/"+nameTempSer+".ser");
        }catch(IOException i)
        {
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
        try
        {
            FileInputStream fileIn = new FileInputStream("/tmp/"+nameTempSer+".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object = (T) in.readObject();
            in.close();
            fileIn.close();
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
            e.printStackTrace();
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
     * Method for concatenate the content of two arrays in a single array.
     * @param a first array.
     * @param b second array.
     * @param <T> generic variable.
     * @return array merged.
     */
    public static <T> T[] concatenateArrays(T[] a,T[] b) {
        int aLen = a.length;
        int bLen = b.length;
        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


    /*
     * Method to concatenate the content of n arrays toa single array.
     * @param first first array to merge.
     * @param rest other arrays to merge.
     * @param <T> generic variable.
     * @return merged array.
     
    public static <T> T[] concatenateArrays(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
    */


    /**
     * Method for copy the content of a array to aniother array of the same type.
     * @param baseArray array.
     * @param <T> generic type.
     * @return array copied.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copyContentArray(T[] baseArray){
        T[] b = (T[]) Array.newInstance(baseArray.getClass().getComponentType(), baseArray.length);
        //b = Arrays.copyOf(baseArray, baseArray.length);
        //b = new T[baseArray.length];
        System.arraycopy(baseArray, 0, b, 0, b.length);
        //b = baseArray.clone();
        return b;
    }


    /**
     * Method to check is a array is empty or with all value null or empty.
     * @param array array.
     * @param <T> generic type.
     * @return boolean value.
     */
    public static <T> boolean isArrayEmpty(T[] array){
        boolean empty = true;
        if(array!=null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    empty = false;
                    break;
                }
            }
        }
        return empty;
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
        String xmlStringData = writer.toString();
        return xmlStringData;
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
        T obj = (T) unmarshaller.unmarshal(reader);
        return obj;
    }

    /**
     * Method read a properties file.
     * @param file file.
     * @return map of properties.
     */
    public static Map<String,String> readPropertiesFile(File file){
        Map<String,String> map = new HashMap<>();
        Properties prop = new Properties();
        InputStream inputStream = StringKit.class.getClassLoader().getResourceAsStream(file.getAbsolutePath());
        try {
            prop.load(inputStream);
            for(Map.Entry<Object, Object> e : prop.entrySet()) {
                map.put(e.getKey().toString(),e.getValue().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }




}//end of the class StringKit
       

