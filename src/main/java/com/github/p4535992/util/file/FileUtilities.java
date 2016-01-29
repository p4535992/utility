package com.github.p4535992.util.file;

import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.file.resources.ClassLoaderUtil;
import com.github.p4535992.util.string.StringUtilities;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static java.util.Arrays.*;

/**
 * Class with many utilities mathod for magage the file object.
 *
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class FileUtilities {
    
     private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(FileUtilities.class);

    public static final String pathSeparatorReference = "/";
   
    public static char pathSeparator = File.separatorChar;
    private static String fullPath;
    //private static char extensionSeparator = '.';
    //private static String extensionSeparatorS = ".";
    //public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    private static FileUtilities instance = null;
    //------------------------------------------------------------------------------------------------------
    private static Map<String, String> unicodeCodePoint = new HashMap<>();

    protected FileUtilities() {
    }

    /**
     * Constructor .
     *
     * @param f file of input
     */
    public FileUtilities(File f) {
        FileUtilities.fullPath = f.getAbsolutePath();
    }

    /**
     * Constructor.
     *
     * @param filePath string of the path to the file
     */
    public FileUtilities(String filePath) {
        FileUtilities.fullPath = filePath;
    }

    /**
     * Constructor.
     *
     * @param str       string of the path to the file
     * @param separator path separator
     * @param extension extension separator (usually '.')
     */
    public FileUtilities(String str, char separator, char extension) {
        FileUtilities.fullPath = str;
        FileUtilities.pathSeparator = separator;
        //FileUtilities.extensionSeparator = extension;
    }

    /**
     * Check the file separator to see if we're on a Windows platform.
     *
     * @return boolean True if the platform is Windows, false otherwise.
     */
    private static boolean platformIsWindows() {
        return File.separatorChar == '\\';
    }

    public static FileUtilities getInstance() {
        if (instance == null) {
            instance = new FileUtilities();
        }
        return instance;
    }

    /**
     * Method for get the extension from a file.
     *
     * @param f file of input
     * @return string of the extension of the file
     */
    public static String getExtension(File f) {
        return getExtension(f.getAbsolutePath());
    }

    /**
     * Method for get the extension from a file.
     *
     * @param fullPath string of the path to the file
     * @return string of the extension of the file
     */
    public static String getExtension(String fullPath) {
        String extension;
        int iSlash = fullPath.lastIndexOf( '/' );
        int iBack = fullPath.lastIndexOf( '\\' );
        int iExt = fullPath.lastIndexOf( '.' );
        if (iBack > iSlash) iSlash = iBack;
        extension = iExt > iSlash ? fullPath.substring( iExt+1 ) : "";

        //WORK
        if(extension.isEmpty()) {
            if (!fullPath.contains(".")) fullPath = fullPath + ". ";
            extension = fullPath.substring(fullPath.lastIndexOf('.') + 1);
        }

        if(extension.isEmpty()) {
            int i = fullPath.lastIndexOf('.');
            if (i > 0 &&  i < fullPath.length() - 1) {
                extension = fullPath.substring(i + 1);
            }
        }
        return extension;
    }

    /**
     * Method for get the filename without extension.
     *
     * @param f file of input
     * @return name of the file without the extension
     */
    public static String getFilenameWithoutExt(File f) {
        return getFilenameWithoutExt(f.getAbsolutePath());
    }

    /**
     * Method for get the filename without extension.
     *
     * @param fullPath string of the path to the file
     * @return name of the file without the extension
     */
    public static String getFilenameWithoutExt(String fullPath) {
        if (!fullPath.contains(".")) fullPath = fullPath + ". ";
        int dot = fullPath.lastIndexOf('.');
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }

    /**
     * Method for get the name of the file (with extensions).
     *
     * @return name of the file
     */
    public static String getFilename() {
        return new File(fullPath).getName();
    }

    /**
     * Method for get the name of the file (with extensions).
     *
     * @param f file of input
     * @return name of the file
     */
    public static String getFilename(File f) {
        return f.getName();
    }

    /**
     * Method for get the name of the file (with extensions).
     *
     * @param fullPath string of the path to the file
     * @return name of the file
     */
    public static String getFilename(String fullPath) {
        String name;
        if (fullPath.contains(File.separator)) name = fullPath.replace(getPath(fullPath), "");
        else name = fullPath;

        name = name.replace(File.separator, "");
        return name;
    }

    /**
     * Method for convert a absolute path to the file to a relative path.
     *
     * @param base         the base of the absolute path where you want start the
     *                     relative path e.g. /var/data
     * @param absolutePath the full pth to the file e.g. /var/data/stuff/xyz.dat
     * @return the relative path to the file e.g. stuff/xyz.dat
     */
    public static String getRelativePath(String base, String absolutePath) {
        return new File(base).toURI().relativize(new File(absolutePath).toURI()).getPath();
    }

    /**
     * Method or get the local path in the project.
     *
     * @param file File object.
     * @return the local path to the file in the project.
     */
    public static String getLocalPath(File file) {
        return getLocalPath("", file.getAbsolutePath());
    }

    /**
     * Method for get the local path in the project.
     *
     * @param absolutePath string of the absolute path to the file in the project.
     * @return the local path to the file in the project
     */
    public static String getLocalPath(String absolutePath) {
        return getLocalPath("", absolutePath);
    }

    /**
     * Method for get the local path in the project.
     *
     * @param basePath  string of the absolute path to the direcotry of the project.
     * @param localPath string of the absolute path to the file in the project.
     * @return the local path to the file in the project
     */
    public static String getLocalPath(String basePath, String localPath) {
        basePath = basePath.replace(System.getProperty("user.dir"), "");
        return basePath + File.separator + localPath;
    }

    /**
     * Method for get the path of a file.
     *
     * @return the path to the file
     */
    public static String getPath() {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }

    /**
     * Method for get the path of a file.
     *
     * @param f file of input
     * @return the path to the file
     */
    public static String getPath(File f) {
        return getPath(f.getAbsolutePath());
    }

    /**
     * Method for get the path of a file.
     *
     * @param fullPath string of the path to the file
     * @return the path to the file
     */
    public static String getPath(String fullPath) {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }

    /**
     * Method to create a new File Object in a specific path.
     *
     * @param fullPath String output location of the new File .
     * @return the new File object.
     */
    public static File createFile(String fullPath) {
        return createFile(new File(fullPath));
    }

    /**
     * Method to create a new File Object in a specific path.
     *
     * @param file File output location of the new File .
     * @return the new File object.
     */
    public static File createFile(File file) {
        try {
            if (file.createNewFile()) return file;
            else {
                logger.warn("Can't create the file" + file.getName());
                return null;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to copy the content from a file to another in char format.
     *
     * @param fullPathInput  string path to the file you want to read the copy.
     * @param fullPathOutput string path to the file you want write the copy.
     * @return if true all the operation are done.
     */
    public static boolean copy(String fullPathInput, String fullPathOutput) {
        return copy(new File(fullPathInput), new File(fullPathOutput));
    }

    /**
     * Method to copy a file.
     *
     * @param destination the String destination for the copy of the file.
     * @param source      the String source of the File to copy.
     * @return if true all the operation are done.
     */
    public static boolean copy(File destination, File source) {
        if (!destination.exists()) createFile(destination);
        try (OutputStream out = new FileOutputStream(destination);
             InputStream in = new FileInputStream(source)) {
            copy(in, out, StandardCharsets.UTF_8);
            logger.info("Done copying contents of " + source.getName() + " to " + destination.getName());
            return true;
        } catch (IOException e) {
            logger.error("Copying file/folder: " + source + " to " + destination + ":"+e.getMessage(), e);
            return false;
        }
    }

    public static boolean copy(Path src, Path dest) {
        try {
            //Method 1
            dest = Files.isDirectory(src) ? dest.resolve(src) : dest;
            EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            TreeCopier tc = new TreeCopier(src, dest, false, false);
            Files.walkFileTree(src, opts, Integer.MAX_VALUE, tc);

            //Method 2
            /*
            Path newPath = dest.resolve(src);
            Files.createDirectories(newPath.getParent());
            try (OutputStream output = Files.newOutputStream(
                    newPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                output.write(FileUtilities.toBytes(src));
            }
            */
            logger.info("Done copying contents of " + src + " to " + dest);
            return true;
        } catch (IOException e) {
            logger.error("Copying file/folder: " + src + " to " + dest + ":"+e.getMessage(), e);
        }
        return false;
    }

    /**
     * Method to copy a file.
     *
     * @param input    the InpuStream to copy.
     * @param output   the OutputStream where put the copy.
     * @param encoding the Charset encoding of the Stream.
     * @return if true all the operation are done.
     */
    public static boolean copy(InputStream input, OutputStream output, Charset encoding) {
        InputStreamReader in = new InputStreamReader(input, encoding);
        OutputStreamWriter out = new OutputStreamWriter(output, encoding);
        long count = copyLarge(in, out);
        return !(count == -1 || count > Integer.MAX_VALUE);
    }

    /**
     * Method to copy a file.
     *
     * @param input  the Reader input.
     * @param output the Writer Output
     * @return the count of the characters in the Stream.
     */
    private static long copyLarge(Reader input, Writer output) {
        //copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE])
        char[] buffer = new char[1024 * 4];
        long count = 0;
        int n; // n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Method to create a directory.
     *
     * @param fullPathDir string path to the location of the directory.
     * @return if true you have created the directory.
     */
    public static boolean createDirectory(String fullPathDir) {
        File d = new File(fullPathDir);
        return d.mkdirs();
    }

    /**
     * Recursively traverse a directory hierachy and obtain a list of all
     * absolute file names.
     * <p>Regular expression patterns can be provided to explicitly include
     * and exclude certain file names.
     *
     * @param file     the directory whose file hierarchy will be traversed
     * @param included an array of regular expression patterns that will be
     *                 used to determine which files should be included; or
     *                 <p><code>null</code> if all files should be included
     * @param excluded an array of regular expression patterns that will be
     *                 used to determine which files should be excluded; or
     *                 <p><code>null</code> if no files should be excluded
     * @return the list of absolute file names
     * @since 1.0
     */
    public static List<String> getFileList(File file, Pattern[] included, Pattern[] excluded) {
        return getFileList(file, included, excluded, true);
    }

    private static List<String> getFileList(File file, Pattern[] included, Pattern[] excluded, boolean root) {
        if (null == file) return new ArrayList<>();
        ArrayList<String> filelist = new ArrayList<>();
        if (file.isDirectory()) {
            String[] list = file.list();
            if (null != list) {
                String list_entry;
                for (String aList : list) {
                    list_entry = aList;
                    File next_file = new File(file.getAbsolutePath() + File.separator + list_entry);
                    List<String> dir = getFileList(next_file, included, excluded, false);
                    Iterator<String> dir_it = dir.iterator();
                    String file_name;
                    while (dir_it.hasNext()) {
                        file_name = dir_it.next();
                        if (root) {
                            // if the file is not accepted, don't process it further
                            if (!StringUtilities.isMatch(file_name, included, excluded)) {
                                continue;
                            }
                        } else {
                            file_name = file.getName() + File.separator + file_name;
                        }
                        int filelist_size = filelist.size();
                        for (int j = 0; j < filelist_size; j++) {
                            if ((filelist.get(j)).compareTo(file_name) > 0) {
                                filelist.add(j, file_name);
                                break;
                            }
                        }
                        if (filelist.size() == filelist_size) {
                            filelist.add(file_name);
                        }
                    }
                }
            }
        } else if (file.isFile()) {
            String file_name = file.getName();
            if (root) {
                if (StringUtilities.isMatch(file_name, included, excluded)) {
                    filelist.add(file_name);
                }
            } else filelist.add(file_name);
        }
        return filelist;
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory file of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(File directory) {
        return getFilesFromDirectory(directory.getAbsolutePath());
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory file of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(Path directory) {
        return getFilesFromDirectory(directory.toFile().getAbsolutePath());
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param fullPathDir string path to the location of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(String fullPathDir) {
       return getFilesFromDirectory(fullPathDir,null,null);
    }
    /**
     * Method to read all file ina directory/folder.
     *
     * @param fullPathDir string path to the location of the directory/folder.
     * @param offset the {@link Integer} offset iundex of the Files.
     * @param limit the {@link Integer} limit iundex of the Files.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(String fullPathDir,Integer offset,Integer limit) {
        String[] paths;
        List<File> files = new ArrayList<>();
        try {
            paths = new File(fullPathDir).list();
            if (offset != null && limit != null &&
                    offset + limit > paths.length) limit = paths.length;
            for (int i =0; i < paths.length; i++) {
                if(offset != null){
                    if(i >= offset )files.add(new File(fullPathDir + File.separator + paths[i]));
                    else continue;
                }else{
                    files.add(new File(fullPathDir + File.separator + paths[i]));
                }
                if(limit != null){
                    if(i >= limit-1)break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return files;
    }

    public static List<Path> getPathsFromDirectory(Path directory) {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory.toUri()))) {
            for (Path path : directoryStream) {
                paths.add(path);
            }
        } catch (IOException e) {
            logger.error("Listing files in directory: {}", directory, e);
        }
        return paths;
    }

    public static List<Path> getPathsFromDirectory(String directory) {
        return getPathsFromDirectory(Paths.get(directory));
    }

    public static List<Path> getPathsFromDirectory(File directory) {
        return getPathsFromDirectory(directory.toPath());
    }

    public static List<Path> getPathsDirectoriesFromDirectory(Path dir) throws IOException {
        return getPathsFromDirectory(dir, null, null);
    }

    public static List<Path> getPathsFromDirectory(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return getPathsFromDirectory(dir, filter, null);
    }

    public static List<Path> getPathsFromDirectory(Path dir, String glob) throws IOException {
        //e.g. glob "*.{txt,doc,pdf,ppt}"
        return getPathsFromDirectory(dir, null, glob);
    }

    private static List<Path> getPathsFromDirectory(Path directory, DirectoryStream.Filter<? super Path> filter, String glob) {
        List<Path> paths = new ArrayList<>();
        DirectoryStream<Path> directoryStream;
        try {
            if (filter != null) directoryStream = Files.newDirectoryStream(directory, filter);
            else if (glob != null) directoryStream = Files.newDirectoryStream(directory, glob);
            else directoryStream = Files.newDirectoryStream(directory);
            for (Path path : directoryStream) {
                paths.add(path);
            }
            directoryStream.close();
        } catch (IOException e) {
            logger.error("Listing files in directory: {}", directory, e);
        }
        return paths;
    }

    public static File getFromResourceAsFile(String fileNameResource, Class<?> thisClass, File outputFile) {
        StringBuilder result = new StringBuilder("");
        //Get file from resources folder
        ClassLoader classLoader = thisClass.getClassLoader();
        //noinspection ConstantConditions
        String resourcePath = classLoader.getResource(fileNameResource).getFile();
        File file = new File(resourcePath);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            //scanner.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return toFile(result.toString(), outputFile);
    }

    public static File getFromResourceAsFileWithCommons(String fileNameResource, Class<?> thisClass, File outputFile) {
        ClassLoader classLoader = thisClass.getClassLoader();
        String result = "";
        try {
            result = org.apache.commons.io.IOUtils.toString(classLoader.getResourceAsStream(fileNameResource));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return toFile(result, outputFile);
    }

    public static Iterator<URL> getFromResources(String resourceName, Class<?> callingClass, boolean aggregate)
            throws IOException {
        return ClassLoaderUtil.getResources(resourceName, callingClass, aggregate);
    }

    public static URL getFromResourceAsURL(String resourceName, Class<?> callingClass) {
        return ClassLoaderUtil.getResourceAsURL(resourceName, callingClass);
    }

    public static InputStream getFromResourceAsStream(String resourceName, Class<?> callingClass) {
        return ClassLoaderUtil.getResourceAsStream(resourceName, callingClass);
    }

    public static Class<?> getFromResourceAsClass(String className, Class<?> callingClass) throws ClassNotFoundException {
        return ClassLoaderUtil.loadClass(className, callingClass);
    }

    public static String[] getFromResourceAsListing(String path, Class<?> callingClass) throws URISyntaxException, IOException {
        return ClassLoaderUtil.getResourceListing(callingClass, path);
    }

    /**
     * Removes all files from a given folder.
     * href: http://www.adam-bien.com/roller/abien/entry/java_7_deleting_recursively_a.
     *
     * @param filePathToTheFile string of the path to the file
     * @return if true all the operation are done.
     */
    public static boolean deleteDirectory(String filePathToTheFile) {
        File filePath = new File(filePathToTheFile);
        if (filePath.exists()) {
            if (filePath.isDirectory()) {
                Path path = filePath.toPath();
                try {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    return false;
                }
                return true;
            } else {
                logger.warn("The file to the path:" + filePathToTheFile + "is not a directory");
                return false;
            }
        } else {
            logger.warn("Not exists the file to the path:" + filePathToTheFile);
            return false;
        }
        /*if (filePath.exists()) {
            for (String fileInDirectory : filePath.list()) {
                File tmpFile = new File(path + "/" + fileInDirectory);
                if(!tmpFile.delete()){
                    logger.warn("Can't delete the file:" + tmpFile.getAbsolutePath());
                    return false;
                }
            }
            if(!filePath.delete()){
                logger.warn("Can't delete the file:" + filePath.getAbsolutePath());
                return false;
            }
        }
        return true;*/
    }

    /**
     * Method to convert a File to a URI
     *
     * @param file the File to convert.
     * @return the URI.
     */
    public static URI toUri(File file) {
        return file.toURI();
    }

    /**
     * Method to convert a File to a URI
     *
     * @param filePath the String to the File to convert.
     * @return the URI.
     */
    public static URI toUri(String filePath) {
        return toUri(new File(filePath));
    }

    /**
     * Method to convert a File to a URL
     *
     * @param file the File to convert.
     * @return the URL.
     * @throws java.net.MalformedURLException if any error is occurred.
     */
    public static URL toURL(File file) throws MalformedURLException {
        return toUri(file).toURL();
    }

    /**
     * Method to convert a File to a URL
     *
     * @param filePath the String to the File to convert.
     * @return the URL.
     * @throws java.net.MalformedURLException if any error is occurrred.
     */
    public static URL toURL(String filePath) throws MalformedURLException {
        return toURL(new File(filePath));
    }

    /**
     * Method to convert a URI to a File.
     *
     * @param uri the URI to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     */
    public static File toFile(URI uri) throws MalformedURLException {
        File file = new File(uri.toURL().getFile());
        if(file.exists()) return file;
        else return null;
    }

    /**
     * Method to convert a URI to a File.
     *
     * @param url the URL to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     * @throws URISyntaxException    throw if any error is occurred.
     */
    public static File toFile(URL url) throws URISyntaxException, MalformedURLException {
        if ( url == null) return null;
        File file = toFile(url.toURI());
        if(file == null){ //try again with a different approach...
            if (!url.getProtocol().equalsIgnoreCase( "file" ) ) return null;
            String filename = url.getFile().replace( '/', File.separatorChar );
            int pos = -1;
            while ( ( pos = filename.indexOf( '%', pos + 1 ) ) >= 0 )
            {
                if ( pos + 2 < filename.length() )
                {
                    String hexStr = filename.substring( pos + 1, pos + 3 );
                    char ch = (char) Integer.parseInt( hexStr, 16 );
                    filename = filename.substring( 0, pos ) + ch + filename.substring( pos + 3 );
                }
            }
            file = new File( filename );
        }
        return file;
    }

    /**
     * Method to convert a URI to a File.
     *
     * @param url the URL to convert.
     * @return the File.
     * @throws MalformedURLException throw if any error is occurred.
     * @throws URISyntaxException    throw if any error is occurred.
     */
    public static File toFile(String url) throws URISyntaxException, MalformedURLException {
        return toFile(new URL(url).toURI());
    }

    /**
     * Method to convert a URI to Stream.
     *
     * @param uri the URI to convert.
     * @return the Stream.
     * @throws IOException throw if any error is occurred.
     */
    public static InputStream toStream(URI uri) throws IOException {
        return uri.toURL().openStream();
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     *
     * @param filename the String path tot the the File to convert.
     * @return the URI with prefix.
     */
    public static URI toUriWithPrefix(String filename) {
        return URI.create(toStringUriWithPrefix(filename));
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     *
     * @param file the File to convert.
     * @return the String URI with prefix.
     */
    public static String toStringUriWithPrefix(File file) {
        return toStringUriWithPrefix(file.getAbsolutePath());
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     *
     * @param file the File to convert.
     * @return the URI with prefix.
     */
    public static URI toUriWithPrefix(File file) {
        return URI.create(toStringUriWithPrefix(file.getAbsolutePath()));
    }

    /**
     * Method for convert a reference path to a resource in the classpath to a file with path in the system.
     *
     * @param referenceResourcePath string of the reference path to the resource.
     * @param thisClass             thi class.
     * @return file correspondent to the reference file of the resources.
     */
    public static File toFile(String referenceResourcePath, Class<?> thisClass) {
        try {
            //noinspection ConstantConditions
            return new File(thisClass.getClassLoader().getResource(referenceResourcePath).getFile());
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }



    /**
     * Convert filename string to a URI.
     * Map '\' characters to '/' (this might break if '\' is used in
     * a Unix filename, but this is assumed to be a very rare occurrence
     * as '\' is often used with special meaning on Unix.)
     * For unix-like systems, the absolute filename begins with a '/' and is preceded by "file://".
     * For other systems an extra '/' must be supplied.
     *
     * @param filePath string of the path to the file
     * @return path to the in uri formato with prefix file:///
     */
    public static String toStringUriWithPrefix(String filePath) {
        StringBuilder mapFileName = new StringBuilder(filePath);
        for (int i = 0; i < mapFileName.length(); i++) {
            if (mapFileName.charAt(i) == '\\')
                mapFileName.setCharAt(i, '/');
        }
        if (filePath.charAt(0) == '/') return "file://" + mapFileName.toString();
        else return "file:///" + mapFileName.toString();
    }

    /**
     * Method for get in more dinamic way the current directory of the projct
     * equivalent to : dir = System.getProperty("user.dir");
     *
     * @return string of the path to the user directory of the project
     */
    public static String getDirectoryUser() {
        String dir;
        try {
            //1 Method
            //File currentDirFile = new File("");
            //dir = currentDirFile.getAbsolutePath();
            //2 Method
            //dir = System.getProperty("user.dir")+File.separator;
            //3 Method
            //dir = convertFileToUri2(dir)+"/";
            //dir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());
            //4 Method
            dir = new File(".").getCanonicalPath() + File.separator;
            //5 Method
            //Path currentRelativePath = Paths.get("");
            //dir = currentRelativePath.toAbsolutePath().toString()+File.separator;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            dir = null;
        }
        return dir;
    }

    /**
     * Method to get the directory name where a file is allocated
     *
     * @param file the File Object.
     * @return the String name of the directory where the file is allocated.
     */
    public static String getDirectoryName(File file) {
        if (file.exists()) {
            return file.getParent();
        } else {
            logger.warn("The file " + file.getAbsolutePath() + " not exists.");
            return null;
        }
    }

    /**
     * Method to get the directory File where a file is allocated
     *
     * @param file the File Object.
     * @return the File of the directory where the file is allocated.
     */
    public static File getDirectoryFile(File file) {
        if (file.exists()) {
            return file.getParentFile();
        } else {
            logger.warn("The file " + file.getAbsolutePath() + " not exists.");
            return null;
        }
    }

    public static File getDirectoryFile(String fullPathFile) {
        return getDirectoryFile(new File(fullPathFile));
    }

    public static String getDirectoryFullPath(File file) {
        if (file.exists()) {
            return file.getAbsoluteFile().getParentFile().getAbsolutePath();
        } else {
            logger.warn("The file " + file.getAbsolutePath() + " not exists.");
            return null;
        }
    }

    public static String getDirectoryFullPath(String fullPathFile) {
        return getDirectoryFullPath(new File(fullPathFile));
    }

    /**
     * Method to get the letter fo teh current disk where the file is located.
     *
     * @return the letter of the disk.
     */
    public static String getCurrentDisk() {
        String dir = getDirectoryUser();
        String[] split = dir.split(":");
        dir = split[0];
        return dir + ":".toLowerCase();
    }

    /**
     * Method to convert a String content to a Temporary File.
     *
     * @param content  the String content.
     * @param fullPath the String output path for the temporary File.
     * @return the temporary File.
     */
    public static File createTempFile(String content, String fullPath) {
        try {
            //create a temp file
            File temp = File.createTempFile(
                    getFilename(fullPath),
                    getExtension(fullPath),
                    getDirectoryFile(fullPath)
            );
            // Delete temp file when program exits.
            temp.deleteOnExit();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
                bw.write(content);
            }
            return temp;
        } catch (IOException e) {
            try{
                return createTempFile(getFilename(fullPath),getExtension(fullPath),getDirectoryFile(fullPath));
            }catch(Exception e1) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * Create a temporary file in a given directory.
     * <p/>
     * <p>The file denoted by the returned abstract pathname did not
     * exist before this method was invoked, any subsequent invocation
     * of this method will yield a different file name.</p>
     * <p/>
     * The filename is prefixNNNNNsuffix where NNNN is a random number
     * </p>
     * <p>This method is different to {@link File#createTempFile(String, String, File)} of JDK 1.2
     * as it doesn't create the file itself.
     * It uses the location pointed to by java.io.tmpdir
     * when the parentDir attribute is
     * null.</p>
     * <p>To delete automatically the file created by this method, use the
     * {@link File#deleteOnExit()} method.</p>
     *
     * @param prefix prefix before the random number
     * @param suffix file extension; include the '.'
     * @param parentDir Directory to create the temporary file in <code>-java.io.tmpdir</code>
     * used if not specificed
     * @return a File reference to the new temporary file.
     */
    private static File createTempFile( String prefix, String suffix, File parentDir ) {
        File result;
        String parent = System.getProperty( "java.io.tmpdir" );
        if ( parentDir != null )  parent = parentDir.getPath();

        DecimalFormat fmt = new DecimalFormat( "#####" );
        SecureRandom secureRandom = new SecureRandom();
        long secureInitializer = secureRandom.nextLong();
        Random rand = new Random( secureInitializer + Runtime.getRuntime().freeMemory() );
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized ( rand ) {
            do {
                result = new File( parent, prefix + fmt.format( Math.abs( rand.nextInt() ) ) + suffix );
            }
            while ( result.exists() );
        }
        return result;
    }


    /**
     * Method to convert a resource file to a Stream.
     *
     * @param pathToFile String path to the Resource File to read(reference path).
     * @return the Stream of the File.
     */
    public static InputStream toStream(String pathToFile) {
        // JDK7 try-with-resources ensures to close stream automatically
        // return new FileInputStream(new File(pathToFile));
        try (InputStream is = FileUtilities.class.getResourceAsStream(pathToFile)) {
            int Byte; // Byte because byte is keyword!
            while ((Byte = is.read()) != -1) {
                logger.info(String.valueOf((char) Byte));
            }
            return is;
        } catch (IOException e) {
            logger.error("The file:" + pathToFile + " not exists:" + e.getMessage(), e);
            return null;
        }
        //Alternative
//        URL resourceUrl = getClass().getResource("/sample.txt");
//        Path resourcePath = Paths.get(resourceUrl.toURI());
//        File f = new File("/spring-hibernate4v2.xml");
    }


    /**
     * Method to covnert a resource file to a Stream.
     *
     * @param file File to read.
     * @return the Stream of the File.
     */
    public static InputStream toStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("The file:" + file.getAbsolutePath() + " not exists:" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to convert a Stream to a File.
     *
     * @param inStream       the InputStream to decode.
     * @param filePathOutput the String path the the new location of the file.
     * @return the File Object.
     */
    public static File toFile(InputStream inStream, String filePathOutput) {
        try (OutputStream outputStream = new FileOutputStream(new File(filePathOutput))) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        if (new File(filePathOutput).exists()) return new File(filePathOutput);
        else {
            logger.warn("The file:" + new File(filePathOutput).getAbsolutePath() + " not exists.");
            return null;
        }
    }

    /**
     * Method to convert a resource file to a Stream.
     *
     * @param fileName String name of the Resource File to read(reference path).
     * @param clazz    the Class who call this method.
     * @return the Stream of the File..
     */
    public static String toString(String fileName, Class<?> clazz) {
        try {
            StringBuilder result = new StringBuilder("");
            //Get file from resources folder
            //noinspection ConstantConditions
            File file = new File(clazz.getClassLoader().getResource(fileName).getFile());
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.append(line).append("\n");
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
            return result.toString();
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to convert a resource file to a Stream.
     *
     * @param name  String name of the class
     * @param clazz the Class who call this method.
     * @return the Stream of the File.
     */
    public static InputStream toStream(String name, Class<?> clazz) {
        name = resolveName(name);
        try {
            // A system class.
            return ClassLoader.getSystemResourceAsStream(name);
        } catch (NullPointerException e) {
            try {
                return clazz.getClassLoader().getResourceAsStream(name);
            } catch (NullPointerException ex) {
                logger.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    /**
     * Add a package name prefix if the name is not absolute Remove leading "/"
     * if name is absolute.
     *
     * @param name string name of the class
     * @return the full name package+class
     */
    private static String resolveName(String name) {
        if (StringUtilities.isNullOrEmpty(name)) return name;
        if (!name.startsWith("/")) {
            Class<?> clazz = FileUtilities.class;
            while (clazz.isArray()) {
                clazz = clazz.getComponentType();
            }
            String baseName = clazz.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) name = baseName.substring(0, index).replace('.', '/') + "/" + name;
        } else {
            name = name.substring(1);
        }
        return name;
    }

    /**
     * Method for compress file of triple before upload to thte repository make
     * the upload more faster.
     *
     * @param file file of input
     * @return InputStream of the file
     */
    public static GZIPInputStream toGZIP(File file) {
        try {
            return new GZIPInputStream(new FileInputStream(file));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method for compress file of triple before upload to the repository
     * make the upload more faster.
     *
     * @param filePathToFile string of the path tot the file
     * @return InputStream of the file
     */
    public static GZIPInputStream toGZIP(String filePathToFile) {
        return toGZIP(new File(filePathToFile));
    }

    /**
     * Method utility: help to coded the content of the file.
     *
     * @param file      the File to code.
     * @param algorithm the Hash algorithm you use.
     * @return the String content of the file coded.
     */
    private static String hashFile(File file, String algorithm) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytesBuffer = new byte[1024];
            int bytesRead;//= -1
            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }
            byte[] hashedBytes = digest.digest();
            return StringUtilities.toHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to convert a File to a MD5 hash string.
     *
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String toMD5(File file) {
        return hashFile(file, "MD5");
    }

    /**
     * Method to convert a File to a SHA-1 hash string.
     *
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String toSHA1(File file) {
        return hashFile(file, "SHA-1");
    }

    /**
     * Method to convert a File to a SHA-256 hash string.
     *
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static String toSHA256(File file) {
        return hashFile(file, "SHA-256");
    }

    /**
     * Method to convert a String to a File.
     *
     * @param stringText   the String content.
     * @param fullPathfile the String to the new location of the File.
     * @return the File Object.
     */
    public static File toFile(String stringText, String fullPathfile) {
        return toFile(stringText, new File(fullPathfile));
    }

    /**
     * Method to convert a String to a File.
     *
     * @param stringText the String content.
     * @param file       the  File.
     * @return the File Object.
     */
    public static File toFile(String stringText, File file) {
        return writeToFile(stringText, file);
    }

    /**
     * Method to convert a File to a Writer Object.
     *
     * @param file the File to convert.
     * @return the Writer Object.
     */
    public static Writer toWriter(File file) {
        Writer writer;
        try {
            writer = new FileWriter(file);
            return writer;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to convert a File to a Reader Object.
     *
     * @param file the File to convert.
     * @return the Writer Object.
     */
    public static Reader toReader(File file) {
        Reader reader;
        try {
            reader = new FileReader(file);
            return reader;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Locate the specific file.
     * Return the (URL decoded) abolute pathname to the file or null.
     *
     * @param findFile the String name of file to search.
     * @param basePath the String base of the path to the File.
     * @return the String path to the file.
     */
    public static String locateFile(String findFile, String basePath) {
        URL url;
        String fullPathName;
        StringBuffer decodedPathName;
        int pos, len, start;
        try {
            if (findFile == null) throw new FileNotFoundException("locateFile: null file name");
            if (findFile.startsWith(basePath)) return findFile.substring(basePath.length());
            if ((fullPathName = locateByProperty(findFile)) != null) return fullPathName;
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
                if (start < len) decodedPathName.append(fullPathName.substring(start, len));
                fullPathName = decodedPathName.toString();
                if (platformIsWindows()) fullPathName = fullPathName.substring(1, fullPathName.length());
                return fullPathName;
            }
            throw new FileNotFoundException("locateFile: file not found: " + findFile);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Locate the specific file.
     * Return the file name in URL form or null.
     *
     * @param findFile the String name of file to search.
     * @param basePath the string prefix of the findFile e.g. "abs://"
     * @return the String path to the file.
     */
    public static URL locateURL(String findFile, String basePath) {
        URL url;
        String fullPathName;
        try {
            if (findFile == null) throw new FileNotFoundException("locateURL: null file name");
            try {
                if (findFile.startsWith(basePath)) {
                    return (new URL("file:/" + findFile.substring(basePath.length())));
                }
                if ((fullPathName = locateByProperty(findFile)) != null) {
                    if (platformIsWindows()) url = new URL("file:/" + fullPathName);
                    else url = new URL("file:" + fullPathName);
                    return url;
                }
            } catch (MalformedURLException e) {
                logger.error("locateURL: URL creation problem:" + e.getMessage(), e);
                throw new FileNotFoundException("locateURL: URL creation problem");
            }
            if ((url = locateByResource(findFile)) != null) return url;
            throw new FileNotFoundException("locateURL: file not found: " + findFile);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Search for a file using the properties: user.dir, user.home, java.home
     * Returns absolute path name or null.
     *
     * @param findFile the String name of file to search.
     * @return the String path to the file.
     */
    private static synchronized String locateByProperty(String findFile) {
        String fullPathName = null;
        String dir;
        File f = null;
        if (findFile == null) {
            logger.error("The findFile parameter can't be NULL.");
            return null;
        }
        try {
            logger.warn("Searching in 'user.dir' for: " + findFile);
            dir = System.getProperty("user.dir");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                logger.warn("Found in 'user.dir':" + fullPathName);
                return fullPathName;
            }
            dir = System.getProperty("user.home");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                logger.warn("Found in 'user.home':" + fullPathName);
                return fullPathName;
            }
            dir = System.getProperty("java.home");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                logger.warn("Found in 'java.home':" + fullPathName);
                return fullPathName;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return fullPathName;
    }

    /**
     * Search for a file using the properties: user.dir, user.home, java.home
     * Returns URL or null.
     *
     * @param findFile the String name of file to search.
     * @return the String path to the file.
     */
    private static URL locateByResource(String findFile) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(findFile);
        if (url == null) url = FileUtilities.class.getResource("/" + findFile);
        logger.warn("Search succeeded via getResource()");
        return url;
    }

    /**
     * Method to get the String array of the columns of a CSV File.
     *
     * @param fileCSV      the File CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] CSVGetHeaders(File fileCSV, boolean hasFirstLine) {
       return OpenCsvUtilities.getHeaders(fileCSV,hasFirstLine);
    }

    /**
     * Method to get the content of a comma separated file (.csv,.input,.txt)
     *
     * @param CSV    the File comma separated.
     * @param noHeaders if true jump the first line of the content.
     * @return the List of Array of the content of the File comma separated.
     */
    public static List<String[]> CSVGetContent(File CSV, boolean noHeaders) {
        return OpenCsvUtilities.parseCSVFileAsList(CSV,noHeaders);
    }

    /**
     * Method to convert a MultipartFile to a File
     *
     * @param multiPartFile the MultiPartFile of Spring to convert.
     * @return the File.
     */
    public static File toFile(org.springframework.web.multipart.MultipartFile multiPartFile) {
        File convFile = new File(multiPartFile.getOriginalFilename());
        /*convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile); fos.write(multiPartFile.getBytes());fos.close();*/
        try {
            multiPartFile.transferTo(convFile);
            return convFile;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to convert e array of Bytes to a path Object.
     *
     * @param bytes         the array of bytes.
     * @param pathToTheFile the String path to the path object for the File.
     * @return the Path object populate with the Array of Bytes.
     */
    public static Path toPath(byte[] bytes, String pathToTheFile) {
        Path path = Paths.get(pathToTheFile);
        return toPath(bytes, path);
    }

    /**
     * Method to convert e array of Bytes to a path Object.
     *
     * @param bytes         the array of bytes.
     * @param pathToTheFile the String path to the path object for the File.
     * @return the path object populate with the Array of Bytes.
     */
    public static Path toPath(byte[] bytes, Path pathToTheFile) {
        try {
            Files.write(pathToTheFile, bytes);
            return pathToTheFile;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to convert e array of Bytes to a path Object.
     *
     * @param bytes         the array of bytes.
     * @param pathToTheFile the String path to the path object for the File.
     * @return the File object populate with the Array of Bytes.
     */
    public static File toFile(byte[] bytes, String pathToTheFile) {
        return toPath(bytes, pathToTheFile).toFile();
    }

    /**
     * Method to 'walk' within many directory under a root directory and load alll files in these.
     *
     * @param preload the String path to the root directory.
     * @return the List of File prsent in the Directory preload.
     */
    public static List<File> walk(String preload) {
        return walk(new File(preload));
    }

    /**
     * Method to 'walk' within many directory under a root directory and load alll files in these..
     *
     * @param preload the File root directory
     * @return the List of File prsent in the Directory preload.
     */
    public static List<File> walk(File preload) {
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
     * Map for brute force replace of all unicode escape on the text
     */
    private static void setMapUnicodeEscaped() {
        unicodeCodePoint.put("U+0000", "");
        unicodeCodePoint.put("U+0001", "");
        unicodeCodePoint.put("U+0002", "");
        unicodeCodePoint.put("U+0003", "");
        unicodeCodePoint.put("U+0004", "");
        unicodeCodePoint.put("U+0005", "");
        unicodeCodePoint.put("U+0006", "");
        unicodeCodePoint.put("U+0007", "");
        unicodeCodePoint.put("U+0008", "");
        unicodeCodePoint.put("U+0009", "");
        unicodeCodePoint.put("U+000A", "");
        unicodeCodePoint.put("U+000B", "");
        unicodeCodePoint.put("U+000C", "");
        unicodeCodePoint.put("U+000D", "");
        unicodeCodePoint.put("U+000E", "");
        unicodeCodePoint.put("U+000F", "");
        unicodeCodePoint.put("U+0010", "");
        unicodeCodePoint.put("U+0011", "");
        unicodeCodePoint.put("U+0012", "");
        unicodeCodePoint.put("U+0013", "");
        unicodeCodePoint.put("U+0014", "");
        unicodeCodePoint.put("U+0015", "");
        unicodeCodePoint.put("U+0016", "");
        unicodeCodePoint.put("U+0017", "");
        unicodeCodePoint.put("U+0018", "");
        unicodeCodePoint.put("U+0019", "");
        unicodeCodePoint.put("U+001A", "");
        unicodeCodePoint.put("U+001B", "");
        unicodeCodePoint.put("U+001C", "");
        unicodeCodePoint.put("U+001D", "");
        unicodeCodePoint.put("U+001E", "");
        unicodeCodePoint.put("U+001F", "");
        unicodeCodePoint.put("U+0020", " ");
        unicodeCodePoint.put("U+0021", "!");
        unicodeCodePoint.put("U+0022", "\"");
        unicodeCodePoint.put("U+0023", "#");
        unicodeCodePoint.put("U+0024", "$");
        unicodeCodePoint.put("U+0025", "%");
        unicodeCodePoint.put("U+0026", "&");
        unicodeCodePoint.put("U+0027", "'");
        unicodeCodePoint.put("U+0028", "(");
        unicodeCodePoint.put("U+0029", ")");
        unicodeCodePoint.put("U+002A", "*");
        unicodeCodePoint.put("U+002B", "+");
        unicodeCodePoint.put("U+002C", ",");
        unicodeCodePoint.put("U+002D", "-");
        unicodeCodePoint.put("U+002E", ".");
        unicodeCodePoint.put("U+002F", "/");
        unicodeCodePoint.put("U+0030", "0");
        unicodeCodePoint.put("U+0031", "1");
        unicodeCodePoint.put("U+0032", "2");
        unicodeCodePoint.put("U+0033", "3");
        unicodeCodePoint.put("U+0034", "4");
        unicodeCodePoint.put("U+0035", "5");
        unicodeCodePoint.put("U+00FF", "ÿ");
        unicodeCodePoint.put("U+FEFF", "");
        unicodeCodePoint.put("U+0036", "6");
        unicodeCodePoint.put("U+0037", "7");
        unicodeCodePoint.put("U+0038", "8");
        unicodeCodePoint.put("U+0039", "9");
        unicodeCodePoint.put("U+003A", ":");
        unicodeCodePoint.put("U+003B", ";");
        unicodeCodePoint.put("U+003C", "<");
        unicodeCodePoint.put("U+003D", "=");
        unicodeCodePoint.put("U+003E", ">");
        unicodeCodePoint.put("U+003F", "?");
        unicodeCodePoint.put("U+0040", "@");
        unicodeCodePoint.put("U+0041", "A");
        unicodeCodePoint.put("U+0042", "B");
        unicodeCodePoint.put("U+0043", "C");
        unicodeCodePoint.put("U+0044", "D");
        unicodeCodePoint.put("U+0045", "E");
        unicodeCodePoint.put("U+0046", "F");
        unicodeCodePoint.put("U+0047", "G");
        unicodeCodePoint.put("U+0048", "H");
        unicodeCodePoint.put("U+0049", "I");
        unicodeCodePoint.put("U+004A", "J");
        unicodeCodePoint.put("U+004B", "K");
        unicodeCodePoint.put("U+004C", "L");
        unicodeCodePoint.put("U+004D", "M");
        unicodeCodePoint.put("U+004E", "N");
        unicodeCodePoint.put("U+004F", "O");
        unicodeCodePoint.put("U+0050", "P");
        unicodeCodePoint.put("U+0051", "Q");
        unicodeCodePoint.put("U+0052", "R");
        unicodeCodePoint.put("U+0053", "S");
        unicodeCodePoint.put("U+0054", "T");
        unicodeCodePoint.put("U+0055", "U");
        unicodeCodePoint.put("U+0056", "V");
        unicodeCodePoint.put("U+0057", "W");
        unicodeCodePoint.put("U+0058", "X");
        unicodeCodePoint.put("U+0059", "Y");
        unicodeCodePoint.put("U+005A", "Z");
        unicodeCodePoint.put("U+005B", "[");
        unicodeCodePoint.put("U+005C", "\\");
        unicodeCodePoint.put("U+005D", "]");
        unicodeCodePoint.put("U+005E", "^");
        unicodeCodePoint.put("U+005F", "_");
        unicodeCodePoint.put("U+0060", "`");
        unicodeCodePoint.put("U+0061", "a");
        unicodeCodePoint.put("U+0062", "b");
        unicodeCodePoint.put("U+0063", "c");
        unicodeCodePoint.put("U+0064", "d");
        unicodeCodePoint.put("U+0065", "e");
        unicodeCodePoint.put("U+0066", "f");
        unicodeCodePoint.put("U+0067", "g");
        unicodeCodePoint.put("U+0068", "h");
        unicodeCodePoint.put("U+0069", "i");
        unicodeCodePoint.put("U+006A", "j");
        unicodeCodePoint.put("U+006B", "k");
        unicodeCodePoint.put("U+006C", "l");
        unicodeCodePoint.put("U+006D", "m");
        unicodeCodePoint.put("U+006E", "n");
        unicodeCodePoint.put("U+006F", "o");
        unicodeCodePoint.put("U+0070", "p");
        unicodeCodePoint.put("U+0071", "q");
        unicodeCodePoint.put("U+0072", "r");
        unicodeCodePoint.put("U+0073", "s");
        unicodeCodePoint.put("U+0074", "t");
        unicodeCodePoint.put("U+0075", "u");
        unicodeCodePoint.put("U+0076", "v");
        unicodeCodePoint.put("U+0077", "w");
        unicodeCodePoint.put("U+0078", "x");
        unicodeCodePoint.put("U+0079", "y");
        unicodeCodePoint.put("U+007A", "z");
        unicodeCodePoint.put("U+007B", "{");
        unicodeCodePoint.put("U+007C", "|");
        unicodeCodePoint.put("U+007D", "}");
        unicodeCodePoint.put("U+007E", "~");
        unicodeCodePoint.put("U+007F", "");
        unicodeCodePoint.put("U+0080", "");
        unicodeCodePoint.put("U+0081", "");
        unicodeCodePoint.put("U+0082", "");
        unicodeCodePoint.put("U+0083", "");
        unicodeCodePoint.put("U+0084", "");
        unicodeCodePoint.put("U+0085", "");
        unicodeCodePoint.put("U+0086", "");
        unicodeCodePoint.put("U+0087", "");
        unicodeCodePoint.put("U+0088", "");
        unicodeCodePoint.put("U+0089", "");
        unicodeCodePoint.put("U+008A", "");
        unicodeCodePoint.put("U+008C", "");
        unicodeCodePoint.put("U+008D", "");
        unicodeCodePoint.put("U+008E", "");
        unicodeCodePoint.put("U+008F", "");
        unicodeCodePoint.put("U+0090", "");
        unicodeCodePoint.put("U+0091", "");
        unicodeCodePoint.put("U+0092", "");
        unicodeCodePoint.put("U+0093", "");
        unicodeCodePoint.put("U+0094", "");
        unicodeCodePoint.put("U+0095", "");
        unicodeCodePoint.put("U+0096", "");
        unicodeCodePoint.put("U+0097", "");
        unicodeCodePoint.put("U+0098", "");
        unicodeCodePoint.put("U+0099", "");
        unicodeCodePoint.put("U+009A", "");
        unicodeCodePoint.put("U+009B", "");
        unicodeCodePoint.put("U+009C", "");
        unicodeCodePoint.put("U+009D", "");
        unicodeCodePoint.put("U+009E", "");
        unicodeCodePoint.put("U+009F", "");
        unicodeCodePoint.put("U+00A0", "");
        unicodeCodePoint.put("U+00A1", "¡");
        unicodeCodePoint.put("U+00A2", "¢");
        unicodeCodePoint.put("U+00A3", "£");
        unicodeCodePoint.put("U+00A4", "¤");
        unicodeCodePoint.put("U+00A5", "¥");
        unicodeCodePoint.put("U+00A6", "¦");
        unicodeCodePoint.put("U+00A7", "§");
        unicodeCodePoint.put("U+00A8", "¨");
        unicodeCodePoint.put("U+00A9", "©");
        unicodeCodePoint.put("U+00AA", "ª");
        unicodeCodePoint.put("U+00AB", "«");
        unicodeCodePoint.put("U+00AC", "¬");
        unicodeCodePoint.put("U+00AD", "­");
        unicodeCodePoint.put("U+00AE", "®");
        unicodeCodePoint.put("U+00AF", "¯");
        unicodeCodePoint.put("U+00B0", "°");
        unicodeCodePoint.put("U+00B1", "±");
        unicodeCodePoint.put("U+00B2", "²");
        unicodeCodePoint.put("U+00B3", "³");
        unicodeCodePoint.put("U+00B4", "´");
        unicodeCodePoint.put("U+00B5", "µ");
        unicodeCodePoint.put("U+00B6", "¶");
        unicodeCodePoint.put("U+00B7", "·");
        unicodeCodePoint.put("U+00B8", "¸");
        unicodeCodePoint.put("U+00B9", "¹");
        unicodeCodePoint.put("U+00BA", "º");
        unicodeCodePoint.put("U+00BB", "»");
        unicodeCodePoint.put("U+00BC", "¼");
        unicodeCodePoint.put("U+00BD", "½");
        unicodeCodePoint.put("U+00BE", "¾");
        unicodeCodePoint.put("U+00BF", "¿");
        unicodeCodePoint.put("U+00C0", "À");
        unicodeCodePoint.put("U+00C1", "Á");
        unicodeCodePoint.put("U+00C2", "Â");
        unicodeCodePoint.put("U+00C3", "Ã");
        unicodeCodePoint.put("U+00C4", "Ä");
        unicodeCodePoint.put("U+00C5", "Å");
        unicodeCodePoint.put("U+00C6", "Æ");
        unicodeCodePoint.put("U+00C7", "Ç");
        unicodeCodePoint.put("U+00C8", "È");
        unicodeCodePoint.put("U+00C9", "É");
        unicodeCodePoint.put("U+00CA", "Ê");
        unicodeCodePoint.put("U+00CB", "Ë");
        unicodeCodePoint.put("U+00CC", "Ì");
        unicodeCodePoint.put("U+00CD", "Í");
        unicodeCodePoint.put("U+00CE", "Î");
        unicodeCodePoint.put("U+00CF", "Ï");
        unicodeCodePoint.put("U+00D0", "Ð");
        unicodeCodePoint.put("U+00D1", "Ñ");
        unicodeCodePoint.put("U+00D2", "Ò");
        unicodeCodePoint.put("U+00D3", "Ó");
        unicodeCodePoint.put("U+00D4", "Ô");
        unicodeCodePoint.put("U+00D5", "Õ");
        unicodeCodePoint.put("U+00D6", "Ö");
        unicodeCodePoint.put("U+00D7", "×");
        unicodeCodePoint.put("U+00D8", "Ø");
        unicodeCodePoint.put("U+00D9", "Ù");
        unicodeCodePoint.put("U+00DA", "Ú");
        unicodeCodePoint.put("U+00DB", "Û");
        unicodeCodePoint.put("U+00DC", "Ü");
        unicodeCodePoint.put("U+00DD", "Ý");
        unicodeCodePoint.put("U+00DE", "Þ");
        unicodeCodePoint.put("U+00DF", "ß");
        unicodeCodePoint.put("U+00E0", "à");
        unicodeCodePoint.put("U+00E1", "á");
        unicodeCodePoint.put("U+00E2", "â");
        unicodeCodePoint.put("U+00E3", "ã");
        unicodeCodePoint.put("U+00E4", "ä");
        unicodeCodePoint.put("U+00E5", "å");
        unicodeCodePoint.put("U+00E6", "æ");
        unicodeCodePoint.put("U+00E7", "ç");
        unicodeCodePoint.put("U+00E8", "è");
        unicodeCodePoint.put("U+00E9", "é");
        unicodeCodePoint.put("U+00EA", "ê");
        unicodeCodePoint.put("U+00EB", "ë");
        unicodeCodePoint.put("U+00EC", "ì");
        unicodeCodePoint.put("U+00ED", "í");
        unicodeCodePoint.put("U+00EE", "î");
        unicodeCodePoint.put("U+00EF", "ï");
        unicodeCodePoint.put("U+00F0", "ð");
        unicodeCodePoint.put("U+00F1", "ñ");
        unicodeCodePoint.put("U+00F2", "ò");
        unicodeCodePoint.put("U+00F3", "ó");
        unicodeCodePoint.put("U+00F4", "ô");
        unicodeCodePoint.put("U+00F5", "õ");
        unicodeCodePoint.put("U+00F6", "ö");
        unicodeCodePoint.put("U+00F7", "÷");
        unicodeCodePoint.put("U+00F8", "ø");
        unicodeCodePoint.put("U+00F9", "ù");
        unicodeCodePoint.put("U+00FA", "ú");
        unicodeCodePoint.put("U+00FB", "û");
        unicodeCodePoint.put("U+00FC", "ü");
        unicodeCodePoint.put("U+00FD", "ý");
        unicodeCodePoint.put("U+00FE", "þ");

    }

    /**
     * Read small and large file of text
     * Note: the javadoc of Files.readAllLines says it's intended for small
     * files. But its implementation uses buffering, so it's likely good
     * even for fairly large files
     *
     * @param fileInput     the file you want to read
     * @param encodingInput the Charset encoding for the input File.
     * @return a list of lines
     */
    public static List<String> readSmall(File fileInput, Charset encodingInput) {
        if (!fileInput.exists()) createFile(fileInput);
        if (encodingInput == null) encodingInput = StandardCharsets.UTF_8;
        Path path = Paths.get(fileInput.getAbsolutePath());
        try {
            return Files.readAllLines(path, encodingInput);
        } catch (IOException e) {
            encodingInput = StandardCharsets.UTF_8;
            try {
                return Files.readAllLines(path, encodingInput);
            } catch (IOException e1) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

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

    /**
     * Method to write Small File.
     *
     * @param content        a List of String to write on the File.
     * @param fileOutput     the File where to write.
     * @param encodingOutput the Charset Encoding if null is UTF8.
     * @return if true all the operation are done.
     */
    public static Boolean writeSmallFile(Collection<String> content, File fileOutput, Charset encodingOutput) {
        if (!fileOutput.exists()) createFile(fileOutput);
        if (encodingOutput == null) encodingOutput = StandardCharsets.UTF_8;
        Path path = Paths.get(fileOutput.getAbsolutePath());
        try {
            Files.write(path, content, encodingOutput);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
     * escaped sequence for characters outside of ASCII.
     * It is equivalent to: native2ascii -encoding utf-8.
     *
     * @param UTF8 encoding of input.
     * @return ASCII encoding of output.
     */
    public static List<String> toAscii(File UTF8) {
        try {
            List<String> list = new ArrayList<>();
            if (UTF8 == null) {
                logger.info("=== Usage: java UTF8ToAscii <filename> ===");
                return null;
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(UTF8), "UTF-8"))) {
                String line = r.readLine();
                while (line != null) {
                    logger.info(StringUtilities.unicodeEscape(line));
                    line = r.readLine();
                    list.add(line);
                }
            }
            return list;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Reads file with unicode escaped characters and write them out to
     * stdout in UTF-8.
     * This utility is equivalent to: native2ascii -reverse -encoding utf-8.
     *
     * @param ASCII file of input in ASCII encoding.
     * @return UTF8 file of input in UTF8 encoding.
     */
    public static List<String> toUTF8(File ASCII) {
        try {
            List<String> list = new ArrayList<>();
            if (ASCII == null) {
                logger.info(" === Usage: java UnicodeEscape2UTF8 <filename> ===");
                return null;
            }
            try (BufferedReader r = new BufferedReader(new FileReader(ASCII))) {
                String line = r.readLine();
                while (line != null) {
                    line = StringUtilities.convertUnicodeEscapeToASCII(line);
                    byte[] bytes = line.getBytes("UTF-8");
                    list.add(StringUtilities.toString(bytes));
                }
            }
            return list;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to rewrite a file in the UTF-8 encoding
     *
     * @param fileASCII file of input in ASCII encoding
     * @return the File converted to UTF8 encoding.
     */
    public static File writeToUTF8(File fileASCII) {
        List<String> list = toUTF8(fileASCII);
        File fileUTF8 = new File(fileASCII.getAbsolutePath());
        //fileASCII = new File(filePathASCII);
        write(list, fileUTF8, StringUtilities.US_ASCII, StringUtilities.UTF_8);
        boolean delete = fileASCII.delete();
        return fileUTF8;
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     *
     * @param fileUTF8 file of input in UTF8 encoding
     * @return the File converted with ASCII.
     */
    public static File writeToASCII(File fileUTF8) {
        List<String> list = toAscii(fileUTF8);
        File fileAscii = new File(fileUTF8.getAbsolutePath());
        write(list, fileAscii, null, StringUtilities.US_ASCII);
        boolean b = fileUTF8.delete();
        if(!b) return null;
        return fileAscii;
    }

   /* private static final char[] hexChar = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    */
    /*
     * Method for convert a string UTF-8 to HEX
     * @param text string of text you want to convert to HEX
     * @return the text in HEX encoding
     */
    /*
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
    }*/

    /**
     * Method to write to ANSI encoding a UTF8 File.
     * @param fileUTF8 the File UTF8.
     * @return the File with ANSi encoding.
     */
    public static File writeToANSI(File fileUTF8) {
        File fileANSI = new File(fileUTF8.getAbsolutePath());
        write(fileUTF8, fileANSI, StringUtilities.UTF_8, StringUtilities.CP1252);
        boolean delete = fileUTF8.delete();
        return fileANSI;
    }

   /* enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}*/

    /*
     *  convert unicode escapes back to char
     * @param s string to convert unicode escape.
     * @return string converted.
     */
    /*private static String convertUnicodeEscape(String s) {
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
    }*/

    /**
     * Saves a string to a file.
     *
     * @param str        string to write on the file.
     * @param fileOutput string name of the file.
     */
    private static File writeToFile(String str, File fileOutput) {
        write(Collections.singletonList(str), fileOutput, StringUtilities.DEFAULT_ENCODING, StringUtilities.UTF_8);
        return fileOutput;
    }

    public static File write(String str, File fileOutput) {
        write(Collections.singletonList(str), fileOutput, StringUtilities.DEFAULT_ENCODING, StringUtilities.UTF_8);
        return fileOutput;
    }

    public static boolean write(Collection<String> collectionContent, File fileOutput) {
        return write(collectionContent, fileOutput, null, null);
    }

    public static boolean write(Collection<String> collectionContent, File fileOutput, Charset encodingOutput) {
        return write(collectionContent, fileOutput, null, encodingOutput);
    }

    public static boolean write(Collection<String> collectionContent, File fileOutput, Charset encodingInput, Charset encodingOutput) {
        boolean replace = false;
        if (encodingInput != null) {
            Collection<String> newCol = new ArrayList<>();
            for (String s : collectionContent) {
                if (encodingInput.name().equals(StringUtilities.US_ASCII.name())) s = StringUtilities.toASCII(s);
                if (encodingInput.name().equals(StringUtilities.UTF_8.name())) s = StringUtilities.toUTF8(s);
                newCol.add(s);
            }
            if (encodingInput.name().equals(StringUtilities.UTF_8.name())) replace = true;
            collectionContent = new ArrayList<>();
            collectionContent.addAll(newCol);
            newCol.clear();
        }
        if (encodingOutput == null) encodingOutput = StandardCharsets.UTF_8;
        if (encodingOutput.name().toUpperCase().startsWith("UTF")) replace = true;
        logger.info("Try to writing to file named " + fileOutput.getAbsolutePath() + " with Encoding: " + encodingOutput.name());
        Path path = Paths.get(fileOutput.getAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(path, encodingOutput)) {
            for (String line : collectionContent) {
                if (replace) {
                    for (Map.Entry<String, String> entry : unicodeCodePoint.entrySet()) {
                        try {
                            String s = entry.getKey().replace("U+", "\\u");
                            if (line.contains(s)) line = line.replace(s, entry.getValue());
                        } catch (java.lang.NullPointerException ne) {
                            break;
                        }
                    } //foreach entry
                }
                //With printwriter..
                /*try (PrintWriter outWriter = new PrintWriter(writer)) {
                    outWriter.println(line);
                }*/
                writer.write(line + System.getProperty("line.separator"));
                //writer.newLine();
                writer.flush();
            }
            return true;
        } catch (java.lang.NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(File fileInput, File fileOutput, Charset encodingInput, Charset encodingOutput) {
        return write(read(fileInput, encodingInput), fileOutput, encodingInput, encodingOutput);
    }

    public static Collection<String> read(File fileInput, Charset encodingInput) {
        if (encodingInput == null) encodingInput = StandardCharsets.UTF_8;
        Collection<String> collection = new ArrayDeque<>();
        try {
            //FileInputStream fis = new FileInputStream(fileInput);
            // try (Scanner scanner = new Scanner(new FileInputStream(aFile), encoding.name())) {
            // try (BufferedReader reader = Files.newBufferedReader(path, encoding)) {
            //try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileInput), encodingInput))) {
                //with bytes...
                /*int ch;
                while ((ch = in.read()) > -1) {
                    collection.add((char) ch);
                }*/
                //with Scanner...
                /*while (scanner.hasNextLine()) {
                    collection.add(scanner.nextLine() + System.getProperty("line.separator"));
                }*/
                //with stream for...
               /* for(String line; (line = in.readLine()) != null;) {
                    collection.add(line+System.getProperty("line.separator"));
                }*/
                //with stream while...
                String line;
                while ((line = in.readLine()) != null) {
                    //process each line in some way
                    collection.add(line);
                }
            }
            return collection;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to read a {@link File}
     * @param file the {@link File}.
     * @return the {@link InputStreamReader}.
     */
    public static InputStreamReader read(File file) {
        FileInputStream inputSreamfile;
        try {
            inputSreamfile = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage(),e);
            return null;
        }
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(inputSreamfile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        }
        return in;
    }

    /**
     * Method to get the String content of the File.
     * href: http://www.adam-bien.com/roller/abien/entry/java_8_reading_a_file
     * href: http://stackoverflow.com/questions/16919501/create-a-path-from-string-in-java7
     *
     * @param file     the File to copy.
     * @param encoding the Charset of the File you desire.
     * @return the String of the content of the File.
     */
    public static String toString(File file, Charset encoding) {
        return toString(file.toPath(), encoding);
    }

    /**
     * Method to get the String content of the File.
     * href: http://www.adam-bien.com/roller/abien/entry/java_8_reading_a_file
     * href: http://stackoverflow.com/questions/16919501/create-a-path-from-string-in-java7
     *
     * @param path     the Path top the File to copy.
     * @param encoding the Charset of the File you desire.
     * @return the String of the content of the File.
     */
    public static String toString(Path path, Charset encoding) {
        //OLD METHOD java 1.6
        /*StringWriter sw = new StringWriter();
        long count = 0;
        int n ; // n = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            logger.info("Reading file: " + file + " using encoding: " + encoding);
            //org.apache.commons.io.IOUtils.copy(fis, sw, encoding);
            InputStreamReader in = new InputStreamReader(fis, StringUtilities.toCharset(encoding));
            char[] buffer = new char[1024 * 4];
            while (-1 != (n = in.read(buffer))) {
                sw.write(buffer, 0, n);
                count += n;
            }
        }catch(IOException e){
            logger.error( e.getMessage(), e);
            return null;
        }
        if (count > Integer.MAX_VALUE) return null;
        return sw.toString();*/

        //NEW METHOD MORE PERFORMANCE java 1.7,1.8
        //Method 1 - This way i will be able to use Lambda expressions and streams easily also.
       /* try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get(file.toURI()),StringUtilities.UTF_8)) {
            return StringUtilities.toString(reader);
        }catch(IOException e){
            logger.error( e.getMessage(), e);
            return null;
        }*/
        //Method 2
        try {
            return new String(Files.readAllBytes(path), encoding);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to get the String content of the Stream of a File.
     * href: http://www.java2s.com/Tutorials/Java/java.nio.file/Files/Java_Files_copy_InputStream_in_Path_target_CopyOption_options_.htm.
     *
     * @param stream   the Stream of the File to convert to a String object.
     * @param encoding the Charset of the encoding of the String.
     * @return the String content o f the InputStream.
     */
    public static String toString(InputStream stream, Charset encoding) {
        Path copy_to = Paths.get(new File(".").toURI());
        try {
            Files.copy(stream, copy_to, StandardCopyOption.REPLACE_EXISTING);
            return toString(copy_to, encoding);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to read the Binary Content of a File.
     * OLD_NAME: readBinaryFileContent.
     *
     * @param pathInput the Path to the File to read.
     * @return the arrays of Bytes of the content of the File.
     */
    public static byte[] toBytes(Path pathInput) {
        try {
            return Files.readAllBytes(pathInput);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to read the Binary Content of a File.
     * OLD_NAME: readBinaryFileContent.
     *
     * @param filePathInput the String path to the File to read.
     * @return the arrays of Bytes of the content of the File.
     */
    public static byte[] toBytes(String filePathInput) {
        return toBytes(Paths.get(filePathInput));
    }

    /**
     * Method to read the Binary Content of a File.
     * OLD_NAME: readBinaryFileContent.
     *
     * @param fileInput the File to read.
     * @return the arrays of Bytes of the content of the File.
     */
    public static byte[] toBytes(File fileInput) {
        return toBytes(fileInput.toPath());
        //OLD_METHOD
        /*ByteArrayOutputStream bs;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileInput))) {
            bs = new ByteArrayOutputStream();
            try (BufferedOutputStream out = new BufferedOutputStream(bs)) {
                byte[] ioBuf = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(ioBuf)) != -1) out.write(ioBuf, 0, bytesRead);
            }
            return bs.toByteArray();
        }catch(IOException e){
            logger.error(e.getMessage(),e);
            return null;
        }*/
    }

    /**
     * Convenience method for writing bytes to an OutputStream.
     * OLD_NAME: writeBinaryFileContent.
     *
     * @param fileOutput File to write.
     * @param bbuf       The contents to write to the OutputStream, OPut.
     * @return if true all the operation are done.
     */
    public static boolean write(File fileOutput, byte[] bbuf) {
        /* try{OPut.write(bbuf, 0, bbuf.length);OPut.flush();
        }catch(Exception ex){SystemLog.exception(ex,StringUtilities.class);}
        finally{ OPut.close();}
         */
        try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(bbuf))) {
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileOutput))) {
                byte[] ioBuf = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(ioBuf)) != -1) out.write(ioBuf, 0, bytesRead);
            }
            return true;
        }//try
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to append a String text to a already existent File.
     * Important note: This method hasn't been tested yet, and was originally written a long, long time ago.
     *
     * @param fileToUpdate the File to update.
     * @param textToAppend the String text to append.
     */
    public static void appendToFile(File fileToUpdate, String textToAppend) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileToUpdate, true));
            bw.write(textToAppend);
            bw.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally { // always close the file
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
        }

    }

    /**
     * Method to check if a String path to a File is valid.
     * @param file the String path to the File.
     * @return if true the String path reference to a File.
     */
    public static boolean isFileValid(String file) {
        try {
            File f = new File(file);
            if (f.isFile() && !f.isDirectory()) return true;
            f = new File(getDirectoryUser() + file);
            if (f.isFile() && !f.isDirectory()) return true;
            f = new File(getDirectoryUser() + file);
            if (f.isFile() && !f.isDirectory()) return true;
            else {
                if (f.isDirectory()) logger.warn("The path:" + file + " is a directory");
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public InputStream getFromResourceAsStream(String name) {
        return ClassLoaderUtil.getResourceAsStream(name);
    }

    /**
     * Returns a human-readable version of the file size (original is in
     * bytes).
     *
     * @param size The number of bytes.
     * @return A human-readable display value (includes units).
     */
    public static String byteCountToDisplaySize( int size ) {
        String displaySize;
        int ONE_KB = 1024,ONE_MB = ONE_KB * ONE_KB,ONE_GB = ONE_MB * ONE_KB;
        if ( size / ONE_GB > 0 ) displaySize = String.valueOf( size / ONE_GB ) + " GB";
        else if ( size / ONE_MB > 0 ) displaySize = String.valueOf( size / ONE_MB ) + " MB";
        else if ( size / ONE_KB > 0 )displaySize = String.valueOf( size / ONE_KB ) + " KB";
        else displaySize = String.valueOf( size ) + " bytes";
        return displaySize;
    }

    /**
     * Method to check is a file exists and is valid.
     * I would recommend using isFile() instead of exists().
     * Most of the time you are looking to check if the path points to a file not only that it exists.
     * Remember that exists() will return true if your path points to a directory.
     *
     * If both exists and notExists return false, the existence of the file cannot be verified.
     * (maybe no access right to this path)
     * @param pathToFile tje {@link String} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isFileExists(String  pathToFile) {
       /* File file = new File(pathToFile);
        Path path = Paths.get(pathToFile);
        return file.isFile() && file.exists() || Files.exists(path) && Files.isRegularFile(path);*/
        return isFileExists(new File(pathToFile));
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param file tje {@link File} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isFileExists(File file) {
        Path path = Paths.get(file.getAbsolutePath());
        return file.isFile() && file.exists() || Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param pathToFile tje {@link String} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isDirectoryExists(String pathToFile) {
        /*File file = new File(pathToFile);
        Path path = Paths.get(pathToFile);
        return file.isDirectory() && file.exists() || Files.exists(path) && Files.isDirectory(path);*/
        return isDirectoryExists(new File(pathToFile));
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param file tje {@link File} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isDirectoryExists(File file) {
        Path path = Paths.get(file.getAbsolutePath());
        return file.isDirectory() && file.exists() || Files.exists(path) && Files.isDirectory(path);
    }

    /*public static boolean isDirectory(File file) {
        if (file.exists()) {
            return !file.isFile() && file.isDirectory();
        } else {
            logger.warn("The file:" + file.getAbsolutePath() + " not exists!");
            return false;
        }
    }*/

    /**
     * Method to rename the extension of a file.
     * @param source the {@link File} the file.
     * @param newExtension the {@link String} name o f the new extension.
     * @param tempMode the {@link Boolean} rename the file only on java memory not on the disk.
     * @return the {@link String} name of the file without extension.
     */
    public static String renameExtension(File source, String newExtension,boolean tempMode){
        return renameExtension(source.getAbsolutePath(),newExtension,tempMode);
    }

    /**
     * Method to rename the extension of a file.
     * @param source the {@link String} path to the file.
     * @param newExtension the {@link String} name o f the new extension.
     * @param tempMode the {@link Boolean} rename the file only on java memory not on the disk.
     * @return the {@link String} name of the file without extension.
     */
    public static String renameExtension(String source, String newExtension,boolean tempMode){
        String target;
        String currentExtension = getExtension(source);
        if (currentExtension.equals(""))target = source + "." + newExtension;
        else {
            //not work
            /*target = source.replaceFirst(Pattern.quote("." +
                    currentExtension) + "$", Matcher.quoteReplacement("." + newExtension));*/
            target = source.replace("."+currentExtension,"."+newExtension);
            try{target = target.replace("\\","\\\\");}catch(Exception ignored){}
        }
        File file = new File(source);
        if(tempMode) return target;
        if(file.renameTo(new File(target))){
            if(isFileExists(target)) {
                return target;
            }else{
                logger.error("Can't rename the extension of the file:"+file.getAbsolutePath()+" because not exists!");
                return target;
            }
        }else{
            logger.error("Can't rename the extension of the file the file to rename not exists:"+new File(source).getAbsolutePath());
            return target;
        }
    }

    /**
     * Method to remove the extension from a file
     * @param file the {@link File} path to the file.
     * @return the {@link File} name of the file without extension.
     */
    public static File removeExtension(File file) {
        String fileName = file.getAbsolutePath();
        int extPos = fileName.lastIndexOf(".");
        if(extPos == -1) return file;
        else{
            if(file.renameTo(new File(fileName.substring(0, extPos)))){
                return file;
            }else{
                logger.error("Can't remove the extension of the file:"+file.getAbsolutePath());
                return file;
            }
        }
    }

    /**
     * Method to remove the extension from a file
     * @param fileName the {@link String} path to the file.
     * @return the {@link String} name of the file without extension.
     */
    public static String removeExtension(String fileName) {
        int extPos = fileName.lastIndexOf(".");
        if(extPos == -1)return fileName;
        else return fileName.substring(0, extPos);
    }

    /**
     * Utility for a depth first traversal of a file-system starting from a
     * given node (file or directory). e.g.
     * FileWalker.Handler handler = new FileWalker.Handler() {
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
        private Handler handler;

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
        public void walk(File node) throws Exception {
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
    } //end of class FileWalker

    /**
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    static public class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;
        private final boolean prompt;
        private final boolean preserve;

        TreeCopier(Path source, Path target, boolean prompt, boolean preserve) {
            this.source = source;
            this.target = target;
            this.prompt = prompt;
            this.preserve = preserve;
        }

        /**
         * Copy source file to target location. If {@code prompt} is true then
         * prompt user to overwrite target if it exists. The {@code preserve}
         * parameter determines if file attributes should be copied/preserved.
         */
        private static void copyFile(Path source, Path target, boolean prompt, boolean preserve) {
            CopyOption[] options = (preserve) ? new CopyOption[]{
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            try {
                Files.createDirectories(target.getParent());
            } catch (IOException e) {
                logger.error("Unable to create: " + target.getParent() + "", e);
            }
            try {
                Files.copy(source, target, options);
            } catch (IOException e) {
                logger.error("Unable to copy: " + source + "", e);
            }

        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                                                 BasicFileAttributes attrs) {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = (preserve) ? new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES}
                    : new CopyOption[0];

            Path newdir = target.resolve(source.relativize(dir));
            try {
                if (Files.notExists(target.getParent())) {
                    Files.createDirectories(target.getParent());
                }
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException e) {
                logger.error("Unable to create: " + newdir + "", e);
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            copyFile(file, target.resolve(source.relativize(file)), prompt, preserve);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // fix up modification time of directory when done
            if (exc == null && preserve) {
                Path newdir = target.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException e) {
                    logger.error("Unable to copy all attributes to: " + newdir + "", e);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) {
            if (e instanceof FileSystemLoopException) {
                logger.error("cycle detected: " + file);
            } else {
                logger.error("Unable to copy: " + file + "", e);
            }
            return FileVisitResult.CONTINUE;
        }
    }



}
