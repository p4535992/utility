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
public class RoutesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int route_id = Arrays.asList(columns).indexOf("route_id");
        int agency_id = Arrays.asList(columns).indexOf("agency_id");
        int route_short_name = Arrays.asList(columns).indexOf("route_short_name");
        int route_long_name =  Arrays.asList(columns).indexOf("route_long_name");
        int route_desc = Arrays.asList(columns).indexOf("route_desc");
        int route_type = Arrays.asList(columns).indexOf("route_type");
        int route_url =  Arrays.asList(columns).indexOf("route_url");
        int route_color = Arrays.asList(columns).indexOf("route_color");
        int route_textColor = Arrays.asList(columns).indexOf("route_textColor");


        for(String[] row : content) {
            String sub = _feedbaseuri  + "/routes/" + row[route_id];
            stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Route"));
            if (agency_id != -1 && !ne(row[agency_id])) {
                stmt.add(st(sub,"http://vocab.gtfs.org/terms#agency", _feedbaseuri + "/agencies/" + row[agency_id]));
            }
            if (route_short_name != -1 && !ne(row[route_short_name])) {
                stmt.add(st(sub,"http://vocab.gtfs.org/terms#shortName", row[route_short_name] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (route_long_name != -1 && !ne(row[route_long_name])) {
                stmt.add(st(sub,"http://vocab.gtfs.org/terms#longName",row[route_long_name] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (route_desc != -1 && !ne(row[route_desc])) {
                stmt.add(st(sub,"http://purl.org/dc/terms/description",row[route_desc] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (route_type != -1 && !ne(row[route_type])) {
                if (Objects.equals(row[route_type], "0")) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#routeType", "http://vocab.gtfs.org/terms#LightRail"));
                } else if (Objects.equals(row[route_type], "1")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType", "http://vocab.gtfs.org/terms#SubWay"));
                } else if (Objects.equals(row[route_type], "2")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType", "http://vocab.gtfs.org/terms#Rail"));
                } else if (Objects.equals(row[route_type], "3")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType", "http://vocab.gtfs.org/terms#Bus"));
                } else if (Objects.equals(row[route_type], "4")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType","http://vocab.gtfs.org/terms#Ferry"));
                } else if (Objects.equals(row[route_type], "5")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType", "http://vocab.gtfs.org/terms#CableCar"));
                } else if (Objects.equals(row[route_type], "6")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType", "http://vocab.gtfs.org/terms#Gondola"));
                } else if (Objects.equals(row[route_type], "7")) {
                    stmt.add(st(sub,"http://vocab.gtfs.org/terms#routeType","http://vocab.gtfs.org/terms#Funicular"));
                }
            }
            if (route_url != -1 && !ne(row[route_url])) {
                stmt.add(st(sub, "http://xmlns.com/foaf/0.1/page", row[route_url]));
            }
            if (route_color != -1 && !ne(row[route_color])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#color", row[route_color] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
            if (route_textColor!=-1 && !ne(row[route_textColor])) {
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#textColor", row[route_textColor] + "^^http://www.w3.org/2001/XMLSchema#string"));
            }
        }

        return stmt;
    }

}
