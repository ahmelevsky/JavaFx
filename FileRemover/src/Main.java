import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;


public class Main {
	static File currentDir = new File(".");
	
	public static void main(String[] args) {

		  while (true) {
		        
	      	File file = getNewFile();
	      	if (file == null)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				else {
	      			file.delete();
				}
		  }
	  }
	

public static File getNewFile(){
	
	  FileFilter filter = new FileFilter() {
          @Override
          public boolean accept(File pathname) {
             return pathname.getName().endsWith(".eps") && pathname.length() > 50000000;
          }
       };
	
	 File[] files = currentDir.listFiles(filter);
	 if (files.length>0) return files[0];
	 else return null;
      
}
	
}
