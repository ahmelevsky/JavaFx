package rf;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import rf.ui.MainController;
import rf.ui.WebController;


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
	        mainStage.setTitle("RejectsFinder");
	        mainController.app = this;
			mainStage.getIcons().add(new Image("file:resources/icon.png"));
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
	        dialog.showAndWait(); 
	}
	
	public static void main(String[] args) {
		launch(args);
	}


	public Window getPrimaryStage() {
		return this.mainStage;
	}
	
	public Scene getPrimaryScene() {
		return this.mainScene;
	}
	
}