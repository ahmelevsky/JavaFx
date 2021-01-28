package sm;

import java.util.ArrayList;
import java.util.List;

public class FileRule implements Comparable<FileRule> {

	public String prefix;
	public boolean isIllustration;
	public List<String> categories = new ArrayList<String>();
	public List<String> releases = new ArrayList<String>();
	
	public FileRule(String prefix) {
		this.prefix = prefix;
	}
	

	@Override
	public int compareTo(FileRule o) {
		return this.prefix.compareTo(o.prefix);
	}


	@Override
	public String toString() {
		return "FileRule [prefix=" + prefix + "]";
	}
	
	
}
