package com.github.p4535992.util.html.parser;

import javax.swing.text.html.HTMLEditorKit;

public class ParserGetter extends HTMLEditorKit {
    private static final long serialVersionUID = 5L;
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}