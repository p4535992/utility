package com.github.p4535992.util.calendar;

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

    public static final String FORMAT_YYYYMMDD = "yyyy-MM-dd";
    public static final String FORMAT_YYYYMMDD_SLASHES = "yyyy/MM/dd";
    public static final String GENERIC_DISPLAY_FORMAT = "E, dd MMM yyyy";
    public static final String TIME_DISPLAY_FORMAT = "HH mm ss";
    public static final int LAST_WEEK = 1;
    public static final int LAST_MONTH = 2;

    // List of all date formats that we want to parse.
    // Add your own format here.
    /*
    private static final List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {
        {
            add(new SimpleDateFormat("M/dd/yyyy"));
            add(new SimpleDateFormat("dd.M.yyyy"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
            add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
            add(new SimpleDateFormat("dd.MMM.yyyy"));
            add(new SimpleDateFormat("dd-MMM-yyyy"));
        }
    };
    */

    /**
     * Convert String with various formats into java.util.Date
     *
     * @param input
     *            Date as a string
     * @return java.util.Date object if input string is parsed
     *          successfully else returns null
     */
    public static Date toDate(String input) {
        Date date = null;
        if(null == input) {
            return null;
        }
        List<SimpleDateFormat> dateFormats = new ArrayList<>();
        dateFormats.add(new SimpleDateFormat("M/dd/yyyy"));
        dateFormats.add(new SimpleDateFormat("dd.M.yyyy"));
        dateFormats.add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
        dateFormats.add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
        dateFormats.add(new SimpleDateFormat("dd.MMM.yyyy"));
        dateFormats.add(new SimpleDateFormat("dd-MMM-yyyy"));
        
        for (SimpleDateFormat format : dateFormats) {
            try {
                format.setLenient(false);
                date = format.parse(input);
            } catch (ParseException e) {
                logger.error("Shhh.. try other date formats:"+e.getMessage(),e);
            }
            if (date != null) {
                break;
            }
        }

        return date;
    }

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

    //---------------------------------------------------------------------------------------------------------------

    public static String formatDate(Date dt, String format) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(cal.getTime()));
    }

    public static String getCurrentDate(String dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(cal.getTime()));
    }

    public static String dateToString(Date dt, String dateFormat) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        StringBuilder ret = new StringBuilder();
        String separator = "";
        if(dateFormat.equals(FORMAT_YYYYMMDD) ) {
            separator = "-";
        }
        if(dateFormat.equals(FORMAT_YYYYMMDD_SLASHES) ) {
            separator = "/";
        }
        ret.append(cal.get(Calendar.YEAR));
        ret.append(separator);
        ret.append(cal.get(Calendar.MONTH) + 1);
        ret.append(separator);
        ret.append(cal.get(Calendar.DATE));

        return ret.toString();
    }

    public static String dateToString(Date dt, String tzString, String dateFormat) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        cal.setTimeZone(TimeZone.getTimeZone(tzString));

        StringBuilder ret = new StringBuilder();
        String separator = "";
        if(dateFormat.equals(FORMAT_YYYYMMDD) ) {
            separator = "-";
        }
        if(dateFormat.equals(FORMAT_YYYYMMDD_SLASHES) ) {
            separator = "/";
        }
        ret.append(cal.get(Calendar.YEAR));
        ret.append(separator);
        ret.append(cal.get(Calendar.MONTH) + 1);
        ret.append(separator);
        ret.append(cal.get(Calendar.DATE));

        return ret.toString();
    }

    public static String getTimeFromDate(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return String.valueOf(cal.get(Calendar.HOUR)) + ":" + cal.get(Calendar.MINUTE);
    }

    public static String getTimeFromDate(Date dt, String tzString) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dt);
            gc.setTimeZone(TimeZone.getTimeZone(tzString));
            StringBuilder ret = new StringBuilder();
            ret.append(gc.get(Calendar.HOUR));
            ret.append(":");
            ret.append(gc.get(Calendar.MINUTE));
            ret.append(" ");
            if(gc.get(Calendar.AM_PM) == 0) {
                ret.append("AM");
            }
            else {
                ret.append("PM");
            }
            return ret.toString();
        }
        catch(Exception e) {
            return "";
        }
    }

    public static String getDateTimeFromDate(Date dt, String tzString) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dt);
            gc.setTimeZone(TimeZone.getTimeZone(tzString));
            StringBuilder ret = new StringBuilder();
            ret.append(gc.get(Calendar.YEAR));
            ret.append("-");
            ret.append(gc.get(Calendar.MONTH) - 1);
            ret.append("-");
            ret.append(gc.get(Calendar.DATE));
            ret.append(" ");
            ret.append(gc.get(Calendar.HOUR));
            ret.append(":");
            ret.append(gc.get(Calendar.MINUTE));
            ret.append(" ");
            if(gc.get(Calendar.AM_PM) == 0) {
                ret.append("AM");
            }
            else {
                ret.append("PM");
            }
            return ret.toString();
        }
        catch(Exception e) {
            return "";
        }
    }

    public static String calendarToString(Calendar cal, String dateFormat) {
        StringBuilder ret = new StringBuilder();
        if(dateFormat.equals(FORMAT_YYYYMMDD) ) {
            ret.append(cal.get(Calendar.YEAR));
            ret.append("-");
            String month;
            int mo = cal.get(Calendar.MONTH) + 1; /* Calendar month is zero indexed, string months are not */
            if(mo < 10) {
                month = "0" + mo;
            }
            else {
                month = "" + mo;
            }
            ret.append(month);

            ret.append("-");
            String date;
            int dt = cal.get(Calendar.DATE);
            if(dt < 10) {
                date = "0" + dt;
            }
            else {
                date = "" + dt;
            }
            ret.append(date);
        }
        return ret.toString();
    }



    public static GregorianCalendar getCurrentCalendar(String utimezonestring) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeZone(TimeZone.getTimeZone(utimezonestring));
            return gc;
        }
        catch(Exception e) {
            //If exception, return server TimeStamp
            return new GregorianCalendar();
        }
    }

    public static String[] getDateRange(int cmd) {
        GregorianCalendar gc = new GregorianCalendar();
        GregorianCalendar gc2 = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
        String ret[] = new String[2];
        ret[1] = sdf.format(gc.getTime());
        if(cmd == LAST_WEEK) {
            for(int i = 0; i < 7; i++) {
                gc2.add(Calendar.DATE, -1);
            }

        }
        if(cmd == LAST_MONTH) {
            gc2.add(Calendar.MONTH, -1);
        }
        ret[0] = sdf.format(gc2.getTime());
        return ret;
    }


    public static String getDayString(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
        }
        return "";
    }

    /**
     * Method to print the content of a Calendar object.
     * e.g. Year:2013, Month:9, Date:6, Hour:4, Minutes:25, Seconds:0, AM_PM: +1
     * @param cal the Calendar Object.
     * @return the String format of the contetn of the Calendar object.
     */
    public static String printCal(Calendar cal) {
        return "Year:" + cal.get(Calendar.YEAR) + ", Month:"
                + cal.get(Calendar.MONTH) + ", Date:"
                + cal.get(Calendar.DATE) + ", Hour:"
                + cal.get(Calendar.HOUR) + ", Minutes:"
                + cal.get(Calendar.MINUTE) + ", Seconds:"
                + cal.get(Calendar.SECOND) + ", AM_PM: +"+ cal.get(Calendar.AM);
    }
   

}
