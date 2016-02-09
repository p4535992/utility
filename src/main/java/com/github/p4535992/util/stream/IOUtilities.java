package com.github.p4535992.util.stream;

/**
 * Created by 4535992 on 04/02/2016.
 * @author 4535992.
 */
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class IOUtilities {
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final char DIR_SEPARATOR;
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    public static final String LINE_SEPARATOR;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public IOUtilities() {}

    public static void closeQuietly(InputStream input) {
        try {
            if(input != null) {
                input.close();
            }
        } catch (IOException ignored) {}

    }

    public static void closeQuietly(OutputStream output) {
        try {
            if(output != null) {
                output.close();
            }
        } catch (IOException ignored) {}
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static String toString(InputStream input, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L?-1:(int)count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;

        int n1;
        for(boolean n = false; -1 != (n1 = input.read(buffer)); count += (long)n1) {
            output.write(buffer, 0, n1);
        }

        return count;
    }

    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        if(encoding == null) {
            copy(input, output);
        } else {
            InputStreamReader in = new InputStreamReader(input, encoding);
            copy(in, output);
        }

    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L?-1:(int)count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[4096];
        long count = 0L;

        int n1;
        for(boolean n = false; -1 != (n1 = input.read(buffer)); count += (long)n1) {
            output.write(buffer, 0, n1);
        }

        return count;
    }

    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        if(!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }

        if(!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch2;
        for(int ch = input1.read(); -1 != ch; ch = input1.read()) {
            ch2 = input2.read();
            if(ch != ch2) {
                return false;
            }
        }

        ch2 = input2.read();
        return ch2 == -1;
    }

    /**
     * Method to copy a file.
     *
     * @param input    the InputStream to copy.
     * @param output   the OutputStream where put the copy.
     * @param encoding the Charset encoding of the Stream.
     * @return if true all the operation are done.
     */
    public static boolean copy(InputStream input, OutputStream output, Charset encoding) {
        InputStreamReader in = new InputStreamReader(input, encoding);
        OutputStreamWriter out = new OutputStreamWriter(output, encoding);
        long count = copyLargeSimple(in, out);
        return !(count == -1 || count > Integer.MAX_VALUE);
    }

    /**
     * Method to copy a file.
     *
     * @param input  the Reader input.
     * @param output the Writer Output
     * @return the count of the characters in the Stream.
     */
    private static long copyLargeSimple(Reader input, Writer output) {
        //copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE])
        char[] buffer = new char[1024 * 4];
        long count = 0;
        int n; // n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            return -1;
        }
    }

    static {
        DIR_SEPARATOR = File.separatorChar;
        StringWriter buf = new StringWriter(4);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
    }
}
