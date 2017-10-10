package idlast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

	
	   private static final Logger LOGGER = Logger.getLogger(Main.class.getName() );
	   static FileHandler fh;
	    static int ydt;
	    static int ldt;
		public static int remindTime = 180;
		public static int checkEach = 60000;
		static Timer t = new Timer();
		static boolean isSent;
		static boolean isSent2;
		static boolean isLogged;
		
		static List<Integer> queue = new ArrayList<>();
		static final int queueLength = 10;
	public static void main(String[] args) throws SecurityException, IOException {
		
		fh = new FileHandler("idlast.log");  
		LOGGER.addHandler(fh);
		MyFormatter formatter = new MyFormatter(); 
        fh.setFormatter(formatter);  
		
		LOGGER.log(Level.INFO, "Starting");
		setTask(false);
		

	}


	public static void setTask(boolean cancel){
		if (cancel) {t.cancel();
		t = new Timer();
		}
		
		t.scheduleAtFixedRate(new TimerTask() {
		        @Override
		        public void run() {
		            	try {
							update();
						} catch (Exception e) {
							StringBuilder sb = new StringBuilder();
							sb.append(e.getMessage() + "\n");
							 for (StackTraceElement s:e.getStackTrace())
								 sb.append(s + "\n");
							 LOGGER.log(Level.WARNING, sb.toString());
						}
		        }
			}, 0, checkEach);
		
	}
	
	public static void update() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		 String  yd = "";
		
		   Connection.Response loginForm;
		try {
			loginForm = Jsoup.connect("http://idlast.com/login.php")
			            .method(Connection.Method.GET)
			            .execute();
		} catch (IOException e1) {
			LOGGER.log(Level.WARNING, "Connection error");
			return;
		}

		           Document document;
				try {
					document = Jsoup.connect("http://idlast.com/login.php")
					    .data("login", "glaesarius")
					    .data("pass", "hmovfnde")
					    .cookies(loginForm.cookies())
					    .post();
				} catch (IOException e1) {
					LOGGER.log(Level.WARNING, "Connection error");
					return;
				}
		       
				Elements es = document.select("div[id*='dellegend']");
			    if (es.size()<2){
			    	 if(!isLogged)
		        		  saveSourceToFile(document.toString());
			    	LOGGER.log(Level.WARNING, "Data error");
					return;
			    }
			    
			    try {
			    int d1 = delegentToMinutes(es.get(es.size()-2));
			    int d2 = delegentToMinutes(es.get(es.size()-1));
			    ldt = (d1>d2) ? d1 : d2;
			    }
			    catch (Exception ex){
			    	 if(!isLogged)
		        		  saveSourceToFile(document.toString());
			    	LOGGER.log(Level.WARNING, "Data error");
					return;
			    }
				
		         	           
		          String regexString = Pattern.quote("Времени: ") + "(.*?)" + Pattern.quote(" <br /></p>\");");
		          Pattern pattern = Pattern.compile(regexString);
		          Matcher matcher = pattern.matcher(document.toString());
		       
		          if (matcher.find()) 
		    	     yd = matcher.group(1);
		          else {
		        	  if(!isLogged)
		        		  saveSourceToFile(document.toString());
		        	  LOGGER.log(Level.INFO, "No delegates");
		        	  isSent = false;
		        	  isSent2 = false;
					  return;
		          }
		          
		          try{
		          ydt = toMinutes(yd);
		          putQueue(ydt);
		          }
		          catch (Exception ex){
		        	  if(!isLogged)
		        		  saveSourceToFile(document.toString());
		        	  LOGGER.log(Level.WARNING, "Parsing error");
					  return;
		          }
		          
		          isLogged = false;
		          
		          if (isReady(1)){
		          LOGGER.log(Level.INFO, "It's time! Time of record, min: " + ldt + ", Your delegate, min: " + ydt + ",Left: " + (ydt - ldt) + " min");
		          LOGGER.log(Level.INFO, "Your delegate time queue: " + printQueue());
	        	  if (!isSent){
	        		  isSent = send();
	        		  if (!isSent)
	        			  LOGGER.log(Level.WARNING, "Mail error");
	        	  }
	        	  if (!isSent2 && (isReady(2))){
	        		  isSent2 = send();
	        		  if (!isSent2)
	        			  LOGGER.log(Level.WARNING, "Mail error");
	        	  }
		          }
		          else 
		          {
		          LOGGER.log(Level.INFO, "Waiting. Time of record, min: " + ldt + ", Your delegate, min: " + ydt + ",Left: " + (ydt - ldt) + " min");
		          LOGGER.log(Level.INFO, "Your delegate time queue: " + printQueue());
	        	  isSent = false;
	        	  isSent2 = false;
		          }
	}


	private static int delegentToMinutes(Element delegend){
		 String ld = delegend.text();
		 String ldd = ld.split(": ")[1].split(" ")[0];
	 	 return Integer.parseInt(ldd);
	}

	private static boolean send(){
		 List<String> text = new ArrayList<String>();
		  text.add("Время надписи, мин: " + ldt);
		  text.add("Ваш засланец. Времени, мин: " + ydt);
		  text.add("Осталось: " + getTime(ydt - ldt));
		  return MailSender.createEmailAndSend(text);
	}

	public static String getTime(int m){
	    int hours = m / 60;
	    int minutes = m % 60;
	    return  hours + "ч. " + minutes + "мин.";
	}

	private static int toMinutes(String data)
	{
		int result = 0;
		if (data.contains("дн."))
			result += Integer.parseInt(data.split("дн.")[0].trim()) * 1400; 
		if (data.contains("ч.")){
			String[] d = data.trim().split("ч.")[0].split(" ");
		    result += Integer.parseInt(d[d.length-1])  * 60; 
		}
		if (data.contains("мин.")){
			String[] dd = data.trim().split(" ");
			result += Integer.parseInt(dd[dd.length-2]); 
		}
		
		return result;
	}
	
	static private void putQueue(int value){
		queue.add(0,value);
		if(queue.size() > queueLength)
			queue.remove(queue.size() - 1);
	}

	static private String printQueue(){
		StringBuilder sb = new StringBuilder();
		queue.forEach(s -> sb.append(s + " "));
		return sb.toString();
	}
	
	static private boolean isReady(int devider){
		int timeToCheck = remindTime + queueLength*checkEach/60000;
		return queue.stream().allMatch(q -> (q - ldt) < timeToCheck/devider);
		
	}
	
	static private void saveSourceToFile(String data) throws IOException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(df.format(new Date())+".txt"), "UTF-8"));
			try {
			    out.write(data);
			} finally {
			    out.close();
			}
			isLogged = true;
	}
	
}


class MyFormatter extends Formatter {
    // Create a DateFormat to format the logger timestamp.
    private static final DateFormat df = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss.SSS");

    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(df.format(new Date(record.getMillis()))).append(" - ");
        builder.append("[").append(record.getSourceClassName()).append(".");
        builder.append(record.getSourceMethodName()).append("] - ");
        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }

    public String getHead(Handler h) {
        return super.getHead(h);
    }

    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
