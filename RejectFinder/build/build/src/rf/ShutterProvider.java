package rf;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOException;

import org.jsoup.Connection;
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
	
	
	public String getRejects(int per_page, int page) {
		Map<String,String> parameters = new HashMap<String,String>(){{
			   put("order", "newest");
			   put("per_page", String.valueOf(per_page));
			   put("page", String.valueOf(page));
			   put("xhr_id", "1");
			   put("status", "rejected");
			}};
		try {
			Connection.Response resp=	get("/api/content_editor/photo", parameters);
			return resp.body();
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
	
	
	
	
	
	private Connection.Response get(String url, Map<String,String> parameters) throws IOException{
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
				  .execute();
		 
	}
	
}
