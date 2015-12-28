package com.github.p4535992.util.xml;

import com.github.p4535992.util.string.StringUtilities;
import com.github.p4535992.util.xml.impl.PrefixGrabber;
import com.github.p4535992.util.xml.impl.TreeWalkerImpl;
import org.w3c.dom.html.HTMLTitleElement;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
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
 * I dont' own all the code i put some of the reference to the page
 * where i find some useful method.
 * href: http://www.java2s.com/Code/Java/XML/CopyanXMLdocument.htm
 * href: http://www.java2s.com/Code/Java/XML/W3CDOMutilitymethods.htm
 * href: http://www.java2s.com/Code/Java/XML/Getthecontentofthegivenelement.htm
 * @author 4535992.
 * @version 2015-06-29.
 */
@SuppressWarnings("unused")
public class XMLUtilities {

    private static final org.slf4j.Logger logger = 
            org.slf4j.LoggerFactory.getLogger(XMLUtilities.class);
    
    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }


    //FIELD
    private static javax.xml.parsers.DocumentBuilderFactory docFactory;
    private static javax.xml.parsers.DocumentBuilder docBuilder;
    private static org.w3c.dom.Document doc;
    
    private static final java.util.Queue<DocumentBuilder> builders = new java.util.concurrent.ConcurrentLinkedQueue<>();

    //public static Map<String,String> namespaces = new Hashtable<>();


    private static XMLUtilities instance = null;
    protected XMLUtilities(){ }
    public static XMLUtilities getInstance(){
        if(instance == null) {
            instance = new XMLUtilities();
        }
        return instance;
    }

    public static XMLUtilities getNewInstance(){
        instance = new XMLUtilities();
        return instance;
    }

    /**
     * Method to load the XML Document from file.
     * @param fileXML the XML file to input.
     * @return the document object initialize.
     */
    public static Document loadDocumentFromFile(File fileXML){
        try{
            //FileInputStream file = new FileInputStream(fileXML); //optional
            doc = initDocumentXML();
            doc = docBuilder.parse(fileXML);
            logger.info("Document W3C loaded from file:" + fileXML.getAbsolutePath());
         }catch (SAXException|IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
        return doc;
    }

    /**
     * Method to load a XML Document from String.
     * @param xml string xml.
     * @return the document object initialize.
     */
    public static Document loadDocumentFromFile(String xml) {
        try {
            doc = initDocumentXML();
            //doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
            doc = docBuilder.parse(new InputSource(new StringReader(xml)));
            logger.info("Document W3C loaded from file:" + xml);
            return doc;
        }catch (SAXException|IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to load a XML Document from inputstream.
     * @param in the InputStream of the XML document.
     * @return the document object initialize.
     */
    public static Document loadDocumentFromInputStream(InputStream in){
        try {
            doc = initDocumentXML();
            doc = docBuilder.parse(new InputSource(in));
            //doc = docBuilder.parse(new InputSource(new StringReader(xml)));
            logger.info("Document W3C loaded from stream:" + in);
        }catch (SAXException|IOException e) {
            logger.error(gm() + e.getMessage(),e);
            return null;
        }
        return doc;
    }

    /**
     * Method to load a XML Document from specific element XML.
     * @param fileXML the File of the XML document.
     * @param defElement the Element XML to convert to a Document.
     * @return the document object initialize.
     */
    public static Element loadDocumentFromElement(File fileXML,String defElement){
        try {
            //FileInputStream file = new FileInputStream(fileXML); //optional
            doc = initDocumentXML();
            doc = docBuilder.parse(fileXML);
            logger.info("Document W3C loaded from file:" + fileXML.getAbsolutePath());
            return doc.getDocumentElement();
        }catch(SAXException|IOException e){
            if (defElement != null) return getDocumentBuilder().newDocument().createElement(defElement);
            else {
                logger.warn(gm() + "Document W3C loaded from stream:" + fileXML + " is NULL");
                return null;
            }
        }
    }

    public static Element loadDocumentFromElement(InputStream in,String defElement){
        try {
            //FileInputStream file = new FileInputStream(fileXML); //optional
            doc = initDocumentXML();
            doc = docBuilder.parse(new InputSource(in));
            logger.info("Document W3C loaded from stream:" + in);
            return doc.getDocumentElement();
        }catch(SAXException|IOException e){
            if (defElement != null) return getDocumentBuilder().newDocument().createElement(defElement);
            else {
                logger.warn(gm() +"Document W3C loaded from stream:" + in + " is NULL");
                return null;
            }
        }
    }

    /**
     * Method to initialize a new XML document.
     * @return the document object initialize.
     */
    public static Document initDocumentXML(){
        try{
            docFactory = DocumentBuilderFactory.newInstance();
            //this line of code not work properly
            //docFactory.setValidating(false);
            //this line of code evite to search the validation parameter just if you want read the xml coe withourt meta information
            docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        }catch(ParserConfigurationException pe){
            logger.error(pe.getMessage(),pe);
        }
        return doc;
    }

    /**
     * Method to initialize a new XML document.
     * @param rootName string name of the root element.
     * @return the document object initialize.
     */
    public static Document initDocumentXML(String rootName){
        doc = initDocumentXML();
        Element root = doc.createElement(rootName);
        doc.appendChild(root);
        return doc;
    }


    public static Document toDocument(String rootQName){
        try{
            doc = getDocumentBuilderFactory().newDocumentBuilder().getDOMImplementation()
                    .createDocument(null, rootQName, null);
            //doc = docBuilder.newDocument();
        }catch(ParserConfigurationException|DOMException pe){
            logger.error(pe.getMessage(),pe);
        }
        return doc;
    }

    /**
     * Method to insert a new XML file.
     * @param pathFile where you want to insert the new file.
     * @param nameFile name of the XML file.
     * @return if true the creation has succeded.
     */
    public static boolean createXMLFile(String pathFile,String nameFile){
        String path = pathFile+File.separator+nameFile;
        try {
            doc = initDocumentXML();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
            logger.info("File " + path + "saved!");
            return true;
        } catch (TransformerException tfe) {
            logger.error(tfe.getMessage(),tfe);
            return false;
        }
    }


    public static DocumentBuilder getBuilder() throws ParserConfigurationException {
        DocumentBuilder builder = builders.poll();
        if (builder == null) {
            if (docFactory == null) {
                docFactory = DocumentBuilderFactory.newInstance();
                docFactory.setNamespaceAware(true);
            }
            builder = docFactory.newDocumentBuilder();
        }
        return builder;
    }

    public static void releaseBuilder(DocumentBuilder builder) {
        builders.add(builder);
    }

    public static Element createElement(String name, String prefix, String namespaceURI) {
        if (prefix != null)
            name = prefix + ":" + name;
        return doc.createElementNS(namespaceURI, name);
    }

    /**
     * Method for update the value of a attribute
     * @param tagName string rootTag.
     * @param nameAttribute string name of attribute.
     * @param newValueAttribute string new value attribute.
     * @return if true all the operation are done. 
     */
    public static boolean updateValueOfAttribute(
            String tagName,String nameAttribute,String newValueAttribute) {
        try{
            //doc =  loadDocumentFromFile(xmlFile);
            Element el = selectFirstElementByAttribute(tagName, nameAttribute);
            //get map containing the attributes of this node
            NamedNodeMap attributes = el.getAttributes();
            //get the number of nodes in this map
            int numAttrs = attributes.getLength();
            for(int i =0; i < numAttrs; i++){
                Attr attr = (Attr) attributes.item(i);
                if(Objects.equals(attr.getNodeName(), nameAttribute)){
                    attr.setValue(newValueAttribute);
                    logger.info("Update the value of the attribute:"+
                            attr.getName()+ "="+newValueAttribute);
                    break;
                }
            }
            return true;
            //saveToXml(doc, xmlFile.getAbsolutePath());
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * 
     * Method for update the value of a attribute
     * @param xmlFile the File XML to update.
     * @param tagName string rootTag.
     * @param nameAttribute string name of attribute.
     * @param newValueAttribute string new value attribute.
     * @return if true all the operation are done.
     */
    public static boolean updateValueOfAttribute(
            File xmlFile,String tagName,String nameAttribute,String newValueAttribute){
        try{
            doc =  loadDocumentFromFile(xmlFile);
            Element el = selectFirstElementByAttribute(tagName, nameAttribute);
            NamedNodeMap attributes = el.getAttributes();
            int numAttrs = attributes.getLength();
            for(int i =0; i < numAttrs; i++){
                Attr attr = (Attr) attributes.item(i);
                if(Objects.equals(attr.getNodeName(), nameAttribute)){
                    attr.setValue(newValueAttribute);
                    logger.info("Update the value of the attribute:"
                            +attr.getName()+ "="+newValueAttribute);
                    break;
                }
            }
            writeDocumentToXmlFile(doc, xmlFile.getAbsolutePath());
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }


    /*
    public static void updateValueOfAttribute2(
            String tagName,String nameAttribute,String newValueAttribute)
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
                logger.info("Update the value of the attribute:"+attr.getName()+ "="+newValueAttribute);
                break;
            }
        }
    }
    */
    
    /**
     * Method to update the value of element by the tag name.
     * @param element  the Element XML to update.
     * @param tagName the TagName where update the value.
     * @param newContent the String of the new value fot the tagname.
     * @return if true all the operation are done.
     */
    public static boolean updateValueOfInnerText(Element element,String tagName,String newContent){
        try{
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
                        logger.info("Update the content of the tag:"
                                +tagName+ "="+newContent);
                    }
                    break;
                }

            }
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
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
     * @return if true all the operation are done.
     */
    public static boolean addAttribute(Element element,String newAttribute,String valueAttribute){
        try{
            Element person = (Element)element.getFirstChild();
            person.setAttribute(newAttribute,valueAttribute);
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
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
        doc = loadDocumentFromFile(xmlFile);
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        logger.info("Root element XML document:" + doc.getDocumentElement().getNodeName());
        String tagName = doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName(tagName);
        for (int temp = 0; temp < nList.getLength(); temp++){
            Node nNode = nList.item(temp);
            logger.info("\nCurrent Element :" + nNode.getNodeName());
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
    private static String printNode(NodeList nodeList) {
        StringBuilder sb = new StringBuilder();
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                sb.append("\nNode Name =").append(tempNode.getNodeName()).append(" [OPEN]\n");
                sb.append("Node Value =").append(tempNode.getTextContent()).append("\n");
                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        sb.append("attr name : ").append(node.getNodeName()).append(",");
                        sb.append("attr value : ").append(node.getNodeValue()).append("\n");
                    }
                }
                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNode(tempNode.getChildNodes());
                }
                sb.append("Node Name =").append(tempNode.getNodeName()).append(" [CLOSE] \n");
            }
        }
        logger.info(sb.toString());
        return sb.toString();
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
     */
    public void updateValueofAttribute(File xmlFile,String tagName,String nameAttribute,
              String newValueAttribute){
        doc = loadDocumentFromFile(xmlFile);
        Node n = doc.getDocumentElement().getElementsByTagName(tagName).item(0);
        Element e = convertNodeToElement(n);
        e.setAttribute(nameAttribute, newValueAttribute);
        writeDocumentToXmlFile(doc, xmlFile.getAbsolutePath());
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
        docBuilder = getDocumentBuilder();
        return docBuilder.parse(filePath);
    }

    /**
     * Returns a default DocumentBuilder instance or throws an
     * ExceptionInInitializerError if it can't be created.
     * @return a default DocumentBuilder instance.
     */
    public static DocumentBuilder getDocumentBuilder(){
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setIgnoringComments(true);
            builderFactory.setCoalescing(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            builderFactory.setValidating(false);
            docBuilder = builderFactory.newDocumentBuilder();
            return docBuilder;
        } catch (ParserConfigurationException ex) {
           logger.error(ex.getMessage(),ex);
           return null;
        }
    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException{
        docFactory = DocumentBuilderFactory.newInstance();
        //this line of code not work properly
        //docFactory.setValidating(false);
        //this line of code evite to search the validation parameter just if you want read the xml coe withourt meta information
        docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return docFactory;
    }


    /**
     * Method To save the Document in xml file.
     * @param xmlDoc the XML Docuemnt you wan to save.
     * @param filePath where you want ot save the file.
     */
    public static void writeDocumentToXmlFile(Document xmlDoc, String filePath){
        try {
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
        }catch(TransformerException ex) {
            logger.error(ex.getMessage(),ex);
        }
    }

    /**
     * Method to Print out the target Document in specified encoding
     * @param xmlDoc the Document W3C to convert to a File.
     * @param file the target file
     * @throws IOException throw if the File Output directory not exists.
     */
    public void writeToFile(Document xmlDoc,File file) throws IOException {
        try (PrintWriter tmpPW = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StringUtilities.UTF_8))) {
            tmpPW.println(toStringXML(xmlDoc));
            tmpPW.flush();
        }
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
     * Method to convert Document to String.
     * @return the string content of the document.
     */
    public static String toStringXML(){
        if(doc == null){
            doc = initDocumentXML();
        }
        return toStringXML(doc);
    }

    /**
     * Method to convert Document to String.
     * @param doc the Document W3C to convert to a String.
     * @return the string content of the document.
     */
    public static String toStringXML(Document doc){
        //doc = initDocumentXML();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        convertElementToStream(doc.getDocumentElement(), baos);
        return new String(baos.toByteArray());
    }

    /**
     * Method to convert Element to Stream.
     * @param element Element to convert to Stream.
     * @param out outputStream.
     * @return the Stream of the Element XML.
     */
    public static StreamResult convertElementToStream(Element element, OutputStream out) {
        try {
            DOMSource source = new DOMSource(element);
            StreamResult result = new StreamResult(out);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
            return result;
        } catch (TransformerException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
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
                @Override
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
            });
            // open and parse XML-file
            doc = docBuilder.parse(input);
        } catch (SAXException|IOException|ParserConfigurationException e) {
            logger.error(e.getMessage(),e);
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

        } catch (IllegalArgumentException | TransformerException e) {
            logger.error(e.getMessage(),e);
            return null;
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

    public String getXMLString(String targetS) {
        return targetS.replaceAll("&", "&amp;").replaceAll("<","&lt;")
                .replaceAll(">", "&gt;");
    }

    /**
     * Simple transformation method using Saxon XSLT 2.0 processor.
     * @param sourcePath - Absolute path to source GML file.
     * @param xsltPath - Absolute path to XSLT file.
     * @param resultPath - Absolute path to the resulting RDF file.
     */
    public static void saxonTransform(String sourcePath, String xsltPath, String resultPath) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();    
            Transformer transformer = tFactory.newTransformer(
                    new StreamSource(new File(xsltPath)));
            transformer.transform(new StreamSource(new File(sourcePath)), 
                    new StreamResult(new File(resultPath)));
            logger.info("XSLT transformation completed successfully.\nOutput writen into file: " 
                    + resultPath );
        } catch (TransformerException ex) {
            logger.error(ex.getMessage(),ex);
        } 
    }


    /**
     * Method to check the validate of a prefix on the string part of the xml document.
     * @param qname name of the prefix.
     */
    public static void checkPrefix(String qname){
        try{
            if(qname == null) {
                throw new IllegalArgumentException("Unexpected null QName");
            }
            if(qname.indexOf(":") <= 0) {
                throw new IllegalArgumentException("Missing prefix: " + qname);
            }
        }catch(IllegalArgumentException e){
            logger.error(gm() + e.getMessage(),e);
        }
    }

    /**
     * Method to check the validate of a qName on the string part of the xml document.
     * @param qname string of the qName.
     * @throws Exception throw if any  error is occurred.
     */
    public static void checkQName(String qname) throws Exception {
        checkPrefix(qname);
        if(qname.indexOf(":") == qname.length()) {
            throw new Exception("Missing local name: " + qname);
        }
    }

    /**
     * Determine if the specified string satisfies the constraints of an XML Name.
     * This code is seriously incomplete.
     * @param str string to check.
     * @return if the string is the name of the xml file.
     */
    public static boolean isXMLName(String str) {
      char name[] = str.toCharArray();
      if(name[0] == '_' || Character.isLetter(name[0])) {
          for (char aName : name) {
              if (!Character.isLetter(aName)&& !Character.isDigit(aName)
                      && aName != '.'&& aName != '-'&& aName != '_') {
                  return false;
              }
          }
        return true;
      }
      return false;
    }

    /**
     * Method to decode a string xml text.
     * @param text string xml to decode.
     * @return the string xml decoded.
     * @throws Exception throw if any  error is occurred.
     */
    public static String xmlDecode(String text) throws Exception {
      String origText = text;
      String newText = "";
      while(text.contains("&")) {
        int pos = text.indexOf("&");
        newText += text.substring(0, pos);
        text = text.substring(pos + 1);
        pos = text.indexOf(";");
        if(pos <= 0) {
          throw new Exception("Improperly escaped character: "+ origText);
        }
        String charref = text.substring(0, pos);
        text = text.substring(pos + 1);
        if(charref.equals("lt")) { newText += "<"; }
        else if(charref.equals("gt")) {newText += ">";}
        else if(charref.equals("amp")) { newText += "&";}
        else if(charref.equals("quot")) { newText += "\"";}
        else if(charref.equals("apos")) { newText += "'";}
        else if(charref.startsWith("#")) {
          String number = charref.substring(1);
          int radix = 10;
          if(charref.startsWith("#x")|| charref.startsWith("#X")) {
                number = charref.substring(2);
                radix = 16;
          }
          if("".equals(number)){
              throw new Exception("Improperly escaped character: "+ charref);
          }
          char ch;
          try {
            ch = (char) Integer.parseInt( number,radix);
          }
          catch(NumberFormatException nfe) {
              throw new Exception("Improperly escaped character: "+ charref);
          }
          newText += ch;
        }
        else{
            throw new Exception("Improperly escaped character: "+ charref);
        }
      }//while
      return newText + text;
    }

    /**
     * Method to get the Map of the namespaces.
     * @param xmlFile the string of the file xml.
     * @return the Map of all namespace on the XML File.
     */
    @SuppressWarnings("unchecked")
    public static Map<String,String> getNamespaces(String xmlFile) {
        Map<String,String> namespaces = new HashMap<>();
        // Construct a SAX Parser using JAXP
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // For this app, namespaces and validity are irrelevant
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        // Our handler will actually count the words
        PrefixGrabber handler = new PrefixGrabber();
        try{
            // Construct the parser and
            SAXParser parser = factory.newSAXParser();
            // use it to parse the document
            parser.parse(xmlFile, handler);
        } catch(ParserConfigurationException | SAXException | IOException e) {
            // Maybe FileNotFound, maybe something else, anyway, life goeson...
            logger.error(gm() + "Maybe FileNotFound:"+e.getMessage(),e);
            return null;
        }
        // Add any newly discovered prefixes to the namespace bindings
        Map<String,String> docNamespaces = handler.getNamespaces();
        Enumeration<String> document = (Enumeration<String>) docNamespaces.keySet();
        while(document.hasMoreElements()) {
            String prefix = document.nextElement();
            if(!namespaces.containsKey(prefix)) {
              namespaces.put(prefix, docNamespaces.get(prefix));
            }
        }//while
        return namespaces;
    }//getNameSpaces

    /**
     * Method to encode the xml text.
     * @param rawtext string of the xml text.
     * @return the encode string.
     */
    public static String xmlEncode(String rawtext) {
        // Now turn that UTF-8 string into something "safe"
        String rdfString ="<?xml version='1.0' encoding='ISO-8859-1'?>\n";
        char[] sbuf = rawtext.toCharArray();
        int lastPos = 0;
        int pos = 0;
        while(pos < sbuf.length){
            char ch = sbuf[pos];
            if(!(ch == '\n' || (ch >= ' ' && ch <= '~'))){
                if(pos > lastPos){
                    String range =new String(sbuf,lastPos,pos - lastPos);
                    rdfString += range;
                }
                rdfString += "&#" + (int) ch + ";";
                lastPos = pos + 1;
            }
            pos++;
        }
        if(pos > lastPos) {
            String range =  new String(sbuf, lastPos, pos - lastPos);
            rdfString += range;
        }
        return rdfString;
    }//xmlEncode

    /**
     * Copy an XML document, adding it as a child of the target document root.
     * @param source Document to copy.
     * @param target Document to contain copy.
     * @return  the target document.
     */
    public static Document copyDocument(Document source, Document target) {
        Node node = target.importNode(source.getDocumentElement(), true);
        target.getDocumentElement().appendChild(node);
        return target;
    }

    //http://www.java2s.com/Code/Java/XML/W3CDOMutilitymethods.htm

    /**
     * Copy child node references from source to target.
     * @param source Source Node.
     * @param target Target Node.
     * @return if true all the operation are done.
     */
    public static boolean copyChildNodes(Node source, Node target) {
        try{
            List<Node> nodeList = copyNodeList(source.getChildNodes());
            //int childCount = nodeList.size();
            for (Object aNodeList : nodeList) {
                target.appendChild((Node) aNodeList);
            }
            return true;
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return false;
        }
    }

    /**
     * Replace one node with another node.
     * @param newNode New node - added in same location as oldNode.
     * @param oldNode Old node - removed.
     * @return if true all the operation are done.
     */
    public static boolean replaceNode(Node newNode, Node oldNode) {
        Node parentNode = oldNode.getParentNode();
        if(parentNode == null) {
            logger.warn("Cannot replace node [" + oldNode + "] with [" + newNode + "]. "
                    + "[" + oldNode + "] has no parent.");
            return false;
        } else {
            parentNode.replaceChild(newNode, oldNode);
            return true;
        }
    }

    /**
     * Replace one node with a list of nodes.
     * Clones the NodeList elements.
     * @param newNodes New nodes - added in same location as oldNode.
     * @param oldNode Old node - removed.
     * @return if true all the operation are done. 
     */
    public static boolean replaceNode(NodeList newNodes, Node oldNode) {
        return replaceNode(newNodes, oldNode, true);
    }

    /**
     * Replace one node with a list of nodes.
     * @param newNodes New nodes - added in same location as oldNode.
     * @param oldNode Old node - removed.
     * @param clone Clone Nodelist Nodes.
     * @return if true all the operation are done.
     */
    public static boolean replaceNode(NodeList newNodes, Node oldNode, boolean clone) {
        Node parentNode = oldNode.getParentNode();
        if(parentNode == null) {
            logger.warn("Cannot replace [" + oldNode + "] with a NodeList. "
                    + "[" + oldNode + "] has no parent.");
            return false;
        }
        int nodeCount = newNodes.getLength();
        List<Node> nodeList = copyNodeList(newNodes);
        if(nodeCount == 0) {
            if(!(parentNode instanceof Document)) {
                parentNode.removeChild(oldNode);
            }
        }
        if(parentNode instanceof Document) {
            List<Element> elements = getElements(newNodes, "*", null);
            if(!elements.isEmpty()) {
                logger.warn("Request to replace the Document root node with a 1+ "
                        + "in length NodeList.  Replacing root node with the first element"
                        + " node from the NodeList.");
                parentNode.removeChild(oldNode);
                parentNode.appendChild(elements.get(0));
                return true;
            } else {
                logger.warn("Cannot replace document root element with a NodeList "
                        + "that doesn't contain an element node.");
                return false;
            }
        } else {
            for(int i = 0; i < nodeCount; i++) {
                if(clone) {
                    parentNode.insertBefore((nodeList.get(i)).cloneNode(true), oldNode);
                } else {
                    parentNode.insertBefore(nodeList.get(i), oldNode);
                }
            }
            parentNode.removeChild(oldNode);         
        }
        return true;
    }

    /**
     * Insert the supplied node before the supplied reference node (refNode).
     * @param newNode Node to be inserted.
     * @param refNode Reference node before which the supplied nodes should
     * be inserted.
     * @return if true all the operation are done. 
     */
    public static boolean insertBefore(Node newNode, Node refNode) {
        Node parentNode = refNode.getParentNode();
        if(parentNode == null) {
            logger.warn("Cannot insert [" + newNode + "] before [" + refNode + "]. "
                    + "[" + refNode + "] has no parent.");
            return false;
        }
        if(parentNode instanceof Document && newNode.getNodeType() == Node.ELEMENT_NODE) {
            logger.warn("Request to insert an element before the Document root node. "
                    + "This is not allowed.  Replacing the Document root with the new Node.");
            parentNode.removeChild(refNode);
            parentNode.appendChild(newNode);
        } else {
            parentNode.insertBefore(newNode, refNode);
        }
        return true;
    }

    /**
     * Insert the supplied nodes before the supplied reference node (refNode).
     * @param newNodes Nodes to be inserted.
     * @param refNode Reference node before which the supplied nodes should
     * be inserted.
     * @return if true all the operation are done. 
     */
    public static boolean insertBefore(NodeList newNodes, Node refNode) {
        Node parentNode = refNode.getParentNode();
        if(parentNode == null) {
            logger.warn("Cannot insert a NodeList before [" + refNode + "]. "
                    + "[" + refNode + "] has no parent.");
            return false;
        }
        int nodeCount = newNodes.getLength();
        List<Node> nodeList = copyNodeList(newNodes);
        if(nodeCount == 0) {
            return false;
        }
        if(parentNode instanceof Document) {
            List<Element> elements = getElements(newNodes, "*", null);
            if(!elements.isEmpty()) {
               logger.warn("Request to insert a NodeList before the Document root node.  "
                       + "Will replace the root element with the 1st element node from the NodeList.");
                parentNode.removeChild(refNode);
                parentNode.appendChild(elements.get(0));
            } else {
                logger.warn("Cannot insert before the document root element from "
                        + "a NodeList that doesn't contain an element node.");
                return false;
            }
            for(int i = 0; i < nodeCount; i++) {
                Node node = nodeList.get(i);
                if(node.getNodeType() != Node.ELEMENT_NODE) {
                    logger.info("****" + node);
                    parentNode.insertBefore(node, refNode);
                }
            }
        } else {
            for(int i = 0; i < nodeCount; i++) {
                parentNode.insertBefore(nodeList.get(i), refNode);
            }
        }
        return true;
    }

    /**
     * Rename element.
     * @param element The element to be renamed.
     * @param replacementElement The tag name of the replacement element.
     * @param keepChildContent <code>true</code> if the target element's child content
     * is to be copied to the replacement element, false if not. Default <code>true</code>.
     * @param keepAttributes <code>true</code> if the target element's attributes
     * are to be copied to the replacement element, false if not. Default <code>true</code>.
     * @return The renamed element.
     */
    public static Element renameElement(Element element, String replacementElement, boolean keepChildContent, boolean keepAttributes) {
        Element replacement = element.getOwnerDocument().createElement(replacementElement);
        if(keepChildContent) {
            copyChildNodes(element, replacement);
        }
        if(keepAttributes) {
            NamedNodeMap attributes = element.getAttributes();
            int attributeCount = attributes.getLength();
            for(int i = 0; i < attributeCount; i++) {
                Attr attribute = (Attr)attributes.item(i);
                replacement.setAttribute(attribute.getName(), attribute.getValue());
            }
        }
        replaceNode(replacement, element);
        return replacement;
    }

    /**
     * Remove the supplied element from its containing document.
     * Tries to manage scenarios where a request is made to remove the root element.
     * Cannot remove the root element in any of the following situations:
     * &lt;ul&gt;
     *  &lt;li&gt;"keepChildren" parameter is false.&lt;/li&gt;
     *  &lt;li&gt;root element is empty of {@link Node#ELEMENT_NODE} nodes.&lt;/li&gt;
     * &lt;/ul&gt;
     * @param element Element to be removed.
     * @param keepChildren Keep child content.
     * @return if true all the operation are done. 
     */
    public static boolean removeElement(Element element, boolean keepChildren) {
        Node parent = element.getParentNode();
        if(parent == null) {
            logger.warn("Cannot remove element [" + element + "]. "
                    + "[" + element + "] has no parent.");
            return false;
        }
        NodeList children = element.getChildNodes();
        if (parent instanceof Document) {
            List<Element> childElements = null;
            if(!keepChildren) {
                logger.warn("Cannot remove document root element "
                        + "[" + getName(element) + "] without keeping child content.");
                return false;
            } else {
                if(children != null && children.getLength() > 0) {
                    childElements = getElements(element, "*", null);
                }
                if(childElements != null && !childElements.isEmpty()) {
                    parent.removeChild(element);
                    parent.appendChild(childElements.get(0));
                } else {
                    logger.warn("Cannot remove empty document root element "
                            + "[" + getName(element) + "].");
                    return false;
                }
            }
        } else {
            if(keepChildren && children != null) {
                if(!insertBefore(children, element)){
                    return false;
                }
            }
            parent.removeChild(element);
        }
        return true;
    }

    /**
     * Remove all child nodes from the supplied node.
     * @param node to be "cleared".
     * @return if true all the operation are done.  
     */
    public static boolean removeChildren(Node node) {
        NodeList children = node.getChildNodes();
        int nodeCount = children.getLength();
        for(int i = 0; i < nodeCount; i++) {
            node.removeChild(children.item(0));
        }
        return true;
    }

    /**
     * Copy the nodes of a NodeList into the supplied list.
     * This is not a clone.  It's just a copy of the node references.
     * Allows iteration over the Nodelist using the copy in the knowledge that
     * the list will remain the same length.  Using the NodeList can result in problems
     * because elements can get removed from the list while we're iterating over it.
     * @param nodeList Nodelist to copy.
     * @return List copy.
     */
    @SuppressWarnings("unchecked")
    public static List<Node> copyNodeList(NodeList nodeList) {
        List<Node> copy = new ArrayList<>();
        if(nodeList != null) {
            int nodeCount = nodeList.getLength();
            for(int i = 0; i < nodeCount; i++) {
                copy.add(nodeList.item(i));
            }
        }
        return copy;
    }

    /**
     * Append the nodes from the supplied list to the supplied node.
     * @param node Node to be appended to.
     * @param nodes List of nodes to append.
     * @return the Node with the appended list.
     */
    public static Node appendList(Node node, List<Node> nodes) {
        //int nodeCount = nodes.size();
        for(Object node1 : nodes) {
            node.appendChild((Node) node1);
        }
        return node;
    }

    /**
     * Get a boolean attribute from the supplied element.
     * @param element The element.
     * @param attribName The attribute name.
     * @return True if the attribute value is "true" (case insensitive), otherwise false.
     */
    public static boolean getBooleanAttrib(Element element, String attribName) {
        String attribVal = element.getAttribute(attribName);
        return (attribVal != null && attribVal.equalsIgnoreCase("true"));
    }

    /**
     * Get a boolean attribute from the supplied element.
     * @param element The element.
     * @param namespaceURI Namespace URI of the required attribute.
     * @param attribName The attribute name.
     * @return True if the attribute value is "true" (case insensitive), otherwise false.
     */
    public static boolean getBooleanAttrib(Element element, String attribName, String namespaceURI) {
        String attribVal = element.getAttributeNS(namespaceURI, attribName);
        return (attribVal != null && attribVal.equalsIgnoreCase("true"));
    }

    /**
     * Get the parent element of the supplied element having the
     * specified tag name.
     * @param child Child element.
     * @param parentLocalName Parent element local name.
     * @return The first parent element of "child" having the tagname "parentName",
     * or null if no such parent element exists.
     */
    public static Element getParentElement(Element child, String parentLocalName) {
        return getParentElement(child, parentLocalName, null);
    }

    /**
     * Get the parent element of the supplied element having the
     * specified tag name.
     * @param child Child element.
     * @param parentLocalName Parent element local name.
     * @param namespaceURI Namespace URI of the required parent element,
     * or null if a non-namespaced get is to be performed.
     * @return The first parent element of "child" having the tagname "parentName",
     * or null if no such parent element exists.
     */
    public static Element getParentElement(Element child, String parentLocalName, String namespaceURI) {
        Node parentNode = child.getParentNode();
        while(parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
            Element parentElement = (Element)parentNode;
            if(getName(parentElement).equalsIgnoreCase(parentLocalName)) {
                if(namespaceURI == null) {
                    return parentElement;
                } else if(parentElement.getNamespaceURI().equals(namespaceURI)) {
                    return parentElement;
                }
            }
            parentNode = parentNode.getParentNode();
        }
        return null;
    }

    /**
     * Get the name from the supplied element.
     * Returns the {@link Node#getLocalName() localName} of the element
     * if set (namespaced element), otherwise the
     * element's {@link Element#getTagName() tagName} is returned.
     * @param element The element.
     * @return The element name.
     */
    public static String getName(Element element) {
        String name = element.getLocalName();
        if(name != null) {
            return name;
        } else {
            return element.getTagName();
        }
    }

    /**
     * Get attribute value, returning <code>null</code> if unset.
     * Some DOM implementations return an empty string for an unset
     * attribute.
     * @param element The DOM element.
     * @param attributeName The attribute to get.
     * @return The attribute value, or &lt;code&gt;null&lt;/code&gt; if unset.
     */
    public static String getAttributeValue(Element element, String attributeName) {
        return getAttributeValue(element, attributeName, null);
    }

    /**
     * Get attribute value, returning <code>null</code> if unset.
     * Some DOM implementations return an empty string for an unset
     * attribute.
     * @param element The DOM element.
     * @param attributeName The attribute to get.
     * @param namespaceURI Namespace URI of the required attribute, or null
     * to perform a non-namespaced get.
     * @return The attribute value, or &lt;code&gt;null&lt;/code&gt; if unset.
     */
    public static String getAttributeValue(Element element, String attributeName, String namespaceURI) {
        String attributeValue;
        if(namespaceURI == null) {
            attributeValue = element.getAttribute(attributeName);
        } else {
            attributeValue = element.getAttributeNS(namespaceURI, attributeName);
        }
        if(attributeValue.length() == 0 && !element.hasAttribute(attributeName)) {
            return null;
        }
        return attributeValue;
    }

    public String getAttributeValue(Element element, Node attr,NodeFilter attrFilter) {
        if (attrFilter == null || attrFilter.acceptNode(attr) != -1) {
            String value = getXMLString(attr.getNodeValue());
            String quat = "\"";
            if (value.indexOf("\"") > 0) {
                quat = "'";
            }
            return " " + attr.getNodeName() + "=" + quat + value + quat;
        }
        return "";
    }

    /**
     * Method to get the previous sibling in a XML Node.
     * @param node the Node where  execute the research.
     * @param nodeType the Type of the Node to research.
     * @return the Node founded.
     */
    public static Node getPreviousSibling(Node node, short nodeType) {
        Node parent = node.getParentNode();
        if(parent == null) {
            logger.warn(gm() + "Cannot get node [" + node + "] previous sibling. " +
                    "[" + node + "] has no parent.");
            return null;
        }
        NodeList siblings = parent.getChildNodes();
        int siblingCount = siblings.getLength();
        int nodeIndex = 0;
        // Locate the node
        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);
            if(sibling == node) {
                nodeIndex = i;
                break;
            }
        }
        if(nodeIndex == 0) {
            return null;
        }
        // Wind back to sibling
        for(int i = nodeIndex - 1; i >= 0; i--) {
            Node sibling = siblings.item(i);

            if(sibling.getNodeType() == nodeType) {
                return sibling;
            }
        }
        return null;
    }

    /**
     * Count the DOM nodes of the supplied type (nodeType) before the supplied
     * node, not including the node itself.
     * Counts the sibling nodes.
     * @param node Node whose siblings are to be counted.
     * @param nodeType The DOM {@link Node} type of the siblings to be counted.
     * @return The number of siblings of the supplied type before the supplied node.
     */
    public static int countNodesBefore(Node node, short nodeType) {
        Node parent = node.getParentNode();
        if(parent == null) {
            logger.warn(gm() +"Cannot count nodes before [" + node + "]. [" + node + "] has no parent.");
            return 0;
        }
        NodeList siblings = parent.getChildNodes();
        int count = 0;
        int siblingCount = siblings.getLength();
        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);
            if(sibling == node) {
                break;
            }
            if(sibling.getNodeType() == nodeType) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count the DOM nodes of the supplied type (nodeType) between the supplied
     * sibling nodes, not including the nodes themselves.
     * Counts the sibling nodes.
     * @param node1 First sibling node.
     * @param node2 Second sibling node.
     * @param nodeType The DOM {@link Node} type of the siblings to be counted.
     * @return The number of siblings of the supplied type between the supplied
     * sibling nodes.
     * @throws UnsupportedOperationException if the supplied {@link Node Nodes}
     * don't have the same parent node i.e. are not sibling nodes.
     */
    public static int countNodesBetween(Node node1, Node node2, short nodeType) {
        Node parent1 = node1.getParentNode();
        if(parent1 == null) {
            logger.warn(gm() + "Cannot count nodes between [" + node1 + "] " +
                    "and [" + node2 + "]. [" + node1 + "] has no parent.");
            return 0;
        }
        Node parent2 = node2.getParentNode();
        if(parent2 == null) {
            logger.warn(gm() + "Cannot count nodes between [" + node1 + "] " +
                    "and [" + node2 + "]. [" + node2 + "] has no parent.");
            return 0;
        }
        if(parent1 != parent2) {
            logger.warn(gm() + "Cannot count nodes between [" + node1 + "] " +
                    "and [" + node2 + "]. These nodes do not share the same sparent.");
            return 0;
        }
        int countBeforeNode1 = countNodesBefore(node1, nodeType);
        int countBeforeNode2 = countNodesBefore(node2, nodeType);
        int count = countBeforeNode2 - countBeforeNode1;
        if(node1.getNodeType() == nodeType) {
            count--;
        }
        return count;
    }

    /**
     * Count the DOM nodes before the supplied node, not including the node itself.
     * Counts the sibling nodes.
     * @param node Node whose siblings are to be counted.
     * @return The number of siblings before the supplied node.
     */
    public static int countNodesBefore(Node node) {
        Node parent = node.getParentNode();
        if(parent == null) {
            logger.info("Cannot count nodes before [" + node + "]. "
                    + "[" + node + "] has no parent.");
            return 0;
        }
        NodeList siblings = parent.getChildNodes();
        int count = 0;
        int siblingCount = siblings.getLength();
        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);
            if(sibling == node) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * Count the DOM nodes between the supplied sibling nodes, not including
     * the nodes themselves.
     * Counts the sibling nodes.
     * @param node1 First sibling node.
     * @param node2 Second sibling node.
     * @return The number of siblings between the supplied sibling nodes.
     * @throws UnsupportedOperationException if the supplied {@link Node Nodes}
     * don't have the same parent node i.e. are not sibling nodes.
     */
    public static int countNodesBetween(Node node1, Node node2) {
        Node parent1 = node1.getParentNode();
        if(parent1 == null) {
            logger.info("Cannot count nodes between [" + node1 + "] and [" + node2 + "]. "
                    + "[" + node1 + "] has no parent.");
            return 0;
        }
        Node parent2 = node2.getParentNode();
        if(parent2 == null) {
            logger.info("Cannot count nodes between [" + node1 + "] and [" + node2 + "]. "
                    + "[" + node2 + "] has no parent.");
            return 0;
        }
        if(parent1 != parent2) {
            logger.info("Cannot count nodes between [" + node1 + "] and [" + node2 + "]. "
                    + "These nodes do not share the same sparent.");
            return 0;
        }
        int countBeforeNode1 = countNodesBefore(node1);
        int countBeforeNode2 = countNodesBefore(node2);
        return countBeforeNode2 - countBeforeNode1 - 1;
    }

    /**
     * Count the DOM element nodes before the supplied node, having the specified
     * tag name, not including the node itself.
     * Counts the sibling nodes.
     * @param node Node whose element siblings are to be counted.
     * @param tagName The tag name of the sibling elements to be counted.
     * @return The number of siblings elements before the supplied node with the
     * specified tag name.
     */
    public static int countElementsBefore(Node node, String tagName) {
        Node parent = node.getParentNode();
        if(parent == null) {
            logger.info("Cannot count nodes before [" + node + "]. [" + node + "] has no parent.");
            return 0;
        }
        NodeList siblings = parent.getChildNodes();
        int count = 0;
        int siblingCount = siblings.getLength();
        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);
            if(sibling == node) {
                break;
            }
            if(sibling.getNodeType() == Node.ELEMENT_NODE && ((Element)sibling).getTagName().equals(tagName)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get all the text DOM sibling nodes before the supplied node and
     * concatenate them together into a single String.
     * @param node Text node.
     * @return String containing the concatentated text.
     */
    public static String getTextBefore(Node node) {
        Node parent = node.getParentNode();
        if(parent == null) {
            logger.info("Cannot get text before node [" + node + "]. "
                    + "[" + node + "] has no parent.");
            return "";
        }
        NodeList siblings = parent.getChildNodes();
        StringBuilder text = new StringBuilder();
        int siblingCount = siblings.getLength();
        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);
            if(sibling == node) {
                break;
            }
            if(sibling.getNodeType() == Node.TEXT_NODE) {
                text.append(((Text)sibling).getData());
            }
        }
        return text.toString();
    }

    /**
     * Get all the text DOM sibling nodes before the supplied node and
     * concatenate them together into a single String.
     * @param node1 Test Node 1. 
     * @param node2 Test Node 2.
     * @return String containing the concatentated text.
     */
    public static String getTextBetween(Node node1, Node node2) {
        Node parent1 = node1.getParentNode();
        if(parent1 == null) {
            logger.info("Cannot get text between nodes [" + node1 + "] and [" + node2 + "]. "
                    + "[" + node1 + "] has no parent.");
            return "";
        }
        Node parent2 = node2.getParentNode();
        if(parent2 == null) {
            logger.info("Cannot get text between nodes [" + node1 + "] and [" + node2 + "]. "
                    + "[" + node2 + "] has no parent.");
            return "";
        }
        if(parent1 != parent2) {
            logger.info("Cannot get text between nodes [" + node1 + "] and [" + node2 + "]. "
                    + "These nodes do not share the same sparent.");
            return "";
        }
        NodeList siblings = parent1.getChildNodes();
        StringBuilder text = new StringBuilder();
        boolean append = false;
        int siblingCount = siblings.getLength();
        for(int i = 0; i < siblingCount; i++) {
            Node sibling = siblings.item(i);
            if(sibling == node1) {
                append = true;
            }
            if(sibling == node2) {
                break;
            }
            if(append && sibling.getNodeType() == Node.TEXT_NODE) {
                text.append(((Text)sibling).getData());
            }
        }
        return text.toString();
    }

    /**
     * Construct the XPath of the supplied DOM Node.
     * Supports element, comment and cdata sections DOM Node types.
     * @param node DOM node for XPath generation.
     * @return XPath string representation of the supplied DOM Node.
     */
    public static String getXPath(Node node) {
        StringBuilder xpath = new StringBuilder();
        Node parent = node.getParentNode();
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                xpath.append(getXPathToken((Element)node));
                break;
            case Node.COMMENT_NODE:
                int commentNum = countNodesBefore(node, Node.COMMENT_NODE);
                xpath.append("/{COMMENT}[").append(commentNum).append(1).append("]");
                break;
            case Node.CDATA_SECTION_NODE:
                int cdataNum = countNodesBefore(node, Node.CDATA_SECTION_NODE);
                xpath.append("/{CDATA}[").append(cdataNum).append(1).append("]");
                break;
            default:
                throw 
                new UnsupportedOperationException(
                        "XPath generation for supplied DOM Node type not supported."
                                + "  Only supports element, comment and cdata section DOM nodes.");
        }
        while(parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
            xpath.insert(0, getXPathToken((Element)parent));
            parent = parent.getParentNode();
        }
        return xpath.toString();
    }

    /**
     * Method to get the XPath of a Element.
     * @param element the XML Element.
     * @return the String of the XPath.
     */
    private static String getXPathToken(Element element) {
        String tagName = element.getTagName();
        int count = countElementsBefore(element, tagName);
        String xpathToken;
        if(count > 0) {
            xpathToken = "/" + tagName + "[" + (count + 1) + "]";
        } else {
            xpathToken = "/" + tagName;
        }
        return xpathToken;
    }

    /**
     * Method to get the depth of a XML Element.
     * @param element the XML Element.
     * @return the int depth of the XML Element.
     */
    public static int getDepth(Element element) {
        Node parent = element.getParentNode();
        int depth = 0;
        while(parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
            depth++;
            parent = parent.getParentNode();
        }
        return depth;
    }

    /**
     * Add literal text to the supplied element.
     * @param element Target DOM Element.
     * @param literalText Literal text to be added.
     * @return if true all the operation are done. 
     */
    public static boolean addLiteral(Element element, String literalText) {
        try{
            Document document = element.getOwnerDocument();
            Text literal = document.createTextNode(literalText);
            element.appendChild(literal);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Get the child element having the supplied localname, position
     * and namespace.
     * Can be used instead of XPath.
     * @param parent Parent element to be searched.
     * @param localname Localname of the element required.
     * @param position The position of the element relative to other sibling
     *              elements having the same name (and namespace if specified) e.g. if
     *              searching for the 2nd &lt;input&gt; element, this param needs to have a value of 2.
     * @return The element at the requested position, or null if no such child
     * element exists on the parent element.
     */
    public static Element getElement(Element parent, String localname, int position) {
        return getElement(parent, localname, position, null);
    }

    /**
     * Get the child element having the supplied localname, position
     * and namespace.
     * Can be used instead of XPath.
     * @param parent Parent element to be searched.
     * @param localname Localname of the element required.
     * @param position The position of the element relative to other sibling
     *                  elements having the same name (and namespace if specified) e.g. if
     *                  searching for the 2nd &lt;input&gt; element, this param needs to have a value of 2.
     * @param namespaceURI Namespace URI of the required element, or null
     * if a namespace comparison is not to be performed.
     * @return The element at the requested position, or null if no such child
     * element exists on the parent element.
     */
    public static Element getElement(Element parent, String localname, int position, String namespaceURI) {
        List<Element> elements = getElements(parent, localname, namespaceURI);
        position = Math.max(position, 1);
        if(position > elements.size()) {
            return null;
        }
        return elements.get(position - 1);
    }

    /**
     * Get the child elements having the supplied localname and namespace.
     * Can be used instead of XPath.
     * @param parent Parent element to be searched.
     * @param localname Localname of the element required.  Supports "*" wildcards.
     * @param namespaceURI Namespace URI of the required element, or null
     * if a namespace comparison is not to be performed.
     * @return A list of W3C DOM {@link Element}s.  An empty list if no such
     * child elements exist on the parent element.
     */
    public static List<Element> getElements(Element parent, String localname, String namespaceURI) {
        return getElements(parent.getChildNodes(), localname, namespaceURI);
    }

    /**
     * Get the child elements having the supplied localname and namespace.
     * Can be used instead of XPath.
     * @param nodeList List of DOM nodes on which to perform the search.
     * @param localname Localname of the element required.  Supports "*" wildcards.
     * @param namespaceURI Namespace URI of the required element, or null
     * if a namespace comparison is not to be performed.
     * @return A list of W3C DOM {@link Element}s.  An empty list if no such
     * child elements exist on the parent element.
     */
    public static List<Element> getElements(NodeList nodeList, String localname, String namespaceURI) {
        int count = nodeList.getLength();
        List<Element> elements = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if(localname.equals("*") || getName(element).equals(localname)) {
                    // The local name matches the element we're after...
                    if(namespaceURI == null || namespaceURI.equals(element.getNamespaceURI())) {
                        elements.add(element);
                    }
                }
            }
        }
        return elements;
    }
    
  /**
   * Get the content of the given element.
   * @param element       The element to get the content for.
   * @param defaultStr    The default to return when there is no content.
   * @return              The content of the element or the default.
   */
  public static String getElementContent(Element element, String defaultStr){
     if (element == null)return defaultStr;
     NodeList children = element.getChildNodes();
     String result = "";
     for (int i = 0; i < children.getLength(); i++){
        if (children.item(i).getNodeType() == Node.TEXT_NODE || 
            children.item(i).getNodeType() == Node.CDATA_SECTION_NODE){
           result += children.item(i).getNodeValue();
        }
        else if( children.item(i).getNodeType() == Node.COMMENT_NODE ) {
           // Ignore comment nodes
        }
     }
     return result.trim();
  }
  /**
   * Gets the child of the specified element having the specified unique
   * name.  If there are more than one children elements with the same name
   * and exception is thrown.
   * @param element    The parent element.
   * @param tagName    The name of the desired child.
   * @return           The named child.
   */
  public static Element getUniqueChild(Element element, String tagName){
    try{
        Iterator<Element> goodChildren = getChildrenByTagName(element, tagName);
        if (goodChildren != null && goodChildren.hasNext()) {
           Element child = goodChildren.next();
           if (goodChildren.hasNext()) {
              throw new IllegalArgumentException
                 ("expected only one " + tagName + " tag");
           }
           return child;
        } else {
           throw new IllegalArgumentException
              ("expected one " + tagName + " tag");
        }
    }catch(IllegalArgumentException e){
        logger.error(gm() + "Child was not found or was not unique:"+e.getMessage(),e);
        return null;
    }
  
  }
  /**
   * Returns an iterator over the children of the given element with
   * the given tag name.
   * @param element    The parent element
   * @param tagName    The name of the desired child
   * @return           An interator of children or null if element is null.
   */
  public static Iterator<Element> getChildrenByTagName(Element element,String tagName){
     if (element == null) return null;
     // getElementsByTagName gives the corresponding elements in the whole 
     // descendance. We want only children
     NodeList children = element.getChildNodes();
     ArrayList<Element> goodChildren = new ArrayList<>();
     for (int i=0; i<children.getLength(); i++) {
        Node currentChild = children.item(i);
        if (currentChild.getNodeType() == Node.ELEMENT_NODE && 
            ((Element)currentChild).getTagName().equals(tagName)) {
           goodChildren.add((Element)currentChild);
        }
     }
     return goodChildren.iterator();
  }

    //-------------------------------------------------------------

    private boolean checkNewLine(Node target,boolean indent) {
        if (indent && target.hasChildNodes()) {
            short type = target.getFirstChild().getNodeType();
            return !(type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE);
        }
        return false;
    }


    /**
     * Returns XML text converted from the target DOM
     * @param document the Document W3C to convert tpo String.
     * @param nodeFilter the NodeFilter for use a filter on the result of the String.
     * @param entityReferenceExpansion if true abilitate the Entity refrence Expansion.
     * @return String format XML converted from the target DOM
     */
    public String toXMLString(
            Document document,NodeFilter nodeFilter,boolean entityReferenceExpansion) {
        String LINE_SEP = System.getProperty("line.separator");
        String EMPTY_STR = "";
        String LT = "<";
        String GT = ">";
        String AMP = "&";
        String QUAT = "\"";
        String SINGLE_QUAT = "'";
        String ESC_LT = "&lt;";
        String ESC_GT = "&gt;";
        String ESC_AMP = "&amp;";
        int whatToShow = NodeFilter.SHOW_ALL; //show all element
        //NodeFilter nodeFilter = null;
        //entityReferenceExpansion = false;
        boolean indent = true;
        boolean escapeTagBracket = false;

        StringBuilder tmpSB = new StringBuilder(8192);
        TreeWalkerImpl treeWalker = new TreeWalkerImpl(document, whatToShow,
                nodeFilter, entityReferenceExpansion);

        String lt = escapeTagBracket ? ESC_LT : LT;
        String gt = escapeTagBracket ? ESC_GT : GT;
        String line_sep = indent ? LINE_SEP : EMPTY_STR;

        Node tmpN = treeWalker.nextNode();
        boolean prevIsText = false;

        String indentS = EMPTY_STR;
        while (tmpN != null) {
            short type = tmpN.getNodeType();
            switch (type) {
                case Node.ELEMENT_NODE:
                    if (prevIsText) {
                        tmpSB.append(line_sep);
                    }
                    tmpSB.append(indentS).append(lt).append(tmpN.getNodeName());
                    NamedNodeMap attrs = tmpN.getAttributes();
                    int len = attrs.getLength();
                    for (int i = 0; i < len; i++) {
                        Node attr = attrs.item(i);
                        String value = attr.getNodeValue();
                        if (null != value) {
                            tmpSB.append(getAttributeValue((Element) tmpN, attr,nodeFilter));
                        }
                    }
                    if (tmpN instanceof HTMLTitleElement && !tmpN.hasChildNodes()) {
                        tmpSB.append(gt).append(((HTMLTitleElement) tmpN).getText());
                        prevIsText = true;
                    } else if (checkNewLine(tmpN,indent)) {
                        tmpSB.append(gt).append(line_sep);
                        prevIsText = false;
                    } else {
                        tmpSB.append(gt);
                        prevIsText = true;
                    }
                    break;
                case Node.TEXT_NODE:
                    if (!prevIsText) {
                        tmpSB.append(indentS);
                    }
                    tmpSB.append(getXMLString(tmpN.getNodeValue()));
                    prevIsText = true;
                    break;
                case Node.COMMENT_NODE:
                    String comment;
                    if (escapeTagBracket) {
                        comment = getXMLString(tmpN.getNodeValue());
                    } else {
                        comment = tmpN.getNodeValue();
                    }
                    tmpSB.append(line_sep).append(indentS).append(lt)
                            .append("!--").append(comment).append("--").append(gt).append(line_sep);
                    prevIsText = false;
                    break;
                case Node.CDATA_SECTION_NODE:
                    tmpSB.append(line_sep).append(indentS).append(lt).append("!CDATA[")
                            .append(tmpN.getNodeValue()).append("]]").append(line_sep);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    if (tmpN instanceof DocumentType) {
                        DocumentType docType = (DocumentType) tmpN;
                        String pubId = docType.getPublicId();
                        String sysId = docType.getSystemId();
                        if (null != pubId && pubId.length() > 0) {
                            if (null != sysId && sysId.length() > 0) {
                                tmpSB.append(lt).append("!DOCTYPE ").append(docType.getName())
                                        .append(" PUBLIC \"").append(pubId).append(" \"").append(sysId).append("\">").append(line_sep);
                            } else {
                                tmpSB.append(lt).append("!DOCTYPE ").append(docType.getName())
                                        .append(" PUBLIC \"").append(pubId).append("\">").append(line_sep);
                            }
                        } else {
                            tmpSB.append(lt).append("!DOCTYPE ").append(docType.getName())
                                    .append(" SYSTEM \"").append(docType.getSystemId()).append("\">").append(line_sep);
                        }
                    } else {
                        logger.warn(gm() + "Document Type node does not implement DocumentType: "+ tmpN);
                    }
                    break;
                default:
                    logger.warn(gm() + tmpN.getNodeType() + " : " + tmpN.getNodeName());
            }

            Node next = treeWalker.firstChild();
            if (null != next) {
                if (type == Node.ELEMENT_NODE && indent) {
                    indentS = indentS + " ";
                }
                tmpN = next;
                continue;
            }

            if (tmpN.getNodeType() == Node.ELEMENT_NODE) {
                tmpSB.append(lt).append("/").append(tmpN.getNodeName()).append(gt).append(line_sep);
                prevIsText = false;
            }

            next = treeWalker.nextSibling();
            if (null != next) {
                tmpN = next;
                continue;
            }

            tmpN = null;
            next = treeWalker.parentNode();
            while (null != next) {
                if (next.getNodeType() == Node.ELEMENT_NODE) {
                    if (indent) {
                        if (indentS.length() > 0) {
                            indentS = indentS.substring(1);
                        } else {
                            logger.error(gm() + "indent: " + next.getNodeName() + " " + next);
                        }
                    }
                    tmpSB.append(line_sep).append(indentS).append(lt).append("/")
                            .append(next.getNodeName()).append(gt).append(line_sep);
                    prevIsText = false;
                }
                next = treeWalker.nextSibling();
                if (null != next) {
                    tmpN = next;
                    break;
                }
                next = treeWalker.parentNode();
            }
        }
        return tmpSB.toString();
    }
}

    

