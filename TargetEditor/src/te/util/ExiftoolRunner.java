package te.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import te.Main;

public class ExiftoolRunner {
	static String exifpath = "lib/exiftool.exe";
	public static Main app;
	static boolean isWindows;
	static boolean isLinux;
	static boolean isMac;

	static {
		getOperationSystem();
	}

	public static void writeMetadataToFile(File toFile, List<String> keys,
			String title, String description) throws IOException {

		List<String> sb = new ArrayList<String>();
		
		if (isWindows) sb.add(exifpath);
		else if (isMac)  sb.add("/usr/local/bin/exiftool");
		else throw new IOException("Unsupported OS");
		
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

		app.log("");
		app.log("Запись метаданных в файл " + toFile.getAbsolutePath());
		
		try {
			 app.log("Exiftool exit code: " +runCommand(sb)); 
		} catch (InterruptedException | IOException e) {
			app.log("Exiftool process interrupted on file "
					+ toFile.getAbsolutePath());
			app.log(e.getMessage());
		}
		app.log("Decription: " + description);
		app.log("Keys: " + StringUtils.join(keys, ", "));
		app.log("Title: " + title);
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
			app.log("Error: Unsupported OS");
			return -1;
		}
		Process process = new ProcessBuilder(wrappedCommand)
				.redirectErrorStream(true).start();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				app.log(line); // Your superior logging approach here
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
