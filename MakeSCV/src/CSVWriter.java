import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVWriter {

   private String fileName;
	
   public CSVWriter (String fileName) {
   
    this.fileName = fileName;
   }
	
	public String convertToCSV(String[] data) {
	    return Stream.of(data)
	      .map(this::escapeSpecialCharacters)
	      .collect(Collectors.joining(","));
	}
	
	
	public void write(List<String[]> dataLines ) throws FileNotFoundException {
	
	File csvOutputFile = new File(fileName);
    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
        dataLines.stream()
          .map(this::convertToCSV)
          .forEach(pw::println);
    }
}
	
public String escapeSpecialCharacters(String data) {
    if (data == null) {
        throw new IllegalArgumentException("Input data cannot be null");
    }
    String escapedData = data.replaceAll("\\R", " ");
    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
        data = data.replace("\"", "\"\"");
        escapedData = "\"" + data + "\"";
    }
    return escapedData;
}

}
