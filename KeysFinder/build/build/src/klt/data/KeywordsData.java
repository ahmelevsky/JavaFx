package klt.data;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class KeywordsData {
	 public String id;
	 Set<String> keywords = new LinkedHashSet<String>();
	 
	 public KeywordsData(String id, Collection<String> keywords) {
		 this.id = id;
		 this.keywords.addAll(keywords);
	 }
	 
}
