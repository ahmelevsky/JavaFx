import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	public static void main (String[] args){
		File currentDir = new File(".");
		File odd = new File(currentDir.getAbsolutePath() + File.separator + "odd"); 
		odd.mkdir();
		File even = new File(currentDir.getAbsolutePath() + File.separator + "even");
		even.mkdir();
		File[] files = currentDir.listFiles();
		  if (files != null) {
		    for (File file : files) {
		     String name = file.getName().replaceFirst("[.][^.]+$", "");
		     Matcher m = Pattern.compile("[0-9]+$").matcher(name);
		     if(m.find()) {
		         if (Integer.parseInt(m.group())%2 ==0)
		            file.renameTo(new File(odd.getAbsolutePath() + File.separator + file.getName()));
		         else 
		        	file.renameTo(new File(even.getAbsolutePath() + File.separator + file.getName()));
		     }
		    }
		
		
	}
	}
	
}
