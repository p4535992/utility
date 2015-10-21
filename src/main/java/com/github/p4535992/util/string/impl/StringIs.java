package com.github.p4535992.util.string.impl;

import com.github.p4535992.util.string.pattern.Patterns;

/**
 * Created by 4535992 on 21/10/2015.
 */
public class StringIs {

    /**
     * Uses androids android.util.Patterns.EMAIL_ADDRESS to check if an email address is valid.
     *
     * @param email Address to check
     * @return true if the <code>email</code> is a valid email address.
     */
    public static boolean isEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Uses androids android.telephony.PhoneNumberUtils to check if an phone number is valid.
     *
     * @param number Phone number to check
     * @return true if the <code>number</code> is a valid phone number.
     */
    public static boolean isPhoneNumber(String number) {
        return number != null && Patterns.PHONE.matcher(number).matches();
        //return Patterns.isGlobalPhoneNumber(number);
    }

    /**
     * Uses androids android.util.Patterns.WEB_URL to check if an url is valid.
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
    public  static boolean isValidURL(String url) {
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * Method to  check if an url is valid.
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
    public  static boolean isURLSimple(String url) {
        return url != null && url.matches("^(https?|ftp)://.*$");
    }

    /**
     * Method to check if an url has the valid protocol.
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
    public static boolean isURLWithProtocol(String url){
        return isValidURL(url) && Patterns.Protocol_URL.matcher(url).matches();
    }

    /**
     * Method to check if an url has the valid protocol.
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
    public static boolean isURLWithoutProtocol(String url){
        return Patterns.WEB_URL_NO_PROTOCOL.matcher(url).matches() &&
                !Patterns.Protocol_URL.matcher(url).matches();
    }

    /**
     * Method to check if a s tring is a url address web or not.
     * @param url the string address web.
     * @return if tru is a url address web.
     */
    public static boolean isURL(String url){
        return isValidURL(url);
    }

    /**
     * Method to check if a s tring is a url address web or not.
     * @param url the string address web.
     * @return if tru is a url address web.
     */
    /*public static boolean isURLWithProtocol(String url){
        return url.matches("^(https?|ftp)://.*$");
    }*/

    /**
     * Method for check if a string rappresent a numeric value.
     * @param str string rapresentative of a number.
     * @return booleanvalue if the string rappresent a number or not.
     */
    public static boolean isNumeric(String str) {
        //match a number with optional '-' and decimal.
        str = str.replace(",",".").replace(" ",".");
        return str.matches("(\\-|\\+)?\\d+(\\.\\d+)?");
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    public static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty();
    }
}


