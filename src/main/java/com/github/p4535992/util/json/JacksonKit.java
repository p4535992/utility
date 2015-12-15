package com.github.p4535992.util.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.bind.JAXBException;
import java.io.IOException;


/**
 * Created by Marco on 02/06/2015.
 */
public class JacksonKit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JacksonKit.class);

    /**
     * Java program to convert JSON String into Java object using Jackson library.
     * Jackson is very easy to use and require just two lines of code to create a Java object
     * from JSON String format.
     *   @author http://javarevisited.blogspot.com
    Read more:
    http://javarevisited.blogspot.com/2013/
    02/how-to-convert-json-string-to-java-object-jackson-example-tutorial.html#ixzz3tvraaTP7

    maven: dependency: org.codehaus.jackson.jackson-xc, org.codehaus.jackson.jackson-mapper-asl,
    org.codehaus.jackson.jackson-core-asl
     */
    public <T> T fromJson(String json,Class<T> classToMap) throws IOException {
        /* logger.info("Java Object created from JSON String ");
        logger.info("JSON String : " + json);
        logger.info("Java Object : " + garima);*/
        return new ObjectMapper().readValue(json, classToMap);
    }

    private String pojo2Json(Object obj) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    private Object json2Pojo(String jsonString,Class<?> clazz) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString,clazz);
    }


}
