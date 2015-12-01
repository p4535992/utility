package com.github.p4535992.util.gtfs.tordf.transformer.impl;


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
public class FeedInfoTransformer extends AbstractTransformer{

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);

        int _feedcount = 0;
        int feed_publisher_name = Arrays.asList(columns).indexOf("feed_publisher_name");
        int feed_publisher_url = Arrays.asList(columns).indexOf("feed_publisher_url");
        int feed_lang =  Arrays.asList(columns).indexOf("feed_lang");
        int feed_start_date =  Arrays.asList(columns).indexOf("feed_start_date");
        int feed_end_date =  Arrays.asList(columns).indexOf("feed_end_date");
        int feed_version = Arrays.asList(columns).indexOf("feed_version");

        for(String[] row : content) {
            String sub = _feedbaseuri + "#feed";
            // dct:identifier triple
            stmt.add(st( sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#Feed"));
            stmt.add(st( sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/ns/dcat#Dataset"));
            //this is a blank node: we're not sure what URI the publisher has, but we know this URI has a foaf:page and a foaf:name
            stmt.add(st( sub, "http://purl.org/dc/terms/publisher", "_:b" + _feedcount));
            if (feed_publisher_name != -1 && !ne(row[feed_publisher_name])) {
                stmt.add(st( "_:b" + _feedcount,"http://xmlns.com/foaf/0.1/name" , row[feed_publisher_name]));
            }
            if (feed_publisher_url != -1 && !ne(row[feed_publisher_url])) {
                stmt.add(st( "_:b" + _feedcount, "http://xmlns.com/foaf/0.1/page" , row[feed_publisher_url]));
            }
            if (feed_lang != -1 && !ne(row[feed_lang])) {
                //todo: change this into a URI
                stmt.add(st( sub, "http://purl.org/dc/terms/language", row[feed_lang]));
            }
            if (feed_start_date != -1 && feed_end_date != -1 && !ne(row[feed_start_date]) && !ne(row[feed_end_date])) {
                String temporal = _feedbaseuri + "/timespan";
                stmt.add(st( sub, "http://purl.org/dc/terms/temporal", temporal));
                stmt.add(st( temporal, "http://schema.org/startDate", moment(row[feed_start_date]) + "^^http://www.w3.org/2001/XMLSchema#date"));
                stmt.add(st( temporal, "http://schema.org/endDate", moment(row[feed_end_date]) + "^^http://www.w3.org/2001/XMLSchema#date"));
            }
            if (feed_version != -1 && !ne(row[feed_version])) {
                stmt.add(st( sub, "http://schema.org/version", row[feed_version]));
            } else {
                stmt.add(st( sub, "http://schema.org/version", "1"));
            }
            //has Distribution 1: the path towards the zip

            //TODO: use the URI of the download location, and not a hash
            String zipsubject = _feedbaseuri + "#zip";
            stmt.add(st( zipsubject , "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/ns/dcat#Distribution" ));
            stmt.add(st( zipsubject , "http://www.w3.org/ns/dcat#mediaType", "application/zip"));
            //TODO ...
            //has Distribution 2: the path towards the Linked Data Fragments startfragment
            String ldfsubject = "http://data.gtfs.org/triples/all";
            stmt.add(st( ldfsubject , "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/ns/dcat#Distribution" ));
            stmt.add(st( ldfsubject , "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://rdfs.org/ns/void#Dataset" ));

            _feedcount++;
        }
        return stmt;
    }


}
