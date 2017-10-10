package artiom;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import artiom.view.ImageGridController;
import artiom.view.RootLayoutController;

public class Main extends Application {
	
	private Stage primaryStage;
	private AnchorPane rootLayout; 
    static RootLayoutController rootLayoutController;
	
	
	//private MasterDetailPane masterPane;
	
	@Override
	public void start(Stage primaryStage) {
		  this.primaryStage = primaryStage;
	        this.primaryStage.setTitle("Keyword Master");
	        //this.primaryStage.getIcons().add(new Image("file:resources/images/Address_Book.png"));
		initRootLayout();
		initGridLayout();
		
		rootLayoutController.addDirectoryTree("d:\\Photo");
	}
	
	public static void main(String[] args) {
		System.setProperty("javafx.animation.fullspeed", "true");
		launch(args);
		rootLayoutController.rootDirItem.dispose();
	}
	
	
	  public void initRootLayout() {
	        try {
	            // Загружаем корневой макет из fxml файла.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(Main.class
	                    .getResource("view/RootLayout.fxml"));
	            rootLayout = (AnchorPane) loader.load();

	            // Отображаем сцену, содержащую корневой макет.
	            Scene scene = new Scene(rootLayout);
	            primaryStage.setScene(scene);

	            // Даём контроллеру доступ к главному прилодению.
	            rootLayoutController = loader.getController();
	            
	            
	            rootLayoutController.setMainApp(this);

	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    }
	
	  
	  public void initGridLayout() {
	        try {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(Main.class.getResource("view/ImageGrid.fxml"));
	            FlowPane personOverview = (FlowPane) loader.load();

	            // Помещаем сведения об адресатах в центр корневого макета.
	            rootLayoutController.masterPane.setMasterNode(personOverview);
	            // Даём контроллеру доступ к главному приложению.
	            ImageGridController imGridController = loader.getController();
	            imGridController.setMainApp(this);
	            rootLayoutController.setImageGridController(imGridController);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	  
}
