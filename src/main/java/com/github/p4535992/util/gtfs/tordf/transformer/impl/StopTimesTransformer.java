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
public class StopTimesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int trip_id = Arrays.asList(columns).indexOf("trip_id");
        int stop_id = Arrays.asList(columns).indexOf("stop_id");
        int arrival_time = Arrays.asList(columns).indexOf("arrival_time");
        int departure_time = Arrays.asList(columns).indexOf("departure_time");
        int stop_sequence = Arrays.asList(columns).indexOf("stop_sequence");
        int stop_headsign = Arrays.asList(columns).indexOf("stop_headsign");
        int pickup_type = Arrays.asList(columns).indexOf("pickup_type");
        int drop_off_type = Arrays.asList(columns).indexOf("drop_off_type");
        int shape_dist_traveled = Arrays.asList(columns).indexOf("shape_dist_traveled");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/trip/" + row[trip_id] + "/stop/" + row[stop_id];
            stmt.add(st(sub,"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#StopTime"));
            stmt.add(st(sub,"http://vocab.gtfs.org/terms#stop", _feedbaseuri + "/stops/"  + row[stop_id]));
            stmt.add(st(sub, "http://vocab.gtfs.org/terms#trip", _feedbaseuri + "/trips/" + row[trip_id]));
            if (arrival_time != -1 && !ne(row[arrival_time])){
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#arrivalTime", row[arrival_time] + "^^http://www.w3.org/2001/XMLSchema#duration"));
            }
            if (departure_time != -1 && !ne(row[departure_time])){
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#departureTime", row[departure_time] + "^^http://www.w3.org/2001/XMLSchema#duration"));
            }
            if (stop_sequence != -1 && !ne(row[stop_sequence])){
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#stopSequence", row[stop_sequence] + "^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
            }
            if (stop_headsign != -1 && !ne(row[stop_headsign])){
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#headsign", row[stop_headsign] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (pickup_type != -1 && !ne(row[pickup_type])){
                if (Objects.equals(row[pickup_type], "0")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#pickupType", "http://vocab.gtfs.org/terms#Regular"));
                } else if (Objects.equals(row[pickup_type], "1")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#pickupType", "http://vocab.gtfs.org/terms#NotAvailable"));
                } else if (Objects.equals(row[pickup_type], "2")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#pickupType", "http://vocab.gtfs.org/terms#MustPhone"));
                } else if (Objects.equals(row[pickup_type], "3")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#pickupType", "http://vocab.gtfs.org/terms#MustCoordinateWithDriver"));
                }
            }
            if (drop_off_type != -1 && !ne(row[drop_off_type])){
                if (Objects.equals(row[drop_off_type], "0")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#dropOffType", "http://vocab.gtfs.org/terms#Regular"));
                } else if (Objects.equals(row[drop_off_type], "1")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#dropOffType", "http://vocab.gtfs.org/terms#NotAvailable"));
                } else if (Objects.equals(row[drop_off_type], "2")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#dropOffType", "http://vocab.gtfs.org/terms#MustPhone"));
                } else if (Objects.equals(row[drop_off_type], "3")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#dropOffType", "http://vocab.gtfs.org/terms#MustCoordinateWithDriver"));
                }
            }
            if (shape_dist_traveled != -1 && !ne(row[shape_dist_traveled])){
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#distanceTraveled", row[shape_dist_traveled] + "^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
            }
        }
        return stmt;
    }


}
