package sm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import sm.web.ContentResponse;
import sm.web.SubmitResponse;

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
			   root.put("releases", file.releases);
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
        
       
        
        if (obj.has("saved")) {
		JSONArray savedarr = obj.getJSONArray("saved");
		
		for (int j = 0; j < savedarr.length(); j++) {
			saved.add(savedarr.getString(j));
		}
        }
        if (obj.has("notSaved")) {
		JSONArray notsavedarr = obj.getJSONArray("notSaved");
		for (int j = 0; j < notsavedarr.length(); j++) {
			notSaved.add(notsavedarr.getString(j));
		}
        }
		int success;
		if (!obj.isNull("success"))
			success = obj.getInt("success");
		else
			success = 0;
		ContentResponse response = new ContentResponse(success, saved, notSaved);
		if (obj.has("error")) 
	    		response.error = obj.getString("error");
		 
		return response;
		
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
			JSONObject errorObj = item_errors_arr.getJSONObject(j);
			SubmitResponse.ItemError itemError = response.getItemError();
			itemError.upload_id = errorObj.getString("upload_id");
			JSONObject validation_errors = errorObj.getJSONObject("validation_errors");
			JSONObject details = validation_errors.getJSONObject("details");
			JSONObject keywords = details.getJSONObject("keywords");
			StringJoiner  errorMsg =  new StringJoiner (", ");
			if (keywords.getBoolean("hard_validation_failure"))
				errorMsg.add("Keywords error: " + keywords.getString("message"));
			JSONObject title = details.getJSONObject("title");
			if (title.getBoolean("hard_validation_failure"))
				errorMsg.add("Title error: " + title.getString("message"));
			itemError.message = errorMsg.toString();
		}
		
		
		JSONArray success_arr = data.getJSONArray("success");
		for (int j = 0; j < success_arr.length(); j++) {
			JSONObject successobj = success_arr.getJSONObject(j);
			SubmitResponse.ShutterImageShort successImage = response.getSuccessImage();
			successImage.media_id = successobj.getNumber("media_id").toString();
			successImage.upload_id = successobj.getString("upload_id");
			successImage.media_type = successobj.getString("media_type");
		}
		
		if (!data.isNull("batch_error")) {
			JSONObject batch_error  = data.getJSONObject("batch_error");
			response.batch_error_code = batch_error.getString("code");
			response.batch_error_message= batch_error.getString("message");
		}
		if (!data.isNull("first_submit_check")) {
			JSONObject first_submit_check_obj = data.getJSONObject("first_submit_check");
			response.first_submit_check_code = first_submit_check_obj.getString("code");
			response.first_submit_check_message = first_submit_check_obj.getString("message");
			response.first_submit_check_original_code = first_submit_check_obj.getString("original_code");
		}
		return response;
		
	}
	
	public static List<PropertyRelease> parsePropertyReleases(String jsonString) throws ParseException{
		List<PropertyRelease> releases  = new ArrayList<PropertyRelease>();
		JSONObject obj = new JSONObject(jsonString);
		JSONArray data = obj.getJSONArray("data");
		for (int j = 0; j < data.length(); j++) {
			JSONObject releaseObj = data.getJSONObject(j);
			String id = releaseObj.getString("id");
			String name = releaseObj.getString("name");
			String workflow_status = releaseObj.getString("workflow_status");
			String ext = releaseObj.getString("ext");
			String visible = releaseObj.getString("visible");
			String added_date = releaseObj.getString("added_date");
			String type = releaseObj.getString("type");
			releases.add(new PropertyRelease(workflow_status, ext, visible, added_date, name, id, type));
		}
		return releases;
	}
	
	
	public static List<Category> parseCaterogies(String jsonString) {
		List<Category> categories  = new ArrayList<Category>();
		JSONObject obj = new JSONObject(jsonString);
		JSONArray data = obj.getJSONArray("data");
		for (int j = 0; j < data.length(); j++) {
			JSONObject categoryObj = data.getJSONObject(j);
			String cat_id = categoryObj.getNumber("cat_id").toString();
			String name = categoryObj.getString("name");
			String code = categoryObj.getString("code");
			categories.add(new Category(cat_id, name, code));
		}
		return categories;
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

	
	public static List<FileRule> parseFileRules(String jsonString){
		 List<FileRule> rules = new ArrayList<FileRule>();
		 JSONArray data = new JSONArray(jsonString);
		 for (int i = 0; i < data.length(); i++) {
			 JSONObject ruleObj = data.getJSONObject(i);
			 FileRule rule = new FileRule(ruleObj.getString("file"));
			 rule.isIllustration = ruleObj.getBoolean("isillustration");
  			 JSONArray categoriesArr = ruleObj.getJSONArray("categories");
				for (int j = 0; j < categoriesArr.length(); j++) {
					rule.categories.add(categoriesArr.getString(j));
				}
			JSONArray releasesArr = ruleObj.getJSONArray("releases");
				for (int j = 0; j < releasesArr.length(); j++) {
					rule.releases.add(releasesArr.getString(j));
				}
			rules.add(rule);
		 }
		 return rules;
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
	
	public static List<ShutterImageRejected> parseImagesRejectedData(String jsonString) {
		List<ShutterImageRejected> result = new ArrayList<ShutterImageRejected>();
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
			
			long media_id = 0;
			if (!imageobj.isNull("media_id"))
				media_id = imageobj.getLong("media_id");
			
			
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
			
			
			ShutterImageRejected image = new ShutterImageRejected(media_id, media_type, reasons,
					uploaded_date, original_filename, verdict_time);
			
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
			
			if (!imageobj.isNull("submitter_note"))
				image.setSubmitter_note(imageobj.getString("submitter_note"));
			else
				image.setSubmitter_note("");
			
			if (!imageobj.isNull("is_illustration"))
				image.setIs_illustration(imageobj.getBoolean("is_illustration"));
			else
				image.setIs_illustration(false);
			
			if (!imageobj.isNull("status"))
				image.setStatus(imageobj.getString("status"));
			else
				image.setStatus("");
			
			if (imageobj.isNull("has_property_release"))
				image.setHas_property_release(false);
			else
				image.setHas_property_release(true);
			
			if (!imageobj.isNull("upload_id"))
				image.setUpload_id(imageobj.getLong("upload_id"));
			
			
			result.add(image);
		}
		return result;
	}
	
}