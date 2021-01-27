package sm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubmitResponse {
	public List<ShutterImageShort> images = new ArrayList<ShutterImageShort>();
	public List<String> errors = new ArrayList<String>();
    public String first_submit_check_code;
    public String first_submit_check_message;
    public String first_submit_check_original_code;
    
	
	public class ShutterImageShort {
		public String upload_id;
		public String media_id;
		public String media_type;
		public String getUploadId() {
			return upload_id;
		}
	}
	
   public String print() {
	   return "SUBMIT RESPONSE: Errors: " + String.join(", ", errors) + "; Success images IDs: " + String.join(", ",  images.stream().map(ShutterImageShort::getUploadId)
			    .collect(Collectors.toList()) + ", First Submit Check: Code: " + first_submit_check_code + ", Message: " +  first_submit_check_message +
			    ", Original Code: "  + first_submit_check_original_code); 
   }
	
}
