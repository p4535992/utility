package com.github.p4535992.util.json;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Created by 4535992 on 28/04/2015.
 */
public class GsonKit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GsonKit.class);

    public static JsonObject getData(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try
        {
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());
            Gson gson = new Gson();
            JsonObject jobj = new Gson().fromJson(content, JsonObject.class);
            return jobj;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static String pojoToJson(Object obj){
        return new Gson().toJson(obj);
    }

    public static Object jsonToPojo(String sJson,Class<?> clazz){
        return new Gson().fromJson(sJson, clazz);
    }

//    public static <T> List jsontToListPojo(List<T> arrayOfObjectJSON){
//        Gson gson = new Gson();
//        List list = gson.fromJson(arrayOfObjectJSON, new TypeToken<List<T>>().getType());
//        return list;
//    }




}
