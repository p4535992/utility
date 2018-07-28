package util;

import com.drew.tools.FileUtil;
import com.github.p4535992.util.file.archive.ZtZipUtilities;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by 4535992 on 09/02/2016.
 */
public class Test_ZtZipUtilities{


    @Test
    public void getFromDirectory() throws IOException{

        File test = new File(Test_ZtZipUtilities.class.getClassLoader().getResource("ac-transit_20150218_1708.zip").getFile());



        List<File> files = ZtZipUtilities.extractFilesFromZip(test);

        List<File> files2 = ZtZipUtilities.extractFilesByPattern(test,"agency");

        List<File> files3 = ZtZipUtilities.extractFilesBySuffix(test,".mp3");

        List<File> files4 = ZtZipUtilities.extractFilesBySuffix(test,".txt");

        String content = FileUtils.readFileToString(files2.get(0));
        String s ="";
    }
}
