package com.htsc.alluxioproxy;

import javax.security.auth.login.Configuration;
import javax.ws.rs.client.Client;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Test uploading a file and then downloading it.
 */
public class FileTest {

  private static final ExecutorService TRANSCODE_SERVICE =
      Executors.newFixedThreadPool(20);

  /**
   * Main entry.
   *
   * @throws Exception if any error occurs
   */
  public void createFile(String s) throws IOException {
    File f = new File("/root/software/lockTest/" + s);
    File ff = f.getParentFile();
    while(!ff.exists()) {
      ff.mkdir();
      ff = ff.getParentFile();
    }
    try (FileOutputStream outputStream = new FileOutputStream(f);
         BufferedOutputStream out = new BufferedOutputStream(outputStream) ){
      for(int i = 0 ; i < 100 ; i++) {
        out.write(1);
      }
    }
  }

  public void createFiles() throws  Exception {
    for(int i = 0 ;i < 20 ; i++) {
      createFile(i+"");
    }
  }

  public void submit(final String baseUri) throws Exception {
    for(int i = 0 ; i < 20 ; i++) {
      final File f = new File("/root/software/lockTest/" + i );
      TRANSCODE_SERVICE.submit(new Runnable() {
        @Override
        public void run() {
          try {
            sendAndGet(f, baseUri);
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            System.out.println("finish " + f.getName());
            f.delete();
          }
        }
      });
    }
    //Thread.join();
  }

  public void sendAndGet(File file,String baseUri) throws Exception {
    Client client = HttpsUtils.getHttpClient();

    InputStream in = new BufferedInputStream(new FileInputStream(file));

    Uploader uploader = new Uploader(client, baseUri);
    System.out.println("start upload");
    String path = uploader.upload(in);
    System.out.println(path);

    System.out.println("start to download");

    Downloader downloader = new Downloader(client, baseUri);
    InputStream in2 = new BufferedInputStream(downloader.download(path));

    in = new BufferedInputStream(new FileInputStream(file));
    //OutputStream out = new BufferedOutputStream(new FileOutputStream("/root/software/test.mp4"));
    int read;
    while ((read = in2.read()) != -1) {
      if (read != in.read()) {
        // Data inconsistency
        throw new Error("Data inconsistency");
      }
      //  out.write(read);
    }
    if (in.read() != -1) {
      // Data inconsistency
      throw new Error("Data inconsistency");
    }
    in.close();
    in2.close();
  }

  public static void main(String[] args) throws Exception {
    FileTest fileTest = new FileTest();
    fileTest.createFiles();
    fileTest.submit(args[0]);
  }

  /*
  public static void main(String[] args) throws Exception {
   // if (args.length != 2) {
   //   throw new Exception("Illegal arguments");
   // }
    String baseUri = args[0];
    File file = new File(args[1]);

    Client client = HttpsUtils.getHttpClient();

    InputStream in = new BufferedInputStream(new FileInputStream(file));

    Uploader uploader = new Uploader(client, baseUri);
    System.out.println("start upload");
    String path = uploader.upload(in);
    System.out.println(path);

    System.out.println("start to download");

    Downloader downloader = new Downloader(client, baseUri);
    InputStream in2 = new BufferedInputStream(downloader.download(path));

    in = new BufferedInputStream(new FileInputStream(file));
    //OutputStream out = new BufferedOutputStream(new FileOutputStream("/root/software/test.mp4"));
    int read;
    while ((read = in2.read()) != -1) {
      if (read != in.read()) {
        // Data inconsistency
        throw new Error("Data inconsistency");
      }
    //  out.write(read);
    }
    if (in.read() != -1) {
      // Data inconsistency
      throw new Error("Data inconsistency");
    }
    in.close();
    in2.close();
    //out.close();
  }*/

}
