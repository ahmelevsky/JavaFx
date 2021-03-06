package te.model;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "FolderVariable")
public class FolderVariable {
	private final File folder;
	private final StringProperty keyVariable;
    private final StringProperty descriptionVariable;
    private final StringProperty folderPath;
    
    public FolderVariable() {
    	this(null);
    }
    
       
    public FolderVariable(File folder){
    	this.folder = folder;
    	if (folder == null)
    		this.folderPath = new SimpleStringProperty("");
    	else 
    		this.folderPath = new SimpleStringProperty(folder.getName());
    	this.keyVariable = new SimpleStringProperty("");
    	this.descriptionVariable = new SimpleStringProperty("");
    }

    
    public String getFolderPath() {
		return this.folderPath.get();
	}
    
    public StringProperty folderPathProperty(){
    	return this.folderPath;
    }
    
    public void setFolderPath(String folderPath){
    	this.folderPath.set(folderPath);
    }
    
	public String getKeyVariable() {
		return this.keyVariable.get();
	}
    
    public StringProperty keyVariableProperty(){
    	return this.keyVariable;
    }
    
    public void setKeyVariable(String key){
    	this.keyVariable.set(key);
    }
    
    public String getDescriptionVariable() {
		return this.descriptionVariable.get();
	}
    
    public StringProperty descriptionVariableProperty(){
    	return this.descriptionVariable;
    }
    
    public void setDescriptionVariable(String description){
    	this.descriptionVariable.set(description);
    }
    
    
    public File getFolder(){
    	return this.folder;
    }

    
}
