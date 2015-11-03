package com.github.p4535992.util.log;

import com.github.p4535992.util.file.impl.FileUtil;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

/**
 * Class for print a personal log file.
 * @author 4535992.
 * @version 2015-07-14.
 * @param <T> the generic type.
 */
@SuppressWarnings("unused")
public class SystemLog<T> {

    private static java.lang.reflect.Type t;
    private static java.lang.reflect.ParameterizedType pt;
    private static Class<?> cl ;
    private static String clName ;

    /** {@code org.slf4j.Logger} */
    private static final org.slf4j.Logger SLF4JLogger = 
            org.slf4j.LoggerFactory.getLogger(SystemLog.class);
    private static final org.apache.log4j.Logger LOG4JLogger = 
            org.apache.log4j.Logger.getLogger(SystemLog.class);
    private static final java.util.logging.Logger UTILLogger = 
            java.util.logging.Logger.getLogger(SystemLog.class.getName());

    private static org.slf4j.Logger slf4j ;
    private static org.apache.log4j.Logger log4j;
    private static java.util.logging.Logger logUtil;

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
    private static boolean isInline = false;

    private static boolean isLogOff = false;
    private static boolean isLog4j = false;
    private static boolean isSlf4j = false;

    public static boolean isERROR() {return isERROR;}

    public static void setIsERROR(boolean isERROR) {SystemLog.isERROR = isERROR;}

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

    public static boolean isLog4j() {return isLog4j;}

    public static void setIsLog4j(boolean isLog4j) {SystemLog.isLog4j = isLog4j;}

    public static boolean isSlf4j() {return isSlf4j;}

    public static void setIsSlf4j(boolean isSlf4j) {SystemLog.isSlf4j = isSlf4j;}

    public static boolean isInline() { return isInline; }

    public static void setIsInline(boolean isInline) { SystemLog.isInline = isInline; }

    /** Default {@code DateFormat} instance, used when custom one not set. */
    private static DateFormat df;
    /** {@code PrintWriter} instance used for logging. */
    private static PrintWriter logWriter;
    /** Flag determining whether log entries are written to the log stream. */
    private static boolean logging = false;
    /** Separator string (between date and log message). */
    private static String separator = ": ";

    public SystemLog(){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            SystemLog.LOGNAME = "LOG_"+timeStamp+".txt";
            SystemLog.logging = true;
            SystemLog.LOGFILE = FileUtil.createFile(LOGNAME);
            setLogWriter();
        } catch (IOException ex) {
            //java.util.logging.Logger
            java.util.logging.Logger.getLogger(SystemLog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
    @SuppressWarnings("rawtypes")
    protected static void write(String logEntry) {
        try {
            if (logEntry != null) {
                if (isLogOff) {
                    if(isInline){
                        if (isERROR) System.err.print(logEntry);
                        else System.out.print(logEntry);
                    }else {
                        if (isERROR) System.err.println(logEntry);
                        else System.out.println(logEntry);
                    }
                } else {
                    if (LOGFILE == null || !LOGFILE.exists()) new SystemLog();
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
                    if(isInline){
                        if (isERROR) System.err.print(sb.toString());
                        else System.out.print(sb.toString());
                    }else {
                        if (isERROR) System.err.println(sb.toString());
                        else System.out.println(sb.toString());
                    }
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
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            isERROR=false;
        }
    }
    
    public static void console(String logEntry){console(logEntry,null);}
    public static void console(String logEntry,Class<?> thisClass){
        level = Level.VOID;
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){
                //String confidentialMarkerText = "CONFIDENTIAL";
                //org.slf4j.Marker confidentialMarker = org.slf4j.MarkerFactory.getMarker(confidentialMarkerText);
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else System.out.println(logEntry);
        }
        else System.out.println(logEntry);
    }

    public static void messageInline(String logEntry){isInline=true; message(logEntry,null);}
    public static void message(String logEntry){message(logEntry,null);}
    public static void message(String logEntry,Class<?> thisClass){
        level = Level.OUT;
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void error(String logEntry){error(logEntry,new Throwable(logEntry),null);}
    public static void error(String logEntry,Exception ex){error(logEntry+"->"+ex.getMessage(),null,null);}
    public static void error(String logEntry,Exception ex,Class<?> thisClass){error(logEntry, new Throwable(ex.getCause()),thisClass);}
    public static void error(String logEntry,Throwable th){error(logEntry,th,null);}
    public static void error(String logEntry,Throwable th,Class<?> thisClass){
        level = Level.ERR;
        isERROR=true;
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void warning(String logEntry){warning(logEntry,null,null);}
    public static void warning(String logEntry,Class<?> thisClass){warning(logEntry,null,thisClass);}
    public static void warning(Throwable th){warning(th.getMessage() + "," + th.getLocalizedMessage(), th, null);}
    public static void warning(Throwable th,Class<?> thisClass){warning(th.getMessage() + "," + th.getLocalizedMessage(), th, thisClass);}
    public static void warning(String logEntry,Throwable th,Class<?> thisClass){
        level = Level.WARN;
        isERROR=true;
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void hibernate(String logEntry) {hibernate(logEntry,null);}
    public static void hibernate(String logEntry,Class<?> thisClass) {
        level = Level.HIBERNATE;
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void sparql(String logEntry) {sparql(logEntry,null,null);}
    public static void sparql(String logEntry,Class<?> clazz) {sparql(logEntry,null,clazz);}
    public static void sparql(String logEntry,Exception e) {sparql(logEntry,new Throwable(e.getCause()),null);}
    //public static void sparql(String logEntry,Exception e,Class<?> clazz) {sparql(logEntry,new Throwable(e.getCause()),clazz);}
    public static void sparql(Exception e) {sparql(e.getMessage(),new Throwable(e.getCause()),null);}
    public static void sparql(Exception e,Class<?> clazz) {sparql(e.getMessage(),new Throwable(e.getCause()),clazz);}
    public static void sparql(String logEntry,Throwable th,Class<?> thisClass){
        level = Level.SPARQL;
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }


    public static void query(String logEntry) { query(logEntry,null);}
    public static void query(String logEntry,Class<?> thisClass){
        level = Level.QUERY;
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void attention(String logEntry) {attention(logEntry,null);}
    public static void attention(String logEntry,Class<?> thisClass) {
        level = Level.ATTENTION;
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void abort(int rc){System.exit(rc);}
    public static void abort(int rc,String logEntry){abort(rc,logEntry,null);}
    public static void abort(int rc, String logEntry,Class<?> thisClass) {
        level = Level.ABORT;
        isERROR=true;
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry); System.exit(rc);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry); System.exit(rc);}
            else  write(logEntry); System.exit(rc);
        }
        else  write(logEntry); System.exit(rc);
    }

    public static void throwException(Throwable th){ throwException(th.getMessage(), th, null);}
    public static void throwException(Exception e){throwException(e.getMessage()+"->"+e.getCause().toString(),null,null);}
    public static void throwException(String logEntry,Throwable th,Class<?> thisClass){
        level = Level.THROW;
        isERROR=true;
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void exception(Exception e){ exception(e.getMessage(), new Throwable(e.getCause()), null);}
    public static void exception(Exception e,Class<?> clazz){ exception(e.getMessage(), new Throwable(e.getCause()), clazz);}
    public static void exception(String logEntry,Throwable th,Class<?> thisClass){
        level = Level.EXCEP;
        isERROR=true;
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void exceptionAndAbort(Exception e){
        exception(e);
        abort(0);
    }


    public static void logStackTrace(Exception e, org.slf4j.Logger logger) {
        logger.debug(e.getMessage());
        for (StackTraceElement stackTrace : e.getStackTrace()) {
            logger.error(stackTrace.toString());
        }
    }

    public static void logException(Exception e, org.slf4j.Logger logger) {
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
     * Set the current {@code PrintWriter} used to write to the log.
     */
    private void setLogWriter()  {
        try {
            logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)));
            /*SystemLog.t = getClass().getGenericSuperclass();
            SystemLog.pt = (java.lang.reflect.ParameterizedType) t;
            SystemLog.cl = (Class) pt.getActualTypeArguments()[0];
            SystemLog.clName = cl.getSimpleName();*/
        } catch (IOException e) {
            logStackTrace(e, SLF4JLogger);
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
            logWriter.close();
        }
        logWriter = null;
    }

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


