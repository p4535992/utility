package com.github.p4535992.util.log.logback;

import java.io.IOException;

/**
 * Created by 4535992 on 09/12/2015.
 */
public class MainLogbackTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MainLogbackTest.class);

    public static void main(String[] args) throws IOException {
        LogBackUtil.init("testLog");
        System.out.println("Test 45");
        System.err.println("Test 42");

        logger.info("Msg #1");
        logger.warn("Msg #2");
        logger.error("Msg #3");
        logger.debug("Msg #4");

    }
}
