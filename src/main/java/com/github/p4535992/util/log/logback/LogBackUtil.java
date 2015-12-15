package com.github.p4535992.util.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.string.StringUtilities;
import org.slf4j.bridge.SLF4JBridgeHandler;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 4535992 on 09/12/2015.
 */
@SuppressWarnings("unused")
public class LogBackUtil {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(LogBackUtil.class);

    protected static SimpleDateFormat logTimestamp = new SimpleDateFormat("[HH:mm:ss]");
    protected static String logTimestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    protected static File logfile;

    protected static boolean isERROR,isPRINT,isInline, isLogOff = false;
    protected static boolean isLogUtil ,isLog4j ,isSlf4j =false;
    protected static boolean logging = false;

    protected static LoggerContext loggerContext;



    private static LogBackUtil instance = null;

    public static LogBackUtil init(){
        if(instance == null) {
            instance = new LogBackUtil();
            prepareLogFile(null, null, null);
            start();
        }
        return instance;
    }

    public static LogBackUtil init(File file){
        if(instance == null){
            instance = new LogBackUtil();
            prepareLogFile(
                    FileUtilities.getFilenameWithoutExt(file),
                    FileUtilities.getExtension(file),
                    FileUtilities.getPath(file));
            start();
        }
        return instance;
    }

    public static LogBackUtil init(String LOGNAME, String SUFFIX){
        if(instance == null){
            instance = new LogBackUtil();
            prepareLogFile(LOGNAME, SUFFIX, null);
            start();
        }
        return instance;
    }

    public static LogBackUtil init(String LOGNAME){
        if(instance == null){
            instance = new LogBackUtil();
            prepareLogFile(LOGNAME, ".txt", null);
            start();
        }
        return instance;
    }

    private static void prepareLogFile(String LOGNAME, String SUFFIX,String PATHFILE){
        //Set default value
        logTimestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        logging = true;

        if (LOGNAME != null) {
            if (StringUtilities.isNullOrEmpty(SUFFIX)) SUFFIX = "log";
            if (PATHFILE == null) {
                isPRINT = true;
                logfile =
                        new File(System.getProperty("user.dir") + File.separator
                                + LOGNAME + "_" + logTimestampFile + "." + SUFFIX);
                //logfile.createNewFile();
            } else {
                isPRINT = true;
                logfile = new File(
                        PATHFILE + File.separator + LOGNAME + "_" + logTimestampFile + "." + SUFFIX);
                //logfile.createNewFile();
            }
        } else {
            if (isPRINT) {
                if (logfile == null || !logfile.exists()) {
                    logfile = new File(System.getProperty("user.dir") + File.separator
                            + "createdAutomaticLog" + "_" + logTimestampFile + ".log");
                    //logfile.createNewFile();
                }
            }
        }
    }

    private static PatternLayoutEncoder createPatternLayoutEncoder(){
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%d{HH:mm:ss.SSS} [%-5level] %msg %n");
        //%d{HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n
        //ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(loggerContext);
        ple.start();
        return ple;
    }

    private static Logger createLogger(String nameLogger){
        return loggerContext.getLogger(nameLogger);
        //return (Logger) LoggerFactory.getLogger(nameLogger);
    }



   /* private static Logger createLoggerFor(String nameLogger, String file) {
        Logger myLogger = createLogger(nameLogger);
        PatternLayoutEncoder ple = createPatternLayoutEncoder();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(loggerContext);
        fileAppender.start();

        myLogger.addAppender(fileAppender);
        myLogger.setLevel(Level.DEBUG);
        myLogger.setAdditive(false); *//* set to true if root should log too *//*
        return  myLogger;
    }*/

    private static Logger createLoggerFileAppender(String nameLogger,String file){
        Logger myLogger = createLogger(nameLogger);
        PatternLayoutEncoder ple = createPatternLayoutEncoder();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName(nameLogger);
        fileAppender.setAppend(false);
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.start();

        myLogger.addAppender(fileAppender);
        myLogger.setLevel(Level.DEBUG);
        myLogger.setAdditive(false); /* set to true if root should log too */
        return myLogger;
    }

    private static  Logger createLoggerRollingAppender(String nameLogger,String fileName){
        Logger myLogger = createLogger(nameLogger);
        PatternLayoutEncoder ple = createPatternLayoutEncoder();

        RollingFileAppender<ILoggingEvent> rfAppender = new RollingFileAppender<ILoggingEvent>();
        rfAppender.setContext(loggerContext);
        rfAppender.setFile(fileName);

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        // rolling policies need to know their parent it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern(fileName + ".%i.log");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
        triggeringPolicy.setMaxFileSize("5MB");
        triggeringPolicy.start();

        rfAppender.setEncoder(ple);
        rfAppender.setRollingPolicy(rollingPolicy);
        rfAppender.setTriggeringPolicy(triggeringPolicy);
        rfAppender.start();

        // attach the rolling file appender to the logger of your choice
        myLogger.addAppender(rfAppender);

        // OPTIONAL: print logback internal status messages
        StatusPrinter.print(loggerContext);

        // log something myLogger.debug("hello");
        return myLogger;
    }


    protected static void start(){
        try {
            String pathToLogBackXML = "C:\\Users\\tenti\\Desktop\\EAT\\utility\\src\\main\\resources\\logback.xml";

            //Redirect all System.out and System.err to SLF4J.
            SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

            //Redirect all java util logging to SLF4J
            java.util.logging.LogManager.getLogManager().reset();
            // Optionally remove existing handlers attached to j.u.l root logger (since SLF4J 1.6.5)
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
            // the initialization phase of your application
            SLF4JBridgeHandler.install();
            java.util.logging.Logger.getLogger("global").setLevel(java.util.logging.Level.FINEST);

            // assume SLF4J is bound to logback in the current environment
            loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
            // print logback's internal status
            StatusPrinter.print(loggerContext);

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            loggerContext.reset();
            // inject the name of the current application as "application-name"
            // property of the LoggerContext
            loggerContext.putProperty("DEV_HOME",System.getProperty("user.dir"));
            //context.putProperty("application-name", "NAME_OF_CURRENT_APPLICATION");
            loggerContext.putProperty("logFileName22", logfile.getName());

            /*Logger rootLogger = loggerContext.getLogger("com.github.p4535992.util");
            rootLogger.setLevel(Level.INFO);*/

            /*Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            LOG.setLevel(Level.WARN);*/
            try {
                configurator.doConfigure(ClassLoader.getSystemClassLoader().getResource("logback.xml").getFile());
            } catch (JoranException je) {
                StatusPrinter.print(loggerContext);
            } catch(NullPointerException ne){
                logger.error("Wrong name resources for the LogBack config file.",ne);
            }
               // configurator.doConfigure(pathToLogBackXML);
            //optional
            //StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
            //StatusPrinter.print(loggerContext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

   /* public static void start(String filePath) {
        init();
        stop();
        fileAppender.setFile(filePath);
        fileAppender.start();
    }

    public static void stop() {
        if (fileAppender.isStarted()) {
            fileAppender.stop();
        }
    }*/

    //---------------------------------------------------------------------------------------------------------

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
                       /* if (isDEBUG) {
                            sb.append(MyLevel.DEBUG);
                        }
                        sb.append(myLevel.toString());*/
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

    //-----------------------------------------------------------------------------------------------------------------
    // CUSTOMIZE LOGGING
    //-----------------------------------------------------------------------------------------------------------------

    public static void logException(org.slf4j.Logger logger, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.error(sw.toString());
    }


}
