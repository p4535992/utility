package com.github.p4535992.util.file.archive;

import com.github.p4535992.util.file.FileUtilities;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.zip.*;

/**
 * Created by 4535992 on 30/11/2015.
 */
@SuppressWarnings("unused")
public class ArchiveUtilities {
    
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ArchiveUtilities.class);

    private static List<File> filesListInZip = new ArrayList<>();
    private static final List<String> filesListInDir = new ArrayList<>();



    /*
  * File signatures found at: http://www.garykessler.net/library/file_sigs.html
  */
    private static final char[] SIG_SEVENZIP = new char[] { 0x37, 0x7A, 0xBC, 0xAF, 0x27, 0x1C };
    private static final char[] SIG_RAR = new char[] { 0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00 };
    private static final char[] SIG_ZIP = new char[] { 0x50, 0x4B, 0x03, 0x04 };
    private static final char[] SIG_PAK = new char[] { 0x53, 0x42, 0x42, 0x46, 0x30, 0x32 };

    private static final char[][] SIGNATURES = new char[][] { SIG_SEVENZIP, SIG_RAR, SIG_ZIP, SIG_PAK };

    private static final Set<String> SUPPORTED_TYPES = new HashSet<>(Arrays.asList("zip", "7z", "rar", "pak"));

    private Path path;

    protected ArchiveUtilities() {}

    /*public ArchiveUtilities(final Path path) {
        this.path = path;
    }

    public ArchiveUtilities(final String path) {
        this(Paths.get(path));
    }*/

    private static ArchiveUtilities instance = null;

    public static ArchiveUtilities getInstance(){
        if(instance == null) {
            instance = new ArchiveUtilities();
        }
        return instance;
    }

    private static List<File> extractFilesFromZipFile(File zipFile,String nameOfFile){
        filesListInZip = new ArrayList<>();
        try (ZipInputStream inStream = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            String destinationName = zipFile.getAbsoluteFile().getParentFile().getAbsolutePath();
            ZipEntry entry ;
            //if ((entry = inStream.getNextEntry()) != null) {
            while((entry = inStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if(nameOfFile != null){
                    if(entryName.equals(nameOfFile)) {
                        supportZip(entryName,destinationName,inStream);
                    }
                }else{
                    supportZip(entryName,destinationName,inStream);
                }
            }
        }catch(IOException e){
            logger.error(e.getMessage(),e);
            return null;
        }
        return filesListInZip;
    }

    private static boolean supportZip(
            String entryName,String destinationName,ZipInputStream inStream) throws IOException {
        int read;
        byte[] buffer = new byte[1024];
        File newFile = new File(entryName);
        String directory = newFile.getParent();
        if (directory == null) {
            if (newFile.isDirectory())
                return false;
        }
        try (OutputStream outStream = new FileOutputStream(destinationName + File.separator + entryName)) {
            while ((read = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, read);
            }
        }
        filesListInZip.add(newFile);
        return true;
    }

    public static List<File> extractAllFilesFromZipFile(File zipFile){
        return extractFilesFromZipFile(zipFile,null);
    }

    public static File extractFileFromZipFile(File zipFile,String nameOfFile){
        return extractFilesFromZipFile(zipFile,nameOfFile).get(0);
    }

    public static List<File> unzip(File zipFilePath, String destDirectory){
        return unzip(zipFilePath.getAbsolutePath(), destDirectory);
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath the String path to the Zip File to Read.
     * @param destDirectory the String path to the destination directory.
     * @return the List of the File we created on the testDirectory
     */
    public static List<File> unzip(String zipFilePath, String destDirectory){
        try{
            List<File> files = new ArrayList<>();
            File destDir = new File(destDirectory);
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            try (ZipInputStream zipIn = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipFilePath)))) {
                ZipEntry entry = zipIn.getNextEntry();
                // iterates over entries in the zip file
                while (entry != null) {
                    //String entryName = entry.getName();
                    String filePath = destDirectory + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        extractFileFromZip(zipIn, filePath);
                        files.add(new File(destDirectory + File.separator + entry.getName()));
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        boolean mkdir = dir.mkdir();
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
            }
            return files;
        }catch(IOException e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn the ZipInputStream of the ZipFile.
     * @param filePath the String path to the destination of the file extracted File.
     * @return if true all operation are done.
     */
    private static boolean extractFileFromZip(ZipInputStream zipIn, String filePath){
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }catch(IOException e){
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    /**
     * This method zips the directory
     * @param dir the File of the Directory to Zip.
     * @param zipDirName the String path destination of the Zip File.
     */
    /*public static void zip(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            //now zip filesListInZip one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                logger.info("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }*/

    /**
     * This method populates all the filesListInZip in a directory to a List
     * @param dir the File of the Directory to Zip.
     */
    private static void populateFilesList(File dir){
        File[] files = dir.listFiles();
        if(files != null && files.length >0) {
            for (File file : files) {
                if (file.isFile()) filesListInDir.add(file.getAbsolutePath());
                else populateFilesList(file);
            }
        }else{
            logger.warn("The directory:"+dir.getAbsolutePath()+" is a empty directory can't populate");
        }
    }


    /**
     * This method compresses the single file to zip format
     * @param file the File to Zip.
     * @param zipFileName the String path destination of the Zip File.
     */
    private static void zipSingleFile(File file, String zipFileName) {
        try {
            try ( //create ZipOutputStream to write to the zip file
                    FileOutputStream fos = new FileOutputStream(zipFileName); 
                    ZipOutputStream zos = new ZipOutputStream(fos)) {
                //add a new Zip Entry to the ZipOutputStream
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                //Close the zip entry to write to zip file
                zos.closeEntry();
            }
            logger.info(file.getCanonicalPath()+" is zipped to "+zipFileName);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

    }

    public static long getChecksum(final Path pathToTheArchive) throws IOException {
        CheckedInputStream checkedStream;
        try (FileInputStream inputFile = new FileInputStream(pathToTheArchive.toFile())) {
            checkedStream = new CheckedInputStream(inputFile, new Adler32());
            try (BufferedInputStream input = new BufferedInputStream(checkedStream)) {
                while (input.read() != -1) {
                    //Do nothing; simply reading file contents.
                }
            }
            checkedStream.close();
        }

        long checksum = checkedStream.getChecksum().getValue();

        logger.info("Checksum ({}) created for file: {}", checksum, pathToTheArchive);

        return checksum;

    }

    public static String identifyType(final byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        String extension = "";
        if (buffer.remaining() < 7) return "";
        for (char[] array : SIGNATURES) {
            boolean typeFound = false;
            for (int i = 0; i < array.length; i++) {
                if (buffer.get() != array[i]) {
                    buffer.rewind();
                    break;
                }
                if (i == array.length - 1)typeFound = true;
            }
            if (typeFound) {
                extension =  identifyType(array);
                break;
            }
        }
        return extension;

    }

    public static String identifyType(final Path path, final boolean suppressLogging) {
        if (path == null) return null;
        String extension = "";
        try {
            try (ByteChannel b = Files.newByteChannel(path, StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate(10);
                b.read(buffer);
                buffer.position(0);
                for (char[] array : SIGNATURES) {
                    boolean typeFound = false;
                    for (int i = 0; i < array.length; i++) {
                        byte get = buffer.get();
                        if (get != (byte) array[i]) {
                            buffer.position(0);
                            break;
                        }
                        if (i == array.length - 1) {
                            typeFound = true;
                            break;
                        }
                    }
                    if (typeFound) {
                        extension = identifyType(array);
                        break;
                    }
                }
                if (!suppressLogging) {
                    logger.info("File '{}' identified as '{}'", path, extension);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return extension;

    }

    private static String identifyType(char[] signatureBytes){
        String extension;
        if (signatureBytes == SIG_SEVENZIP) {
            extension = "7z";
        } else if (signatureBytes == SIG_RAR) {
            extension = "rar";
        } else if (signatureBytes == SIG_ZIP) {
            extension = "zip";
        } else if (signatureBytes == SIG_PAK) {
            extension = "pak";
        } else extension =
                "";
        return extension;
    }

    public static boolean isSupported(final Path path, final boolean suppressLogging) {
        return path != null && SUPPORTED_TYPES.contains(identifyType(path, suppressLogging));

    }

    public static boolean isSupported(final Path path) {
        return isSupported(path, false);
    }

    public static boolean zip(File filePathDirectoryToCompress,String filePathOutputZip) {
        return zip(filePathDirectoryToCompress.toPath(),filePathOutputZip);
    }

    public static boolean zip(String filePathDirectoryToCompress,String filePathOutputZip) {
        return zip(Paths.get(filePathDirectoryToCompress),filePathOutputZip);
    }

    /**
     * This method creates the zip archive and then goes through
     * each file in the chosen directory, adding each one to the
     * archive. Note the use of the try with resource to avoid
     * any finally blocks.
     * href: http://www.thecoderscorner.com/team-blog/java-and-jvm/63-writing-a-zip-file-in-java-using-zipoutputstream
     * @param filePathDirectoryToCompress the Path object to the directoryOrFile to Compress.
     * @param filePathOutputZip the String path to the folder where create the Archive file.
     * @return if true all the operation are succesfull.
     */
    public static boolean zip(Path filePathDirectoryToCompress,String filePathOutputZip) {
        // the directory to be zipped
        //Path directory = Paths.get(filePathDirectoryToCompress);
        // the zip file name that we will create
        File zipFileName = Paths.get(filePathOutputZip).toFile();
        // open the zip stream in a try resource block, no finally needed
        try( ZipOutputStream zipStream = new ZipOutputStream(
                new FileOutputStream(zipFileName)) ) {

            // traverse every file in the selected directory and add them
            // to the zip file by calling addToZipFile(..)
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(filePathDirectoryToCompress);
            //dirStream.forEach(path -> addToZipFile(path, zipStream));
            for(Path path : dirStream){
                addToZipFile(path,zipStream);
            }
            logger.info("Zip file created in " + filePathDirectoryToCompress.toFile().getPath());
            return true;
        }
        catch(IOException|ZipParsingException e) {
            logger.error("Error while zipping.", e);
            return false;
        }
    }

    /**
     * Adds an extra file to the zip archive, copying in the created
     * date and a comment.
     * @param file file to be archived
     * @param zipStream archive to contain the file.
     */
    private static void addToZipFile(Path file, ZipOutputStream zipStream) {
        String inputFileName = file.toFile().getPath();
        try (FileInputStream inputStream = new FileInputStream(inputFileName)) {

            // create a new ZipEntry, which is basically another file
            // within the archive. We omit the path from the filename
            ZipEntry entry = new ZipEntry(file.toFile().getName());
            long fileTime =  FileTime.fromMillis(file.toFile().lastModified()).toMillis();
            entry.setTime(fileTime);
            entry.setComment("Created by TheCodersCorner");
            zipStream.putNextEntry(entry);

            logger.info("Generated new entry for: " + inputFileName);

            // Now we copy the existing file into the zip archive. To do
            // this we write into the zip stream, the call to putNextEntry
            // above prepared the stream, we now write the bytes for this
            // entry. For another source such as an in memory array, you'd
            // just change where you read the information from.
            byte[] readBuffer = new byte[2048];
            int amountRead;
            int written = 0;

            while ((amountRead = inputStream.read(readBuffer)) > 0) {
                zipStream.write(readBuffer, 0, amountRead);
                written += amountRead;
            }

            logger.info("Stored " + written + " bytes to " + inputFileName);


        }
        catch(IOException e) {
            throw new ZipParsingException("Unable to process " + inputFileName, e);
        }
    }

    /**
     * We want to let a checked exception escape from a lambda that does not
     * allow exceptions. The only way I can see of doing this is to wrap the
     * exception in a RuntimeException. This is a somewhat unfortunate side
     * effect of lambda's being based off of interfaces.
     */
    private static class ZipParsingException extends RuntimeException {
        
        private static final long serialVersionUID = 123758348L;
        
        public ZipParsingException(String reason, Exception inner) {
            super(reason, inner);
        }
    }

    public boolean writeToFile(final File file) {
        try {
            long time = System.currentTimeMillis();
            try (FileOutputStream fileOutput = new FileOutputStream(file); 
                    ZipOutputStream zipOutput = new ZipOutputStream(fileOutput)) {
                List<File> files = FileUtilities.getFilesFromDirectory(file);
                for (File archiveFile : files) {
                    if (archiveFile.isDirectory()) continue;
                    logger.trace("Writing file to {}: {}", file.getName(), archiveFile.getPath());
                    ZipEntry entry = new ZipEntry(archiveFile.getPath());
                    zipOutput.putNextEntry(entry);
                    zipOutput.write(FileUtilities.toBytes(archiveFile));
                    zipOutput.closeEntry();
                }
            }
            logger.debug("Time to write '{}': {}ms", file.getName(), (System.currentTimeMillis() - time));
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    public boolean extractToFolder(final Path folder) {
        try {
            List<File> files = FileUtilities.getFilesFromDirectory(folder);
            for (File file : files) {
                if (file.isDirectory())continue;
                Files.createDirectories(folder.resolve(file.getPath()).getParent());
                try (OutputStream output = Files.newOutputStream(folder.resolve(file.getPath()), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    output.write(FileUtilities.toBytes(file));
                }
            }
            return true;
        } catch (IOException e) {
            logger.error("Extracting archive to folder.", e);
            return false;
        }

    }

    public boolean extractFileToFolder(Path file,Path folder) {
        Path newPath = folder.resolve(file);
        try {
            Files.createDirectories(newPath.getParent());
            try (OutputStream output = Files.newOutputStream(newPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                output.write(FileUtilities.toBytes(file));
            }
        } catch (IOException e) {
            logger.error("Extracting file to folder.", e);
            return false;
        }
        return true;
    }

}
