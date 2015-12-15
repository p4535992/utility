package com.github.p4535992.util.log.systemlog;

import com.github.p4535992.util.log.SystemLog;

import java.io.File;
import java.io.IOException;

/**
 * Created by 4535992 on 05/11/2015.
 */
public class Test_SystemLog {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws IOException {
        SystemLog log;
        /*log = SystemLog.start();
        System.out.println("Test 45");
        System.err.println("Test 42");
        SystemLog.message("Message 1");
        SystemLog.warning("Warning 1");
        log.close();*/

        log = SystemLog.start("aaaaaa","txt");
        System.out.println("Test 45");
        System.err.println("Test 42");
        SystemLog.message("Message 1");
        SystemLog.warning("Warning 1");
        log.close();

        SystemLog.setIsLog4j(true);
        log = SystemLog.startWithLog4J(
                new File(System.getProperty("user.dir") + File.separator + "cccccc"),
                new File(System.getProperty("user.dir") + File.separator +
                                "utility\\src\\main\\java\\com\\github\\p4535992\\util\\log\\logging_file\\test_log4j.xml")
                );
        System.out.println("Test 45");
        System.err.println("Test 42");
        SystemLog.message("Message 1");
        SystemLog.warning("Warning 1");
        log.close();

    }
}
