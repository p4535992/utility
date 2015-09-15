package com.github.p4535992.util.string;

import org.joda.time.LocalDateTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by 4535992 on 16/07/2015.
 * @author 4535992.
 * @version 2015-07-16.
 */
@SuppressWarnings("unused")
public class DateAndTimeKit {

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

}
