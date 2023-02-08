package am.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

import org.jsoup.Connection;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import am.ShutterImage;

public class ShutterProvider {

	String baseURL = "https://submit.shutterstock.com";
	String sessionId;
	Map<String,String> cookies;
    Map<String,String> headers;
	private String baseURLShutter = "https://www.shutterstock.com";
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
	public ShutterProvider(String sessionId) {
		this.sessionId = sessionId;
		
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
	
	
	
	
	public boolean isConnection() {
		try {
			get("/upload/portfolio", new HashMap<String,String>());
		return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private String get(String url, Map<String,String> parameters) throws IOException{
		Map<String,String> cookiesGet = new HashMap<String,String>(){{
			   put("session", sessionId);
			}};

		Map<String,String> headersGet = new HashMap<String,String>(){{
			}};
			if (parameters==null)
				parameters = new HashMap<String,String>();
		
			return
				  Jsoup.connect(this.baseURL + url )
				  .headers(headersGet)
				  .followRedirects(false)
				  .cookies(cookiesGet)
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



}
