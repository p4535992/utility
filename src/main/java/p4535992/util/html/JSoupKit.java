/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p4535992.util.html;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esd91martent
 */
public class JSoupKit {
    
    //Variable to filter the attributes
        private static boolean filterAttr = false;

    public static boolean isFilterAttr() {
        return filterAttr;
    }

    public static void setFilterAttr(boolean filterAttr) {
        JSoupKit.filterAttr = filterAttr;
    }
      

        /// <summary>
        /// Method for clean the string o html of the HTMLO document
        /// </summary>
        /// <param name="Testo"></param>
        /// <returns></returns>
        private static String clean(String Testo)
        {
            if (Testo!=null && !Testo.isEmpty() && !Testo.trim().isEmpty())
            {          
                Testo = Testo.replace("&nbsp;", " ").replace("\t", " ").replace("\r", " ").replace("\n", " ").replace("�", "à").trim();
                Testo = Testo.replaceAll("\\s+", " ");
            }
            //else if(Testo!=null && !Testo.isEmpty()){Testo = "";}
            else { Testo = null; }                        
            return Testo;    
        }
        
        /// <summary>
        /// Method for extract any type of element from a html document 
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> UniversalExtractor(String HTMLDocument, boolean HTML, String tagName, String attribute, String valueAttribute)throws Exception
        {        
            tagName = tagName + "["+attribute+"="+valueAttribute+"]";
            return UniversalExtractor(HTMLDocument, HTML, tagName);
        }

        /// <summary>
        /// Method for extract any type of element from a html document 
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> UniversalExtractor(String HTMLDocument, boolean HTML, String tagName)throws Exception
        {        
            org.jsoup.nodes.Document htmldoc = org.jsoup.Jsoup.parse(HTMLDocument);          
            return UniversalExtractor(htmldoc, HTML, tagName);
        }
        
        /// <summary>
        /// Method for extract any type of element from a html document 
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> UniversalExtractor(URL url, boolean HTML, String tagName)throws Exception
        {        
            org.jsoup.nodes.Document htmldoc = org.jsoup.Jsoup.connect(url.toString()).get();          
            return UniversalExtractor(htmldoc, HTML, tagName);
        }

        /// <summary>
        /// Method for extract any type of element from a html document 
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> UniversalExtractor(org.jsoup.nodes.Document HTMLDocument,boolean HTML, String tagName) throws Exception
        {           
            String rootTag ="table";
            if (tagName.toLowerCase().equals("ul") || tagName.toLowerCase().contains("//ul")) { rootTag = "ul"; }
            else if (tagName.toLowerCase().equals("ol")|| tagName.toLowerCase().contains("//ol")) { rootTag = "ol"; }
            else if (tagName.toLowerCase().equals("li") || tagName.toLowerCase().contains("//li")) { rootTag = "li"; }
            else if (tagName.toLowerCase().equals("table") || tagName.toLowerCase().contains("//table")) { rootTag = "table"; }
            else { throw new Exception("ERROR: the selected tagName in not supported, plese use : li,ul,ol or table"); }
            List<List<List<String>>> ResultCollection;
            //HtmlNodeCollection RootTag2 = HTMLWork.SelectNodes(".//tbody");
            org.jsoup.select.Elements RootTag = HTMLDocument.select(rootTag);//e.home ul,ol,table
            ResultCollection = subExtractor6(RootTag, HTML);       
            return ResultCollection;
             
        }

        /// <summary>
        /// Method for the extraction of simple item list <li></li>"
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> TablesExtractor(String HTMLDocument, boolean HTML) throws Exception
        {            
            return UniversalExtractor(HTMLDocument, HTML, "table");
        }
        
        /// <summary>
        /// Method for the extraction of simple item list <li></li>"
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> TablesExtractor(URL url, boolean HTML) throws Exception
        {            
            return UniversalExtractor(url, HTML, "table");
        }

        /// <summary>
        /// Method for the extraction of simple item list <li></li>"
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> SimpleListItemExtractor(String HTMLDocument, boolean HTML) throws Exception
        {
            return UniversalExtractor(HTMLDocument, HTML, "li");
        }

        /// <summary>
        /// Method for the extraction of a order list <ol></ol>"
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> OrderListItemExtractor(String HTMLDocument, boolean HTML) throws Exception
        {           
           return UniversalExtractor(HTMLDocument, HTML, "ol");
        }

        /// <summary>
        /// Method for the extraction of a order list <ul></ul>"
        /// </summary>
        /// <param name="HTMLWork"></param>
        /// <param name="HTML"></param>
        /// <param name="HtmlResponse"></param>
        /// <returns></returns>
        public static List<List<List<String>>> UnorderListItemExtractor(String HTMLDocument, boolean HTML) throws Exception
        {        
            return UniversalExtractor(HTMLDocument, HTML, "ul");
        }

                         
        /// <summary>
        /// Method for extract all elements from a html document , version : 6 (2015-04-15)
        /// </summary>
        /// <param name="TableColl"></param>
        /// <param name="HTML"></param>
        /// <param name="ResultCollection"></param>
        private static List<List<List<String>>> subExtractor6(org.jsoup.select.Elements TableColl, boolean HTML)
        {
            List<List<List<String>>> ResultCollection = new ArrayList<List<List<String>>>();
            //Nuova tabella
            for(org.jsoup.nodes.Element TableElem : TableColl)
            {
                switch (TableElem.tagName().toUpperCase())
                {
                    case "TABLE":
                    case "UL":
                    case "OL":
                        {
                            int iMaxCol = 0;
                            List<List<String>> dtTableList = new ArrayList<List<String>>();
                            for(org.jsoup.nodes.Element _CurrentElement : TableElem.children())
                            {
                                switch (_CurrentElement.tagName().toUpperCase())
                                {
                                    case "TR":                                         
                                        {
                                            List<String> Cols = new ArrayList<String>();                                        
                                            for (int iElem = 0; iElem < _CurrentElement.children().size(); iElem++)
                                            {
                                                if (_CurrentElement.child(iElem).tagName().toUpperCase() == "TD"
                                                    || _CurrentElement.child(iElem).tagName().toUpperCase() == "TH")
                                                {
                                                    if (_CurrentElement.child(iElem).children().size() == 0) Cols = Extractor6(_CurrentElement.child(iElem), HTML, Cols);                                                 
                                                    else
                                                        for(org.jsoup.nodes.Element node : _CurrentElement.child(iElem).children())
                                                        {
                                                            Cols = Extractor6(node, HTML, Cols);
                                                        }                                                  
                                                }
                                                //if td o th
                                                else
                                                {
                                                    Cols = Extractor6(_CurrentElement.child(iElem), HTML, Cols);
                                                }
                                            }//for each node in tr
                                            if (Cols.size() > iMaxCol) iMaxCol = Cols.size();
                                            if (Cols.size() > 0) dtTableList.add(Cols);
                                        }
                                        break;
                                    case "TBODY":
                                    case "THEAD":
                                    case "TFOOT":
                                        {
                                            //Cols = ExtractorRomis(_CurrentElement, HTML, Cols);
                                            for(org.jsoup.nodes.Element _rows : _CurrentElement.children())
                                            {                                              
                                                if (_rows.tagName().toUpperCase() == "TR")
                                                {
                                                    List<String> Cols = new ArrayList<String>();                                                  
                                                    for (int iElem = 0; iElem < _rows.children().size(); iElem++)
                                                    {                                                      
                                                        if (_rows.child(iElem).tagName().toUpperCase() == "TD" || _rows.child(iElem).tagName().toUpperCase() == "TH")
                                                        {
                                                            if (_rows.child(iElem).children().size() == 0) Cols = Extractor6(_rows.child(iElem), HTML, Cols);                                                          
                                                            else 
                                                                for(org.jsoup.nodes.Element node : _rows.child(iElem).children())
                                                                {
                                                                    Cols = Extractor6(node, HTML, Cols);
                                                                }                                                          
                                                        }
                                                    }//for ielem _rows.ChildNodes
                                                    if (Cols.size() > iMaxCol) iMaxCol = Cols.size();
                                                    if (Cols.size() > 0) dtTableList.add(Cols);
                                                }//if _rows NamespaceHandling is TR
                                            }//foreach HtmlNode _rows in _CurrentElement.ChildNodes
                                        }
                                        break;
                                    case "CAPTION":
                                    case "H3":
                                    case "H2":
                                    case "LI":
                                        {
                                            List<String> Cols = new ArrayList<String>();
                                            Cols = Extractor6(_CurrentElement, HTML, Cols);
                                            if (Cols.size() > iMaxCol) iMaxCol = Cols.size();
                                            if (Cols.size() > 0) dtTableList.add(Cols);
                                            break;
                                        }
                                }//end of the switch TR,TBODY,CAPTION
                            }
                            ResultCollection.add(dtTableList);
                        }
                        break;
                    case "LI":
                        {
                            int iMaxCol = 0;
                            List<List<String>> dtTableList = new ArrayList<List<String>>();
                            List<String> Cols = new ArrayList<String>();
                            Cols = Extractor6(TableElem, HTML, Cols);
                            if (Cols.size() > iMaxCol) iMaxCol = Cols.size();
                            if (Cols.size() > 0) dtTableList.add(Cols);
                            ResultCollection.add(dtTableList);
                        }
                        break;
                    case "DL":
                        {
                            org.jsoup.nodes.Element _CurrentElement = TableElem;
                            //iCurCol = 0; iCurRow = 0;
                            int iMaxCol = 0;
                            //int iCurCol = 0;
                            List<List<String>> dtTable = new ArrayList<List<String>>();
                            List<String> Cols = new ArrayList<String>();
                            for (int iRow = 0; iRow < _CurrentElement.children().size(); iRow++)
                            {
                                if (_CurrentElement.child(iRow).tagName().toUpperCase() == "DT")
                                {
                                    Cols = Extractor6(_CurrentElement.child(iRow), HTML, Cols);
                                    if (Cols.size() > iMaxCol) iMaxCol = Cols.size();
                                    if (Cols.size() > 0) dtTable.add(Cols);
                                    Cols = new ArrayList<String>();
                                    //iCurCol = 0;                                
                                    //CurTabella[iCurRow - 1, iCurCol] = (Testo == null) ? "" : Testo;
                                    //iCurCol = 1;
                                }

                                if (_CurrentElement.child(iRow).tagName().toUpperCase() == "DD" 
                                    //&& iCurCol == 1
                                    )
                                {
                                    Cols = Extractor6(_CurrentElement.child(iRow), HTML, Cols);
                                }
                                //iCurCol++;
                            }
                            ResultCollection.add(dtTable);
                        }
                        break;
                }
            }
            return ResultCollection;
        }//subTableWithHtmlNode6

        /// <summary>
        /// Method of support for UniversalExtractor(); , version : 6 (2015-04-15)
        /// </summary>
        /// <param name="node"></param>
        /// <param name="HTML"></param>
        /// <param name="sCols"></param>
        /// <returns></returns>
        private static List<String> Extractor6(org.jsoup.nodes.Element node, boolean HTML, List<String> Cols)
        {
            String[] goodTag = new String[] { "A", "IMG", "P", "SPAN", "CAPTION", "DIV", "DD", "DT", "DL", "LI", "DIV", "BR", "STRONG"};//...possible new entry -> STRONG,#TEXT
            String[] goodAttr = new String[] { "SRC", "HREF", "ID", "NAME", "VALUE", "TITLE", "ALT","ONCLICK" }; //... aggiungere via via gli attributi che si reputano interessanti
            String Testo;                      
            org.jsoup.nodes.Element child = null;                
            if (node.children().size() == 1 && Arrays.asList(goodTag).contains(node.children().first().tagName().toUpperCase())) //Se è una tag in cui non si deve estarre informazioni a questo giro
            { 
                return Cols; //..do nothing
            }
            else if (node.children().size() == 1 && node.children().first().tagName().toUpperCase() != "#TEXT")
            {  //...o il figlio è unico e di tipo testo...               
                child = node.child(0);
            }
            else
            {
                child = node; //...non ho a figli
            }

            if (HTML == false)
            {
                Testo = clean(child.ownText());
                if (Testo != null) Cols.add(Testo); 
                if (!isFilterAttr() && Arrays.asList(goodTag).contains(child.tagName().toUpperCase()))
                {
                    Testo = "";
                    boolean check = false;
                    for(String attr : goodAttr)
                    {
                        if (child.attributes().asList().contains(attr) && clean(child.attr(attr))!=null)
                        {
                            Testo = Testo + "[" + attr + "=" + clean(child.attr(attr)) + "] ";
                        }
                        else if (child.attributes().asList().contains(attr) && Testo == "" && Arrays.asList(goodAttr).contains(attr))
                        {
                            check = true;
                        }
                    }
                    Testo = clean(Testo);
                    if (check == true && clean(Testo)==null) Cols.add((Testo == null) ? "" : Testo); //Testo = ""; 
                    else if (Testo != null)  Cols.add(Testo); 
                }
                else if ("IMG".contains(child.tagName().toUpperCase()))//.Equals("IMG")...Se il filtro degli attrbituti è true ma sappiamo che vi è un qualcosa di utile l'unico caso trovato sono le immagini
                {
                    Testo = "";
                    Cols.add(Testo); 
                }
            }
            else
            {
                Testo = clean(child.outerHtml());
                if (Testo != null) Cols.add(Testo); 
            }          
            return Cols;
        }
        
        public static org.jsoup.nodes.Document convertHTMLFileToJsoupDocument(File file,String baseUrl) throws IOException{
            //File input = new File("/tmp/input.html");
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, "UTF-8", baseUrl);
            return doc;
        }
        
        public static org.jsoup.nodes.Document convertHTMLStringToJsoupDocument(String html) throws IOException{
            //File input = new File("/tmp/input.html");
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
            return doc;
        }
        
        private List<Map<String,String>> parseDataSet(String xml){
            List<Map<String,String>> maps=new ArrayList<Map<String,String>>();
            Map<String,String> map;
            org.jsoup.nodes.Document doc=org.jsoup.Jsoup.parse(xml);
            org.jsoup.select.Elements rows=doc.getElementsByTag("Table");
            for (org.jsoup.nodes.Element row : rows) {
              org.jsoup.select.Elements cells=row.children();
              map=new HashMap<String,String>();
              for (org.jsoup.nodes.Element cell : cells) {
                map.put(cell.tagName(),cell.html());
              }
              maps.add(map);
            }
            return maps;
        }
        
//        public DataSet convert(String xml){       
//            DataSet dataSet=new DataSet();
//            Document doc=Jsoup.parse(xml);
//            Column column;
//            Elements headerEls=doc.select(getColumnSelector());
//            String type;
//            int index;
//            for (  Element rowEl : headerEls) {
//              type=rowEl.attr("type");
//              index=type.indexOf(":");
//              column=new Column(rowEl.attr("name").toLowerCase(),index != -1 ? type.substring(index + 1) : type);
//              dataSet.addColumn(column);
//              Formater<?> formater=this.getFormaters().get(column.getType());
//              if (formater != null) {
//                column.setFormater(formater);
//              }
//            }
//        }
        
        public static String getFullContentOfFooter(org.jsoup.nodes.Document doc){
            String[] specialFooter = new String[]{"footer",
                "div[id$=footer]"/*div id ending with footer*/,"div[id^=footer]"/*div id starting with footer*/};
            return getContentOfTag(doc, specialFooter,false);
        }
        
        public static String getFullContentOfHeader(org.jsoup.nodes.Document doc){
            String[] specialHeader = new String[]{"head","title",
                "div[id$=header]"/*div id ending with footer*/,"div[id^=header]"/*div id starting with footer*/};
            return getContentOfTag(doc, specialHeader,false);
        }
        
        public static String getContentOfTitle(org.jsoup.nodes.Document doc){
            String[] specialHeader = new String[]{"title"};
            return getContentOfTag(doc, specialHeader,true);
        }
        
        public static String getContentOfTag(org.jsoup.nodes.Document doc,String[] special,boolean first){         
            StringBuilder sb = new StringBuilder();
            sb.append("");
            org.jsoup.select.Elements sFooter;
            int i = 0;
            int j = 0;
            while(i < special.length){
                sFooter = doc.select(special[i]);//div id ending with footer
                while(j < sFooter.size()){
                    sb.append(sFooter.get(j).outerHtml()+System.getProperty("line.separator"));
                    if(first) break;
                    else j++;
                }
                i++;
                if(!sb.toString().isEmpty()) break;
            }      
            return sb.toString();
        }

        public static org.jsoup.nodes.Document getResourceAsStream(File file) throws IOException{
              InputStream ins = JSoupKit.class.getClassLoader().getResourceAsStream(file.getAbsolutePath());
              org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(ins, "UTF-8", file.getName());
              return doc;
        }
        
    }
    

 

