package klt.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.jsoup.Connection;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import klt.data.ImageData;
import klt.data.JsonParser;
import klt.ui.SelectableBorderPane;
import klt.workers.ImagesType;

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

	
	public static String findImages(String query, String user, ImagesType type, int page) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[display_name]", "name"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("q", query));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("language", "en"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[size]", "100"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[number]", String.valueOf(page)));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "keywords"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "link"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "src"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "image_type"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "displays"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "alt"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "has_model_release"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "has_property_release"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "aspect"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "is_editorial"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("allow_inject", "true"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("activity_type", "footage_search"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("include", "contributor-limited-meta"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("recordActivity", "true"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("sort", "popular"));
			
			 if (user!=null) {
				 parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[submitter]", user));
				   String userId = getUser(user);
				   if (userId != null ) 
					   parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[submitter]", userId));
			   }
			
			 switch (type) {
				case PHOTOS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "photo"));
					break;
				case VECTORS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "vector"));
					break;
				case ILLUSTRATIONS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "illustration"));
					break;
				case VECTORSILLUSTRATIONS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "illustration"));
					break;
				}
			//System.out.println(get("/studioapi/images/search", parameters));
			return get("/studioapi/images/search", parameters);
	}
	
	
	public static String findImages(String query, String user, ImagesType type, int page, int recordsCount) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[display_name]", "name"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("q", query));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("language", "en"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[size]", String.valueOf(recordsCount)));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[number]", String.valueOf(page)));
		 parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "keywords"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "link"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "src"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "image_type"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "displays"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "alt"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "has_model_release"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "has_property_release"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "aspect"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "is_editorial"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("allow_inject", "true"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("activity_type", "footage_search"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("include", "contributor-limited-meta"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("recordActivity", "true"));
		    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("sort", "popular"));
			
			 if (user!=null) {
				   String userId = getUser(user);
				   if (userId != null ) 
					   parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[submitter]", userId));
			   }
			
			switch (type) {
			case PHOTOS:
				parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "photo"));
				break;
			case VECTORS:
				parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "vector"));
				break;
			case ILLUSTRATIONS:
				parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "illustration"));
				break;
			case VECTORSILLUSTRATIONS:
				parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[image_type]", "illustration"));
				break;
			}
			return get("/studioapi/images/search", parameters);
	}
	
	public static Set<String> getKeywords(String link) throws IOException {
	    	Set<String> result = new LinkedHashSet<String>();
	    	
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
	    	/*
	    	Element elem = doc.select("[data-automation='ExpandableKeywordsList_container_div'] > div >div").first();
	    	for (Element el:elem.children()) {
	    		if (el.hasText())
	    			result.add(el.ownText());
	    	}
	    	*/
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
	
	private static String get(String url, Collection<KeyVal> parameters) throws IOException{

			if (parameters==null)
				parameters = new ArrayList<KeyVal>();
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
	
	public static String getUser(String name) throws IOException {
		if (name==null) return null;
		if (name.chars().allMatch(Character::isDigit)) return name;
		else {
			List<KeyVal> parameters = new ArrayList<KeyVal>();
			parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[display_name]", name));
		    String response = get("/studioapi/contributors", parameters);
		    return JsonParser.parseContributorId(response);
		}
	}
}
