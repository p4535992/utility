package com.github.p4535992.util.calendar;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Classe di Utils per la conversione delle Date nei formati {@link Date} e {@link XMLGregorianCalendar}
 * 
 * @author Pancioni Marco
 * @version 1.0
 *
 */
public class DateUtils
{														
	//SimpleDateFormat per la conversiona da String a XMLGregorianCalendar e viceversa
	//static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * Converte una {@link Date} in un {@link XMLGregorianCalendar}
	 * 
	 * @param date data in formato {@link Date} 
	 * @return data in formato {@link XMLGregorianCalendar}
	 * @throws DatatypeConfigurationException
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws DatatypeConfigurationException
	{
		GregorianCalendar gCalendar = new GregorianCalendar();
		gCalendar.setTime(date);
		XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);

		return xmlCalendar;
	}
	
	/**
	 * Converte una {@link Date} in un {@link XMLGregorianCalendar}
	 * 
	 * @param date data in formato {@link Date} 
	 * @return data in formato {@link XMLGregorianCalendar}
	 * @throws DatatypeConfigurationException
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date,String FORMATER) throws DatatypeConfigurationException
	{
		//String FORMATER = "yyyy-MM-dd'T'HH:mm:ss";        
		DateFormat format = new SimpleDateFormat(FORMATER);	        		
		XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(date));
		
		return xmlCalendar;
	}

	/**
	 * Converte un {@link XMLGregorianCalendar} in una {@link Date}
	 *  
	 * @param calendar  data in formato {@link XMLGregorianCalendar}
	 * @return date data in formato {@link Date} 
	 */
	public static Date toDate(XMLGregorianCalendar calendar)
	{
		if(calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}
	
	/**
	 * Converte un {@link XMLGregorianCalendar} in una {@link Date}
	 *  
	 * @param calendar  data in formato {@link XMLGregorianCalendar}
	 * @param format la stringa pattern della data {@link String}
	 * @return date data in formato {@link Date} 
	 */
	public static String toDateString(XMLGregorianCalendar calendar,String format)
	{
		if(calendar == null) {
			return null;
		}
		return dateToString(calendar.toGregorianCalendar().getTime(),format);
	}
	
	/**
	 * Converte un {@link GregorianCalendar} in una {@link Date}
	 *  
	 * @param calendar  data in formato {@link GregorianCalendar}
	 * @return date data in formato {@link Date} 
	 */
	public static String toDateString(GregorianCalendar calendar,String format)
	{
		if(calendar == null) {
			return null;
		}
		return dateToString(calendar.getTime(),format);
		//SimpleDateFormat df = new SimpleDateFormat(format);
	    //return df.format(calendar.getTime());		
	}
	
	public static int toYear(Date date){
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
	}

	/**
	 * Converte una stringa contenente una data proveniente dal formato {@link XMLGregorianCalendar} in una {@link Date}
	 * 
	 * @param date una stringa contenente una data proveniente dal formato {@link XMLGregorianCalendar}
	 * @return date data in formato {@link Date} 
	 * @throws ParseException
	 */
	public static Date toXmlGregorianCalendarDateFormat(String date) throws ParseException
	{
		if(date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.parse(date);
	}

	/**
	 * Converte una data nel formato {@link XMLGregorianCalendar} nella corrispondente Stringa 
	 * 
	 * @param calendar data nel formato {@link XMLGregorianCalendar}
	 * @return corrispondente Stringa
	 * @throws ParseException
	 */
	public static String toXmlGregorianCalendarStringFormat(XMLGregorianCalendar calendar) throws ParseException
	{
		if(calendar == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String date = sdf.format(calendar.toGregorianCalendar().getTime());
		return date;
	}

	public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);

		Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
		long milliesRest = diffInMillies;
		for ( TimeUnit unit : units ) {
			long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit,diff);
		}
		return result;
	}

	/**
	 * Converte una stringa contenente una data proveniente dal formato {@link XMLGregorianCalendar} in una {@link Date}
	 * 
	 * @param date una stringa contenente una data proveniente dal formato {@link XMLGregorianCalendar}
	 * @return date data in formato {@link Date} 
	 * @throws ParseException
	 */
	public static Date toXmlGregorianCalendarDateFormat(String date,SimpleDateFormat sdf) throws ParseException
	{
		if(date == null) {
			return null;
		}
		return sdf.parse(date);
	}

	/**
	 * Converte una data nel formato {@link XMLGregorianCalendar} nella corrispondente Stringa 
	 * 
	 * @param calendar data nel formato {@link XMLGregorianCalendar}
	 * @return corrispondente Stringa
	 * @throws ParseException
	 */
	public static String toXmlGregorianCalendarStringFormat(XMLGregorianCalendar calendar,SimpleDateFormat sdf) throws ParseException
	{
		if(calendar == null) {
			return null;
		}
		String date = sdf.format(calendar.toGregorianCalendar().getTime());
		return date;
	}

	/**
	 * Metodo per convertire una data in una stringa in un formato specifico
	 */
	public static String dateToString(Date date, String format){
		if(format==null || format.isEmpty()){
			format = "dd/MM/yyyy";
		}
		LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);		
		String dataS = ldt.format(fmt);	
		return dataS;
	}
	
	/**
	 * Metodo per convertire una data in una stringa in un formato specifico
	 */
	public static String calendarToString(Calendar calendar, String format){
		if(format==null || format.isEmpty()){
			format = "dd/MM/yyyy";
		}	
		LocalDateTime ldt = LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);		
		String dataS = ldt.format(fmt);	
		return dataS;
	}

	//	   public static String changeFormatDateString(String date, String format){
	//		    if(format==null || format.isEmpty()){
	//		    	format = "dd/MM/yyyy";
	//		    }
	//			Date d = stringToDate(date);
	//			return dateToString(d, format);
	//	   }

	/**
	 * Metodo per convertire il formato di una data in un altro
	 */
	public static Date changeFormatDate(String date, String format){
		if(date==null || date=="0" || date.length()==1){
			return null;
		}
		if(format==null || format.isEmpty()){
			format = "dd/MM/yyyy";
		}
		Date d = stringToDate(date);
		return stringToDate(dateToString(d, format));
	}
	
	/**
	 * Metodo per convertire il formato di una data in un altro
	 */
	public static String changeFormatDateString(String date, String format){
		if(format==null || format.isEmpty()){
			format = "dd/MM/yyyy";
		}
		Date d = stringToDate(date);
		return dateToString(d, format);
	}


	/*http://stackoverflow.com/questions/4024544/how-to-parse-dates-in-multiple-formats-using-simpledateformat*/
	private static final String[] timeFormats = {"HH:mm:ss","HH:mm"};
	private static final String[] dateSeparators = {"/","-"," ",""};

	private static final String DMY_FORMAT = "dd{sep}MM{sep}yyyy";
	private static final String YMD_FORMAT = "yyyy{sep}MM{sep}dd";

	private static final String ymd_template = "\\d{4}{sep}\\d{2}{sep}\\d{2}.*";
	private static final String dmy_template = "\\d{2}{sep}\\d{2}{sep}\\d{4}.*";

	/**
	 * Metodo universale per convertire una qualsiasi stringa pattern di una data in un'oggetto data
	 * @param input
	 * @return
	 */
	public static Date stringToDate(String input){
		Date date = null;

		if(input.length()==4){
			return tryParse(input, "yyyy");
		}

		String dateFormat = getDateFormat(input);
		if(dateFormat == null){
			throw new IllegalArgumentException("Date is not in an accepted format " + input);
		}

		for(String sep : dateSeparators){
			String actualDateFormat = patternForSeparator(dateFormat, sep);
			//try first with the time
			for(String time : timeFormats){
				date = tryParse(input,actualDateFormat + " " + time);
				if(date != null){
					return date;
				}
			}
			//didn't work, try without the time formats
			date = tryParse(input,actualDateFormat);
			if(date != null){
				return date;
			}
		}
		// logger.error("Can't convert the String:"+input+" to a Date Object");
		return date;
	}

	private static String getDateFormat(String date){
		for(String sep : dateSeparators){
			String ymdPattern = patternForSeparator(ymd_template, sep);
			String dmyPattern = patternForSeparator(dmy_template, sep);
			if(date.matches(ymdPattern)){
				return YMD_FORMAT;
			}
			if(date.matches(dmyPattern)){
				return DMY_FORMAT;
			}
		}
		return null;
	}

	private static String patternForSeparator(String template, String sep){
		return template.replace("{sep}", sep);
	}

	private static Date tryParse(String input, String pattern){
		try{
			return new SimpleDateFormat(pattern).parse(input);
		}
		catch (ParseException e) {
			// logger.error(e.getMessage(),e);
			//ignored
		}
		return null;
	}

	/**
	 * @deprecated usa {@link DateUtils#xmlGregorianToString(XMLGregorianCalendar, String)}
	 */
	public static String convertXmlGregorianToString(XMLGregorianCalendar xc,String patternDate){
		return xmlGregorianToString(xc,patternDate);
	}
	
	/**
	 * Metodo per convertire un'oggetto XMLGregorianCalendar a stringa
	 * @param xc the XMLGregorianCalendar
	 * @param stringa della data  e.g. "MM/dd/yyyy hh:mm a z"
	 * @return the string of the XMLGregorianCalendar
	 */
	public static String xmlGregorianToString(XMLGregorianCalendar xc,String patternDate)
    {
        TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        TimeZone fromTimeZone = TimeZone.getDefault();
        GregorianCalendar gCalendar = xc.toGregorianCalendar();
        DateFormat df = new SimpleDateFormat(patternDate);
        Date date = adjustToTimezone(gCalendar.getTime(), fromTimeZone, gmtTimeZone);
        String dateString = df.format(date);
        return dateString;
    }
 
    private static Date adjustToTimezone(Date date, TimeZone fromZone, TimeZone toZone)
    {
        Date adjustedToTimezone = new Date(date.getTime() + toZone.getRawOffset() - fromZone.getRawOffset());
        // Is the adjusted date in Daylight savings?
        if (fromZone.inDaylightTime(adjustedToTimezone) != toZone.inDaylightTime(adjustedToTimezone)) {
            adjustedToTimezone = new Date(adjustedToTimezone.getTime() + toZone.getDSTSavings() - fromZone.getDSTSavings());
        }
        return adjustedToTimezone;
    }
    
    /**
	 * Calcola il numero di anni che intercorrono fra due date
	 * @param sStartDate
	 * @param sEndDate
	 * @return
	 * @throws ParseException
	 */
	public static Integer calcolaAnni(Date startDate,Date endDate) throws ParseException{	
	    Calendar cal1 = Calendar.getInstance(Locale.ITALIAN);
	    Calendar cal2 = Calendar.getInstance(Locale.ITALIAN);
	    cal1.setTime(startDate);
	    cal2.setTime(endDate);
	    int years = 0;
	    while (cal1.before(cal2)) {
	        cal1.add(Calendar.YEAR, 1);
	        if (cal1.before(cal2)) {
	            years++;
	        }
	    } 
	    return years;	
	}
	
	public static Date getRandomDate(){
		 
		 long beginTime = Timestamp.valueOf("2013-01-01 00:00:00").getTime();
	     long endTime = Timestamp.valueOf("2018-12-31 00:58:00").getTime();
	     
	     long diff = endTime - beginTime + 1;
	     long diff2 = beginTime + (long) (Math.random() * diff);
	     
		 DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		 Instant instant = Instant.ofEpochSecond(diff2);
		 LocalDateTime randomDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC-06:00"));
		 randomDate.format(format);

		 //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		 return new Date(randomDate.atZone(ZoneId.of("UTC-06:00")).toEpochSecond());
		
	}
	
	public static int getRandomYear(){
		return getRandomDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
	}
}
