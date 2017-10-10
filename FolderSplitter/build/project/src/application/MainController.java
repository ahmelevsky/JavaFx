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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
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

Main app;


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

      directoryChooser.setTitle("�������� ������ ����� �����");
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
	
	File folder = new File(path.getText());
	if (!folder.exists()) return;
	
	File[] filearray = folder.listFiles(new FileFilter(){
		@Override
		public boolean accept(File arg0) {
			return arg0.isFile() && !arg0.getName().startsWith("._") && !arg0.getName().toLowerCase().startsWith(".ds_store");
		}
	});
	
	//Arrays.sort(filearray);
	List<File> files = new ArrayList<>(Arrays.asList(filearray));
	
	
	orderByNumericName(files);
	
	int count = 0;
	try {
		
		if (isPair.isSelected()){
			List<File> hasNoPair = checkHasNotPair(files);
			if (!hasNoPair.isEmpty()){
				Alert alert = new Alert(AlertType.CONFIRMATION);
		          alert.setTitle("��� ����� ����������?");
		          StringBuilder sb = new StringBuilder("����� �� ����� ����:\n");
		          hasNoPair.forEach(f -> sb.append(f.getName()+"\n"));
		          alert.setContentText(sb.toString());
		          alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		          Optional<ButtonType> result = alert.showAndWait();
		          if (result.get() == ButtonType.NO)
		              return;
			}
			if (isRandom.isSelected())
				count = SplitRandomPair(folder, files);
			else 
				count = SplitOrderPair(folder, files);
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("������!");
			alert.setContentText("���������� " + count + " ��� ������");
			alert.showAndWait();
		}
		else {
			if (isRandom.isSelected())
				count = SplitRandom(folder, files);
			else 
				count = SplitOrder(folder, files);
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("������!");
		alert.setContentText("���������� " + count + " ������");
		alert.showAndWait();
		}
	} catch (IOException e) {
		  Alert alert = new Alert(AlertType.ERROR);
          alert.setTitle("������");
          alert.setContentText(e.getMessage());
          alert.showAndWait();
	}
}

private int SplitRandom(File folder, List<File> files) throws IOException{
	int  i = 0;
	int count = 0;
	while (!files.isEmpty()){
		i++;
		if (i>valueFactory.getValue()) i=1;
		
		File file = files.remove(new Random().nextInt(files.size()));
		
		File destFolder =  new File(folder.getAbsolutePath() + File.separator + i);
		if (!destFolder.exists()) destFolder.mkdir();
	 	String dest = destFolder.getAbsolutePath() + File.separator + file.getName();
    	moveFileRenameTo(file, dest);
    	count++;
	}
	return count;
}

private int SplitOrder(File folder, List<File> files) throws IOException{
	int  i = 0;
	int count = 0;
	for (File file:files){
		i++;
		if (i>valueFactory.getValue()) i=1;
		File destFolder =  new File(folder.getAbsolutePath() + File.separator + i);
		if (!destFolder.exists()) destFolder.mkdir();
	 	String dest = destFolder.getAbsolutePath() + File.separator + file.getName();
    	moveFileRenameTo(file, dest);
    	count++;
	}
	return count;
}


private int SplitRandomPair(File folder, List<File> files) throws IOException{
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
		
		File destFolder =  new File(folder.getAbsolutePath() + File.separator + i);
		if (!destFolder.exists()) destFolder.mkdir();
	 	String destjpg = destFolder.getAbsolutePath() + File.separator + jpg.getName();
	 	String desteps = destFolder.getAbsolutePath() + File.separator + eps.getName();
	 	
	 	moveFileRenameTo(jpg, destjpg);
	 	moveFileRenameTo(eps, desteps);
    	
    	count++;
	}
	return count;
}

private int SplitOrderPair(File folder, List<File> files) throws IOException{
	int  i = 0;
	int count = 0;
	List<File> jpgs = files.stream().filter(f->f.getName().toLowerCase().endsWith(".jpg")).collect(Collectors.toList());
	orderByNumericName(jpgs);
	for (File jpg:jpgs){
		i++;
		if (i>valueFactory.getValue()) i=1;
		File destFolder =  new File(folder.getAbsolutePath() + File.separator + i);
		
		Optional<File> of = files.stream().filter(f -> f.getName().toLowerCase().equals(
    			nameWithoutExtension(jpg.getName()).toLowerCase() + ".eps")).findFirst();
		if (of.equals(Optional.empty())) continue; 
		File eps = of.get();
		
		if (!destFolder.exists()) destFolder.mkdir();
		
		String destjpg = destFolder.getAbsolutePath() + File.separator + jpg.getName();
	 	String desteps = destFolder.getAbsolutePath() + File.separator + eps.getName();
	 	
	 	moveFileRenameTo(jpg, destjpg);
	 	moveFileRenameTo(eps, desteps);
    	
    	count++;
	}
	return count;
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

private void moveFileRenameTo (File file, String dest) throws IOException{
	file.renameTo(new File(dest));
}

public void setMainApp(Main app) {
		this.app = app;
	}


	
}
