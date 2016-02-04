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




import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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


    protected static LoggerContext loggerContext;

    protected static LOGPATTERN logpattern = LOGPATTERN.PATTERN_CLASSIC;
    protected static LOGPATTERN logpatternConsole ;
    protected static LOGPATTERN logpatternFile;
    protected static LOGPATTERN logpatternFileError;


    public enum LOGPATTERN {
        PATTERN_CLASSIC("%d{yyyy-MM-dd_HH:mm:ss.SSS} [%thread] %-5level %logger{52} - %msg%n"),
        PATTERN_CLASSIC_NOTIME("[%thread] %-5level %logger{52} - %msg%n"),
        PATTERN_COLORED1("%d{yyyy-MM-dd_HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{52}) - %msg%n"),
        PATTERN_COLORED1_NOTIME("[%thread] %highlight(%-5level) %cyan(%logger{52}) - %msg%n"),
        PATTERN_COLORED2_NOTIME("[%thread] %highlightex(%-5level) %logger{15} - %highlightex(%msg) %n"),
        PATTERN_COLORED1_METHOD_NOTIME("[%thread] %highlight(%-5level) %cyan(%logger{52}.%M) - %msg%n"),
        PATTERN_METHOD("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36}.%M - %msg%n"),
        PATTERN_METHOD_LINE("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36}.%M - %msg%n"),
        PATTERN_METHOD_LINE_NOTIME("[%thread] %-5level %logger{36}.%M - %msg%n");

        private final String value;

        LOGPATTERN(String value) {
            this.value = value;
        }

        public String getValue() {
            return new String(value.getBytes(), StringUtilities.DEFAULT_ENCODING);
        }

        @Override
        public String toString() {
            return getValue();
        }
    }

    public static void setCustomPattern(LOGPATTERN logpattern){
        LogBackUtil.logpattern = logpattern;
    }

    private static LogBackUtil instance = null;

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(){
        return LogBackUtil.initBase(null,null,null,null);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(LOGPATTERN logpattern) {
        return LogBackUtil.initBase(null,null,null,logpattern);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog) {
        return LogBackUtil.initBase(pathFileOutputLog,null,null,null);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog) {
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null,null,null);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog,File xmlConfigLogBack){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, xmlConfigLogBack,null);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog,File xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, xmlConfigLogBack,logpattern);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog,LOGPATTERN logpattern){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, null,logpattern);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,null, null,logpattern);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog,String xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,null, new File(xmlConfigLogBack),logpattern);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog, String suffixPathFileOutputLog,
                                   File xmlConfigLogBack){
        return LogBackUtil.initBase(pathFileOutputLog,suffixPathFileOutputLog, xmlConfigLogBack,null);
    }

    /**
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog, String suffixPathFileOutputLog,
                                   File xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,suffixPathFileOutputLog, xmlConfigLogBack,null);
    }
    
    //
    public static LogBackUtil ConsoleAndFile(){
        return LogBackUtil.initBase(null,null,null,null);
    }

    public static LogBackUtil ConsoleAndFile(LOGPATTERN logpattern) {
        return LogBackUtil.initBase(null,null,null,logpattern);
    }

    public static LogBackUtil ConsoleAndFile(String pathFileOutputLog) {
        return LogBackUtil.initBase(pathFileOutputLog,null,null,null);
    }

    public static LogBackUtil ConsoleAndFile(File fileOutputLog) {
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null,null,null);
    }

    public static LogBackUtil ConsoleAndFile(File fileOutputLog,File xmlConfigLogBack){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, xmlConfigLogBack,null);
    }

    public static LogBackUtil ConsoleAndFile(File fileOutputLog,File xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, xmlConfigLogBack,logpattern);
    }

    public static LogBackUtil ConsoleAndFile(File fileOutputLog,LOGPATTERN logpattern){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, null,logpattern);
    }

    public static LogBackUtil ConsoleAndFile(String pathFileOutputLog,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,null, null,logpattern);
    }

    public static LogBackUtil ConsoleAndFile(String pathFileOutputLog,String xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,null, new File(xmlConfigLogBack),logpattern);
    }

    public static LogBackUtil ConsoleAndFile(String pathFileOutputLog, String suffixPathFileOutputLog,
                                   File xmlConfigLogBack){
        return LogBackUtil.initBase(pathFileOutputLog,suffixPathFileOutputLog, xmlConfigLogBack,null);
    }

    public static LogBackUtil ConsoleAndFile(String pathFileOutputLog, String suffixPathFileOutputLog,
                                   File xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,suffixPathFileOutputLog, xmlConfigLogBack,null);
    }


    public static LogBackUtil console(){
        if(instance == null) {
            instance = new LogBackUtil();
        }
      /*  String basePath = "utility\\src\\main\\java\\com\\github\\" +
                "p4535992\\util\\log\\logback\\resources\\logback_base_console.xml";*/

        File consoleFile = null;
        String sFile = null;
        try {
            //String sFile =  ClassLoader.class.getResource("logback/logback_base_console.xml").getFile(); //not work
            sFile =  LogBackUtil.class.getClassLoader().getResource("logback/logback_base_console.xml").getFile();
            //url = FileUtilities.toURL(sFile);
            consoleFile = new File(sFile);
        } catch (java.lang.NullPointerException e) {
            System.err.println(e.getMessage()+","+e.getCause());
        }
        if (sFile == null || consoleFile == null) {
            InputStream inputStreamDobs =
                    ClassLoader.class.getResourceAsStream("logback/logback_base_console.xml");
            consoleFile = FileUtilities.toFile(inputStreamDobs,"./logback_base_console.xml");
        }

        if(consoleFile == null) {
            System.err.println("missing resources folder");
            return instance;
        } else {
            if(FileUtilities.isDirectoryExists(consoleFile)) {
                //noinspection ConstantConditions
                for (File nextFile : consoleFile.listFiles()) {
                    if (nextFile.getName().equalsIgnoreCase("logback_base_console.xml")) {
                        start(nextFile.getAbsolutePath(), true);
                        return instance;
                    }
                }
                logger.error("Can't find the file \"logback_base_console.xml\" on resource directory");
                return instance;
            }else{
                start(consoleFile.getAbsolutePath(), true);
                return instance;
            }
        }
    }

    public static LogBackUtil console(Path xmlConfig){
        if(instance == null) {
            instance = new LogBackUtil();
        }
        start(xmlConfig.toAbsolutePath().toString(),true);
        return instance;
    }

    public static LogBackUtil console(String xmlConfig){
        return console(Paths.get(xmlConfig));
    }

    private static LogBackUtil initBase(String pathFileOutputLog, String suffixPathFileOutputLog,
                                   File xmlConfigLogBack,LOGPATTERN logpattern){
        if(instance == null){
            instance = new LogBackUtil();
            prepareLogFile(pathFileOutputLog, suffixPathFileOutputLog);
            if(xmlConfigLogBack == null){
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                Class<?> myCLass;
                try {
                    myCLass = Class.forName(stackTraceElements[3].getClassName());
                } catch (ClassNotFoundException e) {
                    System.err.println("Can't load the java Class invoke the LogbackUtil, " +
                            "we used a not customize configuration");
                    myCLass = LogBackUtil.class;
                }
                System.err.println("You have not specified the path for the logback.xml "
                        + "configuration file by default we use a logback.xml "
                        + "file on the root path of the \"resources\" folder");
                ClassLoader classLoader = myCLass.getClassLoader();
                try {
                    xmlConfigLogBack = new File(classLoader.getResource("logback/logback.xml").getFile());
                }catch(Exception e){
                    System.err.println("Sorry the XML configuration file you try to load from the resource " +
                            "folder not exists, by default we use a logback.xml "
                            + "file on the root path of the \"resources\" folder");
                    classLoader = LogBackUtil.class.getClassLoader();
                    xmlConfigLogBack = new File(classLoader.getResource("logback/logback.xml").getFile());
                }
            }
            start(xmlConfigLogBack,logpattern);
        }
        return instance;
    }


    //METHOD

    private static void prepareLogFile(String LOGNAME, String SUFFIX){
        String logTimestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        if (StringUtilities.isNullOrEmpty(LOGNAME)) LOGNAME = "createdAutomaticLog";
        if(!(LOGNAME.contains("/")||LOGNAME.contains("\\"))) {
            LOGNAME = StringUtilities.PROJECT_DIR + File.separator + LOGNAME;
        }
        if (StringUtilities.isNullOrEmpty(SUFFIX)) SUFFIX = "log";
        logfile = new File(LOGNAME + "_" + logTimestampFile + "." + SUFFIX);
    }

    private static void prepareLogFile(File LOGNAME){
        String logTimestampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        if(LOGNAME == null){
            String file = StringUtilities.PROJECT_DIR + File.separator + "createdAutomaticLog";
            logfile = new File(file + "_" + logTimestampFile + ".log");
        }else {
            String SUFFIX = FileUtilities.getExtension(LOGNAME);
            //Set default value
            if (StringUtilities.isNullOrEmpty(SUFFIX)) SUFFIX = "log";
            logfile = new File(LOGNAME + "_" + logTimestampFile + "." + SUFFIX);
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

    protected static void start(File pathToLogBackXML,LOGPATTERN pattern){
        if(pattern != null)LogBackUtil.logpattern = pattern;
        start(pathToLogBackXML.getAbsolutePath());
    }

    protected static void start(String pathToLogBackXML){
        start(pathToLogBackXML,false);
    }

    protected static void start(String pathToLogBackXML,boolean justConsole){
        try {
            //String pathToLogBackXML = "C:\\Users\\tenti\\Desktop\\EAT\\utility\\src\\main\\resources\\logback.xml";

            //Redirect all System.out and System.err to SLF4J.
            uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

            //org.apache.log4j.Logger logger = LogManager.getLogger(LogBackUtil.class.getName());


            //Redirect all java util logging to SLF4J
            java.util.logging.LogManager.getLogManager().reset();
            // Optionally remove existing handlers attached to j.u.l root logger (since SLF4J 1.6.5)
            org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger();
            // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
            // the initialization phase of your application
            org.slf4j.bridge.SLF4JBridgeHandler.install();
            java.util.logging.Logger.getLogger("global").setLevel(java.util.logging.Level.FINEST);

            // assume SLF4J is bound to logback in the current environment
            loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
            // print logback's internal status
            //StatusPrinter.print(loggerContext);

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            loggerContext.reset();
            if(!justConsole) {
                // inject the name of the current application as "application-name"
                // property of the LoggerContext
                loggerContext.putProperty("DEV_HOME", System.getProperty("user.dir"));
                //context.putProperty("application-name", "NAME_OF_CURRENT_APPLICATION");
                loggerContext.putProperty("logFileName",
                        logfile.getName());
                loggerContext.putProperty("logPatternConsole",
                        logpatternConsole == null ? logpattern.getValue() : logpatternConsole.getValue());
                loggerContext.putProperty("logPatternFile",
                        logpatternFile == null ? logpattern.getValue() : logpatternFile.getValue());
                loggerContext.putProperty("logPatternFileError",
                        logpatternFileError == null ? logpattern.getValue() : logpatternFileError.getValue());
                /*Logger rootLogger = loggerContext.getLogger("com.github.p4535992.util");
                rootLogger.setLevel(Level.INFO);*/

                /*Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
                LOG.setLevel(Level.WARN);*/
            }else{
                // go to the console logback.xml
            }
            try {
                if(pathToLogBackXML.contains("resources")) {
                    configurator.doConfigure(ClassLoader.getSystemClassLoader().getResource(pathToLogBackXML).getFile());
                }else{
                    try {
                        configurator.doConfigure(pathToLogBackXML);
                    }catch(Exception e){
                        System.err.println(e.getMessage());
                    }
                }

            } catch (JoranException je) {
                StatusPrinter.print(loggerContext);
            } catch(NullPointerException ne){
                //The context XML File is not on the Resource Folder
                try {
                    pathToLogBackXML = new File(FileUtilities.getDirectoryUser()+pathToLogBackXML).getAbsolutePath();
                    configurator.doConfigure(pathToLogBackXML);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
               // configurator.doConfigure(pathToLogBackXML);
            //optional
            //StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
            //StatusPrinter.print(loggerContext);
        } catch (Exception e) {
           e.printStackTrace();
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

    public static void setLogpatternConsole(LOGPATTERN logpatternConsole) {
        LogBackUtil.logpatternConsole = logpatternConsole;
    }

    public static void setLogpatternFile(LOGPATTERN logpatternFile) {
        LogBackUtil.logpatternFile = logpatternFile;
    }

    public static void setLogpatternFileError(LOGPATTERN logpatternFileError) {
        LogBackUtil.logpatternFileError = logpatternFileError;
    }


    public static File getMySQLScript(){
        String basePath = "utility\\src\\main\\java\\com\\github\\p4535992" +
                "\\util\\log\\logback\\script\\mysql.sql";
        return new File(FileUtilities.getDirectoryUser()+basePath);
    }

    public static File getMySQLScript(String pathResourceFileName,Class<?> thisClass,File outputFile){
        return FileUtilities.getFromResourceAsFile(pathResourceFileName,thisClass,outputFile);
    }


}
