package sm.web;

import java.util.ArrayList;
import java.util.List;

import sm.Data;
import sm.web.SubmitResponse.ItemError;

public class ContentResponse {

	public int success;
	public List<String> saved;
	public List<String> notSaved;
	public String error = "";
	
	public ContentResponse(int success, List<String> saved, List<String> notSaved) {
		this.success = success;
		this.saved = saved;
		this.notSaved = notSaved;
	}


	@Override
	public String toString() {
		return "ContentResponse [success=" + success + ", saved=" + getStringFromListIds(saved) + ", notSaved="+ getStringFromListIds(notSaved) + "]";
	}
	
	private String getStringFromListIds(List<String> list) {
		List<String> result = new ArrayList<String>();
		for (String s:list) 
			result.add(Data.getFileNameById(s));
		return String.join(", ", result);
	}
	
	
}
