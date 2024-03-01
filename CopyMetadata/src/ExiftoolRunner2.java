

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;



public class ExiftoolRunner2 {
	static String exifpath = "lib/exiftool.exe";
	public static Main app;
	static boolean isWindows;
	static boolean isLinux;
	static boolean isMac;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	static {
		getOperationSystem();
	}

	public static void copyMetadataFolder(File fromFolder, File toFolder) throws IOException, InterruptedException {

		//List<String> sb = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		//if (isWindows) sb.add(exifpath);
		if (isWindows) sb.append("exiftool ");
		else if (isMac)  sb.append("/usr/local/bin/exiftool ");
		else throw new IOException("Unsupported OS");
		
		sb.append("-tagsfromfile ");
		sb.append("\"" + fromFolder.getAbsolutePath() + File.separator + "%f.%e\" ");
		sb.append("\"" + toFolder.getAbsolutePath() + "\"");

		sb.append(" -overwrite_original");
		
		
  		Runtime runTime = Runtime.getRuntime();
  		System.out.println(sb.toString());
		Process process = runTime.exec(sb.toString());
		
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
	
}