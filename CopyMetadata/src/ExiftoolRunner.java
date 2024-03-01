
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

	public static boolean copyMetadataFolder(File fromFolder, File toFolder) throws IOException, InterruptedException {

		List<String> sb = new ArrayList<String>();
		//StringBuffer sb = new StringBuffer();
		//if (isWindows) sb.add(exifpath);
		if (isWindows) sb.add("exiftool");
		else if (isMac)  sb.add("/usr/local/bin/exiftool");
		else throw new IOException("Unsupported OS");
		
		sb.add("-tagsfromfile");
		sb.add("\"" + fromFolder.getAbsolutePath() + File.separator + "%f.%e\"");
		sb.add("\"" + toFolder.getAbsolutePath() + "\"");

		sb.add("-overwrite_original");
		
		
		
		try {
			int code = runCommand(sb);
			if (code==0 || code==141) return true;
			else return false;
		} catch (InterruptedException | IOException e) {
			return false;
		}
		
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
	
	static int runCommand(List<String> com) throws IOException, InterruptedException {
		Process process = new ProcessBuilder(com).redirectErrorStream(true).start();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()))) {
			/*String line;
			while ((line = reader.readLine()) != null) {
				app.log(line); // Your superior logging approach here
			}*/
		}
		return process.waitFor();
	}

}
