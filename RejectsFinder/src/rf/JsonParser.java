package rf;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParser {

	public static List<String> getFileNames(String jsonString) {
		List<String> result = new ArrayList<String>();
		JSONObject obj = new JSONObject(jsonString);
//String pageName = obj.getJSONObject("data").getString("pageName");

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
			result.add(new ShutterImage(imageobj.getInt("media_id"), imageobj.getString("media_type"), reasons,
					imageobj.getString("uploaded_date"), imageobj.getString("original_filename"), imageobj.getString("verdict_time")));
		}
		return result;
	}
	
	
}