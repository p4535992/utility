package com.github.p4535992.util.file.archive.sevenzipjbinding;

/**
 * Created by 4535992 on 27/12/2015.
 */
public class ExtractionException extends Exception {
    private static final long serialVersionUID = -5108931481040742838L;

    public ExtractionException(String msg) {
        super(msg);
    }

    public ExtractionException(String msg, Exception e) {
        super(msg, e);
    }
}
