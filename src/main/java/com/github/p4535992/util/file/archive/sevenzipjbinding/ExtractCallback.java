package com.github.p4535992.util.file.archive.sevenzipjbinding;

import net.sf.sevenzipjbinding.*;

import java.io.*;

/**
 * Created by 4535992 on 27/12/2015.
 */
public class ExtractCallback implements IArchiveExtractCallback {

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
