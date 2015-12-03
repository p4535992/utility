package com.github.p4535992.util.gtfs.tordf;

import com.github.p4535992.util.file.ArchiveUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.gtfs.tordf.helper.TransformerPicker;
import com.github.p4535992.util.gtfs.tordf.transformer.Transformer;
import com.github.p4535992.util.gtfs.tordf.transformer.impl.AbstractTransformer;
import com.github.p4535992.util.repositoryRDF.jena.Jena2Kit;
import com.github.p4535992.util.string.StringUtilities;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 30/11/2015.
 * @author 4535992.
 * @version 2015-11-30.
 */
public class gtfsToRdf {

    /*private void die(String msg) {
        logger.info(msg);
        System.exit(0);
    }*/

    protected gtfsToRdf(){}

    private static gtfsToRdf instance = null;

    public static gtfsToRdf getInstance(){
        if(instance == null){
            instance = new gtfsToRdf();
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

    private Model prepareModel(File zipFile,String baseUri) throws IOException {
        Model jModel = Jena2Kit.createModel();
        jModel.setNsPrefixes(setPrefixes());
        for(List<Statement> list : mapper(zipFile,baseUri)){
            jModel.add(list);
        }
        return jModel;
    }

    public void convertGTFSZipToRDF(File zipFile,String baseUri,File fileOutput, String outputFormat) throws IOException {
        Model model = prepareModel(zipFile,baseUri);
        Jena2Kit.write(fileOutput,model,outputFormat);
    }


    public static void main(String[] args) throws IOException {
        File zip = new File("C:\\Users\\tenti\\Desktop\\ac-transit_20150218_1708.zip");

        File output = new File("C:\\Users\\tenti\\Desktop\\ac-transit_20150218_1708.n3");

        gtfsToRdf.getInstance().convertGTFSZipToRDF(zip,"http://baseuri#",output,"n-triples");
    }







}
