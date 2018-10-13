package te.model;

import javafx.beans.DefaultProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Target {

	private final SimpleStringProperty targetKwd;
    private final SimpleStringProperty targetDescr1;
    private final SimpleStringProperty targetDescr2;
    
    public Target() {
    	this.targetKwd = new SimpleStringProperty("");
    	this.targetDescr1 = new SimpleStringProperty("");
    	this.targetDescr2 = new SimpleStringProperty("");
    }
    
    public Target(final String targKwd, final String targDescr1, final String targDescr2) {
    	this.targetKwd = new SimpleStringProperty(targKwd);
    	this.targetDescr1 = new SimpleStringProperty(targDescr1);
    	this.targetDescr2 = new SimpleStringProperty(targDescr2);
    }


	public String getTargetKwd() {
		return targetKwd.get();
	}
    
    public StringProperty targetKwdProperty(){
    	return this.targetKwd;
    }
    
    public void setTargetKwd(String target){
    	this.targetKwd.set(target);
    }
    
    public String getTargetDescr1() {
		return targetDescr1.get();
	}
    
    public StringProperty targetDescr1Property(){
    	return this.targetDescr1;
    }
    
    public void setTargetDescr1(String target){
    	this.targetDescr1.set(target);
    }
    
    public String getTargetDescr2() {
		return targetDescr2.get();
	}
    
    public StringProperty targetDescr2Property(){
    	return this.targetDescr2;
    }
    
    public void setTargetDescr2(String target){
    	this.targetDescr2.set(target);
    }
    
}
