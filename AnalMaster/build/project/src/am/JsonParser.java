package am;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonParser {

	
	
	public static String getUserId(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		JSONObject respHeader = obj.getJSONObject("responseHeader");
		JSONObject params = respHeader.getJSONObject("params");
		JSONArray fq = params.getJSONArray("fq");
		return fq.getString(0).split(":")[1];
	}
	
	public static long getTotalImagesCountFind(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		JSONObject meta = obj.getJSONObject("meta");
		JSONObject pagination = meta.getJSONObject("pagination");
			long result = 0;
			if (!pagination.isNull("total_records"))
				result = pagination.getLong("total_records");
		return result;
	}
	
	
	public static long getTotalImagesCount(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
			long result = 0;
			if (!obj.isNull("total"))
				result = obj.getLong("total");
		return result;
	}
	
	public static List<ShutterImage> parseImagesDataFind(String jsonString) {
		List<ShutterImage> result = new ArrayList<ShutterImage>();
		JSONObject obj = new JSONObject(jsonString);
		JSONArray arr = obj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			if (!imageobj.isNull("id"))
				result.add(new ShutterImage(Long.parseLong(imageobj.getString("id"))));
		}
		return result;
	}
	
	
	public static List<ShutterImage> parseImagesData(String jsonString) {
		List<ShutterImage> result = new ArrayList<ShutterImage>();
		JSONObject obj = new JSONObject(jsonString);

		JSONArray arr = obj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			
			if (!imageobj.isNull("media_id"))
				result.add(new ShutterImage(Long.parseLong(imageobj.getString("media_id"))));
		}
		return result;
	}
	
	public static ShutterImage parseImageDataAndPaste(String jsonString, ShutterImage image) {
		JSONObject obj = new JSONObject(jsonString);

		JSONObject imageobj = obj.getJSONObject("data");
			
			if (!imageobj.isNull("id")) {
				long id = Long.parseLong(imageobj.getString("id").substring(1));
				if (image.getMedia_id()!=id)
					throw new JSONException("Media_id is incorrect. Image media_id = " + image.getMedia_id() + " but json returns " + id);
			}
			

			if (!imageobj.isNull("description"))
				image.setDescription(imageobj.getString("description"));
			else
				image.setDescription("");
			
			if (!imageobj.isNull("is_illustration"))
				image.setIs_illustration(imageobj.getBoolean("is_illustration"));
			else
				image.setIs_illustration(false);
			
			if (!imageobj.isNull("keywords")) {
				JSONArray keywordsarr = imageobj.getJSONArray("keywords");
				for (int j = 0; j < keywordsarr.length(); j++) {
					image.keywords.add(keywordsarr.getString(j));
				}
			}
			if (!imageobj.isNull("large_thumb")) {
				JSONObject previewObject = imageobj.getJSONObject("large_thumb");
				image.setPreviewPath(previewObject.getString("url"));
			}

			if (!imageobj.isNull("original_filename"))
				image.setOriginal_filename(imageobj.getString("original_filename"));
			else
				image.setOriginal_filename("");
			
			
			if (!imageobj.isNull("upload_id"))
				image.setUpload_id(imageobj.getLong("upload_id"));
			else
				image.setUpload_id(0);
			
			if (!imageobj.isNull("uploaded_date"))
				image.setUploaded_date(imageobj.getString("uploaded_date"));
			else
				image.setUploaded_date("");
			
			
		return image;
	}
	
	
	public static ShutterImage parseKeywordsTopAndPaste(String jsonString, ShutterImage image) {
		JSONObject root = new JSONArray(jsonString).getJSONObject(0);
		JSONArray arr = root.getJSONArray("keywords");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject imageobj = arr.getJSONObject(i);
			String keyword = null;
			double percentage = 0.0;
			
			if (!imageobj.isNull("keyword"))
				keyword = imageobj.getString("keyword");
			if (!imageobj.isNull("percentage"))
				percentage = imageobj.getDouble("percentage");
			
			if (keyword==null || percentage==0.0)
				throw new JSONException("Can't get keywords data: " + jsonString);
			else
				image.keywordsRate.put(keyword, percentage);
			}
		return image;
	}
	
	

	public static List<ShutterImage> parseKeywordsTopAndPasteToMultiple(String jsonString) {
		JSONArray rootArray = new JSONArray(jsonString);
		List<ShutterImage> images = new ArrayList<ShutterImage>();
        for (int a = 0; a < rootArray.length(); a++) {		
        	JSONObject root = rootArray.getJSONObject(a);
        	ShutterImage im = new ShutterImage(Long.parseLong(root.getString("media_id")));
        			JSONArray arr = root.getJSONArray("keywords");
        	for (int i = 0; i < arr.length(); i++) {
        		JSONObject imageobj = arr.getJSONObject(i);
        		String keyword = null;
        		double percentage = 0.0;
			
        		if (!imageobj.isNull("keyword"))
        			keyword = imageobj.getString("keyword");
        		if (!imageobj.isNull("percentage"))
        			percentage = imageobj.getDouble("percentage");
			
        		if (keyword==null || percentage==0.0)
        			throw new JSONException("Can't get keywords data: " + jsonString);
        		else
        			im.keywordsRate.put(keyword, percentage);
			}
        	images.add(im);
        }
		return images;
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
	
	
	public static int parseDayEarningsPages(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		return obj.getInt("pages");
	}
	
	
	public static List<Earning> parseDayEarnings(String jsonString, String day) {
		List<Earning> result = new ArrayList<Earning>();
		JSONObject obj = new JSONObject(jsonString);
		JSONArray arr = obj.getJSONArray("media");
		for (int i = 0; i < arr.length(); i++) {
			String category = arr.getJSONObject(i).getString("category");
			long media_id = arr.getJSONObject(i).getLong("mediaId");
			int count =  arr.getJSONObject(i).getInt("count");
			double total =  arr.getJSONObject(i).getDouble("total");
			result.add(new Earning(media_id, day, category, count, total));
		}
		return result;
	}
	
}