package te.view;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import te.Main;
import te.MemoryLogger;
import te.Settings;
import te.model.FolderVariable;
import te.model.Target;
import te.model.Variable;
import te.util.DataException;
import te.util.ExiftoolRunner;

import java.util.logging.Level;
import java.util.logging.Logger;


public class MainFrameController implements Initializable{

	public Main app;
	private File rootFolder;
	public FolderVariable currentFolder;
	public Target currentTarget;
	private Task<String> task;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
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
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu menuData;
	@FXML
	private Menu menuWrite;
	@FXML
	private Menu menuSettings;
	@FXML
	private MenuItem loadItem;
	@FXML
	private MenuItem saveStateItem;
	@FXML
	private MenuItem saveAsItem;
	@FXML
	private MenuItem clearAllItem;
	@FXML
	private MenuItem closeItem;
	@FXML
	private RadioMenuItem writeOnlyEPSItem;
	@FXML
	private RadioMenuItem writeOnlyJPGItem;
	@FXML
	private RadioMenuItem writeAllItem;
	@FXML
	private MenuItem writeMetadataItem;
	@FXML
	private RadioMenuItem languageRuItem;
	@FXML
	private RadioMenuItem languageEnItem;
	@FXML
	private MenuItem aboutItem;
	
	
	private boolean isWriteBothExtensions;
	private String ext = "jpg";
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void setup(){
		setLanguage(Settings.getLanguage());
        setWriteOption(Settings.getWriteOption());
        
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
					alert.setTitle(Settings.bundle.getString("alert.error.title"));
					alert.setContentText(Settings.bundle.getString("alert.error.duplicatevariables"));
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
					alert.setTitle(Settings.bundle.getString("alert.error.title"));
					alert.setContentText(Settings.bundle.getString("alert.error.duplicatevariables"));
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
		LOGGER.info("Start Writing metadata");
		MemoryLogger.print();
		
		try{
			app.saveLastData();
		}
		 catch (Exception e) {
			 Alert alert = new Alert(AlertType.ERROR);
			    alert.setTitle(Settings.bundle.getString("alert.error.title"));
	            alert.setHeaderText(Settings.bundle.getString("alert.error.saveerror.header"));
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
		 }
		app.isProblem = false;
		if (rootFolder==null || !rootFolder.exists() || !rootFolder.isDirectory()){
			app.showAlert(Settings.bundle.getString("alert.error.nofolder"));
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
              	return f.isFile() && f.getName().toLowerCase().endsWith(ext);
              }
 	        }).length;
		 }
		 
		 if (allImagesCounter==0){
				app.showAlert(Settings.bundle.getString("alert.error.nofiles") + this.ext);
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
			app.showAlert(Settings.bundle.getString("alert.error.incorrectdata"));
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
			                	return f.isFile() && f.getName().toLowerCase().endsWith(ext);
			                }
			   	        });
			
			   	        if (images.length==0) {
			   	        	LOGGER.warning("There is no " + ext + " files in the folder: " + dir.getAbsolutePath());
			   	        }
		    	   
			   	        
			   	      LOGGER.info("Finishing pre-oprateion, start writing files");
			   	        
			   	        for (File image:images){
			   	        	try{
			   	        	    LOGGER.info("Current file: " + image.getName());
			   	        		currentTarget = app.getRandomTarget();
			   	        		List<String> keywords = app.keysEditorController.generateKeywordsForMetadata();
			   	        		String description  =  app.descriptionEditorController.generateDescriptionForMetadata();
			   	        		String title = app.titleEditorController.getTitleForMetadata();
			   	        		ExiftoolRunner.writeMetadataToFile (image, keywords, title, description);
			   	         		success++;
			   	         		
			   	        	}
			   	        	catch (IOException ex) {
			   	        		LOGGER.warning(Settings.bundle.getString("alert.error.writeerror") + image.getAbsolutePath() + " : " + ex.getMessage());
			   	        		failures++;
			   	        	}
			   	        	finally{
			   	        		updateProgress(++done, allImagesCount);
			   	        	 MemoryLogger.print();
			   	        	}
			   	            }
		        }
		        
		        LOGGER.info("Finish Writing metadata");
		        MemoryLogger.print();
		           
			    String result = (failures==0)  ? 	(Settings.bundle.getString("alert.info.done") + success) : (Settings.bundle.getString("alert.warn.done") + success + Settings.bundle.getString("alert.warn.done2")   + failures);
			    if (app.isProblem)
			    	result += Settings.bundle.getString("alert.problem.done");
			    return result;
			   	}
		 };
		 
			task.setOnFailed(e -> {
				LOGGER.warning(task.getException().getMessage());
				for (StackTraceElement ste:task.getException().getStackTrace())
					LOGGER.warning(ste.getClassName() + "." + ste.getMethodName() + "("+ste.getLineNumber()+")");
				writeBtn.setDisable(false);
				stopProgress();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Error");
				alert.setContentText(Settings.bundle.getString("alert.error.done"));
				alert.showAndWait();
			});
			
			task.setOnCancelled(e -> {
				writeBtn.setDisable(false);
				stopProgress();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Cancel");
				alert.setHeaderText("Cancel");
				alert.setContentText(Settings.bundle.getString("alert.write.cancelled"));
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
		if (rootFolder==null) return false;
		this.rootFolder = new File(rootFolder);
		if (!this.rootFolder.exists()) return false;
		this.folderPath.setText(rootFolder);
		app.folderVariableController.fillFolderVariables(this.rootFolder);
		return true;
	}

	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle(Settings.bundle.getString("alert.info.selectfolder"));
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
		ButtonType yes = new ButtonType("Да");
		ButtonType no = new ButtonType("Нет");
		Alert alert = new Alert(AlertType.CONFIRMATION, Settings.bundle.getString("alert.conf.clear"), yes, no);
		alert.setTitle(Settings.bundle.getString("alert.conf.clear.title"));
		alert.setHeaderText(Settings.bundle.getString("alert.conf.clear.header"));
		alert.showAndWait();

		if (alert.getResult() == yes) {
			app.clearAllData();
		}
		
	}

	
	
	public void setLanguage(String language) {
		if (language.equals("ru")) 
			languageRuItem.setSelected(true);
		else if (language.equals("en")) 
			languageEnItem.setSelected(true);
		else 
			languageEnItem.setSelected(true);
	}
		
    	public void setWriteOption(String writeOption) {
			if (writeOption.equals("jpg")) {
				writeOnlyJPGItem.setSelected(true);
				this.isWriteBothExtensions = false;
				this.ext = "jpg";
			}
			else if (writeOption.equals("eps")) {
				writeOnlyEPSItem.setSelected(true);
				this.isWriteBothExtensions = false;
				this.ext = "eps";
			}
			else if (writeOption.equals("all")) {
				writeAllItem.setSelected(true);
				this.isWriteBothExtensions = true;
			}
			else {
				writeOnlyJPGItem.setSelected(true);
				this.isWriteBothExtensions = false;
				this.ext = "jpg";
			}
		}
		
		public void updateSettings(ActionEvent e) {
			switch (((RadioMenuItem)e.getSource()).getId()) {
			
			case "writeOnlyJPGItem":
				Settings.setWriteOption("jpg");
				this.isWriteBothExtensions = false;
				this.ext = "jpg";
				break;
			case "writeOnlyEPSItem":
				Settings.setWriteOption("eps");
				this.isWriteBothExtensions = false;
				this.ext = "eps";
				break;
			case "writeAllItem":
				Settings.setWriteOption("all");
				this.isWriteBothExtensions = true;
				break;
			case "languageRuItem":
				Settings.setLanguage("ru");
				showChangeLanguageAlert();
				break;
			case "languageEnItem":
				Settings.setLanguage("en");
				showChangeLanguageAlert();
				break;     
			default:
				break;
			}
			app.saveSettings();
		}
		
		private void showChangeLanguageAlert() {
			 Alert alert = new Alert(AlertType.INFORMATION);
	            alert.setContentText("Please restart TargetEditor to apply the language settings!\n\rПожалуйста перезапустите TargetEditor, чтобы применить новые языковые настройки");
	            alert.showAndWait();
		}
}
