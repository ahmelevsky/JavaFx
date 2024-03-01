package klt.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection.KeyVal;

import klt.workers.ImagesType;

public class ShutterProviderNewApi extends ShutterProvider {

	
	private String newApiString = "0DAcVsesQdyAehy-dhlNe";
	
	public ShutterProviderNewApi(String string) {
		super(string);
	}

	public String findImages(String query, String user, ImagesType type, int page, int recordsCount) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		query = query.replaceAll(" +", " ");
		query = query.replace(" ", "-");
	//	parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[display_name]", "name"));
		
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("term", query));
	//	parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("language", "en"));
	//	parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[size]", String.valueOf(recordsCount)));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page", String.valueOf(page)));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "keywords"));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "link"));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "src"));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "image_type"));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "displays"));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "alt"));
	   // parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("sort", "popular"));
			
			 if (user!=null) {
				 parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("contributor", user));
				  // String userId = getUser(user);
				  // if (userId != null ) 
					//   parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("contributor", userId));
			   }
			
			 switch (type) {
				case PHOTOS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("image_type", "photo"));
					break;
				case VECTORS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("image_type", "vector"));
					break;
				case ILLUSTRATIONS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("image_type", "illustration"));
					break;
				case VECTORSILLUSTRATIONS:
					parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("image_type", "illustration"));
					break;
				}
			//System.out.println(get("/studioapi/images/search", parameters));
			return getApacheGETResponse("/_next/data/" + newApiString + "/en/_shutterstock/search/" + query + ".json", parameters);
	}
	
	

public String getApacheGETResponse(String url, List<KeyVal> parameters) {
		
		    BufferedReader rd = null;
		    StringBuffer result = new StringBuffer();
		    String line = "";
		    HttpResponse response = null;
		    
		    HttpClient httpclient =  HttpClientBuilder.create()
		    		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36").build();
		    
		    URIBuilder builder;
			try {
				builder = new URIBuilder(baseURL + url);
			} catch (URISyntaxException e1) {
				return null;
			}
		    
		    for (KeyVal parameter:parameters) 
		    	builder.setParameter(parameter.key(), parameter.value());

		    HttpGet httpGet;
			try {
				httpGet = new HttpGet(builder.build());
			} catch (URISyntaxException e1) {
				return null;
			}
		   
			CookieStore cookieStore = new BasicCookieStore(); 
		    HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		    
		    
		    try{            
		        //Execute and get the response.
		        response = httpclient.execute(httpGet,localContext);

		        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		        rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        while ((line = rd.readLine()) != null) {
		                        System.out.println(line);
		                        result.append(line);
		        }

		    }catch(Exception e){
		    	  return null;
		    }
	    return result.toString();
	  }
	
	
}
