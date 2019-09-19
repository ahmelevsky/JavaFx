package rf;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;
import rf.ui.MainController;


public class Main extends Application {
	
      private MainController mainController;
      private Stage mainStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		 mainStage = primaryStage;
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainForm.fxml"));
	        loader.setLocation(Main.class.getResource("ui/MainForm.fxml"));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        mainController = loader.getController();
	        mainStage.setTitle("RejectsFinder");
	        mainController.app = this;
			mainStage.show();
	}
	
	
	public void showWeb() throws IOException {
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/WebForm.fxml"));
	        loader.setLocation(Main.class.getResource("ui/WebForm.fxml"));
	        WebView page = (WebView) loader.load();
	        Scene scene = new Scene(page);
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        mainController = loader.getController();
	}
	
	public static void main(String[] args) {
		launch(args);
	}


	public Window getPrimaryStage() {
		return this.mainStage;
	}
	
	
}
