package klt.workers;

import java.util.ArrayList;
import java.util.List;

import klt.data.ImageData;

public class ResponseData {

	public List<ImageData> images = new ArrayList<ImageData>();
	public int matchesCount;
	public List<String> relatedKeywords  = new ArrayList<String>();
	
}
