package sm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sm.Data;
import sm.ShutterImage;

public class SubmitResponse {
	public List<ShutterImageShort> successImages = new ArrayList<ShutterImageShort>();
	public List<ItemError> itemErrors = new ArrayList<ItemError>();
    public String first_submit_check_code;
    public String first_submit_check_message;
    public String first_submit_check_original_code;
    public String batch_error_code;
    public String batch_error_message;
    
    public ShutterImageShort getSuccessImage() {
    	ShutterImageShort image = new ShutterImageShort();
    	successImages.add(image);
    	return image;
    }
    
    public ItemError getItemError() {
    	ItemError error = new ItemError();
    	itemErrors.add(error);
    	return error;
    }
	
	public class ShutterImageShort {
		public String upload_id;
		public String media_id;
		public String media_type;
		public String getUploadId() {
			return upload_id;
		}
	}
	
	public class ItemError {
		public String upload_id;
		public String message;
		public String getUploadId() {
			return upload_id;
		}
		public String getMessage() {
			return message;
		}
		
		public String getIdAndMessage() {
			return "Image ID: " + upload_id + ", Error: " + message;
		}
	}
	
	/*
	@Override
	public String toString() {
	   return "SUBMIT RESPONSE: Error images IDs: " + String.join(", ", itemErrors.stream().map(ItemError::getUploadId).collect(Collectors.toList())) + "; Success images IDs: " + String.join(", ",  successImages.stream().map(ShutterImageShort::getUploadId)
			    .collect(Collectors.toList())) + ", First Submit Check: Code: " + first_submit_check_code + ", Message: " +  first_submit_check_message +
			    ", Original Code: "  + first_submit_check_original_code; 
   }
	*/
	
	
	@Override
	public String toString() {
	   return "SUBMIT RESPONSE: Error images: " +  getErrorItems() + "; Success images IDs: " +  getSucessItems() 
			    + ", First Submit Check: Code: " + first_submit_check_code + ", Message: " +  first_submit_check_message +
			    ", Original Code: "  + first_submit_check_original_code; 
   }
	
	private String getErrorItems() {
		List<String> result = new ArrayList<String>();
		for (ItemError ie:itemErrors) 
			result.add(Data.getFileNameById(ie.getUploadId()));
		return String.join(", ", result);
	}
	
	private String getSucessItems() {
		List<String> result = new ArrayList<String>();
		for (ShutterImageShort  sis:successImages) 
			result.add(Data.getFileNameById(sis.getUploadId()));
		return String.join(", ", result);
	}
	
	
}
