/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.transform.TransformerConfigurationException;

import org.xml.sax.SAXException;
import com.github.p4535992.util.repositoryRDF.jena.Jena2Kit;

/**
 *
 * @author 4535992
 */
public class Test_Jena {
    
    
     public static void main(String args[]) throws NullPointerException, InterruptedException, InvocationTargetException, SAXException, IOException,TransformerConfigurationException{  
         //GeoDocumentDAO dao = new GeoDocumentDAO();
         
         //home.home.utils.log log = new home.home.utils.log("xxx",".txt");
         String outputN3Knime= "C:\\Users\\Marco\\Desktop\\File SQL Supporto Programma Tesi\\2015-05-05\\result_silk_km4c-InfoDoc_M-wgs84_COORD_B1.nt";
         File file = new File(outputN3Knime);
         //EncodingUtil.rewriteTheFileToUTF8(file);
         if(file.exists()) {
             //List<String> lines = EncodingUtil.convertUnicodeEscapeToUTF8(new File(outputN3Knime));
             //EncodingUtil.writeLargerTextFileWithReplace2(outputN3Knime, lines);
             Jena2Kit.convertFileTripleToAnotherFormat(new File(outputN3Knime), "csv");
             //JenaKit.convertTo(file, "turtle");
         }


//         JenaInfoDocument jInfo = new JenaInfoDocument();
//
//
//         jInfo.readQueryAndCleanTripleInfoDocument(
//                 FileUtil.filenameNoExt(file),
//                 FileUtil.path(file),
//                 "output",
//                 FileUtil.extension(file),
//                 "ttl"
//         );
            
         //JENAUtil.convertTo(file,"csv");
         //JENAUtil.convertTo(file,"xml");
         
         //JENAUtil6.test();
        
     }
}
