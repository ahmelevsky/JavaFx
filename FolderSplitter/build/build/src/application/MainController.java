package application;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;

public class MainController implements Initializable{
@FXML
private Button btn;

@FXML
private Label path;

@FXML
private  CheckBox isRandom;
@FXML
private  CheckBox isPair;

@FXML
private Spinner<Integer> parts;
SpinnerValueFactory<Integer> valueFactory;

@FXML
private Button splitBtn;

@FXML
private TextArea namesArea;

Main app;
String[] folderNames;
File folder;
List<File> files;

@Override
public void initialize(URL arg0, ResourceBundle arg1) {
	this.valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
	parts.setValueFactory(valueFactory);
	parts.focusedProperty().addListener((observable, oldValue, newValue) -> {
		  if (!newValue) {
			  parts.increment(0); // won't change value, but will commit editor
		  }
		});
} 

@FXML
private void selectPath(){
	  DirectoryChooser directoryChooser = new DirectoryChooser(); 

      directoryChooser.setTitle("Выберите откуда брать файлы");
      File selected = new File(this.path.getText());
      if (selected.exists())
    	  directoryChooser.setInitialDirectory(selected);
      File file = directoryChooser.showDialog(app.getPrimaryStage());

     if(file!=null) {
    	 this.path.setText(file.getAbsolutePath());
     }
}


@FXML
private void split(){
	
	int count = 0;
	this.folder = new File(path.getText());
	readFoldersNames();
	
	if (!this.folder.exists()) return;
	try {
		
	List<File> folders = getFoldersRecoursively(this.folder);
	folders.add(this.folder);
	
	for (File fd:folders) {

		File[] filearray = fd.listFiles(new FileFilter(){
			@Override
			public boolean accept(File arg0) {
				return arg0.isFile() && !arg0.getName().startsWith("._") && !arg0.getName().toLowerCase().startsWith(".ds_store");
			}
		});
		
		this.files = new ArrayList<>(Arrays.asList(filearray));
		orderByNumericName(this.files);
		
		
	    if (isPair.isSelected()){
				List<File> hasNoPair = checkHasNotPair(this.files);
				if (!hasNoPair.isEmpty()){
					Alert alert = new Alert(AlertType.CONFIRMATION);
			          alert.setTitle("Все равно продолжить?");
			          StringBuilder sb = new StringBuilder("Файлы не имеют пары:\n");
			          hasNoPair.forEach(f -> sb.append(f.getName()+"\n"));
			          alert.setContentText(sb.toString());
			          alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
			          Optional<ButtonType> result = alert.showAndWait();
			          if (result.get() == ButtonType.NO)
			              return;
				}
				if (isRandom.isSelected())
					count += SplitRandomPair();
				else 
					count += SplitOrderPair();
				
			}
			else {
				if (isRandom.isSelected())
					count += SplitRandom();
				else 
					count += SplitOrder();
			}
	} 
	
	if (listFileTree(this.folder).isEmpty())
		this.folder.delete();
	
	    Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Готово!");
		if (isPair.isSelected())
			alert.setContentText("Перемещено " + count + " пар файлов");
		else
			alert.setContentText("Перемещено " + count + " файлов");
		alert.showAndWait();
		
	
	} catch (IOException e) {
		  Alert alert = new Alert(AlertType.ERROR);
          alert.setTitle("Ошибка");
          alert.setContentText(e.getMessage());
          alert.showAndWait();
	}
}

private int SplitRandom() throws IOException{
	int  i = 0;
	int count = 0;
	while (!files.isEmpty()){
		i++;
		if (i>valueFactory.getValue()) i=1;
		File file = files.remove(new Random().nextInt(files.size()));
		if (moveFile(file, null,this.getFolderNameByIndex(i)))
    	count++;
	}
	return count;
}

private int SplitOrder() throws IOException{
	int  i = 0;
	int count = 0;
	for (File file:files){
		i++;
		if (i>valueFactory.getValue()) i=1;
		if (moveFile(file, null,this.getFolderNameByIndex(i)))
    		count++;
	}
	return count;
}


private int SplitRandomPair() throws IOException{
	int  i = 0;
	int count = 0;
	List<File> jpgs = files.stream().filter(f->f.getName().toLowerCase().endsWith(".jpg")).collect(Collectors.toList());
	while (!jpgs.isEmpty()){
		i++;
		if (i>valueFactory.getValue()) i=1;
		
		File jpg = jpgs.remove(new Random().nextInt(jpgs.size()));
		Optional<File> of = files.stream().filter(f -> f.getName().toLowerCase().equals(
    			nameWithoutExtension(jpg.getName()).toLowerCase() + ".eps")).findFirst();
		if (of.equals(Optional.empty())) continue; 
		File eps = of.get();
		if (moveFile(eps, jpg, this.getFolderNameByIndex(i)))
			count++;
	}
	return count;
}

private int SplitOrderPair() throws IOException{
	int  i = 0;
	int count = 0;
	List<File> jpgs = files.stream().filter(f->f.getName().toLowerCase().endsWith(".jpg")).collect(Collectors.toList());
	orderByNumericName(jpgs);
	for (File jpg:jpgs){
		i++;
		if (i>valueFactory.getValue()) i=1;
		
		Optional<File> of = files.stream().filter(f -> f.getName().toLowerCase().equals(
    			nameWithoutExtension(jpg.getName()).toLowerCase() + ".eps")).findFirst();
		if (of.equals(Optional.empty())) continue; 
		File eps = of.get();
		if (moveFile(eps, jpg, this.getFolderNameByIndex(i)))
    	count++;
	}
	return count;
}

private boolean moveFile(File eps, File jpg, String folderName) throws IOException {
	boolean result = true;
	File destFolder =  new File(folder.getParent() + File.separator + folderName);
	if (!destFolder.exists()) destFolder.mkdir();
 	if (eps!=null) {
 		String desteps = destFolder.getAbsolutePath()  + eps.getAbsolutePath().replace(folder.getAbsolutePath(), "");
 		result = result & moveFileRenameTo(eps, desteps);
 	}
 	if (jpg!=null) {
 		String destjpg = destFolder.getAbsolutePath() + jpg.getAbsolutePath().replace(folder.getAbsolutePath(), "");;
 		result = result & moveFileRenameTo(jpg, destjpg);
 	}
 	return result;
}


private List<File> checkHasNotPair(List<File> files){
	List<File> noPair = new ArrayList<File>();

	for (File f:files){
	
		if (f.getName().toLowerCase().endsWith(".jpg") && 
				files.stream().noneMatch(ff -> ff.getName().toLowerCase().equals(nameWithoutExtension(f.getName()).toLowerCase() + ".eps")))
			noPair.add(f);
			
		if (f.getName().toLowerCase().endsWith(".eps") && 
				files.stream().noneMatch(ff -> ff.getName().toLowerCase().equals(nameWithoutExtension(f.getName()).toLowerCase() + ".jpg")))
			noPair.add(f);
		
	}
	
	return noPair;
}

private void orderByNumericName(List<File> files){
	Collections.sort(files, new Comparator<File>() {
	    public int compare(File o1, File o2) {
	        return extractInt(o1) - extractInt(o2);
	    }

	    int extractInt(File s) {
	        String num = s.getName().replaceAll("\\D", "");
	        // return 0 if no digits found
	        return num.isEmpty() ? 0 : Integer.parseInt(num);
	    }
	});
}


private String nameWithoutExtension(String name){
	return name.replaceFirst("[.][^.]+$", "");
}

private boolean moveFileRenameTo (File file, String dest) throws IOException{
	File destFile  = new File(dest);
	File destDir = destFile.getParentFile();
	if (!destDir.exists())
		destDir.mkdirs();
	return file.renameTo(destFile);
}

public void setMainApp(Main app) {
		this.app = app;
	}

private void readFoldersNames(){
	List<String> names = new ArrayList<String>();
	String data = namesArea.getText().trim();
	this.folderNames = data.split("[\\r\\n]+");
}

private String getFolderNameByIndex(int index){
	int i = index-1;
	if (this.folderNames == null || this.folderNames.length < index || this.folderNames[i].trim().isEmpty())
		 return String.valueOf(index);
	else
		return this.folderNames[i].trim();
}
	
private List<File> getFoldersRecoursively(File file) throws IOException{
	  List<File> subdirs = Arrays.asList(file.listFiles(new FileFilter() {
	        public boolean accept(File f) {
	            return f.isDirectory();
	        }
	    }));
	    subdirs = new ArrayList<File>(subdirs);

	    List<File> deepSubdirs = new ArrayList<File>();
	    for(File subdir : subdirs) {
	        deepSubdirs.addAll(getFoldersRecoursively(subdir)); 
	    }
	    subdirs.addAll(deepSubdirs);
	    return subdirs;
}


public static Collection<File> listFileTree(File dir) {
    Set<File> fileTree = new HashSet<File>();
    if(dir==null||dir.listFiles()==null){
        return fileTree;
    }
    for (File entry : dir.listFiles()) {
        if (entry.isFile()) fileTree.add(entry);
        else fileTree.addAll(listFileTree(entry));
    }
    return fileTree;
}

}
