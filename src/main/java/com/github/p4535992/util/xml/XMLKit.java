package com.github.p4535992.util.xml;

import org.xml.sax.InputSource;
import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.github.p4535992.util.log.SystemLog;

import java.io.*;
import java.util.*;
import java.util.jar.Attributes;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Little Utility for Create,Read,Delete  adn Update XML File without Third Library JAVA
 * Created by 4535992 on 28/03/2015.
 * @author 4535992.
 * @version 2015-06-29.
 */
@SuppressWarnings("unused")
public class XMLKit {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(XMLKit.class);
    private static DocumentBuilderFactory docFactory;
    private static DocumentBuilder docBuilder;
    private static Document doc;
    public static Map<String,String> namespaces = new Hashtable<>();


    private static XMLKit instance = null;
    protected XMLKit(){ }
    public static XMLKit getInstance(){
        if(instance == null) {
            instance = new XMLKit();
        }
        return instance;
    }

    /**
     * Method to load the XML Document.
     * @param fileXML the XML file to input.
     * @return the document object initialize.
     * @throws SAXException error.
     * @throws IOException error.
     */
    public static Document loadDocumentFromFile(File fileXML) throws SAXException, IOException {
        //FileInputStream file = new FileInputStream(fileXML); //optional
        doc = newDocumentXML();
        doc = docBuilder.parse(fileXML);
        SystemLog.message("Documento W3C caricato da file:" + fileXML.getAbsolutePath());
        return doc;
    }

    /**
     * Method to load a XML Document.
     * @param xml string xml.
     * @return the document object initialize.
     * @throws SAXException error.
     * @throws IOException error.
     */
    public static Document loadDocumentFromFile(String xml) throws SAXException, IOException{
        doc = newDocumentXML();
        doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        SystemLog.message("Documento W3C caricato da file:" + xml);
        return doc;
    }

    /**
     * Method to initialize a new XML document.
     * @return the document object initialize.
     */
    public static Document newDocumentXML(){
        try{
            docFactory = DocumentBuilderFactory.newInstance();
            //this line of code not work properly
            //docFactory.setValidating(false);
            //this line of code evite to search the validation parameter just if you want read the xml coe withourt meta information
            docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        }catch(ParserConfigurationException pe){
            pe.printStackTrace();
        }
        return doc;
    }

    /**
     * Method to insert a new XML file.
     * @param pathFile where you want to insert the new file.
     * @param nameFile name of the XML file.
     */
    public static void creatXMLFile(String pathFile,String nameFile){
        String path = pathFile+File.separator+nameFile;
        try {
            doc = newDocumentXML();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
            SystemLog.message("File " + path + "saved!");
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            //log.write(tfe.getMessageAndLocation(),"ERROR");
            Logger.getLogger(XMLKit.class.getName()).log(Level.SEVERE, null, tfe);
        }
    }

    /**
     * Method for update the value of a attribute
     * @param xmlFile file xml.
     * @param tagName string rootTag.
     * @param nameAttribute string name of attribute.
     * @param newValueAttribute string new value attribute.
     * @throws TransformerException error.
     * @throws IOException error.
     * @throws SAXException error.
     */
    public static void updateValueOfAttribute(File xmlFile,String tagName,String nameAttribute,String newValueAttribute)
            throws TransformerException, IOException, SAXException {
            doc =  loadDocumentFromFile(xmlFile);
            Element el = selectFirstElementByAttribute(tagName, nameAttribute);
            //get map containing the attributes of this node
            NamedNodeMap attributes = el.getAttributes();
            //get the number of nodes in this map
            int numAttrs = attributes.getLength();
            for(int i =0; i < numAttrs; i++){
                Attr attr = (Attr) attributes.item(i);
                if(Objects.equals(attr.getNodeName(), nameAttribute)){
                    attr.setValue(newValueAttribute);
                    SystemLog.message("Update the value of the attribute:"+attr.getName()+ "="+newValueAttribute);
                    break;
                }
            }
            saveToXml(doc, xmlFile.getAbsolutePath());
    }

    public static void updateValueOfAttribute(String tagName,String nameAttribute,String newValueAttribute)
            throws TransformerException, IOException, SAXException {
        Element el = selectFirstElementByAttribute(tagName, nameAttribute);
        //get map containing the attributes of this node
        NamedNodeMap attributes = el.getAttributes();
        //get the number of nodes in this map
        int numAttrs = attributes.getLength();
        for(int i =0; i < numAttrs; i++){
            Attr attr = (Attr) attributes.item(i);
            if(Objects.equals(attr.getNodeName(), nameAttribute)){
                attr.setValue(newValueAttribute);
                SystemLog.message("Update the value of the attribute:"+attr.getName()+ "="+newValueAttribute);
                break;
            }
        }
    }

    public static void updateValueOfInnerText(Element element,String tagName,String newContent){
        Element root = doc.getDocumentElement();
        NodeList rootlist = root.getChildNodes();
        for(int i=0; i<rootlist.getLength(); i++) {
            Element theTagFirstLevel = (Element)rootlist.item(i);
            if(theTagFirstLevel.getTagName().equalsIgnoreCase(tagName)) {
                NodeList personlist = theTagFirstLevel.getChildNodes();
                Element name = (Element) personlist.item(0);
                NodeList namelist = name.getChildNodes();
                Text nametext = (Text) namelist.item(0);
                String oldname = nametext.getData();
                if (!oldname.equals(newContent)) {
                    nametext.setData(newContent);
                    SystemLog.message("Update the content of the tag:"+tagName+ "="+newContent);
                }
                break;
            }

        }
    }

    /**
     * Get an Attribute from an Element.  Returns an empty String if none found
     * http://www.java2s.com/Code/Java/XML/ReturnalistofnamedElementswithaspecificattributevalue.htm
     * @param element the containing Element.
     * @param name the attribute name.
     * @return Attribute as a String.
     */
    public static String getAttribute(Element element, String name) {
        return element.getAttribute(name);
    }

    /**
     * Method to add a new attribute to a Element of the document XML.
     * @param element the element where add the new attribute.
     * @param newAttribute the nea attribute toadd.
     * @param valueAttribute the value of the new attribute.
     */
    public static void addAttribute(Element element,String newAttribute,String valueAttribute){
        Element person = (Element)element.getFirstChild();
        person.setAttribute(newAttribute,valueAttribute);
    }

    /**
     * Method to return a list of named Elements with a specific attribute value.
     * http://www.java2s.com/Code/Java/XML/ReturnalistofnamedElementswithaspecificattributevalue.htm
     * @param element the containing Element.
     * @param name the tag name.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @param returnFirst Return only the first matching value?.
     * @return List of matching elements.
     */
    public static List<Node> selectElementsByAttributeValue(
            Element element, String name,String attribute, String value,boolean returnFirst) {
        NodeList  elementList = element.getElementsByTagName(name);
        List<Node> resultList  = new ArrayList<>();
        for (int i = 0; i < elementList.getLength(); i++) {
            if (getAttribute((Element) elementList.item(i), attribute).equals(value)) {
                resultList.add(elementList.item(i));
                if (returnFirst) {
                    break;
                }
            }
        }
        return resultList;
    }

    /**
     * Method t o return a single first Element with a specific attribute value. 
     * (maybe you can find a better method))
     * @param tagName string of the name tag xml.
     * @param nameAttribute string of the name attribute xml.
     * @return the element xml with the specific attribute.
     */
    public static Element selectFirstElementByAttribute(String tagName,String nameAttribute) {
        Element el = doc.getDocumentElement(); //get root element
        NodeList  elementList = el.getElementsByTagName(tagName);
        for (int i = 0; i < elementList.getLength(); i++) {
            if (getAttribute((Element) elementList.item(i), nameAttribute) != null) {
                el = (Element) elementList.item(i);
                break;
            }
        }
        return el;
    }

    /**
     * Method get all attributes in a xml element.
     * @param el xml element on input.
     * @return array list of attributes.
     */
    public static List<Attr> getAllAttributes(Element el){
        List<Attr> listAttr = new ArrayList<>();
        NamedNodeMap attributes = el.getAttributes();//get map containing the attributes of this node
        int numAttrs = attributes.getLength();  //get the number of nodes in this map
        for(int i =0; i < numAttrs; i++){
            Attr attr = (Attr) attributes.item(i);
            listAttr.add(attr);
        }
        return listAttr;
    }

    /**
     * Get root element.
     * @return element root of the xml document.
     */
    public static Element getRootElement(){
        return doc.getDocumentElement();
    }

    /**
     * Method for print on the console the content of a xml file.
     * @param xmlFile the xml file in input.
     * @throws IOException error.
     * @throws SAXException error.
     */
    public static void readXMLFileAndPrint(File xmlFile) throws IOException, SAXException {
        //File xmlFile = new File("/Users/mkyong/staff.xml");
        Document doc = loadDocumentFromFile(xmlFile);
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        SystemLog.message("Root element XML document:" + doc.getDocumentElement().getNodeName());
        String tagName = doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName(tagName);
        for (int temp = 0; temp < nList.getLength(); temp++){
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            //NodeList nList = doc.getElementsByTagName("staff");
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
               //you can do something wtih this node
            }
        }
        if (doc.hasChildNodes()) {
            printNode(doc.getChildNodes());
        }
    }

    /**
     * Method to print a single node of a xml document.
     * @param nodeList lista of nodes of a xml document.
     */
    private static void printNode(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());
                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());
                    }
                }
                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNode(tempNode.getChildNodes());
                }
                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
            }
        }
    }


    /*
     * Metodo per l'update del valore di un attributo di un tag in un XML Document with SAX JDOM
     * @param xmlFile file xml.
     * @param tagName string tag root xml file.
     * @param nameAttribute string name of attribute.
     * @param newValueAttribute string new value attribute.
     */
    /*public static void updateValueOfattributeWithXPath(File xmlFile,String tagName,String nameAttribute,
                           String newValueAttribute) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
            doc = loadDocumentFromFile(xmlFile);
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String expression = ".//"+tagName+"[0]";
            //String result = xPath.compile(expression).evaluate(xmlDocument);
            Node result = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
            NamedNodeMap l = result.getAttributes();

    }//updateValueOfattributeSAX*/

    /**
     * Method for update the value of a attribute.
     * @param xmlFile file xml.
     * @param tagName string tag root xml file.
     * @param nameAttribute string name of attribute.
     * @param newValueAttribute string new value attribute.
     * @throws ParserConfigurationException error.
     * @throws SAXException error.
     * @throws XPathExpressionException error.
     * @throws IOException error.
     * @throws TransformerException error.
     */
    public void updateValueofAttribute(File xmlFile,String tagName,String nameAttribute,
              String newValueAttribute) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, TransformerException {
        doc = loadDocumentFromFile(xmlFile);
        Node n = doc.getDocumentElement().getElementsByTagName(tagName).item(0);
        Element e = convertNodeToElement(n);
        e.setAttribute(nameAttribute,newValueAttribute);
        saveToXml(doc, xmlFile.getAbsolutePath());
    }

    /**
     * To get DOM Document from the xml file.
     * @param filePath string path to the file.
     * @return DOM Document.
     * @throws ParserConfigurationException error.
     * @throws SAXException error.
     * @throws IOException error.
     */
    public static Document getDocument(String filePath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(filePath);
    }

    /**
     * Method To save the Document in xml file.
     * @param xmlDoc the XML Docuemnt you wan to save.
     * @param filePath where you want ot save the file.
     * @throws TransformerException error.
     */
    public static void saveToXml(Document xmlDoc, String filePath) throws TransformerException {
        DOMSource source = new DOMSource(xmlDoc);
        StreamResult result = new StreamResult(new File(filePath));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DocumentType doctype = xmlDoc.getDoctype();
        if(doctype != null) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
        }
        transformer.transform(source, result);
    }

    /**
     * Method to convert a Node XML to a Element XML.
     * @param node node XML to input.
     * @return elment XML.
     */
    public static Element convertNodeToElement(Node node){
        NodeList list = node.getChildNodes();
        Element m = null;
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            //Node n = elem.getFirstChild();
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                m = (Element) n;
            }
        }
        return m;
    }

    /**
     * Method to convert a Element XML to a Node XML.
     * @param elem element XML to input.
     * @return node XML.
     */
    public static Node convertElementToNode(Element elem){
        return elem.getFirstChild();
    }

    /**
     * Method to convert a NODE XML to a string.
     * @param n node XML to input.
     * @return string of the node n.
     */
    public static String convertElementToString(Node n) {
        String name = n.getNodeName();
        short type = n.getNodeType();
        if (Node.CDATA_SECTION_NODE == type) {
            return "<![CDATA[" + n.getNodeValue() + "]]&gt;";
        }
        if (name.startsWith("#")) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(name);
        NamedNodeMap attrs = n.getAttributes();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                sb.append(' ').append(attr.getNodeName())
                        .append("=\"").append(attr.getNodeValue()).append("\"");
            }
        }
        String textContent;
        NodeList children = n.getChildNodes();
        if (children.getLength() == 0) {
            if ((textContent = n.getTextContent()) != null && !"".equals(textContent)) {
                sb.append(textContent).append("</").append(name).append('>');
                //;
            } else {
                sb.append("/>").append('\n');
            }
        } else {
            sb.append('>').append('\n');
            boolean hasValidChildren = false;
            for (int i = 0; i < children.getLength(); i++) {
                String childToString = convertElementToString(children.item(i));
                if (!"".equals(childToString)) {
                    sb.append(childToString);
                    hasValidChildren = true;
                }
            }
            if (!hasValidChildren && ((textContent = n.getTextContent()) != null)) {
                sb.append(textContent);
            }
            sb.append("</").append(name).append('>');
        }
        return sb.toString();
    }

    /**
     * Load XML document and parse it into DOM.
     * @param input input stream.
     */
    public static void loadXML(InputStream input) {
        try {
            // Create Document Builder Factory
            docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setValidating(false);
            // Create Document Builder
            docBuilder = docFactory.newDocumentBuilder();
            docBuilder.isValidating();
            // Disable loading of external Entityes
            docBuilder.setEntityResolver(new EntityResolver() {
                // Dummi resolver - alvays do nothing
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }

            });
            // open and parse XML-file
            doc = docBuilder.parse(input);
        } catch (SAXException|IOException|ParserConfigurationException e) {
            SystemLog.exception(e);
        }
    }


    /**
     * Method to Check the name of root element is as expected.
     * @param name name of the root tag.
     * @return boolean value.
     */
    public static boolean isRootName(String name) {
        return getRootElement().getNodeName().equals(name);
    }

    /**
     * Get the type of the xml doc.
     * @return string of the type.
     */
    public static String getDoctype() {
        DocumentType doctype = doc.getDoctype();
        if (null != doctype) {
            return doctype.getName();
        }
        return null;
    }

    /**
     * Get the public id of the xml doc.
     * @return string of the public id.
     */
    public static String getPublicId() {
        DocumentType doctype = doc.getDoctype();
        if (null != doctype) {
            return doctype.getPublicId();
        }
        return null;
    }

    /**
     * Get the type of the root type name of the xml doc.
     * @return string of the name of root type.
     */
    public static String getRootTypeName() {
        return getRootElement().getSchemaTypeInfo().getTypeName();
    }

    /**
     * Get the content of a  xml doc.
     * @return string of the content o a  xml doc.
     * @throws Exception error.
     */
    public static String getContent() throws Exception {
        NodeList childNodes = getRootElement().getChildNodes();
        return serializeNodes(childNodes);
    }

    /**
     * Support the getCOntent() method.
     * @param childNodes nde list of input.
     * @return the string of the nodelist of input.
     * @throws Exception error.
     */
    private static String serializeNodes(NodeList childNodes) throws Exception {
        DocumentFragment fragment =doc.createDocumentFragment();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            StringWriter out = new StringWriter();
            StreamResult result = new StreamResult(out);
            transformer.transform(new DOMSource(fragment), result);
            return out.toString();

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String getContent(String xpath) throws Exception{
        XPath path = XPathFactory.newInstance().newXPath();
        NodeList childNodes;
        try {
            childNodes = (NodeList) path.evaluate(xpath,doc,XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new Exception("Error evaluate xpath",e);
        }
        return serializeNodes(childNodes);
    }

    /**
     * Simple transformation method using Saxon XSLT 2.0 processor.
     * @param sourcePath - Absolute path to source GML file.
     * @param xsltPath - Absolute path to XSLT file.
     * @param resultPath - Absolute path to the resulting RDF file.
     */
    public static void saxonTransform(String sourcePath, String xsltPath, String resultPath) {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try
        {
            Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xsltPath)));
            transformer.transform(new StreamSource(new File(sourcePath)), new StreamResult(new File(resultPath)));
            SystemLog.message("XSLT transformation completed successfully.\nOutput writen into file: " + resultPath );
        }
        catch (Exception e) {  SystemLog.exception(e);  }
    }
    


    public static void checkPrefix(String qname)throws Exception{
        if(qname == null)
        {
            throw new Exception("Unexpected null QName");
        }

        if(qname.indexOf(":") <= 0)
        {
            throw new Exception("Missing prefix: " + qname);
        }
    }

     public static void checkQName(String qname) throws Exception {
        checkPrefix(qname);
        if(qname.indexOf(":") == qname.length())
        {
            throw new Exception("Missing local name: " + qname);
        }
    }

    /**
     * Determine if the specified string satisfies the constraints of an XML Name.
     * This code is seriously incomplete.
     * @param str string to check.
     * @return if the string is the name of the xml file.
     */
    public static boolean isXMLName(String str)
    {
      char name[] = str.toCharArray();
      if(name[0] == '_'
      || Character.isLetter(name[0]))
      {
          for (char aName : name) {
              if (!Character.isLetter(aName)
                      && !Character.isDigit(aName)
                      && aName != '.'
                      && aName != '-'
                      && aName != '_') {
                  return false;
              }
          }
        return true;
      }
      return false;
    }

    public static String xmlDecode(String text) throws Exception {
      String origText = text;
      String newText = "";
      while(text.contains("&"))
      {
        int pos = text.indexOf("&");
        newText += text.substring(0, pos);
        text = text.substring(pos + 1);
        pos = text.indexOf(";");
        if(pos <= 0)
        {
          throw new Exception("Improperly escaped character: "+ origText);
        }
        String charref = text.substring(0, pos);
        text = text.substring(pos + 1);

        if(charref.equals("lt"))
        {
          newText += "<";
        }
        else if(charref.equals("gt"))
        {
          newText += ">";
        }
        else if(charref.equals("amp"))
        {
          newText += "&";
        }
        else if(charref.equals("quot"))
        {
              newText += "\"";
        }
        else if(charref.equals("apos"))
        {
          newText += "'";
        }
        else if(charref.startsWith("#"))
        {
          String number = charref.substring(1);
          int radix = 10;

          if(charref.startsWith("#x")
              || charref.startsWith("#X"))
              {
                number = charref.substring(2);
                radix = 16;
          }

          if("".equals(number))
          {
              throw new Exception("Improperly escaped character: "+ charref);
          }
          char ch;
          try
          {
            ch =
            (char) Integer.parseInt(
            number,
            radix);
          }
          catch(NumberFormatException nfe)
          {
              throw new Exception("Improperly escaped character: "+ charref);
          }
          newText += ch;
        }
        else
        {
            throw new Exception("Improperly escaped character: "+ charref);
        }
      }//while
      return newText + text;
    }

    public static void getNamespaces(String xmlFile) {
        // Construct a SAX Parser using JAXP
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // For this app, namespaces and validity are irrelevant
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Our handler will actually count the words
        PrefixGrabber handler = new PrefixGrabber();

        try
        {
            // Construct the parser and
            SAXParser parser = factory.newSAXParser();
            // use it to parse the document
            parser.parse(xmlFile, handler);
          }
          catch(Exception e)
          {
            // Maybe FileNotFound, maybe something else, anyway, life goes
            // on...
            return;
          }

          // Add any newly discovered prefixes to the namespace bindings
          Hashtable<String,String> docNamespaces = handler.getNamespaces();
          Enumeration<String> document = docNamespaces.keys();
          while(document.hasMoreElements())
          {
            String prefix = document.nextElement();
            if(!namespaces.containsKey(prefix))
            {
              namespaces.put(prefix, docNamespaces.get(prefix));
            }
          }//while
    }//getNameSpaces

    public static String xmlEncode(String rawtext) {
        // Now turn that UTF-8 string into something "safe"
        String rdfString ="<?xml version='1.0' encoding='ISO-8859-1'?>\n";
        char[] sbuf = rawtext.toCharArray();

        int lastPos = 0;
        int pos = 0;
        while(pos < sbuf.length)
        {
            char ch = sbuf[pos];
            if(!(ch == '\n' || (ch >= ' ' && ch <= '~')))
            {
                if(pos > lastPos)
                {
                    String range =new String(sbuf,lastPos,pos - lastPos);
                    rdfString += range;
                }
                rdfString += "&#" + (int) ch + ";";
                lastPos = pos + 1;
            }
            pos++;
        }
        if(pos > lastPos)
        {
            String range =  new String(sbuf, lastPos, pos - lastPos);
            rdfString += range;
        }
        return rdfString;
    }//xmlEncode
}

    

