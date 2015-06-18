package com.github.p4535992.util.encoding;

import com.github.p4535992.util.log.SystemLog;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EncodingUtil {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EncodingUtil.class);
    private static Map<String,String> map = new HashMap<>();

    /**Map for brute force replace of all unicode escape on the text*/
    private static void setMapUicodeEscaped()
    {
        map.put("U+0000","");
        map.put("U+0001","");
        map.put("U+0002","");
        map.put("U+0003","");
        map.put("U+0004","");
        map.put("U+0005","");
        map.put("U+0006","");
        map.put("U+0007","");
        map.put("U+0008","");
        map.put("U+0009","");
        map.put("U+000A","");
        map.put("U+000B","");
        map.put("U+000C","");
        map.put("U+000D","");
        map.put("U+000E","");
        map.put("U+000F","");
        map.put("U+0010","");
        map.put("U+0011","");
        map.put("U+0012","");
        map.put("U+0013","");
        map.put("U+0014","");
        map.put("U+0015","");
        map.put("U+0016","");
        map.put("U+0017","");
        map.put("U+0018","");
        map.put("U+0019","");
        map.put("U+001A","");
        map.put("U+001B","");
        map.put("U+001C","");
        map.put("U+001D","");
        map.put("U+001E","");
        map.put("U+001F","");
        map.put("U+0020"," ");
        map.put("U+0021","!");
        map.put("U+0022","\"");
        map.put("U+0023","#");
        map.put("U+0024","$");
        map.put("U+0025","%");
        map.put("U+0026","&");
        map.put("U+0027","'");
        map.put("U+0028","(");
        map.put("U+0029",")");
        map.put("U+002A","*");
        map.put("U+002B","+");
        map.put("U+002C",",");
        map.put("U+002D","-");
        map.put("U+002E",".");
        map.put("U+002F","/");
        map.put("U+0030","0");
        map.put("U+0031","1");
        map.put("U+0032","2");
        map.put("U+0033","3");
        map.put("U+0034","4");
        map.put("U+0035","5");
        map.put("U+0036","6");
        map.put("U+0037","7");
        map.put("U+0038","8");
        map.put("U+0039","9");
        map.put("U+003A",":");
        map.put("U+003B",";");
        map.put("U+003C","<");
        map.put("U+003D","=");
        map.put("U+003E",">");
        map.put("U+003F","?");
        map.put("U+0040","@");
        map.put("U+0041","A");
        map.put("U+0042","B");
        map.put("U+0043","C");
        map.put("U+0044","D");
        map.put("U+0045","E");
        map.put("U+0046","F");
        map.put("U+0047","G");
        map.put("U+0048","H");
        map.put("U+0049","I");
        map.put("U+004A","J");
        map.put("U+004B","K");
        map.put("U+004C","L");
        map.put("U+004D","M");
        map.put("U+004E","N");
        map.put("U+004F","O");
        map.put("U+0050","P");
        map.put("U+0051","Q");
        map.put("U+0052","R");
        map.put("U+0053","S");
        map.put("U+0054","T");
        map.put("U+0055","U");
        map.put("U+0056","V");
        map.put("U+0057","W");
        map.put("U+0058","X");
        map.put("U+0059","Y");
        map.put("U+005A","Z");
        map.put("U+005B","[");
        map.put("U+005C","\\");
        map.put("U+005D","]");
        map.put("U+005E","^");
        map.put("U+005F","_");
        map.put("U+0060","`");
        map.put("U+0061","a");
        map.put("U+0062","b");
        map.put("U+0063","c");
        map.put("U+0064","d");
        map.put("U+0065","e");
        map.put("U+0066","f");
        map.put("U+0067","g");
        map.put("U+0068","h");
        map.put("U+0069","i");
        map.put("U+006A","j");
        map.put("U+006B","k");
        map.put("U+006C","l");
        map.put("U+006D","m");
        map.put("U+006E","n");
        map.put("U+006F","o");
        map.put("U+0070","p");
        map.put("U+0071","q");
        map.put("U+0072","r");
        map.put("U+0073","s");
        map.put("U+0074","t");
        map.put("U+0075","u");
        map.put("U+0076","v");
        map.put("U+0077","w");
        map.put("U+0078","x");
        map.put("U+0079","y");
        map.put("U+007A","z");
        map.put("U+007B","{");
        map.put("U+007C","|");
        map.put("U+007D","}");
        map.put("U+007E","~");
        map.put("U+007F","");
        map.put("U+0080","");
        map.put("U+0081","");
        map.put("U+0082","");
        map.put("U+0083","");
        map.put("U+0084","");
        map.put("U+0085","");
        map.put("U+0086","");
        map.put("U+0087","");
        map.put("U+0088","");
        map.put("U+0089","");
        map.put("U+008A","");
        map.put("U+008C","");
        map.put("U+008D","");
        map.put("U+008E","");
        map.put("U+008F","");
        map.put("U+0090","");
        map.put("U+0091","");
        map.put("U+0092","");
        map.put("U+0093","");
        map.put("U+0094","");
        map.put("U+0095","");
        map.put("U+0096","");
        map.put("U+0097","");
        map.put("U+0098","");
        map.put("U+0099","");
        map.put("U+009A","");
        map.put("U+009B","");
        map.put("U+009C","");
        map.put("U+009D","");
        map.put("U+009E","");
        map.put("U+009F","");
        map.put("U+00A0","");
        map.put("U+00A1","¡");
        map.put("U+00A2","¢");
        map.put("U+00A3","£");
        map.put("U+00A4","¤");
        map.put("U+00A5","¥");
        map.put("U+00A6","¦");
        map.put("U+00A7","§");
        map.put("U+00A8","¨");
        map.put("U+00A9","©");
        map.put("U+00AA","ª");
        map.put("U+00AB","«");
        map.put("U+00AC","¬");
        map.put("U+00AD","­");
        map.put("U+00AE","®");
        map.put("U+00AF","¯");
        map.put("U+00B0","°");
        map.put("U+00B1","±");
        map.put("U+00B2","²");
        map.put("U+00B3","³");
        map.put("U+00B4","´");
        map.put("U+00B5","µ");
        map.put("U+00B6","¶");
        map.put("U+00B7","·");
        map.put("U+00B8","¸");
        map.put("U+00B9","¹");
        map.put("U+00BA","º");
        map.put("U+00BB","»");
        map.put("U+00BC","¼");
        map.put("U+00BD","½");
        map.put("U+00BE","¾");
        map.put("U+00BF","¿");
        map.put("U+00C0","À");
        map.put("U+00C1","Á");
        map.put("U+00C2","Â");
        map.put("U+00C3","Ã");
        map.put("U+00C4","Ä");
        map.put("U+00C5","Å");
        map.put("U+00C6","Æ");
        map.put("U+00C7","Ç");
        map.put("U+00C8","È");
        map.put("U+00C9","É");
        map.put("U+00CA","Ê");
        map.put("U+00CB","Ë");
        map.put("U+00CC","Ì");
        map.put("U+00CD","Í");
        map.put("U+00CE","Î");
        map.put("U+00CF","Ï");
        map.put("U+00D0","Ð");
        map.put("U+00D1","Ñ");
        map.put("U+00D2","Ò");
        map.put("U+00D3","Ó");
        map.put("U+00D4","Ô");
        map.put("U+00D5","Õ");
        map.put("U+00D6","Ö");
        map.put("U+00D7","×");
        map.put("U+00D8","Ø");
        map.put("U+00D9","Ù");
        map.put("U+00DA","Ú");
        map.put("U+00DB","Û");
        map.put("U+00DC","Ü");
        map.put("U+00DD","Ý");
        map.put("U+00DE","Þ");
        map.put("U+00DF","ß");
        map.put("U+00E0","à");
        map.put("U+00E1","á");
        map.put("U+00E2","â");
        map.put("U+00E3","ã");
        map.put("U+00E4","ä");
        map.put("U+00E5","å");
        map.put("U+00E6","æ");
        map.put("U+00E7","ç");
        map.put("U+00E8","è");
        map.put("U+00E9","é");
        map.put("U+00EA","ê");
        map.put("U+00EB","ë");
        map.put("U+00EC","ì");
        map.put("U+00ED","í");
        map.put("U+00EE","î");
        map.put("U+00EF","ï");
        map.put("U+00F0","ð");
        map.put("U+00F1","ñ");
        map.put("U+00F2","ò");
        map.put("U+00F3","ó");
        map.put("U+00F4","ô");
        map.put("U+00F5","õ");
        map.put("U+00F6","ö");
        map.put("U+00F7","÷");
        map.put("U+00F8","ø");
        map.put("U+00F9","ù");
        map.put("U+00FA","ú");
        map.put("U+00FB","û");
        map.put("U+00FC","ü");
        map.put("U+00FD","ý");
        map.put("U+00FE","þ");
        map.put("U+00FF","ÿ");
    }
        	
	//PRIVATE
	//final static String FILE_NAME = "C:\\Temp\\input.properties";
	//System.getProperty("user.dir")+"\\src\\triple_karma_output_20150214_150502.n3";
	private static String FILE_NAME;
	private static String OUTPUT_FILE_NAME;
	private static Charset ENCODING = StandardCharsets.UTF_8;
	private static String FIXED_TEXT = "But soft! what code in yonder program breaks?";

	private String quote(String aText){
           String QUOTE = "'";
	   return QUOTE + aText + QUOTE;
	}
	 	
	public EncodingUtil(){
            setMapUicodeEscaped();        
        }
        
        public EncodingUtil(String FILE_NAME,String OUTPUT_FILE_NAME,Charset ENCODING)
        {
            setMapUicodeEscaped();
            EncodingUtil.FILE_NAME=FILE_NAME;
            EncodingUtil.OUTPUT_FILE_NAME=OUTPUT_FILE_NAME;
            EncodingUtil.ENCODING=ENCODING;
        }
        
        public EncodingUtil(String FILE_NAME,Charset ENCODING)
        {
            setMapUicodeEscaped();
            EncodingUtil.FILE_NAME=FILE_NAME;           
            EncodingUtil.ENCODING=ENCODING;
        }
             	
	//For smaller files

    /**
     * Read small and large file of text
     *  Note: the javadoc of Files.readAllLines says it's intended for small
     *   files. But its implementation uses buffering, so it's likely good 
     *  even for fairly large files
     * @param aFileName string path to the file you want to read
     * @return a list of lines 
     * @throws IOException file not found
     */
    public static List<String> readSmallTextFile(String aFileName) throws IOException {
	    Path path = Paths.get(aFileName);
	    return Files.readAllLines(path, ENCODING);
    }
	  
	  
    public static void writeSmallTextFile(List<String> aLines, String aFileName) throws IOException {
	    Path path = Paths.get(aFileName);
	    Files.write(path, aLines, ENCODING);
    }
      
	
     //For larger files
	/* public static List<String> readLargerTextFile(String aFileName) throws IOException {
        List<String> list = new ArrayList<>();
	    Path path = Paths.get(aFileName);
	    try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	      while (scanner.hasNextLine()){
	        //process each line in some way
	        //SystemLog.console(scanner.nextLine());
            list.add(scanner.nextLine())
	      }      
	    }
         return list;
     }*/
	  
     public static List<String> readLargerTextFile(String aFileName) throws IOException {
        List<String> list = new ArrayList<>();
        Path path = Paths.get(aFileName);
        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
          while (scanner.hasNextLine()){
            //process each line in some way
            try{
              list.add(scanner.nextLine().toString());
            }catch( java.util.NoSuchElementException e){
              if(scanner.hasNextLine()){
                 continue;
              }else{
                  break;
              }
            }
          }
        }
        return list;
     }
	  
     public static List<String> readLargerTextFileAlternate(String aFileName) throws IOException {
        List<String> list = new ArrayList<>();
	    Path path = Paths.get(aFileName);
	    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //process each line in some way
                list.add(line);
            }
        }
	    return list;
     }
	  
     public static void writeLargerTextFile(String aFileName, List<String> aLines) throws IOException {
	    Path path = Paths.get(aFileName);
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	      for(String line : aLines){
	        writer.write(line);
	        writer.newLine();
	      }
	    }catch(java.lang.NullPointerException e){
                //------DO NOTHING
        }
     }
          
     public static void writeLargerTextFileWithReplace(String aFileName, List<String> aLines) throws IOException {
	    Path path = Paths.get(aFileName);
	    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), true)))) {                        
	      for(String line : aLines){  
              try{
                for (Map.Entry<String, String> entry : map.entrySet())
                {
                    try{
                        String s = entry.getKey().replace("U+","\\u");
                        if(line.contains(s)){
                            line = line.toString().replace(s,entry.getValue());
                        }
                    }catch(java.lang.NullPointerException ne){break;}
                }
                out.print(line.toString() + System.getProperty("line.separator"));
                out.flush();
              }catch(java.lang.NullPointerException ne){break;}
	      }                
                //out.close();
	    }
        catch(java.lang.NullPointerException ne){return;}
     }
           
     public static void writeLargerTextFileWithReplace2(String aFileName, List<String> aLines) throws IOException {
	    Path path = Paths.get(aFileName);
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	      for(String line : aLines){
                  try{
                    for (Map.Entry<String, String> entry : map.entrySet())
                      {
                          try{
                          String s = entry.getKey().replace("U+","\\u");
                          if(line.contains(s)){
                              line = line.toString().replace(s,entry.getValue());
                          }
                          }catch(java.lang.NullPointerException ne){
                              break;
                          }
                      } //foreach entry
                  writer.write(line);
                  writer.newLine();
                }catch(java.lang.NullPointerException ne){
                    break;
                }
	      }//FOREACH LINE
	    }catch(java.lang.NullPointerException ne){
                SystemLog.warning(ne.getMessage());
        }
     }
	  
   /**
    * Template method that calls {@link #processLine(String)}.
    * @param aFileName string of the path to the file
    * @throws IOException file not found
    */
    public final void processLineByLine(String aFileName) throws IOException {
          Path path = Paths.get(aFileName);
      try (Scanner scanner =  new Scanner(path, ENCODING.name())){
        while (scanner.hasNextLine()){
          processLine(scanner.nextLine());
        }      
      }catch(java.lang.NullPointerException ne){return;}
    }
	  
	  
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  /** 
	   Overridable method for processing lines in different ways.	    
	   This simple default implementation expects simple name-value pairs, separated by an 
	   '=' sign. Examples of valid input: 
	   height = 167cm
	   mass =  65kg
	   disposition =  "grumpy"
	   this is the name = this is the value
           * @param aLine string correspond to a line of the file
	  */
	  protected void processLine(String aLine){
	    //use a second Scanner to parse the content of each line 
	    Scanner scanner = new Scanner(aLine);
	    scanner.useDelimiter("=");
	    if (scanner.hasNext()){
	      //assumes the line has a certain structure
	      SystemLog.console("Name:"+scanner.next()+",Value:"+scanner.next());
	    }
	    else {
          SystemLog.console("Empty or invalid line. Unable to process.");
	    }
	  }
	  
    /** Write fixed content to the given file. */
    void write() throws IOException  {
        SystemLog.message("Try to writing to file named " + FILE_NAME + " with Encoding: " + ENCODING);
	    Writer out = null;
	    try{
            out = new OutputStreamWriter(new FileOutputStream(FILE_NAME), ENCODING);
        }
        finally {
            out.close();
        }
    }
	  
    
    /**
     * Read the contents of the given file. 
     * @throws IOException file not found
     */
    public void read() throws IOException {
      //log("Reading from file.");
      StringBuilder text = new StringBuilder();
      String NL = System.getProperty("line.separator");
      Scanner scanner = new Scanner(new FileInputStream(FILE_NAME), ENCODING.name());
      try {
        while (scanner.hasNextLine()){
          text.append(scanner.nextLine() + NL);
        }
      }
      finally{
        scanner.close();
      }
      //log("Text read in: " + text);
    }

	 
	  /////////////////////////////////////////////////////////////////////////////////////////////////////////
	  /**
	   * Fetch the entire contents of a text file, and return it in a String.
	   * This style of implementation does not throw Exceptions to the caller.
	   *
	   * @param aFile is a file which already exists and can be read.
           * @return content of the file
	   */
	   static public String getContents(File aFile) {
	     //...checks on aFile are elided
	     StringBuilder contents = new StringBuilder();
	     try {
	       //use buffering, reading one line at a time
	       //FileReader always assumes default encoding is OK!
	       BufferedReader input =  new BufferedReader(new FileReader(aFile));
	       try {
	         String line = null; //not declared within while loop
	         /*
	         * readLine is a bit quirky :
	         * it returns the content of a line MINUS the newline.
	         * it returns null only for the END of the stream.
	         * it returns an empty String if two newlines appear in a row.
	         */
	         while (( line = input.readLine()) != null){
	           contents.append(line);
	           contents.append(System.getProperty("line.separator"));
	         }
	       }
	       finally {
	         input.close();
	       }
	     }
	     catch (IOException ex){
	       ex.printStackTrace();
	     }
	     
	     return contents.toString();
	   }

        /**
         * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
         * escaped sequence for characters outside of ASCII.
         * It is equivalent to: native2ascii -encoding utf-8
         * @param UTF8 encoding of input
         * @return ASCII encoding of output
         * @throws UnsupportedEncodingException unsupported encoding type
         * @throws FileNotFoundException file not found
         * @throws IOException  file not found
         */
        public static List<String> convertUTF8ToUnicodeEscape(File UTF8) throws UnsupportedEncodingException, FileNotFoundException, IOException{
            List<String> list = new ArrayList<>();
            if (UTF8==null) {
                 System.out.println("Usage: java UTF8ToAscii <filename>");
                 return null;
             }
             BufferedReader r = new BufferedReader(
                 new InputStreamReader(
                     new FileInputStream(UTF8),"UTF-8" )
             );
             String line = r.readLine();

             while (line != null) {
                  System.out.println(unicodeEscape(line));                     
                  line = r.readLine();
                  list.add(line);
             }
             r.close();   
             return list;
        }

        private static final char[] hexChar = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        /**
         * Method for convert a string UTF-8 to HEX
         * @param text string of text you want to convert to HEX
         * @return the text in HEX encoding
         */
        private static String unicodeEscape(String text) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                 char c = text.charAt(i);
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
        * @param ASCII file of input in ASCII encoding
        * @return UTF8 file of input in UTF8 encoding
        * @throws FileNotFoundException file not found
        * @throws IOException file not found
        */
        public static List<String> convertUnicodeEscapeToUTF8(File ASCII) throws FileNotFoundException, IOException {
            List<String> list = new ArrayList<>();
            if (ASCII == null) {
                System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
                return null;
            }
            BufferedReader r = new BufferedReader(new FileReader(ASCII));
            String line = r.readLine();
            while (line != null) {
                line = convertUnicodeEscape(line);
                byte[] bytes = line.getBytes("UTF-8");
                //System.out.write(bytes, 0, bytes.length);
                //System.out.println();
                line = r.readLine();
                list.add(line);
            }
            r.close();
            return list;
        }
        
        static enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}
        
        /**
         *  convert unicode escapes back to char
         * @param s string to convert unicode escape.
         * @return string converted.
         */
        private static String convertUnicodeEscape(String s) {
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
     * Method to rewrite a file in the UTF-8 encoding
     * @param fileASCII file of input in ASCII encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToUTF8(File fileASCII) throws IOException{
        List<String> list = convertUnicodeEscapeToUTF8(fileASCII);
        String filePathASCII = fileASCII.getAbsolutePath();
        fileASCII.delete();
        //fileASCII = new File(filePathASCII);
        writeLargerTextFile(filePathASCII, list);
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     * @param fileUTF8 file of input in UTF8 encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToASCII(File fileUTF8) throws IOException{
        List<String> list = convertUTF8ToUnicodeEscape(fileUTF8);
        String filePathUTF8 = fileUTF8.getAbsolutePath();
        fileUTF8.delete();
        //fileASCII = new File(filePathASCII);
        writeLargerTextFile(filePathUTF8, list);
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     * @param filePathUTF8 file of input in UTF8 encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToASCII(String filePathUTF8) throws IOException{
        rewriteTheFileToASCII(new File(filePathUTF8));
    }

    /**
     * Method to rewrite a file in the UTF-8 encoding
     * @param filePathASCII file of input in ASCII encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToUTF8(String filePathASCII) throws IOException{
        rewriteTheFileToUTF8(new File(filePathASCII));
    }

    /**
     * Convert byte array to Hex String
     * @param b array of byte
     * @return Hex String
     */
    private static String convertByteArrayToHexString(byte[] b) {
        int len = b.length;
        String data = new String();

        for (int i = 0; i < len; i++){
            data += Integer.toHexString((b[i] >> 4) & 0xf);
            data += Integer.toHexString(b[i] & 0xf);
        }
        return data;
    }

    //OTHER


    public static void rewriteTheFileToUTF8(String filePathInput,String filePathOutput) {
        try {
            FileOutputStream fos = new FileOutputStream(filePathInput);
            Writer out = new OutputStreamWriter(fos, ENCODING);
            out.write(filePathOutput);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readLargeTextFileUTF8(String filePathInput) {
        //List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(filePathInput);
            InputStreamReader isr = new InputStreamReader(fis, ENCODING);
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char) ch);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

  /*
    Normalizer.normalize(geo.getEdificio(), Normalizer.Form.NFD);
    geo.setEdificio(geo.getEdificio().replaceAll("[^\\p{ASCII}]", ""));
    geo.setIndirizzo(geo.getIndirizzo().replace("[^a-zA-Z\\d\\s:]",""));
    Normalizer.normalize(geo.getIndirizzo(), Normalizer.Form.NFD);
    geo.setIndirizzo(geo.getIndirizzo().replaceAll("[^\\p{ASCII}]", ""));
    */
}
