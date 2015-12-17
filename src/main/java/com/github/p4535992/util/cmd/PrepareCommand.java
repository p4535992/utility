package com.github.p4535992.util.cmd;

import com.github.p4535992.util.string.StringUtilities;
import org.apache.commons.exec.*;

import java.io.*;
import java.util.Map;

/**
 * Class utility for read the content of a file
 * @author 4535992.
 * @version 2015-07-03.
 */
@SuppressWarnings("unused")
public class PrepareCommand {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(PrepareCommand.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }
    
    public PrepareCommand(){}
    
    public StreamWrapper getStreamWrapper(InputStream is, String type){
            return new StreamWrapper(is, type);
    }
     
    private class StreamWrapper extends Thread {
        InputStream is = null;
        String type = null;         
        String message = null;

        public String getMessage() {
                return message;
        }

        StreamWrapper(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ( (line = br.readLine()) != null) {
                    buffer.append(line);//.append("\n");
                    if(!line.isEmpty()){
                        logger.info("CMD:" + line);}
                }
                message = buffer.toString();
            } catch (IOException e) {
                logger.error(gm() + e.getMessage(),e);
            }
        }
    }

    public int RunBatchWithProcessAndRuntime(String fileName,String[] listString) throws FileNotFoundException, UnsupportedEncodingException{
        Runtime rt = Runtime.getRuntime();
        int exitVal = 0;
        PrepareCommand rte = new PrepareCommand();
        PrepareCommand.StreamWrapper error, output;
        File file = new File(StringUtilities.PROJECT_DIR+File.separator+fileName);
        //Creating a text file (note that this will overwrite the file if it already exists
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        for (String aListString : listString) {
            writer.println(aListString);
        }
        writer.close();
        try {   
            
            //File file = WriteFileBatch(fileName,listString,System.getProperty("user.dir").toString());
            Process proc = rt.exec(file.getAbsolutePath());
            //Si può invocare direttamente il file .bat
            //for(File s : file.listFiles()){
            //    if(s.getName().equals(fileName)){               
            //        proc = rt.exec(file.getAbsolutePath());
            //        break;
            //    }        
            //}
            //oppure chiamiamo i comandi singolarmente
            //String[] commands = new String[]{"grep", "hello world", "/tmp/f.txt"};
            //Process proc = rt.exec(commands);
                       
            error = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
            output = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
            logger.info("Output: " + output.message);
            logger.warn("Error: " + error.message);
            
            //file.delete();
            //file.deleteOnExit();
        }catch(Exception e){
            logger.error(gm() + e.getMessage(),e);
            return -1;
        }finally{
            file.delete();
        }
        return exitVal;
    }

    private int RunCommandsWithCommonsExec(String workingDirectory,String mainClass,Map<String,String> params) throws IOException{
        long startTime = System.currentTimeMillis();
        long printJobTimeout = 1000;
        int exitValue ; // = 0;
        // build up the command line to using a 'java.io.File'
        //CommandLine commandLine = new CommandLine("cd "+System.getProperty("user.dir")+"/Web-Karma-master v2.031/karma-offline");
        //commandLine.setSubstitutionMap(map);
        // insert the executor and consider the exitValue '0' as success
        Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(workingDirectory));
        //executor.setExitValue(0);
        CommandLine commandLine = new CommandLine("mvn exec:java");
        commandLine.addArgument(" -Dexec.mainClass=\"" + mainClass + "\"");
        StringBuilder builder = new StringBuilder();
        builder.append(" -Dexec.args=\"");
        for(Map.Entry<String,String> entry: params.entrySet()){
            builder.append("--").append(entry.getKey()).append(StringUtilities.NBSP)
                    .append(entry.getValue()).append(StringUtilities.NBSP);
        }
        builder.append("\"");
        commandLine.addArgument(builder.toString());
        // insert a watchdog if requested
        ExecuteWatchdog watchdog = new ExecuteWatchdog(printJobTimeout);
        executor.setWatchdog(watchdog);
        
        // handle output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        try {
            exitValue = executor.execute(commandLine);
        } catch (ExecuteException e) {
            logger.error(gm() + e.getMessage(),e);
            return -1;
        }
        logger.info(outputStream.toString());
        return exitValue;
    }

    /*
    private void RunBatchWithCommonsExec(){                
        try {     
            String[] listString = new String[]{
                "c:",
                "@Echo OFF",
                "cd %~dp0\\Web-Karma-master v2.031",
                "PUSHD \".\\karma-offline\"",
                "mvn exec:java -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineRdfGenerator\" -Dexec.args=\" --sourcetype DB --modelfilepath \\\"%~dp0/karma_files/model/R2RML_infodocument_nadia3_ontology-model_2014-12-22.ttl\\\" --outputfile \\\"%~dp0/karma_files/output/tripla-model-java-2015-01-13.n3\\\" --dbtype MySQL --hostname localhost --username siimobility --password siimobility --portnumber 3306 --dbname geolocationdb --tablename infodocument_u4_link_test_ontology\""                     
            };
            File file = WriteFileBatch("test.bat",listString,System.getProperty("user.dir").toString());
            for(File s : file.listFiles()){
                if(s.getName().equals("karmaJavaTest_test.bat")){
                    long startTime = System.currentTimeMillis();
                    long printJobTimeout = 1000;
                    //Executor executor = new DefaultExecutor();
                    int exitValue;
                    ExecuteWatchdog watchdog = null;    

                    // build up the command line to using a 'java.io.File'
                    //CommandLine commandLine = new CommandLine("cd "+System.getProperty("user.dir")+"/Web-Karma-master v2.031/karma-offline");
                    //commandLine.setSubstitutionMap(map);

                    // insert the executor and consider the exitValue '0' as success
                    Executor executor = new DefaultExecutor();
                    //DefaultExecutor executor = new DefaultExecutor();
                    executor.setWorkingDirectory(file);
                    //executor.setWorkingDirectory(new File(System.getProperty("user.dir")+"/Web-Karma-master v2.031/karma-offline/"));
                    executor.setExitValue(0);
                             
                    CommandLine cl = new CommandLine(s.getAbsolutePath());
                    
                    // insert a watchdog if requested
                    watchdog = new ExecuteWatchdog(printJobTimeout);
                    executor.setWatchdog(watchdog);

                    // handle output
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
                    executor.setStreamHandler(streamHandler);
                    try{
                      executor.execute(cl);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    System.out.println(outputStream.toString());

                }        
            }
              
        }catch(Exception e){
             e.printStackTrace();
        }
    }
    */
}
