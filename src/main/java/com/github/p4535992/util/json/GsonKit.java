package com.github.p4535992.util.json;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Created by 4535992 on 28/04/2015.
 */
public class GsonKit {

    public static JsonObject getData(String url)
    {
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
        Gson gson = new Gson();
        String sJson = gson.toJson(obj);
        return sJson;
    }

    public static Object jsonToPojo(String sJson,Class<?> clazz){
        Gson gson = new Gson();
        Object obj = new Gson().fromJson(sJson, clazz);
        return obj;
    }

//    public static <T> List jsontToListPojo(List<T> arrayOfObjectJSON){
//        Gson gson = new Gson();
//        List list = gson.fromJson(arrayOfObjectJSON, new TypeToken<List<T>>().getType());
//        return list;
//    }




}
