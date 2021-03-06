package com.github.p4535992.util.file.archive.sevenzipjbinding;

import com.github.p4535992.util.file.archive.ArchiveUtilities;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ext.com.google.common.io.Files;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 27/12/2015.+
 * https://gist.github.com/borisbrodski/6120309
 * http://sourceforge.net/p/sevenzipjbind/discussion/757964/thread/b64a36fb/
 * 7-Zip-JBinding examples: Snippet to extract all files from an archive into a specified folder on the disk.
 * 
 * Usage:
 * java -classpath ... example.ExtractExample [-t] archive-to-extract output-folder [mask] -t - test files mask -
 * match files to extract, for example, *.txt
 */
@SuppressWarnings("unused")
public class SevenZipUtilities extends ArchiveUtilities{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SevenZipUtilities.class);

    private String archive;
    private String outputDirectory;
    private File outputDirectoryFile;
    private boolean test;
    private String filterRegex;

    private static List<File> filesListInZip = new ArrayList<>();

    public static List<File> getFilesListInZip() {
        return filesListInZip;
    }

    /**
     *
     * @param archive the String path to the Archive.
     * @param outputDirectory the String path to the directory output of the extraction.
     * @param test if true abilitate the test extraction, no phisical extraction of the archive.
     * @param filter the String pattern for the file to extract e.g. "*.txt"
     */
    public SevenZipUtilities(String archive, String outputDirectory, boolean test, String filter) {
        this.archive = archive;
        this.outputDirectory = outputDirectory;
        this.test = test;
        this.filterRegex = filterToRegex(filter);
    }

    /**
     *
     * @param archive the String path to the Archive.
     * @param outputDirectory the String path to the directory output of the extraction.
     * @param test if true abilitate the test extraction, no phisical extraction of the archive.
     */
    public SevenZipUtilities(String archive, String outputDirectory, boolean test) {
        this.archive = archive;
        this.outputDirectory = outputDirectory;
        this.test = test;
    }

    public SevenZipUtilities(String archive, String outputDirectory) {
        this.archive = archive;
        this.outputDirectory = outputDirectory;
    }

    protected SevenZipUtilities() {}

    private void extractAll() throws ExtractionException {
        checkArchiveFile();
        prepareOutputDirectory();
        extractAllFromArchive();
    }

    private void prepareOutputDirectory() throws ExtractionException {
        outputDirectoryFile = new File(outputDirectory);
        if (!outputDirectoryFile.exists()) {
            boolean mkdirs = outputDirectoryFile.mkdirs();
        } else {
            if (outputDirectoryFile.list().length != 0) {
                throw new ExtractionException("Output directory not empty: "
                        + outputDirectory);
            }
        }
    }

    private void checkArchiveFile() throws ExtractionException {
        if (!new File(archive).exists()) {
            throw new ExtractionException("Archive file not found: " + archive);
        }
        if (!new File(archive).canRead()) {
            logger.warn("Can't read archive file: " + archive);
        }
    }

    /**
     * href: http://stackoverflow.com/questions/21897286/how-to-extract-files-from-a-7-zip-stream-in-java-without-store-it-on-hard-disk
     * @param zipFile the {@link File} of the Zip.
     * @return the {@link List} of {@link File} into to the Zip. 
     */
    public List<File> extractFilesFromArchive(File zipFile){
        List<File> files = new ArrayList<>();
        try {
            try (SevenZFile sevenZFile = new SevenZFile(zipFile)) {
                SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                while (entry != null) {
                    File file = new File(zipFile.getAbsolutePath()+File.separator+entry.getName());
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        byte[] content = new byte[(int) entry.getSize()];
                        sevenZFile.read(content, 0, content.length);
                        out.write(content);
                    }
                    entry = sevenZFile.getNextEntry();
                    files.add(file);
                }
            }
        }catch(IOException e){
            logger.error(e.getMessage(),e);
        }
        return files;
    }

    public Boolean extractAllFromArchive() throws ExtractionException {
        return extractAllFromArchive(archive);
    }

    public Boolean extractAllFromArchive(String fileArchivePath) throws ExtractionException {
        return extractAllFromArchive(new File(fileArchivePath));
    }

    public Boolean extractAllFromArchive(File archive) throws ExtractionException {
        if(!archive.exists()) return false;
        long time = System.currentTimeMillis();
        logger.info("Extracting the archive file "+ archive.getAbsolutePath()+"...");
        RandomAccessFile randomAccessFile;
        boolean ok = false;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
        } catch (FileNotFoundException e) {
            throw new ExtractionException("File not found", e);
        }
        try {
            if(outputDirectoryFile == null)outputDirectoryFile = new File(".");
            extractArchive(randomAccessFile,outputDirectoryFile,archive.getAbsolutePath());
            ok = true;
        } finally {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                if (ok) {
                    throw new ExtractionException("Error closing archive file",e);
                }
            }
        }
        logger.info("Time to extract '" + archive.getAbsolutePath() + "' to memory: " + (System.currentTimeMillis() - time) + "ms");
        return true;
    }

   /* public void extractArchive(String archive, String outputDirectoryFile,boolean test) throws IOException {
        IInArchive inArchive = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(new File(archive), "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            inArchive.extract(null, test, new ExtractCallback(inArchive, outputDirectoryFile));
        } finally {
            if (inArchive != null) {
                inArchive.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }*/

    private String filterToRegex(String filter) {
        if (filter == null) return null;
        return "\\Q" + filter.replace("*", "\\E.*\\Q") + "\\E";
    }

    private Boolean extractArchive(RandomAccessFile file,File outputDirectoryFile,String archive)throws ExtractionException {
        IInArchive inArchive;
        boolean ok = false;
        try {
            inArchive = SevenZip.openInArchive(null,new RandomAccessFileInStream(file));
        } catch (SevenZipException e) {
            throw new ExtractionException("Error opening archive", e);
        }
        try {

            int[] ids = null; // All items
            if (filterRegex != null && !filterRegex.isEmpty()) {
                ids = filterIds(inArchive, filterRegex);
            }
            inArchive.extract(ids, test, new ExtractCallback(inArchive,outputDirectoryFile));
            ok = true;
        } catch (SevenZipException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error extracting archive '");
            stringBuilder.append(archive);
            stringBuilder.append("': ");
            stringBuilder.append(e.getMessage());
            if (e.getCause() != null) {
                stringBuilder.append(" (");
                stringBuilder.append(e.getCause().getMessage());
                stringBuilder.append(')');
            }
            String message = stringBuilder.toString();

            throw new ExtractionException(message, e);
        } finally {
            try {
                inArchive.close();
            } catch (SevenZipException e) {
                if (ok) {
                    logger.error("Error closing archive", e);
                }
            }
        }
        return true;
    }

    private int[] filterIds(IInArchive inArchive, String regex) throws SevenZipException {
        List<Integer> idList = new ArrayList<>();

        int numberOfItems = inArchive.getNumberOfItems();

        Pattern pattern = Pattern.compile(regex);
        for (int i = 0; i < numberOfItems; i++) {
            String path = (String) inArchive.getProperty(i, PropID.PATH);
            String fileName = new File(path).getName();
            if (pattern.matcher(fileName).matches()) {
                idList.add(i);
            }
        }

        int[] result = new int[idList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = idList.get(i);
        }
        return result ;
    }

    /*public static void main(String[] args) {
        boolean test = false;
        String filter = null;
        List<String> argList = new ArrayList<String>(Arrays.asList(args));
        if (argList.size() > 0 && argList.get(0).equals("-t")) {
            argList.remove(0);
            test = true;
        }
        if (argList.size() != 2 && argList.size() != 3) {
            System.out.println("Usage:");
            System.out.println("java -cp ... example.ExtractExample [-t] <archive> <output-dir> [filter]");
            System.exit(1);
        }
        if (argList.size() == 3) {
            filter = argList.get(2);
        }
        try {
            new ExtractUtilities(argList.get(0), argList.get(1), test, filter).extract();
            System.out.println("Extraction successfull");
        } catch (ExtractionException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        //---------------------------------------------------------------------------------
        if (args.length == 0) {
            System.out.println("Usage: java SevenZipJBindingExtractor file extractPath");
            return;
        }

        new SevenZipJBindingExtractor().extract(args[0], args[1]);
    }*/

    /**
     * Method to extract the content of a Archive File.
     * href: http://www.programcreek.com/java-api-examples/index.php?source_dir=Starbound-Mod-Manager-master/src/main/java/net/krazyweb/helpers/Archive.java
     * @param path the Path object of the Archive file to extract on the specific folder.
     * @return if true all the operations are succesfull.
     * @throws com.github.p4535992.util.file.archive.sevenzipjbinding.SevenZipUtilities.ExtractionException if any error is occurred.
     */
    public boolean extractAllFromArchive(Path path) throws ExtractionException {
        logger.info("Extracting the archive file "+ path.toString()+"...");
        try {
            long time = System.currentTimeMillis();
            if (!isSupported(path, false)) return false;
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r"); 
                    IInArchive inArchive = SevenZip.openInArchive(null, 
                            new RandomAccessFileInStream(randomAccessFile))) {
                try {
                    //METHOD 1
                    //inArchive.extract(null, false, new ExtractCallback(inArchive, outputDirectoryFile));

                    //METHOD 2
                    for (ISimpleInArchiveItem item : inArchive.getSimpleInterface().getArchiveItems()) {
                        //final ArchiveFile file = new ArchiveFile();
                        //file.setPath(Paths.get(item.getPath()));
                        Path pathArchiveFile = Paths.get(item.getPath());

                        if (item.isFolder()) {
                            logger.debug("{}", item.getPath());
                            filesListInZip.add(pathArchiveFile.toFile());
                            continue;
                        }
                        ExtractOperationResult result;
                        final byte[] outputData = new byte[item.getSize().intValue()];
                        result = item.extractSlow(new ISequentialOutStream() {
                            int offset = 0;

                            @Override
                            public int write(byte[] data) throws SevenZipException {
                                System.arraycopy(data, 0, outputData, offset, data.length);
                                offset += data.length;
                                return data.length;
                            }
                        });
                        FileUtils.writeByteArrayToFile(pathArchiveFile.toFile(),outputData);
                        if (result != ExtractOperationResult.OK) return false;
                        if (pathArchiveFile == null) return false;
                        filesListInZip.add(pathArchiveFile.toFile());
                    }
                }finally{
                    /*
                    if (inArchive != null)inArchive.close();             
                    randomAccessFile.close();
                    */
                }
            } catch (FileNotFoundException e) {
                throw new ExtractionException("File not found", e);
            }
            logger.info("Time to extract '" + path + "' to memory: " + (System.currentTimeMillis() - time) + "ms");
            return true;
        } catch (IOException  e) {
            logger.error("Extracting archive: "+ path, e);
            return false;
        }

    }

    /**
     * Created by 4535992 on 27/12/2015.
     */
    static class ExtractionException extends Exception {
        private static final long serialVersionUID = -5108931481040742838L;

        public ExtractionException(String msg) {
            super(msg);
        }

        public ExtractionException(String msg, Exception e) {
            super(msg, e);
        }
    }
    
    /**
     * Created by 4535992 on 27/12/2015.
     */
    private static class ExtractCallback implements IArchiveExtractCallback {

        private static final org.slf4j.Logger logger =
                org.slf4j.LoggerFactory.getLogger(ExtractCallback.class);

        //private net.sf.sevenzipjbinding.ISevenZipInArchive inArchive;
        private IInArchive inArchive;
        private File outputDirectoryFile;
        private int index;
        private OutputStream outputStream;
        private File file;
        private ExtractAskMode extractAskMode;
        private boolean isFolder;

        public ExtractCallback(IInArchive inArchive,File outputDirectoryFile) {
            this.inArchive = inArchive;
            this.outputDirectoryFile = outputDirectoryFile;
        }

       /* private final IInArchive inArchive;
        private final String extractPath;*/

        public ExtractCallback(IInArchive inArchive, String outputDirectoryFile) {
            this.inArchive = inArchive;
            this.outputDirectoryFile =
                    new File(outputDirectoryFile.endsWith(File.separator) ?
                            outputDirectoryFile : outputDirectoryFile+File.separator);
        }

        @Override
        public void setTotal(long total) throws SevenZipException {

        }

        @Override
        public void setCompleted(long completeValue) throws SevenZipException {

        }

        @Override
        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {

            closeOutputStream();
            this.index = index;
            this.extractAskMode = extractAskMode;
            this.isFolder = (Boolean) inArchive.getProperty(index,PropID.IS_FOLDER);

            if (extractAskMode != ExtractAskMode.EXTRACT) {
                // Skipped files or files being tested
                return null;
            }

            //SET File Path
            //String filePath = (String) inArchive.getProperty(index, PropID.PATH);
            String filePath  = inArchive.getStringProperty(index, PropID.PATH);

            file = new File(outputDirectoryFile, filePath );
            //file = new File(outputDirectoryFile + filePath);

            if (isFolder) {
                createDirectory(file);
               /* if (!file.exists()) {
                    file.createNewFile();
                }*/
                return null;
            }

            createDirectory(file.getParentFile());
            /*  if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
            }*/

            try {
                outputStream = new FileOutputStream(file,true);
            } catch (FileNotFoundException e) {
                throw new SevenZipException("Error opening file: "+ file.getAbsolutePath(), e);
            }

            return new ISequentialOutStream() {
                @Override
                public int write(byte[] data) throws SevenZipException {
                    try {
                        outputStream.write(data);
                    } catch (IOException e) {
                        throw new SevenZipException("Error writing to file: "+ file.getAbsolutePath());
                    }finally {
                        try {
                            if (outputStream != null) {
                                outputStream.flush();
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            logger.error("Could not close FileOutputStream", e);
                        }
                    }
                    return data.length; // Return amount of consumed data
                }
            };

        }

        private void createDirectory(File parentFile) throws SevenZipException {
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new SevenZipException("Error creating directory: "
                            + parentFile.getAbsolutePath());
                }
            }
        }

        private void closeOutputStream() throws SevenZipException {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    throw new SevenZipException("Error closing file: "
                            + file.getAbsolutePath());
                }
            }
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode)
                throws SevenZipException {

        }

        @Override
        public void setOperationResult(
                ExtractOperationResult extractOperationResult)
                throws SevenZipException {
            closeOutputStream();
            String path = (String) inArchive.getProperty(index, PropID.PATH);
            if (extractOperationResult != ExtractOperationResult.OK) {
                throw new SevenZipException("Invalid file: " + path);
            }

            if (!isFolder) {
                switch (extractAskMode) {
                    case EXTRACT:
                        logger.info("Extracted " + path);
                        break;
                    case TEST:
                        logger.info("Tested " + path);
                        break;
                    default:
                        logger.warn("No Extract Mode setted");
                }
            }
        }

    }


}
