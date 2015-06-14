package com.p4535992.util.html.parser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
/**
 * Created by 4535992 on 05/05/2015.
 */
public class PageSaver extends HTMLEditorKit.ParserCallback {

    private Writer out;

    private URL base;

    public PageSaver(Writer out, URL base) {
        this.out = out;
        this.base = base;
    }

    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        try {
            out.write("<" + tag);
            this.writeAttributes(attributes);
            if (tag == HTML.Tag.APPLET && attributes.getAttribute(HTML.Attribute.CODEBASE) == null) {
                String codebase = base.toString();
                if (codebase.endsWith(".htm") || codebase.endsWith(".html")) {
                    codebase = codebase.substring(0, codebase.lastIndexOf('/'));
                }
                out.write(" codebase=\"" + codebase + "\"");
            }
            out.write(">");
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void handleEndTag(HTML.Tag tag, int position) {
        try {
            out.write("</" + tag + ">");
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void writeAttributes(AttributeSet attributes) throws IOException {

        Enumeration e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            String value = (String) attributes.getAttribute(name);
            try {
                if (name == HTML.Attribute.HREF || name == HTML.Attribute.SRC
                        || name == HTML.Attribute.LOWSRC || name == HTML.Attribute.CODEBASE) {
                    URL u = new URL(base, value);
                    out.write(" " + name + "=\"" + u + "\"");
                } else {
                    out.write(" " + name + "=\"" + value + "\"");
                }
            } catch (MalformedURLException ex) {
                System.err.println(ex);
                System.err.println(base);
                System.err.println(value);
                ex.printStackTrace();
            }
        }
    }

    public void handleComment(char[] text, int position) {
        try {
            out.write("<!-- ");
            out.write(text);
            out.write(" -->");
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    public void handleText(char[] text, int position) {

        try {
            out.write(text);
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        try {
            out.write("<" + tag);
            this.writeAttributes(attributes);
            out.write(">");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

