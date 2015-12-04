package com.github.p4535992.util.gtfs.tordf.transformer.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Statement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 * @version 2015-11-30.
 */
public class AgenciesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);

        int agency_id = Arrays.asList(columns).indexOf("agency_id");
        int agency_name = Arrays.asList(columns).indexOf("agency_name");
        int agency_url = Arrays.asList(columns).indexOf("agency_url");
        int agency_phone = Arrays.asList(columns).indexOf("agency_phone");
        int agency_fare_url = Arrays.asList(columns).indexOf("agency_fare_url");
        int agency_timezone = Arrays.asList(columns).indexOf("agency_timezone");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/agencies/" + row[agency_id];
            stmt.add(st(sub,"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Agency"));

            // foaf:name triple
            if (agency_name != -1 && !ne(row[agency_name])) {
                //stmt.add(st(sub,"http://xmlns.com/foaf/0.1/name", row[agency_name] + "^^http://www.w3.org/2001/XMLSchema#string"));
                stmt.add(st(sub,"http://xmlns.com/foaf/0.1/name", row[agency_name], XSDDatatype.XSDstring));
            }

            // foaf:page triple
            if (agency_url!= -1 && !ne(row[agency_url])) {
                stmt.add(st(sub,"http://xmlns.com/foaf/0.1/page",row[agency_url]));
            }

            // foaf:phone triple
            if (agency_phone != -1 && !ne(row[agency_phone])) {
                stmt.add(st(sub, "http://xmlns.com/foaf/0.1/phone", row[agency_phone]));
            }
            // gtfs:fareUrl triple
            if (agency_fare_url!= -1 && !ne(row[agency_fare_url])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#fareUrl", row[agency_fare_url]));
            }

            if (agency_timezone != -1 && !ne(row[agency_timezone])) {
                stmt.add(st(sub, "http://www.w3.org/2006/time#timeZone", row[agency_timezone] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }

        }
        return stmt;
    }


}
