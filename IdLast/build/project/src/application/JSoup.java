package application;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JSoup {

	public static void main(String[] args) throws Exception{
		
	update();
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
		          System.out.println(regexString);
		          
		          Pattern pattern = Pattern.compile(regexString);
		       // text contains the full text that you want to extract data
		       Matcher matcher = pattern.matcher(document.toString());
		       
		       while (matcher.find()) {
		    	   yd = matcher.group(1); // Since (.*?) is capturing group 1
		         System.out.println(yd);
		       }
		       Element e = document.select("div[id*='dellegend']").last();
		 
		 ld = e.text();
		 
		 String ldd = ld.split(": ")[1].split(" ")[0];
		 System.out.println(ldd);
		 
		System.out.println(Integer.parseInt(ldd));
		System.out.println(toMinutes(yd));
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

	private static void play(){
		String bip = "bip.mp3";
		Media hit = new Media(new File(bip).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}

	private static void play2(){
		String bip = "bip2.mp3";
		Media hit = new Media(new File(bip).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}
	
}
