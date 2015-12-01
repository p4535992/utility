package com.github.p4535992.util.gtfs.tordf.transformer.impl;
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
public class TripsTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int trip_id = Arrays.asList(columns).indexOf("trip_id");
        int route_id = Arrays.asList(columns).indexOf("route_id");
        int service_id = Arrays.asList(columns).indexOf("service_id");
        int trip_headsign = Arrays.asList(columns).indexOf("trip_headsign");
        int trip_short_name = Arrays.asList(columns).indexOf("trip_short_name");
        int direction_id = Arrays.asList(columns).indexOf("direction_id");
        int block_id = Arrays.asList(columns).indexOf("block_id");
        int shape_id = Arrays.asList(columns).indexOf("shape_id");
        int wheelchair_accessible = Arrays.asList(columns).indexOf("wheelchair_accessible");
        int bikes_allowed = Arrays.asList(columns).indexOf("bikes_allowed");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/trips/" + row[trip_id];
            stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Trip"));
            if (route_id != -1 && !ne(row[route_id])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#route", _feedbaseuri + "/routes/" + row[route_id]));
            }
            if (service_id != -1 && !ne(row[service_id])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#service", _feedbaseuri + "/services/" + row[service_id]));
            }
            if (trip_headsign != -1 && !ne(row[trip_headsign])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#headsign", row[trip_headsign] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (trip_short_name != -1 && !ne(row[trip_short_name])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#shortName", row[trip_short_name] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (direction_id != -1 && !ne(row[direction_id])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#direction", String.valueOf(Objects.equals(row[direction_id], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
            if (block_id != -1 && !ne(row[block_id])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#block", row[block_id]));
            }
            if (shape_id != -1 && !ne(row[shape_id])) {
                stmt.add(st( _feedbaseuri + "/shapes/" + row[shape_id],"http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://vocab.gtfs.org/terms#Shape"));
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#shape", _feedbaseuri + "/shapes/" + row[shape_id]));
            }
            if (wheelchair_accessible != -1 && !ne(row[wheelchair_accessible])) {
                if (Objects.equals(row[wheelchair_accessible], "0")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#wheelchairAccessible", "http://vocab.gtfs.org/terms#CheckParentStation"));
                } else if (Objects.equals(row[wheelchair_accessible], "1")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#wheelchairAccessible", "http://vocab.gtfs.org/terms#WheelchairAccessible"));
                } else if (Objects.equals(row[wheelchair_accessible], "2")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#wheelchairAccessible", "http://vocab.gtfs.org/terms#NotWheelchairAccessible"));
                }
            }
            if (bikes_allowed != -1 && !ne(row[bikes_allowed])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#bikesAllowed", String.valueOf(Objects.equals(row[bikes_allowed], "1")) + "^^http://www.w3.org/2001/XMLSchema#boolean"));
            }
        }
        return stmt;
    }


}
