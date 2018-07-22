package be.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

public class Utils {
	private static Logger l = Logger.getLogger();
	private static List<String> ignoreTemplate = new ArrayList<String>(){{
		add("\\._.*");
	}};
	private static final int limit = 1000000;
	
	
	public static void hackTooltipStartTiming(Tooltip tooltip) {
	    try {
	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(tooltip);

	        Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	        fieldTimer.setAccessible(true);
	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

	        objTimer.getKeyFrames().clear();
	        objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public static int selectFilesAndMove(File inputFolder, File outputFolder, int filesToMoveCount, boolean isJpgOnly, StringBuffer sb, int movedcount){
			
		List<File> subfolders = getSubdirsInCorrectOrder(inputFolder);
		l.debug("Start enumerating source subfloders for batch source item, subfolders count: " + subfolders.size());
		l.debug("Input folder: " + inputFolder.getAbsolutePath());
		l.debug("Output folder: " + outputFolder.getAbsolutePath());
		l.debug("Files to move: " + filesToMoveCount);
		
		subfolders.forEach(s -> l.debug("\t"+s.getAbsolutePath()));
		int step = 0;
		int errcount = 0;
		while (filesToMoveCount>0){
			
			if (!outputFolder.getAbsolutePath().endsWith("_add") && movedcount>=limit-1)
				outputFolder = new File(outputFolder.getAbsolutePath() + "_add");
			
			l.debug("filesToMoveCount left " + filesToMoveCount);
			if (errcount>10) {
				l.error("ОШИБКА: Слишком много ошибок, операция над папкой прервана.");
				sb.append("ОШИБКА: Слишком много ошибок, операция над папкой прервана.\n");
				return movedcount;
			}
			if (countFilesInDirectories(subfolders, ".jpg")==0){
				l.debug("ПРЕДУПРЕЖДЕНИЕ: В папке " + inputFolder.getAbsolutePath() + " больше нет файлов.\n");
				sb.append("ПРЕДУПРЕЖДЕНИЕ: В папке " + inputFolder.getAbsolutePath() + " больше нет файлов.");
				return movedcount;		
			}
			if (step==subfolders.size())
				step=0;
			
			File[] images = subfolders.get(step++).listFiles(new FileFilter() {
	        public boolean accept(File f) {
	        	return f.getName().toLowerCase().endsWith(".jpg") && ignoreTemplate.stream().noneMatch(t -> f.getName().toLowerCase().matches(t));
	        	}
		});
			l.debug("JPEG files count: " + images.length + " in subfolder: " + subfolders.get(step-1).getAbsolutePath());
 			if (images.length>0){
				File jpg = images[new Random().nextInt(images.length)];
				l.debug("Randomly selected file: " + jpg.getAbsolutePath());
				if (!isJpgOnly) {
					File eps = new File(jpg.getParentFile() + File.separator + jpg.getName().replaceFirst("[.][^.]+$", ".eps")); 
					if (!eps.exists()) {
						l.error("ПРЕДУПРЕЖДЕНИЕ: Для файла " + jpg.getAbsolutePath() + " не нашлось одноименного eps.");
						sb.append("ПРЕДУПРЕЖДЕНИЕ: Для файла " + jpg.getAbsolutePath() + " не нашлось одноименного eps.\n");
						errcount++;
						continue;
					}
					l.debug("Found EPS file: " + eps.getAbsolutePath());
					try {
						if (!createBatchFolder(outputFolder)) return movedcount;
						Files.move(eps.toPath(), new File(outputFolder.getAbsolutePath() + File.separator + eps.getName()).toPath());
						l.log("FILE successully moved. From: " + eps.getAbsolutePath() + " To " + outputFolder.getAbsolutePath() + File.separator + eps.getName());
						movedcount++;
					} catch (IOException e) {
						l.error("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + eps.getAbsolutePath() + " Error message: " + e.getMessage());
						sb.append("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + eps.getAbsolutePath() + "\n");
						continue;
					}
				}
				try {
					if (!createBatchFolder(outputFolder)) return movedcount;
					Files.move(jpg.toPath(), new File(outputFolder.getAbsolutePath() + File.separator + jpg.getName()).toPath());
					l.log("FILE successully moved. From: " + jpg.getAbsolutePath() + " To " + outputFolder.getAbsolutePath() + File.separator + jpg.getName());
					movedcount++;
				} catch (IOException e) {
					l.error("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + jpg.getAbsolutePath() + " Error message: " + e.getMessage());
					sb.append("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + jpg.getAbsolutePath() + "\n");
					continue;
				}
				filesToMoveCount--;
			}
		}
		return movedcount;
	}
	
	
	public static int selectFilesAndMoveRandom(File inputFolder, File outputFolder, int filesToMoveCount, boolean isJpgOnly, StringBuffer sb, int movedcount){
		l.debug("Start RANDOM enumerating source subfolders for batch source item");
		l.debug("Input folder: " + inputFolder.getAbsolutePath());
		l.debug("Output folder: " + outputFolder.getAbsolutePath());
		l.debug("Files to move: " + filesToMoveCount);
		
		int errcount = 0;
		while (filesToMoveCount>0){
			
			if (!outputFolder.getAbsolutePath().endsWith("_add") && movedcount>=limit-1)
				outputFolder = new File(outputFolder.getAbsolutePath() + "_add");
			
			l.debug("filesToMoveCount left " + filesToMoveCount);
			if (errcount>10) {
				l.error("ОШИБКА: Слишком много ошибок, операция над папкой прервана.");
				sb.append("ОШИБКА: Слишком много ошибок, операция над папкой прервана.\n");
				return movedcount;
			}
			int countFilesInLastFolders = countFilesInDirectoryIfNoSubDirectories(inputFolder, ".jpg");
			if (countFilesInLastFolders==0){
				l.debug("ПРЕДУПРЕЖДЕНИЕ: В конечных подпапках директории " + inputFolder.getAbsolutePath() + " больше нет подходящих файлов");
				sb.append("ПРЕДУПРЕЖДЕНИЕ: В конечных подпапках директории " + inputFolder.getAbsolutePath() + " больше нет подходящих файлов.\n");
				return movedcount;		
			}
			else 
				l.debug("Количество файлов jpeg в конечных папках: " + countFilesInLastFolders);
			
			File selectedDirectory = inputFolder; 
			File dir = selectedDirectory;
			while (dir!=null)
			{
				dir  = getRandomFolder(selectedDirectory);
				if (dir!=null) {
					l.debug("Go to " + dir.getAbsolutePath());
					selectedDirectory = dir;
				}
			}
			
			
			File[] images = selectedDirectory.listFiles(new FileFilter() {
	        public boolean accept(File f) {
	        	return f.getName().toLowerCase().endsWith(".jpg") && ignoreTemplate.stream().noneMatch(t -> f.getName().toLowerCase().matches(t));
	        	}
		});
			l.debug("JPEG files count: " + images.length + " in subdolder: " + selectedDirectory.getAbsolutePath());
 			if (images.length>0){
				File jpg = images[new Random().nextInt(images.length)];
				l.debug("Randomly selected file: " + jpg.getAbsolutePath());
				if (!isJpgOnly) {
					File eps = new File(jpg.getParentFile() + File.separator + jpg.getName().replaceFirst("[.][^.]+$", ".eps")); 
					if (!eps.exists()) {
						l.error("ПРЕДУПРЕЖДЕНИЕ: Для файла " + jpg.getAbsolutePath() + " не нашлось одноименного eps.");
						sb.append("ПРЕДУПРЕЖДЕНИЕ: Для файла " + jpg.getAbsolutePath() + " не нашлось одноименного eps.\n");
						errcount++;
						continue;
					}
					l.debug("Found EPS file: " + eps.getAbsolutePath());
					try {
						if (!createBatchFolder(outputFolder)) return movedcount;
						Files.move(eps.toPath(), new File(outputFolder.getAbsolutePath() + File.separator + eps.getName()).toPath());
						l.log("FILE successully moved. From: " + eps.getAbsolutePath() + " To " + outputFolder.getAbsolutePath() + File.separator + eps.getName());
						movedcount++;
					} catch (IOException e) {
						l.error("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + eps.getAbsolutePath() + " Error message: " + e.getMessage());
						sb.append("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + eps.getAbsolutePath() + "\n");
						continue;
					}
				}
				try {
					if (!createBatchFolder(outputFolder)) return movedcount;
					Files.move(jpg.toPath(), new File(outputFolder.getAbsolutePath() + File.separator + jpg.getName()).toPath());
					l.log("FILE successully moved. From: " + jpg.getAbsolutePath() + " To " + outputFolder.getAbsolutePath() + File.separator + jpg.getName());
					movedcount++;
				} catch (IOException e) {
					l.error("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + jpg.getAbsolutePath() + " Error message: " + e.getMessage());
					sb.append("ПРЕДУПРЕЖДЕНИЕ: Не получилось переместить файл " + jpg.getAbsolutePath() + "\n");
					continue;
				}
				filesToMoveCount--;
			}
		}
		return movedcount;
	}
	
	private static File getRandomFolder(File directory){
		File[] dirs = directory.listFiles(new FileFilter() {
	        public boolean accept(File f) {
	        	return f.isDirectory();
	        	}
		});
		
		if (dirs!=null && dirs.length>0)
			return dirs[new Random().nextInt(dirs.length)];
		else return null;
	}
	
	
	public static List<File> filterFoldersSet(List<File> folders, String pattern){
		List<File> resultFolders = new ArrayList<File>();
		for (File folder:folders){
			if (folder.listFiles(new FileFilter() {
		        public boolean accept(File f) {
		        	return f.getName().toLowerCase().endsWith(pattern) && ignoreTemplate.stream().noneMatch(t -> f.getName().toLowerCase().matches(t));
		        	}
			}).length>0)
				resultFolders.add(folder);
		}
		return resultFolders;
	}
	
	
	public static List<File> getSubdirsInCorrectOrder(File file) {
		List<File> result = new ArrayList<File>();
		result.add(file);
	    List<File> subdirs = Arrays.asList(file.listFiles(new FileFilter() {
	        public boolean accept(File f) {
	        	 return f.isDirectory();
	        }
	    }));
	    if (!subdirs.isEmpty()) {
	    	List<List<File>> structure = new ArrayList<List<File>>();
	    	for (File subf:subdirs){
	    		List<File> subfolders =  getSubdirs(subf);
	    		subfolders.add(subf);
	    		structure.add(filterFoldersSet(subfolders, ".jpg"));
	    	}
	    	while(structure.stream().anyMatch(list -> !list.isEmpty()))
	    		for (List<File> list:structure)
	    			if (!list.isEmpty())
	    				result.add(list.remove(0));
	    	}
	    return filterFoldersSet(result, ".jpg");
	}
	
	
	
	
	public static List<File> getSubdirs(File file) {
	    List<File> subdirs = Arrays.asList(file.listFiles(new FileFilter() {
	        public boolean accept(File f) {
	        	 return f.isDirectory();
	        }
	    }));
	    subdirs = new ArrayList<File>(subdirs);

	    List<File> deepSubdirs = new ArrayList<File>();
	    for(File subdir : subdirs) {
	        deepSubdirs.addAll(getSubdirs(subdir)); 
	    }
	    subdirs.addAll(deepSubdirs);
	    return subdirs;
	}
	
	 public static int countFilesInDirectory(File directory, String pattern) {
	      int count = 0;
	      for (File file : directory.listFiles()) {
	          if (file.isFile() && file.getName().toLowerCase().endsWith(pattern) && ignoreTemplate.stream().noneMatch(t -> file.getName().toLowerCase().matches(t))) {
	              count++;
	          }
	          if (file.isDirectory()) {
	              count += countFilesInDirectory(file, pattern);
	          }
	      }
	      return count;
	  }
	 
	 public static int countFilesInDirectoryIfNoSubDirectories(File directory, String pattern) {
	      int count = 0;
	      List<File> files = Arrays.asList(directory.listFiles());
	      if (files.stream().anyMatch(f -> f.isDirectory())) 
	        for (File file : files) {
	          if (file.isDirectory()) 
	              count += countFilesInDirectoryIfNoSubDirectories(file, pattern);
	        }
	      else 
	    	  for (File file : files) 
	    		  if (file.getName().toLowerCase().endsWith(pattern) && ignoreTemplate.stream().noneMatch(t -> file.getName().toLowerCase().matches(t))) 
	    			  count++;
	      return count;
	  }
	 
	 public static int countFilesInDirectories(List<File> directories, String pattern) {
	      int count = 0;
	      for (File file : directories) {
	              count += countFilesInDirectory(file, pattern);
	      }
	      return count;
	  }
	 
	  public static boolean createBatchFolder(File folder){
		    if (folder.exists()) return true;
			if (!folder.mkdir()){
				 Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle("Ошибка");
		            alert.setHeaderText("не могу создать папку для батча: " + folder.getAbsolutePath());
		            l.error("Error creating batch output folder" + folder.getAbsolutePath());
		            alert.showAndWait();
		            return false;
			}
			return true;
	  }
	 
	 
	 //Alternative (non-recursive) version with alphabetic order
	 /*public static Set<File> subdirs(File d) throws IOException {
	        TreeSet<File> closed = new TreeSet<File>(new Comparator<File>() {
	            @Override
	            public int compare(File f1, File f2) {
	                return f1.toString().compareTo(f2.toString());
	            }
	        });
	        Deque<File> open = new ArrayDeque<File>();
	        open.push(d);
	        closed.add(d);
	        while ( ! open.isEmpty()) {
	            d = open.pop();
	            for (File f : d.listFiles()) {
	                if (f.isDirectory() && ! closed.contains(f)) {
	                    open.push(f);
	                    closed.add(f);
	                }
	            }
	        }
	        return closed;
	    }
	 */
}
