package com.github.p4535992.util.gtfs.tordf.transformer.impl;

import org.apache.jena.rdf.model.Statement;

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
public class FareRulesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);

        int fare_id = Arrays.asList(columns).indexOf("fare_id");
        int route_id = Arrays.asList(columns).indexOf("route_id");
        int origin_id = Arrays.asList(columns).indexOf("origin_id");
        int destination_id = Arrays.asList(columns).indexOf("destination_id");
        int contains_id = Arrays.asList(columns).indexOf("contains_id");

        for(String[] row : content) {
            if (ne(row[fare_id])) {
                //"Parser error: could not find a fare_id. Data follows:");
               continue;
            } else {
                String sub = _feedbaseuri + "/fareclasses/" + row[fare_id] + "/farerules/"
                        + (!ne(row[route_id]) ? row[route_id] :"")
                        + (!ne(row[origin_id]) ? row[origin_id] : "")
                        + (!ne(row[destination_id]) ? row[destination_id] : "")
                        + (!ne(row[contains_id]) ? row[contains_id] : "");
                stmt.add(st( sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#FareRule"));
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#fareClass", _feedbaseuri + "/fareclasses/" + row[fare_id]));
                if (route_id != -1 && !ne(row[route_id])) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#route",_feedbaseuri + "/routes/" + row[route_id]));
                }
                if (origin_id != -1 && !ne(row[origin_id])) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#originZone", _feedbaseuri + "/zones/" + row[origin_id]));
                }
                if (destination_id != -1 && !ne(row[destination_id])) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#destinationZone",_feedbaseuri + "/zones/" + row[destination_id]));
                }
                if (contains_id != -1 && !ne(row[contains_id])) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#zone", _feedbaseuri + "/zones/" + row[contains_id]));
                }
            }
        }
        return stmt;
    }

}
