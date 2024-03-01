import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class Main {

	public static void main(String[] args)  {
		
		
		File file = new File(".");
		String[] directories = file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
				  File currentFile =  new File (current, name);
			    return currentFile.isDirectory() && currentFile.getName().contains(" RE-EXPORTED");
			  }
			});
		
		for (String dir:directories) {
			
			File sourceDir = new File(file.getAbsolutePath(), dir.split(" ")[0]);
			if (!sourceDir.exists())
				continue;
			try {
				ExiftoolRunner.copyMetadataFolder(sourceDir, new File(dir));
			} catch (IOException | InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
			
		}
		
		

	}

}
