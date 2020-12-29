package stocknote;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

	private MainFrameController mainController;
	
	private Stage mainStage;
	
	public static void main(String[] args) {
		launch(args); 
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainFrameWindow.fxml"));
	        loader.setLocation(Main.class.getResource("MainFrameWindow.fxml"));
	        VBox page = (VBox) loader.load();
	        Scene scene = new Scene(page);
	        this.mainStage.setScene(scene);
	        this.mainStage.sizeToScene();
	        mainController = loader.getController();
	        mainStage.setTitle("Stocknote 1.5");
	        mainStage.getIcons().add(new Image("file:resources/icon.png"));
	        addTab("Удалить дубликаты", "RemoveDuplicatesForm.fxml");
	        addTab("Вырезать слова", "RemoveWordsForm.fxml");
	        addTab("Соединить слова", "JoinWordsForm.fxml");
	        addTab("Заглавные буквы", "CapitalizeForm.fxml");
	        addTab("Точки", "EndDotForm.fxml");
	        addTab("Длина строк", "SortByLengthForm.fxml");
	        addTab("Строки в запятые", "LinesToCommasForm.fxml");
	        addTab("Запятые в строки", "CommasToLinesForm.fxml");
			mainStage.show();
	}
	
	
	
	private void addTab(String tabTitle, String fxml) throws IOException{
		 FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
	        loader.setLocation(Main.class.getResource(fxml));
	        Node page = (Node) loader.load();
	        Tab tab = new Tab(tabTitle, page);
	        tab.setStyle("-fx-padding: 15 0 15 0;-fx-min-height: 30px;-fx-focus-color: transparent;");
	        Label label = new Label(tabTitle);
	        label.setStyle("-fx-padding: 20px;-fx-min-height: 100px;-fx-focus-color: transparent;");
	        label.setWrapText(true);
	        label.setAlignment(Pos.CENTER);
	        label.setTextAlignment(TextAlignment.CENTER);
	        tab.setText(null);
	        tab.setGraphic(label);
	        mainController.addTab(tab);
	        loader.getController();
	}
	 

}
