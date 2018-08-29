package te.view;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.DirectoryChooser;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


import org.xml.sax.SAXException;


import te.Main;
import te.model.Target;
import te.model.Variable;
import te.util.ExiftoolRunner;

public class MainFrameController implements Initializable{

	public Main app;
	private File rootFolder;
	public Target currentTarget;
	@FXML
	private Button writeBtn;
	@FXML
	private Hyperlink folderPath;
	
	
	@FXML
	private TabPane tabs;
	
	@FXML
	private ProgressBar progress;
	
	private Task<String> task;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (oldTab!=null && oldTab.getText().equals("Переменные")){
				for (int i=app.variableControllers.size(); i>0; i--) {
					Variable v = app.variableControllers.get(i-1).getVariable();
					if ( v == null || v.getName().isEmpty())
						   app.variableEditorContainerController.removeVariableLayout(app.variableControllers.get(i-1));
				}
				if (!app.variables.stream().map(Variable::getName)
			              .collect(Collectors.toList()).stream().allMatch(new HashSet<>()::add)){
					tabs.getSelectionModel().select(oldTab);
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("ОШИБКА!");
					alert.setContentText("Имена переменных не должны повторяться, исправьте прежде чем продолжить.");
					alert.showAndWait();
				}
			}
			switch (newTab.getText()) {
			case "Описания":
				app.descriptionEditorController.updateLists();
				//app.descriptionEditorController.updateForm();
				break;
			case "Заголовки":
				app.titleEditorController.updateLists();
				app.titleEditorController.updateCounter();
				break;
			default:
				break;
			} 
	    });
			
	}
	
	public void addTab(String tabTitle, Node node){
		tabs.getTabs().add(new Tab(tabTitle, node));
	}
	
	
	
	@FXML
	public void writeMetadata() throws SAXException, ParserConfigurationException, TransformerException, IOException{
		if (rootFolder==null || !rootFolder.exists() || !rootFolder.isDirectory()){
			app.showAlert("Папка не выбрана или не существует");
			return;
		}
		 int allImagesCounter = 0;
		 File[] directoriesToWalk = rootFolder.listFiles(new FilenameFilter() {
	           @Override
	          public boolean accept(File current, String name) {
	          return new File(current, name).isDirectory();
	          }
	       });
		 for (File d:directoriesToWalk){
			 allImagesCounter = allImagesCounter + d.listFiles(new FileFilter() {
              public boolean accept(File f) {
              	return f.isFile() && f.getName().toLowerCase().endsWith(".jpg");
              }
 	        }).length;
		 }
		 
		 if (allImagesCounter==0){
				app.showAlert("В выбранной корневой папке нет папок с файлами изображений jpg");
				return;
			}
		 
	   	 final long allImagesCount = allImagesCounter;
		
		
		
		app.keysEditorController.saveKeywordsSource();
		app.descriptionEditorController.saveDescriptionsSource();
		app.titleEditorController.saveTitleSource();
		
		StringJoiner validationErrors = new StringJoiner("\n");
		if (!app.keysEditorController.checkDataIsCorrect())
			validationErrors.add("Unallowed symbols are in the keywords.");
		if (!app.descriptionEditorController.checkDataIsCorrect())
			validationErrors.add("Unallowed symbols are in the description.");
		if (!app.titleEditorController.checkDataIsCorrect())
			validationErrors.add("Unallowed symbols are in the title.");
       		
		if (validationErrors.length()>0){ 
			validationErrors.add("Operation cancelled.");
			app.showAlert(validationErrors.toString());
			return;
		}
		 task = new Task<String>() {
			    @Override public String call() {
			    	int success = 0;
			    	int failures = 0;
			    	int done = 0;
		             File[] directories = rootFolder.listFiles(new FilenameFilter() {
			           @Override
			          public boolean accept(File current, String name) {
			          return new File(current, name).isDirectory();
		 	          }
			       });
		             
		             
		           for (File dir:directories){
			            currentTarget = app.getTargetsData().stream().filter(t->t.getFolder().equals(dir)).findFirst().get();
			   	        File[] images =	dir.listFiles(new FileFilter() {
			                public boolean accept(File f) {
			                	return f.isFile() && f.getName().toLowerCase().endsWith(".jpg");
			                }
			   	        });
			
			   	        if (images.length==0) {
			   	        	app.log("There is no .jpg files in the folder: " + dir.getAbsolutePath());
			   	        }
		    	   
			   	        for (File image:images){
			   	        	try{
			   	        		List<String> keywords = app.keysEditorController.generateKeywordsForMetadata();
			   	        		String description  =  app.descriptionEditorController.generateRandomDescription();
			   	        		String title = app.titleEditorController.getTitleForMetadata();
			   	        		ExiftoolRunner.writeMetadataToFile (image, keywords, title, description);
			   	         		success++;
			   	        	}
			   	        	catch (IOException ex) {
			   	        		app.log("ERROR: failed writing metadata to " + image.getAbsolutePath() + ", exception: " + ex.getMessage());
			   	        		failures++;
			   	        	}
			   	        	finally{
			   	        		updateProgress(++done, allImagesCount);
			   	        	}
			   	            }
		        }
			    String result = (failures==0)  ? 	("DONE! The metadata was written for files number: " + success) : ("WARNING! The metadata was written for files number: " + success + ", failed for files number: " + failures);
			    return result;
			   	}
		 };
		 
			task.setOnFailed(e -> {
				app.log(task.getException().getMessage());
				for (StackTraceElement ste:task.getException().getStackTrace())
					app.log(ste.getClassName() + "." + ste.getMethodName() + "("+ste.getLineNumber()+")");
				writeBtn.setDisable(false);
				stopProgress();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Error");
				alert.setContentText("The metadata write operation failed. See Log for error messages.");
				alert.showAndWait();
			});
			
			task.setOnCancelled(e -> {
				writeBtn.setDisable(false);
				stopProgress();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Cancel");
				alert.setHeaderText("Cancel");
				alert.setContentText("The metadata write operation was cancelled");
				alert.showAndWait();
			});
			
			task.setOnSucceeded(e -> {
				writeBtn.setDisable(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Done!");
				alert.setHeaderText("Done!");
				alert.setContentText(task.getValue());
				alert.showAndWait();
			});
			
	      progress.progressProperty().bind(task.progressProperty());
	      
	    writeBtn.setDisable(true);
		new Thread(task).start();
		 
	}
	
	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle("Select root folder for batches");
          File selected = new File(this.folderPath.getText());
          if (selected.exists())
        	  directoryChooser.setInitialDirectory(selected.getParentFile());

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.rootFolder = file;
        	 this.folderPath.setText(file.getAbsolutePath());
        	 app.targetsController.fillTargets(file);
         }
	}
	
	
	public void stopProgress(){
		 Platform.runLater(new Runnable() {
            public void run() {
            	progress.progressProperty().unbind();
            	progress.setProgress(0.0);
            }
		 });
	}


}
