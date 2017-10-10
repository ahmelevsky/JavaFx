package application;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {
	static int ydt;
	static int ldt;
	public static int remindTime = 600000;
	
	
	public static void main(String[] args) throws Exception {
		
		   AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("bip2.wav").getAbsoluteFile());
		
		new Timer().schedule(
			    new TimerTask() {

			        @Override
			        public void run() {
			        	try {
							update();
							 if ((ydt - ldt) < remindTime){
								 AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("bip2.wav").getAbsoluteFile());
								 Clip  clip = AudioSystem.getClip();
							       clip.open(audioInputStream);
								   clip.start();
							 }
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    }, 0, 20000);

	}
	
	

public static void update() throws Exception{
	 String  yd = "";
	 String ld = "";
	
	
	   Connection.Response loginForm = Jsoup.connect("http://idlast.com/login.php")
	            .method(Connection.Method.GET)
	            .execute();

	           Document document = Jsoup.connect("http://idlast.com/login.php")
	            .data("login", "glaesarius")
	            .data("pass", "hmovfnde")
	            .cookies(loginForm.cookies())
	            .post();
	         
	          String regexString = Pattern.quote("¬ремени: ") + "(.*?)" + Pattern.quote(" <br /></p>\");");
	          Pattern pattern = Pattern.compile(regexString);
	          Matcher matcher = pattern.matcher(document.toString());
	       
	          while (matcher.find()) 
	    	     yd = matcher.group(1); 
	       
	       Element e = document.select("div[id*='dellegend']").last();
	 
	       ld = e.text();
			 
			 String ldd = ld.split(": ")[1].split(" ")[0];
			 
			ldt = Integer.parseInt(ldd);
			ydt = toMinutes(yd);
}


private static int toMinutes(String data)
{
	int result = 0;
	if (data.contains("дн."))
		result += Integer.parseInt(data.split("дн.")[0].trim()) * 1400; 
	if (data.contains("ч."))
		result += Integer.parseInt(data.split("дн.")[1].split("ч.")[0].trim()) * 60; 
	if (data.contains("мин.")){
		String[] d = data.trim().split(" ");
		result += Integer.parseInt(d[d.length-2]); 
	}
	
	return result;
}

}
