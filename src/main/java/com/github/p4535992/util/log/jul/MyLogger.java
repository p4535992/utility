package com.github.p4535992.util.log.jul;

import java.io.IOException;
import java.util.logging.*;


public class MyLogger {
    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.INFO);
        FileHandler fileTxt = new FileHandler("Logging.txt");
        FileHandler fileHTML = new FileHandler("Logging.html");

        // create a TXT formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        // create an HTML formatter
        Formatter formatterHTML = new UtilLogHtml();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);

    }

}


