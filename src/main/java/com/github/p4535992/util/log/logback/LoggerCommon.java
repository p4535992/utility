package com.github.p4535992.util.log.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerCommon
{
    private Logger logger;

    public LoggerCommon(Class<?> c)
    {
        logger = LoggerFactory.getLogger(c);
    }

    public void doLog(String message)
    {
        logger.info(message);
    }
}