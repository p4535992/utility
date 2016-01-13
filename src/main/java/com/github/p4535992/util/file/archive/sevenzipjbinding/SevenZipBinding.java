package com.github.p4535992.util.file.archive.sevenzipjbinding;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.file.archive.ArchiveUtilities;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 27/12/2015.
 * 7-Zip-JBinding examples: Snippet to extract all files from an archive into a specified folder on the disk.
 * Usage:
 * java -classpath ... example.ExtractExample [-t] archive-to-extract output-folder [mask] -t - test files mask -
 * match files to extract, for example, *.txt
 */
@SuppressWarnings("unused")
public class SevenZipBinding extends ArchiveUtilities{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SevenZipBinding.class);

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
    public SevenZipBinding(String archive, String outputDirectory, boolean test, String filter) {
        this.archive = archive;
        this.outputDirectory = outputDirectory;
        this.test = test;
        this.filterRegex = filterToRegex(filter);
    }

    public SevenZipBinding(String archive, String outputDirectory) {
        this.archive = archive;
        this.outputDirectory = outputDirectory;
    }

    public SevenZipBinding() {}

    void extract() throws ExtractionException {
        checkArchiveFile();
        prepareOutputDirectory();
        extractArchive();
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

    public Boolean extractArchive() throws ExtractionException {
        return extractArchive(archive);
    }

    public Boolean extractArchive(String fileArchivePath) throws ExtractionException {
        return extractArchive(new File(fileArchivePath));
    }

    public Boolean extractArchive(File archive) throws ExtractionException {
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
            if (filterRegex != null) {
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
     */
    public boolean extracArchive(Path path) throws ExtractionException {
        logger.info("Extracting:"+ path);
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
                        pathArchiveFile = FileUtilities.toPath(outputData, pathArchiveFile);
                        if (result != ExtractOperationResult.OK) return false;
                        if (pathArchiveFile == null) return false;
                        filesListInZip.add(pathArchiveFile.toFile());
                    }
                }finally{
                    if (inArchive != null) {
                        inArchive.close();
                    }
                    randomAccessFile.close();
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

}
