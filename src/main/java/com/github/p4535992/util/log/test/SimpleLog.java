package com.github.p4535992.util.log.test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

public class SimpleLog extends OutputStream{

    private static File logfile;
    OutputStream[] outputStreams;

    public SimpleLog(){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            SimplePrintLog.start();
        } catch (Exception ex) {
            Logger.getLogger(SimpleLog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public SimpleLog(String LOGNAME, String SUFFIX){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            SimpleLog.logfile =
                    new File(System.getProperty("user.dir")+File.separator
                            + LOGNAME + "_" +  timeStamp + "." + SUFFIX);
            SimplePrintLog.start(logfile);
        }catch (Exception ex) {
            Logger.getLogger(SimpleLog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public SimpleLog(OutputStream... outputStreams) {
       this.outputStreams= outputStreams;
    }

    @Override
    public void close() throws IOException {
        if(outputStreams != null) {
            for (OutputStream out : outputStreams) {
                if(out!=null)out.close();
            }
        }
        SimplePrintLog.stop();
    }

    @Override
    public void flush() throws IOException{
        for (OutputStream out: outputStreams) {
            if (out != null) out.flush();
        }
    }

    @Override
    public void write(int b) throws IOException{
        for (OutputStream out : outputStreams) {
            if(out!=null)out.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException{
        for (OutputStream out : outputStreams) {
            if(out!=null)out.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        for (OutputStream out : outputStreams) {
            if(out!=null)out.write(b, off,len);
        }
    }
}


