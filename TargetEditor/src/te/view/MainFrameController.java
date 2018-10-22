package te.view;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import te.model.FolderVariable;
import te.model.Target;
import te.model.Variable;
import te.util.DataException;
import te.util.ExiftoolRunner;

public class MainFrameController implements Initializable{

	public Main app;
	private File rootFolder;
	public FolderVariable currentFolder;
	public Target currentTarget;
	private Task<String> task;
	
	@FXML
	private Button writeBtn;
	@FXML
	private Hyperlink folderPath;
	@FXML
	private TabPane tabs;
	@FXML
	private ProgressBar progress;
	@FXML
	private Button clearBtn;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
			
	}
	
	public void setup(){
		tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (oldTab!=null && oldTab.equals(app.keyVariableEditorContainerController.tab)){
				for (int i=app.keyVariableControllers.size(); i>0; i--) {
					Variable v = app.keyVariableControllers.get(i-1).getVariable();
					if ( v == null || v.getName().isEmpty())
						   app.keyVariableEditorContainerController.removeVariableLayout(app.keyVariableControllers.get(i-1));
				}
				if (!app.keyVariableEditorContainerController.variables.stream().map(Variable::getName)
			              .collect(Collectors.toList()).stream().allMatch(new HashSet<>()::add)){
					tabs.getSelectionModel().select(oldTab);
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("ОШИБКА!");
					alert.setContentText("Имена переменных не должны повторяться, исправьте прежде чем продолжить.");
					alert.showAndWait();
				}
			}
			if (oldTab!=null && oldTab.equals(app.descriptionVariableEditorContainerController.tab)){
				for (int i=app.descriptionVariableControllers.size(); i>0; i--) {
					Variable v = app.descriptionVariableControllers.get(i-1).getVariable();
					if ( v == null || v.getName().isEmpty())
						   app.descriptionVariableEditorContainerController.removeVariableLayout(app.descriptionVariableControllers.get(i-1));
				}
				if (!app.descriptionVariableEditorContainerController.variables.stream().map(Variable::getName)
			              .collect(Collectors.toList()).stream().allMatch(new HashSet<>()::add)){
					tabs.getSelectionModel().select(oldTab);
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("ОШИБКА!");
					alert.setContentText("Имена переменных не должны повторяться, исправьте прежде чем продолжить.");
					alert.showAndWait();
				}
			}
			if (newTab.equals(app.descriptionEditorController.tab)){
				app.descriptionEditorController.updateLists();
			}
			else if (newTab.equals(app.titleEditorController.tab)){
				app.titleEditorController.updateLists();
			}
			else if (newTab.equals(app.keysEditorController.tab)){
				app.keysEditorController.update();
			}
	    });
	}
	
	
	public void addTab(Tab tab){
		tabs.getTabs().add(tab);
	}
	
	
	
	@FXML
	public void writeMetadata() throws SAXException, ParserConfigurationException, TransformerException, IOException{
		try{
			app.saveLastSettings();
		}
		 catch (Exception e) {
			 Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Ошибка");
	            alert.setHeaderText("Ошибка сохранения данных в xml");
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
		 }
		app.isProblem = false;
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
		
		try {
			app.saveTargetsData();
			app.keyVariableEditorContainerController.saveVariables();
			app.descriptionVariableEditorContainerController.saveVariables();
			app.folderVariableController.saveVariables();
			app.keysEditorController.saveKeywordsSource();
			app.descriptionEditorController.saveDescriptionsSource();
			app.titleEditorController.saveTitleSource();
		} catch (DataException e1) {
			tabs.getSelectionModel().select(e1.errorTab);
			return;
		}
		       		
		if (!app.checkDataIsCorrect()){ 
			app.showAlert("Некорректные входные данные, невозможно осуществить запись. Подробности на вкладке Лог.");
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
			            currentFolder = app.getSavedFolderVariableData().stream().filter(t->t.getFolder().equals(dir)).findFirst().get();
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
			   	        		currentTarget = app.getRandomTarget();
			   	        		List<String> keywords = app.keysEditorController.generateKeywordsForMetadata();
			   	        		String description  =  app.descriptionEditorController.generateDescriptionForMetadata();
			   	        		String title = app.titleEditorController.getTitleForMetadata();
			   	        		ExiftoolRunner.writeMetadataToFile (image, keywords, title, description);
			   	         		success++;
			   	        	}
			   	        	catch (IOException ex) {
			   	        		app.log("ERROR: ошибка записи метаданных в " + image.getAbsolutePath() + ", текст ошибки: " + ex.getMessage());
			   	        		failures++;
			   	        	}
			   	        	finally{
			   	        		updateProgress(++done, allImagesCount);
			   	        	}
			   	            }
		        }
			    String result = (failures==0)  ? 	("ГОТОВО: Метеданные были записаны для файлов: " + success) : ("WARNING! Метеданные были записаны для файлов: " + success + ", не получилось записать файлов: " + failures);
			    if (app.isProblem)
			    	result += "\nВо время вычисления метаданных были проблемы, обратите внимание на вкладку Лог";
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
				alert.setContentText("Запись метаданных завершена с ошибкой. Смотрите вкладку Лог для пояснения.");
				alert.showAndWait();
			});
			
			task.setOnCancelled(e -> {
				writeBtn.setDisable(false);
				stopProgress();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Cancel");
				alert.setHeaderText("Cancel");
				alert.setContentText("Запись методанных была отменена");
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
	
	public String getRootFolder() {
		if (rootFolder==null) return null;
		else return rootFolder.getAbsolutePath();
	}

	public boolean setRootFolder(String rootFolder) {
		this.rootFolder = new File(rootFolder);
		if (!this.rootFolder.exists()) return false;
		this.folderPath.setText(rootFolder);
		app.folderVariableController.fillFolderVariables(this.rootFolder);
		return true;
	}

	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle("Выберите корневую папку (в ней должны быть папки с вашими файлами)");
          File selected = new File(this.folderPath.getText());
          if (selected.exists())
        	  directoryChooser.setInitialDirectory(selected.getParentFile());

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.rootFolder = file;
        	 this.folderPath.setText(file.getAbsolutePath());
        	 app.folderVariableController.fillFolderVariables(file);
        	 app.updateAllForms();
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

	@FXML
	private void clearAllData(){
		app.clearAllData();
	}

}
