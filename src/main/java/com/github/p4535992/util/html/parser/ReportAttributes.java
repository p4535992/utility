package com.github.p4535992.util.html.parser;
import com.github.p4535992.util.log.SystemLog;

import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class ReportAttributes  extends HTMLEditorKit.ParserCallback {

    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        this.listAttributes(attributes);
    }

    private void listAttributes(AttributeSet attributes) {
        Enumeration<?> e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            Object value = attributes.getAttribute(name);
            if (!attributes.containsAttribute(name.toString(), value)) {
                SystemLog.console("containsAttribute() fails");
            }
            if (!attributes.isDefined(name.toString())) {
                SystemLog.console("isDefined() fails");
            }
            SystemLog.console(name + "=" + value);
        }
    }

    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        this.listAttributes(attributes);
    }
}
