/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;


import com.github.p4535992.util.encoding.EncodingUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 *
 * @author Marco
 */
public class Test_Encoding {
    
	public static void main(String[] args) throws IOException{
		
		//String.getBytes(Charset)
                //String s ="";
                //s.getBytes("UTF-16")
		//treat as a small file
		List<String> lines;
		File f = new File("");
                //lines = text.readSmallTextFile(FILE_NAME);
                //log(lines);
                //lines.add("This is a line added in code.");
                //text.writeSmallTextFile(lines, FILE_NAME);
                
                //treat as a large file - use some buffering
                lines = EncodingUtil.convertUnicodeEscapeToUTF8(f);
                //lines = readLargerTextFileWithReturn(FileUtil.filename(f));                     
                EncodingUtil.writeLargerTextFileWithReplace2("output", lines);
            }
            
          //main
}
