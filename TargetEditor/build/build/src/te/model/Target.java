package te.model;

import java.io.File;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Target {

	private final File folder;
	private final StringProperty target;
    private final StringProperty target1;
    private final StringProperty target2;
    
    public Target() {
    	this(null);
    }
    
    public Target(File folder){
    	this.folder = folder;
    	if (folder == null)
    		this.target = new SimpleStringProperty("");
    	else 
    		this.target = new SimpleStringProperty(folder.getName());
    	this.target1 = new SimpleStringProperty("");
    	this.target2 = new SimpleStringProperty("");
    }

	public String getTarget() {
		return target.get();
	}
    
    public StringProperty targetProperty(){
    	return this.target;
    }
    
    public void setTarget(String target){
    	this.target.set(target);
    }
    
    public String getTarget1() {
		return target1.get();
	}
    
    public StringProperty targetProperty1(){
    	return this.target1;
    }
    
    public void setTarget1(String target){
    	this.target1.set(target);
    }
    
    public String getTarget2() {
		return target2.get();
	}
    
    public StringProperty targetProperty2(){
    	return this.target2;
    }
    
    public void setTarget2(String target){
    	this.target2.set(target);
    }
    
    
    public File getFolder(){
    	return this.folder;
    }
    
    
}
