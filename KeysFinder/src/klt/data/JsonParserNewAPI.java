package klt.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParserNewAPI extends JsonParser {

	
	public List<ImageData> parseImagesData(String jsonString) throws JSONException{
		List<ImageData> result = new ArrayList<ImageData>();
		JSONObject obj = new JSONObject(jsonString);
		JSONObject objProps = obj.getJSONObject("pageProps");
		
		JSONArray arr = objProps.getJSONArray("assets");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			ImageData imageData = new ImageData();
			
			if (!imageobj.isNull("id"))
				imageData.id = imageobj.getString("id");
			
			if (!imageobj.isNull("type"))
				imageData.type = imageobj.getString("type");
			
			if (!imageobj.isNull("imageType"))
				imageData.image_type = imageobj.getString("imageType");
			
			if (!imageobj.isNull("src"))
				imageData.src = imageobj.getString("src");
			
			if (!imageobj.isNull("link"))
				imageData.link = imageobj.getString("link");
			
			if (!imageobj.isNull("title"))
				imageData.title = imageobj.getString("title");
			
			if (!imageobj.isNull("description"))
				imageData.description = imageobj.getString("description");
			result.add(imageData);
		}
		return result;
	}
	
	
    public int getAllMatchesCount(String jsonString) throws JSONException{
    	JSONObject obj = new JSONObject(jsonString);
    	JSONObject objProps = obj.getJSONObject("pageProps");
    	JSONObject meta = objProps.getJSONObject("meta");
    	JSONObject pagination = meta.getJSONObject("pagination");
    	return pagination.getInt("totalRecords");
	}
    
    
	public List<String> getRelatedKeywords(String jsonString) throws JSONException{
		List<String> result = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
		JSONObject objProps = obj.getJSONObject("pageProps");
		JSONObject meta = objProps.getJSONObject("meta");
		JSONArray arr = meta.getJSONArray("relatedKeywords");
		for (int i = 0; i < arr.length(); i++) {
			 result.add(arr.getString(i));
		}
		return result;
	}
	
	
}
