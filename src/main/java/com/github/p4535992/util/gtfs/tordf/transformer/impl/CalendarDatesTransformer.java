package com.github.p4535992.util.gtfs.tordf.transformer.impl;

import org.apache.jena.rdf.model.Statement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 * @version 2015-11-30.
 */
public class CalendarDatesTransformer extends AbstractTransformer {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(CalendarDatesTransformer.class);

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int service_id = Arrays.asList(columns).indexOf("service_id");
        int date = Arrays.asList(columns).indexOf("date");
        int exception_type = Arrays.asList(columns).indexOf("exception_type");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/services/" + row[service_id];
            stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Service"));
            String servicerule = sub + "/servicerules/" + row[date];
            stmt.add(st(sub, "http://vocab.gtfs.org/terms#serviceRule", servicerule));
            stmt.add(st(servicerule, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#ServiceRule"));
            stmt.add(st( servicerule, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#CalendarDateRule"));

            if (exception_type != -1 && !ne(row[exception_type])){
                //1 is added for the certain date
                //2 is removed for the certain date
                stmt.add(st( servicerule, "http://vocab.gtfs.org/terms#dateAddition", String.valueOf(Objects.equals(row[exception_type], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }

            if (date != -1 && !ne(row[date])){
                stmt.add(st( servicerule, "http://purl.org/dc/terms/date",  moment(row[date]) + "^^http://www.w3.org/2001/XMLSchema#date"));
            }
        }
        return stmt;
    }

}
