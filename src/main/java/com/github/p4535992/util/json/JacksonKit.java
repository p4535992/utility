package com.github.p4535992.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.bind.JAXBException;
import java.io.IOException;


/**
 * Created by Marco on 02/06/2015.
 */
public class JacksonKit {

    private String pojo2Json(Object obj) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(obj);
        return jsonString;
    }
    private Object json2Pojo(String jsonString,Class<?> clazz) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object empBeanList = objectMapper.readValue(jsonString,clazz);
        Object object = empBeanList;
        return object;
    }


}
