package com.github.p4535992.util.log.slf4j.frame;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FrameLoggerFactory implements ILoggerFactory {

    ConcurrentMap<String,Logger> loggerMap;

    @SuppressWarnings("rawtypes")
    public FrameLoggerFactory() {
        loggerMap = new ConcurrentHashMap<>();
    }

    /**
     * Return an appropriate instance by name.
     */
    public Logger getLogger(String name) {
        Logger frameLogger = loggerMap.get(name);
        if (frameLogger != null) {
            return frameLogger;
        } else {
            Logger newInstance = new FrameLogger(name);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    void reset() {
        loggerMap.clear();
    }
}