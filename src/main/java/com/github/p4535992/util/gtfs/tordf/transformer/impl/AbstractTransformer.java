package com.github.p4535992.util.gtfs.tordf.transformer.impl;

import com.github.p4535992.util.gtfs.tordf.transformer.Transformer;
import com.github.p4535992.util.repositoryRDF.jena.Jena2Kit;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Statement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    protected Statement st(String subject,String predicate,Object object){
        return Jena2Kit.createStatement(subject, predicate, object);
    }

    protected Statement st(String subject,String predicate,Object object,XSDDatatype xsdDatatype){
        return Jena2Kit.createStatement(subject, predicate, object,xsdDatatype);
    }

    /**
     * Method to get the String array of the columns of a CSV File.
     * @param fileCSV the File CSV.
     * @param hasFirstLine if true the first line of CSV File contains the columns name.
     * @return a String Array of the columns.
     */
    protected String[] CSVGetHeaders(File fileCSV,boolean hasFirstLine){
        String[] columns = new String[0];
        try{
            com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(fileCSV));
            columns = reader.readNext(); // assuming first read
            if(!hasFirstLine){
                int columnCount =0;
                if (columns != null)  columnCount = columns.length;
                columns = new String[columnCount];
                for(int  i=0; i< columnCount; i++){
                    columns[i] = "Column#"+i;
                }
            }
        }catch(IOException e){
            logger.error("Can't find the CSV File",e);
        }
        return columns;
    }

    /**
     * Method to get the content of a comma separated file (.csv,.input,.txt)
     * @param CSV the File comma separated.
     * @param header if true jump the first line of the content.
     * @return the List of Array of the content of the File comma separated.
     */
    protected List<String[]> CSVGetContent(File CSV,boolean header){
        List<String[]> content = new ArrayList<>();
        try {
            com.opencsv.CSVReader reader1 = new com.opencsv.CSVReader(new FileReader(CSV));
            content = reader1.readAll();
            /* List<String[]> myDatas = reader1.readAll();
            String[] lineI = myDatas.get(i);
            for (String[] line : myDatas) {
                for (String value : line) {
                    //do stuff with value
                }
            }*/
            if (header) content.remove(0);
        }catch(IOException e){
            logger.error("Can't find the CSV File",e);
        }
        return content;
    }

    protected Date fc(String date) throws ParseException {
        return new SimpleDateFormat("YYYY-MM-DD").parse(date);
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
            pe.printStackTrace();
            return "";
        }
    }

}
