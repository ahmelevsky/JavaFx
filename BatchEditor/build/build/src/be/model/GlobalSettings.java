package be.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "settings")
public class GlobalSettings {
	
	private StringProperty path;
	private ObjectProperty<Integer> batchescount;
    
	
    public GlobalSettings() {
		this(null,0);
	}

	public GlobalSettings(String s, int i){
    	this.path = new SimpleStringProperty(s);
    	this.batchescount = new SimpleIntegerProperty(i).asObject();
    }

	public final StringProperty pathProperty() {
		return this.path;
	}
	 @XmlElement(name = "savepath")
	public final java.lang.String getPath() {
		return this.pathProperty().get();
	}

	public final void setPath(final java.lang.String path) {
		this.pathProperty().set(path);
	}

	public final ObjectProperty<Integer> batchescountProperty() {
		return this.batchescount;
	}
	 @XmlElement(name = "batchescount")
	public final java.lang.Integer getBatchescount() {
		return this.batchescountProperty().get();
	}

	public final void setBatchescount(final java.lang.Integer batchescount) {
		this.batchescountProperty().set(batchescount);
	}

}
