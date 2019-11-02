package klt.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ShutterProvider {

	static String baseURL = "https://www.shutterstock.com";
	static Map<String,String> cookies;
	static Map<String,String> headers;

    
	static {
		headers = new HashMap<String,String>(){{
			   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			}};
		cookies = new HashMap<String,String>(){{
			}};
	}
	
	
	private ShutterProvider() {
		super();
	}


	public static String findImages(String query) {
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("q", query);
			   put("language", "en");
			   put("page%5Bsize%5D", "100");
			   put("page%5Bnumber%5D", "1");
			   put("recordActivity", "true");
			 //  put("fields%5Bimages%5D", "title");
			 //  put("fields%5Bimages%5D", "link");
			  // put("fields%5Bimages%5D", "displays");
			 //  put("fields%5Bimages%5D", "alt");
			  // put("fields[images]", "aspect");
			 //  put("fields[images]", "image_type");
			  // put("fields[images]", "is_editorial");
			  // put("fields[images]", "has_model_release");
			  // put("fields[images]", "has_property_release");
			}};
		try {
			//System.out.println(get("/studioapi/images/search", parameters));
			return get("/studioapi/images/search", parameters);
		} catch (IOException e) {
			return null;
		}
	}
	
	/*
	public static String getKeywords(String id) {
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("productId", id);
			   put("query", "data");
			   put("safe", "true");
			   put("searchSource", "base_product_page");
			}};
		try {
		//	System.out.println(get("/api/related", parameters));
			return get("/api/related", parameters);
		} catch (IOException e) {
			return null;
		}
	}
	
	*/
	
	
	public static List<String> getKeywords(String link) throws IOException {
	    	List<String> result = new ArrayList<String>();
	    	
	    	Document doc = Jsoup.connect(baseURL + link )
			  .headers(headers)
			  .followRedirects(false)
			  .cookies(cookies)
			  .method(Connection.Method.GET)
			  .validateTLSCertificates(false)
			  .ignoreContentType(true)
			  .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			  .get();
	    	
	    	Elements elems = doc.select("[data-automation='ExpandableKeywordsList_container_div'] > div >div >a");
	    	for (Element el:elems) {
	    		if (el.hasText())
	    			result.add(el.ownText());
	    	}
	    	
			//writeToFile("D:\\imagedata.txt",source);
			
			return result;
	}
	
	

	  private static void writeToFile(String fileName, String data) {
		 File file = new File(fileName);
		 if (file.exists()) file.delete();
		 try {
			file.createNewFile();
		} catch (IOException e1) {
		}
		  try {
			PrintWriter out = new PrintWriter(fileName);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
		}
	  }
	
	private static String get(String url, Map<String,String> parameters) throws IOException{

			if (parameters==null)
				parameters = new HashMap<String,String>();
		
			return
				  Jsoup.connect(baseURL + url )
				  .headers(headers)
				  .followRedirects(false)
				  .cookies(cookies)
				  .method(Connection.Method.GET)
				  .validateTLSCertificates(false)
				  .ignoreContentType(true)
				  .data(parameters)
				  .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
				  .execute().body();
			
	}
	
	
}
