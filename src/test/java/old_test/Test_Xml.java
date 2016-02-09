/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package old_test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import com.github.p4535992.util.xml.XMLUtilities;

/**
 *
 * @author 4535992
 */
public class Test_Xml {
    
    
    //TEST
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException, org.jdom2.JDOMException, SAXException, TransformerException {
          String path= System.getProperty("user.dir")+
                 "\\src\\home.home\\utils\\xml\\hibernate.cfg.xml";
          File file = new File(path);
          // <property name="connection.driver_class">com.sql.jdbc.Driver</property>
          //XMLUtil2.updateInnerTextOfSpecificElement(path, "property", "name", "connection.driver_class", "XXXX");
          XMLUtilities.updateValueOfAttribute(file, "mapping", "resource", "YYY");
    }//main
    
}
