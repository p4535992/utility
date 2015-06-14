package com.p4535992.util.html;

import com.p4535992.util.html.parser.ReportAttributes;
import com.p4535992.util.html.parser.ParserGetter;

import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by 4535992 on 05/05/2015.
 */
public class Test_Html {

    public static void main(String[] args) throws Exception {
        String encoding = "UTF-8";
        URL url = new URL("http://www.java2s.com");
        InputStream is = url.openStream();
        //TEST 1
        /*
        HtmlKit.getParser(in,encoding);
        HtmlKit.parse(url, encoding);
        */

        //TEST 2
        ParserGetter kit = new ParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        HTMLEditorKit.ParserCallback callback = new ReportAttributes();

        try {
            InputStreamReader r = new InputStreamReader(is);
            parser.parse(r, callback, true);
        } catch (IOException e) {
            System.err.println(e);
        }


    }
}
