package mc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.KeyVal;

public class WebWorker {

	static Map<String,String> cookies;
	static Map<String,String> headers;
	
    
	static {
		headers = new HashMap<String,String>(){{
			   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			}};
		cookies = new HashMap<String,String>(){{
			}};
	}
	/*
	public List<String> getImagesList(String jsonString){
		JSONObject obj = new JSONObject(jsonString);
    	JSONObject meta = obj.getJSONObject("meta");
    	JSONObject pagination = meta.getJSONObject("pagination");
    	return pagination.getInt("total_records");
	}
	*/
	
	public static String getImages(String keyword) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("q", keyword));
		return get("https://serpapi.com/search", parameters);
	}
	
	
	private static String get(String url, Collection<KeyVal> parameters) throws IOException{

		if (parameters==null)
			parameters = new ArrayList<KeyVal>();
		return
			  Jsoup.connect(url )
			 // .headers(headers)
			  .followRedirects(false)
			//  .cookies(cookies)
			  .method(Connection.Method.GET)
			  .validateTLSCertificates(false)
			  .ignoreContentType(true)
			  .data(parameters)
			  .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			  .execute().body();
		
}
	
	
}
