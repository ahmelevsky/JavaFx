package st;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Main {

	public static void main(String[] args) throws IOException {
		connect();
	}
	
	
	public static void connect() throws IOException {
		
		 
Map<String,String> cookies = new HashMap<String,String>(){{
   put("session", "s:xbEY-WB1czhddUGg52x0afDzjCmurJqE.mQFzBG+JZBkNLVQDtFzfX4GbWND45VY4v01gBXxAe2c");
}};

Map<String,String> headers = new HashMap<String,String>(){{
   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
//   put("__ssid", "f4fca768a122d131e78499fdcf7c9ae");
}};

		
		  Connection.Response getResp =
		  Jsoup.connect("https://submit.shutterstock.com/upload/portfolio").headers(headers).followRedirects(false).cookies(cookies)
		  .method(Connection.Method.GET).validateTLSCertificates(false) .execute();
		 
		  
		  System.out.println(getResp.body());
		/*
		 * Document document = Jsoup.connect("https://submit.shutterstock.com")
		 * .data("username", "cheburashka1799@gmail.com") .data("password", "Arap1799")
		 * .cookies(getResp.cookies()) .headers(headers) .followRedirects(false)
		 * .post(); 
		 * System.out.println(document.body().html());
		 * s%3A8lY1QdM0fjH_ioFhTzMUOoc7rVR1lg5c.IKekVl7GFgK4Hfsa1sjKUnklw9f8bTVvvaA%2BszOx6Do
		 */
		           
	}
	
	
}
