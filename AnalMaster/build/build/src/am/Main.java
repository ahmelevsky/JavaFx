package am;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import am.ui.TabsController;
import am.db.SQLManager;
import am.ui.MainController;
import am.ui.SetController;

public class Main extends Application {

	public TabsController tabsController;
	public MainController mainController;
	public SetController setController;
	private Stage mainStage;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public SQLManager sqlManager;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		  
  		try {
            AnalLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
  		 this.sqlManager = new SQLManager(this);
  		
  		 mainStage = primaryStage;
  	     mainStage.setTitle("AnalMaster v0.1 alpha");
  	     tabsController = organizeStage("ui/TabsForm.fxml");
  	     tabsController.app=this;
  	      
  	     mainController = (MainController) addTab("Database", "ui/MainForm.fxml", MainController.class);
	     mainController.app = this;
	     mainController.setup();
	     mainController.loadData();
	     
	     setController = (SetController) addTab("Set Analitics", "ui/SetForm.fxml", SetController.class);
	     setController.app = this;
	     setController.setup();
	     setController.loadData();
	     
	     
  	     mainStage.getIcons().add(new Image("file:resources/icon.png"));
	     mainController.app=this;
  	    
  	     tabsController.setup();
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
	
	 

	 private TabsController organizeStage(String fxml) throws IOException{
	        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
	        if (controller.getClass() == MainController.class)
	        	((MainController) controller).tab = tab;
	        if (controller.getClass() == SetController.class)
	        	((SetController) controller).tab = tab;
	        return controller;
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
		
		
	  public void openLink(String link) {
		  getHostServices().showDocument(link);
	  }
		
}
