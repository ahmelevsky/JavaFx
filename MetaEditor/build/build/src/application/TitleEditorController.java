package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class TitleEditorController implements Initializable {
	public Main app;
	
	@FXML
	private TextArea titleText;
	
	@FXML
	private Label countLabel;
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("(" + getWordsCount(newValue) + "/" + newValue.length() + ")");
		});
		
	}
	
	
	private int getWordsCount(String text){
		return text.split("\\s+").length;
	}
	
	
	public String getTitle(){
		return titleText.getText();
	}
	
}
