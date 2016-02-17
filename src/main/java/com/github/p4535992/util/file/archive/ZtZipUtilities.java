package com.github.p4535992.util.file.archive;

import org.apache.commons.collections.map.MultiKeyMap;
import org.zeroturnaround.zip.*;
import org.zeroturnaround.zip.commons.IOUtils;
import org.zeroturnaround.zip.transform.StringZipEntryTransformer;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by 4535992 on 04/02/2016.
 * @author 4535992.
 * href: https://github.com/zeroturnaround/zt-zip
 */
public class ZtZipUtilities{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ZtZipUtilities.class);

    private static final ZtZipUtilities ourInstance = new ZtZipUtilities();

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
        return createTempFile(bytes);
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
        return getFilesFromDirectory(dir);
    }

    /**
     * Method Extract a ZIP archive.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @return the List of {@link File} output extracted.
     */
    public static List<File> extractFilesFromZip(File fileZip){
        File dir = extractDirectoryFromZip(fileZip,createTempDirectory());
        return getFilesFromDirectory(dir);
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
     * @return the {@link File} direcotry extarct from the zip.
     */
    public static File extractDirectoryFromZip(File fileZip){
        //ZipUtil.explode(fileZip);
        return extractDirectoryFromZip(fileZip,createTempDirectory());
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
            @Override
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
        return extractDirectoryFromZipIncludeDirectoryPath(fileZip,createTempDirectory(),prefixDirName);
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
            @Override
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
        return extractDirectoryFromZipExcludeDirectoryPath(fileZip,createTempDirectory(),prefixDirName);
    }

    /**
     * Method Extract files from a ZIP archive that match a name pattern.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param patternNameFileToSearch the {@link Pattern} name of the file to search e.g. foo.txt
     * @return the {@link File} output extracted.
     */
    public static List<File> extractFilesByPattern(File fileZip, final Pattern patternNameFileToSearch){
        return extractFilesByPattern(fileZip,createTempDirectory(),patternNameFileToSearch);
    }

    /**
     * Method Extract files from a ZIP archive that match a name pattern.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param patternNameFileToSearch the {@link String} name of the file to search e.g. foo.txt
     * @param caseInsensitive the {@link Boolean} if true enable the case insensitive options for the regex pattern.
     * @return the {@link File} output extracted.
     */
    public static List<File> extractFilesByPattern(File fileZip, final String patternNameFileToSearch,boolean caseInsensitive){
        if(caseInsensitive) {
            return extractFilesByPattern(fileZip, createTempDirectory(), Pattern.compile(patternNameFileToSearch, Pattern.CASE_INSENSITIVE));
        }else{
            return extractFilesByPattern(fileZip, createTempDirectory(), Pattern.compile(patternNameFileToSearch));
        }
    }

    /**
     * Method Extract files from a ZIP archive that match a name pattern.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param patternNameFileToSearch the {@link String} name of the file to search e.g. foo.txt
     * @return the {@link File} output extracted.
     */
    public static List<File> extractFilesByPattern(File fileZip, final String patternNameFileToSearch) {
        return extractFilesByPattern(fileZip,patternNameFileToSearch,false);
    }

    /**
     * Method Extract files from a ZIP archive that match a name pattern.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param directoryTempOutput the {@link File} output of the extraction e.g. /tmp/demo .
     * @param patternNameFileToSearch the {@link Pattern} name of the file to search e.g. foo.txt
     * @return the {@link File} list of outputs extracted.
     */
    public static List<File> extractFilesByPattern(File fileZip,File directoryTempOutput, final Pattern patternNameFileToSearch){
        //final List<File> files = new ArrayList<>();
        ZipUtil.unpack(fileZip, directoryTempOutput, new NameMapper() {
            @Override
            public String map(String name) {
                //if (name.contains(patternNameFileToSearch)) {
                if (patternNameFileToSearch.matcher(name).matches()) {
                    //look up for regular expression
                    return name;
                }
                else if (patternNameFileToSearch.matcher(name).find()) {
                    //look up for substring
                    return name;
                }
                else {
                    // returning null from the map method will disregard the entry
                    return null;
                }
            }
        });
        if(directoryTempOutput.isDirectory() && directoryTempOutput.exists()){
            return getFilesFromDirectory(directoryTempOutput);
        }else{
            return Collections.singletonList(directoryTempOutput);
        }
    }

    /**
     * Method to print .XXX entries in a ZIP archive (uses IoUtils from Commons IO)
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param suffix the {@link String} suffix of the name of the file to search e.g. .txt
     * @return the {@link File} list of outputs extracted.
     */
    public static List<File> extractFilesBySuffix(File fileZip, String suffix){
        return extractFilesBySuffix(fileZip,createTempDirectory(),suffix);
    }

    /**
     * Method to print .XXX entries in a ZIP archive (uses IoUtils from Commons IO)
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param directoryTempOutput the {@link File} output of the extraction e.g. /tmp/demo .
     * @param suffix the {@link String} suffix of the name of the file to search e.g. .txt
     * @return the {@link File} list of outputs extracted.
     */
    public static List<File> extractFilesBySuffix(File fileZip,File directoryTempOutput, String suffix){
        ZipUtil.iterate(fileZip, new ZipEntryCallback() {
            @Override
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                if (zipEntry.getName().endsWith(suffix)) {
                    //logger.info("Found " + zipEntry.getName());
                    IOUtils.copy(in,
                            new FileOutputStream(
                                    new File(directoryTempOutput + File.separator + zipEntry.getName())));
                }
            }
        });
        if(directoryTempOutput.isDirectory() && directoryTempOutput.exists()){
            return getFilesFromDirectory(directoryTempOutput);
        }else{
            return Collections.singletonList(directoryTempOutput);
        }
    }

    /**
     * Method to compress a directory into a ZIP archive
     * @param outputZip the {@link File} zip to create e.g. /tmp/demo.zip
     * @param inputDirectory the {@link File} directory of the file to compress e.g. /tmp/demo
     * @return the {@link File} Zip created.
     */
    public static File compressToZip(File outputZip,File inputDirectory){
        ZipUtil.pack(inputDirectory, outputZip);
        return outputZip;
    }

    /**
     * Method to compress a directory into a ZIP archive
     * @param outputZip the {@link File} zip to create e.g. /tmp/demo.zip
     * @param inputDirectory the {@link File} directory of the file to compress e.g. /tmp/demo
     * @param parentDirectoryForZipFile the {@link String} path to the directory parent of the zip file.
     * @return the {@link File} Zip created.
     */
    public static File compressToZip(File outputZip,File inputDirectory,String parentDirectoryForZipFile) {
        if(!parentDirectoryForZipFile.endsWith("/")) parentDirectoryForZipFile = parentDirectoryForZipFile +"/";
        final String finalParentDirectoryForZipFile = parentDirectoryForZipFile;
        ZipUtil.pack(inputDirectory, outputZip, new NameMapper() {
            @Override
            public String map(String name) {
                return finalParentDirectoryForZipFile + File.separator + name;
            }
        });
        return outputZip;
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param pathEntry the {@link String} path where add the entry in the Zip file e.g. doc/readme.txt .
     * @param fileInputEntry the {@link File} to add to the Zip e.g. f/tmp/oo.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File addEntryTo(File fileInputZip,String pathEntry,File fileInputEntry,File fileOutputZip ){
        ZipUtil.addEntry(fileInputZip,pathEntry, fileInputEntry, fileOutputZip);
        return fileOutputZip;
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param pathEntry the {@link String} path where add the entry in the Zip file e.g. doc/readme.txt .
     * @param bytes the {@link Byte} array to add to the Zip .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File addEntryTo(File fileInputZip,String pathEntry,byte[] bytes,File fileOutputZip ){
        ZipUtil.addEntry(fileInputZip,pathEntry, bytes, fileOutputZip);
        return fileOutputZip;
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param pathEntry the {@link String} path where add the entry in the Zip file e.g. doc/readme.txt .
     * @param textContentOfAFile the {@link String} content to add to the Zip.
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File addEntryTo(File fileInputZip,String pathEntry,String textContentOfAFile,File fileOutputZip ){
        return addEntryTo(fileInputZip,pathEntry,textContentOfAFile.getBytes(),fileOutputZip);
    }

    /**
     * Method to add an entry from file and from byte array to a ZIP archive.
     * @param fileInputZip the {@link File} zip to update e.g. /tmp/demo.zip.
     * @param zipEntrySource the {@link ZipEntrySource} to add to the zip.
     * @param fileOutputZip the {@link File} zip to updated e.g. /tmp/new.zip .
     * @return the {@link File} of the zip updated.
     */
    public static File addZipEntrySourceTo(File fileInputZip,ZipEntrySource zipEntrySource,File fileOutputZip){
        ZipUtil.addEntry(fileInputZip,zipEntrySource, fileOutputZip);
        return fileOutputZip;
    }

    /**
     * Method to add an entry from file and from byte array to a ZIP archive.
     * @param fileInputZip the {@link File} zip to update e.g. /tmp/demo.zip.
     * @param zipEntrySource the {@link ZipEntrySource} to add to the zip.
     * @return the {@link File} of the zip updated.
     */
    public static File addZipEntrySourceTo(File fileInputZip,ZipEntrySource zipEntrySource){
        return addZipEntrySourceTo(fileInputZip,zipEntrySource, createTempDirectory());
    }

    /**
     * Method to add an entry from file and from byte array to a ZIP archive.
     * @param fileInputZip the {@link File} zip to update e.g. /tmp/demo.zip.
     * @param zipEntriesSource the {@link ZipEntrySource} array to add to the zip.
     * @param fileOutputZip the {@link File} zip to updated e.g. /tmp/new.zip .
     * @return the {@link File} of the zip updated.
     */
    public static File addZipEntriesSourceTo(File fileInputZip,ZipEntrySource[] zipEntriesSource,File fileOutputZip){
        ZipUtil.addEntries(fileInputZip,zipEntriesSource, fileOutputZip);
        return fileOutputZip;
    }

    /**
     * Method to add an entry from file and from byte array to a ZIP archive.
     * @param fileInputZip the {@link File} zip to update e.g. /tmp/demo.zip.
     * @param zipEntriesSource the {@link ZipEntrySource} array to add to the zip.
     * @return the {@link File} of the zip updated.
     */
    public static File addZipEntriesSourceTo(File fileInputZip,ZipEntrySource[] zipEntriesSource){
        return addZipEntriesSourceTo(fileInputZip,zipEntriesSource,createTempDirectory());
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param pathEntry the {@link String} path where add the entry in the Zip file e.g. doc/readme.txt .
     * @param fileInputEntry the {@link File} to add to the Zip e.g. f/tmp/oo.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntryOnZip(File fileInputZip,String pathEntry,File fileInputEntry,File fileOutputZip){
        if(ZipUtil.replaceEntry(fileInputZip, pathEntry, fileInputEntry, fileOutputZip)){
            return fileOutputZip;
        }else{
            logger.warn("Can't replace the entry on the Zip file:"+fileInputZip.getAbsolutePath());
            return fileOutputZip;
        }
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param pathEntry the {@link String} path where add the entry in the Zip file e.g. doc/readme.txt .
     * @param bytes the {@link Byte} array to add to the Zip .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntryOnZip(File fileInputZip,String pathEntry,byte[] bytes,File fileOutputZip){
        if(ZipUtil.replaceEntry(fileInputZip, pathEntry, bytes, fileOutputZip)){
            return fileOutputZip;
        }else{
            logger.warn("Can't replace the entry on the Zip file:"+fileInputZip.getAbsolutePath());
            return fileOutputZip;
        }
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param pathEntry the {@link String} path where add the entry in the Zip file e.g. doc/readme.txt .
     * @param textContentOfAFile the {@link String} content to add to the Zip.
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntryOnZip(File fileInputZip,String pathEntry,String textContentOfAFile,File fileOutputZip){
        return replaceEntryOnZip(fileInputZip, pathEntry, textContentOfAFile.getBytes(), fileOutputZip);
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param zipEntrySource the {@link ZipEntrySource} to add to the zip.
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntryOnZip(File fileInputZip,ZipEntrySource zipEntrySource,File fileOutputZip){
        if(ZipUtil.replaceEntry(fileInputZip, zipEntrySource, fileOutputZip)){
            return fileOutputZip;
        }else{
            logger.warn("Can't replace the entry on the Zip file:"+fileInputZip.getAbsolutePath());
            return fileOutputZip;
        }
    }

    /**
     * Method to add an entry from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param zipEntrySource the {@link ZipEntrySource} to add to the zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntryOnZip(File fileInputZip,ZipEntrySource zipEntrySource){
        return replaceEntryOnZip(fileInputZip, zipEntrySource, createTempDirectory());
    }


    /**
     * Method to add entries from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param zipEntriesSource the {@link ZipEntrySource} array to add to the zip.
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntriesOnZip(File fileInputZip,ZipEntrySource[] zipEntriesSource,File fileOutputZip){
        if(ZipUtil.replaceEntries(fileInputZip, zipEntriesSource, fileOutputZip)){
            return fileOutputZip;
        }else{
            logger.warn("Can't replace the entry on the Zip file:"+fileInputZip.getAbsolutePath());
            return fileOutputZip;
        }
    }

    /**
     * Method to add entries from file to a ZIP archive
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param zipEntriesSource the {@link ZipEntrySource} array to add to the zip.
     * @return the {@link File} Zip Updated.
     */
    public static File replaceEntriesOnZip(File fileInputZip,ZipEntrySource[] zipEntriesSource){
        return replaceEntriesOnZip(fileInputZip,zipEntriesSource,createTempDirectory());
    }

    /**
     * Method to add and replace some entries on the Zip file.
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param zipEntriesSource the {@link ZipEntrySource} array to add to the zip.
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/new.zip.
     * @return the {@link File} Zip Updated.
     */
    public static File addOrReplaceOnZip(File fileInputZip,ZipEntrySource[] zipEntriesSource,File fileOutputZip){
        ZipUtil.addOrReplaceEntries(fileInputZip, zipEntriesSource,fileOutputZip);
        return fileOutputZip;
    }

    /**
     * Method to add and replace some entries on the Zip file.
     * @param fileInputZip the {@link File} Zip to update  e.g. /tmp/demo.zip .
     * @param zipEntriesSource the {@link ZipEntrySource} array to add to the zip.
     * @return the {@link File} Zip Updated.
     */
    public static File addOrReplaceOnZip(File fileInputZip,ZipEntrySource[] zipEntriesSource){
        return addOrReplaceOnZip(fileInputZip, zipEntriesSource,createTempDirectory());
    }

    /**
     * Method to transform a Zip archive entry into uppercase.
     * @param zipFile the {@link File} Zip to update e.g. /tmp/demo .
     * @param pathEntry the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/demo.zip .
     * @param transformType the {@link TransformStringType} for rename the entry.
     * @return the {@link File} Zip updated.
     */
    public static File transformStringEntry(File zipFile,String pathEntry,File fileOutputZip,TransformStringType transformType){
        try {
            transformStringEntry(new FileInputStream(zipFile),pathEntry,new FileOutputStream(fileOutputZip),transformType);
            return fileOutputZip;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
            return fileOutputZip;
        }
    }

    /**
     * Method to transform a Zip archive entry into uppercase.
     * @param zipFile the {@link InputStream} Zip to update e.g. /tmp/demo .
     * @param pathEntry the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param fileOutputZip the {@link OutputStream} Zip updated e.g. /tmp/demo.zip .
     * @param transformType the {@link TransformStringType} for rename the entry.
     * @return the {@link Boolean} is true if all operation are done.
     */
    public static Boolean transformStringEntry(InputStream zipFile,String pathEntry,OutputStream fileOutputZip,TransformStringType transformType){
        //return transformEntry(new FileInputStream(zipFile),pathEntry,new FileOutputStream(fileOutputZip),transformType);
        boolean transformed = ZipUtil.transformEntry(zipFile, pathEntry, new StringZipEntryTransformer() {
            @Override
            protected String transform(ZipEntry zipEntry, String input) throws IOException {
                switch(transformType){
                    case UPERCASE:return input.toUpperCase();
                    case LOWERCASE:return input.toLowerCase();
                    default: return input.toUpperCase();
                }
            }
        },fileOutputZip);
        if(!transformed)logger.warn("Can't rename the entry on the Zip file");
        return transformed;
    }

    /**
     * Method to compare two ZIP archives (ignoring timestamps of the entries).
     * @param zipFile the {@link File} Zip to update e.g. /tmp/demo .
     * @param pathEntry the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/demo.zip .
     * @return  the {@link Boolean} if true the archive are the same.
     */
    public static Boolean compareArchives(File zipFile,String pathEntry,File fileOutputZip){
        return ZipUtil.archiveEquals(zipFile, fileOutputZip);
    }

    /**
     * Method to compare two ZIP archive entries with same name (ignoring timestamps of the entries).
     * @param zipFile the {@link File} Zip to update e.g. /tmp/demo .
     * @param pathEntry the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/demo.zip .
     * @return  the {@link Boolean} if true the entry are the same.
     */
    public static Boolean compareEntriesWithSameName(File zipFile,String pathEntry,File fileOutputZip){
        return ZipUtil.entryEquals(zipFile,fileOutputZip,pathEntry);
    }

    /**
     * Method to compare two ZIP archive entries with different names (ignoring timestamps of the entries).
     * @param zipFile the {@link File} Zip to update e.g. /tmp/demo .
     * @param pathEntry the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param pathEntry2 the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/demo.zip .
     * @return  the {@link Boolean} if true the entry are the same.
     */
    public static Boolean compareEntriesWithDifferentNames(File zipFile,String pathEntry,String pathEntry2,File fileOutputZip){
        return ZipUtil.entryEquals(zipFile, fileOutputZip, pathEntry, pathEntry2);
    }

    /**
     * Method to compare two ZIP archive entries with different names (ignoring timestamps of the entries).
     * @param zipFile the {@link File} Zip to update e.g. /tmp/demo .
     * @param pathEntry the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param pathEntry2 the {@link String} path of the entry on the Zip File e.g. sample.txt .
     * @param fileOutputZip the {@link File} Zip updated e.g. /tmp/demo.zip .
     * @return  the {@link Boolean} if true the entry are the same.
     */
    public static Boolean compareEntriesWithDifferentNames(ZipFile zipFile, String pathEntry, String pathEntry2, ZipFile fileOutputZip){
        return ZipUtil.entryEquals(zipFile, fileOutputZip, pathEntry, pathEntry2);
    }

    /**
     * Method to convert a List of objects to a array of ZipEntrySource.
     * @param resourcesToAddToTheZip the {@link List} of {@link Object} to convert.
     * @return the array of {@link ZipEntrySource}.
     */
    public static ZipEntrySource[] toZipEntrySource(List<Object> resourcesToAddToTheZip) {
        return toZipEntrySource(resourcesToAddToTheZip.toArray());
    }

    /**
     * Method to convert a array of objects to a array of ZipEntrySource.
     * @param resourcesToAddToTheZip the array of {@link Object} to convert.
     * @return the array of {@link ZipEntrySource}.
     */
    @SuppressWarnings("unchecked")
    public static ZipEntrySource[] toZipEntrySource(Object[] resourcesToAddToTheZip){
        Map<String, Object> map = new MultiKeyMap();
        for(Object obj : resourcesToAddToTheZip){
            map.put("",obj);
        }
        return toZipEntrySource(map);
    }

    /**
     * Method to convert a collection of objects to a array of ZipEntrySource.
     * @param resourcesToAddToTheZip the {@link Map} of element to convert.
     * @return the array of {@link ZipEntrySource}.
     */
    public static ZipEntrySource[] toZipEntrySource(Map<String, Object> resourcesToAddToTheZip){
        List<ZipEntrySource> entries = new ArrayList<>();
        for(Map.Entry<String,Object> entry : resourcesToAddToTheZip.entrySet()){
            if(entry.getValue() instanceof File){
                entries.add(new FileSource(entry.getKey(),(File) entry.getValue()));
            }else if(entry.getValue() instanceof Path){
                entries.add(new FileSource(entry.getKey(),((Path)entry.getValue()).toFile()));
            }else if(entry.getValue() instanceof URI){
                entries.add(new FileSource(entry.getKey(),new File(((URI)entry.getValue()))));
            }else if(entry.getValue() instanceof URL){
                try {
                    entries.add(new FileSource(entry.getKey(),new File(((URL)entry.getValue()).toURI())));
                } catch (URISyntaxException e) {
                    logger.warn("Some Problem we ignore this input:"+e.getMessage());
                }
            }else if(entry.getValue() instanceof InputStream){
                try {
                    entries.add(new ByteSource(entry.getKey(),IOUtils.toByteArray((InputStream)entry.getValue())));
                } catch (IOException e) {
                    logger.warn("Some Problem we ignore this input:"+e.getMessage());
                }
            }else if(entry.getValue() instanceof byte[]){
                entries.add(new ByteSource(entry.getKey(),(byte[]) entry.getValue()));
            }else if(entry.getValue() instanceof String){
                entries.add(new ByteSource(entry.getKey(),((String) entry.getValue()).getBytes()));
            }else{
                //other input to set
                logger.warn("The input type:"+entry.getValue().getClass().getName()+" is not supported");
            }
        }
        return entries.toArray(new ZipEntrySource[entries.size()]);
    }

    /**
     * Method for extract all the files in many directories in a Zip file.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @param output the {@link File} output of the extraction e.g. /tmp/demo .
     * @return the {@link List} of {@link File} extracted.
     */
    public static List<File> extractAllFromZip(File fileZip,File output){
        return getFilesFromDirectory(extractDirectoryFromZip(fileZip,output));
    }

    /**
     * Method for extract all the files in many directories in a Zip file.
     * @param fileZip the {@link File} of the zip e.g. /tmp/demo.zip .
     * @return the {@link List} of {@link File} extracted.
     */
    public static List<File> extractAllFromZip(File fileZip){
        return getFilesFromDirectory(extractDirectoryFromZip(fileZip));
    }

    //Enumerator (to complete)

    public enum TransformStringType {UPERCASE,LOWERCASE}

    //Some private methods

    private static File createTempDirectory(){
        File dir = new File("/tmp/"+ generateAlphaNumericString(6)+"/");
        dir.mkdir();
        dir.deleteOnExit();
        return dir;
    }

    private static File createTempFile(byte[] bytes){  
        try {
            //file with no extenstion and no directory is saved on C:\\temp\\file.tmp
             Path tempFile = Files.createTempFile(generateAlphaNumericString(6), null);
            Files.write(tempFile,bytes,StandardOpenOption.DELETE_ON_CLOSE);
            return  tempFile.toFile();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    private static File createTempFile(String filename){
        try {
            //file with no extenstion and no directory is saved on C:\\temp\\file.tmp
            Path tempFile = Files.createTempFile(filename, null);
            return  tempFile.toFile();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method simple to generate a alphanumerical String.
     * @param length the {@link Integer} length of the String.
     * @return the {@link String} generate.
     */
    @SuppressWarnings("Duplicates")
    private static String generateAlphaNumericString(int length){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (new Random().nextFloat() * (rightLimit - leftLimit));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    /**
     * Method to read all file in a directory/folder recursively.
     *
     * @param directory the {@link File} directory/folder.
     * @return the  {@link List} of {@link File} in the directory.
     */
    private static List<File> getFilesFromDirectory(File directory) {
        List<File> files = new ArrayList<>();
        try {
            Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    files.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Listing files in directory: {}", directory, e);
        }
        return files;
    }






























}
