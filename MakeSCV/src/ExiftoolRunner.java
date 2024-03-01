

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;



public class ExiftoolRunner {
	static String exifpath = "lib/exiftool.exe";
	public static Main app;
	static boolean isWindows;
	static boolean isLinux;
	static boolean isMac;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	static {
		getOperationSystem();
	}

	

	public static Metadata readMetadataFromFileExtended(File fromFile) throws IOException, InterruptedException {

		    String keywords = readTag (fromFile, "-subject");
		    if (keywords.isEmpty())
		    		keywords = readTag (fromFile, "-keywords");
		    
		    String description = readTag (fromFile, "-headline");
		    if (description.isEmpty())
		    	description = readTag (fromFile, "-IPTC:Caption-Abstract");
		    
			 Metadata m = new Metadata();
			 m.description = description;
			 m.keywords = keywords;
			 return m;
		 
	}
	
	
	private static String readTag(File fromFile, String tag) throws IOException, InterruptedException {
	    List<String> sb = new ArrayList<String>();
		
		if (isWindows) sb.add(exifpath);
		else if (isMac)  sb.add("/usr/local/bin/exiftool");
		else throw new IOException("Unsupported OS");
		
		        sb.add("-T");
				sb.add(tag);
				
				sb.add(fromFile.getAbsolutePath());
		  		List<String> result = new ArrayList<String>();
					int code = runCommand(sb, result);
					if (code==0 || code==141) {
						if (result.size()<1)
							throw new IOException("Empty metadata");
					    return result.get(0);
					}
					else throw new IOException("Can't read metadata");
	}
	
	
	
	public static Metadata readMetadataFromFile(File fromFile) throws IOException, InterruptedException {

		List<String> sb = new ArrayList<String>();
		
		if (isWindows) sb.add(exifpath);
		else if (isMac)  sb.add("/usr/local/bin/exiftool");
		else throw new IOException("Unsupported OS");
		
		sb.add("-T");
		//sb.add("-description");
		//sb.add("-IPTC:Caption-Abstract");
		sb.add("-headline");
       
		//sb.add("-keywords");

		sb.add("-subject");
		
		
		sb.add(fromFile.getAbsolutePath());
  		List<String> result = new ArrayList<String>();
		
			int code = runCommand(sb, result);
			if (code==0 || code==141) {
				if (result.size()<1)
					throw new IOException("Empty metadata");
			 Metadata m = new Metadata();
			 m.description = result.get(0).split("\t")[0];
			 m.keywords = result.get(0).split("\t")[1];
			 //m.keys = Arrays.asList(m.keywords.split(","));
			 return m;
			}
			else throw new IOException("Can't read metadata");
		
	}

	static void getOperationSystem() {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows"))
			isWindows = true;
		else if (os.toLowerCase().contains("mac"))
			isMac = true;
		else if (os.toLowerCase().contains("linux"))
			isLinux = true;
	}

	static int executeInTerminal(String command) throws IOException,
			InterruptedException {
		final String[] wrappedCommand;

		if (isWindows) {
			wrappedCommand = new String[] { "cmd", "/c", "start", "/wait",
					"cmd.exe", "/K", command };
		} else if (isLinux) {
			wrappedCommand = new String[] { "xterm", "-e", "bash", "-c",
					command };
		} else if (isMac) {
			wrappedCommand = new String[] {
					"osascript",
					"-e",
					"tell application \"Terminal\" to activate",
					"-e",
					"tell application \"Terminal\" to do script \"" + command
							+ ";exit\"" };
		} else {
			LOGGER.severe("Error: Unsupported OS");
			return -1;
		}
		Process process = new ProcessBuilder(wrappedCommand)
				.redirectErrorStream(true).start();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				LOGGER.fine(line);
			}
		}
		return process.waitFor();
	}
	
	static int runCommand(List<String> com, List<String> result) throws IOException, InterruptedException {
		Process process = new ProcessBuilder(com).redirectErrorStream(true).start();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				result.add(line);
			}
		}
		return process.waitFor();
	}

}
