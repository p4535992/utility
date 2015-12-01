package com.github.p4535992.util.gtfs.tordf.transformer.impl;
import com.hp.hpl.jena.rdf.model.Statement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 * @version 2015-11-30.
 */
public class CalendarTransformer extends AbstractTransformer{

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int service_id = Arrays.asList(columns).indexOf("service_id");
        int monday = Arrays.asList(columns).indexOf("monday");
        int tuesday = Arrays.asList(columns).indexOf("tuesday");
        int wednesday = Arrays.asList(columns).indexOf("wednesday");
        int thursday = Arrays.asList(columns).indexOf("thursday");
        int friday = Arrays.asList(columns).indexOf("friday");
        int saturday = Arrays.asList(columns).indexOf("saturday");
        int sunday = Arrays.asList(columns).indexOf("sunday");
        int start_date = Arrays.asList(columns).indexOf("start_date");
        int end_date = Arrays.asList(columns).indexOf("end_date");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/services/" + row[service_id];
            stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Service"));
            String servicerule = sub + "/servicesrules/calendarrule";
            stmt.add(st( servicerule, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#ServiceRule"));
            stmt.add(st( servicerule, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#CalendarRule"));
            stmt.add(st( sub, "http://vocab.gtfs.org/terms#serviceRule", servicerule));

            if (monday != -1 && !ne(row[monday])) {
                stmt.add(st(servicerule, "http://vocab.gtfs.org/terms#monday", String.valueOf(Objects.equals(row[monday], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (tuesday != -1 && !ne(row[tuesday])) {
                stmt.add(st(servicerule, "http://vocab.gtfs.org/terms#tuesday", String.valueOf(Objects.equals(row[tuesday], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (wednesday != -1 && !ne(row[wednesday])) {
                stmt.add(st(servicerule, "http://vocab.gtfs.org/terms#wednesday", String.valueOf(Objects.equals(row[wednesday], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (thursday != -1 && !ne(row[thursday])) {
                stmt.add(st(servicerule, "http://vocab.gtfs.org/terms#thursday", String.valueOf(Objects.equals(row[thursday], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (friday != -1 && !ne(row[friday])) {
                stmt.add(st(servicerule, "http://vocab.gtfs.org/terms#friday", String.valueOf(Objects.equals(row[friday], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (saturday != -1 && !ne(row[saturday])) {
                stmt.add(st(servicerule, "http://vocab.gtfs.org/terms#saturday", String.valueOf(Objects.equals(row[saturday], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (sunday != -1 && !ne(row[sunday])) {
                stmt.add(st( servicerule, "http://vocab.gtfs.org/terms#sunday", String.valueOf(Objects.equals(row[sunday], "1"))  + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (start_date != -1 && end_date != -1 && !ne(row[start_date]) && !ne(row[end_date])){
                String temporal = servicerule + "/temporal";

                stmt.add(st( servicerule, "http://purl.org/dc/terms/temporal", temporal));
                stmt.add(st(temporal,  "http://schema.org/startDate", moment(row[start_date]) + "^^http://www.w3.org/2001/XMLSchema#date"));
                stmt.add(st( temporal, "http://schema.org/endDate", moment(row[end_date]) + "^^http://www.w3.org/2001/XMLSchema#date"));
            }
        }
        return stmt;
    }
}
