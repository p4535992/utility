package com.github.p4535992.util.string.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 21/10/2015.
 * @author 4535992.
 * @version 2015-10-21.
 */
public class StringRegex {

    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    public static List<String> findWithRegex(String text,String expression,boolean justFirstResult){
        List<String> result =new ArrayList<>();
        Pattern pattern = Pattern.compile(expression);
        return findWithRegex(text,pattern,justFirstResult);
    }

    /**
     * Method to find all the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern of regular expression.
     * @param justFirstResult if true get only the first element.
     * @return a list of string of all matches.
     */
    public static List<String> findWithRegex(String text,Pattern pattern,boolean justFirstResult){
        List<String> result =new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            result.add(matcher.group());
            if(justFirstResult)break;

        }
        return result;
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return the first match on the text string.
     */
    public static String findWithRegex(String text,String expression){
        Pattern pat = Pattern.compile(expression);
        return findWithRegex(text,pat);
    }

    /**
     * Method to find the string matches of the expression with regular expression.
     * @param text string text to check.
     * @param pattern pattern regular expression.
     * @return the first match on the text string.
     */
    public static String findWithRegex(String text,Pattern pattern){
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group(0);
        }
        return "?";
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param expression string regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    public static boolean isMatch(String text,String expression){
        return text != null && Pattern.compile(expression).matcher(text).matches();
    }

    /**
     * Method to check if  a string contain some match for the regular expression.
     * @param text string text to check.
     * @param pattern the pattern of the regular expression.
     * @return if true the string contains a match for the regular expression.
     */
    public static boolean isMatch(String text,Pattern pattern){
        return text != null && pattern.matcher(text).matches();
    }

    /**
     * Metodo che matcha e sostituisce determinati parti di una stringa attraverso le regular expression.
     * @param input stringa di input.
     * @param expression regular expression da applicare.
     * @param replace setta la stringa con cui sostituire il risultato del match.
     * @return il risultato in formato stringa della regular expression.
     */
    public static String regexAndReplace(String input,String expression,String replace){
        String result ="";
        if(replace==null){
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(input);
            while(matcher.find()){
                result = matcher.group();
                if(!StringIs.isNullOrEmpty(result)){break;}
            }
            return result;
        }else{
            return input.replaceAll(expression, replace);
        }
    }
}
