/*
 * Classe che contiene metodi utili alla gestione dei file
 */

package com.p4535992.util.file;

import com.p4535992.util.log.SystemLog;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Class with many utilities mathod for magage the file object.
 * @author 4535992
 */
public class FileUtil {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FileUtil.class);
    private static String fullPath;
    private static char pathSeparator = '\\';
    private static char extensionSeparator = '.';

    /**
     * Constructor .
     * @param f file of input
     */
    public FileUtil(File f) {
        FileUtil.fullPath = f.getAbsolutePath();
        FileUtil.pathSeparator = '/';
        FileUtil.extensionSeparator = '.';
    }

    /**
     * Constructor.
     * @param filePath string of the path to the file
     */
    public FileUtil(String filePath) {
        FileUtil.fullPath = filePath;
        FileUtil.pathSeparator = '/';
        FileUtil.extensionSeparator = '.';
    }

    /**
     * Constructor.
     * @param str string of the path to the file
     * @param sep path separator
     * @param ext extension separator (usually '.')
     */
    public FileUtil(String str, char sep, char ext) {
        FileUtil.fullPath = str;
        FileUtil.pathSeparator = sep;
        FileUtil.extensionSeparator = ext;
    }

    /**
     * Method for get the extension from a file.
     * @param f file of input
     * @return string of the extension of the file
     */
    public static String extension(File f) {
        return extension(f.getAbsolutePath());
    }
    /**
     * Method for get the extension from a file.
     * @param fullPath string of the path to the file
     * @return string of the extension of the file
     */
    public static String extension(String fullPath) {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }

    /**
     * Method for get the filename without extension.
     * @param f file of input
     * @return name of the file without the extension
     */
    public static String filenameNoExt(File f) { 
        return filenameNoExt(f.getAbsolutePath());
    }

    /**
     * Method for get the filename without extension.
     * @param fullPath string of the path to the file
     * @return name of the file without the extension
     */
    public static String filenameNoExt(String fullPath) { 
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }

    /**
     * Method for get the name of the file (with extensions).
     * @return name of the file
     */
    public static String filename() {     
        return new File(fullPath).getName();
    }

    /**
     * Method for get the name of the file (with extensions).
     * @param f file of input
     * @return name of the file
     */
    public static String filename(File f) {            
        return f.getName();
    }

    /**
     * Method for get the name of the file (with extensions).
     * @param fullPath string of the path to the file
     * @return name of the file
     */
    public static String filename(String fullPath) {             
        String name = fullPath.replace(FileUtil.path(fullPath), "");
        name = name.replace(File.separator,"");
        return name;
    }

    /**
     * Method for convert a absolut path to the file to a relative path.
     * @param base the base of the absolute path where you want start the 
     * relative path e.g. /var/data
     * @param absolutePath the full pth to the file e.g. /var/data/stuff/xyz.dat
     * @return the relative path to the file e.g. stuff/xyz.dat
     */
    public static String convertToRelativePath(String base,String absolutePath){
        return new File(base).toURI().relativize(new File(absolutePath).toURI()).getPath();
    }

    /**
     * Method for get the local path in the project.
     * @param localPath string of the absolute path to the file in the project.
     * @return the local path to the file in the project
     */
    public static String localPath(String localPath){
        return localPath("", localPath);
    }

    /**
     * Method for get the local path in the project.
     * @param basePath string of the absolute path to the direcotry of the project.
     * @param localPath string of the absolute path to the file in the project.
     * @return the local path to the file in the project
     */
    public static String localPath(String basePath,String localPath){
        basePath = basePath.replace(System.getProperty("user.dir"),"");
        return basePath+File.separator+localPath;
    }

    /**
     * Method for get the path of a file.
     * @return the path to the file
     */
    public static String path() {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }

    /**
     * Method for get the path of a file.
     * @param f file of input
     * @return the path to the file
     */
    public static String path(File f) {
       return path(f.getAbsolutePath());
    }

    /**
     * Method for get the path of a file.
     * @param fullPath string of the path to the file
     * @return the path to the file
     */
    public static String path(String fullPath) {
        //int sep = fullPath.lastIndexOf(pathSeparator);
        //String path = fullPath.substring(0, sep);
        //String fullPath = f.getAbsolutePath();
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }

    public static void createFile(String fullPath) throws IOException {
        File file = new File(fullPath);
        if (file.createNewFile()) {
            //System.out.println("File is created!");
        } else {
            //System.out.println("File already exists.");
        }
    }

    public static void copyFileCharStream(String fullPathInput, String fullPathOutput)
            throws FileNotFoundException, IOException {
        FileReader in = null;
        FileWriter out = null;
        try {
            in = new FileReader(fullPathInput);
            out = new FileWriter(fullPathOutput);

            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void copyFileByteStream(String fullPathInput, String fullPathOutput)
            throws FileNotFoundException, IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(fullPathInput);
            out = new FileOutputStream(fullPathOutput);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void createDirectory(String fullPathDir) {
        String dirname = fullPathDir;
        File d = new File(dirname);
        // Create directory now.
        d.mkdirs();
    }

    public static List<File> readDirectory(String fullPathDir) {
        File file = null;
        //File[] listOfFiles = new File(fullPathDir).listFiles();
        String[] paths;
        List<File> files = new ArrayList<>();
        try {
            // insert new file object
            file = new File(fullPathDir);
            // array of files and directory
            paths = file.list();
            // for each name in the path array
            for (String path : paths) {
                // prints filename and directory name
                files.add(new File(fullPathDir+File.separator+path));
            }
        } catch (Exception e) {
            // if any org.p4535992.mvc.error occurs
            e.printStackTrace();
        }
        return files;
    }

    /**
     * Removes all files from a given folder.
     * @param path string of the path to the file
     */
    public static void removeDirectory(String path)
    {
        File filePath = new File(path);
        if (filePath.exists()) {
            for (String fileInDirectory : filePath.list()) {
                File tmpFile = new File(path + "/" + fileInDirectory);
                tmpFile.delete();
            }
            filePath.delete();
        }
    }

    public static URI convertFileToUri(File file){
        return file.toURI();
    }

    public static URI convertFileToUri(String filePath){
        return convertFileToUri(new File(filePath));
    }

    public static File convertURIToFile(URI uri) throws MalformedURLException {
        return new File(uri.toURL().getFile());
    }

    public static InputStream convertURIToStream(URI uri) throws IOException {
        return uri.toURL().openStream();
        //is.close();
    }

    public static File convertResourceToFile(String referenceResourcePath) throws URISyntaxException{
        return new File(FileUtil.class.getClassLoader().getResource(referenceResourcePath).getFile());
    }

    /**
     * Convert filename string to a URI.
     * Map '\' characters to '/' (this might break if '\' is used in
     * a Unix filename, but this is assumed to be a very rare occurrence
     * as '\' is often used with special meaning on Unix.)
     * For unix-like systems, the absolute filename begins with a '/'
     * and is preceded by "file://".
     * For other systems an extra '/' must be supplied.
     * @param filePath string of the path to the file
     * @return path to the in uri formato with prefix file:///
     */
    public static String convertFileToStringUriWithPrefix(String filePath){
        StringBuffer mapfilename = new StringBuffer( filePath ) ;
        for ( int i = 0 ; i < mapfilename.length() ; i++ )
        {
            if ( mapfilename.charAt(i) == '\\' )
                mapfilename.setCharAt(i, '/') ;
        }
        if (filePath.charAt(0) == '/')
        {
            return "file://"+mapfilename.toString() ;
        }
        else
        {
            return "file:///"+mapfilename.toString() ;
        }
    }

    public static URI convertFileToUriWithPrefix(String filename){
        return URI.create(convertFileToStringUriWithPrefix(filename));
    }

    public static String convertFileToStringUriWithPrefix(File file){
        return convertFileToStringUriWithPrefix(file.getAbsolutePath());
    }

    public static URI convertFileToUriWithPrefix(File file){
        return URI.create(convertFileToStringUriWithPrefix(file.getAbsolutePath()));
    }


    /**
     * Method for get in more dinamica way the currentdirectory of the projct
     * equivalent to : dir = System.getProperty("user.dir");
     * @return string of the path to the user directory of the project
     */
    public static String getUserDir() {
        String dir;
        try {
            //1 Method
            //File currentDirFile = new File("");
            //dir = currentDirFile.getAbsolutePath();
            //2 Method
            dir = System.getProperty("user.dir")+File.separator;
            //dir = convertFileToUri2(dir)+"/";
            //dir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());
        } catch (Exception e) {
            dir = null;
        }
        return dir;
    }

    public static String getCurrentDisk() {
        String dir="";
        try {
            dir = getUserDir();
            String[] split = dir.split(":");
            dir = split[0];
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return dir + ":".toLowerCase();
    }


    public static String readStringFromFileLineByLine(String pathToFile) {
        StringBuilder stringBuffer = new StringBuilder();
        try
        {
            File file = new File(pathToFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\r\n");
            }
            fileReader.close();
            System.out.println("Contents of file:");
            System.out.println(stringBuffer.toString());
        }
        catch( IOException e)
        {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static Map<String,String> readStringFromFileLineByLine(String pathToFile, char separator, SimpleParameters params) {
        Map<String,String> map = new HashMap<>();
        try
        {
            File file = new File(pathToFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            String[] lines;
            List<String> linesSupport= new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if(line.trim().length() == 0 || line.contains("#")){
                    continue;
                }
                linesSupport.add(line.trim());
            }
            fileReader.close();
            lines = new String[ linesSupport.size()];
            linesSupport.toArray(lines);
            params.parseNameValuePairs(lines, separator, true);
            map = params.getParameters();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return map;
    }

    public static  InputStream convertFileToStream(String pathToFile) throws IOException{
        // JDK7 try-with-resources ensures to close stream automatically
        //try (InputStream is = getClass().getResourceAsStream(pathToFile)) {
        try (InputStream is = FileUtil.class.getResourceAsStream(pathToFile)) {
            int Byte;       // Byte because byte is keyword!
            while ((Byte = is.read()) != -1 ) {
                System.out.print((char) Byte);
            }
            return is;
        }
        //Alternative
//        URL resourceUrl = getClass().getResource("/sample.txt");
//        Path resourcePath = Paths.get(resourceUrl.toURI());
//        File f = new File("/spring-hibernate4v2.xml");
    }

    public static String getResourceAsString(String fileName) {
        StringBuilder result = new StringBuilder("");
        //Get file from resources folder
        //getClass().getResource("")
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        //File file = new File(FileUtil.class.getResource(fileName).getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            //scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static InputStream getResourceAsStream(Class<?> clazz,String name) {

        name = resolveName(name);
        ClassLoader cl = clazz.getClassLoader();
        if (cl==null) {
            // A system class.
            return ClassLoader.getSystemResourceAsStream(name);
        }
        return cl.getResourceAsStream(name);
    }

    /**
     * Add a package name prefix if the name is not absolute Remove leading "/"
     * if name is absolute.
     * @param name string name of the class
     * @return the full name package+class
     */
    public static String resolveName(String name) {
        if (name == null) {
            return name;
        }
        if (!name.startsWith("/")) {
            Class<?> c = FileUtil.class;
            while (c.isArray()) {
                c = c.getComponentType();
            }
            String baseName = c.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/')
                        +"/"+name;
            }
        } else {
            name = name.substring(1);
        }
        return name;
    }


    /**
     * Method for compress file of triple before upload to thte repository make
     * the upload more faster.
     * @param file file of input
     * @return inputstream of the file
     * @throws IOException file not found
     */
    public static InputStream compressFileForUpload(File file) throws IOException{
        return  new GZIPInputStream(new FileInputStream(file));
    }

    /**
     * Method for compress file of triple before upload to thte repository 
     * make the upload more faster.
     * @param filePathToFile string of the path tot the file
     * @return inputstream of the file
     * @throws IOException file not found
     */
    public static InputStream compressFileForUpload(String filePathToFile) throws IOException{
        File file = new File(filePathToFile);
        return compressFileForUpload(file);
    }

    /**
     * Utility for a depth first traversal of a file-system starting from a
     * given node (file or directory). e.g.
     * FileWalker.Handler handler = new FileWalker.Handler() {
     *
     * Override public void file(File file) throws Exception {
     * statementsLoaded.addAndGet( loadFileChunked(file) );
     * }
     * Override public void directory(File directory) throws Exception {
     * log("Loading files from: " + directory.getAbsolutePath());
     * }
     * };
     * FileWalker walker = new FileWalker();
     * walker.setHandler(handler);
     * walker.walk(new File(preload));
     */
    static public class FileWalker {
        /**
         * The call back interface for traversal.
         */
        public interface Handler {
            /**
             * Called to notify that a normal file has been encountered.
             *
             * @param file The file encountered.
             * @throws Exception error during the search. 
             */
            void file(File file) throws Exception;

            /**
             * Called to notify that a directory has been encountered.
             *
             * @param directory The directory encountered.
             * @throws Exception error during the search. 
             */
            void directory(File directory) throws Exception;
        }

        /**
         * Set the notification handler.
         *
         * @param handler The object that receives notifications of encountered nodes.
         */
        public void setHandler(Handler handler) {
            this.handler = handler;
        }

        /**
         * Start the walk at the given location, which can be a file, for a very
         * short walk, or a directory which will be traversed recursively.
         *
         * @param node The starting point for the walk.
         * @throws Exception error during the search. 
         */
        public void walk(File node) throws Exception{
            if (node.isDirectory()) {
                handler.directory(node);
                File[] children = node.listFiles();
                Arrays.sort(children, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                for (File child : children) {
                    walk(child);
                }
            } else {
                handler.file(node);
            }
        }
        private Handler handler;
    } //end of class FileWalker


    /////////////////////////////////////////
    //OTHER METHODS WITH COMMONS UTIL APACHE
    /////////////////////////////////////////


    /*public static File stream2fileWithUtil (InputStream in,String filename,String extension) throws IOException {
        String PREFIX = filename;
        String SUFFIX = "."+extension;
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            org.apache.commons.io.IOUtils.copy(in, out);
        }
        return tempFile;
    }*/

   /* public static String getStringFromResourceFileWithUtil(String fileName) {
        String result = "";
        //ClassLoader classLoader = getClass().getClassLoader();
        try {
            //result = org.apache.commons.io.IOUtils.toString(classLoader.getResourceAsStream(fileName));
            result = org.apache.commons.io.IOUtils.toString(FileUtil.class.getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/


    /////////////////////////////////
    //OTHER METHODS DERPECATED
    ///////////////////////////////

    /**
     * Get current working directory as a URI.
     */
    /*public static String uriFromCwd() {
        String cwd = System.getProperty("user.dir");
        return uriFromFilename( cwd ) + "/" ;
    }*/

    /**
     * Convert File descriptor string to a URI.
     */
   /* public static String uriFromFile(File filespec){
        return uriFromFilename( filespec.getAbsolutePath() ) ;
    }*/

    /**
     * Convert filename string to a URI.
     * Map '\' characters to '/' (this might break if '\' is used in
     * a Unix filename, but this is assumed to be a very rare occurrence
     * as '\' is often used with special meaning on Unix.)
     * For unix-like systems, the absolute filename begins with a '/'
     * and is preceded by "file://".
     * For other systems an extra '/' must be supplied.
     */
    /*public static String uriFromFilename(String filename){
        StringBuffer mapfilename = new StringBuffer( filename ) ;
        for ( int i = 0 ; i < mapfilename.length() ; i++ )
        {
            if ( mapfilename.charAt(i) == '\\' )
                mapfilename.setCharAt(i, '/') ;
        }
        if (filename.charAt(0) == '/')
        {
            return "file://"+mapfilename.toString() ;
        }
        else
        {
            return "file:///"+mapfilename.toString() ;
        }
    }*/



          
    
}
