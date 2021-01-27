package sm;
	
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sm.ui.MainController;
import sm.ui.RulesController;
import sm.ui.TabsController;
import sm.ui.WebController;


public class Main extends Application {
	
	  public TabsController tabsController;
	  public MainController mainController;
	  public RulesController rulesController;
      public WebController webController;
      private Stage mainStage;
      private Scene mainScene;
      
  	  private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
  	  
  	  @Override
	public void start(Stage primaryStage) throws Exception {
		 mainStage = primaryStage;
		 tabsController = organizeStage("ui/TabsForm.fxml");
	     mainStage.setTitle("SubmitMaster v0.1 alpha");
	     tabsController.app = this;
	     
	     mainController = (MainController) addTab("Submitter", "ui/MainForm.fxml", MainController.class);
	     mainController.app = this;
	     mainController.setup();
			
	     rulesController = (RulesController) addTab("Rules", "ui/RulesForm.fxml", RulesController.class);
	     rulesController.app = this;
	     rulesController.setup();
	     
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


	 @Override
	 public void stop(){
		 rulesController.unload();
	     for(Handler h:LOGGER.getHandlers())
	     {
	         h.close();   
	     }
	 }
	
	
	public Window getPrimaryStage() {
		return this.mainStage;
	}
	
	public Scene getPrimaryScene() {
		return this.mainScene;
	}
	
	

	
	 private TabsController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        return loader.getController();
	    }
	
	private Initializable addTab(String tabTitle, String fxml, @SuppressWarnings("rawtypes") Class c) throws IOException{
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        VBox page = (VBox) loader.load();
	        Tab tab = new Tab(tabTitle, page);
	        tab.setText(tabTitle);
	        tabsController.addTab(tab);
	        Initializable controller = loader.getController();
	        return controller;
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
