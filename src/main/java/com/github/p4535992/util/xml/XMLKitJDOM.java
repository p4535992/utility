package com.github.p4535992.util.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * note you need the JDOM2 library for use these
 * Created by 4535992 on 28/03/2015.
 * @author 4535992
 * @version 2015-06-29
 */
@SuppressWarnings("unused")
public class XMLKitJDOM {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(XMLKitJDOM.class);

    private static XMLKitJDOM instance = null;
    protected XMLKitJDOM(){ }
    public static XMLKitJDOM getInstance(){
        if(instance == null) {
            instance = new XMLKitJDOM();
        }
        return instance;
    }

    /**
     * Method for update the value of a attribute of a specific tag in a XML document
     * @param xmlPath xml file of input
     * @param tagName name of the tag
     * @param nameAttribute name of the attribute
     * @param newValueAttribute name of the value
     */
    public static void updateValueOfAttributeSAX(String xmlPath,String tagName,String nameAttribute,String newValueAttribute){
        try {
            org.jdom2.input.SAXBuilder builderSAX = new org.jdom2.input.SAXBuilder();
            File xmlFile = new File(xmlPath);
            // org.jdom2.DefaultJDOMFactory factory =   new org.jdom2.DefaultJDOMFactory();
            org.jdom2.Document doc = builderSAX.build(xmlFile);
            org.jdom2.Element hibernate = doc.getRootElement();
            // update class table attribute
            org.jdom2.Element class2 = hibernate.getChild(tagName);
            class2.getAttribute(nameAttribute).setValue(newValueAttribute);

            // add new age element
            //org.jdom.Element age = new org.jdom.Element("age").setText("28");
            //staff.addContent(age);

            // update salary value
            //staff.getChild("salary").setText("7000");

            // remove firstname element
            //staff.removeChild("firstname");
            org.jdom2.output.XMLOutputter xmlOutput = new org.jdom2.output.XMLOutputter();
            // display nice nice
            xmlOutput.setFormat(org.jdom2.output.Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(xmlFile.getAbsolutePath()));
        } catch (org.jdom2.JDOMException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }//updateValueOfattributeSAX

    /**
     * Method for convert a org.jdom2.Document to a org.w3c.dom document.
     * @param jdomdoc org.jdom2.Document of input.
     * @return org.w3c.dom.Document.
     * @throws org.jdom2.JDOMException error.
     */
    public static org.w3c.dom.Document convertJDOM2DOMDocument( org.jdom2.Document jdomdoc) throws org.jdom2.JDOMException{
        // insert JDOM to DOM converter:
        org.jdom2.output.DOMOutputter output = new org.jdom2.output.DOMOutputter();
        // here we have a DOM-document:
        org.w3c.dom.Document w3cdoc = output.output(jdomdoc);
        return  w3cdoc;
    }

    /**
     * Method for convert a org.w3c.dom.Document to a org.jdom2.Document
     * @param w3cdoc org.w3c.dom.Document of input
     * @return org.jdom2.Document
     */
    public static org.jdom2.Document convertDOM2JDOMDocument(org.w3c.dom.Document w3cdoc){
        // we have a DOM-document as input:
        //org.w3c.dom.Document dom = getDOM(domDoc);
        // just convert:
        org.jdom2.input.DOMBuilder builder = new org.jdom2.input.DOMBuilder();
        org.jdom2.Document jdomdoc = builder.build(w3cdoc);
        return jdomdoc;
    }


}
