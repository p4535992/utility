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
public class ShapesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data,Charset encoding,String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);

        int shape_id = Arrays.asList(columns).indexOf("shape_id");
        int shape_pt_lat = Arrays.asList(columns).indexOf("shape_pt_lat");
        int shape_pt_lon = Arrays.asList(columns).indexOf("shape_pt_lon");
        int shape_pt_sequence = Arrays.asList(columns).indexOf("shape_pt_sequence");
        int shape_dist_traveled = Arrays.asList(columns).indexOf("shape_dist_traveled ");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/shapes/" + row[shape_id];
            stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Shape"));
            if (shape_pt_lat != -1 && shape_pt_lon !=-1 && shape_pt_sequence != -1 &&
                    !ne(row[shape_pt_lat]) && !ne(row[shape_pt_lon]) && !ne(row[shape_pt_sequence])) {
                String point = sub + "/shapepoints/" + row[shape_pt_sequence];
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#shapePoint", point));
                stmt.add(st( point, "http://www.w3.org/2003/01/geo/wgs84_pos#lat", row[shape_pt_lat] +  "^^http://www.w3.org/2001/XMLSchema#float"));
                stmt.add(st( point, "http://www.w3.org/2003/01/geo/wgs84_pos#long", row[shape_pt_lon] + "^^http://www.w3.org/2001/XMLSchema#float"));
                stmt.add(st( point, "http://vocab.gtfs.org/terms#pointSequence", row[shape_pt_sequence] + "^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));

                if (shape_dist_traveled != -1 && !ne(row[shape_dist_traveled])) {
                    stmt.add(st( point, "http://vocab.gtfs.org/terms#distanceTraveled", row[shape_dist_traveled] + "^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
                }

            } else {
                //"Required column not set in shape_points. Data dump follows:");
                continue;
            }
        }
        return stmt;
    }

}
