package klt.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import klt.web.ShutterProvider;

public class DataProvider {

	public static List<ImageData> findImagesAll(String query) {
		List<ImageData> data = new ArrayList<ImageData>();
		
		
		
		return data;
	}
	
	
	public static List<ImageData> findImagesPhotos(String query) {
		List<ImageData> data = new ArrayList<ImageData>();
		
		
		
		return data;
	}
	
	
	public static List<ImageData> findImagesVector(String query) {
		List<ImageData> data = new ArrayList<ImageData>();
		
		
		
		return data;
	}
	
	public static List<ImageData> findImagesIllustration(String query) {
		List<ImageData> data = new ArrayList<ImageData>();
		
		String result  = ShutterProvider.findImagesIllustration(query, 1);
		
		data = JsonParser.parseImagesData(result);
		List<ImageData> illustrationsList = new ArrayList<ImageData>();
		illustrationsList.addAll(data.stream().filter(im->im.image_type.equals("illustration")).collect(Collectors.toList()));
		int page = 2;
		while (illustrationsList.size()<100 && page<6) {
			result  = ShutterProvider.findImagesIllustration(query, page);
			if (result==null)
				break;
			data = JsonParser.parseImagesData(result);
			illustrationsList.addAll(data.stream().filter(im->im.image_type.equals("illustration")).collect(Collectors.toList()));
			page++;
		}
		data = illustrationsList;
		
		
		return data;
	}
}
