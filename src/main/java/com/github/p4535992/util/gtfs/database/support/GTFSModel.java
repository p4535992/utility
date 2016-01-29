package com.github.p4535992.util.gtfs.database.support;

import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import org.onebusaway.gtfs.model.Agency;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 4535992 on 28/01/2016.
 */
public class GTFSModel {

    GTFSModel(){}

    private static Map<String,String> map = new HashMap<>();

    public static Map<String,String> getAgencyModel(){
        map = new HashMap<>();
        map.put("agency_id", "VARCHAR(255) NOT NULL PRIMARY KEY");
        map.put("agency_name", "VARCHAR(255)");
        map.put("agency_url", "VARCHAR(255)");
        map.put("agency_timezone", "VARCHAR(50)");
        map.put("agency_phone", "VARCHAR(255)");
        map.put("agency_lang", "VARCHAR(50)");
        return map;
    }

    public static String prepareColumn(File csv, String id){
        String[] columns = OpenCsvUtilities.getHeadersWithUnivocity(csv,true);
        String[] newColumns = new String[columns.length];
        if(Objects.equals(id, "agency")){
            for(int i=0; i < columns.length; i++){
                newColumns[i] = columns[i] + " "+ map.get(columns[i]);
            }
        }
        return ArrayUtilities.toString(newColumns,',');
    }

}
