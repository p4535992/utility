package com.github.p4535992.util.file;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.impl.StringIs;
import com.github.p4535992.util.string.impl.StringKit;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import static java.util.Arrays.*;

/**
 * Class with many utilities mathod for magage the file object.
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class FileUtilities {
    private static String fullPath;
    private static char pathSeparator = File.separatorChar;
    private static char extensionSeparator = '.';
    public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();


    protected FileUtilities() {}

    private static FileUtilities instance = null;

    public static FileUtilities getInstance(){
        if(instance == null) {
            instance = new FileUtilities();
        }
        return instance;
    }

    /**
     * Constructor .
     * @param f file of input
     */
    public FileUtilities(File f) {
        FileUtilities.fullPath = f.getAbsolutePath();
    }

    /**
     * Constructor.
     * @param filePath string of the path to the file
     */
    public FileUtilities(String filePath) {
        FileUtilities.fullPath = filePath;
    }

    /**
     * Constructor.
     * @param str string of the path to the file
     * @param separator path separator
     * @param extension extension separator (usually '.')
     */
    public FileUtilities(String str, char separator, char extension) {
        FileUtilities.fullPath = str;
        FileUtilities.pathSeparator = separator;
        FileUtilities.extensionSeparator = extension;
    }

    /**
     * Method for get the extension from a file.
     * @param f file of input
     * @return string of the extension of the file
     */
    public static String getExtension(File f) {return getExtension(f.getAbsolutePath());}

    /**
     * Method for get the extension from a file.
     * @param fullPath string of the path to the file
     * @return string of the extension of the file
     */
    public static String getExtension(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf(extensionSeparator) + 1);
    }

    /**
     * Method for get the filename without extension.
     * @param f file of input
     * @return name of the file without the extension
     */
    public static String getFilenameWithoutExt(File f) {
        return getFilenameWithoutExt(f.getAbsolutePath());
    }

    /**
     * Method for get the filename without extension.
     * @param fullPath string of the path to the file
     * @return name of the file without the extension
     */
    public static String getFilenameWithoutExt(String fullPath) {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }

    /**
     * Method for get the name of the file (with extensions).
     * @return name of the file
     */
    public static String getFilename() {return new File(fullPath).getName();}

    /**
     * Method for get the name of the file (with extensions).
     * @param f file of input
     * @return name of the file
     */
    public static String getFilename(File f) {return f.getName();}

    /**
     * Method for get the name of the file (with extensions).
     * @param fullPath string of the path to the file
     * @return name of the file
     */
    public static String getFilename(String fullPath) {
        String name;
        if (fullPath.contains(File.separator)) name = fullPath.replace(FileUtilities.getPath(fullPath), "");
        else name = fullPath;

        name = name.replace(File.separator, "");
        return name;
    }


    /**
     * Method for convert a absolute path to the file to a relative path.
     * @param base the base of the absolute path where you want start the
     * relative path e.g. /var/data
     * @param absolutePath the full pth to the file e.g. /var/data/stuff/xyz.dat
     * @return the relative path to the file e.g. stuff/xyz.dat
     */
    public static String getRelativePath(String base,String absolutePath){
        return new File(base).toURI().relativize(new File(absolutePath).toURI()).getPath();
    }

    /**
     * Method or get the local path in the project.
     * @param file File object.
     * @return the local path to the file in the project.
     */
    public static String getLocalPath(File file){ return getLocalPath("", file.getAbsolutePath());}

    /**
     * Method for get the local path in the project.
     * @param absolutePath string of the absolute path to the file in the project.
     * @return the local path to the file in the project
     */
    public static String getLocalPath(String absolutePath){return getLocalPath("", absolutePath);}

    /**
     * Method for get the local path in the project.
     * @param basePath string of the absolute path to the direcotry of the project.
     * @param localPath string of the absolute path to the file in the project.
     * @return the local path to the file in the project
     */
    public static String getLocalPath(String basePath,String localPath){
        basePath = basePath.replace(System.getProperty("user.dir"),"");
        return basePath+File.separator+localPath;
    }

    /**
     * Method for get the path of a file.
     * @return the path to the file
     */
    public static String getPath() {return fullPath.substring(0, fullPath.lastIndexOf(File.separator));}

    /**
     * Method for get the path of a file.
     * @param f file of input
     * @return the path to the file
     */
    public static String getPath(File f) {return getPath(f.getAbsolutePath());}

    /**
     * Method for get the path of a file.
     * @param fullPath string of the path to the file
     * @return the path to the file
     */
    public static String getPath(String fullPath) {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));}


    /**
     * Method to create a new File Object in a specific path.
     * @param fullPath String output location of the new File .
     * @return the new File object.
     */
    public static File createFile(String fullPath){
        return createFile(new File(fullPath));}


    /**
     * Method to create a new File Object in a specific path.
     * @param file File output location of the new File .
     * @return the new File object.
     */
    public static File createFile(File file) {
        try {
            if(file.createNewFile())return file;
            else  return null;
        } catch (IOException e) {
            SystemLog.exception(e, FileUtilities.class);
            return null;
        }
    }

    /**
     * Method to copy the content from a file to another in char format.
     * @param fullPathInput string path to the file you want to read the copy.
     * @param fullPathOutput string path to the file you want write the copy.
     * @throws IOException throw if any error is occurrred.
     */
    public static void copyFile(String fullPathInput, String fullPathOutput) throws IOException {
        //FileReader in = null;
        //FileWriter out = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            //char...
            //in = new FileReader(fullPathInput);
            //out = new FileWriter(fullPathOutput);
            //bytes...
            in = new FileInputStream(fullPathInput);
            out = new FileOutputStream(fullPathOutput);

            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    /**
     * Method to create a directory.
     * @param fullPathDir string path to the location of the directory.
     * @return if true you have created the directory.
     */
    public static boolean createDirectory(String fullPathDir) {
        File d = new File(fullPathDir);
        return d.mkdirs();
    }

    /**
     * Method to read all file ina direcotry/folder.
     * @param directory file of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> readDirectory(File directory) {
        return readDirectory(directory.getAbsolutePath());
    }

    /**
     * Method to read all file ina direcotry/folder.
     * @param fullPathDir string path to the loaction of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> readDirectory(String fullPathDir) {
        File file;
        String[] paths;
        List<File> files = new ArrayList<>();
        try {
            paths =  new File(fullPathDir).list();
            for (String path : paths) {
                files.add(new File(fullPathDir+File.separator+path));
            }
        } catch (Exception e) {
            SystemLog.exception(e);
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
                if(!tmpFile.delete()){
                    SystemLog.warning("Can't delete the file:"+tmpFile.getAbsolutePath());
                }
            }
            if(!filePath.delete()){
                SystemLog.warning("Can't delete the file:"+filePath.getAbsolutePath());
            }
        }
    }

    /**
     * Method to convert a File to a URI
     * @param file the File to convert.
     * @return the URI.
     */
    public static URI toUri(File file){
        return file.toURI();
    }

    /**
     * Method to convert a File to a URI
     * @param filePath the String to the File to convert.
     * @return the URI.
     */
    public static URI toUri(String filePath) {
        return toUri(new File(filePath));
    }

    /**
     * Method to convert a File to a URL
     * @param file the File to convert.
     * @return the URL.
     * @throws java.net.MalformedURLException if any error is occurred.
     */
    public static URL toURL(File file) throws MalformedURLException {return toUri(file).toURL();}

    /**
     * Method to convert a File to a URL
     * @param filePath the String to the File to convert.
     * @return the URL.
     * @throws java.net.MalformedURLException if any error is occurrred.
     */
    public static URL toURL(String filePath)throws MalformedURLException{return toURL(new File(filePath));}

    /**
     * Method to convert a URI to a File.
     * @param uri the URI to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     */
    public static File toFile(URI uri) throws MalformedURLException {return new File(uri.toURL().getFile());}

    /**
     * Method to convert a URI to a File.
     * @param url the URL to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     * @throws URISyntaxException throw if any error is occurred.
     */
    public static File toFile(URL url) throws URISyntaxException, MalformedURLException {
        return toFile(url.toURI());
    }

    /**
     * Method to convert a URI to a File.
     * @param url the URL to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     * @throws URISyntaxException throw if any error is occurred.
     */
    public static File toFile(String url) throws URISyntaxException, MalformedURLException {
        return toFile(new URL(url).toURI());
    }

    /**
     * Method to convert a URI to Stream.
     * @param uri  the URI to convert.
     * @return the Stream.
     * @throws IOException throw if any error is occurred.
     */
    public static InputStream toStream(URI uri) throws IOException {return uri.toURL().openStream();}

    /**
     * Method to convert a File to a URI with the prefix file://.
     * @param filename the String path tot the the File to convert.
     * @return the URI with prefix.
     */
    public static URI toUriWithPrefix(String filename){
        return URI.create(toStringUriWithPrefix(filename));
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     * @param file the File to convert.
     * @return the String URI with prefix.
     */
    public static String toStringWithPrefix(File file){
        return toStringUriWithPrefix(file.getAbsolutePath());
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     * @param file the File to convert.
     * @return the URI with prefix.
     */
    public static URI toUriWithPrefix(File file){
        return URI.create(toStringUriWithPrefix(file.getAbsolutePath()));
    }

    /**
     * Method for convert a reference path to a resource in the classpath to a file with path in the system.
     * @param referenceResourcePath string of the reference path to the resource.
     * @param thisClass thi class.
     * @return file correspondent to the reference file of the resources.
     */
    public static File toFile(String referenceResourcePath,Class<?> thisClass){
        try {
            return new File(thisClass.getClassLoader().getResource(referenceResourcePath).getFile());
        }catch(NullPointerException ne){
            SystemLog.exception(ne);
            return null;
        }
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
    private static String toStringUriWithPrefix(String filePath){
        StringBuilder mapfilename = new StringBuilder( filePath ) ;
        for ( int i = 0 ; i < mapfilename.length() ; i++ ) {
            if ( mapfilename.charAt(i) == '\\' )
                mapfilename.setCharAt(i, '/') ;
        }
        if (filePath.charAt(0) == '/') return "file://"+mapfilename.toString() ;
        else  return "file:///"+mapfilename.toString() ;
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

    /**
     * Method to get the directory name where a file is allocated
     * @param file the File Object.
     * @return the String name of the directory where the file is allocated.
     */
    public static String getDirectoryName(File file){
        if(file.exists()) {
            return file.getParent();
        }
        return null;
    }

    /**
     * Method to get the directory File where a file is allocated
     * @param file the File Object.
     * @return the File of the directory where the file is allocated.
     */
    public static File getDirectoryFile(File file){
        if(file.exists()) {
            return file.getParentFile();
        }
        return null;
    }

    public static File getDirectoryFile(String fullPathFile) {
        return getDirectoryFile(new File(fullPathFile));
    }

    public static String getDirectoryFullPath(File file){
        if(file.exists()) {
            return file.getAbsoluteFile().getParentFile().getAbsolutePath();
        }
        return null;
    }

    public static String getDirectoryFullPath(String fullPathFile){
        return getDirectoryFullPath(new File(fullPathFile));
    }

    /**
     * Method to get the letter fo teh current disk where the file is located.
     * @return the letter of the disk.
     */
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

    /**
     * Method to convert a String content to a Temporary File.
     * @param content the String content.
     * @param fullPath the String output path for the temporary File.
     * @return the temporary File.
     */
    public static File toTempFile(String content,String fullPath){
        try {
            File file = File.createTempFile(
                    getFilename(fullPath),
                    getExtension(fullPath),
                    getDirectoryFile(fullPath)
            );
            // Delete temp file when program exits.
            file.deleteOnExit();
            try ( //Writer writer = new FileWriter(file);
                  //PrintWriter out = new PrintWriter(writer);
                  //out.println(content);
                  BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(content);
            }
            return file;
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Method to read the content of a file line by line.
     * @param pathToFile the String path to the file to read.
     * @return the String content ofd the file.
     */
    public static String readFile(String pathToFile) {
        return readFile(new File(pathToFile));
    }

    /**
     * Method to read the cotnetn of a file line by line.
     * @param file the file to read.
     * @return the String content ofd the file.
     */
    public static String readFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            //File file = new File(pathToFile);
            try (FileReader fileReader = new FileReader(file)) {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line.trim());
                    //stringBuilder.append("\r\n");
                }
            }
            //System.out.println("Contents of file:");
            //System.out.println(stringBuilder.toString());
        }catch( IOException e){
            SystemLog.exception(e);
        }
        return stringBuilder.toString();
    }

    /**
     * Method to read the cotnetn of a file line by line.
     * @param pathToFile the file to read.
     * @param separator the Char separator.
     * @param params map of parameters and value.
     * @return the String content ofd the file.
     */
    public static Map<String,String> readFile(String pathToFile, char separator, SimpleParameters params) {
        Map<String,String> map = new HashMap<>();
        try{
            File file = new File(pathToFile);
            String[] lines;
            List<String> linesSupport;
            try (FileReader fileReader = new FileReader(file)) {
                BufferedReader br = new BufferedReader(fileReader);
                String line;
                linesSupport = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    if(line.trim().length() == 0 || line.contains("#")){
                        continue;
                    }
                    linesSupport.add(line.trim());
                }
            }
            lines = new String[ linesSupport.size()];
            linesSupport.toArray(lines);
            params.parseNameValuePairs(lines, separator, true);
            map = params.getParameters();
        }catch(IOException e){
            SystemLog.exception(e);
        }
        return map;
    }

    /**
     * Method to copy a file.
     * @param destination the String destination for the copy of the file.
     * @param source the String source of the File to copy.
     * @throws IOException throw if any error is occurrred.
     */
    public static void copyFile(File destination, File source) throws IOException {
        if (!destination.exists()) createFile(destination);
        OutputStream out;
        try (InputStream in = new FileInputStream(source)) {
            out = new FileOutputStream(destination);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) { out.write(buf, 0, len);}
        }
        out.close();
        SystemLog.message("Done copying contents of " + source.getName() + " to " + destination.getName());
    }

    /**
     * Method to convert a resource file to a Stream.
     * @param pathToFile String path to the Resource File to read(reference path).
     * @return the Stream of the File.
     */
    public static InputStream toStream(String pathToFile) {
        // JDK7 try-with-resources ensures to close stream automatically
        // return new FileInputStream(new File(pathToFile));
        try (InputStream is = FileUtilities.class.getResourceAsStream(pathToFile)) {
            int Byte; // Byte because byte is keyword!
            while ((Byte = is.read()) != -1 ) {
                System.out.print((char) Byte);
            }
            return is;
        } catch (IOException e) {
            SystemLog.warning("The file:" + pathToFile + " not exists!!!");
            return null;
        }
        //Alternative
//        URL resourceUrl = getClass().getResource("/sample.txt");
//        Path resourcePath = Paths.get(resourceUrl.toURI());
//        File f = new File("/spring-hibernate4v2.xml");
    }


    /**
     * Method to covnert a resource file to a Stream.
     * @param file File to read.
     * @return the Stream of the File.
     */
    public static InputStream toStream(File file){
        try {
            return new FileInputStream(file);
        }catch(FileNotFoundException e){
            SystemLog.warning("The file:" + file.getAbsolutePath() + " not exists!!!");
            return null;
        }
    }

    /**
     * Method to convert a Stream to a File.
     * @param inStream the Inputstream to decode.
     * @param filePathOutput the String path the the new location of the file.
     * @return the File Object.
     */
    public static File toFile(InputStream inStream,String filePathOutput) {
        try(OutputStream outputStream = new FileOutputStream(new File(filePathOutput))) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inStream.read(bytes)) != -1) { outputStream.write(bytes, 0, read);}
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        if(new File(filePathOutput).exists()) return new File(filePathOutput);
        else{
            SystemLog.warning("The file:"+ new File(filePathOutput).getAbsolutePath() +" not exists!!!");
            return null;
        }
    }

    /**
     * Method to covnert a resource file to a Stream.
     * @param fileName String name of the Resource File to read(reference path).
     * @param clazz the Class who call this method.
     * @return the Stream of the File..
     */
    public static String toStringContent(String fileName,Class<?> clazz) {
        try {
            StringBuilder result = new StringBuilder("");
            //Get file from resources folder
            File file = new File(clazz.getClassLoader().getResource(fileName).getFile());
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.append(line).append("\n");
                }
            } catch (IOException e) {
                SystemLog.exception(e);
            }
            return result.toString();
        }catch(NullPointerException ne){
            SystemLog.exception(ne);
            return null;
        }
    }

    /**
     * Method to convert a resource file to a Stream.
     * @param name String name of the class
     * @param clazz the Class who call this method.
     * @return the Stream of the File.
     */
    public static InputStream toStream(String name,Class<?> clazz) {
        name = resolveName(name);
        try {
            // A system class.
            return ClassLoader.getSystemResourceAsStream(name);
        }catch(NullPointerException e) {
            try {
                return clazz.getClassLoader().getResourceAsStream(name);
            }catch(NullPointerException ne){
                SystemLog.exception(ne);
                return null;
            }
        }
    }

    /**
     * Add a package name prefix if the name is not absolute Remove leading "/"
     * if name is absolute.
     * @param name string name of the class
     * @return the full name package+class
     */
    private static String resolveName(String name) {
        if (StringIs.isNullOrEmpty(name))  return name;
        if (!name.startsWith("/")) {
            Class<?> clazz = FileUtilities.class;
            while (clazz.isArray()) { clazz = clazz.getComponentType();}
            String baseName = clazz.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1)  name = baseName.substring(0, index).replace('.', '/')+"/"+name;
        } else {
            name = name.substring(1);
        }
        return name;
    }

    /**
     * Method for compress file of triple before upload to thte repository make
     * the upload more faster.
     * @param file file of input
     * @return InputStream of the file
     */
    public static GZIPInputStream toGZIP(File file){
        try {
            return  new GZIPInputStream(new FileInputStream(file));
        } catch (IOException e) {
            SystemLog.exception(e,FileUtilities.class);
            return null;
        }
    }

    /**
     * Method for compress file of triple before upload to the repository
     * make the upload more faster.
     * @param filePathToFile string of the path tot the file
     * @return InputStream of the file
     */
    public static GZIPInputStream toGZIP(String filePathToFile){
        return toGZIP(new File(filePathToFile));
    }

    /**
     * Method for check is a file is a directory/folder.
     * @param file the file to inspect.
     * @return if true is a directory else ia simple file.
     */
    public static boolean isDirectory(File file){
        if(file.exists()) {
            return !file.isFile() && file.isDirectory();
        }else{
            SystemLog.warning("The file:"+file.getAbsolutePath()+" not exists!");
            return false;
        }
    }

    /**
     * Method utility: help to coded the content of the file.
     * @param file the File to code.
     * @param algorithm the Hash algorithm you use.
     * @return the String content of the file coded.
     */
    private static String hashFile(File file, String algorithm){
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytesBuffer = new byte[1024];
            int bytesRead;//= -1
            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }
            byte[] hashedBytes = digest.digest();
            return StringKit.convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException ex) {
            SystemLog.error("Could not generate hash from file");
            SystemLog.exception(ex);
            return null;
        }
    }

    /**
     * Method to convert a File to a MD5 hash string.
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String toMD5(File file) {
        return hashFile(file, "MD5");
    }

    /**
     * Method to convert a File to a SHA-1 hash string.
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String toSHA1(File file) {
        return hashFile(file, "SHA-1");
    }

    /**
     * Method to convert a File to a SHA-256 hash string.
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String toSHA256(File file)  {
        return hashFile(file, "SHA-256");
    }

    /**
     * Method to convert a String to a File.
     * @param stringText the String content.
     * @param fullPathfile the String to the new location of the File.
     * @return the File Object.
     */
    public static File toFile(String stringText,String fullPathfile){
        return toFile(stringText, new File(fullPathfile));
    }

    /**
     * Method to convert a String to a File.
     * @param stringText the String content.
     * @param file the  File.
     * @return the File Object.
     */
    public static File toFile(String stringText,File file){
        return writeToFile(stringText, file);
    }

    /**
     * Method to convert a File to a Writer Object.
     * @param file the File to convert.
     * @return the Writer Object.
     */
    public static Writer toWriter(File file){
        Writer writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return writer;
    }

    /**
     * Method to convert a File to a Reader Object.
     * @param file the File to convert.
     * @return the Writer Object.
     */
    public static Reader toReader(File file){
        Reader reader = null;
        try {
            reader = new FileReader(file);
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return reader;
    }

    /**
     * Saves a string to a file.
     * @param str string to write on the file.
     * @param fileName string name of the file.
     */
    private static File writeToFile(String str, File fileName) {
        try {
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            BufferedWriter bw = new BufferedWriter(fw);
            try (PrintWriter outWriter = new PrintWriter(bw)) {
                outWriter.println(str);
            }
        } catch (UnsupportedEncodingException|FileNotFoundException e) {
            SystemLog.exception(e);
        }
        return fileName;
    }

    /**
     * Locate the specific file.
     * Return the (URL decoded) abolute pathname to the file or null.
     * @param findFile  the String name of file to search.
     * @return the String path to the file.
     * @throws java.io.FileNotFoundException throw if any error is occurrred.
     */
    public static String locateFile (String findFile,String basePath) throws FileNotFoundException {
        URL url;
        String fullPathName;
        StringBuffer decodedPathName;
        int pos, len, start;
        if (findFile == null)throw new FileNotFoundException("locateFile: null file name");
        if (findFile.startsWith(basePath)) return findFile.substring(basePath.length());
        if ((fullPathName = locateByProperty(findFile)) != null)return fullPathName;
        if ((url = locateByResource(findFile)) != null) {
          /*
           * The URL that we receive from getResource /might/ have ' '
           * (space) characters converted to "%20" strings.  However,
           * it doesn't have other URL encoding (e.g '+' characters are
           * kept intact), so we'll just convert all "%20" strings to
           * ' ' characters and hope for the best.
           */
            fullPathName = url.getFile();
            //pos = 0;
            len = fullPathName.length();
            start = 0;
            decodedPathName = new StringBuffer();

            while ((pos = fullPathName.indexOf("%20", start)) != -1) { //pct = %20
                decodedPathName.append(fullPathName.substring(start, pos));
                decodedPathName.append(' ');
                start = pos + 3; //pct.length = 3
            }
            if (start < len)decodedPathName.append(fullPathName.substring(start, len));
            fullPathName=decodedPathName.toString();
            if (platformIsWindows())fullPathName = fullPathName.substring(1, fullPathName.length());
            return fullPathName;
        }
        throw new FileNotFoundException("locateFile: file not found: " + findFile);
    }

    /**
     * Locate the specific file.
     * Return the file name in URL form or null.
     * @param findFile  the String name of file to search.
     * @param basePath the string prefix of the findFile e.g. "abs://"
     * @return the String path to the file.
     * @throws java.io.FileNotFoundException throw if any error is occurrred.
     */
    public static URL locateURL (String findFile,String basePath) throws FileNotFoundException {
        URL url;
        String fullPathName;
        if (findFile == null) throw new FileNotFoundException("locateURL: null file name");
        try {
            if (findFile.startsWith(basePath)) {
                return (new URL("file:/" + findFile.substring(basePath.length())));
            }
            if ((fullPathName = locateByProperty(findFile)) != null) {
                if(platformIsWindows())url = new URL("file:/" + fullPathName);
                else url = new URL("file:" + fullPathName);
                return url;
            }
        }catch (MalformedURLException e) {
            System.err.println("locateURL: URL creation problem");
            throw new FileNotFoundException("locateURL: URL creation problem");
        }
        if ((url = locateByResource(findFile)) != null)return url;
        throw new FileNotFoundException("locateURL: file not found: " + findFile);
    }

    /**
     * Search for a file using the properties: user.dir, user.home, java.home
     * Returns absolute path name or null.
     * @param findFile  the String name of file to search.
     * @return the String path to the file.
     */
    private static synchronized String locateByProperty(String findFile) {
        String fullPathName = null;
        String dir;
        File f = null;
        if (findFile == null) return null;

        try {
            // System.err.println("Searching in user.dir for: " + findFile);
            dir = System.getProperty("user.dir");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                // System.err.println("Found in user.dir");
                return fullPathName;
            }
            dir = System.getProperty("user.home");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                // System.err.println("Found in user.home");
                return fullPathName;
            }
            dir = System.getProperty("java.home");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()){
                // System.err.println("Found in java.home");
                return fullPathName;
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Search for a file using the properties: user.dir, user.home, java.home
     * Returns URL or null.
     * @param findFile  the String name of file to search.
     * @return the String path to the file.
     */
    private static URL locateByResource(String findFile){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(findFile);
        if (url == null)  url = FileUtilities.class.getResource("/" + findFile);
        // System.err.println("Search succeeded via getResource()");
        return url;
    }

    /**
     * Check the file separator to see if we're on a Windows platform.
     * @return  boolean True if the platform is Windows, false otherwise.
     */
    private static boolean platformIsWindows() {return File.separatorChar == '\\';}

    /**
     * Method to get the String array of the columns of a CSV File.
     * @param fileCSV the File CSV.
     * @param hasFirstLine if ture the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] getColumns(File fileCSV,boolean hasFirstLine){
        String[] columns = new String[0];
        try {
            com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(fileCSV));
            columns = reader.readNext(); // assuming first read
            if(!hasFirstLine){
                int columnCount =0;
                if (columns != null)  columnCount = columns.length;
                columns = new String[columnCount];
                for(int  i=0; i< columnCount; i++){
                    columns[i] = "Column#"+i;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return columns;
    }

    /**
     * Method to convert a MultipartFile to a File
     * @param multiPartFile the MultiPartFile of Spring to convert.
     * @return the File.
     * @throws IOException
     */
    public File toFile(org.springframework.web.multipart.MultipartFile multiPartFile) throws IOException {
        File convFile = new File(multiPartFile.getOriginalFilename());
        /*convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile); fos.write(multiPartFile.getBytes());fos.close();*/
        multiPartFile.transferTo(convFile);
        return convFile;
    }

    /**
     * Method to 'walk' within many directory under a root directory and load alll files in these..
     * @param preload the String path to the root directory
     */
    public static List<File> walk(String preload){
        return walk(new File(preload));
    }

    /**
     * Method to 'walk' within many directory under a root directory and load alll files in these..
     * @param preload the File root directory
     */
    public static List<File> walk(File preload){
        final List<File> listFiles = new ArrayList<>();
        FileWalker.Handler handler = new FileWalker.Handler() {
            @Override
            public void file(File file) throws Exception {
                listFiles.add(file);
            }
            @Override
            public void directory(File directory) throws Exception {
                System.out.println("Loading files from: " + directory.getAbsolutePath());
            }
        };
        FileWalker walker = new FileWalker();
        walker.setHandler(handler);
        try {
            walker.walk(preload);
        } catch (Exception e) {
            System.out.println("Error during the Search");
        }
        return listFiles;
    }




    //////////////////////////////////////////////////////////////////////////////////////////////

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
                //Arrays.sort --> sort
                if (children != null) {
                    sort(children, new Comparator<File>() {
                        @Override
                        public int compare(File lhs, File rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    for (File child : children) {
                        walk(child);
                    }
                }
            } else {
                handler.file(node);
            }
        }
        private Handler handler;
    } //end of class FileWalker


    /////////////////////////////////////////
    //OTHER METHODS WITH COMMONS UTIL APACHE COMMONS
    /////////////////////////////////////////


    /*public static File toFile(InputStream in,String filename,String extension) throws IOException {
        String PREFIX = filename;
        String SUFFIX = "."+extension;
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            org.apache.commons.io.IOUtils.copy(in, out);
        }
        return tempFile;
    }*/

   /*public static String toStringContent(String fileResourcePath,Class<?> thisClass) {
        String result = "";
        //ClassLoader classLoader = getClass().getClassLoader();
        try {
            //result = org.apache.commons.io.IOUtils.toString(classLoader.getResourceAsStream(fileName));
            result = org.apache.commons.io.IOUtils.toString(thisClass.getResourceAsStream(fileResourcePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/

    //------------------------------------------------------------------------------------------------------
    private static Map<String,String> unicodeCodePoint = new HashMap<>();

    /**
     * Map for brute force replace of all unicode escape on the text
     */
    private static void setMapUnicodeEscaped(){
        unicodeCodePoint.put("U+0000","");unicodeCodePoint.put("U+0001","");unicodeCodePoint.put("U+0002","");unicodeCodePoint.put("U+0003","");
        unicodeCodePoint.put("U+0004","");unicodeCodePoint.put("U+0005","");unicodeCodePoint.put("U+0006","");unicodeCodePoint.put("U+0007","");
        unicodeCodePoint.put("U+0008","");unicodeCodePoint.put("U+0009","");unicodeCodePoint.put("U+000A","");unicodeCodePoint.put("U+000B","");
        unicodeCodePoint.put("U+000C","");unicodeCodePoint.put("U+000D","");unicodeCodePoint.put("U+000E","");unicodeCodePoint.put("U+000F","");
        unicodeCodePoint.put("U+0010","");unicodeCodePoint.put("U+0011","");unicodeCodePoint.put("U+0012","");unicodeCodePoint.put("U+0013","");
        unicodeCodePoint.put("U+0014","");unicodeCodePoint.put("U+0015","");unicodeCodePoint.put("U+0016","");unicodeCodePoint.put("U+0017","");
        unicodeCodePoint.put("U+0018","");unicodeCodePoint.put("U+0019","");unicodeCodePoint.put("U+001A","");unicodeCodePoint.put("U+001B","");
        unicodeCodePoint.put("U+001C","");unicodeCodePoint.put("U+001D","");unicodeCodePoint.put("U+001E","");unicodeCodePoint.put("U+001F","");
        unicodeCodePoint.put("U+0020"," ");unicodeCodePoint.put("U+0021","!");unicodeCodePoint.put("U+0022","\"");unicodeCodePoint.put("U+0023","#");
        unicodeCodePoint.put("U+0024","$");unicodeCodePoint.put("U+0025","%");unicodeCodePoint.put("U+0026","&");unicodeCodePoint.put("U+0027","'");
        unicodeCodePoint.put("U+0028","(");unicodeCodePoint.put("U+0029",")");unicodeCodePoint.put("U+002A","*");unicodeCodePoint.put("U+002B","+");
        unicodeCodePoint.put("U+002C",",");unicodeCodePoint.put("U+002D","-");unicodeCodePoint.put("U+002E",".");unicodeCodePoint.put("U+002F","/");
        unicodeCodePoint.put("U+0030","0");unicodeCodePoint.put("U+0031","1");unicodeCodePoint.put("U+0032","2");unicodeCodePoint.put("U+0033","3");
        unicodeCodePoint.put("U+0034","4");unicodeCodePoint.put("U+0035","5");unicodeCodePoint.put("U+00FF","ÿ");unicodeCodePoint.put("U+FEFF","");
        unicodeCodePoint.put("U+0036","6");unicodeCodePoint.put("U+0037","7");unicodeCodePoint.put("U+0038","8");unicodeCodePoint.put("U+0039","9");
        unicodeCodePoint.put("U+003A",":");unicodeCodePoint.put("U+003B",";");unicodeCodePoint.put("U+003C","<");unicodeCodePoint.put("U+003D","=");
        unicodeCodePoint.put("U+003E",">");unicodeCodePoint.put("U+003F","?");unicodeCodePoint.put("U+0040","@");unicodeCodePoint.put("U+0041","A");
        unicodeCodePoint.put("U+0042","B");unicodeCodePoint.put("U+0043","C");unicodeCodePoint.put("U+0044","D");unicodeCodePoint.put("U+0045","E");
        unicodeCodePoint.put("U+0046","F");unicodeCodePoint.put("U+0047","G");unicodeCodePoint.put("U+0048","H");unicodeCodePoint.put("U+0049","I");
        unicodeCodePoint.put("U+004A","J");unicodeCodePoint.put("U+004B","K");unicodeCodePoint.put("U+004C","L");unicodeCodePoint.put("U+004D","M");
        unicodeCodePoint.put("U+004E","N");unicodeCodePoint.put("U+004F","O");unicodeCodePoint.put("U+0050","P");unicodeCodePoint.put("U+0051","Q");
        unicodeCodePoint.put("U+0052","R");unicodeCodePoint.put("U+0053","S");unicodeCodePoint.put("U+0054","T");unicodeCodePoint.put("U+0055","U");
        unicodeCodePoint.put("U+0056","V");unicodeCodePoint.put("U+0057","W");unicodeCodePoint.put("U+0058","X");unicodeCodePoint.put("U+0059","Y");
        unicodeCodePoint.put("U+005A","Z");unicodeCodePoint.put("U+005B","[");unicodeCodePoint.put("U+005C","\\");unicodeCodePoint.put("U+005D","]");
        unicodeCodePoint.put("U+005E","^");unicodeCodePoint.put("U+005F","_");unicodeCodePoint.put("U+0060","`");unicodeCodePoint.put("U+0061","a");
        unicodeCodePoint.put("U+0062","b");unicodeCodePoint.put("U+0063","c");unicodeCodePoint.put("U+0064","d");unicodeCodePoint.put("U+0065","e");
        unicodeCodePoint.put("U+0066","f");unicodeCodePoint.put("U+0067","g");unicodeCodePoint.put("U+0068","h");unicodeCodePoint.put("U+0069","i");
        unicodeCodePoint.put("U+006A","j");unicodeCodePoint.put("U+006B","k");unicodeCodePoint.put("U+006C","l");unicodeCodePoint.put("U+006D","m");
        unicodeCodePoint.put("U+006E","n");unicodeCodePoint.put("U+006F","o");unicodeCodePoint.put("U+0070","p");unicodeCodePoint.put("U+0071","q");
        unicodeCodePoint.put("U+0072","r");unicodeCodePoint.put("U+0073","s");unicodeCodePoint.put("U+0074","t");unicodeCodePoint.put("U+0075","u");
        unicodeCodePoint.put("U+0076","v");unicodeCodePoint.put("U+0077","w");unicodeCodePoint.put("U+0078","x");unicodeCodePoint.put("U+0079","y");
        unicodeCodePoint.put("U+007A","z");unicodeCodePoint.put("U+007B","{");unicodeCodePoint.put("U+007C","|");unicodeCodePoint.put("U+007D","}");
        unicodeCodePoint.put("U+007E","~");unicodeCodePoint.put("U+007F","");unicodeCodePoint.put("U+0080","");unicodeCodePoint.put("U+0081","");
        unicodeCodePoint.put("U+0082","");unicodeCodePoint.put("U+0083","");unicodeCodePoint.put("U+0084","");unicodeCodePoint.put("U+0085","");
        unicodeCodePoint.put("U+0086","");unicodeCodePoint.put("U+0087","");unicodeCodePoint.put("U+0088","");unicodeCodePoint.put("U+0089","");
        unicodeCodePoint.put("U+008A","");unicodeCodePoint.put("U+008C","");unicodeCodePoint.put("U+008D","");unicodeCodePoint.put("U+008E","");
        unicodeCodePoint.put("U+008F","");unicodeCodePoint.put("U+0090","");unicodeCodePoint.put("U+0091","");unicodeCodePoint.put("U+0092","");
        unicodeCodePoint.put("U+0093","");unicodeCodePoint.put("U+0094","");unicodeCodePoint.put("U+0095","");unicodeCodePoint.put("U+0096","");
        unicodeCodePoint.put("U+0097","");unicodeCodePoint.put("U+0098","");unicodeCodePoint.put("U+0099","");unicodeCodePoint.put("U+009A","");
        unicodeCodePoint.put("U+009B","");unicodeCodePoint.put("U+009C","");unicodeCodePoint.put("U+009D","");unicodeCodePoint.put("U+009E","");
        unicodeCodePoint.put("U+009F","");unicodeCodePoint.put("U+00A0","");unicodeCodePoint.put("U+00A1","¡");unicodeCodePoint.put("U+00A2","¢");
        unicodeCodePoint.put("U+00A3","£");unicodeCodePoint.put("U+00A4","¤");unicodeCodePoint.put("U+00A5","¥");unicodeCodePoint.put("U+00A6","¦");
        unicodeCodePoint.put("U+00A7","§");unicodeCodePoint.put("U+00A8","¨");unicodeCodePoint.put("U+00A9","©");unicodeCodePoint.put("U+00AA","ª");
        unicodeCodePoint.put("U+00AB","«");unicodeCodePoint.put("U+00AC","¬");unicodeCodePoint.put("U+00AD","­");unicodeCodePoint.put("U+00AE","®");
        unicodeCodePoint.put("U+00AF","¯");unicodeCodePoint.put("U+00B0","°");unicodeCodePoint.put("U+00B1","±");unicodeCodePoint.put("U+00B2","²");
        unicodeCodePoint.put("U+00B3","³");unicodeCodePoint.put("U+00B4","´");unicodeCodePoint.put("U+00B5","µ");unicodeCodePoint.put("U+00B6","¶");
        unicodeCodePoint.put("U+00B7","·");unicodeCodePoint.put("U+00B8","¸");unicodeCodePoint.put("U+00B9","¹");unicodeCodePoint.put("U+00BA","º");
        unicodeCodePoint.put("U+00BB","»");unicodeCodePoint.put("U+00BC","¼");unicodeCodePoint.put("U+00BD","½");unicodeCodePoint.put("U+00BE","¾");
        unicodeCodePoint.put("U+00BF","¿");unicodeCodePoint.put("U+00C0","À");unicodeCodePoint.put("U+00C1","Á");unicodeCodePoint.put("U+00C2","Â");
        unicodeCodePoint.put("U+00C3","Ã");unicodeCodePoint.put("U+00C4","Ä");unicodeCodePoint.put("U+00C5","Å");unicodeCodePoint.put("U+00C6","Æ");
        unicodeCodePoint.put("U+00C7","Ç");unicodeCodePoint.put("U+00C8","È");unicodeCodePoint.put("U+00C9","É");unicodeCodePoint.put("U+00CA","Ê");
        unicodeCodePoint.put("U+00CB","Ë");unicodeCodePoint.put("U+00CC","Ì");unicodeCodePoint.put("U+00CD","Í");unicodeCodePoint.put("U+00CE","Î");
        unicodeCodePoint.put("U+00CF","Ï");unicodeCodePoint.put("U+00D0","Ð");unicodeCodePoint.put("U+00D1","Ñ");unicodeCodePoint.put("U+00D2","Ò");
        unicodeCodePoint.put("U+00D3","Ó");unicodeCodePoint.put("U+00D4","Ô");unicodeCodePoint.put("U+00D5","Õ");unicodeCodePoint.put("U+00D6","Ö");
        unicodeCodePoint.put("U+00D7","×");unicodeCodePoint.put("U+00D8","Ø");unicodeCodePoint.put("U+00D9","Ù");unicodeCodePoint.put("U+00DA","Ú");
        unicodeCodePoint.put("U+00DB","Û");unicodeCodePoint.put("U+00DC","Ü");unicodeCodePoint.put("U+00DD","Ý");unicodeCodePoint.put("U+00DE","Þ");
        unicodeCodePoint.put("U+00DF","ß");unicodeCodePoint.put("U+00E0","à");unicodeCodePoint.put("U+00E1","á");unicodeCodePoint.put("U+00E2","â");
        unicodeCodePoint.put("U+00E3","ã");unicodeCodePoint.put("U+00E4","ä");unicodeCodePoint.put("U+00E5","å");unicodeCodePoint.put("U+00E6","æ");
        unicodeCodePoint.put("U+00E7","ç");unicodeCodePoint.put("U+00E8","è");unicodeCodePoint.put("U+00E9","é");unicodeCodePoint.put("U+00EA","ê");
        unicodeCodePoint.put("U+00EB","ë");unicodeCodePoint.put("U+00EC","ì");unicodeCodePoint.put("U+00ED","í");unicodeCodePoint.put("U+00EE","î");
        unicodeCodePoint.put("U+00EF","ï");unicodeCodePoint.put("U+00F0","ð");unicodeCodePoint.put("U+00F1","ñ");unicodeCodePoint.put("U+00F2","ò");
        unicodeCodePoint.put("U+00F3","ó");unicodeCodePoint.put("U+00F4","ô");unicodeCodePoint.put("U+00F5","õ");unicodeCodePoint.put("U+00F6","ö");
        unicodeCodePoint.put("U+00F7","÷");unicodeCodePoint.put("U+00F8","ø");unicodeCodePoint.put("U+00F9","ù");unicodeCodePoint.put("U+00FA","ú");
        unicodeCodePoint.put("U+00FB","û");unicodeCodePoint.put("U+00FC","ü");unicodeCodePoint.put("U+00FD","ý");unicodeCodePoint.put("U+00FE","þ");

    }

    /**
     * Read small and large file of text
     *   Note: the javadoc of Files.readAllLines says it's intended for small
     *   files. But its implementation uses buffering, so it's likely good
     *   even for fairly large files
     * @param aFile the file you want to read
     * @return a list of lines
     */
    public static List<String> readSmallFile(File aFile,Charset encoding) {
        if(!aFile.exists()) createFile(aFile);
        if(encoding==null) encoding = StandardCharsets.UTF_8;
        Path path = Paths.get(aFile.getAbsolutePath());
        try {
            return Files.readAllLines(path,  encoding);
        } catch (IOException e) {
            encoding = StandardCharsets.UTF_8;
            try {
                return Files.readAllLines(path, encoding);
            } catch (IOException e1) {
                SystemLog.exception(e1,FileUtilities.class);
                return null;
            }
        }
    }

    /**
     * Method to write Small File.
     * @param aLines a List of String to write on the File.
     * @param aFile the File where to write.
     * @param encoding the Charset Encoding if null is UTF8.
     */
    public static void writeSmallFile(List<String> aLines, File aFile,Charset encoding) {
        if(!aFile.exists()) createFile(aFile);
        if(encoding==null) encoding = StandardCharsets.UTF_8;
        Path path = Paths.get(aFile.getAbsolutePath());
        try {
            Files.write(path, aLines, encoding);
        } catch (IOException e) {
            encoding = StandardCharsets.UTF_8;
            try {
                Files.write(path, aLines, encoding);
            } catch (IOException e1) {
                SystemLog.exception(e1,FileUtilities.class);
            }
        }
    }

    public static List<String> parseLargeFile(File aFile,Charset encoding) throws IOException {
        if(encoding==null) encoding = StandardCharsets.UTF_8;
        List<String> list = new ArrayList<>();
        Path path = Paths.get(aFile.getAbsolutePath());
        try (Scanner scanner =  new Scanner(path, encoding.name())){
            while (scanner.hasNextLine()){
                //process each line in some way
                try{
                    list.add(scanner.nextLine());
                }catch( java.util.NoSuchElementException e){
                    if(!scanner.hasNextLine()) break;
                }
            }
        }
        return list;
    }

    public static List<String> readLargeFile(File aFile,Charset encoding) throws IOException {
        if(encoding==null) encoding = StandardCharsets.UTF_8;
        List<String> list = new ArrayList<>();
        Path path = Paths.get(aFile.getAbsolutePath());
        try (BufferedReader reader = Files.newBufferedReader(path, encoding)) {
            String line;
            while ((line = reader.readLine()) != null) {
                //process each line in some way
                list.add(line);
            }
        }
        return list;
    }

    public static void writeLargeFile(File aFile, List<String> aLines,Charset encoding){
        if(encoding==null) encoding = StandardCharsets.UTF_8;
        Path path = Paths.get(aFile.getAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(path, encoding)){
            for(String line : aLines){
                writer.write(line);
                writer.newLine();
            }
        }catch(java.lang.NullPointerException|IOException e){
            SystemLog.warning(e.getMessage(),FileUtilities.class);
        }
    }

    public static void writeLargeFileWithoutUnicode(File aFile, List<String> aLines) throws IOException {
        Path path = Paths.get(aFile.getAbsolutePath());
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), true)))) {
            for(String line : aLines){
                try{
                    for (Map.Entry<String, String> entry : unicodeCodePoint.entrySet()) {
                        try{
                            String s = entry.getKey().replace("U+","\\u");
                            if(line.contains(s))line = line.replace(s, entry.getValue());
                        }catch(java.lang.NullPointerException ne){break;}
                    }
                    out.print(line + System.getProperty("line.separator"));
                    out.flush();
                }catch(java.lang.NullPointerException ne){break;}
            }
        } catch(java.lang.NullPointerException ne){
            SystemLog.warning("Can't decode the file:"+aFile.getAbsolutePath());
        }
    }

    public static void writeLargeFileWithoutUnicode(File aFile, List<String> aLines,Charset encoding){
        if(encoding==null) encoding = StandardCharsets.UTF_8;
        Path path = Paths.get(aFile.getAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(path, encoding)){
            for(String line : aLines){
                try{
                    for (Map.Entry<String, String> entry : unicodeCodePoint.entrySet()) {
                        try{
                            String s = entry.getKey().replace("U+","\\u");
                            if(line.contains(s))line = line.replace(s,entry.getValue());
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
        }catch(java.lang.NullPointerException|IOException ne){
            SystemLog.warning(ne.getMessage(),FileUtilities.class);
        }
    }



    /**
     * Write fixed content to the given file.
     */
    public void write(File aFile,Charset encoding) throws IOException  {
        SystemLog.message("Try to writing to file named " + aFile.getAbsolutePath() + " with Encoding: " + encoding);
        Writer out = new OutputStreamWriter(new FileOutputStream(aFile),  encoding);
    }

    /**
     * Read the contents of the given file.
     * @return the String content of the file.
     * @throws IOException file not found.
     */
    public String read(File aFile,Charset encoding) throws IOException {
        //log("Reading from file.");
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        try (Scanner scanner = new Scanner(new FileInputStream(aFile), encoding.name())) {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append(NL);
            }
        }
        return text.toString();
    }

    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     * @param aFile is a file which already exists and can be read.
     * @return content of the file
     */
    public static String read(File aFile) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            try (BufferedReader input = new BufferedReader(new FileReader(aFile))) {
                String line; //not declared within while loop
                 /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
        }catch (IOException ex){
            SystemLog.exception(ex,FileUtilities.class);
        }
        return contents.toString();
    }

    /**
     * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
     * escaped sequence for characters outside of ASCII.
     * It is equivalent to: native2ascii -encoding utf-8.
     * @param UTF8 encoding of input.
     * @return ASCII encoding of output.
     * @throws IOException  file not found.
     */
    public static List<String> toAscii(File UTF8) throws IOException{
        List<String> list = new ArrayList<>();
        if (UTF8==null) {
            System.out.println("Usage: java UTF8ToAscii <filename>");
            return null;
        }
        try (BufferedReader r = new BufferedReader( new InputStreamReader(new FileInputStream(UTF8),"UTF-8" ))) {
            String line = r.readLine();
            while (line != null) {
                System.out.println(unicodeEscape(line));
                line = r.readLine();
                list.add(line);
            }
        }
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
     * stdout in UTF-8.
     * This utility is equivalent to: native2ascii -reverse -encoding utf-8.
     * @param ASCII file of input in ASCII encoding.
     * @return UTF8 file of input in UTF8 encoding.
     * @throws IOException file not found.
     */
    public static List<String> toUTF8(File ASCII) throws IOException {
        List<String> list = new ArrayList<>();
        if (ASCII == null) {
            System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
            return null;
        }
        try (BufferedReader r = new BufferedReader(new FileReader(ASCII))) {
            String line = r.readLine();
            while (line != null) {
                line = convertUnicodeEscape(line);
                byte[] bytes = line.getBytes("UTF-8");
                list.add(StringKit.convertByteArrayToString(bytes));
            }
        }
        return list;
    }

    enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}

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
    public static void writeFileWithUTF8(File fileASCII) throws IOException{
        List<String> list = toUTF8(fileASCII);
        File file = new File(fileASCII.getAbsolutePath());
        //fileASCII = new File(filePathASCII);
        fileASCII.delete();
        writeLargeFile(file, list, null);
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     * @param fileUTF8 file of input in UTF8 encoding
     * @throws IOException file not found
     */
    public static void writeFileWithASCII(File fileUTF8) throws IOException{
        List<String> list = toAscii(fileUTF8);
        File filePathUTF8 = new File(fileUTF8.getAbsolutePath());
        fileUTF8.delete();
        //fileASCII = new File(filePathASCII);
        writeLargeFile(filePathUTF8, list,null);
    }

    public static void writeFileWithUTF8(String filePathInput,String filePathOutput) {
        try {
            FileOutputStream fos = new FileOutputStream(filePathInput);
            try (Writer out = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                out.write(filePathOutput);
            }
        } catch (IOException e) {
            SystemLog.exception(e);
        }
    }

    public static String readLargeFileWithUTF8(String filePathInput) {
        StringBuilder buffer = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(filePathInput);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            try (Reader in = new BufferedReader(isr)) {
                int ch;
                while ((ch = in.read()) > -1) {
                    buffer.append((char) ch);
                }
            }
            return buffer.toString();
        } catch (IOException e) {
            SystemLog.exception(e);
            return null;
        }
    }

    public static File toAnsi(File fileUTF8) {
        File fileANSI =
                new File(getPath(fileUTF8)+File.separator+getFilenameWithoutExt(fileUTF8)+"ansi."+getExtension(fileUTF8));
        try {
            boolean firstLine = true;
            FileInputStream fis = new FileInputStream(fileUTF8);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(fis,StandardCharsets.UTF_8))) {
                FileOutputStream fos = new FileOutputStream(fileANSI);
                try (Writer w = new BufferedWriter(new OutputStreamWriter(fos, "Cp1252"))) {
                    for (String s; (s = r.readLine()) != null;) {
                        if (firstLine) {
                            if (s.startsWith("\uFEFF")) {
                                s = s.substring(1);
                            }
                            firstLine = false;
                        }
                        w.write(s + System.getProperty("line.separator"));
                        w.flush();
                    }
                }
            }
            return fileANSI;
        }catch (Exception e) {
            SystemLog.exception(e);
            return null;
        }
    }
}
