package com.github.p4535992.util.string;

import com.github.p4535992.util.string.impl.StringIs;
import com.github.p4535992.util.string.impl.StringKit;
import com.github.p4535992.util.string.impl.StringRegex;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 21/10/2015.
 * @author 4535992.
 * @version 2015-10-21.
 */
@SuppressWarnings("unused")
public class StringUtil {


    public static boolean isEmail(String email) {return StringIs.isEmail(email);}

    public static boolean isPhoneNumber(String number) { return StringIs.isPhoneNumber(number);}

    public  static boolean isValidURL(String url) {
        return StringIs.isValidURL(url);
    }

    public  static boolean isURLSimple(String url) {return StringIs.isURLSimple(url);}

    public static boolean isURLWithProtocol(String url){return StringIs.isURLWithProtocol(url);}

    public static boolean isURLWithoutProtocol(String url){return StringIs.isURLWithoutProtocol(url);}

    public static boolean isURL(String url){
        return StringIs.isURL(url);
    }

    public static boolean isNumeric(String str) { return StringIs.isNumeric(str);}

    public static boolean isInt(String str) { return StringIs.isInt(str);}

    public static boolean isDouble(String str) { return StringIs.isDouble(str);}

    public static boolean isFloat(String str) { return StringIs.isFloat(str);}

    public static boolean isDecimal(String str) { return StringIs.isDecimal(str);}

    public static boolean isNullOrEmpty(String text) { return StringIs.isNullOrEmpty(text);}

    public static String cleanHTML(String stringHtml){return StringKit.cleanStringHTML(stringHtml);}

    public static String toInline(String stringText){return StringKit.toStringInline(stringText);}

    public static String collapseSpaces(String argStr) {return StringKit.collapseSpaces(argStr);}

    public static String toString(InputStream is, Charset encoding) {
        return StringKit.convertInputStreamToStringWithEncoding(is, encoding);
    }

    //public static String toString(InputStream is){ return  StringKit.convertInputStreamToString(is);}

    public static InputStream toInputStream(String string) { return StringKit.convertStringToInputStream(string);}

    public static String toASCII(String UTF8) throws IOException{
        return StringKit.convertStringUTF8ToStringASCII(UTF8);
    }

    public static String toUTF8(String ASCII) throws IOException {
        return StringKit.convertStringASCIIToStringUTF8(ASCII);
    }

    public static String toString(InputStream in) throws IOException {return StringKit.convertStreamToString(in);}

    public static String createUUID(){ return StringKit.randomUUID(); }

    public static java.util.UUID toUUID(String uuid){return StringKit.convertStringToUUID(uuid); }

    public static String removeFirstAndLast(String stringToUpdate, String symbol) {
        return StringKit.removeFirstAndLast(stringToUpdate, symbol);
    }

    public static String toNull(String s){return StringKit.setNullForEmptyString(s);}

    public static String toFirstToken(String content,String symbol){
      return StringKit.getTheFirstTokenOfATokenizer(content, symbol);
    }

    public File toFile(String content, File outputPathFileName) {
      return StringKit.copyStringToFile(content, outputPathFileName);
    }

    public String fromConsole() throws IOException { return StringKit.readConsole();}

    public static String fromFile(File filename) {return StringKit.readFileWithStringBuilder(filename);}

    public static int[] count(String text) throws IOException {return StringKit.countElementOfAString(text);}

    public static <T> void toSerializable(T object,String nameTempSer){
         StringKit.convertObjectToSerializable(object,nameTempSer);
    }

    public static <T> T toObject(T object,String nameTempSer){
        return StringKit.convertSerializableToObject(object, nameTempSer);
    }

    public static <T> T toObject(Object objectToCast,Class<T> clazz) {
        return StringKit.convertInstanceOfObject(objectToCast, clazz);
    }

    public static <T> String toXml(T object, Class<T> clazz)throws JAXBException {
       return StringKit.convertPojoToXml(object, clazz);
    }

    public static <T> T toPojo(String xmlStringData, Class<T> clazz)  throws JAXBException {
        return StringKit.convertXmlToPojo(xmlStringData, clazz);
    }

    public static char[] toChar(String string){return StringKit.convertStringToChar(string);}

    public static String toURLWithProtocol(String url) {return StringKit.convertStringURLToStringURLWithProtocol(url);}

    public static String toString(byte[] arrayBytes){return StringKit.convertByteArrayToString(arrayBytes);}

    public static String toHexString(byte[] arrayBytes){return StringKit.convertByteArrayToHexString(arrayBytes);}

    public static String toStringWithSpring(byte[] arrayBytes){return StringKit.convertByteArrayToStringWithSpring(arrayBytes);}

    public static String toMD5(String message) {
        return StringKit.convertStringToMD5(message);
    }

    public static String toSHA1(String message) {
        return StringKit.convertStringToSHA1(message);
    }

    public static String toSHA256(String message) {return StringKit.convertStringToSHA256(message);}

    public static int toInt(Integer integer){return StringKit.convertIntegerToInt(integer);}

    public static Integer toInteger(int numInt){return StringKit.convertIntToInteger(numInt);}

    public static Double toDouble(int numInt){ return StringKit.convertIntToDouble(numInt);}

    public static Double toDouble(Integer numInt){ return StringKit.convertIntegerToDouble(numInt);}

    public static Integer toInteger(String numericText){return StringKit.convertStringToInteger(numericText);}

    public static int toInt(String numericText){ return StringKit.convertStringToInt(numericText);}

    public static String toString(Object object){return StringKit.convertObjectToString(object);}

    public static int toInt(Object object){
        return StringKit.convertObjectToInt(object);
    }

    public static List<String> find(String text,String expression,boolean justFirstResult){
       return StringRegex.findWithRegex(text,expression,justFirstResult);
    }

    public static List<String> find(String text,Pattern pattern,boolean justFirstResult){
      return StringRegex.findWithRegex(text,pattern,justFirstResult);
    }

    public static String find(String text,String expression){
        return StringRegex.findWithRegex(text,expression);
    }

    public static String find(String text,Pattern pattern){
        return StringRegex.findWithRegex(text, pattern);
    }


    public static boolean isMatch(String text,String expression){
        return StringRegex.isMatch(text, expression);
    }


    public static boolean isMatch(String text,Pattern pattern){
        return StringRegex.isMatch(text, pattern);
    }


    public static String findAndReplace(String input,String expression,String replace){
        return StringRegex.regexAndReplace(input,expression,replace);
    }

}
