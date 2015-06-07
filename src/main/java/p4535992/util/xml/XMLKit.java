package p4535992.util.xml;

import org.xml.sax.InputSource;
import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import p4535992.util.log.SystemLog;

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
 */
public class XMLKit {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(XMLKit.class);
    private static DocumentBuilderFactory docFactory;
    private static DocumentBuilder docBuilder;
    private static Document doc;
    private static Element rootElement;


    /**
     * Method to load the XML Document
     * @param fileXML the XML file to input
     * @return the document object initialize
     * @throws SAXException
     * @throws IOException
     */
    public static Document loadDocumentFromFile(File fileXML) throws SAXException, IOException {
        //FileInputStream file = new FileInputStream(fileXML); //optional
        doc = newDocumentXML();
        doc = docBuilder.parse(fileXML);
        SystemLog.message("Documento W3C caricato da file:" + fileXML.getAbsolutePath());
        return doc;
    }

    /**
     * Method to load a XML Document
     * @param xml
     * @return the document object initialize
     * @throws SAXException
     * @throws IOException
     */
    public static Document loadDocumentFromFile(String xml) throws SAXException, IOException{
        doc = newDocumentXML();
        doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        SystemLog.message("Documento W3C caricato da file:" + xml);
        return doc;
    }

    /**
     * Method to initialize a new XML document
     * @return the document object initialize
     */
    public static Document newDocumentXML(){
        doc=null;
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
     * Method to insert a new XML file
     * @param pathFile where you want to insert the new file
     * @param nameFile name of the XML file
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
     *  Method for update the value of a attribute
     * @param xmlFile
     * @param tagName
     * @param nameAttribute
     * @param newValueAttribute
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     */
    public static void updateValueOfAttribute(File xmlFile,String tagName,String nameAttribute,String newValueAttribute)
            throws TransformerException, IOException, SAXException {
            doc =  loadDocumentFromFile(xmlFile);
            Element el = selectFirstElementByAttribute(doc, tagName, nameAttribute);
            //get map containing the attributes of this node
            NamedNodeMap attributes = el.getAttributes();
            //get the number of nodes in this map
            int numAttrs = attributes.getLength();
            for(int i =0; i < numAttrs; i++){
                Attr attr = (Attr) attributes.item(i);
                if( attr.getNodeName()==nameAttribute){
                    attr.setValue(newValueAttribute);
                    SystemLog.message("Update the value of the attribute:"+attr.getName()+ "="+newValueAttribute);
                    break;
                }
            }
            saveToXml(doc, xmlFile.getAbsolutePath());
    }

    /**
     * Get an Attribute from an Element.  Returns an empty String if none found
     * http://www.java2s.com/Code/Java/XML/ReturnalistofnamedElementswithaspecificattributevalue.htm
     * @param element the containing Element
     * @param name the attribute name
     * @return Attribute as a String
     */
    public static String getAttribute(Element element, String name) {
        return element.getAttribute(name);
    }

    /**
     * Method to return a list of named Elements with a specific attribute value.
     * @note http://www.java2s.com/Code/Java/XML/ReturnalistofnamedElementswithaspecificattributevalue.htm
     * @param element the containing Element
     * @param name the tag name
     * @param attribute Attribute name
     * @param value Attribute value
     * @param returnFirst Return only the first matching value?
     * @return List of matching elements
     */
    public static List selectElementsByAttributeValue(
            Element element, String name,String attribute, String value,boolean returnFirst) {
        NodeList  elementList = element.getElementsByTagName(name);
        List resultList  = new ArrayList();
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
     * Method t o return a single first Element with a specific attribute value. (maybe you can find a better method))
     * @param doc xml file of input
     * @param tagName string of the name tag xml
     * @param nameAttribute string of the name attribute xml
     * @return the element xml with the specific attribute
     */
    public static Element selectFirstElementByAttribute(Document doc,String tagName,String nameAttribute) {
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
     * Method get all attributes in a xml element
     * @param el xml element on input
     * @return arraylist oa attributes
     */
    public static ArrayList getAllAttributes(Element el){
        ArrayList<Attr> listAttr = new ArrayList<>();
        NamedNodeMap attributes = el.getAttributes();//get map containing the attributes of this node
        int numAttrs = attributes.getLength();  //get the number of nodes in this map
        for(int i =0; i < numAttrs; i++){
            Attr attr = (Attr) attributes.item(i);
            listAttr.add(attr);
        }
        return listAttr;
    }

    /**
     * Get root element
     * @deprecated  use {@link  Element root = doc.getDocumentElement();} instead.
     * @param doc xml document file
     * @return element root of the xml document
     */
    @Deprecated
    public static Element getRootElement(Document doc){
        Element root;
//        NodeList list = doc.getChildNodes();
//        for(int i =0; i < list.getLength(); i++){
//            Node nRoot = list.item(i);
//            if(nRoot.getNodeType()==Node.ELEMENT_NODE){
//                root = (Element)nRoot;
//                break;
//            }
//        }
        root = doc.getDocumentElement();
        return root;
    }

    /**
     * Method for print on the console the content of a xml file
     * @param xmlFile the xml file in input
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
     * Method to print a single node of a xml document
     * @param nodeList lista of nodes of a xml document
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


    /**
     * Metodo per l'update del valore di un attributo di un tag in un XML Document with SAX JDOM
     * @param xmlFile
     * @param tagName
     * @param nameAttribute
     * @param newValueAttribute
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
     * Method for update the value of a attribute
     * @param xmlFile
     * @param tagName
     * @param nameAttribute
     * @param newValueAttribute
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws TransformerException
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
     * @param filePath
     * @return DOM Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Document getDocument(String filePath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xmlDoc = docBuilder.parse(filePath);
        return xmlDoc;
    }

    /**
     * Method To save the Document in xml file
     * @param xmlDoc the XML Docuemnt you wan to save
     * @param filePath where you want ot save the file
     * @throws TransformerException
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
     * Method to convert a Node XML to a Element XML
     * @param node node XML to input
     * @return elment XML
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
     * Method to convert a Element XML to a Node XML
     * @param elem element XML to input
     * @return node XML
     */
    public static Node convertElementToNode(Element elem){
        Node n = elem.getFirstChild();
        return n;
    }

    /**
     * Method to convert a NODE XML to a string
     * @param n node XML to input
     * @return string of the node n
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
        StringBuffer sb = new StringBuffer();
        sb.append('<').append(name);
        NamedNodeMap attrs = n.getAttributes();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                sb.append(' ').append(attr.getNodeName())
                        .append("=\"").append(attr.getNodeValue()).append("\"");
            }
        }
        String textContent = null;
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
     * @param input
     */
    public static void loadXML(InputStream input) throws Exception {
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
            // Get Root xmlElement
            setRootElement();
        } catch (Exception e) {
            throw new Exception("Error load XML ", e);
        }
    }

    /**
     * Set the Root Element
     */
    public static void setRootElement(){
        rootElement = doc.getDocumentElement();
    }

    /**
     * Get the Root Element
     * @return the root element
     */
    public static Element getRootElement(){
        return rootElement;
    }

    /**
     * Check name of root element is as expected.
     * @param name
     * @return
     */
    public static boolean isRootName(String name) {
        return rootElement.getNodeName().equals(name);
    }

    /**
     * Get the type of the xml doc
     * @return string of the type
     */
    public static String getDoctype() {
        DocumentType doctype = doc.getDoctype();
        if (null != doctype) {
            return doctype.getName();
        }
        return null;
    }

    /**
     * Get the public id of the xml doc
     * @return string of the public id
     */
    public static String getPublicId() {
        DocumentType doctype = doc.getDoctype();
        if (null != doctype) {
            return doctype.getPublicId();
        }
        return null;
    }

    /**
     * Get the type of the root type name of the xml doc
     * @return string of the name of root type
     */
    public static String getRootTypeName() {
        return rootElement.getSchemaTypeInfo().getTypeName();
    }

    /**
     * Get the content of a  xml doc
     * @return string of the content o a  xml doc
     * @throws Exception
     */
    public static String getContent() throws Exception {
        NodeList childNodes = rootElement.getChildNodes();
        return serializeNodes(childNodes);
    }

    /**
     * Support the getCOntent() method
     * @param childNodes nde list of input
     * @return the string of the nodelist of input
     * @throws Exception
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
}

    /**
 	 * A SAX ContentHandler to find the prefixes declared on the root element.
     * @note I don't own any right on this piece of code that belongs to Norman Walsh , i just modified for my purpose
 	 * @author Norman Walsh
 	 * @version $Revision: 1.1 $
 	 */
	class PrefixGrabber extends DefaultHandler {
        private Hashtable nsHash = new Hashtable();
        private boolean root = true;

        public Hashtable getNamespaces() {
            return nsHash;
        }

        public void startPrefixMapping (String prefix, String uri) throws SAXException {
            if (root) {
                nsHash.put(prefix, uri);
            }
        }

        public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
        	    root = false;
        }
    }

