package com.github.p4535992.util.gtfs.tordf.transformer;

import org.apache.jena.rdf.model.Statement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by 4535992 on 30/11/2015.
 */
public interface Transformer{

    List<Statement> _Transform(File data, Charset encoding, String _feedbaseuri);

}
