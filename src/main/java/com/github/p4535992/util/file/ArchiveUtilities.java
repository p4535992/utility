package com.github.p4535992.util.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by 4535992 on 30/11/2015.
 */
public class ArchiveUtilities {
    
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ArchiveUtilities.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }
    
    protected ArchiveUtilities() {}

    private static ArchiveUtilities instance = null;

    public static ArchiveUtilities getInstance(){
        if(instance == null) {
            instance = new ArchiveUtilities();
        }
        return instance;
    }

    private static List<File> extractFilesFromZipFile(File zipFile,String nameOfFile){
        List<File> files = new ArrayList<>();
        try (ZipInputStream inStream = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            OutputStream outStream;
            String destinationname = zipFile.getAbsoluteFile().getParentFile().getAbsolutePath();
            byte[] buffer = new byte[1024];
            int read;
            ZipEntry entry ;
            //if ((entry = inStream.getNextEntry()) != null) {
            while((entry = inStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if(nameOfFile != null){
                    if(entryName.equals(nameOfFile)) {
                        File newFile = new File(entryName);
                        String directory = newFile.getParent();
                        if (directory == null) {
                            if (newFile.isDirectory())
                                break;
                        }
                        outStream = new FileOutputStream(destinationname + File.separator + entryName);
                        while ((read = inStream.read(buffer)) > 0) {
                            outStream.write(buffer, 0, read);
                        }
                        outStream.close();
                        files.add(newFile);
                    }
                }else{
                    File newFile = new File(entryName);
                    String directory = newFile.getParent();
                    if (directory == null) {
                        if (newFile.isDirectory())
                            break;
                    }
                    outStream = new FileOutputStream(destinationname + File.separator + entryName);
                    while ((read = inStream.read(buffer)) > 0) {
                        outStream.write(buffer, 0, read);
                    }
                    outStream.close();
                    files.add(newFile);
                }
            }
        }catch(IOException e){
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
        return files;
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
                        extractFile(zipIn, filePath);
                        files.add(new File(destDirectory + File.separator + entry.getName()));
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        dir.mkdir();
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
            }
            return files;
        }catch(IOException e){
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn the ZipInputStream of the ZipFile.
     * @param filePath the String path to the destination of the file extracted File.
     * @return if true all operation are done.
     */
    private static boolean extractFile(ZipInputStream zipIn, String filePath){
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }catch(IOException e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
        return true;
    }
}
