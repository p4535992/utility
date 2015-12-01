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
public class FareAttributesTransformer extends AbstractTransformer {

    @Override
    public List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri) {
        List<Statement> stmt = new ArrayList<>();

        List<String[]> content = CSVGetContent(data, true);
        String[] columns = CSVGetHeaders(data, true);

        int fare_id = Arrays.asList(columns).indexOf("fare_id");
        int price =  Arrays.asList(columns).indexOf("price");
        int currency_time =  Arrays.asList(columns).indexOf("currency_time");
        int currency_type =  Arrays.asList(columns).indexOf("currency_type");
        int payment_method = Arrays.asList(columns).indexOf("payment_method");
        int transfers =  Arrays.asList(columns).indexOf("transfers");
        int transfer_duration =  Arrays.asList(columns).indexOf("transfer_duration");

        for(String[] row : content) {
            String sub = _feedbaseuri + "/fareclasses/" + row[fare_id];
            stmt.add(st( sub, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://vocab.gtfs.org/terms#FareClass"));

            if (price != -1 && !ne(row[price])) {
                stmt.add(st( sub, "http://schema.org/price", row[price] ));
            }

            //TODO check if is correct...
            if (currency_time != -1 && !ne(row[currency_time])) {
                stmt.add(st( sub, "http://schema.org/priceCurrency", row[currency_type] ));
            }

            if (payment_method != -1 && !ne(row[payment_method])) {
                if (Objects.equals(row[payment_method], "1")) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#paymentMethod", "http://vocab.gtfs.org/terms#BeforeBoarding" ));
                } else {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#paymentMethod", "http://vocab.gtfs.org/terms#OnBoard"));
                }
            }

            if (transfers != -1 && !ne(row[transfers])) {
                if (Objects.equals(row[payment_method], "0")) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#transfers", "http://vocab.gtfs.org/terms#NoTransfersAllowed"));
                } else if (Objects.equals(row[payment_method], "1")) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#transfers", "http://vocab.gtfs.org/terms#OneTransfersAllowed"));
                } else if (Objects.equals(row[payment_method], "2")) {
                    stmt.add(st( sub, "http://vocab.gtfs.org/terms#transfers", "http://vocab.gtfs.org/terms#TwoTransfersAllowed"));
                }
            } else {
                stmt.add(st( sub, "http://vocab.gtfs.org/terms#transfers", "http://vocab.gtfs.org/terms#UnlimitedTransfersAllowed"));
            }

            if (transfer_duration != -1 && !ne(row[transfer_duration])) {
                stmt.add(st( sub, "http://vocab.gtfs.org/terms#transferExpiryTime", row[transfer_duration] + "^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
            }
        }
        return stmt;
    }
}
