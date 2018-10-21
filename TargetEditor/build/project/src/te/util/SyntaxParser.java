package te.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import te.Main;
import te.model.Variable;

public class SyntaxParser {
	public static Main app;
	private static final Pattern TAG_REGEX = Pattern.compile("<(.+?)>(\\[(|[0-9]+?)\\])?");
	
	public static String pasteVariables(List<Variable> variables, String string, boolean isMaxLengthValues, String delimiter) throws TextException{
		    final Matcher matcher = TAG_REGEX.matcher(string);
		    while (matcher.find()) {
		    	if (!matcher.group(1).isEmpty()) {
		    		Optional<Variable> opt = variables.stream().filter(p -> p.getName().equals(matcher.group(1))).findFirst();
		    		if (opt==null || !opt.isPresent())
		    			throw new TextException("ѕеременна€ с именем " + matcher.group(1) + " не существует");
		    		Variable var = opt.get();
		    		
		    		int multiplier = 1;
		    		if (matcher.group(3)!= null)
		    			if (matcher.group(3).isEmpty())
		    				string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(var.getValues(), delimiter));
		    			else {
		    				multiplier = Integer.parseInt(matcher.group(3));
		    				if (isMaxLengthValues) 
		    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(var.getNMaxValues(multiplier), delimiter));
		    				else 
		    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(var.getNRandomValues(multiplier), delimiter));
		    			}
		    		else
		    			if (isMaxLengthValues) 
		    				string = StringUtils.replaceOnce(string, matcher.group(0), var.getMaxValue());
	    				else 
	    					string = StringUtils.replaceOnce(string, matcher.group(0), var.getRandomValue());
		    			
		    	}
		    }
		    if(StringUtils.containsAny(string, '[', ']', '<', '>'))
		    		throw new TextException("¬ тексте присутствуют специальные символы <>[] вне правильного синтаксиса вставки переменной");
		    return string;
	}
		
	
	public static String pasteVariablesUnique(List<Variable> variables, String string, boolean isMaxLengthValues, String delimiter) throws TextException{
		List<String> excludeList = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(string);
	    while (matcher.find()) {
	    	if (!matcher.group(1).isEmpty()) {
	    		Optional<Variable> opt = variables.stream().filter(p -> p.getName().equals(matcher.group(1))).findFirst();
	    		if (opt==null || !opt.isPresent())
	    			throw new TextException("ѕеременна€ с именем " + matcher.group(1) + " не существует");
	    		Variable var = opt.get();
	    		
	    		int multiplier = 1;
	    		if (matcher.group(3)!= null)
	    			if (matcher.group(3).isEmpty())
	    				string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(var.getValues(), delimiter));
	    			else {
	    				multiplier = Integer.parseInt(matcher.group(3));
	    				List<String> values = null;
	    				if (isMaxLengthValues) 
	    					values = var.getNMaxValuesExcluding(multiplier, excludeList);
	    				else 
	    					values = var.getNRandomValuesExcluding(multiplier, excludeList);
	    				excludeList.addAll(values);
	    				string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(values, delimiter));
	    			}
	    		else {
	    			String value = "";
	    			if (isMaxLengthValues) 
	    				value = var.getMaxValueExcluding(excludeList);
    				else 
    					value = var.getRandomValueExcluding(excludeList);
	    			excludeList.add(value);
	    			string = StringUtils.replaceOnce(string, matcher.group(0), value);
	    		}
	    	}
	    }
	    if(StringUtils.containsAny(string, '[', ']', '<', '>'))
	    		throw new TextException("¬ тексте присутствуют специальные символы <>[] вне правильного синтаксиса вставки переменной");
	    return string;
}
	
	
	public static String checkVariables(List<Variable> variables, String string) throws TextException{
	    final Matcher matcher = TAG_REGEX.matcher(string);
	    while (matcher.find()) {
	    	if (!matcher.group(1).isEmpty()) {
	    		Optional<Variable> opt = variables.stream().filter(p -> p.getName().equals(matcher.group(1))).findFirst();
	    		if (opt==null || !opt.isPresent())
	    			throw new TextException("ѕеременна€ с именем " + matcher.group(1) + " не существует");
	            string = string.replace(matcher.group(0),"");
	    	}
	    }
	    if(StringUtils.containsAny(string, '[', ']', '<', '>'))
	    		throw new TextException("¬ тексте присутствуют специальные символы <>[] вне правильного синтаксиса вставки переменной");
	    return string;
}
	
}
