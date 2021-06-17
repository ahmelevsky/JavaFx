import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		File f = new File(".");

		FilenameFilter filter = new FilenameFilter() {
		        @Override
		        public boolean accept(File f, String name) {
		            return name.endsWith(".eps");
		        }
		    };
		    
		    
		    String[] pathnames = f.list(filter);
		    
		    
		    for (String pathname:pathnames) {
				  try {
				  Path epsPath = Paths.get(pathname); 
				  String epsName = pathname.replaceFirst("[.][^.]+$", "");
  				  Files.move(epsPath, epsPath.resolveSibling(epsName.split("-")[0] + "_"+ epsName.split("-")[1].split("_")[1] + "-" + epsName.split("-")[1].split("_")[0]  + ".eps"));
				  }
				  catch (Exception e) {
					  
				  }
			  }
		    
	}

}
