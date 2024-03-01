package klt.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

	public List<String> getRelatedKeywords(String jsonString) throws JSONException{
		List<String> result = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
		JSONObject meta = obj.getJSONObject("meta");
		JSONArray arr = meta.getJSONArray("related_keywords");
		for (int i = 0; i < arr.length(); i++) {
			 result.add(arr.getString(i));
		}
		return result;
	}
	
    public int getAllMatchesCount(String jsonString) throws JSONException{
    	JSONObject obj = new JSONObject(jsonString);
    	JSONObject meta = obj.getJSONObject("meta");
    	JSONObject pagination = meta.getJSONObject("pagination");
    	return pagination.getInt("total_records");
	}
	
	
	public List<ImageData> parseImagesData(String jsonString) throws JSONException{
		List<ImageData> result = new ArrayList<ImageData>();
		JSONObject obj = new JSONObject(jsonString);

		JSONArray arr = obj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			ImageData imageData = new ImageData();
			
			if (!imageobj.isNull("id"))
				imageData.id = imageobj.getString("id");
			
			if (!imageobj.isNull("type"))
				imageData.type = imageobj.getString("type");
			
			
			JSONObject attr = imageobj.getJSONObject("attributes");
			
			if (!attr.isNull("image_type"))
				imageData.image_type = attr.getString("image_type");
			
			if (!attr.isNull("src"))
				imageData.src = attr.getString("src");
			
			if (!attr.isNull("link"))
				imageData.link = attr.getString("link");
			
			if (!attr.isNull("title"))
				imageData.title = attr.getString("title");
			
			if (!attr.isNull("description"))
				imageData.description = attr.getString("description");
			/*
			if (!attr.isNull("keywords")) {
				JSONArray kwds = attr.getJSONArray("keywords");
				Set<String> keys = new HashSet<String>();
				for (int k=0; k<kwds.length();k++)
					keys.add(kwds.getString(k));
				imageData.setKeywords(keys);
			}
			*/
			result.add(imageData);
		}
		return result;
	}
	
	
	public List<String> parseKeywords(String jsonString, String imageId) throws JSONException{
		List<String> result = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
		JSONArray arr = obj.getJSONArray("sameArtist");
		boolean found = false;
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			String id = imageobj.getString("id");
			if (id.equals(imageId))
			{
				found= true;
				JSONArray kwds = imageobj.getJSONArray("keywords"); 
				for (int k = 0; k < kwds.length(); k++) 
				   result.add(kwds.getString(k));
				return result;
			}
		}
		if (!found)
			return null;
		else 
			return result;
	}
	

	public Set<String> parseKeywords(String jsonString) throws JSONException{
		Set<String> result = new LinkedHashSet<String>();
		JSONObject obj = new JSONObject(jsonString);
		JSONObject imageobj = obj.getJSONObject("data");
		JSONObject attrobj = imageobj.getJSONObject("attributes");	
			if (!attrobj.isNull("keywords")) {
				JSONArray kwds = attrobj.getJSONArray("keywords");
				
				for (int k = 0; k < kwds.length(); k++) 
					   result.add(kwds.getString(k));
				 	}
		return result;
	}
	
	
	public String parseContributorId(String jsonString){
		try {
		JSONObject obj = new JSONObject(jsonString);
		JSONArray arr = obj.getJSONArray("data");
		if (arr==null)
			return null;
		if (arr.isEmpty() && arr.isNull(0))
			return null;
		;
		//JSONObject dataObj = obj.getJSONObject("data");
    	if (!arr.getJSONObject(0).isNull("id"))
			return arr.getJSONObject(0).getString("id");
    	else
    		return null;
		}
		catch (JSONException e) {
			return null;
		}
	}
}