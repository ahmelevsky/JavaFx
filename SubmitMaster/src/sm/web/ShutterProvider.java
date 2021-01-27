package sm.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection;
import org.jsoup.Connection.KeyVal;

import sm.JsonParser;
import sm.ShutterImage;

import org.jsoup.Jsoup;

public class ShutterProvider {

	String baseURL = "https://submit.shutterstock.com";
	String sessionId;
	Map<String,String> cookies;
    Map<String,String> headers;
    
	public ShutterProvider(String sessionId) {
		this.sessionId = sessionId;
		
		this.headers = new HashMap<String,String>(){{
			   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			}};
			
		this.cookies = new HashMap<String,String>(){{
			   put("session", sessionId);
			}};
	}
	
	
	public String getPropertyReleasesList(int per_page, int page) {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("visible", "true"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("per_page", String.valueOf(per_page)));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page", String.valueOf(page)));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("sort", "az"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("type", "both"));
		
		try {
			return	get("/api/releases", parameters);
		} catch (IOException e) {
			return null;
		}
	}
	
	
	public String getLoadedFilesList(int per_page, int page) {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("order", "newest"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("per_page", String.valueOf(per_page)));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page", String.valueOf(page)));
	//	parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("status", "edit"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("status", "edit"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("xhr_id", "1"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "keywords"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "link"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "src"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "image_type"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "displays"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "alt"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "has_model_release"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "has_property_release"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "aspect"));
	    //parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "is_editorial"));
		
		try {
			return	get("/api/content_editor/photo", parameters);
		} catch (IOException e) {
			return null;
		}
	}
	
	
	public boolean isConnection() {
		try {
			get("/upload/portfolio", null);
		return true;
		} catch (IOException e) {
			return false;
		}
	}
	public ContentResponse contentPost(Collection<ShutterImage> files) throws IOException {
		String json = JsonParser.createContentPayload(files);
		String res = getApachePatchResponse("/api/content_editor", json);
		return JsonParser.parseContentResponse(res);
	}
	
	public SubmitResponse submitPost(Collection<ShutterImage> files) throws IOException {
		String json = JsonParser.createSubmitPayload(files);
		return JsonParser.parseSubmitResponse(getApachePostResponse("/api/content_editor/submit", json));
	}
	
	private String get(String url, Collection<KeyVal> parameters) throws IOException{

		if (parameters==null)
			parameters = new ArrayList<KeyVal>();
		return
			  Jsoup.connect(this.baseURL + url )
			  .headers(this.headers)
			  .followRedirects(false)
			  .cookies(this.cookies)
			  .method(Connection.Method.GET)
			  .validateTLSCertificates(false)
			  .ignoreContentType(true)
			  .data(parameters)
			  .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			  .execute().body();
		
}
	
	
	
	private String post(String url, String payload) throws IOException{

			return  Jsoup.connect(this.baseURL + url ).timeout(60000)
			  .headers(this.headers)
			  .followRedirects(false)
			  .cookies(this.cookies)
			  .method(Connection.Method.POST)
			  .validateTLSCertificates(false)
			  .ignoreContentType(true)
			  .requestBody(payload)
			  .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			  .execute().body();
		
}
	

	public String getApachePatchResponse(String url, String payload) throws IOException {
		
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("session", this.sessionId);
		cookie.setPath("/");
		cookie.setDomain(".shutterstock.com");
		cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
		cookieStore.addCookie(cookie);
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		BufferedReader rd = null;
		StringBuffer result = new StringBuffer();
		String line = "";
		HttpResponse response = null;

		HttpClient httpclient = HttpClientBuilder.create().setUserAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
				.build();
		HttpPatch httpPatch = new HttpPatch(baseURL + url);

		StringEntity params = new StringEntity(payload);
		params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		httpPatch.setEntity(params);

		response = httpclient.execute(httpPatch, localContext);

		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
			result.append(line);
		}

		return result.toString();
	}
	

	public String getApachePostResponse(String url, String payload) throws IOException {
		
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("session", this.sessionId);
		cookie.setPath("/");
		cookie.setDomain(".shutterstock.com");
		cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
		cookieStore.addCookie(cookie);
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		BufferedReader rd = null;
		StringBuffer result = new StringBuffer();
		String line = "";
		HttpResponse response = null;

		HttpClient httpclient = HttpClientBuilder.create().setUserAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
				.build();
		HttpPost httpPost = new HttpPost(baseURL + url);

		StringEntity params = new StringEntity(payload);
		params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		httpPost.setEntity(params);

		response = httpclient.execute(httpPost, localContext);

		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
			result.append(line);
		}

		return result.toString();
	}
	
		
	
public String getApacheGETResponse( String url) throws URISyntaxException{
		
		CookieStore cookieStore = new BasicCookieStore(); 
		BasicClientCookie cookie = new BasicClientCookie("session", this.sessionId);
		cookie.setPath("/");
		cookie.setDomain(".shutterstock.com");
		cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
		cookieStore.addCookie(cookie); 
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		
		    BufferedReader rd = null;
		    StringBuffer result = new StringBuffer();
		    String line = "";
		    HttpResponse response = null;
		    
		    HttpClient httpclient =  HttpClientBuilder.create()
		    		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36").build();
		    
		    URIBuilder builder = new URIBuilder(baseURL + url);
		    builder.setParameter("order", "newest")
		    	.setParameter("per_page", "100")
		    	.setParameter("page", "1")
		    	.setParameter("status", "edit")
		    	.setParameter("xhr_id", "finish")
		    	.setParameter("fields[images]", "keywords");
		    HttpGet httpGet = new HttpGet(builder.build());
		   
		 
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

		    }
	      return null;
	  }
	
	
}
