package com.github.p4535992.util.gtfs.tordf.transformer.impl;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.gtfs.tordf.transformer.Transformer;
import com.github.p4535992.util.repositoryRDF.jena.Jena3Utilities;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Statement;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 * @version 2015-11-30.
 */
public abstract class AbstractTransformer implements Transformer {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(AbstractTransformer.class);

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        return null;
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    protected boolean ne(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty() ;
    }

    /**
     * Method utility: create statement form a jena Model.
     * @param subject the iri subject.
     * @param predicate the iri predicate.
     * @param object the iri object.
     * @return Statement.
     */
    protected Statement st(String subject, String predicate, Object object){
        return Jena3Utilities.toStatement(subject, predicate, object);
    }

    protected Statement st(String subject,String predicate,Object object,XSDDatatype xsdDatatype){
        return Jena3Utilities.toStatement(subject, predicate, object,xsdDatatype);
    }

    /**
     * Method to get the content of a comma separated file (.csv,.input,.txt)
     * @param CSV the File comma separated.
     * @param header if true jump the first line of the content.
     * @return the List of Array of the content of the File comma separated.
     */
    protected List<String[]> CSVGetContent(File CSV,boolean header){
        return FileUtilities.CSVGetContent(CSV,header);
    }

    /**
     * Method to get the String array of the columns of a CSV File.
     * @param fileCSV the File CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    public static String[] CSVGetHeaders(File fileCSV,boolean hasFirstLine){
        return FileUtilities.CSVGetHeaders(fileCSV,hasFirstLine);
    }

    protected Date fc(String date) throws ParseException {
        //return new SimpleDateFormat("YYYY-MM-DD").parse(date);
        if(date.contains("-")||date.contains(" ")||date.contains("/"))
            return  stringToDate(date);
        else{
            String date2 = checkAndPrepareEngDate(date);
            if(!date2.isEmpty())return new SimpleDateFormat("YYYY-MM-DD").parse(date2);

            date2 = checkAndPrepareEuropeanDate(date);
            if(!date2.isEmpty())return new SimpleDateFormat("DD-MM-YYYY").parse(date2);

            return  stringToDate(date);
        }
    }

    protected String moment(String date){
        try {
            // Create an instance of SimpleDateFormat used for formatting
            // the string representation of date (month/day/year)
            DateFormat df = new SimpleDateFormat("YYYY-MM-DD");
            // Get the date today using Calendar object.
            //Date today = Calendar.getInstance().getTime();
            // Using DateFormat format method we can create a string
            // representation of a date with the defined format.
            return df.format(fc(date));
        }catch(ParseException pe){
            logger.error(pe.getMessage(),pe);
            return "";
        }
    }


    /*http://stackoverflow.com/questions/4024544/how-to-parse-dates-in-multiple-formats-using-simpledateformat*/
    private static final String[] timeFormats = {"HH:mm:ss","HH:mm"};
    private static final String[] dateSeparators = {"/","-"," "};

    private static final String DMY_FORMAT = "dd{sep}MM{sep}yyyy";
    private static final String YMD_FORMAT = "yyyy{sep}MM{sep}dd";

    private static final String ymd_template = "\\d{4}{sep}\\d{2}{sep}\\d{2}.*";
    private static final String dmy_template = "\\d{2}{sep}\\d{2}{sep}\\d{4}.*";

    private static Date stringToDate(String input){
        Date date = null;
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
        logger.error("Can't convert the String:"+input+" to a Date Object");
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
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /*http://stackoverflow.com/questions/10013998/fastest-way-to-parse-a-yyyymmdd-date-in-java

    year = Integer.parseInt(dateString.substring(0, 4));
    month = Integer.parseInt(dateString.substring(4, 6));
    day = Integer.parseInt(dateString.substring(6));

    * */
    private String checkAndPrepareEngDate(String dateString){
        //Checkout ddmmyyyy
        /*int date = Integer.parseInt(dateString);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4, 6));
        int day = Integer.parseInt(dateString.substring(6));*/
        int date = Integer.parseInt(dateString);
        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;
        if(day <= 31 && month <= 12 && year < 3000){
            return year+"-"+month+"-"+day;
        }else{
            return "";
        }
    }

    private String checkAndPrepareEuropeanDate(String dateString){
        //Checkout ddmmyyyy
        int date = Integer.parseInt(dateString);
        int year = Integer.parseInt(dateString.substring(4));
        int month = Integer.parseInt(dateString.substring(2, 4));
        int day = Integer.parseInt(dateString.substring(0,2));
        if(day <= 31 && month <= 12 && year <= 3000){
            return year+"-"+month+"-"+day;
        }else{
            return "";
        }
    }


}
