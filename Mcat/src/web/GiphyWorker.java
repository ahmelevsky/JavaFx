package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.KeyVal;

public class GiphyWorker {
	static Map<String,String> cookies;
	static Map<String,String> headers;
	static String baseURL = "http://api.giphy.com/v1";
	static String apiKey = "GkePmu94vQHnrdHI1JwKZ9hlf7Cmsnpg";
    
	static {
		headers = new HashMap<String,String>(){{
			   put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			}};
		cookies = new HashMap<String,String>(){{
			}};
	}
	
	public static List<String> getImagesList(String jsonString){
		List<String> res = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
		JSONArray data = obj.getJSONArray("data");
		for (int i = 0; i < data.length(); i++) {
			JSONObject images = data.getJSONObject(i).getJSONObject("images");
			//JSONObject object = images.getJSONObject("fixed_height_small");
			JSONObject object = images.getJSONObject("original");
			res.add(object.getString("url"));
		}
    	return res;
	}
	
	
	public static String getRandomImage(String jsonString){
		List<String> res = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
		JSONObject data = obj.getJSONObject("data");
		JSONObject images = data.getJSONObject("images");
		JSONObject object = images.getJSONObject("original");
		return object.getString("url");
	}
	
	
	public static String searchSticker(String keyword) throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("q", keyword));
		//parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("tbm", "isch"));
		//parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("ijn", "0"));
		//return get("/stickers/search", parameters);
		return get("/gifs/search", parameters);
	}
	
	public static String searchRandomSticker() throws IOException {
		List<KeyVal> parameters = new ArrayList<KeyVal>();
		return get("/gifs/random", parameters);
	}
	
	
	private static String get(String url, Collection<KeyVal> parameters) throws IOException{

		if (parameters==null)
			parameters = new ArrayList<KeyVal>();
		
		parameters.add(org.jsoup.helper.HttpConnection.KeyVal.create("api_key", apiKey));
		
		return
			  Jsoup.connect(baseURL + url)
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
