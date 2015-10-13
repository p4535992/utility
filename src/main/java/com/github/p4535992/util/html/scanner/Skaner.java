package com.github.p4535992.util.html.scanner;
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
import java.io.*;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.Parser;

/**
 * Created by 4535992 on 05/05/2015.
 * href : https://www.java.net/node/650704
 */
public class Skaner {
        final Parser parser;
        @SuppressWarnings({"unchecked","rawtypes"})
        public static void main(String[] args) throws IOException {
            Skaner scanner = new Skaner();
            File file = new File("C:\\Users\\Marco\\Desktop\\www.unifi.it.htm");
            final Map<Tag,Object> map = scanner.scanHierarchy(file);
            //sort keys by values
            List<Object> list = new ArrayList(map.keySet());
            Collections.sort(list,
                    new Comparator() {
                        public int compare(Object key1, Object key2) {
                            return (Integer)map.get(key2) - (Integer)map.get(key1);
                        }
                    });
            for (Object t : list) {
                System.out.format("%-10s %s\n", t, map.get(t));
            }
        }
        public Skaner() {
            parser = (new ScannerHTMLEditorKit()).getParser();
        }

        public Map<Tag,Object> scanHierarchy(File file) throws FileNotFoundException, IOException {
            Map<Tag,Object> map = new HashMap<>();
            scanHierarchyImpl(file, map);
            return map;
        }
        private void scanHierarchyImpl(File file, Map<Tag,Object> map) throws FileNotFoundException, IOException {
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

        public void scan(File file, Map<Tag,Object> map) throws IOException {
            scan(new BufferedReader(new FileReader(file)), map);
        }
        public Map<Tag,Object> scan(File file) throws IOException {
            return scan(new BufferedReader(new FileReader(file)));
        }

        public void scan(Reader in, Map<Tag,Object> map) throws IOException {
            parser.parse(in, new ScannerParserCallback(map), false);
        }
        public Map<Tag,Object> scan(Reader in) throws IOException {
            Map<Tag,Object> map = new HashMap<>();
            scan(in, map);
            return map;
        }
        //we need this class only to get the default html parser
        //the returned parser creates a new parser on every parse call
        //so one parser is enough
        private static class ScannerHTMLEditorKit extends HTMLEditorKit {
            
            private final static long serialVersionUID = 10L;
            
            @Override
            public Parser getParser() {
                return super.getParser();
            }
        }

        HTMLEditorKit.ParserCallback callback =
                new HTMLEditorKit.ParserCallback () {
                    public void handleText(char[] data, int pos) {
                        System.out.println(data);
                    }
                };

        private static class ScannerParserCallback extends HTMLEditorKit.ParserCallback {
            final Map<Tag,Object> map;
            ScannerParserCallback(Map<Tag,Object> map) {this.map = map;}
            @Override
            public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
                System.out.println("hhhhhhh"+t.toString());
                if ((t.toString()).compareTo("table")==0){
                    System.out.println("TABELKA SIE ZACZYNA");
                }
                if ((t.toString()).compareTo("td")==0){
                    System.out.println("mamy kolumne nowa ");

                }
                handleSimpleTag(t, a, pos);
            }
            @Override
            public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
                Integer integer = (Integer)map.get(t);
                int counter = (integer != null) ? integer : 0;
                map.put(t, ++counter);
            }

            public void handleText(char[] data, int pos) {
                System.out.println(data);
            }

            public void handleEndTag(Tag t,MutableAttributeSet a,int pos){
                if ((t.toString()).compareTo("/table")==0){
                    System.out.println("TABELKA SIE KONCZY");
                }
                System.out.println("aaaasssssssssssssss");
            }
        }
}
