package com.github.p4535992.util.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by 4535992 on 08/07/2015.
 * href:https://github.com/usc-isi-i2/Web-Karma/blob/master/karma-common/src/main/java/edu/isi/karma/util/JSONLDReducerComparator.java
 * @author 4535992.
 * @version 2015-07-08.
 */
@SuppressWarnings("unused")
public class OrgJsonKit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(OrgJsonKit.class);

    public enum JsonKeys {
        name, value, type
    }

//    public static void iterateJSONArray(String sJSON){
//        JSONArray jsonArray = new JSONArray(sJSON);
//        JSONArray jsonPersonData = jsonArray.getJSONArray(1);
//        for (int i=0; i<jsonPersonData.length(); i++) {
//            JSONObject item = jsonPersonData.getJSONObject(i);
//            String name = item.getString("name");
//            String surname = item.getString("surname");
//        }
//    }



    public static void writePrettyPrintedJSONObjectToFile(JSONObject json, File jsonFile)
            throws JSONException, IOException {
        String prettyPrintedJSONString = json.toString(4);
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(prettyPrintedJSONString);
        writer.close();
        logger.info("Done writing JSON Object into a File: " + jsonFile.getAbsolutePath());
    }


    public static JSONObject getJSONObjectWithName(String arg, JSONArray json) throws JSONException {
        for(int i=0; i<json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String nameS = obj.getString(JsonKeys.name.name());
            if(nameS.equals(arg)) {
                return obj;
            }
        }
        return null;
    }

    public static String getStringValue(String arg, JSONArray json) throws JSONException {
        JSONObject obj = getJSONObjectWithName(arg, json);
        if (obj == null)
            return null;
        else
            return obj.getString(JsonKeys.value.name());
    }

    public static JSONArray getJSONArrayValue(String name, JSONArray json) throws JSONException {
        JSONObject obj = getJSONObjectWithName(name, json);
        if (obj == null)
            return null;
        else
            return obj.getJSONArray(JsonKeys.value.name());
    }

    public static String enclose(String x, String delimiter) {
        return delimiter + x + delimiter;
    }

    public static String doubleQuote(String x) {
        return enclose(x, "\"");
    }

    public static String jsonLast(Enum<?> key, String value) {
        return doubleQuote(key.name()) + " : " + JSONObject.quote(value);
    }

    public static String jsonLast(Enum<?> key, int value) {
        return doubleQuote(key.name()) + " : " + value;
    }

    public static String jsonLast(Enum<?> key, boolean value) {
        return doubleQuote(key.name()) + " : " + value;
    }

    public static String json(Enum<?> key, String value) {
        return doubleQuote(key.name()) + " : " + JSONObject.quote(value)
                + " , ";
    }

    public static String json(Enum<?> key, boolean value) {
        return doubleQuote(key.name()) + " : " + value + " , ";
    }

    public static String json(Enum<?> key, int value) {
        return doubleQuote(key.name()) + " : " + value + " , ";
    }

    public static String jsonStartList(Enum<?> key) {
        return doubleQuote(key.name()) + " : [";
    }

    public static String jsonStartObject(Enum<?> key) {
        return doubleQuote(key.name()) + " : ";
    }

    public static String truncateForHeader(String x, int maxChars) {
        if (x.length() > maxChars) {
            if (maxChars > 5) {
                String prefix = x.substring(0, maxChars - 5);
                String suffix = x.substring(x.length() - 3, x.length());
                return prefix + ".." + suffix;
            } else {
                return x.substring(0, maxChars - 2) + "..";
            }
        } else {
            return x;
        }
    }

    public static String truncateCellValue(String x, int maxChars) {
        if (x.length() > maxChars) {
            return x.substring(0, Math.max(3, maxChars)) + " ...";
        } else {
            return x;
        }
    }

    public static String readerToString(Reader reader) {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader bufferedReader = new BufferedReader(reader);
        char[] buf = new char[1024];
        int numRead;
        try {
            while ((numRead = bufferedReader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            bufferedReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(),e);
        }
        return fileData.toString();
    }

    public static JSONArray createJSONArray(JSONTokener tokener) {
        JSONArray result = null;
        try {
            result = new JSONArray(tokener);
        } catch (JSONException e1) {
            // Don't do anything.
        }
        return result;
    }

    public static JSONObject createJSONObject(JSONTokener tokener) {
        JSONObject result = null;
        try {
            result = new JSONObject(tokener);
        } catch (JSONException ignored) {
            // Don't do anything.
        }
        return result;
    }

    public static Object createJson(String jsonString) {
        Object result = createJSONObject(new JSONTokener(jsonString));
        if (result == null) {
            result = createJSONArray(new JSONTokener(jsonString));
        }
        if (result == null) {
            logger.warn("Could not parse as JSONObject or JSONArray");
            return null;
        }
        return result;
    }

    public static Object createJson(Reader reader) {
        // This is an ugly, and surely inefficient solution, but I can't figure
        // out a way around it.
        String x = readerToString(reader);
        return createJson(x);
    }

    public static String prettyPrintJson(String jsonString) {
        try {
            Object o = createJson(jsonString);
            if (o instanceof JSONObject) {
                return ((JSONObject) o).toString(4);
            } else if (o instanceof JSONArray) {
                return ((JSONArray) o).toString(4);
            } else {
                logger.warn("Is not a JSON");
                return "not JSON";
            }
        } catch (JSONException e) {
            logger.error("Is not a JSON:"+e.getMessage(),e);
            return "not JSON";
        }
    }

    public static boolean compareJSONObjects(Object obj1, Object obj2) throws JSONException {
        if(obj1 instanceof JSONArray && obj2 instanceof JSONArray) {
            JSONArray a1 = (JSONArray) obj1;
            JSONArray a2 = (JSONArray) obj2;

            if(a1.length() != a2.length())
                return false;

            for (int i=0; i<a1.length(); i++) {
                Object a = a1.get(i);
                Object b = a2.get(i);

                if(!compareJSONObjects(a, b))
                    return false;
            }

        } else if (obj1 instanceof JSONObject && obj2 instanceof JSONObject) {
            JSONObject a1 = (JSONObject) obj1;
            JSONObject a2 = (JSONObject) obj2;

            if(a1.length() != a2.length()) {
                //logger.warn("Could not compare the JSONObjects:" + a1.length() + "!=" + a2.length());
                return false;
            }

            @SuppressWarnings("rawtypes")
            Iterator keys = a1.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object val1 = a1.get(key);
                Object val2;
                try {
                    val2 = a2.get(key);
                } catch (JSONException e) {
                    //logger.warn(e.getMessage(), e);
                    return false;
                }
                if (!compareJSONObjects(val1, val2)) {
                    //logger.warn("Could not compare the JSONObjects:"+val1.toString()+","+val2.toString());
                    return false;
                }
            }

        } else if (obj1 instanceof String && obj2 instanceof String) {
            return obj1.toString().equals(obj2.toString());
        } else if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return Objects.equals(obj1, obj2);
        } else if (obj1 instanceof Double && obj2 instanceof Double) {
            return Objects.equals(obj1, obj2);
        } else if (obj1 instanceof Long && obj2 instanceof Long) {
            return Objects.equals(obj1, obj2);
        } else if (obj1 instanceof Boolean && obj2 instanceof Boolean) {
            return Objects.equals(obj1, obj2);
        } else return obj1 == JSONObject.NULL && obj2 == JSONObject.NULL;

        return true;
    }

    public static void writeJsonFile(Object o, String name) {
        try {
            FileWriter outFile = new FileWriter(name);
            PrintWriter pw = new PrintWriter(outFile);
            if (o instanceof JSONObject) {
                JSONObject x = (JSONObject) o;
                pw.println(x.toString(2));
            } else if (o instanceof JSONArray) {
                JSONArray x = (JSONArray) o;
                pw.println(x.toString(2));
            }
            outFile.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(),e);
        }

    }

    //-------------------------------------------------------------------
    //JSONLD UTILS
    //-------------------------------------------------------------------
    private static JSONLDReducerComparator comparator;

    static {
        comparator =  new JSONLDReducerComparator();
    }

    public static JSONObject mergeJSONObjects(Iterator<String> iterator) {

        JSONObject accumulatorObject = new JSONObject();

        while(iterator.hasNext())
        {

            String value = iterator.next();
            JSONObject object = new JSONObject(value);
            accumulatorObject = mergeJSONObjects(accumulatorObject, object);
        }

        return accumulatorObject;
    }

    public static JSONObject mergeJSONObjects(JSONObject left, JSONObject right)
    {
        String[] names = JSONObject.getNames(right);
        for(String name : names)
        {
            if(!left.has(name))
            {
                left.put(name, right.get(name));
            }
            else
            {
                Object leftObject = left.get(name);
                Object rightObject = right.get(name);
                if(leftObject instanceof JSONArray)
                {
                    if(rightObject instanceof JSONArray)
                    {
                        mergeArrays(left, name, (JSONArray) leftObject, (JSONArray) rightObject);
                    }
                    else
                    {
                        JSONArray newRightArray = new JSONArray();
                        newRightArray.put(rightObject);
                        mergeArrays(left, name, (JSONArray) leftObject, newRightArray);
                    }

                }
                else
                {
                    if(rightObject instanceof JSONArray)
                    {
                        JSONArray newLeftArray = new JSONArray();
                        newLeftArray.put(leftObject);
                        mergeArrays(left, name, newLeftArray, (JSONArray)rightObject);
                    }
                    else
                    {
                        JSONArray newLeftArray = new JSONArray();
                        JSONArray newRightArray = new JSONArray();
                        newLeftArray.put(leftObject);
                        newRightArray.put(rightObject);
                        mergeArrays(left, name, newLeftArray, newRightArray);
                    }
                }
            }
        }
        return left;
    }
    protected static void mergeArrays(JSONObject left, String name,
                                      JSONArray leftArray, JSONArray rightArray) {
        JSONArray newArray = new JSONArray();
        int leftIndex = 0;
        int rightIndex = 0;
        while(leftIndex < leftArray.length() && rightIndex < rightArray.length() )
        {
            int result = comparator.compare(leftArray.get(leftIndex),rightArray.get(rightIndex));
            if(result < 0)
            {
                newArray.put(leftArray.get(leftIndex++));
            }
            else if (result == 0)
            {
                Object tempLeft = leftArray.get(leftIndex++);
                Object tempRight = rightArray.get(rightIndex++);
                Object mergedResult = mergeStringsAndJSONObjects(
                        tempLeft, tempRight);
                newArray.put(mergedResult);
            }
            else
            {
                newArray.put(rightArray.get(rightIndex++));
            }
        }
        while(leftIndex < leftArray.length())
        {
            newArray.put(leftArray.get(leftIndex++));
        }
        while(rightIndex < rightArray.length())
        {
            newArray.put(rightArray.get(rightIndex++));
        }
        if(newArray.length() > 1)
        {
            left.put(name, newArray);
        }
        else if(newArray.length() == 1)
        {
            left.put(name, newArray.get(0));
        }
    }
    private static Object mergeStringsAndJSONObjects(Object tempLeft,
                                                     Object tempRight) {
        Object mergedResult;
        if(tempLeft instanceof String && tempRight instanceof String)
        {
            mergedResult = tempLeft;
        }
        else if(tempLeft instanceof JSONObject && tempRight instanceof String)
        {
            mergedResult = tempLeft;
        }
        else if(tempLeft instanceof String && tempRight instanceof JSONObject)
        {
            mergedResult = tempRight;
        }
        else if(tempLeft instanceof JSONObject && tempRight instanceof JSONObject)
        {
            mergedResult = mergeJSONObjects((JSONObject)tempLeft, (JSONObject)tempRight);
        }
        else {
            if (tempLeft instanceof String) {
                mergedResult = tempRight.toString();
            }
            else {
                mergedResult = tempLeft.toString();
            }
        }
        return mergedResult;
    }

    static class JSONLDReducerComparator implements Comparator<Object>
    {

        public JSONLDReducerComparator()
        {

        }
        @Override
        public int compare(Object o1, Object o2) {
            if(o1 instanceof String && o2 instanceof String)
            {
                return ((String)o1).compareToIgnoreCase((String)o2);
            }
            else if(o1 instanceof JSONObject && o2 instanceof String)
            {
                JSONObject t = (JSONObject)o1;
                if (t.has("uri")) {
                    return t.getString("uri").compareToIgnoreCase((String)o2);
                }
                else if (t.has("@id")) {
                    return t.getString("@id").compareToIgnoreCase((String)o2);
                }

                else {
                    return t.toString().compareToIgnoreCase((String)o2);
                }
            }
            else if(o1 instanceof String && o2 instanceof JSONObject)
            {
                JSONObject t2 = (JSONObject)o2;
                if (t2.has("uri")) {
                    return (((String)o1).compareToIgnoreCase(t2.getString("uri")));
                }
                else if (t2.has("@id")) {
                    return (((String)o1).compareToIgnoreCase(t2.getString("@id")));
                }
                else {
                    return o1.toString().compareToIgnoreCase(t2.toString());
                }
            }
            else if(o1 instanceof JSONObject && o2 instanceof JSONObject)
            {
                JSONObject t1 = (JSONObject)o1;
                JSONObject t2 = (JSONObject)o2;
                if (t1.has("uri") && t2.has("uri")) {
                    return t1.getString("uri").compareTo(t2.getString("uri"));
                }
                else if (t1.has("@id") && t2.has("@id")) {
                    return t1.getString("@id").compareTo(t2.getString("@id"));
                }
                else {
                    return t1.toString().compareToIgnoreCase(t2.toString());
                }
            }
            return 0;
        }

    }

}
