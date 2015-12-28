/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.github.p4535992.util.file.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * This class is extremely useful for loading resources and classes in a fault tolerant manner
 * that works across different applications servers.
 *
 * It has come out of many months of frustrating use of multiple application servers at Atlassian,
 * please don't change things unless you're sure they're not going to break in one server or another!
 * 
 * It was brought in from oscore trunk revision 147.
 *
 * href: http://grepcode.com/file/repo1.maven.org/maven2/com.opensymphony/xwork/2.1.3/com/opensymphony/xwork2/util/ClassLoaderUtil.java
 *
 * @author $Author: hani $
 * @version $Revision: 117 $
 */
@SuppressWarnings("unused")
public class ClassLoaderUtil {
    //~ Methods ////////////////////////////////////////////////////////////////

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ClassLoaderUtil.class);

    /**
     * Load all resources with a given name, potentially aggregating all results 
     * from the searched classloaders.  If no results are found, the resource name
     * is prepended by '/' and tried again.
     *
     * This method will try to load the resources using the following methods (in order):
     * <ul>
     *  <li>From Thread.currentThread().getContextClassLoader()
     *  <li>From ClassLoaderUtil.class.getClassLoader()
     *  <li>callingClass.getClassLoader()
     * </ul>
     *
     * @param resourceName The name of the resources to load
     * @param callingClass The Class object of the calling object
     * @param aggregate if true search for all the aggregates resource on the folder 'resources'.
     * @return the Iterator Collection of all URl resource on the folder esources.
     * @throws java.io.IOException if any error is occurred.
     */
     public static Iterator<URL> getResources(String resourceName, 
             Class<?> callingClass, boolean aggregate) throws IOException {
         AggregateIterator<URL> iterator = new AggregateIterator<>();
         iterator.addEnumeration(Thread.currentThread().getContextClassLoader().getResources(resourceName));
         if (!iterator.hasNext() || aggregate) {
             iterator.addEnumeration(ClassLoaderUtil.class.getClassLoader().getResources(resourceName));
         }
         if (!iterator.hasNext() || aggregate) {
             ClassLoader cl = callingClass.getClassLoader();
             if (cl != null) {
                 iterator.addEnumeration(cl.getResources(resourceName));
             }
         }
         if (!iterator.hasNext() && (resourceName != null) &&
                 ((resourceName.length() == 0) || (resourceName.charAt(0) != '/'))) {
             return getResources('/' + resourceName, callingClass, aggregate);
         }
         return iterator;
     }

    /**
    * Load a given resource.
    *
    * This method will try to load the resource using the following methods (in order):
    * <ul>
    *  <li>From Thread.currentThread().getContextClassLoader()
    *  <li>From ClassLoaderUtil.class.getClassLoader()
    *  <li>callingClass.getClassLoader()
    * </ul>
    *
    * @param resourceName The name IllegalStateException("Unable to call ")of the resource to load
    * @param callingClass The Class object of the calling object
     * @return the URL of the specific resource on the 'resources' folder.
    */
    public static URL getResourceAsURL(String resourceName, Class<?> callingClass) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

        if (url == null) {
            url = ClassLoaderUtil.class.getClassLoader().getResource(resourceName);
        }
        if (url == null) {
            ClassLoader cl = callingClass.getClassLoader();

            if (cl != null) {
                url = cl.getResource(resourceName);
            }
        }

        if ((url == null) && (resourceName != null) && ((resourceName.length() == 0) || (resourceName.charAt(0) != '/'))) { 
            return getResourceAsURL('/' + resourceName, callingClass);
        }

        return url;
    }

    /**
    * This is a convenience method to load a resource as a stream.
    *
    * The algorithm used to find the resource is given in getResource()
    *
    * @param resourceName The name of the resource to load
    * @param callingClass The Class object of the calling object
    * @return  the InputStream of the specific resource on the 'resources' folder.
    */
    public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = getResourceAsURL(resourceName, callingClass);
        try {
            return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
    * Load a class with a given name.
    *
    * It will try to load the class in the following order:
    * <ul>
    *  <li>From Thread.currentThread().getContextClassLoader()
    *  <li>Using the basic Class.forName()
    *  <li>From ClassLoaderUtil.class.getClassLoader()
    *  <li>From the callingClass.getClassLoader()
    * </ul>
    *
    * @param className The name of the class to load
    * @param callingClass The Class object of the calling object
    * @return the Class with the name we have specified.
    * @throws ClassNotFoundException If the class cannot be found anywhere.
    */
    public static Class<?> loadClass(String className, Class<?> callingClass) 
            throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                try {
                    return ClassLoaderUtil.class.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }

    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     *
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException if any URI error is occurred.
     * @throws IOException if any IO error is occurred.
     */
    public static String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }
        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL != null && dirURL.getProtocol().equals("jar")) {
        /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }
        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }

    public static InputStream getResourceAsStream(String name) {
        name = resolveName(name);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (cl==null) {
            // A system class.
            return ClassLoader.getSystemResourceAsStream(name);
        }
        return cl.getResourceAsStream(name);
    }

    /**
     * Add a package name prefix if the name is not absolute Remove leading "/"
     * if name is absolute
     */
    private static String resolveName(String name) {
        if (name == null) {
            return null;
        }
        if (!name.startsWith("/")) {
            Class<?> c = ClassLoaderUtil.class;
            while (c.isArray()) {
                c = c.getComponentType();
            }
            String baseName = c.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/')
                        +"/"+name;
            }
        } else {
            name = name.substring(1);
        }
        return name;
    }

    public static void copyFileToResourceDirectory(File node) throws IOException {
        if (!node.getAbsoluteFile().getName().endsWith(".java") && node.getAbsoluteFile().isFile()) {
            logger.info("hg mv "  + node.getAbsoluteFile().toString() + " "
                    + node.getAbsoluteFile().toString().replace("/java/", "/resources/"));
        }
        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                copyFileToResourceDirectory(new File(node, filename));
            }
        }

    }





    /**
     * Aggregates Enumeration instances into one iterator and filters out duplicates.  Always keeps one
     * ahead of the enumerator to protect against returning duplicates.
     */
    static class AggregateIterator<E> implements Iterator<E> {

        LinkedList<Enumeration<E>> enums = new LinkedList<>();
        Enumeration<E> cur = null;
        E next = null;
        Set<E> loaded = new HashSet<>();

        public AggregateIterator<E> addEnumeration(Enumeration<E> e) {
            if (e.hasMoreElements()) {
                if (cur == null) {
                    cur = e;
                    next = e.nextElement();
                    loaded.add(next);
                } else {
                    enums.add(e);
                }
            }
            return this;
        }

        @Override
        public boolean hasNext() {
            return (next != null);
        }

        @Override
        public E next() {
            if (next != null) {
                E prev = next;
                next = loadNext();
                return prev;
            } else {
                throw new NoSuchElementException();
            }
        }

        private Enumeration<E> determineCurrentEnumeration() {
            if (cur != null && !cur.hasMoreElements()) {
                if (enums.size() > 0) {
                    cur = enums.removeLast();
                } else {
                    cur = null;
                }
            }
            return cur;
        }

        private E loadNext() {
            if (determineCurrentEnumeration() != null) {
                E tmp = cur.nextElement();
                int loadedSize = loaded.size();
                while (loaded.contains(tmp)) {
                    tmp = loadNext();
                    if (tmp == null || loaded.size() > loadedSize) {
                        break;
                    }
                }
                if (tmp != null) {
                    loaded.add(tmp);
                }
                return tmp;
            }
            return null;

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
