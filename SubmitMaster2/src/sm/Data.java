package sm;

import javafx.collections.ObservableList;

public class Data {

	public static Main app;
	public static  ObservableList<ShutterImage> images;
	
	
	public static String getFileNameById(String id) {
		ShutterImage image = images.stream().filter(im->im.getId().equals(id)).findAny().orElse(null);
		if (image==null)
			return id;
		else 
			return image.getUploaded_filename();
	}
}
