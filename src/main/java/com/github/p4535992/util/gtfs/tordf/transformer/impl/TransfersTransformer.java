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
public class TransfersTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);
        int from_stop_id = Arrays.asList(columns).indexOf("from_stop_id");
        int to_stop_id = Arrays.asList(columns).indexOf("to_stop_id");
        int transfer_type =  Arrays.asList(columns).indexOf("transfer_type");
        int min_transfer_time = Arrays.asList(columns).indexOf("min_transfer_time");

        for(String[] row : content) {
            if (from_stop_id != -1 && to_stop_id != -1 && !ne(row[from_stop_id]) && !ne(row[to_stop_id])) {
                String sub = _feedbaseuri + "/transferrules/" + row[from_stop_id] + "-" + row[to_stop_id];
                stmt.add(st(sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#TransferRule"));
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#originStop",_feedbaseuri + "/stops/" + row[from_stop_id]));
                stmt.add(st(sub, "http://vocab.gtfs.org/terms#destinationStop", _feedbaseuri + "/stops/" + row[to_stop_id]));
                if (transfer_type != -1 && !ne(row[transfer_type])) {
                    if (Objects.equals(row[transfer_type], "0")) {
                        stmt.add(st(sub, "http://vocab.gtfs.org/terms#transferType", "http://vocab.gtfs.org/terms#Recommended"));
                    } else if (Objects.equals(row[transfer_type], "1")) {
                        stmt.add(st(sub, "http://vocab.gtfs.org/terms#transferType", "http://vocab.gtfs.org/terms#EnsuredTransfer"));
                    } else if (Objects.equals(row[transfer_type], "2")) {
                        stmt.add(st(sub, "http://vocab.gtfs.org/terms#transferType", "http://vocab.gtfs.org/terms#MinimumTimeTransfer"));
                    } else if (Objects.equals(row[transfer_type], "3")) {
                        stmt.add(st(sub, "http://vocab.gtfs.org/terms#transferType", "http://vocab.gtfs.org/terms#NoTransfer"));
                    }
                } else {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#transferType", "http://vocab.gtfs.org/terms#Recommended"));
                }
                if (min_transfer_time != -1 && !ne(row[min_transfer_time])) {
                    stmt.add(st(sub, "http://vocab.gtfs.org/terms#minimumTransferTime", row[min_transfer_time] + "^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
                }
            } else {
                //("Transfer does not contain all required field. Data follows:");
                continue;
            }

        }
        return stmt;
    }

}
