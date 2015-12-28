package com.github.p4535992.util.string;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 4535992 on 16/07/2015.
 * @author 4535992.
 * @version 2015-12-15.
 */
@SuppressWarnings("unused")
public class DateUtilities {
    
    private static final org.slf4j.Logger logger = 
            org.slf4j.LoggerFactory.getLogger(DateUtilities.class);

    /**
     * Method to get the current GMT time for user notification.
     * @return timestamp value as string.
     */
    public static String getDateGMTime() {
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        return gmtDateFormat.format(new java.util.Date());
    }

    /**
     * Method to get the current org.joda.time.LocalDateTime.
     * @return the org.joda.time.LocalDateTime.
     */
    public static org.joda.time.LocalDateTime getCurrentJodaDateTime() {
        return org.joda.time.LocalDateTime.now();
    }

    /**
     * Method to get the current java.sql.Timestamp.
     * @return the java.sql.Timestamp.
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Method to convert Timestamp to GregorianCalendar.
     * @param timestamp the java.sql.Timestamp.
     * @return the java.util.GregorianCalendar.
     */
    public static GregorianCalendar toGregorianCalendar(Timestamp timestamp) {
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar;
    }

    /**
     * Method to convert Timestamp to GregorianCalendar.
     * @param gregorianCalendar the ava.util.GregorianCalendarj.
     * @return the ava.sql.Timestamp.
     */
    public static Timestamp toTimestamp(GregorianCalendar gregorianCalendar) {
        return new Timestamp(gregorianCalendar.getTime().getTime());
    }

    /**
     * Method to get the current timestamp.
     * @return timestamp object.
     */
    public static java.sql.Timestamp getCurrentDayTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }

    /**
     * Method to get the current date.
     * @return date object.
     */
    public static java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }

    /**
     * Method to convert a date to a ISO date.
     * @param date date to convert.
     * @return the dat in format iso.
     */
    public static String toIsoDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(date);
    }

    /**
     * Method to convert a string date to a  ISO Date.
     * e.g. 2003-10-29T10:05:35-05:00.
     * @param string sting of a date eg 2003-10-29.
     * @return sring of a date in iso date format.
     */
    public static Date toIsoDate(String string) {
        Date date = null;
        string =string.substring(0, 19)+ "GMT"+ string.substring(19);
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").parse(string);
        } catch (ParseException e) {
            logger.error(e.getMessage(),e);
        }
        return date;
    }

    /**
     * Method to print the RFC2822 Date.
     * href: https://github.com/azeckoski/reflectutils/blob/master/src/main/java/org/azeckoski/reflectutils/DateUtils.java
     * @param date the Date Object to modify.
     * @return the String of the Date modified.
     */
    public static String getDateRFC2822(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
        return df.format(date);
    }

    /**
     * Method to print the RFC2822 Date.
     * href: https://github.com/azeckoski/reflectutils/blob/master/src/main/java/org/azeckoski/reflectutils/DateUtils.java
     * @param date the Date Object to modify.
     * @return the String of the Date modified.
     */
    public static String getDateISO8601(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String result = df.format(date);
        // convert YYYYMMDDTHH:mm:ss+HH00 into YYYYMMDDTHH:mm:ss+HH:00
        result = result.substring(0, result.length() - 2) + ":" + result.substring(result.length() - 2);
        return result;
    }
   

}
