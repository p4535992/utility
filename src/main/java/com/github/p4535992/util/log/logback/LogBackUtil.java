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
import uk.org.lidalia.sysoutslf4j.context.LogLevel;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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


    protected static ch.qos.logback.classic.LoggerContext loggerContext;

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
            return new String(value.getBytes(), Charset.defaultCharset());
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
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(){
        return LogBackUtil.initBase(null,null,null,null);
    }

    /**
     * @param logpattern the {@link LOGPATTERN}
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(LOGPATTERN logpattern) {
        return LogBackUtil.initBase(null,null,null,logpattern);
    }

    /**
     * @param pathFileOutputLog the {@link String} path to the outputlog file.
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog) {
        return LogBackUtil.initBase(pathFileOutputLog,null,null,null);
    }

    /**
     * @param fileOutputLog the {@link File} path to the outputlog file.
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog) {
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null,null,null);
    }

    /**
     * @param fileOutputLog the {@link File} path to the outputlog file.
     * @param xmlConfigLogBack the {@link File} xml configuration.
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog,File xmlConfigLogBack){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, xmlConfigLogBack,null);
    }

    /**
     * @param fileOutputLog the {@link File} path to the outputlog file.
     * @param logpattern the {@link LOGPATTERN}
     * @param xmlConfigLogBack the {@link File} xml configuration.
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog,File xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, xmlConfigLogBack,logpattern);
    }

    /**
     * @param fileOutputLog the {@link File} path to the outputlog file.
     * @param logpattern the {@link LOGPATTERN}
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(File fileOutputLog,LOGPATTERN logpattern){
        return LogBackUtil.initBase(fileOutputLog.getAbsolutePath(),null, null,logpattern);
    }

    /**
     * @param pathFileOutputLog the {@link String} path to the outputlog file.
     * @param logpattern the {@link LOGPATTERN}
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,null, null,logpattern);
    }

    /**
     * @param pathFileOutputLog the {@link String} path to the outputlog file.
     * @param xmlConfigLogBack the {@link String} path to the XML configuration file.
     * @param logpattern the {@link LOGPATTERN}
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog,String xmlConfigLogBack,LOGPATTERN logpattern){
        return LogBackUtil.initBase(pathFileOutputLog,null, new File(xmlConfigLogBack),logpattern);
    }

    /**
    * @param pathFileOutputLog the {@link String} path to the outputlog file.
     * @param suffixPathFileOutputLog the {@link String} suffix of the log file.
     * @param xmlConfigLogBack the {@link File} path to the XML configuration file.
     * @return the {@link LogBackUtil}
     * @deprecated use {@link #ConsoleAndFile()} instead.
     */
    @Deprecated
    public static LogBackUtil init(String pathFileOutputLog, String suffixPathFileOutputLog,
                                   File xmlConfigLogBack){
        return LogBackUtil.initBase(pathFileOutputLog,suffixPathFileOutputLog, xmlConfigLogBack,null);
    }

    /**
     * @param pathFileOutputLog the {@link String} path to the outputlog file.
     * @param suffixPathFileOutputLog the {@link String} suffix of the log file.
     * @param xmlConfigLogBack the {@link File} path to the XML configuration file.
     * @param logpattern the {@link LOGPATTERN}
     * @return the {@link LogBackUtil}
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
            try {
                InputStream inputStreamDobs =
                        LogBackUtil.class.getResourceAsStream("logback/logback_base_console.xml");
                Files.copy(inputStreamDobs,consoleFile.toPath());
                //consoleFile = FileUtilities.toFile(inputStreamDobs,"./logback_base_console.xml");
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }

        if(consoleFile == null) {
            System.err.println("missing resources folder");
            return instance;
        } else {
            if(consoleFile.isDirectory() && consoleFile.exists()) {
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
        String logTimeStampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        if (LOGNAME == null || LOGNAME.isEmpty()) LOGNAME = "createdAutomaticLog";
        if(!(LOGNAME.contains("/")||LOGNAME.contains("\\"))) {
            try {
                LOGNAME = new File(".").getCanonicalPath() + File.separator + LOGNAME;
            } catch (IOException ignored) {}
        }
        if (SUFFIX == null || SUFFIX.isEmpty()) SUFFIX = "log";
        logfile = new File(LOGNAME + "_" + logTimeStampFile + "." + SUFFIX);
    }

    private static void prepareLogFile(File LOGNAME){
        String logTimeStampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        if(LOGNAME == null){
            try {
            String file = new File(".").getCanonicalPath() + File.separator + "createdAutomaticLog";    
            logfile = new File(file + "_" + logTimeStampFile + ".log");
             } catch (IOException ignored) {}
        }else {
            String SUFFIX = ".txt";
            //Set default value
            if (SUFFIX.isEmpty()) SUFFIX = "log";
            logfile = new File(LOGNAME + "_" + logTimeStampFile + "." + SUFFIX);
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

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
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

        RollingFileAppender<ILoggingEvent> rfAppender = new RollingFileAppender<>();
        rfAppender.setContext(loggerContext);
        rfAppender.setFile(fileName);

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        // rolling policies need to know their parent it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern(fileName + ".%i.log");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
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
        JoranConfigurator configurator = new JoranConfigurator();
        try {
            //String pathToLogBackXML = "C:\\Users\\tenti\\Desktop\\EAT\\utility\\src\\main\\resources\\logback.xml";
            System.out.println("222222222222222222222222222222222");
            boolean noError = true;
            try {
                uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.registerLoggingSystem("com.github.p4535992");
                //Redirect all System.out and System.err to SLF4J.
                uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);
            } catch (java.lang.NoSuchMethodError e) {
                if (e.getMessage().contains("" +
                        "org.slf4j.spi.LocationAwareLogger.log(Lorg/slf4j/Marker;Ljava/lang/String;ILjava/lang" +
                        "/String;[Ljava/lang/Object;Ljava/lang/Throwable;)")) {
                    System.err.println("[1] Probably you use a wrong version of SLF4j for work with sysoutslf4j:" + e.getMessage());
                    noError = false;
                } else {
                    System.err.println("[2] Some error not expected:" + e.getMessage());
                    noError = false;
                }
            } catch (Exception e) {
                System.err.println("[3] Some error not expected:" + e.getMessage());
                noError = false;
            }
            //org.apache.log4j.Logger logger = LogManager.getLogger(LogBackUtil.class.getName());

            //Redirect all java util logging to SLF4J
            java.util.logging.LogManager.getLogManager().reset();

            // Optionally remove existing handlers attached to j.u.l root logger (since SLF4J 1.6.5)
            try {
                org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger();
                // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
                // the initialization phase of your application
                org.slf4j.bridge.SLF4JBridgeHandler.install();
                java.util.logging.Logger.getLogger("global").setLevel(java.util.logging.Level.FINEST);
            } catch (java.lang.NoSuchMethodError e) {
                if (e.getMessage().contains("" +
                        "org.slf4j.spi.LocationAwareLogger.log(Lorg/slf4j/Marker;Ljava/lang/String;ILjava/lang" +
                        "/String;[Ljava/lang/Object;Ljava/lang/Throwable;)")) {
                    System.err.println("[4] Probably you use a wrong version of SLF4j for work with JUL:" + e.getMessage());
                    noError = false;
                } else {
                    System.err.println("[5] Some error not expected:" + e.getMessage());
                    noError = false;
                }
            } catch (Exception e) {
                System.err.println("[5.5] Some error not expected:" + e.getMessage());
                noError = false;
            }

            try {
                // assume SLF4J is bound to logback in the current environment (not work)
                try {
                    //loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
                    loggerContext = new LoggerContext();
                }catch(Exception e) {
                    System.err.println("5.5.2"+e.getMessage());
                    //loggerContext = new LoggerContext();
                }
                // print logback's internal status
                //StatusPrinter.print(loggerContext);
                //JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(loggerContext);
                // Call context.reset() to clear any previous configuration, e.g. default
                // configuration. For multi-step configuration, omit calling context.reset().
                loggerContext.reset();
            } catch (Exception e) {
                System.err.println("[5.6] Some error not expected:" + e.getMessage());
                noError = false;
            }
            if (noError) {
                if (!justConsole) {
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
                } else {
                    // go to the console logback.xml
                    System.out.println("[5.7] USE JUST THE CONSOLE FOR THE LOGGING");
                }
            } else {
                //continue without custmoization;
                System.err.println("[5.8] USE JUST THE BASIC STANDARD CONFIGURATION OF JORA");
            }
            try {
                if (pathToLogBackXML.contains("resources")) {
                    //noinspection ConstantConditions,AccessStaticViaInstance
                    configurator.doConfigure(ClassLoader.
                            getSystemClassLoader().getResource(pathToLogBackXML).getFile());
                } else {
                    try {
                        configurator.doConfigure(pathToLogBackXML);
                    } catch (Exception e) {
                        System.err.println("[6] " + e.getMessage());
                    }
                }

            } catch (JoranException je) {
                System.err.print("[7] " + je.getMessage());
                //StatusPrinter.print(loggerContext);
            } catch (NullPointerException ne) {
                //The context XML File is not on the Resource Folder
                try {
                    pathToLogBackXML = new File(new File(".").getCanonicalPath() +
                            File.separator + pathToLogBackXML).getAbsolutePath();
                    configurator.doConfigure(pathToLogBackXML);
                } catch (IOException | JoranException e) {
                    System.err.println("[8] CAN'T SET THE XML:" + e.getMessage());
                } catch(Exception e){
                    System.err.println("[8.2] CAN'T SET THE XML:" + e.getMessage());
                }
            }
            // configurator.doConfigure(pathToLogBackXML);
            //optional
            //StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
            //StatusPrinter.print(loggerContext);
        }catch(java.lang.NoSuchMethodError e){
                if(e.getMessage().contains("" +
                        "org.slf4j.spi.LocationAwareLogger.log(Lorg/slf4j/Marker;Ljava/lang/String;ILjava/lang" +
                        "/String;[Ljava/lang/Object;Ljava/lang/Throwable;)")){
                    System.err.println("[8.5] Probably you use a wrong version of SLF4j for work with JUL:"+e.getMessage());

                }else {
                    System.err.println("[8.7] Some error not expected:" + e.getMessage());
                }
        } catch (Exception e) {
            System.err.println("[9] CAN'T SET THE XML:"+e.getMessage());
            //e.printStackTrace();
            try {
                configurator.doConfigure("logback/logback.xml");
            } catch (JoranException e1) {
                System.err.println("[10] CAN'T SET THE XML:"+e.getMessage());
                e1.printStackTrace();
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    // CUSTOMIZE LOGGING
    //-----------------------------------------------------------------------------------------------------------------

   /* public static <T> T withSystemOutAndErrSentToSLF4J(Callable<T> work) throws Exception {
        try {
            sendSystemOutAndErrToSLF4J();
            return work.call();
        } finally {
            restoreOriginalSystemOutputs();
        }
    }*/

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
        try {
            String basePath = "utility\\src\\main\\java\\com\\github\\p4535992" +
                    "\\util\\log\\logback\\script\\mysql.sql";
            return new File(new File(".").getCanonicalPath() + File.separator+basePath);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

  
}
