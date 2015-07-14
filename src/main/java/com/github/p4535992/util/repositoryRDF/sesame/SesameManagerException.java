package com.github.p4535992.util.repositoryRDF.sesame;

/**
 * Created by 4535992 on 13/06/2015.
 * @author unknow.
 * @version 2015-07-02.
 */
@SuppressWarnings("unused")
public class SesameManagerException  extends RuntimeException {
    private static final long serialVersionUID = 3L;
    public SesameManagerException() {}

    public SesameManagerException(String message) {
        super(message);
    }

    public SesameManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SesameManagerException(Throwable e) {
        super(e);
    }
    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace(java.io.PrintStream s) {
        s.flush();
        super.printStackTrace(s);
        Throwable cause = getCause();
        if (cause != null) {
            s.print("Caused by:\n");
            cause.printStackTrace(s);
        }
    }

    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        s.flush();
        super.printStackTrace(s);
        Throwable cause = getCause();
        if (cause != null) {
            s.print("Caused by:\n");
            cause.printStackTrace(s);
        }
    }
}
