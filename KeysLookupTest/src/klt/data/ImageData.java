package klt.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ImageData {

	public String  id;
	public String type;
	public String title;
	public String description;
	public String image_type;
	public String src;
	public String link;
	public Set<String> keywords = new LinkedHashSet<String>();
	public boolean selected;
	
	@Override
	public String toString() {
		return "ImageData [id=" + id + ", type=" + type + ", title=" + title + ", description=" + description
				+ ", image_type=" + image_type + ", src=" + src + "]";
	}
	
	
	
}
