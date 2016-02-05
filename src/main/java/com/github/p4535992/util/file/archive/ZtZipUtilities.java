package com.github.p4535992.util.file.archive;

import com.github.p4535992.util.file.FileUtilities;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by 4535992 on 04/02/2016.
 * @author 4535992.
 * href: https://github.com/zeroturnaround/zt-zip
 */
public class ZtZipUtilities extends ArchiveUtilities{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ZtZipUtilities.class);

    private static ZtZipUtilities ourInstance = new ZtZipUtilities();

    public static ZtZipUtilities getInstance() {return ourInstance;}

    private ZtZipUtilities() {}

    /**
     * Method to Check if an entry exists in a ZIP archive
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip
     * @param nameFileToSearch the {@link String} name of the file to search e.g. foo.txt
     * @return the {@link Boolean} if true the file exists on the zip file.
     */
    public static Boolean containsFile(File fileZip,String nameFileToSearch){
        return ZipUtil.containsEntry(fileZip,nameFileToSearch);
    }

    /**
     * Method Extract an entry from a ZIP archive into a byte array.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip
     * @param nameFileToSearch the {@link String} name of the file to search e.g. foo.txt
     * @return the {@link File} output extracted.
     */
    public static File extractFileFromZip(File fileZip,String nameFileToSearch){
        byte[] bytes = ZipUtil.unpackEntry(fileZip,nameFileToSearch);
        return FileUtilities.toTempFile(bytes);
    }

    /**
     * Method Extract an entry from a ZIP archive into file system
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param nameFileToSearch the {@link String} name of the file to search e.g. foo.txt .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @return the {@link File} output extracted.
     */
    public static File extractFileFromZip(File fileZip,String nameFileToSearch,File output){
        if(ZipUtil.unpackEntry(fileZip,nameFileToSearch,output)){
           return output;
        }else{
            logger.error("Some error on extract the:"+nameFileToSearch+" on the file system:"+output);
            return null;
        }
    }

    /**
     * Method Extract a ZIP archive.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @return the List of {@link File} output extracted.
     */
    public static List<File> extractFilesFromZip(File fileZip,File output){
        File dir = extractDirectoryFromZip(fileZip,output);
        return FileUtilities.getFilesFromDirectory(dir);
    }

    /**
     * Method Extract a ZIP archive.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @return the {@link File} output extracted.
     */
    public static File extractDirectoryFromZip(File fileZip,File output){
        ZipUtil.unpack(fileZip, output);
        return output;
    }

    /**
     * Method Extract a ZIP archive which becomes a directory.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     */
    public static void extractDirectoryFromZip(File fileZip){
        ZipUtil.explode(fileZip);
    }

    /**
     * Method Extract a directory from a ZIP archive including the directory name.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @param prefixDirName the {@link String} name of the file to search e.g. doc/ .
     * @return the {@link File} output extracted.
     */
    public static File extractDirectoryFromZipIncludeDirectoryPath(File fileZip,File output,final String prefixDirName){
        ZipUtil.unpack(fileZip, output, new NameMapper() {
            public String map(String name) {
                return name.startsWith(prefixDirName) ? name : null;
            }
        });
        return output;
    }

    /**
     * Method Extract a directory from a ZIP archive including the directory name.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param prefixDirName the {@link String} name of the file to search e.g. doc/ .
     * @return the {@link File} output extracted.
     */
    public static File extractDirectoryFromZipIncludeDirectoryPath(File fileZip,final String prefixDirName){
        File directoryTempOutput = new File("/tmp/");
        return extractDirectoryFromZipIncludeDirectoryPath(fileZip,directoryTempOutput,prefixDirName);
    }

    /**
     * Method Extract a directory from a ZIP archive excluding the directory name.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @param prefixDirName the {@link String} name of the file to search e.g. doc/ .
     * @return the {@link File} output extracted.
     */
    public static File  extractDirectoryFromZipExcludeDirectoryPath(File fileZip,File output,final String prefixDirName){
        ZipUtil.unpack(fileZip, output, new NameMapper() {
            public String map(String name) {
                return name.startsWith(prefixDirName) ? name.substring(prefixDirName.length()) : name;
            }
        });
        return output;
    }

    /**
     * Method Extract a directory from a ZIP archive excluding the directory name.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param prefixDirName the {@link String} name of the file to search e.g. doc/ .
     * @return the {@link File} output extracted.
     */
    public static File extractDirectoryFromZipExcludeDirectoryPath(File fileZip,final String prefixDirName){
        File directoryTempOutput = new File("/tmp/");
        return extractDirectoryFromZipExcludeDirectoryPath(fileZip,directoryTempOutput,prefixDirName);
    }

    /**
     * Method Extract files from a ZIP archive that match a name pattern.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param patternNameFileToSearch the {@link String} name of the file to search e.g. foo.txt
     * @return the {@link File} output extracted.
     */
    public static List<File> extractFilesByPattern(File fileZip, final String patternNameFileToSearch){
        File directoryTempOutput = new File("/tmp/");
        return extractFilesByPattern(fileZip,directoryTempOutput,patternNameFileToSearch);
    }

    /**
     * Method Extract files from a ZIP archive that match a name pattern.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @param patternNameFileToSearch the {@link String} name of the file to search e.g. foo.txt
     * @return the {@link File} output extracted.
     */
    public static List<File> extractFilesByPattern(File fileZip,File output, final String patternNameFileToSearch){
        //final List<File> files = new ArrayList<>();
        ZipUtil.unpack(fileZip, output, new NameMapper() {
            public String map(String name) {
                if (name.contains(patternNameFileToSearch)) {
                    return name;
                }
                else {
                    // returning null from the map method will disregard the entry
                    return null;
                }
            }
        });
        if(FileUtilities.isDirectoryExists(output)){
            return FileUtilities.getFilesFromDirectory(output);
        }else{
            return Collections.singletonList(output);
        }
    }




















}
