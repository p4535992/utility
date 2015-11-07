package com.github.p4535992.util.file.impl;

import com.github.p4535992.util.log.SystemLog;

import java.io.*;

/**
 * Created by 4535992 on 04/11/2015.
 */
public class FileStream {


    public static InputStreamReader getInputStreamReader(InputStream is, String encoding) throws IOException {
        SystemLog.message("Reading stream: using encoding: " + encoding);
        org.apache.commons.io.input.BOMInputStream bis = new org.apache.commons.io.input.BOMInputStream(is); //So that we can remove the BOM
        return new InputStreamReader(bis, encoding);
    }

    public static InputStreamReader getInputStreamReader(File file, String encoding) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        SystemLog.message("Reading file: " + file + " using encoding: " + encoding);
        org.apache.commons.io.input.BOMInputStream bis =
                new org.apache.commons.io.input.BOMInputStream(fis); //So that we can remove the BOM
        return new InputStreamReader(bis, encoding);
    }

    public static String getString(File file, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        FileInputStream fis = new FileInputStream(file);
        SystemLog.message("Reading file: " + file + " using encoding: " + encoding);
        org.apache.commons.io.IOUtils.copy(fis, sw, encoding);
        return sw.toString();
    }
}
