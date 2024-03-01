package am.web;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

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

import am.Earning;
import am.JsonParser;
import am.ShutterImage;

public class ShutterProvider {

	String baseURL = "https://submit.shutterstock.com";
	String sessionId;
	public String nextc_sid = "";
	Map<String,String> cookies;
    Map<String,String> headers;
	private String baseURLShutter = "https://www.shutterstock.com";
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
	public ShutterProvider(String sessionId) {
		this.sessionId = sessionId;
		System.out.println(sessionId);
		this.headers = new HashMap<String,String>(){{
			   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			}};
			
		this.cookies = new HashMap<String,String>(){{
			   put("session", sessionId);
			}};
	}
	
	public String getImages(int per_page, int page_number) throws IOException {
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("sort", "newest");
			   put("per_page", String.valueOf(per_page));
			   put("page_number", String.valueOf(page_number));
			}};
			return get("/api/catalog_manager/media_types/all/items", parameters);
	}
	
	public String getImageDetails(long media_id) throws IOException {
		String id = "P" + media_id;
		Map<String,String> parameters = new HashMap<String,String>(){{
			}};
			return get("/api/content_editor/media/" + id, parameters);
	}
	
	
	public List<ShutterImage> getTopPerformers(int per_page, int page_number) throws IOException{
		List<ShutterImage> result = new ArrayList<ShutterImage>();
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("sort_direction", "desc");
			   put("date_range", "0");
			   put("per_page", String.valueOf(per_page));
			   put("page", String.valueOf(page_number));
			}};
		String html = get("/earnings/top-performers", parameters);
		if (html.contains("Found. Redirecting to")) {
			return null;
		}
		Document doc = Jsoup.parse(html);
		String s = "";
		Elements elements = doc.body().getElementById("gallery-table").getElementsByTag("tr");
		for (Element el:elements) {
			if (el.hasAttr("class"))
				continue;
			else {
			    
				String id = el.getElementsByClass("media-description").first().getElementsByTag("a").first().text();
				long media_id = Long.parseLong(id.split(" ")[1]);
				ShutterImage im = new ShutterImage(media_id);
				
				Element downloadsEl = el.getElementsByClass("media-description").first().nextElementSibling();
				String downloads = downloadsEl.text().trim().replaceAll("[^0-9.]", "");
				Element earningsEl = downloadsEl.nextElementSibling();
				String earnings = earningsEl.text().trim().replaceAll("[^0-9.]", "");
				im.setDownloads(Integer.parseInt(downloads));
				im.setEarnings(Double.parseDouble(earnings));
				result.add(im);
			}
		}
		
		return result;
	}
	
	public List<Earning> getDayEarnings(String day) throws IOException{
		List<Earning> earnings = new ArrayList<Earning>();
		
		for (String category:Earning.CATEGORIES) {
			int per_page = 100;
			int page_number = 1;
			String jsonString = getDayEarningsApache(day, category, per_page, page_number);
			if (jsonString.isEmpty())
				continue;
			System.out.println(jsonString);
			int pagesCount = JsonParser.parseDayEarningsPages(jsonString);
			LOGGER.fine("Category: " + category + ", pages=" + pagesCount);
			earnings.addAll(JsonParser.parseDayEarnings(jsonString, day));
			if (pagesCount>1) {
				while (page_number<pagesCount) {
					page_number++;
					jsonString = getDayEarningsApache(day, category, per_page, page_number);
					if (jsonString.isEmpty())
						continue;
					earnings.addAll(JsonParser.parseDayEarnings(jsonString, day));
				}
			}
		}
		LOGGER.fine("Found records for a day: " + earnings.size());
		
		return earnings;
	}
	
	@Deprecated
	private String getDayEarningsOld(String day, String category, int per_page, int page_number) throws IOException{
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("date", day);
			   put("display_column", category);
			   put("page", String.valueOf(page_number));
			   put("per_page", String.valueOf(per_page));
			}};
			
			this.cookies.put("", this.nextc_sid);
		    String result = get("/api/next/v2/earnings/media_stats/day", parameters);
		    if (result.contains("Found. Redirecting to <a href=\"https://contributor-accounts.shutterstock.com/oauth"))
		    	throw new IOException("Incorrect nextc.sid");
			return result;
	}
	
	private String getDayEarningsApache(String day, String category, int per_page, int page_number) throws IOException{
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("date", day);
			   put("display_column", category);
			   put("page", String.valueOf(page_number));
			   put("per_page", String.valueOf(per_page));
			}};
			
		    String result = getApacheGETResponse("/api/next/v2/earnings/media_stats/day", parameters);
		    if (result.isEmpty())
		    	return "";
		    if (result.contains("Found. Redirecting to <a href=\"https://contributor-accounts.shutterstock.com/oauth"))
		    	throw new IOException("Incorrect nextc.sid");
			return result;
	}
	
	
	
	public String getKeywordsTop(long media_id) throws IOException {
			Map<String,String> parameters = new HashMap<String,String>(){{
				   put("ids[]", String.valueOf(media_id));
				}};
			return get("/api/earnings/keywords", parameters);
		}
	
	
	public String getKeywordsTop(List<Long> ids) throws IOException {
        List<KeyVal> parameters = new ArrayList<KeyVal>();
		
		for (long id:ids) {
			parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("ids[]", String.valueOf(id)));
		}
		return get(this.baseURL + "/api/earnings/keywords", parameters);
	}

	

	public String findImages(String userId, int page_size, int page) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[display_name]", "name"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("q", "*"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("language", "en"));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[size]", String.valueOf(page_size)));
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("page[number]", String.valueOf(page)));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "keywords"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "link"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "src"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "image_type"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "displays"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("fields[images]", "alt"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("allow_inject", "true"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("include", "contributor-limited-meta"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("recordActivity", "true"));
	    parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("sort", "newest"));
        parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("filter[submitter]", userId));
		return get(this.baseURLShutter  + "/studioapi/images/search", parameters);
	}
	
	
	
	
	public boolean isConnection() throws IOException {
			String s = get("/upload/portfolio", new HashMap<String,String>());
			if (s.contains("Found. Redirecting to <a href=\"https://contributor-accounts.shutterstock.com/oauth/authorize"))
				throw new IOException ("Ошибка авторизации"); 
		return true;
	}
	
	private String get(String url, Map<String,String> parameters) throws IOException{
		
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
	
	private String get(String url, Collection<KeyVal> parameters) throws IOException{

		if (parameters==null)
			parameters = new ArrayList<KeyVal>();
		return
			  Jsoup.connect(url)
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
	

public byte[] downloadByteArray(String link) throws IOException {
	URL url = new URL(link);
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	InputStream is = null;
	try {
	  is = url.openStream ();
	  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
	  int n;

	  while ( (n = is.read(byteChunk)) > 0 ) {
	    baos.write(byteChunk, 0, n);
	  }
	  return baos.toByteArray();
	}
	catch (IOException e) {
	  throw e;
	}
	finally {
	  if (is != null) { is.close(); }
	}
}

public byte[] downloadImage(String url) throws MalformedURLException, IOException {
	BufferedImage bi = ImageIO.read(new URL(url));
	return toByteArray(bi, "jpg");
}

public static byte[] toByteArray(BufferedImage bi, String format)
        throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }

public void saveStringToFile(String s, String filename) throws IOException {
	Path path  = Paths.get(System.getProperty("user.home"), filename);
	 Files.write(path, s.getBytes(), StandardOpenOption.CREATE);
}


public String getApacheGETResponse( String url, Map<String,String> parameters) throws IOException{
		
		CookieStore cookieStore = new BasicCookieStore(); 
		BasicClientCookie cookie = new BasicClientCookie("", "");
		cookie.setValue("; session= " +this.sessionId + "; nextc.sid=" + this.nextc_sid + "; ");
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
		    		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
		    		.build();
		    	
		    
		    
		    URIBuilder builder;
		    HttpGet httpGet;
			try {
				builder = new URIBuilder(baseURL + url);
			
				for (String key : parameters.keySet()) {
					builder.setParameter(key,parameters.get(key));
				}
		    
		    	httpGet = new HttpGet(builder.build());
		    
			} catch (URISyntaxException e1) {
				return e1.getMessage();
			}
		    
		        //Execute and get the response.
			    System.out.println("SEND REQUEST: " + httpGet.toString());
			    LOGGER.fine("SEND REQUEST: " + httpGet.toString());
			   
			
		        response = httpclient.execute(httpGet,localContext);

		        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		        LOGGER.fine("Response Code : " + response.getStatusLine().getStatusCode());
		        
		        if (response.getStatusLine().getStatusCode()==500) {
		        	return "";
		        }
		        
		        rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        while ((line = rd.readLine()) != null) {
		                        System.out.println(line);
		                        result.append(line);
		        }

	      return result.toString();
	  }
	

}
