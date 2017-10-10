package application;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

public class Main {

	public static void main (String[] args)
	{
		File[] allFiles = new File(".").listFiles();
       for (int i=0; i<allFiles.length;i++){
    	   if (allFiles[i].getName().endsWith("eps")){
    		   File eps = allFiles[i];
    		   String name = eps.getName().replaceFirst("[.][^.]+$", "");
    		   String path = eps.getParent();
    		   File jpg = new File(path + File.separator + name + ".jpg");
    		   if (jpg.exists()){
    			   List<File> tozip = new ArrayList<File>();
    			   tozip.add(eps);
    			   tozip.add(jpg);
    			   zip(tozip);
    			   eps.delete();
    			   jpg.delete();
    		   }
    	   }
       }
		
	}
	
	public static void mainr (String[] args)
	{
		System.out.println("Hello!");
	}
	
	
	public static void zip(List<File> files){
		if (files.isEmpty()) return;
		String name = files.get(0).getName().replaceFirst("[.][^.]+$", "");
		String path = files.get(0).getParent();
		String zipName = path + File.separator + name + ".zip";
		
		 try(ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipName, false)))
		 {		 
	        for (File f:files){
			    FileInputStream fis= new FileInputStream(f.getAbsolutePath());
	            ZipEntry entry1=new ZipEntry(f.getName());
	            zout.putNextEntry(entry1);
	            // считываем содержимое файла в массив byte
	            byte[] buffer = new byte[fis.available()];
	            fis.read(buffer);
	            // добавляем содержимое к архиву
	            zout.write(buffer);
	            // закрываем текущую запись для новой записи
	            zout.closeEntry();
	            fis.close();
	        }
	        }
	        catch(Exception ex){
	            System.out.println(ex.getMessage());
	        } 
	}
	
}
