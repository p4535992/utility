package com.github.p4535992.util.log.test;

import com.github.p4535992.util.log.PrintLog;
import com.github.p4535992.util.log.SystemLog;

import java.io.File;
import java.io.IOException;

/**
 * Created by 4535992 on 05/11/2015.
 */
public class Test_SimpleLog {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws IOException {
        //The console output WORK!!!!
        SimpleLog log = new SimpleLog();
        System.out.println("Test 45");
        System.err.println("Test 42");
        log.close();

        //The console output work but the messeges is not print on the file....
        log = new SimpleLog("aaaaaa","txt");
        System.out.println("Test 45");
        System.err.println("Test 42");
        log.close();
        //WHY THIS WORK????
        //The same code i recall directly from the SimplePrintLog print the messages on the file.
        SimplePrintLog.start(System.getProperty("user.dir") + File.separator + "bbbb.txt");
        System.out.println("Why...");
        System.err.println("..this..");
        System.out.println("..work????");
        //new Exception().printStackTrace();
        SimplePrintLog.stop();

    }
}
