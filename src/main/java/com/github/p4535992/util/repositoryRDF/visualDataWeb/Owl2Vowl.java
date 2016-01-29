package com.github.p4535992.util.repositoryRDF.visualDataWeb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 11/01/2016.
 * @version 2016-01-11.
 */
public class Owl2Vowl {

    private static Owl2Vowl instance;

    public static Owl2Vowl getInstance() {
        if(instance == null){
            instance  = new Owl2Vowl();
        }
        return instance;
    }

    private Owl2Vowl() {}

    /**
     * Convert an ontology from a local file
     * e.g.  java -jar owl2vowl.jar -file path/to/local/ontology
     * @param file the {@link File} Ontology to convert.
     */
    public static void convertOntologyByFile(File file){
        String[] arrayarams = new String[]{"-file", file.getAbsolutePath()};
        de.uni_stuttgart.vis.vowl.owl2vowl.Main.main(arrayarams);
    }

    /**
     * Convert an ontology with dependencies from a local file.
     * e.g. java -jar owl2vowl.jar -file path/to/local/ontology
     * -dependencies path/to/dependency1 path/to/dependency2 ...
     * @param ontologyFile the {@link File} Ontology to convert.
     * @param dependencies the {@link List} of ontologies with some dependency on the ontologyFile.
     */
    public static void convertOntologyByFile(File ontologyFile, List<File> dependencies){
        List<String> list = new ArrayList<>();
        list.add("-file");
        list.add(ontologyFile.getAbsolutePath());
        list.add("-dependencies");
        for(File file : dependencies) {
        //dependencies.stream().forEach((file) -> {
                list.add(file.getAbsolutePath());
        //});
        }
        String[] arrayarams = list.toArray(new String[list.size()]);
        de.uni_stuttgart.vis.vowl.owl2vowl.Main.main(arrayarams);
    }
    /**
     * Convert an ontology by its IRI.
     * e.g. java -jar owl2vowl.jar -iri "http://ontovibe.visualdataweb.org"
     * @param iri the {@link String} IRI of the Ontology to convert.
     */
    public static void convertOntologyByIRI(String iri){
        String[] arrayarams = new String[]{"-iri",iri};
        de.uni_stuttgart.vis.vowl.owl2vowl.Main.main(arrayarams);
    }

    public static void main(String[] args) {
        //LogBackUtil.console();
        File fileOwl = new File("C:\\Users\\tenti\\Desktop\\km4c Virtuoso 1.6.2.owl");
        convertOntologyByFile(fileOwl);

    }


}
