package com.github.p4535992.util.string;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;

/**
 * Created by 4535992 on 06/11/2015.
 */
public class StringUtilities {

    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_16 = Charset.forName("UTF-16");
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset CP1252 = Charset.forName("Cp1252");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final Charset DEFAULT_ENCODING = Charset.forName(System.getProperty("file.encoding"));

    public static Charset toCharset(Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    public static Charset toCharset(String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }

    public static String toString(Collection<String> collection){
        StringBuilder builder = new StringBuilder();
        for(String line: collection){
            builder.append(line).append(LINE_SEPARATOR);
        }
        return builder.toString();
    }


}
