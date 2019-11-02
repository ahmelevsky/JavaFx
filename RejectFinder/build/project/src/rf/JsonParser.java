package rf;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParser {

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
	
	public static List<ShutterImage> parseImagesData(String jsonString) {
		List<ShutterImage> result = new ArrayList<ShutterImage>();
		JSONObject obj = new JSONObject(jsonString);

		JSONArray arr = obj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			JSONArray reasonsarr = imageobj.getJSONArray("reasons");
			List<String> reasons = new ArrayList<String>();
			
			for (int j = 0; j < reasonsarr.length(); j++) {
				reasons.add(reasonsarr.getJSONObject(j).getString("reason"));
			}
			
			String filename = imageobj.getString("original_filename");
			
			int media_id = 0;
			if (!imageobj.isNull("media_id"))
				media_id = imageobj.getInt("media_id");
			
			String media_type = "";
			if (!imageobj.isNull("media_type"))
				media_type = imageobj.getString("media_type");
			
			String uploaded_date = "";
			if (!imageobj.isNull("uploaded_date"))
				uploaded_date = imageobj.getString("uploaded_date");
			
			String original_filename = "";
			if (!imageobj.isNull("original_filename"))
				original_filename = imageobj.getString("original_filename");
			
			String verdict_time = "";
			if (!imageobj.isNull("verdict_time"))
				verdict_time = imageobj.getString("verdict_time");
			
			
			ShutterImage image = new ShutterImage(media_id, media_type, reasons,
					uploaded_date, original_filename, verdict_time);
			
			if (!imageobj.isNull("description"))
				image.setDescription(imageobj.getString("description"));
			else
				image.setDescription("");
			if (!imageobj.isNull("thumbnail_url_480"))
				image.setPreviewPath(imageobj.getString("thumbnail_url_480"));
			else 
				image.setPreviewPath("");
			
			if (!imageobj.isNull("submitter_note"))
				image.setSubmitter_note(imageobj.getString("submitter_note"));
			else
				image.setSubmitter_note("");
			
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
	
	
}