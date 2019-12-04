package te.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import te.Settings;
import te.model.Variable;

public class MultiParser {
    private final List<Variable> global;
    private Map<String,Set<String>> local = new HashMap<String,Set<String>>();
    private final Pattern TAG_REGEX = Pattern.compile("<(.+?)>(\\[(|[0-9]+?)\\])?");
	
	public MultiParser(List<Variable> variables) {
		global = variables;
	    for (Variable v:global) {
	    	Set<String> values = new HashSet<String>();
	    	values.addAll(v.getValues());
	    	local.put(v.getName(), values);
	    }
	}
	
	
	
	public String pasteVariables(String string, boolean isMaxLengthValues, String delimiter) throws TextException{
		final Matcher matcher = TAG_REGEX.matcher(string);
	    while (matcher.find()) {
	    	if (!matcher.group(1).isEmpty()) {
	    		if (!local.containsKey(matcher.group(1)))
	    			throw new TextException(Settings.bundle.getString("parser.unexistedvar.begin") + matcher.group(1) + Settings.bundle.getString("parser.unexistedvar.end"));
	    		
	    		int multiplier = 1;
	    		if (matcher.group(3)!= null)
	    			if (matcher.group(3).isEmpty()) {
	    				Optional<Variable> opt = global.stream().filter(p -> p.getName().equals(matcher.group(1))).findFirst();
	    				if (opt!=null && opt.isPresent()) {
	    					Variable var = opt.get();
	    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(var.getValues(), delimiter));
	    				}
	    			}
	    			else {
	    				multiplier = Integer.parseInt(matcher.group(3));
	    				if (isMaxLengthValues) 
	    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(getNMaxValues(matcher.group(1), multiplier), delimiter));
	    				else 
	    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(getNRandomValues(matcher.group(1), multiplier), delimiter));
	    			}
	    		else
	    			if (isMaxLengthValues) 
	    				string = StringUtils.replaceOnce(string, matcher.group(0), getMaxValue(matcher.group(1)));
    				else 
    					string = StringUtils.replaceOnce(string, matcher.group(0), getRandomValue(matcher.group(1)));
	    	}
	    }
	    if(StringUtils.containsAny(string, '[', ']', '<', '>'))
	    		throw new TextException(Settings.bundle.getString("parser.badsymbols"));
	    return string;
	}


	public String pasteVariablesMin(String string, String delimiter) throws TextException{
		final Matcher matcher = TAG_REGEX.matcher(string);
	    while (matcher.find()) {
	    	if (!matcher.group(1).isEmpty()) {
	    		if (!local.containsKey(matcher.group(1)))
	    			throw new TextException(Settings.bundle.getString("parser.unexistedvar.begin") + matcher.group(1) + Settings.bundle.getString("parser.unexistedvar.end"));
	    		
	    		int multiplier = 1;
	    		if (matcher.group(3)!= null)
	    			if (matcher.group(3).isEmpty()) {
	    				Optional<Variable> opt = global.stream().filter(p -> p.getName().equals(matcher.group(1))).findFirst();
	    				if (opt!=null && opt.isPresent()) {
	    					Variable var = opt.get();
	    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(var.getValues(), delimiter));
	    				}
	    			}
	    			else {
	    				multiplier = Integer.parseInt(matcher.group(3));
	    					string = StringUtils.replaceOnce(string, matcher.group(0), StringUtils.join(getNMinValues(matcher.group(1), multiplier), delimiter));
	    			}
	    		else
	    				string = StringUtils.replaceOnce(string, matcher.group(0), getMinValue(matcher.group(1)));
	    	}
	    }
	    if(StringUtils.containsAny(string, '[', ']', '<', '>'))
	    		throw new TextException(Settings.bundle.getString("parser.badsymbols"));
	    return string;
	}

	private String getRandomValue(String name) {
		Set<String> values = local.get(name);
		if (values.isEmpty())
			refill(name);
		String result = getRandomValueFromCollection(values);
		values.remove(result);
		return result;
	}


	private String getMaxValue(String name) {
		Set<String> values = local.get(name);
		if (values.isEmpty())
			refill(name);
		if (values.isEmpty()) return "";
		String result = Collections.max(values, Comparator.comparing(s -> s.length()));
		values.remove(result);
		return result;
	}

	private String getMinValue(String name) {
		Set<String> values = local.get(name);
		if (values.isEmpty())
			refill(name);
		if (values.isEmpty()) return "";
		String result = Collections.min(values, Comparator.comparing(s -> s.length()));
		values.remove(result);
		return result;
	}



	private List<String> getNRandomValues(String name, int multiplier) {
		List<String> result = new ArrayList<String>();
		Set<String> values = local.get(name);
		for (int i=0; i<multiplier; i++) {
			if (values.isEmpty())
				refill(name);
			if (values.isEmpty()) 
				break;
			String res = getRandomValueFromCollection(values);
			values.remove(res);
			result.add(res);
		}
		return result;
	}



	private List<String> getNMaxValues(String name, int multiplier) {
		List<String> result = new ArrayList<String>();
		Set<String> values = local.get(name);
		for (int i=0; i<multiplier; i++) {
			if (values.isEmpty())
				refill(name);
			if (values.isEmpty()) 
				break;
			String res = Collections.max(values, Comparator.comparing(s -> s.length()));
			values.remove(res);
			result.add(res);
		}
		return result;
	}

	private List<String> getNMinValues(String name, int multiplier) {
		List<String> result = new ArrayList<String>();
		Set<String> values = local.get(name);
		for (int i=0; i<multiplier; i++) {
			if (values.isEmpty())
				refill(name);
			if (values.isEmpty()) 
				break;
			String res = Collections.min(values, Comparator.comparing(s -> s.length()));
			values.remove(res);
			result.add(res);
		}
		return result;
	}

	private void refill(String variableName) {
		Optional<Variable> opt = global.stream().filter(p -> p.getName().equals(variableName)).findFirst();
		if (opt==null || !opt.isPresent())
			return;
		Variable var = opt.get();
		local.get(variableName).addAll(var.getValues());
	}
	
	private String getRandomValueFromCollection(Collection<String> c){
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
	
}
