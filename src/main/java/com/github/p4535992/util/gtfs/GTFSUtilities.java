package com.github.p4535992.util.gtfs;

import com.github.p4535992.util.database.sql.SQLUtilities;
import com.github.p4535992.util.file.archive.ArchiveUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.gtfs.database.support.GTFSModel;
import com.github.p4535992.util.gtfs.tordf.helper.TransformerPicker;
import com.github.p4535992.util.gtfs.tordf.transformer.Transformer;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.github.p4535992.util.repositoryRDF.jena.Jena3Utilities;
import com.github.p4535992.util.string.StringUtilities;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 * @version 2015-11-30.
 */
public class GTFSUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GTFSUtilities.class);

    protected GTFSUtilities(){}

    private static GTFSUtilities instance = null;

    public static GTFSUtilities getInstance(){
        if(instance == null){
            instance = new GTFSUtilities();
        }
        return instance;
    }

    private Map<String,String> setPrefixes(){
        Map<String,String>  map = new HashMap<>();
        map.put("gtfs","http://vocab.gtfs.org/terms#");
        map.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        map.put("foaf","http://xmlns.com/foaf/0.1/");
        map.put("dct","http://purl.org/dc/terms/");
        map.put("rdfs","http://www.w3.org/2000/01/rdf-schema#");
        map.put("owl","http://www.w3.org/2002/07/owl#");
        map.put("xsd","http://www.w3.org/2001/XMLSchema#");
        map.put("vann","http://purl.org/vocab/vann/");
        map.put("skos","http://www.w3.org/2004/02/skos/core#");
        map.put("schema","http://schema.org/");
        map.put("dcat","http://www.w3.org/ns/dcat#");
        return map;
    }

    private List<List<Statement>> mapper(File zipFile, String baseUri) throws IOException {
        List<File> files = ArchiveUtilities.unzip(zipFile,FileUtilities.getDirectoryFullPath(zipFile));
        List<List<Statement>> listStmt = new ArrayList<>();
        for(File file : files){
            String nameFile = FileUtilities.getFilenameWithoutExt(file.getAbsolutePath());
            Transformer specificTransformer = TransformerPicker.getInstance(nameFile).getTransformer();
            List<Statement> stmts = specificTransformer._Transform(file, StringUtilities.UTF_8, baseUri);
            listStmt.add(stmts);
        }
        return listStmt;
    }

    private Model prepareModel(File zipFile, String baseUri) throws IOException {
        Model jModel = Jena3Utilities.createModel();
        jModel.setNsPrefixes(setPrefixes());
        for(List<Statement> list : mapper(zipFile,baseUri)){
            jModel.add(list);
        }
        return jModel;
    }

    public void convertGTFSZipToRDF(File zipFile,String baseUri,File fileOutput, String outputFormat) throws IOException {
        Model model = prepareModel(zipFile,baseUri);
        Jena3Utilities.write(fileOutput,model,outputFormat);
    }

    /**
     * href:https://github.com/SparkandShine/data_analysis/blob/master/traces/tisseo_toulouse_gtfs/load.sql
     * @param zipFile the {@link File} to the Zip File.
     * @param conn the {@link Connection} to the SQL Database.
     * @param database the {@link String} name of the dataabase/schema.
     * @param createDatabase the {@link Boolean} is true if drop and create the database.
     * @param createTables the {@link Boolean} is true if drop and create the tables.
     * @return the {@link Boolean} is true if all the operations are done.
     */
    public Boolean importGTFSZipToDatabase(File zipFile,Connection conn,String database,boolean createDatabase,boolean createTables){
        try {
            if(createDatabase){
                //-- CREATE DATABASE IF NOT EXISTS gtfs;
                SQLUtilities.executeSQL(
                        "DROP DATABASE IF EXISTS "+database+"; \n" +
                        "CREATE DATABASE "+database+" \n" +
                        "DEFAULT CHARACTER SET utf8 \n" +
                        "DEFAULT COLLATE utf8_general_ci;",
                        conn
                );
            }
            //Map<String,String[]> map = SQLUtilities.getTableAndColumn(conn,database);
            Map<String,File> files2 = getGTFSFilesFromZipFile(zipFile);
            if (createTables) {
                SQLUtilities.executeSQL(
                        "USE gtfs;\n" +
                        "DROP TABLE IF EXISTS agency;\n" +
                        //"-- agency_id,agency_name,agency_url,agency_timezone,agency_phone,agency_lang\n" +
                        "CREATE TABLE `agency` (\n" +
                                GTFSModel.prepareColumn(files2.get("agency"),"agency")+

                        "\n" +
                        "DROP TABLE IF EXISTS shapes;\n" +
                        //"-- shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence\n" +
                        "CREATE TABLE `shapes` (\n" +
                        "shape_id VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                        "shape_pt_lat DECIMAL(8,6),\n" +
                        "shape_pt_lon DECIMAL(8,6),\n" +
                        "shape_pt_sequence VARCHAR(255)\n" +
                        ");\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS calendar;\n" +
                        //"-- service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\n" +
                        "CREATE TABLE `calendar` (\n" +
                        "    service_id VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                        "monday TINYINT(1),\n" +
                        "tuesday TINYINT(1),\n" +
                        "wednesday TINYINT(1),\n" +
                        "thursday TINYINT(1),\n" +
                        "friday TINYINT(1),\n" +
                        "saturday TINYINT(1),\n" +
                        "sunday TINYINT(1),\n" +
                        "start_date VARCHAR(8),\n" +
                        "end_date VARCHAR(8)\n" +
                        ");\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS calendar_dates;\n" +
                        //"-- service_id,date,exception_type\n" +
                        "CREATE TABLE `calendar_dates` (\n" +
                        "    service_id VARCHAR(255),\n" +
                        "    `date` VARCHAR(8),\n" +
                        "    exception_type INT(2),\n" +
                        "    FOREIGN KEY (service_id) REFERENCES calendar(service_id),\n" +
                        "    KEY `exception_type` (exception_type)    \n" +
                        ");\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS routes;\n" +
                        //"-- route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color\n" +
                        "CREATE TABLE `routes` (\n" +
                        "    route_id VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                        "agency_id VARCHAR(255),\n" +
                        "route_short_name VARCHAR(50),\n" +
                        "route_long_name VARCHAR(255),\n" +
                        "route_desc VARCHAR(255),\n" +
                        "route_type INT(2),\n" +
                        "route_url VARCHAR(255),\n" +
                        "route_color VARCHAR(20),\n" +
                        "route_text_color VARCHAR(20),\n" +
                        "FOREIGN KEY (agency_id) REFERENCES agency(agency_id),\n" +
                        "KEY `agency_id` (agency_id),\n" +
                        "KEY `route_type` (route_type)\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS trips;\n" +
                        //"-- trip_id,service_id,route_id,trip_headsign,direction_id,shape_id\n" +
                        "CREATE TABLE `trips` (\n" +
                        "trip_id VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                        "service_id VARCHAR(255),\n" +
                        "route_id VARCHAR(255),\n" +
                        "trip_headsign VARCHAR(255),\n" +
                        "direction_id TINYINT(1),\n" +
                        "shape_id VARCHAR(255),\n" +
                        "FOREIGN KEY (service_id) REFERENCES calendar(service_id),\n" +
                        "FOREIGN KEY (shape_id) REFERENCES shapes(shape_id),\n" +
                        "KEY `route_id` (route_id),\n" +
                        "KEY `service_id` (service_id),\n" +
                        "KEY `direction_id` (direction_id)\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS stops;\n" +
                        //"-- stop_id,stop_code,stop_name,stop_lat,stop_lon,location_type,parent_station,wheelchair_boarding\n" +
                        "CREATE TABLE `stops` (\n" +
                        "    stop_id VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                        "stop_code VARCHAR(255),\n" +
                        "stop_name VARCHAR(255),\n" +
                        "stop_lat DECIMAL(8,6),\n" +
                        "stop_lon DECIMAL(8,6),\n" +
                        "location_type INT(2),\n" +
                        "parent_station VARCHAR(255),\n" +
                        "wheelchair_boarding INT(2),\n" +
                        "stop_desc VARCHAR(255),\n" +
                        "zone_id VARCHAR(255)\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS stop_times;\n" +
                        //"-- trip_id,stop_id,stop_sequence,arrival_time,departure_time,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled\n" +
                        "CREATE TABLE `stop_times` (\n" +
                        "    trip_id VARCHAR(255),\n" +
                        "stop_id VARCHAR(255),\n" +
                        "stop_sequence VARCHAR(255),\n" +
                        "arrival_time VARCHAR(8),\n" +
                        "departure_time VARCHAR(8),\n" +
                        "stop_headsign VARCHAR(8),\n" +
                        "pickup_type INT(2),\n" +
                        "drop_off_type INT(2),\n" +
                        "shape_dist_traveled VARCHAR(8),\n" +
                        "FOREIGN KEY (trip_id) REFERENCES trips(trip_id),\n" +
                        "FOREIGN KEY (stop_id) REFERENCES stops(stop_id),\n" +
                        "KEY `trip_id` (trip_id),\n" +
                        "KEY `stop_id` (stop_id),\n" +
                        "KEY `stop_sequence` (stop_sequence),\n" +
                        "KEY `pickup_type` (pickup_type),\n" +
                        "KEY `drop_off_type` (drop_off_type)\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "DROP TABLE IF EXISTS frequencies;\n" +
                        "\n" +
                        //"-- trip_id,start_time,end_time,headway_secs\n" +
                        "CREATE TABLE `frequencies` (\n" +
                        "trip_id VARCHAR(255),\n" +
                        "start_time VARCHAR(50),\n" +
                        "end_time VARCHAR(50),\n" +
                        "headway_secs VARCHAR(50),\n" +
                        "FOREIGN KEY (trip_id) REFERENCES trips(trip_id)\n" +
                        ");", conn
                );
            }
            //load data
            /*List<File> files = ArchiveUtilities.extractFilesFromZipFile(zipFile);
            for(File file :files){
                if(FileUtilities.isFileExists(file)) {
                    //String nameTable = database+"."+FileUtilities.getFilenameWithoutExt(file);
                    String nameTable = FileUtilities.getFilenameWithoutExt(file);
                    String path = file.getAbsolutePath();
                    path = path.replace("\\","\\\\");
                    file = new File(path);
                  *//*  SQLUtilities.executeSQL(
                            "LOAD DATA LOCAL INFILE '" + path +
                                    "' INTO TABLE " + nameTable +
                                    " FIELDS TERMINATED BY ',' IGNORE 1 LINES;", conn);*//*
                    SQLUtilities.importData(conn,file,database,nameTable);
                }
            }*/
            for(Map.Entry<String,File> entry : files2.entrySet()){
                SQLUtilities.importData(conn,entry.getValue(),database,entry.getKey());
            }
            return  true;
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public Boolean exportGTFSZipFromDatabase(Connection conn,String database,String tableName,File file){
        return SQLUtilities.exportData(conn,file.getAbsolutePath(),database+"."+tableName);
    }

    public static Map<String,File> getGTFSFilesFromZipFile(File zipFile){
        Map<String,File> map = new HashMap<>();
        List<File> files = ArchiveUtilities.extractFilesFromZipFile(zipFile);
        for (File file : files) {
            if (FileUtilities.isFileExists(file)) {
                //String nameTable = database+"."+FileUtilities.getFilenameWithoutExt(file);
                String nameTable = FileUtilities.getFilenameWithoutExt(file);
                String path = file.getAbsolutePath();
                path = path.replace("\\", "\\\\");
                map.put(nameTable, new File(path));
            }
        }
        return map;
    }

    public enum GTFSFile{agency,shapes,calendar,calendar_dates,routes,stops,trips,frequencies,stop_times}

    public static void main(String[] args) throws IOException, SQLException {
        LogBackUtil.console();

        File zip = new File("C:\\Users\\tenti\\Desktop\\ac-transit_20150218_1708.zip");

        Connection conn = SQLUtilities.getMySqlConnection("localhost","3306","gtfs","siimobility","siimobility");

        GTFSUtilities.getInstance().importGTFSZipToDatabase(zip,conn,"gtfs",true,true);

        List<String> list  = SQLUtilities.getTablesFromConnection(conn,"gtfs");
        for(String s : list){
             GTFSUtilities.getInstance().exportGTFSZipFromDatabase(conn,"gtfs",s,new File(s+".txt"));
        }




        File output = new File("C:\\Users\\tenti\\Desktop\\ac-transit_20150218_1708.n3");

        GTFSUtilities.getInstance().convertGTFSZipToRDF(zip,"http://baseuri#",output,"n-triples");
    }






}
