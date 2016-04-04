package com.github.p4535992.util.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 4535992 on 02/06/2015.
 */
public class JacksonKit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JacksonKit.class);

    /**
     * Java program to convert JSON String into Java object using Jackson library.
     * Jackson is very easy to use and require just two lines of code to create a Java object
     * from JSON String format.
     * href: http://javarevisited.blogspot.com/2013/02/how-to-convert-json-string-to-java-object-jackson-example-tutorial.html#ixzz3tvraaTP7
     * maven: dependency: org.codehaus.jackson.jackson-xc, org.codehaus.jackson.jackson-mapper-asl,
     * org.codehaus.jackson.jackson-core-asl
     * @param <T> the generic variable.
     * @param json the JSON object to parse.
     * @param classToMap the Class of the Object to map.
     * @return the Object of the class.
     */
    public <T> T fromJson(String json,Class<T> classToMap){
        try {
            logger.info("Java Object created from JSON String ");
            logger.info("JSON String : " + json);
            logger.info("Java Object : " + classToMap.getName());
            return new ObjectMapper().readValue(json, classToMap);
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public <T> T fromJson(File jsonFile,Class<T> classToMap){
        try {
            logger.info("Java Object created from JSON String ");
            logger.info("JSON File : " + jsonFile.getAbsolutePath());
            logger.info("Java Object : " + classToMap.getName());
            return new ObjectMapper().readValue(jsonFile, classToMap);
        }catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    //http://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
    public static <T> T[] fromJsonArray(String jsonArray,T classToMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonArray,new TypeReference<T[]>(){});
    }

    public static <T> List<T> fromJsonList(String jsonArray, Class<T> classToMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonArray, new TypeReference<List<T>>(){});
    }

    public static String toJson(Object javaObject) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        //Convert object to JSOn String
        return mapper.writeValueAsString(javaObject);
        //Convert object to JSOn String with pretty format.
        //return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(javaObject);
    }

    public static  String toJsonPretty(Object javaObject) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        //Convert object to JSOn String
        return mapper.writeValueAsString(javaObject);
        //Convert object to JSOn String with pretty format.
        //return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(javaObject);
    }

    public static File toJsonPretty(Object javaObject,File jsonFile) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        //Convert object to JSOn String and save to filoe directly
        mapper.writeValue(jsonFile,javaObject);
        return jsonFile;
    }


    public  <T> String writeListToJsonArray(List<T> listObjects) throws IOException {
        /*final List<T> list = new ArrayList<Event>(2);
        list.add(new Event("a1","a2"));
        list.add(new Event("b1","b2"));*/
        //final OutputStream out = new ByteArrayOutputStream();
        //final StringWriter sw =new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();
        //mapper.writeValue(sw, listObjects);
        //final byte[] data = out.toByteArray();
        //return new String(data);
        //return sw.toString();
        return mapper.writeValueAsString(listObjects);
    }




    //Deprecated

    private String pojo2Json(Object obj) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);

    }
    private Object json2Pojo(String jsonString,Class<?> clazz) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString,clazz);
    }


}
