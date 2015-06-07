package p4535992.util.archive;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipStreamer implements Runnable {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private AtomicLong counter = new AtomicLong();
    private PipedOutputStream out;
    private ZipOutputStream zipOut;

    public ZipStreamer() {
        out = new PipedOutputStream();
        zipOut = new ZipOutputStream(out, UTF8);
        new Thread(this).start();
    }

    public ZipStreamer(PipedOutputStream out,ZipOutputStream zipout) {
        this.out =out;
        this.zipOut = zipout;
        new Thread(this).start();
    }

    public String readZip(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        ZipStreamer  z = new  ZipStreamer();
        ZipInputStream zipIn = new ZipInputStream(z.getInputStream(), UTF8);
        //ZipInputStream zipIn = new ZipInputStream(in, UTF8);
        while (true) {
            ZipEntry entry = zipIn.getNextEntry();
            if (entry == null) {
                break;
            }
            System.out.println("Reading " + entry.getName());
            System.out.println("File time: " + new Date(entry.getTime()));
            // Naive - this only works because we only produce lines in the producer
            BufferedReader reader = new BufferedReader(new InputStreamReader(zipIn, UTF8));
            String line = reader.readLine();
            sb.append(line);
            System.out.println(line);
            System.out.flush();

        }
        return sb.toString();
    }

    public InputStream getInputStream() throws IOException {
        return new PipedInputStream(out);
    }

//    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("Writing " + counter.get());
                ZipEntry e = new ZipEntry("folder/file" + counter.incrementAndGet());
                zipOut.putNextEntry(e);
                zipOut.write(("This is file " + counter.get() + "\n\n")
                        .getBytes(UTF8));
                zipOut.closeEntry();
                System.out.println("Wrote " + counter.get());
            } catch (Exception e) {
                try {
                    zipOut.close();
                } catch (IOException e1) {
                }
                break;
            }
        }
    }


}
