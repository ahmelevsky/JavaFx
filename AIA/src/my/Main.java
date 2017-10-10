package my;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

	public static void main(String[] args){
		File[] files = new File(".").listFiles( pathname -> pathname.getName().endsWith("aia"));
		for (File f:files){
			try {
				List<String> lines = Files.readAllLines(Paths.get(f.getAbsolutePath()));
				try (PrintWriter pw = new PrintWriter(new FileWriter(f.getAbsolutePath()+"_"))){
					for (String line:lines)
						pw.println("\"" +line.trim() + "\"," );
				}
			} catch (IOException e) {
			}
		}
	}
	
}
