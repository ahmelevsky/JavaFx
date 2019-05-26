package te.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FolderTheme {
	
	private final StringProperty name;
	
	private final Set<FolderVariable> values = new HashSet<FolderVariable>();
	
	public FolderTheme(){
		this("",null);
	}
	
	public FolderTheme(String name, Collection folderVariables){
		this.name =  new SimpleStringProperty(name);
		if (folderVariables!=null)
			values.addAll(folderVariables);
	}
	
	@XmlElement(name = "name")
	public String getName(){
		return this.name.get();
	}
	
	public StringProperty nameProperty(){
		return this.name;
	}
	
	public void setName(String name){
		this.name.set(name);
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@XmlElementWrapper(name = "variableslist") 
	@XmlElement(name = "FolderVariable")
	 public Set<FolderVariable> getFolderVariables() {
	        return values;
	    }
	 
	 public void setDescriptionVariables(List<FolderVariable> variables) {
	    	this.values.clear();
	        this.values.addAll(variables);
	    }

}
