import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

	static String filename = "OldMaster.txt";
	static String filenameTemplate = "OldMaster.*.txt";
	static String errorFilename = "OldMasterError.txt";
	static Set<Path> filesWithStatistics = new HashSet<Path>();
	static Set<String> oldTxtData = new HashSet<String>();
	static Set<String> statistics = new HashSet<String>();
	
	
	public static void main(String[] args) {
		try {
			
			Path file = Files.find(Paths.get(".").toAbsolutePath().normalize(),
								1,
								(path, basicFileAttributes) -> path.toFile().getName().matches(filenameTemplate)
								).findFirst().orElse(null);
			if (file!=null) {
				filesWithStatistics.add(file);
				filename = file.getFileName().toString();
			}
			
			readStatistics();
			scan();
			
			statistics.removeAll(oldTxtData);
			writeToFile();
		}

		catch (IOException e) {
			writeError(e.getMessage());
		}
		
	}
	
	public static void readStatistics() throws IOException {
		for (Path file:filesWithStatistics) {
			oldTxtData.addAll(Files.readAllLines(file, StandardCharsets.UTF_8));
		}
	}
	
	public static void scan() throws IOException {
		statistics = Files.find(Paths.get(".").toAbsolutePath().normalize(),
		           Integer.MAX_VALUE,
		           (path, basicFileAttributes) -> path.toFile().getName().matches("(?i).*\\.(eps|jpg)$")).
				   filter(Files::isRegularFile).
				   map(Path::getFileName).
				   map(Path::toString).
				   collect(Collectors.toSet());
	}
	
	public static void writeToFile() throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(filename, true), "UTF-8"));
		Iterator<String> it = statistics.iterator(); 
		while(it.hasNext()) {
		    out.write(it.next());
		    out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public static void writeError(String message) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(errorFilename, "UTF-8");
			writer.println(message);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
		}
	}

}
