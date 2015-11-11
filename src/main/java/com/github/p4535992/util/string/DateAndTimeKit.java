package com.github.p4535992.util.string;

import com.github.p4535992.util.log.SystemLog;
import org.joda.time.LocalDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by 4535992 on 16/07/2015.
 * @author 4535992.
 * @version 2015-07-16.
 */
@SuppressWarnings("unused")
public class DateAndTimeKit {

    public static SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
    public static SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Method to get the current GMT time for user notification.
     * @return timestamp value as string.
     */
    public static String getDateGMTime() {
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        return gmtDateFormat.format(new java.util.Date());
    }

    /**
     * Method to get the current org.joda.time.LocalDateTime.
     * @return the org.joda.time.LocalDateTime.
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
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
    public static GregorianCalendar convertTimestampToGregorianCalendar(Timestamp timestamp) {
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar;
    }

    /**
     * Method to convert Timestamp to GregorianCalendar.
     * @param gregorianCalendar the ava.util.GregorianCalendarj.
     * @return the ava.sql.Timestamp.
     */
    public static Timestamp convertGregorianCalendarToTimestamp(GregorianCalendar gregorianCalendar) {
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
    public static String convertDateToIsoDate(Date date) {
        return isoDate.format(date);
    }

    /**
     * Method to convert a string date to a  ISO Date.
     * e.g. 2003-10-29T10:05:35-05:00.
     * @param string sting of a date eg 2003-10-29.
     * @return sring of a date in iso date format.
     */
    public static Date convertStringDateToIsoDate(String string) {
        Date date = null;
        string =string.substring(0, 19)+ "GMT"+ string.substring(19);
        try {
            date = isoDate.parse(string);
        } catch (ParseException e) {
            SystemLog.exception(e);
        }
        return date;
    }

    /**
     * Method to convert a date to a ISO date.
     * @param date date to convert.
     * @return the dat in format iso.
     */
    /*
    public static String convertDateToIsoDate(Date date) {
        return isoDate.format(date);
    }
    */
    /**
     * Method to convert a string date to a  ISO Date.
     * e.g. 2003-10-29T10:05:35-05:00.
     * @param string sting of a date eg 2003-10-29.
     * @return sring of a date in iso date format.
     */
    /*
    public static Date convertStringDateToIsoDate(String string) {
        Date date = null;
        string =string.substring(0, 19)+ "GMT"+ string.substring(19);
        try {
            date = isoDate.parse(string);
        } catch (ParseException e) {
           SystemLog.exception(e);
        }
        return date;
    }*/

}
