package com.github.p4535992.util.tmp;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempFileCleaner extends Thread {
    private int protectHours = 1;
    
    private static final Logger logger = Logger.getLogger(TempFileCleaner.class.getName());
    
    public TempFileCleaner() { }
    
    public void run() {
    	while(true) {
			doJob();
			try {
				Thread.sleep(1000 * 60 * 60); //1 ora
			} catch (InterruptedException e) {
				logger.info("TempFileCleaner Interrupted!");
				break;
			}
		}
    }
    
    public void close() {
		logger.log(Level.INFO,"Chiusura TempFileCleaner Checker...");
		this.interrupt();
	}

    /**
     * Gets a list of all files in the {@link TempFileProvider#ALFRESCO_TEMP_FILE_DIR temp directory}
     * and deletes all those that are older than the given number of hours.
     */
    public void doJob() {
        // get the number of hours to protect the temp files
        if (protectHours < 0 || protectHours > 8760)
        {
            throw new IllegalArgumentException("Hours to protect temp files must be 0 <= x <= 8760");
        }

        String directoryName = TempFileProvider.TEMP_FILE_DIR;
        
        long now = System.currentTimeMillis();
        long aFewHoursBack = now - (3600L * 1000L * protectHours);
        
        long aLongTimeBack = now - (24 * 3600L * 1000L);
        
        File tempDir = TempFileProvider.getTempDir(directoryName);
        int count = removeFiles(tempDir, aFewHoursBack, aLongTimeBack, false);  // don't delete this directory
        // done
//        if (logger.isDebugEnabled())
        {
            logger.log(Level.FINER,"Removed " + count + " files from temp directory: " + tempDir);
        }
    }
    
    /**
     * Removes all temporary files created before the given time.
     * <p>
     * The delete will cascade down through directories as well.
     * 
     * @param removeBefore only remove files created <b>before</b> this time
     * @return Returns the number of files removed
     */
    public static int removeFiles(long removeBefore)
    {
        File tempDir = TempFileProvider.getTempDir();
        return removeFiles(tempDir, removeBefore, removeBefore, false);
    }
    
    /**
     * @param directory the directory to clean out - the directory will optionally be removed
     * @param removeBefore only remove files created <b>before</b> this time
     * @param removeDir true if the directory must be removed as well, otherwise false
     * @return Returns the number of files removed
     */
    private static int removeFiles(File directory, long removeBefore, long longLifeBefore, boolean removeDir)
    {
        if (!directory.isDirectory())
        {
            throw new IllegalArgumentException("Expected a directory to clear: " + directory);
        }
        // check if there is anything to to
        if (!directory.exists())
        {
            return 0;
        }
        // list all files
        File[] files = directory.listFiles();
        int count = 0;
        for (File file : files)
        {
            if (file.isDirectory())
            {
                if(TempFileProvider.isLongLifeTempDir(file))
                {
                    // long life for this folder and its children
                    int countRemoved = removeFiles(file, longLifeBefore, longLifeBefore, true);  
//                    if (logger.isDebugEnabled())
                    {
                    	logger.log(Level.FINER,"Removed " + countRemoved + " files from temp directory: " + file);
                    }
                }
                else
                {
                    // enter subdirectory and clean it out and remove itsynetics
                    int countRemoved = removeFiles(file, removeBefore, longLifeBefore, true);
//                    if (logger.isDebugEnabled())
                    {
                    	logger.log(Level.FINER,"Removed " + countRemoved + " files from directory: " + file);
                    }
                }
            }
            else
            {
                // it is a file - check the created time
                if (file.lastModified() > removeBefore)
                {
                    // file is not old enough
                    continue;
                }
                // it is a file - attempt a delete
                try
                {
//                    if(logger.isDebugEnabled())
                    {
                    	logger.log(Level.FINER,"Deleting temp file: " + file);
                    }
                    file.delete();
                    count++;
                }
                catch (Throwable e)
                {
                    logger.info("Failed to remove temp file: " + file);
                }
            }
        }
        // must we delete the directory we are in?
        if (removeDir)
        {
            // the directory must be removed if empty
            try
            {
                File[] listing = directory.listFiles();
                if(listing != null && listing.length == 0)
                {
                    // directory is empty
//                    if(logger.isDebugEnabled())
                    {
                    	logger.log(Level.FINER,"Deleting empty directory: " + directory);
                    }
                    directory.delete();
                }
            }
            catch (Throwable e)
            {
            	logger.log(Level.FINER,"Failed to remove temp directory: " + directory, e);
            }
        }
        // done
        return count;
    }
}
