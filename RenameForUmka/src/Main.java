import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

public class Main {

	public static void main(String[] args) throws IOException {
		File f = new File(".");

		FilenameFilter filter = new FilenameFilter() {
		        @Override
		        public boolean accept(File f, String name) {
		            return name.endsWith(".json");
		        }
		    };

		String[] pathnames = f.list(filter);

	  for (String pathname:pathnames) {
		  try {
		  Path jsonPath = Paths.get(pathname);
		  String epsName = pathname.replaceFirst("[.][^.]+$", "") + ".eps";
		  Path epsPath = Paths.get(epsName); 
		  File eps = new File(epsName); 
		  if (eps.exists()) {
			 
			  String jsonString = new String(Files.readAllBytes(jsonPath));
			  String country = getCountry(jsonString);
			  String nationality = getNationality(jsonString);
			  Files.move(epsPath, epsPath.resolveSibling(country + "_"+ nationality + "_" + epsName));
			 
		  }
		  }
		  catch (Exception e) {
			  
		  }
	  }
			
	}

	private static String getCountry(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		JSONObject context = obj.getJSONObject("context");
		return context.getString("country");
	}
	
	private static String getNationality(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		JSONObject context = obj.getJSONObject("context");
		return context.getString("nationality");
	}
	
}
