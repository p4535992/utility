package com.github.p4535992.util.repositoryRDF;

import com.github.p4535992.util.repositoryRDF.cumulusrdf.CumulusRDFKit;
import com.github.p4535992.util.repositoryRDF.sesame.SesameUtilities;
import com.hp.hpl.jena.graph.Triple;
import org.openrdf.model.*;
import org.openrdf.query.Operation;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.base.RepositoryConnectionWrapper;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 4535992 on 27/10/2015.
 */
@SuppressWarnings("unused")
public class RepositoryRDFUtilities {

    private static SesameUtilities s;

    protected RepositoryRDFUtilities() {}

    private static RepositoryRDFUtilities instance = null;

    public static RepositoryRDFUtilities getInstance(){
        if(instance == null) {
            instance = new RepositoryRDFUtilities();
            //help with very large repository....
            System.setProperty("entityExpansionLimit", "1000000");
            RepositoryRDFUtilities.s = SesameUtilities.getInstance();
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

    public List<String> getSesameRepositories(){
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

    public List<String[]> toSesameTupleResult(String queryString,String[] bindingName){
        return s.TupleQueryEvalutation(queryString, bindingName);
    }

    public List<Statement> toSesameGraphResult(String queryString){
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

    public Model createModel(){ return s.createModel();}

    public Graph createGraph(){return s.createGraph();}

    public RepositoryManager createRepositoryManagerRemote(String urlRepositoryId){
        return s.createRepositoryManagerRemote(urlRepositoryId);
    }

    public RepositoryManager createRepositoryManagerLocal(File baseDirectory){
        return s.createRepositoryManagerLocal(baseDirectory);
    }

    public boolean createRepository(String pathToTheConfigFile){return s.createRepository(pathToTheConfigFile);}

    public RepositoryConnectionWrapper createRepositoryConnectionWrapper(
            Repository repository,RepositoryConnection repositoryConnection){
        return s.createRepositoryConnectionWrapper(repository, repositoryConnection);
    }

    public RepositoryConnectionWrapper createRepositoryConnectionWrapper(
            Repository repository){
        return s.createRepositoryConnectionWrapper(repository);
    }

    public Repository createRepositoryStack(RepositoryImplConfig repositoryImplConfig){
        return s.createRepositoryStack(repositoryImplConfig);
    }

    public Repository createRepositoryUnManaged(File repositoryDirFile,File configFile){
        return s.createRepositoryUnManaged(repositoryDirFile,configFile);
    }

    public Statement createStatement(Object subject,Object predicate,Object objectOrUri,Object context){
        return s.createStatement(subject,predicate,objectOrUri,context);
    }

    public Literal createLiteral(Object literalObject){
      return s.createLiteral(literalObject);
    }

    public Resource createResource(Object uriOrString){
        return s.createResource(uriOrString);
    }

    public Value createValue(Object resourceOrLiteral){
       return s.createValue(resourceOrLiteral);
    }

    public URI createURI(Object uri){
       return s.createURI(uri);
    }

    public Long numberOfExplicitStatements(RepositoryConnection repConn){
        return s.numberOfExplicitStatements(repConn);
    }

    public Long numberOfExplicitStatements(){
        return s.numberOfExplicitStatements();
    }

    public Long numberOfImplicitStatements(RepositoryConnection repConn){
        return s.numberOfImplicitStatements(repConn);
    }

    public Long numberOfImplicitStatements(){
        return s.numberOfImplicitStatements();
    }

    public Long getExecutionQueryTime(Object queryOrOperation){
        return s.getExecutionQueryTime(queryOrOperation);
    }

    public QueryLanguage toQueryLanguage(String queryLanguage){
        return s.stringToQueryLanguage(queryLanguage);
    }

    public QueryLanguage checkQueryLanguage(String queryString){
        return s.checkLanguageOfQuery(queryString);
    }

    public String updateConfigTemplate(String configTemplate,Map<String,String> variables){
         return s.updateConfigTemplate(configTemplate,variables);
    }

    public boolean isRepositoryInitialized(){
        return s.isRepositoryInitialized();
    }

    public boolean isRepositoryConnected(){return s.isRepositoryConnected();}

    public boolean isRepositoryActive(){return s.isRepositoryActive();}

    public boolean isRepositoryEmpty(){return s.isRepositoryEmpty(); }

    public void showStatistic(long startupTime){s.showInitializationStatistics(startupTime);}

    public void showStatement(TupleQuery tupleQuery){s.showStatements(tupleQuery);}

    public Map<String,Object> getAllInfoSesame(){
        Map<String,Object> map = new HashMap<>();
        map.put("Repository",s.getRepository());
        map.put("RepositoryConnection",s.getRepositoryConnection());
        map.put("RepositoryConnectionWrapper",s.getRepositoryConnectionWrapper());
        map.put("RepositoryManager",s.getRepositoryManager());
        map.put("RepositoryLocation",s.getRepositoryLocation());
        map.put("RepositoryName",s.getRepositoryName());
        map.put("RepositoryProvider",s.getRepositoryProvider());
        map.put("Prefixes",s.getNamespacePrefixesFromRepository());
        map.put("Repositories",s.getRepositories());
        map.put("ServerRepositories",s.getURL_REPOSITORIES());
        map.put("ServerRepositoryID",s.getURL_REPOSITORY_ID());
        map.put("ServerSesame",s.getURL_SESAME());
        return map;
    }

    public void setURLRepositoryId(String repositoryId){
        s.setURLRepositoryId(repositoryId);
    }

    public void setURLRepositoryId(String ID_REPOSITORY,String server,String port){
        s.setURLRepositoryId(ID_REPOSITORY,server, port);
    }



    //--------------------------------------------------------------------





















    //--------------------------------------------------------------------

    public Repository connectToCassandra(String host,String keySpace,boolean isQuadStore){
        CumulusRDFKit cumulus = CumulusRDFKit.getInstance();
        return cumulus.connectToCassandraRepository(host,keySpace,isQuadStore);
    }














}
