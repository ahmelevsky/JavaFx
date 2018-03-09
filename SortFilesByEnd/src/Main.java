import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class Main {

	static File currentDir = new File(".");
	
	public static void main(String[] args) {
		
		 File[] files = currentDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jpg") || name.endsWith(".eps") ||  name.endsWith(".ai");
				}
			});
		 for (File f:files){
			 String fileNameWithOutExt = f.getName().replaceFirst("[.][^.]+$", "");
			 String[] parts = fileNameWithOutExt.split("_");
			 if (parts.length<2) continue;
			 
			 String suffix = parts[parts.length-1];
			 if (suffix.isEmpty()) continue;
			 File folder = new File(suffix);
			 if (!folder.exists())
				 folder.mkdir();
			 
			 try {
				Files.move(Paths.get(f.getAbsolutePath()), Paths.get(folder  + File.separator + f.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
			}
		 }
	}

}
