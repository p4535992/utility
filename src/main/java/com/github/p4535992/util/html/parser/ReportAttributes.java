package com.github.p4535992.util.html.parser;

import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class ReportAttributes  extends HTMLEditorKit.ParserCallback {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ReportAttributes.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        this.listAttributes(attributes);
    }

    private void listAttributes(AttributeSet attributes) {
        Enumeration<?> e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            Object value = attributes.getAttribute(name);
            if (!attributes.containsAttribute(name.toString(), value)) {
                logger.info(gm() + "containsAttribute() fails");
            }
            if (!attributes.isDefined(name.toString())) {
                logger.info(gm() + "isDefined() fails");
            }
            logger.info(gm() + name + "=" + value);
        }
    }

    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        this.listAttributes(attributes);
    }
}
