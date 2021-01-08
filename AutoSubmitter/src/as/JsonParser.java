package as;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import as.web.ContentResponse;
import as.web.SubmitResponse;

public class JsonParser {


	public static List<ShutterImage> parseImagesData(String jsonString) {
		List<ShutterImage> result = new ArrayList<ShutterImage>();
		JSONObject obj = new JSONObject(jsonString);

		JSONArray arr = obj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			
			
			String uploaded_filename = "";
			if (!imageobj.isNull("uploaded_filename"))
				uploaded_filename = imageobj.getString("uploaded_filename");
			
			String id = "";
			if (!imageobj.isNull("id"))
				id = imageobj.getString("id");
			
			
			ShutterImage image = new ShutterImage(id, uploaded_filename);
			
			JSONArray catarr = imageobj.getJSONArray("categories");
			for (int j = 0; j < catarr.length(); j++) {
				image.categories.add(catarr.getString(j));
			}
			
			JSONArray keywordsarr = imageobj.getJSONArray("keywords");
			for (int j = 0; j < keywordsarr.length(); j++) {
				image.keywords.add(keywordsarr.getString(j));
			}
			
			JSONArray releasesarr = imageobj.getJSONArray("releases");
			for (int j = 0; j < releasesarr.length(); j++) {
				image.releases.add(releasesarr.getString(j));
			}
			
			
			if (!imageobj.isNull("contributor_id"))
				image.setContributor_id(String.valueOf(imageobj.getInt("contributor_id")));
			else
				image.setContributor_id("");
			
			if (!imageobj.isNull("created"))
				image.setCreated(imageobj.getString("created"));
			else
				image.setCreated("");
			
			
			if (!imageobj.isNull("description"))
				image.setDescription(imageobj.getString("description"));
			else
				image.setDescription("");
			
			if (!imageobj.isNull("thumbnail_url_480"))
				image.setPreviewPath(imageobj.getString("thumbnail_url_480"));
			else 
				image.setPreviewPath("");
			
			if (!imageobj.isNull("is_illustration"))
				image.setIs_illustration(imageobj.getBoolean("is_illustration"));
			else
				image.setIs_illustration(false);
		
			
			if (imageobj.isNull("has_property_release"))
				image.setHas_property_release(false);
			else
				image.setHas_property_release(true);
			
			
			result.add(image);
		}
		return result;
	}
	
	 public static String createContentPayload(Collection<ShutterImage> files) {
		   JSONArray data = new JSONArray();
		   for (ShutterImage file:files) {
			   JSONObject root = new JSONObject();
			   root.put("categories", file.categories);
			   root.put("description", file.getDescription());
			   root.put("id", file.getId());
			   root.put("is_adult", file.getIs_adult());
			   root.put("is_editorial", file.getIs_editorial());
			   root.put("is_illustration", file.getIs_illustration());
			   root.put("keywords", file.keywords);
			   root.put("location", new JSONObject()
					   .put("collected_full_location_string", "")
					   .put("english_full_location", "")
					   .put("external_metadata", "")
					   );
			   root.put("releases", new JSONArray());
			   root.put("submitter_note", "");
			   data.put(root);
		   }
		   System.out.println("JSON CONTENT PAYLOAD: " +  data.toString());
		  return data.toString();
	   }
	 
	 
	public static ContentResponse parseContentResponse(String jsonString) {
		System.out.println("JSON CONtENT RESPONSE: " + jsonString);
		
		JSONObject obj = new JSONObject(jsonString);
        List<String> saved = new ArrayList<String>();
        List<String> notSaved = new ArrayList<String>();
        
		JSONArray savedarr = obj.getJSONArray("saved");
		
		for (int j = 0; j < savedarr.length(); j++) {
			saved.add(savedarr.getString(j));
		}
		
		JSONArray notsavedarr = obj.getJSONArray("notSaved");
		for (int j = 0; j < notsavedarr.length(); j++) {
			notSaved.add(notsavedarr.getString(j));
		}
		
		int success;
		if (!obj.isNull("success"))
			success = obj.getInt("success");
		else
			success = 0;
		
		return new ContentResponse(success, saved, notSaved);
		
	}
	
   public static String createSubmitPayload(Collection<ShutterImage> files) {
	   JSONObject root = new JSONObject();
	   JSONArray media = new JSONArray();
	  
		   for (ShutterImage file:files) {
			   JSONObject mediaObj = new JSONObject();
			   mediaObj.put("media_type", "photo");
			   mediaObj.put("media_id", file.getId());
			   media.put(mediaObj);
		   }
		  root.put("media", media);
		  root.put("keywords_not_to_spellcheck", new JSONArray());
		  root.put("skip_spellcheck", "true");
		  
		  System.out.println("JSON SUBMIT PAYLOAD: " + root.toString());
		  
		  return root.toString();
   }
   
   
	public static SubmitResponse parseSubmitResponse(String jsonString) {
		
		System.out.println("JSON SUBMIT RESPONSE: " + jsonString);
		
		SubmitResponse response = new SubmitResponse();
		
		JSONObject obj = new JSONObject(jsonString);
		JSONObject data = obj.getJSONObject("data");
		JSONArray item_errors_arr = data.getJSONArray("item_errors");
		
		for (int j = 0; j < item_errors_arr.length(); j++) {
			response.errors.add(item_errors_arr.getString(j));
		}
		
		JSONArray success_arr = data.getJSONArray("success");
		for (int j = 0; j < success_arr.length(); j++) {
			JSONObject successobj = success_arr.getJSONObject(j);
			response.images.get(j).media_id = successobj.getString("media_id");
			response.images.get(j).upload_id = successobj.getString("upload_id");
			response.images.get(j).media_type = successobj.getString("media_type");
		}
		
		JSONObject first_submit_check_obj = data.getJSONObject("first_submit_check");
		response.first_submit_check_code = first_submit_check_obj.getString("code");
		response.first_submit_check_message = first_submit_check_obj.getString("message");
		response.first_submit_check_original_code = first_submit_check_obj.getString("original_code");
		
		return response;
		
	}
}