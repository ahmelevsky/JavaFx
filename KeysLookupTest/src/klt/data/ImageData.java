package klt.data;

import java.util.ArrayList;
import java.util.List;

public class ImageData {

	public String  id;
	public String type;
	public String title;
	public String description;
	public String image_type;
	public String src;
	public String link;
	public List<String> keywords = new ArrayList<String>();
	
	@Override
	public String toString() {
		return "ImageData [id=" + id + ", type=" + type + ", title=" + title + ", description=" + description
				+ ", image_type=" + image_type + ", src=" + src + "]";
	}
	
	
	
}
