package com.github.p4535992.util.log;

import org.slf4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
/**
 * Class for print a personal log file.
 * @author 4535992.
 * @version 2015-07-14.
 */
@SuppressWarnings("unused")
public class SystemLog {

    /** {@code org.slf4j.Logger} */
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SystemLog.class);
    private static Level level;
    /** {@code DateFormat} instance for formatting log entries. */
    private static SimpleDateFormat logTimestamp = new SimpleDateFormat("[HH:mm:ss]");
    // private static SimpleDateFormat logTimesAndDateStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    private static String LOGNAME;
    private static File LOGFILE;
    /** Flag to provide basic support for debug information (not used within class). */
    private static Integer LEVEL;
    private static boolean isDEBUG=false;
    private static boolean isERROR;
    private static boolean isPRINT = true;
    private static boolean isLogOff = false;

    public static boolean isLogOff() {return isLogOff;}

    public static void setIsLogOff(boolean isLogOff) {SystemLog.isLogOff = isLogOff;}

    public static boolean isPRINT() {
        return isPRINT;
    }

    public static void setIsPRINT(boolean isPRINT) {
        SystemLog.isPRINT = isPRINT;
    }

    public static boolean isDEBUG() {
        return isDEBUG;
    }

    public static void setIsDEBUG(boolean isDEBUG) {
        SystemLog.isDEBUG = isDEBUG;
    }




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

    /*
     * Method for print the string to a specific file
     * @param content string content of th log message to print.
     */
    /*private static void printString2File(String content) {
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
    }*/

    /*
     * Method to print a string to a file
     * @param content
     */
   /* private static void print2File(String content) {
        if (logging) {
            logWriter.print(content + System.getProperty("line.separator"));
            logWriter.flush();
            logWriter.close();
        }
    }*/

    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    protected static void write(String logEntry) {
        try{
            if(isLogOff){
                if(isERROR)System.err.println(logEntry);
                else System.out.println(logEntry);
            } else {
                if (LOGFILE == null) new SystemLog();
                //if(!logging){ log = new SystemLog();}
                StringBuilder sb = new StringBuilder();
                if (logTimestamp != null)
                    sb.append(logTimestamp.format(new Date()));
                else {
                    if (df == null)
                        df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                    sb.append(df.format(logTimestamp));
                }
                if (isDEBUG) {
                    sb.append(Level.DEBUG);
                }
                sb.append(level.toString());
                sb.append(logEntry);
                if (isERROR) System.err.println(sb.toString());
                else System.out.println(sb.toString());
                if (isPRINT) {
                    try (PrintWriter pWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)))) {
                        //try{
                        pWriter.print(sb.toString() + System.getProperty("line.separator"));
                        pWriter.flush();
                        //logWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }finally{
            isERROR=false;
        }
    }
    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    public static void console(String logEntry){level = Level.VOID; System.out.println(logEntry);}
    public static void message(String logEntry){level = Level.OUT; write(logEntry);}
    public static void error(String logEntry){level = Level.ERR; isERROR=true; write(logEntry);}
    public static void warning(String logEntry){level = Level.WARN; isERROR=true; write(logEntry);}
    public static void warning(Exception e){level = Level.WARN; isERROR=true; write(e.getMessage() + "," + e.getLocalizedMessage());}
    public static void hibernate(String logEntry) { level = Level.HIBERNATE; write(logEntry);}
    public static void sparql(String logEntry) { level = Level.SPARQL; write(logEntry);}
    public static void query(String logEntry) { level = Level.QUERY; write(logEntry);}
    public static void attention(String logEntry) {level = Level.ATTENTION; write(logEntry);}
    public static void abort(int rc){System.exit(rc);}
    public static void abort(int rc, String logEntry) {level = Level.ABORT;  isERROR=true; write(logEntry); System.exit(rc);}
    public static void throwException(Throwable throwable){ level = Level.THROW;  isERROR=true; write(throwable.getMessage());}
    public static void exception(Exception e){ level = Level.EXCEP; isERROR=true;e.printStackTrace();}
    public static void throwException(Exception e){Throwable thrw = e.getCause();  throwException(thrw);}
    public static void exceptionAndAbort(Exception e){level = Level.EXCEP; isERROR=true; e.printStackTrace();System.exit(0);}


    public static void loggerInfoSLF4J(org.slf4j.Logger log,String msg){log.info(msg);}
    public static void logger(Class<?> c){logger = org.slf4j.LoggerFactory.getLogger(c);}

    public static void logStackTrace(Exception e, Logger logger) {
        logger.debug(e.getMessage());
        for (StackTraceElement stackTrace : e.getStackTrace()) {
            logger.error(stackTrace.toString());
        }
    }

    public static void logException(Exception e, Logger logger) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.error(sw.toString());
    }


    /*
     * Sets the separator string between the date and log message (default &quot;: &quot;).
     * To set the default separator, call with a null argument.
     * @param sep string to use as separator
     */
     /*public static void setSeparator(String sep) {separator = (sep == null) ? ": " : sep;}*/

    /*
     * Sets the log stream and enables logging.
     * By default the {@code PrintWriter} is closed when the {@link #close()}
     * method is called.
     * @param writer {@code PrintWriter} to which to write log entries
     */
    /*public final  void setLog(PrintWriter writer) {setLog(writer, true);}
*/
    /*
     * Sets the log stream and enables logging.
     * @param writer {@code PrintWriter} to which to write log entries
     * @param closeOnExit whether to close the {@code PrintWriter} when {@link #close()} is called
     */
    /*public  static void setLog(PrintWriter writer, boolean closeOnExit) {
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
        closeWriterOnExit = (logWriter != null) && closeOnExit;
    }
*/
    /**
     * Returns the current {@code PrintWriter} used to write to the log.
     * @return The current {@code PrintWriter} used to write to the log
     */
    private static  void setLogWriter()  {
        try {
            logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)));
        } catch (IOException e) {
            logStackTrace(e, logger);
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

    /*
     * Determines whether calls to the logging methods actually write to the log.
     * @param b flag indicating whether to write to the log
     */
    //public static void setLogging(boolean b) {logging = b;}

    /*
     * Returns whether calls to the logging methods actually write to the log.
     * @return true if logging is enabled, false otherwise.
     */
    //public static  boolean isLogging() {return logging;}

    /*
     * Determines whether to perform debug logging.
     * @param b flag indicating whether to perform debug logging
     */
    //public static  void setDebug(boolean b) {isDEBUG = b;}

    /*
     * Returns whether debug logging is enabled.
     * @return true if debug logging is enabled, false otherwise.
     */
    //public static  boolean isDebug() {return isDEBUG;}


    public enum Level {
        VOID(0), OUT(1), WARN(2),ERR(3),ABORT(4),HIBERNATE(5),SPARQL(6),QUERY(7),THROW(8),EXCEP(9),ATTENTION(10),DEBUG(11);
        private final Integer value;
        Level(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            String prefix="";
            String suffix ="";
            switch (this) {
                case ERR: prefix = "[ERROR] -> "; break;
                case WARN: prefix ="[WARNING] -> "; break;
                case ABORT: prefix ="[EXIT] -> "; break;
                case HIBERNATE: prefix = "[HIBERNATE] -> "; break;
                case SPARQL: prefix ="[SPARQL] -> "; break;
                case QUERY: prefix ="[QUERY] -> "; break;
                case ATTENTION: prefix ="=====[ATTENTION]===== -> "; break;
                case EXCEP: prefix ="[EXCEPTION] ->"; break;
                case DEBUG: prefix = "<!-- [debug] ";  break;
            }
            return prefix;
        }
    }


        
}


