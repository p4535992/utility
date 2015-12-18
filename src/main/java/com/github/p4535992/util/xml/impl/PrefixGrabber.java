package com.github.p4535992.util.xml.impl;

import java.util.HashMap;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.jar.Attributes;

/**
  * A SAX ContentHandler to find the prefixes declared on the root element.
 * I don't own any right on this piece of code that belongs to Norman Walsh ,
 * i just modified for my purpose
  * @author Norman Walsh
  * @version $Revision: 1.1 $
  */
@SuppressWarnings("unused")
public class PrefixGrabber extends DefaultHandler {
    private final HashMap<String,String> nsHash = new HashMap<>();
    private boolean root = true;

    public HashMap<String,String> getNamespaces() {
        return nsHash;
    }

    @Override
    public void startPrefixMapping (String prefix, String uri) throws SAXException {
        if (root) {
            nsHash.put(prefix, uri);
        }
    }

    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
            root = false;
    }
}//prefixgrabber
