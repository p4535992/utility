package com.github.p4535992.util.file.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * eXist-specific Crawler
 * extracts the paths of Documents in a http manner
 *
 * @author marco juergens
 *
 */
@SuppressWarnings("unused")
public class PathExtractor {
    
     private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(PathExtractor.class);
    
    
    private List<String> ressourceLoc;
    private String excludes;

    public PathExtractor() {

    }

    public List<String> initExtractor(String startingUri, String excludes) {
        this.excludes = excludes;
        ressourceLoc = new ArrayList<>();
        // parameter necessary, because it's recursive, thus changing the uri
        extractDocLocations(startingUri);
        return this.ressourceLoc;
    }

    /**
     * recursive Method to extract the path of the resources
     *
     * @param startUrl the url where invoke the GET request.
     */
    private void extractDocLocations(String startUrl) {

        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpGet httpget = new HttpGet(startUrl);
        HttpResponse resp = null;
        try {
            resp = client.execute(httpget);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        HttpEntity entity = null;
        if (resp != null) {
            entity = resp.getEntity();
        }
        if (entity != null) {
            XMLInputFactory iFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = null;
            try {
                reader = iFactory.createXMLStreamReader(entity.getContent());
            } catch (IllegalStateException | XMLStreamException | IOException e1) {
                logger.error(e1.getMessage(),e1);
            }
            try {
                while (true) {
                    int event = 0;
                    if (reader != null) {
                        event = reader.next();
                    }
                    if (event == XMLStreamConstants.END_DOCUMENT) {
                        reader.close();
                        break;
                    }
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        String nameAttributeValue = reader.getAttributeValue(null, "name");
                        if ((nameAttributeValue) != null) {
                            if (reader.getLocalName().equals("collection") && !(startUrl.endsWith(nameAttributeValue))) {
                                if (! isNameExcluded(nameAttributeValue.toLowerCase())) {
                                    if (nameAttributeValue.startsWith("/")) {
                                        client.close();
                                        //client.getConnectionManager().closeExpiredConnections();
                                        extractDocLocations(startUrl + nameAttributeValue);
                                    } else {
                                        client.close();
                                        //client.getConnectionManager().closeExpiredConnections();
                                        if (! startUrl.endsWith("/")) {
                                            extractDocLocations(startUrl + "/" + nameAttributeValue);
                                        } else {
                                            extractDocLocations(startUrl + nameAttributeValue);
                                        }
                                    }
                                }
                            }
                            if (reader.getLocalName().equals("resource")) {
                                String url = startUrl + "/" + nameAttributeValue;
                                if (startUrl.endsWith("/"))
                                    url = startUrl + nameAttributeValue;
                                boolean startUrlIsExcluded = isExcluded(url); // if exclude contains a full file name e.g. verzeichnisse/personenkorrektur.xml
                                if (! startUrlIsExcluded) {
                                    ressourceLoc.add(url);
                                }
                            }
                        }
                    }
                   /* if (event == XMLStreamConstants.ATTRIBUTE) {
                        // System.out.println("localName : "+reader.getLocalName());
                    }*/
                }
            } catch (XMLStreamException|IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

    private boolean isExcluded(String url) {
        if (excludes != null && url != null) {
            String[] exludeArrayStr = excludes.split(" ");
            for (String exclude : exludeArrayStr) {
                if (url.endsWith(exclude))
                    return true;
            }
        }
        return false;
    }

    private boolean isNameExcluded(String name) {
        if (excludes != null && name != null) {
            String[] exludeArrayStr = excludes.split(" ");
            for (String exclude : exludeArrayStr) {
                if (name.equals(exclude))
                    return true;
            }
        }
        return false;
    }

    /**
     * extrahiert ebenso wie extractDocLocations(String startUri) Pfade, tut dies
     * aber local und nicht über HTTP
     * @param startUrl the {@link String} url of the location directory to inspect.
     *
     * @return the List of String
     */
    public List<String> extractPathLocally(String startUrl) {
        List<String> pathList = new ArrayList<>();
        // home verzeichnis pfad über system variable
        // String loc = System.getenv("HOME")+"/wsp/configs";
        // out.println("hom variable + conf datei : "+loc);
        File f = new File(startUrl);
        // out.println("readable : "+Boolean.toString(f.canRead()));
        // out.println("readable : "+f.isDirectory());
        if (f.isDirectory()) {
            File[] filelist = f.listFiles();
            if (filelist != null) {
                for (File file : filelist) {
                    if (file.getName().toLowerCase().contains("config")) {
                        if (!startUrl.endsWith("/")) {
                            pathList.add(startUrl + "/" + file.getName());
                        } else {
                            pathList.add(startUrl + file.getName());
                        }
                    }
                }
            }
        }
        return pathList;
    }
}