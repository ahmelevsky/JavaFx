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

	
	public static String pasteVariables(String string, boolean isMaxLengthValues, String delimiter) throws TextException{
		 final Pattern TAG_REGEX = Pattern.compile("<(.+?)>(\\[([0-9]+?)\\])?");
		    final Matcher matcher = TAG_REGEX.matcher(string);
		    while (matcher.find()) {
		    	if (!matcher.group(1).isEmpty()) {
		    		Optional<Variable> opt = app.variables.stream().filter(p -> p.getName().equals(matcher.group(1))).findFirst();
		    		if (opt==null || !opt.isPresent())
		    			throw new TextException("ѕеременна€ с именем " + matcher.group(1) + " не существует");
		    		Variable var = opt.get();
		    		
		    		int multiplier = 1;
		    		if (matcher.group(3)!= null && !matcher.group(3).isEmpty())
		    			multiplier = Integer.parseInt(matcher.group(3));
		    		
		    		if (isMaxLengthValues) {
		    			string = string.replace(matcher.group(0), StringUtils.repeat(var.getMaxValue(), delimiter, multiplier));
		    		}
		    		else {
		    			List<String> values = new ArrayList<String>();
		    			for (int i = 0; i<multiplier; i++){
		    				values.add(var.getRandomValue());
		    			}
		    			string = string.replace(matcher.group(0), StringUtils.join(values, delimiter));
		    		}
		    	}
		    }
		    if(StringUtils.containsAny(string, '[', ']', '<', '>'))
		    		throw new TextException("¬ тексте присутствуют специальные символы <>[] вне правильного синтаксиса вставки переменной");
		    return string;
	}
		
	
}
