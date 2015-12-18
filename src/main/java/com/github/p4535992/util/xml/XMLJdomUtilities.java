package com.github.p4535992.util.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * note you need the JDOM2 library for use these
 * Created by 4535992 on 28/03/2015.
 * @author 4535992
 * @version 2015-06-29
 */
@SuppressWarnings("unused")
public class XMLJdomUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(XMLJdomUtilities.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    private static org.w3c.dom.Document w3cdoc;
    private static org.jdom2.Document jdom2doc;

    private static XMLJdomUtilities instance = null;
    protected XMLJdomUtilities(){ }
    public static XMLJdomUtilities getInstance(){
        if(instance == null) {
            instance = new XMLJdomUtilities();
        }
        return instance;
    }

    /**
     * Method for update the value of a attribute of a specific tag in a XML document.
     * @param xmlPath xml file of input.
     * @param tagName name of the tag.
     * @param nameAttribute name of the attribute.
     * @param newValueAttribute name of the value.
     * @return if true all the operation are done. 
     */
    public static boolean updateValueOfAttributeSAX(String xmlPath,String tagName,String nameAttribute,String newValueAttribute){
        try {
            org.jdom2.input.SAXBuilder builderSAX = new org.jdom2.input.SAXBuilder();
            File xmlFile = new File(xmlPath);
            // org.jdom2.DefaultJDOMFactory factory =   new org.jdom2.DefaultJDOMFactory();
            org.jdom2.Document doc = builderSAX.build(xmlFile);
            Element hibernate = doc.getRootElement();
            // update class table attribute
            Element class2 = hibernate.getChild(tagName);
            class2.getAttribute(nameAttribute).setValue(newValueAttribute);
            org.jdom2.output.XMLOutputter xmlOutput = new org.jdom2.output.XMLOutputter();
            // display nice nice
            xmlOutput.setFormat(org.jdom2.output.Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(xmlFile.getAbsolutePath()));
            return true;
        } catch (org.jdom2.JDOMException|IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }//updateValueOfattributeSAX

    /**
     * Method for convert a org.jdom2.Document to a org.w3c.dom document.
     * @param jdomdoc org.jdom2.Document of input.
     * @return org.w3c.dom.Document.
     */
    public static org.w3c.dom.Document convertJDOM2DOMDocument( org.jdom2.Document jdomdoc) {
        try {
            // insert JDOM to DOM converter:
            org.jdom2.output.DOMOutputter output = new org.jdom2.output.DOMOutputter();
            // here we have a DOM-document:
            w3cdoc = output.output(jdomdoc);
            return  w3cdoc;
        }catch(org.jdom2.JDOMException e){
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method for convert a org.w3c.dom.Document to a org.jdom2.Document.
     * @param w3cdoc org.w3c.dom.Document of input.
     * @return org.jdom2.Document.
     */
    public static org.jdom2.Document convertDOM2JDOMDocument(org.w3c.dom.Document w3cdoc){
        // we have a DOM-document as input:
        //org.w3c.dom.Document dom = getDOM(domDoc);
        // just convert:
        org.jdom2.input.DOMBuilder builder = new org.jdom2.input.DOMBuilder();
        jdom2doc = builder.build(w3cdoc);
        return jdom2doc;
    }


    public static boolean initAndWriteFromElemenrt(Element element,String xmlNameFile){
        if(!xmlNameFile.toLowerCase().endsWith(".xml")){
            xmlNameFile = xmlNameFile + ".xml";
        }
        try {
            SAXBuilder builder = new SAXBuilder();//
            jdom2doc = builder.build( xmlNameFile);
            Element root = jdom2doc.getRootElement();
            root.addContent(element);
            XMLOutputter out = new XMLOutputter();
            out.output(root, System.out);
            return true;
        } catch (JDOMException|IOException e) {
            logger.error(gm() + e.getMessage(), e);
            return false;
        }
    }


    /**
     * Method to init and write a XML Document.
     * Notice it is easier than using SAX or DOM directly.
     * @param file the File XML to create and init
     * @return if true the init and create operation of the XML file are succeded.
     */
    public static boolean initAndWriteFromFile(File file) {
        try {
            SAXBuilder builder = new SAXBuilder();//
            jdom2doc = builder.build(file);
            XMLOutputter out = new XMLOutputter();
            out.output(jdom2doc, System.out);
            return true;
        } catch (JDOMException|IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }


    /**
     * Method to init and write a XML JDOM2 Document.
     * Notice it is easier than using SAX or DOM directly.
     * @param doc the Document XML to create and init
     * @return if true the init and create operation of the XML file are succeded.
     */
    public static boolean initAndWriteFromJDOM2Document(Document doc){
        try {
            jdom2doc = doc;
            XMLOutputter out = new XMLOutputter();
            out.output(jdom2doc, System.out);
            return true;
        } catch (IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Method to find and get all the element froma specificXPath.
     * @param xpath the String of the Xpath.
     * @return the List of XML Element.
     */
    public static List<Element> findElementFromXPath(String xpath){
        XPathExpression<Element> expression =
                XPathFactory.instance()
                        .compile(xpath, Filters.element());
        return expression.evaluate(jdom2doc);
    }


    /** Generate the XML document
     * @return the XML Document with a specific strucuture.
     * @throws java.lang.Exception throw if any error is occurrred.
     */
    protected Document makeDoc() throws Exception {
        Document doc = new Document(new Element("Poem"));
        doc.getRootElement().
                addContent(new Element("Stanza").
                        addContent(new Element("Line").
                                setText("Once, upon a midnight dreary")).
                        addContent(new Element("Line").
                                setText("While I pondered, weak and weary")));

        return doc;
    }

    /**
     * Method to read the String content of a String XML text.
     * @param xmlText the String xml.
     * @return the the String of the text of the XML String.
     */
    public static List<List<Map<String,String>>> readContent(String xmlText){
        List<List<Map<String,String>>> listOfListsOfElement = new ArrayList<>();

        SAXBuilder builder = new SAXBuilder();
        org.jdom2.Document document;
        try {
            document = builder.build(new ByteArrayInputStream(xmlText.getBytes()));
            Element root = document.getRootElement();
            List<Element> rows = root.getChildren("row");
            for (Object row1 : rows) {
                Element row = (Element) row1;
                List<Element> columns = row.getChildren("column");

                List<Map<String, String>> listOfElement = new ArrayList<>();
                for (Object column1 : columns) {
                    Element column = (Element) column1;
                    String name = column.getAttribute("name").getValue();
                    String value = column.getText();
                    int length = column.getAttribute("length").getIntValue();
                    //System.out.println("name = " + name);
                    //System.out.println("value = " + value);
                    //System.out.println("length = " + length);

                    Map<String, String> mapOfContent = new HashMap<>();
                    mapOfContent.put("name", name);
                    mapOfContent.put("value", value);
                    mapOfContent.put("length", String.valueOf(length));
                    listOfElement.add(mapOfContent);
                }
                listOfListsOfElement.add(listOfElement);
            }
            return listOfListsOfElement;
        } catch (JDOMException|IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }

    }




}
