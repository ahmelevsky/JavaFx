package sm.web;

import java.util.List;

public class ContentResponse {

	public int success;
	public List<String> saved;
	public List<String> notSaved;
	
	
	public ContentResponse(int success, List<String> saved, List<String> notSaved) {
		this.success = success;
		this.saved = saved;
		this.notSaved = notSaved;
	}


	@Override
	public String toString() {
		return "ContentResponse [success=" + success + ", saved=" + String.join(", ", saved) + ", notSaved="+ String.join(", ", notSaved) + "]";
	}
	
	
	
}
