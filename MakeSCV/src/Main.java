import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

	
	public final static String CSV_FILE_NAME = "test.csv";
	
	public static void main(String[] args) throws FileNotFoundException {
		  File dir = new File(".");
		  File[] directoryListing = dir.listFiles(new FilenameFilter() {
		        @Override
		        public boolean accept(File dir, String name) {
		            return name.toLowerCase().endsWith(".eps");
		        }
		    });
		  
		  
		  
		    List<String[]> dataLines = new ArrayList<>();
		    dataLines.add(new String[] 
					  { "Filename", "Title", "Keywords", "Category", "Releases" });
		    for (File child : directoryListing) {
		    try {
				Metadata m = ExiftoolRunner.readMetadataFromFileExtended(child);
				
				dataLines.add(new String[] 
				  { child.getName(), m.description, m.keywords, m.category, m.releases });
				
			} catch (IOException | InterruptedException e) {
				System.out.println(e.getMessage());
			}
			//System.out.println(child.getName());
		  }
           CSVWriter writer = new CSVWriter("_metadata.csv");
           writer.write(dataLines);
	}

}
