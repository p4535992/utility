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
public class StopsTransformer extends AbstractTransformer{

    /**
     *  Creates a gtfs:Station or gtfs:Stop with additional properties. Also creates a gtfs:Zone
     */
    @Override
    public List<Statement> _Transform(File data,Charset encoding,String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data,true);
        String[] columns = CSVGetHeaders(data, true);
        int stop_id = Arrays.asList(columns).indexOf("stop_id");
        int location_type = Arrays.asList(columns).indexOf("location_type");
        int parent_station = Arrays.asList(columns).indexOf("parent_station");
        int zone_id = Arrays.asList(columns).indexOf("zone_id");
        int stop_code = Arrays.asList(columns).indexOf("stop_code");
        int stop_name = Arrays.asList(columns).indexOf("stop_name");
        int stop_desc = Arrays.asList(columns).indexOf("stop_desc");
        int stop_lat = Arrays.asList(columns).indexOf("stop_lat");
        int stop_lon = Arrays.asList(columns).indexOf("stop_lon");
        int stop_url  = Arrays.asList(columns).indexOf("stop_url");
        int wheelchair_boarding = Arrays.asList(columns).indexOf("wheelchair_boarding");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/stops/" + row[stop_id];
            stmt.add(st(sub, "http://purl.org/dc/terms/identifier", row[stop_id]));
            // gtfs:locationType triple
            if (location_type != -1 && Objects.equals(row[location_type], "1")) {
                stmt.add(st(sub,"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Station"));
            } else {
                stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Stop"));
                //Stations can't have a parent_station, only Stops can have a parent station.
                if (parent_station != -1 && !ne(row[parent_station])) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#parentStation", row[parent_station]));
                }
                if (zone_id != -1 && !ne(row[zone_id])) {
                    stmt.add(st(_feedbaseuri + "/zones/" + row[zone_id], "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Zone"));
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#zone", _feedbaseuri + "/zones/" + row[zone_id]));
                }
            }
            // gtfs:code triple
            if (stop_code != -1 && !ne(row[stop_code])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#code", row[stop_code]));
            }
            // foaf:name triple
            if (stop_name != -1 && !ne(row[stop_name])) {
                stmt.add(st(sub, "http://xmlns.com/foaf/0.1/name", row[stop_name]));
            }
            if (stop_desc != -1 && !ne(row[stop_desc])) {
                stmt.add(st(sub, "http://purl.org/dc/terms/description", row[stop_desc]));
            }
            // geo:lat triple
            if (stop_lat != -1 && !ne(row[stop_lat])) {
                stmt.add(st(sub, "http://www.w3.org/2003/01/geo/wgs84_pos#lat", row[stop_lat]));
            }
            // geo:long triple
            if (stop_lon != -1 && !ne(row[stop_lon])) {
                stmt.add(st(sub, "http://www.w3.org/2003/01/geo/wgs84_pos#long", row[stop_lon]));
            }
            if (stop_url != -1 && ne(row[stop_url])) {
                stmt.add(st(sub, "http://xmlns.com/foaf/0.1/page", row[stop_url]));
            }
            if (wheelchair_boarding != -1 && ne(row[wheelchair_boarding])) {
                if (Objects.equals(row[wheelchair_boarding], "0")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#wheelchairAccessible", "http://vocab.gtfs.org/terms#CheckParentStation"));
                } else if (Objects.equals(row[wheelchair_boarding], "1")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#wheelchairAccessible", "http://vocab.gtfs.org/terms#WheelchairAccessible"));
                } else if (Objects.equals(row[wheelchair_boarding], "2")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#wheelchairAccessible", "http://vocab.gtfs.org/terms#NotWheelchairAccessible"));
                }
            }
        }
        return stmt;
    }

}
