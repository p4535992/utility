package com.github.p4535992.util.log;

import java.io.File;
import java.io.IOException;

/**
 * Created by 4535992 on 05/11/2015.
 */
public class Test_SystemLog {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws IOException {
        SystemLog log = new SystemLog();
        System.out.println("Test 45");
        System.err.println("Test 42");
        log.close();
        SystemLog.setIsPRINT(true);
        log = new SystemLog("aaaaaa","txt");
        System.out.println("Test 45");
        System.err.println("Test 42");

        try {
            // Start capturing characters into the log file.
            PrintLog.start(System.getProperty("user.dir") + File.separator + "bbbbsamplelog.txt");
            // The next three lines should appear BOTH on the console and in the log file.
            System.out.println("Here's is some stuff to stdout.");
            System.err.println( "Here's is some stuff to stderr.");
            System.out.println("Let's throw an exception...");
            //new Exception().printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Turn off logging
            PrintLog.stop();
        }
    }
}
