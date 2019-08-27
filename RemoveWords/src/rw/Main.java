package rw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private MainController controller;
	private Stage mainStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainForm.fxml"));
	        loader.setLocation(Main.class.getResource("MainForm.fxml"));
	        HBox page = (HBox) loader.load();
	        Scene scene = new Scene(page);
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        controller = loader.getController();
	        mainStage.setTitle("RemoveWords");
			mainStage.show();
	}

}
