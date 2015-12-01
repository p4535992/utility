package com.github.p4535992.util.gtfs.tordf.transformer.impl;

import com.github.p4535992.util.gtfs.tordf.transformer.Transformer;
import com.hp.hpl.jena.rdf.model.Statement;

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
public class FrequenciesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int trip_id = Arrays.asList(columns).indexOf("trip_id");
        int start_time = Arrays.asList(columns).indexOf("start_time");
        int end_time = Arrays.asList(columns).indexOf("end_time");
        int headway_secs = Arrays.asList(columns).indexOf("headway_secs");
        int exact_times = Arrays.asList(columns).indexOf("exact_times");

        for(String[] row : content) {
            if (trip_id != -1 && start_time != -1 && end_time != -1 && headway_secs != -1 &&
                    !ne(row[trip_id]) && !ne(row[start_time]) && !ne(row[end_time]) && !ne(row[headway_secs]) ) {
                String sub = _feedbaseuri + "/trips/" + row[trip_id] + "/frequencies/" + row[start_time] + row[end_time];
                stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Frequency"));
                stmt.add(st( sub, "http://vocab.gtfs.org/terms#startTime", row[start_time]));
                stmt.add(st( sub, "http://vocab.gtfs.org/terms#endTime", row[end_time]));
                stmt.add(st( sub, "http://vocab.gtfs.org/terms#headwaySeconds", row[headway_secs] + "^^http://www.w3.org/2001/XMLSchema#string"));
                if (exact_times != -1 && !ne(row[exact_times])) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#headwaySeconds", String.valueOf(Objects.equals(row[exact_times], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
                } else {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#headwaySeconds", "false"+"^^http://www.w3.org/2001/XMLSchema#boolean"));
                }
            } else {
               //"Frequency doesn't contain all required fields");
                continue;
        }

    }

    return stmt;
    }
}
