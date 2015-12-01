package com.github.p4535992.util.log;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.string.StringUtilities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.logging.Logger;

/**
 * Class for print a personal log file.
 * href : http://www.codeproject.com/Tips/315892/A-quick-and-easy-way-to-direct-Java-System-out-to
 * href: http://users.csc.calpoly.edu/~jdalbey/SWE/Tools/LogFile.java.
 * @author 4535992.
 * @version 2015-07-14.
 */
public class SystemLog extends OutputStream{

   /* private static java.lang.reflect.Type t;
    private static java.lang.reflect.ParameterizedType pt;
    private static Class<?> cl ;
    private static String clName ;
    private static PrintLog printLog;*/

    /** {@code org.slf4j.Logger} */
    private static final org.slf4j.Logger SLF4JLogger =
            org.slf4j.LoggerFactory.getLogger(SystemLog.class);
    private static final org.apache.log4j.Logger LOG4JLogger = 
            org.apache.log4j.Logger.getLogger(SystemLog.class);
    private static final java.util.logging.Logger UTILLogger = 
            java.util.logging.Logger.getLogger(SystemLog.class.getName());

    protected static org.slf4j.Logger slf4j;
    protected static org.apache.log4j.Logger log4j;
    protected static java.util.logging.Logger logUtil;

    protected static MyLevel myLevel;
    protected static SimpleDateFormat logTimestamp = new SimpleDateFormat("[HH:mm:ss]");
    protected static String logTimestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    protected static File logfile;

    protected static boolean isDEBUG,isERROR,isPRINT,isInline, isLogOff = false;
    protected static boolean isLogUtil ,isLog4j ,isSlf4j =false;
    protected static boolean logging = false;

/*
    private static PrintWriter logWriter;
    private static String separator = ": ";
*/

    OutputStream[] outputStreams;

    public static boolean isERROR() {return isERROR;}

    public static void setIsERROR(boolean isERROR) {SystemLog.isERROR = isERROR;}

    public static boolean isLogOff() {return isLogOff;}

    public static void setIsLogOff(boolean isLogOff) {SystemLog.isLogOff = isLogOff;}

    public static boolean isPRINT() {
        return isPRINT;
    }

    /*public static void setIsPRINT(boolean isPRINT) {
        SystemLog.isPRINT = isPRINT;
    }*/

    public static boolean isDEBUG() { return isDEBUG;}

    public static void setIsDEBUG(boolean isDEBUG) {SystemLog.isDEBUG = isDEBUG; }

    public static boolean isLog4j() {return isLog4j;}

    public static void setIsLog4j(boolean isLog4j) {SystemLog.isLog4j = isLog4j;}

    public static boolean isSlf4j() {return isSlf4j;}

    public static void setIsSlf4j(boolean isSlf4j) {SystemLog.isSlf4j = isSlf4j;}

    public static boolean isInline() { return isInline; }

    public static void setIsInline(boolean isInline) { SystemLog.isInline = isInline; }

    private static SystemLog instance = null;

    public static SystemLog start(){
        if(instance == null) {
            prepareLogFile(null, null, null);
            instance = new SystemLog();
        }
        return instance;
    }

    public static SystemLog start(File file){
        if(instance == null){
            prepareLogFile(
                    FileUtilities.getFilenameWithoutExt(file),
                    FileUtilities.getExtension(file),
                    FileUtilities.getPath(file));
            instance = new SystemLog();
        }
        return instance;
    }

    public static SystemLog start(String LOGNAME, String SUFFIX){
        if(instance == null){
            prepareLogFile(LOGNAME, SUFFIX, null);
            instance = new SystemLog();
        }
        return instance;
    }

    public static SystemLog startWithLog4J(File fileXML){
        isLog4j = true;
        SystemLog4j.configurationLog4j(fileXML);
        if(instance == null)instance = start();
        return instance;
    }

    public static SystemLog startWithLog4J(File fileOutput,File fileXML){
        isLog4j = true;
        isPRINT = true;
        if(instance == null){
            prepareLogFile(fileOutput);
            SystemLog4j.configurationLog4j(fileXML);
            instance = new SystemLog();
        }
        return instance ;
    }

    private static void prepareLogFile(File fileOutput){
        logfile = fileOutput;
    }

    private static void prepareLogFile(String LOGNAME, String SUFFIX,String PATHFILE){
        //Set default value
        logTimestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        logging = true;
        if(LOGNAME!=null) {
            if (StringUtilities.isNullOrEmpty(SUFFIX)) SUFFIX = "log";
            if (PATHFILE == null) {
                isPRINT = true;
                SystemLog.logfile =
                        new File(System.getProperty("user.dir") + File.separator
                                + LOGNAME + "_" + logTimestampFile + "." + SUFFIX);
            } else {
                isPRINT = true;
                SystemLog.logfile = new File(
                        PATHFILE + File.separator + LOGNAME + "_" + logTimestampFile + "." + SUFFIX);
            }
        }else{
            if(isPRINT) {
                if (logfile == null || !logfile.exists()) {
                    logfile = new File(System.getProperty("user.dir") + File.separator
                            + "createdAutomaticLog" + "_" + logTimestampFile + ".log");
                }
            }
        }
    }

    protected SystemLog(){
        try {
            if(isLog4j){ /*do nothing*/}
            else {
                if (logfile == null) PrintLog.start();
                else PrintLog.start(logfile);
            }
        } catch (Exception ex) {
            //java.util.logging.Logger.getLogger(SystemLog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public SystemLog(OutputStream... outputStreams) {
       this.outputStreams= outputStreams;
    }

    @Override
    public void  close() throws IOException {
        if(outputStreams != null) {
            for (OutputStream out : outputStreams) {
                if(out!=null)out.close();
            }
        }
        if(isLog4j)  SystemLog4j.closeLog4J();
        PrintLog.stop();
        // Shut down log4j
        isDEBUG = false; isERROR = false; isPRINT = false; isInline = false;  isLogOff = false;
        isLogUtil  = false; isLog4j  = false; isSlf4j =false;
        logging = false;
        instance = null;
    }

    @Override
    public void flush() throws IOException{
        for (OutputStream out: outputStreams) {
            out.flush();
        }
    }

    @Override
    public void write(int b) throws IOException{
        for (OutputStream out : outputStreams) {
            if(out!=null){
                out.write(b);
                out.flush();
            }
        }

    }

    @Override
    public void write(byte[] b) throws IOException{
        for (OutputStream out : outputStreams) {
            if(out!=null){out.write(b);out.flush();}
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        for (OutputStream out : outputStreams) {
            if(out!=null) { out.write(b, off, len);out.flush();}
        }
    }

    protected static String prepareLogEntry(String logEntry, Class<?> clazz){
        try {
            if (logEntry != null) {
                StringBuilder sb = new StringBuilder();
                if(!isLogOff) {
                    if(!(isLog4j || isSlf4j || isLogUtil)) {
                        if (logTimestamp != null)
                            sb.append(logTimestamp.format(new Date()));
                        else {
                            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                            sb.append(df.format(logTimestamp));
                        }
                        if (isDEBUG) {
                            sb.append(MyLevel.DEBUG);
                        }
                        sb.append(myLevel.toString());
                        //Add to the class.....
                        if(clazz!=null)sb.append(clazz.getName()).append("::");
                    }
                }
                logEntry = StringUtilities.toStringInline(logEntry);//beautify String
                sb.append(logEntry);
                return sb.toString();
            }else{
                return "[NULL]["+logTimestamp+"]";
            }
        }catch(Exception e){
            return "[SOME ERROR WITH THE CREATION OF THE LOG][" + logTimestamp + "]";
        }
    }

    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    protected static void write(String logEntry) {
       try{
            if(isInline){
                if (isERROR) System.err.print(logEntry);
                else System.out.print(logEntry);
            }else {
                if (isERROR) System.err.println(logEntry);
                else System.out.println(logEntry);
            }
       }finally{
            isERROR=false;
       }
    }

    //-----------------------------------------------------------------------------------------------------------------
    // CUSTOMIZE LOGGING
    //-----------------------------------------------------------------------------------------------------------------

    public static void console(String logEntry){console(logEntry,null);}
    public static void console(String logEntry,Class<?> thisClass){
        myLevel = MyLevel.VOID;
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

    public static void messageInline(String logEntry){isInline=true; message(logEntry, null);}
    public static void message(String logEntry){message(logEntry,null);}
    public static void message(String logEntry,Class<?> thisClass){
        myLevel = MyLevel.OUT;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                log4j.info(logEntry);
            }
            else if(isSlf4j){
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                slf4j.info(logEntry);
            }
            else if(isLogUtil){
                logUtil = Logger.getLogger(thisClass.getName());
                logUtil.log(java.util.logging.Level.INFO, logEntry);
            }
            else write(logEntry);
        }
        else{
            if(isLog4j) LOG4JLogger.info(logEntry);
            else if(isSlf4j) SLF4JLogger.info(logEntry);
            else if(isLogUtil)UTILLogger.log(java.util.logging.Level.INFO,logEntry);
            else write(logEntry);
        }
    }

    public static void error(String logEntry){error(logEntry, new Throwable(logEntry), null);}
    public static void error(String logEntry,Exception e){error(logEntry+"->"+e.getMessage(),null,null);}
    public static void error(String logEntry,Exception e,Class<?> thisClass){
        error(logEntry+": "+ e.getClass().getName() + ": " + e.getMessage(), new Throwable(e.getCause()),thisClass);
    }
    public static void error(String logEntry,Throwable th){error(logEntry,th,null);}
    public static void error(String logEntry,Throwable th,Class<?> thisClass){
        myLevel = MyLevel.ERR;
        isERROR=true;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            else if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else if(isLogUtil){
                logUtil = Logger.getLogger(thisClass.getName());
                if (th != null)  logUtil.log(java.util.logging.Level.WARNING, logEntry,th);
                else logUtil.log(java.util.logging.Level.WARNING, logEntry);
            }
            else write(logEntry);
        }
        else{
            if(isLog4j) LOG4JLogger.error(logEntry);
            else if(isSlf4j) SLF4JLogger.error(logEntry);
            else if(isLogUtil)UTILLogger.log(java.util.logging.Level.WARNING,logEntry);
            else write(logEntry);
        }
    }

    public static void warning(String logEntry){warning(logEntry, null, null);}
    public static void warning(String logEntry,Class<?> thisClass){warning(logEntry,null,thisClass);}
    public static void warning(Throwable th){warning(th.getMessage() + "," + th.getLocalizedMessage(), th, null);}
    public static void warning(Throwable th,Class<?> thisClass){warning(th.getMessage() + "," + th.getLocalizedMessage(), th, thisClass);}
    public static void warning(String logEntry,Throwable th,Class<?> thisClass){
        myLevel = MyLevel.WARN;
        isERROR=true;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.info(logEntry,th);
                else log4j.info(logEntry);
            }
            else if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.info(logEntry, th);
                else slf4j.info(logEntry);
            }
            else if(isLogUtil){
                logUtil = Logger.getLogger(thisClass.getName());
                if (th != null)  logUtil.log(java.util.logging.Level.WARNING, logEntry,th);
                else logUtil.log(java.util.logging.Level.WARNING, logEntry);
            }
            else write(logEntry);
        }
        else{
            if(isLog4j) LOG4JLogger.error(logEntry);
            else if(isSlf4j) SLF4JLogger.error(logEntry);
            else if(isLogUtil)UTILLogger.log(java.util.logging.Level.WARNING,logEntry);
            else write(logEntry);
        }
    }

    public static void hibernate(String logEntry) {hibernate(logEntry, null);}
    public static void hibernate(String logEntry,Class<?> thisClass) {
        myLevel = MyLevel.HIBERNATE;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void sparql(String logEntry) {sparql(logEntry, null, null);}
    public static void sparql(String logEntry,Class<?> clazz) {sparql(logEntry,null,clazz);}
    public static void sparql(String logEntry,Exception e) {sparql(logEntry,new Throwable(e.getCause()),null);}
    //public static void sparql(String logEntry,Exception e,Class<?> clazz) {sparql(logEntry,new Throwable(e.getCause()),clazz);}
    public static void sparql(Exception e) {sparql(e.getMessage(),new Throwable(e.getCause()),null);}
    public static void sparql(Exception e,Class<?> clazz) {sparql(e.getMessage(),new Throwable(e.getCause()),clazz);}
    public static void sparql(String logEntry,Throwable th,Class<?> thisClass){
        myLevel = MyLevel.SPARQL;
        logEntry = prepareLogEntry(logEntry,thisClass);
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


    public static void query(String logEntry) { query(logEntry, null);}
    public static void query(String logEntry,Class<?> thisClass){
        myLevel = MyLevel.QUERY;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void attention(String logEntry) {attention(logEntry, null);}
    public static void attention(String logEntry,Class<?> thisClass) {
        myLevel = MyLevel.ATTENTION;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLog4j){ log4j = org.apache.log4j.Logger.getLogger(thisClass); log4j.info(logEntry);}
            if(isSlf4j){ slf4j = org.slf4j.LoggerFactory.getLogger(thisClass); slf4j.info(logEntry);}
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void abort(int rc){System.exit(rc);}
    public static void abort(int rc,String logEntry){abort(rc, logEntry, null);}
    public static void abort(int rc, String logEntry,Class<?> thisClass) {
        myLevel = MyLevel.ABORT;
        logEntry = prepareLogEntry(logEntry,thisClass);
        isERROR=true;
        if(thisClass!=null) {
            if(isLogUtil){
                logUtil = Logger.getLogger(thisClass.getName());
                logUtil.log(java.util.logging.Level.SEVERE, logEntry);
            }
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                log4j.fatal(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                slf4j.error(logEntry);
            }
            else  write(logEntry);
        }
        else  write(logEntry);
        System.exit(rc);
    }

    public static void throwException(Throwable th){
        throwException(th.getClass().getName() + ": " + th.getMessage(), new Throwable(th.getCause()), th.getClass());}
    public static void throwException(Exception e){
        throwException( e.getClass().getName() + ": " + e.getMessage(), new Throwable(e.getCause()), e.getClass());}
    public static void throwException(String logEntry,Throwable th,Class<?> thisClass){
        myLevel = MyLevel.THROW;
        logEntry = prepareLogEntry(logEntry,thisClass);
        isERROR=true;
        if(thisClass!=null) {
            if(isLogUtil){
                logUtil = Logger.getLogger(thisClass.getName());
                if(th!=null)logUtil.log(java.util.logging.Level.SEVERE, logEntry, th);
                else logUtil.log(java.util.logging.Level.SEVERE, logEntry);
            }
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.fatal(logEntry, th);
                else log4j.fatal(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.error(logEntry, th);
                else slf4j.error(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void exception(Exception e){ exception(
            e.getClass().getName() + ": " + e.getMessage(), new Throwable(e.getCause()), e.getClass());}
    public static void exception(Exception e,Class<?> clazz){ exception(
            e.getClass().getName() + ": " + e.getMessage(), new Throwable(e.getCause()), clazz);}
    public static void exception(String logEntry){ exception(logEntry, new Throwable(logEntry), null);}
    public static void exception(String logEntry,Class<?> clazz){ exception(logEntry, new Throwable(logEntry), clazz);}
    public static void exception(String logEntry,Throwable th,Class<?> thisClass){
        myLevel = MyLevel.EXCEP;
        isERROR=true;
        logEntry = prepareLogEntry(logEntry,thisClass);
        if(thisClass!=null) {
            if(isLogUtil){
                logUtil = Logger.getLogger(thisClass.getName());
                if(th!=null)logUtil.log(java.util.logging.Level.SEVERE, logEntry, th);
                else logUtil.log(java.util.logging.Level.SEVERE, logEntry);
            }
            if(isLog4j){
                log4j = org.apache.log4j.Logger.getLogger(thisClass);
                if(th!=null)log4j.fatal(logEntry,th);
                else log4j.fatal(logEntry);
            }
            if(isSlf4j) {
                slf4j = org.slf4j.LoggerFactory.getLogger(thisClass);
                if (th != null) slf4j.error(logEntry, th);
                else slf4j.error(logEntry);
            }
            else write(logEntry);
        }
        else write(logEntry);
    }

    public static void exceptionAndAbort(Exception e){
        exception(e);
        abort(0);
    }

    public enum MyLevel {
        VOID(0), OUT(1), WARN(2),ERR(3),ABORT(4),HIBERNATE(5),SPARQL(6),QUERY(7),THROW(8),EXCEP(9),ATTENTION(10),DEBUG(11);
        private final Integer value;
        MyLevel(Integer value) {
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

    //------------------------------
    // Utility Method
    //------------------------------
    /**
     * Method to get the name of the current method,.
     * Input: Thread.currentThread().getStackTrace()
     * @param e the Array of TraceElement.
     * @return the String Name of the current Method.
     */
    public static String nameOfMethod(StackTraceElement e[]){
        boolean doNext = false;
        for (StackTraceElement s : e) {
            if (doNext) return s.getMethodName();
            doNext = s.getMethodName().equals("getStackTrace");
        }
        return null;
    }
}


