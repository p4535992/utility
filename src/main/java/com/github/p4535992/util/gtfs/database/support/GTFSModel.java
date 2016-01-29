package com.github.p4535992.util.gtfs.database.support;

import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.collection.SetUtilities;
import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.gtfs.GTFSUtilities;
import org.onebusaway.gtfs.model.Agency;

import java.io.File;
import java.util.*;

/**
 * Created by 4535992 on 28/01/2016.
 */
public class GTFSModel {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( GTFSModel.class);

    GTFSModel(){}

    private static Map<String,String> map = new HashMap<>();

    public static Map<String,String> getAgencyModel(){
        map = new HashMap<>();
        map.put("agency_id","agency_id VARCHAR(255) NOT NULL PRIMARY KEY");
        map.put("agency_name","agency_name VARCHAR(255)");
        map.put("agency_url","agency_url VARCHAR(255)");
        map.put("agency_timezone","agency_timezone VARCHAR(50)");
        map.put("agency_phone","agency_phone VARCHAR(255)");
        map.put("agency_lang","agency_lang VARCHAR(50)");
        return map;
    }

    public static Map<String,String> getShapesModel(){
        map = new HashMap<>();
        map.put("shape_id","shape_id VARCHAR(255) NOT NULL PRIMARY KEY");
        //map.put("shape_pt_lat","shape_pt_lat DECIMAL(8,6)");
        //map.put("shape_pt_lon","shape_pt_lon DECIMAL(8,6)");
        map.put("shape_pt_lat","shape_pt_lat VARCHAR(255)");
        map.put("shape_pt_lon","shape_pt_lon VARCHAR(255)");
        map.put("shape_pt_sequence","shape_pt_sequence VARCHAR(255)");
        return map;
    }

    public static Map<String,String> geCalendarModel(){
        map = new HashMap<>();
        map.put("service_id","service_id VARCHAR(255) NOT NULL PRIMARY KEY");
        map.put("monday","monday TINYINT(1)");
        map.put("tuesday","tuesday TINYINT(1)");
        map.put("wednesday","wednesday TINYINT(1)");
        map.put("thursday","thursday TINYINT(1)");
        map.put("friday","friday TINYINT(1)");
        map.put("saturday","saturday TINYINT(1)");
        map.put("sunday","sunday TINYINT(1)");
        map.put("start_date","start_date VARCHAR(8)");
        map.put("end_date","end_date VARCHAR(8)");
        return map;
    }

    public static Map<String,String> getCalendarDatesModel(){
        map = new HashMap<>();
        map.put("service_id", "service_id VARCHAR(255)");
        map.put("date", "`date` VARCHAR(8)");
        map.put("exception_type", "exception_type INT(2)");
        map.put("other", "FOREIGN KEY (service_id) REFERENCES calendar(service_id), " +
                " KEY `exception_type` (exception_type)");
        // FOREIGN KEY (service_id) REFERENCES calendar(service_id), KEY `exception_type` (exception_type)
        return map;
    }

    public static Map<String,String> getRoutesModel(){
        map = new HashMap<>();
        map.put("route_id", "route_id VARCHAR(255) NOT NULL PRIMARY KEY");
        map.put("agency_id", "agency_id VARCHAR(255)");
        map.put("route_short_name", "route_short_name VARCHAR(50)");
        map.put("route_long_name", "route_long_name VARCHAR(255)");
        map.put("route_desc", "route_desc VARCHAR(255)");
        map.put("route_type", "route_type INT(2)");
        map.put("route_url", "route_url VARCHAR(255)");
        map.put("route_color", "route_color VARCHAR(20)");
        map.put("route_text_color", "route_text_color VARCHAR(20)");
        map.put("other",
                "FOREIGN KEY (agency_id) REFERENCES agency(agency_id), " +
                " KEY `agency_id` (agency_id), " +
                " KEY `route_type` (route_type)");
        return map;
    }

    public static Map<String,String> getTripsModel(){
        map = new HashMap<>();
        map.put("trip_id", "trip_id VARCHAR(255) NOT NULL PRIMARY KEY");
        map.put("service_id", "service_id VARCHAR(255)");
        map.put("route_id", "route_id VARCHAR(255)");
        map.put("trip_headsign", "trip_headsign VARCHAR(255)");
        //map.put("direction_id", "direction_id TINYINT(1)");
        map.put("direction_id", "direction_id VARCHAR(5)");
        map.put("block_id","block_id VARCHAR(255)");
        map.put("shape_id", "shape_id VARCHAR(255)");
        map.put("other",
                "FOREIGN KEY (service_id) REFERENCES calendar(service_id), " +
                        "FOREIGN KEY (shape_id) REFERENCES shapes(shape_id), " +
                        " KEY `route_id` (route_id), " +
                        " KEY `service_id` (service_id), " +
                        " KEY `direction_id` (direction_id)");
        return map;
    }

    public static Map<String,String> getStopsModel(){
        map = new HashMap<>();
        map.put("stop_id", "stop_id VARCHAR(255) NOT NULL PRIMARY KEY");
        map.put("stop_code", "stop_code VARCHAR(255)");
        map.put("stop_name", "stop_name VARCHAR(255)");
        //map.put("stop_lat", "stop_lat DECIMAL(8,6)");
        //map.put("stop_lon", "stop_lon DECIMAL(8,6)");
        map.put("stop_lat", "stop_lat VARCHAR(255)");
        map.put("stop_lon", "stop_lon VARCHAR(255)");
        //map.put("location_type", "location_type INT(2)");
        map.put("location_type", "location_type VARCHAR(2)");
        map.put("parent_station", "parent_station VARCHAR(255)");
        //map.put("wheelchair_boarding", "wheelchair_boarding INT(2)");
        map.put("wheelchair_boarding", "wheelchair_boarding VARCHAR(2)");
        map.put("stop_desc", "stop_desc VARCHAR(255)");
        map.put("zone_id", "zone_id VARCHAR(255)");
        return map;
    }

    public static Map<String,String> getStopTimesModel(){
        map = new HashMap<>();
        map.put("trip_id","trip_id VARCHAR(255)");
        map.put("stop_id","stop_id VARCHAR(255)");
        map.put("stop_sequence","stop_sequence VARCHAR(255)");
        map.put("arrival_time","arrival_time VARCHAR(8)");
        map.put("departure_time","departure_time VARCHAR(8)");
        map.put("stop_headsign","stop_headsign VARCHAR(8)");
        //map.put("pickup_type","pickup_type INT(2) DEFAULT NULL");
        //map.put("drop_off_type","drop_off_type INT(2) DEFAULT NULL");
        map.put("pickup_type","pickup_type VARCHAR(2) DEFAULT NULL");
        map.put("drop_off_type","drop_off_type VARCHAR(2) DEFAULT NULL");
        map.put("shape_dist_traveled","shape_dist_traveled VARCHAR(8)");
        map.put("other","FOREIGN KEY (trip_id) REFERENCES trips(trip_id), " +
                "FOREIGN KEY (stop_id) REFERENCES stops(stop_id), " +
                "KEY `trip_id` (trip_id), " +
                "KEY `stop_id` (stop_id), " +
                "KEY `stop_sequence` (stop_sequence), " +
                "KEY `pickup_type` (pickup_type), " +
                "KEY `drop_off_type` (drop_off_type)");
        return map;
    }

    public static Map<String,String> getFrequenciesModel() {
        map = new HashMap<>();
        map.put("trip_id", "trip_id VARCHAR(255)");
        map.put("start_time", "start_time VARCHAR(50)");
        map.put("end_time", "end_time VARCHAR(50)");
        map.put("headway_secs", "headway_secs VARCHAR(50)");
        map.put("other", "FOREIGN KEY (trip_id) REFERENCES trips(trip_id)");
        return map;
    }

    public static String prepareColumn(File csv, GTFSUtilities.GTFSFileType gtfsFile){

        switch(gtfsFile){
            case agency: getAgencyModel(); break;
            case shapes: getShapesModel(); break;
            case calendar: geCalendarModel(); break;
            case calendar_dates: getCalendarDatesModel(); break;
            case routes: getRoutesModel(); break;
            case trips: getTripsModel(); break;
            case stops: getStopsModel(); break;
            case stop_times: getStopTimesModel(); break;
            case frequencies: getFrequenciesModel(); break;
            default: logger.error("The file:"+gtfsFile.name()+" not exists in GTFS structure");
        }
        String[] columns;
        if(csv != null){
           columns = OpenCsvUtilities.getHeadersWithUnivocity(csv,true);
        }else{
            columns = SetUtilities.toArray(map.keySet());
        }
        List<String> newColumns = new ArrayList<>();
        for (String column : columns) {
            newColumns.add(map.get(column));
        }
        if(map.get("other")!=null) newColumns.add(map.get("other"));
        return ArrayUtilities.toString(newColumns.toArray(),',');
    }

}
