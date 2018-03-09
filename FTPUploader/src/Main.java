import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.*;


public class Main {
	 static String server = "ftp.shutterstock.com";
	 static int port = 21;
     static String user = "glaesarius@mail.ru";
     static String pass = "Justatrala9";
     static Logger l = new Logger();
     static FilesList fl = new FilesList();
     static File currentDir = new File(".");
     static FTPClient  ftpClient = new FTPClient();
     static int attempt = 0;
     static boolean success;
     static List<String> blackList = new ArrayList<String>();
	
	 public static void main(String[] args) {
	        l.log("Starting the application and looking for the files in " + currentDir.getAbsolutePath());
	        
	        while (true) {
	        
	        	File file = getNewFile();
	        	if (file == null)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				else {
	        		l.log("Proccessing " + file.getName());
	        		success = false;
	        		attempt = 0;
	        		
	        		do {
	        			checkConnection();
	        		}
	        		while (!uploadFile(file));
	        		if (success){
	        			file.delete();
	        			fl.log(file.getName());
	        		}
	        		
				}
	          
	        }
	    }
	 
	 public static File getNewFile(){
		 File[] files = currentDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return !blackList.contains(name) && (name.endsWith(".jpg") || name.endsWith(".eps"));
				}
			});
		 if (files.length>0) return files[0];
		 else return null;
	        
	 }
	 
	 public static void checkConnection(){
		 try 
		    {
			 if (!ftpClient.sendNoOp()) {
		            disconnect();
		            connect();
		        }
		    }
		    catch (IOException e) 
		    {
		    	 disconnect();
		         connect();
		    }
	 }
	 
	 
	 public static void connect(){
		while (true){
			if (connectToFtp())
				return;
			else
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
		}
	 }
	 
	 public static boolean connectToFtp(){
		 l.log("Start connecting to " + server);
	        try {
	         ftpClient.connect(server, port);
	         ftpClient.login(user, pass);
	         ftpClient.enterLocalPassiveMode();
	         ftpClient.setControlKeepAliveTimeout(10);
	         ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	         ftpClient.setBufferSize(1024 * 1024);
	         l.log("Connected successfully");
	         return true;
	        }
	        catch (IOException ex){
	        	l.log("Error: " + ex.getMessage());
	        	return false;
	        }
	 }
	 
	 public static void disconnect(){
		 l.log("Disconnecting from " + server);
		 try {
             if (ftpClient.isConnected()) {
                 ftpClient.logout();
                 ftpClient.disconnect();
                 l.log("Disconected successfully");
             }
         } catch (IOException ex) {
        	 
         }
	 }
	 
	 public static boolean uploadFile(File file){
		 l.log("Start uploading " + file.getName() + ", attempt number " + ++attempt);
		 boolean done = false;
		 InputStream inputStream = null;
		 Timer timer = new Timer();
		 timer.schedule(
			        new TimerTask() {
			            public void run() {
			            	try {
			            		l.log("Upload timeout exceeded. Aborting upload...");
								ftpClient.abort();
								//ftpClient.disconnect();
								l.log("Upload aborted");
							} catch (IOException e) {
								l.log("Error during abort operation: " + e.getMessage());
							}
			            }
			        }, 
			    1800000 );
		 try {
		 inputStream = new FileInputStream(file);
		 
		 done =  ftpClient.storeFile(file.getName(), inputStream);
		 
         if (done){
             l.log("Uploaded successfully " + file.getName());
             success = true;
         } 
		 else {
			 l.log("File "+ file.getName() +" was not uploaded - Shutterstock cancelled the upload. Putting to the black list.");
			 blackList.add(file.getName());
			 fl.log(file.getName() + " - CANCELLED");
			 done = true;
		 }
	     }
		 catch (IOException ex) {
			 l.log("File "+ file.getName() +" was not uploaded, error: " + ex.getMessage());
			 return done;
		 }
		 finally {
			 timer.cancel();
			 if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
				}
		 }
		 return done;
	 }
	 
	 public static boolean uploadFileWithStream(File file){
		 l.log("Start uploading " + file.getName());
		 boolean done = false;
		 try {
		 FileInputStream inputStream = new FileInputStream(file);
		 
         OutputStream outputStream = ftpClient.storeFileStream(file.getName());
         byte[] bytesIn = new byte[4096];
         int read = 0;

         while ((read = inputStream.read(bytesIn)) != -1) {
             outputStream.write(bytesIn, 0, read);
         }
         inputStream.close();
         outputStream.close();

         done = ftpClient.completePendingCommand();
         l.log("Uploaded successfully " + file.getName());
		 }
		 catch (IOException ex) {
			 l.log("File "+ file.getName() +" was not uploaded, error: " + ex.getMessage());
			 return done;
		 }
		 return done;
	 }
	 
}
