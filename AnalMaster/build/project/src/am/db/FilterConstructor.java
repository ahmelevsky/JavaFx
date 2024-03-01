package am.db;

import java.util.ArrayList;
import java.util.List;

import am.Main;

public class FilterConstructor {
	
	public final String IMAGESTOPTABLE = "imagestopdata";
	public final String IMAGESTABLE = "imagesdata";
	public final String KEYSTABLE = "keysdata";
	
	
	Main app;
	
	String filterSQL = "";
	
	public String name = "";
	public String description = "";
	public String type = "";
	public String uploadDateFrom = "";
	public String uploadDateTo = "";
	public int downloadsLess = Integer.MAX_VALUE;
	public int downloadsMore = 0;
	private final double doubleMax = Double.MAX_VALUE;
	public double earningsLess = doubleMax;
	public double earningsMore = -1;
	
	
	
	public List<String> keywords = new ArrayList<String>();
	
	public FilterConstructor(Main app) {
		this.app = app;
	}
	
	public void constructFilter() {
	//	String escape = " ESCAPE '\\' ";
	//	String orderby = "  ORDER BY \"media_id\" DESC ";
		List<String> filters = new ArrayList<String>();
		
		if (!name.trim().isEmpty())
			filters.add(makeCondition(name, "original_filename", ",", " ", false, true)); 
		if (!description.trim().isEmpty())
			filters.add(makeCondition(description, "description", ",", " ", false, true));
		if (!type.trim().isEmpty())
			filters.add(makeCondition(type, "type", ",", " ", false, true));
		String kwords = String.join(",",keywords);
		if (!kwords.trim().isEmpty())
			filters.add(makeCondition(kwords, "keywords", ",", ",", true, true));
		
		if (!uploadDateFrom.isEmpty() && !uploadDateTo.isEmpty())
			filters.add(" \"uploaded_date\" BETWEEN  '" + uploadDateFrom + "' AND '" + uploadDateTo + "' ");
		else if (!uploadDateFrom.isEmpty())
			filters.add(" \"uploaded_date\" >= '" + uploadDateFrom + "' ");
		else if (!uploadDateTo.isEmpty())
			filters.add(" \"uploaded_date\" <= '" + uploadDateTo + "' ");
		
		if (downloadsLess<Integer.MAX_VALUE || downloadsMore>0) {
			filters.add(" \"downloads\" BETWEEN  " + downloadsMore + " AND " + downloadsLess + " ");
		}
		
		if (earningsLess<Double.MAX_VALUE || earningsMore>0) {
			filters.add(" \"earnings\" BETWEEN  " + earningsMore + " AND " + earningsLess + " ");
		}
		
		
	    if (!filters.isEmpty())
	    	//this.filterSQL =" WHERE " + String.join(" AND ", filters) + orderby;
	    	this.filterSQL =" WHERE " + String.join(" AND ", filters);	
	    else 
	    	//this.filterSQL = orderby;
	    	this.filterSQL = "";
	}
	
	public String getCountSQL() {
		constructFilter();
		return "SELECT Count(*) FROM " + this.IMAGESTABLE + " LEFT JOIN " + this.IMAGESTOPTABLE 
				 + " on " + this.IMAGESTOPTABLE + ".media_id = " + this.IMAGESTABLE + ".media_id "
				+ this.filterSQL;
	}
	
	public String getImagesSQL() {
		constructFilter();
		return "SELECT * FROM " + this.IMAGESTABLE + " LEFT JOIN " + this.IMAGESTOPTABLE 
				 + " on " + this.IMAGESTOPTABLE + ".media_id = " + this.IMAGESTABLE + ".media_id "
				+ this.filterSQL;
	}
	
	
	public String makeCondition(String str, String field, String criteriaSeparator, String textSeparator, boolean isCertain, boolean isAnd) {
		String[] parts = str.split(criteriaSeparator);
		List<String> sb = new ArrayList<String>();
		
		String condition = " AND ";
		
		if (!isAnd)
			condition = " OR ";
		
		for (String s:parts) {
			s = s.trim();
			String like = " LIKE ";
		
			
			if (s.startsWith("-")) {
				s=s.substring(1);
				like = " NOT LIKE ";
			}
			
			if (!isCertain) {
				s = "%"+s+"%";
				sb.add(" \"" + field + "\" " +like + "'" + s + "'");
			}
			else {
				String internalAND = " AND ";
				if (like.equals(" LIKE "))
					internalAND = " OR ";
				 sb.add(" ( \"" + field + "\" "+like + "'%" + textSeparator + s + textSeparator 
						+ "%'" + internalAND + "\"" + field + "\"" + like + "'" + s + textSeparator
						+ "%'" + internalAND + "\"" + field + "\"" + like + "'%" + textSeparator + s + "') ");
			}
		}
			return " (" + String.join(condition, sb) + ") ";
	}
	
}
