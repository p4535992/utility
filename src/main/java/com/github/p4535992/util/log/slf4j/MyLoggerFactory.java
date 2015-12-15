package com.github.p4535992.util.log.slf4j;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class MyLoggerFactory implements ILoggerFactory{
    
    private Map<String,MyLoggerAdapter> loggerMap;

    public MyLoggerFactory() {
        loggerMap = new HashMap<String, MyLoggerAdapter>();
    }

    @Override
    public Logger getLogger(String name) {
        synchronized (loggerMap) {
            if (!loggerMap.containsKey(name)) {
                loggerMap.put(name, new MyLoggerAdapter(name));
            }

            return loggerMap.get(name);
        }
    }
    
}
