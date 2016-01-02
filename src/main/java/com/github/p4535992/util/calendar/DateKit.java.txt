package com.github.p4535992.util.calendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
/**
 * Created by 4535992 on 14/09/2015.
 */
@SuppressWarnings("unused")
public class DateKit {

    public static final String FORMAT_YYYYMMDD = "yyyy-MM-dd";
    public static final String FORMAT_YYYYMMDD_SLASHES = "yyyy/MM/dd";
    public static final String GENERIC_DISPLAY_FORMAT = "E, dd MMM yyyy";
    public static final String TIME_DISPLAY_FORMAT = "HH mm ss";
    public static final int LAST_WEEK = 1;
    public static final int LAST_MONTH = 2;

    public static String formatDate(Date dt, String format) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(cal.getTime()));
    }

    public static String getCurrentDate(String format) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(cal.getTime()));
    }

    public static String dateToString(Date dt, String dateformat) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        StringBuilder ret = new StringBuilder();
        String separator = "";
        if(dateformat.equals(DateKit.FORMAT_YYYYMMDD) ) {
            separator = "-";
        }
        if(dateformat.equals(DateKit.FORMAT_YYYYMMDD_SLASHES) ) {
            separator = "/";
        }
        ret.append(cal.get(Calendar.YEAR));
        ret.append(separator);
        ret.append(cal.get(Calendar.MONTH) + 1);
        ret.append(separator);
        ret.append(cal.get(Calendar.DATE));

        return ret.toString();
    }

    public static String dateToString(Date dt, String tzString, String dateformat) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        cal.setTimeZone(TimeZone.getTimeZone(tzString));

        StringBuilder ret = new StringBuilder();
        String separator = "";
        if(dateformat.equals(DateKit.FORMAT_YYYYMMDD) ) {
            separator = "-";
        }
        if(dateformat.equals(DateKit.FORMAT_YYYYMMDD_SLASHES) ) {
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

    public static String calendarToString(Calendar cal, String dateformat) {
        StringBuilder ret = new StringBuilder();
        if(dateformat.equals(FORMAT_YYYYMMDD) ) {
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
        // TODO Auto-generated method stub
        return "Year:" + cal.get(Calendar.YEAR) + ", Month:"
                + cal.get(Calendar.MONTH) + ", Date:"
                + cal.get(Calendar.DATE) + ", Hour:"
                + cal.get(Calendar.HOUR) + ", Minutes:"
                + cal.get(Calendar.MINUTE) + ", Seconds:"
                + cal.get(Calendar.SECOND) + ", AM_PM: +"+ cal.get(Calendar.AM);
    }
}


