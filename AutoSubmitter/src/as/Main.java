package as;
	
import java.io.IOException;
import java.net.URISyntaxException;

import as.ui.MainController;
import as.ui.WebController;
import as.web.ShutterProvider;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


public class Main extends Application {
	
	  public MainController mainController;
      public WebController webController;
      private Stage mainStage;
      private Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		 mainStage = primaryStage;
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainForm.fxml"));
	        loader.setLocation(Main.class.getResource("ui/MainForm.fxml"));
	        VBox page = (VBox) loader.load();
	        this.mainScene = new Scene(page);
	        this.mainStage.setScene(this.mainScene);
	        this.mainStage.sizeToScene();
	        mainController = loader.getController();
	        mainStage.setTitle("RejectFinder v1.3.6");
	        mainController.app = this;
			mainStage.getIcons().add(new Image("file:resources/icon.png"));
			//mainStage.setMinHeight(500);
			//mainStage.setMinWidth(800);
			mainStage.show();
	}
	
	
	public void showWeb() throws IOException {
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/WebForm.fxml"));
	        loader.setLocation(Main.class.getResource("ui/WebForm.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Scene scene = new Scene(page);
	        Stage dialog = new Stage();
	        dialog.getIcons().add(new Image("file:resources/icon.png"));
	        dialog.setScene(scene);
	        dialog.sizeToScene();
	        webController = loader.getController();
	        dialog.initOwner(this.mainStage);
	        dialog.initModality(Modality.APPLICATION_MODAL); 
	        dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            public void handle(WindowEvent we) {
	            	webController.getSessionId();
	            }
	        });        
	        dialog.showAndWait(); 
	}
	
	public static void main(String[] args) throws URISyntaxException {
		launch(args);
	//	new ShutterProvider("s%3AW8IE9lvkVI88y_UJSG_DqNZw3NIjg2E9.vJFLtTYD83sGFuJBBDSaL7n9D8bPoUHWoWHuhDX3K9Y")
		//	.getApacheGETResponse("/api/content_editor/photo");
	}


	public Window getPrimaryStage() {
		return this.mainStage;
	}
	
	public Scene getPrimaryScene() {
		return this.mainScene;
	}
	
}
