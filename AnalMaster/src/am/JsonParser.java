package am;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


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
			
			JSONArray keywordsarr = imageobj.getJSONArray("keywords");
			for (int j = 0; j < keywordsarr.length(); j++) {
				image.keywords.add(keywordsarr.getString(j));
			}
			
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
		
			
			
			result.add(image);
		}
		return result;
	}
	
	 public static String createContentPayload(Collection<ShutterImage> files) {
		   JSONArray data = new JSONArray();
		   for (ShutterImage file:files) {
			   JSONObject root = new JSONObject();
			   root.put("description", file.getDescription());
			   root.put("id", file.getId());
			   root.put("is_illustration", file.getIs_illustration());
			   root.put("keywords", file.keywords);
			   root.put("location", new JSONObject()
					   .put("collected_full_location_string", "")
					   .put("english_full_location", "")
					   .put("external_metadata", "")
					   );
			   root.put("submitter_note", "");
			   data.put(root);
		   }
		   System.out.println("JSON CONTENT PAYLOAD: " +  data.toString());
		  return data.toString();
	   }
	 
   public static String createSubmitPayload(Collection<ShutterImage> files) {
	   JSONObject root = new JSONObject();
	   JSONArray media = new JSONArray();
	   JSONArray notSpellCheckArray = new JSONArray();
	   
		   for (ShutterImage file:files) {
			   JSONObject mediaObj = new JSONObject();
			   mediaObj.put("media_type", "photo");
			   mediaObj.put("media_id", file.getId());
			   media.put(mediaObj);
			//   for (String k:file.keywords)
				 //  notSpellCheckArray.put(k);
		   }
		  root.put("media", media);
		
		  root.put("keywords_not_to_spellcheck", notSpellCheckArray);
		  root.put("skip_spellcheck", "true");
		  
		  System.out.println("JSON SUBMIT PAYLOAD: " + root.toString());
		  
		  return root.toString();
   }
   
	
	public static String joinJsonArrays(List<String> arrays) {
		JSONArray destinationArray = new JSONArray();
		for (String ar:arrays) {
			JSONObject obj = new JSONObject(ar);
			JSONArray data = obj.getJSONArray("data");
			for (int i=0;i<data.length();i++)
				destinationArray.put(data.get(i));
		}
		JSONObject root = new JSONObject();
		root.put("data",destinationArray);
		System.out.println("DESTINATION RELEASES JSON" + root.toString());
		return root.toString();
	}

	public static List<String> getFileNames(String jsonString) {
		List<String> result = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
        String pageName = obj.getJSONObject("data").getString("pageName");

		JSONArray arr = obj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++) {
			String filename = arr.getJSONObject(i).getString("original_filename");
			result.add(filename);
		}
		return result;
	}
	
}