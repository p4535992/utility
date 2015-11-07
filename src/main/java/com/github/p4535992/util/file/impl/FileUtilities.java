package com.github.p4535992.util.file.impl;

import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.impl.StringIs;
import com.github.p4535992.util.string.impl.StringKit;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
     * @throws IOException throw if the output location not exists!.
     */
    public static File createFile(String fullPath) throws IOException {
        return createFile(new File(fullPath));}


    /**
     * Method to create a new File Object in a specific path.
     * @param file File output location of the new File .
     * @return the new File object.
     * @throws IOException throw if the output location not exists!.
     */
    public static File createFile(File file) throws IOException {
        if(file.createNewFile()){
            return file;
        }
        return null;
    }

    /**
     * Method to copy the content from a file to another in char format.
     * @param fullPathInput string path to the file you want to read the copy.
     * @param fullPathOutput string path to the file you want write the copy.
     * @throws IOException throw if any error is occurrred.
     */
    public static void copyFileCharStream(String fullPathInput, String fullPathOutput) throws IOException {
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

    /**
     * Method to copy the content from a file to another in byte array format.
     * @param fullPathInput string path to the file you want to read the copy.
     * @param fullPathOutput string path to the file you want write the copy.
     * @throws IOException throw if any error is occurrred.
     */
    public static void copyFileByteStream(String fullPathInput, String fullPathOutput) throws IOException {
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
        //File[] listOfFiles = new File(fullPathDir).listFiles();
        String[] paths;
        List<File> files = new ArrayList<>();
        try {
            // insert new file object and array of files and directory
            paths =  new File(fullPathDir).list();
            // for each name in the path array
            for (String path : paths) {
                // prints filename and directory name
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
    public static URI convertFileToUri(File file){
        return file.toURI();
    }

    /**
     * Method to convert a File to a URI
     * @param filePath the String to the File to convert.
     * @return the URI.
     */
    public static URI convertFileToUri(String filePath) {
        return convertFileToUri(new File(filePath));
    }

    /**
     * Method to convert a File to a URL
     * @param file the File to convert.
     * @return the URL.
     * @throws java.net.MalformedURLException if any error is occurred.
     */
    public static URL convertFileToURL(File file) throws MalformedURLException {return convertFileToUri(file).toURL();}

    /**
     * Method to convert a File to a URL
     * @param filePath the String to the File to convert.
     * @return the URL.
     * @throws java.net.MalformedURLException if any error is occurrred.
     */
    public static URL convertFileToURL(String filePath)throws MalformedURLException{return convertFileToURL(new File(filePath));}

    /**
     * Method to convert a URI to a File.
     * @param uri the URI to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     */
    public static File convertURIToFile(URI uri) throws MalformedURLException {return new File(uri.toURL().getFile());}

    /**
     * Method to convert a URI to a File.
     * @param url the URL to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     * @throws URISyntaxException throw if any error is occurred.
     */
    public static File convertURLToFile(URL url) throws URISyntaxException, MalformedURLException {
        return convertURIToFile(url.toURI());
    }

    /**
     * Method to convert a URI to a File.
     * @param url the URL to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     * @throws URISyntaxException throw if any error is occurred.
     */
    public static File convertURLToFile(String url) throws URISyntaxException, MalformedURLException {
        return convertURIToFile(new URL(url).toURI());
    }

    /**
     * Method to convert a URI to Stream.
     * @param uri  the URI to convert.
     * @return the Stream.
     * @throws IOException throw if any error is occurred.
     */
    public static InputStream convertURIToStream(URI uri) throws IOException {return uri.toURL().openStream();}

    /**
     * Method to convert a File to a URI with the prefix file://.
     * @param filename the String path tot the the File to convert.
     * @return the URI with prefix.
     */
    public static URI convertFileToUriWithPrefix(String filename){
        return URI.create(convertFileToStringUriWithPrefix(filename));
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     * @param file the File to convert.
     * @return the String URI with prefix.
     */
    public static String convertFileToStringUriWithPrefix(File file){
        return convertFileToStringUriWithPrefix(file.getAbsolutePath());
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     * @param file the File to convert.
     * @return the URI with prefix.
     */
    public static URI convertFileToUriWithPrefix(File file){
        return URI.create(convertFileToStringUriWithPrefix(file.getAbsolutePath()));
    }

    /**
     * Method for convert a reference path to a resource in the classpath to a file with path in the system.
     * @param referenceResourcePath string of the reference path to the resource.
     * @param thisClass thi class.
     * @return file correspondent to the reference file of the resources.
     */
    public static File convertResourceToFile(String referenceResourcePath,Class<?> thisClass){
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
    public static String convertFileToStringUriWithPrefix(String filePath){
        StringBuilder mapfilename = new StringBuilder( filePath ) ;
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
    public static File convertStringToTempFile(String content,String fullPath){
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
     * Method to convert a File to a String.
     * @param fullPath the String of the path where is allocate the file.
     * @return the String content of the File.
     */
    public static String convertFileToString(String fullPath){
        return convertFileToString(new File(fullPath));
    }


    /**
     * Method to convert a File to a String.
     * @param file the File to convert.
     * @return the String content of the File.
     */
    public static String convertFileToString(File file){
        return readStringFromFileLineByLine(file);
    }

    /**
     * Method to read the content of a file line by line.
     * @param pathToFile the String path to the file to read.
     * @return the String content ofd the file.
     */
    public static String readStringFromFileLineByLine(String pathToFile) {
        return readStringFromFileLineByLine(new File(pathToFile));
    }

    /**
     * Method to read the cotnetn of a file line by line.
     * @param file the file to read.
     * @return the String content ofd the file.
     */
    public static String readStringFromFileLineByLine(File file) {
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
            System.out.println(stringBuilder.toString());
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
    public static Map<String,String> readStringFromFileLineByLine(String pathToFile, char separator, SimpleParameters params) {
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
    public static void copyFiles(File destination, File source) throws IOException {
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
     * @throws IOException throw if the File is not found or the Output directory not exists.
     */
    public static InputStream convertResourceFileToStream(String pathToFile) throws IOException{
        // JDK7 try-with-resources ensures to close stream automatically
        try (InputStream is = FileUtilities.class.getResourceAsStream(pathToFile)) {
            int Byte; // Byte because byte is keyword!
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

    /**
     * Method to covnert a file to a Stream.
     * @param pathToFile String path to the File.
     * @return the Stream of the File.
     */
    public static InputStream convertFileToStream(String pathToFile){
        try {
            return new FileInputStream(new File(pathToFile));
        } catch (FileNotFoundException e) {
            SystemLog.warning("The file:" + pathToFile + " not exists!!!");
            return null;
        }
    }

    /**
     * Method to covnert a resource file to a Stream.
     * @param file File to read.
     * @return the Stream of the File.
     */
    public static InputStream convertFileToStream(File file){
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
    public static File convertStreamToFile(InputStream inStream,String filePathOutput) {
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
    public static String convertResourceFileToString(String fileName,Class<?> clazz) {
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
     * Method to covnert a resource file to a Stream.
     * @param name String name of the class
     * @param clazz the Class who call this method.
     * @return the Stream of the File.
     */
    public static InputStream convertResourceFileToStream(String name,Class<?> clazz) {
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
    public static GZIPInputStream convertFileToGZIPInputStream(File file){
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
    public static GZIPInputStream convertFileToGZIPInputStream(String filePathToFile){
        return convertFileToGZIPInputStream(new File(filePathToFile));
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
    public static String convertFileToMD5(File file) {
        return hashFile(file, "MD5");
    }

    /**
     * Method to convert a File to a SHA-1 hash string.
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String convertFileToSHA1(File file) {
        return hashFile(file, "SHA-1");
    }

    /**
     * Method to convert a File to a SHA-256 hash string.
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String convertFileToSHA256(File file)  {
        return hashFile(file, "SHA-256");
    }

    /**
     * Method to convert a String to a File.
     * @param stringText the String content.
     * @param fullPathfile the String to the new location of the File.
     * @return the File Object.
     */
    public static File convertStringToFile(String stringText,String fullPathfile){
        return convertStringToFile(stringText, new File(fullPathfile));

    }

    /**
     * Method to convert a String to a File.
     * @param stringText the String content.
     * @param file the  File.
     * @return the File Object.
     */
    public static File convertStringToFile(String stringText,File file){
        return writeStringToFile(stringText, file);

    }

    /**
     * Method to convert a File to a Writer Object.
     * @param file the File to convert.
     * @return the Writer Object.
     */
    public static Writer convertFileToWriter(File file){
        Writer writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return writer;
    }

    public static Reader convertFileToReader(File file){
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
    private static File writeStringToFile(String str, File fileName) {
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
     * Method to convert a MultipartFile to a File
     * @param multiPartFile the MultiPartFile of Spring to convert.
     * @return the File.
     * @throws IOException
     */
    /*public File convertMultiPartFileToFile(org.springframework.web.multipart.MultipartFile multiPartFile) throws IOException {
        File convFile = new File(multiPartFile.getOriginalFilename());
        //do
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multiPartFile.getBytes());
        fos.close();
        //or
        // multipart.transferTo(convFile);
        return convFile;
    }*/


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








}
