package te;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import te.model.DataWrapper;
import te.model.FolderVariable;
import te.model.KeysWrapper;
import te.model.SettingsWrapper;
import te.model.Target;
import te.model.Variable;
import te.util.ExiftoolRunner;
import te.util.SyntaxParser;
import te.util.TextException;
import te.view.DescriptionEditorController;
import te.view.FolderVariableController;
import te.view.KeysEditorController;
import te.view.MainFrameController;
import te.view.TargetEditorController;
import te.view.TargetsWindowController;
import te.view.TitleEditorController;
import te.view.VariableLayoutController;
import te.view.VariablesEditorContainerController;

public class Main extends Application {
	public Set<TargetEditorController> controllers = new HashSet<TargetEditorController>();
	public TargetsWindowController targetsController;
	public KeysEditorController keysEditorController;
	public DescriptionEditorController descriptionEditorController;
	public FolderVariableController folderVariableController;
	public MainFrameController mainFrameController;
	public TitleEditorController titleEditorController;
	public VariablesEditorContainerController keyVariableEditorContainerController;
	public VariablesEditorContainerController descriptionVariableEditorContainerController;
	private Stage mainStage;
	private Stage currentStage;
	public ObservableList<FolderVariable> folderVariableData = FXCollections.observableArrayList();
	public ObservableList<FolderVariable> savedFolderVariableData = FXCollections.observableArrayList();
	public ObservableList<Target> targetsData = FXCollections.observableArrayList();
	public ObservableList<Target> savedTargetsData = FXCollections.observableArrayList();
	public List<VariableLayoutController> keyVariableControllers = new ArrayList<VariableLayoutController>();
	public List<VariableLayoutController> descriptionVariableControllers = new ArrayList<VariableLayoutController>();
	public boolean isProblem;
	public File dataFile = new File(System.getProperty("user.home") + File.separator + "TargetEditor.xml");
	public File dataFileTemp = new File(System.getProperty("user.home") + File.separator + "TargetEditor_Temp.xml");
	public File settingsFile = new File(System.getProperty("user.home") + File.separator + "TargetEditorSettings.xml");
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
		try {
            TargetLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
		loadSettings();
		Settings.bundle = ResourceBundle.getBundle("te.Language", Settings.locale);
		ExiftoolRunner.app = this;
		SyntaxParser.app = this;
		mainStage = primaryStage;
		currentStage = mainStage;
		mainFrameController = organizeStage("view/MainFrameWindow.fxml");
		mainFrameController.app = this;
		mainStage.setResizable(false);
		keyVariableEditorContainerController = (VariablesEditorContainerController) addTab(Settings.bundle.getString("ui.tabs.keyvars.header"), "view/VariablesEditorContainer.fxml", VariablesEditorContainerController.class);
		keyVariableEditorContainerController.controllersList = keyVariableControllers;
		descriptionVariableEditorContainerController = (VariablesEditorContainerController) addTab(Settings.bundle.getString("ui.tabs.descvars.header"), "view/VariablesEditorContainer.fxml", VariablesEditorContainerController.class);
		descriptionVariableEditorContainerController.controllersList = descriptionVariableControllers;
		targetsController = (TargetsWindowController) addTab(Settings.bundle.getString("ui.tabs.targets.header"), "view/TargetsWindow.fxml", TargetsWindowController.class);
		targetsController.setup();
		folderVariableController = (FolderVariableController) addTab(Settings.bundle.getString("ui.tabs.foldervars.header"), "view/FolderVariablesWindow.fxml", FolderVariableController.class);
		folderVariableController.setup();
		keysEditorController = (KeysEditorController) addTab(Settings.bundle.getString("ui.tabs.keys.header"), "view/KeysEditorWindow.fxml", KeysEditorController.class);
		descriptionEditorController = (DescriptionEditorController) addTab(Settings.bundle.getString("ui.tabs.descriptions.header"), "view/DescriptionEditorWindow.fxml", DescriptionEditorController.class);
		titleEditorController = (TitleEditorController) addTab(Settings.bundle.getString("ui.tabs.titles.header"), "view/TitleEditorWindow.fxml", TitleEditorController.class);
		mainFrameController.setup();
		keysEditorController.setup();
		mainStage.setTitle("Target Editor v2.0");
		mainStage.getIcons().add(new Image("file:resources/icon.png"));
		loadLastData();
		for (TargetEditorController controller : this.controllers)
			controller.loadData();
		mainStage.show();
	}
	
	 @Override
	 public void stop(){
	     saveLastData();
	     saveSettings();
	     for(Handler h:LOGGER.getHandlers())
	     {
	         h.close();   
	     }
	 }
	
	 private MainFrameController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        loader.setResources(Settings.bundle);
	      //  VBox page = (VBox) loader.load(Main.class.getResource(fxml), this.bundle);
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        return loader.getController();
	    }
	 
	private TargetEditorController addTab(String tabTitle, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        loader.setResources(Settings.bundle);
	        AnchorPane page = (AnchorPane) loader.load();
	        Tab tab = new Tab(tabTitle, page);
	        tab.setStyle("-fx-padding: 15 0 15 0;-fx-min-height: 30px;-fx-focus-color: transparent;");
	        Label label = new Label(tabTitle);
	        label.setStyle("-fx-padding: 20px;-fx-min-height: 100px;-fx-focus-color: transparent;");
	        label.setWrapText(true);
	        label.setAlignment(Pos.CENTER);
	        label.setTextAlignment(TextAlignment.CENTER);
	        tab.setText(null);
	        tab.setGraphic(label);
	        mainFrameController.addTab(tab);
	        TargetEditorController controller = loader.getController();
	        controller.tab = tab;
	        controller.app = this;
	        this.controllers.add(controller);
	        return controller;
	}
	 
	 
	public static void main(String[] args) {
		launch(args); 
	}
	
	
	public Stage getPrimaryStage(){
		return this.currentStage;
	}
	
	public void showAlert(String text){
		 Platform.runLater(new Runnable() {
             public void run() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setHeaderText("ERROR");
		alert.setContentText(text);
		alert.showAndWait();
             }
		 });
	}
	
	
	public ObservableList<Target> getTargetsData() {
	        return targetsData;
	    }
	  
	public ObservableList<Target> getSavedTargetsData() {
        return savedTargetsData;
    }
	
	public void saveTargetsData(){
		savedTargetsData.clear();
		savedTargetsData.addAll(targetsData);
	}
	
	public String getRandomTargetKwd(){
			if (getTargetsData()==null || getTargetsData().isEmpty()) return null;
			return getTargetsData().get(ThreadLocalRandom.current().nextInt(0, getTargetsData().size())).getTargetKwd();
		}
	
	public String getRandomFolderKwd(){
		if (getFolderVariableData()==null || getFolderVariableData().isEmpty()) return null;
		List<FolderVariable> notEmpty = getFolderVariableData().stream().filter(f -> f.getKeyVariable()!=null && !f.getKeyVariable().trim().isEmpty()).collect(Collectors.toList());
		if (notEmpty.isEmpty())
			return null;
		return notEmpty.get(ThreadLocalRandom.current().nextInt(0, notEmpty.size())).getKeyVariable();
	}
	
	public String getRandomFolderDescr(){
		if (getFolderVariableData()==null || getFolderVariableData().isEmpty()) return null;
		List<FolderVariable> notEmpty = getFolderVariableData().stream().filter(f -> f.getDescriptionVariable()!=null && !f.getDescriptionVariable().trim().isEmpty()).collect(Collectors.toList());
		if (notEmpty.isEmpty())
			return null;
		return notEmpty.get(ThreadLocalRandom.current().nextInt(0, notEmpty.size())).getDescriptionVariable();
	}
	
	public Target getRandomTarget(){
		if (getTargetsData()==null || getTargetsData().isEmpty()) return null;
		return getTargetsData().get(ThreadLocalRandom.current().nextInt(0, getTargetsData().size()));
	}
	
	public Target getTargetWithMaxLength(){
		if (getTargetsData()==null || getTargetsData().isEmpty()) return null;
		Target resultTarget = null;
		int max = 0;
		for (Target t:getTargetsData()){
			int l = t.getTargetDescr1().length() + t.getTargetDescr2().length();
			if (l>max){
				max=l;
				resultTarget = t;
			}
		}
		return resultTarget;
	}
	
	
	
	
	public ObservableList<FolderVariable> getFolderVariableData() {
	        return folderVariableData;
	    }
	  
	public ObservableList<FolderVariable> getSavedFolderVariableData() {
        return savedFolderVariableData;
    }
	
	public void saveFolderVariableData(){
		savedFolderVariableData.clear();
		savedFolderVariableData.addAll(folderVariableData);
	}

	
	 public boolean isCorrectKey(String text){
		  if (text == null) return true;
		 return text.trim().isEmpty() || text.matches("\\A\\p{ASCII}*\\z");//|| text.replaceAll("\\s+","").matches("\\w+");
	  }
	 
	 private boolean isCorrectTextIncludingVariablesSyntax(List<String> list, List<Variable> variables){
		 for (String l:list){
			 try {
				if (!isCorrectKey(SyntaxParser.checkVariables(variables, l))){
					return false;
				}
					
			} catch (TextException e) {
				LOGGER.severe("ERROR: " + e.getMessage());
				return false;
			}
		 }
		return true; 
	 }
	 
	 public boolean checkDataIsCorrect() {
			boolean result = true;
			//Description Tab
			
			if (!isCorrectTextIncludingVariablesSyntax(descriptionEditorController.textFieldsStored, descriptionVariableEditorContainerController.variables)){
				LOGGER.severe(Settings.bundle.getString("log.message.unnalloweddescription"));
				result = false;
			}
			//Title Tab
			try {
				if (!isCorrectKey(SyntaxParser.checkVariables(descriptionVariableEditorContainerController.variables, titleEditorController.titleTextSetting))){
					LOGGER.severe(Settings.bundle.getString("log.message.unnallowedkeywords"));
					result = false;
				}
			} catch (TextException e) {
				LOGGER.severe("ERROR: " + e.getMessage());
				return false;
			}
			
			//Targets
			if (!getTargetsData().stream().allMatch(t->isCorrectKey(t.getTargetKwd()))){
				LOGGER.severe(Settings.bundle.getString("log.message.unnallowedtarget"));
				result = false;
				}
			if (!getTargetsData().stream().allMatch(t->isCorrectKey(t.getTargetDescr1()))){
				LOGGER.severe(Settings.bundle.getString("log.message.unnallowedtarget1"));
				result = false;
				}
			if (!getTargetsData().stream().allMatch(t->isCorrectKey(t.getTargetDescr2()))){
				LOGGER.severe(Settings.bundle.getString("log.message.unnallowedtarget2"));
				result = false;
				}
			
			//Variables
			for (Variable v:descriptionVariableEditorContainerController.variables){  
				if(!v.getValues().stream().allMatch(vv->isCorrectKey(vv))) {
					LOGGER.severe(Settings.bundle.getString("log.message.unnallowedvars") + v.getName());
			    		result = false;
				}
			}
			for (Variable v:keyVariableEditorContainerController.variables){  
				if(!v.getValues().stream().allMatch(vv->isCorrectKey(vv))) {
					LOGGER.severe(Settings.bundle.getString("log.message.unnallowedvars") + v.getName());
			    		result = false;
				}
			}
			return result;
		}
		
	 public void updateAllForms(){
		 keysEditorController.update();
		 descriptionEditorController.updateLists();
		 titleEditorController.updateLists();
	 }
	 
	 /** Clears all but not Targets (selected folder), and not saved data -
	  *     to allow use clear function during writing metadata. 
	  */
	 public void clearAllData(){
		 keyVariableEditorContainerController.clear();
		 descriptionVariableEditorContainerController.clear();
		 keysEditorController.clearAll();
		 keysEditorController.setup();
		 descriptionEditorController.clearAll();
		 titleEditorController.clearAll();
		 keyVariableControllers = new ArrayList<VariableLayoutController>();
		 descriptionVariableControllers = new ArrayList<VariableLayoutController>();
		 this.targetsData.clear();
		 this.folderVariableData.forEach(v -> {
			 v.setDescriptionVariable("");
			 v.setKeyVariable("");
		 });
	 }
	 
	 
	 
	    public File getKeysFilePath() {
	        Preferences prefs = Preferences.userNodeForPackage(Main.class);
	        String filePath = prefs.get("targetEditorFilePath", null);
	        if (filePath != null) {
	            return new File(filePath);
	        } else {
	            return null;
	        }
	    }

	    public void setKeysFilePath(File file) {
	        Preferences prefs = Preferences.userNodeForPackage(Main.class);
	        if (file != null) {
	            prefs.put("targetEditorFilePath", file.getPath());
	        } else {
	            prefs.remove("targetEditorFilePath");
	        }
	    }
	 
	    public void loadKeysDataFromFile(File file) {
	        try {
	            JAXBContext context = JAXBContext
	                    .newInstance(KeysWrapper.class);
	            Unmarshaller um = context.createUnmarshaller();

	            // Чтение XML из файла и демаршализация.
	            KeysWrapper wrapper = (KeysWrapper) um.unmarshal(file);

	            
	            this.keyVariableEditorContainerController.clear();
	            this.descriptionVariableEditorContainerController.clear();
	            this.targetsData.clear();
	            this.keyVariableEditorContainerController.variables.addAll(wrapper.getKeyVariables());
	            this.descriptionVariableEditorContainerController.variables.addAll(wrapper.getDescriptionVariables());
	            this.descriptionVariableEditorContainerController.loadData();
	            this.keyVariableEditorContainerController.loadData();
	            this.targetsData.addAll(wrapper.getTarget());
	            this.targetsController.loadData();
	            this.keysEditorController.setup();
	            // Сохраняем путь к файлу в реестре.
	            setKeysFilePath(file);

	        } catch (Exception e) { // catches ANY exception
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Could not load data");
	            alert.setContentText("Could not load data from file:\n" + file.getPath());

	            alert.showAndWait();
	        }
	    }

	    
	    public void saveKeysDataToFile(File file) {
	        try {
	            JAXBContext context = JAXBContext
	                    .newInstance(KeysWrapper.class);
	            Marshaller m = context.createMarshaller();
	            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	            // Обёртываем наши данные об адресатах.
	            KeysWrapper wrapper = new KeysWrapper();
	            wrapper.setKeyVariables(this.keyVariableEditorContainerController.variables);
                wrapper.setDescriptionVariables(this.descriptionVariableEditorContainerController.variables);
                wrapper.setTargets(this.targetsData);
	            // Маршаллируем и сохраняем XML в файл.
	            m.marshal(wrapper, file);

	            // Сохраняем путь к файлу в реестре.
	            setKeysFilePath(file);
	        } catch (Exception e) { // catches ANY exception
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Could not save data");
	            alert.setContentText("Could not save data to file:\n" + file.getPath());

	            alert.showAndWait();
	        }
	    }
	    public void loadLastData(){
			  try {
				   if (!this.dataFile.exists())
					   return;
		            JAXBContext context = JAXBContext
		                    .newInstance(DataWrapper.class);
		            Unmarshaller um = context.createUnmarshaller();

		            DataWrapper wrapper = (DataWrapper) um.unmarshal(this.dataFile);

		            this.keyVariableEditorContainerController.variables.addAll(wrapper.getKeyVariables());
		            this.descriptionVariableEditorContainerController.variables.addAll(wrapper.getDescriptionVariables());
		            this.targetsData.addAll(wrapper.getTarget());
		            if (wrapper.getKeysPage()!=null)
		            	this.keysEditorController.wrapper = wrapper.getKeysPage();
		            if (wrapper.getDescriptionPage()!=null)
		            	this.descriptionEditorController.wrapper = wrapper.getDescriptionPage();
		            if (wrapper.getTitlePage()!=null)
		            	this.titleEditorController.wrapper = wrapper.getTitlePage();
		            if (wrapper.getFolderWrapper() !=null)
		            	this.folderVariableController.wrapper = wrapper.getFolderWrapper();
		            
		        } catch (Exception e) { // catches ANY exception
		        	Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle(Settings.bundle.getString("alert.error.title"));
		            alert.setHeaderText(Settings.bundle.getString("alert.error.loaderror.header"));
		            alert.setContentText(Settings.bundle.getString("alert.error.loaderror.content") +":\n" + this.dataFile.getPath());

		            alert.showAndWait();
		        }
		 }
	    
		 public void saveLastData(){
			 try {
			      for (TargetEditorController controller:this.controllers)
		    	         controller.saveData();
				  JAXBContext context = JAXBContext
		                    .newInstance(DataWrapper.class);
		            Marshaller m = context.createMarshaller();
		            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		            // Обёртываем наши данные об адресатах.
		            DataWrapper wrapper = new DataWrapper();
		            wrapper.setKeyVariables(this.keyVariableEditorContainerController.variables);
	                wrapper.setDescriptionVariables(this.descriptionVariableEditorContainerController.variables);
	                wrapper.setTargets(this.targetsData);
                    if (this.descriptionEditorController.wrapper !=null)
                    	wrapper.setDescriptionPage(this.descriptionEditorController.wrapper);
                    if (this.keysEditorController.wrapper !=null)
                    	wrapper.setKeysPage(this.keysEditorController.wrapper);
                    if (this.titleEditorController.wrapper !=null)
                    	wrapper.setTitlePage(this.titleEditorController.wrapper);
                    if (this.folderVariableController.wrapper !=null)
                    	wrapper.setFolderWrapper(this.folderVariableController.wrapper);
		            // Маршаллируем и сохраняем XML в файл.
		            m.marshal(wrapper, this.dataFileTemp);
		            this.dataFile.delete();
		   	        this.dataFileTemp.renameTo(this.dataFile);
			 } catch (Exception e) { // catches ANY exception
				 Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle(Settings.bundle.getString("alert.error.title"));
		            alert.setHeaderText(Settings.bundle.getString("alert.error.saveerror.header"));
		            alert.setContentText(Settings.bundle.getString("alert.error.saveerror.content") +":\n" + this.settingsFile.getPath());
		            alert.showAndWait();
		        }
		   	        
		 }
		 
		 public void loadSettings(){
			  try {
				   if (!this.settingsFile.exists())
					   return;
		            JAXBContext context = JAXBContext
		                    .newInstance(SettingsWrapper.class);
		            Unmarshaller um = context.createUnmarshaller();

		            SettingsWrapper  wrapper = (SettingsWrapper) um.unmarshal(this.settingsFile);
                    Settings.setLanguage(wrapper.language);
                    Settings.setWriteOption(wrapper.writeOption);
		            
		        } catch (Exception e) { // catches ANY exception
		        	 Alert alert = new Alert(AlertType.ERROR);
		        	alert.setTitle("Error");
		            alert.setHeaderText("Error loading settings");
		            alert.setContentText("Can't read file:\n" + this.settingsFile.getPath());
		            alert.showAndWait();
		        }
		 }
		 
		 
		 public void saveSettings(){
			 try {
		      
			  JAXBContext context = JAXBContext
	                    .newInstance(SettingsWrapper.class);
	            Marshaller m = context.createMarshaller();
	            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	            // Обёртываем наши данные об адресатах.
	            SettingsWrapper wrapper = new SettingsWrapper();
	            wrapper.language = Settings.getLanguage();
	            wrapper.writeOption = Settings.getWriteOption();
               
	            // Маршаллируем и сохраняем XML в файл.
	            m.marshal(wrapper, this.settingsFile);
			 } catch (Exception e) { // catches ANY exception
				 Alert alert = new Alert(AlertType.ERROR);
		            alert.setTitle(Settings.bundle.getString("alert.error.title"));
		            alert.setHeaderText(Settings.bundle.getString("alert.error.saveerror.header"));
		            alert.setContentText(Settings.bundle.getString("alert.error.saveerror.content") +":\n" + this.settingsFile.getPath());
		            alert.showAndWait();
		        }
	 }
		 
		 
}
