package ot;
	
import ot.view.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private BorderPane rootLayout;
	private Stage primaryStage;
	public MainController mainController;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			   // ��������� �������� ����� �� fxml �����.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/MainWindow.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            mainController = loader.getController();
            mainController.setMainApp(this);
            this.primaryStage = primaryStage;
            // ���������� �����, ���������� �������� �����.
            Scene scene = new Scene(rootLayout);
            
            this.primaryStage.setTitle("OSA Sript Test");
   	        this.primaryStage.getIcons().add(new Image("file:	icon.png"));
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
