package application;
	
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	private MainController mainController;
	private Stage mainStage;
	
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
		mainStage = primaryStage;
		mainController = (MainController) organizeStage(mainStage, "MainForm.fxml", MainController.class);
		mainController.setMainApp(this);
		mainStage.setResizable(false);
		mainStage.setTitle("Folder Splitter v1.1");
		mainStage.getIcons().add(new Image("file:resources/icon.png"));
		mainStage.show();
	}
	
	 private Initializable organizeStage(Stage stage, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
	        FXMLLoader loader = new FXMLLoader();
	        InputStream in = c.getResourceAsStream(fxml);
	        loader.setBuilderFactory(new JavaFXBuilderFactory());
	        loader.setLocation(c.getResource(fxml));
	        VBox page;
	        try {
	            page = (VBox) loader.load(in);
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

	public Window getPrimaryStage() {
		// TODO Auto-generated method stub
		return mainStage;
	}
}
