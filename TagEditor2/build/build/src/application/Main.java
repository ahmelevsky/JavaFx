package application;


import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	private MainController mainController;
	private NewController newController;
	private Stage mainStage;
	private Stage currentStage;
	
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
		mainStage = primaryStage;
		currentStage = mainStage;
		mainController = (MainController) organizeStage(mainStage, "MainWindow.fxml", MainController.class);
		mainController.app = this;
		//newController = (NewController) organizeStage(mainStage, "NewWindow.fxml", NewController.class);
		mainStage.setResizable(false);
		mainStage.show();
	}
	
	 private Initializable organizeStage(Stage stage, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
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
	        Scene scene = new Scene(page);
	        stage.setScene(scene);
	        stage.sizeToScene();
	        return (Initializable) loader.getController();
	    }
	 
	public static void main(String[] args) {
		launch(args); 
	}
	
	
	public Stage getPrimaryStage(){
		return this.currentStage;
	}
	
	
	
}
