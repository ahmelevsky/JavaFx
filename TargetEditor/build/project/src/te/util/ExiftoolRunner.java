package te.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import te.Main;
import te.Settings;

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

	public static boolean writeMetadataToFile(File toFile, List<String> keys,
			String title, String description) throws IOException {

		List<String> sb = new ArrayList<String>();
		
		if (isWindows) sb.add(exifpath);
		else if (isMac)  sb.add("/usr/local/bin/exiftool");
		else throw new IOException("Unsupported OS");
		LOGGER.fine("Set OS");
		sb.add("-overwrite_original");
		
		for (String k : keys) {
			//sb.add("-keywords-=" + k); - This for adding keywords to existed
			//sb.add("-keywords+=" + k);
			sb.add("-keywords=" + k);
		}
		if (title!=null && !title.isEmpty()){
			sb.add("-title=" + title);
			sb.add("-XMP-dc:Title=" + title);
			sb.add("-IPTC:Headline=" + title);
			sb.add("-IPTC:ObjectName=" + title);
		}
		if (description!=null && !description.isEmpty()){
			sb.add("-description=" + description);
			sb.add("-IPTC:Caption-Abstract=" + description);
			sb.add("-XMP-dc:Description=" + description);
			sb.add("-EXIF:ImageDescription=" + description);
		}
		
		sb.add(toFile.getAbsolutePath());

		LOGGER.info("");
		LOGGER.info(Settings.bundle.getString("log.message.write") + toFile.getAbsolutePath());
		
		LOGGER.info("Decription (" +toFile.getName() + "): " + description);
		LOGGER.info("Keywords (" +toFile.getName() + "): " + StringUtils.join(keys, ", "));
		LOGGER.info("Title (" +toFile.getName() + "): " + title);
		
		try {
			int code = runCommand(sb);
			LOGGER.info("Exiftool exit code: " + code); 
			if (code==0 || code==141) return true;
			else return false;
		} catch (InterruptedException | IOException e) {
			LOGGER.severe("Exiftool process interrupted on file "
					+ toFile.getAbsolutePath());
			LOGGER.severe(e.getMessage());
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
