package com.github.p4535992.util.file;

import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.file.resources.ClassLoaderUtil;
import com.github.p4535992.util.stream.IOUtilities;

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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Class with many utilities method for manage the file object.
 *
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class FileUtilities {
    
     private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(FileUtilities.class);

    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = 1048576L;
    public static final long ONE_GB = 1073741824L;

    public static final String pathSeparatorReference = "/";

    public static char pathSeparator = File.separatorChar;
    private static String fullPath;
    //private static char extensionSeparator = '.';
    //private static String extensionSeparatorS = ".";
    //public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    private static FileUtilities instance = new FileUtilities();
    //------------------------------------------------------------------------------------------------------
    private static Map<String, String> unicodeCodePoint = new HashMap<>();

    protected FileUtilities() {}

    public static FileUtilities getInstance() {
        return instance;
    }

    /**
     * Method for get the extension from a file.
     *
     * @param f the {@link File} of input.
     * @return the {@link String} of the extension of the file.
     */
    public static String getExtension(File f) {
        return getExtension(f.getAbsolutePath());
    }

    /**
     * Method for get the extension from a file.
     *
     * @param f the {@link Path} of input.
     * @return the {@link String} of the extension of the file.
     */
    public static String getExtension(Path f) {
        return getExtension(f.toAbsolutePath().toString());
    }

    /**
     * Method for get the extension from a file.
     *
     * @param fullPath the the {@link String} of the path to the file
     * @return the {@link String} of the extension of the file
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
     * @param f the {@link File} of input
     * @return the {@link String} name of the file without the extension
     */
    public static String getFilenameWithoutExt(File f) {
        return getFilenameWithoutExt(f.getAbsolutePath());
    }

    /**
     * Method for get the filename without extension.
     *
     * @param f the {@link Path} of input
     * @return the {@link String} name of the file without the extension
     */
    public static String getFilenameWithoutExt(Path f) {
        return getFilenameWithoutExt(f.toAbsolutePath().toString());
    }

    /**
     * Method for get the filename without extension.
     *
     * @param fullPath the the {@link String} of the path to the file
     * @return the {@link String} name of the file without the extension
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
     * @param f the {@link File} of input
     * @return the {@link String} name of the file
     */
    public static String getFilename(File f) {
        return f.getName();
    }

    /**
     * Method for get the name of the file (with extensions).
     *
     * @param fullPath the {@link String} of the path to the file
     * @return the {@link String} name of the file
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
     * @param base         the {@link String} base of the absolute path where you want start the
     *                     relative path e.g. /var/data
     * @param absolutePath the {@link String} full path to the file e.g. /var/data/stuff/xyz.dat
     * @return the {@link String} relative path to the file e.g. stuff/xyz.dat
     */
    public static String getRelativePath(String base, String absolutePath) {
        return new File(base).toURI().relativize(new File(absolutePath).toURI()).getPath();
    }

    /**
     * Method or get the local path in the project.
     *
     * @param file the {@link File} object.
     * @return the {@link String} local path to the file in the project.
     */
    public static String getLocalPath(File file) {
        return getLocalPath("", file.getAbsolutePath());
    }

    /**
     * Method or get the local path in the project.
     *
     * @param file File object.
     * @return the local path to the file in the project.
     */
    public static String getLocalPath(Path file) {
        return getLocalPath("", file.toAbsolutePath().toString());
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
     * @param f file of input
     * @return the path to the file
     */
    public static String getPath(File f) {
        return getPath(f.getAbsolutePath());
    }

    /**
     * Method for get the path of a file.
     *
     * @param f file of input
     * @return the path to the file
     */
    public static String getPath(Path f) {
        return getPath(f.toAbsolutePath().toString());
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
    public static File toFile(String fullPath) {
        return toFile(new File(fullPath));
    }

    /**
     * Method to create a new File Object in a specific path.
     *
     * @param file File output location of the new File .
     * @return the new File object.
     */
    public static File toFile(File file) {
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
     * Method to create a new File Object in a specific path.
     *
     * @param file File output location of the new File .
     * @return the new File object.
     */
    public static File createFile(File file) {
        return toFile(file);
    }

    /**
     * Method to copy the content from a file to another in char format.
     *
     * @param fullPathInput  string path to the file you want to read the copy.
     * @param fullPathOutput string path to the file you want write the copy.
     * @return if true all the operation are done.
     */
    public static boolean copy(String fullPathInput, String fullPathOutput) {
        return copy(Paths.get(fullPathInput), Paths.get(fullPathOutput));
    }

   /* public static boolean copy(String fullPathInput, String fullPathOutput) {
        return copy(new File(fullPathInput), new File(fullPathOutput));
    }*/


    /*public static boolean copy(File destination, File source) {
        if (!destination.exists()) toFile(destination);
        try (OutputStream out = new FileOutputStream(destination);
             InputStream in = new FileInputStream(source)) {
            IOUtilities.copy(in, out, StandardCharsets.UTF_8);
            logger.info("Done copying contents of " + source.getName() + " to " + destination.getName());
            return true;
        } catch (IOException e) {
            logger.error("Copying file/folder: " + source + " to " + destination + ":"+e.getMessage(), e);
            return false;
        }
    }*/

    /**
    * Method to copy a file.
    *
     * @param source      the String source of the File to copy.
    * @param destination the String destination for the copy of the file.
    * @return if true all the operation are done.
    */
    public static boolean copy( Path source,Path destination) {
        if (!Files.exists(destination)) createNewFile(destination);
      /*  try (OutputStream out = Files.newOutputStream(destination);
             InputStream in = Files.newInputStream(source)) {
            IOUtilities.copy(in, out, StandardCharsets.UTF_8);*/
        try{
            Files.copy(source,destination,StandardCopyOption.ATOMIC_MOVE);
            logger.info("Done copying contents of " + source.getFileName().toString()+
                    " to " + destination.getFileName().toString());
            return true;
        } catch (IOException e) {
            logger.error("Copying file/folder: " + source + " to " + destination + ":"+e.getMessage(), e);
            return false;
        }
    }

    /*public static boolean copyWithTreeCopier(Path src, Path dest) {
        try {
            dest = Files.isDirectory(src) ? dest.resolve(src) : dest;
            EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            TreeCopier tc = new TreeCopier(src, dest, false, false);
            Files.walkFileTree(src, opts, Integer.MAX_VALUE, tc);
            logger.info("Done copying contents of " + src + " to " + dest);
            return true;
        } catch (IOException e) {
            logger.error("Copying file/folder: " + src + " to " + dest + ":"+e.getMessage(), e);
        }
        return false;
    }*/

   /* public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }*/

    /*public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
        if(destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if(destDir.exists() && !destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination \'" + destDir + "\' is not a directory");
        } else {
            copyFile(srcFile, new File(destDir, srcFile.getName()), preserveFileDate);
        }
    }*/

    public static void copyFileToDirectory(Path srcFile, Path destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }

    public static void copyFileToDirectory(Path srcFile, Path destDir, boolean preserveFileDate) throws IOException {
        if(destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if(Files.exists(destDir) && !Files.isDirectory(destDir)) {
            throw new IllegalArgumentException("Destination \'" + destDir + "\' is not a directory");
        } else {
            copyFile(srcFile, Paths.get(destDir.toString(), srcFile.getFileName().toString()), preserveFileDate);
        }
    }

    public static Boolean copy(Path path, OutputStream out){
        try {
            Files.copy(path,out);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean copy(InputStream in,Path path){
        try {
            Files.copy(in,path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /*public static void copy(File file, OutputStream out) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            IOUtilities.copy(new BufferedInputStream(in), out);
        } finally {
            IOUtilities.closeQuietly(in);
        }
    }*/

    /*public static void copy(InputStream in, File file) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            IOUtilities.copy(in, out);
        } finally {
            IOUtilities.closeQuietly(out);
        }
    }*/

    /*public static void copyFile(File srcFile, File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }*/

    /*public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if(srcFile == null)  throw new NullPointerException("Source must not be null");
        else if(destFile == null) throw new NullPointerException("Destination must not be null");
        else if(!srcFile.exists()) throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        else if(srcFile.isDirectory()) throw new IOException("Source \'" + srcFile + "\' exists but is a directory");
        else if(srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source \'" + srcFile + "\' and destination \'" + destFile + "\' are the same");
        } else if(destFile.getParentFile() != null && !destFile.getParentFile().exists() && !destFile.getParentFile().mkdirs()) {
            throw new IOException("Destination \'" + destFile + "\' directory cannot be created");
        } else if(destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination \'" + destFile + "\' exists but is read-only");
        } else doCopyFile(srcFile, destFile, preserveFileDate);
    }*/

    /*private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if(destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination \'" + destFile + "\' exists but is a directory");
        } else {
            InputStream input = new FileInputStream(srcFile);
            try {
                OutputStream output = new FileOutputStream(destFile);
                try {
                    IOUtilities.copy(input, output);
                } finally {
                    IOUtilities.closeQuietly(output);
                }
            } finally {
                IOUtilities.closeQuietly(input);
            }
            if(srcFile.length() != destFile.length()) {
                throw new IOException("Failed to copy full contents from \'" + srcFile + "\' to \'" + destFile + "\'");
            } else {
                if(preserveFileDate) {
                    if(!destFile.setLastModified(srcFile.lastModified())){
                        logger.warn("Can't set the last modified on the file.");
                    }
                }

            }
        }
    }*/

    public static void copyFile(Path srcFile, Path destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }

    public static void copyFile(Path srcFile, Path destFile, boolean preserveFileDate) throws IOException {
        if(srcFile == null)  throw new NullPointerException("Source must not be null");
        else if(destFile == null) throw new NullPointerException("Destination must not be null");
        else if(!Files.exists(srcFile)) throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        else if(Files.isDirectory(srcFile)) throw new IOException("Source \'" + srcFile + "\' exists but is a directory");
        else if(getCanonicalPath(srcFile).equals(getCanonicalPath(destFile))) {
            throw new IOException("Source \'" + srcFile + "\' and destination \'" + destFile + "\' are the same");
        } else if(getParentFile(destFile) != null && !getParentFile(destFile).exists() && !getParentFile(destFile).mkdirs()) {
            throw new IOException("Destination \'" + destFile + "\' directory cannot be created");
        } else if(Files.exists(destFile) && !canWrite(destFile)) {
            throw new IOException("Destination \'" + destFile + "\' exists but is read-only");
        } else doCopyFile(srcFile, destFile, preserveFileDate);
    }

    private static void doCopyFile(Path srcFile, Path destFile, boolean preserveFileDate) throws IOException {
        if(Files.exists(destFile) && Files.isDirectory(destFile)) {
            throw new IOException("Destination \'" + destFile + "\' exists but is a directory");
        } else {
            InputStream input = Files.newInputStream(srcFile);
            try {
                OutputStream output = Files.newOutputStream(destFile);
                try {
                    IOUtilities.copy(input, output);
                } finally {
                    IOUtilities.closeQuietly(output);
                }
            } finally {
                IOUtilities.closeQuietly(input);
            }
            if(Files.size(srcFile) != Files.size(destFile)) {
                throw new IOException("Failed to copy full contents from \'" + srcFile + "\' to \'" + destFile + "\'");
            } else {
                if(preserveFileDate) {
                    if(!setLastModified(destFile,lastModified(srcFile))){
                        logger.warn("Can't set the last modified on the file.");
                    }
                }

            }
        }
    }

    public static void copyDirectory(Path srcDir, Path destDir) throws IOException {
        copyDirectory(srcDir, destDir,null, true);
    }

    /*public static void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir.toPath(), destDir.toPath(),null, true);
    }*/

    /*public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }*/

    public static void copyDirectory(Path srcDir, Path destDir, boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    public static Boolean copyDirectory(Path srcDir,Path destDir,DirectoryStream.Filter<? super Path> filter,boolean preserveFileDate){
        try {
            if (srcDir == null) {
                throw new NullPointerException("Source must not be null");
            } else if (destDir == null) {
                throw new NullPointerException("Destination must not be null");
            } else if (!Files.exists(srcDir)) {
                throw new FileNotFoundException("Source \'" + srcDir + "\' does not exist");
            } else if (!Files.isDirectory(srcDir)) {
                throw new IOException("Source \'" + srcDir + "\' exists but is not a directory");
            } else if (srcDir.toString().equals(destDir.toString())) {
                throw new IOException("Source \'" + srcDir + "\' and destination \'" + destDir + "\' are the same");
            } else {
                ArrayList<String> exclusionList = null;
                if (destDir.toAbsolutePath().startsWith(srcDir.toAbsolutePath())) {
                    Path[] srcPaths = filter == null ? listFiles(srcDir) : listFiles(srcDir, filter);
                    //File[] srcFiles = filter == null?srcDir.listFiles():srcDir.listFiles(filter);
                    if (srcPaths.length > 0) {
                        exclusionList = new ArrayList<>(srcPaths.length);
                        for (Path srcFile : srcPaths) {
                            Path copiedFile = Paths.get(destDir.toString(), srcFile.getFileName().toString());
                            exclusionList.add(copiedFile.toAbsolutePath().toString());
                        }
                    }
                }
                doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
            }
            return true;
        }catch(IOException e){
            logger.error("Can't do the copy of the directory from "+srcDir.toString()+ " to "+destDir.toString()+ ":"+e.getMessage());
            return false;
        }
    }

    /*public static void copyDirectory(File  srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) throw new NullPointerException("Source must not be null");
        else if (destDir == null) throw new NullPointerException("Destination must not be null");
        else if (!srcDir.exists()) throw new FileNotFoundException("Source \'" + srcDir + "\' does not exist");
        else if (!srcDir.isDirectory()) throw new IOException("Source \'" + srcDir + "\' exists but is not a directory");
        else if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath()))
            throw new IOException("Source \'" + srcDir + "\' and destination \'" + destDir + "\' are the same");
        else {
            ArrayList<String> exclusionList = null;
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
                File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
                if (srcFiles != null && srcFiles.length > 0) {
                    exclusionList = new ArrayList<>(srcFiles.length);
                    for (File srcFile : srcFiles) {
                        File copiedFile = new File(destDir, srcFile.getName());
                        exclusionList.add(copiedFile.getCanonicalPath());
                    }
                }
            }
            doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
        }
    }*/

    /*private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate, List<String> exclusionList) throws IOException {
        if(destDir.exists()) {
            if(!destDir.isDirectory())  throw new IOException("Destination \'" + destDir + "\' exists but is not a directory");
        } else {
            if(!destDir.mkdirs()) throw new IOException("Destination \'" + destDir + "\' directory cannot be created");
            if(preserveFileDate) {
                if(!destDir.setLastModified(srcDir.lastModified())){
                    logger.warn("Can't set the last modified on the file.");
                }
            }
        }
        if(!destDir.canWrite()) throw new IOException("Destination \'" + destDir + "\' cannot be written to");
        else {
            File[] files = filter == null?srcDir.listFiles():srcDir.listFiles(filter);
            if(files == null) throw new IOException("Failed to list contents of " + srcDir);
            else {
                for (File file : files) {
                    File copiedFile = new File(destDir, file.getName());
                    if (exclusionList == null || !exclusionList.contains(file.getCanonicalPath())) {
                        if (file.isDirectory()) {
                            doCopyDirectory(file, copiedFile, filter, preserveFileDate, exclusionList);
                        } else {
                            doCopyFile(file, copiedFile, preserveFileDate);
                        }
                    }
                }

            }
        }
    }*/

    private static void doCopyDirectory(Path srcDir, Path destDir,DirectoryStream.Filter<? super Path>  filter, boolean preserveFileDate, List<String> exclusionList) throws IOException {
        if(Files.exists(destDir)) {
            if(!Files.isDirectory(destDir))  throw new IOException("Destination \'" + destDir + "\' exists but is not a directory");
        } else {
            if(!mkdirs(destDir)) throw new IOException("Destination \'" + destDir + "\' directory cannot be created");
            if(preserveFileDate) {
                if(!setLastModified(destDir,(lastModified(srcDir)))){
                    logger.warn("Can't set the last modified on the file.");
                }
            }
        }
        if(!canWrite(destDir)) throw new IOException("Destination \'" + destDir + "\' cannot be written to");
        else {
            Path[] paths = filter == null?listFiles(srcDir):listFiles(srcDir,filter);
            if(paths.length == 0) throw new IOException("Failed to list contents of " + srcDir);
            else {
                for (Path path : paths) {
                    Path copiedFile = Paths.get(destDir.toString(), path.getFileName().toString());
                    if (exclusionList == null || !exclusionList.contains(getCanonicalPath(path))) {
                        if (Files.isDirectory(path)){
                            doCopyDirectory(path, copiedFile, filter, preserveFileDate, exclusionList);
                        } else {
                            doCopyFile(path, copiedFile, preserveFileDate);
                        }
                    }
                }

            }
        }
    }

    /**
     * Method to check the cotnent fo two files.
     * @param file1 the {@link Path} source.
     * @param file2 the {@link Path} target.
     * @return the {@link Boolean} if true the content of the two files is the same.
     */
    public static Boolean contentEquals(File file1, File file2){
        return contentEquals(file1.toPath(),file2.toPath());
    }

    public static Boolean contentEquals(Path file1,Path file2){
        try {
            boolean file1Exists = Files.exists(file1);
            if (file1Exists != Files.exists(file2)) return false;
            else if (!file1Exists) return true;
            else if (!Files.isDirectory(file1) && !Files.isDirectory(file2)) {
                if (Files.size(file1) != Files.size(file2)) return false;
                else if (getCanonicalFile(file1).equals(getCanonicalFile(file1))) return true;
                else {
                    InputStream input1 = null;
                    InputStream input2 = null;
                    boolean var5;
                    try {
                        input1 = Files.newInputStream(file1);
                        input2 = Files.newInputStream(file2);
                        var5 = IOUtilities.contentEquals(input1, input2);
                    } finally {
                        IOUtilities.closeQuietly(input1);
                        IOUtilities.closeQuietly(input2);
                    }
                    return var5;
                }
            } else {
                throw new IOException("Can\'t compare directories, only files");
            }
        }catch(IOException e){
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to create a directory.
     *
     * @param fullPathDir string path to the location of the directory.
     * @return if true you have created the directory.
     */
    public static boolean createDirectory(String fullPathDir) {
        //return new File(fullPathDir).mkdirs();
        try {
            Files.createDirectory(Paths.get(fullPathDir));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean deleteDirectory(Path directory){
        try {
            if (Files.exists(directory)) {
                cleanDirectory(directory);
                if (!Files.deleteIfExists(directory)) {
                    throw new IOException("Unable to delete directory " + directory + ".");
                }
            }
            return true;
        }catch(IOException e){
            logger.error(e.getMessage());
            return false;
        }
    }

    /*public static void deleteDirectory(File directory) throws IOException {
        if(directory.exists()) {
            cleanDirectory(directory);
            if(!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }*/

    /*public static boolean deleteQuietly(File file) {
        if(file == null) return false;
        else {
            try {
                if(file.isDirectory()) cleanDirectory(file);
            } catch (Exception ignored) {}
            try {
                return file.delete();
            } catch (Exception var2) {
                return false;
            }
        }
    }*/

    public static Boolean deleteQuietly(Path file) {
        if(file == null) return false;
        else {
            try {
                if(Files.isDirectory(file)) cleanDirectory(file);
            } catch (Exception ignored) {}
            try {
                return delete(file);
            } catch (Exception var2) {
                return false;
            }
        }
    }

    /*public static void cleanDirectory(File directory) throws IOException {
        String var7;
        if(!directory.exists()) {
            var7 = directory + " does not exist";
            throw new IllegalArgumentException(var7);
        } else if(!directory.isDirectory()) {
            var7 = directory + " is not a directory";
            throw new IllegalArgumentException(var7);
        } else {
            File[] files = directory.listFiles();
            if(files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                for (File file : files) {
                    try {
                        forceDelete(file);
                    } catch (IOException var6) {
                        exception = var6;
                    }
                }
                if(null != exception) throw exception;
            }
        }
    }*/

    public static Boolean cleanDirectory(Path directory){
        String var7;
        try {
            if (!Files.exists(directory)) {
                var7 = directory + " does not exist";
                throw new IllegalArgumentException(var7);
            } else if (!Files.isDirectory(directory)) {
                var7 = directory + " is not a directory";
                throw new IllegalArgumentException(var7);
            } else {
                Path[] files = listFiles(directory);
                if (files.length > 0) {
                    throw new IOException("Failed to list contents of " + directory);
                } else {
                    IOException exception = null;
                    for (Path file : files) {
                        try {
                            forceDelete(file);
                        } catch (IOException var6) {
                            exception = var6;
                        }
                    }
                    if (null != exception) throw exception;
                }
            }
            return true;
        }catch(IOException e){
            logger.error("Can't clean the directory in "+directory.toString()+":"+e.getMessage());
            return false;
        }
    }

    /*public static void forceDelete(File file) throws IOException {
        if(file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if(!file.delete()) {
                if(!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }

    }*/

    public static void forceDelete(Path file) throws IOException {
        if(Files.isDirectory(file)) {
            deleteDirectory(file);
        } else {
            boolean filePresent = Files.exists(file);
            if(!delete(file)) {
                if(!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

   /* public static void forceDeleteOnExit(File file) throws IOException {
        if(file.isDirectory()) deleteDirectoryOnExit(file);
        else file.deleteOnExit();
    }*/

    /*private static void deleteDirectoryOnExit(File directory) throws IOException {
        if(directory.exists()) {
            cleanDirectoryOnExit(directory);
            directory.deleteOnExit();
        }
    }*/

    public static void forceDeleteOnExit(Path path) throws IOException {
        if(Files.isDirectory(path)) deleteDirectoryOnExit(path);
        else path.toFile().deleteOnExit();
    }

    private static void deleteDirectoryOnExit(Path directory) throws IOException {
        if(Files.exists(directory)) {
            cleanDirectoryOnExit(directory);
            directory.toFile().deleteOnExit();
        }
    }

/*    private static void cleanDirectoryOnExit(File directory) throws IOException {
        String var7;
        if(!directory.exists()) {
            var7 = directory + " does not exist";
            throw new IllegalArgumentException(var7);
        } else if(!directory.isDirectory()) {
            var7 = directory + " is not a directory";
            throw new IllegalArgumentException(var7);
        } else {
            File[] files = directory.listFiles();
            if(files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                for (File file : files) {
                    try {
                        forceDeleteOnExit(file);
                    } catch (IOException var6) {
                        exception = var6;
                    }
                }
                if(null != exception) throw exception;
            }
        }
    }*/

    private static void cleanDirectoryOnExit(Path directory) throws IOException {
        String var7;
        if(!Files.exists(directory)){
            var7 = directory + " does not exist";
            throw new IllegalArgumentException(var7);
        }else if(!Files.isDirectory(directory)) {
            var7 = directory + " is not a directory";
            throw new IllegalArgumentException(var7);
        } else {
            Path[] paths = listFiles(directory);
            if(paths.length > 0) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                for (Path path : paths) {
                    try {
                        forceDeleteOnExit(path);
                    } catch (IOException var6) {
                        exception = var6;
                    }
                }
                if(null != exception) throw exception;
            }
        }
    }

    public static void forceMkdir(File directory) throws IOException {
        String message;
        if(directory.exists()) {
            if(directory.isFile()) {
                message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else if(!directory.mkdirs()) {
            message = "Unable to create directory " + directory;
            throw new IOException(message);
        }
    }

    public static long sizeOfDirectory(File directory) {
        String var6;
        if(!directory.exists()) {
            var6 = directory + " does not exist";
            throw new IllegalArgumentException(var6);
        } else if(!directory.isDirectory()) {
            var6 = directory + " is not a directory";
            throw new IllegalArgumentException(var6);
        } else {
            long size = 0L;
            File[] files = directory.listFiles();
            if(files == null)  return 0L;
            else {
                for (File file : files) {
                    if (file.isDirectory()) size += sizeOfDirectory(file);
                    else  size += file.length();
                }
                return size;
            }
        }
    }

    /*public static void moveDirectory(File srcDir, File destDir) throws IOException {
        if(srcDir == null) {
            throw new NullPointerException("Source must not be null");
        } else if(destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if(!srcDir.exists()) {
            throw new FileNotFoundException("Source \'" + srcDir + "\' does not exist");
        } else if(!srcDir.isDirectory()) {
            throw new IOException("Source \'" + srcDir + "\' is not a directory");
        } else if(destDir.exists()) {
            throw new IOException("Destination \'" + destDir + "\' already exists");
        } else {
            boolean rename = srcDir.renameTo(destDir);
            if(!rename) {
                copyDirectory(srcDir, destDir);
                deleteDirectory(srcDir);
                if(srcDir.exists()) {
                    throw new IOException("Failed to delete original directory \'" + srcDir + "\' after copy to \'" + destDir + "\'");
                }
            }

        }
    }*/

    /*public static void moveFile(File srcFile, File destFile) throws IOException {
        if(srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if(destFile == null) {
            throw new NullPointerException("Destination must not be null");
        } else if(!srcFile.exists()) {
            throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        } else if(srcFile.isDirectory()) {
            throw new IOException("Source \'" + srcFile + "\' is a directory");
        } else if(destFile.exists()) {
            throw new IOException("Destination \'" + destFile + "\' already exists");
        } else if(destFile.isDirectory()) {
            throw new IOException("Destination \'" + destFile + "\' is a directory");
        } else {
            boolean rename = srcFile.renameTo(destFile);
            if(!rename) {
                copyFile(srcFile, destFile);
                if(!srcFile.delete()) {
                    deleteQuietly(destFile);
                    throw new IOException("Failed to delete original file \'" + srcFile + "\' after copy to \'" + destFile + "\'");
                }
            }

        }
    }*/

    public static void moveFile(Path srcFile, Path destFile) throws IOException {
        if(srcFile == null) throw new NullPointerException("Source must not be null");
        else if(destFile == null) throw new NullPointerException("Destination must not be null");
        else if(!Files.exists(srcFile)) throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        else if(Files.isDirectory(srcFile)) throw new IOException("Source \'" + srcFile + "\' is a directory");
        else if(Files.exists(destFile)) throw new IOException("Destination \'" + destFile + "\' already exists");
        else if(Files.isDirectory(destFile)) throw new IOException("Destination \'" + destFile + "\' is a directory");
        else {
            boolean rename = renameTo(srcFile,destFile);
            if(!rename) {
                copyFile(srcFile, destFile);
                if(!delete(srcFile)) {
                    deleteQuietly(destFile);
                    throw new IOException("Failed to delete original file \'" + srcFile + "\' after copy to \'" + destFile + "\'");
                }
            }

        }
    }

    /**
     * Recursively traverse a directory hierachy and obtain a list of all
     * absolute file names.
     * Regular expression patterns can be provided to explicitly include
     * and exclude certain file names.
     *
     * @param   file the {@link File} directory whose file hierarchy will be traversed
     * @param included the {@link Pattern} an array of regular expression patterns that will be
     *                 used to determine which files should be included; or
     *                 <p><code>null</code> if all files should be included
     * @param excluded the {@link Pattern} an array of regular expression patterns that will be
     *                 used to determine which files should be excluded; or
     *                 <p><code>null</code> if no files should be excluded
     * @return the {@link List} of {@link String} of absolute file names
     */
    public static List<String> getFilesList(File file, Pattern[] included, Pattern[] excluded) {
        return getFilesList(file, included, excluded, true);
    }

    private static List<String> getFilesList(File file, Pattern[] included, Pattern[] excluded, boolean root) {
        if (null == file) return new ArrayList<>();
        ArrayList<String> filelist = new ArrayList<>();
        if (file.isDirectory()) {
            String[] list = file.list();
            if (null != list) {
                String list_entry;
                for (String aList : list) {
                    list_entry = aList;
                    File next_file = new File(file.getAbsolutePath() + File.separator + list_entry);
                    List<String> dir = getFilesList(next_file, included, excluded, false);
                    Iterator<String> dir_it = dir.iterator();
                    String file_name;
                    while (dir_it.hasNext()) {
                        file_name = dir_it.next();
                        if (root) {
                            // if the file is not accepted, don't process it further
                            if (!isMatch(file_name, included, excluded)) {
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
                if (isMatch(file_name, included, excluded)) {
                    filelist.add(file_name);
                }
            } else filelist.add(file_name);
        }
        return filelist;
    }

    /**
     * Recursively traverse a directory hierachy and obtain a list of all
     * absolute file names.
     * Regular expression patterns can be provided to explicitly include
     * and exclude certain file names.
     *
     * @param  directory  the {@link Path} directory whose file hierarchy will be traversed
     * @param included the {@link Pattern} an array of regular expression patterns that will be
     *                 used to determine which files should be included; or
     *                 <p><code>null</code> if all files should be included
     * @param excluded the {@link Pattern} an array of regular expression patterns that will be
     *                 used to determine which files should be excluded; or
     *                 <p><code>null</code> if no files should be excluded
     * @return the {@link List} of {@link String} of absolute file names
     */
    public static List<File> getFilesFromDirectory(Path directory,Pattern[] included, Pattern[] excluded) {
        List<File> files = getFilesFromDirectory(directory);
        List<String> filesName = getFilesList(directory.toFile(),included,excluded);
        List<File> filterFiles = new ArrayList<>();
        for(File file: files){
            if(filesName.contains(file.getName())){
                filterFiles.add(file);
            }
        }
        return filterFiles;
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory file of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(File directory) {
        return toFiles(getPathsFromDirectory(directory.toPath()));
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory file of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(Path directory) {
        return toFiles(getPathsFromDirectory(directory));
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param fullPathDir string path to the location of the directory/folder.
     * @return list of files in the directory.
     */
    public static List<File> getFilesFromDirectory(String fullPathDir) {
       return toFiles(getPathsFromDirectory(fullPathDir));
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
        return toFiles(getPathsFromDirectory(Paths.get(fullPathDir),offset,limit));
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param fullPathDir string path to the location of the directory/folder.
     * @param offset the {@link Integer} offset iundex of the Files.
     * @param limit the {@link Integer} limit iundex of the Files.
     * @return list of files in the directory.
     */
    public static List<Path> getPathsFromDirectory(Path fullPathDir,Integer offset,Integer limit) {
       //return toPaths(getFilesFromDirectory(fullPathDir,offset,limit));
        String[] names;
        List<Path> paths = new ArrayList<>();
        try {
            names = list(fullPathDir);
            if (offset != null && limit != null &&
                    offset + limit > names.length) limit = names.length;
            for (int i =0; i < names.length; i++) {
                if(offset != null){
                    if(i >= offset )paths.add(Paths.get(fullPathDir.toAbsolutePath().toString(),names[i]));
                    else continue;
                }else{
                    paths.add(Paths.get(fullPathDir.toAbsolutePath().toString(),names[i]));
                }
                if(limit != null){
                    if(i >= limit-1)break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return paths;
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory the {@link File} directory/folder.
     * @return the  {@link List} of {@link File} in the directory.
     */
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

    public static List<Path> getPathsFromDirectory(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return getPathsFromDirectory(dir, filter, null);
    }

    public static List<Path> getPathsFromDirectory(Path dir, String glob) throws IOException {
        //e.g. glob "*.{txt,doc,pdf,ppt}"
        return getPathsFromDirectory(dir, null, glob);
    }

    public static List<Path> getPathsFromDirectory(Path directory, DirectoryStream.Filter<? super Path> filter, String glob) {
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
        //int ONE_KB = 1024,ONE_MB = ONE_KB * ONE_KB,ONE_GB = ONE_MB * ONE_KB;
        if ( size / ONE_GB > 0 ) displaySize = String.valueOf( size / ONE_GB ) + " GB";
        else if ( size / ONE_MB > 0 ) displaySize = String.valueOf( size / ONE_MB ) + " MB";
        else if ( size / ONE_KB > 0 )displaySize = String.valueOf( size / ONE_KB ) + " KB";
        else displaySize = String.valueOf( size ) + " bytes";
        return displaySize;
    }

    /**
     * Removes all files from a given folder.
     * href: http://www.adam-bien.com/roller/abien/entry/java_7_deleting_recursively_a.
     *
     * @param filePathToTheFile string of the path to the file
     * @return if true all the operation are done.
     */
    public static boolean deleteFilesOnDirectory(String filePathToTheFile) {
        //File filePath = new File(filePathToTheFile);
        Path filePath = Paths.get(filePathToTheFile);
        if (Files.exists(filePath)) {
            if (Files.isDirectory(filePath)){
                try {
                    Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
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
        /*if (Files.exists(filePath)) {
            try {
                for (String fileInDirectory : list(filePath)) {
                    Path tmpFile = Files.createTempFile(filePath,fileInDirectory,null);
                    if (!Files.deleteIfExists(tmpFile)) {
                        logger.warn("Can't delete the file:" + tmpFile.toAbsolutePath());
                        return false;
                    }
                }
                if (!Files.deleteIfExists(filePath)) {
                    logger.warn("Can't delete the file:" + filePath.toAbsolutePath());
                    return false;
                }
            }catch(IOException e){
                logger.error(e.getMessage(), e);
                return false;
            }
        }*/
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
     */
    public static URL toURL(File file){
        try {
            return toUri(file).toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Method to convert a File to a URL
     *
     * @param filePath the String to the File to convert.
     * @return the URL.
     */
    public static URL toURL(String filePath){
        return toURL(new File(filePath));
    }

    /**
     * Method to convert a URI to a File.
     *
     * @param uri the URI to convert.
     * @return the File.
     */
    public static File toFile(URI uri){
        try {
            File file = new File(uri.toURL().getFile());
            if(file.exists()) return file;
            else return null;
        }catch(MalformedURLException e){
            return null;
        }
    }

    /**
     * Method to convert a URI to a File.
     *
     * @param url the URL to convert.
     * @return the File.
     */
    public static File toFile(URL url){
        return toFile(url,false);
    }

    /**
     * Method to convert a URI to a File.
     *
     * @param url the URL to convert.
     * @param getLikeURIResource the {@link Boolean} if true the URL is a reference to a URI resource.
     * @return the File.
     */
    public static File toFile(URL url,boolean getLikeURIResource){
        File file;
        try {
            if (getLikeURIResource) return toFile(url.toURI());
            if (url == null) return null;
            file = toFile(url.toURI());
        }catch(URISyntaxException e){
            return null;
        }
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
     * Method to convert a URI to Stream.
     *
     * @param uri the URI to convert.
     * @return the Stream.
     * @throws IOException throw if any error is occurred.
     */
    public static InputStream toStream(URI uri) throws IOException {
        return uri.toURL().openStream();
    }

    public static FileInputStream toStreamInput(File file) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException("File \'" + file + "\' exists but is a directory");
            } else if(!file.canRead()) {
                throw new IOException("File \'" + file + "\' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File \'" + file + "\' does not exist");
        }
    }

    public static FileOutputStream toStreamOutput(File file) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException("File \'" + file + "\' exists but is a directory");
            }
            if(!file.canWrite()) {
                throw new IOException("File \'" + file + "\' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if(parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("File \'" + file + "\' could not be created");
            }
        }
        return new FileOutputStream(file);
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

    /*
     * Method for convert a reference path to a resource in the classpath to a file with path in the system.
     *
     * @param referenceResourcePath string of the reference path to the resource.
     * @param thisClass             thi class.
     * @return file correspondent to the reference file of the resources.
     */
    /*public static File toResourceFile(String referenceResourcePath, Class<?> thisClass) {
        try {
            //noinspection ConstantConditions
            return new File(thisClass.getClassLoader().getResource(referenceResourcePath).getFile());
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }*/



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
     * Method for get in more dinamic way the current directory of the project
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
    public static File toTempFile(String content, String fullPath) {
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

    public static File toTempFile() {
        File temp = null;
        try {
            temp = File.createTempFile(
                    generateRandomStringSimple(6), //test
                    null, //.tmp
                    null //c://
            );
            // Delete temp file when program exits.
            temp.deleteOnExit();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return temp;
    }

    public static File toTempFile(File file) {
        File parent = file.getParentFile();
        String name = file.getName();
        int index = 0;
        File result;
        do {
            result = new File(parent, name + "_" + index++);
        } while(result.exists());
        return result;
    }

    public static File toTempFile(byte[] bytes){
        File tempFile = null;
        try {
            //file with no extenstion and no directory is saved on C:\\temp\\file.tmp
            tempFile = File.createTempFile(generateRandomStringSimple(6), null, null);
            // Delete temp file when program exits.
            tempFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);
            return  tempFile;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return tempFile;
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
    public static Path toPath(InputStream inStream, String filePathOutput) {
        /*try (OutputStream outputStream = new FileOutputStream(new File(filePathOutput))) {
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
        }*/
        Path path = Paths.get(filePathOutput);
        try{
            Files.copy(inStream,path);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return path;
        }
        if (Files.exists(path)) return path;
        else {
            logger.warn("The file:" + filePathOutput + " not exists.");
            return path;
        }
    }

    public static File toFile(InputStream inStream, String filePathOutput) {
        return toPath(inStream,filePathOutput).toFile();
    }

    /**
     * Method to convert a Stream to a File.
     *
     * @param inStream       the InputStream to decode.
     * @param filePathOutput the String path the the new location of the file.
     * @return the File Object.
     */
    public static File toFile(InputStream inStream,File filePathOutput) {
        return toPath(inStream,filePathOutput.toPath()).toFile();
    }

    /**
     * Method to convert a Stream to a File.
     *
     * @param inStream       the InputStream to decode.
     * @param filePathOutput the String path the the new location of the file.
     * @return the File Object.
     */
    public static Path toPath(InputStream inStream,Path filePathOutput) {
       /* try (OutputStream outputStream = new FileOutputStream(new File(filePathOutput))) {
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
        }*/
        try {
            Files.copy(inStream,filePathOutput);
            return filePathOutput;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return filePathOutput;
        }
    }

    /**
     * Method to convert a Stream to a File.
     *
     * @param inputStream      the {@link OutputStream} to decode.
     * @return the File Object.
     */
    public static File toFile(InputStream inputStream) {
        return toPath(inputStream,Paths.get("/temp")).toFile();
    }

    /*
     * Add a package name prefix if the name is not absolute Remove leading "/"
     * if name is absolute.
     *
     * @param name string name of the class
     * @return the full name package+class
     */
    /*private static String resolveName(String name) {
        if (isNullOrEmpty(name)) return name;
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
    }*/

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
    private static String hashFile(Path file, String algorithm) {
        try (InputStream inputStream = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytesBuffer = new byte[1024];
            int bytesRead;//= -1
            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }
            byte[] hashedBytes = digest.digest();
            return toHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error(e.getMessage(), e);
            return toString(file);
        }
    }

    /**
     * Method to convert a File to a MD5 hash string.
     *
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static File toMD5(File file) {
        return toMD5(file.toPath()).toFile();
    }
    public static Path toMD5(Path file) {
        try {
            Files.write(file,hashFile(file, "MD5").getBytes(),StandardOpenOption.WRITE);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return file;
    }

    /**
     * Method to convert a File to a SHA-1 hash string.
     *
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static File toSHA1(File file) {
        return toSHA1(file.toPath()).toFile();
    }

    public static Path toSHA1(Path file) {
        try {
            Files.write(file,hashFile(file, "SHA-1").getBytes(),StandardOpenOption.WRITE);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return file;
    }

    /**
     * Method to convert a File to a SHA-256 hash string.
     *
     * @param file the input File to codify to hash.
     * @return the string of the hash.
     */
    public static File toSHA256(File file) {
        return toSHA256(file.toPath()).toFile();
    }

    public static Path toSHA256(Path file) {
        try {
            Files.write(file,hashFile(file, "SHA-256").getBytes(),StandardOpenOption.WRITE);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return file;
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
     * Check the file separator to see if we're on a Windows platform.
     *
     * @return boolean True if the platform is Windows, false otherwise.
     */
    private static boolean platformIsWindows() {
        return File.separatorChar == '\\';
    }



    /**
     * Locate the specific file.
     * Return the (URL decoded) abolute pathname to the file or null.
     *
     * @param filenameToFind the String name of file to search.
     * @param basePath the String base of the path to the File.
     * @return the String path to the file.
     */
    public static String locateFile(String filenameToFind, String basePath) {
        URL url;
        String fullPathName;
        StringBuffer decodedPathName;
        int pos, len, start;
        try {
            if (filenameToFind == null) throw new FileNotFoundException("...null file name");
            logger.info("Try to locate the File:"+filenameToFind+"...");
            if (filenameToFind.startsWith(basePath)) return filenameToFind.substring(basePath.length());
            if ((fullPathName = locateByProperty(filenameToFind)) != null) return fullPathName;
            if ((url = locateByResource(filenameToFind)) != null) {
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
            throw new FileNotFoundException("...file not found: " + filenameToFind);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Locate the specific file.
     * Return the file name in URL form or null.
     *
     * @param filenameToFind the String name of file to search.
     * @param basePath the string prefix of the findFile e.g. "abs://"
     * @return the String path to the file.
     */
    public static URL locateURL(String filenameToFind, String basePath) {
        URL url;
        String fullPathName;
        try {
            if (filenameToFind == null) throw new FileNotFoundException("locateURL: null file name");
            logger.info("Try to locate the File:"+filenameToFind+"...");
            try {
                if (filenameToFind.startsWith(basePath)) {
                    return (new URL("file:/" + filenameToFind.substring(basePath.length())));
                }
                if ((fullPathName = locateByProperty(filenameToFind)) != null) {
                    if (platformIsWindows()) url = new URL("file:/" + fullPathName);
                    else url = new URL("file:" + fullPathName);
                    return url;
                }
            } catch (MalformedURLException e) {
                //logger.error("...URL creation problem:" + e.getMessage(), e);
                throw new FileNotFoundException("...URL creation problem");
            }
            if ((url = locateByResource(filenameToFind)) != null) return url;
            throw new FileNotFoundException("...file not found: " + filenameToFind);
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
            logger.warn("Searching in 'user.dir' for: " + findFile+"...");
            dir = System.getProperty("user.dir");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                logger.warn("...found in 'user.dir':" + fullPathName);
                return fullPathName;
            }
            dir = System.getProperty("user.home");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                logger.warn("...found in 'user.home':" + fullPathName);
                return fullPathName;
            }
            dir = System.getProperty("java.home");
            if (dir != null) {
                fullPathName = dir + File.separatorChar + findFile;
                f = new File(fullPathName);
            }
            if (f != null && f.exists()) {
                logger.warn("...found in 'java.home':" + fullPathName);
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
        logger.warn("...search succeeded via getResource()");
        return url;
    }

    /**
     * Method to locate a File on the Resource folder.
     * OLD_NAME : findFile
     * @param relativePath the {@link String} relative path of the File on the Resources folder.
     * @return the {@link File} if founded else NULL.
     */
    public static File locateFileByResourceRelativePath(String relativePath) {
        return locateFileByResourceRelativePath(relativePath,null);
    }

    /**
     * Method to locate a File on the Resource folder.
     * OLD_NAME : findFile
     * @param relativePath the {@link String} relative path of the File on the Resources folder.
     * @param basePathResources the {@link String} base path of the resource folder.
     * @return the {@link File} if founded else NULL.
     */
    public static File locateFileByResourceRelativePath(String relativePath,String basePathResources) {
        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.ClassPathResource(relativePath);
        if(basePathResources == null){
            basePathResources = "src/main/resources/";
        }
        File file;
        logger.info("Try to locate the File:"+basePathResources+relativePath+"...");
        try {
            file = resource.getFile();
        } catch (IOException e) {
            file = new File(relativePath);
            if (!file.exists()) {
                //we are not including the resources into the jars
                //this is needed to find the resources when executing from the IDE & test cases.
                file = new File(basePathResources+relativePath);
            }
        }
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("...unable to find file specified by path: " + relativePath);
        }
        return file;
    }

    /**
     * Method to get the String array of the columns of a CSV File.
     *
     * @param fileCSV      the File CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] CSVGetHeaders(File fileCSV, boolean hasFirstLine) {
       return OpenCsvUtilities.getHeadersWithUnivocity(fileCSV,hasFirstLine);
    }

    /**
     * Method to get the content of a comma separated file (.csv,.input,.txt)
     *
     * @param CSV    the File comma separated.
     * @param noHeaders if true jump the first line of the content.
     * @return the List of Array of the content of the File comma separated.
     */
    public static List<String[]> CSVGetContent(File CSV, boolean noHeaders) {
        return OpenCsvUtilities.parseCSVFileAsListWithUnivocity(CSV,noHeaders);
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
        return toPath(bytes,Paths.get(pathToTheFile));
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

    ////////////////////////////////////////////////////////////////////////////////////////////

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
        if (!fileInput.exists()) toFile(fileInput);
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
        if (!fileOutput.exists()) toFile(fileOutput);
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
        return read(UTF8,StandardCharsets.US_ASCII);
    }

    /**
     * Reads file with unicode escaped characters and write them out to
     * stdout in UTF-8.
     * This utility is equivalent to: native2ascii -reverse -encoding utf-8.
     *
     * @param ASCII file of input in ASCII encoding.
     * @return UTF8 file of input in UTF8 encoding.
     */
    public static List<String> toUTF8(Path ASCII) {
        return read(ASCII,StandardCharsets.UTF_8);
    }

    public static List<String> toUTF8(File ASCII) {
        return toUTF8(ASCII.toPath());
    }

    /**
     * Method to rewrite a file in the UTF-8 encoding
     *
     * @param fileASCII file of input in ASCII encoding
     * @return the File converted to UTF8 encoding.
     */
    public static File writeToUTF8(File fileASCII) {
      /*  List<String> list = toUTF8(fileASCII);
        Path fileUTF8 = Paths.get(fileASCII.getAbsolutePath());
        write(list, fileUTF8, StandardCharsets.US_ASCII, StandardCharsets.UTF_8);
        if(!fileASCII.delete()) {
            logger.warn("Can't delete the file:"+fileASCII.getAbsolutePath());
            return null;
        }
        return fileUTF8.toFile();*/
        return writeToUTF8(fileASCII.toPath());
    }

    public static File writeToUTF8(Path fileASCII) {
        List<String> list = toUTF8(fileASCII);
        Path fileUTF8 = Paths.get(fileASCII.toAbsolutePath().toString());
        write(list, fileUTF8, StandardCharsets.US_ASCII, StandardCharsets.UTF_8);
        try {
            if(!Files.deleteIfExists(fileASCII)) {
                logger.warn("Can't delete the file:"+fileASCII.toAbsolutePath().toString());
                return null;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        return fileUTF8.toFile();
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     *
     * @param fileUTF8 file of input in UTF8 encoding
     * @return the File converted with ASCII.
     */
    public static File writeToASCII(File fileUTF8) {
        List<String> list = toAscii(fileUTF8);
        //File fileAscii = new File(fileUTF8.getAbsolutePath());
        Path fileAscii = Paths.get(fileUTF8.getAbsolutePath());
        write(list, fileAscii, null, StandardCharsets.US_ASCII);
        if(!fileUTF8.delete()) {
            logger.warn("Can't delete the file:"+fileUTF8.getAbsolutePath());
            return null;
        }
        return fileAscii.toFile();
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
        write(fileUTF8, fileANSI, StandardCharsets.UTF_8,  Charset.forName("Cp1252"));//CP1252
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
        write(Collections.singletonList(str), fileOutput.toPath(), StandardCharsets.UTF_8, StandardCharsets.UTF_8);
        return fileOutput;
    }

    public static File write(String str, File fileOutput) {
        write(Collections.singletonList(str), fileOutput.toPath(), StandardCharsets.UTF_8, StandardCharsets.UTF_8);
        return fileOutput;
    }

    public static boolean write(Collection<String> collectionContent, File fileOutput) {
        return write(collectionContent, fileOutput.toPath(), null, null);
    }

    public static boolean write(Collection<String> collectionContent, File fileOutput, Charset encodingOutput) {
        return write(collectionContent, fileOutput.toPath(), null, encodingOutput);
    }

/*    public static boolean write(Collection<String> collectionContent, File fileOutput, Charset encodingInput, Charset encodingOutput) {
        boolean replace = false;
        if (encodingInput != null) {
            Collection<String> newCol = new ArrayList<>();
            //Brute force encoding conversion.....
            for (String s : collectionContent) {
                if (encodingInput.name().equals(StandardCharsets.US_ASCII.name())) s = toASCII(s);
                if (encodingInput.name().equals(StandardCharsets.UTF_8.name())) s = toUTF8(s);
                newCol.add(s);
            }
            if (encodingInput.name().equals(StandardCharsets.UTF_8.name())) replace = true;
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
                *//*try (PrintWriter outWriter = new PrintWriter(writer)) {
                    outWriter.println(line);
                }*//*
                writer.write(line + System.getProperty("line.separator"));
                //writer.newLine();
                writer.flush();
            }
            return true;
        } catch (java.lang.NullPointerException | IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }*/

    public static boolean write(Collection<String> collectionContent, Path fileOutput, Charset encodingInput, Charset encodingOutput) {
        boolean replace = false;
        if (encodingInput != null) {
            Collection<String> newCol = new ArrayList<>();
            //Brute force encoding conversion.....
            for (String s : collectionContent) {
                if (encodingInput.name().equals(StandardCharsets.US_ASCII.name())) s = toASCII(s);
                if (encodingInput.name().equals(StandardCharsets.UTF_8.name())) s = toUTF8(s);
                newCol.add(s);
            }
            if (encodingInput.name().equals(StandardCharsets.UTF_8.name())) replace = true;
            collectionContent = new ArrayList<>();
            collectionContent.addAll(newCol);
            newCol.clear();
        }
        if (encodingOutput == null) encodingOutput = StandardCharsets.UTF_8;
        if (encodingOutput.name().toUpperCase().startsWith("UTF")) replace = true;
        logger.info("Try to writing to file named " + fileOutput.toAbsolutePath().toString() + " with Encoding: " + encodingOutput.name());
        Path path = Paths.get(fileOutput.toAbsolutePath().toString());
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

    public static boolean write(Collection<String> collectionContent, File fileOutput, Charset encodingInput, Charset encodingOutput) {
        return write(collectionContent, fileOutput.toPath(), encodingInput, encodingOutput);
    }

    public static boolean write(File fileInput, File fileOutput, Charset encodingInput, Charset encodingOutput) {
        return write(read(fileInput, encodingInput), fileOutput, encodingInput, encodingOutput);
    }

    public static List<String> read(File fileInput, Charset encodingInput) {
       return read(fileInput.toPath(),encodingInput);
    }

    public static List<String> read(Path fileInput, Charset encodingInput) {
        if(fileInput == null){
            logger.warn("The fileInput we try to read is a NULL object.");
            return new ArrayList<>();
        }
        if (encodingInput == null) encodingInput = StandardCharsets.UTF_8;
        List<String> collection = new ArrayList<>();
        try {
           /* try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileInput), encodingInput))) {
                String line;
                while ((line = in.readLine()) != null) {collection.add(line);}
            }*/
            //read file into stream, try-with-resources
            try (Stream<String> stream = Files.lines(fileInput,encodingInput)) {
               collection.addAll(stream.collect(Collectors.toList()));
            }
            return collection;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String readAll(File fileInput, Charset encodingInput) {
        return readAll(fileInput.toPath(),encodingInput);
    }

    public static String readAll(Path fileInput, Charset encodingInput) {
        List<String> list = read(fileInput,encodingInput);
        StringBuilder sb = new StringBuilder();
        for(String s: list){
            sb.append(s).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public static List<String> read(File fileInput) {
        return read(fileInput.toPath(),StandardCharsets.UTF_8);
    }

    public static List<String> read(Path fileInput) {
        return read(fileInput,StandardCharsets.UTF_8);
    }

    public static String readAll(File fileInput) {
        return readAll(fileInput.toPath(),StandardCharsets.UTF_8);
    }

    public static String readAll(Path fileInput) {
        return readAll(fileInput,StandardCharsets.UTF_8);
    }

    /**
     * Method to read a {@link File}
     * @param file the {@link File}.
     * @return the {@link InputStreamReader}.
     */
    public static InputStreamReader readInputStream(File file) {
        return readInputStream(file.toPath());
    }

    public static InputStreamReader readInputStream(Path file){
        try {
            return new InputStreamReader(Files.newInputStream(file), "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
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

    public static String toString(File file) {
        return toString(file.toPath(),StandardCharsets.UTF_8);
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
        try {
            return new String(Files.readAllBytes(path), encoding);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "N/A";
        }
    }

    /**
     * Method to get the String content of the File.
     * href: http://www.adam-bien.com/roller/abien/entry/java_8_reading_a_file
     * href: http://stackoverflow.com/questions/16919501/create-a-path-from-string-in-java7
     *
     * @param path     the Path top the File to copy.
     * @return the String of the content of the File.
     */
    public static String toString(Path path) {
        return toString(path,StandardCharsets.UTF_8);
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
        try{
            Files.write(fileOutput.toPath(),bbuf,StandardOpenOption.WRITE);
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
     * @return the {@link Boolean} is true if all the operations are done.
     */
    public static Boolean appendToFile(File fileToUpdate, String textToAppend) {
        try {
            Files.write(fileToUpdate.toPath(),textToAppend.getBytes(),StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Method to check if a String path to a File is valid.
     * href: http://stackoverflow.com/questions/468789/is-there-a-way-in-java-to-determine-if-a-path-is-valid-without-attempting-to-cre
     * @param file the String path to the File.
     * @return if true the String path reference to a File.
     */
    public static boolean isFileValid(String file) {
        /*try {
            File f = new File(file);
            if (f.isFile() && !f.isDirectory()) return true;
            f = new File(getDirectoryUser() + file);
            if (f.isFile() && !f.isDirectory()) return true;
            *//*f = new File(getDirectoryUser() + file);
            if (f.isFile() && !f.isDirectory()) return true;*//*
            if (f.exists())return f.canWrite();
            //else
            if(f.createNewFile()){
                if(f.delete())return true;
            }
            if (f.isDirectory()) logger.warn("The path:" + file + " is a directory");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }*/
        try {
            //The toRealPath() method returns the real path of an existing file.
            //Note that a real file is required in the file system, otherwise there will be an exception:
            Path path = Paths.get(file);
            Path real = path.toRealPath();
            return true;
        }
        catch (IOException e) {
            return false;
        }
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
        return isFileExists(Paths.get(pathToFile));
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param file tje {@link File} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isFileExists(File file) {
        return file!=null && file.isFile() && file.exists();
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param path tje {@link Path} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isFileExists(Path path) {
        return path !=null && Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param pathToFile tje {@link String} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isDirectoryExists(String pathToFile) {
        return isDirectoryExists(Paths.get((pathToFile)));
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param file tje {@link File} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isDirectoryExists(File file) {
        return file != null && file.isDirectory() && file.exists();
    }

    /**
     * Method to check is a directory file exists and is valid.
     * @param path tje {@link Path} path to the file to check.
     * @return the {@link Boolean} is true the file exists and is valid.
     */
    public static Boolean isDirectoryExists(Path path) {
        return path != null && Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * Method to check the file is pointed to a absolute reference.
     * @param pathToFile the {@link String} path to the file.
     * @return the {@link Boolean} is true if the file is referenced from absolute reference.
     */
    public static Boolean isAbsolute(String pathToFile){
        return isAbsolute(new File(pathToFile));
    }

    /**
     * Method to check the file is pointed to a absolute reference.
     * @param file the {@link File} to analyze..
     * @return the {@link Boolean} is true if the file is referenced from absolute reference.
     */
    public static Boolean isAbsolute(File file){
         return file.isAbsolute() || isAbsolute(file.toPath());
    }

    /**
     * Method to check the file is pointed to a absolute reference.
     * @param path the {@link Path} to the file.
     * @return the {@link Boolean} is true if the file is referenced from absolute reference.
     */
    public static Boolean isAbsolute(Path path){
        return path.isAbsolute();
    }

    /**
     * Method to check the file is pointed to a relative reference.
     * @param pathToFile the {@link String} path to the file.
     * @return the {@link Boolean} is true if the file is referenced from relative reference.
     */
    public static Boolean isRelative(String pathToFile){
        return !isAbsolute(pathToFile);
    }

    /**
     * Method to check the file is pointed to a relative reference.
     * @param file the {@link File} path to the file.
     * @return the {@link Boolean} is true if the file is referenced from relative reference.
     */
    public static Boolean isRelative(File file){
        return !isAbsolute(file);
    }

    /**
     * Method to check the file is pointed to a relative reference.
     * @param path the {@link Path} path to the file.
     * @return the {@link Boolean} is true if the file is referenced from relative reference.
     */
    public static Boolean isRelative(Path path){
        return !isAbsolute(path);
    }

    /**
     * Method to check if  a file is direct son of the parent directory.
     * @param directory the {@link File} directory.
     * @param child the {@link String} path of the son.
     * @return the {@link Boolean} is true if the file is founded and exists.
     */
    public static Boolean isFileOnDirectory(File directory,String child){
        if(child.startsWith(File.separator)) child = child.substring(1,child.length());
        if(child.endsWith(File.separator)) child = child.substring(0,child.length()-1);
        return new File(directory,child).exists();
    }

    /**
     * Method to check if  a file is direct son of the parent directory.
     * @param directory the {@link String} directory.
     * @param child the {@link String} path of the son.
     * @return the {@link Boolean} is true if the file is founded and exists.
     */
    public static Boolean isFileOnDirectory(String directory,String child){
        if(child.startsWith(File.separator)) child = child.substring(1,child.length());
        if(child.endsWith(File.separator)) child = child.substring(0,child.length()-1);
        if(directory.endsWith(File.separator)) directory = directory.substring(0,directory.length()-1);
        return new File(directory,child).exists();
    }

    /**
     * Method to check if  a file is direct son of the parent directory.
     * @param directory the {@link Path} directory.
     * @param child the {@link String} path of the son.
     * @return the {@link Boolean} is true if the file is founded and exists.
     */
    public static Boolean isFileOnDirectory(Path directory,String child){
        if(child.startsWith(File.separator)) child = child.substring(1,child.length());
        if(child.endsWith(File.separator)) child = child.substring(0,child.length()-1);
        return Files.exists(Paths.get(getPath(directory),child));
    }

    /**
     * Method to rename the extension of a file.
     * @param source the {@link File} the file.
     * @param newExtension the {@link String} name o f the new extension.
     * @param tempMode the {@link Boolean} rename the file only on java memory not on the disk.
     * @return the {@link String} name of the file without extension.
     */
    public static File renameExtensionFile(File source, String newExtension,boolean tempMode){
        return new File(renameExtension(source.getAbsolutePath(),newExtension,tempMode));
    }

    /**
     * Method to rename the extension of a file.
     * @param source the {@link Path} the file.
     * @param newExtension the {@link String} name o f the new extension.
     * @param tempMode the {@link Boolean} rename the file only on java memory not on the disk.
     * @return the {@link String} name of the file without extension.
     */
    public static Path renameExtensionPath(Path source, String newExtension,boolean tempMode){
        return Paths.get(renameExtension(source.toAbsolutePath().toString(),newExtension,tempMode));
    }

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
     * @param source the {@link Path} the file.
     * @param newExtension the {@link String} name o f the new extension.
     * @param tempMode the {@link Boolean} rename the file only on java memory not on the disk.
     * @return the {@link String} name of the file without extension.
     */
    public static String renameExtension(Path source, String newExtension,boolean tempMode){
        return renameExtension(source.toAbsolutePath().toString(),newExtension,tempMode);
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
        if(isFileExists(file) && file.renameTo(new File(target))){
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
     * @param file the {@link File} path to the file.
     * @return the {@link File} name of the file without extension.
     */
    public static Path removeExtension(Path file) {
        String fileName = file.toAbsolutePath().toString();
        int extPos = fileName.lastIndexOf(".");
        if(extPos == -1) return file;
        else{
            if(renameTo(file,Paths.get(fileName.substring(0, extPos)))){
                return file;
            }else{
                logger.error("Can't remove the extension of the file:"+file.toAbsolutePath());
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
     * Method to convert a collection of path to a collection of File.
     * @param paths the {@link List} of {@link Path} to convert.
     * @return  the {@link List} of {@link File}.
     */
    public static List<File> toFiles(List<Path> paths){
        List<File> files = new ArrayList<>();
        for(Path path: paths){
            files.add(path.toFile());
        }
        return files;
    }

    /**
     * Method to convert a collection of path to a collection of File.
     * @param files the {@link List} of {@link File} to convert.
     * @return the {@link List} of {@link Path}.
     */
    public static List<Path> toPaths(List<File> files){
        List<Path> paths = new ArrayList<>();
        for(File file: files){
            paths.add(file.toPath());
        }
        return paths;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // REPLICATED FILE METHODS WITH JAVA NIO
    // HELP MYSELF TO UPDATE MY CODE FORM THE USE OF FILE TO PATH
    ///////////////////////////////////////////////////////////////////////////////////

    public static Path[] listFilesDirectories(Path path) {
        DirectoryStream.Filter<? super Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return Files.isDirectory(entry);
            }
        };
        return listFiles(path,filter);
    }

    public static Path[] listFiles(Path path) {
        return listFiles(path,null);
    }

    public static Path[] listFiles(Path path,DirectoryStream.Filter<? super Path> filter){
        List<Path> list = new ArrayList<>();
        try (DirectoryStream<Path>  stream =
                     filter!=null?Files.newDirectoryStream(path,filter):Files.newDirectoryStream(path)){
            for(Path path2 : stream){
                list.add(path2);
            }
            return list.toArray(new Path[list.size()]);
        } catch (IOException e) {
            return list.toArray(new Path[list.size()]);
        }
    }
    public static Path[] listFilesRecursively(Path path){
        return listFilesRecursively(path,null);
    }

    public static Path[] listFilesRecursively(Path path,DirectoryStream.Filter<? super Path> filter){
        List<Path> list = new ArrayList<>();
        try (DirectoryStream<Path>  stream =
                     filter!=null?Files.newDirectoryStream(path,filter):Files.newDirectoryStream(path)){
            for(Path path2 : stream){
                if (Files.isDirectory(path2))
                    if(filter!=null)
                        listFilesRecursively(path2,filter);
                    else
                        listFilesRecursively(path2);
                else list.add(path2);
            }
            return list.toArray(new Path[list.size()]);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return list.toArray(new Path[list.size()]);
        }
    }

    public static Path[] listFilesWithVisitor(Path path){
        /*http://stackoverflow.com/questions/20987214/recursively-list-all-files-within-a-directory-using-nio-file-directorystream*/
        final List<Path> list = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(!attrs.isDirectory()){
                        list.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return list.toArray(new Path[list.size()]);
    }

    public static String[] list(Path path){
        DirectoryStream.Filter<? super Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException
            {
                return Files.isDirectory(entry);
            }
        };
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(path,filter)) {
            List<String> list = new ArrayList<>();
            for (Path path2 : stream) {
                list.add(path2.getFileName().toString());
            }
            return list.toArray(new String[list.size()]);
        }catch(IOException e){
            logger.error(e.getMessage(),e);
            return new String[0];
        }
    }

    public static Boolean mkdirs(Path path) {
        try {
            Files.createDirectories(path.getParent());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean mkdir(Path path){
        try {
            Files.createDirectory(path.getParent());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean createNewFile(Path path){
        try {
            Files.createFile(path);
            return true;
        } catch (IOException e) {
           return false;
        }
    }

    public static Boolean createTempFile(Path path){
        try {
            Files.createTempFile(path,generateRandomStringSimple(6),null);
            return true;
        } catch (IOException e) {
           return false;
        }
    }

    public static Boolean delete(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean setLastModified(Path path,FileTime fileTime){
        //FileTime fileTime = FileTime.from(1600, TimeUnit.DAYS);
        try {
            Files.setLastModifiedTime(path,fileTime);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static FileTime lastModified(Path path){
        //FileTime fileTime = FileTime.from(1600, TimeUnit.DAYS);
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            return FileTime.from(1600, TimeUnit.DAYS);
        }
    }

    public static Boolean canWrite(Path path){
        return Files.isWritable(path);
    }

    public static Boolean canRead(Path path){
        return Files.isReadable(path);
    }

    public static Boolean canExecute(Path path){
        return Files.isExecutable(path);
    }

    public static String getCanonicalPath(Path path){
        try {
            return new URI(path.toUri().toString()).normalize().getPath();
        } catch (URISyntaxException e) {
            try {
                return path.toFile().getCanonicalPath();
            } catch (IOException e1) {
                return path.toUri().toString();
            }
        }
    }

    public static String getAbsolutePath(Path path){
        return path.toAbsolutePath().toString();
    }

    public static File getAbsoluteFile(Path path){
        return path.toAbsolutePath().toFile();
    }

    public static File getCanonicalFile(Path path){
        return new File(getCanonicalPath(path));
    }

    public static File getParentFile(Path path){
        return path.getParent().toFile();
    }

    public static Boolean renameTo(Path source,Path target,String newName){
        try {
            Files.move(source, target.resolve(newName),StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean renameTo(Path source,Path target){
        try {
            Files.move(source,target,StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

   /* public Boolean deleteOnExit(Path path){
        try {
            Files.newOutputStream(path, StandardOpenOption.DELETE_ON_CLOSE);
            return true;
        } catch (IOException e) {
            return false;
        }
    }*/



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LOCAL StringUtilities Methods for avoid the dependency
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the name filters through a series of including and excluding
     * regular expressions.
     * @param name The String that will be filtered.
     * @param included An array of regular expressions that need to succeed
     * @param excluded An array of regular expressions that need to fail
     * @return true if the name filtered through correctly; or false otherwise.
     */
    private static boolean isMatch(String name, Pattern[] included, Pattern[] excluded) {
        if (null == name)return false;
        boolean accepted = false;
        // retain only the includes
        if (null == included) accepted = true;
        else {
            Pattern pattern;
            for (Pattern anIncluded : included) {
                pattern = anIncluded;
                if (pattern != null && (pattern.matcher(name).matches() || pattern.matcher(name).find())) {
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
                if (pattern != null && (pattern.matcher(name).matches() || pattern.matcher(name).find())) {
                    accepted = false;
                    break;
                }
            }
        }
        return accepted;
    }

    /**
     * Method simple to generate a alphanumerical String.
     * @param length the {@link Integer} length of the String.
     * @return the {@link String} generate.
     */
    private static String generateRandomStringSimple(int length){
        byte[] array = new byte[length]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    /**
     * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
     * escaped sequence for characters outside of ASCII.
     * It is equivalent to: native2ascii -encoding utf-8
     * @param stringUTF8 string encoding utf8
     * @return ASCII string encoding ascii.
     */
    private static String toASCII(String stringUTF8) {
        if (stringUTF8==null) return null;
        StringReader reader = new StringReader(toHexString(stringUTF8.getBytes(StandardCharsets.UTF_8)));
        //return unicodeEscape(reader.toString());
        return new String(reader.toString().getBytes(),StandardCharsets.US_ASCII);
    }

    /**
     * Method to convert a array of bytes to a string.
     * @param arrayBytes the {@link Byte} array Collection of bytes.
     * @return the {@link String} of the hash.
     */
    private static String toHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    /**
     * Reads file with unicode escaped characters and write them out to
     * stdout in UTF-8
     * This utility is equivalent to: native2ascii -reverse -encoding utf-8
     * @param stringASCII string encoding ascii.
     * @return UTF8 string encoding utf8.
     */
    private static String toUTF8(String stringASCII) {
        if (stringASCII == null) return null;
        StringReader reader = new StringReader(toHexString(stringASCII.getBytes(StandardCharsets.US_ASCII)));
       /* String line = convertUnicodeEscapeToASCII(reader.toString());
        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        return toHexString(bytes);*/
        String utf8 = new String(reader.toString().getBytes(),StandardCharsets.UTF_8);
        return toHexString(utf8.getBytes());
    }

    /**
     * Converts a byte array to a String, taking the
     * eight bits of each byte as the lower eight bits of the chars
     * in the String.
     * @param arrayBytes the {@link Byte} array to convert to char array.
     * @return the {@link String} converted from a byte array.
     */
    private static String toString(byte[] arrayBytes){
        //return new String(toChars(bytes));
        StringBuilder sb = new StringBuilder(2*arrayBytes.length);
        for (byte b : arrayBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    private static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty() ;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Recursive function to descend into the directory tree and find all the files.
     *
     * @param dir the {@link File} object defining the top directory 
     * @param pattern the {@link Pattern} for use regular expression on filter the file in the directory file.
     * @return the {@link List} of the {@link Path} found on the file directory.
     */
    public static List<Path> getPathsFromDirectory(Path dir,Pattern pattern) {
        Path[] listFile = listFiles(dir);
        List<Path> list = new ArrayList<>();
        for (Path aListFile : listFile) {
            if (Files.isDirectory(aListFile)) {
                list.addAll(getPathsFromDirectory(aListFile, pattern));
            } else {
                if(pattern == null){
                    list.add(aListFile);
                }else if(pattern.matcher(aListFile.getFileName().toString()).matches()) {
                    list.add(aListFile);
                }else if(pattern.matcher(aListFile.getFileName().toString()).find()){
                    list.add(aListFile);
                }
            }
        }
        return list;
    }

    /**
     * Recursive function to descend into the directory tree and find all the files.
     *
     * @param dir the {@link File} object defining the top directory
     * @param pattern the {@link Pattern} for use regular expression on filter the file in the directory file.
     * @return the {@link List} of the {@link Path} found on the file directory.
     */
    public static List<Path> getPathsFromDirectoryWithWalkFileTree(Path dir,Pattern pattern) {
        List<Path> list = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    if(pattern == null) list.add(file);
                    else if(pattern.matcher(file.getFileName().toString()).matches()) list.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });
            //JAVA 8 UPGRADE
            /*Files.walk(start)
                    .filter( path -> path.toFile().isFile())
                    .filter( path -> path.toString().endsWith(".mp3"))
                    .forEach( System.out::println );*/
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return list;
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
     * Method to 'walk' within many directory under a root directory and load all files in these.
     *
     * @param preload the String path to the root directory.
     * @return the List of File prsent in the Directory preload.
     */
     public static List<File> getFileFromDirectory(String preload) {
         return getFileFromDirectory(new File(preload));
     }

    /**
     * Method to 'walk' within many directory under a root directory and load alll files in these..
     *
     * @param preload the File root directory
     * @return the List of File prsent in the Directory preload.
     */
    public static List<File> getFileFromDirectory(File preload) {
        if(FileUtilities.isFileExists(preload)) {
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
        }else{
            return null;
        }
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
        public void walk(File node) throws Exception {
            if (node.isDirectory()) {
                handler.directory(node);
                File[] children = node.listFiles();
                //Arrays.sort --> sort
                if (children != null) {
                    Arrays.sort(children, new Comparator<File>() {
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
    /*static public class TreeCopier implements FileVisitor<Path> {
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

        *//**
         * Copy source file to target location. If {@code prompt} is true then
         * prompt user to overwrite target if it exists. The {@code preserve}
         * parameter determines if file attributes should be copied/preserved.
         *//*
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
    }*/



}
