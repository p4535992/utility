package com.p4535992.util.html.parser;

import javax.swing.text.html.HTMLEditorKit;

/**
 * Created by 4535992 on 05/05/2015.
 */
public class ParserGetter extends HTMLEditorKit {
    private static final long serialVersionUID = 5L;
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}