package klt.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import klt.data.KeyWord;

public class TagButton extends Button {

	public KeyWord keyword;
	public boolean isActive;
	public boolean isFound;
	public String key;
	public long salesCount;
	public long allCount;
	public int position;
	private Map<String, String> style = new HashMap<String, String>();
	
	public TagButton(KeyWord keyword, long allCount){
		super(keyword.word);
		this.keyword = keyword;
		this.key = keyword.word;
		this.salesCount = keyword.images.size();
		this.allCount = allCount;
		this.isActive=true;
		this.style.put("radius", "-fx-background-radius: 20px; ");
		this.style.put("minwidth", "-fx-min-width: 20px; ");
		this.style.put("minheight", "-fx-min-height: 5px; ");
		this.style.put("background", "-fx-background-color: " +
                " linear-gradient(#f2f2f2, #d6d6d6)," +
                " linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                " linear-gradient(#dddddd 0%, #f6f6f6 50%);");
		this.style.put("text", "-fx-text-fill: black; ");
		this.setTooltip(new Tooltip(salesCount + " / " + allCount));
	}
	
	
	public TagButton(KeyWord keyword, long allCount, long allCount2){
		super(keyword.word);
		this.keyword = keyword;
		this.key = keyword.word;
		this.salesCount = keyword.images.size();
		this.allCount = allCount;
		this.isActive=true;
		this.style.put("radius", "-fx-background-radius: 20px; ");
		this.style.put("minwidth", "-fx-min-width: 20px; ");
		this.style.put("minheight", "-fx-min-height: 5px; ");
		this.style.put("background", "-fx-background-color: " +
                " linear-gradient(#f2f2f2, #d6d6d6)," +
                " linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                " linear-gradient(#dddddd 0%, #f6f6f6 50%);");
		this.style.put("text", "-fx-text-fill: black; ");
		this.setTooltip(new Tooltip(salesCount + " / " + allCount + " / " + allCount2));
	}
	
	private void setCustomStyle() {
		if(this.isActive) {
			this.style.put("text", "-fx-text-fill: black; ");
			if (this.isFound)
				this.style.put("background", "-fx-background-color: yellow; ");
			else 
				this.style.put("background", "-fx-background-color: " +
		                   " linear-gradient(#f2f2f2, #d6d6d6)," +
		                   " linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
		                   " linear-gradient(#dddddd 0%, #f6f6f6 50%);");
		}
		else {
			this.style.put("text", "-fx-text-fill: grey; ");
			if (this.isFound)
				this.style.put("background", "-fx-background-color: yellow; ");
			else 
				this.style.put("background",   "-fx-background-color: white; ");
		}
			
		
		
		this.setStyle(String.join(" ", this.style.values()));
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		setCustomStyle();
	}

	
	public void setFound(boolean isFound) {
		this.isFound = isFound;
		setCustomStyle();
	}
	
	
	
}
