package com.p4535992.util.html.scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
/**
 * Created by 4535992 on 05/05/2015.
 * @href: https://www.java.net/node/650704
 */
public class HtmlScanner {
    final Parser parser;

    public static void main(String[] args) throws IOException {
        HtmlScanner scanner = new HtmlScanner();
        File file = new File("C:\\Users\\Marco\\Desktop\\www.unifi.it.htm");
        final Map map = scanner.scanHierarchy(file);
        //sort keys by values
        List list = new ArrayList(map.keySet());
        Collections.sort(list,
                new Comparator() {
                    @Override
                    public int compare(Object key1, Object key2) {
                        return (Integer)map.get(key2) - (Integer)map.get(key1);
                    }
                });
        for (Object t : list) {
            System.out.format("%-10s %s\n", (Tag)t, map.get(t));
        }
    }
    public HtmlScanner() {
        parser = (new ScannerHTMLEditorKit()).getParser();
    }

    public Map scanHierarchy(File file) throws FileNotFoundException, IOException {
        Map map = new HashMap();
        scanHierarchyImpl(file, map);
        return map;
    }
    private void scanHierarchyImpl(File file, Map map) throws FileNotFoundException, IOException {
        if (file.isDirectory()) {
            for (File f :file.listFiles()) {
                scanHierarchyImpl(f, map);
            }
        } else if (file.isFile()) {
            String name = file.getName();
            if (name.endsWith(".html")
                    || name.endsWith(".htm")) {
                scan(file, map);
            }

        }
    }

    public void scan(File file, Map map) throws FileNotFoundException, IOException {
        scan(new FileReader(file), map);
    }
    public Map scan(File file) throws FileNotFoundException, IOException {
        return scan(new FileReader(file));
    }

    public void scan(Reader in, Map map) throws IOException {
        parser.parse(in, new ScannerParserCallback(map), true);
    }
    public Map scan(Reader in) throws IOException {
        Map map = new HashMap();
        scan(in, map);
        return map;
    }
    //we need this class only to get the default html parser
//the returned parser creates a new parser on every parse call
//so one parser is enough
    private static class ScannerHTMLEditorKit extends HTMLEditorKit {
        @Override
        public Parser getParser() {
            return super.getParser();
        }
    }
    private static class ScannerParserCallback extends ParserCallback {
        final Map map;
        ScannerParserCallback(Map map) {
            this.map = map;
        }
        @Override
        public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
            handleSimpleTag(t, a, pos);
        }
        @Override
        public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
            Integer integer =(Integer) map.get(t);
            int counter = (integer != null) ? integer.intValue() : 0;
            map.put(t, ++counter);
        }
    }
}
