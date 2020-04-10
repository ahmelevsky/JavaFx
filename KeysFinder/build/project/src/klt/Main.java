package klt;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import klt.ui.MainWindowController;
import klt.ui.SelectableBorderPane;
import klt.ui.SelectionHandler;
import klt.web.ShutterProvider;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	  public MainWindowController mainController;
      private Stage mainStage;
      public Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		 mainStage = primaryStage;
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainWindow.fxml"));
	        loader.setLocation(Main.class.getResource("ui/MainWindow.fxml"));
	        VBox page = (VBox) loader.load();
	        this.mainScene = new Scene(page);
	        this.mainStage.setScene(this.mainScene);
	        this.mainStage.sizeToScene();
	        mainController = loader.getController();
	        mainController.app = this;
	        mainController.setup();
	        mainStage.setTitle("KeysFinder v1.0");
			mainStage.getIcons().add(new Image("file:resources/icon.png"));
			mainStage.setMinHeight(600);
			mainStage.setMinWidth(1200);
			mainStage.show();
			SelectionHandler.app = this;
			SelectableBorderPane.app = this;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void showAlert(String text) {
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
	
	
	public void showMessage(String text) {
		Platform.runLater(new Runnable() {
            public void run() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setContentText(text);
		alert.showAndWait();
            }
		 });
	}
	
}
