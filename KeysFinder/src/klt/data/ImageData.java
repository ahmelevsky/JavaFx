package klt.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
	private Set<String> keywords = new LinkedHashSet<String>();
	private Set<String> saleKeywords = new LinkedHashSet<String>();
	private List<String> otherKeywords = new ArrayList<String>();
	public boolean selected;
	
	
	public Set<String> getAllKeywords(){
		return this.keywords;
	}
	
	public Set<String> getSaleKeywords(){
		return this.saleKeywords;
	}
	
	public List<String> getOtherKeywords(){
		return this.otherKeywords;
	}
	
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
		splitKeywordsToSaleAndOther();
	}
	
	public void addKeywords(Collection<String> keywords) {
		this.keywords.addAll(keywords);
		splitKeywordsToSaleAndOther();
	}
	
	@Override
	public String toString() {
		return "ImageData [id=" + id + ", type=" + type + ", title=" + title + ", description=" + description
				+ ", image_type=" + image_type + ", src=" + src + "]";
	}
	
	
	private void splitKeywordsToSaleAndOther() {
		this.saleKeywords.clear();
		this.otherKeywords.clear();
		if(this.keywords.isEmpty()) return;
		LinkedList<String> list = new LinkedList<String>(this.keywords);
		boolean wasOrdered = false;
		for(int i=list.size()-1;i>0;i--) {
			String word = list.get(i);
			String previous = list.get(i-1);
			if (word.compareTo(previous)>=0) {
				this.otherKeywords.add(0,word);
				list.remove(i);
				wasOrdered = true;
				if (list.size()==1) {
					this.otherKeywords.add(0,previous);
					list.remove(i-1);
					break;
				}
			}
			else {
				if (wasOrdered) {
					this.otherKeywords.add(0,word);
					list.remove(i);
				}
				break;
			}
		}
		this.saleKeywords.addAll(list);
	}
}
