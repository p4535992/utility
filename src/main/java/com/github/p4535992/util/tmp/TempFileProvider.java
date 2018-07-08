package com.github.p4535992.util.tmp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class TempFileProvider {
    private static final int BUFFER_SIZE = 40 * 1024;

    /** 
     * subdirectory in the temp directory where Alfresco temporary files will go 
     */
    public static final String TEMP_FILE_DIR = "tmpDir";
    
    /**
     * The prefix for the long life temporary files.
     */
    public static final String LONG_LIFE_FILE_DIR = "tmpDir-longLife";

    /** the system property key giving us the location of the temp directory */
    public static final String SYSTEM_KEY_TEMP_DIR = "java.io.tmpdir";

    private static final Logger logger = Logger.getLogger(TempFileProvider.class.getName());
    
    private static int MAX_RETRIES = 3;

    /**
     * Static class only
     */
    private TempFileProvider()
    {
    }

    /**
     * Get the Java Temp dir e.g. java.io.tempdir
     * 
     * @return Returns the system temporary directory i.e. <code>isDir == true</code>
     */
    public static File getSystemTempDir()
    {
        String systemTempDirPath = System.getProperty(SYSTEM_KEY_TEMP_DIR);
        if (systemTempDirPath == null)
        {
            throw new IllegalArgumentException("System property not available: " + SYSTEM_KEY_TEMP_DIR);
        }
        File systemTempDir = new File(systemTempDirPath);
//        if (logger.isDebugEnabled())
        {
            logger.finer("Created system temporary directory: " + systemTempDir);
        }
        return systemTempDir;
    }
    
    /**
     * Get the Alfresco temp dir, by defaut %java.io.tempdir%/Alfresco.  
     * Will create the temp dir on the fly if it does not already exist.
     * 
     * @return Returns a temporary directory, i.e. <code>isDir == true</code>
     */
    public static File getTempDir()
    {
        return getTempDir(TEMP_FILE_DIR);
    }
    
    /**
     * Get the specified temp dir, %java.io.tempdir%/dirName.  
     * Will create the temp dir on the fly if it does not already exist.
     * 
     * @param dirName the name of sub-directory in %java.io.tempdir%
     * 
     * @return Returns a temporary directory, i.e. <code>isDir == true</code>
     */
    public static File getTempDir(String dirName)
    {
        File systemTempDir = getSystemTempDir();
        // append the Alfresco directory
        File tempDir = new File(systemTempDir, dirName);
        // ensure that the temp directory exists
        if (tempDir.exists())
        {
            // nothing to do
        }
        else
        {
            // not there yet
            if (!tempDir.mkdirs())
            {
                // We didn't create it but perhaps it was made by some other thread
                if (!tempDir.exists())
                {
                    // It's definitely not there
                    throw new IllegalArgumentException("Failed to create temp directory: " + tempDir);
                }
            }
            else
            {
                // This thread created it
//                if (logger.isDebugEnabled())
                {
                	logger.finer("Created temp directory: " + tempDir);
                }
            }
        }
        // done
        return tempDir;
    }
    
    /**
     * creates a longer living temp dir.   Files within the longer living 
     * temp dir will not be garbage collected as soon as "normal" temporary files.
     * By default long life temp files will live for for 24 hours rather than 1 hour.
     * <p>
     * Code using the longer life temporary files should be careful to clean up since 
     * abuse of this feature may result in out of memory/disk space errors.
     * @param key can be blank in which case the system will generate a folder to be used by all processes
     * or can be used to create a unique temporary folder name for a particular process.  At the end of the process 
     * the client can simply delete the entire temporary folder.  
     * @return the long life temporary directory
     */
    public static File getLongLifeTempDir(String key)
    {
        /**
         * Long life temporary directories have a prefix at the start of the 
         * folder name.
         */
        String folderName = LONG_LIFE_FILE_DIR + "_" + key;
        
        File tempDir = getTempDir();
        
        // append the Alfresco directory
        File longLifeDir = new File(tempDir, folderName);
        // ensure that the temp directory exists
        
        if (longLifeDir.exists())
        {
//            if (logger.isDebugEnabled())
            {
            	logger.finer("Already exists: " + longLifeDir);
            }
            // nothing to do
            return longLifeDir;
        }
        else
        {
            /**
             * We need to create a temporary directory
             * 
             * We may have a race condition here if more than one thread attempts to create 
             * the temp dir.
             *  
             * mkdirs can't be synchronized
             * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4742723
             */
            for(int retry = 0; retry < MAX_RETRIES; retry++)
            {
                boolean created = longLifeDir.mkdirs();
            
                if (created)
                {
                    // Yes we created the temp dir
//                    if (logger.isDebugEnabled())
                    {
                    	logger.finer("Created long life temp directory: " + longLifeDir);
                    }
                    return longLifeDir;
                }
                else
                {   
                    if(longLifeDir.exists())
                    {
                        // created by another thread, but that's O.K.  
//                        if (logger.isDebugEnabled())
                        {
                        	logger.finer("Another thread created long life temp directory: " + longLifeDir);
                        }
                        return longLifeDir;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Failed to create temp directory: " + longLifeDir);
    }

    public static File createTempFile(InputStream in, String namePrefix, String nameSufix) throws Exception
    {
        if (null == in)
        {
            return null;
        }

        File file = createTempFile(namePrefix, nameSufix);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
        try
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int i;
            while ((i = in.read(buffer)) > -1)
            {
                out.write(buffer, 0, i);
            }
        }
        catch (Exception e)
        {
            file.delete();
            throw e;
        }
        finally
        {
            in.close();
            out.flush();
            out.close();
        }

        return file;
    }

    /**
     * Is this a long life folder ?
     * @param file
     * @return true, this is a long life folder.
     */
    protected static boolean isLongLifeTempDir(File file)
    {
        if(file.isDirectory())
        {
            if(file.getName().startsWith(LONG_LIFE_FILE_DIR))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    /**
     * Create a temp file in the alfresco temp dir.
     * 
     * @return Returns a temp <code>File</code> that will be located in the
     *         <b>Alfresco</b> subdirectory of the default temp directory
     * 
     * @see #ALFRESCO_TEMP_FILE_DIR
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File createTempFile(String prefix, String suffix)
    {
        File tempDir = TempFileProvider.getTempDir();
        // we have the directory we want to use
        return createTempFile(prefix, suffix, tempDir);
    }

    /**
     * @return Returns a temp <code>File</code> that will be located in the
     *         given directory
     * 
     * @see #ALFRESCO_TEMP_FILE_DIR
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File createTempFile(String prefix, String suffix, File directory)
    {
        try
        {
            File tempFile = File.createTempFile(prefix, suffix, directory);
//            if (logger.isDebugEnabled())
            {
            	logger.finer("Creating tmp file: " + tempFile);
            }
            return tempFile;
        } catch (IOException e)
        {
            throw new IllegalArgumentException("Failed to created temp file: \n" +
                    "   prefix: " + prefix + "\n"
                    + "   suffix: " + suffix + "\n" +
                    "   directory: " + directory,
                    e);
        }
    }
}
