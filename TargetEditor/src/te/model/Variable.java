package te.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Variable {
	private final StringProperty name;
	private final StringProperty delimiter;
    private List<String> values = new ArrayList<String>();
	
    
    public Variable(){
    	this("", "", "");
    }
	
	public Variable(String name, String delimiter, String values){
		this.name =  new SimpleStringProperty(name);
		this.delimiter = new SimpleStringProperty(delimiter);
		this.values = Arrays.asList(values.split(delimiter));
	}
	
	public String getName(){
		return this.name.get();
	}
	
	public StringProperty nameProperty(){
		return this.name;
	}
	
	public void setName(String name){
		this.name.set(name);
	}
	
	public void setDelimiter(String delimiter){
		this.delimiter.set(delimiter);
	}
	
	public String getDelimiter(){
        return this.delimiter.get();		
	}
	
	public StringProperty delimiterProperty(){
		return this.delimiter;
	}
	
	public List<String> getValues(){
		return this.values;
	}
	
	public void setValues(String values){
		String[] vals = values.split(this.delimiter.get());
		this.values = new ArrayList<String>();
		for (String v:vals){
			if (v!=null && !v.trim().isEmpty())
				this.values.add(v.trim());
		}
	}
	
	public String getRandomValue(){
		if (this.values.isEmpty()) return "";
		return this.values.get(ThreadLocalRandom.current().nextInt(this.values.size()));
	}
}
