package te.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Variable {
	private final StringProperty name;
	private final StringProperty delimiter;
    private Set<String> values = new HashSet<String>();
	
    
    public Variable(){
    	this("", "", "");
    }
	
	public Variable(String name, String delimiter, String values){
		this.name =  new SimpleStringProperty(name);
		this.delimiter = new SimpleStringProperty(delimiter);
		setValues(values);
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
	
	public Set<String> getValues(){
		return this.values;
	}
	
	public void setValues(String values){
		String[] vals = values.split(this.delimiter.get());
		this.values = new HashSet<String>();
		for (String v:vals){
			if (v!=null && !v.trim().isEmpty())
				this.values.add(v.trim());
		}
	}
	
	public String getRandomValue(){
		return getRandomValueFromCollection(this.values);
	}
	
	
	public String getRandomValueFromCollection(Collection<String> c){
		if (c.isEmpty()) return "";
		int index = ThreadLocalRandom.current().nextInt(c.size());
		int i=0;
		for (String v:c){
			if (i==index)
				return v;
			i++;
		}
		return null;
	}
	
	public String getMaxValue(){
		if (this.values.isEmpty()) return "";
		return Collections.max(this.values, Comparator.comparing(s -> s.length()));
	}
	
	public List<String> getNRandomValues(int n){
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		Collections.shuffle(result);
		while (result.size()>n)
			result.remove(0);
		return result;
	}
	
	public List<String> getNMaxValues(int n){
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		Collections.sort(result, Comparator.comparing(s -> s.length()));
		while (result.size()>n)
			result.remove(0);
		return result;
	}
	
	public static String getRandomValueByName(List<Variable> variables, String name){
		Optional<Variable> vo = variables.stream().filter(v -> name.equals(v.getName())).findFirst();
		if (vo ==null || !vo.isPresent())
			return null;
		else
			return vo.get().getRandomValue();
	}
	
	 
    public static String getMaxValueByName(List<Variable> variables, String name){
    	    if (variables == null || name == null)
    	    	return null;
			Optional<Variable> vo = variables.stream().filter(v -> name.equals(v.getName())).findFirst();
			if (vo ==null || !vo.isPresent()) 
			    return null;
			else
				return vo.get().getMaxValue();
				
	}
    
    
    
}
