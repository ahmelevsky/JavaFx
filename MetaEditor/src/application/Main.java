package application;


import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	public KeysEditorController keysEditorController;
	public DescriptionEditorController descriptionEditorController;
	public MainFrameController mainFrameController;
	public TitleEditorController titleEditorController;
	public LogController logController;
	private Stage mainStage;
	private Stage currentStage;
	private VBox rootLayout;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		MetadataWriter.app = this;
		mainStage = primaryStage;
		currentStage = mainStage;
		mainFrameController = organizeStage("MainFrameWindow.fxml");
		mainFrameController.app = this;
		mainStage.setResizable(false);
		keysEditorController = (KeysEditorController) addTab("Ключи", "KeysEditorWindow.fxml", KeysEditorController.class);
		descriptionEditorController = (DescriptionEditorController) addTab("Описания", "DescriptionEditorWindow.fxml", DescriptionEditorController.class);
		titleEditorController = (TitleEditorController) addTab("Заголовки", "TitleEditorWindow.fxml", TitleEditorController.class);
		logController = (LogController) addTab("Лог", "LogWindow.fxml", LogController.class);
		mainStage.setTitle("Meta Editor");
		mainStage.show();
	}
	
	 private MainFrameController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader();
	        InputStream in = MainFrameController.class.getResourceAsStream(fxml);
	        loader.setBuilderFactory(new JavaFXBuilderFactory());
	        loader.setLocation(MainFrameController.class.getResource(fxml));
	        VBox page;
	        try {
	            page = (VBox) loader.load(in);
	            this.rootLayout = page;
	        } finally {
	            in.close();
	        } 
	        Scene scene = new Scene(page);
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        return loader.getController();
	    }
	 
	private Initializable addTab(String tabTitle, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
		 FXMLLoader loader = new FXMLLoader();
	        InputStream in = c.getResourceAsStream(fxml);
	        loader.setBuilderFactory(new JavaFXBuilderFactory());
	        loader.setLocation(c.getResource(fxml));
	        AnchorPane page;
	        try {
	            page = (AnchorPane) loader.load(in);
	        } finally {
	            in.close();
	        } 
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
		logController.log(text);
	}
	
}
