package util;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by 4535992 on 10/02/2016.
 */
public class Test_FileUtilities {


    @Test
    public void getPathOfFile(){
        File dir = new File(Test_ZtZipUtilities.class.getClassLoader().getResource("dir").getFile());

        List<File> files0 = (List<File>) FileUtils.listFiles(dir,TrueFileFilter.INSTANCE,TrueFileFilter.INSTANCE);

        File test = files0.get(0);

        String uri = test.toURI().toString();

        String uri2 = test.toURI().toString();
        String s ="";
    }
}
