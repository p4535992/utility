/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.p4535992.util.log;

import org.slf4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.List;


/**
 * Class for print a personal log file.
 * @author 4535992.
 * @version 2015-06-27.
 */
public class SystemLog {
    private static SystemLog log;
    private static String LOGNAME;
    private static File LOGFILE;
    /** Flag to provide basic support for debug information (not used within class). */
    private static Integer LEVEL;
    private static boolean DEBUG,ERROR;
    /** {@code org.slf4j.Logger} */
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SystemLog.class);
    private static Level level;
    /** {@code DateFormat} instance for formatting log entries. */
    private static SimpleDateFormat logTimestamp = new SimpleDateFormat("[HH:mm:ss]");
   // private static SimpleDateFormat logTimesAndDateStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");


    /** Default {@code DateFormat} instance, used when custom one not set. */
    private static DateFormat df;
    /** {@code PrintWriter} instance used for logging. */
    private static PrintWriter logWriter;
    /** Flag determining whether log entries are written to the log stream. */
    private static boolean logging = false;
    /** Flag determining whether the {@code LogWriter} is closed when {@link #close()} method is called. */
    private static boolean closeWriterOnExit = false;
    /** Separator string (between date and log message). */
    private static String separator = ": ";

    public SystemLog(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        SystemLog.LOGNAME = "LOG_"+timeStamp+".txt";
        SystemLog.logging = true;
        SystemLog.LOGFILE = new File(LOGNAME);
        setLogWriter();
    }

    public SystemLog(String LOGNAME, String SUFFIX){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        SystemLog.LOGNAME = LOGNAME +"_"+timeStamp+"."+ SUFFIX;
        SystemLog.logging = true;
        SystemLog.LOGFILE = new File(LOGNAME);
        setLogWriter();
    }

    public SystemLog(String LOGNAME, String SUFFIX,String PATHFILE){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        SystemLog.LOGNAME = LOGNAME +"_"+timeStamp+"."+ SUFFIX;
        SystemLog.logging = true;
        SystemLog.LOGFILE = new File(PATHFILE+File.separator+LOGNAME);
        setLogWriter();
    }

    /**
     * Method for print the string to a specific file
     * @param content
     */
    private static void printString2File(String content) {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)))) {
            //try{
            out.print(content+System.getProperty("line.separator"));
            out.flush();
            //}finally{
            //    out.close();
            //}             
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }finally {
            if(LEVEL == 5)System.exit(1);
            
        }
    }

    /**
     * Method to print a string to a file
     * @param content
     */
    private static void print2File(String content) {
        if (!logging){}
        logWriter.print(content +System.getProperty("line.separator"));
        logWriter.flush();
        logWriter.close();
    }

    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    protected static void write(String logEntry) {
        if(!logging){ log = new SystemLog();}
        StringBuilder sb = new StringBuilder();
        if (logTimestamp != null)
            sb.append(logTimestamp.format(new Date()));
        else
        {
            if (df == null)
                df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
            sb.append(df.format(logTimestamp));
        }
        sb.append(level.toString());
        sb.append(logEntry);
        if(ERROR)System.err.println(sb.toString());
        else System.out.println(sb.toString());
        try(PrintWriter pWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)))) {
            //try{
            pWriter.print(sb.toString() + System.getProperty("line.separator"));
            pWriter.flush();
            //logWriter.close();
        }catch (IOException e){
        }finally{
            ERROR=false;
        }
    }
    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    public static void console(String logEntry){level = Level.VOID; System.out.println(logEntry);}
    public static void message(String logEntry){level = Level.OUT; write(logEntry);}
    public static void error(String logEntry){level = Level.ERR; ERROR=true;
        write(logEntry);}
    public static void warning(String logEntry){level = Level.WARN; ERROR=true; write(logEntry);}
    public static void warning(Exception e){level = Level.WARN; ERROR=true; write(e.getMessage()+","+e.getLocalizedMessage());}
    //public static void ticket(String message){ LEVEL = 1;printString2File(message);}
    public static void hibernate(String logEntry) { level = Level.HIBERNATE; write(logEntry);}
    public static void sparql(String logEntry) { level = Level.SPARQL; write(logEntry);}
    public static void query(String logEntry) { level = Level.QUERY; write(logEntry);}
    public static void attention(String logEntry) {level = Level.ATTENTION; write(logEntry);}
    public static void abort(int rc, String logEntry) {level = Level.ABORT;  ERROR=true;write(logEntry); System.exit(rc);}
    public static void throwException(Throwable throwable){ level = Level.THROW;  ERROR=true; write(throwable.getMessage());}
    public static void exception(Exception e){ level = Level.EXCEP; ERROR=true;e.printStackTrace();}
    public static void throwException(Exception e){Throwable thrw = e.getCause();  throwException(thrw);}
    public static void loggerInfoSLF4J(org.slf4j.Logger log,String msg){log.info(msg);}

    /**
     * If debug is enabled, writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    public static void debug(String logEntry) {
       // if (isDebug())
        logEntry = "<!-- [debug] " + logEntry + " -->";
        write(logEntry);
    }

    private static void printStackTrace(Exception e){
        StackTraceElement elements[] = e.getStackTrace();
        for (int i = 0, n = elements.length; i < n; i++) {
            System.err.println(elements[i].getFileName()
                    + ":" + elements[i].getLineNumber()
                    + ">> "
                    + elements[i].getMethodName() + "()");
        }
    }

    public static String getUsageMessage() {
        String lines[] ={/*TEST MESSAGE*/};
        String usage = "";
        for (String line : lines) {
            usage += line + "\n";
        }
        return usage;
    }

    public static void logger(Class<?> c){logger = org.slf4j.LoggerFactory.getLogger(c);}

    public static void logStackTrace(Exception e, Logger logger) {
        logger.debug(e.getMessage());
        for (StackTraceElement stackTrace : e.getStackTrace()) {
            logger.error(stackTrace.toString());
        }
    }


    /**
     * Sets the separator string between the date and log message (default &quot;: &quot;).
     * To set the default separator, call with a null argument.
     * @param sep string to use as separator
     */
    public static void setSeparator(String sep) {separator = (sep == null) ? ": " : sep;}

    /**
     * Sets the log stream and enables logging.
     * By default the {@code PrintWriter} is closed when the {@link #close()}
     * method is called.
     * @param writer {@code PrintWriter} to which to write log entries
     */
    public final  void setLog(PrintWriter writer) {setLog(writer, true);}

    /**
     * Sets the log stream and enables logging.
     * @param writer {@code PrintWriter} to which to write log entries
     * @param closeOnExit whether to close the {@code PrintWriter} when {@link #close()} is called
     */
    public  static void setLog(PrintWriter writer, boolean closeOnExit) {
        if (logWriter != null)
        {
            logWriter.flush();
            if (closeWriterOnExit)
                close();
        }
        if (logging = (writer != null))
            logWriter = writer;
        else
            logWriter = null;
        closeWriterOnExit = (logWriter != null) ? closeOnExit : false;
    }

    /**
     * Returns the current {@code PrintWriter} used to write to the log.
     * @return The current {@code PrintWriter} used to write to the log
     */
    private static  void setLogWriter()  {
        try {
            logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)));
        } catch (IOException e) {
            SystemLog.logStackTrace(e, logger);
        }
    }

    /**
     * Closes the log.
     */
    public static  void close() {
        logging = false;
        if (logWriter != null)
        {
            logWriter.flush();
            if (closeWriterOnExit)
                logWriter.close();
        }
        logWriter = null;
    }

    /**
     * Determines whether calls to the logging methods actually write to the log.
     * @param b flag indicating whether to write to the log
     */
    public static void setLogging(boolean b) {logging = b;}

    /**
     * Returns whether calls to the logging methods actually write to the log.
     * @return true if logging is enabled, false otherwise.
     */
    public static  boolean isLogging() {return logging;}

    /**
     * Determines whether to perform debug logging.
     * @param b flag indicating whether to perform debug logging
     */
    public static  void setDebug(boolean b) {DEBUG = b;}

    /**
     * Returns whether debug logging is enabled.
     * @return true if debug logging is enabled, false otherwise.
     */
    public static  boolean isDebug() {return DEBUG;}


    public enum Level {
        VOID(0), OUT(1), WARN(2),ERR(3),ABORT(4),HIBERNATE(5),SPARQL(6),QUERY(7),THROW(8),EXCEP(9),ATTENTION(10);
        private final Integer value;
        Level(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            String prefix="";
            switch (this) {
                case ERR: prefix = "[ERROR] -> "; break;
                case WARN: prefix ="[WARNING] -> "; break;
                case ABORT: prefix ="[EXIT] -> "; break;
                case HIBERNATE: prefix = "[HIBERNATE] -> "; break;
                case SPARQL: prefix ="[SPARQL] -> "; break;
                case QUERY: prefix ="[QUERY] -> "; break;
                case ATTENTION: prefix ="=====[ATTENTION]===== -> "; break;
                case EXCEP: prefix ="[EXCEPTION] ->"; break;
            }
            return prefix;
        }
    }

    public static void logException(Logger logger, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.error(sw.toString());
    }
        
}


