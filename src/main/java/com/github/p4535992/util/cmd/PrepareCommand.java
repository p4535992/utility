/**
int 	execute(CommandLine command)
Methods for starting synchronous execution.
void 	execute(CommandLine command, ExecuteResultHandler handler)
Methods for starting asynchronous execution.
int 	execute(CommandLine command, Map<String,String> environment)
Methods for starting synchronous execution.
void 	execute(CommandLine command, Map<String,String> environment, ExecuteResultHandler handler)
Methods for starting asynchronous execution.
ProcessDestroyer 	getProcessDestroyer()
Set the handler for cleanup of started processes if the main process is going to terminate.
ExecuteStreamHandler 	getStreamHandler()
Get the StreamHandler used for providing input and retrieving the output.
ExecuteWatchdog 	getWatchdog()
Get the watchdog used to kill of processes running, typically, too long time.
File 	getWorkingDirectory()
Get the working directory of the created process.
boolean 	isFailure(int exitValue)
Checks whether exitValue signals a failure.
void 	setExitValue(int value)
Define the exitValue of the process to be considered successful.
void 	setExitValues(int[] values)
Define a list of exitValue of the process to be considered successful.
void 	setProcessDestroyer(ProcessDestroyer processDestroyer)
Get the handler for cleanup of started processes if the main process is going to terminate.
void 	setStreamHandler(ExecuteStreamHandler streamHandler)
Set a custom the StreamHandler used for providing input and retrieving the output.
void 	setWatchdog(ExecuteWatchdog watchDog)
Set the watchdog used to kill of processes running, typically, too long time.
void 	setWorkingDirectory(File dir)
Set the working directory of the created process.
 */
package com.github.p4535992.util.cmd;
import com.github.p4535992.util.log.SystemLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Marco
 */
public class PrepareCommand {
    
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
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ( (line = br.readLine()) != null) {
                    buffer.append(line);//.append("\n");
                    if(!line.isEmpty()){
                        SystemLog.message("CMD:" + line);}
                }
                message = buffer.toString();
            } catch (IOException ioe) {
                SystemLog.exception(ioe);
            }
        }
    }

    public void RunBatchWithProcessAndRuntime(String fileName,String[] listString) throws FileNotFoundException, UnsupportedEncodingException{
        Runtime rt = Runtime.getRuntime();
        PrepareCommand rte = new PrepareCommand();
        PrepareCommand.StreamWrapper error, output;
        File file = WriteFileBatch(fileName,listString,System.getProperty("user.dir").toString());
        try {   
            
            //File file = WriteFileBatch(fileName,listString,System.getProperty("user.dir").toString());
            Process proc = null;
            proc = rt.exec(file.getAbsolutePath());
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
            int exitVal = 0;
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
            SystemLog.message("Output: " + output.message);
            SystemLog.warning("Error: " + error.message);
            
            //file.delete();
            //file.deleteOnExit();
        }catch(Exception e){
             SystemLog.exception(e);
        }finally{
            file.delete();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Other run method">
    private void RunBatchWithSingleCommands(){
        Runtime rt = Runtime.getRuntime();
        PrepareCommand rte = new PrepareCommand();
        PrepareCommand.StreamWrapper error, output;
        try {   
            //System.out.println(System.getProperty("user.dir")); //C:\Users\Marco\Desktop\[Netbeans Project]ExtractInfo AndTripleWithGATEAndKarma 2015-01-14
            File file = new File(System.getProperty("user.dir"));
                     
            //oppure chiamiamo i comandi singolarmente
            String[] commands = new String[]{                
                "@Echo OFF",
                "cd %~dp0\\Web-Karma-master v2.031",
                "PUSHD \".\\karma-offline\"",
                "mvn exec:java -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineRdfGenerator\" -Dexec.args=\" --sourcetype DB --modelfilepath \\\"%~dp0/karma_files/model/R2RML_infodocument_nadia3_ontology-model_2014-12-22.ttl\\\" --outputfile \\\"%~dp0/karma_files/output/tripla-model-java-2015-01-13.n3\\\" --dbtype MySQL --hostname localhost --username siimobility --password siimobility --portnumber 3306 --dbname geolocationdb --tablename infodocument_u4_link_test_ontology\"" 
            };
            /*
            Process proc = rt.exec("@Echo OFF");  
            proc = rt.exec("cd %~dp0\\Web-Karma-master v2.031");  
            proc = rt.exec("PUSHD \".\\karma-offline\"");  
            proc = rt.exec("mvn exec:java -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineRdfGenerator\" -Dexec.args=\" --sourcetype DB --modelfilepath \\\"%~dp0/karma_files/model/R2RML_infodocument_nadia3_ontology-model_2014-12-22.ttl\\\" --outputfile \\\"%~dp0/karma_files/output/tripla-model-java-2015-01-13.n3\\\" --dbtype MySQL --hostname localhost --username siimobility --password siimobility --portnumber 3306 --dbname geolocationdb --tablename infodocument_u4_link_test_ontology\"");  
            
            //rt.exec("cmd.exe /c cd \""+new_dir+"\" & start cmd.exe /k \"java -flag -flag -cp terminal-based-program.jar\"");
            
            org.p4535992.mvc.error = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
            output = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;
            org.p4535992.mvc.error.start();
            output.start();
            org.p4535992.mvc.error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
            System.out.println("Output: "+output.message);
            System.out.println("Error: "+org.p4535992.mvc.error.message);
            */
        }catch(Exception e){
            SystemLog.exception(e);
        }
    }
    /*
    private void RunCommandsWithCommonsExec() throws IOException{
        //C:\Users\Marco\Desktop\[Netbeans Project]ExtractInfo AndTripleWithGATEAndKarma 2015-01-14/Web-Karma-master v2.031/
        //File file = new File(System.getProperty("user.dir")+"/karmaJavaTest_empty.bat");
        //Map map = new HashMap();
        //map.put("file", file);
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
        executor.setWorkingDirectory(new File(System.getProperty("user.dir")+"/Web-Karma-master v2.031/karma-offline/"));
        //executor.setExitValue(0);
        
        
        CommandLine commandLine = new CommandLine("mvn exec:java");
        commandLine.addArgument(" -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineRdfGenerator\"");
        commandLine.addArgument(" -Dexec.args=\" --sourcetype DB --modelfilepath \""+System.getProperty("user.dir")+"/karma_files/model/R2RML_infodocument_nadia3_ontology-model_2014-12-22.ttl\" --outputfile \""+System.getProperty("user.dir")+"/karma_files/output/tripla-model-java-2015-01-13.n3\" --dbtype MySQL --hostname localhost --username siimobility --password siimobility --portnumber 3306 --dbname geolocationdb --tablename infodocument_u4_link_test_ontology\"");
        
        
        // insert a watchdog if requested
        watchdog = new ExecuteWatchdog(printJobTimeout);
        executor.setWatchdog(watchdog);
        
        // handle output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        
        try {
            exitValue = executor.execute(commandLine);
        } catch (ExecuteException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(outputStream.toString());
        
    }
    */
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
    // </editor-fold>
    
    //METODI DI SUPPORTO
    private File WriteFileBatch(String testbat, String[] listString,String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File(filePath+"/"+testbat);          	
        //Creating a text file (note that this will overwrite the file if it already exists
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        for(int i=0; i < listString.length; i++){      
            writer.println(listString[i]);
        }
        writer.close();
        return file;
    }
    

}
