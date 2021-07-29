package sm.db;

import java.util.ArrayList;
import java.util.List;

import sm.Main;

public class FilterConstructor {

	public final String TABLENAME = "imagesdata";
	
	Main app;
	
	String filterSQL = "";
	
	public String name = "";
	public String reason = "";
	public String description = "";
	public String type = "";
	public String uploadDateFrom = "";
	public String uploadDateTo = "";
	public String verdictDateFrom = "";
	public String verdictDateTo = "";
	public String status = "";
	public List<String> keywords = new ArrayList<String>();
	
	public FilterConstructor(Main app) {
		this.app = app;
	}
	
	public void constructFilter() {
	//	String escape = " ESCAPE '\\' ";
		String orderby = "  ORDER BY \"media_id\" DESC ";
		List<String> filters = new ArrayList<String>();
		
		if (!name.trim().isEmpty())
			filters.add(makeCondition(name, "original_filename", ",", " ", false, true)); 
		if (!reason.trim().isEmpty())
			filters.add(makeCondition(reason, "reasons", ",", " ", false, true));
		if (!description.trim().isEmpty())
			filters.add(makeCondition(description, "description", ",", " ", false, true));
		if (!type.trim().isEmpty())
			filters.add(makeCondition(type, "type", ",", " ", false, true));
		if (!status.trim().isEmpty())
			filters.add(makeCondition(status, "status", ",", " ", false, true));
		String kwords = String.join(",",keywords);
		if (!kwords.trim().isEmpty())
			filters.add(makeCondition(kwords, "keywords", ",", ",", true, true));
		
		if (!uploadDateFrom.isEmpty() && !uploadDateTo.isEmpty())
			filters.add(" \"uploaded_date\" BETWEEN  '" + uploadDateFrom + "' AND '" + uploadDateTo + "' ");
		else if (!uploadDateFrom.isEmpty())
			filters.add(" \"uploaded_date\" >= '" + uploadDateFrom + "' ");
		else if (!uploadDateTo.isEmpty())
			filters.add(" \"uploaded_date\" <= '" + uploadDateTo + "' ");
		
		if (!verdictDateFrom.isEmpty() && !verdictDateTo.isEmpty())
			filters.add(" \"verdict_time\" BETWEEN  '" + verdictDateFrom + "' AND '" + verdictDateTo + "' ");
		else if (!verdictDateFrom.isEmpty())
			filters.add(" \"verdict_time\" >= '" + verdictDateFrom + "' ");
		else if (!verdictDateTo.isEmpty())
			filters.add(" \"verdict_time\" <= '" + verdictDateTo + "' ");
		
		
	    if (!filters.isEmpty())
	    	this.filterSQL =" WHERE " + String.join(" AND ", filters) + orderby;
	    else 
	    	this.filterSQL = orderby;
	}
	
	public String getCountSQL() {
		constructFilter();
		return "SELECT Count(*) FROM " + this.TABLENAME + this.filterSQL;
	}
	
	public String getImagesSQL() {
		constructFilter();
		return "SELECT * FROM " + this.TABLENAME + this.filterSQL;
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
	
	
	public void constructFilterOld() {
		String escape = " ESCAPE '\\' ";
		String orderby = "  ORDER BY \"media_id\" DESC ";
		List<String> filters = new ArrayList<String>();
		
		if (!name.trim().isEmpty())
			filters.add(" \"original_filename\" LIKE '%" + name + "%' " + escape);
		if (!reason.trim().isEmpty())
			filters.add(" \"reasons\" LIKE '%" + reason + "%' " + escape);
		if (!description.trim().isEmpty())
			filters.add(" \"description\" LIKE '%" + description + "%' " + escape);
		if (!type.trim().isEmpty())
			filters.add(" \"type\" LIKE '%" + type + "%' " + escape);
		if (!status.trim().isEmpty())
			filters.add(" \"status\" LIKE '%" + status + "%' " + escape);
		for (String k:keywords) {
			filters.add(" (\"keywords\" LIKE '%," + k + ",%' " + escape + " OR  \"keywords\" LIKE '%," + k + "' " + escape
					+ " OR  \"keywords\" LIKE '" + k + ",%' " + escape + ")");
		}
		
		if (!uploadDateFrom.isEmpty() && !uploadDateTo.isEmpty())
			filters.add(" \"uploaded_date\" BETWEEN  '" + uploadDateFrom + "' AND '" + uploadDateTo + "' ");
		else if (!uploadDateFrom.isEmpty())
			filters.add(" \"uploaded_date\" >= '" + uploadDateFrom + "' ");
		else if (!uploadDateTo.isEmpty())
			filters.add(" \"uploaded_date\" <= '" + uploadDateTo + "' ");
		
		if (!verdictDateFrom.isEmpty() && !verdictDateTo.isEmpty())
			filters.add(" \"verdict_time\" BETWEEN  '" + verdictDateFrom + "' AND '" + verdictDateTo + "' ");
		else if (!verdictDateFrom.isEmpty())
			filters.add(" \"verdict_time\" >= '" + verdictDateFrom + "' ");
		else if (!verdictDateTo.isEmpty())
			filters.add(" \"verdict_time\" <= '" + verdictDateTo + "' ");
		
		
	    if (!filters.isEmpty())
	    	this.filterSQL =" WHERE " + String.join(" AND ", filters) + orderby;
	    else 
	    	this.filterSQL = orderby;
	}
}
