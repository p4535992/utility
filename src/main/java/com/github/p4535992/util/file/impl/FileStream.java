package com.github.p4535992.util.file.impl;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringUtilities;

import java.io.*;
import java.nio.charset.Charset;

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
        //org.apache.commons.io.IOUtils.copy(fis, sw, encoding);
        copy(fis, sw, encoding);
        return sw.toString();
    }

    public static int copy(InputStream input, Writer output, String encoding){
        InputStreamReader in = new InputStreamReader(input, StringUtilities.toCharset(encoding));
        long count = copyLarge(in, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output){
        //copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE])
        char[] buffer = new char[1024 * 4];
        long count = 0;
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }catch(IOException e){
            SystemLog.exception(e);
            return 0;
        }
    }
}
