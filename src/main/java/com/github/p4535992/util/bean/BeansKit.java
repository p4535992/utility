package com.github.p4535992.util.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

/**
 * Created by 4535992 on 21/04/2015.
 * @version 2015-06-25
 */
@SuppressWarnings("unused")
public class BeansKit implements  org.springframework.context.ResourceLoaderAware,BeanPostProcessor {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( BeansKit.class);

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
        String path = toStringUriWithPrefix(getResourceAsFile(filePathXml, thisClass));
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
                    File file = getResourceAsFile(filePathXml, thisClass);
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
                        logger.error(e4.getMessage(),e4);
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
                String path = toStringUriWithPrefix(getResourceAsFile(spath, thisClass));
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
                    logger.error(e4.getMessage(), e4);
                }

            }
        }
        return context;
    }

    public static String getResourceAsString(String fileName,Class<?> thisClass) {
        String result;
        try {
            result = org.apache.commons.io.IOUtils.toString(thisClass.getClassLoader().getResourceAsStream(fileName));
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static File getResourceAsFile(String name,Class<?> thisClass) {
        try {
            //noinspection ConstantConditions
            return new File(thisClass.getClassLoader().getResource(name).getFile());
        }catch(java.lang.NullPointerException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static File getResourceSpringAsFile(String pathRelativeToFileOnResourceFolder) {
        try {
            //noinspection ConstantConditions
            return getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,null,null).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static File getResourceSpringAsFile(String pathRelativeToFileOnResourceFolder,Class<?> clazz) {
        try {
            //noinspection ConstantConditions
            return getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,clazz,null).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static File getResourceSpringAsFile(String pathRelativeToFileOnResourceFolder,ClassLoader classLoader) {
        try {
            //noinspection ConstantConditions
            return getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,null,classLoader).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static org.springframework.core.io.Resource getResourceSpringAsResource(
            Object fileNameOrUri,Class<?> clazz,ClassLoader classLoader) {
        try {
            org.springframework.core.io.Resource yourfile;
            //if File
            if(fileNameOrUri instanceof File && ((File) fileNameOrUri).exists()){
                yourfile = new org.springframework.core.io.FileSystemResource(((File) fileNameOrUri));
            }
            //if URL
            else if(org.springframework.util.ResourceUtils.isUrl(String.valueOf(fileNameOrUri))  || fileNameOrUri instanceof URL) {
                if (fileNameOrUri instanceof URL) {
                    yourfile = new org.springframework.core.io.UrlResource((URL) fileNameOrUri);
                } else {
                    yourfile = new org.springframework.core.io.UrlResource(String.valueOf(fileNameOrUri));
                }
            //if Path or URI
            }else if(fileNameOrUri instanceof Path || fileNameOrUri instanceof URI) {
                if (fileNameOrUri instanceof Path) {
                    yourfile = new org.springframework.core.io.PathResource((Path) fileNameOrUri);
                } else {
                    yourfile = new org.springframework.core.io.PathResource((URI) fileNameOrUri);
                }
          /*  }else if(fileNameOrUri instanceof Class){
                org.springframework.core.io.ClassRelativeResourceLoader relativeResourceLoader =
                        new org.springframework.core.io.ClassRelativeResourceLoader((Class<?>) fileNameOrUri);
                yourfile = relativeResourceLoader.getResource("")
                */
            //if InputStream
            }else if(fileNameOrUri instanceof InputStream){
                    yourfile = new org.springframework.core.io.InputStreamResource((InputStream) fileNameOrUri);
            }else if(fileNameOrUri instanceof byte[]){
                yourfile = new org.springframework.core.io.ByteArrayResource((byte[]) fileNameOrUri);
            //if String path toa file or String of a URI
            }else if(fileNameOrUri instanceof String){
                if (classLoader != null) {
                    yourfile = new org.springframework.core.io.ClassPathResource(String.valueOf(fileNameOrUri), classLoader);
                } else if (clazz != null) {
                    yourfile = new org.springframework.core.io.ClassPathResource(String.valueOf(fileNameOrUri), clazz);
                } else {
                    yourfile = new org.springframework.core.io.ClassPathResource(String.valueOf(fileNameOrUri));
                }
            }else{
                logger.error("Can't load the resource for the Object with Class:"+fileNameOrUri.getClass().getName());
                return null;
            }
            return yourfile;
        }catch(IOException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String readResourceSpring(org.springframework.core.io.Resource resource){
        try {
           /* org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.ClassPathResource(fileLocationInClasspath);*/
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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

    /**
     * Method to get a resource from relative path with spring.
     * @param location the {@link String} location of the file on the resource folders e.g. "location.txt".
     * @return the {@link Resource} of Spring core io.
     */
    public Resource getResource(String location){
        return resourceLoader.getResource(location);
    }

    public void showResourceDataOnResourceLoader(String absolutePathToFile,ResourceLoader resourceLoader) throws IOException {
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

    /**
     * Convert filename string to a URI.
     * Map '\' characters to '/' (this might break if '\' is used in
     * a Unix filename, but this is assumed to be a very rare occurrence
     * as '\' is often used with special meaning on Unix.)
     * For unix-like systems, the absolute filename begins with a '/' and is preceded by "file://".
     * For other systems an extra '/' must be supplied.
     *
     * @param filePath string of the path to the file
     * @return path to the in uri formato with prefix file:///
     */
    private static String toStringUriWithPrefix(String filePath) {
        StringBuilder mapFileName = new StringBuilder(filePath);
        for (int i = 0; i < mapFileName.length(); i++) {
            if (mapFileName.charAt(i) == '\\')
                mapFileName.setCharAt(i, '/');
        }
        if (filePath.charAt(0) == '/') return "file://" + mapFileName.toString();
        else return "file:///" + mapFileName.toString();
    }

    /**
     * Method to convert a File to a URI with the prefix file://.
     *
     * @param file the File to convert.
     * @return the String URI with prefix.
     */
    public static String toStringUriWithPrefix(File file) {
        return toStringUriWithPrefix(file.getAbsolutePath());
    }
    
    //-----------------------------------------------------------------------------------------

    /*public static Collection<?> collect(Collection<?> collection, String propertyName) {
        return org.apache.commons.collections.CollectionUtils.collect(collection, new
                org.apache.commons.beanutils.BeanToPropertyValueTransformer(propertyName));
    }*/



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

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeforeInitialization : " + beanName);
        return bean;
        // you can return any other object as well
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("AfterInitialization : " + beanName);
        return bean;
        // you can return any other object as well
    }
}
