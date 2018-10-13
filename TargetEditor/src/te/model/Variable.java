package te.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Variable {
	private final StringProperty name;
	private final StringProperty delimiter;
	//private final StringProperty valuesString;
	
    private final Set<String> values = new HashSet<String>();
	
    
    public Variable(){
    	this("", "", "");
    }
	
	public Variable(String name, String delimiter, String values){
		this.name =  new SimpleStringProperty(name);
		//this.valuesString = new SimpleStringProperty(values);
		this.delimiter = new SimpleStringProperty(delimiter);
		setValues(values);
	}
	@XmlElement
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
	@XmlElement
	public String getDelimiter(){
        return this.delimiter.get();		
	}
	
	public StringProperty delimiterProperty(){
		return this.delimiter;
	}
	@XmlElementWrapper(name = "values")
	@XmlElement(name = "value")
	public Set<String> getValues(){
		return this.values;
	}
	
	public void setValues(String values){
		//this.valuesString.set(values);
		String[] vals = values.split(this.delimiter.get());
		this.values.clear();
		for (String v:vals){
			if (v!=null && !v.trim().isEmpty())
				this.values.add(v.trim());
		}
	}
	
	public void setValues(Set<String> values){
		this.values.clear();
		this.values.addAll(values);
	}
	
	public String getRandomValue(){
		return getRandomValueFromCollection(this.values);
	}
	
	
	public String getRandomValueExcluding(List<String> excludeList){
		if (this.values.isEmpty()) return "";
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		result.removeAll(excludeList);
		if (result.isEmpty()) return "";
		int index = ThreadLocalRandom.current().nextInt(result.size());
		int i=0;
		for (String v:result){
			if (i==index)
				return v;
			i++;
		}
		return null;
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
	
	public String getMaxValueExcluding(List<String> excludeList){
		if (this.values.isEmpty()) return "";
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		result.removeAll(excludeList);
		if (result.isEmpty()) return "";
		return Collections.max(result, Comparator.comparing(s -> s.length()));
	}
	
	public List<String> getNRandomValues(int n){
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		Collections.shuffle(result);
		while (result.size()>n)
			result.remove(0);
		return result;
	}
	
	public List<String> getNRandomValuesExcluding(int n, List<String> excludeList){
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		result.removeAll(excludeList);
		if (result.isEmpty()) return result;
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
	
	public List<String> getNMaxValuesExcluding(int n, List<String> excludeList){
		List<String> result = new ArrayList<String>();
		result.addAll(this.values);
		result.removeAll(excludeList);
		if (result.isEmpty()) return result;
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
/*
	public final StringProperty valuesStringProperty() {
		return this.valuesString;
	}

	public final java.lang.String getValuesString() {
		return this.valuesStringProperty().get();
	}

	@XmlElement
	public final void setValuesString(final java.lang.String valuesString) {
		this.valuesStringProperty().set(valuesString);
	}
  */  
    
    
}
