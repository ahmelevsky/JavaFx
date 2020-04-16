package klt.data;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyWord {
	
	public String word;
	public Set<String> images = new HashSet<String>();
	public Set<String> unsales = new HashSet<String>();
	
	public KeyWord(String word) {
		this.word = word;
	}

	public KeyWord(String word, String imageId) {
		this.word = word;
		this.images.add(imageId);
	}
	
	public int getWeight() {
		return this.images.size();
	}
	
	public static void sortByWeight(List<KeyWord> keywords) {
		Comparator<KeyWord> compareByImagesCount = (KeyWord o1, KeyWord o2) ->  o2.getWeight() - o1.getWeight();

		keywords.sort(compareByImagesCount);
	}
	
}
