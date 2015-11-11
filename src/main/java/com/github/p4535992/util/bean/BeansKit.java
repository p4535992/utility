package com.github.p4535992.util.bean;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.log.SystemLog;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.MalformedURLException;

/**
 * Created by 4535992 on 21/04/2015.
 * @version 2015-06-25
 */
@SuppressWarnings("unused")
public class BeansKit implements  org.springframework.context.ResourceLoaderAware{

    private ResourceLoader resourceLoader;

    public static <T> T getBeanFromContext(String nameOfBean, Class<T> requiredType,AbstractApplicationContext context){
        T obj = context.getBean(nameOfBean,requiredType);
        context.registerShutdownHook();
        return obj;
    }

    public static <T> T getBeanFromContext(String nameOfBean, Class<T> requiredType,ApplicationContext context ){
        // retrieve configured instance
        return context.getBean(nameOfBean, requiredType);
    }

    public static ApplicationContext tryGetContextSpring(String filePathXml,Class<?> thisClass) throws IOException {
        ApplicationContext context = new GenericApplicationContext();
        String path = FileUtilities.toStringUriWithPrefix(getResourceAsFile(filePathXml, thisClass));
        try {
            //This container loads the definitions of the beans from an XML file.
            // Here you do not need to provide the full path of the XML file but
            // you need to set CLASSPATH properly because this container will look
            // bean configuration XML file in CLASSPATH.
            //You can force with the fileSystem using "file:" instead of "classpath:".
            context = new ClassPathXmlApplicationContext(path);
        } catch (Exception e1) {
            try{
                context = new ClassPathXmlApplicationContext(filePathXml,thisClass);
            }catch(Exception e2) {
                try {
                    //This container loads the definitions of the beans from an XML file.
                    // Here you need to provide the full path of the XML bean configuration file to the constructor.
                    //You can force with file: property to the class file.
                    File file = FileUtilities.toFile(filePathXml, thisClass);
                    if (file!= null && file.exists()) {
                        context = new FileSystemXmlApplicationContext(file.getPath());
                    }else{
                        return null;
                    }
                } catch (Exception e3) {
                    try {
                        AbstractApplicationContext abstractContext;
                        abstractContext = new ClassPathXmlApplicationContext(path);
                        context = abstractContext;
                    } catch (Exception e4) {
                        SystemLog.exception(e4);
                    }
                }
            }
        }
        return context;
    }


    public static ApplicationContext tryGetContextSpring(String[] filesPathsXml,Class<?> thisClass) throws IOException {
        String[] paths = new String[filesPathsXml.length];
        int i = 0;
        for(String spath : filesPathsXml){
            if(new File(spath).exists()) {
                String path = FileUtilities.toStringUriWithPrefix(getResourceAsFile(spath, thisClass));
                paths[i] = path;
                i++;
            }
        }
        ApplicationContext context = new GenericApplicationContext();
        try {
            context = new ClassPathXmlApplicationContext(paths);
        } catch (Exception e1) {
            try{
                context = new ClassPathXmlApplicationContext(paths,thisClass);
            }catch(Exception e2) {
                try {
                    AbstractApplicationContext abstractContext;
                    abstractContext = new ClassPathXmlApplicationContext(paths);
                    context = abstractContext;
                } catch (Exception e4) {
                    SystemLog.exception(e4);
                }

            }
        }
        return context;
    }


    public static InputStream getResourceAsStream( String name,Class<?> thisClass ) {
        return thisClass.getClassLoader().getResourceAsStream(name);
    }

    public static File getResourceAsFile(String name,Class<?> thisClass) {
        try {
            return new File(thisClass.getClassLoader().getResource(name).getFile());
        }catch(java.lang.NullPointerException ne){
            SystemLog.exception(ne,BeansKit.class);
            return null;
        }
    }

    public static String getResourceAsString(String fileName,Class<?> thisClass) {
        String result = "";
        try {
            result = org.apache.commons.io.IOUtils.toString(thisClass.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return result;
    }

    public static File getResourceSpringAsFile(String fileName) {
        try {
            final org.springframework.core.io.Resource yourfile = new org.springframework.core.io.ClassPathResource(fileName);
            return yourfile.getFile();
        }catch(IOException e){
            SystemLog.exception(e);
            return null;
        }
    }

    public static org.springframework.core.io.Resource getResourceSpringFromString(String uri) throws MalformedURLException {
        org.springframework.core.io.Resource resource;
        File file=new File(uri);
        if (file.exists()) {
            resource=new org.springframework.core.io.FileSystemResource(uri);
        }
        else   if (org.springframework.util.ResourceUtils.isUrl(uri)) {
            resource = new org.springframework.core.io.UrlResource(uri);
        }
        else {
            resource=new org.springframework.core.io.ClassPathResource(uri);
        }
        return resource;
    }

    public static String readResourceSpring(String fileLocationInClasspath){
        try {
            org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.ClassPathResource(fileLocationInClasspath);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            SystemLog.exception(e);
            return null;
        }
    }

    public static String readResource(String name,Class<?> thisClass ){
        try {
            InputStream inputStream = getResourceAsStream(name, thisClass);
            if (inputStream == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    stringBuilder.append(buffer, 0, read);
                }
            } finally {
                inputStream.close();
            }
            return stringBuilder.toString();
        }catch (IOException e){
            SystemLog.exception(e);
            return null;
        }
    }

    public static void printResourceSpringToConsole(org.springframework.core.io.Resource resource) {
        try{
            InputStream is = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void showResourceData(String absolutePathToFile) throws IOException {
        //This line will be changed for all versions of other examples : "file:c:/temp/filesystemdata.txt"
        org.springframework.core.io.Resource banner = resourceLoader.getResource("file:"+absolutePathToFile);
        InputStream in = banner.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            System.out.println(line);
        }
        reader.close();
    }

//  public static org.springframework.context.ApplicationContext createApplicationContext(String uri) throws MalformedURLException {
//    org.springframework.core.io.Resource resource = getSpringResourceFromString(uri);
//    //LOG.debug("Using " + resource + " from " + uri);
//    try {
//      return new ResourceXmlApplicationContext(resource) {
//        @Override
//        protected void initBeanDefinitionReader(org.springframework.beans.context.xml.XmlBeanDefinitionReader reader) {
//          reader.setValidating(true);
//        }
//      };
//    }
//  }
}
