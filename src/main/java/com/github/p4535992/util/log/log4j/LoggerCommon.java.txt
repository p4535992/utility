package com.github.p4535992.util.log.log4j;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LoggerCommon
{
    private static Logger logger;

    public LoggerCommon(Class<?> c)
    {
        logger = Logger.getLogger(c);
    }

    public void doLog(String message)
    {
        logger.log(LoggerCommon.class.getName(), Level.INFO, message, null);
    }
}