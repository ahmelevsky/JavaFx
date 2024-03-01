package klt.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
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
	private String newApiString;
	
    
	static {
		headers = new HashMap<String,String>(){{
			   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			}};
		cookies = new HashMap<String,String>(){{
			}};
	}
	
	
	public ShutterProvider(String string) {
		super();
		this.newApiString = string;
	}

	/*
	public String findImages(String query, String user, ImagesType type, int page) throws IOException {
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
	*/
	
	
	
	
	public String findImages(String query, String user, ImagesType type, int page, int recordsCount) throws IOException {
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
	
	public Set<String> getKeywords(String link) throws IOException {
	    	Set<String> result = new LinkedHashSet<String>();
	    	/*
	    	Document doc = Jsoup.connect(baseURL + link )
			  .headers(headers)
			  .followRedirects(false)
			  .cookies(cookies)
			  .method(Connection.Method.GET)
			  .validateTLSCertificates(false)
			  .ignoreContentType(true)
			  .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			  .get();
	    	 */
			
	    	String html = "";
			try {
				html = getApacheGETResponse(link);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	Document doc= Jsoup.parse(html);
	    	//writeToFile("D:\\imagedata.html",html);
	    	
	    	//Elements elems = doc.select("div.MuiCollapse-root > div.MuiCollapse-wrapper > div.MuiCollapse-wrapperInner > div > a");
	    	Elements elems = doc.select("div.MuiCollapse-root");
	    	
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
	
	

	public String getKeywordsJson(String id) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("recordActivity", "false"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("include", "categories,contributor.categories,contained-in-collections.categories,same-model.categories,visually-similar.categories,visually-similar-videos.categories,image-scores,contributor-settings"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[contained-in-collections][size]", "1"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("language", "en"));
		return get("/napi/images/"+id, parameters);
		
}
	
	
	public String getKeywordsJsonOldApi(String id) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "keywords"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("language", "en"));
		return get("/studioapi/images/"+id, parameters);
		
}


	
	
public String getApacheGETResponse( String url) throws URISyntaxException{
		
		
		    BufferedReader rd = null;
		    StringBuffer result = new StringBuffer();
		    String line = "";
		    HttpResponse response = null;
		    
		    HttpClient httpclient =  HttpClientBuilder.create()
		    		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36").build();
		    
		    URIBuilder builder = new URIBuilder(baseURL + url);
		  /*
		    builder.setParameter("order", "newest")
		    	.setParameter("per_page", "100")
		    	.setParameter("page", "1")
		    	.setParameter("status", "edit")
		    	.setParameter("xhr_id", "finish")
		    	.setParameter("fields[images]", "keywords");
		    */
		    HttpGet httpGet = new HttpGet(builder.build());
		   
		    try{            
		        //Execute and get the response.
		        response = httpclient.execute(httpGet);
		        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		        rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        while ((line = rd.readLine()) != null) {
		                        //System.out.println(line);
		                        result.append(line);
		        }

		    }catch(Exception e){

		    }
		    
		    return result.toString();
	  }
	
	

	  private void writeToFile(String fileName, String data) {
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
	
	protected String get(String url, Collection<KeyVal> parameters) throws IOException{

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
	
	public String getUser(String name) throws IOException {
		if (name==null) return null;
		if (name.chars().allMatch(Character::isDigit)) return name;
		else {
			List<KeyVal> parameters = new ArrayList<KeyVal>();
			parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[display_name]", name));
		    String response = get("/studioapi/contributors", parameters);
		    return new JsonParser().parseContributorId(response);
		}
	}
}
