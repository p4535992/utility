package com.github.p4535992.util.log.log4j;

import org.apache.log4j.*;

import java.io.File;

/**
 * Created by 4535992 on 13/01/2016.
 */
public class Log4jUtilities {

    protected static void configurationLog4j(String pathFileXML){
        //    File file = new File("path/to/a/different/log4j2.xml");
        configurationLog4j(new File(pathFileXML));
    }

    protected static void configurationLog4j(File fileXML){
        // this will force a reconfiguration
        //LoggerContext context =(LoggerContext)LogManager.getContext(false);
        //context.setConfigLocation(fileXML.toURI());
        //DOMConfigurator is used to configure logger from xml configuration file
        org.apache.log4j.xml.DOMConfigurator.configure(fileXML.getAbsolutePath());

        // creates pattern layout
        String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
        PatternLayout layout = new PatternLayout(conversionPattern);
        //String pat1 = "%-5p: %c - %m%n";
        //layout.setConversionPattern(conversionPattern);

        // configures the root logger
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);

        //Console
        rootLogger.addAppender((Appender) createConsoleAppender(layout));
        //File
        rootLogger.addAppender((Appender) createFileAppender(layout,new File(".")));


    }

    protected static FileAppender createFileAppender(PatternLayout layout,File fileLayout){
        FileAppender fileAppender = new FileAppender(layout,fileLayout.getAbsolutePath());
        //fileAppender.setLayout(layout);
        fileAppender.activateOptions();
        return fileAppender;
    }

    protected static ConsoleAppender createConsoleAppender(PatternLayout layout){
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(layout);
        consoleAppender.activateOptions();
        return consoleAppender;

    }

    protected void configurationLog4JLogger(){
        Logger log = Logger.getLogger("org.spring.jdbc");
        log.setLevel(Level.INFO);
    }

    /**
     *
     * @param name The name of the custom level. Note that level names are case sensitive.
     *             The convention is to use all upper-case names.
     * @param intLevel Determines where the custom level exists in relation to the standard levels built-in to Log4J 2.
     */
    protected void createNewLog4J(String name,int intLevel){
        //final org.apache.log4j.Level newLogger = org.apache.log4j.Level.forName(name, intLevel);
    }

    protected static void closeLog4J(){
      /*  Logger.getRootLogger().getLoggerRepository().resetConfiguration();
        LoggerContext context =(LoggerContext)LogManager.getContext();
        Configurator.shutdown(context);*/
        //Logger.getRootLogger().getLoggerRepository().resetConfiguration();
    }

    //APPENDERS
    /*
    AppenderSkeleton
    AsyncAppender
    ConsoleAppender
    DailyRollingFileAppender
    ExternallyRolledFileAppender
    FileAppender
    JDBCAppender
    JMSAppender
    LF5Appender
    NTEventLogAppender
    NullAppender
    RollingFileAppender
    SMTPAppender
    SocketAppender
    SocketHubAppender
    SyslogAppender
    TelnetAppender
    WriterAppender
    */

}
