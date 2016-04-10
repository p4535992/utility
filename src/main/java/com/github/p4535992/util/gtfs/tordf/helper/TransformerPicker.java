package com.github.p4535992.util.gtfs.tordf.helper;

import com.github.p4535992.util.gtfs.tordf.transformer.Transformer;
import com.github.p4535992.util.gtfs.tordf.transformer.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 */
public class TransformerPicker {

    private Transformer transformer = null;

    protected TransformerPicker(){}

    /*protected TransformerPicker(String fileName,String baseUri,String version){
        for(Map.Entry<String,Transformer> gtfsFileName : gtfsFilenames().entrySet()){
            if (Objects.equals(gtfsFileName.getKey(), fileName.toLowerCase())){
                this.transformer = gtfsFileName.getValue();
                break;
            }
        }
    }*/

    protected TransformerPicker(String fileName){
        for(Map.Entry<String,Transformer> gtfsFileName : gtfsFilenames().entrySet()){
            if (Objects.equals(gtfsFileName.getKey(), fileName.toLowerCase())){
                this.transformer = gtfsFileName.getValue();
                break;
            }
        }
    }

    private static TransformerPicker instance = null;

    public static TransformerPicker getInstance(){
        if(instance == null) {
            instance = new TransformerPicker();
        }
        return instance;
    }

    public static TransformerPicker getNewInstance(){
        instance = new TransformerPicker();
        return instance;
    }

    public static TransformerPicker getInstance(String fileName){
        if(instance == null) {
            instance = new TransformerPicker(fileName);
        }
        return instance;
    }

    public static TransformerPicker getNewInstance(String fileName){
        instance = new TransformerPicker(fileName);
        return instance;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    private Map<String,Transformer> gtfsFilenames(){
        Map<String,Transformer> map = new HashMap<>();
        map.put("stops", new StopsTransformer());
        map.put("agency", new AgenciesTransformer());
        map.put("routes", new RoutesTransformer());
        map.put("trips",new TripsTransformer());
        map.put("stop_times",new StopTimesTransformer());
        map.put("calendar",new CalendarTransformer());
        map.put("calendar_dates",new CalendarDatesTransformer());
        map.put("feed_info", new FeedInfoTransformer());
        map.put("fare_attributes", new FareAttributesTransformer());
        map.put("fare_rules",new FareRulesTransformer());
        map.put("shapes", new ShapesTransformer());
        map.put("frequencies",new FrequenciesTransformer());
        map.put("transfers",new TransfersTransformer());
        return map;

    }

}
