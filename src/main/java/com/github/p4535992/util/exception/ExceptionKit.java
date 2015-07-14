package com.github.p4535992.util.exception;

import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 14/07/2015.
 * @author 4535992,
 * @version 2015-07-14.
 */
@SuppressWarnings("unused")
public class ExceptionKit extends  RuntimeException{

    private static final long serialVersionUID = 3L;
    public ExceptionKit() {}

    public ExceptionKit(String message) {
        super(message);
    }

    public ExceptionKit(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionKit(Throwable e) {
        super(e);
    }

    public ExceptionKit(Exception e){ super(new Throwable(e));}


    private static void printStackTrace(Exception e){
        StackTraceElement elements[] = e.getStackTrace();
        for (StackTraceElement element : elements) {
            System.err.println(element.getFileName()
                    + ":" + element.getLineNumber()
                    + ">> "
                    + element.getMethodName() + "()");
        }
    }

    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace(java.io.PrintStream s) {
        s.flush();
        super.printStackTrace(s);
        Throwable cause = getCause();
        if (cause != null) {
            s.print("Caused by:\n");
            cause.printStackTrace(s);
        }
    }

    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        s.flush();
        super.printStackTrace(s);
        Throwable cause = getCause();
        if (cause != null) {
            s.print("Caused by:\n");
            cause.printStackTrace(s);
        }
    }

    /**
     * Method to generate a Out of Memory Exception.
     */
    public static void generateOutOfMemory(){
        //System.out.println("Initial freeMemory: " + Runtime.getRuntime().freeMemory() / (1024 * 1024));
        List<String> myList = new ArrayList<>();
        for(long i=0; ; i++) {
            myList.add(i + " - " + System.currentTimeMillis());
            if (i%100000==0) {
                System.gc();
                System.out.println("i: "+i+", freeMemory: "+Runtime.getRuntime().freeMemory()/(1024*1024));
            }
        }
    }


}
