package te;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import te.model.Target;
import te.util.ExiftoolRunner;
import te.view.DescriptionEditorController;
import te.view.KeysEditorController;
import te.view.LogController;
import te.view.MainFrameController;
import te.view.TargetsWindowController;
import te.view.TitleEditorController;


public class Main extends Application {
	public TargetsWindowController targetsController;
	public KeysEditorController keysEditorController;
	public DescriptionEditorController descriptionEditorController;
	public MainFrameController mainFrameController;
	public TitleEditorController titleEditorController;
	public LogController logController;
	private Stage mainStage;
	private Stage currentStage;
	private VBox rootLayout;
	private ObservableList<Target> targetsData = FXCollections.observableArrayList();
	public Map<String, List<String>> variables = new HashMap<String, List<String>>();
	
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		ExiftoolRunner.app = this;
		mainStage = primaryStage;
		currentStage = mainStage;
		mainFrameController = organizeStage("view/MainFrameWindow.fxml");
		mainFrameController.app = this;
		mainStage.setResizable(false);
		targetsController = (TargetsWindowController) addTab("Таргеты", "view/TargetsWindow.fxml", TargetsWindowController.class);
		targetsController.setMainApp(this);
		keysEditorController = (KeysEditorController) addTab("Ключи", "view/KeysEditorWindow.fxml", KeysEditorController.class);
		keysEditorController.app = this;
		descriptionEditorController = (DescriptionEditorController) addTab("Описания", "view/DescriptionEditorWindow.fxml", DescriptionEditorController.class);
		descriptionEditorController.app = this;
		titleEditorController = (TitleEditorController) addTab("Заголовки", "view/TitleEditorWindow.fxml", TitleEditorController.class);
		titleEditorController.app = this;
		logController = (LogController) addTab("Лог", "view/LogWindow.fxml", LogController.class);
		mainStage.setTitle("Target Editor v1.0");
		mainStage.getIcons().add(new Image("file:resources/icon.png"));
		mainStage.show();
	}
	
	 private MainFrameController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource(fxml));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        return loader.getController();
	    }
	 
	private Initializable addTab(String tabTitle, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
		 FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource(fxml));
	        AnchorPane page = (AnchorPane) loader.load();
	        mainFrameController.addTab(tabTitle, page);
	        return (Initializable) loader.getController();
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
}
