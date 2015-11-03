package com.github.p4535992.util.repositoryRDF;

import com.github.p4535992.util.repositoryRDF.cumulusrdf.CumulusRDFKit;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame28Kit;
import com.hp.hpl.jena.graph.Triple;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.query.Operation;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * Created by 4535992 on 27/10/2015.
 */
public class RepositoryRDFUtilities {

    private static Sesame28Kit s;

    protected RepositoryRDFUtilities() {}

    private static RepositoryRDFUtilities instance = null;

    public static RepositoryRDFUtilities getInstance(){
        if(instance == null) {
            instance = new RepositoryRDFUtilities();
            //help with very large repository....
            System.setProperty("entityExpansionLimit", "1000000");
            RepositoryRDFUtilities.s = Sesame28Kit.getInstance();
        }
        return instance;
    }

    public Repository connectToSesameHTTP(String url) {
        return s.connectToHTTPRepository(url);
    }

    public Repository connectToSesameHTTP(String sesameServer,String repositoryId) {
        return s.connectToHTTPRepository(sesameServer,repositoryId);
    }

    public Repository connectToSesameHTTP(String url,String user,String password) {
        return s.connectToHTTPRepository(url,user,password);
    }

    public Repository connectToSesameInferencing(File fileRepositoryId) {
        return s.connectToInferencingRepository(fileRepositoryId);
    }

    public Repository connectToSesameInferencing(File directory,String nameRepositoryId) {
        return s.connectToInferencingRepository(directory, nameRepositoryId);
    }

    public Repository connectToSesameInferencing(String directory,String nameRepositoryId) {
        return s.connectToInferencingRepository(directory, nameRepositoryId);
    }

    public Repository connectToSesameMemory(boolean inferencing) {
        return s.connectToMemoryRepository(inferencing);
    }

    public Repository connectToSesameMemory(String directory,String nameRepositoryId) {
        return s.connectToMemoryRepository(directory,nameRepositoryId);
    }

    public Repository connectToSesameLocal(String typeRepository,String directory,String nameRepositoryId) {
        return s.connectToLocal(typeRepository,directory,nameRepositoryId);
    }

    public Repository connectToSesameRemote(String typeRepository,String serverUrl,String nameRepositoryId) {
        return s.connectToRemote(typeRepository,serverUrl,nameRepositoryId);
    }

    public Repository connectToSesameNative(String directory,String indexes) {
        return s.connectToNativeRepository(directory,indexes);
    }

    public Repository connectToSesameNative(File directory,String indexes) {
        return s.connectToNativeRepository(directory,indexes);
    }

    public Repository connectToSesameNative(File directory,String indexes,boolean inferencing) {
        return s.connectToNativeRepository(directory,indexes,inferencing);
    }

    public Repository connectToSesameConfig(String reposiotryId,String username,String password) {
        return s.connectToLocalWithConfigFile(reposiotryId, username, password);
    }

    public RepositoryManager connectToSesameLocal(File directory) {
        return s.connectToLocation(directory);
    }

    public RepositoryManager connectToSesameLocal(String directory) {
        return s.connectToLocation(directory);
    }

    public RepositoryManager connectToSesameLocal(URL directory) {
        return s.connectToLocation(directory);
    }

    public void clearSesameRepository() {
        s.clearRepository();
    }

    public void closeSesameRepository() {
        s.closeRepository();
    }

    public void closeSesameRepository(String filePath) {
        s.convertFileNameToRDFFormat(filePath);
    }

    public void convertTo(String filePath,String inputFormatName,String outputFormatName) {
        s.convertFileNameToRDFFormat(filePath, inputFormatName, outputFormatName);
    }

    public void convertTo(String filePath) {
        s.convertFileNameToRDFFormat(filePath);
    }

    public QueryLanguage toLanguage(String queryString) {
        return s.checkLanguageOfQuery(queryString);
    }

    public Operation toOperation(Query queryString) {
        return s.convertQueryToOperation(queryString);
    }

    public Operation toOperation(String queryString) {
        return s.convertQueryToOperation(queryString);
    }

    public void addToSesame(Triple triple, String context) {
        s.addJenaTripleToSesameRepository(triple, s.createResource(context));
    }

    public List<com.hp.hpl.jena.graph.Triple> findToSesame(Triple triple, String context){
        return s.findJenaTripleFromSesameRepository(triple, s.createResource(context));
    }

    public void removeFromSesame(Triple triple, String context){
        s.removeJenaTripleFromSesameRepository(triple, s.createResource(context));
    }

    public Set<String> getSesameRepositories(){
        return s.getRepositories();
    }

    public Model toSesameModel(Repository repository,String queryGraph){
       return s.convertGraphQueryEvalutationToSesameModel(repository,queryGraph);
    }

    public Model toSesameModel(Repository repository){
        return s.convertRepositoryToModel(repository);
    }

    public Model toSesameModel(Repository repository,int limit){
        return s.convertRepositoryToModel(repository,limit);
    }

    public List<String[]> toSesameResult(String queryString,String[] bindingName){
        return s.TupleQueryEvalutation(queryString, bindingName);
    }

    public List<Statement> toSesameResult(String queryString){
        return s.GraphQueryEvalutation(queryString);
    }

    public void disconnectSesameRepository(){
        s.disconnect();
    }

    public void deleteSesameRepository(String nameRepository){
        s.deleteRepository(nameRepository);
    }

    public boolean execAskOnSesame(String query){ return s.execSparqlAskOnRepository(query);}

    public void execUpdateOnSesame(String query){ s.execSparqlUpdateOnRepository(query);}

    public void execQueryOnSesame(String query){ s.executeQuerySPARQLFromString(query);}

    public void execQueryOnSesame(File fileQueries){s.executeQuerySPARQLFromFile(fileQueries);}

    public RepositoryConnection openSesameRepository(String repositorId){ return s.openRepository(repositorId);}

    public File exportFromSesame(String outputPathFile,String outputFormat,String exportType){
       return s.export(outputPathFile,outputFormat,exportType);
    }

    public void importToSesame(File fileOrDirectory){ s.importIntoRepository(fileOrDirectory);}

    public void importToSesame(File filePath, String baseURI, String inputFormat){
        s.importIntoRepository(filePath,baseURI,inputFormat);
    }

    public void importToSesame(InputStream filePath, String baseURI, String inputFormat){
        s.importIntoRepository(filePath,baseURI,inputFormat);
    }

    public void importToSesame(Reader filePath, String baseURI, String inputFormat){
        s.importIntoRepository(filePath,baseURI,inputFormat);
    }

    public void importToSesame(String file, String baseUri, RDFFormat dataFormat, Resource... contexts){
        s.importIntoRepository(file,baseUri,dataFormat,contexts);
    }

    public void importToSesameDirectoryChunked(String preloadFolder){
        s.importIntoRepositoryDirectoryChunked(preloadFolder);
    }

    public void importToSesameFileChunked(File fileVerylarge){
        s.importIntoRepositoryFileChunked(fileVerylarge);
    }





















    //--------------------------------------------------------------------

    public Repository connectToCassandra(String host,String keySpace,boolean isQuadStore){
        CumulusRDFKit cumulus = CumulusRDFKit.getInstance();
        return cumulus.connectToCassandraRepository(host,keySpace,isQuadStore);
    }














}
