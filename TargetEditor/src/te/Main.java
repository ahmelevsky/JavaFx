package te;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import te.model.Target;
import te.model.Variable;
import te.util.ExiftoolRunner;
import te.util.SyntaxParser;
import te.util.TextException;
import te.view.DescriptionEditorController;
import te.view.KeysEditorController;
import te.view.LogController;
import te.view.MainFrameController;
import te.view.TargetEditorController;
import te.view.TargetsWindowController;
import te.view.TitleEditorController;
import te.view.VariableLayoutController;
import te.view.VariablesEditorContainerController;

public class Main extends Application {
	public TargetsWindowController targetsController;
	public KeysEditorController keysEditorController;
	public DescriptionEditorController descriptionEditorController;
	public MainFrameController mainFrameController;
	public TitleEditorController titleEditorController;
	public VariablesEditorContainerController keyVariableEditorContainerController;
	public VariablesEditorContainerController descriptionVariableEditorContainerController;
	public LogController logController;
	private Stage mainStage;
	private Stage currentStage;
	private ObservableList<Target> targetsData = FXCollections.observableArrayList();
	public List<VariableLayoutController> keyVariableControllers = new ArrayList<VariableLayoutController>();
	public List<VariableLayoutController> descriptionVariableControllers = new ArrayList<VariableLayoutController>();
	public boolean isProblem;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		ExiftoolRunner.app = this;
		SyntaxParser.app = this;
		mainStage = primaryStage;
		currentStage = mainStage;
		mainFrameController = organizeStage("view/MainFrameWindow.fxml");
		mainFrameController.app = this;
		mainStage.setResizable(false);
		keyVariableEditorContainerController = (VariablesEditorContainerController) addTab("Переменные \nключей", "view/VariablesEditorContainer.fxml", VariablesEditorContainerController.class);
		descriptionVariableEditorContainerController = (VariablesEditorContainerController) addTab("Переменные \nописаний", "view/VariablesEditorContainer.fxml", VariablesEditorContainerController.class);
		targetsController = (TargetsWindowController) addTab("Таргеты", "view/TargetsWindow.fxml", TargetsWindowController.class);
		targetsController.setup();
		keysEditorController = (KeysEditorController) addTab("Ключи", "view/KeysEditorWindow.fxml", KeysEditorController.class);
		descriptionEditorController = (DescriptionEditorController) addTab("Описания", "view/DescriptionEditorWindow.fxml", DescriptionEditorController.class);
		titleEditorController = (TitleEditorController) addTab("Заголовки", "view/TitleEditorWindow.fxml", TitleEditorController.class);
		logController = (LogController) addTab("Лог", "view/LogWindow.fxml", LogController.class);
		mainFrameController.setup();
		keysEditorController.setup();
		mainStage.setTitle("Target Editor v1.0");
		mainStage.getIcons().add(new Image("file:resources/icon.png"));
		mainStage.show();
	}
	
	 private MainFrameController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource(fxml));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        return loader.getController();
	    }
	 
	private TargetEditorController addTab(String tabTitle, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
		 FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource(fxml));
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
	
	public void log(String text){
		 Platform.runLater(new Runnable() {
             public void run() {
		logController.log(text);
             }});
	}
	
	
	public ObservableList<Target> getTargetsData() {
	        return targetsData;
	    }
	  
	public String getRandomTarget(){
			if (getTargetsData()==null || getTargetsData().isEmpty()) return null;
			return getTargetsData().get(ThreadLocalRandom.current().nextInt(0, getTargetsData().size())).getTarget();
		}
	
	public Target getTargetWithMaxLength(){
		if (getTargetsData()==null || getTargetsData().isEmpty()) return null;
		Target resultTarget = null;
		int max = 0;
		for (Target t:getTargetsData()){
			int l = t.getTarget().length() + t.getTarget1().length() + t.getTarget2().length();
			if (l>max){
				max=l;
				resultTarget = t;
			}
		}
		return resultTarget;
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
				log("ERROR: " + e.getMessage());
				return false;
			}
		 }
		return true; 
	 }
	 
	 public boolean checkDataIsCorrect() {
			boolean result = true;
			//Description Tab
			
			if (!isCorrectTextIncludingVariablesSyntax(descriptionEditorController.textFieldsStored, descriptionVariableEditorContainerController.variables)){
				log("Недопустимые символы в одном из текстовых полей вкладки Описание");
				result = false;
			}
			//Title Tab
			try {
				if (!isCorrectKey(SyntaxParser.checkVariables(descriptionVariableEditorContainerController.variables, titleEditorController.titleTextSetting))){
					log("Недопустимые символы в текстовом поле вкладки Заголовок");
					result = false;
				}
			} catch (TextException e) {
				log("ERROR: " + e.getMessage());
				return false;
			}
			
			//Targets
			if (!getTargetsData().stream().allMatch(t->isCorrectKey(t.getTarget()))){
				log("Недопустимые символы в одном из полей Таргет");
				result = false;
				}
			if (!getTargetsData().stream().allMatch(t->isCorrectKey(t.getTarget1()))){
				log("Недопустимые символы в одном из полей Таргет1");
				result = false;
				}
			if (!getTargetsData().stream().allMatch(t->isCorrectKey(t.getTarget1()))){
				log("Недопустимые символы в одном из полей Таргет2");
				result = false;
				}
			
			//Variables
			for (Variable v:descriptionVariableEditorContainerController.variables){  
				if(!v.getValues().stream().allMatch(vv->isCorrectKey(vv))) {
			    		log("Недопустимые символы в одном значений переменной " + v.getName());
			    		result = false;
				}
			}
			for (Variable v:keyVariableEditorContainerController.variables){  
				if(!v.getValues().stream().allMatch(vv->isCorrectKey(vv))) {
			    		log("Недопустимые символы в одном значений переменной " + v.getName());
			    		result = false;
				}
			}
			return result;
		}
		
	 
}
