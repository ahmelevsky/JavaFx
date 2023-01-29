package am;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import am.ui.MainController;

public class Main extends Application {

	public MainController mainController;
	private Stage mainStage;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		  
  		try {
            AnalLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
		
  		
  		 mainStage = primaryStage;
  	     mainStage.setTitle("AnalMaster v0.1 alpha");
  	     mainController = organizeStage("ui/MainForm.fxml");
  	     mainController.loadData();
  	     
  	     
  	     mainStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	 @Override
	 public void stop(){
	     for(Handler h:LOGGER.getHandlers())
	     {
	         h.close();   
	     }
	 }
	
	 

	 private MainController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        return loader.getController();
	    }
	 
	 
		public Window getPrimaryStage() {
			return this.mainStage;
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
		
		public void showAlertOK(String text){
			 Platform.runLater(new Runnable() {
	           public void run() {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("OK!");
			alert.setHeaderText("OK!");
			alert.setContentText(text);
			alert.showAndWait();
	           }
			 });
		}
		
		
}
