package sm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PropertyRelease implements Comparable<PropertyRelease>{

	public String workflow_status;
	public String ext;
	public String visible;
	public String added_date;
	public String name;
	public String id;
	public String type;
	public Date date;
	
	public PropertyRelease(String workflow_status, String ext, String visible, String added_date, String name,
			String id, String type) throws ParseException {
		super();
		this.workflow_status = workflow_status;
		this.ext = ext;
		this.visible = visible;
		this.added_date = added_date;
		this.name = name;
		this.id = id;
		this.type = type;
		this.date  = new SimpleDateFormat("yyyy-MM-dd").parse(added_date);
	}

	@Override
	public int compareTo(PropertyRelease o) {
		 return o.date.compareTo(this.date);
	}
	
	
}
