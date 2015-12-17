package com.github.p4535992.util.log.test;

import com.github.p4535992.util.log.SystemLog;

import java.io.*;
import java.util.*;

/**
 * Created by 4535992 on 05/11/2015.
 */
public class SimplePrintLog extends PrintStream {

    //-----------------------------------------------
    //Constructor
    //-----------------------------------------------

    public SimplePrintLog(PrintStream out) {
        super(out);
    }

    public SimplePrintLog(OutputStream out) {
        super(out, false);
    }

    public SimplePrintLog(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public SimplePrintLog(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out,autoFlush,encoding);
    }

    public SimplePrintLog(String fileName) throws FileNotFoundException {
        this(new FileOutputStream(fileName),false);
    }

    public SimplePrintLog(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(new FileOutputStream(fileName), false, csn);
    }

    public SimplePrintLog(File file) throws FileNotFoundException {
        super(new FileOutputStream(file), false);
    }

    public SimplePrintLog(File file, String csn)throws FileNotFoundException, UnsupportedEncodingException {
        super(new FileOutputStream(file), false, csn );
    }

    //------------------------------------
    //Methods
    //------------------------------------

    @Override
    public void flush() {super.flush();}
    @Override
    public void close() {
        // Restore the original standard output and standard error.
        // Then close the log file.
        System.setOut(oldStdout);
        System.setErr(oldStderr);
        try {
            logStreamFile.close();
        }
        catch (Exception e){
          
        }
        super.close();
    }
    @Override
    public boolean checkError() {return super.checkError();}

    @Override
    public void write(int b) {
        super.write(b);//go to SimpleLog
    }

    @Override
    public void write(byte buf[], int off, int len) {
        super.write(buf,off,len);//go to SimpleLog
    }

    //---------------------------------------------------
    // Methods that do not terminate lines
    //---------------------------------------------------

    @Override
    public void print(boolean b) {
        super.print(b);
    }
    @Override
    public void print(char c) {
        super.print(c);
    }
    @Override
    public void print(int i) {
        super.print(i);
    }
    @Override
    public void print(long l) {
        super.print(l);
    }
    @Override
    public void print(float f) {
        super.print(f);
    }
    @Override
    public void print(double d) {super.print(d);}
    @Override
    public void print(char s[]) {super.print(Arrays.toString(s));}
    @Override
    public void print(String s) { super.print(s);}
    @Override
    public void print(Object obj) {
        super.print(obj);
    }

    //---------------------------------------------------
    /* Methods that do terminate lines */
    //---------------------------------------------------

    @Override
    public void println() {super.println();}
    @Override
    public void println(boolean x) {super.println(x);}
    @Override
    public void println(char x) { super.println(x);}
    @Override
    public void println(int x) {super.println(x);}
    @Override
    public void println(long x) {super.println(x); }
    @Override
    public void println(float x) {super.println(x); }
    @Override
    public void println(double x) {super.println(x); }
    @Override
    public void println(char x[]) {super.println(Arrays.toString(x)); }
    @Override
    public void println(String x) {super.println(x); }
    @Override
    public void println(Object x) { super.println(x); }

    @Override
    public SimplePrintLog printf(String format, Object ... args) {
        return (SimplePrintLog) super.printf(format, args);
    }
    @Override
    public SimplePrintLog printf(Locale l, String format, Object ... args) {
        return (SimplePrintLog) super.printf(l,format, args);
    }
    @Override
    public SimplePrintLog format(String format, Object ... args) {
        return (SimplePrintLog) super.format(format, args);
    }
    @Override
    public SimplePrintLog format(Locale l, String format, Object ... args) {
        return (SimplePrintLog) super.format(l,format, args);
    }
    @Override
    public SimplePrintLog append(CharSequence csq) {return (SimplePrintLog) super.append(csq);}
    @Override
    public SimplePrintLog append(CharSequence csq, int start, int end) {return (SimplePrintLog) super.append(csq,start,end);}
    @Override
    public SimplePrintLog append(char c) {return (SimplePrintLog) super.append(c); }

    //------------------------------------------------------------------------
    //New Methods
    //------------------------------------------------------------------------

    static PrintStream oldStdout;
    static PrintStream oldStderr;
    static OutputStream logStreamFile;
    static File logFile;

    /**
     * Now all data written to System.out should be redirected into the file
     * "c:\\data\\system.out.txt". Keep in mind though, that you should make
     * sure to flush System.out and close the file before the JVM shuts down,
     * to be sure that all data written to System.out is actually flushed to the file.
     * @throws java.io.FileNotFoundException throw if any error is occurred.
     */
    public static void start() throws FileNotFoundException {
        start(new File(""));
    }

    public static void start(String fileName) throws FileNotFoundException {
        start(new File(fileName));
    }

    public static void start(File file) throws FileNotFoundException {
        // Save current settings for later restoring.
        oldStdout = System.out;
        oldStderr = System.err;
        SystemLog multiOut;
        SystemLog multiErr;
        if(file.getPath().isEmpty()){
            multiOut = new SystemLog(System.out);
            multiErr = new SystemLog(System.err);
        }else {
            logFile = file;
            logStreamFile = new PrintStream(
                    new BufferedOutputStream(
                            new FileOutputStream(file)));
            multiOut = new SystemLog(System.out, logStreamFile);
            multiErr = new SystemLog(System.err, logStreamFile);
        }
        SimplePrintLog stdout= new SimplePrintLog(multiOut);
        SimplePrintLog stderr= new SimplePrintLog(multiErr);
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     *  Ceases logging and restores the original settings.
     *  Restore the original standard output and standard error.
     *  Then close the log file.
     */
    public static void stop(){
        System.setOut(oldStdout);
        System.setErr(oldStderr);
        try {
            if (logStreamFile != null) {
                logStreamFile.close();
            }
        }catch(IOException e){
        }
    }


    /** A sample test driver.  The file samplelog.txt should contain
     everything that appeared on the console output.
     * @param args xxx.
     */
    public static void main(String[] args) {
        try {
            SimplePrintLog.start(new File(System.getProperty("user.dir") + File.separator + "bbbbsamplelog.txt"));
            System.out.println("Why...");
            System.err.println("..this..");
            System.out.println("..work????");
        }
        catch (Exception e) {
        }
        finally {
            // Turn off logging
            com.github.p4535992.util.log.PrintLog.stop();
        }
    }



}
