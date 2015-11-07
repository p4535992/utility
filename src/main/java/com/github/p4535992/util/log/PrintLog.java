package com.github.p4535992.util.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by 4535992 on 05/11/2015.
 */
public class PrintLog extends PrintStream {

    private static SimpleDateFormat logTimestamp = new SimpleDateFormat("[HH:mm:ss]");



    //-----------------------------------------------
    //Constructor
    //-----------------------------------------------

    public PrintLog(PrintStream out) {
        super(out);
    }

    public PrintLog(OutputStream out) {
        super(out, false);
    }

    public PrintLog(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public PrintLog(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out,autoFlush,encoding);
    }

    public PrintLog(String fileName) throws FileNotFoundException {
        this(new FileOutputStream(fileName),false);
    }

    public PrintLog(String fileName,String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(new FileOutputStream(fileName), false, csn);
    }

    public PrintLog(File file) throws FileNotFoundException {
        super(new FileOutputStream(file), false);
    }

    public PrintLog(File file, String csn)throws FileNotFoundException, UnsupportedEncodingException {
        super(new FileOutputStream(file), false, csn );
    }

    //------------------------------------
    // SETTER
    //------------------------------------

    public static File setFile(String fileName){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return new File(System.getProperty("user.dir")+File.separator +  fileName + "_" +  timeStamp + ".log");
    }

    public static String setName(String fileName){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return System.getProperty("user.dir")+File.separator +  fileName + "_" +  timeStamp + ".log";
    }

    /*private OutputStream setStream(String fileName) throws FileNotFoundException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return new FileOutputStream(
                new File(System.getProperty("user.dir")+File.separator +  fileName + "_" +  timeStamp + ".log"));
    }*/

    //------------------------------------
    //Methods
    //------------------------------------

    public void flush() {super.flush();}
    public void close() {
        // Restore the original standard output and standard error.
        // Then close the log file.
        System.setOut(oldStdout);
        System.setErr(oldStderr);
        try {
            logStreamFile.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.close();
    }
    public boolean checkError() {return super.checkError();}

    public void write(int b) {
        if(logStreamFile!=null) {
            try {
                logStreamFile.write(b);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                setError();
            }
        }
        super.write(b);
    }

    public void write(byte buf[], int off, int len) {
        if(logStreamFile!=null) {
            try {
                logStreamFile.write(buf, off, len);
            } catch (Exception e) {
                e.printStackTrace();
                setError();
            }
        }
        super.write(buf,off,len);
    }

    //---------------------------------------------------
    // Methods that do not terminate lines
    //---------------------------------------------------

    public void print(boolean b) {
        super.print(b);
    }
    public void print(char c) {
        super.print(c);
    }
    public void print(int i) {
        super.print(i);
    }
    public void print(long l) {
        super.print(l);
    }
    public void print(float f) {
        super.print(f);
    }
    public void print(double d) {super.print(d);}
    public void print(char s[]) {super.print(s);}
    public void print(String s) { super.print(s);}
    public void print(Object obj) {
        super.print(obj);
    }

    //---------------------------------------------------
    /* Methods that do terminate lines */
    //---------------------------------------------------

    public void println() {super.println();}
    public void println(boolean x) {super.println(x);}
    public void println(char x) { super.println(x);}
    public void println(int x) {super.println(x);}
    public void println(long x) {super.println(x); }
    public void println(float x) {super.println(x); }
    public void println(double x) {super.println(x); }
    public void println(char x[]) {super.println(x); }
    public void println(String x) {super.println(x); }
    public void println(Object x) { super.println(x); }

    public PrintLog printf(String format, Object ... args) {
        return (PrintLog) super.printf(format, args);
    }
    public PrintLog printf(Locale l, String format, Object ... args) {
        return (PrintLog) super.printf(l,format, args);
    }
    public PrintLog format(String format, Object ... args) {
        return (PrintLog) super.format(format, args);
    }
    public PrintLog format(Locale l, String format, Object ... args) {
        return (PrintLog) super.format(l,format, args);
    }
    public PrintLog append(CharSequence csq) {return (PrintLog) super.append(csq);}
    public PrintLog append(CharSequence csq, int start, int end) {return (PrintLog) super.append(csq,start,end);}
    public PrintLog append(char c) {return (PrintLog) super.append(c); }

    //------------------------------------------------------------------------
    //New Methods
    //------------------------------------------------------------------------

    static PrintStream oldStdout;
    static PrintStream oldStderr;
    static OutputStream logStreamFile;

    public static OutputStream getLogStreamFile() {
        return logStreamFile;
    }

    public static void setLogStreamFile(OutputStream logStreamFile) {
        PrintLog.logStreamFile = logStreamFile;
    }

    /**
     * Now all data written to System.out should be redirected into the file
     * "c:\\data\\system.out.txt". Keep in mind though, that you should make
     * sure to flush System.out and close the file before the JVM shuts down,
     * to be sure that all data written to System.out is actually flushed to the file.
     */
    public static void start(){
        // Save current settings for later restoring.
        oldStdout = System.out;
        oldStderr = System.err;
        SystemLog multiOut = new SystemLog(System.out);
        SystemLog multiErr = new SystemLog(System.err);
        PrintLog stdout= new PrintLog(multiOut);
        PrintLog stderr= new PrintLog(multiErr);
        System.setOut(stdout);
        System.setErr(stderr);
    }

    public static void start(String fileName) throws FileNotFoundException {
        oldStdout = System.out;
        oldStderr = System.err;
        logStreamFile = new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(fileName)));
        SystemLog multiOut = new SystemLog(System.out,logStreamFile);
        SystemLog multiErr = new SystemLog(System.err,logStreamFile);
        PrintLog stdout= new PrintLog(multiOut);
        PrintLog stderr= new PrintLog(multiErr);
        System.setOut(stdout);
        System.setErr(stderr);
    }

    public static void start(File file) throws FileNotFoundException {
        // Save current settings for later restoring.
        oldStdout = System.out;
        oldStderr = System.err;
        logStreamFile = new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(file)));
        SystemLog multiOut = new SystemLog(System.out,logStreamFile);
        SystemLog multiErr = new SystemLog(System.err,logStreamFile);
        PrintLog stdout= new PrintLog(multiOut);
        PrintLog stderr= new PrintLog(multiErr);
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     *  Ceases logging and restores the original settings.
     */
    public static void stop(){
        // Restore the original standard output and standard error.
        // Then close the log file.
        System.setOut(oldStdout);
        System.setErr(oldStderr);
        try {
            if (logStreamFile != null) {
                logStreamFile.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    /** A sample test driver.  The file samplelog.txt should contain
     everything that appeared on the console output.
     */
    public static void main(String[] args) {
        try {
            // Start capturing characters into the log file.
            PrintLog.start(new File(System.getProperty("user.dir") + File.separator + "bbbbsamplelog.txt"));
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
